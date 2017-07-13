package com.ald.fanbei.api.web.api.agencybuy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


@Component("completedAgencyBuyOrderApi")
public class CompletedAgencyBuyOrderApi implements ApiHandle {

	
	@Resource
	AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0);
		Long userId = context.getUserId();
		//用户订单检查
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
		if(null == orderInfo){
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		List<String> canCompleteStatus = new ArrayList<String>();
		canCompleteStatus.add(OrderStatus.PAID.getCode());
		canCompleteStatus.add(OrderStatus.AGENCYCOMPLETED.getCode());
		
		if(canCompleteStatus.contains(orderInfo.getStatus())){
			AfOrderDo afOrderDo = new AfOrderDo();
			afOrderDo.setRid(orderId);
			afOrderDo.setStatus(OrderStatus.FINISHED.getCode());
			afOrderDo.setGmtFinished(new Date());
			afOrderDo.setLogisticsInfo("已签收");
			if(afOrderService.updateOrder(afOrderDo) > 0){
				return resp;
			}else{
				logger.info("completedAgencyBuyOrder fail,update order fail.orderId="+orderId);
			}
		}else{
			logger.info("completedAgencyBuyOrder fail,order status not support.orderId="+orderId);
		}
		return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
	}

}
