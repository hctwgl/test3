package com.ald.fanbei.api.web.api.agencybuy;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


@Component("cancelAgencyBuyOrderApi")
public class CancelAgentBuyOrder implements ApiHandle {

	@Resource
	AfAgentOrderService afAgentOrderService;
	
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserCouponService afUserCouponService;
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
		afAgentOrderDo.setGmtClosed(new Date());
		
		
		if(afOrderService.updateOrder(afOrderDo) > 0){
			AfAgentOrderDo agOrder= afAgentOrderService.getAgentOrderByOrderId(orderId);
          //恢复使用优惠券
			if(agOrder.getCouponId()>0){
	            AfUserCouponDo couponDo =	afUserCouponService.getUserCouponById(agOrder.getCouponId());
	
	            if(couponDo!=null&&couponDo.getGmtEnd().after(new Date())){
	            		couponDo.setStatus(CouponStatus.NOUSE.getCode());
	            		afUserCouponService.updateUserCouponSatusNouseById(agOrder.getCouponId());
	            }else if(couponDo !=null &&couponDo.getGmtEnd().before(new Date())){
	        		couponDo.setStatus(CouponStatus.EXPIRE.getCode());
	        		afUserCouponService.updateUserCouponSatusExpireById(agOrder.getCouponId());
	        	}
			}
		
			if(afAgentOrderService.updateAgentOrder(afAgentOrderDo) > 0){

				return resp;
			}
		}
		
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}

}
