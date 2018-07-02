package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.RecycleKuaijieRepayBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 回收还款service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2017-4-30 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowRecycleRepaymentService")
public class AfBorrowRecycleRepaymentServiceImpl extends UpsPayKuaijieServiceAbstract implements AfBorrowRecycleRepaymentService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /* copy自 AfRepaymentBorrowCashServiceImpl */
    @Resource
    AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;

    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    AfUserAccountDao afUserAccountDao;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfUserAccountSenceService afUserAccountSenceService;
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
	AfBorrowRecycleOrderService recycleOrderService;

    @Resource
    UpsUtil upsUtil;
    @Resource
    RiskUtil riskUtil;
    @Resource
    SmsUtil smsUtil;
    @Resource
    CollectionSystemUtil collectionSystemUtil;

    @Resource
    AfYibaoOrderDao afYibaoOrderDao;
    @Resource
    YiBaoUtility yiBaoUtility;
	@Resource
	KafkaSync kafkaSync;
    @Resource
    CuiShouUtils cuiShouUtils;
    @Resource
    AfTaskUserService afTaskUserService;

    @Resource
    RedisTemplate<String, ?> redisTemplate;
    /* copy自 AfRepaymentBorrowCashServiceImpl */

    @Resource
    private AfTradeCodeInfoService afTradeCodeInfoService;

    @Autowired
    private AfBorrowLegalOrderService afBorrowLegalOrderService;


    @Override
    public Map<String, Object> repay(RepayBo bo, String bankChannel) {
		// 非快捷支付才会锁定还款
		if (!BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {
		    lockRepay(bo.userId);
		}

	    Date now = new Date();
		String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_RECYCLE;
		if (StringUtil.equals("sysJob", bo.remoteIp)) {
		    name = Constants.BORROW_REPAYMENT_RECYCLE_NAME_AUTO;
		}
	
		String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now, bankChannel);
		bo.tradeNo = tradeNo;
		bo.name = name;
	
		generateRepayRecords(bo);
	
		return doRepay(bo, bo.borrowRepaymentDo, bankChannel);

    }

    /**
     * 线下还款
     * 
     * @param restAmount
     */
    @Override
    public void offlineRepay(AfBorrowCashDo cashDo, String borrowNo, String repayType, String repayTime, String repayAmount, String restAmount, String outTradeNo, String isBalance, String repayCardNum, String operator, String isAdmin) {
		checkOfflineRepayment(cashDo, repayAmount, outTradeNo);
	
		RepayBo bo = new RepayBo();
		bo.userId = cashDo.getUserId();
		bo.userDo = afUserAccountDao.getUserAccountInfoByUserId(bo.userId);
	
		bo.cardId = (long) -4;
		bo.repaymentAmount = NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO);
		bo.actualAmount = bo.repaymentAmount;
		bo.borrowId = cashDo.getRid();
		bo.isBalance = isBalance;// 判断是否平账
		bo.tradeNo = generatorClusterNo.getOfflineRepaymentBorrowCashNo(new Date());
		if (isAdmin != null && "Y".equals(isAdmin)) {
		    bo.name = Constants.BORROW_REPAYMENT_NAME_OFFLINE;// 财务线下打款
		} else {
		    bo.name = Constants.COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE;// 催收线下打款
		}
		bo.outTradeNo = outTradeNo;
		bo.cardNo = repayCardNum;
		bo.repayType = repayType;
		generateRepayRecords(bo);
		CuiShouUtils.setAfRepaymentBorrowCashDo(bo.borrowRepaymentDo);
		dealRepaymentSucess(bo.tradeNo, bo.outTradeNo, bo.borrowRepaymentDo, operator, cashDo, bo.isBalance);

    }

    /**
     * 还款成功后调用
     *
     * @param tradeNo
     *            我方交易流水
     * @param outTradeNo
     *            资金方交易流水
     * @return
     */
    @Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfRepaymentBorrowCashDo repaymentDo, String operator, AfBorrowCashDo cashDo, final String isBalance) {
		try {
		    lock(tradeNo);
	
		    logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo));
	
		    this.preCheck(repaymentDo, tradeNo);
	
		    repaymentDo.setOperator(operator);
		    final RepayDealBo repayDealBo = new RepayDealBo();
		    repayDealBo.curTradeNo = tradeNo;
		    repayDealBo.curOutTradeNo = outTradeNo;
		    long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
			    try {
				dealBorrowRepay(repayDealBo, repaymentDo, isBalance);
	
				dealSum(repayDealBo);
	
				dealCouponAndRebate(repayDealBo);
				doAccountLog(repayDealBo);
	
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
		            	notifyUserBySms(repayDealBo,isBalance);
		            	nofityRisk(repayDealBo,cashDo);
	            	} catch(Exception e) {
	            		logger.info("notifyUserBySms or nofityRisk has a Exception ,borrowNo = "+cashDo.getBorrowNo()+", e= "+e );
	            	}
	            	cuiShouUtils.syncCuiShou(repaymentDo);
					try{
						kafkaSync.syncEvent(repaymentDo.getUserId(), KafkaConstants.SYNC_USER_BASIC_DATA,true);
						kafkaSync.syncEvent(repaymentDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);
					}catch (Exception e){
						logger.info("消息同步失败:",e);
					}
	            }
	    		
	    	}finally {
	    		unLock(tradeNo);
	    		
	    		// 解锁还款
	    		unLockRepay(repaymentDo.getUserId());
			}
    }

    @Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo) {
		final AfRepaymentBorrowCashDo repaymentDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(tradeNo);
		final AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(repaymentDo.getBorrowId());
		dealRepaymentSucess(tradeNo, outTradeNo, repaymentDo, null, cashDo, null);
    }

    /**
     * 还款失败后调用
     */
    @Override
    public void dealRepaymentFail(String tradeNo, String outTradeNo, boolean isNeedMsgNotice, String errorMsg) {
		final AfRepaymentBorrowCashDo repaymentDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(tradeNo);
		logger.info("dealRepaymentFail process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",isNeedMsgNotice=" + isNeedMsgNotice + ",errorMsg=" + errorMsg + ",borrowRepayment=" + JSON.toJSONString(repaymentDo));
	
		if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()))) { // 检查交易流水
													  // 对应记录数据库中是否已经处理
		    return;
		}
	
		if (repaymentDo != null) {
		    changBorrowRepaymentStatus(outTradeNo, AfBorrowCashRepmentStatus.NO.getCode(), repaymentDo.getRid());
		}
	
		// 解锁还款
		unLockRepay(repaymentDo.getUserId());
	
		if (isNeedMsgNotice) {
		    // 用户信息及当日还款失败次数校验
		    int errorTimes = 0;
		    AfUserDo afUserDo = afUserService.getUserById(repaymentDo.getUserId());
		    // 如果是代扣，不校验次数
		    String payType = repaymentDo.getName();
		    // 模版数据map处理
		    Map<String, String> replaceMapData = new HashMap<String, String>();
		    replaceMapData.put("errorMsg", errorMsg);
		    // 还款失败短信通知
		    boolean isCashOverdue = false;
		    if (StringUtil.isNotBlank(payType) && payType.indexOf("代扣") > -1) {
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(repaymentDo.getBorrowId());
			// 判断是否逾期，逾期不发短信
			try {
			    Date gmtPlanTime = afBorrowCashDo.getGmtPlanRepayment();
			    gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
			    Date newDate = new Date();
			    newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
	
			    if (gmtPlanTime.getTime() < newDate.getTime()) {
				isCashOverdue = true;
			    }
			} catch (Exception ex) {
			    logger.info("dealRepaymentFalse isCashOverdue error", ex);
			}
			if (isCashOverdue) {
			    logger.info("borrowCash overdue withhold false orverdue,mobile=" + afUserDo.getMobile() + "errorMsg:" + errorMsg);
			} else {
			    smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL.getCode());
			}
		    } else {
			errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(repaymentDo.getUserId());
			smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RECYCLE_REPAYMENT_BORROWCASH_FAIL.getCode());
			String title = "本次支付失败";
			String content = "支付失败：&errorMsg，您可更换其他银行卡或关联支付宝支付，若有疑问请联系客服4000025151咨询";
			content = content.replace("&errorMsg", errorMsg);
			pushService.pushUtil(title, content, afUserDo.getMobile());
		    }
		}

    }

    @Override
    public BigDecimal calculateRestAmount(AfBorrowCashDo cashDo) {
		BigDecimal restAmount = BigDecimal.ZERO;
		if (cashDo != null) {
		    restAmount = BigDecimalUtil.add(restAmount, cashDo.getAmount(), cashDo.getOverdueAmount(), cashDo.getSumOverdue(), cashDo.getRateAmount(), cashDo.getSumRate(), cashDo.getPoundage(), cashDo.getSumRenewalPoundage()).subtract(cashDo.getRepayAmount());
		}
		return restAmount;
    }

    private void generateRepayRecords(RepayBo bo) {

		AfRepaymentBorrowCashDo borrowRepaymentDo = buildRepayment(BigDecimal.ZERO, bo.repaymentAmount, bo.tradeNo, new Date(), bo.actualAmount, bo.couponId, bo.userCouponDto != null ? bo.userCouponDto.getAmount() : null, bo.rebateAmount, bo.borrowId, bo.cardId, bo.outTradeNo, bo.name, bo.userId, bo.repayType, bo.cardNo);
		afRepaymentBorrowCashDao.addRepaymentBorrowCash(borrowRepaymentDo);
	
		bo.borrowRepaymentDo = borrowRepaymentDo;
	
		logger.info("Repay.add repayment finish,name=" + bo.name + "tradeNo=" + bo.tradeNo + ",borrowRepayment=" + JSON.toJSONString(borrowRepaymentDo));
    }

    private Map<String, Object> doRepay(RepayBo bo, AfRepaymentBorrowCashDo repayment, String bankChannel) {
    	Map< String, Object> resultMap = new HashMap<String, Object>();
    	if (bo.cardId > 0) {// 银行卡支付
    	    AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(bo.cardId);
    	    RecycleKuaijieRepayBo bizObject = new RecycleKuaijieRepayBo(repayment, bo);
    	    if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
	    		repayment.setStatus(RepaymentStatus.SMS.getCode());
	    		resultMap = sendKuaiJieSms(bank.getRid(), bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(), bo.userDo.getIdNumber(),
	    			JSON.toJSONString(bizObject), "afBorrowRecycleRepaymentService",Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.BORROW_RECYCLE_REPAY.getCode());
    	    } else {// 代扣
	    		resultMap = doUpsPay(bankChannel, bank.getRid(), bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(),
	    			bo.userDo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_PAY_PURPOSE,bo.name, PayOrderSource.BORROW_RECYCLE_REPAY.getCode());
    	    }
    	} else if (bo.cardId == -2) {// 余额支付
    	    dealRepaymentSucess(bo.tradeNo, "");
    	    resultMap = getResultMap(bo, null);
    	}

    	return resultMap;
	}
    
    @Override
	protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo) {
		
	}

	@Override
	protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
		
	}

	@Override
	protected void kuaijieConfirmPre(String payTradeNo, String bankChannel,	String payBizObject) {
		
	}

	@Override
	protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
		RecycleKuaijieRepayBo kuaijieRepaymentBo = JSON.parseObject(payBizObject, RecycleKuaijieRepayBo.class);
		// 更新状态
		afRepaymentBorrowCashDao.status2Process(payTradeNo, kuaijieRepaymentBo.getRepayment().getRid());
		return getResultMap(kuaijieRepaymentBo.getBo(), respBo);
	}

	@Override
	protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
		if (StringUtils.isNotBlank(payBizObject)) {
		    // 处理业务数据
		    if (StringUtil.isNotBlank(respBo.getRespCode())) {
				dealRepaymentFail(payTradeNo, "", true, errorMsg);
				throw new FanbeiException(errorMsg);
		    } else {
		    	dealRepaymentFail(payTradeNo, "", false, "");
		    }
		} else {
		    // 未获取到缓存数据，支付订单过期
		    throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
		}
	}
    
	private Map<String, Object> getResultMap(RepayBo bo, UpsCollectRespBo respBo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", bo.borrowId);
		data.put("amount", bo.repaymentAmount.setScale(2, RoundingMode.HALF_UP));
		data.put("gmtCreate", new Date());
		data.put("status", AfBorrowCashRepmentStatus.YES.getCode());
		if (bo.userCouponDto != null) {
			data.put("couponAmount", bo.userCouponDto.getAmount());
		}
		if (bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0) {
			data.put("userAmount", bo.rebateAmount);
		}
		data.put("actualAmount", bo.actualAmount);
		data.put("cardName", bo.cardName);
		data.put("cardNumber", bo.cardNo);
		data.put("repayNo", bo.tradeNo);
		data.put("jfbAmount", BigDecimal.ZERO);
		if (respBo != null) {
			// data.put("resp", respBo);
			data.put("outTradeNo", respBo.getTradeNo());
		}

		return data;
	}

    private void preCheck(AfRepaymentBorrowCashDo repaymentDo, String tradeNo) {
		// 检查交易流水 对应记录数据库中是否已经处理
		if (repaymentDo != null && AfBorrowCashRepmentStatus.YES.getCode().equals(repaymentDo.getStatus())) {
		    throw new FanbeiException("preCheck,repayment has been dealed!"); // TODO
		}
	
		/* start 易宝支付侵入逻辑 */
		AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(tradeNo);
		if (afYibaoOrderDo != null) {
		    if (afYibaoOrderDo.getStatus().intValue() == 1) {
			throw new FanbeiException("preCheck,afYibaoOrderDo.status == 1,break!"); // TODO
		    } else {
			afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 1);
		    }
		}
		/* end 易宝支付侵入逻辑 */
    }

    /**
     * 需在事务管理块中调用此函数!
     *
     * @param repayDealBo
     * @param repaymentDo
     */
    private void dealBorrowRepay(RepayDealBo repayDealBo, AfRepaymentBorrowCashDo repaymentDo, String isBalance) {
		if (repaymentDo == null)
		    return;
	
		AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(repaymentDo.getBorrowId());
		cashDo.setRemark(repaymentDo.getOperator());
		repayDealBo.curRepayAmoutStub = repaymentDo.getRepaymentAmount();
		repayDealBo.curRebateAmount = repaymentDo.getRebateAmount();
		repayDealBo.curSumRebateAmount = repayDealBo.curSumRebateAmount.add(repaymentDo.getRebateAmount());
		repayDealBo.curUserCouponId = repaymentDo.getUserCouponId();
		repayDealBo.curSumRepayAmount = repayDealBo.curSumRepayAmount.add(repaymentDo.getRepaymentAmount());
		repayDealBo.curCardName = repaymentDo.getCardName();
		repayDealBo.curCardNo = repaymentDo.getCardNumber();
		repayDealBo.curName = repaymentDo.getName();
	
		repayDealBo.cashDo = cashDo;
		repayDealBo.overdueDay = cashDo.getOverdueDay();
		repayDealBo.borrowNo = cashDo.getBorrowNo();
		repayDealBo.refId += repaymentDo.getBorrowId();
		repayDealBo.userId = cashDo.getUserId();
	
		dealBorrowRepayOverdue(repayDealBo, cashDo);// 逾期费
		dealBorrowRepayPoundage(repayDealBo, cashDo);// 手续费
		dealBorrowRepayInterest(repayDealBo, cashDo);// 利息
	
		changBorrowRepaymentStatus(repayDealBo.curOutTradeNo, AfBorrowCashRepmentStatus.YES.getCode(), repaymentDo.getRid());
	
		cashDo.setSumRebate(BigDecimalUtil.add(cashDo.getSumRebate(), repaymentDo.getRebateAmount()));// 余额使用
		dealBorrowRepayIfFinish(repayDealBo, repaymentDo, cashDo, isBalance);
		afBorrowCashService.updateBorrowCash(cashDo);
    }

    private void dealSum(RepayDealBo repayDealBo) {
		AfBorrowCashDo cashDo = repayDealBo.cashDo;
	
		repayDealBo.sumBorrowAmount = repayDealBo.sumBorrowAmount.add(cashDo.getAmount());
		repayDealBo.sumRepaidAmount = repayDealBo.sumRepaidAmount.add(cashDo.getRepayAmount());
		repayDealBo.sumInterest = repayDealBo.sumInterest.add(cashDo.getRateAmount()).add(cashDo.getSumRate());
		repayDealBo.sumPoundage = repayDealBo.sumPoundage.add(cashDo.getPoundage()).add(cashDo.getSumRenewalPoundage());
		repayDealBo.sumOverdueAmount = repayDealBo.sumOverdueAmount.add(cashDo.getOverdueAmount()).add(cashDo.getSumOverdue());
		repayDealBo.sumIncome = repayDealBo.sumIncome.add(repayDealBo.sumPoundage).add(repayDealBo.sumOverdueAmount).add(repayDealBo.sumInterest);
	
		repayDealBo.sumAmount = repayDealBo.sumBorrowAmount.add(repayDealBo.sumIncome);
    }

    /**
     * 处理优惠卷 和 账户余额
     *
     * @param repayDealBo
     */
    private void dealCouponAndRebate(RepayDealBo repayDealBo) {
		AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(repayDealBo.userId);
	
		if (repayDealBo.curSumRebateAmount != null && repayDealBo.curSumRebateAmount.compareTo(BigDecimal.ZERO) > 0) {// 授权账户可用金额变更
		    accountInfo.setRebateAmount(accountInfo.getRebateAmount().subtract(repayDealBo.curSumRebateAmount));
		}
		afUserAccountDao.updateOriginalUserAccount(accountInfo);

		// add by luoxiao for 边逛边赚，增加零钱明细
		afTaskUserService.addTaskUser(accountInfo.getUserId(), UserAccountLogType.REPAYMENT.getName(), repayDealBo.curSumRebateAmount.multiply(new BigDecimal(-1)));
		// end by luoxiao

		if (repayDealBo.curUserCouponId != null && repayDealBo.curUserCouponId > 0) {
		    afUserCouponDao.updateUserCouponSatusUsedById(repayDealBo.curUserCouponId);// 优惠券设置已使用
		}
	
		// 解锁还款
		unLockRepay(repayDealBo.userId);
    }

    private void doAccountLog(RepayDealBo repayDealBo) {
		AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAY_CASH_LEGAL, repayDealBo.curSumRepayAmount, repayDealBo.userId, repayDealBo.refId);
		afUserAccountLogDao.addUserAccountLog(accountLog);// 增加日志
    }

    private void notifyUserBySms(RepayDealBo repayDealBo, String isBalance) {
		logger.info("notifyUserBySms info begin,sumAmount=" + repayDealBo.sumAmount + ",curSumRepayAmount=" + repayDealBo.curSumRepayAmount + ",sumRepaidAmount=" + repayDealBo.sumRepaidAmount + "Trade= " + repayDealBo.curTradeNo);
		try {
		    AfUserDo afUserDo = afUserService.getUserById(repayDealBo.userId);
			BigDecimal notRepayMoney = BigDecimal.ZERO;
		    if (repayDealBo.curName.equals("代扣付款")) {
			sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), repayDealBo.curSumRepayAmount);
		    } else {
				if (!YesNoStatus.YES.getCode().equals(isBalance)){
					notRepayMoney  = repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount);
				}
				sendRepaymentBorrowRecycleWarnMsg(afUserDo.getMobile(),repayDealBo.cashDo.getRid(),repayDealBo.curSumRepayAmount, notRepayMoney);
			}
		} catch (Exception e) {
		    logger.error("Sms notify user error, userId:" + repayDealBo.userId + ",nowRepayAmount:" + repayDealBo.curSumRepayAmount + ",notRepayMoney" + repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount), e);
		}
    }

	/**
	 * 用户手动现金贷还款成功短信发送
	 *
	 * @param mobile
	 * @param repayMoney
	 */
	private boolean sendRepaymentBorrowRecycleWarnMsg(String mobile,Long cashId,BigDecimal repayMoney, BigDecimal notRepayMoney) {
		// 模版数据map处理
		Map<String, String> replaceMapData = new HashMap<String, String>();
		AfBorrowRecycleOrderDo recycleOrderDo = recycleOrderService.getBorrowRecycleOrderByBorrowId(cashId);
		JSONObject propertyValue = (JSONObject) JSONObject.parse(recycleOrderDo.getPropertyValue());
		replaceMapData.put("repayMoney", repayMoney + "");
		replaceMapData.put("remainAmount", notRepayMoney + "");
		replaceMapData.put("param2", recycleOrderDo.getGoodsName() + "");
		replaceMapData.put("param3", String.valueOf(propertyValue.get("goodsModel")) + "");
		if (notRepayMoney == null || notRepayMoney.compareTo(BigDecimal.ZERO) <= 0) {
			String title = "恭喜您，借款已还清！";
			String content = "成功支付&repayMoney元，您的”&param2“”&param3“回收订单已完成，信用分再度升级，给您点个大大的赞！";
			content = content.replace("&repayMoney", repayMoney.toString()).replace("&param2", recycleOrderDo.getGoodsName()).replace("&param3", String.valueOf(propertyValue.get("goodsModel")));
			pushService.pushUtil(title, content, mobile);
			return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RECYCLE_REPAYMENT_SUCCESS.getCode());
		} else {
			String title = "部分还款成功！";
			String content = "成功支付&repayMoney元，剩余待支付金额&remainAmount元。";
			content = content.replace("&repayMoney", repayMoney.toString());
			content = content.replace("&remainAmount", notRepayMoney.toString());
			pushService.pushUtil(title, content, mobile);
			return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RECYCLE_REPAYMENT_SUCCESS_REMAIN.getCode());
		}
	}

    /**
     * 代扣现金贷还款成功短信发送
     *
     * @param mobile
     * @param nowRepayAmount
     */
    private boolean sendRepaymentBorrowCashWithHold(String mobile, BigDecimal nowRepayAmount) {
		// 模版数据map处理
		Map<String, String> replaceMapData = new HashMap<String, String>();
		replaceMapData.put("nowRepayAmountStr", nowRepayAmount + "");
		return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_SUCCESS.getCode());
    }

    /**
     * 用户手动现金贷还款成功短信发送
     *
     * @param mobile
     * @param repayMoney
     */
    private boolean sendRepaymentBorrowCashWarnMsg(String mobile, BigDecimal repayMoney, BigDecimal notRepayMoney) {
		// 模版数据map处理
		Map<String, String> replaceMapData = new HashMap<String, String>();
		replaceMapData.put("repayMoney", repayMoney + "");
		replaceMapData.put("remainAmount", notRepayMoney + "");
		if (notRepayMoney == null || notRepayMoney.compareTo(BigDecimal.ZERO) <= 0) {
		    String title = "恭喜您，借款已还清！";
		    String content = "您的还款已经处理完成，成功还款&repayMoney元。信用分再度升级，给您点个大大的赞！";
		    content = content.replace("&repayMoney", repayMoney.toString());
		    pushService.pushUtil(title, content, mobile);
		    return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS.getCode());
		} else {
		    String title = "部分还款成功！";
		    String content = "本次成功还款&repayMoney元，剩余待还金额&remainAmount元，请继续保持良好的信用习惯哦。";
		    content = content.replace("&repayMoney", repayMoney.toString());
		    content = content.replace("&remainAmount", notRepayMoney.toString());
		    pushService.pushUtil(title, content, mobile);
		    return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS_REMAIN.getCode());
		}
    }

    private void nofityRisk(RepayDealBo repayDealBo, AfBorrowCashDo cashDo) {
		logger.info("nofityRisk begin, borrowNo={}", cashDo.getBorrowNo());
		String cardNo = repayDealBo.curCardNo;
		cardNo = StringUtils.isNotBlank(cardNo) ? cardNo : String.valueOf(System.currentTimeMillis());
		Boolean isCashOverdueOld = false;
		try {
		    AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(repayDealBo.curTradeNo);
		    Date gmtPlanTime = cashDo.getGmtPlanRepayment();
		    gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
		    Date newDate = new Date();
		    newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
		    if (StringUtils.equals("代扣付款", repayment.getName()) && gmtPlanTime.getTime() < newDate.getTime()) {
			isCashOverdueOld = true;
		    }
		} catch (Exception ex) {
		    logger.info("dealRepaymentSucess isCashOverdue error", ex);
		}
		try {// 涉及运算,放在内部传输数据
		    String riskOrderNo = riskUtil.getOrderNo("tran", cardNo.substring(cardNo.length() - 4, cardNo.length()));
		    JSONArray details = new JSONArray();
		    JSONObject obj = new JSONObject();
		    obj.put("borrowNo", repayDealBo.borrowNo);
		    obj.put("amount", repayDealBo.sumBorrowAmount);
		    obj.put("repayment", repayDealBo.sumRepaidAmount);
		    obj.put("income", repayDealBo.sumIncome);
		    obj.put("interest", repayDealBo.sumInterest);
		    obj.put("overdueAmount", repayDealBo.sumOverdueAmount);
		    obj.put("overdueDay", repayDealBo.overdueDay);
		    details.add(obj);
		    riskUtil.transferBorrowInfo(repayDealBo.userId.toString(), "50", riskOrderNo, details);
		} catch (Exception e) {
		    logger.error("还款时给风控传输数据出错", e);
		}
	
		/**
		 * ------------------------------------fmai风控提额begin--------------------
		 * ----------------------------
		 */
		try {
		    if (AfBorrowCashStatus.finsh.getCode().equals(repayDealBo.cashDo.getStatus())) {
			String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
			int overdueCount = 0;
			if (StringUtil.equals("Y", repayDealBo.cashDo.getOverdueStatus())) {
			    overdueCount = 1;
			}
	
			// 收入添加搭售商品价格
			AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(cashDo.getRid());
			if (afBorrowLegalOrderDo != null && afBorrowLegalOrderDo.getPriceAmount() != null) {
			    repayDealBo.sumIncome = repayDealBo.sumIncome.add(afBorrowLegalOrderDo.getPriceAmount());
			} else {
			    logger.info("未获取到搭售商品信息 cashDo：" + repayDealBo.cashDo.toString());
			}
	
			riskUtil.raiseQuota(repayDealBo.userId.toString(), repayDealBo.borrowNo, "50", riskOrderNo, repayDealBo.sumBorrowAmount, repayDealBo.sumIncome, repayDealBo.overdueDay, overdueCount, cashDo.getOverdueDay(), cashDo.getRenewalNum());
		    }
		} catch (Exception e) {
		    logger.error("notifyRisk.raiseQuota error！", e);
		}
		/**
		 * ------------------------------------fmai风控提额end----------------------
		 * ----------------------------
		 */
	
		// 会对逾期的借款还款，向催收平台同步还款信息
		if (DateUtil.compareDate(new Date(), repayDealBo.cashDo.getGmtPlanRepayment()) && repayDealBo.curName != Constants.COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE) {
		    logger.info("collection consumerRepayment begin, borrowNo={}", repayDealBo.borrowNo);
		    try {
			CollectionSystemReqRespBo respInfo = collectionSystemUtil.consumerRepayment(repayDealBo.curTradeNo, repayDealBo.borrowNo, repayDealBo.curCardNo, repayDealBo.curCardName, DateUtil.formatDateTime(new Date()), repayDealBo.curOutTradeNo, repayDealBo.curSumRepayAmount, repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount).setScale(2, RoundingMode.HALF_UP), // 未还的
				repayDealBo.sumAmount.setScale(2, RoundingMode.HALF_UP), repayDealBo.sumOverdueAmount, repayDealBo.sumRepaidAmount, repayDealBo.sumInterest, isCashOverdueOld);
			logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
		    } catch (Exception e) {
			logger.error("向催收平台同步还款信息失败", e);
		    }
		} else {
		    logger.info("collection consumerRepayment not push,borrowCashId=" + repayDealBo.cashDo.getRid());
		}
    }

    private void dealBorrowRepayOverdue(RepayDealBo repayDealBo, AfBorrowCashDo afBorrowCashDo) {
		if (repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0)
		    return;
	
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

    private void dealBorrowRepayPoundage(RepayDealBo repayDealBo, AfBorrowCashDo afBorrowCashDo) {
		if (repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0)
		    return;
	
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

    private void dealBorrowRepayInterest(RepayDealBo repayDealBo, AfBorrowCashDo afBorrowCashDo) {
		if (repayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0)
		    return;
	
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

    private void dealBorrowRepayIfFinish(RepayDealBo repayDealBo, AfRepaymentBorrowCashDo repaymentDo, AfBorrowCashDo cashDo, String isBalance) {
		BigDecimal sumAmount = BigDecimalUtil.add(cashDo.getAmount(), cashDo.getOverdueAmount(), cashDo.getSumOverdue(), cashDo.getRateAmount(), cashDo.getSumRate(), cashDo.getPoundage(), cashDo.getSumRenewalPoundage());
		BigDecimal allRepayAmount = cashDo.getRepayAmount().add(repaymentDo.getRepaymentAmount());
		cashDo.setRepayAmount(allRepayAmount);
	
		BigDecimal minus = allRepayAmount.subtract(sumAmount); // 容许多还一块钱，兼容离线还款
								       // 场景
		if (minus.compareTo(BigDecimal.ZERO) >= 0 && minus.compareTo(BigDecimal.ONE) <= 0) {
		    cashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
		    cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
		} else if (YesNoStatus.YES.getCode().equals(isBalance)) {// 线下还款平账
		    cashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
		    cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
		}
	
		if (AfBorrowCashStatus.finsh.getCode().equals(cashDo.getStatus())) {
		    afUserAccountSenceService.syncLoanUsedAmount(repayDealBo.userId, SceneType.CASH, repayDealBo.cashDo.getAmount().negate());
		}
		// 判断是否代扣逾期则平账
		try {
		    Boolean isCashOverdueOld = false;
		    Date gmtPlanTime = cashDo.getGmtPlanRepayment();
		    gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
		    Date newDate = new Date();
		    newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
		    if (StringUtils.equals("代扣付款", repaymentDo.getName()) && gmtPlanTime.getTime() < newDate.getTime()) {
			isCashOverdueOld = true;
		    }
		    if (isCashOverdueOld) {
			cashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
			cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
		    }
		} catch (Exception ex) {
		    logger.info("dealRepaymentSucess isCashOverdue error", ex);
		}
    }

    private void checkOfflineRepayment(AfBorrowCashDo cashDo, String offlineRepayAmount, String outTradeNo) {
		if (afRepaymentBorrowCashDao.getRepaymentBorrowCashByTradeNo(null, outTradeNo) != null) {
		    throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_REPEAT_ERROR);
		}
	
		BigDecimal restAmount = calculateRestAmount(cashDo);
		BigDecimal offlineRepayAmountYuan = NumberUtil.objToBigDecimalDivideOnehundredDefault(offlineRepayAmount, BigDecimal.ZERO);
		// 因为有用户会多还几分钱，所以加个安全金额限制，当还款金额 > 用户应还金额+1元 时，返回错误
		if (offlineRepayAmountYuan.compareTo(restAmount.add(BigDecimal.valueOf(200))) > 0) {
		    logger.warn("CheckOfflineRepayment error, offlineRepayAmount=" + offlineRepayAmount + ", restAmount=" + restAmount);
		    throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
		}
    }

    private long changBorrowRepaymentStatus(String outTradeNo, String status, Long rid) {
		AfRepaymentBorrowCashDo repayment = new AfRepaymentBorrowCashDo();
		repayment.setStatus(status);
		repayment.setTradeNo(outTradeNo);
		repayment.setRid(rid);
		return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repayment);
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

    private AfRepaymentBorrowCashDo buildRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, String repayNo, Date gmtCreate, BigDecimal actualAmountForBorrow, Long userCouponId, BigDecimal couponAmountForBorrow, BigDecimal rebateAmountForBorrow, Long borrowId, Long cardId, String payTradeNo, String name, Long userId, String repayType, String cardNo) {
		AfRepaymentBorrowCashDo repay = new AfRepaymentBorrowCashDo();
		repay.setActualAmount(actualAmountForBorrow);
		repay.setBorrowId(borrowId);
		repay.setJfbAmount(jfbAmount);
		repay.setPayTradeNo(repayNo);
		repay.setRebateAmount(rebateAmountForBorrow);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(AfBorrowCashRepmentStatus.APPLY.getCode());
		repay.setUserCouponId(userCouponId);
		repay.setCouponAmount(couponAmountForBorrow);
		repay.setName(name);
		repay.setUserId(userId);
		repay.setTradeNo(payTradeNo);
		if (cardId == -2) {
		    repay.setCardNumber("");
		    repay.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		} else if (cardId == -1) {
		    repay.setCardNumber("");
		    repay.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		} else if (cardId == -3) {
		    repay.setCardNumber("");
		    repay.setCardName(Constants.DEFAULT_ZFB_PAY_NAME);
		} else if (cardId == -4) {
		    if (cardNo == null) {
			cardNo = "";
		    }
		    repay.setCardNumber(cardNo);
		    if ("alipay".equals(repayType)) {
			repay.setCardName("支付宝");
		    } else if ("bank".equals(repayType)) {
			repay.setCardName("银行卡");
		    } else {
			repay.setCardName("线下还款");
		    }
		    // repay.setCardName(repayType);
		} else {
		    AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
		    repay.setCardNumber(bank.getCardNumber());
		    repay.setCardName(bank.getBankName());
		}
		return repay;
    }
    

    public static class RepayBo {
		public Long userId;
	
		/* request字段 */
		public BigDecimal repaymentAmount = BigDecimal.ZERO;
		public BigDecimal actualAmount = BigDecimal.ZERO; // 可选字段
		public BigDecimal rebateAmount = BigDecimal.ZERO; // 可选字段
		public String payPwd; // 可选字段
		public Long cardId;
		public Long couponId; // 可选字段
		public Long borrowId; // 可选字段
		/* request字段 */
	
		/* biz 业务处理字段 */
		public AfRepaymentBorrowCashDo borrowRepaymentDo;
		public AfBorrowCashDo cashDo;
		public AfUserCouponDto userCouponDto; // 可选字段
		public AfUserAccountDo userDo;
		public String remoteIp;
		public String name;
		public String repayType;
		public String isBalance;
		/* biz 业务处理字段 */
	
		/* Response字段 */
		public String cardName; // 交易卡名称
		public String cardNo; // 交易卡号
		public String outTradeNo; // 资金方放交易流水号
		public String tradeNo; // 我方交易流水号
		/* Response字段 */
	
		/* 错误码区域 */
		public Exception e;

    }

    public static class RepayDealBo {
		BigDecimal curRepayAmoutStub; // 当前还款额变化句柄
		BigDecimal curRebateAmount; // 当前还款使用的账户余额
		BigDecimal curSumRebateAmount = BigDecimal.ZERO;// 当前还款使用的账户余额总额
		Long curUserCouponId; // 当前还款使用的还款优惠卷id
		BigDecimal curSumRepayAmount = BigDecimal.ZERO; // 当前还款总额
		String curCardNo; // 当前还款卡号
		String curCardName; // 当前还款卡别名
		String curName; // 当前还款名称，用来识别自动代付还款
		String curTradeNo;
		String curOutTradeNo;
	
		BigDecimal sumRepaidAmount = BigDecimal.ZERO; // 对应借款的还款总额
		BigDecimal sumAmount = BigDecimal.ZERO; // 对应借款总额（包含所有费用）
		BigDecimal sumBorrowAmount = BigDecimal.ZERO; // 对应借款总额（不包含其他费用）
		BigDecimal sumInterest = BigDecimal.ZERO; // 对应借款的利息总额
		BigDecimal sumPoundage = BigDecimal.ZERO; // 对应借款的手续费总额
		BigDecimal sumOverdueAmount = BigDecimal.ZERO; // 对应借款的逾期费总额
		BigDecimal sumIncome = BigDecimal.ZERO; // 对应借款我司产生的总收入
	
		AfBorrowCashDo cashDo; // 借款
		long overdueDay = 0; // 对应借款的逾期天数
		String borrowNo; // 借款流水号
		String refId = ""; // 还款的id串
		Long userId; // 目标用户id
    }

}
