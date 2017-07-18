package com.ald.fanbei.api.web.api.agencybuy;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
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
		//如果为自营订单,不改变订单状态,直接返回操作成功
		if(StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode())){
			logger.info("自营订单用户点击确认收货,系统对订单不做修改记录.orderId="+orderId+",userId="+userId);
			return resp;
		}else{
			AfOrderDo afOrderDo = new AfOrderDo();
			afOrderDo.setRid(orderId);
			afOrderDo.setStatus("FINISHED");
			afOrderDo.setGmtFinished(new Date());
			if(afOrderService.updateOrder(afOrderDo) > 0){
				return resp;
			}
		}
		return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
	}

}
