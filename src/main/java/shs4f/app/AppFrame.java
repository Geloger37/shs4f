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
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;

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
	private JPanel parametrsPanel;
	private JComboBox<String> baundrateComboBox;
	private JComboBox<String> stopbitsComboBox;
	private JComboBox<String> databitsComboBox;
	private JComboBox<String> parityComboBox;
	private Component horizontalStrut;
	private Component horizontalStrut_1;

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
		setBounds(100, 100, 659, 207);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		core = new ArduinoCore();
		
		
		JPanel connectPanel = new JPanel();
		connectPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "\u041F\u043E\u0434\u043A\u043B\u044E\u0447\u0435\u043D\u0438\u0435", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		connectPanel.setBounds(10, 100, 347, 68);
		contentPane.add(connectPanel);
		GridBagLayout gbl_connectPanel = new GridBagLayout();
		gbl_connectPanel.columnWidths = new int[]{70, 60, 181, 0};
		gbl_connectPanel.rowHeights = new int[]{21, 21, 0};
		gbl_connectPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_connectPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		connectPanel.setLayout(gbl_connectPanel);
		
				comComboBox = new JComboBox<>();
				GridBagConstraints gbc_comComboBox = new GridBagConstraints();
				gbc_comComboBox.anchor = GridBagConstraints.NORTH;
				gbc_comComboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comComboBox.insets = new Insets(0, 0, 5, 5);
				gbc_comComboBox.gridx = 0;
				gbc_comComboBox.gridy = 0;
				connectPanel.add(comComboBox, gbc_comComboBox);
				
						JButton updateButton = new JButton("Обновить");
						GridBagConstraints gbc_updateButton = new GridBagConstraints();
						gbc_updateButton.anchor = GridBagConstraints.NORTH;
						gbc_updateButton.fill = GridBagConstraints.HORIZONTAL;
						gbc_updateButton.insets = new Insets(0, 0, 5, 5);
						gbc_updateButton.gridx = 1;
						gbc_updateButton.gridy = 0;
						connectPanel.add(updateButton, gbc_updateButton);
						updateButton.addActionListener(e -> updateComList());
		
				statusLabel = new JLabel("Статус: Нет подключения");
				statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
				statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 9));
				GridBagConstraints gbc_statusLabel = new GridBagConstraints();
				gbc_statusLabel.fill = GridBagConstraints.BOTH;
				gbc_statusLabel.insets = new Insets(0, 0, 5, 0);
				gbc_statusLabel.gridx = 2;
				gbc_statusLabel.gridy = 0;
				connectPanel.add(statusLabel, gbc_statusLabel);
						
								connectButton = new JButton("Подключить");
								GridBagConstraints gbc_connectButton = new GridBagConstraints();
								gbc_connectButton.anchor = GridBagConstraints.NORTH;
								gbc_connectButton.fill = GridBagConstraints.HORIZONTAL;
								gbc_connectButton.insets = new Insets(0, 0, 0, 5);
								gbc_connectButton.gridwidth = 2;
								gbc_connectButton.gridx = 0;
								gbc_connectButton.gridy = 1;
								connectPanel.add(connectButton, gbc_connectButton);
								
										connectButton.addActionListener(e -> {
											if (getComComboBox().getItemCount() != 0) {
												setCoreParametrsFromUI();
												core.openPort((String) getComComboBox().getSelectedItem());
												getProgressConnectBar().setIndeterminate(true);
											}
								
										});
				
						progressConnectBar = new JProgressBar();
						GridBagConstraints gbc_progressConnectBar = new GridBagConstraints();
						gbc_progressConnectBar.fill = GridBagConstraints.BOTH;
						gbc_progressConnectBar.gridx = 2;
						gbc_progressConnectBar.gridy = 1;
						connectPanel.add(progressConnectBar, gbc_progressConnectBar);

		dataLabel = new JLabel("0 C°");
		dataLabel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "\u0414\u0430\u0442\u0447\u0438\u043A", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		dataLabel.setHorizontalAlignment(SwingConstants.CENTER);
		dataLabel.setFont(new Font("Consolas", Font.PLAIN, 50));
		dataLabel.setBounds(359, 15, 276, 153);

		contentPane.add(dataLabel);
		
		parametrsPanel = new JPanel();
		parametrsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "\u041F\u0430\u0440\u0430\u043C\u0435\u0442\u0440\u044B \u043F\u043E\u0434\u043A\u043B\u044E\u0447\u0435\u043D\u0438\u044F", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		parametrsPanel.setBounds(10, 15, 347, 87);
		contentPane.add(parametrsPanel);
		GridBagLayout gbl_parametrsPanel = new GridBagLayout();
		gbl_parametrsPanel.columnWidths = new int[]{0, 46, 40, 0, 37, 81, 0};
		gbl_parametrsPanel.rowHeights = new int[]{0, 21, 21, 0};
		gbl_parametrsPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_parametrsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		parametrsPanel.setLayout(gbl_parametrsPanel);
		
		Component verticalStrut = Box.createVerticalStrut(2);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.anchor = GridBagConstraints.WEST;
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 1;
		gbc_verticalStrut.gridy = 0;
		parametrsPanel.add(verticalStrut, gbc_verticalStrut);
		
		horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_1.gridx = 0;
		gbc_horizontalStrut_1.gridy = 1;
		parametrsPanel.add(horizontalStrut_1, gbc_horizontalStrut_1);
		
		JLabel baundrateLabel = new JLabel("Boundrate");
		GridBagConstraints gbc_baundrateLabel = new GridBagConstraints();
		gbc_baundrateLabel.anchor = GridBagConstraints.WEST;
		gbc_baundrateLabel.insets = new Insets(0, 0, 5, 5);
		gbc_baundrateLabel.gridx = 1;
		gbc_baundrateLabel.gridy = 1;
		parametrsPanel.add(baundrateLabel, gbc_baundrateLabel);
		
		baundrateComboBox = new JComboBox();
		baundrateComboBox.setModel(new DefaultComboBoxModel(new String[] {"110", "300", "600", "1200", "2400", "4800", "9600", "14400", "19200", "38400", "57600", "115200", "128000", "256000"}));
		baundrateComboBox.setSelectedIndex(4);
		GridBagConstraints gbc_baundrateComboBox = new GridBagConstraints();
		gbc_baundrateComboBox.anchor = GridBagConstraints.NORTH;
		gbc_baundrateComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_baundrateComboBox.insets = new Insets(0, 0, 5, 5);
		gbc_baundrateComboBox.gridx = 2;
		gbc_baundrateComboBox.gridy = 1;
		parametrsPanel.add(baundrateComboBox, gbc_baundrateComboBox);
		
		horizontalStrut = Box.createHorizontalStrut(40);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 3;
		gbc_horizontalStrut.gridy = 1;
		parametrsPanel.add(horizontalStrut, gbc_horizontalStrut);
		
		JLabel stopbitsLabel = new JLabel("Stopbits");
		GridBagConstraints gbc_stopbitsLabel = new GridBagConstraints();
		gbc_stopbitsLabel.anchor = GridBagConstraints.WEST;
		gbc_stopbitsLabel.insets = new Insets(0, 0, 5, 5);
		gbc_stopbitsLabel.gridx = 4;
		gbc_stopbitsLabel.gridy = 1;
		parametrsPanel.add(stopbitsLabel, gbc_stopbitsLabel);
		
		stopbitsComboBox = new JComboBox();
		stopbitsComboBox.setModel(new DefaultComboBoxModel(new String[] {"1", "1.5", "2"}));
		GridBagConstraints gbc_stopbitsComboBox = new GridBagConstraints();
		gbc_stopbitsComboBox.anchor = GridBagConstraints.NORTH;
		gbc_stopbitsComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_stopbitsComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_stopbitsComboBox.gridx = 5;
		gbc_stopbitsComboBox.gridy = 1;
		parametrsPanel.add(stopbitsComboBox, gbc_stopbitsComboBox);
		
		JLabel databitsLabel = new JLabel("Databits");
		GridBagConstraints gbc_databitsLabel = new GridBagConstraints();
		gbc_databitsLabel.anchor = GridBagConstraints.WEST;
		gbc_databitsLabel.insets = new Insets(0, 0, 0, 5);
		gbc_databitsLabel.gridx = 1;
		gbc_databitsLabel.gridy = 2;
		parametrsPanel.add(databitsLabel, gbc_databitsLabel);
		
		databitsComboBox = new JComboBox();
		databitsComboBox.setModel(new DefaultComboBoxModel(new String[] {"5", "6", "7", "8"}));
		databitsComboBox.setSelectedIndex(3);
		GridBagConstraints gbc_databitsComboBox = new GridBagConstraints();
		gbc_databitsComboBox.anchor = GridBagConstraints.NORTH;
		gbc_databitsComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_databitsComboBox.insets = new Insets(0, 0, 0, 5);
		gbc_databitsComboBox.gridx = 2;
		gbc_databitsComboBox.gridy = 2;
		parametrsPanel.add(databitsComboBox, gbc_databitsComboBox);
		
		JLabel parityLabel = new JLabel("Parity");
		GridBagConstraints gbc_parityLabel = new GridBagConstraints();
		gbc_parityLabel.anchor = GridBagConstraints.WEST;
		gbc_parityLabel.insets = new Insets(0, 0, 0, 5);
		gbc_parityLabel.gridx = 4;
		gbc_parityLabel.gridy = 2;
		parametrsPanel.add(parityLabel, gbc_parityLabel);
		
		parityComboBox = new JComboBox();
		parityComboBox.setModel(new DefaultComboBoxModel(new String[] {"NONE", "ODD", "EVEN", "MARK", "SPACE"}));
		GridBagConstraints gbc_parityComboBox = new GridBagConstraints();
		gbc_parityComboBox.anchor = GridBagConstraints.NORTH;
		gbc_parityComboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_parityComboBox.gridx = 5;
		gbc_parityComboBox.gridy = 2;
		parametrsPanel.add(parityComboBox, gbc_parityComboBox);
		
		setCoreParametrsFromUI();
		updateComList();
		addActions();
	}

	public void addActions() {
		core.addStatusAction(message -> {
			if (message.equals(ArduinoCore.SerialStatus.NOT_CONNECTED.toString())) {
				connectButton.setText("Подключить");
				getProgressConnectBar().setIndeterminate(false);
				dataLabel.setText("0 C°");
				connectButton.removeActionListener(connectButton.getActionListeners()[0]);
				connectButton.addActionListener(e -> {
					if (getComComboBox().getItemCount() != 0) {
						setCoreParametrsFromUI();
						core.openPort((String) getComComboBox().getSelectedItem());
						getProgressConnectBar().setIndeterminate(true);
					}
				});
			} else {
				connectButton.setText("Отключить");
				connectButton.removeActionListener(connectButton.getActionListeners()[0]);
				connectButton.addActionListener(e -> {
					core.closePort();
					addActions();
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
	public JComboBox<String> getBaundrateComboBox() {
		return baundrateComboBox;
	}
	public JComboBox<String> getStopbitsComboBox() {
		return stopbitsComboBox;
	}
	public JComboBox<String> getDatabitsComboBox() {
		return databitsComboBox;
	}
	public JComboBox<String> getParityComboBox() {
		return parityComboBox;
	}
	
	public void setCoreParametrsFromUI() {
		int baundrate = Integer.parseInt( (String) getBaundrateComboBox().getSelectedItem());
		int databits = Integer.parseInt( (String) getDatabitsComboBox().getSelectedItem());
		int stopbits = switch ((String) getStopbitsComboBox().getSelectedItem()) {
		case "1": {
			yield 1;
		}
		case "2": {
			yield 2;
		}
		case "1.5": {
			yield 3;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + (String) getStopbitsComboBox().getSelectedItem());
		};
		int parity = switch ((String) getParityComboBox().getSelectedItem()) {
		case "NONE": {
			yield 0;
		}
		case "ODD": {
			yield 1;
		}
		case "EVEN": {
			yield 2;
		}
		case "MARK": {
			yield 3;
		}
		case "SPACE": {
			yield 4;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + (String) getParityComboBox().getSelectedItem());
		};
		
		
		core.setParametrs(baundrate, databits, stopbits, parity);
		
	}
}
