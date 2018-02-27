package com.ald.fanbei.api.biz.third.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @Description: 有得卖三方 辅助类
 * @author weiqingeng
 * @date 2018年2月26日 下午4:14:31
 */
public class AppRecycleUtil {

    public static final String PARTNER_ID = "1136587444";//与有得卖合作id，对应我们平台的appid

    public static final String YDM_CALLBACK_URL = "https://";//有得卖订单确认接口
    public static final String PRIVATE_KEY = "";//有得卖签名私钥

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
            prestr = prestr + value;
            /*if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }*/
        }

        return prestr;
    }
}
