package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.JsdBorrowCashOverdueLogDto;
import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.KuaijieRepayBo;
import com.ald.fanbei.api.biz.bo.ups.UpsCollectRespBo;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.CollectionBorrowStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;


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
	private JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
	@Resource
	private JsdBorrowLegalOrderRepaymentDao jsdBorrowLegalOrderRepaymentDao;

	@Resource
	private JsdCollectionService jsdCollectionService;
	
	@Resource
	private RedisTemplate<String, ?> redisTemplate;
	@Resource
	private GeneratorClusterNo generatorClusterNo;
	@Resource
	private TransactionTemplate transactionTemplate;

	@Resource
	private JsdNoticeRecordService jsdNoticeRecordService;

	@Resource
	private JsdCollectionBorrowService jsdCollectionBorrowService;

	@Resource
	private JsdBorrowCashOverdueLogService jsdBorrowCashOverdueLogService;

	@Resource
	private JsdOfflineOverdueRemoveDao jsdOfflineOverdueRemoveDao;

	@Resource
	private JsdOfflineOverdueRemoveService jsdOfflineOverdueRemoveService;

	@Override
	public Map<String, Object> repay(RepayRequestBo bo, String bankPayType) {
		try {
			if (!BankPayChannel.KUAIJIE.getCode().equals(bankPayType)) {
				lockRepay(bo.userId);
			}

			String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(bankPayType);
			bo.tradeNo = tradeNo;
            generateRepayRecords(bo);
			return doRepay(bo,bankPayType);
		}catch (Exception e) {
			logger.info("repay method error", e);
			throw e;
		}finally {
			unLockRepay(bo.userId);
		}
	}
	private Map<String, Object> doRepay(RepayRequestBo bo,String bankChannel) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HashMap<String, Object> bank = jsdUserBankcardDao.getPayTypeByBankNoAndUserId(bo.userId,bo.bankNo);
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

	private void generateRepayRecords(RepayRequestBo bo) {
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
			borrowRepaymentDo = buildRepayment( borrowRepayAmount, bo.repayNo, now, bo.amount,
					bo.borrowId,  bo.tradeNo, name, bo.userId,bo.repayType,bo.bankNo,bo.payTime,bo.cardName,bo.remark);
			jsdBorrowCashRepaymentDao.saveRecord(borrowRepaymentDo);
			bo.repaymentDo=borrowRepaymentDo;
			if(orderCashDo!=null){
				if(!JsdBorrowLegalOrderCashStatus.FINISHED.getCode().equals(orderCashDo.getStatus())) {
					orderRepaymentDo = buildOrderRepayment(bo, orderRemainShouldRepayAmount);
					jsdBorrowLegalOrderRepaymentDao.saveRecord(orderRepaymentDo);
					bo.orderRepaymentDo=orderRepaymentDo;
				}
			}

		} else { //还款全部进入订单欠款中，
			orderRepaymentDo = buildOrderRepayment(bo, bo.amount);
			jsdBorrowLegalOrderRepaymentDao.saveRecord(orderRepaymentDo);
		}
		bo.repaymentDo=borrowRepaymentDo;
		bo.orderRepaymentDo=orderRepaymentDo;
	}

	private JsdBorrowLegalOrderRepaymentDo buildOrderRepayment(RepayRequestBo bo, BigDecimal repayAmount) {
		JsdBorrowLegalOrderRepaymentDo repayment = new JsdBorrowLegalOrderRepaymentDo();
		repayment.setUserId(bo.userId);
		repayment.setBorrowLegalOrderCashId(bo.borrowLegalOrderCashId);
		repayment.setRepayAmount(repayAmount);
		repayment.setActualAmount(repayAmount);
		repayment.setName(bo.name);
		repayment.setTradeNo(bo.tradeNo);	// 我方生成
		repayment.setRepayNo(bo.repayNo);	// 西瓜提供
		repayment.setStatus(JsdBorrowLegalRepaymentStatus.APPLY.getCode());
		repayment.setRemark(bo.remark);
		repayment.setPayTime(bo.payTime);

		if(StringUtil.isNotBlank(bo.cardName)){
			repayment.setCardName(bo.cardName);
		}
		//催收还款没有银行卡号
		if(StringUtil.isNotBlank(bo.bankNo)){
			repayment.setCardNo(bo.bankNo);
			HashMap<?, ?> bank=jsdUserBankcardDao.getPayTypeByBankNoAndUserId(bo.userId,bo.bankNo);
			repayment.setCardName((String) bank.get("bankName"));
		}else {
			repayment.setCardNo("");
			repayment.setCardName("");
		}
		Date now = new Date();
		repayment.setGmtCreate(now);
		repayment.setGmtModified(now);
		repayment.setBorrowId(bo.borrowId);
		repayment.setCardNo(bo.bankNo);
		return repayment;
	}

	private JsdBorrowCashRepaymentDo buildRepayment( BigDecimal repaymentAmount, String tradeNoXgxy, Date gmtCreate,
													 BigDecimal actualAmountForBorrow,
													 Long borrowId,  String TradeNo, String name, Long userId,String repayType,String cardNo,Date payTime,String cardName,String remark) {
		JsdBorrowCashRepaymentDo repay = new JsdBorrowCashRepaymentDo();
		repay.setActualAmount(actualAmountForBorrow);
		repay.setBorrowId(borrowId);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setTradeNo(TradeNo); // 我方生成
		repay.setTradeNoXgxy(tradeNoXgxy);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(JsdBorrowCashRepaymentStatus.APPLY.getCode());
		repay.setName(name);
		repay.setUserId(userId);
		repay.setType(repayType);
		repay.setRemark(remark);
		repay.setPayTime(payTime);

		if(StringUtil.isNotBlank(cardName)){
			repay.setCardName(cardName);
		}
		//催收还款没有银行卡号
		if(StringUtil.isNotBlank(cardNo)){
			repay.setCardNumber(cardNo);
			HashMap<?, ?> bank=jsdUserBankcardDao.getPayTypeByBankNoAndUserId(userId,cardNo);
			repay.setCardName((String) bank.get("bankName"));
		}else {
			repay.setCardNumber("");
		}
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
			throw new BizException(BizExceptionCode.LOAN_REPAY_PROCESS_ERROR);
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

	private Map<String, Object> getResultMap(RepayRequestBo bo, UpsCollectRespBo respBo,String bankChannel) {
		Map<String, Object> data = new HashMap<String, Object>();
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
			throw new BizException(BizExceptionCode.UPS_CACHE_EXPIRE);
		}
	}
	/**
	 * 还款失败后调用
	 */
	@Override
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice, String code, String errorMsg) {
		final JsdBorrowCashRepaymentDo repaymentDo = jsdBorrowCashRepaymentDao.getByTradeNo(tradeNo);
		final JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = jsdBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByTradeNo(tradeNo);
		try {
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
			JsdRepayType repayType=JsdRepayType.INITIATIVE;
			if(repaymentDo!=null&&JsdRepayType.WITHHOLD.getXgxyCode().equals(repaymentDo.getType())){
				repayType=JsdRepayType.WITHHOLD;
			}
			noticeXgxyRepayResult(repaymentDo,orderRepaymentDo,YesNoStatus.NO.getCode(),errorMsg,repayType,"");
		}
		catch (Exception e){
			logger.error("notice eca fail error=",e);
		}finally {
			// 解锁还款
			unLockRepay(repaymentDo!=null?repaymentDo.getUserId():orderRepaymentDo.getUserId());
		}

	}

	@Override
	public void dealRepaymentSucess(String repayNo, String outTradeNo) {
		final JsdBorrowCashRepaymentDo repaymentDo = jsdBorrowCashRepaymentDao.getByTradeNo(repayNo);
		final JsdBorrowLegalOrderRepaymentDo orderRepaymentDo = jsdBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByTradeNo(repayNo);
		JsdRepayType repayType=JsdRepayType.INITIATIVE;
		if(repaymentDo!=null&&JsdRepayType.WITHHOLD.getXgxyCode().equals(repaymentDo.getType())){
			repayType=JsdRepayType.WITHHOLD;
		}
		dealRepaymentSucess(repayNo, outTradeNo, repaymentDo, orderRepaymentDo,repayType);

	}

	@Override
	public JsdBorrowCashRepaymentDo getByTradeNoXgxy(String tradeNoXgxy) {
		return jsdBorrowCashRepaymentDao.getByTradeNoXgxy(tradeNoXgxy);
	}

	@Override
	public List<JsdBorrowCashRepaymentDo> getByBorrowTradeNoXgxy(String tradeNoXgxy) {
		return jsdBorrowCashRepaymentDao.getByBorrowTradeNoXgxy(tradeNoXgxy);
	}

	public void dealRepaymentSucess(String tradeNo, String outTradeNo, final JsdBorrowCashRepaymentDo repaymentDo, final JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, JsdRepayType repayType) {
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
						if(orderRepaymentDo!=null){
							dealOrderRepay(repayDealBo, orderRepaymentDo);
						}
						if(repaymentDo!=null){
							dealBorrowRepay(repayDealBo, repaymentDo);
						}
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
					this.noticeXgxyRepayResult(repaymentDo, orderRepaymentDo, YesNoStatus.YES.getCode(),"", repayType,outTradeNo);
					this.notifyCollection(repayDealBo, repaymentDo, orderRepaymentDo, repayType);
				} catch (Exception e){
					logger.error("notice eca or collection fail error=" + e.getMessage(), e);
				}
			}else {

			}

		}finally {
			unLock(tradeNo);

			// 解锁还款
			unLockRepay(repaymentDo!=null?repaymentDo.getUserId():orderRepaymentDo.getUserId());
		}
	}
	private void noticeXgxyRepayResult(JsdBorrowCashRepaymentDo repaymentDo,JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, String status,String errorMsg,JsdRepayType type,String outTradeNo){
		HashMap<String, String> data = null;
		if(repaymentDo!=null){
			data = buildNoticeXgxyData(repaymentDo.getTradeNoXgxy(),repaymentDo.getBorrowId(),repaymentDo.getActualAmount(),status,errorMsg,type,outTradeNo);
		}else if(orderRepaymentDo!=null){
			data = buildNoticeXgxyData(orderRepaymentDo.getRepayNo(),orderRepaymentDo.getBorrowId(),orderRepaymentDo.getActualAmount(),status,errorMsg,type,outTradeNo);
		}
		logger.info("noticeXgxyRepayResult data  "+JSON.toJSONString(data));

		// 通知记录
		jsdNoticeRecordService.dealRepayNoticed(repaymentDo,orderRepaymentDo,data);
	}
	private HashMap<String, String> buildNoticeXgxyData(String tradeNoXgxy ,Long borrowId,BigDecimal actualAmount, String status,String errorMsg,JsdRepayType type,String outTradeNo){
		HashMap<String,String> map=new HashMap<>();
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashDao.getById(borrowId);
		map.put("repayNo",tradeNoXgxy);
		map.put("status",status);
		map.put("tradeNo",outTradeNo);
		map.put("borrowNo",borrowCashDo.getTradeNoXgxy());
		map.put("period","1");
		map.put("amount", String.valueOf(actualAmount));
		map.put("type",type.getXgxyCode());
		map.put("reason",errorMsg);
		Date now=new Date();
		map.put("timestamp", String.valueOf(now.getTime()));
		if(JsdBorrowCashStatus.FINISHED.name().equals(borrowCashDo.getStatus())){
			map.put("isFinish",YesNoStatus.YES.getCode());
		}else {
			map.put("isFinish",YesNoStatus.NO.getCode());
		}
		return map;
	}
	private void notifyCollection(RepayDealBo repayDealBo, JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, JsdRepayType repayType) {
		if(JsdRepayType.REVIEW_COLLECTION.equals(repayType)) {
			return; //审批模式下，不操作，由外部调用者处理
		}
		
		boolean isCashOverdue = DateUtil.afterDay(new Date(),repayDealBo.cashDo.getGmtPlanRepayment());
		boolean isOrderOverdue = false;
		if(repayDealBo.orderCashDo != null){
			isOrderOverdue = DateUtil.afterDay(new Date(), repayDealBo.orderCashDo.getGmtPlanRepay());
		}
		if(isOrderOverdue || isCashOverdue) {
			jsdCollectionService.nofityRepayment(repayDealBo.curSumRepayAmount, repayDealBo.curOutTradeNo, repayDealBo.borrowNo, repayDealBo.orderId, repayDealBo.userId, repayType, null);
		}
	}
	

	/**
	 * 需在事务管理块中调用此函数!
	 * @param repayDealBo
	 * @param repaymentDo
	 */
	private void dealBorrowRepay(RepayDealBo repayDealBo, JsdBorrowCashRepaymentDo repaymentDo) {
		if(repaymentDo == null) return;

		JsdBorrowCashDo cashDo = jsdBorrowCashDao.getById(repaymentDo.getBorrowId());
		repayDealBo.curRepayAmoutStub = repaymentDo.getRepaymentAmount();
		repayDealBo.curSumRepayAmount = repayDealBo.curSumRepayAmount.add(repaymentDo.getRepaymentAmount());
		repayDealBo.curCardNo = repaymentDo.getCardNumber();
		repayDealBo.curCardName = repaymentDo.getCardName();
		repayDealBo.curName = repaymentDo.getName();

		repayDealBo.cashDo = cashDo;
		repayDealBo.overdueDay = cashDo.getOverdueDay();
		repayDealBo.borrowNo = cashDo.getBorrowNo();
		repayDealBo.refId += repaymentDo.getBorrowId();
		repayDealBo.userId = cashDo.getUserId();
		
		if(repayDealBo.orderId == null) {
			repayDealBo.orderId = jsdBorrowLegalOrderDao.getLastValidOrderByBorrowId(cashDo.getRid()).getRid();
		}

		dealBorrowRepayOverdue(repayDealBo, cashDo);//逾期费
		dealBorrowRepayPoundage(repayDealBo, cashDo);//手续费
		dealBorrowRepayInterest(repayDealBo, cashDo);//利息
		changBorrowRepaymentStatus(repayDealBo.curOutTradeNo, JsdBorrowCashRepaymentStatus.YES.getCode(), repaymentDo.getRid(),"","");

		dealBorrowRepayIfFinish(repayDealBo, repaymentDo, cashDo);
		jsdBorrowCashDao.updateById(cashDo);
	}
	private void dealBorrowRepayOverdue(RepayDealBo repayDealBo, JsdBorrowCashDo afBorrowCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal overdueAmount = afBorrowCashDo.getOverdueAmount();

		if (repayAmount.compareTo(overdueAmount) > 0) {
			afBorrowCashDo.setSumRepaidOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumRepaidOverdue(), overdueAmount));
			afBorrowCashDo.setOverdueAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(overdueAmount);
		} else {
			afBorrowCashDo.setSumRepaidOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumRepaidOverdue(), repayAmount));
			afBorrowCashDo.setOverdueAmount(overdueAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}
	private void dealBorrowRepayPoundage(RepayDealBo repayDealBo, JsdBorrowCashDo afBorrowCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal poundageAmount = afBorrowCashDo.getPoundageAmount();

		if (repayAmount.compareTo(poundageAmount) > 0) {
			afBorrowCashDo.setSumRepaidPoundage(BigDecimalUtil.add(afBorrowCashDo.getSumRepaidPoundage(), poundageAmount));
			afBorrowCashDo.setPoundageAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(poundageAmount);
		} else {
			afBorrowCashDo.setSumRepaidPoundage(BigDecimalUtil.add(afBorrowCashDo.getSumRepaidPoundage(), repayAmount));
			afBorrowCashDo.setPoundageAmount(poundageAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}
	private void dealBorrowRepayInterest(RepayDealBo repayDealBo, JsdBorrowCashDo afBorrowCashDo) {
		if(repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;

		BigDecimal repayAmount = repayDealBo.curRepayAmoutStub;
		BigDecimal rateAmount = afBorrowCashDo.getInterestAmount();

		if (repayAmount.compareTo(rateAmount) > 0) {
			afBorrowCashDo.setSumRepaidInterest(BigDecimalUtil.add(afBorrowCashDo.getSumRepaidInterest(), rateAmount));
			afBorrowCashDo.setInterestAmount(BigDecimal.ZERO);
			repayDealBo.curRepayAmoutStub = repayAmount.subtract(rateAmount);
		} else {
			afBorrowCashDo.setSumRepaidInterest(BigDecimalUtil.add(afBorrowCashDo.getSumRepaidInterest(), repayAmount));
			afBorrowCashDo.setInterestAmount(rateAmount.subtract(repayAmount));
			repayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
	}
	private void dealBorrowRepayIfFinish(RepayDealBo repayDealBo, JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowCashDo cashDo) {
		BigDecimal sumAmount = BigDecimalUtil.add(cashDo.getAmount(),
				cashDo.getOverdueAmount(), cashDo.getSumRepaidOverdue(),
				cashDo.getInterestAmount(), cashDo.getSumRepaidInterest(),
				cashDo.getPoundageAmount(), cashDo.getSumRepaidPoundage());
		BigDecimal allRepayAmount = cashDo.getRepayAmount().add(repaymentDo.getRepaymentAmount());
		cashDo.setRepayAmount(allRepayAmount);
		if (sumAmount.compareTo(allRepayAmount) <= 0) {
			cashDo.setStatus(JsdBorrowCashStatus.FINISHED.name());
			cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
			jsdCollectionBorrowService.updateCollectionStatus(cashDo.getRid(),CollectionBorrowStatus.NORMAL_FINISHED.name());
		}
	}

	/**
	 * 需在事务管理块中调用此函数!
	 * @param repayDealBo
	 * @param orderRepaymentDo
	 */
	private void dealOrderRepay(RepayDealBo repayDealBo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo) {
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
		repayDealBo.orderId = orderCashDo.getBorrowLegalOrderId();

		dealOrderRepayOverdue(repayDealBo, orderCashDo);//逾期费
		dealOrderRepayPoundage(repayDealBo, orderCashDo);//手续费
		dealOrderRepayInterest(repayDealBo, orderCashDo);//利息

		dealOrderRepayIfFinish(repayDealBo, orderRepaymentDo, orderCashDo);
		jsdBorrowLegalOrderCashDao.updateById(orderCashDo);

		changOrderRepaymentStatus(repayDealBo.curOutTradeNo, JsdBorrowLegalRepaymentStatus.YES.getCode(), orderRepaymentDo.getRid());
	}
	private void dealOrderRepayIfFinish(RepayDealBo repayDealBo, JsdBorrowLegalOrderRepaymentDo orderRepaymentBo, JsdBorrowLegalOrderCashDo orderCashDo) {
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
		} else {
			orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.PART_REPAID.getCode());
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
	private void preCheck(JsdBorrowCashRepaymentDo repaymentDo, JsdBorrowLegalOrderRepaymentDo orderRepaymentDo, String tradeNo) {
		// 检查交易流水 对应记录数据库中是否已经处理
		if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) )
				|| (orderRepaymentDo != null && YesNoStatus.YES.getCode().equals(orderRepaymentDo.getStatus()) )) {
			throw new BizException("preCheck, repayment has been dealed!");
		}
	}

	@Override
	public void offlineRepay(JsdBorrowCashDo jsdBorrowCashDo, JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo, 
				String totalAmount, final String repaymentNo, Long userId, final JsdRepayType type, 
				String channel , Date repayTime, String orderNo, final String dataId, String remark) {
		final RepayRequestBo bo = this.buildRepayRequestBo(userId, jsdBorrowCashDo, totalAmount, repaymentNo ,type,channel,repayTime,remark);

		this.checkOfflineRepayment(repaymentNo);
		dealOfflineOverdue(jsdBorrowCashDo, jsdBorrowLegalOrderCashDo, totalAmount, userId, repayTime);
		generateRepayRecords(bo);
		dealRepaymentSucess(bo.tradeNo, repaymentNo, bo.repaymentDo, bo.orderRepaymentDo, type);
	}

	@Override
	public List<FinaneceDataDo> getRepayData() {
		return jsdBorrowCashRepaymentDao.getRepayData();
	}

	/**
	 * @Description: 线下还款超时处理（减免逾期）
	 */
	private void dealOfflineOverdue(JsdBorrowCashDo jsdBorrowCashDo,JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo,String totalAmount, Long userId,Date repayTime) {
		BigDecimal payAmount = new BigDecimal(totalAmount);
		if (new Date().before(jsdBorrowCashDo.getGmtPlanRepayment())) {
			logger.info("offline repay no overdue，borrowId=" + jsdBorrowCashDo.getRid());
		} else {
			if (jsdBorrowLegalOrderCashDo == null) {
				dealBorrowCash(jsdBorrowCashDo,jsdBorrowLegalOrderCashDo,totalAmount,userId,repayTime);
			}else {
				List<JsdBorrowCashOverdueLogDto> orderCashList = jsdBorrowCashOverdueLogService.getListOrderCashOverdueLogByBorrowId(jsdBorrowLegalOrderCashDo.getRid(), repayTime);
				if (orderCashList.size() != 0) {
					StringBuilder sb = new StringBuilder();
					BigDecimal removeOverdue = BigDecimal.ZERO;
					for (JsdBorrowCashOverdueLogDto jsdBorrowCashOverdueLogDto : orderCashList) {
						removeOverdue=removeOverdue.add(jsdBorrowCashOverdueLogDto.getInterest());
						sb.append(jsdBorrowCashOverdueLogDto.getRid() + ",");
					}
					if(jsdOfflineOverdueRemoveService.getInfoByoverdueLogId(sb.toString())==null){
						BigDecimal restOverdue=jsdBorrowLegalOrderCashDo.getOverdueAmount().subtract(removeOverdue);
						BigDecimal restAmount = BigDecimalUtil.add(jsdBorrowLegalOrderCashDo.getInterestAmount(), jsdBorrowLegalOrderCashDo.getPoundageAmount(), jsdBorrowLegalOrderCashDo.getOverdueAmount().subtract(removeOverdue),
								jsdBorrowLegalOrderCashDo.getSumRepaidInterest(), jsdBorrowLegalOrderCashDo.getSumRepaidPoundage(), jsdBorrowLegalOrderCashDo.getSumRepaidOverdue(), jsdBorrowLegalOrderCashDo.getAmount()).subtract(jsdBorrowLegalOrderCashDo.getRepaidAmount());
						if (payAmount.compareTo(restAmount) < 0) {
							logger.info("legal order cash part offline repay，borrowId=" + jsdBorrowLegalOrderCashDo.getBorrowId());
						} else {
							JsdBorrowLegalOrderCashDo borrowLegalOrderCashDoCashDo = new JsdBorrowLegalOrderCashDo();
							borrowLegalOrderCashDoCashDo.setRid(jsdBorrowLegalOrderCashDo.getRid());
							borrowLegalOrderCashDoCashDo.setOverdueAmount(restOverdue);
							borrowLegalOrderCashDoCashDo.setOverdueDay((short) (jsdBorrowLegalOrderCashDo.getOverdueDay() - orderCashList.size()));
							if (repayTime.before(jsdBorrowLegalOrderCashDo.getGmtPlanRepay())) {
								borrowLegalOrderCashDoCashDo.setOverdueStatus("N");
							}
							jsdBorrowLegalOrderCashDao.updateById(borrowLegalOrderCashDoCashDo);
							JsdOfflineOverdueRemoveDo jsdOfflineOverdueRemoveDo = new JsdOfflineOverdueRemoveDo();
							jsdOfflineOverdueRemoveDo.setCurrentAmount(BigDecimalUtil.add(jsdBorrowLegalOrderCashDo.getAmount(), jsdBorrowLegalOrderCashDo.getSumRepaidInterest(), jsdBorrowLegalOrderCashDo.getSumRepaidPoundage(), jsdBorrowLegalOrderCashDo.getSumRepaidOverdue()).subtract(jsdBorrowLegalOrderCashDo.getRepaidAmount()));
							jsdOfflineOverdueRemoveDo.setNewOverdue(restOverdue);
							jsdOfflineOverdueRemoveDo.setOverdueLogId(sb.toString());
							jsdOfflineOverdueRemoveDo.setRemoveOverdue(removeOverdue);
							jsdOfflineOverdueRemoveDo.setGmtRepay(repayTime);
							jsdOfflineOverdueRemoveDo.setType("ORDER_CASH");
							jsdOfflineOverdueRemoveDo.setUserId(userId);
							jsdOfflineOverdueRemoveDao.saveRecord(jsdOfflineOverdueRemoveDo);
							dealBorrowCash(jsdBorrowCashDo,jsdBorrowLegalOrderCashDo,totalAmount,userId,repayTime);
						}
					}
				}
			}
		}
    }

	private void dealBorrowCash(JsdBorrowCashDo jsdBorrowCashDo,JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo,String totalAmount, Long userId, Date repayTime) {
		BigDecimal payAmount = new BigDecimal(totalAmount);
		List<JsdBorrowCashOverdueLogDto> cashList = jsdBorrowCashOverdueLogService.getListCashOverdueLogByBorrowId(jsdBorrowCashDo.getRid(), repayTime);
		if (cashList.size() != 0) {
			StringBuilder sb = new StringBuilder();
			BigDecimal removeOverdue = BigDecimal.ZERO;
			for (JsdBorrowCashOverdueLogDto jsdBorrowCashOverdueLogDto : cashList) {
				removeOverdue=removeOverdue.add(jsdBorrowCashOverdueLogDto.getInterest());
				sb.append(jsdBorrowCashOverdueLogDto.getRid() + ",");
			}
			if(jsdOfflineOverdueRemoveService.getInfoByoverdueLogId(sb.toString())==null){
				BigDecimal restOverdue=jsdBorrowCashDo.getOverdueAmount().subtract(removeOverdue);
				BigDecimal restAmount=BigDecimalUtil.add(jsdBorrowCashDo.getInterestAmount(),jsdBorrowCashDo.getPoundageAmount(),jsdBorrowCashDo.getOverdueAmount().subtract(removeOverdue),
						jsdBorrowCashDo.getSumRepaidInterest(),jsdBorrowCashDo.getSumRepaidPoundage(),jsdBorrowCashDo.getSumRepaidOverdue(),jsdBorrowCashDo.getAmount()).subtract(jsdBorrowCashDo.getRepayAmount());
				
				if(jsdBorrowLegalOrderCashDo != null) {
					payAmount = payAmount.subtract(jsdBorrowLegalOrderCashDo.getRepaidAmount());
				}
				
				if(payAmount.compareTo(restAmount)<0){
					logger.info("part offline repay，borrowId="+jsdBorrowCashDo.getRid());
				}else {
					JsdBorrowCashDo borrowCashDo = new JsdBorrowCashDo();
					borrowCashDo.setRid(jsdBorrowCashDo.getRid());
					borrowCashDo.setOverdueAmount(jsdBorrowCashDo.getOverdueAmount().subtract(removeOverdue));
					borrowCashDo.setOverdueDay(jsdBorrowCashDo.getOverdueDay() - cashList.size());
					if (repayTime.before(jsdBorrowCashDo.getGmtPlanRepayment())) {
						borrowCashDo.setOverdueStatus("N");
					}
					jsdBorrowCashDao.updateById(borrowCashDo);
					JsdOfflineOverdueRemoveDo jsdOfflineOverdueRemoveDo = new JsdOfflineOverdueRemoveDo();
					jsdOfflineOverdueRemoveDo.setCurrentAmount(BigDecimalUtil.add(jsdBorrowCashDo.getAmount(), jsdBorrowCashDo.getSumRepaidInterest(), jsdBorrowCashDo.getSumRepaidPoundage(), jsdBorrowCashDo.getSumRepaidOverdue()).subtract(jsdBorrowCashDo.getRepayAmount()));
					jsdOfflineOverdueRemoveDo.setNewOverdue(restOverdue);
					jsdOfflineOverdueRemoveDo.setOverdueLogId(sb.toString());
					jsdOfflineOverdueRemoveDo.setRemoveOverdue(removeOverdue);
					jsdOfflineOverdueRemoveDo.setGmtRepay(repayTime);
					jsdOfflineOverdueRemoveDo.setType("CASH");
					jsdOfflineOverdueRemoveDo.setUserId(userId);
					jsdOfflineOverdueRemoveDao.saveRecord(jsdOfflineOverdueRemoveDo);
				}
			}
		}
	}

	private RepayRequestBo buildRepayRequestBo(Long userId, JsdBorrowCashDo jsdBorrowCashDo, String repayAmount,
				String outTradeNo, JsdRepayType type, String channel, Date repayTime, String remark){
		RepayRequestBo bo = new RepayRequestBo();
		JsdUserDo jsdUserDo = jsdUserDao.getById(userId);
		bo.userDo = jsdUserDo;
		bo.userId = userId;
		bo.borrowNo = jsdBorrowCashDo.getBorrowNo();
		bo.amount = new BigDecimal(repayAmount);
		bo.actualAmount =  bo.amount;
		bo.borrowId = jsdBorrowCashDo.getRid();
		bo.borrowCashDo = jsdBorrowCashDo;
		bo.tradeNo = generatorClusterNo.getOfflineRepaymentBorrowCashNo(new Date());
		bo.tradeNoUps = outTradeNo;
		bo.repayType = type.name();
		bo.cardName = channel;
		bo.bankNo = "";
		bo.name = type.getCode();
		bo.payTime = repayTime;
        bo.remark = remark;
		return bo;
	}
	private void checkOfflineRepayment(String repaymentNo) {
		if(repaymentNo!=null && !repaymentNo.equals("")) {
			if (jsdBorrowCashRepaymentDao.getByTradeNoOut(repaymentNo) != null) {
				throw new BizException(BizExceptionCode.BORROW_CASH_REPAY_REPEAT_ERROR);
			}
			if (jsdBorrowLegalOrderRepaymentDao.getByTradeNoOut(repaymentNo) != null) {
				throw new BizException(BizExceptionCode.BORROW_CASH_REPAY_REPEAT_ERROR);
			}
		}
	}
	private long changBorrowRepaymentStatus(String outTradeNo, String status, Long rid,String code,String msg) {
		JsdBorrowCashRepaymentDo repayment = new JsdBorrowCashRepaymentDo();
		repayment.setStatus(status);
		repayment.setTradeNoUps(outTradeNo);
		repayment.setRid(rid);
		JsdBorrowCashRepaymentDo jsdBorrowCashRepaymentDo=jsdBorrowCashRepaymentDao.getById(rid);
		if(!YesNoStatus.NO.getCode().equals(jsdBorrowCashRepaymentDo.getStatus())){
			repayment.setFailCode(code);
			repayment.setFailMsg(msg);
		}
		return jsdBorrowCashRepaymentDao.updateById(repayment);
	}
	private long changOrderRepaymentStatus(String outTradeNo, String status, Long rid) {
		JsdBorrowLegalOrderRepaymentDo repayment = new JsdBorrowLegalOrderRepaymentDo();
		repayment.setStatus(status);
		repayment.setTradeNoUps(outTradeNo);
		repayment.setRid(rid);
		return jsdBorrowLegalOrderRepaymentDao.updateBorrowLegalOrderRepayment(repayment);
	}
	
	
	/**
	 * 锁住目标流水号的还款，防止重复回调
	 */
	private void lock(String tradeNo) {
		String key = tradeNo + "_success_legalRepay";
		long count = redisTemplate.opsForValue().increment(key, 1);
		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
		if (count != 1) {
			throw new BizException(BizExceptionCode.UPS_REPEAT_NOTIFY);
		}
	}

	private void unLock(String tradeNo) {
		String key = tradeNo + "_success_legalRepay";
		redisTemplate.delete(key);
	}


	@Override
	public List<JsdBorrowCashRepaymentDo> getRepayByBorrowId(Long borrowId) {
		return jsdBorrowCashRepaymentDao.getRepayByBorrowId(borrowId);
	}

	public static class RepayRequestBo{
		public Long userId;
		public BigDecimal repaymentAmount = BigDecimal.ZERO;	// 还款金额
		public BigDecimal actualAmount = BigDecimal.ZERO;
		
		public Long borrowId;		//可选字段
		public String borrowNo;
		public String period;//都市e贷 借款当前期数
		
		public String remoteIp;
		public String name;
		public String repayType;

		public BigDecimal amount ; //都市e贷 还款金额
		public String cardName;		//交易卡名称
		public String bankNo;		//都市e贷 银行卡卡号
		
		public String repayNo; 		//西瓜还款流水号
		public String tradeNoUps; 	//资金方放交易流水号
		public String tradeNo;		//我方交易流水号

		public Long borrowOrderId; 			//可选字段
		public Long borrowLegalOrderCashId; //可选字段

		public JsdUserDo userDo;

		public String payType;
		public Date payTime;
		public String remark;

		public JsdBorrowCashDo borrowCashDo;
		public JsdBorrowCashRepaymentDo repaymentDo;
		public JsdBorrowLegalOrderRepaymentDo orderRepaymentDo;
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
		Long orderId ;								//商品订单id
	}

}
