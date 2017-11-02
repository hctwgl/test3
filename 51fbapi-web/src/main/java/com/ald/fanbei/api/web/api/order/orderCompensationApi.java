package com.ald.fanbei.api.web.api.order;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("orderCompensationApi")
public class orderCompensationApi {
	@Resource
	AfOrderService afOrderService;
	@Resource
	BoluomeUtil boluomeUtil;
	
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
			Long userId =context.getUserId();
			Long thirdOrderNo = NumberUtil.objToLong(requestDataVo.getParams().get("thirdOrderNo"));
			Long orderId = NumberUtil.objToLong(requestDataVo.getParams().get("orderId"));
			//参数基本检查
			if(orderId == null){
				throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
			}
			
			//用户订单检查
			AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
			if(null == orderInfo){
				//查询接口
				boluomeUtil.pushPayStatus(orderId, null, null, null, thirdOrderNo, null);
				
				
				//保存订单afOrderService.createOrder
				//throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
			}
		
		
		return null;
	}
}
