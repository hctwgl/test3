package com.ald.fanbei.api.biz.third.util.pay;

import com.ald.fanbei.api.biz.bo.thirdpay.ResulitCheck;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayNameEnum;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.huichaopay.HuichaoUtility;
import com.ald.fanbei.api.biz.third.util.yibaopay.YeepayService;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author honghzengpei 2017/10/30 14:27
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("thirdPayUtility")
public class ThirdPayUtility {
    @Resource
    YiBaoUtility yiBaoUtility;
    @Resource
    HuichaoUtility huichaoUtility;

    @Resource
    AfResourceService afResourceService;

    //@Resource
    //AfYibaoOrderDao afYibaoOrderDao;
    /**
     * 新增易宝订单
     * @return
     */
    public Map<String,String> createOrder(BigDecimal orderAmount, String orderId, long userId, ThirdPayTypeEnum thirdPayTypeEnum,PayOrderSource payOrderSource){
        ThirdPayBo thirdPayBo = afResourceService.getThirdPayBo(thirdPayTypeEnum);
        Map<String,String>  mp = new HashMap<String ,String>();
        if(thirdPayBo.getName().equals(ThirdPayNameEnum.YIBAOPAY.getName())){
            mp = yiBaoUtility.createOrder(orderAmount,orderId,userId,payOrderSource);
            if(thirdPayTypeEnum.getCode().equals(ThirdPayTypeEnum.WXPAY.getCode())){
                mp.put("urlscheme",yiBaoUtility.getCashier(mp.get("token").toString(),userId));
            }
        }
        else if(thirdPayBo.getName().equals(ThirdPayNameEnum.HUICAOPAY.getName())){
            if(thirdPayTypeEnum.getCode().equals(ThirdPayTypeEnum.ZFBPAY.getCode())) {
                mp = huichaoUtility.createOrderZFB(orderId, String.valueOf(orderAmount), userId,payOrderSource);
            }
            else{

            }
        }
        mp.put("thirdChannle",ThirdPayNameEnum.findTypeNameByCode(thirdPayBo.getName()).getCode());
        return mp;
    }



    public Map<String,String> getOrderStatus(String orderNo){
        Map<String,String> map = yiBaoUtility.getOrderStatus(orderNo);
        if(map ==null){
            map = huichaoUtility.getOrderStatus(orderNo);
        }
        return map;
    }


    /**
     * 支付成功时判断订单状态
     * @param tradeNo
     * @return
     */
    public boolean checkSuccess(String tradeNo){
        ResulitCheck<Boolean> ret ;
        ret = yiBaoUtility.checkSuccess(tradeNo);
        if(ret.isSuccess()){
            return true;
        }
        if(ret.getResulit()){
            return false;
        }

        ret = huichaoUtility.checkSuccess(tradeNo);
        if(ret.isSuccess()){
            return true;
        }
        if(ret.getResulit()){
            return false;
        }


        return false;
    }

    public boolean checkFail(String tradeNo){
        ResulitCheck<Boolean> ret ;
        ret = yiBaoUtility.checkFail(tradeNo);
        if(ret.isSuccess()){
            return true;
        }
        if(ret.getResulit()){
            return false;
        }

        ret = huichaoUtility.checkFail(tradeNo);
        if(ret.isSuccess()){
            return true;
        }
        if(ret.getResulit()){
            return false;
        }

        return false;
    }

    public void updateAllNotCheck(){
        yiBaoUtility.updateYiBaoAllNotCheck();
        huichaoUtility.updateAllNotCheck();
    }
}
