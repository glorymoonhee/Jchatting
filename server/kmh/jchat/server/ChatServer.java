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
 * �߰��� ���� �ϴ� �κе�
 * 
 * 1. �г��� �ߺ�
 * 2. �г��� �ߺ��� Ŭ���̾�Ʈ���� �뺸�������(��û�ڰ� �� �� �հ�)
 * 3. ���ٸ� ���� ������ Ŭ���̾�Ʈ �������� �׳� ��������. �׷��� �̷� ��� �޼��� �ѷ��ְ� �α��� �������� ƨ�� ������ �ؾ���.
 * 
 * 4. ���� 
 * 5. �ӼӸ�
 * @author Administrator
 *
 */
public class ChatServer {

	private int port ;
	private static Logger logger = Logger.getLogger(ChatServer.class.getName()); 
	
	private List<ClientThread> clients = new ArrayList<>();
	public ChatServer(int port) {
		this.port = port;                //chatServer �����ڿ� port��ȣ �޴´�
	}
	/**
	 * �г��� ���ϼ� ���θ� �˻��մϴ�.
	 * 
	 * @param nickname �˻��� �г���
	 * @return �ߺ��� �г����̸� false��ȯ��. �ߺ����� �ʾ����� true ��ȯ��.
	 */
	public boolean isUniqueNickname ( String nickname ) {           //�г��� ���ϼ� �˻� 
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
	public String[] listNicknames() {                     //�����ڵ� �г��� �迭 ��ȯ 
		 String[] store = new String[clients.size()];
		  for( int i=0 ; i<store.length;i++){
			  store[i] = clients.get(i).nickName;
		  }
		return store;
	}
	
	public void startServer() throws IOException {                  //server���� 
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
							BitConverter.writeString(out, nickname);
							broadcastNewChatter("EVENT_NEW_CHATTER", this);
							
							if(clients.size()==1){
								masterThread = true ;
								broadcastMasterChange ( null, this);
							}
							
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
					} else if ( "REQ_PUB_MSG".equals(type)){
						  String msg = BitConverter.readString(in); 
						  broadcastMsg(msg,this);
					} else if ("REQ_LOGOUT".equals(type)){
						  broadcastLogout(this);
						  break; // �̰� ����� Ŭ���̾�Ʈ ���� �����尡 �����.
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
	 * ������� �˷��ش�
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
	 * ������ ����Ǿ���.
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
	 *������ ä�������ڵ鿡�� �޼����� �˸���  
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
	 * ä�� �����ڸ� �����մϴ�.
	 * @param clientThread
	 */
	public void broadcastClientExit(ClientThread clientThread) {
		// TODO ������ ����ų� ä�� �����ڰ� ��������� ���� �������� ������ �����ڵ����� �뺸���־�� �մϴ�.
		/*
		 * "EVENT_CHATTER_EXIT" , <���� ������ �г���>
		 */
		
		
	}
	/**
	 * ���ο� ��ȭ������ �߻���.
	 * @param type "EVENT_NEW_CHATTER"
	 * @param clientThread ���� �α����� ������
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
