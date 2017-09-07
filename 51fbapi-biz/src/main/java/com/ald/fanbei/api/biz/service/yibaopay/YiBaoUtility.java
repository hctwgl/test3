package com.ald.fanbei.api.biz.service.yibaopay;

import com.ald.fanbei.api.biz.service.wxpay.WxpayCore;
import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author honghzengpei 2017/9/7 13:53
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YiBaoUtility {


    /**
     * 新增易宝订单
     * @return
     */
    public static Map<String,String> createOrder(BigDecimal orderAmount,String orderId){
        String merchantNo ="";  //商户编号
        String redirectUrl =""; //同步回调地止
        String notifyUrl ="";   //异步
        HashMap<String,String> goods = new HashMap<>();
        goods.put("goodsName","51返呗还款");
        goods.put("goodsDesc","");
        String goodsParamExt= JSON.toJSONString(goods);
        String csUr ="";        //清算成功回调地止
        String fundProcessType ="REAL_TIME";

        Map<String,String> ret = new HashMap();
        ret.put("parentMerchantNo",merchantNo);
        ret.put("merchantNo",merchantNo);
        ret.put("orderId",orderId);
        ret.put("orderAmount",String.valueOf( orderAmount));
        ret.put("redirectUrl",redirectUrl);
        ret.put("notifyUrl",notifyUrl);
        ret.put("goodsParamExt",goodsParamExt);
        ret.put("csUr",csUr);
        ret.put("fundProcessType",fundProcessType);

        String uri = YeepayService.getUrl(YeepayService.TRADEORDER_URL);
        Map<String,String> yeeRet =  YeepayService.requestYOP(ret, uri, YeepayService.TRADEORDER);

        return  yeeRet;

    }
}
