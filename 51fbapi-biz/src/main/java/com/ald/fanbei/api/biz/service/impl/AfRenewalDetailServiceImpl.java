package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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

import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;

/**
 * @类描述：
 * 
 * @author fumeiai 2017年5月19日 20:04:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRenewalDetailService")
public class AfRenewalDetailServiceImpl extends BaseService implements AfRenewalDetailService {
	@Resource
	UpsUtil upsUtil;
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

	@Override
	public Map<String, Object> createRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRenewalBorrowCashNo(now);
		final String payTradeNo = repayNo;

		String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;

		final AfRenewalDetailDo renewalDetail = buildRenewalDetailDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, borrow, cardId, payTradeNo, userId);
		Map<String, Object> map = new HashMap<String, Object>();
		afRenewalDetailDao.addRenewalDetail(renewalDetail);

		if (cardId == -1) {// 微信支付
			map = UpsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, actualAmount, PayOrderSource.RENEWAL_PAY.getCode());
		} else if (cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			UpsCollectRespBo respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(), bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, name, "02", UserAccountLogType.RENEWAL_PAY.getCode());
			if (respBo.isSuccess()) {
				dealChangStatus(payTradeNo, "", AfRenewalDetailStatus.PROCESS.getCode(), renewalDetail.getRid());
			} else {
				dealRenewalFail(payTradeNo, "");
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
		AfRenewalDetailDo afRenewalDetailDo = new AfRenewalDetailDo();
		afRenewalDetailDo.setStatus(status);
		afRenewalDetailDo.setTradeNo(tradeNo);
		afRenewalDetailDo.setRid(rid);
		return afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);
	}

	@Override
	public long dealRenewalFail(String outTradeNo, String tradeNo) {
		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
		if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
			return 0l;
		}
		AfUserDo userDo = afUserService.getUserById(afRenewalDetailDo.getUserId());
		pushService.repayRenewalFail(userDo.getUserName());

		return dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), afRenewalDetailDo.getRid());
	}

	@Override
	public long dealRenewalSucess(final String outTradeNo, final String tradeNo) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
					logger.info("afRenewalDetailDo=" + afRenewalDetailDo);
					if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
						return 0l;
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
					BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
					
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

					afBorrowCashDo.setRepayAmount(afBorrowCashDo.getRepayAmount().add(afRenewalDetailDo.getPriorInterest()).add(afRenewalDetailDo.getPriorOverdue()));// 累计已还款金额
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

					AfUserDo userDo = afUserService.getUserById(afBorrowCashDo.getUserId());
					pushService.repayRenewalSuccess(userDo.getUserName());
					
					//当续期成功时,同步逾期天数为0
					dealWithSynchronizeOverduedOrder(afBorrowCashDo);
					
					//TODO 返呗续期通知接口，向催收平台同步续期信息 add by chengkang begin
					try {
						riskUtil.Renewal(afBorrowCashDo.getBorrowNo(), temRenewalDetail.getPayTradeNo(), temRenewalDetail.getRenewalDay());
					}catch(Exception e){
						logger.error("向催收平台同步续期信息",e);
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
		riskUtil.batchSychronizeOverdueBorrow(orderNo, boList);
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

	private AfRenewalDetailDo buildRenewalDetailDo(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, String tradeNo, BigDecimal actualAmount, BigDecimal rebateAmount, Long borrowId, Long cardId, String payTradeNo, Long userId) {

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		AfResourceDo poundageResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_POUNDAGE);
		BigDecimal borrowCashPoundage = new BigDecimal(poundageResource.getValue());// 借钱手续费率（日）
		AfResourceDo baseBankRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BASE_BANK_RATE);
		BigDecimal baseBankRate = new BigDecimal(baseBankRateResource.getValue());// 央行基准利率

		//未还金额
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
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

		if (cardId == -2) {
			afRenewalDetailDo.setCardNumber("");
			afRenewalDetailDo.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		} else if (cardId == -1) {
			afRenewalDetailDo.setCardNumber("");
			afRenewalDetailDo.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		} else {
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
}
