package com.ald.fanbei.api.web.api.barlyClearance;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 * @author honghzengpei 2017/11/28 13:30
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getDataViewApi")
public class GetDataViewApi implements ApiHandle {
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();

        Integer type = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("type")), 0);   //0 订单结清   1 全部结清
        Long billId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("billId")), 0L);   //0 订单结清   1 全部结清
        HashMap resulitMap = new HashMap();
        if(type.intValue() ==0){
            resulitMap = getOrderClear(userId,billId);
        } else{
            resulitMap = getAllClear(userId);
        }
        resp.setResponseData(resulitMap);
        return resp;
    }

    /**
     * 全部结清
     * @param userId
     * @return
     */
    private HashMap getAllClear(Long userId){

        return null;
    }

    /**
     * 订单结清
     * @param userId
     * @param orderId
     * @return
     */
    private HashMap getOrderClear(Long userId,Long billId){

        return null;
    }

}
