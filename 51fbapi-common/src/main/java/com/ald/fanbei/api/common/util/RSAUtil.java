package com.ald.fanbei.api.common.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.RSAPublicKeyStructure;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.dbunit.util.Base64;

/**
 *@类描述：rsa签名辅助工具类
 *@author 陈金虎 2017年3月6日下午13:12:27
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RSAUtil {

	private static final Logger logger = Logger.getLogger(RSAUtil.class);
	
	/**
	 * 默认的key生成方式，根据该参数灵活管理 rsa加密、解密、签名、验签
	 */
	public static String KEY_GEN_STYLE = "openssl";
	
	/**
	 * key生成方式 - openssl生成
	 */
	private static final String KEY_GEN_STYLE_OPENSSL = "openssl";
	
	/**
	 * key生成方式 - RSA.java类代码生成
	 */
	private static final String KEY_GEN_STYLE__CODE = "code";
	
	/**
	 * x509格式公钥转换为Der格式
	 *
	 * @param x509PublicKey x509格式公钥字符串
	 * @return Der格式公钥字符串
	 */
	public static String getRsaPublicKeyDerFromX509(String x509PublicKey) {
		try {
			ASN1InputStream aIn = new ASN1InputStream(hexString2ByteArr(x509PublicKey));
			SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(aIn.readObject());
			RSAPublicKeyStructure struct = RSAPublicKeyStructure.getInstance(info.getPublicKey());
			if (aIn != null)
				aIn.close();
			return byteArr2HexString(struct.getDERObject().getEncoded());
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 从Der格式公钥生成公钥
	 *
	 * @param publicKeyDer Der格式公钥字符串
	 * @return 公钥
	 */
	public static PublicKey generatePublicKeyFromDer(String publicKeyDer) {
		try {
			ASN1InputStream aIn = new ASN1InputStream(hexString2ByteArr(publicKeyDer));
			RSAPublicKeyStructure pStruct = RSAPublicKeyStructure.getInstance(aIn.readObject());
			RSAPublicKeySpec spec = new RSAPublicKeySpec(pStruct.getModulus(), pStruct.getPublicExponent());
			KeyFactory kf = KeyFactory.getInstance("RSA");
			if (aIn != null)
				aIn.close();
			return kf.generatePublic(spec);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 生成私钥文件
	 * @param privateKeyStr
	 * @return
	 */
	public static PrivateKey generatePrivateKey(String privateKeyStr) {
		try {
			byte[] buffer = keyToBytes(privateKeyStr, KEY_GEN_STYLE);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey key = keyFactory.generatePrivate(keySpec);
			return key;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 私钥加密
	 * @param privateKeyStr 私钥字符串
	 * @param str 加密原串
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String privateKeyStr, String str){
		PrivateKey privateKey = RSAUtil.generatePrivateKey(privateKeyStr);
		if (privateKey == null) {
			throw new RuntimeException("加密私钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			byte[] output = cipher.doFinal(str.getBytes("UTF-8"));
			// byte数组base64编码后存在非法字符，所以需要再base64编码一次
			return Base64.encodeString(Base64.encodeBytes(output));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("无此加密算法");
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException("使用默认RSA异常,请检查");
		} catch (InvalidKeyException e) {
			throw new RuntimeException("加密私钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException("明文长度非法");
		} catch (BadPaddingException e) {
			throw new RuntimeException("明文数据已损坏");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持的编码");
		}
	}
	
	
	/**
	 * 生成公钥，主要用于openssl生成的key
	 * @param publicKeyStr
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey genPublicKey(String publicKeyStr) {
		try {
			byte[] publicKeyData = Base64.decode(publicKeyStr);
			X509EncodedKeySpec keySpec  = new X509EncodedKeySpec(publicKeyData);
	        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        PublicKey key = keyFactory.generatePublic(keySpec);
	        return key;
		} catch (Exception e) {
			logger.info("公钥生成有误：", e);
			return null;
		}
        
	}
	
	/**
	 * 生成公钥
	 * @param publicKeyStr
	 * @param genStyle
	 * @return
	 */
	public static PublicKey genPublicKey(String publicKeyStr, String genStyle) {
		if (KEY_GEN_STYLE_OPENSSL.equals(genStyle)) {
			return genPublicKey(publicKeyStr);
		} else if (KEY_GEN_STYLE__CODE.equals(genStyle)) {
			return generatePublicKeyFromDer(publicKeyStr);
		}
		return null;
	}
	
	/**
	 * key转byte数组
	 * @param keyStr
	 * @param genStyle
	 * @return
	 */
	public static byte[] keyToBytes(String keyStr,  String genStyle) {
		if (KEY_GEN_STYLE_OPENSSL.equals(genStyle)) {
			return Base64.decode(keyStr);
		} else if (KEY_GEN_STYLE__CODE.equals(genStyle)) {
			return hexString2ByteArr(keyStr);
		} 
		return null;
	}
	
	
	/**
	 * 公钥解密
	 * @param publicKeyStr 公钥字符串
	 * @param encryptStr 密文
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String publicKeyStr, String encryptStr)  {
		// 
		if (StringUtil.isBlank(encryptStr)) {
			return "";
		}
		
		// 因为密文为base64编码后数据，所以需要先base64解码
		encryptStr = Base64.encodeString(encryptStr);
		PublicKey publicKey = genPublicKey(publicKeyStr, KEY_GEN_STYLE);
		if (publicKey == null) {
			throw new RuntimeException("解密公钥为空, 请设置");
		}
		Cipher cipher = null;
		try {
			// 使用默认RSA
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(Base64.decode(encryptStr));
			return new String(output,Charset.forName("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("无此解密算法");
		} catch (NoSuchPaddingException e) {
			throw new RuntimeException("使用默认RSA异常,请检查");
		} catch (InvalidKeyException e) {
			throw new RuntimeException("解密公钥非法,请检查");
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException("密文长度非法");
		} catch (BadPaddingException e) {
			throw new RuntimeException("密文数据已损坏");
		}
	}

	/**
	 * 字节数组转换为十六进制字符串
	 *
	 * @param byteArr 字节数组
	 * @return 十六进制字符串
	 */
	public static String byteArr2HexString(byte[] byteArr) {
		if (byteArr == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer();

		for (int k = 0; k < byteArr.length; k++) {
			if ((byteArr[k] & 0xFF) < 16) {
				sb.append("0");
			}
			sb.append(Integer.toString(byteArr[k] & 0xFF, 16));
		}
		return sb.toString();
	}

	/**
	 * 十六进制字符串转换为字节数组
	 *
	 * @param hexString 十六进制字符串
	 * @return 字节数组
	 */
	public static byte[] hexString2ByteArr(String hexString) {
		if ((hexString == null) || (hexString.length() % 2 != 0)) {
			return new byte[0];
		}

		byte[] dest = new byte[hexString.length() / 2];

		for (int i = 0; i < dest.length; i++) {
			String val = hexString.substring(2 * i, 2 * i + 2);
			dest[i] = (byte) Integer.parseInt(val, 16);
		}
		return dest;
	}
	
}
