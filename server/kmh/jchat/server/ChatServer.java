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
/**
 * 추가로 들어가야 하는 부분들
 * 
 * 1. 닉네임 중복
 * 2. 닉네임 중복시 클라이언트에게 통보해줘야함(요청자가 알 수 잇게)
 * 3. 서바를 강제 껐을때 클라이언트 페이지가 그냥 남아있음. 그런데 이런 경우 메세지 뿌려주고 로그인 페이지로 튕겨 나가게 해야함.
 * 
 * 4. 강퇴 
 * 5. 귓속말
 * @author Administrator
 *
 */
public class ChatServer {

	private int port ;
	private static Logger logger = Logger.getLogger(ChatServer.class.getName()); 
	
	private List<ClientThread> clients = new ArrayList<>();
	public ChatServer(int port) {
		this.port = port;                //chatServer 생성자에 port번호 받는다
	}
	/**
	 * 닉네임 유일성 여부를 검사합니다.
	 * 
	 * @param nickname 검사할 닉네임
	 * @return 중복된 닉네임이면 false반환함. 중복되지 않았으면 true 반환함.
	 */
	public boolean isUniqueNickname ( String nickname ) {           //닉네임 유일성 검사 
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
	public String[] listNicknames() {                     //참여자들 닉네임 배열 반환 
		 String[] store = new String[clients.size()];
		  for( int i=0 ; i<store.length;i++){
			  store[i] = clients.get(i).nickName;
		  }
		return store;
	}
	
	public void startServer() throws IOException {                  //server시작 
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
		private boolean masterThread = false;
		
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
							BitConverter.writeString(out, nickname);
							broadcastNewChatter("EVENT_NEW_CHATTER", this);
							
							if(clients.size()==1){
								masterThread = true ;
								broadcastMasterChange ( null, this);
							}
							
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
					} else if ( "REQ_PUB_MSG".equals(type)){
						  String msg = BitConverter.readString(in); 
						  broadcastMsg(msg,this);
					} else if ("REQ_LOGOUT".equals(type)){
						  broadcastLogout(this);
						  break; // 이걸 해줘야 클라이언트 전담 스레드가 종료됨.
					}
				} catch (IOException e) {
					e.printStackTrace();
					broadcastClientExit ( this );
				}
			}// end while
			
			try {
				this.sock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public boolean isMaster() {
			return masterThread ;
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		int port = 9999; // "127.0.0.1:9999"
		
		ChatServer server = new ChatServer(port);
		server.startServer(); 
		
	}
	/*
	 * 나간사람 알려준다
	 * */
	public void broadcastLogout(ClientThread goodbyeChatter) {
		clients.remove(goodbyeChatter);
		String type="EVENT_LOGOUT";
		for(int i=0;i<clients.size();i++){
			try {
				BitConverter.writeString(clients.get(i).out, type);
				BitConverter.writeString(clients.get(i).out, goodbyeChatter.nickName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if ( goodbyeChatter.isMaster() ) {
			ClientThread newMaster = clients.get(0);
			newMaster.masterThread = true;
//			BitConverter.writeBoolean(out, b);
			broadcastMasterChange(goodbyeChatter, newMaster);
		}
	}
	/**
	 * 방장이 변경되었음.
	 * @param oldMaster
	 * @param newMaster
	 */
	public void broadcastMasterChange ( ClientThread oldMaster, ClientThread newMaster) {
		/*
		 * "EVENT_MASTR_CHANGE", "", "NEW MASTER MICKANME"
		 * 
		 *                     OR
		 *                     
		 * "EVENT_MASTR_CHANGE", "OLD MASTER NICKNAME", "NEW MASTER MICKANME"
		 * 
		 */
		for(int i=0;i<clients.size();i++){
		try {
			BitConverter.writeString(clients.get(i).out, "EVENT_MASTER_CHANGE");
			if(oldMaster==null){
				BitConverter.writeString(clients.get(i).out, "");
			}else{
				BitConverter.writeString(clients.get(i).out, oldMaster.nickName);
			}
			BitConverter.writeString(clients.get(i).out, newMaster.nickName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
	}
	/**
	 *나머지 채팅참여자들에게 메세지를 알린다  
	 */
	public void broadcastMsg(String msg, ClientThread sender) {
		System.out.println("boradcasting : " + msg);
		for (int i = 0; i < clients.size(); i++) {

			try {
				String type = "EVENT_PUB_MSG";
				BitConverter.writeString(clients.get(i).out, type);
				BitConverter.writeString(clients.get(i).out, sender.nickName );
				BitConverter.writeString(clients.get(i).out, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * 채팅 참여자를 제거합니다.
	 * @param clientThread
	 */
	public void broadcastClientExit(ClientThread clientThread) {
		// TODO 연결이 끊기거나 채팅 참여자가 명시적으로 방을 나갔을때 나머지 참여자들한테 통보해주어야 합니다.
		/*
		 * "EVENT_CHATTER_EXIT" , <나간 참여자 닉네임>
		 */
		
		
	}
	/**
	 * 새로운 대화참여자 발생함.
	 * @param type "EVENT_NEW_CHATTER"
	 * @param clientThread 새로 로그인한 참여자
	 */
	public void broadcastNewChatter(String type, ClientThread newChatter) {
		for(int i=0; i<clients.size();i++){
			
			try {
				BitConverter.writeString(clients.get(i).out, type);
				BitConverter.writeString(clients.get(i).out, newChatter.nickName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
