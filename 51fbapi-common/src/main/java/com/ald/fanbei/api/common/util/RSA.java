package com.ald.fanbei.api.common.util;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *@类描述：RSA
 *@author 陈金虎 2017年3月6日下午13:12:27
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RSA {
	private static final Logger logger = Logger.getLogger(RSA.class);
	//典型的输出如下：
	//私钥PKCS#8 ; 30820277020100300d06092a864886f70d0101010500048202613082025d02010002818100d32309f121281dcf7bdce9908ed257a5894e94bde6c7e4d8577bbeea52548612f056f4348865696f6edd689e7368b6fe49bb7c6fccbf406baf2bb444fbe8098024d4bd607cd14ad0e6618cccb88ed57c55bb5d5e8f72223160427f3ffc51fbcb0ca278e34997d819e4cfadee3ee6857612192164cac75f0b71779770d992434d02030100010281807ddc26420a6cda90639ac5d4797ee3e676da540a7bf6eef113fd5d4a5b77ad50beda66caa6002600da4abbe1b0945c796b448b222e2057eb99496935b220990027f52b03d259fb198083473ffd77a728e26e8af72822accf172cde632b936c401c038014764603408dd60ba94fe97962f6777f6689ce979c825c159573456975024100f3e0c389079fd45fb64b4c1ce4f15a942df96622cf2d9f61b371111967fd3b1b21a41d1d41eb0782b60ab1c2fbdf85d875dd83b4cb78e470e9aca9c3d26fe63f024100dda1a8c6c59763a38c8ade6118961092004cd9c7c3d8b93d9e6a4a96915a28f080451be3de8bd9bdbd32bc41ba4f67cd93fc834fe2a66daff3ae6fe09e8eeb73024100bd125f660844308937b22ec323f7be2aad6df272403ef596f060534a68259a4251958380e04f0f9695fdba8d196ad4c0d9b82fd8e5fac6d8874176a83ba8c74f024008c08585b3efd28376dd5502c891c47dbf7d496c56138cdd1e105c79adabbdca2186991f5a7e734ec6d5e6114266385729ece2d298c4b1b82d0b4a67b85b3479024100c37f06d0bfd3c1c23082c4eec2bc43c475d14af26c6d01cb4d77526c0a15bae6d062a8cb9561b8e5b616f8d23a4a7c95d3a07716a0410682cdfc1beca4408d74
	//公钥der ; 30818902818100d32309f121281dcf7bdce9908ed257a5894e94bde6c7e4d8577bbeea52548612f056f4348865696f6edd689e7368b6fe49bb7c6fccbf406baf2bb444fbe8098024d4bd607cd14ad0e6618cccb88ed57c55bb5d5e8f72223160427f3ffc51fbcb0ca278e34997d819e4cfadee3ee6857612192164cac75f0b71779770d992434d0203010001
	//其中：私钥PKCS#8商户留存用于计算签名，公钥der提供给账户系统用于验证商户的签名
	public static void main(String[] args) throws Exception {
		RSA.generateRSAKey();
	}

	/**
	 * 生成一对公钥和私钥的方法，返回map
	 * @return Map
	 * priKey：私钥
	 * pubKey:公钥
	 */
	public static Map<String, String> generateRSAKey() {
		Map<String, String> rasData = new HashMap<String, String>();
		try {
			SecureRandom sr = new SecureRandom();
			KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA",new BouncyCastleProvider());
			kg.initialize(1024, sr);
			KeyPair keyPair = kg.generateKeyPair();
			PrivateKey priKey = keyPair.getPrivate();
			PublicKey pubKey = keyPair.getPublic();
			byte[] publicKey = pubKey.getEncoded();
			byte[] privateKey = priKey.getEncoded();
			//生成加密私钥
			String priKeyStr = RSAUtil.byteArr2HexString(privateKey);
			rasData.put("priKey", priKeyStr);
			logger.debug("私钥"+priKey.getFormat() + " ; " + priKeyStr);
			String x509key = RSAUtil.byteArr2HexString(publicKey);
			String pubKeyStr = RSAUtil.getRsaPublicKeyDerFromX509(x509key);
			rasData.put("pubKey", pubKeyStr);
			logger.debug("公钥der ; "+pubKeyStr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return rasData;
	}
}
