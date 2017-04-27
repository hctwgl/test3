package com.ald.fanbei.api.biz.service.boluome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;

/**
 * 
 * @类描述：提供给第三方接口
 * @author xiaotianjian 2017年3月28日下午11:05:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ThirdCore {
	public static final String ORDER_ID = "orderId";//第三方订单编号
	public static final String AMOUNT = "amount";
	public static final String PLANT_FORM = "plantform";
	public static final String USER_ID = "userId";
	public static final String TIME_STAMP = "timestamp";
	public static final String APP_KEY = "appKey";
	public static final String SIGN = "sign";
	public static final String REFUND_NO = "refundNo";
	private static Map<String, String> appKeyMap;

    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }
    
    /** 
     * 除去数组中的空值和签名参数
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (StringUtils.isEmpty(value) || key.equalsIgnoreCase(SIGN) || key.equalsIgnoreCase(APP_KEY)) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }
    
    
    /** 
     * 把数组所有元素排序，然后拼接参数
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String concatParams(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if (!key.equals(APP_KEY)) {
            	sb.append(params.get(key));
            }
        }

        return sb.toString();
    }
    
    /** 
     * MD5(APPKEY + CUSTOMER_USER_ID + CUSTOMER_USER_PHONE + CUSTOMER_USER_PHONE +APP_SECRET);
     * @param params
     * @return
     */
    public static String buildSignStr(Map<String, String> params) {
    	String appKey = params.get(APP_KEY);
    	String beforeSign = 
    			appKey + concatParams(params) +AesUtil.decrypt(appKeyMap.get(appKey), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
        return DigestUtil.MD5(beforeSign);
    }


	/**
	 * @param appKeyMap the appKeyMap to set
	 */
	public void setAppKeyMap(Map<String, String> appKeyMap) {
		ThirdCore.appKeyMap = appKeyMap;
	}

}
