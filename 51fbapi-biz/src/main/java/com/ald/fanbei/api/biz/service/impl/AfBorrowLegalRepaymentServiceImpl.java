package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.KuaijieRepayBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalRepaymentService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.UpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepayFromEnum;
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepaymentStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.RepaymentStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 * 借款合规订单的还款service
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowLegalRepaymentService")
public class AfBorrowLegalRepaymentServiceImpl extends UpsPayKuaijieServiceAbstract implements AfBorrowLegalRepaymentService {

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

    @Resource
    AfYibaoOrderDao afYibaoOrderDao;
    @Resource
    YiBaoUtility yiBaoUtility;
    
    @Resource
    RedisTemplate<String, ?> redisTemplate;
    /* copy自 AfRepaymentBorrowCashServiceImpl */
    
	
	@Resource
    private AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;
	@Resource
    private AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;
	@Resource
	private AfBorrowLegalOrderDao afBorrowLegalOrderDao;
	@Resource
    private AfTradeCodeInfoService afTradeCodeInfoService;

	@Autowired
	private AfBorrowLegalOrderService afBorrowLegalOrderService;
	@Resource
	KafkaSync kafkaSync;
	@Resource
	CuiShouUtils cuiShouUtils;
	@Resource
	AfTaskUserService afTaskUserService;

	/**
	 * 新版还钱函
	 * 参考{@link com.ald.fanbei.api.biz.service.impl.AfRepaymentBorrowCashServiceImpl}.createRepayment()
	 * @return
	 */
	@Override
	public Map<String, Object> repay(RepayBo bo,String bankPayType) {
		
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
		bo.borrowOrderCashId = bo.orderCashDo.getRid();
	
		generateRepayRecords(bo);
	
		return doRepay(bo, bo.borrowRepaymentDo, bo.orderRepaymentDo,bankPayType);
		
	}
	
	/**
     * 线下还款
     * 
     * @param restAmount 
     */
	@Override
	public void offlineRepay(AfBorrowLegalOrderCashDo orderCashDo, String borrowNo,
							 String repayType, String repayTime, String repayAmount,
							 String restAmount, String outTradeNo, String isBalance,String repayCardNum,String operator,String isAdmin) {
		RepayBo bo = new RepayBo();
		bo.userId = orderCashDo.getUserId();
		bo.userDo = afUserAccountDao.getUserAccountInfoByUserId(bo.userId);

		bo.cardId = (long) -4;
		bo.repaymentAmount = NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO);
		bo.actualAmount =  bo.repaymentAmount;
		bo.borrowId = orderCashDo.getBorrowId();
		bo.borrowOrderId = orderCashDo.getBorrowLegalOrderId();
		bo.borrowOrderCashId = orderCashDo.getRid();
		bo.from = AfBorrowLegalRepayFromEnum.INDEX.name();

		bo.tradeNo = generatorClusterNo.getOfflineRepaymentBorrowCashNo(new Date());
		if (isAdmin != null && "Y".equals(isAdmin)){
			bo.name = Constants.BORROW_REPAYMENT_NAME_OFFLINE;//财务线下打款
		}else {
			bo.name = Constants.COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE;//催收线下打款
		}
		bo.isBalance = isBalance;//判断是否平账
		bo.outTradeNo = outTradeNo;
		bo.repayType = repayType;
		bo.cardNo = repayCardNum;
		generateRepayRecords(bo);

