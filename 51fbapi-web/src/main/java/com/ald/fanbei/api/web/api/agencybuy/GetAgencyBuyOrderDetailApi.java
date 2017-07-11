package com.ald.fanbei.api.web.api.agencybuy;



import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfOrderStatusMsgRemark;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfAgentOrederDetailInforVo;


@Component("GetAgencyBuyOrderDetailApi")
public class GetAgencyBuyOrderDetailApi implements ApiHandle {

	@Resource
	AfAgentOrderService afAgentOrderService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfAftersaleApplyService afAftersaleApplyService;
	
	/**
	 * 3.6.4新增优惠劵使用
	 */
	@Resource
	AfCouponService afCouponService;
	
	@Resource
	AfUserCouponService  afUserCouponService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"),-1);
		
		if (orderId == 0) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
			 
		}
		
		
		AfOrderDo afOrderDo = afOrderService.getOrderById(orderId);
		AfAgentOrderDo afAgentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderId);
		
		AfAgentOrederDetailInforVo agentOrderDetailVo = getAgentOrderDetailInforVo(afOrderDo, afAgentOrderDo, context);
		
		resp.setResponseData(agentOrderDetailVo);
		return resp;
		
	}
	
	private AfAgentOrederDetailInforVo getAgentOrderDetailInforVo (AfOrderDo afOrderDo, AfAgentOrderDo afAgentOrderDo, FanbeiContext context){
		
		AfAgentOrederDetailInforVo agentOrderDetailVo = new AfAgentOrederDetailInforVo();
		
		// 取出所有所需要的值
		String goodName = afOrderDo.getGoodsName();
		String goodsIcon = afOrderDo.getGoodsIcon();
		Long count = NumberUtil.objToLongDefault(afOrderDo.getCount(), 1);
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(afOrderDo.getSaleAmount(), BigDecimal.ZERO);	// 商品的价格
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(afOrderDo.getActualAmount(),BigDecimal.ZERO); // 用户填写的价格
		String rebateAmount = ObjectUtils.toString(afOrderDo.getRebateAmount(), null);

		final AfResourceDo resource = afResourceService.getSingleResourceBytype(Constants.RES_THIRD_GOODS_REBATE_RATE);
		if(NumberUtil.objToDoubleDefault(rebateAmount, 0.00) == 0.00){
		rebateAmount = parseToVo(saleAmount,NumberUtil.objToBigDecimalDefault(resource.getValue(), BigDecimal.ZERO),
					NumberUtil.objToBigDecimalDefault(resource.getValue1(), BigDecimal.ZERO));	
		}
		String consignee = afAgentOrderDo.getConsignee();
		String province = afAgentOrderDo.getProvince();
		String city = afAgentOrderDo.getCity();
		String county = afAgentOrderDo.getCounty();
		String address = afAgentOrderDo.getAddress();
		String mobile = afAgentOrderDo.getMobile();
		String capture = afAgentOrderDo.getCapture();
		String remark = afAgentOrderDo.getRemark(); // 用户留言
		String gmtCreate = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT,afOrderDo.getGmtCreate());
		String payType =  afOrderDo.getPayType().equals("AP")?"返呗支付":"其他支付方式";
		// 取出一共分几期
		AfBorrowDo borrowDo = afBorrowService.getBorrowByOrderId(afOrderDo.getRid());
		if(borrowDo != null){
			String nper = borrowDo.getNper().toString(); // 分几期	
			payType =  afOrderDo.getPayType().equals("AP")?"返呗支付"+nper+"期":"其他支付方式";
		}
		String gmtPay = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT,afOrderDo.getGmtPay());
		/**
		 * 支付状态【N:(notpay)未支付,D:(dealing)支付中,P:(payed)已经支付,R:(refund)退款】
		 */
		String payStatus = afOrderDo.getPayStatus();
		String payTradeNo = afOrderDo.getPayTradeNo();
		/**
		 * 订单状态【NEW:新建/待付款/等待代买,DEALING:支付中,PAID:已支付/待收货,FINISHED:已收货/订单完成,
		 * REBATED:返利成功,CLOSED:订单关闭(未付款或退款成功,用户主动取消代买订单)，
		 * WAITING_REFUND等待退款 DEAL_REFUNDING:退款中】
		 */
		String status = afOrderDo.getStatus();
		if (context.getAppVersion() < 364){
			if (status.equals(OrderStatus.DEALING.getCode())) {
				status =OrderStatus.PAID.getCode();
			}
		}
		
		// 返利时间
		String gmtRebated = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afOrderDo.getGmtRebated());
		String gmtFinished = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afOrderDo.getGmtFinished());
		// 代买留言
		String agentMessage = afAgentOrderDo.getAgentMessage();
		String gmtAgentBuy = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afAgentOrderDo.getGmtAgentBuy());
		// 订单关闭理由
		String closedReason =afAgentOrderDo.getClosedDetail();
		if(StringUtils.isBlank(closedReason)){
			closedReason = afAgentOrderDo.getClosedReason(); 
		}
				
		String gmtClosed = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afAgentOrderDo.getGmtClosed()); // 用户取消订单时间
		// 订单取消理由
		String cancelReason = afAgentOrderDo.getCancelReason();
		String numId = afOrderDo.getNumId();
		// 根据订单状态和支付状态返回不同的值
		/**
		 * 订单状态【NEW:新建/待付款/等待代买,DEALING:支付中,PAID:已支付/待收货,FINISHED:已收货/订单完成,
		 * REBATED:返利成功,CLOSED:订单关闭(未付款或退款成功,用户主动取消代买订单)，
		 * WAITING_REFUND等待退款 DEAL_REFUNDING:退款中】
		 * 
		 * 支付状态【N:(notpay)未支付,D:(dealing)支付中,P:(payed)已经支付,R:(refund)退款】
		 */
		
