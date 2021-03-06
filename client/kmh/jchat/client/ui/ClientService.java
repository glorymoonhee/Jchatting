package kmh.jchat.client.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import kmh.jchat.common.BitConverter;

public class ClientService {
	
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	
	WinMain main; 
	
	private static Logger logger = Logger.getLogger(ClientService.class.getName());
	
	public ClientService ( WinMain main) {
		this.main = main;
	}
	/**
	 * 채팅 서버에 연결을 합니다.
	 * @param host
	 * @param port
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public void connect ( String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		this.in = socket.getInputStream();
		this.out = socket.getOutputStream();
		
		ReceiverThread thread = new ReceiverThread();
		thread.start();
		
	}
	/**
	 * 서버로 로그인 요청을 보냅니다.
	 * "REQ_LOGIN" nickname
	 * @param nickName
	 */
	public void sendLoginRequest(String nickName) {
		String type = "REQ_LOGIN";
		
		try {
			BitConverter.writeString(out, type);
			BitConverter.writeString(out, nickName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendChatterListRequest() {
		String type="REQ_CHATTERS";
		try {
			BitConverter.writeString(out, type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public boolean sendMessageRequest(String msg) {
		String type="REQ_PUB_MSG";
		try {
			BitConverter.writeString(out, type);
			BitConverter.writeString(out, msg);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	class ReceiverThread extends Thread {
		@Override
		public void run() {
			while ( true ) {
				try {
					String type = BitConverter.readString(in);
					if ( "RES_LOGIN".equals(type)) {
						processLoginResponse();
					} else if ( "RES_LOGOUT".equals(type)) {
						// 로그아웃 요청을 보냈을때 그냥 요청자가 화면 전환을 하게 구현을 했음.
					} else if ( "RES_CHATTERS".equals(type)) {
						
						String [] nicknames = BitConverter.readStrings(in);
						main.updateChatters( nicknames );
					} else if ( "RES_PRIV_MSG".equals(type)) {
						;
					} else if ( "EVENT_NEW_CHATTER".equals(type)) {
						String newChatterName = BitConverter.readString(in);
						main.addChatter ( newChatterName);
					} else if ( "EVENT_PUB_MSG".equals(type)) {
						String sender = BitConverter.readString(in);
						String msg = BitConverter.readString(in);
						System.out.println("new message: " + msg );
						main.add_pub_msg(sender,msg);
					} else if("EVENT_LOGOUT".equals(type)) {
						String logoutchatter = BitConverter.readString(in);
						main.removeChatter(logoutchatter);
					} else if ( "EVENT_MASTER_CHANGE".equals(type)){
						
						String oldMaster = BitConverter.readString(in);
						String newMaster = BitConverter.readString(in);
						main.updateMaster(oldMaster,newMaster);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 로그인 요청에 대한 응답을 처리합니다.
	 * 
	 */
	public void processLoginResponse() {
		boolean loginOK;
		try {
			loginOK = BitConverter.readBoolean(in);
			if ( loginOK ) {
				logger.info("로그인성공. 페이지 전환");
				String myNick = BitConverter.readString(in);
				main.setChatPage(myNick);
			} else {
				// 오류메세지를 대화창에 띄워서 보여줌.
				String cause = BitConverter.readString(in);// "DUP_NICK"
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendLogoutRequest() {
		
		 try {
			BitConverter.writeString(out, "REQ_LOGOUT");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
