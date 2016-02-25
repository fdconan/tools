package jm.tools.cipher;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

/**
 * 3DES�ӽ��ܹ�����
 * @author yjm
 *
 */
public class ThreeDESHelper {
	// ��Կ
	private String key = "A7B3983A99FD687FD3176B18";
	private KeyGenerator keygen;
	// SecretKey���𱣴�Գ���Կ
	private SecretKey deskey;
	// Cipher������ɼ��ܻ���ܹ���
	private static final String Algorithm = "DESede"; //��������㷨,���� DES,DESede,Blowfish
	private Cipher c;
	public ThreeDESHelper(){
		// ʵ����֧��3DES�㷨����Կ���������㷨������DESede
		Security.addProvider(new com.sun.crypto.provider.SunJCE()); 
		try {
			keygen = KeyGenerator.getInstance(Algorithm);
			deskey = new SecretKeySpec(key.getBytes("ASCII"),
					keygen.getAlgorithm());
			c = Cipher.getInstance(Algorithm+"/ECB/PKCS5Padding"); // DESede���㷨��ECB�Ǽ���ģʽ��PKCS5Padding����䷽ʽ
		} catch (Exception e) {
			e.printStackTrace();
		} // ������Կ
	}
	
	/**
	 * ������Կ,24�ֽڳ���
	 * @param key
	 */
	public void setKey(String key){
		try {
			deskey = new SecretKeySpec(key.getBytes("ASCII"),
					keygen.getAlgorithm());
			this.key = key;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ����
	 * @param plainText ����
	 * @return
	 */
	public byte[] encrypt(String plainText){
		
		return this.encrypt(plainText, "GBK");
	}
	
	/**
	 * ����
	 * @param plainText ����
	 * @param encoding �����ַ���
	 * @return
	 */
	public byte[] encrypt(String plainText, String encoding){
		
		byte[] enc = null;
		try {
			byte[] src = plainText.getBytes(encoding);
			c.init(Cipher.ENCRYPT_MODE, deskey);
			// ���ܣ���������enc
			enc = c.doFinal(src);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return enc;
	}
	
	/**
	 * ����
	 * @param encText ����
	 * @return
	 */
	public byte[] decrypt(byte[] encText){
		byte[] dec = null;
		try {
			c.init(Cipher.DECRYPT_MODE, deskey);
			// ���ܣ���������dec
			dec = c.doFinal(encText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dec;
	}
	
	public static void main(String[] args) throws Exception{
		String msg = "12345678";
		String key = "pcisuntepcisuntepcisunte";
		ThreeDESHelper helper = new ThreeDESHelper();
		helper.setKey(key);
		//helper.setKey("pcisuntek_3des_0pcisunte");
		//helper.setKey("pcisunte");
		//helper.setKey("EB664C30A17E35E75565B9B9");
		byte[] enc = helper.encrypt(msg,"utf-8");
		//System.out.println("���ĳ��ȣ�" + enc.length); 
		BASE64Encoder encoder = new BASE64Encoder();
		System.out.println("���ģ�" + msg); 
		System.out.println("��Կ��" + key); 
		System.out.println("�����ǣ�" + encoder.encode(enc)); 
		
		
		
		byte[] dec = helper.decrypt(enc);
		System.out.println("���ܺ�Ľ���ǣ�" + new String(dec, "utf-8"));
	}
}
