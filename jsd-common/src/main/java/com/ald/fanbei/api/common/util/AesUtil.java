package com.ald.fanbei.api.common.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *@类AesUtil.java 的实现描述：
 *@author 陈金虎 2017年1月16日 下午11:23:44
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AesUtil {

    private static final Logger logger = LoggerFactory.getLogger(AesUtil.class);

    /**
     * 加密
     * 
     * @param content 需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
//            kgen.init(128, new SecureRandom(password.getBytes()));

            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(password.getBytes());
            kgen.init(128,secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("UTF-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            logger.error("encrypt NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            logger.error("encrypt NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            logger.error("encrypt InvalidKeyException", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("encrypt UnsupportedEncodingException", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("encrypt IllegalBlockSizeException", e);
        } catch (BadPaddingException e) {
            logger.error("encrypt BadPaddingException", e);
        }
        return null;
    }
    
    /**
     * 解密
     * 
     * @param content 待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decrypt(String contentStr, String password) {
        try {
        	byte[] content = Base64.decodeBase64(contentStr.getBytes());
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
//            kgen.init(128, new SecureRandom(password.getBytes()));
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(password.getBytes());
            kgen.init(128,secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return new String(result); // 加密
        } catch (NoSuchAlgorithmException e) {
            logger.error("decrypt NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            logger.error("decrypt NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            logger.error("decrypt InvalidKeyException", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("decrypt IllegalBlockSizeException", e);
        } catch (BadPaddingException e) {

            logger.error("decrypt BadPaddingException", e);
        }
        return null;
    }

    /**
     * 第三方交互解密
     *
     * @param content 待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decryptThird(byte[] content, String password) {
        try {
            byte[] enCodeFormat = password.getBytes("UTF-8");
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            logger.error("decrypt NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            logger.error("decrypt NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            logger.error("decrypt InvalidKeyException", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("decrypt IllegalBlockSizeException", e);
        } catch (BadPaddingException e) {
            logger.error("decrypt BadPaddingException", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("decrypt UnsupportedEncodingException", e);
        }
        return null;
    }

    public static String decryptFromBase64Third(String base64Str, String password) {
        String result = "";
        try {
            result = new String(decryptThird(Base64.decodeBase64(base64Str.getBytes("UTF-8")), password),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 解密
     * 
     * @param content 待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
//            kgen.init(128, new SecureRandom(password.getBytes()));
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(password.getBytes());
            kgen.init(128,secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            logger.error("decrypt NoSuchAlgorithmException", e);
        } catch (NoSuchPaddingException e) {
            logger.error("decrypt NoSuchPaddingException", e);
        } catch (InvalidKeyException e) {
            logger.error("decrypt InvalidKeyException", e);
        } catch (IllegalBlockSizeException e) {
            logger.error("decrypt IllegalBlockSizeException", e);
        } catch (BadPaddingException e) {
            logger.error("decrypt BadPaddingException", e);
        }
        return null;
    }

    public static String encryptToBase64(String content, String password) {
        byte[] encryptResult = encrypt(content, password);
        try {
			return new String(Base64.encodeBase64(encryptResult),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("encryptToBase64",e);
			e.printStackTrace();
		}
        return "";
    }

    public static String decryptFromBase64(String base64Str, String password) {
        String result = "";
		try {
			result = new String(decrypt(Base64.decodeBase64(base64Str.getBytes("UTF-8")), password),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return result;
    }
    
}
