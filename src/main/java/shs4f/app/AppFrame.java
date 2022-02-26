package shs4f.app;

import java.awt.EventQueue;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import jssc.SerialPort;
import shs4f.model.ArduinoCore;
import shs4f.model.ArduinoCore.SerialStatus;

@SuppressWarnings("serial")
public class AppFrame extends JFrame {

	private static AppFrame instance;
	public static AppFrame getInstance() {
		return instance;
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					AppFrame frame = new AppFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private JComboBox<String> comComboBox;
	private JButton connectButton;
	private JPanel contentPane;
	private ArduinoCore core;
	private JLabel dataLabel;

	private JProgressBar progressConnectBar;

	private JLabel statusLabel;

	/**
	 * Create the frame.
	 */
	public AppFrame() {
		instance = this;
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setResizable(false);
		setTitle("SHS4j");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		core = new ArduinoCore(SerialPort.BAUDRATE_2400, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		comComboBox = new JComboBox<>();
		comComboBox.setBounds(10, 10, 70, 21);
		contentPane.add(comComboBox);

		JButton updateButton = new JButton("Обновить");
		updateButton.addActionListener(e -> updateComList());
		updateButton.setBounds(90, 10, 85, 21);
		contentPane.add(updateButton);

		connectButton = new JButton("Подключить");
		connectButton.setBounds(10, 34, 165, 21);

		connectButton.addActionListener(e -> {
			if (getComComboBox().getItemCount() != 0) {
				core.openPort((String) getComComboBox().getSelectedItem());
				getProgressConnectBar().setIndeterminate(true);
			}

		});
		contentPane.add(connectButton);

		dataLabel = new JLabel("0 C°");
		dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataLabel.setFont(new Font("Consolas", Font.PLAIN, 50));
		dataLabel.setBounds(0, 65, 436, 200);

		contentPane.add(dataLabel);

		progressConnectBar = new JProgressBar();

		progressConnectBar.setBounds(185, 34, 241, 21);
		contentPane.add(progressConnectBar);

		statusLabel = new JLabel("Статус: Нет подключения");

		statusLabel.setBounds(185, 12, 241, 17);
		contentPane.add(statusLabel);

		updateComList();
		addActions();
	}

	public void addActions() {
		core.addStatusAction(message -> {
			if (!message.equals(ArduinoCore.SerialStatus.NOT_CONNECTED.toString())) {
				connectButton.setText("Отключить");
				connectButton.removeActionListener(connectButton.getActionListeners()[0]);
				connectButton.addActionListener(e -> {
					core.closePort();
					addActions();
				});
			} else {
				connectButton.setText("Подключить");
				dataLabel.setText("0 C°");
				connectButton.removeActionListener(connectButton.getActionListeners()[0]);

				connectButton.addActionListener(e -> {
					if (getComComboBox().getItemCount() != 0) {
						core.openPort((String) getComComboBox().getSelectedItem());
						getProgressConnectBar().setIndeterminate(true);
					}
				});

			}
		});

		core.addStatusAction(message -> {
			if (message.equals(ArduinoCore.SerialStatus.CONNECTED.toString())) {
				getProgressConnectBar().setIndeterminate(false);
			}
		});

		core.addMessageAction(message -> {
			if (core.getStatus().equals(ArduinoCore.SerialStatus.CONNECTED)) {
				dataLabel.setText(message + " C°");
			}
		});

		core.addStatusAction(message -> statusLabel.setText(toStatusString(message)));
	}

	public JComboBox<String> getComComboBox() {
		return comComboBox;
	}

	public JButton getConnectButton() {
		return connectButton;
	}

	public JLabel getDataLabel() {
		return dataLabel;
	}

	public JProgressBar getProgressConnectBar() {
		return progressConnectBar;
	}

	public JLabel getStatusLabel() {
		return statusLabel;
	}

	private String toStatusString(String status) {
		SerialStatus s = ArduinoCore.SerialStatus.valueOf(status);
		return "Статус: " + s.getLabel();
	}

	private void updateComList() {
		getComComboBox().removeAllItems();
		Arrays.stream(ArduinoCore.getComList()).forEach(s -> getComComboBox().addItem(s));
	}
}
