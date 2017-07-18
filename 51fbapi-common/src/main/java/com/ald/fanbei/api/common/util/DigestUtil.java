package com.ald.fanbei.api.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;


/**
 * 
 *@类DigestUtil.java 的实现描述：加解密
 *@author 陈金虎 2017年1月16日 下午11:29:26
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class DigestUtil{
    
    private static final Logger logger           = LoggerFactory.getLogger(DigestUtil.class);
    
    public static final String SHA1                       = "SHA-1";
    public static final int    DEFAULT_BUFFER_LENGTH      = 8 * 1024;
    public static final int    DEFAULT_BYTES_SIZE         = 8;
    public static final int    DEFAULT_DIGEST_TIMES       = 1024;
    
    /**
     * 获取16位MD5码
     * 
     * @param md5Str
     * @return
     */
    public static String MD5_16(String md5Str) {
        String md5 = MD5(md5Str);
        return StringUtils.substring(md5, 8, 24);
    }

    /**
     * 32位Md5码
     * 
     * @param md5Str
     * @return
     */
    public static String MD5(String md5Str) {
        if (StringUtils.isBlank(md5Str)) {
            return null;
        }
        return DigestUtils.md5Hex(md5Str);
    }

    public static String getSha1Code(MultipartFile fileObj) throws IOException {
        if (null == fileObj) {
            return StringUtils.EMPTY;
        }
        InputStream ins = null;
        try {
            ins = fileObj.getInputStream();
            return DigestUtils.sha1Hex(ins);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        } finally {
            if (null != ins) {
                ins.close();
            }
        }
    }

    public static String getMd5(String pathname) throws IOException {
        if (StringUtils.isBlank(pathname)) {
            return StringUtils.EMPTY;
        }
        InputStream ins = null;
        try {
            ins = new FileInputStream(pathname);
            return DigestUtils.md5Hex(ins);
        } catch (Exception e) {
            return StringUtils.EMPTY;
        } finally {
            if (null != ins) {
                ins.close();
            }
        }
    }
    
    /**
     * 计算sha256值
     * 
     *@param signStrBefore 需要计算的字符串
     *@return
     */
    public static String getDigestStr(String signStrBefore) { 
		try {
	    	MessageDigest md = MessageDigest.getInstance("SHA-256");
	    	md.update(signStrBefore.getBytes());
	        String tempStr = null; 
	        StringBuilder stb = new StringBuilder(); 
	        byte[] origBytes = md.digest();
	        for (int i = 0; i < origBytes.length; i++) { 
	            // 这里按位与是为了把字节转整时候取其正确的整数，java中一个int是4个字节 
	            // 如果origBytes[i]最高位为1，则转为int时，int的前三个字节都被1填充了 
	            tempStr = Integer.toHexString(origBytes[i] & 0xff); 
	            if (tempStr.length() == 1) { 
	                stb.append("0"); 
	            } 
	            stb.append(tempStr);

	        } 
	        return stb.toString(); 
		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
			throw new FanbeiException("sign is error",FanbeiExceptionCode.SYSTEM_ERROR);
		}
    }
    public static String SHA1(String decript) {
		try {
			MessageDigest digest = java.security.MessageDigest
					.getInstance("SHA-1");
			digest.update(decript.getBytes());
			byte messageDigest[] = digest.digest();
			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < messageDigest.length; i++) {
				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
				if (shaHex.length() < 2) {
					hexString.append(0);
				}
				hexString.append(shaHex);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}



//    public static String getSha1Code(String pathname) throws IOException {
//        if (StringUtils.isBlank(pathname)) {
//            return StringUtils.EMPTY;
//        }
//        InputStream ins = null;
//        try {
//            ins = new FileInputStream(pathname);
//            return getSha1Code(ins);
//        } catch (Exception e) {
//            return StringUtils.EMPTY;
//        } finally {
//            if (null != ins) {
//                ins.close();
//            }
//        }
//    }

//    public static String getMd5(File file) throws IOException {
//        if (null == file) {
//            return StringUtils.EMPTY;
//        }
//        InputStream ins = null;
//        try {
//            ins = new FileInputStream(file);
//            return getMd5(ins);
//        } catch (Exception e) {
//            return StringUtils.EMPTY;
//        } finally {
//            if (null != ins) {
//                ins.close();
//            }
//        }
//    }

//    public static String getSha1Code(File file) throws IOException {
//        if (null == file) {
//            return StringUtils.EMPTY;
//        }
//        InputStream ins = null;
//        try {
//            ins = new FileInputStream(file);
//            return getSha1Code(ins);
//        } catch (Exception e) {
//            return StringUtils.EMPTY;
//        } finally {
//            if (null != ins) {
//                ins.close();
//            }
//        }
//    }

    public static String getSha1Code(byte[] byteArray) throws IOException {
        if (null == byteArray || byteArray.length < 1) {
            return StringUtils.EMPTY;
        }
        return DigestUtils.sha1Hex(byteArray);
    }

    /**
     * get Sha1 Code with inputStream
     * 
     * @param iStream
     * @return
     * @throws IOException
     */
//    public static String getSha1Code(InputStream iStream) throws IOException {
//        if (null == iStream) {
//            return StringUtils.EMPTY;
//        }
//        return DigestUtils.sha1Hex(iStream);
//    }

    /**
     * Generate Sha1 Code with inputStream
     * 
     * @param iStream
     * @return
     * @throws IOException
     */
//    public static String getMd5(InputStream iStream) throws IOException {
//        if (null == iStream) {
//            return StringUtils.EMPTY;
//        }
//        return DigestUtils.md5Hex(iStream);
//    }

    /**
     * 根据流获取文件的sha1值
     * 
     * @param in
     * @return
     * @throws OutOfMemoryError
     * @throws IOException
     */
//    public static String getFileSha1(InputStream in) throws OutOfMemoryError, IOException {
//        try {
//            return byte2hex(digest(in, SHA1));
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//            throw e;
//        } finally {
//            if (null != in) {
//                in.close();
//            }
//        }
//    }

    /**
     * 对字符串进行给定算法散列(无盐值)
     * 
     * @param bytes
     * @param algorithm
     * @return
     */
    public static byte[] digestString(byte[] bytes, String algorithm) {
        return digest(bytes, algorithm, null, 1);
    }

    /**
     * 对字符串进行给定算法散列(包含盐值)
     * 
     * @param bytes
     * @param algorithm
     * @return
     */
    public static byte[] digestString(byte[] bytes, byte[] salt, String algorithm) {
        return digest(bytes, algorithm, salt, 1);
    }

    /**
     * 对字符串进行给定次数和指定算法的散列(包含盐值)
     * 
     * @param bytes
     * @param algorithm
     * @return
     */
    public static String digestString(String password, String salt) {
        byte[] saltBytes = salt.getBytes();
        byte[] pwdBytes =  digest(getStringByte(password), SHA1, saltBytes, DEFAULT_DIGEST_TIMES);
        return encodeHex(pwdBytes);
    }
    
    public static byte[] getStringByte(String str){
        if(str != null && !"".equals(str.trim())){
            try {
                return str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * 生成随机salt的字节数组
     * 
     * @param num
     * @return
     */
    public static byte[] generateSalt(int num) {
        Validate.isTrue(num > 0, "num argument must be a positive integer, larger than 0", num);

        byte[] bytes = new byte[num];
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * 对文件进行指定算法的散列散列
     * 
     * @param bytes
     * @return
     * @throws IOException
     */
    public static byte[] digestFile(InputStream bytes, String algorithm) throws IOException {
        return digest(bytes, algorithm);
    }

    private static byte[] digest(byte[] bytes, String algorithm, byte[] salt, int counts) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                digest.update(salt);
            }

            byte[] result = digest.digest(bytes);

            for (int i = 1; i < counts; i++) {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        } catch (GeneralSecurityException e) {
            logger.error("general security exception occurs, detail exception is ", e);
            return null;
        }
    }

    private static byte[] digest(InputStream input, String algorithm) throws IOException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[DEFAULT_BUFFER_LENGTH];
            int read = input.read(buffer, 0, DEFAULT_BUFFER_LENGTH);

            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, DEFAULT_BUFFER_LENGTH);
            }

            return messageDigest.digest();
        } catch (GeneralSecurityException e) {
            logger.error("general security exception occurs when digest inputstream ", e);
            return null;
        }
    }

    /**
     * byte[]字节组转为字符串
     * 
     * @param b
     * @return
     */
