package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRenewalServiceImpl.JsdRenewalDealBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**  
 * @Description: 极速贷  续期确认支付
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年8月22日
 */
@Component("jsdConfirmRenewalPayApi")
public class JsdConfirmRenewalPayApi implements DsedH5Handle {

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
	
	@Override
	public DsedH5HandleResponse process(Context context) {
		
		JsdRenewalDealBo paramBo = getParam(context);
		
		// 借款记录
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByBorrowNo(paramBo.borrowNo);
		
		// 最近一次还款记录
		JsdBorrowCashRepaymentDo lastRepaymentDo = jsdBorrowCashRepaymentService.getLastByBorrowId(borrowCashDo.getRid());
		if (null != lastRepaymentDo && StringUtils.equals(lastRepaymentDo.getStatus(), "P")) {
            throw new FanbeiException("There is a repayment is processing", FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING);
        }
		
		// 最近一次续期记录
		JsdBorrowCashRenewalDo lastRenewalDo = jsdBorrowCashRenewalService.getLastJsdRenewalByBorrowId(borrowCashDo.getRid());
		if (null != lastRenewalDo && StringUtils.equals(lastRenewalDo.getStatus(), "P")) {
            throw new FanbeiException("There is a renewal is processing", FanbeiExceptionCode.HAVE_A_RENEWAL_PROCESSING);
        }
		
		
		// 用户信息
		JsdUserDo userDo = jsdUserService.getById(paramBo.userId);
		JsdUserBankcardDo userBankcardDo = jsdUserBankcardService.getByBankNo(paramBo.bankNo);
		paramBo.userDo = userDo;
		paramBo.userBankDo = userBankcardDo;
		paramBo.borrowCashDo = borrowCashDo;
		
		
		JsdBorrowCashRenewalDo renewalDo = buildRenewalDo(paramBo);
		JsdBorrowLegalOrderDo legalOrderDo = buildOrderDo(paramBo);
		JsdBorrowLegalOrderCashDo legalOrderCashDo = buildOrderCashDo(paramBo);
		
		paramBo.renewalDo = renewalDo;
		paramBo.legalOrderDo = legalOrderDo;
		paramBo.legalOrderCashDo = legalOrderCashDo;
		
		Map<String, Object> resultMap = jsdBorrowCashRenewalService.doRenewal(paramBo);
		
		DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功", resultMap);
        return resp;
	}


	/**
	 * 续期记录
	 */
	private JsdBorrowCashRenewalDo buildRenewalDo(JsdRenewalDealBo paramBo) {
		JsdBorrowCashDo borrowCashDo = paramBo.borrowCashDo;
		
		// 上期借款手续费
		BigDecimal poundage = borrowCashDo.getPoundage();
		// 上期借款利息
		BigDecimal rateAmount = borrowCashDo.getRateAmount();
		// 上期借款逾期费
		BigDecimal overdueAmount = borrowCashDo.getOverdueAmount();
		
		// 续借需要支付本金
		BigDecimal capital = BigDecimalUtil.multiply(borrowCashDo.getAmount(), paramBo.capitalRate);
		
		// 总金额
		BigDecimal allAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumOverdue(), borrowCashDo.getSumRate(), borrowCashDo.getSumRenewalPoundage());
		// 续期金额 = 总金额 - 借款已还金额 - 续借需要支付本金
		BigDecimal renewalAmount = BigDecimalUtil.subtract(allAmount, borrowCashDo.getRepayAmount()).subtract(capital);
		
