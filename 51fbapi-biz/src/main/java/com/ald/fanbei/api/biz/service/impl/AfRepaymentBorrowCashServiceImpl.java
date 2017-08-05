package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.OfflinePayType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月27日下午9:01:41
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRepaymentBorrowCashService")
public class AfRepaymentBorrowCashServiceImpl extends BaseService implements AfRepaymentBorrowCashService {

	@Resource
	AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Resource
	AfUserCouponDao afUserCouponDao;

	@Resource
	private JpushService pushService;

	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Resource
	AfUserService afUserService;

	@Resource
	UpsUtil upsUtil;
	@Resource
	RiskUtil riskUtil;
	@Resource
	SmsUtil smsUtil;
	@Resource
	CollectionSystemUtil collectionSystemUtil;
	
	@Override
	public int addRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo) {
		return afRepaymentBorrowCashDao.addRepaymentBorrowCash(afRepaymentBorrowCashDo);
	}

	@Override
	public int updateRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo) {
		return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(afRepaymentBorrowCashDo);
	}

	@Override
	public int deleteRepaymentBorrowCash(Long rid) {
		return afRepaymentBorrowCashDao.deleteRepaymentBorrowCash(rid);
	}

	@Override
	public List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashByBorrowId(Long borrowCashId) {
		return afRepaymentBorrowCashDao.getRepaymentBorrowCashByBorrowId(borrowCashId);
	}

	@Override
	public AfRepaymentBorrowCashDo getRepaymentBorrowCashByrid(Long rid) {
		return afRepaymentBorrowCashDao.getRepaymentBorrowCashByrid(rid);
	}

	@Override
	public List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashListByUserId(Long userId) {
		return afRepaymentBorrowCashDao.getRepaymentBorrowCashListByUserId(userId);
	}

	private AfRepaymentBorrowCashDo buildRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, String repayNo, Date gmtCreate, BigDecimal actualAmount,
			AfUserCouponDto coupon, BigDecimal rebateAmount, Long borrowId, Long cardId, String payTradeNo, String name, Long userId) {
		AfRepaymentBorrowCashDo repay = new AfRepaymentBorrowCashDo();
		repay.setActualAmount(actualAmount);
		repay.setBorrowId(borrowId);
		repay.setJfbAmount(jfbAmount);
		repay.setPayTradeNo(payTradeNo);
		repay.setRebateAmount(rebateAmount);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(AfBorrowCashRepmentStatus.APPLY.getCode());
		if (null != coupon) {
			repay.setUserCouponId(coupon.getRid());
			repay.setCouponAmount(coupon.getAmount());
		}
		repay.setName(name);
		repay.setUserId(userId);
		if (cardId == -2) {
			repay.setCardNumber("");
			repay.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		} else if (cardId == -1) {
			repay.setCardNumber("");
			repay.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		} else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			repay.setCardNumber(bank.getCardNumber());
			repay.setCardName(bank.getBankName());
		}
		return repay;
	}

	@Override
	public Map<String, Object> createRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, AfUserCouponDto coupon, BigDecimal rebateAmount,
			Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRepaymentBorrowCashNo(now);
		final String payTradeNo = repayNo;
		// 新增还款记录
		String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;

		final AfRepaymentBorrowCashDo repayment = buildRepayment(jfbAmount, repaymentAmount, repayNo, now, actualAmount, coupon, rebateAmount, borrow, cardId, payTradeNo, name,
				userId);
		Map<String, Object> map = new HashMap<String, Object>();
		afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
		if (cardId == -1) {// 微信支付
			map = UpsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, actualAmount, PayOrderSource.REPAYMENTCASH.getCode());
		} else if (cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			UpsCollectRespBo respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(), bank.getBankCode(),
					bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, name, "02", UserAccountLogType.REPAYMENTCASH.getCode());
			if (respBo.isSuccess()) {
				dealChangStatus(payTradeNo, "", AfBorrowCashRepmentStatus.PROCESS.getCode(), repayment.getRid());
			} else {
				dealRepaymentFail(payTradeNo, "");
			}
			map.put("resp", respBo);
		} else if (cardId == -2) {// 余额支付
			dealRepaymentSucess(repayment.getPayTradeNo(), "");
		}
		map.put("refId", repayment.getRid());
		map.put("type", UserAccountLogType.REPAYMENTCASH.getCode());

		return map;
	}

	@Override
	public long dealRepaymentSucess(final String outTradeNo, final String tradeNo) {

		return transactionTemplate.execute(new TransactionCallback<Long>() {

			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
					logger.info("repayment=" + repayment);
					if (YesNoStatus.YES.getCode().equals(repayment.getStatus())) {
						return 0l;
					}

					AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(repayment.getBorrowId());
					BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
					
					AfBorrowCashDo bcashDo = new AfBorrowCashDo();
					bcashDo.setRid(afBorrowCashDo.getRid());
					bcashDo.setSumRenewalPoundage(afBorrowCashDo.getSumRenewalPoundage());
					bcashDo.setRenewalNum(afBorrowCashDo.getRenewalNum());
//					BigDecimal repayAllAmount = afRepaymentBorrowCashDao.getRepaymentAllAmountByBorrowId(repayment.getBorrowId());
					BigDecimal nowRepayAmount = repayment.getRepaymentAmount();
					BigDecimal repayAmount = nowRepayAmount.add(afBorrowCashDo.getRepayAmount());
					logger.info("repayAmount=" + repayAmount);
					
					String nowRepayAmountStr = NumberUtil.format2Str(nowRepayAmount);
					String notRepayMoneyStr = "";
					
					AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(afBorrowCashDo.getUserId());
					String cardNo = StringUtils.EMPTY;
					if (card != null) {
						cardNo = card.getCardNumber();
					} else {
						cardNo = System.currentTimeMillis() + StringUtils.EMPTY;
					}
					
					BigDecimal interest = BigDecimal.ZERO;
					
					// 还款的时候 需要判断是否能还清利息 同时修改累计利息 start
					BigDecimal tempRepayAmount = BigDecimal.ZERO;
					tempRepayAmount = repayment.getRepaymentAmount();
					if (tempRepayAmount.compareTo(afBorrowCashDo.getRateAmount()) > 0) {
						bcashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), afBorrowCashDo.getRateAmount()));
						bcashDo.setRateAmount(BigDecimal.ZERO);
						interest = afBorrowCashDo.getRateAmount();
						tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getRateAmount());
					} else {
						bcashDo.setSumRate(BigDecimalUtil.add(bcashDo.getSumRate(), tempRepayAmount));
						bcashDo.setRateAmount(afBorrowCashDo.getRateAmount().subtract(tempRepayAmount));
						interest = tempRepayAmount;
						tempRepayAmount = BigDecimal.ZERO;
					}
					// 还款的时候 需要判断是否能还清利息 同时修改累计利息 end

					// 累计集分宝
					bcashDo.setSumJfb(BigDecimalUtil.add(afBorrowCashDo.getSumJfb(), repayment.getJfbAmount()));
					
					BigDecimal overdueAmount = BigDecimal.ZERO;

					// 还款的时候 需要判断是否能还清滞纳金 同时修改累计滞纳金 start
					if (tempRepayAmount.compareTo(afBorrowCashDo.getOverdueAmount()) > 0) {
						bcashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount()));
						bcashDo.setOverdueAmount(BigDecimal.ZERO);
						overdueAmount = afBorrowCashDo.getOverdueAmount();
						tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getOverdueAmount());
					} else {
						bcashDo.setSumOverdue(BigDecimalUtil.add(bcashDo.getSumOverdue(), tempRepayAmount));
						bcashDo.setOverdueAmount(afBorrowCashDo.getOverdueAmount().subtract(tempRepayAmount));
						overdueAmount = tempRepayAmount;
						tempRepayAmount = BigDecimal.ZERO;
					}
					// 还款的时候 需要判断是否能还清滞纳金 同时修改累计滞纳金 end

					// 累计使用余额
					bcashDo.setSumRebate(BigDecimalUtil.add(afBorrowCashDo.getSumRebate(), repayment.getRebateAmount()));
					bcashDo.setRepayAmount(repayAmount);

					
					// 优惠券设置已使用
					afUserCouponDao.updateUserCouponSatusUsedById(repayment.getUserCouponId());

					// 授权账户可用金额变更
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(repayment.getUserId());
					account.setJfbAmount(repayment.getJfbAmount().multiply(new BigDecimal(-1)));

					account.setRebateAmount(repayment.getRebateAmount().multiply(new BigDecimal(-1)));
					afUserAccountDao.updateUserAccount(account);
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENTCASH, repayment.getRebateAmount(), repayment.getUserId(), repayment.getRid()));
					
					AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
					temRepayMent.setStatus(AfBorrowCashRepmentStatus.YES.getCode());
					temRepayMent.setTradeNo(tradeNo);
					temRepayMent.setRid(repayment.getRid());
					// 变更还款记录为已还款
					afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
					
					try {
						String riskOrderNo = riskUtil.getOrderNo("tran", cardNo.substring(cardNo.length() - 4, cardNo.length()));
						JSONArray details = new JSONArray();
						JSONObject obj = new JSONObject();
						obj.put("borrowNo", afBorrowCashDo.getBorrowNo());
						obj.put("amount", afBorrowCashDo.getAmount());
						obj.put("repayment", repayment.getRepaymentAmount());
						obj.put("income", BigDecimal.ZERO);
						obj.put("interest", interest);
						obj.put("overdueAmount", overdueAmount);
						obj.put("overdueDay", afBorrowCashDo.getOverdueDay());
						details.add(obj);
						riskUtil.transferBorrowInfo(afBorrowCashDo.getUserId().toString(), "50", riskOrderNo, details);
					} catch (Exception e) {
						logger.error("还款时给风控传输数据出错", e);
					}
					
					if (allAmount.compareTo(repayAmount) == 0) {
						Long userId = afBorrowCashDo.getUserId();
						AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
						//减少使用额度
						accountInfo.setUsedAmount(BigDecimalUtil.subtract(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount()));
						afUserAccountDao.updateOriginalUserAccount(accountInfo);
						//增加日志
						AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAYMENTCASH, 
								afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
						afUserAccountLogDao.addUserAccountLog(accountLog);
						
						bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
						// 在此处调用 风控接口存入白名单 add by fumeiai
						/*try {
							riskUtil.addwhiteUser(afBorrowCashDo.getUserId());
						} catch (Exception e) {
							logger.error("加入白名单失败", e);
						}*/
//						increaseBorrowCashAccount(afBorrowCashDo,afBorrowCashDo.getUserId());
						/**------------------------------------fmai风控提额begin------------------------------------------------*/
						try {
							String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
							BigDecimal income = BigDecimalUtil.add(afBorrowCashDo.getPoundage(), afBorrowCashDo.getOverdueAmount(),afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
							int overdueCount = 0;
							if (StringUtil.equals("Y", afBorrowCashDo.getOverdueStatus())) {
								overdueCount = 1;
							}
							riskUtil.raiseQuota(afBorrowCashDo.getUserId().toString(), afBorrowCashDo.getBorrowNo(), "50", riskOrderNo, afBorrowCashDo.getAmount(), income, afBorrowCashDo.getOverdueDay(), overdueCount);
						} catch (Exception e) {
							logger.error("风控提额提额失败", e);
						}
						/**------------------------------------fmai风控提额end--------------------------------------------------*/
					} else {
						notRepayMoneyStr = NumberUtil.format2Str(allAmount.subtract(repayAmount));
					}
					afBorrowCashService.updateBorrowCash(bcashDo);
					
					//add by chengkang 待添加还款成功短信 start
					try {
						AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
						smsUtil.sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), nowRepayAmountStr, notRepayMoneyStr);
					} catch (Exception e) {
						logger.error("还款成功发送短信异常,userId:"+afBorrowCashDo.getUserId()+",nowRepayAmount:"+nowRepayAmountStr+",notRepayMoney"+notRepayMoneyStr, e);
					}
					//add by chengkang 待添加还款成功短信 end
					
					//会对逾期的借款还款，向催收平台同步还款信息
					try {
						CollectionSystemReqRespBo respInfo = collectionSystemUtil.consumerRepayment(repayment.getRepayNo(), afBorrowCashDo.getBorrowNo(), repayment.getCardNumber(), repayment.getCardName(),DateUtil.formatDateTime(new Date()),tradeNo,repayment.getRepaymentAmount(), (bcashDo.getAmount().add(bcashDo.getRateAmount().add(bcashDo.getOverdueAmount().add(bcashDo.getSumRate().add(bcashDo.getSumOverdue())))).subtract(bcashDo.getRepayAmount()).setScale(2, RoundingMode.HALF_UP)), (bcashDo.getAmount().add(bcashDo.getRateAmount().add(bcashDo.getOverdueAmount().add(bcashDo.getSumRate().add(bcashDo.getSumOverdue())))).setScale(2, RoundingMode.HALF_UP)), bcashDo.getOverdueAmount(), bcashDo.getRepayAmount(),bcashDo.getRateAmount());
						logger.info("collection consumerRepayment req success, respinfo=",respInfo);
					}catch(Exception e){
						logger.error("向催收平台同步还款信息失败",e);
					}
					
					return 1l;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealRepaymentSucess error", e);
					return 0l;
				}
			}
		});
	}
	
