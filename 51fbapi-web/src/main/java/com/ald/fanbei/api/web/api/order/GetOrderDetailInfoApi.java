package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StatusConvertUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfOrderVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @类描述：获取订单详情
 * @author 何鑫 2017年2月17日下午16:13:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderDetailInfoApi")
public class GetOrderDetailInfoApi implements ApiHandle {

	@Resource
	AfOrderService afOrderService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfAftersaleApplyService afAftersaleApplyService;
	@Resource
	AfTradeBusinessInfoService afTradeBusinessInfoService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGoodsPriceService afGoodsPriceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
									 FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long orderId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("orderId"), ""), 0l);
		AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId, userId);
		if (null == orderInfo) {
			throw new FanbeiException(FanbeiExceptionCode.USER_ORDER_NOT_EXIST_ERROR);
		}
		AfOrderVo orderVo = getOrderVo(orderInfo);
		resp.setResponseData(orderVo);
		return resp;
	}

	private AfOrderVo getOrderVo(AfOrderDo order) {
		AfOrderVo vo = new AfOrderVo();
		vo.setGoodsPriceName(order.getGoodsPriceName());
		vo.setGmtCreate(order.getGmtCreate());
		vo.setGmtFinished(order.getGmtFinished());
		vo.setGmtRebated(order.getGmtRebated());
		vo.setGoodsCount(order.getCount());
		vo.setGoodsIcon(order.getGoodsIcon());
		vo.setGoodsName(order.getGoodsName());
		vo.setOrderAmount(order.getSaleAmount());
		AfUserCouponDo userCouponDo = afUserCouponService.getUserCouponById(order.getUserCouponId());
		BigDecimal couponAmount = BigDecimal.ZERO;
		if (userCouponDo != null) {
			AfCouponDo couponDo = afCouponService.getCouponById(userCouponDo.getCouponId());
			if (couponDo != null) {
				couponAmount = couponDo.getAmount();
			}
		}
		vo.setCouponAmount(couponAmount);
		vo.setActualAmount(order.getActualAmount());
		vo.setOrderNo(order.getOrderNo());
		vo.setOrderStatus(order.getStatus());
		vo.setRebateAmount(order.getRebateAmount());
		vo.setType(order.getOrderType());
		vo.setGmtClosed(order.getGmtClosed());
		vo.setMobile(order.getMobile());
		if (StringUtil.equals(order.getPayStatus(), PayStatus.PAYED.getCode())) {
			vo.setGmtPay(DateUtil.formatDateToYYYYMMddHHmmss(order.getGmtPay()));
		}
		//update by renchunlei 2017-08-21
		if (StringUtils.isNotBlank(order.getLogisticsNo())) {
			//有物流单号就显示物流信息
			vo.setShowLogistics(1);
		}
		vo.setAddress(StringUtil.appendStrs(order.getProvince(), order.getCity(), order.getDistrict(), order.getAddress()));
		vo.setConsignee(order.getConsignee());
		vo.setConsigneeMobile(order.getConsigneeMobile());
		vo.setInvoiceHeader(order.getInvoiceHeader());
		vo.setLogisticsInfo(StringUtil.logisticsInfoDeal(order.getLogisticsInfo()));
		vo.setPayType(order.getPayType());
		vo.setGmtPayStart(new Date());
		vo.setGmtPayEnd(DateUtil.addHoures(order.getGmtCreate(), Constants.ORDER_PAY_TIME_LIMIT));
		vo.setGoodsId(order.getGoodsId());
		//商品售价处理(订单价格除以商品数量)
		BigDecimal saleCount = NumberUtil.objToBigDecimalZeroToDefault(BigDecimal.valueOf(order.getCount()), BigDecimal.ONE);
		vo.setGoodsSaleAmount(order.getSaleAmount().divide(saleCount, 2));
		//售后相关设置
		Boolean isExistAftersaleApply = false;
		String afterSaleStatus = "";
		AfAftersaleApplyDo afAftersaleApplyDo = afAftersaleApplyService.getByOrderId(order.getRid());
		if (afAftersaleApplyDo != null) {
			afterSaleStatus = afAftersaleApplyDo.getStatus();
			vo.setAfterSaleStatus(afAftersaleApplyDo.getStatus());
			vo.setGmtRefundApply(afAftersaleApplyDo.getGmtApply());
		} else {
			vo.setGmtRefundApply(new Date(0));
			vo.setAfterSaleStatus("");
		}
		vo.setIsCanApplyAfterSale(afOrderService.isCanApplyAfterSale(order.getRid()));
		//状态备注及说明 
		String closeReason = "";
		closeReason = order.getCancelReason();
		if (StringUtil.isBlank(closeReason)) {
			closeReason = order.getClosedReason();
		}
		if (afAftersaleApplyDo != null && !AfAftersaleApplyStatus.CLOSE.getCode().equals(afAftersaleApplyDo.getStatus())) {
			//如果存在售后申请记录并且未被用户关闭，则标识记录，后续结合订单关闭状态，确认订单关闭是退款还是其它操作导致
			isExistAftersaleApply = true;
		}
		Boolean isExistRebates = order.getRebateAmount().compareTo(BigDecimal.ZERO) > 0;
		//代买类型订单，返利是一个范围，按最大返利来看是否存在返利,特殊处理
		if (isExistRebates == false && OrderType.AGENTBUY.getCode().equals(order.getOrderType())) {
			AfResourceDo resource = afResourceService.getSingleResourceBytype(Constants.RES_THIRD_GOODS_REBATE_RATE);
			BigDecimal maxRate = NumberUtil.objToBigDecimalDefault(resource.getValue1(), BigDecimal.ZERO);
			BigDecimal maxRebateAmount = order.getSaleAmount().multiply(maxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
			if (maxRebateAmount.compareTo(BigDecimal.ZERO) > 0) {
				isExistRebates = true;
			}
		}
		StatusConvertUtil orderStatusMsgRemark = AfOrderStatusMsgRemark.findRoleTypeByCodeAndOrderType(order.getStatus(), order.getOrderType(), order.getPayType(),
				isExistRebates, afterSaleStatus, isExistAftersaleApply, closeReason, order.getStatusRemark());
		if (orderStatusMsgRemark != null) {
			vo.setOrderStatusMsg(orderStatusMsgRemark.getStatusMsg());
			vo.setOrderStatusRemark(orderStatusMsgRemark.getStatusRemark());
		} else {
			vo.setOrderStatusMsg("");
			vo.setOrderStatusRemark("");
		}

		//订单是否满足删除条件设置 1、订单完成 （无返利-确认收货 有返利-返利完成）2、订单关闭
		String isCanDelOrder = YesNoStatus.NO.getCode();
		if (OrderStatus.CLOSED.getCode().equals(order.getStatus())
				|| OrderStatus.REBATED.getCode().equals(order.getStatus())
				|| (OrderStatus.FINISHED.getCode().equals(order.getStatus()) && isExistRebates == false)) {
			isCanDelOrder = YesNoStatus.YES.getCode();
		}
		vo.setIsCanDelOrder(isCanDelOrder);

		//发货物流信息及时间
		vo.setLogisticsCompany(order.getLogisticsCompany());
		vo.setLogisticsNo(order.getLogisticsNo());
		if (order.getGmtDeliver() != null) {
			vo.setGmtDeliver(order.getGmtDeliver());
		} else {
			vo.setGmtDeliver(new Date(0));
		}
		//查询分期信息
		AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(order.getRid());
		if (afBorrowDo != null) {
			//分期信息设置 ¥300.00X12期
			vo.setInstallmentInfo(NumberUtil.format2Str(afBorrowDo.getNperAmount()) + "X" + afBorrowDo.getNper() + "期");
			vo.setNper(afBorrowDo.getNper());
			vo.setNperAmount(afBorrowDo.getNperAmount());
		} else {
			vo.setInstallmentInfo("");
			vo.setNper(order.getNper());
			vo.setNperAmount(BigDecimal.ZERO);
		}
		vo.setQuotaAmount(order.getBorrowAmount());
		vo.setBankPayAmount(order.getBankAmount());

		vo.setFeeAmount(order.getFeeAmount());
		vo.setCardType(order.getCardType());
		//实付金额需要加上信用卡手续费
		vo.setActualAmount(vo.getActualAmount().add(vo.getFeeAmount()));
		vo.setRefundMsg(getRefundMsg(order));
		return vo;
	}

	private String getRefundMsg(AfOrderDo order) {
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("REFUNDSERVICE", "MESSAGE");

		if (PayType.BANK.getCode().equals(order.getPayType())) {
			if (BankCardType.CREDIT.getCode().equals(order.getCardType())) {
				return afResourceDo.getValue1();
			} else if (BankCardType.DEBIT.getCode().equals(order.getCardType())) {
				return afResourceDo.getValue();
			}
		} else if (PayType.COMBINATION_PAY.getCode().equals(order.getPayType())) {
			if (BankCardType.CREDIT.getCode().equals(order.getCardType())) {
				return afResourceDo.getValue3();
			} else if (BankCardType.DEBIT.getCode().equals(order.getCardType())) {
				return afResourceDo.getValue2();
			}
		} else if (PayType.AGENT_PAY.getCode().equals(order.getCardType())) {
			return StringUtils.isBlank(afResourceDo.getValue4()) ? "" : afResourceDo.getValue4();
		}

		return "";
	}

}