		// 下期利息
		BigDecimal nextInterest = BigDecimalUtil.multiply(renewalAmount, paramBo.rate, new BigDecimal(paramBo.delayDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		// 下期手续费
		BigDecimal nextPoundage = BigDecimalUtil.multiply(renewalAmount, paramBo.poundageRate, new BigDecimal(paramBo.delayDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		
		// 续期实付=上期利息 +上期逾期费+上期手续费+本金还款部分 +上期商品价格
		BigDecimal actualAmount = BigDecimalUtil.add(poundage, rateAmount ,overdueAmount, capital, paramBo.goodsPrice);
		
		JsdBorrowCashRenewalDo renewalDo = new JsdBorrowCashRenewalDo();
		renewalDo.setBorrowId(borrowCashDo.getRid());
		renewalDo.setUserId(paramBo.userId);
		renewalDo.setStatus("A");
		renewalDo.setRemark("");
		renewalDo.setRenewalAmount(renewalAmount);
		renewalDo.setPriorInterest(borrowCashDo.getRateAmount());
		renewalDo.setPriorPoundage(borrowCashDo.getPoundage());
		renewalDo.setPriorOverdue(borrowCashDo.getOverdueAmount());
		renewalDo.setCapital(capital);
		renewalDo.setActualAmount(actualAmount);
		renewalDo.setNextInterest(nextInterest);
		renewalDo.setNextPoundage(nextPoundage);
		renewalDo.setCardName(paramBo.userBankDo.getBankName());
		renewalDo.setCardNumber(paramBo.userBankDo.getBankCardNumber());
		renewalDo.setRenewalNo(paramBo.delayNo);
		renewalDo.setTradeNo(paramBo.delayNo);
		renewalDo.setRenewalDay(paramBo.delayDay);
		renewalDo.setPoundageRate(paramBo.poundageRate);
		renewalDo.setBaseBankRate(paramBo.rate);
		renewalDo.setOverdueDay(borrowCashDo.getOverdueDay().intValue());
		renewalDo.setOverdueStatus(borrowCashDo.getOverdueStatus());
		renewalDo.setGmtPlanRepayment(borrowCashDo.getGmtPlanRepayment());
		
		return renewalDo;
	}
	/**
	 * 续期订单记录
	 */
	private JsdBorrowLegalOrderDo buildOrderDo(JsdRenewalDealBo paramBo) {
		JsdBorrowLegalOrderDo orderDo = new JsdBorrowLegalOrderDo();
		
		return orderDo;
	}
	/**
	 * 续期订单借款记录
	 */
	private JsdBorrowLegalOrderCashDo buildOrderCashDo(JsdRenewalDealBo paramBo) {
		JsdBorrowLegalOrderCashDo orderCashDo = new JsdBorrowLegalOrderCashDo();
		
		return orderCashDo;
	}
	
	
	/**
	 * 请求参数
	 */
	private JsdRenewalDealBo getParam(Context context){
		
		JsdRenewalDealBo bo = new JsdRenewalDealBo();
		bo.borrowNo = ObjectUtils.toString(context.getDataMap().get("borrowNo"), "");	// 借款编号
		bo.delayNo = ObjectUtils.toString(context.getDataMap().get("delayNo"), "");		// 展期编号
		bo.bankNo = ObjectUtils.toString(context.getDataMap().get("bankNo"), "");		// 银行卡号
		bo.amount = NumberUtil.objToBigDecimalDefault(context.getDataMap().get("amount"),BigDecimal.ZERO);	// 金额
		bo.delayDay = NumberUtil.objToLongDefault(context.getDataMap().get("delayDay"), 0l);		// 展期天数
		bo.isTying = ObjectUtils.toString(context.getDataMap().get("isTying"), "");				// 是否搭售【Y：搭售，N：不搭售】
		bo.tyingType = ObjectUtils.toString(context.getDataMap().get("tyingType"), "");			// 搭售模式【BEHEAD：砍头，SELL：赊销】（搭售为Y时）
		bo.goodsName = ObjectUtils.toString(context.getDataMap().get("goodsName"), "");			// 商品名称（搭售为Y时）
		bo.goodsImage = ObjectUtils.toString(context.getDataMap().get("goodsImage"), "");		// 商品图片
		bo.goodsPrice = NumberUtil.objToBigDecimalDefault(context.getDataMap().get("goodsPrice"),BigDecimal.ZERO);	// 商品价格
		bo.userId = context.getUserId();
		
		if(StringUtil.equals("N", bo.isTying) && StringUtil.equals("BEHEAD", bo.tyingType)) {
			throw new FanbeiException(FanbeiExceptionCode.FUNCTIONAL_MAINTENANCE);
		}
		
		getRateInfo(bo);
		
		return bo;
	}
	
	/**
	 * 获取利率
	 */
	private void getRateInfo(JsdRenewalDealBo paramBo) {
		// 利率配置
		JsdResourceDo resourceDo = JsdResourceService.getByTypeAngSecType("JSD_CONFIG", "JSD_RATE_INFO");
		
		//借款手续费率
		BigDecimal poundageRate = null;
		//借款利率
		BigDecimal baseBankRate = null;
		//借款利率
		BigDecimal capitalRate = null;
		
		if(resourceDo!=null) {
			String rateStr = resourceDo.getValue();
			JSONArray array = JSONObject.parseArray(rateStr);
			
			for (int i = 0; i < array.size(); i++) {
				JSONObject info = array.getJSONObject(i);
				String borrowTag = info.getString("borrowTag");
				if (StringUtils.equals("BEHEAD", borrowTag)) {
					baseBankRate = info.getBigDecimal("interestRate");
					poundageRate = info.getBigDecimal("poundageRate");
					capitalRate = info.getBigDecimal("capitalRate");
				}
				if (StringUtils.equals("SELL", borrowTag)) {
					baseBankRate = info.getBigDecimal("interestRate");
					poundageRate = info.getBigDecimal("poundageRate");
					capitalRate = info.getBigDecimal("capitalRate");
				}
			}
		}else {
			throw new FanbeiException(FanbeiExceptionCode.GET_JSD_RATE_ERROR);
		}
		
				
		paramBo.poundageRate = poundageRate;
		paramBo.rate = baseBankRate;
		paramBo.capitalRate = capitalRate;
	}
}
