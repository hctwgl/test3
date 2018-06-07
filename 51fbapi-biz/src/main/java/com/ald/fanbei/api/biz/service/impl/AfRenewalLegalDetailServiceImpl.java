package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepaymentStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**  
 * @Description: 
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年12月11日
 */
@Service("afRenewalLegalDetailService")
public class AfRenewalLegalDetailServiceImpl extends BaseService implements AfRenewalLegalDetailService {
	@Resource
	UpsUtil upsUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	private JpushService pushService;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	private AfUserService afUserService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserBankcardDao afUserBankcardDao;
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfRenewalDetailDao afRenewalDetailDao;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	RiskUtil riskUtil;
	@Resource
	CollectionSystemUtil collectionSystemUtil;

	@Resource
	AfYibaoOrderDao afYibaoOrderDao;

	@Resource
	YiBaoUtility yiBaoUtility;

	@Resource
	RedisTemplate redisTemplate;

	@Resource
	ContractPdfThreadPool contractPdfThreadPool;
    @Resource
    SmsUtil smsUtil;
    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    @Resource
    AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
    @Resource
    AfRenewalLegalDetailService afRenewalLegalDetailService;
    @Resource
    AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;
    @Resource
    AfBorrowLegalOrderDao afBorrowLegalOrderDao;
    @Resource
    AfGoodsDao afGoodsDao;
    @Resource
    AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;
    @Resource
    private AfTradeCodeInfoService afTradeCodeInfoService;
    @Resource
    AfTaskUserService afTaskUserService;
    
