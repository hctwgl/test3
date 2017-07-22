package com.ald.fanbei.api.web.api.agencybuy;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfGoodsPriceService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
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
	@Resource
	AfGoodsPriceService afGoodsPriceService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0);
		String cancelReason = ObjectUtils.toString(requestDataVo.getParams().get("cancelReason"), null);
		String cancelDetail = ObjectUtils.toString(requestDataVo.getParams().get("cancelDetail"), null);
		//参数校验
		if(0 ==  orderId || StringUtils.isBlank(cancelReason)){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		Date currDate = new Date();
		AfOrderDo currAfOrderDo = afOrderService.getOrderById(orderId);
		//订单校验
		if(currAfOrderDo == null){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		if(OrderStatus.CLOSED.getCode().equals(currAfOrderDo.getStatus())){
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_ORDER_HAVE_CLOSED);
		}
		
		try {
			//String originStatus = currAfOrderDo.getStatus();
			AfOrderDo afOrderDo = new AfOrderDo();
			afOrderDo.setStatus(OrderStatus.CLOSED.getCode());
			afOrderDo.setRid(orderId);
			afOrderDo.setCancelReason(cancelReason);
			afOrderDo.setCancelDetail(cancelDetail);
			afOrderDo.setGmtClosed(currDate);
			
			if(afOrderService.updateOrder(afOrderDo) > 0){
				//更新库存
				if(currAfOrderDo.getGoodsPriceId() != null){
					afGoodsPriceService.updateStockAndSaleByPriceId(currAfOrderDo.getGoodsPriceId(), false);
				}
				
				//优惠券处理
				if(currAfOrderDo.getUserCouponId()>0){
					AfUserCouponDo couponDo =	afUserCouponService.getUserCouponById(currAfOrderDo.getUserCouponId());
		            if(couponDo!=null&&couponDo.getGmtEnd().after(new Date())){
		            		couponDo.setStatus(CouponStatus.NOUSE.getCode());
		            		afUserCouponService.updateUserCouponSatusNouseById(currAfOrderDo.getUserCouponId());
		            }else if(couponDo !=null &&couponDo.getGmtEnd().before(new Date())){
		        		couponDo.setStatus(CouponStatus.EXPIRE.getCode());
		        		afUserCouponService.updateUserCouponSatusExpireById(currAfOrderDo.getUserCouponId());
		        	}
				}
				//区分代买和非代买
				if(OrderType.AGENTBUY.getCode().equals(currAfOrderDo.getOrderType())){
					AfAgentOrderDo afAgentOrderDo = new AfAgentOrderDo();
					afAgentOrderDo.setOrderId(orderId);
					afAgentOrderDo.setCancelReason(cancelReason);
					afAgentOrderDo.setCancelDetail(cancelDetail);
					afAgentOrderDo.setGmtClosed(currDate);
					afAgentOrderService.updateAgentOrder(afAgentOrderDo);
				}
				
				//如果支付类型为代付，返回用户额度 -- 只有在待审核状态下才可以--现在去除
				/*if(PayType.AGENT_PAY.getCode().equals(currAfOrderDo.getPayType()) 
						&& OrderStatus.PAID.getCode().equals(originStatus)){
					AfUserAccountDo updateAccountDo = new AfUserAccountDo();
					updateAccountDo.setUserId(currAfOrderDo.getUserId());
					updateAccountDo.setUsedAmount(currAfOrderDo.getActualAmount().negate());
					afUserAccountService.updateUserAccount(updateAccountDo);
					
					AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
					afUserAccountLogDo.setRefId( currAfOrderDo.getRid()+"");
					afUserAccountLogDo.setType(UserAccountLogType.BORROWAP.getCode());
					afUserAccountLogDo.setUserId(currAfOrderDo.getUserId());
					afUserAccountLogDo.setAmount(currAfOrderDo.getActualAmount());
					afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);
					
				}*/
				return resp;
			}
		} catch (Exception e) {
			logger.error("cancelOrder request error,orderId="+orderId,e);
		}
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}

}
