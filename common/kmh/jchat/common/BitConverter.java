package kmh.jchat.common;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/**
 * READER vs STREAM
 * 
 * 2000�⵵ - READER�� ������. STREAM 
 * 
 * 'a' - byte
 * 'b' - byte
 * '��' - 'utf-8', 'euc-kr'
 * @author Administrator
 *
 */
public class BitConverter {
	private DataInputStream dis;
	
	/**
	 * stream ���� 4����Ʈ�� �о  int �� ��ȯ�մϴ�.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static int readInt(InputStream in ) throws IOException {
		int i24 = in.read() << 24;  // AAAAAA01 00000000 00000000 00000000 		
		int i16 = in.read() << 16 ; // 00000000 BBBBBBBB 00000000 00000000 
		int i08 = in.read() << 8;  //  00000000 00000000  CCCCCCCC 00000000 
		int i00 = in.read() << 0 ;  // 00000000 00000000  00000000 DDDDDDDD
		return i24 | i16 | i08 | i00;//AAAAAA01 BBBBBBBB CCCCCCCC DDDDDDDD
		
		
		//int�� 4byte = 4 * 8bit = 32 
	}
	/**
	 * stream�� 4����Ʈ�� �� �ֽ��ϴ�.
	 */
	public static void writeInt(OutputStream out, int value) throws IOException {
		// AAAAAAAABBBBBBBBCCCCCCCCDDDDDDDD
		// 000000000000000000000000AAAAAAAA  >> 24bits  1byte = 8bits 32bits = 4byte
		int i24 = value >>> 24 ; // 000000000000000000000000AAAAAAAA
		out.write(i24); // (000000000000000000000000)AAAAAAAA
		int i16 = value >>> 16 ; // 0000000000000000AAAAAAAABBBBBBBB
		out.write(i16);         // (0000000000000000AAAAAAAA) BBBBBBBB
		int i08 = value >>> 8;  // 00000000AAAAAAAABBBBBBBBCCCCCCCC
		out.write(i08);         // (00000000AAAAAAAABBBBBBBB)CCCCCCCC
		
		out.write(value);
	}
	
	public static String readString( InputStream in ) throws IOException {
		int size = readInt(in);//4byte
		byte [] data = new byte[size];
		in.read(data, 0, data.length);
                                          		
		return new String(data, "UTF-8");
	}
	public static void writeString ( OutputStream out, String value) throws IOException{
		// value = "������";
		byte [] data = value.getBytes("utf-8");
		int sz = data.length; // 64
	
		writeInt(out, sz);   /////////////////////////////////////////////////////->>>>>>>>>����
		out.write(data, 0, sz); // 32bits    4bytes  
		out.flush();  // 64 bytes   68bytes   
	}
	
	public static void writeBoolean ( OutputStream out, boolean b) throws IOException {
		out.write( b == true ? 1 : 0);
		out.flush();
	}
	public static boolean readBoolean(InputStream in) throws IOException {
		int v = in.read();
		if(v==1){
			return true;
		}else if(v==0){
			return false;
		} else {
			throw new IOException("boolean should be 1 or 0, but " + v);
		}
		
	}
	/**
	 * ���ڿ� �迭�� �����մϴ�.
	 * @param out
	 * @param values
	 * @throws IOException
	 */
	public static void writeStrings ( OutputStream out, String [] values) throws IOException {
	  int size = values.length;
       writeInt(out, size);	  
	  for(String v: values){
		  writeString(out, v);
	  }
	  out.flush();
	}
	
	/**
	 * ���ڿ� �迭�� �о���Դϴ�.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static String [] readStrings ( InputStream in ) throws IOException {
		
	     int size = readInt(in);
	     String []array = new String[size];
		
	      for(int i=0; i<size; i++){
	    	  array[i] = readString(in);
	      }
	     return array;
	}
	

}
