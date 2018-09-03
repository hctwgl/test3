package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderRepaymentDao;
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
import com.ald.fanbei.api.biz.bo.KuaijieJsdRenewalPayBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.DsedUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
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
    private JsdBorrowCashDao jsdBorrowCashDao;
    @Resource
    private JsdUserBankcardDao jsdUserBankcardDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private JsdUserDao jsdUserDao;
    @Resource
    private JsdNoticeRecordService jsdNoticeRecordService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdResourceService jsdResourceService;
    @Resource
    private XgxyUtil xgxyUtil;
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
			map = sendKuaiJieSms(bank, renewalDo.getRenewalNo(), renewalDo.getActualAmount(), userDo.getRid(), userDo.getRealName(), 
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
				throw new FanbeiException(errorMsg);
			} else {
				dealJsdRenewalFail(renewalNo, "", false, "", "UPS响应码为空");
			}
		} else {
			// 未获取到缓存数据，支付订单过期
			throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
		}
	}

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
		    		if(!JsdRenewalDetailStatus.NO.getCode().equals(status)){
		    			renewalDo.setFailCode(errorCode);
		    			renewalDo.setFailMsg(errorMsg);
		    		}
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
		    				orderCashDo.setGmtModifed(now);
		    			}
		    			// 关闭新增订单
		    			JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getById(orderCashDo.getBorrowLegalOrderId());
		    			if(JsdBorrowLegalOrderStatus.UNPAID.getCode().equals(orderDo.getStatus())){
		    				orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.getCode());
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
	public long dealJsdRenewalSucess(final String renewalNo, final String tradeNo) {
		
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
					JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByRenewalNo(renewalNo);
					if(JsdRenewalDetailStatus.YES.getCode().equals(renewalDo.getStatus())) return 0l;
					// 本次订单
					JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(renewalDo.getBorrowId());
					JsdBorrowLegalOrderDo orderDo = jsdBorrowLegalOrderDao.getById(orderCashDo.getBorrowLegalOrderId());
					// 本次订单还款
					JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = jsdBorrowLegalOrderRepaymentDao.getNewOrderRepaymentByBorrowId(renewalDo.getBorrowId());
					
					// 借款记录
					JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(renewalDo.getBorrowId());
					// 上期订单借款
					JsdBorrowLegalOrderCashDo lastOrderCashDo = jsdBorrowLegalOrderCashDao.getLastOrderCashByBorrowId(borrowCashDo.getRid());
					
					
					if(lastOrderCashDo.getStatus().equals(JsdBorrowLegalOrderCashStatus.AWAIT_REPAY.getCode()) 
							|| lastOrderCashDo.getStatus().equals(JsdBorrowLegalOrderCashStatus.PART_REPAID.getCode())){
						// 更新上期订单借款记录为FINISHED
						lastOrderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.FINISHED.getCode());
						lastOrderCashDo.setSumRepaidPoundage(lastOrderCashDo.getSumRepaidPoundage().add(lastOrderCashDo.getPoundageAmount()));
						lastOrderCashDo.setPoundageAmount(BigDecimal.ZERO);
						lastOrderCashDo.setSumRepaidOverdue(lastOrderCashDo.getSumRepaidOverdue().add(lastOrderCashDo.getOverdueAmount()));
						lastOrderCashDo.setOverdueAmount(BigDecimal.ZERO);
						lastOrderCashDo.setSumRepaidInterest(lastOrderCashDo.getSumRepaidInterest().add(lastOrderCashDo.getInterestAmount()));
						lastOrderCashDo.setInterestAmount(BigDecimal.ZERO);
						lastOrderCashDo.setRepaidAmount(BigDecimalUtil.add(lastOrderCashDo.getAmount(),lastOrderCashDo.getSumRepaidInterest(),lastOrderCashDo.getSumRepaidPoundage(),lastOrderCashDo.getSumRepaidOverdue()));
						jsdBorrowLegalOrderCashDao.updateById(lastOrderCashDo);
			
						// 更新本次 订单还款记录为已结清（对应上期订单）
						orderRepaymentDo.setStatus(JsdBorrowLegalRepaymentStatus.YES.getCode());
						orderRepaymentDo.setTradeNoUps(tradeNo);
						orderRepaymentDo.setActualAmount(orderRepaymentDo.getRepayAmount());
						jsdBorrowLegalOrderRepaymentDao.updateById(orderRepaymentDo);
					}
					
					// 更新本次 订单借款状态
					orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.AWAIT_REPAY.getCode());//待还款
					orderCashDo.setGmtModifed(new Date());
					jsdBorrowLegalOrderCashDao.updateById(orderCashDo);
					
					// 更新本次 订单状态为待发货
					orderDo.setStatus(JsdBorrowLegalOrderStatus.AWAIT_DELIVER.getCode());//待发货
					orderDo.setGmtModified(new Date());
					jsdBorrowLegalOrderDao.updateById(orderDo);
					
					// 更新本次 续期成功
					renewalDo.setStatus(JsdRenewalDetailStatus.YES.getCode());
					renewalDo.setTradeNo(tradeNo);
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
					borrowCashDo.setSumOverdue(borrowCashDo.getSumOverdue().add(borrowCashDo.getOverdueAmount()));// 累计滞纳金
					borrowCashDo.setOverdueAmount(BigDecimal.ZERO);// 滞纳金置0
					borrowCashDo.setSumRate(borrowCashDo.getSumRate().add(borrowCashDo.getRateAmount()));// 累计利息
					borrowCashDo.setRateAmount(rateAmount);// 利息改成本次续期金额的利息
					borrowCashDo.setSumRenewalPoundage(borrowCashDo.getSumRenewalPoundage().add(borrowCashDo.getPoundage()));// 累计续期手续费
					borrowCashDo.setPoundage(poundage);
					borrowCashDo.setRenewalNum(borrowCashDo.getRenewalNum() + 1);// 累计续期次数
					jsdBorrowCashDao.updateById(borrowCashDo);
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
			//还款成功，调用西瓜信用通知接口
			JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByRenewalNo(renewalNo);
			JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(renewalDo.getBorrowId());
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("borrowNo", borrowCashDo.getBorrowNo());
			data.put("delayNo", renewalNo);
			data.put("status", JsdRenewalDetailStatus.YES.name());
			data.put("reason", "");
			data.put("tradeNo", tradeNo);
			data.put("timestamp", System.currentTimeMillis()+"");
			
			JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
			noticeRecordDo.setUserId(renewalDo.getUserId());
			noticeRecordDo.setRefId(String.valueOf(renewalDo.getRid()));
			noticeRecordDo.setType(PayOrderSource.RENEW_JSD.getCode());
			noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
			noticeRecordDo.setParams(JSON.toJSONString(data));
			jsdNoticeRecordService.addNoticeRecord(noticeRecordDo);
			if (xgxyUtil.jsdRenewalNoticeRequest(data)) {
				noticeRecordDo.setRid(noticeRecordDo.getRid());
				noticeRecordDo.setGmtModified(new Date());
				jsdNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
			}
		}
		
		return result;
	}
	
	@Override
	public long dealJsdRenewalFail(String renewalNo, String tradeNo, boolean isNeedMsgNotice, String errorCode, String errorMsg) {
		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getByRenewalNo(renewalNo);
		if(JsdRenewalDetailStatus.YES.name().equals(renewalDo.getStatus())){
			return 0l;
		}
		
		dealChangStatus(renewalNo, tradeNo, JsdRenewalDetailStatus.NO.name(), renewalDo.getRid(), errorCode, errorMsg);
		
		//还款失败，调用西瓜信用通知接口
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(renewalDo.getBorrowId());
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("borrowNo", borrowCashDo.getBorrowNo());
		data.put("delayNo", renewalNo);
		data.put("status", JsdRenewalDetailStatus.NO.name());
		data.put("reason", errorMsg);
		data.put("tradeNo", tradeNo);
		data.put("timestamp", System.currentTimeMillis()+"");
		
		JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
		noticeRecordDo.setUserId(renewalDo.getUserId());
		noticeRecordDo.setRefId(String.valueOf(renewalDo.getRid()));
		noticeRecordDo.setType(PayOrderSource.RENEW_JSD.getCode());
		noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
		noticeRecordDo.setParams(JSON.toJSONString(data));
		jsdNoticeRecordService.addNoticeRecord(noticeRecordDo);
		if (xgxyUtil.jsdRenewalNoticeRequest(data)) {
			noticeRecordDo.setRid(noticeRecordDo.getRid());
			noticeRecordDo.setGmtModified(new Date());
			jsdNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
		}
		
		return 1l;
	}
	
	@Override
	public void checkCanRenewal(JsdBorrowCashDo borrowCashDo) {
		// 还款记录
		JsdBorrowCashRepaymentDo cashRepaymentDo = jsdBorrowCashRepaymentService.getLastByBorrowId(borrowCashDo.getRid());
		if (null != cashRepaymentDo && StringUtils.equals(cashRepaymentDo.getStatus(), JsdBorrowCashRepaymentStatus.PROCESS.getCode())) {
			throw new FanbeiException("There is a repayment is processing", FanbeiExceptionCode.HAVE_A_REPAYMENT_PROCESSING);
		}
		// 续期记录
		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalDao.getLastJsdRenewalByBorrowId(borrowCashDo.getRid());
		if (null != renewalDo && StringUtils.equals(renewalDo.getStatus(), JsdRenewalDetailStatus.PROCESS.getCode())) {
			throw new FanbeiException("There is a renewal is processing", FanbeiExceptionCode.HAVE_A_RENEWAL_PROCESSING);
		}
		
		// 当前日期与预计还款时间之前的天数差小于配置的betweenDuedate，并且未还款金额大于配置的限制金额时，可续期
		JsdResourceDo resource = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceType.JSD_RENEWAL_INFO.getCode());
		if(resource==null) throw new FanbeiException(FanbeiExceptionCode.GET_JSD_RATE_ERROR);
		BigDecimal betweenDuedate = new BigDecimal(resource.getValue2()); // 距还款日天数
		BigDecimal amountLimit = new BigDecimal(resource.getValue3()); // 最低续期金额
		logger.info("checkCanRenewal betweenDuedate="+betweenDuedate+"amountLimit="+amountLimit);
		BigDecimal waitRepayAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumOverdue(), borrowCashDo.getSumRate(), 
													borrowCashDo.getSumRenewalPoundage(), borrowCashDo.getOverdueAmount(), borrowCashDo.getRateAmount(), 
													borrowCashDo.getPoundage()).subtract(borrowCashDo.getRepayAmount());
		long betweenGmtPlanRepayment = DateUtil.getNumberOfDatesBetween(new Date(), borrowCashDo.getGmtPlanRepayment());
		
		if (new BigDecimal(betweenGmtPlanRepayment).compareTo(betweenDuedate) > 0 && amountLimit.compareTo(waitRepayAmount) >= 0) {
			throw new FanbeiException(FanbeiExceptionCode.CAN_NOT_RENEWAL_ERROR);
		}
		
	}
	
	public static class JsdRenewalDealBo {
		
		public JsdBorrowCashDo borrowCashDo;
		public JsdBorrowCashRenewalDo renewalDo;
		public JsdBorrowLegalOrderCashDo legalOrderCashDo;
		public JsdBorrowLegalOrderDo legalOrderDo;
		public JsdUserDo userDo; 
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
		public String bankName;
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

	@Override
	public JsdBorrowCashRenewalDo getRenewalByDelayNo(String delayNo) {
		return jsdBorrowCashRenewalDao.getByRenewalNo(delayNo);
	}
	
}