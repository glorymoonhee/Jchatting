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
	 * 닉네임 유일성 여부를 검사합니다.
	 * 
	 * @param nickname 검사할 닉네임
	 * @return 중복된 닉네임이면 false반환함. 중복되지 않았으면 true 반환함.
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
	 * 현재 채팅 참여자들의 닉네임의 배열을 반환합니다.
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
						// 클라이언트가 로그인 요청을 보냈음, 닉네임 확이해야함.
						String nickname = BitConverter.readString(in);
						logger.info("checking nick: " + nickname);
						if ( isUniqueNickname(nickname)) {
							this.nickName = nickname;
							this.setName("T-" + nickname);
							clients.add(this);
							// 성공 응답을 클라이인틍한테 전송함.
							BitConverter.writeString(out, "RES_LOGIN");
							BitConverter.writeBoolean(out, true);
						} else {
							// 실패 응답을 클라이언트한테 전송함.
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
