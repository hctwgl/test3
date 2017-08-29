package com.ald.fanbei.api.web.api.agencybuy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.rebate.RebateContext;
import com.ald.fanbei.api.common.util.SpringBeanContextUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
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
	@Resource
	RebateContext rebateContext;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Integer appVersion = context.getAppVersion();
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0);
		Long userId = context.getUserId();
		//用户订单检查
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
		if(null == orderInfo){
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		//app371之前自营订单不处理
		if(appVersion<371){
			//如果为自营订单,不改变订单状态,直接返回操作成功
			if(StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode())){
				//自营确认收货走返利
				rebateContext.rebate(orderInfo);
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
		}else{
			if(StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode())){
				//自营确认收货走返利处理，由于返利在确认收货收货状态之后，所以直接修改为返利成功即可
				rebateContext.rebate(orderInfo);
			}else{
				if(OrderStatus.DELIVERED.getCode().equals(orderInfo.getStatus())){
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
			}

		}
		return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
	}

}
