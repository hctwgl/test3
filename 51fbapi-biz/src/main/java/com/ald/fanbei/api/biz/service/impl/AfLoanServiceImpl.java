package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo;
import com.ald.fanbei.api.biz.bo.loan.ApplyLoanBo.ReqParam;
import com.ald.fanbei.api.biz.bo.loan.LoanDBCfgBo;
import com.ald.fanbei.api.biz.bo.loan.LoanHomeInfoBo;
import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.Documents;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfLoanRejectType;
import com.ald.fanbei.api.common.enums.AfLoanReviewStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.AfLoanProductDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfLoanProductDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;



/**
 * 贷款业务ServiceImpl
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanService")
public class AfLoanServiceImpl extends ParentServiceImpl<AfLoanDo, Long> implements AfLoanService {
    @Resource
    private AfResourceService afResourceService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private AfLoanPeriodsService afLoanPeriodsService;
	
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private UpsUtil upsUtil;
	@Resource
	private RiskUtil riskUtil;
	@Resource
	private SmsUtil smsUtil;
	@Resource
	private JpushService jpushService;
	
	@Resource
    private AfLoanDao afLoanDao;
	@Resource
    private AfLoanPeriodsDao afLoanPeriodsDao;
	@Resource
    private AfLoanProductDao afLoanProductDao;
	@Resource
	private AfUserAccountDao afUserAccountDao;
	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	@Resource
	private AfBorrowDao afBorrowDao;
	@Resource
	private AfResourceDao afResourceDao;
	
	@Resource
    private GeneratorClusterNo generatorClusterNo;
	@Resource
	private TransactionTemplate transactionTemplate;
	@Resource
	private RedisTemplate<String, ?> redisTemplate;
	
	@Override
	public List<LoanHomeInfoBo> getHomeInfo(Long userId){
		AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
		
		List<AfLoanProductDo> prdDos = afLoanProductDao.getAll();
		if(userAccount != null) {
			return dealHomeLogin(userAccount, prdDos);
		}else{
			return dealHomeUnlogin(prdDos);
		}
	}
	
	@Override
	public void doLoan(ApplyLoanBo bo) {
		Long userId = bo.userId;
		this.lockLoan(userId);
		
		try {
			//检查是否已有有效贷款申请
			AfLoanDo lastLoanDo = afLoanDao.fetchLastByUserId(userId);
			String lastLoanStatus = lastLoanDo.getStatus();
			if(AfLoanStatus.TRANSFERING.name().equals(lastLoanStatus) 
					|| AfLoanStatus.TRANSFERRED.name().equals(lastLoanStatus) 
					|| AfLoanStatus.APPLY.name().equals(lastLoanStatus) ) {
				throw new FanbeiException(FanbeiExceptionCode.LOAN_REPEAT_APPLY);
			}
			
			//自检是否放行贷款
			AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
			AfLoanRejectType res = rejectCheck(userAccount, lastLoanDo, getDBCfg(bo.reqParam.prdType));
			if(!res.equals(AfLoanRejectType.PASS)) {
				throw new FanbeiException(res.exceptionCode);
			}
			
			// 解析分期
			final ReqParam reqParam = bo.reqParam;
			String loanNo = generatorClusterNo.getLoanNo(new Date());
			final List<Object> objs = afLoanPeriodsService.resolvePeriods(reqParam.amount, userId, reqParam.periods, loanNo, reqParam.prdType);
			final AfLoanDo loanDo = (AfLoanDo)objs.remove(0);
			final List<AfLoanPeriodsDo> periodDos = new ArrayList<>();
			for(Object o : objs) {
				periodDos.add((AfLoanPeriodsDo)o);
			}
			
			final AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(bo.reqParam.cardId);
			
			// 数据入库
			this.saveLoanRecords(bo, loanDo, periodDos, bank);
			
			// 弱风控
			this.weakRiskCheck(bo, loanDo);
			
			// 调用UPS打款
			UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(bo.reqParam.amount,
					bank.getRealName(), bank.getCardNumber(), userId.toString(), bank.getMobile(),
					bank.getBankName(), bank.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
					UserAccountLogType.LOAN.getCode(), loanNo);
			if (!upsResult.isSuccess()) {
				loanDo.setTradeNoOut(upsResult.getOrderNo());
				dealLoanFail(loanDo, periodDos, upsResult.getRespCode());
				throw new FanbeiException(FanbeiExceptionCode.LOAN_UPS_DRIECT_FAIL);
			}
			
			//贷款成功 通知用户
			try {
				String bankNumber = bank.getCardNumber();
				String lastBankCode = bankNumber.substring(bankNumber.length() - 4);
				smsUtil.sendSmsToDhst(userAccount.getUserName(), String.format(Documents.LOAN_SUCC_SMS_MSG, lastBankCode));
				jpushService.pushUtil(Documents.LOAN_SUCC_TITLE, String.format(Documents.LOAN_SUCC_MSG, lastBankCode), userAccount.getUserName());
			}catch (Exception e) {
				logger.error("DoLoan success, notify user occur error!", e); //通知过程抛出任何异常捕获，不影响主流程
			}
		}finally {
			this.unlockLoan(userId);
		}
	}
	private void saveLoanRecords(final ApplyLoanBo bo, final AfLoanDo loanDo, 
				final List<AfLoanPeriodsDo> periodDos, final AfUserBankDto bank) {
		transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                try {
                	ReqParam reqParam = bo.reqParam;
                	
                	loanDo.setCardNo(bank.getCardNumber());
                	loanDo.setCardName(bank.getBankName());
                	loanDo.setAddress(reqParam.address);
                	loanDo.setCity(reqParam.city);
                	loanDo.setCounty(reqParam.county);
                	loanDo.setLatitude(new BigDecimal(reqParam.latitude));
                	loanDo.setLongitude(new BigDecimal(reqParam.longitude));
                	loanDo.setRemark(reqParam.remark);
                	loanDo.setLoanRemark(reqParam.loanRemark);
                	loanDo.setRepayRemark(reqParam.repayRemark);
            		loanDo.setAppName(reqParam.appName);
            		afLoanDao.saveRecord(loanDo);
            		
            		Long loanId = loanDo.getRid();
            		for(Object o: periodDos) {
            			AfLoanPeriodsDo periodDo = (AfLoanPeriodsDo)o;
            			periodDo.setLoanId(loanId);
            			afLoanPeriodsDao.saveRecord(periodDo);
            			periodDos.add(periodDo);
            		}
            		
                    return 1L;
                } catch (Exception e) {
                    logger.error("saveLoanRecords,DB error", e);
                    throw e;
                }
            }
        });
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void weakRiskCheck(ApplyLoanBo bo, AfLoanDo tarLoanDo) {
		Long userId = bo.userId;
		String cardNo = tarLoanDo.getCardNo();
		
		String riskOrderNo = riskUtil.getOrderNo("vefy", cardNo.substring(cardNo.length() - 4, cardNo.length()));
		
		HashMap<String, HashMap> riskDataMap = new HashMap();
        HashMap summaryData = afBorrowDao.getUserSummary(userId);
        riskDataMap.put("summaryData", summaryData);
        riskDataMap.put("summaryOrderData", new HashMap<>());
		RiskVerifyRespBo verifyBo = riskUtil.verifyNew(
				userId.toString(),
				tarLoanDo.getLoanNo(), 
				tarLoanDo.getPrdType(),
				"50", 
				tarLoanDo.getCardNo(), 
				bo.reqParam.appType, 
				bo.reqParam.ip, 
				bo.reqParam.blackBox, 
				riskOrderNo, 
				bo.userName, 
				tarLoanDo.getAmount(), 
				tarLoanDo.getTotalInterestFee().add(tarLoanDo.getTotalServiceFee()), 
				DateUtil.getNow(),
				"贷款", 
				StringUtils.EMPTY, 
				null, null, 0l,
				tarLoanDo.getCardName(), 
				null, "", 
				riskDataMap,
				bo.reqParam.bqsBlackBox,
				null);
		
		tarLoanDo.setRiskNo(verifyBo.getOrderNo());
		if(verifyBo.isSuccess()) {
			tarLoanDo.setReviewStatus(AfLoanReviewStatus.AGREE.name());
			tarLoanDo.setReviewDetails("");
		}else {
			tarLoanDo.setStatus(AfLoanStatus.CLOSED.name());
			tarLoanDo.setReviewStatus(AfLoanReviewStatus.REFUSE.name());
			tarLoanDo.setReviewDetails(verifyBo.getMsg());
			afLoanDao.updateById(tarLoanDo);
			
			throw new FanbeiException(FanbeiExceptionCode.LOAN_RISK_REFUSE);
		}
		
	}
	
	@Override
	public void dealLoanSucc(String loanNo, String tradeNoOut) {
		AfLoanDo loanDo = afLoanDao.getByLoanNo(loanNo);
		String status = loanDo.getStatus();
		if(AfLoanStatus.TRANSFERRED.name().equals(status)) {//已经处理过，防重复回调
			logger.warn("DealLoanSucc, transfer has succ, repeat UPS invoke! loanNo="+loanNo+",tradeNoOut="+tradeNoOut);
			return;
		}
		
		if(AfLoanStatus.CLOSED.name().equals(status) 
					|| AfLoanStatus.TRANSFER_FAIL.name().equals(status)) { // 已失败订单，但UPS仍回调成功，日志打点记录
			logger.warn("DealLoanSucc, transfer has fail, but still callback! original status= "+status+",loanNo="+loanNo+",tradeNoOut="+tradeNoOut);
		}
		
		Date cur = new Date();
		loanDo.setTradeNoOut(tradeNoOut);
		loanDo.setStatus(AfLoanStatus.TRANSFERRED.name());
		loanDo.setArrivalAmount(loanDo.getAmount());
		loanDo.setGmtArrival(cur);
		afLoanDao.updateById(loanDo);
	}
	
	@Override
	public void dealLoanFail(String loanNo, String tradeNoOut, String msgOut) {
		AfLoanDo loanDo = afLoanDao.getByLoanNo(loanNo);
		loanDo.setTradeNoOut(tradeNoOut);
		List<AfLoanPeriodsDo> periodDos = afLoanPeriodsDao.listByLoanId(loanDo.getRid());
		
		dealLoanFail(loanDo, periodDos, msgOut);
	}
	private void dealLoanFail(AfLoanDo loanDo, List<AfLoanPeriodsDo> periodDos, String msg) {
		Date cur = new Date();
		loanDo.setStatus(AfLoanStatus.TRANSFER_FAIL.name());
		loanDo.setGmtClose(cur);
		afLoanDao.updateById(loanDo);
	}
	
	@Override
	public LoanDBCfgBo getDBCfg(String prdType) {
		LoanDBCfgBo bo = new LoanDBCfgBo();
		return bo;
	}
	
	@Override
	public BigDecimal getUserLayDailyRate(Long userId, String prdType) {
		try {
			String key = Constants.CACHEKEY_USER_LAY_DAILY_RATE + prdType + ":" + userId;
			BigDecimal dailyRate = (BigDecimal)bizCacheUtil.getObject(key);
			
			if(dailyRate == null) {
				dailyRate = BigDecimal.valueOf(0.0002);// TODO ASK risk 分层利率 暂时写死
				bizCacheUtil.saveObject(key, dailyRate, Constants.SECOND_OF_AN_HOUR_INT);
			}
			
			return dailyRate;
		} catch (Exception e) {
			throw new FanbeiException("getUserLayRate error!", e);
		}
	}
	
	/**
	 * 处理登陆场景下首页信息
	 */
	private List<LoanHomeInfoBo> dealHomeLogin(AfUserAccountDo userAccount, List<AfLoanProductDo> prdDos) {
		List<LoanHomeInfoBo> infoBos = new ArrayList<>();
		for(AfLoanProductDo prdDo : prdDos) {
			LoanHomeInfoBo bo = new LoanHomeInfoBo();
			bo.isLogin = true;
			
			String prdType = prdDo.getPrdType();
			LoanDBCfgBo loanCfg = this.getDBCfg(prdType);
			
			// 处理 配置 信息
			BigDecimal maxAuota = loanCfg.maxQuota;
			// 获取对应贷款产品额度 af_user_account_sence 中获取 TODO
			BigDecimal usableAmount = null;
			
			maxAuota = maxAuota.compareTo(usableAmount) < 0 ? maxAuota : usableAmount;
			bo.maxQuota = maxAuota;
			bo.minQuota = loanCfg.minQuota;
			
			bo.interestRate = new BigDecimal(loanCfg.interestRate);
			BigDecimal userLayDailyRate = getUserLayDailyRate(userAccount.getUserId(), prdType);
			BigDecimal userLayRate = userLayDailyRate.multiply(AfLoanPeriodsServiceImpl.DAYS_OF_YEAR);
			bo.poundageRate = userLayRate.subtract(bo.interestRate);
			bo.overdueRate = new BigDecimal(loanCfg.overdueRate);
			
			bo.periods = prdDo.getPeriods();
			bo.prdName = prdDo.getName();
			bo.prdType = prdType;
			
			AfLoanDo lastLoanDo = afLoanDao.fetchLastByUserId(userAccount.getUserId());
			this.dealHomeLoginLoan(bo, lastLoanDo);// 处理 贷款 信息
			
			bo.rejectCode = rejectCheck(userAccount, lastLoanDo, loanCfg).name();
			
			infoBos.add(bo);
		}
		
		return infoBos;
	}
	private void dealHomeLoginLoan(LoanHomeInfoBo bo, AfLoanDo lastLoanDo) {
		if(lastLoanDo == null) {
			bo.hasLoan = false;
			return;
		}
		
		String status = lastLoanDo.getStatus();
		if(AfLoanStatus.FINISHED.name().equals(status)
			|| AfLoanStatus.TRANSFER_FAIL.name().equals(status)
			|| AfLoanStatus.CLOSED.name().equals(status)) {
			bo.hasLoan = false;
			return;
		}
		
		bo.hasLoan = true;
		bo.loanId = lastLoanDo.getRid();
		bo.loanStatus = status;
		bo.loanAmount = lastLoanDo.getAmount();
		bo.loanArrivalAmount = lastLoanDo.getArrivalAmount();
		bo.loanGmtApply = lastLoanDo.getGmtCreate();
		
		// 处理贷款当期
		AfLoanPeriodsDo activePeriod = afLoanPeriodsDao.getLastActivePeriodByLoanId(lastLoanDo.getRid());
		bo.curPeriodId = activePeriod.getRid();
		bo.curPeriodAmount = activePeriod.getAmount();
		bo.curPeriodRestAmount = afLoanPeriodsService.calcuRestAmount(activePeriod);
		bo.curPeriodGmtPlanRepay = activePeriod.getGmtPlanRepay();
		if(activePeriod.getOverdueAmount().compareTo(BigDecimal.ZERO) > 0) { 
			bo.isOverdue = true;
		}else {
			bo.isOverdue = false;
		}
	}
	/**
	 * 处理非登陆场景下首页信息
	 */
	private List<LoanHomeInfoBo> dealHomeUnlogin(List<AfLoanProductDo> prdDos){
		List<LoanHomeInfoBo> infoBos = new ArrayList<>();
		for(AfLoanProductDo prdDo : prdDos) {
			LoanDBCfgBo loanCfg = getDBCfg(prdDo.getPrdType());
			
			LoanHomeInfoBo bo = new LoanHomeInfoBo();
			bo.rejectCode = AfLoanRejectType.PASS.name();
			bo.isLogin = false;

			bo.interestRate = new BigDecimal(loanCfg.interestRate);
			bo.poundageRate = new BigDecimal(loanCfg.poundageRate);
			bo.overdueRate = new BigDecimal(loanCfg.overdueRate);
			bo.maxQuota = loanCfg.maxQuota;
			bo.minQuota = loanCfg.minQuota;
			
			if(YesNoStatus.YES.getCode().equals(loanCfg.switch_)) {//贷款总开关
				bo.rejectCode = AfLoanRejectType.SWITCH_OFF.name();
			}
			
			infoBos.add(bo);
		}
		return infoBos;
	}
	
	/**
	 * 检查用户是否可以进行贷款行为，只有返回PASS才可以放行
	 * @param userAccount
	 * @param loanCfg
	 * @return
	 */
	private AfLoanRejectType rejectCheck(AfUserAccountDo userAccount, AfLoanDo lastLoanDo, LoanDBCfgBo loanCfg) {
		//贷款总开关
		if(YesNoStatus.YES.getCode().equals(loanCfg.switch_)) {
			return AfLoanRejectType.SWITCH_OFF;
		}
		
		// TODO 检查是否认证过 af_user_auth_status中获取
		
		
		//检查强风控
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userAccount.getUserId());
		if(RiskStatus.NO.getCode().equals(afUserAuthDo.getRiskStatus())) { 
			return AfLoanRejectType.NO_PASS_STRO_RISK;
		}
		
		// 检查上笔贷款
		if (lastLoanDo != null 
				&& AfLoanStatus.CLOSED.name().equals(lastLoanDo.getStatus()) 
				&& AfLoanReviewStatus.REFUSE.name().equals(lastLoanDo.getReviewStatus()) ) {
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.RiskManagementBorrowcashLimit.getCode(), AfResourceSecType.RejectTimePeriod.getCode());
			if (afResourceDo != null && AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())) {
				Integer rejectTimePeriod = NumberUtil.objToIntDefault(afResourceDo.getValue1(), 0);
				Date desTime = DateUtil.addDays(lastLoanDo.getGmtCreate(), rejectTimePeriod);
				if (DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(desTime), DateUtil.getToday()) < 0) {// 风控拒绝日期内
					return AfLoanRejectType.NO_PASS_WEAK_RISK;
				}
			}
		}
		
		// 检查额度
		if(loanCfg.minQuota.compareTo(userAccount.getAuAmount()) > 0) {
			return AfLoanRejectType.QUOTA_TOO_SMALL;
		}
		
		return AfLoanRejectType.PASS;
	}
	
	/**
	 * 同一时刻每个用户只允许发生一笔借款操作
	 */
	private void lockLoan(Long userId) {
		String key = "LOAN_LOCK_" + userId;
        long count = redisTemplate.opsForValue().increment(key, 1);
        if (count > 1) {
            throw new FanbeiException(FanbeiExceptionCode.LOAN_CONCURRENT_LIMIT);
        }
        redisTemplate.expire(key, 30, TimeUnit.SECONDS);
	}	
	private void unlockLoan(Long userId) {
		String key = "LOAN_LOCK_" + userId;
		redisTemplate.delete(key);
	}
	
	@Override
	public BaseDao<AfLoanDo, Long> getDao() {
		return afLoanDao;
	}

}