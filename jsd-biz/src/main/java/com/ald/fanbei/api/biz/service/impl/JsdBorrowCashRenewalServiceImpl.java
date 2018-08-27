package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.dbunit.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.JsdUserBankcardDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
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
    private JsdBorrowLegalOrderCashDao jsdBorrowLegalOrderCashDao;
    @Resource
    private JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    private JsdBorrowLegalOrderRepaymentDao jsdBorrowLegalOrderRepaymentDao;
    @Resource
    private JsdUserBankcardDao jsdUserBankcardDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private JsdUserDao jsdUserDao;

	
	@Override
	public Map<String, Object> doRenewal(JsdRenewalDealBo bo) {
		Map<String, Object> map = new HashMap<String, Object>();

		if (bo.cardId > 0) { // 银行卡支付
			String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;
			JsdBorrowCashRenewalDo renewalDo = bo.renewalDo;
			JsdUserDo userDo = bo.userDo; 
			
			HashMap<String,Object> bank = jsdUserBankcardDao.getUserBankInfoByBankNo("");
		    KuaijieJsdRenewalPayBo bizObject = new KuaijieJsdRenewalPayBo(bo.renewalDo, bo);
		    
		    if (BankPayChannel.KUAIJIE.getCode().equals(bo.bankChannel)) {// 快捷支付
				map = sendKuaiJieSms(bank, renewalDo.getRenewalNo(), bo.actualAmount, userDo.getRid(), userDo.getRealName(), 
						userDo.getIdNumber(), JSON.toJSONString(bizObject), "jsdBorrowCashRenewalService",Constants.DEFAULT_PAY_PURPOSE, name, 
					PayOrderSource.RENEW_JSD.getCode());
			} else {// 代扣
				map = doUpsPay(bo.bankChannel, bank, renewalDo.getTradeNo(), renewalDo.getActualAmount(), userDo.getRid(), userDo.getRealName(),
						userDo.getIdNumber(), "", JSON.toJSONString(bizObject),Constants.DEFAULT_PAY_PURPOSE, name, PayOrderSource.RENEW_JSD.getCode());
		    }
		} else {
		    throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
		
		return map;
	}
	
	@Override
	protected void daikouConfirmPre(String renewalNo, String bankChannel, String payBizObject) {
		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		if(renewalPayBo!=null){
			dealChangStatus(renewalNo, "", JsdRenewalDetailStatus.PROCESS.name(), renewalPayBo.getRenewal().getRid());
		}
	}
	
	@Override
	protected void kuaijieConfirmPre(String renewalNo, String bankChannel, String payBizObject) {
		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		if(renewalPayBo!=null){
			dealChangStatus(renewalNo, "", JsdRenewalDetailStatus.PROCESS.name(), renewalPayBo.getRenewal().getRid());
		}
	}

	@Override
	protected void quickPaySendSmmSuccess(String renewalNo, String payBizObject, UpsCollectRespBo respBo) {
		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		if(renewalPayBo!=null){
			dealChangStatus(renewalNo, "", JsdRenewalDetailStatus.SMS.name(), renewalPayBo.getRenewal().getRid());
		} 
	}

	@Override
	protected Map<String, Object> upsPaySuccess(String renewalNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		
		Map<String, Object> resulMap = new HashMap<String, Object>();
        resulMap.put("outTradeNo", respBo.getOrderNo());
        resulMap.put("tradeNo", respBo.getTradeNo());
        resulMap.put("cardNo", Base64.encodeString(cardNo));
        resulMap.put("refId", renewalPayBo.getRenewal().getRid());
        resulMap.put("type", "");
		return resulMap;
	}

	@Override
	protected void roolbackBizData(String renewalNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
		dealJsdRenewalFail(renewalNo, "", errorMsg);
	}

	private long dealChangStatus(final String renewalNo, final String tradeNo, final String status, final Long renewalId) {
		
		return transactionTemplate.execute(new TransactionCallback<Long>() {
		    @Override
		    public Long doInTransaction(TransactionStatus t) {
		    	try {
		    		Date now = new Date();
		    		
		    		// 更新当前续期状态
		    		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getById(renewalId);
		    		renewalDo.setStatus(status);
		    		renewalDo.setGmtModified(now);
		    		jsdBorrowCashRenewalDao.updateById(renewalDo);
		    		
		    		// 更新新增订单还款状态
		    		JsdBorrowLegalOrderRepaymentDo repaymentByBorrowId = jsdBorrowLegalOrderRepaymentDao.getNewOrderRepaymentByBorrowId(renewalDo.getBorrowId());
		    		if(repaymentByBorrowId!=null){
		    			repaymentByBorrowId.setStatus(status);
		    			repaymentByBorrowId.setGmtModified(now);
		    			jsdBorrowLegalOrderRepaymentDao.updateById(repaymentByBorrowId);
		    		}
		    		
		    		if(JsdRenewalDetailStatus.NO.name().equals(status)){
		    			// 关闭新增订单借款
		    			JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(renewalDo.getBorrowId());
		    			if(JsdBorrowLegalOrderCashStatus.APPLYING.name().equals(orderCashDo.getStatus())){
		    				orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.name());
		    				orderCashDo.setGmtModifed(now);
		    			}
		    			// 关闭新增订单
		    			JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getById(orderCashDo.getBorrowLegalOrderId());
		    			if("UNPAID".equals(orderDo.getStatus())){
		    				orderDo.setStatus("CLOSED");
		    				orderDo.setGmtModified(now);
		    			}
		    			jsdBorrowLegalOrderCashDao.updateById(orderCashDo);
		    			jsdBorrowLegalOrderDao.updateById(orderDo);
		    		}
		    		
		    		return 1l;
				} catch (Exception e) {
					t.setRollbackOnly();
				    logger.info("update renewal status error", e);
				    return 0l;
				}
		    }
		});
	}
	
	@Override
	public long dealJsdRenewalSucess(String renewalNo, String tradeNo) {
		
		return 1l;
	}
	
	@Override
	public long dealJsdRenewalFail(String renewalNo, String tradeNo, String errorMsg) {
		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByRenewalNo(renewalNo);
		if(JsdRenewalDetailStatus.YES.name().equals(renewalDo.getStatus())){
			return 0l;
		}
		
		dealChangStatus(renewalNo, tradeNo, JsdRenewalDetailStatus.NO.name(), renewalDo.getRid());
		
		return 1l;
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


	@Override
	public int saveRecord(JsdBorrowCashRenewalDo renewalDo) {
		return jsdBorrowCashRenewalDao.saveRecord(renewalDo);
	}
	
	@Override
	public JsdBorrowCashRenewalDo getLastJsdRenewalByBorrowId(Long borrowId) {
		return jsdBorrowCashRenewalDao.getLastJsdRenewalByBorrowId(borrowId);
	}
	
}