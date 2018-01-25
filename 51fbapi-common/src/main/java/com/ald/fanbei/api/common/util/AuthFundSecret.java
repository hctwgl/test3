package com.ald.fanbei.api.common.util;

import com.ald.fanbei.api.common.util.HttpUtil;
import com.alibaba.fastjson.JSON;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.Md5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static java.lang.System.out;

public class AuthFundSecret {


    public static final byte keyStrSzie = 16;

    public static final String ALGORITHM = "AES";

    public static final String AES_CBC_NOPADDING = "AES/CBC/NoPadding";


    public static final Logger logger = LoggerFactory.getLogger(AuthFundSecret.class);

    public static final Charset CHARSET = Charset.forName("utf-8");

    public static final String ALGORITHMS_SHA1 = "SHA-1";

    public static final String ALGORITHMS_MD5 = "MD5";

    //region 加密

    /**
     * 选择 AES/CBC/NoPadding 的模式
     * <p>
     * AES/CBC/NoPadding encrypt
     * 16 bytes secretKeyStr
     * 16 bytes intVector
     *
     * @param secretKeyBytes
     * @param intVectorBytes
     * @param input
     * @return
     */
    public static byte[] encryptCBCNoPadding(byte[] secretKeyBytes, byte[] intVectorBytes, byte[] input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(intVectorBytes);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);
            int inputLength = input.length;
            int srcLength;

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            int blockSize = cipher.getBlockSize();
            byte[] srcBytes;
            if (0 != inputLength % blockSize) {
                srcLength = inputLength + (blockSize - inputLength % blockSize);
                srcBytes = new byte[srcLength];
                System.arraycopy(input, 0, srcBytes, 0, inputLength);
            } else {
                srcBytes = input;
            }

            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptBytes = cipher.doFinal(srcBytes);
            return encryptBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * AES/CBC/NoPadding decrypt
     * 16 bytes secretKeyStr
     * 16 bytes intVector
     *
     * @param secretKeyBytes
     * @param intVectorBytes
     * @param input
     * @return
     */
    public static byte[] decryptCBCNoPadding(byte[] secretKeyBytes, byte[] intVectorBytes, byte[] input) {
        try {
            IvParameterSpec iv = new IvParameterSpec(intVectorBytes);
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_CBC_NOPADDING);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] encryptBytes = cipher.doFinal(input);
            return encryptBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用 AES 算法加密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     *
     * @param secretStr
     * @param inputStr
     * @return
     */
    public static byte[] encode(String secretStr, String inputStr) {
        if (keyStrSzie != secretStr.length()) {
            return null;
        }
        byte[] secretKeyBytes = secretStr.getBytes(CHARSET);
        byte[] ivBytes = Arrays.copyOfRange(secretKeyBytes, 0, 16);
        byte[] inputBytes = inputStr.getBytes(CHARSET);

        byte[] outputBytes = encryptCBCNoPadding(secretKeyBytes, ivBytes, inputBytes);
        return outputBytes;
    }


    /**
     * 用 AES 算法解密 inputStr。
     * 使用 secretStr 作为 key，secretStr 的前 16 个字节作为 iv。
     *
     * @param secretStr
     * @param inputBytes
     * @return
     */
    public static byte[] decode(String secretStr, byte[] inputBytes) {
        if (keyStrSzie != secretStr.length()) {
            return null;
        }
        byte[] secretKeyBytes = secretStr.getBytes(CHARSET);
        byte[] ivBytes = Arrays.copyOfRange(secretKeyBytes, 0, 16);

        byte[] outputBytes = decryptCBCNoPadding(secretKeyBytes, ivBytes, inputBytes);
        return outputBytes;
    }

    //endregion

    //region 签名

    /**
     * 消息摘要，使用参数 algorithms 指定的算法
     *
     * @param algorithms
     * @param inputStr
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static byte[] sign(String algorithms, String inputStr) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithms);
        messageDigest.update(inputStr.getBytes(CHARSET));
        return messageDigest.digest();
    }

    /**
     * byte 数组转 十六进制字符串
     *
     * @param byteArray
     * @return
     */
    public static String byte2HexStr(byte[] byteArray) {

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }

        return new String(resultCharArray);
    }

    /**
     * 签名,并将签名结果转换成 十六进制字符串
     *
     * @param inputStr
     * @param inputStr
     * @return
     */
    public static String signToHexStr(String algorithms, String inputStr) {
        try {
            return byte2HexStr(sign(algorithms, inputStr));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 拼接参数字符串
     *
     * @param paramMap
     * @return
     */
    public static String paramTreeMapToString(TreeMap<String, String> paramMap) {
        StringBuilder paramStrBuilder = new StringBuilder();

        Iterator<Map.Entry<String, String>> it = paramMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entrySet = it.next();
            paramStrBuilder.append(entrySet.getKey()).append('=').append(entrySet.getValue()).append('&');
        }
        return paramStrBuilder.substring(0, paramStrBuilder.length() - 1);
    }

    public static String signParams(String key, String sourceStr) {
        String md5Str1 = signToHexStr(ALGORITHMS_MD5, sourceStr);
        String sourceStr2 = md5Str1 + key;
        return signToHexStr(ALGORITHMS_MD5, sourceStr2);
    }

    public static String signParams(String key, TreeMap<String, String> paramMap) {
        String sourceStr = paramTreeMapToString(paramMap);
        return signParams(key, sourceStr);
    }

}
