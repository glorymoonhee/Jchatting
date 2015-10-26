package kmh.jchat;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import kmh.jchat.common.BitConverter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBitConverter {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BitConverter.writeInt(bos, 584647881);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		int value = BitConverter.readInt(bis);
		
		assertEquals ( 584647881, value);
	}
	
	@Test
	public void test_string() throws IOException {
	//	String s = "dlakdfkdsajflkdsjaflkdsjlk"; 
	//	byte [] data = s.getBytes();
		
	//	 new String(data, "UTF-8");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BitConverter.writeString(bos, "가나다ABC");
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		String s = BitConverter.readString(bis);
		
		assertEquals ( "가나다ABC", s);
	}

}
