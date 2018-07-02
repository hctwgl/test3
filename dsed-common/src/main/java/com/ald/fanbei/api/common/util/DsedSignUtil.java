package com.ald.fanbei.api.common.util;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author chefeipeng  2018年6月6日下午13:12:27
 * @类描述：ras生成签名和验签的工具类
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class DsedSignUtil {

    private static final Logger logger = Logger.getLogger(DsedSignUtil.class);




    /**
     * 通过appSecret解密获取参数
     * @param params
     * @param appSecret
     * @return
     */
    public static JSONObject getParams(String params, String appSecret, String sign) {
        //解密参数
        JSONObject result = paramsDecrypt(params, appSecret);
        //验证参数是否被修改
        String signCode = generateSign(result, appSecret).toUpperCase();
        if (signCode.equals(sign.toUpperCase())) {
            return result;
        }
        return null;
    }

    /**
     * 通过appSecret加密参数
     *
     * @param params
     * @param appSecret
     * @return
     */
    public static String paramsEncrypt(JSONObject params, String appSecret) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        JSONObject obj = new JSONObject(true);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.getString(key);
            obj.put(key, value);
        }
        String result = obj.toString();
        result = DsedAesUtil.encryptToBase64Third(result, appSecret);
        return result;
    }

    /**
     * 通过appSecret解密参数
     *
     * @param params
     * @param appSecret
     * @return
     */
    public  static JSONObject paramsDecrypt(String params, String appSecret) {
        params = DsedAesUtil.decryptFromBase64Third(params, appSecret);
        JSONObject result = JSONObject.parseObject(params);
        return result;
    }

    /**
     * 生成本地签名
     *
     * @param params
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static String generateSign(JSONObject params, String appSecret) throws IllegalArgumentException {
        List<String> keys = new ArrayList<String>(params.keySet());
        keys.remove("signCode");
        Collections.sort(keys);
        StringBuffer result = null;
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (i == 0) result = new StringBuffer();
            else result.append("&");
            result.append(key).append("=").append(params.getString(key));
        }
        result.append("&appSecret=" + appSecret);
        return params == null ? null : md5(result.toString());
    }



    public static String md5(String text) {
        String str = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            str = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("MD5加密-NoSuchAlgorithm异常", e);
        } catch (Exception e) {
            logger.error("MD5加密异常", e);
        }
        return str.toUpperCase();
    }




}
