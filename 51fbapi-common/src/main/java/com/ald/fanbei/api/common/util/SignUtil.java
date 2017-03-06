package com.ald.fanbei.api.common.util;


import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

import org.apache.log4j.Logger;

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
		logger.debug("签名明文："+str);
		logger.debug("签名私钥："+privateKey);
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
		
		logger.debug("签名参数sign："+signature);
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
		logger.info("验签明文："+str);
		logger.info("签名参数sign："+sign);
		logger.info("签名公钥："+pubKey);
		
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
	
	public static void main(String[] args) {
		System.out.println(sign("bankCode=ICBC&cardNo=6212261202009408759&certNo=33018319901101383X&certType=01&clientType=02&merNo=01151209000&merPriv=&notifyUrl=http://testapp.51fanbei.com/third/ups/authSignNotify&orderNo=01sign671170221488779150319&phone=17767117022&realName=何鑫&reqExt=&returnUrl=http://testapp.51fanbei.com/third/ups/authSignReturn&service=authSign&userNo=17&version=10"
				, "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs="));
	}
}
