package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.dal.domain.AfRecycleRatioDo;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * @describe: 有得卖三方 辅助类
 * @author weiqingeng
 * @date 2018年2月26日 下午4:14:31
 */
public class RecycleUtil {

    public static final String PARTNER_ID = "10000215";//与有得卖合作id，对应我们平台的appid

    //public static final String CALLBACK_BASE_URL_TEST = "http://120.27.147.62:8011";//有得卖握手接口  测试
    public static final String CALLBACK_BASE_URL_TEST = "http://51fanbei.youdemai.com";//有得卖握手接口  测试
    public static final String CALLBACK__BASE_URL_ONLINE = "http://51fanbei.youdemai.com";//有得卖握手接口  线上


    public static final String YDM_CALLBACK_URL = "/api/fanbei/sendverify";//有得卖订单确认接口

    public static final String PRIVATE_KEY = "FANBEI_YDM_@#$#%$&";//有得卖签名私钥
    public static final String COUPON_NAME = "翻倍兑换优惠券";//券名称
    public static final BigDecimal LIMIT_AMOUNT = BigDecimal.valueOf(0.00);//最小限制金额，无门槛
    public static final Integer LIMIT_COUNT = Integer.MAX_VALUE;//每个人限制领取张数,不限制
    public static final String RECYCLE_AMOUNT_WARN = "recycle_amount_warn";//有得卖账号余额不足key
    public static final BigDecimal RECYCLE_AMOUNT_MIN_THRESHOLD = BigDecimal.valueOf(100000);//有得卖账号最小预警余额，10万

    public static final String RECYCLE_AMOUNT_MIN_THRESHOLD_KEY = "RECYCLE_MIN_AMOUNT_THRESHOLD";//有得卖账号最小余额报警key，af_resource设置的type
    public static final String RECYCLE_AMOUNT_MIN_THRESHOLD_MOBILE_KEY = "RECYCLE_MIN_AMOUNT_THRESHOLD_MOBILE";//有得卖账号最小余额报警的手机key，af_resource设置的type



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
            prestr = prestr + key + value;
        }
        return prestr;
    }


    /**
     * 根据概率获取翻倍比率
     * @param list
     * @return
     */
    public static BigDecimal getExchangeRatio(List<AfRecycleRatioDo> list){
        BigDecimal ratio = BigDecimal.ZERO;
        BigDecimal totalProbability = BigDecimal.ZERO;
        if(CollectionUtils.isNotEmpty(list)){
            int random = new Random().nextInt(100) + 1;//获取1-100之间的随机整数
            for(AfRecycleRatioDo afRecycleRatioDo : list){
                ratio = afRecycleRatioDo.getRatio();//倍数
                BigDecimal probability = afRecycleRatioDo.getProbability();//概率
                totalProbability = totalProbability.add(probability);
                if(random <= probability.multiply(BigDecimal.valueOf(100)).intValue()){
                    return ratio;
                }
            }
        }
        return ratio;
    }
}
