package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.KuaijieDsedLoanBo;
import com.ald.fanbei.api.biz.bo.KuaijieLoanBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.DsedUpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.DsedUserBankcardDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanRepaymentDao;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 都市易贷借款还款表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
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
	SmsUtil smsUtil;
	@Resource
	private JpushService pushService;


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
	protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		KuaijieDsedLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieDsedLoanBo.class);
		if (kuaijieLoanBo.getRepayment() != null) {
			changLoanRepaymentStatus(null, AfLoanRepaymentStatus.PROCESSING.name(), kuaijieLoanBo.getRepayment().getRid());
		}
		return getResultMap(kuaijieLoanBo.getBo(),respBo);
	}

	private Map<String, Object> getResultMap(LoanRepayBo bo, UpsCollectRespBo respBo)
	{
		Map<String, Object> data = Maps.newHashMap();
		data.put("rid", bo.loanId);
		data.put("amount", bo.amount.setScale(2, RoundingMode.HALF_UP));
		data.put("gmtCreate", new Date());
		data.put("status", AfLoanRepaymentStatus.SUCC.name());
		data.put("actualAmount", bo.amount);
		data.put("cardName", bo.cardName);
		data.put("cardNumber", bo.cardNo);
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
		if (StringUtils.isNotBlank(payBizObject)) {
			// 处理业务数据
			if (StringUtil.isNotBlank(respBo.getRespCode())) {
				dealRepaymentFail(payTradeNo, respBo.getTradeNo(), true, errorMsg);
			} else {
				dealRepaymentFail(payTradeNo, respBo.getTradeNo(), false, "");
			}
		} else {
			// 未获取到缓存数据，支付订单过期
			throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
		}
	}


	@Override
	protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
//		KuaijieLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieLoanBo.class);
//		if (kuaijieLoanBo.getRepayment() != null) {
//			changLoanRepaymentStatus(null, AfLoanRepaymentStatus.PROCESSING.name(), kuaijieLoanBo.getRepayment().getRid());
//		}
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
	public Map<String, Object> repay(LoanRepayBo bo, String bankPayType) {

		if (!BankPayChannel.KUAIJIE.getCode().equals(bankPayType)) {
			lockRepay(bo.userId);
		}
		if (!canRepay(bo.loanPeriodsDo)) {
			// 未出账时拦截按期还款
			unLockRepay(bo.userId);
			throw new FanbeiException(FanbeiExceptionCode.LOAN_PERIOD_CAN_NOT_REPAY_ERROR);
		}

		Date now = new Date();
//		String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
//		if (StringUtil.equals("sysJob", bo.remoteIp)) {
		String name = Constants.BORROW_REPAYMENT_NAME_AUTO;
//		}

		String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now, bankPayType);
		bo.tradeNo = tradeNo;
		bo.name = name;

		// 增加还款记录
		generateRepayRecords(bo);

		// 还款操作
		return doRepay(bo, bo.dsedloanRepaymentDo, bankPayType);

	}



	/**
	 * @Description:  增加还款记录
	 * @return  void
	 */
	private void generateRepayRecords(LoanRepayBo bo) {
		Date now = new Date();
		String tradeNo = bo.tradeNo;
		String name = bo.name;

		DsedLoanRepaymentDo loanRepaymentDo = buildRepayment( bo.amount, tradeNo, now, bo.amount, 0l,
				null, BigDecimal.ZERO, bo.loanPeriodsDo.getLoanId(), bo.outTradeNo, name, bo.userId,bo.loanPeriodsDo.getPrdType(),bo.bankNo,bo.cardName);

		dsedLoanRepaymentDao.saveRecord(loanRepaymentDo);

		bo.dsedloanRepaymentDo = loanRepaymentDo;

		logger.info("Repay.add repayment finish,name="+ name +",tradeNo="+tradeNo+",borrowRepayment="+ JSON.toJSONString(loanRepaymentDo));
	}

	private DsedLoanRepaymentDo buildRepayment( BigDecimal repaymentAmount, String repayNo, Date gmtCreate, BigDecimal actualAmount,
											 Long userCouponId, BigDecimal couponAmount, BigDecimal rebateAmount, Long loanId, String payTradeNo, String name, Long userId,
												String prdType,String bankNo,String cardName) {
		DsedLoanRepaymentDo loanRepay = new DsedLoanRepaymentDo();
		loanRepay.setUserId(userId);
		loanRepay.setLoanId(loanId);
		loanRepay.setName(name);
		loanRepay.setRepayAmount(repaymentAmount);
		loanRepay.setActualAmount(actualAmount);
		loanRepay.setStatus(AfLoanRepaymentStatus.APPLY.name());
		loanRepay.setTradeNo(repayNo);
		loanRepay.setTradeNoOut(payTradeNo);
		loanRepay.setUserCouponId(userCouponId);
		loanRepay.setCouponAmount(couponAmount);
		loanRepay.setUserAmount(rebateAmount);
		loanRepay.setPreRepayStatus("N");
		loanRepay.setRepayPeriods("");
		loanRepay.setPrdType(prdType);
		loanRepay.setGmtCreate(gmtCreate);
		loanRepay.setCardNo(bankNo);
		loanRepay.setCardName(cardName);

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
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
		}
	}


	private void unLockRepay(Long userId) {
		String key = userId + "_success_dsedLoanRepay";
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
//		if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
//			repayment.setStatus(RepaymentStatus.SMS.getCode());
//			resultMap = sendKuaiJieSms(bank, bo.tradeNo, bo.amount, bo.userId, bo.dsedUserDo.getRealName(),
//					bo.dsedUserDo.getIdNumber(), JSON.toJSONString(bizObject), "dsedLoanRepaymentService", Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_LOAN.getCode());
//		} else {// 代扣
			resultMap = doUpsPay(bankChannel, bank, bo.tradeNo, bo.amount, bo.userId, bo.dsedUserDo.getRealName(),
					bo.dsedUserDo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_LOAN.getCode());
//		}
		return resultMap;
	}


	/**
	 * 还款失败后调用
	 */
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice,String errorMsg) {
		final DsedLoanRepaymentDo loanRepaymentDo = dsedLoanRepaymentDao.getRepayByTradeNo(tradeNo);
		logger.info("dealRepaymentFail process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo
				+ ",isNeedMsgNotice=" + isNeedMsgNotice + ",errorMsg=" + errorMsg
				+ ",borrowRepayment=" + JSON.toJSONString(loanRepaymentDo));

		if ((loanRepaymentDo != null && AfLoanRepaymentStatus.SUCC.name().equals(loanRepaymentDo.getStatus()) )) { // 检查交易流水 对应记录数据库中是否已经处理
			return;
		}

		if(loanRepaymentDo != null) {
			changLoanRepaymentStatus(outTradeNo, AfLoanRepaymentStatus.FAIL.name(), loanRepaymentDo.getRid());
		}

		// 解锁还款
		unLockRepay(loanRepaymentDo.getUserId());

		if(isNeedMsgNotice){
		//通知还款成功

		}else{
		//通知还款失败

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
		public AfLoanRepaymentDo loanRepaymentDo;
		public AfLoanDo loanDo;
		public List<AfLoanPeriodsDo> loanPeriodsDoList = new ArrayList<AfLoanPeriodsDo>();	//借款分期
		public AfUserCouponDto userCouponDto; 	//可选字段
		public AfUserAccountDo userDo;
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





	}

}