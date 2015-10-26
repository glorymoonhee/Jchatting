package kmh.jchat.codec;

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
	public void test_string_arrays() throws IOException {
		String [] src = {"감자", "고구마", "양파"};
		ByteArrayOutputStream bos = new ByteArrayOutputStream(); // socket.getOutputStrem();
		BitConverter.writeStrings(bos, src);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray()); // socket.getInputStream();
		
		String [] dest = BitConverter.readStrings(bis);
		
		assertArrayEquals(src, dest);
		
		
	}

}
