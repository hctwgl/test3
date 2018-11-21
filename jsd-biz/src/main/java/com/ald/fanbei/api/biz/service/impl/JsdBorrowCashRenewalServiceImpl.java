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
import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;
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
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.common.enums.JsdRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.dao.JsdUserBankcardDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;



/**
 * 极速贷续期ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashRenewalService")
public class JsdBorrowCashRenewalServiceImpl extends JsdUpsPayKuaijieServiceAbstract implements JsdBorrowCashRenewalService {
	
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
    private JsdBorrowCashDao jsdBorrowCashDao;
    @Resource
    private JsdUserBankcardDao jsdUserBankcardDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
	JsdNoticeRecordDao jsdNoticeRecordDao;
    @Resource
    private JsdNoticeRecordService jsdNoticeRecordService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdResourceService jsdResourceService;
    @Resource
    private XgxyUtil xgxyUtil;
    @Resource
    CollectionNoticeUtil collectionNoticeUtil;
    @Resource
    private RedisTemplate<String, ?> redisTemplate;

	
	@Override
	public Map<String, Object> doRenewal(JsdRenewalDealBo bo) {
		Map<String, Object> map = new HashMap<String, Object>();

		String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;
		JsdBorrowCashRenewalDo renewalDo = bo.renewalDo;
		JsdUserDo userDo = bo.userDo; 
		
		HashMap<String,Object> bank = jsdUserBankcardDao.getUserBankInfoByBankNo(bo.bankNo);
	    KuaijieJsdRenewalPayBo bizObject = new KuaijieJsdRenewalPayBo(bo.renewalDo, bo);
	    
	    if (BankPayChannel.KUAIJIE.getCode().equals(bo.bankChannel)) {// 快捷支付
			map = sendKuaiJieSms(bank, renewalDo.getTradeNo(), renewalDo.getActualAmount(), userDo.getRid(), userDo.getRealName(), 
					userDo.getIdNumber(), JSON.toJSONString(bizObject), "jsdBorrowCashRenewalService",Constants.DEFAULT_PAY_PURPOSE, name, 
				PayOrderSource.RENEW_JSD.getCode());
		} else {// 代扣
			map = doUpsPay(bo.bankChannel, bank, renewalDo.getTradeNo(), renewalDo.getActualAmount(), userDo.getRid(), userDo.getRealName(),
					userDo.getIdNumber(), "", JSON.toJSONString(bizObject),Constants.DEFAULT_PAY_PURPOSE, name, PayOrderSource.RENEW_JSD.getCode());
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
		    		
		    		// 更新新增订单还款状态
		    		JsdBorrowLegalOrderRepaymentDo repaymentByBorrowId = jsdBorrowLegalOrderRepaymentDao.getNewOrderRepaymentByBorrowId(renewalDo.getBorrowId());
		    		if(repaymentByBorrowId!=null){
		    			repaymentByBorrowId.setStatus(status);
		    			repaymentByBorrowId.setGmtModified(now);
		    			jsdBorrowLegalOrderRepaymentDao.updateById(repaymentByBorrowId);
		    		}
		    		
		    		if(JsdRenewalDetailStatus.NO.getCode().equals(status)){
		    			// 关闭新增订单借款
		    			JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(renewalDo.getBorrowId());
		    			if(JsdBorrowLegalOrderCashStatus.APPLYING.getCode().equals(orderCashDo.getStatus())){
		    				orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.getCode());
		    				orderCashDo.setGmtModified(now);
		    				orderCashDo.setGmtClose(now);
		    			}
		    			// 关闭新增订单
		    			JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getById(orderCashDo.getBorrowLegalOrderId());
		    			if(JsdBorrowLegalOrderStatus.UNPAID.getCode().equals(orderDo.getStatus())){
		    				orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.getCode());
		    				orderDo.setGmtModified(now);
		    				orderDo.setGmtClosed(now);
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
	
	/**
	 * 续期成功
	 */
	@Override
	public long dealJsdRenewalSucess(final String renewalNo, final String tradeNoOut) {
		logger.info("dealJsdRenewalSucess renewalNo="+renewalNo+", tradeNoOut="+tradeNoOut);
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
					logger.info("dealJsdRenewalSucess delayNo="+renewalDo.getTradeNoXgxy());
					if(JsdRenewalDetailStatus.YES.getCode().equals(renewalDo.getStatus())) {
						logger.warn("cur renewalNo " + renewalNo + "have success! repeat UPS callback!");
						return 0l;
					}
					// 本次订单
					JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(renewalDo.getBorrowId());
					JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getById(orderCashDo.getBorrowLegalOrderId());
					// 本次订单还款
					JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = jsdBorrowLegalOrderRepaymentDao.getNewOrderRepaymentByBorrowId(renewalDo.getBorrowId());
					// 借款记录
					JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(renewalDo.getBorrowId());
					// 上期订单借款
					JsdBorrowLegalOrderCashDo lastOrderCashDo = jsdBorrowLegalOrderCashDao.getPreviousOrderCashByBorrowId(borrowCashDo.getRid());



					Date now = new Date(System.currentTimeMillis());
					if(lastOrderCashDo!=null){
						// 更新上期订单借款记录为FINISHED
						lastOrderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.FINISHED.getCode());
						lastOrderCashDo.setSumRepaidPoundage(lastOrderCashDo.getSumRepaidPoundage().add(lastOrderCashDo.getPoundageAmount()));
						lastOrderCashDo.setPoundageAmount(BigDecimal.ZERO);
						lastOrderCashDo.setSumRepaidOverdue(lastOrderCashDo.getSumRepaidOverdue().add(lastOrderCashDo.getOverdueAmount()));
						lastOrderCashDo.setOverdueAmount(BigDecimal.ZERO);
						lastOrderCashDo.setSumRepaidInterest(lastOrderCashDo.getSumRepaidInterest().add(lastOrderCashDo.getInterestAmount()));
						lastOrderCashDo.setInterestAmount(BigDecimal.ZERO);
						lastOrderCashDo.setRepaidAmount(BigDecimalUtil.add(lastOrderCashDo.getAmount(),lastOrderCashDo.getSumRepaidInterest(),lastOrderCashDo.getSumRepaidPoundage(),lastOrderCashDo.getSumRepaidOverdue()));
						lastOrderCashDo.setGmtFinish(now);
						lastOrderCashDo.setGmtLastRepayment(now);
						jsdBorrowLegalOrderCashDao.updateById(lastOrderCashDo);

						// 更新本次 订单还款记录为已结清（对应上期订单）
						//续期有bug 暂时优化
						if(orderRepaymentDo != null){
							orderRepaymentDo.setStatus(JsdBorrowLegalRepaymentStatus.YES.getCode());
							orderRepaymentDo.setTradeNoUps(tradeNoOut);
							orderRepaymentDo.setActualAmount(orderRepaymentDo.getRepayAmount());
							jsdBorrowLegalOrderRepaymentDao.updateById(orderRepaymentDo);
						}
					}

					// 更新本次 订单借款状态
					orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.AWAIT_REPAY.getCode());//待还款
					orderCashDo.setGmtModified(now);
					jsdBorrowLegalOrderCashDao.updateById(orderCashDo);

					// 更新本次 订单状态为待发货
					orderDo.setStatus(JsdBorrowLegalOrderStatus.AWAIT_DELIVER.getCode());//待发货
					orderDo.setGmtModified(now);
					jsdBorrowLegalOrderDao.updateById(orderDo);

					// 更新本次 续期成功
					renewalDo.setStatus(JsdRenewalDetailStatus.YES.getCode());
					renewalDo.setTradeNoUps(tradeNoOut);
					renewalDo.setGmtModified(now);
					jsdBorrowCashRenewalDao.updateById(renewalDo);

					// 更新借款记录--->
					BigDecimal waitPayAmount = renewalDo.getRenewalAmount();
					BigDecimal rateAmount = BigDecimalUtil.multiply(waitPayAmount, renewalDo.getBaseBankRate(), new BigDecimal(renewalDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
		    		BigDecimal poundage = BigDecimalUtil.multiply(waitPayAmount, renewalDo.getPoundageRate(), new BigDecimal(renewalDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));

					Date gmtPlanRepayment = borrowCashDo.getGmtPlanRepayment();
					JsdBorrowCashDo jsdBorrowCashDo = new JsdBorrowCashDo();
					jsdBorrowCashDo.setRid(borrowCashDo.getRid());
					// 	如果预计还款时间在今天之后，则在原预计还款时间的基础上加上续期天数，否则在今天的基础上加上续期天数，作为新的预计还款时间
					Date repaymentDay = new Date();
					if (gmtPlanRepayment.after(now)) {
						repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, renewalDo.getRenewalDay().intValue()));
					} else {
						repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(now, renewalDo.getRenewalDay().intValue()));
					}
					jsdBorrowCashDo.setGmtPlanRepayment(repaymentDay);
					jsdBorrowCashDo.setRepayAmount(BigDecimalUtil.add(borrowCashDo.getRepayAmount(), renewalDo.getPriorInterest(),
							renewalDo.getPriorOverdue(), renewalDo.getPriorPoundage(), renewalDo.getCapital()));// 累计已还款金额
					jsdBorrowCashDo.setSumRepaidOverdue(borrowCashDo.getSumRepaidOverdue().add(borrowCashDo.getOverdueAmount()));// 累计滞纳金
					jsdBorrowCashDo.setOverdueAmount(BigDecimal.ZERO);// 滞纳金置0
					jsdBorrowCashDo.setSumRepaidInterest(borrowCashDo.getSumRepaidInterest().add(borrowCashDo.getInterestAmount()));// 累计利息
					jsdBorrowCashDo.setInterestAmount(rateAmount);// 利息改成本次续期金额的利息
					jsdBorrowCashDo.setSumRepaidPoundage(borrowCashDo.getSumRepaidPoundage().add(borrowCashDo.getPoundageAmount()));// 累计续期手续费
					jsdBorrowCashDo.setPoundageAmount(poundage);
					jsdBorrowCashDo.setRenewalNum(borrowCashDo.getRenewalNum() + 1);// 累计续期次数
					jsdBorrowCashDao.updateById(jsdBorrowCashDo);
					// ---<

					return 1l;
				} catch (Exception e) {
					t.setRollbackOnly();
					logger.info("update renewal sucess error", e);
					return 0l;
				}
		    }
		});

		if(result == 1l){
			//续期成功，调用西瓜信用通知接口
			JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByTradeNo(renewalNo);
			List<JsdBorrowLegalOrderDo> list = jsdBorrowLegalOrderDao.getBorrowOrdersByBorrowId(renewalDo.getBorrowId());
			notifyXgxyRenewalResult("Y", tradeNoOut, "", renewalDo);
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
		try{
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
		}catch (Exception e){
			logger.info("renew notify push is error");
			e.printStackTrace();
		}

	}
	
	/**
	 * 续期校验
	 */
	@Override
	public void checkCanRenewal(JsdBorrowCashDo borrowCashDo) {
		// 还款记录
		JsdBorrowCashRepaymentDo cashRepaymentDo = jsdBorrowCashRepaymentService.getLastByBorrowId(borrowCashDo.getRid());
		if (null != cashRepaymentDo && StringUtils.equals(cashRepaymentDo.getStatus(), JsdBorrowCashRepaymentStatus.PROCESS.getCode())) {
			throw new BizException("There is a repayment is processing", BizExceptionCode.HAVE_A_REPAYMENT_PROCESSING);
		}
		// 续期记录
		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getLastJsdRenewalByBorrowId(borrowCashDo.getRid());
		if (null != renewalDo && StringUtils.equals(renewalDo.getStatus(), JsdRenewalDetailStatus.PROCESS.getCode())) {
			throw new BizException("There is a renewal is processing", BizExceptionCode.HAVE_A_RENEWAL_PROCESSING);
		}
		
		// 当前日期与预计还款时间之前的天数差小于配置的betweenDuedate，并且未还款金额大于配置的限制金额时，可续期
		JsdResourceDo resource = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RENEWAL_INFO.getCode());
		if(resource==null) throw new BizException(BizExceptionCode.GET_JSD_RATE_ERROR);
		BigDecimal betweenDuedate = new BigDecimal(resource.getValue2()); // 距还款日天数
		BigDecimal amountLimit = new BigDecimal(resource.getValue3()); // 最低续期金额
		BigDecimal capitalRate = new BigDecimal(resource.getValue1()); // 续期支付最小本金比例
		BigDecimal capital = borrowCashDo.getAmount().multiply(capitalRate);
		logger.info("checkCanRenewal betweenDuedate="+betweenDuedate+", amountLimit="+amountLimit+", capitalRate="+capitalRate);
		// 本次续期之后 待还本金
		BigDecimal waitRepayAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumRepaidOverdue(), borrowCashDo.getSumRepaidInterest(), 
													borrowCashDo.getSumRepaidPoundage()).subtract(borrowCashDo.getRepayAmount().add(capital));
