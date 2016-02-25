package jm.tools.cipher;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Encoder;

/**
 * 3DES加解密工具类
 * @author yjm
 *
 */
public class ThreeDESHelper {
	// 密钥
	private String key = "A7B3983A99FD687FD3176B18";
	private KeyGenerator keygen;
	// SecretKey负责保存对称密钥
	private SecretKey deskey;
	// Cipher负责完成加密或解密工作
	private static final String Algorithm = "DESede"; //定义加密算法,可用 DES,DESede,Blowfish
	private Cipher c;
	public ThreeDESHelper(){
		// 实例化支持3DES算法的密钥生成器，算法名称用DESede
		Security.addProvider(new com.sun.crypto.provider.SunJCE()); 
		try {
			keygen = KeyGenerator.getInstance(Algorithm);
			deskey = new SecretKeySpec(key.getBytes("ASCII"),
					keygen.getAlgorithm());
			c = Cipher.getInstance(Algorithm+"/ECB/PKCS5Padding"); // DESede是算法，ECB是加密模式，PKCS5Padding是填充方式
		} catch (Exception e) {
			e.printStackTrace();
		} // 生成密钥
	}
	
	/**
	 * 设置密钥,24字节长度
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
	 * 加密
	 * @param plainText 明文
	 * @return
	 */
	public byte[] encrypt(String plainText){
		
		return this.encrypt(plainText, "GBK");
	}
	
	/**
	 * 加密
	 * @param plainText 明文
	 * @param encoding 明文字符集
	 * @return
	 */
	public byte[] encrypt(String plainText, String encoding){
		
		byte[] enc = null;
		try {
			byte[] src = plainText.getBytes(encoding);
			c.init(Cipher.ENCRYPT_MODE, deskey);
			// 加密，结果保存进enc
			enc = c.doFinal(src);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return enc;
	}
	
	/**
	 * 解密
	 * @param encText 密文
	 * @return
	 */
	public byte[] decrypt(byte[] encText){
		byte[] dec = null;
		try {
			c.init(Cipher.DECRYPT_MODE, deskey);
			// 解密，结果保存进dec
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
		//System.out.println("密文长度：" + enc.length); 
		BASE64Encoder encoder = new BASE64Encoder();
		System.out.println("明文：" + msg); 
		System.out.println("密钥：" + key); 
		System.out.println("密文是：" + encoder.encode(enc)); 
		
		
		
		byte[] dec = helper.decrypt(enc);
		System.out.println("解密后的结果是：" + new String(dec, "utf-8"));
	}
}
