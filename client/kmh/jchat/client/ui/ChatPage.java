package kmh.jchat.client.ui;

import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ChatPage extends JPanel {
	WinMain main = null;
	private JList chatterList;
	DefaultListModel<String> model;

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
		
		JTextArea chatArea = new JTextArea();
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
		
		JTextArea inputArea = new JTextArea();
		scrollPane_2.setViewportView(inputArea);

	}

	/**
	 * 1. 서버로 로그아웃 요청을 전송합니다.
	 * 2. 서버에서 로그아웃 성공 응답이 옵니다.
	 * 3. 페이지 전환 요청을  winmain 에게 요구합니다.
	 */
	protected void processExit() {
		// 1. 생략
		// 2. 생략
		// 3. 페이 전환
		main.setLoginPage();
		
		
	}

	public void renderChatters(String[] nicknames) {
		chatterList.setModel(new DefaultListModel<String>());
		 model = (DefaultListModel<String>) chatterList.getModel();
		model.clear();
		
		for ( String nick : nicknames) {
			model.addElement(nick);			
		}
		
	}

}
