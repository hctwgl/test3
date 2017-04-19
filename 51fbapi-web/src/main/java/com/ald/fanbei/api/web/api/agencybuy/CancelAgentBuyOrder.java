package com.ald.fanbei.api.web.api.agencybuy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


@Component("cancelAgencyBuyOrderApi")
public class CancelAgentBuyOrder implements ApiHandle {

	@Resource
	AfAgentOrderService afAgentOrderService;
	
	@Resource
	AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0);
		String cancelReason = ObjectUtils.toString(requestDataVo.getParams().get("cancelReason"), null);
		String cancelDetail = ObjectUtils.toString(requestDataVo.getParams().get("cancelDetail"), null);
		if(0 ==  orderId || StringUtils.isBlank(cancelReason)){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		
		AfOrderDo afOrderDo = new AfOrderDo();
		afOrderDo.setStatus("CLOSED");
		afOrderDo.setRid(orderId);
		
		AfAgentOrderDo afAgentOrderDo = new AfAgentOrderDo();
		afAgentOrderDo.setOrderId(orderId);
		afAgentOrderDo.setCancelReason(cancelReason);
		afAgentOrderDo.setCancelDetail(cancelDetail);
		
		if(afOrderService.updateOrder(afOrderDo) > 0){
			if(afAgentOrderService.updateAgentOrder(afAgentOrderDo) > 0){
				return resp;
			}
		}
		
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}

}
