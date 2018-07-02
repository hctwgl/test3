package com.ald.fanbei.api.biz.third.util.pay;

import com.ald.fanbei.api.biz.bo.thirdpay.ResulitCheck;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;

import java.util.Date;
import java.util.Map;

/**
 * @author honghzengpei 2017/11/2 16:56
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface ThirdInterface {
    void type0Proess(long id, Date updateTime, String orderNo, String thirdOrderNo, String resultStatus, int orderStatus);
    void type1Proess(long id,Date updateTime,String orderNo,String thirdOrderNo,String resultStatus,int orderStatus);
    void type2Proess(long id,Date updateTime,String orderNo,String thirdOrderNo,String resultStatus,int orderStatus);
    ResulitCheck<Boolean> checkSuccess(String tradeNo);
    boolean checkCanNext(long userId,String type,int biztype);

    Map<String,String> getOrderStatus(String orderNo);
}
