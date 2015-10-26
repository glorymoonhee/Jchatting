package kmh.jchat.client.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class WinMain extends JFrame {

	private JPanel contentPane;
	ClientService service ;

	JComponent activePage ;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WinMain main = new WinMain();
					main.service = new ClientService(main);
					main.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WinMain() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		LoginPage loginPanel = new LoginPage();
		loginPanel.main = this;
		contentPane.add(loginPanel, BorderLayout.CENTER);
		
		
	}
	
	/**
	 * 채팅 화면으로 바꿔줍니다.
	 */
	void setChatPage() {
		contentPane.removeAll();
		ChatPage page = new ChatPage();
		contentPane.add(page, BorderLayout.CENTER);
		contentPane.revalidate(); // 이거 호출 안하면 화면 갱신이 자동으로 안됩니다.
		
		page.main = this;
		activePage = page;
		
	}
	void setLoginPage() {
		contentPane.removeAll();
		LoginPage page = new LoginPage();
		page.main = this;
		
		contentPane.add(page, BorderLayout.CENTER);
		contentPane.revalidate(); // 이거 호출 안하면 화면 갱신이 자동으로 안됩니다.
		
		activePage = page;
	}

	/**
	 * 채팅 참여자들을 화면에 그려줍니다.
	 * @param nicknames
	 */
	public void updateChatters(String[] nicknames) {
		ChatPage page = (ChatPage) activePage;
		page.renderChatters ( nicknames);
		
	}
}
