package kmh.jchat.client.ui;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import kmh.jchat.common.BitConverter;

public class ChatPage extends JPanel {
	WinMain main = null;
	private JList chatterList;
	DefaultListModel<String> model;
	private JTextArea inputArea;
	private JTextArea chatArea;

	/**
	 * 
	 * Create the panel.
	 */
	public ChatPage() {
		setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.8);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, BorderLayout.CENTER);
		
		JPanel chatPanel = new JPanel();
		splitPane.setLeftComponent(chatPanel);
		chatPanel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane_1 = new JSplitPane();
		splitPane_1.setResizeWeight(0.8);
		chatPanel.add(splitPane_1, BorderLayout.CENTER);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane);
		
		chatArea = new JTextArea();
		scrollPane.setViewportView(chatArea);
		
		JPanel panel = new JPanel();
		splitPane_1.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel = new JLabel("Users");
		panel.add(lblNewLabel, BorderLayout.NORTH);
		
		JPanel panel_1 = new JPanel();
		panel.add(panel_1, BorderLayout.SOUTH);
		
		JButton btnNewButton = new JButton("\uAC15\uC81C\uD1F4\uC7A5");
		panel_1.add(btnNewButton);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel.add(scrollPane_1, BorderLayout.CENTER);
		
		chatterList = new JList();
		
		scrollPane_1.setViewportView(chatterList);
		
		JPanel inputPanel = new JPanel();
		splitPane.setRightComponent(inputPanel);
		inputPanel.setLayout(new BorderLayout(0, 0));
		
		JButton sendButton = new JButton("SEND");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				processSendmessage();
			}
		});
		inputPanel.add(sendButton, BorderLayout.EAST);
		
		JButton exitButton = new JButton("EXIT");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processExit();
				
			}
		});
		inputPanel.add(exitButton, BorderLayout.SOUTH);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		inputPanel.add(scrollPane_2, BorderLayout.CENTER);
		
		inputArea = new JTextArea();
		scrollPane_2.setViewportView(inputArea);
		
		chatterList.setModel(new DefaultListModel<String>());
		model = (DefaultListModel<String>) chatterList.getModel();

	}

	/**
	 * �޼����� �ܾ �������� ����.
	 */
	protected void processSendmessage() {
		
		String message = inputArea.getText();
		
		if ( main.service.sendMessageRequest(message) ) {
			inputArea.setText("");
		} else {
			JOptionPane.showMessageDialog(main, "fail to send Message");
		}
	}

	/**
	 * 1. ������ �α׾ƿ� ��û�� �����մϴ�.
	 * 2. �������� �α׾ƿ� ���� ������ �ɴϴ�.
	 * 3. ������ ��ȯ ��û��  winmain ���� �䱸�մϴ�.
	 */
	protected void processExit() {
		// 1. ����
		// 2. ����
		// 3. ���� ��ȯ
//		main.setLoginPage();
	   main.service.sendLogoutRequest();
	   main.setLoginPage();
		
		
	}

	/**
	 * ��ȭ �����ڵ韼 ��� ���� ������.
	 * @param nicknames
	 */
	public void renderChatters(String[] nicknames) {
		
		
		model.clear();
		
		for ( String nick : nicknames) {
			model.addElement(nick);			
		}
	}
	/**
	 * ��ȭ �����ڸ� �߰��մϴ�.
	 * @param nickname
	 */
	public void addChatter ( String nickname) {
		model.addElement(nickname);
	}

	public void add_pub_msg(String sender,String msg) {
		
		String cur = chatArea.getText();
		cur += "[" +sender + "] "+ msg;
		cur += "\n";
		chatArea.setText(cur);
		
	}

	public void removeChatter(String nickname) {
		model.removeElement(nickname);
		String text = chatArea.getText();
		text += " *** " + nickname + "���� �����̽��ϴ�";
		text += "\n";
		chatArea.setText(text);
	}

	public void updateMaster(String oldMaster, String newMaster) {
		System.out.println("�� ������?-----");
		if ( oldMaster != null && oldMaster.length() > 0 ) { //oldMaster�� �ְ� 
			String value = "[M]" + oldMaster ;
			System.out.println(oldMaster);
			model.removeElement(value) ; // [M]AA
			
			/*
			 * ���� ������ �ٲ�� ���� ���� ����(oldMaster)�� �α׾ƿ��� ������ ���Դϴ�.
			 * �����ʿ����� �α׾ƿ� ó���� ���� �Ŀ� ���� ���� �̺�Ʈ�� �뺸�ϱ� ������
			 * �Ʒ����� �ٽ� ���� ������ ����Ʈ�� �߰��ϸ� �������� ������ �߻��մϴ�.
			 * 
			 * ���� �ϴ� ���ƵӴϴ�.
			 */
			// model.addElement(oldMaster); // AA
		}
		
		if ( newMaster.length() > 0 ) {
			System.out.println("newmaster"+newMaster);
			model.removeElement(newMaster);
			String value = "[M]" + newMaster;
			model.insertElementAt(value, 0);
		}
	
	
	}

}
