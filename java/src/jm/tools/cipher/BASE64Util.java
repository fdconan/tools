package jm.tools.cipher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Base64π§æﬂ¿‡
 * @author yjm
 *
 */
public abstract class BASE64Util {
	public static String encode(byte[] content) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(content);
	}

	public static String encode(ByteBuffer content) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(content);
	}
	
	public static void encode(byte[] content, OutputStream out) {
		BASE64Encoder encoder = new BASE64Encoder();
		try {
			encoder.encode(content, out);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void encode(ByteBuffer content, OutputStream out) {
		BASE64Encoder encoder = new BASE64Encoder();
		try {
			encoder.encode(content, out);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void encode(InputStream input, OutputStream out) {
		BASE64Encoder encoder = new BASE64Encoder();
		try {
			encoder.encode(input, out);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static String encodeBuffer(byte[] content) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encodeBuffer(content);
	}

	public static String encodeBuffer(ByteBuffer content) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encodeBuffer(content);
	}
	
	public static void encodeBuffer(byte[] content, OutputStream out) {
		BASE64Encoder encoder = new BASE64Encoder();
		try {
			encoder.encodeBuffer(content, out);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void encodeBuffer(ByteBuffer content, OutputStream out) {
		BASE64Encoder encoder = new BASE64Encoder();
		try {
			encoder.encodeBuffer(content, out);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void encodeBuffer(InputStream input, OutputStream out) {
		BASE64Encoder encoder = new BASE64Encoder();
		try {
			encoder.encodeBuffer(input, out);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static byte[] decode(String content) {
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			return decoder.decodeBuffer(content);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static byte[] decode(InputStream input) {
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			return decoder.decodeBuffer(input);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static void decode(InputStream input, OutputStream out) {
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			decoder.decodeBuffer(input, out);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static ByteBuffer decodeToByteBuffer(InputStream input) {
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			return decoder.decodeBufferToByteBuffer(input);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static ByteBuffer decodeToByteBuffer(String content) {
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			return decoder.decodeBufferToByteBuffer(content);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
