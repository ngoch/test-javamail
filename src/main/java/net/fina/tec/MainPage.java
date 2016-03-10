package net.fina.tec;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class MainPage extends JFrame {

	private static Properties properties = null;
	private Container container;
	private JTabbedPane tabs;
	private JPanel pop3Panel;
	private JTextArea pop3TextArea;
	private JButton testPOP3;
	private JPanel imapPanel;
	private JTextArea imapTextArea;
	private JButton testImap;
	private JPanel smtpPanel;
	private JTextField sendTo;
	private JButton sendButton;
	private JTextArea sendTextArea;
	private JLabel POP3savedChangesLabel = new JLabel(" ");
	private JLabel IMAPsavedChangesLabel = new JLabel(" ");
	private JLabel SMTPsavedChangesLabel = new JLabel(" ");
	private String fileName;
	final Vector<String> col = new Vector<String>();

	public MainPage(String fileName) {
		this.fileName = fileName;
		initComponents();
	}

	private void initComponents() {

		JButton POP3AddButton = new JButton("Add");
		JButton IMAPAddButton = new JButton("Add");
		JButton SMTPAddButton = new JButton("Add");

		JButton POP3DeleteButton = new JButton("Delete");
		JButton IMAPDeleteButton = new JButton("Delete");
		JButton SMTPDeleteButton = new JButton("Delete");

		JButton POP3CopyButton = new JButton("Copy text");
		JButton IMAPCopyButton = new JButton("Copy text");
		JButton SMTPCopyButton = new JButton("Copy text");

		POP3AddButton.setToolTipText("Add POP3 Properties");
		IMAPAddButton.setToolTipText("Add IMAP Properties");
		SMTPAddButton.setToolTipText("Add SMTP Properties");

		POP3DeleteButton.setToolTipText("Delete selected Properties");
		IMAPDeleteButton.setToolTipText("Delete selected Properties");
		SMTPDeleteButton.setToolTipText("Delete selected Properties");

		POP3CopyButton.setToolTipText("Copy whole text area");
		IMAPCopyButton.setToolTipText("Copy whole text area");
		SMTPCopyButton.setToolTipText("Copy whole text area");

		this.setTitle("Test Email Configuration");
		container = this.getContentPane();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		container.setLayout(new BorderLayout());
		Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
		size.height = size.height - 100;
		size.width = size.width - 100;
		this.setSize(size);
		this.setLocation(50, 50);

		tabs = new JTabbedPane();

		col.add("key");
		col.add("Value");

		// ===========================================================================================================
		// ============================================================================================================
		// POP3 Tab
		pop3Panel = new JPanel(new BorderLayout());
		JPanel tempNorthPanel = new JPanel();
		testPOP3 = new JButton("TEST | POP3");
		testPOP3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				class Test implements Runnable {

					private JButton parent = null;
					private String parentText;

					public Test(JButton parent) {
						this.parent = parent;
						this.parentText = this.parent.getText();
					}

					public void run() {

						this.parent.setText("Please wait...");
						pop3TextArea.setText("");
						parent.setEnabled(false);

						try {
							int messageCount = TestConnectionPOP3.readMails(fileName);
							pop3TextArea.setText(pop3TextArea.getText() + "\n" + "Connected successfully.\n");
							pop3TextArea.setText(pop3TextArea.getText() + "\n" + "Start mail Read.\n");
							pop3TextArea.setText(pop3TextArea.getText() + "\n" + "Message(s) Count = " + messageCount + "\n");
							pop3TextArea.setText(pop3TextArea.getText() + "\nPOP3 Read Test is OK");
							pop3TextArea.setText(pop3TextArea.getText() + "\n-----------------------------------------------------------------------------------");
						} catch (Exception e1) {
							e1.printStackTrace();
							pop3TextArea.setText(pop3TextArea.getText() + "\n" + e1.getMessage());
						} finally {
							this.parent.setText(this.parentText);
							this.parent.setEnabled(true);
						}
					}
				}
				new Thread(new Test((JButton) e.getSource())).start();
			}
		});

		JPanel temp_temp_and_even_tempPanel = new JPanel(new GridLayout());
		temp_temp_and_even_tempPanel.add(this.testPOP3);
		temp_temp_and_even_tempPanel.add(POP3CopyButton);

		tempNorthPanel.add(temp_temp_and_even_tempPanel);
		this.pop3Panel.add(tempNorthPanel, "North");

		JSplitPane popSplit = new JSplitPane();
		this.pop3TextArea = new JTextArea();
		this.pop3TextArea.setEditable(false);
		this.pop3TextArea.setToolTipText("Console");
		popSplit.setRightComponent(this.pop3TextArea);

		final JTable pop3Table = new JTable(getCurrentRows("pop3"), col);
		pop3Table.setModel(new PropertiesTableModel());
		DefaultTableModel pop3DefaultTableModel = (DefaultTableModel) pop3Table.getModel();

		for (Vector<String> v : getCurrentRows("pop3")) {
			pop3DefaultTableModel.addRow(v);
		}

		JPanel pop3RPanel = new JPanel(new BorderLayout());
		pop3RPanel.add(new JScrollPane(pop3Table), BorderLayout.CENTER);
		popSplit.setLeftComponent(pop3RPanel);

		pop3Table.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == 10) {
					MainPage.this.saveProperties(pop3Table);
				}
				MainPage.this.tableUpdateRows(pop3Table, "pop3");
			}

			public void keyReleased(KeyEvent e) {
				keyTyped(e);
			}

			public void keyPressed(KeyEvent e) {
				keyTyped(e);
			}
		});

		POP3DeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteTableRow(pop3Table);
			}
		});
		POP3CopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				CopyText(pop3TextArea);
			}
		});

		POP3AddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainPage.this.addPropertyAttribure();
				tableUpdateRows(pop3Table, "pop3");
			}
		});
		JPanel pop3SaveTmpPanel = new JPanel(new GridLayout(2, 1));
		pop3SaveTmpPanel.add(this.POP3savedChangesLabel);

		JPanel secondaryPOP3SaveTmpPanel = new JPanel(new GridLayout(1, 2));
		secondaryPOP3SaveTmpPanel.add(POP3AddButton);
		secondaryPOP3SaveTmpPanel.add(POP3DeleteButton);

		temp_temp_and_even_tempPanel = new JPanel();
		temp_temp_and_even_tempPanel.add(secondaryPOP3SaveTmpPanel);

		pop3SaveTmpPanel.add(temp_temp_and_even_tempPanel);

		pop3RPanel.add(pop3SaveTmpPanel, "South");

		this.pop3Panel.add(popSplit, "Center");

		this.tabs.addTab("POP", this.pop3Panel);

		// IAMP Tab

		this.imapPanel = new JPanel(new BorderLayout());

		JPanel tempNorthPanelIMAP = new JPanel();
		this.testImap = new JButton("TEST | IMAP");
		this.testImap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				class Test implements Runnable {
					private JButton parent = null;
					private String parentText;

					public Test(JButton parent) {
						this.parent = parent;
						this.parentText = this.parent.getText();
					}

					public void run() {
						try {
							this.parent.setText("Please wait...");
							MainPage.this.imapTextArea.setText("");
							this.parent.setEnabled(false);
							int messagesCount = TestConnectionIMAP.readMails(MainPage.this.fileName);

							MainPage.this.imapTextArea.setText(MainPage.this.imapTextArea.getText() + "\nConnected successfully.\n");

							MainPage.this.imapTextArea.setText(MainPage.this.imapTextArea.getText() + "\n" + "Start mail Read.\n");

							MainPage.this.imapTextArea.setText(MainPage.this.imapTextArea.getText() + "\n" + "Message(s) Count = " + messagesCount + "\n");

							MainPage.this.imapTextArea.setText(MainPage.this.imapTextArea.getText() + "\nIMAP Read Test is OK!");

							MainPage.this.imapTextArea.setText(MainPage.this.imapTextArea.getText() + "\n-----------------------------------------------------------------------------------");
						} catch (Exception e1) {
							e1.printStackTrace();
							MainPage.this.imapTextArea.setText(MainPage.this.imapTextArea.getText() + "\n" + e1.getMessage());
						} finally {
							this.parent.setText(this.parentText);
							this.parent.setEnabled(true);
						}
					}
				}
				new Thread(new Test((JButton) e.getSource())).start();
			}
		});
		temp_temp_and_even_tempPanel = new JPanel(new GridLayout());
		temp_temp_and_even_tempPanel.add(this.testImap);
		temp_temp_and_even_tempPanel.add(IMAPCopyButton);

		tempNorthPanelIMAP.add(temp_temp_and_even_tempPanel);
		this.imapPanel.add(tempNorthPanelIMAP, "North");

		JSplitPane imapSplit = new JSplitPane();

		this.imapTextArea = new JTextArea();
		this.imapTextArea.setEditable(false);
		this.imapTextArea.setToolTipText("Console");
		this.imapTextArea.scrollRectToVisible(getBounds());
		imapSplit.setRightComponent(new JScrollPane(this.imapTextArea));

		final JTable imapTable = new JTable(getCurrentRows("imap"), this.col);
		imapTable.setModel(new PropertiesTableModel());
		JPanel imapRPanel = new JPanel(new BorderLayout());
		imapRPanel.add(new JScrollPane(imapTable), "Center");
		imapSplit.setLeftComponent(imapRPanel);

		imapTable.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				int keyCode = e.getKeyCode();

				if (keyCode == 10) {
					saveProperties(imapTable);
				}
				tableUpdateRows(imapTable, "imap");
			}

			public void keyReleased(KeyEvent e) {
				keyTyped(e);
			}

			public void keyPressed(KeyEvent e) {
				keyTyped(e);
			}
		});
		IMAPDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteTableRow(imapTable);
			}
		});
		IMAPCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainPage.this.CopyText(MainPage.this.imapTextArea);
			}
		});
		IMAPAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainPage.this.addPropertyAttribure();
				tableUpdateRows(imapTable, "imap");
			}
		});
		JPanel imapSaveTmpPanel = new JPanel(new GridLayout(2, 1));
		imapSaveTmpPanel.add(this.IMAPsavedChangesLabel);

		JPanel secondaryIMAPSaveTmpPanel = new JPanel(new GridLayout(1, 2));
		secondaryIMAPSaveTmpPanel.add(IMAPAddButton);
		secondaryIMAPSaveTmpPanel.add(IMAPDeleteButton);

		temp_temp_and_even_tempPanel = new JPanel();
		temp_temp_and_even_tempPanel.add(secondaryIMAPSaveTmpPanel);
		imapSaveTmpPanel.add(temp_temp_and_even_tempPanel);

		imapRPanel.add(imapSaveTmpPanel, "South");

		this.imapPanel.add(imapSplit, "Center");
		this.tabs.addTab("IMAP", this.imapPanel);

		this.smtpPanel = new JPanel(new BorderLayout());

		JPanel tempSendNorthPanel = new JPanel(new GridLayout());
		JLabel toJlabel = new JLabel("TO");
		toJlabel.setHorizontalAlignment(4);

		tempSendNorthPanel.add(toJlabel);

		this.sendTo = new JTextField(20);
		this.sendTo.setText("support@fina2.net");
		tempSendNorthPanel.add(this.sendTo);

		this.sendButton = new JButton("TEST | SMTP (Send Email)");
		this.sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				class Test implements Runnable {
					private JButton parent = null;
					private String parentText;

					public Test(JButton parent) {
						this.parent = parent;
						this.parentText = this.parent.getText();
					}

					public void run() {
						try {
							this.parent.setText("Please wait...");

							this.parent.setEnabled(false);
							MainPage.this.sendTextArea.setText("");
							String to = MainPage.this.sendTo.getText();
							TestConnectionSMPT.sendMail(fileName, new String[] { to });
							MainPage.this.sendTextArea.setText("Send successfully.\nSend To : " + to);

							MainPage.this.sendTextArea.setText(MainPage.this.sendTextArea.getText() + "\n" + "Send test is OK!");

							MainPage.this.sendTextArea.setText(MainPage.this.sendTextArea.getText() + "\n-----------------------------------------------------------------------------------");
						} catch (Exception e1) {
							MainPage.this.sendTextArea.setText(MainPage.this.sendTextArea.getText() + "\n" + e1.getMessage());

							e1.printStackTrace();
						} finally {
							this.parent.setText(this.parentText);
							this.parent.setEnabled(true);
						}
					}
				}
				new Thread(new Test((JButton) e.getSource())).start();
			}
		});
		SMTPCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainPage.this.CopyText(MainPage.this.sendTextArea);
			}
		});
		tempSendNorthPanel.add(this.sendButton);
		tempSendNorthPanel.add(SMTPCopyButton);

		temp_temp_and_even_tempPanel = new JPanel();
		temp_temp_and_even_tempPanel.add(tempSendNorthPanel);
		this.smtpPanel.add(temp_temp_and_even_tempPanel, "North");

		JSplitPane smtpSplit = new JSplitPane();
		this.sendTextArea = new JTextArea();
		this.sendTextArea.setEditable(false);
		this.sendTextArea.setToolTipText("Console");
		smtpSplit.setRightComponent(new JScrollPane(this.sendTextArea));

		final JTable smtpTable = new JTable(getCurrentRows("smtp"), this.col);
		smtpTable.setModel(new PropertiesTableModel());
		JPanel smtpRPanel = new JPanel(new BorderLayout());
		smtpRPanel.add(new JScrollPane(smtpTable), "Center");
		smtpSplit.setLeftComponent(smtpRPanel);

		smtpTable.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				int keyCode = e.getKeyCode();
				if (keyCode == 10) {
					saveProperties(smtpTable);
				}
				tableUpdateRows(smtpTable, "smtp");
			}

			public void keyReleased(KeyEvent e) {
				keyTyped(e);
			}

			public void keyPressed(KeyEvent e) {
				keyTyped(e);
			}
		});
		SMTPDeleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DeleteTableRow(smtpTable);
			}
		});
		SMTPAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MainPage.this.addPropertyAttribure();
				tableUpdateRows(smtpTable, "smtp");
			}
		});
		JPanel smtpSaveTmpPanel = new JPanel(new GridLayout(2, 1));
		smtpSaveTmpPanel.add(this.SMTPsavedChangesLabel);

		JPanel secondarySMTPSaveTmpPanel = new JPanel(new GridLayout(1, 2));
		secondarySMTPSaveTmpPanel.add(SMTPAddButton);
		secondarySMTPSaveTmpPanel.add(SMTPDeleteButton);

		temp_temp_and_even_tempPanel = new JPanel();
		temp_temp_and_even_tempPanel.add(secondarySMTPSaveTmpPanel);
		smtpSaveTmpPanel.add(temp_temp_and_even_tempPanel);

		smtpRPanel.add(smtpSaveTmpPanel, "South");

		this.smtpPanel.add(smtpSplit, "Center");
		this.tabs.addTab("SMTP", this.smtpPanel);

		tabs.addChangeListener(new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				tableUpdateRows(pop3Table, "pop3");
				tableUpdateRows(imapTable, "imap");
				tableUpdateRows(smtpTable, "smtp");
			}
		});

		container.add(tabs);
	}

	private void tableUpdateRows(JTable table, String co) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = 0; i < model.getRowCount(); i++) {
			model.removeRow(i);
		}
		model.setDataVector(getCurrentRows(co), col);
		table.repaint();
	}

	private void saveProperties(JTable table) {
		int rowCount = table.getModel().getRowCount();

		Properties props = new Properties();
		try {
			FileReader fr = new FileReader(fileName);
			props.load(fr);
			fr.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		for (int i = 0; i < rowCount; i++) {
			String key = (String) table.getModel().getValueAt(i, 0);
			String val = (String) table.getModel().getValueAt(i, 1);
			props.put(key, val);
		}
		try {
			FileWriter fw = new FileWriter(fileName);
			props.store(fw, "Mail Properties");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Animation().start();
	}

	private Vector<Vector<String>> getCurrentRows(String co) {
		Properties prop = TestConnectionPOP3.createProperties(this.fileName);
		Set s = prop.keySet();
		Object[] arr = s.toArray();
		Vector row = new Vector();
		ArrayList<String> general = ReadGeneralComponents(prop);

		for (int i = 0; i < arr.length; i++) {
			String p = (String) arr[i];
			Vector<String> v = new Vector<String>();

			for (String string : general) {
				if (p.contains(string)) {
					v.add(p);
					v.add(prop.get(arr[i]) + "");
					row.add(v);
				}
			}
			if (p.contains(co)) {
				v.add(p);
				v.add(prop.get(arr[i]) + "");
				row.add(v);
			}
		}

		return row;
	}

	private ArrayList<String> ReadGeneralComponents(Properties prop) {
		Set s = prop.keySet();
		Object[] arr = s.toArray();
		ArrayList arrayResult = new ArrayList();
		for (Object obj : arr) {
			String string = (String) obj;
			if (string.split("[.]").length == 2) {
				arrayResult.add(string);
			}
		}
		return arrayResult;
	}

	class PropertiesTableModel extends DefaultTableModel {

		public PropertiesTableModel() {
			super(new Object[][] {}, new String[] { "Key", "Value" });
		}

		Class<?>[] columnTypes = new Class<?>[] { String.class, String.class };

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex != 0);
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length != 1) {
			throw new RuntimeException("Please Set Parameter Properties Folde Name");
		}

		Logger log = Logger.getLogger(MainPage.class);
		log.info("Start Application");

		final String folderName = args[0];
		try {
			properties = new Properties();
			properties.load(new FileReader(folderName));
		} catch (IOException ioex) {
			log.error(ioex.getMessage(), ioex);
			ioex.printStackTrace();
		}
		try {
			javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
		Runnable run = new Runnable() {

			public void run() {
				new MainPage(folderName).setVisible(true);
			}
		};
		SwingUtilities.invokeLater(run);
	}

	private void addPropertyAttribure() {
		KeyValueDialog keyValue = new KeyValueDialog();

		int choice = JOptionPane.showConfirmDialog(this, keyValue, "enter properties", 2);

		if (choice != 0) {
			return;
		}
		String[] KV = keyValue.getKeyValue();
		if ((KV[0] == null) || (KV[0].trim().length() == 0) || (KV[1] == null) || (KV[1].trim().length() == 0)) {
			return;
		}

		properties.put(KV[0], KV[1]);
		try {
			FileWriter fw = new FileWriter(this.fileName);
			properties.store(fw, "Mail Properties");
			fw.close();
			new Animation().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void DeleteTableRow(JTable table) {
		if (JOptionPane.showConfirmDialog(table, "Are you shure to delete the selected row?", "delete row", 2) != 0) {
			return;
		}

		if ((table.getSelectedRowCount() == 0) && (table.getSelectedColumnCount() == 0)) {
			return;
		}
		int[] rows = table.getSelectedRows();

		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i : rows) {
			properties.remove(table.getValueAt(i, 0));
			model.removeRow(i);
		}
		try {
			FileWriter fw = new FileWriter(this.fileName);
			properties.store(fw, "Mail Properties");
			fw.close();
			new Animation().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void CopyText(JTextArea text) {
		text.selectAll();
		text.copy();
	}

	class Animation extends Thread {
		Animation() {
		}

		public synchronized void run() {
			try {
				MainPage.this.initsavedChangesLabel();
				sleep(5000L);
				MainPage.this.clearsavedChangesLabel();
			} catch (InterruptedException e) {
			}
		}
	}

	private void initsavedChangesLabel() {
		String PSaved = "Properties Saved!";
		Font font = new Font("SansSerif", 0, 20);
		Color color = new Color(255, 0, 0);

		this.POP3savedChangesLabel.setText(PSaved);
		this.POP3savedChangesLabel.setFont(font);
		this.POP3savedChangesLabel.setForeground(color);
		this.POP3savedChangesLabel.setHorizontalAlignment(0);
		this.IMAPsavedChangesLabel.setText(PSaved);
		this.IMAPsavedChangesLabel.setFont(font);
		this.IMAPsavedChangesLabel.setForeground(color);
		this.IMAPsavedChangesLabel.setHorizontalAlignment(0);
		this.SMTPsavedChangesLabel.setText(PSaved);
		this.SMTPsavedChangesLabel.setFont(font);
		this.SMTPsavedChangesLabel.setForeground(color);

		this.SMTPsavedChangesLabel.setHorizontalAlignment(0);
	}

	private void clearsavedChangesLabel() {
		this.POP3savedChangesLabel.setText(" ");
		this.IMAPsavedChangesLabel.setText(" ");
		this.SMTPsavedChangesLabel.setText(" ");
	}

}

class KeyValueDialog extends JPanel {
	private JTextField ValueTextField;
	private JLabel keyLabel;
	private JTextField keyTextField;
	private JLabel valueLabel;

	public KeyValueDialog() {
		initComponents();
		keyTextField.setFocusable(true);
	}

	private void initComponents() {
		this.keyLabel = new JLabel();
		this.keyTextField = new JTextField("mail.");
		this.valueLabel = new JLabel();
		this.ValueTextField = new JTextField();

		setLayout(new GridBagLayout());

		this.keyLabel.setText("key");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = 18;
		gridBagConstraints.insets = new Insets(33, 58, 0, 0);
		add(this.keyLabel, gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.ipadx = 130;
		gridBagConstraints.anchor = 18;
		gridBagConstraints.insets = new Insets(30, 18, 0, 58);
		add(this.keyTextField, gridBagConstraints);

		this.valueLabel.setText("value");
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = 18;
		gridBagConstraints.insets = new Insets(14, 58, 0, 0);
		add(this.valueLabel, gridBagConstraints);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.ipadx = 156;
		gridBagConstraints.anchor = 18;
		gridBagConstraints.insets = new Insets(11, 18, 0, 58);
		add(this.ValueTextField, gridBagConstraints);
	}

	public String[] getKeyValue() {
		String[] result = new String[2];
		result[0] = this.keyTextField.getText();
		result[1] = this.ValueTextField.getText();
		return result;
	}
}