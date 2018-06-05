package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.*;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.ald.fanbei.api.biz.third.util.pay.ThirdPayUtility;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @类描述：
 * 
 * @author fumeiai 2017年5月19日 20:04:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRenewalDetailService")
public class AfRenewalDetailServiceImpl extends BaseService implements AfRenewalDetailService {
	@Resource
	CuiShouUtils cuiShouUtils;
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
	ThirdPayUtility thirdPayUtility;
	@Resource
	ContractPdfThreadPool contractPdfThreadPool;
    @Resource
    SmsUtil smsUtil;
    @Resource
    private AfTradeCodeInfoService afTradeCodeInfoService;
    
	@Override
	public Map<String, Object> createRenewalYiBao(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo, Integer appVersion,String bankPayType) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRenewalBorrowCashNo(now);
		final String payTradeNo = repayNo;

		String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;

		final AfRenewalDetailDo renewalDetail = buildRenewalDetailDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, capital, borrow, cardId, payTradeNo, userId, appVersion);
		Map<String, Object> map = new HashMap<String, Object>();
		afRenewalDetailDao.addRenewalDetail(renewalDetail);
		if(cardId == -1 || cardId ==-3){
			Map<String, String> map1;
			if(cardId ==-1) {
				map1 = thirdPayUtility.createOrder(actualAmount,payTradeNo,userId, ThirdPayTypeEnum.WXPAY,PayOrderSource.RENEWAL_PAY);
			}
			else {
				map1 =thirdPayUtility.createOrder(actualAmount,payTradeNo,userId,ThirdPayTypeEnum.ZFBPAY,PayOrderSource.RENEWAL_PAY);
			}
			for (String key : map1.keySet()) {
				map.put(key,map1.get(key));
			}
		}
		else if (cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			dealChangStatus(payTradeNo, "", AfRenewalDetailStatus.PROCESS.getCode(), renewalDetail.getRid());
			UpsCollectRespBo respBo = (UpsCollectRespBo) upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), 
				bank.getMobile(), bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, name, "02", 
				UserAccountLogType.RENEWAL_PAY.getCode());
			if (!respBo.isSuccess()) {
			    String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
			    dealRenewalFail(payTradeNo, "",errorMsg,"");
			    throw new FanbeiException(errorMsg);
			}
			map.put("resp", respBo);
		} else if (cardId == -2) {// 余额支付
			dealRenewalSucess(renewalDetail.getPayTradeNo(), "");
		}
		map.put("refId", renewalDetail.getRid());
		map.put("type", UserAccountLogType.RENEWAL_PAY.getCode());

		return map;
	}

	@Override
	public Map<String, Object> createRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo, Integer appVersion,String  bankPayType,String majiabaoName) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRenewalBorrowCashNo(now);
		final String payTradeNo = repayNo;

		String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;

		final AfRenewalDetailDo renewalDetail = buildRenewalDetailDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, capital, borrow, cardId, payTradeNo, userId, appVersion);
		Map<String, Object> map = new HashMap<String, Object>();
		afRenewalDetailDao.addRenewalDetail(renewalDetail);

		if (cardId == -1) {// 微信支付
			map = UpsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, actualAmount, PayOrderSource.RENEWAL_PAY.getCode());
		} else if (cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			dealChangStatus(payTradeNo, "", AfRenewalDetailStatus.PROCESS.getCode(), renewalDetail.getRid());
			UpsCollectRespBo respBo = (UpsCollectRespBo) upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(),
				bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, name, "02", 
				UserAccountLogType.RENEWAL_PAY.getCode());
            	    	if (!respBo.isSuccess()) {
            	    	    String errowMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
            	    	    dealRenewalFail(payTradeNo, "", errowMsg,majiabaoName);
            	    	    throw new FanbeiException(errowMsg);
            	    	}
			map.put("resp", respBo);
		} else if (cardId == -2) {// 余额支付
			dealRenewalSucess(renewalDetail.getPayTradeNo(), "");
		}
		map.put("refId", renewalDetail.getRid());
		map.put("type", UserAccountLogType.RENEWAL_PAY.getCode());

		return map;
	}

	long dealChangStatus(String outTradeNo, String tradeNo, String status, Long rid) {

//		AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
//		if(afYibaoOrderDo !=null){
//			if(afYibaoOrderDo.getStatus().intValue() == 1){
//				return 1L;
//			}
//			else{
//				if(status.equals("N")) {
//					afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 2);
//				}
//			}
//		}
		if(AfBorrowCashRepmentStatus.NO.getCode().equals(status)) {
			if (thirdPayUtility.checkFail(outTradeNo)) {
				return 1L;
			}
		}

		AfRenewalDetailDo afRenewalDetailDo = new AfRenewalDetailDo();
		afRenewalDetailDo.setStatus(status);
		afRenewalDetailDo.setTradeNo(tradeNo);
		afRenewalDetailDo.setRid(rid);
		return afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
	}

	@Override
	public long dealRenewalFail(String outTradeNo, String tradeNo,String errorMsg,String majiabaoName) {
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
		if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
			return 0l;
		}
		long result = dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), afRenewalDetailDo.getRid());
		
		//通知相关
		AfUserDo userDo = afUserService.getUserById(afRenewalDetailDo.getUserId());
		try {
			pushService.repayRenewalFail(userDo.getUserName());
		}catch (Exception e){
			logger.error("dealRenewalFail push exception.",e);
		}
		//fmf_add 续借失败短信通知 alter by chengkang20171220
		//模版数据map处理
		Map<String,String> replaceMapData = new HashMap<String, String>();
		replaceMapData.put("errorMsg", errorMsg);
		//用户信息及当日还款失败次数校验
		int errorTimes = afRenewalDetailDao.getCurrDayRepayErrorTimes(afRenewalDetailDo.getUserId());
		try {
			if (majiabaoName.contains("borrowSuperman")){
				smsUtil.sendYsSmsConfigMessageToMobile(userDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_JKCR_RENEWAL_DETAIL_FAIL.getCode());
			}else {
				smsUtil.sendConfigMessageToMobile(userDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RENEWAL_DETAIL_FAIL.getCode());
			}
			String title = "本次续借支付失败";
			String content = "续借支付失败：&errorMsg。";
			content = content.replace("&errorMsg",errorMsg);
			pushService.pushUtil(title,content,userDo.getMobile());
		} catch (Exception e) {
			logger.error("sendRenewalFailWarnMsg is Fail.",e);
		}
		return result;
	}

	@Override
	public long dealRenewalFail(String outTradeNo, String tradeNo,String errorMsg) {
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
		if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
			return 0l;
		}
		long result = dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), afRenewalDetailDo.getRid());

		//通知相关
		AfUserDo userDo = afUserService.getUserById(afRenewalDetailDo.getUserId());
		try {
			pushService.repayRenewalFail(userDo.getUserName());
		}catch (Exception e){
			logger.error("dealRenewalFail push exception.",e);
		}
		//fmf_add 续借失败短信通知 alter by chengkang20171220
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

	@Override
	public long dealRenewalSucess(final String outTradeNo, final String tradeNo) {

		final String key = outTradeNo +"_success_repayCash_renewal";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
		if (count != 1) {
			return -1;
		}

		final AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
		logger.info("afRenewalDetailDo=" + afRenewalDetailDo);
		if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
			redisTemplate.delete(key);
			return 0l;
		}

		long resultValue =  transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