//	private void increaseBorrowCashAccount(AfBorrowCashDo afBorrowCashDo,Long userId){
//		//提升借款额度
//		
//		BigDecimal borrowAmount = afBorrowCashDo.getAmount();
//		AfUserAccountDo accountBorrowDo= afUserAccountDao.getUserAccountInfoByUserId(userId);
//		BigDecimal accountBorrowAoumt = accountBorrowDo.getBorrowCashAmount();
//		AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashMoreAmount.getCode());
//		BigDecimal max = NumberUtil.objToBigDecimalDefault(resourceDo.getValue(), BigDecimal.ZERO);
//		BigDecimal addRate = NumberUtil.objToBigDecimalDefault(resourceDo.getValue1(), BigDecimal.ZERO);
//		BigDecimal addAmount = borrowAmount.multiply(addRate).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
//
//		if(max.compareTo(accountBorrowAoumt)>0&& StringUtils.equals(afBorrowCashDo.getType(), AfBorrowCashType.FOURTEEN.getName())) {
//			AfUserAccountDo accountChange = new AfUserAccountDo();
//			accountChange.setRid(accountBorrowDo.getRid());
//			BigDecimal borrowAccountNew = accountBorrowAoumt.add(addAmount);
//			accountChange.setBorrowCashAmount(borrowAccountNew.compareTo(max)>0?max:borrowAccountNew);
//			afUserAccountDao.updateOriginalUserAccount(accountChange);
//		}
//	}
	
	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type, BigDecimal amount, Long userId, Long repaymentId) {
		// 增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(repaymentId + "");
		accountLog.setType(type.getCode());
		return accountLog;
	}

	@Override
	public long dealRepaymentFail(String outTradeNo, String tradeNo) {
		AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
		if (YesNoStatus.YES.getCode().equals(repayment.getStatus())) {
			return 0l;
		}

		return dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), repayment.getRid());
	}

	long dealChangStatus(String outTradeNo, String tradeNo, String status, Long rid) {
		AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
		temRepayMent.setStatus(status);
		temRepayMent.setTradeNo(tradeNo);
		temRepayMent.setRid(rid);

		return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
	}

	@Override
	public AfRepaymentBorrowCashDo getLastRepaymentBorrowCashByBorrowId(Long borrowCashId) {
		return afRepaymentBorrowCashDao.getLastRepaymentBorrowCashByBorrowId(borrowCashId);
	}

	@Override
	public BigDecimal getRepaymentAllAmountByBorrowId(Long borrowId) {
		return afRepaymentBorrowCashDao.getRepaymentAllAmountByBorrowId(borrowId);
	}
	
	@Override
	public BigDecimal getRepayingTotalAmountByBorrowId(Long borrowId){
		return afRepaymentBorrowCashDao.getRepayingTotalAmountByBorrowId(borrowId);
	}

	@Override
	public String getCurrentLastRepayNo(String orderNoPre) {
		return afRepaymentBorrowCashDao.getCurrentLastRepayNo(orderNoPre);
	}
	
	@Override
	public String dealOfflineRepaymentSucess(final String repayNo,final String borrowNo,final String repayType,final String repayTime,final BigDecimal repayAmount,final BigDecimal restAmount,final String tradeNo,final String isBalance) {
		return transactionTemplate.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus status) {
				try {
					logger.info("dealOfflineRepaymentSucess begin repayNo="+repayNo+",borrowNo"+borrowNo+",repayType"+repayType+",repayTime"+repayTime
							+",repayAmount"+repayAmount+",restAmount"+restAmount+",tradeNo"+tradeNo+",isBalance"+isBalance);
					AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
					if(afBorrowCashDo==null){
						logger.error("dealOfflineRepaymentSucess fail,borrowcash not exist,borrowNo="+borrowNo);
						return FanbeiThirdRespCode.BORROW_CASH_NOT_EXISTS.getCode();
					}
					//校验是否已经还款完成
					if(AfBorrowCashStatus.finsh.getCode().equals(afBorrowCashDo.getStatus())){
						logger.error("dealOfflineRepaymentSucess fail,borrowcash have finished,id="+afBorrowCashDo.getRid());
						return FanbeiThirdRespCode.BORROW_CASH_HAVE_FINISHED.getCode();
					}
					
					Date currDate = new Date();
					Date gmtCreate = DateUtil.parseDateTimeShortExpDefault(repayTime,currDate);
					//还款方式解析
					OfflinePayType offlinePayType = OfflinePayType.findPayTypeByCode(repayType);
					//线下还款记录添加
					AfRepaymentBorrowCashDo repayment = new AfRepaymentBorrowCashDo(gmtCreate, currDate, "催收平台线下还款", repayNo, repayAmount, repayAmount, afBorrowCashDo.getRid(), repayNo, tradeNo,
							0L, BigDecimal.ZERO, BigDecimal.ZERO, AfBorrowCashRepmentStatus.YES.getCode(), afBorrowCashDo.getUserId(), "", offlinePayType==null?repayType:offlinePayType.getName(), BigDecimal.ZERO);
					afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
					
					BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(),afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
					BigDecimal haveRepayAmount = repayAmount.add(afBorrowCashDo.getRepayAmount());
					
					//更新对象
					AfBorrowCashDo bcashDo = new AfBorrowCashDo();
					bcashDo.setRid(afBorrowCashDo.getRid());
					bcashDo.setRepayAmount(haveRepayAmount);
					
					// 还款的时候 需要判断是否能还清利息 同时修改累计利息 
					BigDecimal tempRepayAmount = repayAmount;
					if (tempRepayAmount.compareTo(afBorrowCashDo.getRateAmount()) > 0) {
						bcashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), afBorrowCashDo.getRateAmount()));
						bcashDo.setRateAmount(BigDecimal.ZERO);
						tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getRateAmount());
					} else {
						bcashDo.setSumRate(BigDecimalUtil.add(bcashDo.getSumRate(), tempRepayAmount));
						bcashDo.setRateAmount(afBorrowCashDo.getRateAmount().subtract(tempRepayAmount));
						tempRepayAmount = BigDecimal.ZERO;
					}
					
					// 还款的时候 需要判断是否能还清滞纳金 同时修改累计滞纳金 start
					if (tempRepayAmount.compareTo(afBorrowCashDo.getOverdueAmount()) > 0) {
						bcashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount()));
						bcashDo.setOverdueAmount(BigDecimal.ZERO);
						tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getOverdueAmount());
					} else {
						bcashDo.setSumOverdue(BigDecimalUtil.add(bcashDo.getSumOverdue(), tempRepayAmount));
						bcashDo.setOverdueAmount(afBorrowCashDo.getOverdueAmount().subtract(tempRepayAmount));
						tempRepayAmount = BigDecimal.ZERO;
					}
					
					if(YesNoStatus.YES.getCode().equals(isBalance) || allAmount.compareTo(haveRepayAmount) <= 0){
						if(YesNoStatus.YES.getCode().equals(isBalance)){
							logger.info("dealOfflineRepaymentSucess isBalance yes process,borrowCashId="+afBorrowCashDo.getRid());
						}
						//平账处理或者非平账但是已还金额满足欠款
						Long userId = afBorrowCashDo.getUserId();
						AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
						//减少使用额度
						accountInfo.setUsedAmount(BigDecimalUtil.subtract(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount()));
						afUserAccountDao.updateOriginalUserAccount(accountInfo);
						//增加日志
						AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAYMENTCASH, 
								afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
						afUserAccountLogDao.addUserAccountLog(accountLog);
						
						bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
					}
					afBorrowCashService.updateBorrowCash(bcashDo);
					return FanbeiThirdRespCode.SUCCESS.getCode();
				}catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealOfflineRepaymentSucess error", e);
					return FanbeiThirdRespCode.FAILED.getCode();
				}
			}
		});
	}
}