//		优惠券类型【MOBILE：话费充值， REPAYMENT：还款, FULLVOUCHER:满减卷,CASH:现金奖励】
		AfUserCouponDo userCouponDo = afUserCouponService.getUserCouponById(afAgentOrderDo.getCouponId());
		if (userCouponDo != null){
			 AfCouponDo couponDo = afCouponService.getCouponById(userCouponDo.getCouponId());
			 if (couponDo != null){
				 if(StringUtils.equals("MOBILE", couponDo.getType())){
					 agentOrderDetailVo.setCouponName("充值劵");
				 }
				 
				 if(StringUtils.equals("REPAYMENT", couponDo.getType())){
					 agentOrderDetailVo.setCouponName("还款劵");
				 }
				 
				 if(StringUtils.equals("FULLVOUCHER", couponDo.getType())){
					 agentOrderDetailVo.setCouponName("满减劵");
				 }
				 
				 if(StringUtils.equals("CASH", couponDo.getType())){
					 agentOrderDetailVo.setCouponName("现金劵");
				 }
				 
				 if(StringUtils.equals("ACTIVITY", couponDo.getType())){
					 agentOrderDetailVo.setCouponName("会场劵");
				 }
				 agentOrderDetailVo.setCouponAmount(couponDo.getAmount());
				 
				 // 如果有优惠劵,那么实际支付金额就是填写的订单金额
				 BigDecimal actualPayAmount = actualAmount;
				 actualAmount = actualAmount.add(couponDo.getAmount());
				 
				 agentOrderDetailVo.setActualPayAmount(actualPayAmount);
				
			 }
		}
		
		agentOrderDetailVo.setStatus(StringUtils.isBlank(status)?"":status);
		agentOrderDetailVo.setPayStatus(StringUtils.isBlank(payStatus)?payStatus:"");
		agentOrderDetailVo.setConsignee(StringUtils.isBlank(consignee)?"":consignee);
		agentOrderDetailVo.setMobile(StringUtils.isBlank(mobile)?"":mobile);
		agentOrderDetailVo.setProvince(StringUtils.isBlank(province)?"":province);
		agentOrderDetailVo.setCity(StringUtils.isBlank(city)?"":city);
		agentOrderDetailVo.setCounty(StringUtils.isBlank(county)?"":county);
		agentOrderDetailVo.setAddress(StringUtils.isBlank(address)?"":address);
		agentOrderDetailVo.setGoodName(StringUtils.isBlank(goodName)?"":goodName);
		agentOrderDetailVo.setGoodsIcon(StringUtils.isBlank(goodsIcon)?"":goodsIcon);
		agentOrderDetailVo.setCount(count);
		agentOrderDetailVo.setCapture(StringUtils.isBlank(capture)?"":capture);
		agentOrderDetailVo.setRemark(StringUtils.isBlank(remark)?"":remark);
		agentOrderDetailVo.setRebateAmount(StringUtils.isBlank(rebateAmount)?"0":rebateAmount);
		agentOrderDetailVo.setAgentMessage(StringUtils.isBlank(agentMessage)?"":agentMessage);
		agentOrderDetailVo.setGmtCreate(StringUtils.isBlank(gmtCreate)?"":gmtCreate);
		agentOrderDetailVo.setPayType(StringUtils.isBlank(payType)?"":payType);
		agentOrderDetailVo.setGmtPay(StringUtils.isBlank(gmtPay)?"":gmtPay);
		agentOrderDetailVo.setPayTradeNo(StringUtils.isBlank(payTradeNo)?"":payTradeNo);
		agentOrderDetailVo.setGmtAgentBuy(StringUtils.isBlank(gmtAgentBuy)?"":gmtAgentBuy);
		agentOrderDetailVo.setGmtRebated(StringUtils.isBlank(gmtRebated)?"":gmtRebated);
		agentOrderDetailVo.setGmtFinished(StringUtils.isBlank(gmtFinished)?"":gmtFinished);
		agentOrderDetailVo.setClosedReason(StringUtils.isBlank(closedReason)?"":closedReason);
		agentOrderDetailVo.setCancelReason(StringUtils.isBlank(cancelReason)?"":cancelReason);
		agentOrderDetailVo.setGmtClosed(StringUtils.isBlank(gmtClosed)?"":gmtClosed);
		agentOrderDetailVo.setNumId(StringUtils.isBlank(numId)?"":numId);
		agentOrderDetailVo.setSaleAmount(saleAmount);
		agentOrderDetailVo.setActualAmount(actualAmount);
		agentOrderDetailVo.setNper(afOrderDo.getNper());
		//商品售价处理(订单价格除以商品数量)
		BigDecimal saleCount = NumberUtil.objToBigDecimalZeroToDefault(BigDecimal.valueOf(afOrderDo.getCount()), BigDecimal.ONE);
		agentOrderDetailVo.setGoodsSaleAmount(afOrderDo.getSaleAmount().divide(saleCount, 2));
		//售后相关设置
		Boolean isExistAftersaleApply = false;
		String afterSaleStatus = "";
		AfAftersaleApplyDo afAftersaleApplyDo = afAftersaleApplyService.getByOrderId(afOrderDo.getRid());
		if(afAftersaleApplyDo!=null){
			isExistAftersaleApply = true;
			afterSaleStatus = afAftersaleApplyDo.getStatus();
			agentOrderDetailVo.setAfterSaleStatus(afAftersaleApplyDo.getStatus());
			agentOrderDetailVo.setGmtRefundApply(afAftersaleApplyDo.getGmtApply());
		}else{
			agentOrderDetailVo.setGmtRefundApply(new Date(0));
			agentOrderDetailVo.setAfterSaleStatus("");
		}
		//状态备注及说明 
		AfOrderStatusMsgRemark orderStatusMsgRemark = AfOrderStatusMsgRemark.findRoleTypeByCodeAndOrderType(afOrderDo.getStatus(), afOrderDo.getOrderType(), afOrderDo.getPayType(),
				afOrderDo.getRebateAmount().compareTo(BigDecimal.ZERO)>0,afterSaleStatus, isExistAftersaleApply);
		if(orderStatusMsgRemark!=null){
			agentOrderDetailVo.setOrderStatusMsg(orderStatusMsgRemark.getStatusMsg());
			agentOrderDetailVo.setOrderStatusRemark(orderStatusMsgRemark.getStatusRemark());	
		}else{
			agentOrderDetailVo.setOrderStatusMsg("");
			agentOrderDetailVo.setOrderStatusRemark("");
		}
		return agentOrderDetailVo;
	}
	
	
	private String parseToVo(BigDecimal saleAmount, BigDecimal minRate,BigDecimal maxRate){
		BigDecimal minRebateAmount = saleAmount.multiply(minRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		BigDecimal maxRebateAmount = saleAmount.multiply(maxRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		return new StringBuffer("").append(minRebateAmount).append("~").append(maxRebateAmount).toString();
	}

}
