package kmh.jchat.client.ui;

import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import java.awt.Insets;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class LoginPage extends JPanel {
	private JTextField ipField;
	private JTextField portField;
	private JTextField nicknameField;
       
	WinMain main = null; // 좋은 설계는 아닙니다. (강한 결합)
	/**
	 * Create the panel.
	 */
	public LoginPage() {
		
		JPanel innerPanel = new JPanel();
		innerPanel.setPreferredSize(new Dimension(300, 200));
		add(innerPanel);
		GridBagLayout gbl_innerPanel = new GridBagLayout();
		gbl_innerPanel.columnWidths = new int[]{0, 0, 0};
		gbl_innerPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_innerPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_innerPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		innerPanel.setLayout(gbl_innerPanel);
		
		JLabel lblServerIp = new JLabel("SERVER IP");
		GridBagConstraints gbc_lblServerIp = new GridBagConstraints();
		gbc_lblServerIp.insets = new Insets(0, 0, 5, 5);
		gbc_lblServerIp.anchor = GridBagConstraints.EAST;
		gbc_lblServerIp.gridx = 0;
		gbc_lblServerIp.gridy = 0;
		innerPanel.add(lblServerIp, gbc_lblServerIp);
		
		ipField = new JTextField();
		ipField.setText("127.0.0.1");
		GridBagConstraints gbc_ipField = new GridBagConstraints();
		gbc_ipField.insets = new Insets(0, 0, 5, 0);
		gbc_ipField.fill = GridBagConstraints.HORIZONTAL;
		gbc_ipField.gridx = 1;
		gbc_ipField.gridy = 0;
		innerPanel.add(ipField, gbc_ipField);
		ipField.setColumns(10);
		
		JLabel lblPort = new JLabel("PORT");
		GridBagConstraints gbc_lblPort = new GridBagConstraints();
		gbc_lblPort.anchor = GridBagConstraints.EAST;
		gbc_lblPort.insets = new Insets(0, 0, 5, 5);
		gbc_lblPort.gridx = 0;
		gbc_lblPort.gridy = 1;
		innerPanel.add(lblPort, gbc_lblPort);
		
		portField = new JTextField();
		portField.setText("9999");
		GridBagConstraints gbc_portField = new GridBagConstraints();
		gbc_portField.insets = new Insets(0, 0, 5, 0);
		gbc_portField.fill = GridBagConstraints.HORIZONTAL;
		gbc_portField.gridx = 1;
		gbc_portField.gridy = 1;
		innerPanel.add(portField, gbc_portField);
		portField.setColumns(10);
		
		JLabel lblNickName = new JLabel("NICK NAME");
		GridBagConstraints gbc_lblNickName = new GridBagConstraints();
		gbc_lblNickName.anchor = GridBagConstraints.EAST;
		gbc_lblNickName.insets = new Insets(0, 0, 5, 5);
		gbc_lblNickName.gridx = 0;
		gbc_lblNickName.gridy = 2;
		innerPanel.add(lblNickName, gbc_lblNickName);
		
		nicknameField = new JTextField();
		GridBagConstraints gbc_nicknameField = new GridBagConstraints();
		gbc_nicknameField.insets = new Insets(0, 0, 5, 0);
		gbc_nicknameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_nicknameField.gridx = 1;
		gbc_nicknameField.gridy = 2;
		innerPanel.add(nicknameField, gbc_nicknameField);
		nicknameField.setColumns(10);
		
		JButton joinButton = new JButton("JOIN");
		joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processLogin();
			}
		});
		GridBagConstraints gbc_joinButton = new GridBagConstraints();
		gbc_joinButton.gridx = 1;
		gbc_joinButton.gridy = 3;
		innerPanel.add(joinButton, gbc_joinButton);

	}

	/**
	 * 로그인 과정
	 * 
	 * 1. 서버에 접속함(server ip:port)
	 * 2. 로그인 요청을 전송함.
	 * 3. 서버가 로그인 성공 응답을 보내주면 chat page로 전환함.
	 */
	protected void processLogin() {
		
		String host = ipField.getText().trim();
		int port = Integer.parseInt(portField.getText().trim());
		String nickName = nicknameField.getText().trim();
		try {
			// 1. 서버 접속
			main.service.connect(host, port);
			// 2. 로그인 요청 전송
			main.service.sendLoginRequest(nickName);
//			// 3. 페이지 전환
//			
//			main.setChatPage(nickName);
			main.service.sendChatterListRequest();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