//    private static String byte2hex(byte[] b) {
//        String hs = "";
//        String stmp = "";
//        for (int n = 0; n < b.length; n++) {
//            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
//            if (stmp.length() == 1) {
//                hs = hs + "0" + stmp;
//            } else {
//                hs = hs + stmp;
//            }
//            if (n < b.length - 1) {
//                hs = hs + ":";
//            }
//        }
//        return hs.toUpperCase(Locale.ENGLISH);
//    }
    
    /**
     * decode String to bytes[]
     * 
     * @param src
     * @return
     */
    public static byte[] decodeHex(String src) {
        if (StringUtils.isBlank(src)) return null;
        try {
            return Hex.decodeHex(src.toCharArray());
        } catch (DecoderException e) {
            logger.error("decode hex src failed, src vlaue is " + src, e);
        }
        return null;
    }

    public static String encodeHex(byte[] data) {
        return Hex.encodeHexString(data);
    }
    
    public static void main(String[] args) {
//        byte[] saltBytes = DigestUtil.generateSalt(DEFAULT_BYTES_SIZE);
//        String salt = DigestUtil.encodeHex(saltBytes);
//        System.out.println(salt);
    	System.out.println(DigestUtil.getDigestStr("hello1234"));
    }

//    public static void main(String[] args) {
//        byte[] salts = generateSalt(8);
//        String saltRaw = "d2eb9f693820ce5d";
//        byte[] saltsRawBytes = CodecUtil.decodeHex(saltRaw);
//        System.out.println(saltsRawBytes);
//        String passwordRaw = "hello1234";
//        byte[] hashPasswordRaw = digestString(passwordRaw.getBytes(), saltsRawBytes, 1024, ConstantPool.SHA1);
//        System.out.println(CodecUtil.encodeHex(hashPasswordRaw));
//
//        System.out.println(salts);
//        String salt = CodecUtil.encodeHex(salts);
//        System.out.println(salt);
//        byte[] salts2 = CodecUtil.decodeHex(salt);
//        System.out.println(salts2);
//        System.out.println(CodecUtil.encodeHex(salts2));
//        String password = "123456";
//        byte[] hashPassword = digestString(password.getBytes(), salts, 1024, ConstantPool.SHA1);
//        System.out.println(CodecUtil.encodeHex(hashPassword));
//        byte[] hashPassword2 = digestString(password.getBytes(), salts2, 1024, ConstantPool.SHA1);
//        System.out.println(CodecUtil.encodeHex(hashPassword2));
//    }

}
