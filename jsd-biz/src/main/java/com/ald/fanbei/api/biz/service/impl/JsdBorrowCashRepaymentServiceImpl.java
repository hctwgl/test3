package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.KuaijieDsedLoanBo;
import com.ald.fanbei.api.biz.bo.KuaijieRepayBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
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

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRepaymentDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
public class JsdBorrowCashRepaymentServiceImpl extends DsedUpsPayKuaijieServiceAbstract implements  JsdBorrowCashRepaymentService {
	
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
	private JsdBorrowLegalOrderRepaymentDao jsdBorrowLegalOrderRepaymentDao;
	@Resource
	RedisTemplate<String, ?> redisTemplate;

	@Resource
	GeneratorClusterNo generatorClusterNo;
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

		return doRepay(bo,bo.repaymentDo,bankPayType);
	}
	private Map<String, Object> doRepay(BorrowCashRepayBo bo,JsdBorrowCashRepaymentDo repayment ,String bankChannel) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HashMap bank = jsdUserBankcardDao.getBankByBankNoAndUserId(bo.userId,bo.bankNo);
		KuaijieRepayBo bizObject = new KuaijieRepayBo();
		if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
			repayment.setStatus(JsdBorrowCashRepaymentStatus.SMS.getCode());
			resultMap = sendKuaiJieSms(bank, bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(), bo.userDo.getIdNumber(),
					JSON.toJSONString(bizObject), "afBorrowLegalRepaymentService", Constants.DEFAULT_PAY_PURPOSE,bo.name, PayOrderSource.REPAY_CASH_LEGAL.getCode());
		} else {// 代扣
			resultMap = doUpsPay(bankChannel, bank, bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(),
					bo.userDo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_CASH_LEGAL.getCode());
		}
		return resultMap;
	}

	private void generateRepayRecords(BorrowCashRepayBo bo) {
		Date now = new Date();
		String tradeNo = bo.tradeNo;
		String name = bo.name;
		JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(bo.borrowId);
		bo.borrowOrderId = orderCashDo.getBorrowLegalOrderId();
		bo.borrowLegalOrderCashId=orderCashDo.getRid();
		JsdBorrowCashRepaymentDo borrowRepaymentDo = null;
		JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = null;
		BigDecimal orderSumAmount = orderCashDo.getAmount()
				.add(orderCashDo.getOverdueAmount()).add(orderCashDo.getSumRepaidOverdue())
				.add(orderCashDo.getPoundageAmount()).add(orderCashDo.getSumRepaidPoundage())
				.add(orderCashDo.getInterestAmount()).add(orderCashDo.getSumRepaidInterest());
		BigDecimal orderRemainShouldRepayAmount = orderSumAmount.subtract(orderCashDo.getRepaidAmount()); //剩余应还金额
		BigDecimal borrowRepayAmount = bo.repaymentAmount.subtract(orderRemainShouldRepayAmount);
		if(borrowRepayAmount.compareTo(BigDecimal.ZERO) > 0) { //还款额大于订单应还总额，拆分还款
			borrowRepaymentDo = buildRepayment( borrowRepayAmount, tradeNo, now, borrowRepayAmount,
					 bo.borrowId,  bo.outTradeNo, name, bo.userId,bo.repayType,bo.cardNo);
			jsdBorrowCashRepaymentDao.saveRecord(borrowRepaymentDo);
			bo.repaymentDo=borrowRepaymentDo;
			if(!JsdBorrowLegalOrderCashStatus.FINISHED.getCode().equals(orderCashDo.getStatus())) {
				orderRepaymentDo = buildOrderRepayment(bo, borrowRepayAmount,
						 orderRemainShouldRepayAmount,bo.cardNo);
				jsdBorrowLegalOrderRepaymentDao.saveRecord(orderRepaymentDo);
				bo.orderRepaymentDo=orderRepaymentDo;
			}
		} else { //还款全部进入订单欠款中
			orderRepaymentDo = buildOrderRepayment(bo, borrowRepayAmount,
					orderRemainShouldRepayAmount,bo.cardNo);
			jsdBorrowLegalOrderRepaymentDao.saveRecord(orderRepaymentDo);
			bo.repaymentDo=borrowRepaymentDo;
			bo.orderRepaymentDo=orderRepaymentDo;
		}

		logger.info("Repay.add repayment finish,name="+ name +"tradeNo="+tradeNo+",borrowRepayment="+ JSON.toJSONString(borrowRepaymentDo) + ",legalOrderRepayment="+ JSON.toJSONString(orderRepaymentDo));
	}

	private JsdBorrowLegalOrderRepaymentDo buildOrderRepayment(BorrowCashRepayBo bo, BigDecimal actualAmountForOrder, BigDecimal repayAmount,String cardNo) {
		JsdBorrowLegalOrderRepaymentDo repayment = new JsdBorrowLegalOrderRepaymentDo();

		repayment.setUserId(bo.userId);
		repayment.setBorrowLegalOrderCashId(bo.borrowLegalOrderCashId);
		repayment.setRepayAmount(repayAmount);
		repayment.setActualAmount(actualAmountForOrder);
		repayment.setName(bo.name);
		repayment.setTradeNo(bo.tradeNo);
		repayment.setStatus(JsdBorrowLegalRepaymentStatus.APPLY.getCode());
		repayment.setCardNo(cardNo);
		HashMap bank=jsdUserBankcardDao.getBankByBankNoAndUserId(bo.userId,cardNo);
		repayment.setCardName((String) bank.get("bankName"));
		Date now = new Date();
		repayment.setGmtCreate(now);
		repayment.setGmtModified(now);
		repayment.setBorrowId(bo.borrowId);
		repayment.setCardNo(cardNo);
		return repayment;
	}

	private JsdBorrowCashRepaymentDo buildRepayment( BigDecimal repaymentAmount, String repayNo, Date gmtCreate,
												   BigDecimal actualAmountForBorrow,
												   Long borrowId,  String TradeNo, String name, Long userId,String repayType,String cardNo) {
		JsdBorrowCashRepaymentDo repay = new JsdBorrowCashRepaymentDo();
		repay.setActualAmount(actualAmountForBorrow);
		repay.setBorrowId(borrowId);
		repay.setTradeNo(TradeNo);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(JsdBorrowCashRepaymentStatus.APPLY.getCode());
		repay.setName(name);
		repay.setUserId(userId);
		repay.setTradeNo(repayNo);
		repay.setCardNumber(cardNo);
		repay.setType(repayType);
		HashMap bank=jsdUserBankcardDao.getBankByBankNoAndUserId(userId,cardNo);
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
		jsdBorrowCashRepaymentDao.status2Process(payTradeNo, kuaijieRepaymentBo.getRepayment().getRid());// 更新状态
		return getResultMap(kuaijieRepaymentBo.getBo(), respBo);
	}

	private Map<String, Object> getResultMap(BorrowCashRepayBo bo, UpsCollectRespBo respBo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", bo.borrowId);
		data.put("amount", bo.repaymentAmount.setScale(2, RoundingMode.HALF_UP));
		data.put("gmtCreate", new Date());
		data.put("status", JsdBorrowCashRepaymentStatus.YES.getCode());
		data.put("actualAmount", bo.actualAmount);
		data.put("cardName", bo.cardName);
		data.put("cardNumber", bo.cardNo);
		data.put("repayNo", bo.tradeNo);
		if (respBo != null) {
			data.put("outTradeNo", respBo.getTradeNo());
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
				dealRepaymentFail(payTradeNo, "", true, errorMsg);
			} else {
				dealRepaymentFail(payTradeNo, "", false, "");
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
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice,String errorMsg) {
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
			changBorrowRepaymentStatus(outTradeNo, JsdBorrowCashRepaymentStatus.NO.getCode(), repaymentDo.getRid());
		}
		if(orderRepaymentDo != null) {
			changOrderRepaymentStatus(outTradeNo, JsdBorrowLegalRepaymentStatus.NO.getCode(), orderRepaymentDo.getRid());
		}

		// 解锁还款
		unLockRepay(repaymentDo.getUserId());

	}

	private long changBorrowRepaymentStatus(String outTradeNo, String status, Long rid) {
		JsdBorrowCashRepaymentDo repayment = new JsdBorrowCashRepaymentDo();
		repayment.setStatus(status);
		repayment.setTradeNo(outTradeNo);
		repayment.setRid(rid);
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

}