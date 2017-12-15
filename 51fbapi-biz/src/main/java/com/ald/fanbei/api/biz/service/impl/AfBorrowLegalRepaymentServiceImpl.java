package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
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
import com.ald.fanbei.api.biz.service.AfBorrowLegalRepaymentService;
import com.ald.fanbei.api.biz.service.AfResourceService;
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
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepayFromEnum;
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepaymentStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
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

/**
 * 借款合规订单的还款service
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowLegalRepaymentService")
public class AfBorrowLegalRepaymentServiceImpl extends ParentServiceImpl<AfBorrowLegalOrderRepaymentDo, Long> implements AfBorrowLegalRepaymentService {

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

	
	/**
	 * 新版还钱函
	 * 参考{@link com.ald.fanbei.api.biz.service.impl.AfRepaymentBorrowCashServiceImpl}.createRepayment()
	 * 
	 * @param repaymentAmount
	 * @param actualAmount
	 * @param coupon
	 * @param rebateAmount
	 * @param borrow
	 * @param cardId
	 * @param userId
	 * @param clientIp
	 * @param afUserAccountDo
	 * @return
	 */
	public void repay(RepayBo bo) {
		Date now = new Date();
		String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
		if(StringUtil.equals("sysJob",bo.remoteIp)){
			name = "代扣付款";
		}
		
		String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now);
		bo.tradeNo = tradeNo;
		bo.name = name;
		
		AfRepaymentBorrowCashDo repayment = null;
		AfBorrowLegalOrderRepaymentDo legalOrderRepayment = null;
		if(AfBorrowLegalRepayFromEnum.INDEX.name().equalsIgnoreCase(bo.from)) {
			AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(bo.borrowId);
			bo.borrowOrderId = orderCashDo.getBorrowLegalOrderId();
			
			BigDecimal orderSumCash = orderCashDo.getAmount().add(orderCashDo.getOverdueAmount()).add(orderCashDo.getInterestAmount()).add(orderCashDo.getPoundageAmount()).add(orderCashDo.getOverdueAmount());
			BigDecimal orderRemainCash = orderSumCash.subtract(orderCashDo.getRepaidAmount()); //剩余应还金额
			
			BigDecimal orderBorrowCash = bo.repaymentAmount.subtract(orderRemainCash);
			if(orderBorrowCash.compareTo(BigDecimal.ZERO) > 0) { //还款额大于订单应还总额，拆分还款到借款中
				repayment = buildRepayment(BigDecimal.ZERO, orderBorrowCash, tradeNo, now, orderBorrowCash, bo.userCouponDto, bo.rebateAmount, bo.borrowId, bo.cardId, tradeNo, name, bo.userId);
				afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
				if(!AfBorrowLegalOrderCashStatus.FINISHED.getCode().equals(orderCashDo.getStatus())) {
					legalOrderRepayment = buildOrderRepayment(bo, orderRemainCash, name);
					afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(legalOrderRepayment);
				}
			} else { //还款全部进入订单欠款中
				legalOrderRepayment = buildOrderRepayment(bo, bo.repaymentAmount, name);
				afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(legalOrderRepayment);
			}
		}
		
		else if (AfBorrowLegalRepayFromEnum.BORROW.name().equalsIgnoreCase(bo.from)) {
			repayment = buildRepayment(BigDecimal.ZERO, bo.repaymentAmount, tradeNo, now, bo.actualAmount, bo.userCouponDto, bo.rebateAmount, bo.borrowId, bo.cardId, tradeNo, name, bo.userId);
			afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
		}
		
		else if(AfBorrowLegalRepayFromEnum.ORDER.name().equalsIgnoreCase(bo.from)) {
			legalOrderRepayment = buildOrderRepayment(bo, bo.repaymentAmount, name);
			afBorrowLegalOrderRepaymentDao.addBorrowLegalOrderRepayment(legalOrderRepayment);
		}
		
		logger.info("repay.add repayment finish,tradeNo="+tradeNo+",borrowRepayment="+ JSON.toJSONString(repayment) + ",legalOrderRepayment="+ JSON.toJSONString(legalOrderRepayment));
		
		doRepay(bo, repayment, legalOrderRepayment);
		
	}
	private void doRepay(RepayBo bo, AfRepaymentBorrowCashDo repayment, AfBorrowLegalOrderRepaymentDo legalOrderRepayment) {
		if (bo.cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(bo.cardId);
			UpsCollectRespBo respBo = upsUtil.collect(bo.tradeNo, bo.actualAmount, bo.userId.toString(), 
						bo.userDo.getRealName(), bank.getMobile(), bank.getBankCode(),
						bank.getCardNumber(), bo.userDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, bo.name, "02", PayOrderSource.REPAY_CASH_LEGAL.getCode());
			
			logger.info("doRepay,ups respBo="+JSON.toJSONString(respBo));
			if(repayment != null) {
				changBorrowRepaymentStatus(respBo.getTradeNo(), AfBorrowCashRepmentStatus.PROCESS.getCode(), repayment.getRid());
			}
			if(legalOrderRepayment != null) {
				changOrderRepaymentStatus(respBo.getTradeNo(), AfBorrowLegalRepaymentStatus.PROCESS.getCode(), legalOrderRepayment.getId());
			}
			if (!respBo.isSuccess()) {
				if(StringUtil.isNotBlank(respBo.getRespDesc())){
					String addMsg = "";
					try{
						if(bank!=null){
							String bankName = bank.getBankName();
							String cardNum = bank.getCardNumber();
							if(StringUtil.isNotBlank(bankName)&&StringUtil.isNotBlank(cardNum)){
								if(cardNum.length()>4){
									cardNum = cardNum.substring(cardNum.length()- 4,cardNum.length());
									addMsg = "{"+ bankName + cardNum + "}";
								}
							}
						}
					}catch (Exception e){
						logger.error("BorrowCash sendMessage but addMsg error for:"+e);
					}
					dealRepaymentFail(bo.tradeNo, "", true, addMsg+StringUtil.processRepayFailThirdMsg(respBo.getRespDesc()));
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
	
	
	/**
	 * @param tradeNo 我方交易流水
	 * @param outTradeNo 资金方交易流水
	 * @return
	 */
    public void dealRepaymentSucess(String tradeNo, String outTradeNo) {
    	try {
    		lock(tradeNo);
    		
    		final AfRepaymentBorrowCashDo repaymentDo = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(tradeNo);
            final AfBorrowLegalOrderRepaymentDo orderRepaymentDo = afBorrowLegalOrderRepaymentDao.getBorrowLegalOrderRepaymentByPayTradeNo(tradeNo);
            logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo) + ", orderRepayment=" + JSON.toJSONString(orderRepaymentDo));
            
            preCheck(repaymentDo, orderRepaymentDo, tradeNo);
            
            final RepayDealBo repayDealBo = new RepayDealBo();
            repayDealBo.curTradeNo = tradeNo;
            repayDealBo.curOutTradeNo = outTradeNo;
            
            long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus status) {
                    try {
                		dealOrderRepay(repayDealBo, orderRepaymentDo);
                		dealBorrowRepay(repayDealBo, repaymentDo);
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
    
    private void preCheck(AfRepaymentBorrowCashDo repaymentDo, AfBorrowLegalOrderRepaymentDo orderRepaymentDo, String tradeNo) {
    	// 检查交易流水 对应记录数据库中是否已经处理
        if ((repaymentDo != null && YesNoStatus.YES.getCode().equals(repaymentDo.getStatus()) ) 
        		|| (orderRepaymentDo != null && YesNoStatus.YES.getCode().equals(orderRepaymentDo.getStatus()) )) {
        	throw new FanbeiException("preCheck,repayment has been dealed!");
        }
        
        /* start 易宝支付侵入逻辑 */
        AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(tradeNo);
        if (afYibaoOrderDo != null) {
            if (afYibaoOrderDo.getStatus().intValue() == 1) {
            	throw new FanbeiException("preCheck,afYibaoOrderDo.status == 1,break!");
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
	private void dealOrderRepay(RepayDealBo repayDealBo, AfBorrowLegalOrderRepaymentDo orderRepaymentDo) {
		if(orderRepaymentDo == null) return;
		
		AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashDao.getById(orderRepaymentDo.getBorrowLegalOrderCashId());
		AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(orderCashDo.getBorrowId());
		
		repayDealBo.curRepayAmoutStub = orderRepaymentDo.getRepayAmount();
		repayDealBo.curRebateAmount = orderRepaymentDo.getRebateAmount();
		repayDealBo.curUserCouponId = orderRepaymentDo.getUserCouponId();
		repayDealBo.curSumRepayAmount = repayDealBo.curSumRepayAmount.add(orderRepaymentDo.getRepayAmount());
		repayDealBo.curCardNo = orderRepaymentDo.getCardNo();
		repayDealBo.curName = orderRepaymentDo.getName();
		
		repayDealBo.cashDo = cashDo;
		repayDealBo.orderCashDo = orderCashDo;
		repayDealBo.overdueDay = cashDo.getOverdueDay();
		repayDealBo.borrowNo = cashDo.getBorrowNo();
		repayDealBo.refId += orderCashDo.getRid();
		repayDealBo.userId = cashDo.getUserId();
		
		changOrderRepaymentStatus(repayDealBo.curOutTradeNo, AfBorrowLegalRepaymentStatus.YES.getCode(), orderRepaymentDo.getId());
        
        dealOrderRepayIfFinish(repayDealBo, orderRepaymentDo, orderCashDo);
        afBorrowLegalOrderCashDao.updateById(orderCashDo);
		
		repayDealBo.sumRepaidAmount = repayDealBo.sumRepaidAmount.add(orderCashDo.getRepaidAmount());
        repayDealBo.sumInterest = repayDealBo.sumInterest.add(orderCashDo.getInterestAmount());
        repayDealBo.sumPoundage = repayDealBo.sumPoundage.add(orderCashDo.getPoundageAmount());
        repayDealBo.sumOverdueAmount = repayDealBo.sumOverdueAmount.add(orderCashDo.getOverdueAmount());
        repayDealBo.sumIncome = repayDealBo.sumIncome.add(repayDealBo.sumPoundage).add(repayDealBo.sumOverdueAmount).add(repayDealBo.sumInterest);
	}
    
    /**
	 * 需在事务管理块中调用此函数!
	 * @param repayDealBo
	 * @param repaymentDo
	 */
	private void dealBorrowRepay(RepayDealBo repayDealBo, AfRepaymentBorrowCashDo repaymentDo) {
		if(repaymentDo == null) return;
		
		AfBorrowCashDo cashDo = afBorrowCashService.getBorrowCashByrid(repaymentDo.getBorrowId());
		AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(cashDo.getRid());//
		
		repayDealBo.curRepayAmoutStub = repaymentDo.getRepaymentAmount();
		repayDealBo.curRebateAmount = repaymentDo.getRebateAmount();
		repayDealBo.curUserCouponId = repaymentDo.getUserCouponId();
		repayDealBo.curSumRepayAmount = repayDealBo.curSumRepayAmount.add(repaymentDo.getRepaymentAmount());
		repayDealBo.curCardNo = repaymentDo.getCardNumber();
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
        dealBorrowRepayIfFinish(repayDealBo, repaymentDo, cashDo);
        afBorrowCashService.updateBorrowCash(cashDo);
        
        repayDealBo.sumRepaidAmount = repayDealBo.sumRepaidAmount.add(cashDo.getRepayAmount());
        repayDealBo.sumInterest = repayDealBo.sumInterest.add(cashDo.getRateAmount()).add(cashDo.getSumRate());
        repayDealBo.sumPoundage = repayDealBo.sumPoundage.add(cashDo.getPoundage()).add(cashDo.getSumRenewalPoundage());
        repayDealBo.sumOverdueAmount = repayDealBo.sumOverdueAmount.add(cashDo.getOverdueAmount()).add(cashDo.getSumOverdue());
        repayDealBo.sumIncome = repayDealBo.sumIncome.add(repayDealBo.sumPoundage).add(repayDealBo.sumOverdueAmount).add(repayDealBo.sumInterest);
	}
	
	/**
     * 处理优惠卷 和 账户余额
     * @param repayDealBo
     */
    private void dealCouponAndRebate(RepayDealBo repayDealBo) {
    	if(repayDealBo.curRebateAmount != null && repayDealBo.curRebateAmount.compareTo(BigDecimal.ZERO) > 0) {// 授权账户可用金额变更
    		AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(repayDealBo.userId);
            accountInfo.setUsedAmount(accountInfo.getUsedAmount().add(repayDealBo.curRebateAmount));
            accountInfo.setRebateAmount(accountInfo.getRebateAmount().subtract(repayDealBo.curRebateAmount));
            afUserAccountDao.updateOriginalUserAccount(accountInfo);
    	}
    	
    	if(repayDealBo.curUserCouponId != null && repayDealBo.curUserCouponId > 0) {
    		afUserCouponDao.updateUserCouponSatusUsedById(repayDealBo.curUserCouponId);// 优惠券设置已使用	
    	}
    }
    
    private void doAccountLog(RepayDealBo repayDealBo) {
    	AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAY_CASH_LEGAL, repayDealBo.curSumRepayAmount, repayDealBo.userId, repayDealBo.refId);
        afUserAccountLogDao.addUserAccountLog(accountLog);//增加日志
    }
    
    private void notifyUserBySms(RepayDealBo repayDealBo) {
        try {
            AfUserDo afUserDo = afUserService.getUserById(repayDealBo.userId);
            if(repayDealBo.curName.equals("代扣付款")){
                String content= "代扣还款"+repayDealBo.sumAmount+"元，该笔借款已结清。";
                smsUtil.sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), content);
            }else{
                smsUtil.sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), repayDealBo.curSumRepayAmount.toString(), repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount).toString());
            }
        } catch (Exception e) {
            logger.error("Sms notify user error, userId:" + repayDealBo.userId + ",nowRepayAmount:" + repayDealBo.curSumRepayAmount + ",notRepayMoney" + repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount), e);
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
            obj.put("amount", repayDealBo.sumAmount);
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
            		AfBorrowCashStatus.finsh.getCode().equals(repayDealBo.orderCashDo.getStatus()) ) {
            	String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                int overdueCount = 0;
                if (StringUtil.equals("Y", repayDealBo.cashDo.getOverdueStatus())) {
                    overdueCount = 1;
                }
                riskUtil.raiseQuota(repayDealBo.userId.toString(), 
                			repayDealBo.borrowNo, "50", riskOrderNo, 
                			repayDealBo.sumAmount, 
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
	
	private void dealOrderRepayIfFinish(RepayDealBo repayDealBo, AfBorrowLegalOrderRepaymentDo orderRepaymentBo, AfBorrowLegalOrderCashDo orderCashDo) {
		BigDecimal allBorrowAmount = BigDecimalUtil.add(orderCashDo.getAmount(), 
														orderCashDo.getOverdueAmount(),
														orderCashDo.getPoundageAmount(),
														orderCashDo.getInterestAmount());
		repayDealBo.sumAmount = repayDealBo.sumAmount.add(allBorrowAmount);
		BigDecimal allRepayAmount = orderCashDo.getRepaidAmount().add(orderRepaymentBo.getRepayAmount());
		Date now = new Date();
		orderCashDo.setRepaidAmount(allRepayAmount);
		orderCashDo.setGmtLastRepayment(now);
		
		if (allBorrowAmount.compareTo(allRepayAmount) == 0) {
			orderCashDo.setStatus(AfBorrowLegalOrderCashStatus.FINISHED.getCode());
			orderCashDo.setGmtFinish(now);
        }else {
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
		
        if (repayAmount.compareTo(afBorrowCashDo.getRateAmount()) > 0) {
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
		BigDecimal allBorrowAmount = BigDecimalUtil.add(cashDo.getAmount(), 
					cashDo.getOverdueAmount(), cashDo.getSumOverdue(),
					cashDo.getRateAmount(), cashDo.getSumRate(),
					cashDo.getPoundage(), cashDo.getSumRenewalPoundage());
		repayDealBo.sumAmount = repayDealBo.sumAmount.add(allBorrowAmount);
		BigDecimal allRepayAmount = cashDo.getRepayAmount().add(repaymentDo.getRepaymentAmount());
		cashDo.setRepayAmount(allRepayAmount);
		
		if (allBorrowAmount.compareTo(allRepayAmount) == 0) {
			cashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
			cashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
        }
	}
    
	
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
        
		if(isNeedMsgNotice){
			int errorTimes = 0;//用户信息及当日还款失败次数校验
			String payType = repaymentDo.getName();
			if(StringUtil.isNotBlank(payType)&&payType.indexOf("代扣")>-1){ //如果是代扣，不校验次数
				errorTimes = 0;
			}else{
				errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(repaymentDo.getUserId());
			}
			AfUserDo afUserDo = afUserService.getUserById(repaymentDo.getUserId());
			smsUtil.sendRepaymentBorrowCashFail(afUserDo.getMobile(),errorMsg,errorTimes);//还款失败短信通知
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
	
	
	private AfRepaymentBorrowCashDo buildRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, String repayNo, Date gmtCreate, BigDecimal actualAmount,
            AfUserCouponDto coupon, BigDecimal rebateAmount, Long borrowId, Long cardId, String payTradeNo, String name, Long userId) {
		AfRepaymentBorrowCashDo repay = new AfRepaymentBorrowCashDo();
		repay.setActualAmount(actualAmount);
		repay.setBorrowId(borrowId);
		repay.setJfbAmount(jfbAmount);
		repay.setPayTradeNo(payTradeNo);
		repay.setRebateAmount(rebateAmount);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(AfBorrowCashRepmentStatus.APPLY.getCode());
		if (null != coupon) {
			repay.setUserCouponId(coupon.getRid());
			repay.setCouponAmount(coupon.getAmount());
		}
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

		} else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			repay.setCardNumber(bank.getCardNumber());
			repay.setCardName(bank.getBankName());
		}
		return repay;
	}
	
	private AfBorrowLegalOrderRepaymentDo buildOrderRepayment(RepayBo bo, BigDecimal repayAmount, String name) {
		AfBorrowLegalOrderRepaymentDo repayment = new AfBorrowLegalOrderRepaymentDo();
		
		repayment.setUserId(bo.userId);
		repayment.setBorrowLegalOrderCashId(bo.borrowOrderId);
		repayment.setRepayAmount(repayAmount);
		repayment.setName(name);
		repayment.setTradeNo(bo.tradeNo);
		if (null != bo.userCouponDto) {
			repayment.setUserCouponId(bo.couponId);
			repayment.setCouponAmount(bo.userCouponDto.getAmount());
		}
		repayment.setRebateAmount(bo.rebateAmount);
		repayment.setStatus(AfBorrowLegalRepaymentStatus.APPLY.getCode());
		
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
		} else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(bo.cardId);
			repayment.setCardNo(bank.getCardNumber());
			repayment.setCardName(bank.getBankName());
		}
		
		Date now = new Date();
		repayment.setGmtCreate(now);
		repayment.setGmtModified(now);
		
		return repayment;
	}
	
	
	public static class RepayBo{
		public Long userId;
		
		/* request字段 */
		public BigDecimal repaymentAmount;
		public BigDecimal actualAmount;
		public BigDecimal rebateAmount; //可选字段
		public String payPwd;			//可选字段
		public Long cardId;
		public Long couponId;		//可选字段
		public Long borrowId;			//可选字段
		public Long borrowOrderId; 		//可选字段
		public String from;
		/* request字段 */
		
		/* biz 业务处理字段 */
		public AfBorrowCashDo cashDo;
		public AfBorrowLegalOrderCashDo orderCashDo;
		public AfUserCouponDto userCouponDto; //可选字段
		public AfUserAccountDo userDo;
		public String remoteIp;
		public String name;
		/* biz 业务处理字段 */
		
		/* Response字段 */
		public String cardName;		//交易卡名称
		public String cardNo;		//交易卡号
		public String outTradeNo; 	//自己放交易流水号
		public String tradeNo;		//我方交易流水号
		/* Response字段 */
		
		/* 错误码区域 */
		public Exception e;
		
	}
	
	public static class RepayDealBo {
		BigDecimal curRepayAmoutStub; 	//当前还款额变化句柄
		BigDecimal curRebateAmount; 	//当前还款使用的账户余额
		Long curUserCouponId;			//当前还款使用的还款优惠卷id
		BigDecimal curSumRepayAmount = BigDecimal.ZERO;	//当前还款总额(借款还款+订单还款)
		String curCardNo;				//当前还款卡号
		String curCardName;				//当前还款卡别名
		String curName;					//当前还款名称，用来识别自动代付还款
		String curTradeNo;
		String curOutTradeNo;
		
		BigDecimal sumRepaidAmount = BigDecimal.ZERO;	//对应借款的还款总额
		BigDecimal sumAmount = BigDecimal.ZERO;			//对应借款总额
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
	}
	
	@Override
	public BaseDao<AfBorrowLegalOrderRepaymentDo, Long> getDao() {
		return afBorrowLegalOrderRepaymentDao;
	}

}
