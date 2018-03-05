package com.ald.fanbei.api.common.util;


import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.log4j.Logger;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi;
import sun.misc.BASE64Encoder;

/**
 *@类描述：ras生成签名和验签的工具类
 *@author 陈金虎 2017年3月6日下午13:12:27
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SignUtil {
	
	private static final Logger logger = Logger.getLogger(SignUtil.class);
	

	/**
	 * 生成签名的方法,必须为utf-8格式
	 * @param str  签名明文字符串
	 * @param privateKey  私钥
	 * @return
	 */
	public static String sign(String str,String privateKey){
		String signature = null;
		try {
			byte[] prikeybytes = RSAUtil.keyToBytes(privateKey, RSAUtil.KEY_GEN_STYLE);
			// 构造PKCS8EncodedKeySpec对象
			PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(prikeybytes);
			// 指定的加密算法
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			// 取私钥匙对象
			PrivateKey privatekey = keyFactory.generatePrivate(pkcs8KeySpec);
			Signature instance = Signature.getInstance("SHA1withRSA");
			instance.initSign(privatekey);
			byte[] digest = str.getBytes("UTF-8");
			instance.update(digest);
			byte[] sign = instance.sign();
			signature = RSAUtil.byteArr2HexString(sign);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return signature;
	}
	
	/**
	 * 校验签名参数,必须为utf-8格式
	 * @param str  签名明文
	 * @param sign  签名参数
	 * @param pubKey 公钥
	 * @return 返回true和false，true代表验签通过，false代表验签失败
	 */
	public static boolean verifySign(String str,String sign,String pubKey){
		boolean flag = false;
		try {
			Signature instance = Signature.getInstance("SHA1withRSA");
			instance.initVerify(RSAUtil.genPublicKey(pubKey, RSAUtil.KEY_GEN_STYLE));
			byte[] digest = str.getBytes("UTF-8");
			instance.update(digest);
			flag = instance.verify(RSAUtil.hexString2ByteArr(sign));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return flag;
	}




	/**
	 *  有得卖的签名方式
	 * @param str  签名明文字符串
	 * @param privateKey  私钥
	 * @return
	 */
	public static String signForYdm(String str,String privateKey){
		String sign = null;
		try {
			String param= str + "&key=" + privateKey;
			//确定计算方法
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			BASE64Encoder base64en = new BASE64Encoder();
			//加密后的字符串
			sign = base64en.encode(md5.digest(str.getBytes("utf-8")));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return sign;
	}
}
