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
	 * ä�� ȭ������ �ٲ��ݴϴ�.
	 */
	void setChatPage() {
		contentPane.removeAll();
		ChatPage page = new ChatPage();
		contentPane.add(page, BorderLayout.CENTER);
		contentPane.revalidate(); // �̰� ȣ�� ���ϸ� ȭ�� ������ �ڵ����� �ȵ˴ϴ�.
		
		page.main = this;
		activePage = page;
		
	}
	void setLoginPage() {
		contentPane.removeAll();
		LoginPage page = new LoginPage();
		page.main = this;
		
		contentPane.add(page, BorderLayout.CENTER);
		contentPane.revalidate(); // �̰� ȣ�� ���ϸ� ȭ�� ������ �ڵ����� �ȵ˴ϴ�.
		
		activePage = page;
	}

	/**
	 * ä�� �����ڵ��� ȭ�鿡 �׷��ݴϴ�.
	 * @param nicknames
	 */
	public void updateChatters(String[] nicknames) {
		ChatPage page = (ChatPage) activePage;
		page.renderChatters ( nicknames);
		
	}
}
