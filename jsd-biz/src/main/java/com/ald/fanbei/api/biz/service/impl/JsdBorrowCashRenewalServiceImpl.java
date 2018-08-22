package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.DsedUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;



/**
 * 极速贷续期ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashRenewalService")
public class JsdBorrowCashRenewalServiceImpl extends DsedUpsPayKuaijieServiceAbstract implements JsdBorrowCashRenewalService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowCashRenewalServiceImpl.class);
   
    @Resource
    private JsdBorrowCashRenewalDao jsdBorrowCashRenewalDao;

	
	@Override
	public Map<String, Object> doRenewal(JsdRenewalDealBo bo) {

		return null;
	}

	@Override
	public long dealJsdRenewalSucess(String outTradeNo, String tradeNo) {

		return 0;
	}

	@Override
	public long dealJsdRenewalFail(String outTradeNo, String tradeNo, String errorMsg) {

		return 0;
	}

	@Override
	public JsdBorrowCashRenewalDo getLastJsdRenewalByBorrowId(Long borrowId) {

		return null;
	}

	
	@Override
	protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo) {
		
	}

	@Override
	protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
		
	}

	@Override
	protected void kuaijieConfirmPre(String payTradeNo, String bankChannel,	String payBizObject) {
		
	}

	@Override
	protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		
		return null;
	}

	@Override
	protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
		
	}

	
	public static class JsdRenewalDealBo {
		JsdBorrowCashDo borrowCashDo;
		JsdBorrowCashRenewalDo renewalDo;
		JsdBorrowLegalOrderCashDo legalOrderCashDo;
		JsdBorrowLegalOrderDo legalOrderDo;
		JsdUserDo userDo; 
		Long cardId; 
		
		BigDecimal repaymentAmount;
		BigDecimal actualAmount; 
		BigDecimal capital; 
		
		Long goodsId; // 搭售商品id
		String deliveryUser; // 收件人
		String deliveryPhone; // 收件人号码
		String address; // 地址
		String bankChannel; 
		Integer appVersion;
	}
}