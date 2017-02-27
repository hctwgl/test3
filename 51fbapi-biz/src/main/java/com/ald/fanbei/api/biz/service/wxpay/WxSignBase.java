package com.ald.fanbei.api.biz.service.wxpay;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;

/**
 * 
 *@类描述：微信支付签名
*@author hexin 2017年2月27日 下午17:03:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WxSignBase {
	public static final String SR_SHA1PRNG = "SHA1PRNG";

	public static final String DEFAULT_RANDOM = SR_SHA1PRNG;

	protected SecureRandom rng;
	
	
	public static String checkWxSign(Properties properties,String appKey){
		List<Object> paramNamesObj = new ArrayList<Object>(properties.keySet());
		List<String> paramNames = CollectionConverterUtil.convertToListFromList(paramNamesObj, new Converter<Object, String>() {
			@Override
			public String convert(Object source) {
				if(source == null || source.toString().equals("xml")){
					return null;
				}
				return source.toString();
			}
		});
		Collections.sort(paramNames);
		
		WxURLBuilder ub = new WxURLBuilder();
		for (String key : paramNames) {
			if (WxpayConfig.KEY_SIGN.equals(key))
				continue;

			String value = properties.get(key) == null?null:properties.get(key)+"";
			if (value == null || value.isEmpty())
				continue;

			ub.appendParam(key, value);
		}
		ub.appendParam("key", appKey);
//		return (ub.toString());
		
		
		return ub.toString();
	}

	/**
	 * 设定随机数生成器, 一般无需调用这个方法
	 */
	public void setRng(String algorithm) throws NoSuchAlgorithmException {
		this.rng = SecureRandom.getInstance("SHA1PRNG");

		return;
	}

	// SINGLETON
	public WxSignBase() {
		try {
			this.setRng(SR_SHA1PRNG);
		} catch (Exception ex) {
			throw (new RuntimeException(ex));
		}

		return;
	}

	private static class Singleton {
		public static WxSignBase instance = new WxSignBase();
	}

	public static WxSignBase getInstance() {
		return (Singleton.instance);
	}

	// DIGEST
	/**
	 * 计算字节流 in 的 MD5 通常需要用 bytesToHex 转换为可读的字符串
	 */
	public static byte[] MD5Digest(byte[] in) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte[] out = md.digest(in);

			return (out);
		} catch (NoSuchAlgorithmException ex) {
			// never occur
			ex.printStackTrace();
			return (null);
		}
	}

	/**
	 * 计算字节流 in 的 SHA-1 通常需要用 bytesToHex 转换为可读的字符串
	 */
	public byte[] SHA1Digest(byte[] in) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			byte[] out = md.digest(in);

			return (out);
		} catch (NoSuchAlgorithmException ex) {
			// never occur
			ex.printStackTrace();
			return (null);
		}
	}

	// BASE64
	/**
	 * 转换字节数组 bytes 为 base64 编码
	 */
	public String base64Encode(byte[] bytes) {
		return (Base64.encode(bytes));
	}

	/**
	 * 转换Base64串 b64String 为 字节数组
	 */
	public byte[] base64Decode(String b64String) {
		return (Base64.decode(b64String));
	}

	// RANDOM
	/**
	 * 生成长度为 length 的随机字节数组
	 */
	public byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];

		this.rng.nextBytes(bytes);
		return (bytes);
	}

	// CONVERT
	/**
	 * 转换十六进制字符串为字节数组
	 */
	public static byte[] hexToBytes(String s) {
		int l = s.length() / 2;

		ByteBuffer buf = ByteBuffer.allocate(l);
		for (int i = 0; i < s.length(); i += 2) {
			buf.put(Integer.valueOf(s.substring(i, i + 2), 16).byteValue());
		}

		return (buf.array());
	}

	/**
	 * 转换字节数组为十六进制字符串
	 */
	public static String byteToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++)
			sb.append(String.format("%02x", bytes[i] & 0xff));

		return (sb.toString());
	}

	/**
	 * @copy
	 */
	public static String bytesToHex(byte[] bytes) {
		return (byteToHex(bytes));
	}
}
