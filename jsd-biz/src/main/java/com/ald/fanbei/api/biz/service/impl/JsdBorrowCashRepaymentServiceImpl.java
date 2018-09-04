package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.KuaijieRepayBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.JsdBorrowCashRepaymentDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 极速贷ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdBorrowCashRepaymentService")
public class JsdBorrowCashRepaymentServiceImpl extends JsdUpsPayKuaijieServiceAbstract implements  JsdBorrowCashRepaymentService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdBorrowCashRepaymentServiceImpl.class);

    @Resource
    private JsdBorrowCashRepaymentDao jsdBorrowCashRepaymentDao;

    @Resource
	private JsdBorrowLegalOrderCashDao jsdBorrowLegalOrderCashDao;
    @Resource
	private JsdUserBankcardDao jsdUserBankcardDao;

    @Resource
	private JsdUserDao jsdUserDao;

    @Resource
	private JsdBorrowCashDao jsdBorrowCashDao;

    @Resource
	private JsdBorrowLegalOrderRepaymentDao jsdBorrowLegalOrderRepaymentDao;
	@Resource
	RedisTemplate<String, ?> redisTemplate;

	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	private JsdNoticeRecordDao jsdNoticeRecordDao;
    @Resource
	XgxyUtil xgxyUtil;
	@Override
	public String getCurrentLastRepayNo(String orderNoPre) {
		return jsdBorrowCashRepaymentDao.getCurrentLastRepayNo(orderNoPre);
	}

	@Override
	public Map<String, Object> repay(BorrowCashRepayBo bo, String bankPayType) {

		if (!BankPayChannel.KUAIJIE.getCode().equals(bankPayType)) {
			lockRepay(bo.userId);
		}

		Date now = new Date();
		String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
		if(StringUtil.equals("sysJob",bo.remoteIp)){
			name = Constants.BORROW_REPAYMENT_NAME_AUTO;
		}
		String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now,bankPayType);
		bo.tradeNo = tradeNo;
		bo.name = name;
		generateRepayRecords(bo);
		return doRepay(bo,bankPayType);
}
	private Map<String, Object> doRepay(BorrowCashRepayBo bo,String bankChannel) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HashMap bank = jsdUserBankcardDao.getPayTypeByBankNoAndUserId(bo.userId,bo.bankNo);
		KuaijieRepayBo bizObject = new KuaijieRepayBo(bo.repaymentDo,bo);
		if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
			resultMap = sendKuaiJieSms(bank, bo.tradeNo, bo.amount, bo.userId, bo.userDo.getRealName(), bo.userDo.getIdNumber(),
					JSON.toJSONString(bizObject), "jsdBorrowCashRepaymentService", Constants.DEFAULT_PAY_PURPOSE,bo.name, PayOrderSource.REPAY_JSD.getCode());
		} else {// 代扣
			resultMap = doUpsPay(bankChannel, bank, bo.tradeNo, bo.amount, bo.userId, bo.userDo.getRealName(),
					bo.userDo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_JSD.getCode());
		}
		return resultMap;
	}

	private void generateRepayRecords(BorrowCashRepayBo bo) {
		Date now = new Date();
		String name = bo.name;
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(bo.borrowId);
		JsdBorrowCashRepaymentDo borrowRepaymentDo = null;
		JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = null;
		BigDecimal orderSumAmount=BigDecimal.ZERO;
		BigDecimal orderRemainShouldRepayAmount=BigDecimal.ZERO;
		if(orderCashDo!=null){
			bo.borrowOrderId = orderCashDo.getBorrowLegalOrderId();
			bo.borrowLegalOrderCashId=orderCashDo.getRid();
			orderSumAmount = orderCashDo.getAmount()
					.add(orderCashDo.getOverdueAmount()).add(orderCashDo.getSumRepaidOverdue())
					.add(orderCashDo.getPoundageAmount()).add(orderCashDo.getSumRepaidPoundage())
					.add(orderCashDo.getInterestAmount()).add(orderCashDo.getSumRepaidInterest());
			orderRemainShouldRepayAmount = orderSumAmount.subtract(orderCashDo.getRepaidAmount()); //剩余应还金额
		}
		BigDecimal borrowRepayAmount = bo.amount.subtract(orderRemainShouldRepayAmount);
		if(borrowRepayAmount.compareTo(BigDecimal.ZERO) > 0) { //还款额大于订单应还总额，拆分还款
			borrowRepaymentDo = buildRepayment( borrowRepayAmount, bo.repayNo, now, borrowRepayAmount,
					 bo.borrowId,  bo.tradeNo, name, bo.userId,bo.repayType,bo.bankNo);
			jsdBorrowCashRepaymentDao.saveRecord(borrowRepaymentDo);
			bo.repaymentDo=borrowRepaymentDo;
			if(orderCashDo!=null){
				if(!JsdBorrowLegalOrderCashStatus.FINISHED.getCode().equals(orderCashDo.getStatus())) {
					orderRepaymentDo = buildOrderRepayment(bo, borrowRepayAmount,
							orderRemainShouldRepayAmount);
					jsdBorrowLegalOrderRepaymentDao.saveRecord(orderRepaymentDo);
					bo.orderRepaymentDo=orderRepaymentDo;
				}
			}

		} else { //还款全部进入订单欠款中
			orderRepaymentDo = buildOrderRepayment(bo, bo.amount,
					orderRemainShouldRepayAmount);
			jsdBorrowLegalOrderRepaymentDao.saveRecord(orderRepaymentDo);
		}
		bo.repaymentDo=borrowRepaymentDo;
		bo.orderRepaymentDo=orderRepaymentDo;
		logger.info("Repay.add repayment finish,name="+ name +"tradeNo="+bo.repayNo+",borrowRepayment="+ JSON.toJSONString(borrowRepaymentDo) + ",legalOrderRepayment="+ JSON.toJSONString(orderRepaymentDo));
	}

	private JsdBorrowLegalOrderRepaymentDo buildOrderRepayment(BorrowCashRepayBo bo, BigDecimal actualAmountForOrder, BigDecimal repayAmount) {
		JsdBorrowLegalOrderRepaymentDo repayment = new JsdBorrowLegalOrderRepaymentDo();

		repayment.setUserId(bo.userId);
		repayment.setBorrowLegalOrderCashId(bo.borrowLegalOrderCashId);
		repayment.setRepayAmount(repayAmount);
		repayment.setActualAmount(actualAmountForOrder);
		repayment.setName(bo.name);
		repayment.setTradeNo(bo.repayNo);
		repayment.setStatus(JsdBorrowLegalRepaymentStatus.APPLY.getCode());
		repayment.setCardNo(bo.bankNo);
		HashMap bank=jsdUserBankcardDao.getPayTypeByBankNoAndUserId(bo.userId,bo.bankNo);
        repayment.setCardName((String) bank.get("bankChannel"));
		Date now = new Date();
		repayment.setGmtCreate(now);
		repayment.setGmtModified(now);
		repayment.setBorrowId(bo.borrowId);
		repayment.setCardNo(bo.bankNo);
		return repayment;
	}

	private JsdBorrowCashRepaymentDo buildRepayment( BigDecimal repaymentAmount, String repayNo, Date gmtCreate,
												   BigDecimal actualAmountForBorrow,
												   Long borrowId,  String TradeNo, String name, Long userId,String repayType,String cardNo) {
		JsdBorrowCashRepaymentDo repay = new JsdBorrowCashRepaymentDo();
		repay.setActualAmount(actualAmountForBorrow);
		repay.setBorrowId(borrowId);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(JsdBorrowCashRepaymentStatus.APPLY.getCode());
		repay.setName(name);
		repay.setUserId(userId);
		repay.setCardNumber(cardNo);
		repay.setType(repayType);
		repay.setJsdRepayNo(TradeNo);
		HashMap bank=jsdUserBankcardDao.getPayTypeByBankNoAndUserId(userId,cardNo);
		repay.setCardName((String) bank.get("bankName"));
		return repay;
	}

	/**
	 * 锁住还款
	 */
	private void lockRepay(Long userId) {
		String key = userId + "_success_loanRepay";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 300, TimeUnit.SECONDS);
		if (count != 1) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
		}
	}

	private void unLockRepay(Long userId) {
		String key = userId + "_success_loanRepay";
		redisTemplate.delete(key);
	}

	@Override
	public JsdBorrowCashRepaymentDo getLastRepaymentBorrowCashByBorrowId(Long borrowId) {
		return jsdBorrowCashRepaymentDao.getLastRepaymentBorrowCashByBorrowId(borrowId);
	}

	@Override
	protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo) {
		KuaijieRepayBo kuaijieBo = JSON.parseObject(payBizObject, KuaijieRepayBo.class);
		if (kuaijieBo.getRepayment() != null) {
		}
	}

	@Override
	protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {

	}

	@Override
	protected void kuaijieConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {

	}

	@Override
	protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		KuaijieRepayBo kuaijieRepaymentBo = JSON.parseObject(payBizObject, KuaijieRepayBo.class);
		if(kuaijieRepaymentBo.getRepayment()!=null){
			jsdBorrowCashRepaymentDao.status2Process(payTradeNo, kuaijieRepaymentBo.getRepayment().getRid());// 更新状态
		}
		jsdBorrowLegalOrderRepaymentDao.updateStatus(payTradeNo);
		return getResultMap(kuaijieRepaymentBo.getBo(), respBo,bankChannel);
	}

	private Map<String, Object> getResultMap(BorrowCashRepayBo bo, UpsCollectRespBo respBo,String bankChannel) {
		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("rid", bo.borrowId);
//		data.put("amount", bo.repaymentAmount.setScale(2, RoundingMode.HALF_UP));
//		data.put("gmtCreate", new Date());
//		data.put("status", JsdBorrowCashRepaymentStatus.YES.getCode());
//		data.put("actualAmount", bo.actualAmount);
//		data.put("cardName", bo.cardName);
//		data.put("cardNumber", bo.cardNo);
//		data.put("repayNo", bo.repayNo);
//		if (respBo != null) {
//			data.put("outTradeNo", respBo.getTradeNo());
//		}
		if(!BankPayChannel.KUAIJIE.getCode().equals(bankChannel)){
			data.put("repaySMS", "N");
		}
		return data;
	}
	@Override
	public JsdBorrowCashRepaymentDo getLastByBorrowId(Long borrowId) {
		return jsdBorrowCashRepaymentDao.getLastByBorrowId(borrowId);
	}

	@Override
	protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
		if (StringUtils.isNotBlank(payBizObject)) {
			// 处理业务数据
			if (StringUtil.isNotBlank(respBo.getRespCode())) {
				dealRepaymentFail(payTradeNo, "", true, respBo.getRespCode(),errorMsg);
			} else {
				dealRepaymentFail(payTradeNo, "", false, "","UPS响应码为空");
			}
		} else {
			// 未获取到缓存数据，支付订单过期
			throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
		}
	}
	/**
	 * 还款失败后调用
	 */
	@Override
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice,String code,String errorMsg) {
		final JsdBorrowCashRepaymentDo repaymentDo = jsdBorrowCashRepaymentDao.getRepaymentByPayTradeNo(tradeNo);
		final JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = jsdBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByTradeNo(tradeNo);
		logger.info("dealRepaymentFail process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo
				+ ",isNeedMsgNotice=" + isNeedMsgNotice + ",errorMsg=" + errorMsg
				+ ",borrowRepayment=" + JSON.toJSONString(repaymentDo) + ", orderRepayment=" + JSON.toJSONString(orderRepaymentDo));

		if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) ) // 检查交易流水 对应记录数据库中是否已经处理
				|| (orderRepaymentDo != null && YesNoStatus.YES.getCode().equals(orderRepaymentDo.getStatus()) )) {
			return;
		}

		if(repaymentDo != null) {
			changBorrowRepaymentStatus(outTradeNo, JsdBorrowCashRepaymentStatus.NO.getCode(), repaymentDo.getRid(),code,errorMsg);
		}
		if(orderRepaymentDo != null) {
			changOrderRepaymentStatus(outTradeNo, JsdBorrowLegalRepaymentStatus.NO.getCode(), orderRepaymentDo.getRid());
		}

		// 解锁还款
		if(repaymentDo!=null){
			unLockRepay(repaymentDo.getUserId());
		}

	}

	@Override
	public void dealRepaymentSucess(String repayNo, String outTradeNo) {
		final JsdBorrowCashRepaymentDo repaymentDo = jsdBorrowCashRepaymentDao.getRepaymentByPayTradeNo(repayNo);
		final JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = jsdBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByTradeNo(repayNo);
		dealRepaymentSucess(repayNo, outTradeNo, repaymentDo, orderRepaymentDo,false,null);
	}

	@Override
	public JsdBorrowCashRepaymentDo getByRepayNo(String repayNo) {
		return jsdBorrowCashRepaymentDao.getByRepayNo(repayNo);
	}

	public void dealRepaymentSucess(String tradeNo, String outTradeNo, final JsdBorrowCashRepaymentDo repaymentDo, final JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, Boolean flag, final String isBalance) {
		try {
			lock(tradeNo);

			logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo) + ", orderRepayment=" + JSON.toJSONString(orderRepaymentDo));

			preCheck(repaymentDo, orderRepaymentDo, tradeNo);
			final RepayDealBo repayDealBo = new RepayDealBo();
			repayDealBo.curTradeNo = tradeNo;
			repayDealBo.curOutTradeNo = outTradeNo;

			long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
				@Override
				public Long doInTransaction(TransactionStatus status) {
					try {
						dealOrderRepay(repayDealBo, orderRepaymentDo,isBalance);
						if(repaymentDo!=null){
							dealBorrowRepay(repayDealBo, repaymentDo,isBalance);
						}
//						dealSum(repayDealBo);
						return 1L;
					} catch (Exception e) {
						status.setRollbackOnly();
						logger.info("dealRepaymentSucess error", e);
						throw e;
					}
				}
			});

			if (resultValue == 1L) {
				try {
					repaymentDo.setStatus(DsedLoanRepaymentStatus.SUCC.name());
					//还款成功，调用西瓜信用通知接口
					JsdNoticeRecordDo noticeRecordDo = new JsdNoticeRecordDo();
					noticeRecordDo.setUserId(repaymentDo.getUserId());
					noticeRecordDo.setRefId(String.valueOf(repaymentDo.getRid()));
					noticeRecordDo.setType(getStatus(repaymentDo.getBorrowId()));
					noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
					HashMap data = buildData(repaymentDo);
					noticeRecordDo.setParams(JSON.toJSONString(data));
					jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
					try {
//						if(!flag){
//							//app逾期还款通知催收
//							nofityRisk(loanRepayDealBo,repaymentDo);
//						}
//						//催收逾期还款
//						collectRisk(loanRepayDealBo.loanPeriodsDoList,repaymentDo,loanRepayDealBo.loanDo.getRid());
					}catch (Exception e){
						e.printStackTrace();
					}
					logger.info("dealRepaymentSucess data cfp "+JSON.toJSONString(data));
					if(xgxyUtil.dsedRePayNoticeRequest(data)){
						noticeRecordDo.setRid(noticeRecordDo.getRid());
						noticeRecordDo.setGmtModified(new Date());
						jsdNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
					}
				}
				catch (Exception e){
					logger.error("notice eca fail error=",e);
				}
			}

		}finally {
			unLock(tradeNo);

			// 解锁还款
			unLockRepay(repaymentDo.getUserId());
		}
	}

	private HashMap buildData(JsdBorrowCashRepaymentDo repaymentDo){
		    HashMap<String,String> map=new HashMap<>();
		    JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(repaymentDo.getBorrowId());
		    map.put("repayNo",repaymentDo.getRepayNo());
			map.put("status",YesNoStatus.YES.getCode());
			map.put("tradeNo",repaymentDo.getTradeNo());
			map.put("borrowNo",borrowCashDo.getBorrowNo());
			map.put("period","1");
			map.put("amount", String.valueOf(repaymentDo.getActualAmount()));
			map.put("type",repaymentDo.getType());
			Date now=new Date();
		    map.put("timestamp", String.valueOf(now.getTime()));
		    if(JsdBorrowCashStatus.FINISHED.getCode().equals(borrowCashDo.getStatus())){
				map.put("isFinish",YesNoStatus.YES.getCode());
			}else {
				map.put("isFinish",YesNoStatus.NO.getCode());
			}
            return map;
	}

	public String getStatus (Long borrowId){
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(borrowId);
		if(borrowCashDo!=null){
			if(StringUtil.equals(borrowCashDo.getOverdueStatus(), YesNoStatus.YES.getCode())){
				if(StringUtil.equals(borrowCashDo.getType(), JsdRepayType.COLLECT.getName())){
					return DsedNoticeType.XGXY_COLLECT.code;
				}else {
					return DsedNoticeType.XGXY_OVERDUEREPAY.code;
				}
			}
		}
		return DsedNoticeType.REPAY.code;
	}
	private void dealSum(RepayDealBo repayDealBo){
		JsdBorrowLegalOrderCashDo orderCashDo = repayDealBo.orderCashDo;
		JsdBorrowCashDo cashDo = repayDealBo.cashDo;

		repayDealBo.sumBorrowAmount = repayDealBo.sumBorrowAmount.add(orderCashDo.getAmount());
		repayDealBo.sumRepaidAmount = repayDealBo.sumRepaidAmount.add(orderCashDo.getRepaidAmount());
		repayDealBo.sumInterest = repayDealBo.sumInterest.add(orderCashDo.getInterestAmount()).add(orderCashDo.getSumRepaidInterest());
		repayDealBo.sumPoundage = repayDealBo.sumPoundage.add(orderCashDo.getPoundageAmount()).add(orderCashDo.getSumRepaidPoundage());
		repayDealBo.sumOverdueAmount = repayDealBo.sumOverdueAmount.add(orderCashDo.getOverdueAmount()).add(orderCashDo.getSumRepaidOverdue());
		repayDealBo.sumIncome = repayDealBo.sumIncome.add(repayDealBo.sumPoundage).add(repayDealBo.sumOverdueAmount).add(repayDealBo.sumInterest);

		repayDealBo.sumBorrowAmount = repayDealBo.sumBorrowAmount.add(cashDo.getAmount());
		repayDealBo.sumRepaidAmount = repayDealBo.sumRepaidAmount.add(cashDo.getRepayAmount());
		repayDealBo.sumInterest = repayDealBo.sumInterest.add(cashDo.getRateAmount()).add(cashDo.getSumRate());
		repayDealBo.sumPoundage = repayDealBo.sumPoundage.add(cashDo.getPoundage()).add(cashDo.getSumRenewalPoundage());
		repayDealBo.sumOverdueAmount = repayDealBo.sumOverdueAmount.add(cashDo.getOverdueAmount()).add(cashDo.getSumOverdue());
		repayDealBo.sumIncome = repayDealBo.sumIncome.add(repayDealBo.sumPoundage).add(repayDealBo.sumOverdueAmount).add(repayDealBo.sumInterest);

		repayDealBo.sumAmount = repayDealBo.sumBorrowAmount.add(repayDealBo.sumIncome);
	}

	/**
	 * 需在事务管理块中调用此函数!
	 * @param repayDealBo
	 * @param repaymentDo
	 */
	private void dealBorrowRepay(RepayDealBo repayDealBo, JsdBorrowCashRepaymentDo repaymentDo,String isBalance) {
		if(repaymentDo == null) return;

		JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(repaymentDo.getBorrowId());
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(cashDo.getRid());//
		cashDo.setRemark("");
		repayDealBo.curRepayAmoutStub = repaymentDo.getRepaymentAmount();
		repayDealBo.curSumRepayAmount = repayDealBo.curSumRepayAmount.add(repaymentDo.getRepaymentAmount());
		repayDealBo.curCardNo = repaymentDo.getCardNumber();
		repayDealBo.curCardName = repaymentDo.getCardName();
		repayDealBo.curName = repaymentDo.getName();

		repayDealBo.cashDo = cashDo;
		repayDealBo.orderCashDo = orderCashDo;
		repayDealBo.overdueDay = cashDo.getOverdueDay();
		repayDealBo.borrowNo = cashDo.getBorrowNo();
		repayDealBo.refId += repaymentDo.getBorrowId();
		repayDealBo.userId = cashDo.getUserId();

		dealBorrowRepayOverdue(repayDealBo, cashDo);//逾期费
		dealBorrowRepayPoundage(repayDealBo, cashDo);//手续费
		dealBorrowRepayInterest(repayDealBo, cashDo);//利息

		changBorrowRepaymentStatus(repayDealBo.curOutTradeNo, JsdBorrowCashRepaymentStatus.YES.getCode(), repaymentDo.getRid(),"","");

		dealBorrowRepayIfFinish(repayDealBo, repaymentDo, cashDo,isBalance);
		jsdBorrowCashDao.updateById(cashDo);
	}


	private void dealBorrowRepayOverdue(RepayDealBo repayDealBo, JsdBorrowCashDo afBorrowCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal overdueAmount = afBorrowCashDo.getOverdueAmount();

		if (repayAmount.compareTo(overdueAmount) > 0) {
			afBorrowCashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), overdueAmount));
			afBorrowCashDo.setOverdueAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(overdueAmount);
		} else {
			afBorrowCashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), repayAmount));
			afBorrowCashDo.setOverdueAmount(overdueAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}
	private void dealBorrowRepayPoundage(RepayDealBo repayDealBo, JsdBorrowCashDo afBorrowCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal poundageAmount = afBorrowCashDo.getPoundage();

		if (repayAmount.compareTo(poundageAmount) > 0) {
			afBorrowCashDo.setSumRenewalPoundage(BigDecimalUtil.add(afBorrowCashDo.getSumRenewalPoundage(), poundageAmount));
			afBorrowCashDo.setPoundage(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(poundageAmount);
		} else {
			afBorrowCashDo.setSumRenewalPoundage(BigDecimalUtil.add(afBorrowCashDo.getSumRenewalPoundage(), repayAmount));
			afBorrowCashDo.setPoundage(poundageAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}
	private void dealBorrowRepayInterest(RepayDealBo repayDealBo, JsdBorrowCashDo afBorrowCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal rateAmount = afBorrowCashDo.getRateAmount();

		if (repayAmount.compareTo(rateAmount) > 0) {
			afBorrowCashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), rateAmount));
			afBorrowCashDo.setRateAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(rateAmount);
		} else {
			afBorrowCashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), repayAmount));
			afBorrowCashDo.setRateAmount(rateAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}
	private void dealBorrowRepayIfFinish(RepayDealBo repayDealBo, JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowCashDo cashDo,String isBalance) {
		BigDecimal sumAmount = BigDecimalUtil.add(cashDo.getAmount(),
				cashDo.getOverdueAmount(), cashDo.getSumOverdue(),
				cashDo.getRateAmount(), cashDo.getSumRate(),
				cashDo.getPoundage(), cashDo.getSumRenewalPoundage());
		BigDecimal allRepayAmount = cashDo.getRepayAmount().add(repaymentDo.getRepaymentAmount());
		cashDo.setRepayAmount(allRepayAmount);
		cashDo.setType(JsdRepayType.ONLINE.getCode());
		if (sumAmount.compareTo(allRepayAmount) <= 0) {
			cashDo.setStatus(JsdBorrowCashStatus.FINISHED.getCode());
			cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
		}else if (YesNoStatus.YES.getCode().equals(isBalance)){//线下还款平账
			cashDo.setStatus(JsdBorrowCashStatus.FINISHED.getCode());
			cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
		}
	}


	/**
	 * 需在事务管理块中调用此函数!
	 * @param repayDealBo
	 * @param orderRepaymentDo
	 */
	private void dealOrderRepay(RepayDealBo repayDealBo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo,String isBalance) {
		if(orderRepaymentDo == null) return;

		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getById(orderRepaymentDo.getBorrowLegalOrderCashId());
		JsdBorrowCashDo cashDo =jsdBorrowCashDao.getById(orderCashDo.getBorrowId());

		repayDealBo.curRepayAmoutStub = orderRepaymentDo.getRepayAmount();
		repayDealBo.curSumRepayAmount = repayDealBo.curSumRepayAmount.add(orderRepaymentDo.getRepayAmount());
		repayDealBo.curCardNo = orderRepaymentDo.getCardNo();
		repayDealBo.curCardName = orderRepaymentDo.getCardName();
		repayDealBo.curName = orderRepaymentDo.getName();

		repayDealBo.cashDo = cashDo;
		repayDealBo.orderCashDo = orderCashDo;
		repayDealBo.overdueDay = cashDo.getOverdueDay();
		repayDealBo.borrowNo = cashDo.getBorrowNo();
		repayDealBo.refId += orderCashDo.getRid();
		repayDealBo.userId = cashDo.getUserId();
		repayDealBo.renewalNum = cashDo.getRenewalNum();

		dealOrderRepayOverdue(repayDealBo, orderCashDo);//逾期费
		dealOrderRepayPoundage(repayDealBo, orderCashDo);//手续费
		dealOrderRepayInterest(repayDealBo, orderCashDo);//利息

		dealOrderRepayIfFinish(repayDealBo, orderRepaymentDo, orderCashDo,isBalance);
		jsdBorrowLegalOrderCashDao.updateById(orderCashDo);

		changOrderRepaymentStatus(repayDealBo.curOutTradeNo, JsdBorrowLegalRepaymentStatus.YES.getCode(), orderRepaymentDo.getRid());
	}
	private void dealOrderRepayIfFinish(RepayDealBo repayDealBo, JsdBorrowLegalOrderRepaymentDo orderRepaymentBo, JsdBorrowLegalOrderCashDo orderCashDo,String isBalance) {
		BigDecimal sumAmount = BigDecimalUtil.add(orderCashDo.getAmount(),
				orderCashDo.getOverdueAmount(),orderCashDo.getSumRepaidOverdue(),
				orderCashDo.getPoundageAmount(),orderCashDo.getSumRepaidPoundage(),
				orderCashDo.getInterestAmount(),orderCashDo.getSumRepaidInterest());
		BigDecimal allRepayAmount = orderCashDo.getRepaidAmount().add(orderRepaymentBo.getRepayAmount());
		Date now = new Date();
		orderCashDo.setRepaidAmount(allRepayAmount);
		orderCashDo.setGmtLastRepayment(now);

		if (sumAmount.compareTo(allRepayAmount) == 0) {
			orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.FINISHED.getCode());
			orderCashDo.setGmtFinish(now);
		}else if (YesNoStatus.YES.getCode().equals(isBalance)){//线下还款平账
			orderCashDo.setStatus(JsdBorrowCashStatus.FINISHED.getCode());
			orderCashDo.setGmtFinish(now);
		} else {
			orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.PART_REPAID.getCode());
		}
	}
	private void preCheck(JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, String tradeNo) {
		// 检查交易流水 对应记录数据库中是否已经处理
		if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) )
				|| (orderRepaymentDo != null && YesNoStatus.YES.getCode().equals(orderRepaymentDo.getStatus()) )) {
			throw new FanbeiException("preCheck,repayment has been dealed!"); // TODO
		}

	}
	private void dealOrderRepayOverdue(RepayDealBo repayDealBo, JsdBorrowLegalOrderCashDo orderCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal overdueAmount = orderCashDo.getOverdueAmount();

		if (repayAmount.compareTo(overdueAmount) > 0) {
			orderCashDo.setSumRepaidOverdue(BigDecimalUtil.add(orderCashDo.getSumRepaidOverdue(), overdueAmount));
			orderCashDo.setOverdueAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(overdueAmount);
		} else {
			orderCashDo.setSumRepaidOverdue(BigDecimalUtil.add(orderCashDo.getSumRepaidOverdue(), repayAmount));
			orderCashDo.setOverdueAmount(overdueAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}

	private void dealOrderRepayPoundage(RepayDealBo repayDealBo, JsdBorrowLegalOrderCashDo orderCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal poundageAmount = orderCashDo.getPoundageAmount();

		if (repayAmount.compareTo(poundageAmount) > 0) {
			orderCashDo.setSumRepaidPoundage(BigDecimalUtil.add(orderCashDo.getSumRepaidPoundage(), poundageAmount));
			orderCashDo.setPoundageAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(poundageAmount);
		} else {
			orderCashDo.setSumRepaidPoundage(BigDecimalUtil.add(orderCashDo.getSumRepaidPoundage(), repayAmount));
			orderCashDo.setPoundageAmount(poundageAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}
	private void dealOrderRepayInterest(RepayDealBo repayDealBo, JsdBorrowLegalOrderCashDo orderCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal rateAmount = orderCashDo.getInterestAmount();

		if (repayAmount.compareTo(rateAmount) > 0) {
			orderCashDo.setSumRepaidInterest(BigDecimalUtil.add(orderCashDo.getSumRepaidInterest(), rateAmount));
			orderCashDo.setInterestAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(rateAmount);
		} else {
			orderCashDo.setSumRepaidInterest(BigDecimalUtil.add(orderCashDo.getSumRepaidInterest(), repayAmount));
			orderCashDo.setInterestAmount(rateAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
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

	private long changBorrowRepaymentStatus(String outTradeNo, String status, Long rid,String code,String msg) {
		JsdBorrowCashRepaymentDo repayment = new JsdBorrowCashRepaymentDo();
		repayment.setStatus(status);
		repayment.setTradeNo(outTradeNo);
		repayment.setRid(rid);
		JsdBorrowCashRepaymentDo jsdBorrowCashRepaymentDo=jsdBorrowCashRepaymentDao.getById(rid);
		if(!YesNoStatus.NO.getCode().equals(jsdBorrowCashRepaymentDo.getStatus())){
			repayment.setFailCode(code);
			repayment.setFailMsg(msg);
		}
		return jsdBorrowCashRepaymentDao.updateRepaymentBorrowCash(repayment);
	}
	private long changOrderRepaymentStatus(String outTradeNo, String status, Long rid) {
		JsdBorrowLegalOrderRepaymentDo repayment = new JsdBorrowLegalOrderRepaymentDo();
		repayment.setStatus(status);
		repayment.setTradeNoUps(outTradeNo);
		repayment.setRid(rid);
		return jsdBorrowLegalOrderRepaymentDao.updateBorrowLegalOrderRepayment(repayment);
	}
	public static class BorrowCashRepayBo{
		public Long userId;

		/* request字段 */
		public BigDecimal repaymentAmount = BigDecimal.ZERO;	// 还款金额
		public BigDecimal actualAmount = BigDecimal.ZERO;
		public BigDecimal rebateAmount = BigDecimal.ZERO; //可选字段
		public BigDecimal reductionAmount = BigDecimal.ZERO; //可选字段
		public String payPwd;
		public Long cardId;
		public String borrowNo;
		public Long collectionRepaymentId;
		/* request字段 */

		/* biz 业务处理字段 */
		public String remoteIp;
		public String name;
		public String repayType;
		/* biz 业务处理字段 */

		/* Response字段 */
		public String cardName;		//交易卡名称
		public String cardNo;		//交易卡号
		public String outTradeNo; 	//资金方放交易流水号
		public String tradeNo;		//我方交易流水号
		/* Response字段 */
		public Long borrowId;			//可选字段

		/* 错误码区域 */
		public Exception e;

		public Long borrowOrderId; 		//可选字段

		public Long borrowLegalOrderCashId; 		//可选字段


		public String bankNo;//都市e贷 银行卡卡号

		public int period;//都市e贷 借款当前期数

		public BigDecimal amount ; //都市e贷 还款金额

		public String repayNo;

		public Long timestamp;
		public JsdUserDo userDo;

		public String payType;

		public JsdBorrowLegalOrderCashDo orderCashDo;

		public JsdBorrowCashRepaymentDo repaymentDo;

		public JsdBorrowLegalOrderRepaymentDo orderRepaymentDo;

		public JsdBorrowCashDo borrowCashDo;




	}


	public static class RepayDealBo {
		BigDecimal curRepayAmoutStub; 	//当前还款额变化句柄
		BigDecimal curSumRepayAmount = BigDecimal.ZERO;	//当前还款总额(借款还款+订单还款)
		String curCardNo;				//当前还款卡号
		String curCardName;				//当前还款卡别名
		String curName;					//当前还款名称，用来识别自动代付还款
		String curTradeNo;
		String curOutTradeNo;

		BigDecimal sumRepaidAmount = BigDecimal.ZERO;	//对应借款的还款总额
		BigDecimal sumAmount = BigDecimal.ZERO;			//对应借款总额（包含所有费用）
		BigDecimal sumBorrowAmount = BigDecimal.ZERO;	//对应借款总额（不包含其他费用）
		BigDecimal sumInterest = BigDecimal.ZERO;		//对应借款的利息总额
		BigDecimal sumPoundage = BigDecimal.ZERO;		//对应借款的手续费总额
		BigDecimal sumOverdueAmount = BigDecimal.ZERO;	//对应借款的逾期费总额
		BigDecimal sumIncome = BigDecimal.ZERO;			//对应借款我司产生的总收入

		JsdBorrowCashDo cashDo;							//借款
		JsdBorrowLegalOrderCashDo orderCashDo;			//订单借款
		long overdueDay = 0;								//对应借款的逾期天数
		String borrowNo;								//借款流水号
		String refId = "";								//还款的id串
		Long userId ;									//目标用户id
		Long renewalNum;                            //续借次数
	}

}