//		long betweenGmtPlanRepayment = DateUtil.getNumberOfDatesBetween(new Date(), borrowCashDo.getGmtPlanRepayment());
		
		/*if (new BigDecimal(betweenGmtPlanRepayment).compareTo(betweenDuedate) > 0 && amountLimit.compareTo(waitRepayAmount) >= 0) {
			throw new FanbeiException(FanbeiExceptionCode.CAN_NOT_RENEWAL_ERROR);
		}*/
		
		if(amountLimit.compareTo(waitRepayAmount) >= 0){
			throw new BizException(BizExceptionCode.CAN_NOT_RENEWAL_ERROR);
		}
		
	}
	
	/**
	 * 搭售 - 赊销
	 */
	@Override
	public JSONArray getRenewalDetail(JsdBorrowCashDo borrowCashDo) {
		JSONArray delayArray = new JSONArray();
		Map<String, Object> delayInfo = new HashMap<String, Object>();

		//上一笔订单记录
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(borrowCashDo.getRid());
		if(orderCashDo == null)	throw new BizException(BizExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);

		JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getById(orderCashDo.getBorrowLegalOrderId());
		if(orderDo==null) throw new BizException(BizExceptionCode.RENEWAL_ORDER_NOT_EXIST_ERROR);
		logger.info("last orderCash record = {} " , orderCashDo);

		JsdResourceDo renewalResource = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RENEWAL_INFO.getCode());
		if(renewalResource==null) throw new BizException(BizExceptionCode.GET_JSD_RATE_ERROR);

		// 允许续期天数
		BigDecimal allowRenewalDay = new BigDecimal(renewalResource.getValue());

		// 续借需还本金比例
		BigDecimal renewalCapitalRate = new BigDecimal(renewalResource.getValue1());
		//续借需要支付本金 = 借款金额 * 续借需还本金比例
		BigDecimal capital = borrowCashDo.getAmount().multiply(renewalCapitalRate).setScale(2, RoundingMode.HALF_UP);

		// 上期订单待还本金
		BigDecimal waitOrderAmount = BigDecimalUtil.add(orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),
				orderCashDo.getSumRepaidPoundage()).subtract(orderCashDo.getRepaidAmount());

		// 上期总利息
		BigDecimal rateAmount = BigDecimalUtil.add(borrowCashDo.getInterestAmount(),orderCashDo.getInterestAmount());
		// 上期总手续费
		BigDecimal poundage = BigDecimalUtil.add(borrowCashDo.getPoundageAmount(),orderCashDo.getPoundageAmount());
		// 上期总逾期费
		BigDecimal overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(),orderCashDo.getOverdueAmount());

		// 续期应缴费用(上期总利息+上期总手续费+上期总逾期费+要还本金  +上期待还订单)
		BigDecimal renewalPayAmount = BigDecimalUtil.add(rateAmount, poundage, overdueAmount, capital, waitOrderAmount);

		String deferRemark = "上期总利息"+rateAmount+
							 "元,上期总服务费"+poundage+
							 "元,上期总逾期费"+overdueAmount+
							 "元,本金还款部分"+capital+
							 "元,上期商品价格"+waitOrderAmount+"元。";

		BigDecimal principalAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumRepaidOverdue(),
				borrowCashDo.getSumRepaidInterest(), borrowCashDo.getSumRepaidPoundage())
				.subtract(borrowCashDo.getRepayAmount().add(capital));

		delayInfo.put("principalAmount", principalAmount+"");	// 展期后剩余借款本金
		delayInfo.put("payCapital", capital+"");				//此次续期需支付的本金
		delayInfo.put("delayAmount", renewalPayAmount+"");	// 需支付总金额
		delayInfo.put("delayDay", allowRenewalDay+"");	// 续期天数
		delayInfo.put("delayRemark", deferRemark);	// 费用明细	展期金额的相关具体描述（多条说明用英文逗号,用间隔）
		this.getRenewalRate(delayInfo);
		delayInfo.put("totalDiffFee", this.getDiffFee(borrowCashDo, delayInfo).toPlainString());	// 展期后的利润差，西瓜会根据此金额匹配搭售商品 TODO

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


	public static class JsdRenewalDealBo {
		
		public JsdBorrowCashDo borrowCashDo;
		public JsdBorrowCashRenewalDo renewalDo;
		public JsdBorrowLegalOrderCashDo legalOrderCashDo;
		public JsdBorrowLegalOrderDo legalOrderDo;
		public JsdUserDo userDo; 
		
		public BigDecimal capitalRate;
		public BigDecimal cashRate;
		public BigDecimal cashPoundageRate;
		public BigDecimal cashOverdueRate;
		
		public BigDecimal orderRate;
		public BigDecimal orderPoundageRate;
		public BigDecimal orderOverdueRate;
		
		public String bankChannel; 
		public String bankName;
		public Integer appVersion;
		public String renewalNo; // 我方生成的续期编号
		
		/*-请求参数->*/
		public String borrowNo;	// 对应借款表 trade_no_xgxy
		public String delayNo; // 西瓜提供的续期编号
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
	}


	@Override
	public int saveRecord(JsdBorrowCashRenewalDo renewalDo) {
		return jsdBorrowCashRenewalDao.saveRecord(renewalDo);
	}
	
	@Override
	public JsdBorrowCashRenewalDo getLastJsdRenewalByBorrowId(Long borrowId) {
		return jsdBorrowCashRenewalDao.getLastJsdRenewalByBorrowId(borrowId);
	}

	@Override
	public JsdBorrowCashRenewalDo getByTradeNoXgxy(String tradeNoXgxy) {
		return jsdBorrowCashRenewalDao.getByTradeNoXgxy(tradeNoXgxy);
	}

	@Override
	public List<JsdBorrowCashRenewalDo> getJsdRenewalByBorrowId(Long borrowId) {
		return jsdBorrowCashRenewalDao.getJsdRenewalByBorrowId(borrowId);
	}

	@Override
	public List<JsdBorrowCashRenewalDo> getJsdRenewalByBorrowIdAndStatus(Long borrowId) {
		return jsdBorrowCashRenewalDao.getJsdRenewalByBorrowIdAndStatus(borrowId);
	}

	@Override
	public List<JsdBorrowCashRenewalDo> getMgrJsdRenewalByBorrowId(Long borrowId) {
		return jsdBorrowCashRenewalDao.getMgrJsdRenewalByBorrowId(borrowId);
	}

	@Override
	public List<FinaneceDataDo> getRenewalData() {
		return jsdBorrowCashRenewalDao.getRenewalData();
	}
}
