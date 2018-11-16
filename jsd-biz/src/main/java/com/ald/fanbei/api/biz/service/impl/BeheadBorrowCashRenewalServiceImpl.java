package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.third.util.CollectionNoticeUtil;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.dal.dao.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.KuaijieJsdRenewalPayBo;
import com.ald.fanbei.api.biz.bo.ups.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.BeheadBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdCollectionBorrowService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRenewalServiceImpl.JsdRenewalDealBo;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionBorrowDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;



/**
 * 极速贷续期V2ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-12 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("beheadBorrowCashRenewalService")
public class BeheadBorrowCashRenewalServiceImpl extends JsdUpsPayKuaijieServiceAbstract implements BeheadBorrowCashRenewalService {
	
    private static final Logger logger = LoggerFactory.getLogger(BeheadBorrowCashRenewalServiceImpl.class);
   
    @Resource
    private JsdBorrowCashRenewalDao jsdBorrowCashRenewalDao;
    @Resource
    private JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    private JsdBorrowCashDao jsdBorrowCashDao;
    @Resource
    private JsdUserBankcardDao jsdUserBankcardDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private JsdNoticeRecordDao jsdNoticeRecordDao;
    @Resource
    private JsdNoticeRecordService jsdNoticeRecordService;
    @Resource
    private JsdResourceService jsdResourceService;
    @Resource
    private JsdCollectionBorrowService jsdCollectionBorrowService;
    @Resource
    private XgxyUtil xgxyUtil;
    @Resource
    private RedisTemplate<String, ?> redisTemplate;
	@Resource
	CollectionNoticeUtil collectionNoticeUtil;

    
    @Override
	public Map<String, Object> dealRenewalV2(final JsdRenewalDealBo paramBo) {
    	
    	long result = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					JsdBorrowCashRenewalDo renewalDo = buildRenewalDo(paramBo);
					JsdBorrowLegalOrderDo orderDo = buildOrderDo(paramBo);
					
					jsdBorrowCashRenewalDao.saveRecord(renewalDo);
					jsdBorrowLegalOrderDao.saveRecord(orderDo);
					
					paramBo.renewalDo = renewalDo;
					return 1l;
				}catch(Exception e) {
					status.setRollbackOnly();
					logger.info("JsdConfirmRenewal v2 sava record error", e);
					throw e;
				}
			}
		});
    	
    	Map<String, Object> resultMap = Maps.newHashMap();
    	if(result == 1l){
    		resultMap = doRenewal(paramBo);
    	}else {
			throw new BizException("JsdConfirmRenewal v2 error", BizExceptionCode.RENEWAL_FAIL_ERROR);
		}
    	
		return resultMap;
	}
	
	
	private Map<String, Object> doRenewal(JsdRenewalDealBo bo) {
		Map<String, Object> map = new HashMap<String, Object>();

		String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;
		JsdBorrowCashRenewalDo renewalDo = bo.renewalDo;
		JsdUserDo userDo = bo.userDo; 
		
		HashMap<String,Object> bank = jsdUserBankcardDao.getUserBankInfoByBankNo(bo.bankNo);
		KuaijieJsdRenewalPayBo bizObject = new KuaijieJsdRenewalPayBo(bo.renewalDo, bo);
	    
	    if (BankPayChannel.KUAIJIE.getCode().equals(bo.bankChannel)) {// 快捷支付
			map = sendKuaiJieSms(bank, renewalDo.getTradeNo(), renewalDo.getActualAmount(), userDo.getRid(), userDo.getRealName(), 
					userDo.getIdNumber(), JSON.toJSONString(bizObject), "beheadBorrowCashRenewalService",Constants.DEFAULT_PAY_PURPOSE, name, 
				PayOrderSource.RENEW_JSD_V2.getCode());
		} else {// 代扣
			map = doUpsPay(bo.bankChannel, bank, renewalDo.getTradeNo(), renewalDo.getActualAmount(), userDo.getRid(), userDo.getRealName(),
					userDo.getIdNumber(), "", JSON.toJSONString(bizObject),Constants.DEFAULT_PAY_PURPOSE, name, PayOrderSource.RENEW_JSD_V2.getCode());
	    }
		
		return map;
	}
	
	@Override
	protected void daikouConfirmPre(String renewalNo, String bankChannel, String payBizObject) {
		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		if(renewalPayBo!=null){
			dealChangStatus(renewalNo, "", JsdRenewalDetailStatus.PROCESS.getCode(), renewalPayBo.getRenewal().getRid(), "", "");
		}
	}
	
	@Override
	protected void kuaijieConfirmPre(String renewalNo, String bankChannel, String payBizObject) {
		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		if(renewalPayBo!=null){
			dealChangStatus(renewalNo, "", JsdRenewalDetailStatus.PROCESS.getCode(), renewalPayBo.getRenewal().getRid(), "", "");
		}
	}

	@Override
	protected void quickPaySendSmmSuccess(String renewalNo, String payBizObject, UpsCollectRespBo respBo) {
		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		if(renewalPayBo!=null){
			dealChangStatus(renewalNo, "", JsdRenewalDetailStatus.SMS.getCode(), renewalPayBo.getRenewal().getRid(), "", "");
		} 
	}

	@Override
	protected Map<String, Object> upsPaySuccess(String renewalNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
//		KuaijieJsdRenewalPayBo renewalPayBo = JSON.parseObject(payBizObject,KuaijieJsdRenewalPayBo.class);
		
		Map<String, Object> resulMap = new HashMap<String, Object>();
//        resulMap.put("outTradeNo", respBo.getOrderNo());
//        resulMap.put("tradeNo", respBo.getTradeNo());
//        resulMap.put("cardNo", Base64.encodeString(cardNo));
//        resulMap.put("refId", renewalPayBo.getRenewal().getRid());
//        resulMap.put("type", "");
		if(!BankPayChannel.KUAIJIE.getCode().equals(bankChannel)){
			resulMap.put("repaySMS", "N");
		}
		return resulMap;
	}

	@Override
	protected void roolbackBizData(String renewalNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
		logger.info("payBizObject="+payBizObject+",renewalNo="+renewalNo);
		
		if (StringUtils.isNotBlank(payBizObject)) {
			if (StringUtil.isNotBlank(respBo.getRespCode())) { // 处理业务数据
				dealJsdRenewalFail(renewalNo, "", true, respBo.getRespCode(), respBo.getRespDesc());
				throw new BizException(errorMsg);
			} else {
				dealJsdRenewalFail(renewalNo, "", false, "", "UPS响应码为空");
			}
		} else {
			// 未获取到缓存数据，支付订单过期
			throw new BizException(BizExceptionCode.UPS_CACHE_EXPIRE);
		}
	}

	/**
	 * 修改状态
	 */
	private long dealChangStatus(final String renewalNo, final String tradeNo, final String status, final Long renewalId, final String errorCode, final String errorMsg) {
		
		return transactionTemplate.execute(new TransactionCallback<Long>() {
		    @Override
		    public Long doInTransaction(TransactionStatus t) {
		    	try {
		    		Date now = new Date();
		    		
		    		// 更新当前续期状态
		    		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getById(renewalId);
		    		renewalDo.setStatus(status);
		    		renewalDo.setGmtModified(now);
	    			renewalDo.setFailCode(errorCode);
	    			renewalDo.setFailMsg(errorMsg);
		    		jsdBorrowCashRenewalDao.updateById(renewalDo);
		    		
		    		if(JsdRenewalDetailStatus.NO.getCode().equals(status)){
		    			// 关闭新增订单
		    			JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(renewalDo.getBorrowId());
		    			if(JsdBorrowLegalOrderStatus.UNPAID.getCode().equals(orderDo.getStatus())){
		    				orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.getCode());
		    				orderDo.setGmtModified(now);
		    				orderDo.setGmtClosed(now);
		    			}
		    			jsdBorrowLegalOrderDao.updateById(orderDo);
		    		}
		    		
		    		return 1l;
				} catch (Exception e) {
					t.setRollbackOnly();
				    logger.info("update renewal V2 status error", e);
				    return 0l;
				}
		    }
		});
	}
	
	/**
	 * 续期成功
	 */
	@Override
	public long dealJsdRenewalSucess(final String renewalNo, final String tradeNoOut) {
		logger.info("dealJsdRenewalSucess V2 renewalNo="+renewalNo+", tradeNoOut="+tradeNoOut);
		final String key = renewalNo + "_success_repayCash_renewal";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
		if (count != 1) {
		    return 0l;
		}
		
		long result = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus t) {
				try {
					// 本次续借
					JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByTradeNo(renewalNo);
					// 借款记录
					JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(renewalDo.getBorrowId());
					logger.info("dealJsdRenewalSucess V2 delayNo="+renewalDo.getTradeNoXgxy());
					if(JsdRenewalDetailStatus.YES.getCode().equals(renewalDo.getStatus())) return 0l;
					// 本次订单
					JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getLastOrderByBorrowId(renewalDo.getBorrowId());
					
					// 更新本次 订单状态为待发货
					orderDo.setStatus(JsdBorrowLegalOrderStatus.AWAIT_DELIVER.getCode());//待发货
					orderDo.setGmtModified(new Date());
					jsdBorrowLegalOrderDao.updateById(orderDo);
					
					// 更新本次 续期成功
					renewalDo.setStatus(JsdRenewalDetailStatus.YES.getCode());
					renewalDo.setTradeNoUps(tradeNoOut);
					renewalDo.setGmtModified(new Date());
					jsdBorrowCashRenewalDao.updateById(renewalDo);
					
					// 更新借款记录--->
					BigDecimal waitPayAmount = renewalDo.getRenewalAmount();
					BigDecimal rateAmount = BigDecimalUtil.multiply(waitPayAmount, renewalDo.getBaseBankRate(), new BigDecimal(renewalDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		    		BigDecimal poundage = BigDecimalUtil.multiply(waitPayAmount, renewalDo.getPoundageRate(), new BigDecimal(renewalDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
					
					Date gmtPlanRepayment = borrowCashDo.getGmtPlanRepayment();
					Date now = new Date(System.currentTimeMillis());

					// 	如果预计还款时间在今天之后，则在原预计还款时间的基础上加上续期天数，否则在今天的基础上加上续期天数，作为新的预计还款时间
					if (gmtPlanRepayment.after(now)) {
						Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, renewalDo.getRenewalDay().intValue()));
						borrowCashDo.setGmtPlanRepayment(repaymentDay);
					} else {
						Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(now, renewalDo.getRenewalDay().intValue()));
						borrowCashDo.setGmtPlanRepayment(repaymentDay);
					}

					borrowCashDo.setRepayAmount(BigDecimalUtil.add(borrowCashDo.getRepayAmount(), renewalDo.getPriorInterest(), renewalDo.getPriorOverdue(), renewalDo.getPriorPoundage(), renewalDo.getCapital()));// 累计已还款金额
					borrowCashDo.setSumRepaidOverdue(borrowCashDo.getSumRepaidOverdue().add(borrowCashDo.getOverdueAmount()));// 累计滞纳金
					borrowCashDo.setOverdueAmount(BigDecimal.ZERO);// 滞纳金置0
					borrowCashDo.setSumRepaidInterest(borrowCashDo.getSumRepaidInterest().add(borrowCashDo.getInterestAmount()));// 累计利息
					borrowCashDo.setInterestAmount(rateAmount);// 利息改成本次续期金额的利息
					borrowCashDo.setSumRepaidPoundage(borrowCashDo.getSumRepaidPoundage().add(borrowCashDo.getPoundageAmount()));// 累计续期手续费
					borrowCashDo.setPoundageAmount(poundage);
					borrowCashDo.setRenewalNum(borrowCashDo.getRenewalNum() + 1);// 累计续期次数
					logger.info("dealJsdRenewalSucess V2 borrowCashDo =" + borrowCashDo);
					jsdBorrowCashDao.updateById(borrowCashDo);
					// ---<
					
					// 更新借款催收状态-已续期
					jsdCollectionBorrowService.updateCollectionStatus(borrowCashDo.getRid(), CollectionBorrowStatus.RENEWALED.name());
					
					return 1l;
				} catch (Exception e) {
					t.setRollbackOnly();
					logger.info("update renewal V2 sucess error", e);
					return 0l;
				}
		    }
		});
		
		if(result == 1l){
			//续期成功，调用西瓜信用通知接口
			JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByTradeNo(renewalNo);
			notifyXgxyRenewalResult("Y", tradeNoOut, "", renewalDo);
			List<JsdBorrowLegalOrderDo> list = jsdBorrowLegalOrderDao.getBorrowOrdersByBorrowId(renewalDo.getBorrowId());
			logger.info("renewalDo = " + renewalDo + ",list = " +list);
			if(StringUtils.equals(renewalDo.getOverdueStatus(), YesNoStatus.YES.getCode())){
				Map<String, String> repayData = new HashMap<String, String>();
				repayData.put("info",String.valueOf(list.get(1).getRid()));
				JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
				noticeRecordDo.setType(JsdNoticeType.COLLECT_RENEW.code);
				noticeRecordDo.setUserId(renewalDo.getUserId());
				noticeRecordDo.setRefId(String.valueOf(renewalDo.getRid()));
				noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
				noticeRecordDo.setParams(JSON.toJSONString(repayData));
				jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
				if(collectionNoticeUtil.collectRenewal(repayData)){
					noticeRecordDo.setRid(noticeRecordDo.getRid());
					noticeRecordDo.setGmtModified(new Date());
					jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 续期失败
	 */
	@Override
	public long dealJsdRenewalFail(String renewalNo, String tradeNo, boolean isNeedMsgNotice, String errorCode, String errorMsg) {
		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByTradeNo(renewalNo);
		logger.info("dealJsdRenewalFail renewalNo="+renewalNo+", tradeNo="+tradeNo+", delayNo="+renewalDo.getTradeNoXgxy()+
				", isNeedMsgNotice="+isNeedMsgNotice+", errorCode="+errorCode+", errorMsg="+errorMsg);
		if(JsdRenewalDetailStatus.NO.getCode().equals(renewalDo.getStatus())){
			return 0l;
		}
		
		long result = dealChangStatus(renewalNo, tradeNo, JsdRenewalDetailStatus.NO.getCode(), renewalDo.getRid(), errorCode, errorMsg);
		
		if(result == 1l){
			//续期失败，调用西瓜信用通知接口
			notifyXgxyRenewalResult("N", tradeNo, errorMsg, renewalDo);
		}
		
		return 1l;
	}

	/**
	 * 通知西瓜 续期结果
	 */
	private void notifyXgxyRenewalResult(String status, String tradeNo, String errorMsg, JsdBorrowCashRenewalDo renewalDo) {
		//还款失败，调用西瓜信用通知接口
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(renewalDo.getBorrowId());
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("borrowNo", borrowCashDo.getTradeNoXgxy());
		data.put("delayNo", renewalDo.getTradeNoXgxy());
		data.put("status", status);
		data.put("reason", errorMsg);
		data.put("tradeNo", tradeNo);
		data.put("timestamp", System.currentTimeMillis()+"");
		
		JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
		noticeRecordDo.setUserId(renewalDo.getUserId());
		noticeRecordDo.setRefId(String.valueOf(renewalDo.getRid()));
		noticeRecordDo.setType(JsdNoticeType.RENEW.code);
		noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
		noticeRecordDo.setParams(JSON.toJSONString(data));
		jsdNoticeRecordService.addNoticeRecord(noticeRecordDo);
		if (xgxyUtil.jsdRenewalNoticeRequest(data)) {
			noticeRecordDo.setRid(noticeRecordDo.getRid());
			noticeRecordDo.setGmtModified(new Date());
			jsdNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
		}
	}
	

    /**
	 * 续期记录
	 */
	private JsdBorrowCashRenewalDo buildRenewalDo(JsdRenewalDealBo paramBo) {
		JsdBorrowCashDo borrowCashDo = paramBo.borrowCashDo;

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

		// 续期实付=上期利息 +上期逾期费+上期手续费+本金还款部分 +本期商品价格
		BigDecimal actualAmount = BigDecimalUtil.add(poundage, rateAmount ,overdueAmount, capital, paramBo.goodsPrice);

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
		renewalDo.setGmtPlanRepayment(borrowCashDo.getGmtPlanRepayment());
		renewalDo.setGmtCreate(new Date());
		renewalDo.setGmtModified(new Date());

		Date curr = DateUtil.getStartOfDate(new Date(System.currentTimeMillis()));
		Integer overdueday = NumberUtil.objToInteger(DateUtil.getNumberOfDayBetween(curr, borrowCashDo.getGmtPlanRepayment()));
		if(overdueday<0) overdueday=0;
		renewalDo.setOverdueDay(overdueday);
		renewalDo.setOverdueStatus(curr.after(borrowCashDo.getGmtPlanRepayment())?"Y":"N");
		
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
	 * 搭售 - 砍头
	 */
	@Override
	public JSONArray getBeheadRenewalDetail(JsdBorrowCashDo borrowCashDo) {
		JSONArray delayArray = new JSONArray();
		Map<String, Object> delayInfo = new HashMap<String, Object>();
		
		JsdResourceDo renewalResource = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RENEWAL_INFO.getCode());
		if(renewalResource==null) throw new BizException(BizExceptionCode.GET_JSD_RATE_ERROR);

		// 允许续期天数
		BigDecimal allowRenewalDay = new BigDecimal(renewalResource.getValue());
		
		// 续借需还本金比例
		BigDecimal renewalCapitalRate = new BigDecimal(renewalResource.getValue1());
		//续借需要支付本金 = 借款金额 * 续借需还本金比例
		BigDecimal capital = borrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);
		
		// 上期总利息
		BigDecimal rateAmount = BigDecimalUtil.add(borrowCashDo.getInterestAmount());
		// 上期总手续费
		BigDecimal poundage = BigDecimalUtil.add(borrowCashDo.getPoundageAmount());
		// 上期总逾期费
		BigDecimal overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount());
		
		BigDecimal principalAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumRepaidOverdue(), 
				borrowCashDo.getSumRepaidInterest(), borrowCashDo.getSumRepaidPoundage())
				.subtract(borrowCashDo.getRepayAmount().add(capital));
		
		delayInfo.put("principalAmount", principalAmount+"");	// 展期后剩余借款本金
		delayInfo.put("payCapital", capital+"");				//此次续期需支付的本金
		delayInfo.put("delayDay", allowRenewalDay+"");	// 续期天数
		
		// 利润差
		this.getRenewalRate(delayInfo);
		BigDecimal diffFee = this.getDiffFee(borrowCashDo, delayInfo);
		
		// 续期应缴费用(上期总利息+上期总手续费+上期总逾期费+要还本金  +本期商品(利润差))
		BigDecimal renewalPayAmount = BigDecimalUtil.add(rateAmount, poundage, overdueAmount, capital, diffFee);
		
		delayInfo.put("delayAmount", renewalPayAmount+"");	// 需支付总金额
		delayInfo.put("totalDiffFee", diffFee.toPlainString());	// 展期后的利润差，西瓜会根据此金额匹配搭售商品
		
		String deferRemark = "上期利息"+rateAmount+
							 "元,上期服务费"+poundage+
							 "元,上期逾期费"+overdueAmount+
							 "元,本金还款部分"+capital+
							 "元,本期商品价格"+diffFee.toPlainString()+"元。";
		
		delayInfo.put("delayRemark", deferRemark);	// 费用明细	展期金额的相关具体描述（多条说明用英文逗号,用间隔）

		delayArray.add(delayInfo);
		
		return delayArray;
	}
	
	private void getRenewalRate(Map<String, Object> delayInfo) {
		
		ResourceRateInfoBo rateInfo = jsdResourceService.getRateInfo(delayInfo.get("delayDay").toString());
		//借款手续费率
		delayInfo.put("interestRate", rateInfo.interestRate);
		//借款利率
		delayInfo.put("serviceRate", rateInfo.serviceRate);
	}
	
	private BigDecimal getDiffFee(JsdBorrowCashDo borrowCashDo, Map<String, Object> delayInfo) {
		
		BigDecimal interestRate = new BigDecimal(delayInfo.get("interestRate").toString());
		BigDecimal serviceRate = new BigDecimal(delayInfo.get("serviceRate").toString());
		BigDecimal renewalAmount = new BigDecimal(delayInfo.get("principalAmount").toString());
		BigDecimal renewalDay = new BigDecimal(delayInfo.get("delayDay").toString());

		// 借款利息手续费
		BigDecimal rateAmount = BigDecimalUtil.multiply(renewalAmount, interestRate, renewalDay.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		BigDecimal poundage = BigDecimalUtil.multiply(renewalAmount, serviceRate, renewalDay.divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		
		// 用户分层利率
		BigDecimal riskDailyRate = borrowCashDo.getRiskDailyRate();
		// 总利润
		BigDecimal totalDiffFee = BigDecimalUtil.multiply(renewalAmount, renewalDay, riskDailyRate);
		
//		BigDecimal finalDiffProfit = totalDiffFee.subtract(rateAmount).subtract(poundage).subtract(orderCashService);
		BigDecimal diffProfit = totalDiffFee.subtract(rateAmount).subtract(poundage);
		BigDecimal finalDiffProfit = diffProfit.setScale(-1, RoundingMode.UP);	// 向上取十
		logger.info("jsd renewal diffProfit="+diffProfit+", return finalDiffProfit="+finalDiffProfit);
		if (finalDiffProfit.compareTo(BigDecimal.ZERO) <= 0) {
        	finalDiffProfit = BigDecimal.ZERO;
        }
		
		return finalDiffProfit;
	}

}