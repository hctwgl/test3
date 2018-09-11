package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRenewalServiceImpl.JsdRenewalDealBo;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowOrderRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.google.gson.Gson;

/**  
 * @Description: 极速贷  续期确认支付
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年8月22日
 */
@Component("jsdConfirmRenewalPayApi")
public class JsdConfirmRenewalPayApi implements JsdH5Handle {

	@Resource
	JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
	@Resource
	JsdBorrowCashService jsdBorrowCashService;
	@Resource
	JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
	@Resource
	JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
	@Resource
	JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
	@Resource
	JsdUserService jsdUserService;
	@Resource
	JsdUserBankcardService jsdUserBankcardService;
	@Resource
	JsdResourceService JsdResourceService;
	@Resource
	JsdBorrowLegalOrderRepaymentService jsdBorrowLegalOrderRepaymentService;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	GeneratorClusterNo generatorClusterNo;

	@Override
	public JsdH5HandleResponse process(Context context) {
		
		JsdRenewalDealBo paramBo = this.getParam(context);

		// 借款记录
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(paramBo.borrowNo);
		if(borrowCashDo == null || !StringUtil.equals(borrowCashDo.getStatus(), JsdBorrowCashStatus.TRANSFERRED.name())){
			throw new FanbeiException("No borrow can renewal", FanbeiExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
		}
		
		// 续期校验
		jsdBorrowCashRenewalService.checkCanRenewal(borrowCashDo);

		// 用户信息
		JsdUserDo userDo = jsdUserService.getById(paramBo.userId);
		if(userDo == null) {
			throw new FanbeiException("user not exist error", FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
		}
		// 银行卡信息
		HashMap<String,Object> map = jsdUserBankcardService.getBankByBankNoAndUserId(paramBo.userId, paramBo.bankNo);
		paramBo.bankChannel = map.get("bankChannel").toString();
		paramBo.bankName = map.get("bankName").toString();
		
		paramBo.userDo = userDo;
		paramBo.borrowCashDo = borrowCashDo;

		long result = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {

					JsdBorrowCashRenewalDo renewalDo = buildRenewalDo(paramBo);
					JsdBorrowLegalOrderDo legalOrderDo = buildOrderDo(paramBo);
					JsdBorrowLegalOrderCashDo legalOrderCashDo = buildOrderCashDo(paramBo);
					JsdBorrowLegalOrderCashDo lastOrderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(borrowCashDo.getRid());
					JsdBorrowLegalOrderRepaymentDo orderCashRepaymentDo = null;
					if(lastOrderCashDo.getStatus().equals(JsdBorrowLegalOrderCashStatus.AWAIT_REPAY.getCode()) 
							|| lastOrderCashDo.getStatus().equals(JsdBorrowLegalOrderCashStatus.PART_REPAID.getCode())){
						orderCashRepaymentDo = buildOrderCashRepaymentDo(paramBo);
						jsdBorrowLegalOrderRepaymentService.saveRecord(orderCashRepaymentDo);
					}

					jsdBorrowCashRenewalService.saveRecord(renewalDo);
					jsdBorrowLegalOrderService.saveRecord(legalOrderDo);
					legalOrderCashDo.setBorrowLegalOrderId(legalOrderDo.getRid());
					jsdBorrowLegalOrderCashService.saveRecord(legalOrderCashDo);

					paramBo.renewalDo = renewalDo;
					paramBo.legalOrderDo = legalOrderDo;
					paramBo.legalOrderCashDo = legalOrderCashDo;
					logger.info("JsdConfirmRenewal renewalId="+renewalDo.getRid()+",legalOrder="+legalOrderDo.getRid()+",legalOrderCash="+legalOrderCashDo.getRid());
					return 1l;
				}catch(Exception e) {
					status.setRollbackOnly();
					logger.info("JsdConfirmRenewal sava record error", e);
					throw e;
				}
			}
		});