//					AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
//					if(afYibaoOrderDo !=null){
//						if(afYibaoOrderDo.getStatus().intValue() == 1){
//							return 0L;
//						}else{
//							afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),1);
//						}
//					}
					if( thirdPayUtility.checkSuccess(outTradeNo)){
						return 1L;
					}

					AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(afRenewalDetailDo.getBorrowId());

					// 变更续期记录为续期成功
					AfRenewalDetailDo temRenewalDetail = new AfRenewalDetailDo();
					temRenewalDetail.setStatus(AfRenewalDetailStatus.YES.getCode());
					temRenewalDetail.setTradeNo(tradeNo);
					temRenewalDetail.setRid(afRenewalDetailDo.getRid());
					afRenewalDetailDao.updateRenewalDetail(temRenewalDetail);
					
					AfResourceDo bankDoubleResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_BASE_BANK_DOUBLE);
					BigDecimal bankDouble = new BigDecimal(bankDoubleResource.getValue());// 借钱最高倍数
					
					//未还金额
					BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
					BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(afRenewalDetailDo.getCapital());
					
					BigDecimal rateAmount = BigDecimalUtil.multiply(waitPaidAmount, afRenewalDetailDo.getBaseBankRate(), bankDouble, new BigDecimal(afRenewalDetailDo.getRenewalDay()).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP));

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

					afBorrowCashDo.setRepayAmount(BigDecimalUtil.add(afBorrowCashDo.getRepayAmount(), afRenewalDetailDo.getPriorInterest(), afRenewalDetailDo.getPriorOverdue(), afRenewalDetailDo.getCapital()));// 累计已还款金额
					afBorrowCashDo.setSumOverdue(afBorrowCashDo.getSumOverdue().add(afBorrowCashDo.getOverdueAmount()));// 累计滞纳金
					afBorrowCashDo.setOverdueAmount(BigDecimal.ZERO);// 滞纳金置0
					afBorrowCashDo.setSumRate(afBorrowCashDo.getSumRate().add(afBorrowCashDo.getRateAmount()));// 累计利息
					afBorrowCashDo.setRateAmount(rateAmount);// 利息改成续期后的
					afBorrowCashDo.setSumJfb(afBorrowCashDo.getSumJfb().add(afRenewalDetailDo.getJfbAmount()));// 累计使用集分宝
					afBorrowCashDo.setSumRebate(afBorrowCashDo.getSumRebate().add(afRenewalDetailDo.getRebateAmount()));// 累计使用账户余额
					afBorrowCashDo.setSumRenewalPoundage(afBorrowCashDo.getSumRenewalPoundage().add(afRenewalDetailDo.getNextPoundage()));// 累计续期手续费
					afBorrowCashDo.setRenewalNum(afBorrowCashDo.getRenewalNum() + 1);// 累计续期次数
					afBorrowCashService.updateBorrowCash(afBorrowCashDo);

					// 授权账户可用金额变更
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(afRenewalDetailDo.getUserId());
					account.setJfbAmount(afRenewalDetailDo.getJfbAmount().multiply(new BigDecimal(-1)));
					account.setRebateAmount(afRenewalDetailDo.getRebateAmount().multiply(new BigDecimal(-1)));
					afUserAccountDao.updateUserAccount(account);

					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.RENEWAL_PAY, afRenewalDetailDo.getRebateAmount(), afRenewalDetailDo.getUserId(), afRenewalDetailDo.getRid()));
					return 1l;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealRenewalSucess error", e);
					return 0l;
				} finally {
					redisTemplate.delete(key);
				}
			}
		});

		if(resultValue == 1L){
			//续期成功,推送等抽出
			logger.info("续期成功，推送消息和向催收平台同步进入borrowCashId:"+afRenewalDetailDo.getBorrowId()+",afRenewalDetailDoId:"+afRenewalDetailDo.getRid());

			AfBorrowCashDo currAfBorrowCashDo = afBorrowCashService.getBorrowCashByrid(afRenewalDetailDo.getBorrowId());
			AfUserDo userDo = afUserService.getUserById(currAfBorrowCashDo.getUserId());
			cuiShouUtils.syncXuqi(currAfBorrowCashDo);//新催收
			try {
				pushService.repayRenewalSuccess(userDo.getUserName());
				logger.info("续期成功，推送消息成功outTradeNo:"+outTradeNo);
			}catch (Exception e){
				logger.error("续期成功，推送消息异常outTradeNo:"+outTradeNo,e);
			}

			//当续期成功时,同步逾期天数为0
			dealWithSynchronizeOverduedOrder(currAfBorrowCashDo);

			/*try {
				//生成续期凭据
				afContractPdfCreateService.protocolRenewal(afRenewalDetailDo.getUserId(),afRenewalDetailDo.getBorrowId(),afRenewalDetailDo.getRid(),afRenewalDetailDo.getRenewalDay(),afRenewalDetailDo.getRenewalAmount());
				logger.error("生成续期凭据成功，renewId="+afRenewalDetailDo.getUserId());
			} catch (Exception e) {
				logger.error("生成续期凭据异常，renewId="+afRenewalDetailDo.getUserId(),e);
			}*/
			//爱上街续期通知接口，向催收平台同步续期信息
			try {
				if (currAfBorrowCashDo.getOverdueStatus().equals("Y") || currAfBorrowCashDo.getOverdueDay() > 0) {
					CollectionSystemReqRespBo respInfo = collectionSystemUtil.renewalNotify(currAfBorrowCashDo.getBorrowNo(), afRenewalDetailDo.getPayTradeNo(), afRenewalDetailDo.getRenewalDay(),(afRenewalDetailDo.getNextPoundage().multiply(BigDecimalUtil.ONE_HUNDRED))+"");
					logger.info("collection renewalNotify req success, respinfo={}",respInfo+" ,borrowNo="+currAfBorrowCashDo.getBorrowNo(),respInfo);
				}else{
					logger.info("collection renewalNotify req unPush, renewalNo=" + afRenewalDetailDo.getPayTradeNo());
				}
			}catch(Exception e){
				logger.error("向催收平台同步续期信息:"+currAfBorrowCashDo.getBorrowNo(),e);
			}
		}
		if (resultValue == 1L){
			//生成续期凭据
			contractPdfThreadPool.protocolRenewalPdf(afRenewalDetailDo.getUserId(),afRenewalDetailDo.getBorrowId(),afRenewalDetailDo.getRid(),afRenewalDetailDo.getRenewalDay(),afRenewalDetailDo.getRenewalAmount());
		}
		return resultValue;
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

	private AfRenewalDetailDo buildRenewalDetailDo(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, String tradeNo, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrowId, Long cardId, String payTradeNo, Long userId, Integer appVersion) {

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		/*AfResourceDo poundageResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_POUNDAGE);
		BigDecimal borrowCashPoundage = new BigDecimal(poundageResource.getValue());// 借钱手续费率（日）
		Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
		if (poundageRateCash != null) {
			borrowCashPoundage = new BigDecimal(poundageRateCash.toString());
		}*/
		BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
		AfResourceDo baseBankRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BASE_BANK_RATE);
		BigDecimal baseBankRate = new BigDecimal(baseBankRateResource.getValue());// 央行基准利率

		//未还金额
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);

		if (appVersion < 380) {
			waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
		}
		
		// 本期手续费  = 未还金额 * 续期天数 * 借钱手续费率（日）
		BigDecimal poundage = waitPaidAmount.multiply(allowRenewalDay).multiply(borrowCashPoundage).setScale(2, RoundingMode.HALF_UP);
		
		AfRenewalDetailDo afRenewalDetailDo = new AfRenewalDetailDo();
		afRenewalDetailDo.setBorrowId(borrowId);
		afRenewalDetailDo.setStatus(AfRenewalDetailStatus.APPLY.getCode());
		afRenewalDetailDo.setGmtPlanRepayment(afBorrowCashDo.getGmtPlanRepayment());// 原预计还款时间
		afRenewalDetailDo.setRenewalAmount(waitPaidAmount);// 续期本金
		afRenewalDetailDo.setPriorInterest(afBorrowCashDo.getRateAmount());// 上期利息
		afRenewalDetailDo.setPriorOverdue(afBorrowCashDo.getOverdueAmount());// 上期滞纳金
		afRenewalDetailDo.setNextPoundage(poundage);// 下期手续费
		afRenewalDetailDo.setJfbAmount(jfbAmount);// 集分宝个数
		afRenewalDetailDo.setRebateAmount(rebateAmount);// 账户余额
		afRenewalDetailDo.setPayTradeNo(payTradeNo);// 平台提供给三方支付的交易流水号
		afRenewalDetailDo.setTradeNo(tradeNo);// 第三方的交易流水号
		afRenewalDetailDo.setRenewalDay(allowRenewalDay.intValue());// 续期天数
		afRenewalDetailDo.setUserId(userId);
		afRenewalDetailDo.setActualAmount(actualAmount);
		afRenewalDetailDo.setPoundageRate(borrowCashPoundage);// 借钱手续费率（日）
		afRenewalDetailDo.setBaseBankRate(baseBankRate);// 央行基准利率
		
		if (appVersion >= 380) {
			afRenewalDetailDo.setCapital(capital);
		} else {
			afRenewalDetailDo.setCapital(BigDecimal.ZERO);
		}
		
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

		return afRenewalDetailDo;
	}

	@Override
	public List<AfRenewalDetailDo> getRenewalListByBorrowId(Long borrowId, Integer start) {
		return afRenewalDetailDao.getRenewalListByBorrowId(borrowId, start);
	}

	@Override
	public AfRenewalDetailDo getRenewalDetailByRenewalId(Long rId) {
		return afRenewalDetailDao.getRenewalDetailByRenewalId(rId);
	}

	@Override
	public AfRenewalDetailDo getRenewalDetailByBorrowId(Long borrowId) {
		return afRenewalDetailDao.getRenewalDetailByBorrowId(borrowId);
	}

	@Override
	public List<AfRenewalDetailDo> getRenewalDetailListByBorrowId(Long borrowId) {
		return afRenewalDetailDao.getRenewalDetailListByBorrowId(borrowId);
	}

	@Override
	public AfRenewalDetailDo getRenewalDetailHoursByBorrowId(Long borrowId) {
		return afRenewalDetailDao.getRenewalDetailHoursByBorrowId(borrowId);
	}

}
