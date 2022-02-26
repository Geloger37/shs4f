package shs4f.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;
import shs4f.app.AppFrame;

public class ArduinoCore {
	public enum SerialStatus {
		BAD_BAUNDRATE_OR_DATABITS_OR_PARITY("Ошибка в настройках подключения"),
		BAD_DATABITS("Ошибка размера пакета передачи"), CONNECTED("Подключено"), IN_CONNECT("Подключение..."),
		LOST_CONNECT("Подключение потерянно"), NOT_CONNECTED("Нет подключения"), NOT_TESTED("Не протестировано"),
		UNKNOWN_ERROR("Неизвестная ошибка");

		private String label;

		SerialStatus(String s) {
			this.label = s;
		}
		
		public String getLabel() {
			return label;
		}
	}

	public static String[] getComList() {
		return SerialPortList.getPortNames();
	}

	private int baudrate;

	private int databits;

	private final String INIT_MESSAGE = "initial arduino";
	private boolean isConnect;
	private final Logger logger = LoggerFactory.getLogger(ArduinoCore.class);
	private List<Action> messageActionList = new LinkedList<>();

	private int parity;
	private SerialPort serial;
	private SerialStatus status;
	private List<Action> statusActionList = new LinkedList<>();
	private int stopbits;

	private Timer timeoutTimer;

	public ArduinoCore() {
		super();
		statusActionList.add(message -> logger.info("status---->>----" + message));
		changeStatus(SerialStatus.NOT_CONNECTED);
	}
	
	public void setParametrs(int boudrate, int databits, int stopbits, int parity) {
		this.baudrate = boudrate;
		this.databits = databits;
		this.stopbits = stopbits;
		this.parity = parity;
	}
	
	public void addMessageAction(Action action) {
		messageActionList.add(action);
	}

	public void addStatusAction(Action action) {
		statusActionList.add(action);
	}

	public void changeStatus(SerialStatus s) {
		this.status = s;
		statusActionList.forEach(a -> a.notifyElement(s.toString()));
	}

	public void closePort() {
		try {
			changeStatus(SerialStatus.NOT_CONNECTED);
			serial.removeEventListener();
			serial.closePort();
			messageActionList.clear();
			statusActionList.clear();
			closeTimeout();
		} catch (SerialPortException e) {
			logger.error(e.toString());
			changeStatus(SerialStatus.UNKNOWN_ERROR);
		}

	}

	private void closeTimeout() {
		timeoutTimer.cancel();
	}

	public SerialStatus getStatus() {
		return status;
	}

	public void openPort(String port) {

		try {
			this.serial = new SerialPort(port);
			setTimeout();
			serial.openPort();
			changeStatus(SerialStatus.IN_CONNECT);
			serial.setParams(baudrate, databits, stopbits, parity);
			serial.setEventsMask(SerialPort.MASK_RXCHAR);
			serial.addEventListener(serialPortEvent -> {
				if (serialPortEvent.isRXCHAR() && serialPortEvent.getEventValue() > 0) {
					try {
						isConnect = true;
						String message = serial.readString(serialPortEvent.getEventValue());
						logger.info(message);
						messageActionList.forEach(a -> a.notifyElement(message));
					} catch (SerialPortException e) {
						changeStatus(SerialStatus.UNKNOWN_ERROR);
					}

				}
			});

			Action initAction = message -> {
				if (message.equals(INIT_MESSAGE)) {
					changeStatus(SerialStatus.NOT_TESTED);
				} else {
					changeStatus(SerialStatus.BAD_BAUNDRATE_OR_DATABITS_OR_PARITY);
					try {
						serial.closePort();
					} catch (SerialPortException e) {
						changeStatus(SerialStatus.UNKNOWN_ERROR);
					}
				}
			};
			addMessageAction(initAction);

			Action testInitAction = message -> {
				if (message.equals(SerialStatus.NOT_TESTED.toString())) {
					removeMessageAction(initAction);
					testSerialSettings();
				}
			};
			addStatusAction(testInitAction);

			Action start = message -> {
				if (message.equals(SerialStatus.CONNECTED.toString())) {
					sendStartMessage();
				}
			};
			addStatusAction(start);
		} catch (SerialPortException e) {
			changeStatus(SerialStatus.UNKNOWN_ERROR);
		}

	}

	public void removeMessageAction(Action action) {
		messageActionList.remove(action);
	}

	public void removeStatusAction(Action action) {
		statusActionList.remove(action);
	}

	private void sendStartMessage() {
		try {
			serial.writeByte((byte) 200);
		} catch (SerialPortException e) {
			changeStatus(SerialStatus.UNKNOWN_ERROR);
		}
	}

	private void setTimeout() {
		timeoutTimer = new Timer(true);
		timeoutTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (isConnect) {
					isConnect = false;
				} else {
					changeStatus(SerialStatus.NOT_CONNECTED);
					messageActionList.clear();
					statusActionList.clear();
					AppFrame.getInstance().addActions();
					closeTimeout();
				}
			}
		}, 2500, 2500);
	}

	private void testDatabits() {
		try {
			Action action = new Action() {
				@Override
				public void notifyElement(String message) {
					if (message.getBytes()[0] == -1) {
						testStopbits();
					} else {
						changeStatus(SerialStatus.BAD_DATABITS);
						try {
							serial.closePort();
						} catch (SerialPortException e) {
							changeStatus(SerialStatus.UNKNOWN_ERROR);
						}
					}
					removeMessageAction(this);
				}
			};
			serial.writeByte((byte) 255);
			addMessageAction(action);
		} catch (SerialPortException e) {
			changeStatus(SerialStatus.UNKNOWN_ERROR);
		}
	}

	private void testParity() {
		changeStatus(SerialStatus.CONNECTED);
	}

	private void testSerialSettings() {
		testDatabits();
	}

	private void testStopbits() {
		testParity();
	}
}
