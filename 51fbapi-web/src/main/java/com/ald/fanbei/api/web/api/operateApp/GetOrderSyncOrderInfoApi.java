/**
 * 
 */
package com.ald.fanbei.api.web.api.operateApp;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：运营app同步订单信息
 * @author suweili 2017年4月24日下午6:41:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderSyncOrderInfoApi")
public class GetOrderSyncOrderInfoApi implements ApiHandle {

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfAgentOrderService afAgentOrderService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String orderNo = ObjectUtils.toString(requestDataVo.getParams().get("orderNo"), "");
		Long otherOrderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0L);
//        String test = "{\"order_id\":\"10607643000050904\"}";
//        afOrderService.updateOrderTradePaidDone(test);
		if (afOrderService.syncOrderNoWithAgencyUser(userId, orderNo, otherOrderId) > 0) {
		
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