        CuiShouUtils.setAfRepaymentBorrowCashDo(bo.borrowRepaymentDo);
		dealRepaymentSucess(bo.tradeNo, bo.outTradeNo, bo.borrowRepaymentDo, bo.orderRepaymentDo,operator,bo.isBalance);

	}


	/**
	 * 还款成功后调用
	 * @param tradeNo 我方交易流水
	 * @param outTradeNo 资金方交易流水
	 * @return
	 */
	@Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfRepaymentBorrowCashDo repaymentDo, final AfBorrowLegalOrderRepaymentDo orderRepaymentDo, String operator, final String isBalance) {
    	try {
    		lock(tradeNo);
    		
            logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo) + ", orderRepayment=" + JSON.toJSONString(orderRepaymentDo));
            
            preCheck(repaymentDo, orderRepaymentDo, tradeNo);
			repaymentDo.setOperator(operator);
            final RepayDealBo repayDealBo = new RepayDealBo();
            repayDealBo.curTradeNo = tradeNo;
            repayDealBo.curOutTradeNo = outTradeNo;
            
            long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus status) {
                    try {
                		dealOrderRepay(repayDealBo, orderRepaymentDo,isBalance);
                		dealBorrowRepay(repayDealBo, repaymentDo,isBalance);
                		
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

					notifyUserBySms(repayDealBo, isBalance);
					nofityRisk(repayDealBo);
				}
				catch (Exception e){
					logger.error("nofityRisk or notifyUserBySms error",e);
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
        final AfBorrowLegalOrderRepaymentDo orderRepaymentDo = afBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByPayTradeNo(tradeNo);
        dealRepaymentSucess(tradeNo, outTradeNo, repaymentDo, orderRepaymentDo,null,null);
    }
    
    /**
     * 还款失败后调用
     */
    @Override
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice,String errorMsg) {
		final AfRepaymentBorrowCashDo repaymentDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(tradeNo);
        final AfBorrowLegalOrderRepaymentDo orderRepaymentDo = afBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByPayTradeNo(tradeNo);
        logger.info("dealRepaymentFail process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo 
        		+ ",isNeedMsgNotice=" + isNeedMsgNotice + ",errorMsg=" + errorMsg 
        		+ ",borrowRepayment=" + JSON.toJSONString(repaymentDo) + ", orderRepayment=" + JSON.toJSONString(orderRepaymentDo));
        
        if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) ) // 检查交易流水 对应记录数据库中是否已经处理
        		|| (orderRepaymentDo != null && YesNoStatus.YES.getCode().equals(orderRepaymentDo.getStatus()) )) {
            return;
        }
        
        if(repaymentDo != null) {
			changBorrowRepaymentStatus(outTradeNo, AfBorrowCashRepmentStatus.NO.getCode(), repaymentDo.getRid());
		}
		if(orderRepaymentDo != null) {
			changOrderRepaymentStatus(outTradeNo, AfBorrowLegalRepaymentStatus.NO.getCode(), orderRepaymentDo.getId());
		}
		
		// 解锁还款
     	unLockRepay(repaymentDo.getUserId());
        
		if(isNeedMsgNotice){
			//用户信息及当日还款失败次数校验
			int errorTimes = 0;
			AfUserDo afUserDo = afUserService.getUserById(repaymentDo.getUserId());
			//如果是代扣，不校验次数
			String payType = repaymentDo.getName();
			//模版数据map处理
			Map<String,String> replaceMapData = new HashMap<String, String>();
			replaceMapData.put("errorMsg", errorMsg);
			//还款失败短信通知
			if(StringUtil.isNotBlank(payType)&&payType.indexOf("代扣")>-1){
				smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL.getCode());
			}else{
				errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(repaymentDo.getUserId());
				smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_FAIL.getCode());
				String title = "本次还款支付失败";
				String content = "非常遗憾，本次还款失败：&errorMsg，您可更换银行卡或采用其他还款方式。";
				content = content.replace("&errorMsg",errorMsg);
				pushService.pushUtil(title,content,afUserDo.getMobile());
			}
		}
		
	}
    
    private void generateRepayRecords(RepayBo bo) {
    	Date now = new Date();
    	String tradeNo = bo.tradeNo;
    	String name = bo.name;
    	AfRepaymentBorrowCashDo borrowRepaymentDo = null;
		AfBorrowLegalOrderRepaymentDo orderRepaymentDo = null;
		if(AfBorrowLegalRepayFromEnum.INDEX.name().equalsIgnoreCase(bo.from)) {
			AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(bo.borrowId);
			bo.borrowOrderId = orderCashDo.getBorrowLegalOrderId();
			
			BigDecimal orderSumAmount = orderCashDo.getAmount()
						.add(orderCashDo.getOverdueAmount()).add(orderCashDo.getSumRepaidOverdue())
						.add(orderCashDo.getPoundageAmount()).add(orderCashDo.getSumRepaidPoundage())
						.add(orderCashDo.getInterestAmount()).add(orderCashDo.getSumRepaidInterest());
			BigDecimal orderRemainShouldRepayAmount = orderSumAmount.subtract(orderCashDo.getRepaidAmount()); //剩余应还金额
			BigDecimal borrowRepayAmount = bo.repaymentAmount.subtract(orderRemainShouldRepayAmount);
			if(borrowRepayAmount.compareTo(BigDecimal.ZERO) > 0) { //还款额大于订单应还总额，拆分还款
				BigDecimal couponAmountForBorrow = BigDecimal.ZERO;
				BigDecimal couponAmountForOrder = BigDecimal.ZERO;
				BigDecimal rabateAmountForBorrow = BigDecimal.ZERO;
				BigDecimal rabateAmountForOrder = BigDecimal.ZERO;
				BigDecimal actualAmountForBorrow = BigDecimal.ZERO;
				BigDecimal actualAmountForOrder = BigDecimal.ZERO;
				
				BigDecimal couponAmount = bo.userCouponDto != null?bo.userCouponDto.getAmount():BigDecimal.ZERO;
				
				BigDecimal minusAmount = couponAmount.subtract(orderRemainShouldRepayAmount);
				if(minusAmount.compareTo(BigDecimal.ZERO) >= 0) { 			// 优惠卷面额 大于 订单应还金额
					couponAmountForOrder = orderRemainShouldRepayAmount; 
					if(minusAmount.compareTo(borrowRepayAmount) >= 0) { 		//拆分的优惠卷面额 大于等于 本次借款可还金额
						couponAmountForBorrow = borrowRepayAmount;
					} else { 													//拆分的优惠卷面额 小于 本次借款可还金额
						couponAmountForBorrow = minusAmount;
						BigDecimal borrowRemainRepayAmount = borrowRepayAmount.subtract(minusAmount);
						if(bo.rebateAmount.compareTo(borrowRemainRepayAmount) >= 0) {// 账户余额 大于等于 本次借款剩余可还金额
							rabateAmountForBorrow = borrowRemainRepayAmount;
						} else {													 // 账户余额 小于 本次借款剩余可还金额
							rabateAmountForBorrow = bo.rebateAmount;
							actualAmountForBorrow = bo.actualAmount;
						}
					}
				}else {														// 优惠卷面额 小于 订单应还金额
					couponAmountForOrder = couponAmount;
					minusAmount = minusAmount.abs();//差值
					if(bo.rebateAmount.compareTo(minusAmount) >= 0) {			//账户余额 大于等于 订单剩余应还金额
						rabateAmountForOrder = minusAmount;
						rabateAmountForBorrow = bo.rebateAmount.subtract(rabateAmountForOrder);
						actualAmountForBorrow = bo.actualAmount;
					} else {													//账户余额 小于 订单剩余应还金额
						rabateAmountForOrder = bo.rebateAmount;
						minusAmount = minusAmount.subtract(rabateAmountForOrder);
						if(bo.actualAmount.compareTo(minusAmount) >= 0) {			//现金额 大于等于 订单剩余应还金额
							actualAmountForOrder = minusAmount;
							actualAmountForBorrow = bo.actualAmount.subtract(actualAmountForOrder);
						}else {
							actualAmountForOrder = bo.actualAmount;
						}
					}
				}

				borrowRepaymentDo = buildRepayment(BigDecimal.ZERO, borrowRepayAmount, tradeNo, now, actualAmountForBorrow, bo.couponId,
						couponAmountForBorrow, rabateAmountForBorrow, bo.borrowId, bo.cardId, bo.outTradeNo, name, bo.userId,bo.repayType,bo.cardNo);
				afRepaymentBorrowCashDao.addRepaymentBorrowCash(borrowRepaymentDo);

				if(!AfBorrowLegalOrderCashStatus.FINISHED.getCode().equals(orderCashDo.getStatus())) {
					orderRepaymentDo = buildOrderRepayment(bo, actualAmountForOrder, bo.couponId,
							couponAmountForOrder, rabateAmountForOrder, orderRemainShouldRepayAmount,bo.repayType,bo.outTradeNo,bo.cardNo);
					afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(orderRepaymentDo);
				}
			} else { //还款全部进入订单欠款中
				orderRepaymentDo = buildOrderRepayment(bo, bo.actualAmount, bo.couponId,
						bo.userCouponDto != null?bo.userCouponDto.getAmount():null, bo.rebateAmount, bo.repaymentAmount,bo.repayType,bo.outTradeNo,bo.cardNo);
				afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(orderRepaymentDo);
			}
		}

		else if (AfBorrowLegalRepayFromEnum.BORROW.name().equalsIgnoreCase(bo.from)) {
			borrowRepaymentDo = buildRepayment(BigDecimal.ZERO, bo.repaymentAmount, tradeNo, now, bo.actualAmount, bo.couponId,
					bo.userCouponDto != null?bo.userCouponDto.getAmount():null, bo.rebateAmount, bo.borrowId, bo.cardId, bo.outTradeNo, name, bo.userId,bo.repayType,bo.cardNo);
			afRepaymentBorrowCashDao.addRepaymentBorrowCash(borrowRepaymentDo);
		}

		else if(AfBorrowLegalRepayFromEnum.ORDER.name().equalsIgnoreCase(bo.from)) {
			AfBorrowLegalOrderDo orderDo = afBorrowLegalOrderDao.getById(bo.borrowOrderId);
			bo.borrowId = orderDo.getBorrowId();
			orderRepaymentDo = buildOrderRepayment(bo, bo.actualAmount, bo.couponId,
					bo.userCouponDto != null?bo.userCouponDto.getAmount():null, bo.rebateAmount, bo.repaymentAmount,bo.repayType,bo.outTradeNo,bo.cardNo);
			afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(orderRepaymentDo);
		}
		
		bo.orderRepaymentDo = orderRepaymentDo;
		bo.borrowRepaymentDo = borrowRepaymentDo;
		
		logger.info("Repay.add repayment finish,name="+ name +"tradeNo="+tradeNo+",borrowRepayment="+ JSON.toJSONString(borrowRepaymentDo) + ",legalOrderRepayment="+ JSON.toJSONString(orderRepaymentDo));
    }
    
    private Map<String, Object> doRepay(RepayBo bo, AfRepaymentBorrowCashDo repayment, AfBorrowLegalOrderRepaymentDo legalOrderRepayment, String bankChannel) {
	Map<String, Object> resultMap = new HashMap<String, Object>();
	if (bo.cardId > 0) {// 银行卡支付
	    AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(bo.cardId);
	    KuaijieRepayBo bizObject = new KuaijieRepayBo(repayment, legalOrderRepayment, bo);
	    if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
		repayment.setStatus(RepaymentStatus.SMS.getCode());
		resultMap = sendKuaiJieSms(bank.getRid(), bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(), bo.userDo.getIdNumber(),
			JSON.toJSONString(bizObject), "afBorrowLegalRepaymentService", Constants.DEFAULT_PAY_PURPOSE,bo.name, PayOrderSource.REPAY_CASH_LEGAL.getCode());
	    } else {// 代扣
		resultMap = doUpsPay(bankChannel, bank.getRid(), bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(),
			bo.userDo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_CASH_LEGAL.getCode());
	    }
	} else if (bo.cardId == -2) {// 余额支付
	    dealRepaymentSucess(bo.tradeNo, "", repayment, legalOrderRepayment, null, null);
	    resultMap = getResultMap(bo, null);
	}

	return resultMap;
    }

    @Override
    protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo) {
//	KuaijieRepayBo kuaijieRepaymentBo = JSON.parseObject(payBizObject, KuaijieRepayBo.class);
//	changOrderRepaymentStatus(payTradeNo, AfBorrowLegalRepaymentStatus.SMS.getCode(), kuaijieRepaymentBo.getLegalOrderRepayment().getId());
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
	if (kuaijieRepaymentBo.getLegalOrderRepayment() != null) {
	    // 更新状态
	    changOrderRepaymentStatus(payTradeNo, AfBorrowLegalRepaymentStatus.PROCESS.getCode(), kuaijieRepaymentBo.getLegalOrderRepayment().getId());
	}

	return getResultMap(kuaijieRepaymentBo.getBo(), respBo);
    }

    private Map<String, Object> getResultMap(RepayBo bo,UpsCollectRespBo respBo)
    {
	Map<String, Object> data = Maps.newHashMap();
	data.put("rid", bo.borrowId);
	data.put("amount", bo.repaymentAmount.setScale(2, RoundingMode.HALF_UP));
	data.put("gmtCreate", new Date());
	data.put("status", AfBorrowLegalRepaymentStatus.YES.getCode());
	if(bo.userCouponDto != null) {
		data.put("couponAmount", bo.userCouponDto.getAmount());
	}
	if(bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0) {
		data.put("userAmount", bo.rebateAmount);
	}
	data.put("actualAmount", bo.actualAmount);
	data.put("cardName", bo.cardName);
	data.put("cardNumber", bo.cardNo);
	data.put("repayNo", bo.tradeNo);
	data.put("jfbAmount", BigDecimal.ZERO);
	if(respBo!=null)
	{
	    //data.put("resp", respBo);
	    data.put("outTradeNo", respBo.getTradeNo());
	}

	return data;
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

    private void preCheck(AfRepaymentBorrowCashDo repaymentDo, AfBorrowLegalOrderRepaymentDo orderRepaymentDo, String tradeNo) {
    	// 检查交易流水 对应记录数据库中是否已经处理
        if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) ) 
        		|| (orderRepaymentDo != null && YesNoStatus.YES.getCode().equals(orderRepaymentDo.getStatus()) )) {
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
	 * @param repayDealBo
	 * @param orderRepaymentDo
	 */
	private void dealOrderRepay(RepayDealBo repayDealBo, AfBorrowLegalOrderRepaymentDo orderRepaymentDo,String isBalance) {
		if(orderRepaymentDo == null) return;
		
		AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashDao.getById(orderRepaymentDo.getBorrowLegalOrderCashId());
		AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(orderCashDo.getBorrowId());
		
		repayDealBo.curRepayAmoutStub = orderRepaymentDo.getRepayAmount();
		repayDealBo.curRebateAmount = orderRepaymentDo.getRebateAmount();
		repayDealBo.curSumRebateAmount = repayDealBo.curSumRebateAmount.add(orderRepaymentDo.getRebateAmount());
		repayDealBo.curUserCouponId = orderRepaymentDo.getUserCouponId();
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
        afBorrowLegalOrderCashDao.updateById(orderCashDo);
        
        changOrderRepaymentStatus(repayDealBo.curOutTradeNo, AfBorrowLegalRepaymentStatus.YES.getCode(), orderRepaymentDo.getId());
	}
    
    /**
	 * 需在事务管理块中调用此函数!
	 * @param repayDealBo
	 * @param repaymentDo
	 */
	private void dealBorrowRepay(RepayDealBo repayDealBo, AfRepaymentBorrowCashDo repaymentDo,String isBalance) {
		if(repaymentDo == null) return;
		
		AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(repaymentDo.getBorrowId());
		AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(cashDo.getRid());//
		cashDo.setRemark(repaymentDo.getOperator());
		repayDealBo.curRepayAmoutStub = repaymentDo.getRepaymentAmount();
		repayDealBo.curRebateAmount = repaymentDo.getRebateAmount();
		repayDealBo.curSumRebateAmount = repayDealBo.curSumRebateAmount.add(repaymentDo.getRebateAmount());
		repayDealBo.curUserCouponId = repaymentDo.getUserCouponId();
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
        
        changBorrowRepaymentStatus(repayDealBo.curOutTradeNo, AfBorrowCashRepmentStatus.YES.getCode(), repaymentDo.getRid());
        
        cashDo.setSumRebate(BigDecimalUtil.add(cashDo.getSumRebate(), repaymentDo.getRebateAmount()));//余额使用
        dealBorrowRepayIfFinish(repayDealBo, repaymentDo, cashDo,isBalance);
        afBorrowCashService.updateBorrowCash(cashDo);
	}
	
	private void dealSum(RepayDealBo repayDealBo){
		AfBorrowLegalOrderCashDo orderCashDo = repayDealBo.orderCashDo;
		AfBorrowCashDo cashDo = repayDealBo.cashDo;
		
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
     * 处理优惠卷 和 账户余额
     * @param repayDealBo
     */
    private void dealCouponAndRebate(RepayDealBo repayDealBo) {
    	AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(repayDealBo.userId);
    	if (AfBorrowCashStatus.finsh.getCode().equals(repayDealBo.cashDo.getStatus()) && 
        		AfBorrowLegalOrderCashStatus.FINISHED.getCode().equals(repayDealBo.orderCashDo.getStatus()) ) {
    		AfBorrowLegalOrderCashDo primaryOrderCashDo = afBorrowLegalOrderCashDao.getPrimaryOrderCashByBorrowId(repayDealBo.cashDo.getRid());
    		accountInfo.setUsedAmount(accountInfo.getUsedAmount().subtract(repayDealBo.cashDo.getAmount().add(primaryOrderCashDo.getAmount())));
    	}
    	
    	if(repayDealBo.curSumRebateAmount != null && repayDealBo.curSumRebateAmount.compareTo(BigDecimal.ZERO) > 0) {// 授权账户可用金额变更
            accountInfo.setRebateAmount(accountInfo.getRebateAmount().subtract(repayDealBo.curSumRebateAmount));
    	}
    	afUserAccountDao.updateOriginalUserAccount(accountInfo);

		// add by luoxiao for 边逛边赚，增加零钱明细
		afTaskUserService.addTaskUser(accountInfo.getUserId(), UserAccountLogType.REPAYMENT.getName(), repayDealBo.curSumRebateAmount.multiply(new BigDecimal(-1)));
		// end by luoxiao
    	
    	if(repayDealBo.curUserCouponId != null && repayDealBo.curUserCouponId > 0) {
    		afUserCouponDao.updateUserCouponSatusUsedById(repayDealBo.curUserCouponId);// 优惠券设置已使用
    	}
    	
    	// 解锁还款
    	unLockRepay(repayDealBo.userId);
    }
    
    private void doAccountLog(RepayDealBo repayDealBo) {
    	AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAY_CASH_LEGAL, repayDealBo.curSumRepayAmount, repayDealBo.userId, repayDealBo.refId);
        afUserAccountLogDao.addUserAccountLog(accountLog);//增加日志
    }
    
    private void notifyUserBySms(RepayDealBo repayDealBo,String isBalance) {
    	logger.info("notifyUserBySms info begin,sumAmount="+repayDealBo.sumAmount+",curSumRepayAmount="+repayDealBo.curSumRepayAmount+",sumRepaidAmount="+repayDealBo.sumRepaidAmount);
        try {
            AfUserDo afUserDo = afUserService.getUserById(repayDealBo.userId);
            if(repayDealBo.curName.equals("代扣付款")){
                sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), repayDealBo.sumAmount);
            }else if (YesNoStatus.YES.getCode().equals(isBalance)){
				sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), repayDealBo.curSumRepayAmount, BigDecimal.ZERO);
			} else{
            	sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), repayDealBo.curSumRepayAmount, repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount));
            }
        } catch (Exception e) {
            logger.error("Sms notify user error, userId:" + repayDealBo.userId + ",nowRepayAmount:" + repayDealBo.curSumRepayAmount + ",notRepayMoney" + repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount), e);
        }
    }
    
    /**
     * 代扣现金贷还款成功短信发送
     * @param mobile
     */
    private boolean sendRepaymentBorrowCashWithHold(String mobile,BigDecimal nowRepayAmount){
    	//模版数据map处理
		Map<String,String> replaceMapData = new HashMap<String, String>();
		replaceMapData.put("nowRepayAmountStr", nowRepayAmount+"");
		return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_SUCCESS.getCode());
    }
    
    
    /**
     * 用户手动现金贷还款成功短信发送
     * @param mobile
     */
    private boolean sendRepaymentBorrowCashWarnMsg(String mobile,BigDecimal repayMoney,BigDecimal notRepayMoney){
    	//模版数据map处理
    	Map<String,String> replaceMapData = new HashMap<String, String>();
 		replaceMapData.put("repayMoney", repayMoney+"");
 		replaceMapData.put("remainAmount", notRepayMoney+"");
         if (notRepayMoney==null || notRepayMoney.compareTo(BigDecimal.ZERO)<=0) {
			 String title = "恭喜您，借款已还清！";
			 String content = "您的还款已经处理完成，成功还款&repayMoney元。信用分再度升级，给您点个大大的赞！";
			 content = content.replace("&repayMoney",repayMoney.toString());
			 pushService.pushUtil(title,content,mobile);
         	return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS.getCode());
         } else {
			 String title = "部分还款成功！";
			 String content = "本次成功还款&repayMoney元，剩余待还金额&remainAmount元，请继续保持良好的信用习惯哦。";
			 content = content.replace("&repayMoney",repayMoney.toString());
			 content = content.replace("&remainAmount",notRepayMoney.toString());
			 pushService.pushUtil(title,content,mobile);
         	return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS_REMAIN.getCode());
         }
    }
    
    private void nofityRisk(RepayDealBo repayDealBo) {
    	String cardNo = repayDealBo.curCardNo;
    	cardNo = StringUtils.isNotBlank(cardNo)?cardNo:String.valueOf(System.currentTimeMillis());
    	
    	try {//涉及运算,放在内部传输数据
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
    	
        /**------------------------------------fmai风控提额begin------------------------------------------------*/
        try {
            if (AfBorrowCashStatus.finsh.getCode().equals(repayDealBo.cashDo.getStatus()) && 
            		AfBorrowLegalOrderCashStatus.FINISHED.getCode().equals(repayDealBo.orderCashDo.getStatus()) ) {
            	String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                int overdueCount = 0;
                if (StringUtil.equals("Y", repayDealBo.cashDo.getOverdueStatus())) {
                    overdueCount = 1;
                }

				final AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(repayDealBo.cashDo.getRid());
				//收入添加搭售商品价格
				AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(cashDo.getRid());
				if(afBorrowLegalOrderDo!=null&&afBorrowLegalOrderDo.getPriceAmount()!=null) {
				    repayDealBo.sumIncome =repayDealBo.sumIncome.add(afBorrowLegalOrderDo.getPriceAmount());
				}
				else {
					logger.info("未获取到搭售商品信息 cashDo："+repayDealBo.cashDo.toString());
				}

                riskUtil.raiseQuota(repayDealBo.userId.toString(), 
                			repayDealBo.borrowNo, "50", riskOrderNo, 
                			repayDealBo.sumBorrowAmount,
                			repayDealBo.sumIncome, 
                			repayDealBo.overdueDay,
                			overdueCount,
                			repayDealBo.overdueDay,
                			repayDealBo.renewalNum);
            }
        } catch (Exception e) {
            logger.error("notifyRisk.raiseQuota error！", e);
        }
        /**------------------------------------fmai风控提额end--------------------------------------------------*/

        //会对逾期的借款还款，向催收平台同步还款信息
        if (DateUtil.compareDate(new Date(), repayDealBo.cashDo.getGmtPlanRepayment()) && repayDealBo.curName != Constants.COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE){
            try {
                CollectionSystemReqRespBo respInfo = collectionSystemUtil.consumerRepayment(
                		repayDealBo.curTradeNo,
                		repayDealBo.borrowNo,
                		repayDealBo.curCardNo,
                		repayDealBo.curCardName,
                        DateUtil.formatDateTime(new Date()),
                        repayDealBo.curOutTradeNo,
                        repayDealBo.curSumRepayAmount,
                        repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount).setScale(2, RoundingMode.HALF_UP), //未还的
                        repayDealBo.sumAmount.setScale(2, RoundingMode.HALF_UP),	
                        repayDealBo.sumOverdueAmount,
                		repayDealBo.sumRepaidAmount,
                		repayDealBo.sumInterest,false);
                logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
            } catch (Exception e) {
                logger.error("向催收平台同步还款信息失败", e);
            }
        }else{
			logger.info("collection consumerRepayment not push,borrowCashId="+repayDealBo.cashDo.getRid());
		}
    }
	
    private void dealOrderRepayOverdue(RepayDealBo repayDealBo, AfBorrowLegalOrderCashDo orderCashDo) {
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
	private void dealOrderRepayPoundage(RepayDealBo repayDealBo, AfBorrowLegalOrderCashDo orderCashDo) {
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
	private void dealOrderRepayInterest(RepayDealBo repayDealBo, AfBorrowLegalOrderCashDo orderCashDo) {
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
	private void dealOrderRepayIfFinish(RepayDealBo repayDealBo, AfBorrowLegalOrderRepaymentDo orderRepaymentBo, AfBorrowLegalOrderCashDo orderCashDo,String isBalance) {
		BigDecimal sumAmount = BigDecimalUtil.add(orderCashDo.getAmount(), 
														orderCashDo.getOverdueAmount(),orderCashDo.getSumRepaidOverdue(),
														orderCashDo.getPoundageAmount(),orderCashDo.getSumRepaidPoundage(),
														orderCashDo.getInterestAmount(),orderCashDo.getSumRepaidInterest());
		BigDecimal allRepayAmount = orderCashDo.getRepaidAmount().add(orderRepaymentBo.getRepayAmount());
		Date now = new Date();
		orderCashDo.setRepaidAmount(allRepayAmount);
		orderCashDo.setGmtLastRepayment(now);
		
		if (sumAmount.compareTo(allRepayAmount) == 0) {
			orderCashDo.setStatus(AfBorrowLegalOrderCashStatus.FINISHED.getCode());
			orderCashDo.setGmtFinish(now);
        }else if (YesNoStatus.YES.getCode().equals(isBalance)){//线下还款平账
			orderCashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
			orderCashDo.setGmtFinish(now);
		} else {
        	orderCashDo.setStatus(AfBorrowLegalOrderCashStatus.PART_REPAID.getCode());
        }
	}
	
	private void dealBorrowRepayOverdue(RepayDealBo repayDealBo, AfBorrowCashDo afBorrowCashDo) {
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
	private void dealBorrowRepayPoundage(RepayDealBo repayDealBo, AfBorrowCashDo afBorrowCashDo) {
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
	private void dealBorrowRepayInterest(RepayDealBo repayDealBo, AfBorrowCashDo afBorrowCashDo) {
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
	private void dealBorrowRepayIfFinish(RepayDealBo repayDealBo, AfRepaymentBorrowCashDo repaymentDo, AfBorrowCashDo cashDo,String isBalance) {
		BigDecimal sumAmount = BigDecimalUtil.add(cashDo.getAmount(), 
					cashDo.getOverdueAmount(), cashDo.getSumOverdue(),
					cashDo.getRateAmount(), cashDo.getSumRate(),
					cashDo.getPoundage(), cashDo.getSumRenewalPoundage());
		BigDecimal allRepayAmount = cashDo.getRepayAmount().add(repaymentDo.getRepaymentAmount());
		cashDo.setRepayAmount(allRepayAmount);
		
		if (sumAmount.compareTo(allRepayAmount) <= 0) {
			cashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
			cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
        }else if (YesNoStatus.YES.getCode().equals(isBalance)){//线下还款平账
			cashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
			cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
		}
	}
    
	private long changBorrowRepaymentStatus(String outTradeNo, String status, Long rid) {
        AfRepaymentBorrowCashDo repayment = new AfRepaymentBorrowCashDo();
        repayment.setStatus(status);
        repayment.setTradeNo(outTradeNo);
        repayment.setRid(rid);
        return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(repayment);
    }
	private long changOrderRepaymentStatus(String outTradeNo, String status, Long rid) {
        AfBorrowLegalOrderRepaymentDo repayment = new AfBorrowLegalOrderRepaymentDo();
        repayment.setStatus(status);
        repayment.setTradeNoUps(outTradeNo);
        repayment.setId(rid);
        return afBorrowLegalOrderRepaymentDao.updateBorrowLegalOrderRepayment(repayment);
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


	private AfRepaymentBorrowCashDo buildRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, String repayNo, Date gmtCreate,
												   BigDecimal actualAmountForBorrow, Long userCouponId, BigDecimal couponAmountForBorrow, BigDecimal rebateAmountForBorrow,
												   Long borrowId, Long cardId, String payTradeNo, String name, Long userId,String repayType,String cardNo) {
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
			if (cardNo == null){
				cardNo = "";
			}
			repay.setCardNumber(cardNo);
			if ("alipay".equals(repayType)){
				repay.setCardName("支付宝");
			}else if ("bank".equals(repayType)){
				repay.setCardName("银行卡");
			}else {
				repay.setCardName("线下还款");
			}
//      repay.setCardNumber("");
//      repay.setCardName(Constants.DEFAULT_OFFLINE_PAY_NAME);
		} else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			repay.setCardNumber(bank.getCardNumber());
			repay.setCardName(bank.getBankName());
		}
		return repay;
	}

	private AfBorrowLegalOrderRepaymentDo buildOrderRepayment(RepayBo bo, BigDecimal actualAmountForOrder, Long userCouponId,
															  BigDecimal couponAmountForOrder, BigDecimal rebateAmountForOrder,BigDecimal repayAmount,String repayType,String payTradeNo,String cardNo) {
		AfBorrowLegalOrderRepaymentDo repayment = new AfBorrowLegalOrderRepaymentDo();

		repayment.setUserId(bo.userId);
		repayment.setBorrowLegalOrderCashId(bo.borrowOrderCashId);
		repayment.setRepayAmount(repayAmount);
		repayment.setActualAmount(actualAmountForOrder);
		repayment.setName(bo.name);
		repayment.setTradeNo(bo.tradeNo);
		repayment.setUserCouponId(userCouponId);
		repayment.setCouponAmount(couponAmountForOrder);
		repayment.setRebateAmount(rebateAmountForOrder);
		repayment.setStatus(AfBorrowLegalRepaymentStatus.APPLY.getCode());
		if ("alipay".equals(repayType)){
			repayment.setTradeNoZfb(payTradeNo);
		}else if ("bank".equals(repayType)){
			repayment.setTradeNoUps(payTradeNo);
		}
		if (bo.cardId == -2) {
			repayment.setCardNo("");
			repayment.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		} else if (bo.cardId == -1) {
			repayment.setCardNo("");
			repayment.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		} else if (bo.cardId == -3) {
			repayment.setCardNo("");
			repayment.setCardName(Constants.DEFAULT_ZFB_PAY_NAME);
		} else if (bo.cardId == -4) {
			if (cardNo == null){
				cardNo = "";
			}
			repayment.setCardNo(cardNo);
			if ("alipay".equals(repayType)){
				repayment.setCardName("支付宝");
			}else if ("bank".equals(repayType)){
				repayment.setCardName("银行卡");
			}else {
				repayment.setCardName("线下还款");
			}
		} else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(bo.cardId);
			repayment.setCardNo(bank.getCardNumber());
			repayment.setCardName(bank.getBankName());
		}

		Date now = new Date();
		repayment.setGmtCreate(now);
		repayment.setGmtModified(now);
		repayment.setBorrowId(bo.borrowId);

		return repayment;
	}
	
	
	public static class RepayBo{
		public Long userId;
		
		/* request字段 */
		public BigDecimal repaymentAmount = BigDecimal.ZERO;
		public BigDecimal actualAmount = BigDecimal.ZERO; //可选字段
		public BigDecimal rebateAmount = BigDecimal.ZERO; //可选字段
		public String payPwd;			//可选字段
		public Long cardId;
		public Long couponId;			//可选字段
		public Long borrowId;			//可选字段
		public Long borrowOrderId; 		//可选字段
		public String from;
		public String isBalance;
		/* request字段 */
		
		/* biz 业务处理字段 */
		public AfBorrowLegalOrderRepaymentDo orderRepaymentDo;
		public AfRepaymentBorrowCashDo borrowRepaymentDo;
		public AfBorrowCashDo cashDo;
		public AfBorrowLegalOrderCashDo orderCashDo;
		public AfUserCouponDto userCouponDto; //可选字段
		public AfUserAccountDo userDo;
		public String remoteIp;
		public String name;
		public Long borrowOrderCashId;
		/* biz 业务处理字段 */
		
		/* Response字段 */
		public String cardName;		//交易卡名称
		public String cardNo;		//交易卡号
		public String outTradeNo; 	//自己放交易流水号
		public String tradeNo;		//我方交易流水号
		public String repayType;    //交易类型
		/* Response字段 */
		
		/* 错误码区域 */
		public Exception e;
		
	}
	
	public static class RepayDealBo {
		BigDecimal curRepayAmoutStub; 	//当前还款额变化句柄
		BigDecimal curRebateAmount; 	//当前还款使用的账户余额
		BigDecimal curSumRebateAmount = BigDecimal.ZERO;//当前还款使用的账户余额总额
		Long curUserCouponId;			//当前还款使用的还款优惠卷id
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
		
		AfBorrowCashDo cashDo;							//借款
		AfBorrowLegalOrderCashDo orderCashDo;			//订单借款
		long overdueDay = 0;								//对应借款的逾期天数
		String borrowNo;								//借款流水号
    	String refId = "";								//还款的id串
    	Long userId ;									//目标用户id
    	int renewalNum;                            //续借次数
	}
}