	@Override
	public Map<String, Object> createLegalRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo, Integer appVersion, Long goodsId, String deliveryUser, String deliveryPhone, String address,String bankPayType) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRenewalBorrowCashNo(now);
		final String payTradeNo = repayNo;

		String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;
		
		//新增还款记录
		final AfRenewalDetailDo renewalDetail = buildRenewalDetailDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, capital, borrow, cardId, payTradeNo, userId, appVersion);
	
		//上一笔订单记录
		AfBorrowLegalOrderDo afBorrowLegalOrder = afBorrowLegalOrderDao.getLastOrderByBorrowId(afBorrowCashDo.getRid());
		if(afBorrowLegalOrder==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(afBorrowLegalOrder.getRid());
		if(afBorrowLegalOrderCash==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderRepaymentDo legalOrderRepayment = null;
		if(afBorrowLegalOrderCash.getStatus().equals("AWAIT_REPAY")||afBorrowLegalOrderCash.getStatus().equals("PART_REPAID")){
			// 新增订单还款记录
			legalOrderRepayment = buildBorrowLegalOrderRepaymentDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, capital, borrow, cardId, payTradeNo, userId, appVersion);
			afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(legalOrderRepayment);
		}
		
		//新增订单记录（订单记录，订单借款记录）
		final AfBorrowLegalOrderDo borrowLegalOrder = buildAfBorrowLegalOrderDo(afBorrowCashDo, userId, goodsId, deliveryUser, deliveryPhone, address);
		final AfBorrowLegalOrderCashDo borrowLegalOrderCash = buildAfBorrowLegalOrderCashDo(afBorrowCashDo,userId,payTradeNo,goodsId);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					afBorrowLegalOrderService.saveBorrowLegalOrder(borrowLegalOrder);
					borrowLegalOrderCash.setBorrowLegalOrderId(borrowLegalOrder.getRid());
					afBorrowLegalOrderCashDao.saveRecord(borrowLegalOrderCash);
					afRenewalDetailDao.addRenewalDetail(renewalDetail);
					
					return 1l;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("sava record error", e);
					throw e;
				}
			}
		});
		
		/*if (cardId == -1) {// 微信支付
			map = UpsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, actualAmount, PayOrderSource.RENEWAL_PAY.getCode());
		} else*/ 
		if (cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			dealChangStatus(payTradeNo, repayNo, AfBorrowLegalRepaymentStatus.PROCESS.getCode(), renewalDetail.getRid());
			UpsCollectRespBo respBo = (UpsCollectRespBo) upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(),
				bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, name, "02", 
				PayOrderSource.RENEW_CASH_LEGAL.getCode());
			if (!respBo.isSuccess()) {
			    String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
			    dealLegalRenewalFail(payTradeNo, repayNo,errorMsg);
			    throw new FanbeiException(errorMsg);
			}
			map.put("resp", respBo);
		}else{
			throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
		}
		map.put("refId", renewalDetail.getRid());
		map.put("refOrderId", borrowLegalOrder.getRid());
		map.put("type", UserAccountLogType.RENEWAL_PAY.getCode());

		return map;
	}
	
	
	
	long dealChangStatus(final String outTradeNo, final String tradeNo, final String status, final Long rid) {

		AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
		if(afYibaoOrderDo !=null){
			if(afYibaoOrderDo.getStatus().intValue() == 1){
				return 1L;
			}
			else{
				if(status.equals("N")) {
					afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 2);
				}
			}
		}
		
		transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus t) {
				try {
					
					//更新还款记录
					AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByRenewalId(rid);
					afRenewalDetailDo.setStatus(status);
					afRenewalDetailDo.setTradeNo(tradeNo);
					afRenewalDetailDo.setRid(rid);
					afRenewalDetailDo.setGmtModified(new Date());
					afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
					
					// 获取新增的订单还款记录
					AfBorrowLegalOrderRepaymentDo borrowLegalOrderRepayment = afBorrowLegalOrderRepaymentDao.getNewOrderRepayment(afRenewalDetailDo.getBorrowId());
					if(borrowLegalOrderRepayment!=null){
						//更新订单还款记录
						borrowLegalOrderRepayment.setStatus(status);
						borrowLegalOrderRepayment.setTradeNo(tradeNo);
						borrowLegalOrderRepayment.setGmtModified(new Date());
						afBorrowLegalOrderRepaymentDao.updateBorrowLegalOrderRepayment(borrowLegalOrderRepayment);
					}
					
					if(status.equals("N")){
						// 获取新增的订单借款记录
						AfBorrowLegalOrderCashDo borrowLegalOrderCashDo = afBorrowLegalOrderCashDao.getNewOrderCash(afRenewalDetailDo.getBorrowId());
						// 获取新增的订单
						AfBorrowLegalOrderDo borrowLegalOrderDo = afBorrowLegalOrderDao.getById(borrowLegalOrderCashDo.getBorrowLegalOrderId());
			
						//关闭新增订单借款记录
						if(borrowLegalOrderCashDo.getStatus().equals("APPLYING")){ // 只对新增订单借款操作
							borrowLegalOrderCashDo.setStatus("CLOSED");
							borrowLegalOrderCashDo.setGmtModifed(new Date());
							afBorrowLegalOrderCashDao.updateById(borrowLegalOrderCashDo);
						}
						//关闭新增订单记录
						if(borrowLegalOrderDo.getStatus().equals("UNPAID")){ // 只对新增订单操作
							borrowLegalOrderDo.setStatus("CLOSED");
							borrowLegalOrderDo.setGmtModified(new Date());
							afBorrowLegalOrderDao.updateById(borrowLegalOrderDo);
						}
					}
					
					return 1l;
				} catch (Exception e) {
					t.setRollbackOnly();
					logger.info("sava record error", e);
					return 0l;
				}
			}
		});
		return 1L;
	}

	@Override
	public long dealLegalRenewalFail(String outTradeNo, String tradeNo,String errorMsg) {
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
		
		if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
			return 0l;
		}
		long result = dealChangStatus(outTradeNo, tradeNo, AfBorrowLegalRepaymentStatus.NO.getCode(), afRenewalDetailDo.getRid());
		
		AfUserDo userDo = afUserService.getUserById(afRenewalDetailDo.getUserId());
		try {
			pushService.repayRenewalFail(userDo.getUserName());
		}catch (Exception e){
			logger.error("dealLegalRenewalFail push exception.",e);
		}
		//fmf_add 续借失败短信通知
		//模版数据map处理
		Map<String,String> replaceMapData = new HashMap<String, String>();
		replaceMapData.put("errorMsg", errorMsg);
		//用户信息及当日还款失败次数校验
		int errorTimes = afRenewalDetailDao.getCurrDayRepayErrorTimes(afRenewalDetailDo.getUserId());
		try {
			smsUtil.sendConfigMessageToMobile(userDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RENEWAL_DETAIL_FAIL.getCode());
			String title = "本次续借支付失败";
			String content = "续借支付失败：&errorMsg。";
			content = content.replace("&errorMsg",errorMsg);
			pushService.pushUtil(title,content,userDo.getMobile());
		} catch (Exception e) {
			logger.error("sendRenewalFailWarnMsg is Fail.",e);
		}

		return result;
	}
	@Resource
	CuiShouUtils cuiShouUtils;
	@Override
	public long dealLegalRenewalSucess(final String outTradeNo, final String tradeNo) {

		final String key = outTradeNo +"_success_repayCash_renewal";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
		if (count != 1) {
			return -1;
		}
		try{
			// 获取新增本金还款记录
			final AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
			// 获取新增的订单还款记录
			final AfBorrowLegalOrderRepaymentDo borrowLegalOrderRepayment = afBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByPayTradeNo(outTradeNo);
			// 获取新增的订单借款记录
			final AfBorrowLegalOrderCashDo borrowLegalOrderCashDo = afBorrowLegalOrderCashDao.getNewOrderCash(afRenewalDetailDo.getBorrowId());
			// 获取新增的订单
			final AfBorrowLegalOrderDo borrowLegalOrderDo = afBorrowLegalOrderDao.getById(borrowLegalOrderCashDo.getBorrowLegalOrderId());
			
			logger.info("afRenewalLegalDetailService : afRenewalDetailDo=" + afRenewalDetailDo+"; borrowLegalOrderRepayment="+borrowLegalOrderRepayment+"; borrowLegalOrderCashDo"+borrowLegalOrderCashDo+"; borrowLegalOrderDo="+borrowLegalOrderDo);
			if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
				redisTemplate.delete(key);
				return 0l;
			}
	
			long resultValue =  transactionTemplate.execute(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(TransactionStatus status) {
					try {
						AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
						if(afYibaoOrderDo !=null){
							if(afYibaoOrderDo.getStatus().intValue() == 1){
								return 0L;
							}else{
								afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),1);
							}
						}
	
						AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(afRenewalDetailDo.getBorrowId());
						
						// 获取要偿还的订单 
						AfBorrowLegalOrderDo lastBorrowLegalOrderDo = afBorrowLegalOrderDao.getLastOrderByBorrowId(afRenewalDetailDo.getBorrowId());
						
						// 更新上期订单借款记录为FINISHED 
						AfBorrowLegalOrderCashDo lastBorrowLegalOrderCash =  afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowLegalOrderId(lastBorrowLegalOrderDo.getRid());
						lastBorrowLegalOrderCash.setStatus("FINISHED");
						lastBorrowLegalOrderCash.setSumRepaidPoundage(lastBorrowLegalOrderCash.getSumRepaidPoundage().add(lastBorrowLegalOrderCash.getPoundageAmount()));
						lastBorrowLegalOrderCash.setPoundageAmount(BigDecimal.ZERO);
						lastBorrowLegalOrderCash.setSumRepaidOverdue(lastBorrowLegalOrderCash.getSumRepaidOverdue().add(lastBorrowLegalOrderCash.getOverdueAmount()));
						lastBorrowLegalOrderCash.setOverdueAmount(BigDecimal.ZERO);
						lastBorrowLegalOrderCash.setSumRepaidInterest(lastBorrowLegalOrderCash.getSumRepaidInterest().add(lastBorrowLegalOrderCash.getInterestAmount()));
						lastBorrowLegalOrderCash.setInterestAmount(BigDecimal.ZERO);
						lastBorrowLegalOrderCash.setRepaidAmount(BigDecimalUtil.add(lastBorrowLegalOrderCash.getAmount(),lastBorrowLegalOrderCash.getSumRepaidInterest(),lastBorrowLegalOrderCash.getSumRepaidPoundage(),lastBorrowLegalOrderCash.getSumRepaidOverdue()));
						afBorrowLegalOrderCashDao.updateById(lastBorrowLegalOrderCash);
						
						// 更新上期订单还款记录为已结清
						if(borrowLegalOrderRepayment != null) {
							borrowLegalOrderRepayment.setStatus(AfBorrowLegalRepaymentStatus.YES.getCode());
							borrowLegalOrderRepayment.setTradeNoUps(tradeNo);
							borrowLegalOrderRepayment.setActualAmount(borrowLegalOrderRepayment.getRepayAmount());
							afBorrowLegalOrderRepaymentDao.updateBorrowLegalOrderRepayment(borrowLegalOrderRepayment);
						}
						
						
						// 更新新增订单借款记录状态
						borrowLegalOrderCashDo.setStatus("AWAIT_REPAY");//待还款
						borrowLegalOrderCashDo.setGmtModifed(new Date());
						afBorrowLegalOrderCashDao.updateById(borrowLegalOrderCashDo);
						
						// 更新新增订单状态为待发货
						borrowLegalOrderDo.setStatus("AWAIT_DELIVER");//待发货
						borrowLegalOrderDo.setGmtModified(new Date());
						afBorrowLegalOrderDao.updateById(borrowLegalOrderDo);
						
						// 更新续期记录为续期成功
						afRenewalDetailDo.setStatus(AfBorrowLegalRepaymentStatus.YES.getCode());
						afRenewalDetailDo.setTradeNo(tradeNo);
						afRenewalDetailDo.setGmtModified(new Date());
						afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
						
						//续期本金
						BigDecimal waitPaidAmount =afRenewalDetailDo.getRenewalAmount();
						// 查询新利率配置
			    		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,Constants.BORROW_CASH_INFO_LEGAL_NEW);
			    		//借款利率
			    		BigDecimal newRate = null;
			    		//借款手续费率
			    		BigDecimal newServiceRate = null;
			    		
			    		if (rateInfoDo != null) {
			    			String borrowRate = rateInfoDo.getValue2();
			    			JSONArray array = JSONObject.parseArray(borrowRate);
			    			double rate = 0;
			    			double serviceRate = 0;
			    			for (int i = 0; i < array.size(); i++) {
			    				JSONObject info = array.getJSONObject(i);
			    				String borrowTag = info.getString("borrowTag");
			    				if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
			    						rate = info.getDouble("borrowFirstType");
			    				}
			    				if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
			    					serviceRate = info.getDouble("borrowFirstType");
			    				}
			    			}
			    			newRate = BigDecimal.valueOf(rate / 100);
			    			newServiceRate = BigDecimal.valueOf(serviceRate / 100);
			    		}else{
			    			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_RATE_ERROR);
			    		}
			    		BigDecimal rateAmount = BigDecimalUtil.multiply(waitPaidAmount, newRate, new BigDecimal(afRenewalDetailDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
			    		BigDecimal poundage = BigDecimalUtil.multiply(waitPaidAmount, newServiceRate, new BigDecimal(afRenewalDetailDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
						// ----<
			    		
						Date gmtPlanRepayment = afBorrowCashDo.getGmtPlanRepayment();
						Date now = new Date(System.currentTimeMillis());
	
						// 如果预计还款时间在今天之后，则在原预计还款时间的基础上加上续期天数，否则在今天的基础上加上续期天数，作为新的预计还款时间
						if (gmtPlanRepayment.after(now)) {
							Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, afRenewalDetailDo.getRenewalDay()));
							afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
						} else {
							Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(now, afRenewalDetailDo.getRenewalDay()));
							afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
						}
	
						afBorrowCashDo.setRepayAmount(BigDecimalUtil.add(afBorrowCashDo.getRepayAmount(), afRenewalDetailDo.getPriorInterest(), afRenewalDetailDo.getPriorOverdue(), afRenewalDetailDo.getPriorPoundage(), afRenewalDetailDo.getCapital()));// 累计已还款金额
						afBorrowCashDo.setSumOverdue(afBorrowCashDo.getSumOverdue().add(afBorrowCashDo.getOverdueAmount()));// 累计滞纳金
						afBorrowCashDo.setOverdueAmount(BigDecimal.ZERO);// 滞纳金置0
						afBorrowCashDo.setSumRate(afBorrowCashDo.getSumRate().add(afBorrowCashDo.getRateAmount()));// 累计利息
						afBorrowCashDo.setRateAmount(rateAmount);// 利息改成本次续期金额的利息
						afBorrowCashDo.setSumRenewalPoundage(afBorrowCashDo.getSumRenewalPoundage().add(afRenewalDetailDo.getPriorPoundage()));// 累计续期手续费
						afBorrowCashDo.setPoundage(poundage);
						afBorrowCashDo.setRenewalNum(afBorrowCashDo.getRenewalNum() + 1);// 累计续期次数
						afBorrowCashService.updateBorrowCash(afBorrowCashDo);
	
						// 授权账户可用金额变更
						AfUserAccountDo account = new AfUserAccountDo();
						account.setUserId(afRenewalDetailDo.getUserId());
						account.setJfbAmount(afRenewalDetailDo.getJfbAmount().multiply(new BigDecimal(-1)));
						account.setRebateAmount(afRenewalDetailDo.getRebateAmount().multiply(new BigDecimal(-1)));
						afUserAccountDao.updateUserAccount(account);
	
						afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.RENEWAL_PAY, afRenewalDetailDo.getRebateAmount(), afRenewalDetailDo.getUserId(), afRenewalDetailDo.getRid()));

						// add by luoxiao for 边逛边赚，增加零钱明细
						afTaskUserService.addTaskUser(afRenewalDetailDo.getUserId(),UserAccountLogType.RENEWAL_PAY.getName(), afRenewalDetailDo.getRebateAmount().multiply(new BigDecimal(-1)));
						// end by luoxiao

						//续借成功发送短信和消息通知
						AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RENEWAL_DETAIL_SUCCESS.getCode());
						if(null != resourceDo){
							AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
							if(null != afUserDo){
								String content = resourceDo.getValue();
								Calendar c = Calendar.getInstance();
								c.setTime(afBorrowCashDo.getGmtPlanRepayment());
								int month = c.get(Calendar.MONTH)+1;
								int day = c.get(Calendar.DATE);
								content = content.replace("M",month+"");
								content = content.replace("D",day+"");
								smsUtil.sendMessageToMobile(afUserDo.getUserName(),content);
								String title = "恭喜您，续借成功";
								String msgcontent = "恭喜，您已经续借成功，最新还款日为M月D日，请按时还款，保持良好信用。";
								msgcontent = msgcontent.replace("M",month+"");
								msgcontent = msgcontent.replace("D",day+"");
								pushService.pushUtil(title,msgcontent,afUserDo.getUserName());
							}
						}
						return 1l;
					} catch (Exception e) {
						status.setRollbackOnly();
						logger.info("dealRenewalSucess error", e);
						return 0l;
					}
				}
			});
	
			if(resultValue == 1L){
				//续期成功,推送等抽出
				logger.info("续期成功，推送消息和向催收平台同步进入borrowCashId:"+afRenewalDetailDo.getBorrowId()+",afRenewalDetailDoId:"+afRenewalDetailDo.getRid()+",afBorrowLegalOrderRepaymentId:"+borrowLegalOrderRepayment);
	
				AfBorrowCashDo currAfBorrowCashDo = afBorrowCashService.getBorrowCashByrid(afRenewalDetailDo.getBorrowId());
				AfUserDo userDo = afUserService.getUserById(currAfBorrowCashDo.getUserId());
				try {
//					pushService.repayRenewalSuccess(userDo.getUserName());
					logger.info("续期成功，推送消息成功outTradeNo:"+outTradeNo);
				}catch (Exception e){
					logger.error("续期成功，推送消息异常outTradeNo:"+outTradeNo,e);
				}
	
				//当续期成功时,同步逾期天数为0
				dealWithSynchronizeOverduedOrder(currAfBorrowCashDo);
	
				try {
					if (currAfBorrowCashDo.getOverdueStatus().equals("Y") || currAfBorrowCashDo.getOverdueDay() > 0) {
						CollectionSystemReqRespBo respInfo = collectionSystemUtil.renewalNotify(currAfBorrowCashDo.getBorrowNo(), afRenewalDetailDo.getPayTradeNo(), afRenewalDetailDo.getRenewalDay(),(afRenewalDetailDo.getNextPoundage().multiply(BigDecimalUtil.ONE_HUNDRED))+"");
						logger.info("collection renewalNotify req success, respinfo={}",respInfo);
					}else{
						logger.info("collection renewalNotify req unPush, renewalNo=" + afRenewalDetailDo.getPayTradeNo());
					}
				}catch(Exception e){
					logger.error("向催收平台同步续期信息",e);
				}
				cuiShouUtils.syncXuqi(currAfBorrowCashDo);
			}
			if (resultValue == 1L){
				//生成续期凭据
				contractPdfThreadPool.protocolRenewalPdf(afRenewalDetailDo.getUserId(),afRenewalDetailDo.getBorrowId(),afRenewalDetailDo.getRid(),afRenewalDetailDo.getRenewalDay(),afRenewalDetailDo.getRenewalAmount());
			}


			return resultValue;
		}finally {
			redisTemplate.delete(key);
		}
	}
	
	/**
	 * 同步逾期订单
	 * @param borrowCashInfo
	 */
	private void dealWithSynchronizeOverduedOrder(AfBorrowCashDo borrowCashInfo) {
		String identity = System.currentTimeMillis() + StringUtils.EMPTY;
		String orderNo = riskUtil.getOrderNo("over", identity.substring(identity.length() - 4, identity.length()));
		List<RiskOverdueBorrowBo> boList = new ArrayList<RiskOverdueBorrowBo>();
		boList.add(parseOverduedBorrowBo(borrowCashInfo.getBorrowNo(), 0,null));
		logger.info("dealWithSynchronizeOverduedOrder begin orderNo = {} , boList = {}", orderNo, boList);
		try {
			riskUtil.batchSychronizeOverdueBorrow(orderNo, boList);
		} catch (Exception e) {
			logger.error("续借成功时给风控传输数据出错", e);
		}
		
		logger.info("dealWithSynchronizeOverduedOrder completed");
	}
	
	private RiskOverdueBorrowBo parseOverduedBorrowBo(String borrowNo, Integer overdueDay, Integer overduetimes) {
    	RiskOverdueBorrowBo bo = new RiskOverdueBorrowBo();
    	bo.setBorrowNo(borrowNo);
    	bo.setOverdueDays(overdueDay);
    	bo.setOverdueTimes(overduetimes);
    	return bo;
    }

	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type, BigDecimal amount, Long userId, Long renewalDetailId) {
		// 增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(renewalDetailId + "");
		accountLog.setType(type.getCode());
		return accountLog;
	}

	
	/**
	 * @Description:  本金还款记录
	 * @return  AfRenewalDetailDo  
	 * @data  2017年12月14日
	 */
	private AfRenewalDetailDo buildRenewalDetailDo(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, String tradeNo, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrowId, Long cardId, String payTradeNo, Long userId, Integer appVersion) {

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		
		BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
		AfResourceDo baseBankRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BASE_BANK_RATE);
		BigDecimal baseBankRate = new BigDecimal(baseBankRateResource.getValue());// 央行基准利率
		
		//上期借款手续费
		BigDecimal borrowPoundage = afBorrowCashDo.getPoundage();
		//上期借款利息
		BigDecimal borrowRateAmount = afBorrowCashDo.getRateAmount();
		
		// 续借本金（总） 
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getSumRate(),afBorrowCashDo.getSumRenewalPoundage());
		// 续期金额 = 续借本金（总）  - 借款已还金额 - 续借需要支付本金
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);
		
		//---------
		//借款续期记录
		AfRenewalDetailDo afRenewalDetailDo = new AfRenewalDetailDo();
		afRenewalDetailDo.setBorrowId(borrowId);
		afRenewalDetailDo.setStatus(AfBorrowLegalRepaymentStatus.APPLY.getCode());
		afRenewalDetailDo.setGmtPlanRepayment(afBorrowCashDo.getGmtPlanRepayment());// 原预计还款时间
		afRenewalDetailDo.setRenewalAmount(waitPaidAmount);// 续期本金
		afRenewalDetailDo.setPriorInterest(borrowRateAmount);// 上期利息
		afRenewalDetailDo.setPriorOverdue(afBorrowCashDo.getOverdueAmount());// 上期滞纳金
		afRenewalDetailDo.setPriorPoundage(borrowPoundage);// 上期手续费
		afRenewalDetailDo.setNextPoundage(BigDecimal.ZERO);// 下期手续费
		afRenewalDetailDo.setJfbAmount(jfbAmount);// 集分宝个数
		afRenewalDetailDo.setRebateAmount(rebateAmount);// 账户余额
		afRenewalDetailDo.setPayTradeNo(payTradeNo);// 平台提供给三方支付的交易流水号
		afRenewalDetailDo.setTradeNo(tradeNo);// 第三方的交易流水号
		afRenewalDetailDo.setRenewalDay(allowRenewalDay.intValue());// 续期天数
		afRenewalDetailDo.setUserId(userId);
		afRenewalDetailDo.setActualAmount(actualAmount);
		afRenewalDetailDo.setPoundageRate(borrowCashPoundage);// 借钱手续费率（日）
		afRenewalDetailDo.setBaseBankRate(baseBankRate);// 央行基准利率
		
		afRenewalDetailDo.setCapital(capital);
		
		if (cardId == -2) {
			afRenewalDetailDo.setCardNumber("");
			afRenewalDetailDo.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		} else if (cardId == -1) {
			afRenewalDetailDo.setCardNumber("");
			afRenewalDetailDo.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		}
		else if(cardId ==-3){
			afRenewalDetailDo.setCardNumber("");
			afRenewalDetailDo.setCardName(Constants.DEFAULT_ZFB_PAY_NAME);
		}
		else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			afRenewalDetailDo.setCardNumber(bank.getCardNumber());
			afRenewalDetailDo.setCardName(bank.getBankName());
		}
		logger.info("buildRenewalDetailDo :",afRenewalDetailDo);
		return afRenewalDetailDo;
	}
	
	
	/**
	 * @Description:  订单记录
	 * @return  AfBorrowLegalOrderDo  
	 * @data  2017年12月14日
	 */
	private AfBorrowLegalOrderDo buildAfBorrowLegalOrderDo(AfBorrowCashDo afBorrowCashDo, Long userId, Long goodsId, String deliveryUser, String deliveryPhone, String address){
		//新增订单记录
		AfGoodsDo goodsDo = afGoodsDao.getGoodsById(goodsId);
		if(goodsDo==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_GOOD_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderDo borrowLegalOrder = new AfBorrowLegalOrderDo();
		borrowLegalOrder.setBorrowId(afBorrowCashDo.getRid());
		borrowLegalOrder.setGoodsId(goodsId);
		borrowLegalOrder.setDeliveryUser(deliveryUser);
		borrowLegalOrder.setDeliveryPhone(deliveryPhone);
		borrowLegalOrder.setAddress(address);
		borrowLegalOrder.setUserId(userId);
		borrowLegalOrder.setStatus("UNPAID");//未支付
		borrowLegalOrder.setPriceAmount(goodsDo.getSaleAmount());
		borrowLegalOrder.setGoodsName(goodsDo.getName());
		logger.info("buildAfBorrowLegalOrderDo :",borrowLegalOrder);
		return borrowLegalOrder;
	}

	
	/**
	 * @Description:  订单借款记录
	 * @return  AfBorrowLegalOrderCashDo  
	 * @data  2017年12月14日
	 */
	private AfBorrowLegalOrderCashDo buildAfBorrowLegalOrderCashDo(AfBorrowCashDo afBorrowCashDo, Long userId, String payTradeNo,Long goodsId){
		AfGoodsDo goodsDo = afGoodsDao.getGoodsById(goodsId);
		if(goodsDo==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_GOOD_NOT_EXIST_ERROR);
		}
		//上一笔订单记录
		AfBorrowLegalOrderDo afBorrowLegalOrder = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
		if(afBorrowLegalOrder==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(afBorrowLegalOrder.getRid());
		if(afBorrowLegalOrderCash==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		//新增订单借钱记录
		AfBorrowLegalOrderCashDo borrowLegalOrderCash = new AfBorrowLegalOrderCashDo();
		borrowLegalOrderCash.setUserId(userId);
		borrowLegalOrderCash.setBorrowId(afBorrowCashDo.getRid());
		borrowLegalOrderCash.setCashNo(payTradeNo);
		borrowLegalOrderCash.setType(String.valueOf(allowRenewalDay));
		borrowLegalOrderCash.setAmount(goodsDo.getSaleAmount());
		borrowLegalOrderCash.setStatus("APPLYING");
		borrowLegalOrderCash.setBorrowRemark(afBorrowLegalOrderCash.getBorrowRemark());
		borrowLegalOrderCash.setRefundRemark(afBorrowLegalOrderCash.getRefundRemark());
		borrowLegalOrderCash.setOverdueDay((short)0);
		borrowLegalOrderCash.setOverdueStatus("N");
		borrowLegalOrderCash.setRepaidAmount(BigDecimal.ZERO);
		borrowLegalOrderCash.setOverdueAmount(BigDecimal.ZERO);
		borrowLegalOrderCash.setSumRepaidPoundage(BigDecimal.ZERO);
		borrowLegalOrderCash.setPlanRepayDays(allowRenewalDay.intValue());

		// 查询新利率配置
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,Constants.BORROW_CASH_INFO_LEGAL_NEW);
		//借款利率
		BigDecimal newRate = null;
		//借款手续费率
		BigDecimal newServiceRate = null;
		
		if (rateInfoDo != null) {
			String orderRate = rateInfoDo.getValue3();
			JSONArray array = JSONObject.parseArray(orderRate);
			double interestRate = 0;
			double serviceRate = 0;
			for (int i = 0; i < array.size(); i++) {
				JSONObject info = array.getJSONObject(i);
				String consumeTag = info.getString("consumeTag");
				if (StringUtils.equals("INTEREST_RATE", consumeTag)) {
						interestRate = info.getDouble("consumeFirstType");
				}
				if (StringUtils.equals("SERVICE_RATE", consumeTag)) {
					serviceRate = info.getDouble("consumeFirstType");
				}
			}
			newRate = BigDecimal.valueOf(interestRate / 100);
			newServiceRate = BigDecimal.valueOf(serviceRate / 100);
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_RATE_ERROR);
		}		
		
		//BigDecimal orderWiteAmount = BigDecimalUtil.add(afBorrowLegalOrderCash.getAmount(),afBorrowLegalOrderCash.getSumRepaidInterest(),afBorrowLegalOrderCash.getSumRepaidPoundage(),afBorrowLegalOrderCash.getSumRepaidOverdue()).subtract(afBorrowLegalOrderCash.getRepaidAmount());
		
		borrowLegalOrderCash.setPoundageAmount(goodsDo.getSaleAmount().multiply(newServiceRate).multiply(allowRenewalDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS) ,2 , RoundingMode.HALF_UP));
		borrowLegalOrderCash.setInterestAmount(goodsDo.getSaleAmount().multiply(newRate).multiply(allowRenewalDay).divide(new BigDecimal(Constants.ONE_YEAY_DAYS) ,2 , RoundingMode.HALF_UP));
		borrowLegalOrderCash.setPoundageRate(newServiceRate.multiply(new BigDecimal(100)));
		borrowLegalOrderCash.setInterestRate(newRate.multiply(new BigDecimal(100)));
		borrowLegalOrderCash.setSumRepaidOverdue(BigDecimal.ZERO);
		borrowLegalOrderCash.setSumRepaidInterest(BigDecimal.ZERO);
		
		Date date = DateUtil.addDays(afBorrowLegalOrderCash.getGmtPlanRepay(), allowRenewalDay.intValue());
		borrowLegalOrderCash.setGmtPlanRepay(date);
		logger.info("buildAfBorrowLegalOrderCashDo :",borrowLegalOrderCash);
		return borrowLegalOrderCash;
	}
	
	
	/**
	 * @Description:  订单还款记录
	 * @return  AfBorrowLegalOrderRepaymentDo  
	 * @data  2017年12月14日
	 */
	private AfBorrowLegalOrderRepaymentDo buildBorrowLegalOrderRepaymentDo(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, String tradeNo, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrowId, Long cardId, String payTradeNo, Long userId, Integer appVersion) {

		//上一笔订单记录
		AfBorrowLegalOrderDo afBorrowLegalOrder = afBorrowLegalOrderDao.getLastOrderByBorrowId(afBorrowCashDo.getRid());
		if(afBorrowLegalOrder==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(afBorrowLegalOrder.getRid());
		if(afBorrowLegalOrderCash==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		
		//订单还款记录
		AfBorrowLegalOrderRepaymentDo legalOrderRepayment = new AfBorrowLegalOrderRepaymentDo();
		legalOrderRepayment.setUserId(userId);
		legalOrderRepayment.setRepayAmount(BigDecimalUtil.add(afBorrowLegalOrderCash.getAmount(),afBorrowLegalOrderCash.getInterestAmount(),afBorrowLegalOrderCash.getPoundageAmount(),afBorrowLegalOrderCash.getOverdueAmount(),
				afBorrowLegalOrderCash.getSumRepaidInterest(),afBorrowLegalOrderCash.getSumRepaidOverdue(),afBorrowLegalOrderCash.getSumRepaidPoundage()).subtract(afBorrowLegalOrderCash.getRepaidAmount()));
		legalOrderRepayment.setTradeNo(payTradeNo);
		legalOrderRepayment.setActualAmount(BigDecimal.ZERO);
		legalOrderRepayment.setStatus(AfBorrowLegalRepaymentStatus.APPLY.getCode());
		legalOrderRepayment.setRebateAmount(rebateAmount);
		legalOrderRepayment.setBorrowId(afBorrowCashDo.getRid());
		legalOrderRepayment.setBorrowLegalOrderCashId(afBorrowLegalOrderCash.getRid());
		legalOrderRepayment.setName(Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH);
		
		if (cardId == -2) {
			legalOrderRepayment.setCardNo("");
			legalOrderRepayment.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		} else if (cardId == -1) {
			legalOrderRepayment.setCardNo("");
			legalOrderRepayment.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		}
		else if(cardId ==-3){
			legalOrderRepayment.setCardNo("");
			legalOrderRepayment.setCardName(Constants.DEFAULT_ZFB_PAY_NAME);
		}
		else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			legalOrderRepayment.setCardNo(bank.getCardNumber());
			legalOrderRepayment.setCardName(bank.getBankName());
		}
		logger.info("buildBorrowLegalOrderRepaymentDo :",legalOrderRepayment);
		return legalOrderRepayment;
	}
	
	@Override
	public AfRenewalDetailDo getLastRenewalDetailByBorrowId(Long rid) {
		return afRenewalDetailDao.getLastRenewalDetailByBorrowId(rid);
	}
}
