package kmh.jchat;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import kmh.jchat.common.BitConverter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
/**
 * 
 * Observer Pattern
 * 
 * @author Administrator
 *
 */
public class TestProtocol {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		/**
		 * 100 | "뭐하냐?" ->  10100111000110001101101  -> "뭐하냐?"
		 *         serialization(직렬화)            deserialization(역직렬화)
		 *         encoding(UTD-8, euc-kr)        decoding                                    
		 *         marsharling                    unmarsharling     ( java RMI )
		 *         
		 *         
		 */
	}
	
	@Test
	public void normal_message() throws IOException {
		byte protocolType = 100;
		String message = "뭐하냐?";
		byte [] data = message.getBytes("utf-8");
		/* 1 byte 8 bitss(11001011) 
		 * 
		 */
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// AAAAAAAABBBBBBBBCCCCCCCCDDDDDDDD
//		bos.write(protocolType); //1bytes
//		bos.write( data.length >>> 24 ); // 4bytes
//		bos.write( data.length >>> 16 ); // 4bytes
//		bos.write( data.length >>> 8 ); // 4bytes
//		bos.write( data.length        ); // 4bytes
		BitConverter.writeInt(bos, protocolType);
		
		// AAAAAAAABBBBBBBBCCCCCCCCDDDDDDDD
		//                         AAAAAAAA
		//                 AAAAAAAABBBBBBBB
		//         AAAAAAAABBBBBBBBCCCCCCCC
		bos.write(data, 0, data.length);
		
		// 수신측
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		////////////////////////////////////////////////////////////
		byte pType = (byte) bis.read();
		
		int len = 0;
		int b3 = bis.read();
		len += (b3 << 24);
		len += (bis.read() << 16);
		len += (bis.read() << 8);
		len += (bis.read() << 0);
		
		byte [] recv = new byte[len];
		bis.read(recv, 0, recv.length);
		BitConverter.readInt(bis);
		String msg = new String ( recv, "utf-8");
		
		
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(39393);
		
		DataInputStream dis = new DataInputStream(bis);
		dis.readInt();
	}
	
	@Test
	public void prive_message() {
		// protocole type : 101
		// receiver id    : 39383838
		// message
	}
	
	@Test
	public void message_image() {
		// protocole type : 102
		// filename: string
		// image size : int
		// image data: bytes[n]
	}

}
