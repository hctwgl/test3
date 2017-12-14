package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfRenewalLegalDetailService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
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
import com.timevale.tgtext.text.pdf.da;

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

	@Override
	public Map<String, Object> createLegalRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo, Integer appVersion, Long goodsId, String deliveryUser, String deliveryPhone, String address) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRenewalBorrowCashNo(now);
		final String payTradeNo = repayNo;

		String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;
		
		//新增还款记录（本金还款记录，订单还款记录）
		final AfRenewalDetailDo renewalDetail = buildRenewalDetailDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, capital, borrow, cardId, payTradeNo, userId, appVersion);
		final AfBorrowLegalOrderRepaymentDo legalOrderRepayment = buildBorrowLegalOrderRepaymentDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, capital, borrow, cardId, payTradeNo, userId, appVersion);
		
		//新增订单记录（订单记录，订单借款记录）
		final AfBorrowLegalOrderDo borrowLegalOrder = buildAfBorrowLegalOrderDo(afBorrowCashDo, userId, goodsId, deliveryUser, deliveryPhone, address);
		final AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = buildAfBorrowLegalOrderCashDo(afBorrowCashDo,userId,payTradeNo,borrowLegalOrder);
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		afRenewalDetailDao.addRenewalDetail(renewalDetail);
		afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(legalOrderRepayment);
		afBorrowLegalOrderCashDao.saveRecord(afBorrowLegalOrderCashDo);
		afBorrowLegalOrderService.saveBorrowLegalOrder(borrowLegalOrder);
		
		if (cardId == -1) {// 微信支付
			map = UpsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, actualAmount, PayOrderSource.RENEWAL_PAY.getCode());
		} else if (cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			dealChangStatus(payTradeNo, repayNo, AfRenewalDetailStatus.PROCESS.getCode(), renewalDetail.getRid(),legalOrderRepayment.getId(), borrowLegalOrder.getRid());
			UpsCollectRespBo respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(), bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, name, "02", PayOrderSource.RENEW_CASH_LEGAL.getCode());
			if (!respBo.isSuccess()) {
				dealLegalRenewalFail(payTradeNo, repayNo,StringUtil.processRepayFailThirdMsg(respBo.getRespDesc()));
				throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			map.put("resp", respBo);
		} else if (cardId == -2) {// 余额支付
			dealLegalRenewalSucess(renewalDetail.getPayTradeNo(), "");
		}
		map.put("refId", renewalDetail.getRid());
		map.put("refOrderRepaymentId", legalOrderRepayment.getId());
		map.put("refOrderId", borrowLegalOrder.getRid());
		map.put("type", UserAccountLogType.RENEWAL_PAY.getCode());

		return map;
	}
	
	
	
	long dealChangStatus(String outTradeNo, String tradeNo, String status, Long rid, Long orderRepaymentId, Long orderId) {

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

		//更新订单还款记录
		AfBorrowLegalOrderRepaymentDo borrowLegalOrderRepayment = new AfBorrowLegalOrderRepaymentDo();
		borrowLegalOrderRepayment.setStatus(status);
		borrowLegalOrderRepayment.setTradeNo(tradeNo);
		borrowLegalOrderRepayment.setId(orderRepaymentId);
		borrowLegalOrderRepayment.setGmtModified(new Date());
		afBorrowLegalOrderRepaymentDao.updateBorrowLegalOrderRepayment(borrowLegalOrderRepayment);
		
		if(status.equals("Y")){
			//更新订单记录
			AfBorrowLegalOrderDo borrowLegalOrderDo = new AfBorrowLegalOrderDo();
			borrowLegalOrderDo.setRid(orderId);
			borrowLegalOrderDo.setStatus("CLOSED");
			borrowLegalOrderDo.setGmtClosed(new Date());
			borrowLegalOrderDo.setGmtModified(new Date());
			afBorrowLegalOrderDao.updateById(borrowLegalOrderDo);
		}

		//更新还款记录
		AfRenewalDetailDo afRenewalDetailDo = new AfRenewalDetailDo();
		afRenewalDetailDo.setStatus(status);
		afRenewalDetailDo.setTradeNo(tradeNo);
		afRenewalDetailDo.setRid(rid);
		afRenewalDetailDo.setGmtModified(new Date());
		
		return afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
	}

	@Override
	public long dealLegalRenewalFail(String outTradeNo, String tradeNo,String errorMsg) {
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
		AfBorrowLegalOrderRepaymentDo borrowLegalOrderRepayment = afBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByPayTradeNo(outTradeNo);
		AfBorrowLegalOrderDo borrowLegalOrderDo = afBorrowLegalOrderDao.getLastBorrowLegalOrderByBorrowId(afRenewalDetailDo.getBorrowId());
		
		if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
			return 0l;
		}
		AfUserDo userDo = afUserService.getUserById(afRenewalDetailDo.getUserId());
		try {
			pushService.repayRenewalFail(userDo.getUserName());
		}
		catch (Exception e){

		}
		//fmf_add 续借失败短信通知
		//用户信息及当日还款失败次数校验
		int errorTimes = afRenewalDetailDao.getCurrDayRepayErrorTimes(afRenewalDetailDo.getUserId());
		try {
			smsUtil.sendRenewalFailWarnMsg(userDo.getUserName(), errorMsg, errorTimes);
		} catch (Exception e) {
			logger.error("sendRenewalFailWarnMsg is Fail, e"+e);
		}

		return dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), afRenewalDetailDo.getRid(), borrowLegalOrderRepayment.getId(),borrowLegalOrderDo.getRid());
	}

	@Override
	public long dealLegalRenewalSucess(final String outTradeNo, final String tradeNo) {

		final String key = outTradeNo +"_success_repayCash_renewal";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
		if (count != 1) {
			return -1;
		}

		final AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
		final AfBorrowLegalOrderRepaymentDo borrowLegalOrderRepayment = afBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByPayTradeNo(outTradeNo);
		final AfBorrowLegalOrderDo borrowLegalOrderDo = afBorrowLegalOrderDao.getLastBorrowLegalOrderByBorrowId(afRenewalDetailDo.getBorrowId());
		
		logger.info("afRenewalDetailDo=" + afRenewalDetailDo);
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

					// 变更续期记录为续期成功
					AfRenewalDetailDo temRenewalDetail = new AfRenewalDetailDo();
					temRenewalDetail.setStatus(AfRenewalDetailStatus.YES.getCode());
					temRenewalDetail.setTradeNo(tradeNo);
					temRenewalDetail.setRid(afRenewalDetailDo.getRid());
					temRenewalDetail.setGmtModified(new Date());
					afRenewalDetailDao.updateRenewalDetail(temRenewalDetail);
					
					// 变更订单续期记录为已结清
					AfBorrowLegalOrderRepaymentDo temBorrowLegalOrderRepayment = new AfBorrowLegalOrderRepaymentDo();
					temBorrowLegalOrderRepayment.setStatus(AfRenewalDetailStatus.YES.getCode());
					temBorrowLegalOrderRepayment.setTradeNo(tradeNo);
					temBorrowLegalOrderRepayment.setId(borrowLegalOrderRepayment.getId());
					temBorrowLegalOrderRepayment.setGmtModified(new Date());
					afBorrowLegalOrderRepaymentDao.updateBorrowLegalOrderRepayment(temBorrowLegalOrderRepayment);
					
					// 变更订单还款日志
					AfBorrowLegalOrderCashDo borrowLegalOrderCash = new AfBorrowLegalOrderCashDo();
					borrowLegalOrderCash.setRid(borrowLegalOrderRepayment.getBorrowLegalOrderCashId());
					borrowLegalOrderCash.setStatus("FINISHED");
					borrowLegalOrderCash.setGmtModifed(new Date());
					afBorrowLegalOrderCashDao.updateById(borrowLegalOrderCash);
					
					//更新续期订单
					AfBorrowLegalOrderDo temBorrowLegalOrderDo = new AfBorrowLegalOrderDo();
					temBorrowLegalOrderDo.setStatus("AWAIT_DELIVER");
					temBorrowLegalOrderDo.setRid(borrowLegalOrderDo.getRid());
					temBorrowLegalOrderDo.setGmtModified(new Date());
					afBorrowLegalOrderDao.updateById(temBorrowLegalOrderDo);
					
//					AfResourceDo bankDoubleResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_BASE_BANK_DOUBLE);
//					BigDecimal bankDouble = new BigDecimal(bankDoubleResource.getValue());// 借钱最高倍数
//					
//					//未还金额
//					BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
//					BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(afRenewalDetailDo.getCapital());
//					
//					BigDecimal rateAmount = BigDecimalUtil.multiply(waitPaidAmount, afRenewalDetailDo.getBaseBankRate(), bankDouble, new BigDecimal(afRenewalDetailDo.getRenewalDay()).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP));
					
					//续期本金
					BigDecimal waitPaidAmount =afRenewalDetailDo.getRenewalAmount();
					// 查询新利率配置
		    		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,Constants.BORROW_CASH_INFO_LEGAL);
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
		    						rate = info.getDouble("borrowSevenDay");
		    				}
		    				if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
		    					serviceRate = info.getDouble("borrowSevenDay");
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
					afBorrowCashDo.setSumJfb(afBorrowCashDo.getSumJfb().add(afRenewalDetailDo.getJfbAmount()));// 累计使用集分宝
					afBorrowCashDo.setSumRebate(afBorrowCashDo.getSumRebate().add(afRenewalDetailDo.getRebateAmount()));// 累计使用账户余额
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
			logger.info("续期成功，推送消息和向催收平台同步进入borrowCashId:"+afRenewalDetailDo.getBorrowId()+",afRenewalDetailDoId:"+afRenewalDetailDo.getRid()+",afBorrowLegalOrderRepaymentId:"+borrowLegalOrderRepayment.getId());

			AfBorrowCashDo currAfBorrowCashDo = afBorrowCashService.getBorrowCashByrid(afRenewalDetailDo.getBorrowId());
			AfUserDo userDo = afUserService.getUserById(currAfBorrowCashDo.getUserId());
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
			//返呗续期通知接口，向催收平台同步续期信息
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

	
	/**
	 * @Description:  本金还款记录
	 * @return  AfRenewalDetailDo  
	 * @data  2017年12月14日
	 */
	private AfRenewalDetailDo buildRenewalDetailDo(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, String tradeNo, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrowId, Long cardId, String payTradeNo, Long userId, Integer appVersion) {

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		
		BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
		AfResourceDo baseBankRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BASE_BANK_RATE);
		BigDecimal baseBankRate = new BigDecimal(baseBankRateResource.getValue());// 央行基准利率
		
		// 查询新利率配置
		AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE,Constants.BORROW_CASH_INFO_LEGAL);
		
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
						rate = info.getDouble("borrowSevenDay");
				}
				if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
					serviceRate = info.getDouble("borrowSevenDay");
				}
			}
			newRate = BigDecimal.valueOf(rate / 100);
			newServiceRate = BigDecimal.valueOf(serviceRate / 100);
		}else{
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_RATE_ERROR);
		}		

        //---------
		
		//上期借款手续费
		BigDecimal borrowPoundage = BigDecimal.ZERO;
		//上期借款利息
		BigDecimal borrowRateAmount = BigDecimal.ZERO;
		BigDecimal oneYeayDays = new BigDecimal(Constants.ONE_YEAY_DAYS);
		
		if(afBorrowCashDo.getRenewalNum()>0){
			//续借过
			AfRenewalDetailDo renewalDetail = afRenewalLegalDetailService.getLastRenewalDetailByBorrowId(afBorrowCashDo.getRid());
			// 续期手续费 = 上期续借金额 * 上期续借天数 * 借款手续费率  / 360
			borrowPoundage = renewalDetail.getRenewalAmount().multiply(allowRenewalDay).multiply(newServiceRate).divide(oneYeayDays).setScale(2, RoundingMode.HALF_UP);
			// 续期利息 = 上期续借金额 * 上期续借天数  * 借款利率 / 360
			borrowRateAmount = renewalDetail.getRenewalAmount().multiply(allowRenewalDay).multiply(newRate).divide(oneYeayDays).setScale(2, RoundingMode.HALF_UP);
		}else {
			//未续借过
			borrowPoundage = afBorrowCashDo.getPoundage();
			borrowRateAmount = afBorrowCashDo.getRateAmount();
		}

		
		// 续借本金（总） 
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getSumRate(),borrowPoundage);
		// 续期金额 = 续借本金（总）  - 借款已还金额 - 续借需要支付本金
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);
		
		//---------
		//借款续期记录
		AfRenewalDetailDo afRenewalDetailDo = new AfRenewalDetailDo();
		afRenewalDetailDo.setBorrowId(borrowId);
		afRenewalDetailDo.setStatus(AfRenewalDetailStatus.APPLY.getCode());
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
		return borrowLegalOrder;
	}

	
	/**
	 * @Description:  订单借款记录
	 * @return  AfBorrowLegalOrderCashDo  
	 * @data  2017年12月14日
	 */
	private AfBorrowLegalOrderCashDo buildAfBorrowLegalOrderCashDo(AfBorrowCashDo afBorrowCashDo, Long userId, String payTradeNo,AfBorrowLegalOrderDo borrowLegalOrder){
		//上一笔订单记录
		AfBorrowLegalOrderDo afBorrowLegalOrder = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
		if(afBorrowLegalOrder==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(afBorrowLegalOrder.getRid());
		if(afBorrowLegalOrderCash==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		//新增订单借钱记录
		AfBorrowLegalOrderCashDo borrowLegalOrderCash = new AfBorrowLegalOrderCashDo();
		borrowLegalOrderCash.setUserId(userId);
		borrowLegalOrderCash.setBorrowId(afBorrowCashDo.getRid());
		borrowLegalOrderCash.setBorrowLegalOrderId(borrowLegalOrder.getRid());
		borrowLegalOrderCash.setCashNo(payTradeNo);
		borrowLegalOrderCash.setType("SEVEN");
		borrowLegalOrderCash.setAmount(borrowLegalOrder.getPriceAmount());
		borrowLegalOrderCash.setStatus("APPLYING");
		borrowLegalOrderCash.setBorrowRemark(afBorrowLegalOrderCash.getBorrowRemark());
		borrowLegalOrderCash.setRefundRemark(afBorrowLegalOrderCash.getRefundRemark());
		borrowLegalOrderCash.setOverdueDay((short)0);
		borrowLegalOrderCash.setOverdueStatus("N");
		borrowLegalOrderCash.setOverdueAmount(BigDecimal.ZERO);
		borrowLegalOrderCash.setRepaidAmount(BigDecimal.ZERO);
		borrowLegalOrderCash.setPoundageAmount(BigDecimal.ZERO);
		borrowLegalOrderCash.setInterestAmount(BigDecimal.ZERO);
		borrowLegalOrderCash.setPlanRepayDays(allowRenewalDay.intValue());
		
		Date date = DateUtil.addDays(new Date(), 7);
		borrowLegalOrderCash.setGmtPlanRepay(date);
				
		return borrowLegalOrderCash;
	}
	
	
	/**
	 * @Description:  订单还款记录
	 * @return  AfBorrowLegalOrderRepaymentDo  
	 * @data  2017年12月14日
	 */
	private AfBorrowLegalOrderRepaymentDo buildBorrowLegalOrderRepaymentDo(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, String tradeNo, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrowId, Long cardId, String payTradeNo, Long userId, Integer appVersion) {

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数

		BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
		
        //---------
		
		//上一笔订单记录
		AfBorrowLegalOrderDo afBorrowLegalOrder = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
		if(afBorrowLegalOrder==null){
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_ORDER_NOT_EXIST_ERROR);
		}
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCash = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(afBorrowLegalOrder.getRid());
		
		//上期订单手续费
		BigDecimal orderPoundage = NumberUtil.objToBigDecimalDefault(afBorrowLegalOrderCash.getPoundageAmount(),BigDecimal.ZERO);
		//上期订单利息
		BigDecimal orderRateAmount = NumberUtil.objToBigDecimalDefault(afBorrowLegalOrderCash.getInterestAmount(),BigDecimal.ZERO);
		
		//---------
		//订单续期记录
		AfBorrowLegalOrderRepaymentDo legalOrderRepayment = new AfBorrowLegalOrderRepaymentDo();
		legalOrderRepayment.setUserId(userId);
		legalOrderRepayment.setBorrowLegalOrderCashId(afBorrowLegalOrderCash.getRid());
		legalOrderRepayment.setRepayAmount(afBorrowLegalOrderCash.getAmount());
		legalOrderRepayment.setTradeNo(payTradeNo);
		legalOrderRepayment.setTradeNoUps(tradeNo);
		legalOrderRepayment.setStatus("A");
		legalOrderRepayment.setRebateAmount(rebateAmount);
		
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

		return legalOrderRepayment;
	}
	
	@Override
	public AfRenewalDetailDo getLastRenewalDetailByBorrowId(Long rid) {
		// TODO Auto-generated method stub
		return afRenewalDetailDao.getLastRenewalDetailByBorrowId(rid);
	}
}