		if(result == 1l){
			Map<String, Object> resultMap = jsdBorrowCashRenewalService.doRenewal(paramBo);
			JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功", resultMap);
			return resp;
		}else {
			throw new FanbeiException("JsdConfirmRenewal error", FanbeiExceptionCode.RENEWAL_FAIL_ERROR);
		}
	}



	/**
	 * 续期记录
	 */
	private JsdBorrowCashRenewalDo buildRenewalDo(JsdRenewalDealBo paramBo) {
		JsdBorrowCashDo borrowCashDo = paramBo.borrowCashDo;

		// 上期订单借款
		JsdBorrowLegalOrderCashDo lastOrderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(borrowCashDo.getRid());
		BigDecimal orderWaitAmount = BigDecimalUtil.add(lastOrderCashDo.getAmount(),lastOrderCashDo.getInterestAmount(),lastOrderCashDo.getPoundageAmount(),lastOrderCashDo.getOverdueAmount(),
				lastOrderCashDo.getSumRepaidInterest(),lastOrderCashDo.getSumRepaidOverdue(),lastOrderCashDo.getSumRepaidPoundage()).subtract(lastOrderCashDo.getRepaidAmount());

		// 上期借款手续费
		BigDecimal poundage = borrowCashDo.getPoundageAmount();
		// 上期借款利息
		BigDecimal rateAmount = borrowCashDo.getInterestAmount();
		// 上期借款逾期费
		BigDecimal overdueAmount = borrowCashDo.getOverdueAmount();

		// 续借需要支付本金
		BigDecimal capital = BigDecimalUtil.multiply(borrowCashDo.getAmount(), paramBo.capitalRate);

		// 总金额
		BigDecimal allAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumRepaidOverdue(), borrowCashDo.getSumRepaidInterest(), borrowCashDo.getSumRepaidPoundage());
		// 续期金额 = 总金额 - 借款已还金额 - 续借需要支付本金
		BigDecimal renewalAmount = BigDecimalUtil.subtract(allAmount, borrowCashDo.getRepayAmount()).subtract(capital);

		// 下期利息
		BigDecimal nextInterest = BigDecimalUtil.multiply(renewalAmount, paramBo.cashRate, new BigDecimal(paramBo.delayDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		// 下期手续费
		BigDecimal nextPoundage = BigDecimalUtil.multiply(renewalAmount, paramBo.cashPoundageRate, new BigDecimal(paramBo.delayDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));

		// 续期实付=上期利息 +上期逾期费+上期手续费+本金还款部分 +上期商品价格(待还部分)
		BigDecimal actualAmount = BigDecimalUtil.add(poundage, rateAmount ,overdueAmount, capital, orderWaitAmount);

		JsdBorrowCashRenewalDo renewalDo = new JsdBorrowCashRenewalDo();
		renewalDo.setBorrowId(borrowCashDo.getRid());
		renewalDo.setUserId(paramBo.userId);
		renewalDo.setStatus(JsdRenewalDetailStatus.APPLY.getCode());
		renewalDo.setRemark(borrowCashDo.getRemark());
		renewalDo.setRenewalAmount(renewalAmount);
		renewalDo.setPriorInterest(borrowCashDo.getInterestAmount());
		renewalDo.setPriorPoundage(borrowCashDo.getPoundageAmount());
		renewalDo.setPriorOverdue(borrowCashDo.getOverdueAmount());
		renewalDo.setCapital(capital);
		renewalDo.setActualAmount(actualAmount);
		renewalDo.setNextInterest(nextInterest);
		renewalDo.setNextPoundage(nextPoundage);
		renewalDo.setCardName(paramBo.bankName);
		renewalDo.setCardNumber(paramBo.bankNo);
		renewalDo.setTradeNo(paramBo.renewalNo);
		renewalDo.setTradeNoXgxy(paramBo.delayNo);
		renewalDo.setRenewalDay(paramBo.delayDay);
		renewalDo.setPoundageRate(paramBo.cashPoundageRate);
		renewalDo.setBaseBankRate(paramBo.cashRate);
		renewalDo.setOverdueDay(borrowCashDo.getOverdueDay().intValue());
		renewalDo.setOverdueStatus(borrowCashDo.getOverdueStatus());
		renewalDo.setGmtPlanRepayment(borrowCashDo.getGmtPlanRepayment());
		renewalDo.setGmtCreate(new Date());
		renewalDo.setGmtModified(new Date());
		
		return renewalDo;
	}
	/**
	 * 续期订单记录
	 */
	private JsdBorrowLegalOrderDo buildOrderDo(JsdRenewalDealBo paramBo) {
		JsdBorrowLegalOrderDo orderDo = new JsdBorrowLegalOrderDo();
		orderDo.setGmtCreate(new Date());
		orderDo.setGmtModified(new Date());
		orderDo.setUserId(paramBo.userId);
		orderDo.setBorrowId(paramBo.borrowCashDo.getRid());
		orderDo.setOrderNo(paramBo.renewalNo);
		orderDo.setStatus(JsdBorrowLegalOrderStatus.UNPAID.getCode());
		orderDo.setPriceAmount(paramBo.goodsPrice);
		orderDo.setGoodsName(paramBo.goodsName);

		orderDo.setGoodsId(0);
		orderDo.setAddress("");
		orderDo.setLogisticsCompany("");
		orderDo.setLogisticsInfo("");
		orderDo.setLogisticsNo("");
		orderDo.setDeliveryPhone("");
		orderDo.setDeliveryUser("");;
		orderDo.setSmartAddressScore(0);
//		orderDo.setGmtDeliver();
//		orderDo.setGmtConfirmReceived();

		return orderDo;
	}
	/**
	 * 续期订单借款记录
	 */
	private JsdBorrowLegalOrderCashDo buildOrderCashDo(JsdRenewalDealBo paramBo) {

		JsdBorrowCashDo borrowCashDo = paramBo.borrowCashDo;
		JsdBorrowLegalOrderCashDo lastOrderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(borrowCashDo.getRid());
		int renewalDay = paramBo.delayDay.intValue();

		JsdBorrowLegalOrderCashDo orderCashDo = new JsdBorrowLegalOrderCashDo();
		orderCashDo.setUserId(paramBo.userId);
		orderCashDo.setBorrowId(borrowCashDo.getRid());
		orderCashDo.setCashNo(paramBo.renewalNo);
		orderCashDo.setType(renewalDay+"");
		orderCashDo.setAmount(paramBo.goodsPrice);
		orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.APPLYING.getCode());
		orderCashDo.setBorrowRemark(lastOrderCashDo.getBorrowRemark());
		orderCashDo.setRefundRemark(lastOrderCashDo.getRefundRemark());
		orderCashDo.setOverdueDay((short)0);
		orderCashDo.setOverdueStatus("N");
		orderCashDo.setRepaidAmount(BigDecimal.ZERO);
		orderCashDo.setOverdueAmount(BigDecimal.ZERO);
		orderCashDo.setSumRepaidPoundage(BigDecimal.ZERO);
		
		orderCashDo.setPoundageAmount(paramBo.goodsPrice.multiply(paramBo.orderPoundageRate).multiply(new BigDecimal(renewalDay)).divide(new BigDecimal(Constants.ONE_YEAY_DAYS) ,2 , RoundingMode.HALF_UP));
		orderCashDo.setInterestAmount(paramBo.goodsPrice.multiply(paramBo.orderRate).multiply(new BigDecimal(renewalDay)).divide(new BigDecimal(Constants.ONE_YEAY_DAYS) ,2 , RoundingMode.HALF_UP));
		orderCashDo.setPoundageRate(paramBo.orderPoundageRate);
		orderCashDo.setInterestRate(paramBo.orderRate);
		orderCashDo.setOverdueRate(paramBo.orderOverdueRate);
//		orderCashDo.setPoundageAmount(BigDecimal.TEN);
//		orderCashDo.setInterestAmount(BigDecimal.ZERO);
//		orderCashDo.setPoundageRate(BigDecimal.ZERO);
//		orderCashDo.setInterestRate(BigDecimal.ZERO);
		
		orderCashDo.setSumRepaidOverdue(BigDecimal.ZERO);
		orderCashDo.setSumRepaidInterest(BigDecimal.ZERO);
		Date date = DateUtil.addDays(lastOrderCashDo.getGmtPlanRepay(), renewalDay);
		orderCashDo.setGmtPlanRepay(date);
		orderCashDo.setGmtCreate(new Date());
		

		return orderCashDo;
	}

	/**
	 * 续期订单借款记录
	 */
	private JsdBorrowLegalOrderRepaymentDo buildOrderCashRepaymentDo(JsdRenewalDealBo paramBo) {
		JsdBorrowCashDo borrowCashDo = paramBo.borrowCashDo;
		JsdBorrowLegalOrderCashDo lastOrderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(borrowCashDo.getRid());

		JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = new JsdBorrowLegalOrderRepaymentDo();
		orderRepaymentDo.setUserId(paramBo.userId);
		orderRepaymentDo.setBorrowId(borrowCashDo.getRid());
		orderRepaymentDo.setBorrowLegalOrderCashId(lastOrderCashDo.getRid());
		orderRepaymentDo.setRepayAmount(BigDecimalUtil.add(lastOrderCashDo.getAmount(),lastOrderCashDo.getInterestAmount(),lastOrderCashDo.getPoundageAmount(),lastOrderCashDo.getOverdueAmount(),
				lastOrderCashDo.getSumRepaidInterest(),lastOrderCashDo.getSumRepaidOverdue(),lastOrderCashDo.getSumRepaidPoundage()).subtract(lastOrderCashDo.getRepaidAmount()));
		orderRepaymentDo.setActualAmount(BigDecimal.ZERO);
		orderRepaymentDo.setName(Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH);
		orderRepaymentDo.setTradeNo(paramBo.delayNo);
		orderRepaymentDo.setStatus(JsdBorrowOrderRepaymentStatus.APPLY.name());
		orderRepaymentDo.setCardName(paramBo.bankName);
		orderRepaymentDo.setCardNo(paramBo.bankNo);
		orderRepaymentDo.setStatus(JsdBorrowOrderRepaymentStatus.APPLY.getCode());
		orderRepaymentDo.setCardName(paramBo.bankName);
		orderRepaymentDo.setCardNo(paramBo.bankNo);
		orderRepaymentDo.setGmtCreate(new Date());
		orderRepaymentDo.setGmtModified(new Date());

		return orderRepaymentDo;
	}


	/**
	 * 请求参数
	 */
	@SuppressWarnings("unchecked")
	private JsdRenewalDealBo getParam(Context context){

		JsdRenewalDealBo bo = new JsdRenewalDealBo();
		try {
			bo.borrowNo = ObjectUtils.toString(context.getDataMap().get("borrowNo"), "");	// 借款编号，对应借款表 trade_no_xgxy
			bo.delayNo = ObjectUtils.toString(context.getDataMap().get("delayNo"), "");		// 展期编号
			bo.bankNo = ObjectUtils.toString(context.getDataMap().get("bankNo"), "");		// 银行卡号
			bo.amount = NumberUtil.objToBigDecimalDefault(context.getDataMap().get("amount"),BigDecimal.ZERO);	// 金额
			bo.delayDay = NumberUtil.objToLongDefault(context.getDataMap().get("delayDay"), 0l);		// 展期天数
			bo.isTying = ObjectUtils.toString(context.getDataMap().get("isTying"), "");				// 是否搭售【Y：搭售，N：不搭售】
			bo.tyingType = ObjectUtils.toString(context.getDataMap().get("tyingType"), "");			// 搭售模式【BEHEAD：砍头，SELL：赊销】（搭售为Y时）
			bo.userId = context.getUserId();
	
			String goodsInfoStr = ObjectUtils.toString(context.getDataMap().get("goodsInfo"), "");
			Map<String,String> goodsInfo = new HashMap<String, String>();
			Gson gson = new Gson();
			goodsInfo = gson.fromJson(goodsInfoStr, goodsInfo.getClass());
			bo.goodsName = ObjectUtils.toString(goodsInfo.get("goodsName"), "");
			bo.goodsImage = ObjectUtils.toString(goodsInfo.get("goodsImage"), "");
			bo.goodsPrice = NumberUtil.objToBigDecimalDefault(goodsInfo.get("goodsPrice"), BigDecimal.ZERO);
			
			bo.renewalNo = generatorClusterNo.getJsdRenewalNo();
		} catch (Exception e) {
			throw new FanbeiException(FanbeiExceptionCode.JSD_PARAMS_ERROR);
		}
		
		if(StringUtil.equals("N", bo.isTying) && StringUtil.equals("BEHEAD", bo.tyingType)) {
			throw new FanbeiException(FanbeiExceptionCode.FUNCTIONAL_MAINTENANCE);
		}
		this.getRateInfo(bo);

		return bo;
	}

	/**
	 * 获取利率
	 */
	private void getRateInfo(JsdRenewalDealBo paramBo) {
		// 利率配置
		ResourceRateInfoBo borrowRateInfo = JsdResourceService.getRateInfo(paramBo.delayDay.toString());
		ResourceRateInfoBo orderRateInfo = JsdResourceService.getOrderRateInfo(paramBo.delayDay.toString());
		
		JsdResourceDo renewalResource = JsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RENEWAL_INFO.getCode());
		if(renewalResource ==null) 
			throw new FanbeiException(FanbeiExceptionCode.GET_JSD_RATE_ERROR);

		//借款手续费率
		paramBo.cashPoundageRate = borrowRateInfo.serviceRate;
		//借款利率
		paramBo.cashRate = borrowRateInfo.interestRate;
		paramBo.cashOverdueRate = borrowRateInfo.overdueRate;
		
		//需还本金比例
		paramBo.capitalRate = new BigDecimal(renewalResource.getValue1());
		//订单手续费率
		paramBo.orderPoundageRate = orderRateInfo.serviceRate;
		//订单利率
		paramBo.orderRate = orderRateInfo.interestRate;
		paramBo.orderOverdueRate = orderRateInfo.overdueRate;
	}
}
