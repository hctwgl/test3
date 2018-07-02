package com.ald.fanbei.api.web.api.checkoutcounter;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.api.agencybuy.SubmitAgencyBuyOrderApi;
import com.ald.fanbei.api.web.api.order.BuySelfGoodsApi;
import com.ald.fanbei.api.web.api.order.LeaseOrderApi;
import com.ald.fanbei.api.web.api.order.TradeOrderApi;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 收营台前置步骤
 * 1.生成订单
 * 2.跳转到收银台
 * 为了规避风险一期仅做分发处理
 */
@Component("preCashierApi")
public class PreCashierApi  implements ApiHandle {

    @Resource
    BuySelfGoodsApi buySelfGoodsApi;
    @Resource
    SubmitAgencyBuyOrderApi submitAgencyBuyOrderApi;
    @Resource
    TradeOrderApi tradeOrderApi;
    @Resource
    LeaseOrderApi leaseOrderApi;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        Map<String, Object> params = requestDataVo.getParams();
        String orderType = ObjectUtils.toString(params.get("orderType"), "");//添加默认订单类型，todo 记得改回来
        ApiHandle  bussinessHandle=null;
        if(orderType.equals(OrderType.AGENTBUY.getCode())){
            bussinessHandle=submitAgencyBuyOrderApi;
        }   else if(orderType.equals(OrderType.SELFSUPPORT.getCode())){
            bussinessHandle=buySelfGoodsApi;
        }   else if(orderType.equals(OrderType.TRADE.getCode())){
            bussinessHandle=tradeOrderApi;
        } else if(orderType.equals(OrderType.LEASE.getCode())){
            bussinessHandle=leaseOrderApi;
        }
        request.setAttribute("fromCashier",1);
        return  bussinessHandle.process(requestDataVo,context,request);
    }
}
