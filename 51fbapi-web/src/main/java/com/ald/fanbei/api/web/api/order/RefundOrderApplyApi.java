package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfAftersaleApplyStatus;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.RefundSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：订单退款申请
 * @author chengkang 2017年6月15日下午16:49:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("refundOrderApplyApi")
public class RefundOrderApplyApi implements ApiHandle{

	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfOrderRefundService afOrderRefundService;
	@Resource
	AfAftersaleApplyService afAftersaleApplyService;
	@Resource
	AfGoodsService afGoodsService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long orderId = NumberUtil.objToLong(requestDataVo.getParams().get("orderId"));
		//联系电话，订单状态统一版本后，此参数不传递
		String contactsMobile = ObjectUtils.toString(requestDataVo.getParams().get("contactsMobile"),"");
		//退款原因
		String userReason = ObjectUtils.toString(requestDataVo.getParams().get("userReason"),"");
		//退款图片凭证
		String picVouchers = ObjectUtils.toString(requestDataVo.getParams().get("picVouchers"),"");
		
		//参数基本检查
		if(orderId == null){
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}
		//用户订单检查
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,userId);
		if(null == orderInfo){
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		if(OrderStatus.CLOSED.getCode().equals(orderInfo.getStatus())){
			throw new FanbeiException(FanbeiExceptionCode.ORDER_HAVE_CLOSED);
		}
		List<String> supportOrderTypes = new ArrayList<String>();
		supportOrderTypes.add(OrderType.SELFSUPPORT.getCode());
		supportOrderTypes.add(OrderType.AGENTBUY.getCode());
		if(!supportOrderTypes.contains(orderInfo.getOrderType())){
			throw new FanbeiException(FanbeiExceptionCode.ORDER_REFUND_TYPE_ERROR);
		}
		
		if(StringUtil.isNotBlank(contactsMobile)){
			//联系人手机号方式售后
			refundApply(userId, orderId, contactsMobile, orderInfo);
		}else{
			//退款原因及退款图片凭证方式 
			afterSaleApply(userId, orderId, userReason, picVouchers, orderInfo);
		}
		
		return resp;
	}

	private void refundApply(Long userId, Long orderId, String contactsMobile,
			AfOrderDo orderInfo) {
		//退单处理，如果不存在退单项，则新增退单项，如果存在，且手机号有改动，则更新退单中的联系人手机号
		AfOrderRefundDo afOrderRefundDo = afOrderRefundService.getOrderRefundByOrderId(orderInfo.getRid());
		if(afOrderRefundDo==null){
			//插入最新退款记录
			//退款方式默认为订单支付方式 ，如果为空，则默认微信
			PayType payType = PayType.findRoleTypeByCode(orderInfo.getPayType());
			if(payType==null){
				payType = PayType.WECHAT;	
			}
			afOrderRefundDo = BuildInfoUtil.buildOrderRefundDo("", orderInfo.getActualAmount(), BigDecimal.ZERO, userId, orderInfo.getRid(),
					orderInfo.getOrderNo(), OrderRefundStatus.NEW,payType,StringUtils.EMPTY, null,"自营商品用户退款申请",RefundSource.USER.getCode(),StringUtils.EMPTY);
			afOrderRefundDo.setContactsMobile(contactsMobile);
			afOrderRefundService.addOrderRefund(afOrderRefundDo);
			
			logger.info("自营订单用户点击申请售后,系统对订单不做真实退款操作.orderId="+orderId+",userId="+userId);
			//订单状态改为退款中
//				orderInfo.setStatus(OrderStatus.WAITING_REFUND.getCode());
//				afOrderService.updateOrder(orderInfo);
			//减少商品销量
//				afGoodsService.updateSelfSupportGoods(orderInfo.getGoodsId(), -orderInfo.getCount());
		}else{
			//退款记录已存在，校验状态等各种信息，如果退款未完成，更新退款单中的联系人手机号信息
			if(OrderRefundStatus.FINISH.getCode().equals(afOrderRefundDo.getStatus())){
				//返回提示订单已处理完成
				throw new FanbeiException(FanbeiExceptionCode.REFUND_HAVE_SUCCESS);
			}else{
				//更新订单中联系人手机号信息
				afOrderRefundDo.setContactsMobile(contactsMobile);
				afOrderRefundService.updateOrderRefund(afOrderRefundDo);
			}
		}
	}

	private void afterSaleApply(Long userId, Long orderId, String userReason,
			String picVouchers, AfOrderDo orderInfo) {
		if(StringUtil.isBlank(userReason)){
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}
		AfAftersaleApplyDo afAftersaleApplyDo = afAftersaleApplyService.getByOrderId(orderId);
		Date currDate = new Date();
		if(afAftersaleApplyDo==null){
			//新增申请记录
			String applyNo = generatorClusterNo.getAfterSaleApplyNo(currDate);
			afAftersaleApplyDo = new AfAftersaleApplyDo(currDate, userId, orderId, applyNo, AfAftersaleApplyStatus.NEW.getCode(), userReason, picVouchers);
			afAftersaleApplyService.saveRecord(afAftersaleApplyDo);
			
			//订单状态改为退款中
			orderInfo.setStatus(OrderStatus.WAITING_REFUND.getCode());
			afOrderService.updateOrder(orderInfo);
			//自营商品减少商品销量
			if(OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())){
				afGoodsService.updateSelfSupportGoods(orderInfo.getGoodsId(), -orderInfo.getCount());
			}
		}else{
			if(!AfAftersaleApplyStatus.NOTPASS.getCode().equals(afAftersaleApplyDo.getStatus())){
				//返回提示售后处理中
				throw new FanbeiException(FanbeiExceptionCode.AFTERSALE_PROCESSING);
			}else{
				//修改申请记录并置为初始化
				afAftersaleApplyDo.setGmtApply(currDate);
				afAftersaleApplyDo.setUserReason(userReason);
				afAftersaleApplyDo.setPicVouchers(picVouchers);
				afAftersaleApplyDo.setStatus(AfAftersaleApplyStatus.NEW.getCode());
				afAftersaleApplyService.updateById(afAftersaleApplyDo);
			}
		}
	}
	
}
