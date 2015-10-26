package kmh.jchat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import kmh.jchat.common.BitConverter;

public class ChatServer {

	private int port ;
	private static Logger logger = Logger.getLogger(ChatServer.class.getName()); 
	
	private List<ClientThread> clients = new ArrayList<>();
	public ChatServer(int port) {
		this.port = port;
	}
	/**
	 * �г��� ���ϼ� ���θ� �˻��մϴ�.
	 * 
	 * @param nickname �˻��� �г���
	 * @return �ߺ��� �г����̸� false��ȯ��. �ߺ����� �ʾ����� true ��ȯ��.
	 */
	public boolean isUniqueNickname ( String nickname ) {
		for( ClientThread c : clients){
			 if(c.nickName.equals(nickname)){
				 return false;
			 }
		}
		return true;
	}
	/**
	 * ���� ä�� �����ڵ��� �г����� �迭�� ��ȯ�մϴ�.
	 * @return
	 */
	public String[] listNicknames() {
		 String[] store = new String[clients.size()];
		  for( int i=0 ; i<store.length;i++){
			  store[i] = clients.get(i).nickName;
		  }
		return store;
	}
	
	public void startServer() throws IOException {
		ServerSocket ssock = new ServerSocket(port);
		logger.info("starting chat sever at " + port);
		
		while ( true ) {
			
			Socket sock = ssock.accept(); // blocking!
			ClientThread thread = new ClientThread(sock);
			thread.start();
//			clients.add(thread);
		}
	}
	
	class ClientThread extends Thread {
		Socket sock;
		InputStream in;
		OutputStream out;
		String nickName;
		
		public ClientThread ( Socket sock) throws IOException {
			this.sock = sock;
			this.in = sock.getInputStream();
			this.out = sock.getOutputStream();
		}
		
		@Override
		public void run() {
			while ( true ) {
				try {
					String type = BitConverter.readString(in);
					
					if ( "REQ_LOGIN".equals(type)) {
						// Ŭ���̾�Ʈ�� �α��� ��û�� ������, �г��� Ȯ���ؾ���.
						String nickname = BitConverter.readString(in);
						logger.info("checking nick: " + nickname);
						if ( isUniqueNickname(nickname)) {
							this.nickName = nickname;
							this.setName("T-" + nickname);
							clients.add(this);
							// ���� ������ Ŭ�����κv���� ������.
							BitConverter.writeString(out, "RES_LOGIN");
							BitConverter.writeBoolean(out, true);
						} else {
							// ���� ������ Ŭ���̾�Ʈ���� ������.
							BitConverter.writeString(out, "RES_LOGIN");
							BitConverter.writeBoolean(out, false);
							BitConverter.writeString(out, "DUP_NICK");
							break;
						} 
					} else if ("REQ_CHATTERS".equals(type)){
						  String [] nicknames = listNicknames();
						  BitConverter.writeString(out, "RES_CHATTERS");
						  BitConverter.writeStrings(out, nicknames);
					}   
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}// end while
			
			try {
				this.sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		int port = 9999; // "127.0.0.1:9999"
		
		ChatServer server = new ChatServer(port);
		server.startServer(); 
		
	}
	
}
