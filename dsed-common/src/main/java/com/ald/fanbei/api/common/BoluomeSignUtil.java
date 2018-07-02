package com.ald.fanbei.api.common;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/**
 * Created by boluome on 2017/4/10.
 */
public class BoluomeSignUtil {
    public static final int MD532 = 32;
    public static final int MD516 = 16;
    public static final String APP_KEY = "appKey";
    public static final String SIGN = "sign";
    public static final String TIMESTAMP = "timestamp";

    /**
     * 对参数进行排序
     *
     * @param data 待排序参数
     * @return
     */
    private static String convertMap2String(Map<String, Object> data) {
        TreeMap<String, Object> tree = new TreeMap<>();
        Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Object> sf = it.next();
            //过滤空字符，过滤非基本类型格式数据
            if (null != sf.getValue() && !sf.getValue().equals(" ")
                    && !StringUtils.equals(APP_KEY, sf.getKey()) && !StringUtils.equals(SIGN, sf.getKey())
                    && (sf.getValue() instanceof String || sf.getValue() instanceof Double
                    || sf.getValue() instanceof Integer || sf.getValue() instanceof Long
                    || sf.getValue() instanceof Boolean || sf.getValue() instanceof Byte)) {
                tree.put(sf.getKey(), sf.getValue());
            }
        }

        it = tree.entrySet().iterator();
        StringBuilder stringBuilder = new StringBuilder();

        while (it.hasNext()) {
            Map.Entry<String, Object> en = it.next();
            stringBuilder.append(en.getValue());
        }
        return stringBuilder.toString();
    }

    /**
     * MD5加密
     *
     * @param sourceStr
     * @param MDleng
     * @return
     */
    public static String MD5(String sourceStr, int MDleng) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuilder stringBuilder = new StringBuilder("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    stringBuilder.append("0");
                stringBuilder.append(Integer.toHexString(i));
            }
            result = stringBuilder.toString();
            if (MD516 == MDleng) {
                result = stringBuilder.toString().substring(8, 24);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 加签并封装数据
     *
     * @param appSecret
     * @param appKey
     * @param params
     * @return
     */
    public static Map<String,Object> wrapData(String appSecret,String appKey,Map<String,Object> params){
        params.put(APP_KEY, appKey);
        params.put(TIMESTAMP, "1504163752973");
        String sign = convertMap2String(params);
        sign = new StringBuilder(appKey).append(sign).append(appSecret).toString();
        sign = URLEncoder.encode(sign).toUpperCase();
        sign = MD5(sign, MD532).toUpperCase();
        params.put(SIGN, sign);
        return params;
    }

    public static void main(String[] args) {
        String appSecret = "TzWaM9xKBfLgxgbvMAB6D3mZAkx5MJaJ";
        String appKey = "2607839913";

        //参数值
        Map<String, Object> params = new HashMap<>();
        params.put("app_id", "157");
        params.put("campaign_id", "775");
        System.out.println(wrapData(appSecret,appKey,params));
    }
}
