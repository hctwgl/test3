package com.ald.fanbei.api.biz.arbitration;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DesUtils {
	private static byte[] iv1 = { (byte) 0x12, (byte) 0x34, (byte) 0x56,
			(byte) 0x78, (byte) 0x90, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF };
	private static String padding = "DES/CBC/PKCS5Padding";
	private static String mode = "DES";
	
	private static String desKey = "z9H$@!$#";

	/**
	 * java字节码转字符串
	 * 
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) { // 一个字节的数，

		// 转成16进制字符串

		String hs = "";
		String tmp = "";
		for (int n = 0; n < b.length; n++) {
			// 整数转成十六进制表示
			tmp = (Integer.toHexString(b[n] & 0XFF));
			if (tmp.length() == 1) {
				hs = hs + "0" + tmp;
			} else {
				hs = hs + tmp;
			}
		}
		tmp = null;
		return hs.toLowerCase();

	}

	// 加密
	public static String encrypt(String content)
			throws Exception {
		
		return encrypt(content, desKey);
	}

	// 加密
	public static String encrypt(String content, String desKey)
			throws Exception {
		byte[] plainText = content.getBytes("UTF-8");
		IvParameterSpec iv = new IvParameterSpec(iv1);
		DESKeySpec dks = new DESKeySpec(desKey.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(mode);
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(padding);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte data[] = plainText;
		byte encryptedData[] = cipher.doFinal(data);
		return byte2hex(encryptedData);

	}

	/**
	 * 字符串转java字节码
	 * 
	 * @param b
	 * @return
	 */
	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("长度不是偶数");
		}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length - 1; n += 2) {
			String item = new String(b, n, 2);
			// 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		b = null;
		return b2;
	}

	public static String decrypt(String content)
			throws Exception {
		return decrypt(content, desKey);
	}
	
	public static String decrypt(String content, String desKey)
			throws Exception {
		byte[] data = content.getBytes("UTF-8");
		data = hex2byte(data);
		IvParameterSpec iv = new IvParameterSpec(iv1);
		DESKeySpec dks = new DESKeySpec(desKey.getBytes("UTF-8"));
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(mode);
		SecretKey key = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance(padding);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte encryptedData[] = cipher.doFinal(data);
		return new String(encryptedData);

	}

	public static void main(String[] args) throws Exception {
		System.out.print(encrypt("13958199623"));
	}
}