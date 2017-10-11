package com.ald.fanbei.api.common.unionlogin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


/**
 * 借点钱对接消息加密处理等
 */
public class JdqMessageSign {

    public static final Logger logger = LoggerFactory.getLogger(JdqMessageSign.class);

    public static final Charset CHARSET = Charset.forName("utf-8");

    public static final String ALGORITHMS_SHA1 = "SHA-1";

    public static final String ALGORITHMS_MD5 = "MD5";
    public static final String TEMP_SECRET_KEY = "3FADAE9950B216AF";
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
    public static String signParams(TreeMap<String, String> paramMap, String append) {
        String sourceStr = paramTreeMapToString(paramMap);
        sourceStr+=append;
        String md5Str = signToHexStr(ALGORITHMS_MD5, sourceStr);
        return md5Str;
    }
    public static String signParams(String key, TreeMap<String, String> paramMap) {
        String sourceStr = paramTreeMapToString(paramMap);
        return signParams(key, sourceStr);
    }

    /**
     * 利用 TreeMap 对 param 参数名称进行字典排序，然后拼接字符串，然后进行签名
     * <p>
     * 仅作为测试用途，具体加密流程以接口文档为准
     * @return
     */
    public static String jdqSignTest() {

        String applyNo = "201512140075000002";
        String channelNo = "211";
        String applyInfo = "gLi5lSf1FW+r1nuhjheOlA2vYlbt1U9kOKnGPPG/LZUXzq0J7qlqUSckCtGfRiQkkqgfZHwEGaBZkpGWuIyZTtCegU8xj85Xp7bG3Fyfd6k=";
        String userAttribute = "/9Tsys8IEam1eOl8HogR1ZIlNQX+FrZxF0j+LhFHZncgyMB3Kxzi81PwLJIjmkr0YboHn+LzpFqdmLlJausImCWrszc8EFd7rDmLzE5Ercs=";
        String timestamp = "1477915880877";
        String pNameApplyNo = "apply_no";
        String pNameChannelNo = "channel_no";
        String pNameApplyInfo = "apply_info";
        String pNameUserAttr = "user_attribute";
        String pNameTimestamp = "timestamp";
        TreeMap<String, String> paramMap = new TreeMap<>();
        paramMap.put(pNameApplyNo, applyNo);
        paramMap.put(pNameChannelNo, channelNo);
        paramMap.put(pNameApplyInfo, applyInfo);
        paramMap.put(pNameUserAttr, userAttribute);
        paramMap.put(pNameTimestamp, timestamp);

        return signParams(TEMP_SECRET_KEY, paramMap);
    }

    public static void main(String[] args) {
        System.out.println(jdqSignTest());
        System.exit(0);
    }

}
