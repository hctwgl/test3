package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalRepaymentV2Service;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
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

/**
 * 借款合规订单的还款service
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowLegalRepaymentService")
public class AfBorrowLegalRepaymentV2ServiceImpl extends ParentServiceImpl<AfRepaymentBorrowCashDo, Long>  implements AfBorrowLegalRepaymentV2Service {

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
    private AfTradeCodeInfoService afTradeCodeInfoService;
	
	/**
	 * 新版还钱函
	 * 参考{@link com.ald.fanbei.api.biz.service.impl.AfRepaymentBorrowCashServiceImpl}.createRepayment()
	 */
	@Override
	public void repay(RepayBo bo) {
		Date now = new Date();
		String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
		if(StringUtil.equals("sysJob",bo.remoteIp)){
			name = Constants.BORROW_REPAYMENT_NAME_AUTO;
		}
		
		String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now);
		bo.tradeNo = tradeNo;
		bo.name = name;
		
		generateRepayRecords(bo);
		
		doRepay(bo, bo.borrowRepaymentDo);
		
	}
	
	/**
     * 线下还款
     * 
     * @param restAmount 
     */
	@Override
	public void offlineRepay(AfBorrowCashDo cashDo, String borrowNo, 
				String repayType, String repayTime, String repayAmount,
				String restAmount, String outTradeNo, String isBalance) {
		RepayBo bo = new RepayBo();
		bo.userId = cashDo.getUserId();
		bo.userDo = afUserAccountDao.getUserAccountInfoByUserId(bo.userId);
		
		bo.cardId = (long) -4;
		bo.repaymentAmount = NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO);
		bo.actualAmount =  bo.repaymentAmount;
		bo.borrowId = cashDo.getRid();
		
		bo.tradeNo = generatorClusterNo.getOfflineRepaymentBorrowCashNo(new Date());
		bo.name = Constants.BORROW_REPAYMENT_NAME_OFFLINE;
		bo.outTradeNo = outTradeNo;
		
		generateRepayRecords(bo);
		
		dealRepaymentSucess(bo.tradeNo, "", bo.borrowRepaymentDo);
		
	}
	
	/**
	 * 还款成功后调用
	 * @param tradeNo 我方交易流水
	 * @param outTradeNo 资金方交易流水
	 * @return
	 */
	@Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfRepaymentBorrowCashDo repaymentDo) {
    	try {
    		lock(tradeNo);
    		
            logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo) );
            
            preCheck(repaymentDo, tradeNo);
            
            final RepayDealBo repayDealBo = new RepayDealBo();
            repayDealBo.curTradeNo = tradeNo;
            repayDealBo.curOutTradeNo = outTradeNo;
            
            long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus status) {
                    try {
                		dealBorrowRepay(repayDealBo, repaymentDo);
                		
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
            	notifyUserBySms(repayDealBo);
            	nofityRisk(repayDealBo);
            }
    		
    	}finally {
    		unLock(tradeNo);
		}
    }
	@Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo) {
		final AfRepaymentBorrowCashDo repaymentDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(tradeNo);
        dealRepaymentSucess(tradeNo, outTradeNo, repaymentDo);
    }
    
    /**
     * 还款失败后调用
     */
    @Override
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice,String errorMsg) {
		final AfRepaymentBorrowCashDo repaymentDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(tradeNo);
        logger.info("dealRepaymentFail process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo 
        		+ ",isNeedMsgNotice=" + isNeedMsgNotice + ",errorMsg=" + errorMsg 
        		+ ",borrowRepayment=" + JSON.toJSONString(repaymentDo));
        
        if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) )) { // 检查交易流水 对应记录数据库中是否已经处理
            return;
        }
        
        if(repaymentDo != null) {
			changBorrowRepaymentStatus(outTradeNo, AfBorrowCashRepmentStatus.NO.getCode(), repaymentDo.getRid());
		}
        
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
    
    @Override
	public BigDecimal calculateRestAmount(AfBorrowCashDo cashDo) {
		BigDecimal restAmount = BigDecimal.ZERO;
		if(cashDo != null) {
			restAmount = BigDecimalUtil.add(restAmount, cashDo.getAmount(),
					cashDo.getOverdueAmount(), cashDo.getSumOverdue(), 
					cashDo.getRateAmount(),cashDo.getSumRate(),
					cashDo.getPoundage(),cashDo.getSumRenewalPoundage())
					.subtract(cashDo.getRepayAmount());
		}
		return restAmount;
	}
    
    private void generateRepayRecords(RepayBo bo) {
    	Date now = new Date();
    	String tradeNo = bo.tradeNo;
    	String name = bo.name;
		
    	AfRepaymentBorrowCashDo borrowRepaymentDo = buildRepayment(BigDecimal.ZERO, bo.repaymentAmount, tradeNo, now, bo.actualAmount, bo.couponId, 
				bo.userCouponDto != null?bo.userCouponDto.getAmount():null, bo.rebateAmount, bo.borrowId, bo.cardId, tradeNo, name, bo.userId);
		afRepaymentBorrowCashDao.addRepaymentBorrowCash(borrowRepaymentDo);
		
		bo.borrowRepaymentDo = borrowRepaymentDo;
		
		logger.info("Repay.add repayment finish,name="+ name +"tradeNo="+tradeNo+",borrowRepayment="+ JSON.toJSONString(borrowRepaymentDo));
    }
    
    private void doRepay(RepayBo bo, AfRepaymentBorrowCashDo repayment) {
		if (bo.cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(bo.cardId);
			UpsCollectRespBo respBo = upsUtil.collect(bo.tradeNo, bo.actualAmount, bo.userId.toString(), 
						bo.userDo.getRealName(), bank.getMobile(), bank.getBankCode(),
						bank.getCardNumber(), bo.userDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, bo.name, "02", PayOrderSource.REPAY_CASH_LEGAL.getCode());
			
			logger.info("doRepay,ups respBo="+JSON.toJSONString(respBo));
			if(repayment != null) {
				changBorrowRepaymentStatus(respBo.getTradeNo(), AfBorrowCashRepmentStatus.PROCESS.getCode(), repayment.getRid());
			}
			if (!respBo.isSuccess()) {
				if(StringUtil.isNotBlank(respBo.getRespCode())){
					dealRepaymentFail(bo.tradeNo, "", true, afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode()));
				}else{
					dealRepaymentFail(bo.tradeNo, "", false, "");
				}
				throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			
			bo.outTradeNo = respBo.getTradeNo();
		} else if (bo.cardId == -2) {// 余额支付
			dealRepaymentSucess(bo.tradeNo, "");
		}
		
	}
    
    private void preCheck(AfRepaymentBorrowCashDo repaymentDo, String tradeNo) {
    	// 检查交易流水 对应记录数据库中是否已经处理
        if ( repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) ) {
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
	 * @param repaymentDo
	 */
	private void dealBorrowRepay(RepayDealBo repayDealBo, AfRepaymentBorrowCashDo repaymentDo) {
		if(repaymentDo == null) return;
		
		AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(repaymentDo.getBorrowId());
		
		repayDealBo.curRepayAmoutStub = repaymentDo.getRepaymentAmount();
		repayDealBo.curRebateAmount = repaymentDo.getRebateAmount();
		repayDealBo.curSumRebateAmount = repayDealBo.curSumRebateAmount.add(repaymentDo.getRebateAmount());
		repayDealBo.curUserCouponId = repaymentDo.getUserCouponId();
		repayDealBo.curSumRepayAmount = repayDealBo.curSumRepayAmount.add(repaymentDo.getRepaymentAmount());
		repayDealBo.curCardNo = repaymentDo.getCardNumber();
		repayDealBo.curName = repaymentDo.getName();
		
		repayDealBo.cashDo = cashDo;
		repayDealBo.overdueDay = cashDo.getOverdueDay();
		repayDealBo.borrowNo = cashDo.getBorrowNo();
		repayDealBo.refId += repaymentDo.getBorrowId();
		repayDealBo.userId = cashDo.getUserId();
		
        dealBorrowRepayOverdue(repayDealBo, cashDo);//逾期费
        dealBorrowRepayPoundage(repayDealBo, cashDo);//手续费
        dealBorrowRepayInterest(repayDealBo, cashDo);//利息
        
        changBorrowRepaymentStatus(repayDealBo.curOutTradeNo, AfBorrowCashRepmentStatus.YES.getCode(), repaymentDo.getRid());
        
        cashDo.setSumRebate(BigDecimalUtil.add(cashDo.getSumRebate(), repaymentDo.getRebateAmount()));//余额使用
        dealBorrowRepayIfFinish(repayDealBo, repaymentDo, cashDo);
        afBorrowCashService.updateBorrowCash(cashDo);
	}
	
	private void dealSum(RepayDealBo repayDealBo){
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
     * @param repayDealBo
     */
    private void dealCouponAndRebate(RepayDealBo repayDealBo) {
    	AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(repayDealBo.userId);
    	if (AfBorrowCashStatus.finsh.getCode().equals(repayDealBo.cashDo.getStatus())) {
    		accountInfo.setUsedAmount(accountInfo.getUsedAmount().subtract(repayDealBo.cashDo.getAmount()));
    	}
    	
    	if(repayDealBo.curSumRebateAmount != null && repayDealBo.curSumRebateAmount.compareTo(BigDecimal.ZERO) > 0) {// 授权账户可用金额变更
            accountInfo.setRebateAmount(accountInfo.getRebateAmount().subtract(repayDealBo.curSumRebateAmount));
    	}
    	afUserAccountDao.updateOriginalUserAccount(accountInfo);
    	
    	if(repayDealBo.curUserCouponId != null && repayDealBo.curUserCouponId > 0) {
    		afUserCouponDao.updateUserCouponSatusUsedById(repayDealBo.curUserCouponId);// 优惠券设置已使用
    	}
    }
    
    private void doAccountLog(RepayDealBo repayDealBo) {
    	AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAY_CASH_LEGAL, repayDealBo.curSumRepayAmount, repayDealBo.userId, repayDealBo.refId);
        afUserAccountLogDao.addUserAccountLog(accountLog);//增加日志
    }
    
    private void notifyUserBySms(RepayDealBo repayDealBo) {
    	logger.info("notifyUserBySms info begin,sumAmount="+repayDealBo.sumAmount+",curSumRepayAmount="+repayDealBo.curSumRepayAmount+",sumRepaidAmount="+repayDealBo.sumRepaidAmount);
        try {
            AfUserDo afUserDo = afUserService.getUserById(repayDealBo.userId);
            if(repayDealBo.curName.equals("代扣付款")){
                sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), repayDealBo.sumAmount);
            }else{
            	sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), repayDealBo.curSumRepayAmount, repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount));
            }
        } catch (Exception e) {
            logger.error("Sms notify user error, userId:" + repayDealBo.userId + ",nowRepayAmount:" + repayDealBo.curSumRepayAmount + ",notRepayMoney" + repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount), e);
        }
    }
    
    /**
     * 代扣现金贷还款成功短信发送
     * @param mobile
     * @param nowRepayAmountStr
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
     * @param nowRepayAmountStr
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
            if ( AfBorrowCashStatus.finsh.getCode().equals(repayDealBo.cashDo.getStatus()) ) {
            	String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                int overdueCount = 0;
                if (StringUtil.equals("Y", repayDealBo.cashDo.getOverdueStatus())) {
                    overdueCount = 1;
                }
                riskUtil.raiseQuota(repayDealBo.userId.toString(), 
                			repayDealBo.borrowNo, "50", riskOrderNo, 
                			repayDealBo.sumBorrowAmount,
                			repayDealBo.sumIncome, 
                			repayDealBo.overdueDay, overdueCount);
            }
        } catch (Exception e) {
            logger.error("notifyRisk.raiseQuota error！", e);
        }
        /**------------------------------------fmai风控提额end--------------------------------------------------*/

        //会对逾期的借款还款，向催收平台同步还款信息
        if (DateUtil.compareDate(new Date(), repayDealBo.cashDo.getGmtPlanRepayment()) ){
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
                		repayDealBo.sumInterest);
                logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
            } catch (Exception e) {
                logger.error("向催收平台同步还款信息失败", e);
            }
        }else{
			logger.info("collection consumerRepayment not push,borrowCashId="+repayDealBo.cashDo.getRid());
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
	private void dealBorrowRepayIfFinish(RepayDealBo repayDealBo, AfRepaymentBorrowCashDo repaymentDo, AfBorrowCashDo cashDo) {
		BigDecimal sumAmount = BigDecimalUtil.add(cashDo.getAmount(), 
					cashDo.getOverdueAmount(), cashDo.getSumOverdue(),
					cashDo.getRateAmount(), cashDo.getSumRate(),
					cashDo.getPoundage(), cashDo.getSumRenewalPoundage());
		BigDecimal allRepayAmount = cashDo.getRepayAmount().add(repaymentDo.getRepaymentAmount());
		cashDo.setRepayAmount(allRepayAmount);
		
		if (sumAmount.compareTo(allRepayAmount) == 0) {
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
	
	
	private AfRepaymentBorrowCashDo buildRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, String repayNo, Date gmtCreate, 
			BigDecimal actualAmountForBorrow, Long userCouponId, BigDecimal couponAmountForBorrow, BigDecimal rebateAmountForBorrow, 
			Long borrowId, Long cardId, String payTradeNo, String name, Long userId) {
		AfRepaymentBorrowCashDo repay = new AfRepaymentBorrowCashDo();
		repay.setActualAmount(actualAmountForBorrow);
		repay.setBorrowId(borrowId);
		repay.setJfbAmount(jfbAmount);
		repay.setPayTradeNo(payTradeNo);
		repay.setRebateAmount(rebateAmountForBorrow);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(AfBorrowCashRepmentStatus.APPLY.getCode());
		repay.setUserCouponId(userCouponId);
		repay.setCouponAmount(couponAmountForBorrow);
		repay.setName(name);
		repay.setUserId(userId);
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
			repay.setCardNumber("");
			repay.setCardName(Constants.DEFAULT_OFFLINE_PAY_NAME);
		} else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			repay.setCardNumber(bank.getCardNumber());
			repay.setCardName(bank.getBankName());
		}
		return repay;
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
		/* request字段 */
		
		/* biz 业务处理字段 */
		public AfRepaymentBorrowCashDo borrowRepaymentDo;
		public AfBorrowCashDo cashDo;
		public AfUserCouponDto userCouponDto; //可选字段
		public AfUserAccountDo userDo;
		public String remoteIp;
		public String name;
		/* biz 业务处理字段 */
		
		/* Response字段 */
		public String cardName;		//交易卡名称
		public String cardNo;		//交易卡号
		public String outTradeNo; 	//资金方放交易流水号
		public String tradeNo;		//我方交易流水号
		/* Response字段 */
		
		/* 错误码区域 */
		public Exception e;
		
	}
	
	public static class RepayDealBo {
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
		
		BigDecimal sumRepaidAmount = BigDecimal.ZERO;	//对应借款的还款总额
		BigDecimal sumAmount = BigDecimal.ZERO;			//对应借款总额（包含所有费用）
		BigDecimal sumBorrowAmount = BigDecimal.ZERO;	//对应借款总额（不包含其他费用）
		BigDecimal sumInterest = BigDecimal.ZERO;		//对应借款的利息总额
		BigDecimal sumPoundage = BigDecimal.ZERO;		//对应借款的手续费总额
		BigDecimal sumOverdueAmount = BigDecimal.ZERO;	//对应借款的逾期费总额
		BigDecimal sumIncome = BigDecimal.ZERO;			//对应借款我司产生的总收入
		
		AfBorrowCashDo cashDo;							//借款
		long overdueDay = 0;							//对应借款的逾期天数
		String borrowNo;								//借款流水号
    	String refId = "";								//还款的id串
    	Long userId ;									//目标用户id
	}

	@Override
	public BaseDao<AfRepaymentBorrowCashDo, Long> getDao() {
		return null;
	}

}
