package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.sun.org.apache.bcel.internal.generic.NEW;
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
import com.ald.fanbei.api.biz.bo.loan.LoanHomeInfoBo;
import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanProductService;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.Documents;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfLoanPeriodStatus;
import com.ald.fanbei.api.common.enums.AfLoanRejectType;
import com.ald.fanbei.api.common.enums.AfLoanReviewStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.LoanType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.UserAuthSceneStatus;
import com.ald.fanbei.api.common.enums.WeakRiskSceneType;
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
import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfLoanProductDo;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;



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
	private AfLoanRepaymentService afLoanRepaymentService;
	@Resource
	private AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	private AfUserAuthStatusService afUserAuthStatusService;
	@Resource
	private AfLoanProductService afLoanProductService;
	
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
	private AfUserAccountSenceDao afUserAccountSenceDao;
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
			this.doLoanCheck(bo);
			
			// 解析分期
			final ReqParam reqParam = bo.reqParam;
			String loanNo = generatorClusterNo.getLoanNo(new Date());
			final List<Object> objs = afLoanPeriodsService.resolvePeriods(reqParam.amount, userId, reqParam.periods, loanNo, bo.reqParam.prdType);
			final AfLoanDo loanDo = (AfLoanDo)objs.remove(0);
			final List<AfLoanPeriodsDo> periodDos = new ArrayList<>();
			for(Object o : objs) {
				periodDos.add((AfLoanPeriodsDo)o);
			}
			
			final AfUserBankcardDo bankCard = afUserBankcardDao.getUserMainBankcardByUserId(userId);
			
			// 数据入库
			loanDo.setAuAmount(bo.auAmount);
			this.saveLoanRecords(bo, loanDo, periodDos, bankCard);
			
			// 弱风控
			this.weakRiskCheck(bo, loanDo);
			
			AfUserAccountDo userAccount = afUserAccountDao.getUserAccountInfoByUserId(userId);
			// 调用UPS打款
			UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(bo.reqParam.amount,
					userAccount.getRealName(), bankCard.getCardNumber(), userId.toString(), bankCard.getMobile(),
					bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
					UserAccountLogType.LOAN.getCode(), loanDo.getRid().toString());
			loanDo.setTradeNoOut(upsResult.getOrderNo());
			if (!upsResult.isSuccess()) {
				dealLoanFail(loanDo, periodDos, upsResult.getRespCode());
				//审核通过，ups打款失败
				String title = "本次还款支付失败";
				String content = "您&bankName（&cardLastNo）未能成功接收款项，请添加其他银行卡后，联系客服进行更换4000025151";
				String bankNumber = bankCard.getCardNumber();
				String lastBankCode = bankNumber.substring(bankNumber.length() - 4);
				content = content.replace("&cardLastNo",lastBankCode);
				content = content.replace("&bankName",bankCard.getBankName());
				jpushService.pushUtil(title,bo.userName,content);
				smsUtil.sendBorrowPayMoneyFail(bo.userName);
				throw new FanbeiException(FanbeiExceptionCode.LOAN_UPS_DRIECT_FAIL);
			}
			loanDo.setStatus(AfLoanStatus.TRANSFERING.name());
			afLoanDao.updateById(loanDo);
			
			
			//贷款成功 通知用户
			try {
				String bankNumber = bankCard.getCardNumber();
				String lastBankCode = bankNumber.substring(bankNumber.length() - 4);
				smsUtil.sendloanCashCode(userAccount.getUserName(), lastBankCode);
				jpushService.pushUtil(Documents.LOAN_SUCC_TITLE, String.format(Documents.LOAN_SUCC_MSG, lastBankCode), userAccount.getUserName());
			}catch (Exception e) {
				logger.error("DoLoan success, notify user occur error!", e); //通知过程抛出任何异常捕获，不影响主流程
			}
		}finally {
			this.unlockLoan(userId);
		}
	}
	private void doLoanCheck(ApplyLoanBo bo){
		String prdType = bo.reqParam.prdType;
		Long userId = bo.userId;
		
		//检查是否已有有效贷款申请
		AfLoanDo lastLoanDo = afLoanDao.getLastByUserIdAndPrdType(userId, prdType);
		if(lastLoanDo != null) {
			String lastLoanStatus = lastLoanDo.getStatus();
			if(AfLoanStatus.TRANSFERING.name().equals(lastLoanStatus) 
					|| AfLoanStatus.TRANSFERRED.name().equals(lastLoanStatus) 
					|| AfLoanStatus.APPLY.name().equals(lastLoanStatus) ) {
				throw new FanbeiException(FanbeiExceptionCode.LOAN_REPEAT_APPLY);
			}
		}
		
		//自检是否放行贷款
		AfUserAccountSenceDo accScene = afUserAccountSenceService.getByUserIdAndScene(prdType, userId);
		AfLoanRejectType res = rejectCheck(userId, accScene, afLoanProductDao.getByPrdType(prdType), lastLoanDo);
		if(!res.equals(AfLoanRejectType.PASS)) {
			throw new FanbeiException(res.exceptionCode);
		}
		bo.auAmount = accScene.getAuAmount();
		
		//检查是否超贷
		BigDecimal usableAmount = accScene.getAuAmount().subtract(accScene.getUsedAmount());
		if(bo.reqParam.amount.compareTo(usableAmount) > 0) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_OVERFLOW);
		}
		
	}
	private void saveLoanRecords(final ApplyLoanBo bo, final AfLoanDo loanDo, 
				final List<AfLoanPeriodsDo> periodDos, final AfUserBankcardDo bankCard) {
		transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                try {
                	ReqParam reqParam = bo.reqParam;
                	
                	loanDo.setCardNo(bankCard.getCardNumber());
                	loanDo.setCardName(bankCard.getBankName());
                	loanDo.setAddress(reqParam.address);
                	loanDo.setProvince(reqParam.province);
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
            		for(AfLoanPeriodsDo o: periodDos) {
            			AfLoanPeriodsDo periodDo = (AfLoanPeriodsDo)o;
            			periodDo.setLoanId(loanId);
            			afLoanPeriodsDao.saveRecord(periodDo);
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
				WeakRiskSceneType.valueOf(bo.reqParam.prdType).getCode(),
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
		tarLoanDo.setGmtReview(new Date());
		if(verifyBo.isSuccess()) {
			tarLoanDo.setReviewStatus(AfLoanReviewStatus.AGREE.name());
			tarLoanDo.setReviewDetails("");
			afLoanDao.updateById(tarLoanDo);
		}else {
			tarLoanDo.setStatus(AfLoanStatus.CLOSED.name());
			tarLoanDo.setReviewStatus(AfLoanReviewStatus.REFUSE.name());
			tarLoanDo.setReviewDetails(verifyBo.getMsg());
			afLoanDao.updateById(tarLoanDo);
			//审核失败
			jpushService.dealBorrowCashApplyFail(bo.userName, new Date());
			throw new FanbeiException(FanbeiExceptionCode.LOAN_RISK_REFUSE);
		}
		
	}
	
	@Override
	public void dealLoanSucc(Long loanId, String tradeNoOut) {
		final AfLoanDo loanDo = afLoanDao.getById(loanId);
		String status = loanDo.getStatus();
		if(AfLoanStatus.TRANSFERRED.name().equals(status)) {//已经处理过，防重复回调
			logger.warn("DealLoanSucc, transfer has succ, repeat UPS invoke! loanId="+loanId+",tradeNoOut="+tradeNoOut);
			return;
		}
		
		if(AfLoanStatus.CLOSED.name().equals(status)) { // 已失败订单，但UPS仍回调成功，日志打点记录
			logger.warn("DealLoanSucc, transfer has fail, but still callback! original status= "+status+",loanId="+loanId+",tradeNoOut="+tradeNoOut);
		}
		
		Date cur = new Date();
		loanDo.setTradeNoOut(tradeNoOut);
		loanDo.setStatus(AfLoanStatus.TRANSFERRED.name());
		loanDo.setArrivalAmount(loanDo.getAmount());
		loanDo.setGmtArrival(cur);
		transactionTemplate.execute(new TransactionCallback<Long>() { public Long doInTransaction(TransactionStatus status) {
            try {
            	afLoanDao.updateById(loanDo);
        		afUserAccountSenceDao.updateUsedAmount(loanDo.getPrdType(), loanDo.getUserId(), loanDo.getAmount());
                return 1L;
            } catch (Exception e) {
                logger.error("dealLoanSucc update db error", e);
                throw e;
            }
		}});
	}
	
	@Override
	public void dealLoanFail(Long loanId, String tradeNoOut, String msgOut) {
		AfLoanDo loanDo = afLoanDao.getById(loanId);
		loanDo.setTradeNoOut(tradeNoOut);
		List<AfLoanPeriodsDo> periodDos = afLoanPeriodsDao.listByLoanId(loanDo.getRid());
		
		dealLoanFail(loanDo, periodDos, msgOut);
	}
	private void dealLoanFail(AfLoanDo loanDo, List<AfLoanPeriodsDo> periodDos, String msg) {
		Date cur = new Date();
		loanDo.setStatus(AfLoanStatus.CLOSED.name());
		loanDo.setRemark("UPS打款失败，"+msg);
		loanDo.setGmtClose(cur);
		afLoanDao.updateById(loanDo);
	}
	
	@Override
	@Deprecated
	public BigDecimal getUserLayDailyRate(Long userId, String prdType) {
		try {
			String key = Constants.CACHEKEY_USER_LAY_DAILY_RATE + prdType + ":" + userId;
			BigDecimal dailyRate = (BigDecimal)bizCacheUtil.getObject(key);
			
			if(dailyRate == null) {
				dailyRate = BigDecimal.valueOf(0.0002);// ASK risk 分层利率 暂时写死
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
			
			String prdType = prdDo.getPrdType();
			
			// 处理 配置 信息
			BigDecimal maxAuota = prdDo.getMaxAmount();
			AfUserAccountSenceDo accScene = afUserAccountSenceService.getByUserIdAndScene(prdType, userAccount.getUserId());
			BigDecimal auAmount = BigDecimal.ZERO;
			BigDecimal usableAmount = BigDecimal.ZERO;
			if(accScene != null) {
				auAmount = accScene.getAuAmount();
				usableAmount = auAmount.subtract(accScene.getUsedAmount());
			}
			
			maxAuota = maxAuota.compareTo(usableAmount) < 0 ? maxAuota : usableAmount;
			bo.maxQuota = maxAuota;
			bo.minQuota = prdDo.getMinAmount();
			
			bo.loanRates = afLoanProductService.listByPrdType(prdDo.getPrdType());
			
			bo.periods = prdDo.getPeriods();
			bo.prdName = prdDo.getName();
			bo.prdType = prdType;
			
			AfLoanDo lastLoanDo = afLoanDao.getLastByUserIdAndPrdType(userAccount.getUserId(), prdType);
			this.dealHomeLoginLoan(bo, lastLoanDo);// 处理 贷款 信息
			
			AfLoanRejectType res = rejectCheck(userAccount.getUserId(), accScene, prdDo, lastLoanDo);
			if(!AfLoanRejectType.PASS.equals(res)) {
				bo.maxQuota = prdDo.getMaxAmount();
			}
			bo.rejectCode = res.name();
			
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
			|| AfLoanStatus.CLOSED.name().equals(status)) {
			bo.hasLoan = false;
			return;
		}
		AfLoanRepaymentDo processLoanRepayment = afLoanRepaymentService.getProcessLoanRepaymentByLoanId(lastLoanDo.getRid());
		if(processLoanRepayment != null) {
			status = AfLoanStatus.REPAYING.name();
			bo.repayingAmount = processLoanRepayment.getRepayAmount();
		}
		bo.loanStatus = status;
		
		bo.hasLoan = true;
		bo.loanId = lastLoanDo.getRid();
		bo.loanAmount = lastLoanDo.getAmount();
		bo.loanArrivalAmount = lastLoanDo.getArrivalAmount();
		bo.loanGmtApply = lastLoanDo.getGmtCreate();
		
    	List<AfLoanPeriodsDo> ps = afLoanPeriodsDao.listCanRepayPeriods(lastLoanDo.getRid());
    	boolean isOverdue = false;
    	String periodIds = new String("");
    	BigDecimal restAmount = BigDecimal.ZERO;
    	BigDecimal periodsOverdueAmount = BigDecimal.ZERO;
    	Date lastGmtPlanRepay = null;
    	String periodsStatus = AfLoanPeriodStatus.FINISHED.name();
    	if(ps.size() > 0) {
    		for(AfLoanPeriodsDo p : ps) {
        		periodIds += p.getRid() + ",";
        		restAmount = restAmount.add(afLoanPeriodsService.calcuRestAmount(p));
        		
        		if(YesNoStatus.YES.getCode().equals(p.getOverdueStatus())) {
        			isOverdue = true;
        			periodsOverdueAmount = periodsOverdueAmount.add(p.getOverdueAmount());
        		}
        		periodsStatus = AfLoanPeriodStatus.AWAIT_REPAY.name();
        	}
    		periodIds = periodIds.substring(0, periodIds.length()-1);
    		lastGmtPlanRepay = ps.get(ps.size()-1).getGmtPlanRepay();
    	}
    	
    	// 查询是否有处理中的还款
    	if(processLoanRepayment != null) {
    		periodsStatus = AfLoanPeriodStatus.REPAYING.name();
    	}
    	
    	bo.isOverdue = isOverdue;
    	bo.periodIds = periodIds;
    	bo.periodsRestAmount = restAmount;
    	bo.periodsOverdueAmount = periodsOverdueAmount;
    	bo.periodsLastGmtPlanRepay = lastGmtPlanRepay;
    	bo.periodsStatus = periodsStatus;
    	
    	BigDecimal unChargeAmount = BigDecimal.ZERO;
    	List<AfLoanPeriodsDo> unps = afLoanPeriodsDao.listUnChargeRepayPeriods(lastLoanDo.getRid());
		for(AfLoanPeriodsDo p : unps) {
			unChargeAmount = unChargeAmount.add(p.getAmount());
    	}
    	bo.periodsUnChargeAmount = unChargeAmount;
		
	}
	/**
	 * 处理非登陆场景下首页信息
	 */
	private List<LoanHomeInfoBo> dealHomeUnlogin(List<AfLoanProductDo> prdDos){
		List<LoanHomeInfoBo> infoBos = new ArrayList<>();
		for(AfLoanProductDo prdDo : prdDos) {
			LoanHomeInfoBo bo = new LoanHomeInfoBo();
			bo.rejectCode = AfLoanRejectType.PASS.name();

			bo.loanRates = afLoanProductService.listByPrdType(prdDo.getPrdType());
			bo.maxQuota = prdDo.getMaxAmount();
			bo.minQuota = prdDo.getMinAmount();
			
			if(YesNoStatus.NO.getCode().equals(prdDo.getSwitch())) {//贷款总开关
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
		private AfLoanRejectType rejectCheck(Long userId, AfUserAccountSenceDo accScene, AfLoanProductDo prdDo, AfLoanDo lastLoanDo) {
		//贷款总开关
		if(YesNoStatus.NO.getCode().equals(prdDo.getSwitch())) {
			return AfLoanRejectType.SWITCH_OFF;
		}
		
		AfUserAuthStatusDo au = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, prdDo.getPrdType());
		if(accScene == null || au == null) {
			return AfLoanRejectType.NO_AUTHZ;
		}
		String authStatus = au.getStatus();
		
		if(UserAuthSceneStatus.CHECKING.getCode().equals(authStatus)) {
			return AfLoanRejectType.AUTHING;
		}
		
		if(UserAuthSceneStatus.FAILED.getCode().equals(authStatus)) {
			return AfLoanRejectType.NO_PASS_STRO_RISK;
		}
		
		if(accScene.getScene().equals(LoanType.BLD_LOAN.getName())) {
			if(UserAuthSceneStatus.NO.getCode().equals(authStatus)) {
				AfUserAuthDo afAuthInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
				if(afAuthInfo != null && UserAuthSceneStatus.YES.getCode().equals(afAuthInfo.getBasicStatus())) {
					return AfLoanRejectType.GO_BLD_AUTH;
				} else {
					return AfLoanRejectType.NO_AUTHZ;
				}
			}
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
		if(prdDo.getMinAmount().compareTo(accScene.getAuAmount()) > 0) {
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

	@Override
	public AfLoanDo selectById(Long id){
		return afLoanDao.selectById(id);
	}

}