package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepayFromEnum;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
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
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
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
	 * 新版还钱函数 
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
		String typeName = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
		if(StringUtil.equals("sysJob",bo.remoteIp)){
			typeName = "代扣付款"; // TODO 自动代扣付款逻辑交集处理
		}
		
		String payTradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now);
		String finalTypeName = typeName;
		Long cardId = bo.cardId, userId = bo.userId;
		AfUserAccountDo userDo = bo.userDo;
		BigDecimal actualAmount = bo.actualAmount;
		
		//
		AfRepaymentBorrowCashDo repayment;
		AfBorrowLegalOrderRepaymentDo legalOrderRepayment;
		if(AfBorrowLegalRepayFromEnum.INDEX.name().equalsIgnoreCase(bo.from)) {
			AfBorrowLegalOrderCashDo orderCashDo = afBorrowLegalOrderCashDao.getById(bo.borrowOrderId);
			BigDecimal orderSumCash = orderCashDo.getAmount().add(orderCashDo.getOverdueAmount()).add(orderCashDo.getInterestAmount()).add(orderCashDo.getPoundageAmount()).add(orderCashDo.getOverdueAmount());
			BigDecimal orderRemainCash = orderSumCash.subtract(orderCashDo.getRepaidAmount()); //剩余应还金额
			
			BigDecimal borrowCash = bo.repaymentAmount.subtract(orderRemainCash);
			if(borrowCash.compareTo(BigDecimal.ZERO) > 0) { //还款额大于订单应还总额，拆分还款到借款中
				legalOrderRepayment = null; // buildRepayment(BigDecimal.ZERO, orderRemainCash, payTradeNo, now, actualAmount, bo.couponDto, bo.rebateAmount, bo.borrowId, cardId, payTradeNo, finalTypeName, userId);
				repayment = buildRepayment(BigDecimal.ZERO, borrowCash, payTradeNo, now, borrowCash, bo.couponDto, bo.rebateAmount, bo.borrowId, cardId, payTradeNo, finalTypeName, userId);
				// 判断优惠卷 和 余额  是否被订单还款消耗完
				
				
				afBorrowLegalOrderRepaymentDao.saveRecord(legalOrderRepayment);
				afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
				
				// TODO 同步 BorrrowLegalOrderCash和BorrowCash中状态
			} else { //还款全部进入订单欠款中
				legalOrderRepayment = buildOrderRepayment(BigDecimal.ZERO, orderRemainCash, payTradeNo, now, actualAmount, bo.coupon, bo.rebateAmount, bo.borrowId, cardId, payTradeNo, finalTypeName, userId);
				afBorrowLegalOrderRepaymentDao.saveRecord(legalOrderRepayment);
				// TODO 同步 BorrrowLegalOrderCash中状态
			}
		}else if (AfBorrowLegalRepayFromEnum.BORROW.name().equalsIgnoreCase(bo.from)) {
			
			repayment = buildRepayment(BigDecimal.ZERO, bo.repaymentAmount, payTradeNo, now, actualAmount, bo.couponDto, bo.rebateAmount, bo.borrowId, cardId, payTradeNo, finalTypeName, userId);
			afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
			logger.info("createRepayment addRepaymentBorrowCash finish,payTradeNo="+payTradeNo+",repaymentId="+(repayment!=null?repayment.getRid():0));
			
			if(cardId > 0){
				dealChangStatus(payTradeNo, "", AfBorrowCashRepmentStatus.PROCESS.getCode(), repayment.getRid());
			}
			
		}else if(AfBorrowLegalRepayFromEnum.ORDER.name().equalsIgnoreCase(bo.from)) {
			legalOrderRepayment = buildOrderRepayment(BigDecimal.ZERO, orderRemainCash, payTradeNo, now, actualAmount, bo.couponDto, bo.rebateAmount, bo.borrowId, cardId, payTradeNo, finalTypeName, userId);
			afBorrowLegalOrderRepaymentDao.saveRecord(legalOrderRepayment);
			// TODO 同步 BorrrowLegalOrderCash中状态
		}else {
			throw new FanbeiException(" TODO "); // TODO
		}
		//
		
		
		if (cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			UpsCollectRespBo respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", userDto.getRealName(), bank.getMobile(), bank.getBankCode(),
					bank.getCardNumber(), userDto.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, finalTypeName, "02", PayOrderSource.REPAY_CASH_LEGAL.getCode());
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
					dealRepaymentFail(payTradeNo, "",true,addMsg+StringUtil.processRepayFailThirdMsg(respBo.getRespDesc()));
				}else{
					dealRepaymentFail(payTradeNo, "",false,"");
				}
				throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			
			bo.outTradeNo = respBo.getOrderNo();
			bo.tradeNo = respBo.getTradeNo();
			bo.cardNo = Base64.encodeString(respBo.getCardNo());
		} else if (cardId == -2) {// 余额支付
			dealRepaymentSucess(payTradeNo, "");
		}
		
		bo.type = UserAccountLogType.REPAYMENTCASH.getCode();
		
	}
	
    public long dealRepaymentSucess(final String outTradeNo, final String tradeNo) {
        final String key = outTradeNo + "_success_repayCash";
        long count = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 30, TimeUnit.SECONDS);
        if (count != 1) {
            return -1;
        }

        final AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
        logger.info("dealRepaymentSucess process begin, repayment=" + repayment);
        if (repayment == null || YesNoStatus.YES.getCode().equals(repayment.getStatus())) {
            redisTemplate.delete(key);
            return 0l;
        }

        final AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(repayment.getBorrowId());

        AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(repayment.getUserId());
        String userCardNo = StringUtils.EMPTY;
        if (card != null) {
            userCardNo = card.getCardNumber();
        } else {
            userCardNo = System.currentTimeMillis() + StringUtils.EMPTY;
        }
        final String cardNo = userCardNo;
        long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                try {
                    AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
                    if (afYibaoOrderDo != null) {
                        if (afYibaoOrderDo.getStatus().intValue() == 1) {
                            return 0L;
                        } else {
                            afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 1);
                        }
                    }

                    BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());

                    AfBorrowCashDo bcashDo = new AfBorrowCashDo();
                    bcashDo.setRid(afBorrowCashDo.getRid());
                    bcashDo.setSumRenewalPoundage(afBorrowCashDo.getSumRenewalPoundage());
                    bcashDo.setRenewalNum(afBorrowCashDo.getRenewalNum());
                    //region 11-30新增

                    afBorrowCashDo.setSumRenewalPoundage(afBorrowCashDo.getSumRenewalPoundage());
                    afBorrowCashDo.setRenewalNum(afBorrowCashDo.getRenewalNum());

                    //end 11-30新增

                    BigDecimal nowRepayAmount = repayment.getRepaymentAmount();
                    BigDecimal repayAmount = nowRepayAmount.add(afBorrowCashDo.getRepayAmount());
                    logger.info("repayAmount=" + repayAmount);

                    String nowRepayAmountStr = NumberUtil.format2Str(nowRepayAmount);
                    String notRepayMoneyStr = "";

                    BigDecimal interest = BigDecimal.ZERO;

                    // 还款的时候 需要判断是否能还清利息 同时修改累计利息 start
                    BigDecimal tempRepayAmount = BigDecimal.ZERO;
                    tempRepayAmount = repayment.getRepaymentAmount();
                    if (tempRepayAmount.compareTo(afBorrowCashDo.getRateAmount()) > 0) {
                        bcashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), afBorrowCashDo.getRateAmount()));
                        bcashDo.setRateAmount(BigDecimal.ZERO);

                        //region 11-30新增

                        afBorrowCashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), afBorrowCashDo.getRateAmount()));
                        afBorrowCashDo.setRateAmount(BigDecimal.ZERO);

                        //end 11-30新增

                        interest = afBorrowCashDo.getRateAmount();
                        tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getRateAmount());
                    } else {
                        bcashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), tempRepayAmount));
                        bcashDo.setRateAmount(afBorrowCashDo.getRateAmount().subtract(tempRepayAmount));
                        //region 11-30新增
                        afBorrowCashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), tempRepayAmount));
                        afBorrowCashDo.setRateAmount(afBorrowCashDo.getRateAmount().subtract(tempRepayAmount));
                        //end 11-30新增
                        interest = tempRepayAmount;
                        tempRepayAmount = BigDecimal.ZERO;
                    }
                    // 还款的时候 需要判断是否能还清利息 同时修改累计利息 end

                    // 累计集分宝
                    bcashDo.setSumJfb(BigDecimalUtil.add(afBorrowCashDo.getSumJfb(), repayment.getJfbAmount()));

                    //region 11-30新增
                    afBorrowCashDo.setSumJfb(BigDecimalUtil.add(afBorrowCashDo.getSumJfb(), repayment.getJfbAmount()));
                    //end 11-30新增

                    BigDecimal overdueAmount = BigDecimal.ZERO;

                    // 还款的时候 需要判断是否能还清滞纳金 同时修改累计滞纳金 start
                    if (tempRepayAmount.compareTo(afBorrowCashDo.getOverdueAmount()) > 0) {
                        bcashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount()));
                        bcashDo.setOverdueAmount(BigDecimal.ZERO);

                        //region 11-30新增
                        afBorrowCashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount()));
                        afBorrowCashDo.setOverdueAmount(BigDecimal.ZERO);
                        //end 11-30新增

                        overdueAmount = afBorrowCashDo.getOverdueAmount();
                        tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getOverdueAmount());
                    } else {
                        bcashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), tempRepayAmount));
                        bcashDo.setOverdueAmount(afBorrowCashDo.getOverdueAmount().subtract(tempRepayAmount));

                        //region 11-30新增
                        afBorrowCashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), tempRepayAmount));
                        afBorrowCashDo.setOverdueAmount(afBorrowCashDo.getOverdueAmount().subtract(tempRepayAmount));
                        //end 11-30新增

                        overdueAmount = tempRepayAmount;
                        tempRepayAmount = BigDecimal.ZERO;
                    }
                    // 还款的时候 需要判断是否能还清滞纳金 同时修改累计滞纳金 end

                    // 累计使用余额
                    bcashDo.setSumRebate(BigDecimalUtil.add(afBorrowCashDo.getSumRebate(), repayment.getRebateAmount()));
                    bcashDo.setRepayAmount(repayAmount);
                    //region 11-30新增
                    afBorrowCashDo.setSumRebate(BigDecimalUtil.add(afBorrowCashDo.getSumRebate(), repayment.getRebateAmount()));
                    afBorrowCashDo.setRepayAmount(repayAmount);
                    //end 11-30新增
                    //涉及运算,放在内部传输数据
                    try {
                        String riskOrderNo = riskUtil.getOrderNo("tran", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                        JSONArray details = new JSONArray();
                        JSONObject obj = new JSONObject();
                        obj.put("borrowNo", afBorrowCashDo.getBorrowNo());
                        obj.put("amount", afBorrowCashDo.getAmount());
                        obj.put("repayment", repayment.getRepaymentAmount());
                        obj.put("income", BigDecimal.ZERO);
                        obj.put("interest", interest);
                        obj.put("overdueAmount", overdueAmount);
                        obj.put("overdueDay", afBorrowCashDo.getOverdueDay());
                        details.add(obj);
                        riskUtil.transferBorrowInfo(afBorrowCashDo.getUserId().toString(), "50", riskOrderNo, details);
                    } catch (Exception e) {
                        logger.error("还款时给风控传输数据出错", e);
                    }

                    // 优惠券设置已使用
                    afUserCouponDao.updateUserCouponSatusUsedById(repayment.getUserCouponId());

                    // 授权账户可用金额变更
                    AfUserAccountDo account = new AfUserAccountDo();
                    account.setUserId(repayment.getUserId());
                    account.setJfbAmount(repayment.getJfbAmount().multiply(new BigDecimal(-1)));

                    account.setRebateAmount(repayment.getRebateAmount().multiply(new BigDecimal(-1)));
                    afUserAccountDao.updateUserAccount(account);
                    afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENTCASH, repayment.getRebateAmount(), repayment.getUserId(), repayment.getRid()));

                    AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
                    temRepayMent.setStatus(AfBorrowCashRepmentStatus.YES.getCode());
                    temRepayMent.setTradeNo(tradeNo);
                    temRepayMent.setRid(repayment.getRid());
                    // 变更还款记录为已还款
                    afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);

                    if (allAmount.compareTo(repayAmount) == 0) {
                        Long userId = afBorrowCashDo.getUserId();
                        AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
                        //减少使用额度
                        accountInfo.setUsedAmount(BigDecimalUtil.subtract(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount()));
                        afUserAccountDao.updateOriginalUserAccount(accountInfo);
                        //增加日志
                        AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAYMENTCASH,
                                afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
                        afUserAccountLogDao.addUserAccountLog(accountLog);

                        bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
                        //fmf 增加还款成功为FINSH的时间
                        try {
                            bcashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
                        } catch (Exception e) {
                            logger.error("bcashDo.setFinishDate is fail", e);
                        }
                    } else {
                        notRepayMoneyStr = NumberUtil.format2Str(allAmount.subtract(repayAmount));
                    }
                    afBorrowCashService.updateBorrowCash(bcashDo);
                    //add by chengkang 待添加还款成功短信 start
                    try {
                        AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
                        if(repayment.getName().equals("代扣付款")){
                            String content= "代扣还款"+nowRepayAmountStr+"元，该笔借款已结清。";
                            smsUtil.sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), content);
                        }else{
                            smsUtil.sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), nowRepayAmountStr, notRepayMoneyStr);
                        }
                    } catch (Exception e) {
                        logger.error("还款成功发送短信异常,userId:" + afBorrowCashDo.getUserId() + ",nowRepayAmount:" + nowRepayAmountStr + ",notRepayMoney" + notRepayMoneyStr, e);
                    }
                    //add by chengkang 待添加还款成功短信 end
                    return 1l;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    logger.info("dealRepaymentSucess error", e);
                    return 0l;
                } finally {
                    redisTemplate.delete(key);
                }
            }
        });

        if (resultValue == 1L) {
            //还款成功,通知同步数据等操作
            logger.info("还款成功,通知同步数据等操作进入repayNo:" + repayment.getRepayNo() + ",borrowId:" + afBorrowCashDo.getRid());

            /**------------------------------------fmai风控提额begin------------------------------------------------*/
            try {
                String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                BigDecimal income = BigDecimalUtil.add(afBorrowCashDo.getPoundage(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
                int overdueCount = 0;
                if (StringUtil.equals("Y", afBorrowCashDo.getOverdueStatus())) {
                    overdueCount = 1;
                }
                //11-17号加入,还清了才能调用提额
                AfBorrowCashDo afterRepaymentBorrowCashDo = afBorrowCashService.getBorrowCashByrid(repayment.getBorrowId());
                if (afterRepaymentBorrowCashDo.getStatus().equals(AfBorrowCashStatus.finsh.getCode())) {
                    logger.info("还完了调用提额接口 ：" + afterRepaymentBorrowCashDo.getBorrowNo());
                    riskUtil.raiseQuota(afBorrowCashDo.getUserId().toString(), afBorrowCashDo.getBorrowNo(), "50", riskOrderNo, afBorrowCashDo.getAmount(), income, afBorrowCashDo.getOverdueDay(), overdueCount);
                }
                logger.info("没还完不调用提额接口 ：" + afterRepaymentBorrowCashDo.getBorrowNo());
            } catch (Exception e) {
                logger.error("风控提额提额失败", e);
            }
            /**------------------------------------fmai风控提额end--------------------------------------------------*/

            //会对逾期的借款还款，向催收平台同步还款信息
            AfBorrowCashDo currAfBorrowCashDo = afBorrowCashDo;
            if (DateUtil.compareDate(new Date(), afBorrowCashDo.getGmtPlanRepayment())) {
                try {
                    CollectionSystemReqRespBo respInfo = collectionSystemUtil.consumerRepayment(repayment.getRepayNo(),
                            currAfBorrowCashDo.getBorrowNo(),
                            repayment.getCardNumber(),
                            repayment.getCardName(),
                            DateUtil.formatDateTime(new Date()),
                            tradeNo,
                            repayment.getRepaymentAmount(),
                            (currAfBorrowCashDo.getAmount().add(currAfBorrowCashDo.getRateAmount().add(currAfBorrowCashDo.getOverdueAmount().add(currAfBorrowCashDo.getSumRate().add(currAfBorrowCashDo.getSumOverdue())))).subtract(currAfBorrowCashDo.getRepayAmount()).setScale(2, RoundingMode.HALF_UP)),
                            (currAfBorrowCashDo.getAmount().add(currAfBorrowCashDo.getRateAmount().add(currAfBorrowCashDo.getOverdueAmount().add(currAfBorrowCashDo.getSumRate().add(currAfBorrowCashDo.getSumOverdue())))).setScale(2, RoundingMode.HALF_UP)),
                            currAfBorrowCashDo.getOverdueAmount(),
                            currAfBorrowCashDo.getRepayAmount(),
                            currAfBorrowCashDo.getRateAmount());
                    logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
                } catch (Exception e) {
                    logger.error("向催收平台同步还款信息失败", e);
                }
            }else{
				logger.info("collection consumerRepayment not push,borrowCashId="+currAfBorrowCashDo.getRid());
			}
        }

        return resultValue;
    }
	
	public long dealRepaymentFail(String outTradeNo, String tradeNo,boolean isNeedMsgNotice,String errorMsg) {
		AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
		if (YesNoStatus.YES.getCode().equals(repayment.getStatus())) {
			return 0l;
		}
		long rowNums = dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), repayment.getRid());
		
		if(isNeedMsgNotice){
			//用户信息及当日还款失败次数校验
			int errorTimes = 0;
			//如果是代扣，不校验次数
			String payType = repayment.getName();
			if(StringUtil.isNotBlank(payType)&&payType.indexOf("代扣")>-1){
				errorTimes = 0;
			}else{
				errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(repayment.getUserId());
			}
			AfUserDo afUserDo = afUserService.getUserById(repayment.getUserId());
			//还款失败短信通知
			smsUtil.sendRepaymentBorrowCashFail(afUserDo.getMobile(),errorMsg,errorTimes);
		}
		return rowNums;
	}
	
	private long dealChangStatus(String outTradeNo, String tradeNo, String status, Long rid) {
        AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
        temRepayMent.setStatus(status);
        temRepayMent.setTradeNo(tradeNo);
        temRepayMent.setRid(rid);
        return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
    }
	
	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type, BigDecimal amount, Long userId, Long repaymentId) {
        // 增加account变更日志
        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setAmount(amount);
        accountLog.setUserId(userId);
        accountLog.setRefId(repaymentId + "");
        accountLog.setType(type.getCode());
        return accountLog;
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
	
	private AfRepaymentBorrowCashDo buildOrderRepayment(RepayBo bo) {
		AfBorrowLegalOrderRepaymentDo repayment = new AfBorrowLegalOrderRepaymentDo();
		
		repayment.setUserId(bo.userId);
		repayment.setBorrowLegalOrderCashId(bo.borrowOrderId);couponDto
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
	
	@Override
	public BaseDao<AfBorrowLegalOrderRepaymentDo, Long> getDao() {
		return afBorrowLegalOrderRepaymentDao;
	}
	
	public static class RepayBo{
		public Long userId;
		
		/* request字段 */
		public BigDecimal repaymentAmount;
		public BigDecimal actualAmount;
		public BigDecimal rebateAmount; //可选字段
		public String payPwd;			//可选字段
		public Long cardId;
		public Long couponId;			//可选字段
		public Long borrowId;			//可选字段
		public Long borrowOrderId; 		//可选字段
		public String from;
		/* request字段 */
		
		/* biz 业务处理字段 */
		public AfBorrowCashDo cashDo;
		public AfBorrowLegalOrderCashDo orderCashDo;
		public AfUserCouponDto couponDto; //可选字段
		public AfUserAccountDo userDo;
		public String remoteIp;
		
		/* Response字段 */
		public Long rid;
		public String refId;
		public String type;
		public String outTradeNo;
		public String tradeNo;
		public String cardNo;
		/* Response字段 */
		
		/* 错误码区域 */
		public Exception e;
		
	}
	
}
