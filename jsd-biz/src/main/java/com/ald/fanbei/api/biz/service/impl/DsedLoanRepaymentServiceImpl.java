package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.KuaijieDsedLoanBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.XgxyRepayBo;
import com.ald.fanbei.api.biz.bo.assetpush.ModifiedBorrowInfoVo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;
import com.ald.fanbei.api.biz.service.DsedUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.DsedLoanPeriodStatus;
import com.ald.fanbei.api.common.enums.DsedLoanRepaymentStatus;
import com.ald.fanbei.api.common.enums.DsedLoanStatus;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.DsedLoanDao;
import com.ald.fanbei.api.dal.dao.DsedLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.DsedLoanRepaymentDao;
import com.ald.fanbei.api.dal.dao.DsedUserBankcardDao;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.timevale.esign.sdk.tech.service.impl.b;

import net.sf.json.JSONArray;


/**
 * 都市易贷借款还款表ServiceImpl
 *
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@SuppressWarnings("rawtypes")
@Service("dsedLoanRepaymentService")
public class DsedLoanRepaymentServiceImpl  extends DsedUpsPayKuaijieServiceAbstract implements DsedLoanRepaymentService {

	private static final Logger logger = LoggerFactory.getLogger(DsedLoanRepaymentServiceImpl.class);

	@Resource
	private DsedLoanRepaymentDao dsedLoanRepaymentDao;
	@Resource
	private DsedUserBankcardDao dsedUserBankcardDao;
	@Resource
	private RedisTemplate<String, ?> redisTemplate;
	@Resource
	private GeneratorClusterNo generatorClusterNo;
	@Resource
	UpsUtil upsUtil;
	@Resource
	private TransactionTemplate transactionTemplate;
	@Resource
	DsedLoanPeriodsDao dsedLoanPeriodsDao;
	@Resource
	DsedLoanDao dsedLoanDao;
	@Resource
	private CollectionSystemUtil collectionSystemUtil;
	@Resource
	DsedUserService dsedUserService;
	@Resource
	DsedNoticeRecordService dsedNoticeRecordService;
	@Resource
	XgxyUtil xgxyUtil;
	@Resource
	DsedUserDao dsedUserDao;
	@Resource
	DsedContractPdfDao dsedContractPdfDao;

	private static String collectRiskToken = "eyJhbGciOiJIUzI1NiIsImNvbXBhbnlJZCI6MywiYiI6MX0.eyJhdWQiOiJhbGQiLCJpc3MiOiJBTEQiLCJpYXQiOjE1MzAxNzI3MzB9.-ZCGIOHgHnUbtJoOChHSi2fFj_XHnIDJk3bF1zrGLSk";

	private static String nofityRiskToken = "eyJhbGciOiJIUzI1NiIsImNvbXBhbnlJZCI6M30.eyJhdWQiOiIzIiwiaXNzIjoiQUxEIiwiaWF0IjoxNTMxODgwNjE5fQ.hU2GhPAbTKTXdVHpLscbjxJ7pc710jNdsxoteipwdMs";


	DsedLoanPeriodsService dsedLoanPeriodsService;
	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;

	@Override
	public DsedLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId){
		return dsedLoanRepaymentDao.getProcessLoanRepaymentByLoanId(loanId);
	}

	@Override
	public DsedLoanRepaymentDo getProcessingRepayment(Long loanId, Integer nper) {
		return dsedLoanRepaymentDao.getProcessingRepayment(loanId,nper);
	}

	@Override
	protected void kuaijieConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {

	}

	@Override
	protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo)
	{
		KuaijieDsedLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieDsedLoanBo.class);
		if (kuaijieLoanBo.getRepayment() != null) {
			changLoanRepaymentStatus(null, DsedLoanRepaymentStatus.SMS.name(), kuaijieLoanBo.getRepayment().getRid());
		}
	}

	@Override
	protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		KuaijieDsedLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieDsedLoanBo.class);
		if (kuaijieLoanBo.getRepayment() != null) {
			loanRepaymentStatus(null, DsedLoanRepaymentStatus.PROCESSING.name(), kuaijieLoanBo.getRepayment().getRid());
		}
		return getResultMap(kuaijieLoanBo.getBo(),respBo);
	}

	private Map<String, Object> getResultMap(LoanRepayBo bo, UpsCollectRespBo respBo)
	{
		Map<String, Object> data = Maps.newHashMap();
		data.put("rid", bo.loanId);
		data.put("amount", bo.amount.setScale(2, RoundingMode.HALF_UP));
		data.put("gmtCreate", new Date());
		data.put("status", DsedLoanRepaymentStatus.SUCC.name());
		data.put("actualAmount", bo.amount);
		data.put("cardName", bo.cardName);
		data.put("bankCardNumber", bo.cardNo);
		data.put("repayNo", bo.tradeNo);
		data.put("jfbAmount", BigDecimal.ZERO);
		if(respBo!=null)
		{
			data.put("resp", respBo);
			data.put("outTradeNo", respBo.getTradeNo());
		}

		return data;
	}

	@Override
	protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
		logger.info("payBizObject="+payBizObject+",payTradeNo="+payTradeNo);
		if (StringUtils.isNotBlank(payBizObject)) {
			// 处理业务数据
			dealRepaymentFail(payTradeNo, respBo.getTradeNo(), true, errorMsg);
		} else {
			// 未获取到缓存数据，支付订单过期
			throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
		}
	}


	@Override
	protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
		KuaijieDsedLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieDsedLoanBo.class);
		if (kuaijieLoanBo.getRepayment() != null) {
			changLoanRepaymentStatus(null, DsedLoanRepaymentStatus.PROCESSING.name(), kuaijieLoanBo.getRepayment().getRid());
		}
	}

	/**
	 * 计算本期需还金额
	 */
	@Override
	public BigDecimal calculateRestAmount(DsedLoanPeriodsDo dsedLoanPeriodsDo) {
		BigDecimal restAmount = BigDecimal.ZERO;
		restAmount = BigDecimalUtil.add(restAmount,dsedLoanPeriodsDo.getAmount(),
				dsedLoanPeriodsDo.getRepaidInterestFee(),dsedLoanPeriodsDo.getInterestFee(),
				dsedLoanPeriodsDo.getServiceFee(),dsedLoanPeriodsDo.getRepaidServiceFee(),
				dsedLoanPeriodsDo.getOverdueAmount(),dsedLoanPeriodsDo.getRepaidOverdueAmount())
				.subtract(dsedLoanPeriodsDo.getRepayAmount());
		return restAmount;
	}



	@Override
	public void repay(LoanRepayBo bo, String bankPayType) {
		try {
			if (!BankPayChannel.KUAIJIE.getCode().equals(bankPayType)) {
				lockRepay(bo.userId);
			}
			if (!bo.isAllRepay && !canRepay(bo.dsedLoanPeriodsDoList.get(0))) {
				// 未出账时拦截按期还款
				throw new FanbeiException("loan period can not repay error",FanbeiExceptionCode.LOAN_PERIOD_CAN_NOT_REPAY_ERROR);
			}

			String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
			if (StringUtil.equals("sysJob", bo.remoteIp)) {
				name = Constants.BORROW_REPAYMENT_NAME_AUTO;
			}

			String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(bankPayType);
			bo.tradeNo = tradeNo;
			bo.name = name;
            bo.repayType = DsedRepayType.ONLINE.getName();

			// 根据 还款金额 更新期数信息
			if (!bo.isAllRepay) { // 非提前结清
				List<DsedLoanPeriodsDo> loanPeriods = getLoanPeriodsIds(bo.loanId, bo.amount);
				bo.dsedLoanPeriodsIds.clear();
				bo.dsedLoanPeriodsDoList.clear();
				for (DsedLoanPeriodsDo dsedLoanPeriodsDo : loanPeriods) {
					bo.dsedLoanPeriodsIds.add(dsedLoanPeriodsDo.getRid());
					bo.dsedLoanPeriodsDoList.add(dsedLoanPeriodsDo);
				}
			}

			// 增加还款记录
			generateRepayRecords(bo);

			// 还款操作
			doRepay(bo, bo.dsedloanRepaymentDo, bankPayType);
		} catch (Exception e) {
			logger.info("repay fail dsedloanRepaymentDo one = " + bo.dsedloanRepaymentDo);
			if(bo.dsedloanRepaymentDo != null) {
				bo.dsedloanRepaymentDo.setStatus(DsedLoanRepaymentStatus.FAIL.name());
				bo.dsedloanRepaymentDo.setRemark("exception occur,msg = " + e.getMessage());
				if(e instanceof FanbeiException) {
					FanbeiException fanbeiException = (FanbeiException)e;
					FanbeiExceptionCode fanbeiExceptionCode = fanbeiException.getErrorCode();
					bo.dsedloanRepaymentDo.setErrorMessage(fanbeiExceptionCode.getErrorMsg());
					bo.dsedloanRepaymentDo.setErrorCode(fanbeiExceptionCode.getErrorCode());
				}else {
					bo.dsedloanRepaymentDo.setErrorMessage(e.getMessage());
					bo.dsedloanRepaymentDo.setErrorCode(2107);
				}
				logger.info("repay fail dsedloanRepaymentDo = " + bo.dsedloanRepaymentDo);
				dsedLoanRepaymentDao.updateStatusById(bo.dsedloanRepaymentDo);
			}

			unLockRepay(bo.userId);
			if(e instanceof FanbeiException) {
				throw e;
			}else {
				throw new FanbeiException(FanbeiExceptionCode.LOAN_UPS_DRIECT_FAIL, e);
			}
		}

	}


	/**
	 * @Description:  根据还款金额，匹配实际需还的期数信息
	 * @return  List<AfLoanPeriodsDo>
	 */
	public List<DsedLoanPeriodsDo> getLoanPeriodsIds(Long loanId, BigDecimal repaymentAmount){
		List<DsedLoanPeriodsDo> loanPeriodsIds = new ArrayList<DsedLoanPeriodsDo>();

		DsedLoanPeriodsDo loanPeriodDo = dsedLoanPeriodsDao.getLastActivePeriodByLoanId(loanId);
		logger.info("DsedLoanRepaymentServiceImpl getLoanPeriodsIds loanPeriodDo =>{}",JSONObject.toJSONString(loanPeriodDo)+",loanId="+loanId+",repaymentAmount="+repaymentAmount);
		// 最多可还期数(还款金额/（每期需还本金+手续费+利息）+1)
		BigDecimal restAmount = BigDecimal.ZERO;
		Integer nper = loanPeriodDo.getNper();

		for (int i = 0; i < (loanPeriodDo.getPeriods() - nper + 1); i++) {
			if(repaymentAmount.compareTo(BigDecimal.ZERO)>0){
				// 根据 loanId&期数  获取分期信息
				DsedLoanPeriodsDo nextLoanPeriodDo = dsedLoanPeriodsDao.getPeriodByLoanIdAndNper(loanId, nper+i);
				// 当前期需还金额
				restAmount = calculateRestAmount(nextLoanPeriodDo);
				// 更新还款金额
				repaymentAmount = repaymentAmount.subtract(restAmount);

				loanPeriodsIds.add(nextLoanPeriodDo);
			}
		}

		return loanPeriodsIds;
	}



	/**
	 * @Description:  增加还款记录
	 * @return  void
	 */
	private void generateRepayRecords(LoanRepayBo bo) {
		Date now = new Date();
		String tradeNo = bo.tradeNo;
		String name = bo.name;

		DsedLoanRepaymentDo loanRepaymentDo = buildRepayment( bo.repayType,bo.amount, tradeNo, now, bo.amount, 0l,
				null, BigDecimal.ZERO, bo.dsedLoanDo.getRid(), bo.outTradeNo, name, bo.userId,bo.dsedLoanDo.getPrdType(),bo.bankNo,bo.cardName,bo.dsedLoanPeriodsDoList,bo.isAllRepay);

		dsedLoanRepaymentDao.saveRecord(loanRepaymentDo);

		bo.dsedloanRepaymentDo = loanRepaymentDo;

		logger.info("Repay.add repayment finish,name="+ name +",tradeNo="+tradeNo+",borrowRepayment="+ JSON.toJSONString(loanRepaymentDo));
	}

	private DsedLoanRepaymentDo buildRepayment( String repayType ,BigDecimal repaymentAmount, String repayNo, Date gmtCreate, BigDecimal actualAmount,
											 Long userCouponId, BigDecimal couponAmount, BigDecimal rebateAmount, Long loanId, String payTradeNo, String name, Long userId,
												String prdType,String bankNo,String cardName,List<DsedLoanPeriodsDo> loanPeriodsDoList,boolean isAllRepay) {
		DsedLoanRepaymentDo loanRepay = new DsedLoanRepaymentDo();
		loanRepay.setUserId(userId);
		loanRepay.setLoanId(loanId);
		loanRepay.setName(name);
		loanRepay.setRepayAmount(repaymentAmount);
		loanRepay.setActualAmount(actualAmount);
		loanRepay.setStatus(DsedLoanRepaymentStatus.APPLY.name());
		loanRepay.setTradeNo(repayNo);
		loanRepay.setTradeNoOut(payTradeNo);
		loanRepay.setCouponAmount(couponAmount);
		loanRepay.setUserAmount(rebateAmount);
		if(isAllRepay){
			loanRepay.setPreRepayStatus("Y");
		}else {
			loanRepay.setPreRepayStatus("N");
		}
		String repayPeriods = "";
		for (int i = 0; i < loanPeriodsDoList.size(); i++) {
			if(i == loanPeriodsDoList.size()-1){
				repayPeriods += loanPeriodsDoList.get(i).getRid();
			} else {
				repayPeriods += loanPeriodsDoList.get(i).getRid()+",";
			}
		}

		loanRepay.setRepayPeriods(repayPeriods);
		loanRepay.setPrdType(prdType);
		loanRepay.setGmtCreate(gmtCreate);
		loanRepay.setBankCardNumber(bankNo);
		loanRepay.setBankCardName(cardName);
        loanRepay.setRepayChannel(repayType);
		return loanRepay;
	}

	/**
	 * 锁住还款
	 */
	private void lockRepay(Long userId) {
		String key = userId + "_success_dsedLoanRepay";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 300, TimeUnit.SECONDS);
		if (count != 1) {
			throw new FanbeiException("loan repay not exist",FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
		}
	}

	private void unLockRepay(Long userId) {
		String key = userId + "_success_dsedLoanRepay";
		redisTemplate.delete(key);
	}

	/**
	 * 锁住目标流水号的还款，防止重复回调
	 */
	private void lock(String tradeNo) {
		String key = tradeNo + "_success_legalRepay";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
		if (count != 1) {
			throw new FanbeiException(FanbeiExceptionCode.UPS_REPEAT_NOTIFY);
		}
	}

	private void unLock(String tradeNo) {
		String key = tradeNo + "_success_legalRepay";
		redisTemplate.delete(key);
	}

	/**
	 * @return true: 已出账；false： 未出账
	 */
	public boolean canRepay(DsedLoanPeriodsDo loanPeriodsDo) {
		boolean flag = false;
		Date now = new Date();
		Date plan = loanPeriodsDo.getGmtPlanRepay();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(plan);
		calendar.add(Calendar.MONTH, -1);

		Date startTime = calendar.getTime();

		if(now.after(startTime)){ // 已出账
			flag = true;
		}

		return flag;
	}


	/**
	 * @Description:  还款操作
	 * @return  void
	 */
	private Map<String, Object> doRepay(LoanRepayBo bo, DsedLoanRepaymentDo repayment, String bankChannel) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HashMap<String,Object> bank = dsedUserBankcardDao.getUserBankInfo(bo.bankNo);
		KuaijieDsedLoanBo bizObject = new KuaijieDsedLoanBo(repayment, bo);
		if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
			repayment.setStatus(DsedLoanRepaymentStatus.SMS.name());
			resultMap = sendKuaiJieSms(bank, bo.tradeNo, bo.amount, bo.userId, bo.dsedUserDo.getRealName(),
					bo.dsedUserDo.getIdNumber(), JSON.toJSONString(bizObject), "dsedLoanRepaymentService", Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_LOAN.getCode());
		} else {// 代扣
			resultMap = doUpsPay(bankChannel, bank, bo.tradeNo, bo.amount, bo.userId, bo.dsedUserDo.getRealName(),
					bo.dsedUserDo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_LOAN.getCode());
		}
		return resultMap;
	}


	@Override
	public void dealRepaymentSucess(String tradeNo, String outTradeNo) {
		final DsedLoanRepaymentDo repaymentDo = dsedLoanRepaymentDao.getRepayByTradeNo(tradeNo);
		dealRepaymentSucess(tradeNo, outTradeNo, repaymentDo,null,null,null,false);
	}


	/**
	 * 还款成功后调用
	 * @param tradeNo 我方交易流水
	 * @param outTradeNo 资金方交易流水
	 * @return
	 */
	@Override
	public void dealRepaymentSucess(String tradeNo, String outTradeNo, final DsedLoanRepaymentDo repaymentDo, String repayChannel, Long collectionRepaymentId, final List<HashMap> periodsList,boolean flag) {
		try {
			lock(tradeNo);

			logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo) );
			List<DsedLoanPeriodsDo> newLoanPeriodsDoList = new ArrayList<DsedLoanPeriodsDo>();
			preCheck(repaymentDo, tradeNo);
			repaymentDo.setRepayChannel(repayChannel);	// 还款渠道
			final LoanRepayDealBo loanRepayDealBo = new LoanRepayDealBo();
			loanRepayDealBo.curTradeNo = tradeNo;
			loanRepayDealBo.curOutTradeNo = outTradeNo;
			loanRepayDealBo.repaymentDo = repaymentDo;
			if(repaymentDo != null){
				String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");
				for (int i = 0; i < repayPeriodsIds.length; i++) {
					// 获取分期信息
					DsedLoanPeriodsDo loanPeriodsDo = dsedLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
					newLoanPeriodsDoList.add(loanPeriodsDo);
				}
				loanRepayDealBo.newLoanPeriodsDoList = newLoanPeriodsDoList;
			}

			long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(TransactionStatus status) {
					try {
						dealLoanRepay(loanRepayDealBo, repaymentDo,periodsList);
						// 最后一期还完后， 修改loan状态FINSH
						dealLoanStatus(loanRepayDealBo);
						dealSum(loanRepayDealBo);
						return 1L;
					} catch (Exception e) {
						status.setRollbackOnly();
						logger.info("dealRepaymentSucess error", e);
						throw e;
					}
				}

			});
			if (resultValue == 1L) {
				if (collectionRepaymentId != null){
					repaymentDo.setRemark(String.valueOf(collectionRepaymentId));
				}
				repaymentDo.setStatus(DsedLoanRepaymentStatus.SUCC.name());
				//还款成功，调用西瓜信用通知接口
				DsedNoticeRecordDo noticeRecordDo = new DsedNoticeRecordDo();
				noticeRecordDo.setUserId(repaymentDo.getUserId());
				noticeRecordDo.setRefId(String.valueOf(repaymentDo.getRid()));
				noticeRecordDo.setType(getStatus(repaymentDo));
				noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
                HashMap<String,String> data = buildData(repaymentDo);
				noticeRecordDo.setParams(JSON.toJSONString(data));
				dsedNoticeRecordService.addNoticeRecord(noticeRecordDo);
				try {
					if(!flag){
						//app逾期还款通知催收
						nofityRisk(loanRepayDealBo,repaymentDo);
					}
					//催收逾期还款
					collectRisk(loanRepayDealBo.loanPeriodsDoList,repaymentDo,loanRepayDealBo.loanDo.getRid());
				}catch (Exception e){
					e.printStackTrace();
				}
				logger.info("dealRepaymentSucess data cfp "+JSON.toJSONString(data));
				if(xgxyUtil.dsedRePayNoticeRequest(data)){
					noticeRecordDo.setRid(noticeRecordDo.getRid());
					noticeRecordDo.setGmtModified(new Date());
					dsedNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
				}
			}

		}finally {
			unLock(tradeNo);
			// 解锁还款
			unLockRepay(repaymentDo.getUserId());
		}
	}

	public String getStatus (DsedLoanRepaymentDo repaymentDo){
		String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");
		for (int i = 0; i < repayPeriodsIds.length; i++) {
			DsedLoanPeriodsDo loanPeriodsDo = dsedLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
			if(loanPeriodsDo!=null){
				if(StringUtil.equals(loanPeriodsDo.getOverdueStatus(), YesNoStatus.YES.getCode())){
					if(StringUtil.equals(repaymentDo.getRepayChannel(), DsedRepayType.COLLECT.getName())){
                        return DsedNoticeType.XGXY_COLLECT.code;
                    }else {
                        return DsedNoticeType.XGXY_OVERDUEREPAY.code;
                    }
				}
			}
		}
		return DsedNoticeType.REPAY.code;
	}


	private void collectRisk(List<DsedLoanPeriodsDo> list,DsedLoanRepaymentDo repaymentDo,Long rid) {
		List<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
		StringBuffer sb = new StringBuffer();
		int overdueDays = 0;
		List<DsedLoanPeriodsDo> newlist = new ArrayList<DsedLoanPeriodsDo>();
		for(DsedLoanPeriodsDo dsedLoanDo : list){
			if(StringUtil.equals(dsedLoanDo.getOverdueStatus(),YesNoStatus.YES.getCode())){
				newlist.add(dsedLoanDo);
			}
		}

		for(DsedLoanPeriodsDo dsedLoanDo : newlist){
			if(StringUtil.equals(dsedLoanDo.getStatus(),DsedLoanPeriodStatus.FINISHED.name())){
				sb.append(dsedLoanDo.getNper()).append(",");
			}
			if(dsedLoanDo.getOverdueDays() > overdueDays){
				overdueDays = dsedLoanDo.getOverdueDays();
			}
		}
		if(sb.length() > 0){
			sb = sb.deleteCharAt(sb.length()-1);
		}
		DsedLoanDo loanDo = dsedLoanDao.getById(rid);
		DsedContractPdfDo dsedContractPdfDo = new DsedContractPdfDo();
		dsedContractPdfDo.setType((byte) 5);
		dsedContractPdfDo.setTypeId(rid);
		DsedContractPdfDo contractPdfDo = dsedContractPdfDao.selectByTypeId(dsedContractPdfDo);
		for(DsedLoanPeriodsDo dsedLoanDo : newlist){
			Map<String, String> data = new HashMap<String, String>();
			DsedUserDo userDo=dsedUserDao.getById(dsedLoanDo.getUserId());
			//用户信息
			data.put("address",userDo.getAddress());
			String gender = userDo.getGender();
			if(StringUtil.equals(gender, GenderType.M.getCode())){
				gender = GenderType.M.getName();
			}else if(StringUtil.equals(gender,GenderType.F.getCode())){
				gender = GenderType.F.getName();
			}else {
				gender = GenderType.U.getName();
			}
			data.put("gender",gender);
			data.put("birthday",userDo.getBirthday());
			data.put("dataId", String.valueOf(dsedLoanDo.getRid()));
			data.put("caseName","dsed_"+dsedLoanDo.getNper()+"/"+dsedLoanDo.getPeriods());
			data.put("planRepaymenTime", DateUtil.formatDateTime(dsedLoanDo.getGmtPlanRepay()));
			BigDecimal currentAmount = BigDecimalUtil.add(dsedLoanDo.getAmount(), dsedLoanDo.getRepaidOverdueAmount(),dsedLoanDo.getRepaidInterestFee(), dsedLoanDo.getRepaidServiceFee()).subtract(dsedLoanDo.getRepayAmount());//应还金额
			data.put("residueAmount", String.valueOf(BigDecimalUtil.add(currentAmount,dsedLoanDo.getInterestFee(),dsedLoanDo.getOverdueAmount(),dsedLoanDo.getServiceFee())));
			//账单
			data.put("principal", String.valueOf(currentAmount));
			data.put("overdueAmount", String.valueOf(dsedLoanDo.getOverdueAmount()));
			data.put("nper", String.valueOf(dsedLoanDo.getNper()));
			data.put("periods",String.valueOf(dsedLoanDo.getPeriods()));
			data.put("userId", String.valueOf(userDo.getRid()));
			data.put("realName",userDo.getRealName());
			data.put("idNumber",userDo.getIdNumber());
			data.put("payTime", DateUtil.formatDateTime(dsedLoanDo.getGmtCreate()));
			data.put("phoneNumber",userDo.getMobile());
			data.put("address",userDo.getAddress());
			data.put("userName",userDo.getMobile());
			data.put("productName","XGXY");
			data.put("borrowAmount",String.valueOf(dsedLoanDo.getAmount()));
			data.put("appName","dsed");
			data.put("repaymentPeriod","1");
			data.put("havePaied",sb.toString());
			data.put("overdueDay",String.valueOf(dsedLoanDo.getOverdueDays()));
			data.put("overdueAmount",String.valueOf(BigDecimalUtil.add(dsedLoanDo.getOverdueAmount(),dsedLoanDo.getRepaidOverdueAmount())));
			data.put("type",dsedLoanDo.getStatus());
			data.put("repayAmount",String.valueOf(dsedLoanDo.getRepayAmount()));
			data.put("amount",String.valueOf(dsedLoanDo.getAmount()));
			data.put("notReductionAmount","");
			//借款
			data.put("loanNo",String.valueOf(loanDo.getLoanNo()));
			data.put("loanAmount",String.valueOf(loanDo.getAmount()));
			data.put("arrivalAmount",String.valueOf(loanDo.getAmount()));
			data.put("loanStatus",loanDo.getStatus());
			data.put("repayTime",DateUtil.formatDateTime(loanDo.getGmtCreate()));
			data.put("maxOverdueDay",String.valueOf(overdueDays));
			data.put("loanRemark",loanDo.getLoanRemark());
			if(null != contractPdfDo){
				data.put("contractPdfUrl",contractPdfDo.getContractPdfUrl());
			}else {
				data.put("contractPdfUrl","");
			}
			arrayList.add(data);
		}
		if(newlist.size()>0){
            DsedNoticeRecordDo noticeRecordDo = new DsedNoticeRecordDo();
            noticeRecordDo.setUserId(repaymentDo.getUserId());
            noticeRecordDo.setRefId(String.valueOf(repaymentDo.getRid()));
            noticeRecordDo.setType(DsedNoticeType.COLLECT.code);
            noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
            Map<String,String>  params=new HashMap<>();
            params.put("orderNo",getOrderNo("XGXY"));
            params.put("info",JSON.toJSONString(arrayList));
            params.put("companyId","");
            params.put("token",collectRiskToken);
            noticeRecordDo.setParams(JSON.toJSONString(params));
            dsedNoticeRecordService.addNoticeRecord(noticeRecordDo);
            boolean result = collectionSystemUtil.noticeRiskCollect(params);
            if(result){
                noticeRecordDo.setRid(noticeRecordDo.getRid());
                noticeRecordDo.setGmtModified(new Date());
                dsedNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
            }
        }
	}

	/**
	 * 获取订单号
	 * @param method 接口标识（固定4位）
	 */
	private static String getOrderNo(String method){
		return StringUtil.appendStrs("cs",method,System.currentTimeMillis());
	}

	private void nofityRisk(LoanRepayDealBo LoanRepayDealBo,DsedLoanRepaymentDo repaymentDo) {
		//会对逾期的借款还款，向催收平台同步还款信息
		if (DateUtil.compareDate(new Date(), LoanRepayDealBo.newLoanPeriodsDoList.get(0).getGmtPlanRepay()) ){
			try {
				BigDecimal amount = BigDecimal.ZERO;
				BigDecimal repayAmount = BigDecimal.ZERO;
				List<HashMap<String,String>> list = new ArrayList<>();
				Map<String, String> reqBo = new HashMap<String, String>();

				//("订单编号")
				reqBo.put("orderNo", LoanRepayDealBo.loanNo);
				//("还款流水")
				reqBo.put("repaymentNo", LoanRepayDealBo.curTradeNo);
				//("还款时间")
				reqBo.put("repayTime", DateUtil.formatDateTime(new Date()));
				reqBo.put("type", AfRepayCollectionType.APP.getCode());
				reqBo.put("repaymentAcc", LoanRepayDealBo.userId+"");//还款账户
				for(DsedLoanPeriodsDo dsedLoanPeriodsDo : LoanRepayDealBo.newLoanPeriodsDoList){
					if(StringUtil.equals(dsedLoanPeriodsDo.getOverdueStatus(),YesNoStatus.YES.getCode())){
						repayAmount = BigDecimalUtil.add(dsedLoanPeriodsDo.getAmount(),
								dsedLoanPeriodsDo.getRepaidInterestFee(),dsedLoanPeriodsDo.getInterestFee(),
								dsedLoanPeriodsDo.getServiceFee(),dsedLoanPeriodsDo.getRepaidServiceFee(),
								dsedLoanPeriodsDo.getOverdueAmount(),dsedLoanPeriodsDo.getRepaidOverdueAmount())
								.subtract(dsedLoanPeriodsDo.getRepayAmount());
						HashMap<String, String> data = new HashMap<String, String>();
						data.put("dataId",dsedLoanPeriodsDo.getRid().toString());
						if(StringUtil.equals(repaymentDo.getPreRepayStatus(),YesNoStatus.NO.getCode())){
							if(repayAmount.compareTo(repaymentDo.getActualAmount())>=0){
								amount = repaymentDo.getActualAmount();
								data.put("amount",repaymentDo.getActualAmount().toString());
							}
						}else {
							amount = repayAmount.add(amount);
							data.put("amount",repayAmount.toString());
						}
						list.add(data);
					}
				}
				//还款总额(逾期)
				reqBo.put("totalAmount", amount+"");
				reqBo.put("token",nofityRiskToken);
				//("还款详情, 格式: [{'dataId':'数据编号', 'amount':'还款金额(元, 精确到分)'},...]")
				reqBo.put("details", JSON.toJSONString(list));
				DsedNoticeRecordDo noticeRecordDo = new DsedNoticeRecordDo();
				noticeRecordDo.setUserId(repaymentDo.getUserId());
				noticeRecordDo.setRefId(String.valueOf(repaymentDo.getRid()));
				noticeRecordDo.setType(DsedNoticeType.OVERDUEREPAY.code);
				noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
				noticeRecordDo.setParams(JSON.toJSONString(reqBo));
				dsedNoticeRecordService.addNoticeRecord(noticeRecordDo);
				boolean result = collectionSystemUtil.consumerRepayment(reqBo);
				if(result){
					noticeRecordDo.setRid(noticeRecordDo.getRid());
					noticeRecordDo.setGmtModified(new Date());
					dsedNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
				}
			} catch (Exception e) {
				logger.error("向催收平台同步还款信息失败", e);
			}
		}else{
			logger.info("collection consumerRepayment not push,borrowCashId="+LoanRepayDealBo.loanDo.getRid());
		}
	}

	@Override
	public HashMap<String,String> buildData(DsedLoanRepaymentDo repaymentDo){
		HashMap<String,String> data = new HashMap<String,String>();
		DsedLoanDo loanDo = dsedLoanDao.getById(repaymentDo.getLoanId());
		logger.info("buildData loanDo = " + loanDo +",repaymentDo = " +repaymentDo);
		if(StringUtil.equals(repaymentDo.getStatus(),DsedLoanRepaymentStatus.SUCC.name())){
			List<XgxyRepayBo> borrowBillDetails = new ArrayList<XgxyRepayBo>();
			data.put("amount",repaymentDo.getActualAmount().toString());
			data.put("borrowNo",loanDo.getLoanNo());
			data.put("status","REPAYSUCCESS");
			data.put("tradeNo",repaymentDo.getTradeNo());
			data.put("repayChannel",repaymentDo.getRepayChannel());
			if(StringUtil.equals(loanDo.getStatus(),DsedLoanStatus.FINISHED.name())){
				data.put("totalIsFinish","Y");
			}else {
				data.put("totalIsFinish","N");
			}
			String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");

			for (int i = 0; i < repayPeriodsIds.length; i++) {
				// 获取分期信息
				XgxyRepayBo xgxyRepayBo = new XgxyRepayBo();
				DsedLoanPeriodsDo loanPeriodsDo = dsedLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
				if(loanPeriodsDo!=null){	// 提前还款,已出账的分期借款,还款金额=分期本金+手续费+利息（+逾期费）
					if(repaymentDo.getPreRepayStatus().equals("Y")) {	// 提前还款
						xgxyRepayBo.setIsFinish("Y");
					}else if(repaymentDo.getPreRepayStatus().equals("N")) {		// 按期还款（部分还款）
						if(StringUtil.equals("FINISHED",loanPeriodsDo.getStatus())){
							xgxyRepayBo.setIsFinish("Y");
						}else {
							xgxyRepayBo.setIsFinish("N");
						}
					}
					xgxyRepayBo.setCurPeriod(loanPeriodsDo.getNper().toString());
					BigDecimal amount = BigDecimalUtil.add(loanPeriodsDo.getAmount(),
							loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
							loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
							loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
							.subtract(loanPeriodsDo.getRepayAmount());
					xgxyRepayBo.setUnrepayAmount(amount.toString());
					xgxyRepayBo.setUnrepayInterestFee(loanPeriodsDo.getInterestFee().toString());
					xgxyRepayBo.setUnrepayOverdueFee(loanPeriodsDo.getOverdueAmount().toString());
					xgxyRepayBo.setUnrepayServiceFee(loanPeriodsDo.getServiceFee().toString());
				}
				borrowBillDetails.add(xgxyRepayBo);
			}
			JSONArray json = JSONArray.fromObject(borrowBillDetails);
			String jsonStr = json.toString();
			data.put("borrowBillDetails",jsonStr);
		}else if(StringUtil.equals(repaymentDo.getStatus(),DsedLoanRepaymentStatus.FAIL.name())){
			data.put("reason","银行卡交易失败，您可换卡或稍后重试");
			data.put("borrowNo",loanDo.getLoanNo());
			data.put("status","REPAYFAIL");
		}
		return data;
	}

	/**
	 * 还款失败后调用
	 */
	@Override
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice,String errorMsg) {
		final DsedLoanRepaymentDo loanRepaymentDo = dsedLoanRepaymentDao.getRepayByTradeNo(tradeNo);
		logger.info("dealRepaymentFail process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",isNeedMsgNotice=" + isNeedMsgNotice + ",errorMsg=" + errorMsg + ",borrowRepayment=" + JSON.toJSONString(loanRepaymentDo));
		if ((loanRepaymentDo != null && DsedLoanRepaymentStatus.SUCC.name().equals(loanRepaymentDo.getStatus()))) { // 检查交易流水 对应记录数据库中是否已经处理
			return;
		}

		if (loanRepaymentDo != null) {
			changLoanRepaymentStatus(outTradeNo, DsedLoanRepaymentStatus.FAIL.name(), loanRepaymentDo.getRid());
		}

		// 解锁还款
		unLockRepay(loanRepaymentDo.getUserId());
		//还款失败，调用西瓜信用通知接口
		DsedLoanDo loanDo = dsedLoanDao.getById(loanRepaymentDo.getLoanId());
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("reason",errorMsg);
		data.put("borrowNo",loanDo.getLoanNo());
		data.put("status","REPAYFAIL");
		data.put("repayChannel",loanRepaymentDo.getRepayChannel());
		DsedNoticeRecordDo noticeRecordDo = new DsedNoticeRecordDo();
		noticeRecordDo.setUserId(loanRepaymentDo.getUserId());
		noticeRecordDo.setRefId(String.valueOf(loanRepaymentDo.getRid()));
		noticeRecordDo.setType(getStatus(loanRepaymentDo));
		noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
		noticeRecordDo.setParams(JSON.toJSONString(data));
		dsedNoticeRecordService.addNoticeRecord(noticeRecordDo);
		if (xgxyUtil.dsedRePayNoticeRequest(data)) {
			noticeRecordDo.setRid(noticeRecordDo.getRid());
			noticeRecordDo.setGmtModified(new Date());
			dsedNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
		}


	}

	/**
	 * @Description: 还款状态修改
	 * @return  long
	 */
	private long changLoanRepaymentStatus(String outTradeNo, String status, Long rid) {
		DsedLoanRepaymentDo loanRepay = new DsedLoanRepaymentDo();
		loanRepay.setStatus(status);
		loanRepay.setTradeNoOut(outTradeNo);
		loanRepay.setRid(rid);
		loanRepay.setGmtModified(new Date());
		return dsedLoanRepaymentDao.updateById(loanRepay);
	}

	/**
	 * @Description: 还款状态修改(防止ups回调过快，导致数据覆盖)
	 * @return  long
	 */
	private long loanRepaymentStatus(String outTradeNo, String status, Long rid) {
		DsedLoanRepaymentDo loanRepay = new DsedLoanRepaymentDo();
		loanRepay.setStatus(status);
		loanRepay.setTradeNoOut(outTradeNo);
		loanRepay.setRid(rid);
		loanRepay.setGmtModified(new Date());
		return dsedLoanRepaymentDao.updateStatusById(loanRepay);
	}


	/**
	 * @Description: 检查是否已经处理
	 * @return  void
	 */
	private void preCheck(DsedLoanRepaymentDo repaymentDo, String tradeNo) {
		// 检查交易流水 对应记录数据库中是否已经处理
		if ( repaymentDo != null && DsedLoanRepaymentStatus.SUCC.name().equals(repaymentDo.getStatus()) ) {
			throw new FanbeiException("preCheck,repayment has been dealed!");
		}
	}

	/**
	 * 需在事务管理块中调用此函数!
	 * @param loanRepayDealBo
	 * @param repaymentDo
	 */
	private void dealLoanRepay(LoanRepayDealBo loanRepayDealBo, DsedLoanRepaymentDo repaymentDo, List<HashMap> periodsList) {
		if(repaymentDo == null) return;

		DsedLoanDo loanDo = dsedLoanDao.getById(repaymentDo.getLoanId());
		loanRepayDealBo.curRepayAmoutStub = repaymentDo.getRepayAmount();
		loanRepayDealBo.curRebateAmount = repaymentDo.getUserAmount();
		loanRepayDealBo.curSumRebateAmount = loanRepayDealBo.curSumRebateAmount.add(repaymentDo.getUserAmount());
		loanRepayDealBo.curUserCouponId = repaymentDo.getUserCouponId();
		loanRepayDealBo.curSumRepayAmount = loanRepayDealBo.curSumRepayAmount.add(repaymentDo.getRepayAmount());
		loanRepayDealBo.curCardName = repaymentDo.getBankCardName();
		loanRepayDealBo.curCardNo = repaymentDo.getBankCardNumber();
		loanRepayDealBo.curName = repaymentDo.getName();

		loanRepayDealBo.loanDo = loanDo;
		loanRepayDealBo.overdueDay = loanDo.getOverdueDays();
		loanRepayDealBo.loanNo = loanDo.getLoanNo();
		loanRepayDealBo.refId += repaymentDo.getLoanId();
		loanRepayDealBo.userId = loanDo.getUserId();

		List<DsedLoanPeriodsDo> loanPeriodsDoList = new ArrayList<DsedLoanPeriodsDo>();

		if(repaymentDo.getPreRepayStatus().equals("Y")) {	// 提前还款
			loanRepayDealBo.isAllRepay = true;
			String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");
			for (int i = 0; i < repayPeriodsIds.length; i++) {
				// 获取分期信息
				DsedLoanPeriodsDo loanPeriodsDo = dsedLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
				if(loanPeriodsDo!=null){	// 提前还款,已出账的分期借款,还款金额=分期本金+手续费+利息（+逾期费）
					loanPeriodsDoList.add(loanPeriodsDo);
					BigDecimal repayAmount = BigDecimal.ZERO;
					if(canRepay(loanPeriodsDo)){
						BigDecimal reductionAmount = BigDecimal.ZERO;
						if (periodsList != null && periodsList.size() > 0){
							for (HashMap map:periodsList) {
								if (Long.parseLong(String.valueOf(map.get("id"))) == loanPeriodsDo.getRid()){
									reductionAmount = BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("reductionAmount"))) );
								}
							}
						}
						dealLoanRepayOverdue(loanRepayDealBo, loanPeriodsDo, loanDo,reductionAmount);		//逾期费
						dealLoanRepayPoundage(loanRepayDealBo, loanPeriodsDo);		//手续费
						dealLoanRepayInterest(loanRepayDealBo, loanPeriodsDo);		//利息

						repayAmount = calculateRestAmount(loanPeriodsDo);
						loanPeriodsDo.setRepayAmount(loanPeriodsDo.getRepayAmount().add(repayAmount));

					}else{		// 提前还款,未出账的分期借款,还款金额=分期本金
						repayAmount = loanPeriodsDo.getAmount();
						loanPeriodsDo.setRepayAmount(repayAmount);
					}

					loanPeriodsDo.setPreRepayStatus("Y"); 	// 提前还款
					loanPeriodsDo.setRepayId(repaymentDo.getRid());
					loanPeriodsDo.setStatus(DsedLoanPeriodStatus.FINISHED.name());
					loanPeriodsDo.setGmtLastRepay(new Date());
					loanPeriodsDo.setGmtModified(new Date());

					loanRepayDealBo.curRepayAmoutStub = BigDecimalUtil.subtract(loanRepayDealBo.curRepayAmoutStub, repayAmount);
				}
				dsedLoanPeriodsDao.updateById(loanPeriodsDo);
			}
		}else if(repaymentDo.getPreRepayStatus().equals("N")) {		// 按期还款（部分还款）
			loanRepayDealBo.isAllRepay = false;
			String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");
			for (int i = 0; i < repayPeriodsIds.length; i++) {

				DsedLoanPeriodsDo loanPeriodsDo = dsedLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
				if(loanPeriodsDo!=null){
					BigDecimal reductionAmount = BigDecimal.ZERO;
					if (periodsList != null && periodsList.size() > 0){
						for (HashMap map:periodsList) {
							if (Long.parseLong(String.valueOf(map.get("id"))) == loanPeriodsDo.getRid()){
								reductionAmount = BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("reductionAmount"))));
							}
						}
					}
					loanPeriodsDoList.add(loanPeriodsDo);
					dealLoanRepayOverdue(loanRepayDealBo, loanPeriodsDo, loanDo,reductionAmount);		//逾期费
					dealLoanRepayPoundage(loanRepayDealBo, loanPeriodsDo);		//手续费
					dealLoanRepayInterest(loanRepayDealBo, loanPeriodsDo);		//利息
					dealLoanRepayIfFinish(loanRepayDealBo, repaymentDo, loanPeriodsDo,reductionAmount);	//修改借款分期状态
				}
                dsedLoanPeriodsDao.updateById(loanPeriodsDo);
			}
		}

		loanRepayDealBo.loanPeriodsDoList = loanPeriodsDoList;

		changLoanRepaymentStatus(loanRepayDealBo.curOutTradeNo, DsedLoanRepaymentStatus.SUCC.name(), repaymentDo.getRid());

	}


	/**
	 * @Description: 分期记录逾期费处理
	 * @return  void
	 */
	private void dealLoanRepayOverdue(LoanRepayDealBo LoanRepayDealBo, DsedLoanPeriodsDo loanPeriodsDo, DsedLoanDo loanDo, BigDecimal reductionAmount) {
		if(LoanRepayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = LoanRepayDealBo.curRepayAmoutStub;
		BigDecimal overdueAmount = loanPeriodsDo.getOverdueAmount();

		if (repayAmount.compareTo(overdueAmount) > 0) {
			loanPeriodsDo.setRepaidOverdueAmount(BigDecimalUtil.add(loanPeriodsDo.getRepaidOverdueAmount(), overdueAmount).subtract(reductionAmount));
			loanPeriodsDo.setOverdueAmount(BigDecimal.ZERO);
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(overdueAmount,loanPeriodsDo.getRepayAmount()));
			LoanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(overdueAmount);
			LoanRepayDealBo.curRepayOverdue = overdueAmount;

			if(loanDo.getOverdueAmount().compareTo(BigDecimal.ZERO)>0){
				loanDo.setOverdueAmount(BigDecimal.ZERO);
				dsedLoanDao.updateById(loanDo);
			}
		} else {
			loanPeriodsDo.setRepaidOverdueAmount(BigDecimalUtil.add(loanPeriodsDo.getRepaidOverdueAmount(), repayAmount).subtract(reductionAmount));
			loanPeriodsDo.setOverdueAmount(overdueAmount.subtract(repayAmount).add(reductionAmount));
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(repayAmount,loanPeriodsDo.getRepayAmount()));
			LoanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
			LoanRepayDealBo.curRepayOverdue = repayAmount;

			if(loanDo.getOverdueAmount().compareTo(BigDecimal.ZERO)>0){
				loanDo.setOverdueAmount(loanDo.getOverdueAmount().subtract(repayAmount));
				dsedLoanDao.updateById(loanDo);
			}
		}
	}

	/**
	 * @Description: 分期记录手续费处理
	 * @return  void
	 */
	private void dealLoanRepayPoundage(LoanRepayDealBo LoanRepayDealBo, DsedLoanPeriodsDo loanPeriodsDo) {
		if(LoanRepayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = LoanRepayDealBo.curRepayAmoutStub;
		BigDecimal poundageAmount = loanPeriodsDo.getServiceFee();

		if (repayAmount.compareTo(poundageAmount) > 0) {
			loanPeriodsDo.setRepaidServiceFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidServiceFee(), poundageAmount));
			loanPeriodsDo.setServiceFee(BigDecimal.ZERO);
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(poundageAmount,loanPeriodsDo.getRepayAmount()));
			LoanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(poundageAmount);
			LoanRepayDealBo.curRepayService = poundageAmount;
		} else {
			loanPeriodsDo.setRepaidServiceFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidServiceFee(), repayAmount));
			loanPeriodsDo.setServiceFee(poundageAmount.subtract(repayAmount));
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(repayAmount,loanPeriodsDo.getRepayAmount()));
			LoanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
			LoanRepayDealBo.curRepayService = repayAmount;
		}
	}

	/**
	 * @Description: 分期记录利息处理
	 * @return  void
	 */
	private void dealLoanRepayInterest(LoanRepayDealBo LoanRepayDealBo, DsedLoanPeriodsDo loanPeriodsDo) {
		if(LoanRepayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = LoanRepayDealBo.curRepayAmoutStub;
		BigDecimal rateAmount = loanPeriodsDo.getInterestFee();

		if (repayAmount.compareTo(rateAmount) > 0) {
			loanPeriodsDo.setRepaidInterestFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidInterestFee(), rateAmount));
			loanPeriodsDo.setInterestFee(BigDecimal.ZERO);
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(rateAmount,loanPeriodsDo.getRepayAmount()));
			LoanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(rateAmount);
			LoanRepayDealBo.curRepayInterest = rateAmount;
		} else {
			loanPeriodsDo.setRepaidInterestFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidInterestFee(), repayAmount));
			loanPeriodsDo.setInterestFee(rateAmount.subtract(repayAmount));
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(repayAmount,loanPeriodsDo.getRepayAmount()));
			LoanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
			LoanRepayDealBo.curRepayInterest = repayAmount;
		}
	}

	/**
	 * @Description: 分期记录 完成处理
	 * @return  void
	 */
	private void dealLoanRepayIfFinish(LoanRepayDealBo loanRepayDealBo, DsedLoanRepaymentDo repaymentDo, DsedLoanPeriodsDo loanPeriodsDo, BigDecimal reductionAmount) {

		// 所有需还金额
		BigDecimal sumAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount(),
				loanPeriodsDo.getOverdueAmount(), loanPeriodsDo.getRepaidOverdueAmount(),
				loanPeriodsDo.getInterestFee(), loanPeriodsDo.getRepaidInterestFee(),
				loanPeriodsDo.getServiceFee(), loanPeriodsDo.getRepaidServiceFee());

		// 当笔还款处理的逾期费手续费利息
		BigDecimal curRepayOSI = BigDecimalUtil.add(loanRepayDealBo.curRepayOverdue,loanRepayDealBo.curRepayService,loanRepayDealBo.curRepayInterest);

		// 本期需还金额
		BigDecimal calculateRestAmount = calculateRestAmount(loanPeriodsDo);
		BigDecimal repayAmount = loanRepayDealBo.curRepayAmoutStub;

		// 针对多期已出账 的部分还款
		if(repayAmount.compareTo(calculateRestAmount) >= 0){
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(loanPeriodsDo.getRepayAmount(),calculateRestAmount));
			loanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(calculateRestAmount);
		}else {
			loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(loanPeriodsDo.getRepayAmount(),repayAmount));
			loanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}


		// 所有已还金额
		BigDecimal allRepayAmount = loanPeriodsDo.getRepayAmount();

		BigDecimal minus = allRepayAmount.subtract(sumAmount); //容许多还一块钱，兼容离线还款 场景
		logger.info("dealRepaymentSucess process dealLoanRepayIfFinish allRepayAmount="+allRepayAmount+",minus="+minus+",loanPeriodsDo="+ JSONObject.toJSONString(loanPeriodsDo)+",repaymentDo="+JSONObject.toJSONString(repaymentDo)+",loanRepayDealBo="+JSONObject.toJSONString(loanRepayDealBo));
		if (minus.compareTo(BigDecimal.ZERO) >= 0) {
			loanPeriodsDo.setStatus(DsedLoanPeriodStatus.FINISHED.name());
		} else if (minus.compareTo(BigDecimal.ZERO) < 0) {	// 部分还款
			loanPeriodsDo.setStatus(DsedLoanPeriodStatus.PART_REPAY.name());
		}
		loanPeriodsDo.setRepayId(repaymentDo.getRid());
		loanPeriodsDo.setGmtLastRepay(new Date());
		loanPeriodsDo.setGmtModified(new Date());
	}


	private void dealLoanStatus(LoanRepayDealBo loanRepayDealBo) {
		int nper = loanRepayDealBo.loanPeriodsDoList.size();
		DsedLoanPeriodsDo loanPeriodsDo = loanRepayDealBo.loanPeriodsDoList.get(nper-1);
		if(loanPeriodsDo.getNper() == loanRepayDealBo.loanDo.getPeriods() && DsedLoanPeriodStatus.FINISHED.name().equals(loanPeriodsDo.getStatus())) {
			// 最后一期结清， 修改loan状态FINISHED
			DsedLoanDo loanDo = new DsedLoanDo();
			loanDo.setRid(loanRepayDealBo.loanDo.getRid());
			loanDo.setStatus(DsedLoanStatus.FINISHED.name());
			loanDo.setGmtModified(new Date());
			loanDo.setGmtFinish(new Date());
			dsedLoanDao.updateById(loanDo);
			logger.info("dealLoanStatus loanDo = "+ loanDo);
			loanRepayDealBo.loanDo.setStatus(DsedLoanStatus.FINISHED.name());

			boolean isBefore = DateUtil.isBefore(new Date(),DateUtil.addDays(loanPeriodsDo.getGmtPlanRepay(), -1));
			if (isBefore) {
				if (assetSideEdspayUtil.isPush(loanRepayDealBo.loanDo)) {
					List<ModifiedBorrowInfoVo> modifiedLoanInfo = assetSideEdspayUtil.buildModifiedInfo(loanRepayDealBo.loanDo,3);
					boolean result = assetSideEdspayUtil.transModifiedBorrowInfo(modifiedLoanInfo,Constants.ASSET_SIDE_EDSPAY_FLAG, Constants.ASSET_SIDE_FANBEI_FLAG);
					if (result) {
						logger.info("dsed transModifiedBorrowInfo success,loanId="+loanRepayDealBo.loanDo.getRid());
					}else{
						assetSideEdspayUtil.transFailRecord(loanRepayDealBo.loanDo, modifiedLoanInfo);
					}
				}
			}

		}
	}

	private void dealSum(LoanRepayDealBo LoanRepayDealBo){

		for (DsedLoanPeriodsDo loanPeriodsDo : LoanRepayDealBo.loanPeriodsDoList) {

			LoanRepayDealBo.sumLoanAmount = LoanRepayDealBo.sumLoanAmount.add(loanPeriodsDo.getAmount());	// 借款本金
			LoanRepayDealBo.sumRepaidAmount = LoanRepayDealBo.sumRepaidAmount.add(loanPeriodsDo.getRepayAmount());	// 还款总额
			LoanRepayDealBo.sumInterest = LoanRepayDealBo.sumInterest.add(loanPeriodsDo.getInterestFee()).add(loanPeriodsDo.getRepaidInterestFee());	// 利息总额
			LoanRepayDealBo.sumPoundage = LoanRepayDealBo.sumPoundage.add(loanPeriodsDo.getServiceFee()).add(loanPeriodsDo.getRepaidServiceFee());	// 手续费总额
			LoanRepayDealBo.sumOverdueAmount = LoanRepayDealBo.sumOverdueAmount.add(loanPeriodsDo.getOverdueAmount()).add(loanPeriodsDo.getRepaidOverdueAmount());	// 逾期费总额
			LoanRepayDealBo.sumIncome = LoanRepayDealBo.sumIncome.add(LoanRepayDealBo.sumPoundage).add(LoanRepayDealBo.sumOverdueAmount).add(LoanRepayDealBo.sumInterest);// 总收入
			LoanRepayDealBo.sumAmount = LoanRepayDealBo.sumLoanAmount.add(LoanRepayDealBo.sumIncome);	// 借款产生总额
		}
	}

	public static class LoanRepayBo{
		public Long userId;

		/* request字段 */
		public BigDecimal repaymentAmount = BigDecimal.ZERO;	// 还款金额
		public BigDecimal actualAmount = BigDecimal.ZERO;
		public BigDecimal rebateAmount = BigDecimal.ZERO; //可选字段
		public BigDecimal reductionAmount = BigDecimal.ZERO; //可选字段
		public List<HashMap> periodsList;
		public String payPwd;
		public Long cardId;
		public Long couponId;			//可选字段
		public Long loanId;
		public List<Long> loanPeriodsIds = new ArrayList<Long>();
		public Long collectionRepaymentId;
		/* request字段 */

		/* biz 业务处理字段 */
		public String remoteIp;
		public String name;
		public String repayType;
		public boolean isAllRepay = false;	// 是否是提前还款（默认false为按期还款）
		/* biz 业务处理字段 */

		/* Response字段 */
		public String cardName;		//交易卡名称
		public String cardNo;		//交易卡号
		public String outTradeNo; 	//资金方放交易流水号
		public String tradeNo;		//我方交易流水号
		/* Response字段 */

		/* 错误码区域 */
		public Exception e;


		public String bankNo;//都市e贷 银行卡卡号

		public int curPeriod;//都市e贷 借款当前期数

		public String borrowNo;//都市e贷 借款分期id

		public BigDecimal amount ; //都市e贷 还款金额
		/* biz 都市e贷业务处理字段 */
		public DsedLoanPeriodsDo loanPeriodsDo;

		public DsedLoanRepaymentDo dsedloanRepaymentDo;

		public DsedUserDo dsedUserDo;

		public DsedLoanDo dsedLoanDo;

		public List<DsedLoanPeriodsDo> dsedLoanPeriodsDoList = new ArrayList<DsedLoanPeriodsDo>();	//借款分期

		public List<Long> dsedLoanPeriodsIds = new ArrayList<Long>();


	}


	/**
	 * 计算提前还款可以减免的金额
	 */
	@Override
	public BigDecimal getDecreasedAmount(String loanNo,Long userId) {
		BigDecimal allRestAmount = BigDecimal.ZERO;
		List<DsedLoanPeriodsDo> noRepayList = dsedLoanPeriodsDao.getNoRepayListByLoanNoAndUserId(loanNo,userId);
		for (DsedLoanPeriodsDo loanPeriodsDo : noRepayList) {
			if(!canRepay(loanPeriodsDo)) { // 未出账
				allRestAmount = BigDecimalUtil.add(allRestAmount,loanPeriodsDo.getInterestFee(), loanPeriodsDo.getServiceFee());
			}
		}
		return allRestAmount;
	}

	@Override
	public DsedLoanRepaymentDo getById(Long id) {
		return dsedLoanRepaymentDao.getById(id);
	}


	/**
	 * 计算提前还款需还金额
	 */
	@Override
	public BigDecimal calculateAllRestAmount(Long loanId) {

		BigDecimal allRestAmount = BigDecimal.ZERO;

		List<DsedLoanPeriodsDo> noRepayList = dsedLoanPeriodsDao.getNoRepayListByLoanId(loanId);

		for (DsedLoanPeriodsDo loanPeriodsDo : noRepayList) {

			if(canRepay(loanPeriodsDo)) { // 已出账
				allRestAmount = BigDecimalUtil.add(allRestAmount,loanPeriodsDo.getAmount(),
						loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
						loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
						loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
						.subtract(loanPeriodsDo.getRepayAmount());

			}else { // 未出账， 提前还款时不用还手续费和利息
				allRestAmount = BigDecimalUtil.add(allRestAmount,loanPeriodsDo.getAmount());
			}

		}

		return allRestAmount;
	}




	/**
	 * 催收逾期还款
	 *
	 * @param
	 */
	@Override
	public void offlineRepay(String loanNo ,Long loanId,String totalAmount,String repaymentNo,Long userId,String type,String repayTime,String orderNo,List<DsedLoanPeriodsDo> list) {
		try {
			DsedLoanDo dsedLoanDo = dsedLoanDao.getByLoanNo(loanNo);

			LoanRepayBo bo = buildLoanRepayBo(userId,dsedLoanDo, loanNo, DsedRepayType.COLLECT.getName(), totalAmount, repaymentNo,list);

			List<DsedLoanPeriodsDo> loanPeriodsDoList = getLoanPeriodsIds(bo.loanId, bo.amount);
			bo.dsedLoanPeriodsDoList = loanPeriodsDoList;

			checkOfflineRepayment(repaymentNo);

			generateRepayRecords(bo);

			dealRepaymentSucess(bo.tradeNo, repaymentNo, bo.dsedloanRepaymentDo,DsedRepayType.COLLECT.getName(),null,null,true);

		}catch (Exception e){
			e.printStackTrace();
		}

	}




	private LoanRepayBo buildLoanRepayBo(Long userId,DsedLoanDo loanDo, String loanNo,
										 String repayType, String repayAmount,
										  String outTradeNo,List<DsedLoanPeriodsDo> periodsList){
		LoanRepayBo bo = new LoanRepayBo();
		bo.userId = loanDo.getUserId();
		bo.dsedUserDo = dsedUserDao.getById(bo.userId);
		bo.amount = new BigDecimal(repayAmount);
		bo.actualAmount =  bo.amount;
		bo.loanId = loanDo.getRid();
		bo.dsedLoanDo = loanDo;
		bo.tradeNo = generatorClusterNo.getOfflineRepaymentBorrowCashNo(new Date());
		bo.outTradeNo = outTradeNo;
		bo.repayType = repayType;
		bo.cardName = "";
		bo.bankNo = "";
		bo.isAllRepay = false;
		bo.name = DsedRepayType.COLLECT.getName();
		return bo;
	}

	private void checkOfflineRepayment(String repaymentNo) {
		if(dsedLoanRepaymentDao.getLoanRepaymentByTradeNo(repaymentNo) != null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_REPEAT_ERROR);
		}
	}




	@Override
	public String getCurrentLastRepayNo(String orderNoPre) {
		return dsedLoanRepaymentDao.getCurrentLastRepayNo(orderNoPre);
	}



	public static class LoanRepayDealBo {
		BigDecimal curRepayAmoutStub; 	//当前还款额变化句柄
		BigDecimal curRebateAmount; 	//当前还款使用的账户余额
		BigDecimal curSumRebateAmount = BigDecimal.ZERO;//当前还款使用的账户余额总额
		Long curUserCouponId;			//当前还款使用的还款优惠卷id
		BigDecimal curSumRepayAmount = BigDecimal.ZERO;	//当前还款总额
		String curCardNo;				//当前还款卡号
		String curCardName;				//当前还款卡别名
		String curName;					//当前还款名称，用来识别自动代付还款
		String curTradeNo;
		String curOutTradeNo;
		BigDecimal curRepayService; 	//当笔还款手续费
		BigDecimal curRepayOverdue; 	//当笔还款逾期费费
		BigDecimal curRepayInterest; 	//当笔还款手续费费

		BigDecimal sumRepaidAmount = BigDecimal.ZERO;	//对应借款的还款总额
		BigDecimal sumAmount = BigDecimal.ZERO;			//对应借款总额（包含所有费用）
		BigDecimal sumLoanAmount = BigDecimal.ZERO;	//对应借款总额（不包含其他费用）
		BigDecimal sumInterest = BigDecimal.ZERO;		//对应借款的利息总额
		BigDecimal sumPoundage = BigDecimal.ZERO;		//对应借款的手续费总额
		BigDecimal sumOverdueAmount = BigDecimal.ZERO;	//对应借款的逾期费总额
		BigDecimal sumIncome = BigDecimal.ZERO;			//对应借款我司产生的总收入

		DsedLoanDo loanDo;							//借款
		List<DsedLoanPeriodsDo> loanPeriodsDoList;							//借款分期
		List<DsedLoanPeriodsDo> newLoanPeriodsDoList;							//借款分期
		DsedLoanRepaymentDo repaymentDo;
		long overdueDay = 0;							//对应借款的逾期天数
		String loanNo;								//借款流水号
		String refId = "";								//还款的id串
		Long userId ;									//目标用户id
		boolean isAllRepay = false;	// 是否是提前还款（默认false为按期还款）
	}
}