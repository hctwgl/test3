package com.ald.fanbei.api.web.api.agencybuy;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
import com.ald.fanbei.api.common.enums.AfAftersaleApplyStatus;
import com.ald.fanbei.api.common.enums.AfOrderStatusMsgRemark;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StatusConvertUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
		}else if(context.getAppVersion() >= 371){
			if (status.equals(OrderStatus.PAID.getCode())) {
				status =OrderStatus.REVIEW.getCode();
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
				
		String gmtClosed = DateUtil.convertDateToString(DateUtil.DATE_TIME_SHORT, afOrderDo.getGmtClosed()); // 用户取消订单时间
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
		BigDecimal couponAmount = BigDecimal.ZERO;
		// 如果有优惠劵,那么实际支付金额就是填写的订单金额
		BigDecimal actualPayAmount = actualAmount;
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
				 couponAmount = couponDo.getAmount();
				 actualAmount = actualAmount.add(couponAmount);
			 }
		}
		agentOrderDetailVo.setActualPayAmount(actualPayAmount);
		agentOrderDetailVo.setCouponAmount(couponAmount);
		
		agentOrderDetailVo.setOrderNo(afOrderDo.getOrderNo());
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
		agentOrderDetailVo.setGmtPayStart(new Date());
		agentOrderDetailVo.setGmtPayEnd(DateUtil.addHoures(afOrderDo.getGmtCreate(), Constants.ORDER_PAY_TIME_LIMIT));
		//商品售价处理(订单价格除以商品数量)
		BigDecimal saleCount = NumberUtil.objToBigDecimalZeroToDefault(BigDecimal.valueOf(afOrderDo.getCount()), BigDecimal.ONE);
		agentOrderDetailVo.setGoodsSaleAmount(afOrderDo.getSaleAmount().divide(saleCount, 2));
		//售后相关设置
		Boolean isExistAftersaleApply = false;
		String afterSaleStatus = "";
		AfAftersaleApplyDo afAftersaleApplyDo = afAftersaleApplyService.getByOrderId(afOrderDo.getRid());
		if(afAftersaleApplyDo!=null){
			afterSaleStatus = afAftersaleApplyDo.getStatus();
			agentOrderDetailVo.setAfterSaleStatus(afAftersaleApplyDo.getStatus());
			agentOrderDetailVo.setGmtRefundApply(afAftersaleApplyDo.getGmtApply());
		}else{
			agentOrderDetailVo.setGmtRefundApply(new Date(0));
			agentOrderDetailVo.setAfterSaleStatus("");
		}
		agentOrderDetailVo.setIsCanApplyAfterSale(afOrderService.isCanApplyAfterSale(afOrderDo.getRid()));
		//状态备注及说明 
		String closeReason = "";
		closeReason = afOrderDo.getCancelReason();
		if(StringUtil.isBlank(closeReason)){
			closeReason = afOrderDo.getClosedReason();
		}
		if(afAftersaleApplyDo!=null && !AfAftersaleApplyStatus.CLOSE.getCode().equals(afAftersaleApplyDo.getStatus())){
			//如果存在售后申请记录并且未被用户关闭，则标识记录，后续结合订单关闭状态，确认订单关闭是退款还是其它操作导致
			isExistAftersaleApply = true;
		}
		
		Boolean isExistRebates = afOrderDo.getRebateAmount().compareTo(BigDecimal.ZERO)>0;
		//代买类型订单，返利是一个范围，按最大返利来看是否存在返利,特殊处理
		if(isExistRebates==false){
			BigDecimal maxRate = NumberUtil.objToBigDecimalDefault(resource.getValue1(), BigDecimal.ZERO);
			BigDecimal maxRebateAmount = afOrderDo.getSaleAmount().multiply(maxRate).setScale(2,BigDecimal.ROUND_HALF_UP);
			if(maxRebateAmount.compareTo(BigDecimal.ZERO)>0){
				isExistRebates = true;
			}
		}
		
		StatusConvertUtil orderStatusMsgRemark = AfOrderStatusMsgRemark.findRoleTypeByCodeAndOrderType(afOrderDo.getStatus(), afOrderDo.getOrderType(), afOrderDo.getPayType(),
				isExistRebates,afterSaleStatus, isExistAftersaleApply,closeReason,afOrderDo.getStatusRemark());
		if(orderStatusMsgRemark!=null){
			agentOrderDetailVo.setOrderStatusMsg(orderStatusMsgRemark.getStatusMsg());
			agentOrderDetailVo.setOrderStatusRemark(orderStatusMsgRemark.getStatusRemark());	
		}else{
			agentOrderDetailVo.setOrderStatusMsg("");
			agentOrderDetailVo.setOrderStatusRemark("");
		}
		//订单是否满足删除条件设置 1、订单完成 （无返利-确认收货 有返利-返利完成）2、订单关闭
		String isCanDelOrder = YesNoStatus.NO.getCode();
		if(OrderStatus.CLOSED.getCode().equals(afOrderDo.getStatus()) 
				|| OrderStatus.REBATED.getCode().equals(afOrderDo.getStatus())
				||(OrderStatus.FINISHED.getCode().equals(afOrderDo.getStatus()) && isExistRebates==false)){
			isCanDelOrder = YesNoStatus.YES.getCode();
		}
		agentOrderDetailVo.setIsCanDelOrder(isCanDelOrder);
				
		//发货物流信息及时间
		agentOrderDetailVo.setLogisticsInfo(StringUtil.logisticsInfoDeal(afOrderDo.getLogisticsInfo()));
		agentOrderDetailVo.setLogisticsCompany(afOrderDo.getLogisticsCompany());
		agentOrderDetailVo.setLogisticsNo(afOrderDo.getLogisticsNo());
		if(afOrderDo.getGmtDeliver()!=null){
			agentOrderDetailVo.setGmtDeliver(afOrderDo.getGmtDeliver());
		}else{
			agentOrderDetailVo.setGmtDeliver(new Date(0));
		}
		//分期信息设置 ¥300.00X12期
		if(borrowDo!=null){
			agentOrderDetailVo.setInstallmentInfo(NumberUtil.format2Str(borrowDo.getNperAmount())+"X"+borrowDo.getNper()+"期");
		}else{
			agentOrderDetailVo.setInstallmentInfo("");
		}
		agentOrderDetailVo.setQuotaAmount(afOrderDo.getBorrowAmount());
		agentOrderDetailVo.setBankPayAmount(afOrderDo.getBankAmount());
		//update by renchunlei 2017-08-21
		if(StringUtils.isNotBlank(afOrderDo.getLogisticsNo())){
			//有物流单号就显示物流信息
			agentOrderDetailVo.setShowLogistics(1);
		}
		return agentOrderDetailVo;
	}
	
	
	private String parseToVo(BigDecimal saleAmount, BigDecimal minRate,BigDecimal maxRate){
		BigDecimal minRebateAmount = saleAmount.multiply(minRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		BigDecimal maxRebateAmount = saleAmount.multiply(maxRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		return new StringBuffer("").append(minRebateAmount).append("~").append(maxRebateAmount).toString();
	}

}
