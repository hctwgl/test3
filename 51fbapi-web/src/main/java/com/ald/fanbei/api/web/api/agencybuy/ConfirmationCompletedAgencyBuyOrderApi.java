package com.ald.fanbei.api.web.api.agencybuy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


@Component("ConfirmationCompletedAgencyBuyOrderApi")
public class ConfirmationCompletedAgencyBuyOrderApi implements ApiHandle {

	
	@Resource
	AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0);
		AfOrderDo afOrderDo = new AfOrderDo();
		afOrderDo.setRid(orderId);
		afOrderDo.setStatus("FINISHED");
		if(afOrderService.updateOrder(afOrderDo) > 0){
			return resp;
			}
		
		return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
	}

}
