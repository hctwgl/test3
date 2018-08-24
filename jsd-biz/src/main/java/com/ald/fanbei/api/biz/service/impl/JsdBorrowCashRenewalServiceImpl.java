package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.dao.JsdUserBankcardDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.biz.bo.KuaijieJsdRenewalPayBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.DsedUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.alibaba.fastjson.JSON;



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
    @Resource
    private JsdUserBankcardDao JsdUserBankcardDao;

	
	@Override
	public Map<String, Object> doRenewal(JsdRenewalDealBo bo) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (bo.cardId > 0) { // 银行卡支付
			String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;
			JsdBorrowCashDo borrowCashDo = bo.borrowCashDo;
			JsdBorrowCashRenewalDo renewalDo = bo.renewalDo;
			JsdBorrowLegalOrderCashDo legalOrderCashDo = bo.legalOrderCashDo;
			JsdBorrowLegalOrderDo legalOrderDo = bo.legalOrderDo;
			JsdUserDo userDo = bo.userDo; 
			
			HashMap<String,Object> bank = JsdUserBankcardDao.getUserBankInfoByBankNo("");
		    KuaijieJsdRenewalPayBo bizObject = new KuaijieJsdRenewalPayBo(bo.renewalDo, bo);
		    
		    if (BankPayChannel.KUAIJIE.getCode().equals(bo.bankChannel)) {// 快捷支付
				map = sendKuaiJieSms(bank, renewalDo.getTradeNo(), bo.actualAmount, userDo.getRid(), userDo.getRealName(), 
						userDo.getIdNumber(), JSON.toJSONString(bizObject), "jsdBorrowCashRenewalService",Constants.DEFAULT_PAY_PURPOSE, name, 
					PayOrderSource.RENEW_JSD.getCode());
			} else {// 代扣
				map = doUpsPay(bo.bankChannel, bank, renewalDo.getTradeNo(), renewalDo.getActualAmount(), userDo.getRid(), userDo.getRealName(),
						userDo.getIdNumber(), "", JSON.toJSONString(bizObject),Constants.DEFAULT_PAY_PURPOSE, name, 
					PayOrderSource.RENEW_JSD.getCode());
		    }
		} else {
		    throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
		
		return map;
	}
	
	@Override
	protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
		
	}
	
	@Override
	protected void kuaijieConfirmPre(String payTradeNo, String bankChannel,	String payBizObject) {
		
	}

	@Override
	protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo) {
		
	}

	@Override
	protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		
		return null;
	}

	@Override
	protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
		
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
		return jsdBorrowCashRenewalDao.getLastJsdRenewalByBorrowId(borrowId);
	}
	
	
	public static class JsdRenewalDealBo {
		
		public JsdBorrowCashDo borrowCashDo;
		public JsdBorrowCashRenewalDo renewalDo;
		public JsdBorrowLegalOrderCashDo legalOrderCashDo;
		public JsdBorrowLegalOrderDo legalOrderDo;
		public JsdUserDo userDo; 
		public JsdUserBankcardDo userBankDo; 
		public Long cardId; 
		
		public BigDecimal capitalRate;
		public BigDecimal cashRate;
		public BigDecimal cashPoundageRate;
		public BigDecimal orderRate;
		public BigDecimal orderPoundageRate;
		
		/*-请求参数->*/
		public String borrowNo;
		public String delayNo;
		public String bankNo;
		public BigDecimal amount;
		public Long delayDay;
		public String isTying;
		public String tyingType;
		public String goodsName;
		public String goodsImage;
		public BigDecimal goodsPrice;
		public Long userId;
		/*-------<*/
		
		public BigDecimal repaymentAmount;
		public BigDecimal actualAmount; 
		public BigDecimal capital; 
		
		public Long goodsId; // 搭售商品id
		public String deliveryUser; // 收件人
		public String deliveryPhone; // 收件人号码
		public String address; // 地址
		public String bankChannel; 
		public Integer appVersion;
	}
}