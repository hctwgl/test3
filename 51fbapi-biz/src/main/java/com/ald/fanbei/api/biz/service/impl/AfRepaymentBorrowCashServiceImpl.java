package com.ald.fanbei.api.biz.service.impl;


import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.assetpush.ModifiedBorrowInfoVo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.*;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.ald.fanbei.api.biz.third.util.pay.ThirdPayUtility;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author suweili 2017年3月27日下午9:01:41
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRepaymentBorrowCashService")
public class AfRepaymentBorrowCashServiceImpl extends BaseService implements AfRepaymentBorrowCashService {

    @Resource
    CuiShouUtils cuiShouUtils;

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
    private AfTradeCodeInfoService afTradeCodeInfoService;

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

    @Autowired
    AfBorrowLegalOrderService afBorrowLegalOrderService;

    @Resource
    AfBorrowLegalRepaymentV2Service afBorrowLegalRepaymentV2Service;
    
    @Resource
    AssetSideEdspayUtil assetSideEdspayUtil;
    @Autowired
    KafkaSync kafkaSync;
    @Resource
    AfTaskUserService afTaskUserService;
    @Override
    public int addRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo) {
        return afRepaymentBorrowCashDao.addRepaymentBorrowCash(afRepaymentBorrowCashDo);
    }

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ThirdPayUtility thirdPayUtility;


    @Override
    public int updateRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo) {
        return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(afRepaymentBorrowCashDo);
    }

    @Override
    public int deleteRepaymentBorrowCash(Long rid) {
        return afRepaymentBorrowCashDao.deleteRepaymentBorrowCash(rid);
    }

    @Override
    public List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashByBorrowId(Long borrowCashId) {
        return afRepaymentBorrowCashDao.getRepaymentBorrowCashByBorrowId(borrowCashId);
    }

    @Override
    public AfRepaymentBorrowCashDo getRepaymentBorrowCashByrid(Long rid) {
        return afRepaymentBorrowCashDao.getRepaymentBorrowCashByrid(rid);
    }

    @Override
    public List<AfRepaymentBorrowCashDo> getRepaymentBorrowCashListByUserId(Long userId) {
        return afRepaymentBorrowCashDao.getRepaymentBorrowCashListByUserId(userId);
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

    @Override
    public Map<String, Object> createRepayment(final BigDecimal jfbAmount, final BigDecimal repaymentAmount, final BigDecimal actualAmount, final AfUserCouponDto coupon, final BigDecimal rebateAmount,
                                               final Long borrow, final Long cardId, final Long userId, final String clientIp, final AfUserAccountDo afUserAccountDo,String bankPayType,String majiabaoName) {
        Date now = new Date();
        String repayNo = generatorClusterNo.getRepaymentBorrowCashNo(now,bankPayType);
        final String payTradeNo = repayNo;
        String typeName = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
        ;
        if (StringUtil.equals("sysJob", clientIp)) {
            typeName = "代扣付款";
        }
        // 新增还款记录,移至事务外，保证回调先回用户这笔还款发起有记录产生
        final String name = typeName;

        final AfRepaymentBorrowCashDo repayment = buildRepayment(jfbAmount, repaymentAmount, repayNo, now, actualAmount, coupon, rebateAmount, borrow, cardId, payTradeNo, name,
                userId);
        int result = afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
        if(result<=0){
            //防止还款记录插入失败
            logger.error("insert repayment fail data:"+rebateAmount.toString());
            return null;
        }
        logger.info("createRepayment addRepaymentBorrowCash finish,payTradeNo=" + payTradeNo + ",repaymentId=" + (repayment != null ? repayment.getRid() : 0));
        if (cardId > 0) {
            dealChangStatus(payTradeNo, "", AfBorrowCashRepmentStatus.PROCESS.getCode(), repayment);
        }
        final Boolean isV2 = afBorrowLegalOrderService.isV2BorrowCash(borrow);
        //return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
        // @Override
        //public Map<String, Object> doInTransaction(TransactionStatus status) {
        try {
            Map<String, Object> map = new HashMap<String, Object>();
            if (cardId == -1) {// 微信支付
                map = UpsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, actualAmount, PayOrderSource.REPAYMENTCASH.getCode());
            } else if (cardId > 0) {// 银行卡支付
                String merPriv = UserAccountLogType.REPAYMENTCASH.getCode();
                if(isV2){
                    merPriv = PayOrderSource.REPAY_CASH_LEGAL_V2.getCode();
                }
                AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
                UpsCollectRespBo respBo = (UpsCollectRespBo) upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(), 
                	bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, name, "02", merPriv);
                if (!respBo.isSuccess()) {
                    if (StringUtil.isNotBlank(respBo.getRespCode())) {
                	String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
                        dealRepaymentFail(payTradeNo, "", true, errorMsg, repayment,majiabaoName);
                        throw new FanbeiException(errorMsg);
                    } else {
                        dealRepaymentFail(payTradeNo, "", false, "", repayment,majiabaoName);
                    }
                    throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
                }
                map.put("resp", respBo);
            } else if (cardId == -2) {// 余额支付
                //判断是否搭售V2
                if(isV2){
                    afBorrowLegalRepaymentV2Service.dealRepaymentSucess(repayment.getPayTradeNo(), "");
                }else{
                    dealRepaymentSucess(repayment.getPayTradeNo(), "",majiabaoName);
                }

            }
            map.put("refId", repayment.getRid());
            map.put("type", UserAccountLogType.REPAYMENTCASH.getCode());
            return map;
        } catch (Exception e) {
            if (e instanceof FanbeiException) {
                logger.error("createRepayment exist catch error,donot need rollback,payTradeNo=" + payTradeNo);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(Constants.THIRD_REQ_EXCEP_KEY, e);
                map.put("refId", repayment.getRid());
                return map;
            } else {
                logger.error("createRepayment exist error,need rollback,payTradeNo=" + payTradeNo, e);
                // status.setRollbackOnly();
                throw e;
            }
        }
        //}
        //});
    }


    public Map<String, Object> createRepaymentYiBao(final BigDecimal jfbAmount, final BigDecimal repaymentAmount, final BigDecimal actualAmount, final AfUserCouponDto coupon, final BigDecimal rebateAmount,
                                                    final Long borrow, final Long cardId, final Long userId, final String clientIp, final AfUserAccountDo afUserAccountDo,final String bankPayType) {

        Date now = new Date();
        String repayNo = generatorClusterNo.getRepaymentBorrowCashNo(now,bankPayType);

        final String payTradeNo = repayNo;
        // 新增还款记录
        final String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;

        final AfRepaymentBorrowCashDo repayment = buildRepayment(jfbAmount, repaymentAmount, repayNo, now, actualAmount, coupon, rebateAmount, borrow, cardId, payTradeNo, name,
                userId);
        afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
        logger.info("createRepayment addRepaymentBorrowCash finish,payTradeNo=" + payTradeNo + ",repaymentId=" + (repayment != null ? repayment.getRid() : 0));
        if (cardId > 0) {
            dealChangStatus(payTradeNo, "", AfBorrowCashRepmentStatus.PROCESS.getCode(), repayment);
        }
        return transactionTemplate.execute(new TransactionCallback<Map<String, Object>>() {
            @Override
            public Map<String, Object> doInTransaction(TransactionStatus status) {
                try {
                    Map<String, Object> map = new HashMap<String, Object>();

                    if (cardId == -1 || cardId == -3) {
                        Map<String, String> map1;
                        if (cardId == -1) {
                            map1 = thirdPayUtility.createOrder(actualAmount, payTradeNo, userId, ThirdPayTypeEnum.WXPAY, PayOrderSource.REPAYMENTCASH);
                        } else {
                            map1 = thirdPayUtility.createOrder(actualAmount, payTradeNo, userId, ThirdPayTypeEnum.ZFBPAY, PayOrderSource.REPAYMENTCASH);
                        }
                        for (String key : map1.keySet()) {
                            map.put(key, map1.get(key));
                        }
                    } else if (cardId == -4) {

                    } else if (cardId > 0) {// 银行卡支付
                        AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
                        UpsCollectRespBo respBo = (UpsCollectRespBo) upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), 
                        	bank.getMobile(), bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, 
                        	name, "02", UserAccountLogType.REPAYMENTCASH.getCode());
                        if (!respBo.isSuccess()) {
                            String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
                            dealRepaymentFail(payTradeNo, "", true, errorMsg, repayment,"");
                            throw new FanbeiException(errorMsg);
                        }
                        map.put("resp", respBo);
                    } else if (cardId == -2) {// 余额支付
                        dealRepaymentSucess(repayment.getPayTradeNo(), "","");
                    }
                    map.put("refId", repayment.getRid());
                    map.put("type", UserAccountLogType.REPAYMENTCASH.getCode());

                    return map;
                } catch (Exception e) {
                    if (e instanceof FanbeiException) {
                        logger.error("createRepaymentYiBao exist catch error,donot need rollback,payTradeNo=" + payTradeNo, e);
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put(Constants.THIRD_REQ_EXCEP_KEY, e);
                        return map;
                    } else {
                        logger.error("createRepaymentYiBao exist error,need rollback,payTradeNo=" + payTradeNo, e);
                        status.setRollbackOnly();
                        throw e;
                    }
                }
            }
        });

    }


    @Override
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
        final HashMap<String, BigDecimal> tempDataMap = new HashMap();
        final HashMap<String, String> tempSmsDataMap = new HashMap();
        tempDataMap.put("interest", BigDecimal.ZERO);
        tempDataMap.put("overdueAmount", BigDecimal.ZERO);
        tempSmsDataMap.put("nowRepayAmountStr", "");
        tempSmsDataMap.put("notRepayMoneyStr", "");
        final String cardNo = userCardNo;
        Boolean isCashOverdueOld = false;
        try{
            Date gmtPlanTime = afBorrowCashDo.getGmtPlanRepayment();
            gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
            Date newDate = new Date();
            newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
            if (StringUtils.equals("代扣付款",repayment.getName())&&gmtPlanTime.getTime() < newDate.getTime()) {
                isCashOverdueOld = true;
            }
        }catch(Exception ex){
            logger.info("dealRepaymentSucess isCashOverdue error", ex);
        }
        final Boolean isCashOverdue = isCashOverdueOld;
        long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                try {
//					AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
//					if(afYibaoOrderDo !=null){
//						if(afYibaoOrderDo.getStatus().intValue() == 1){
//							return 0L;
//						}else{
//							afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),1);
//						}
//					}
                    if (thirdPayUtility.checkSuccess(outTradeNo)) {
                        return 1L;
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
                    tempDataMap.put("interest", interest);
                    tempDataMap.put("overdueAmount", overdueAmount);


                    // 优惠券设置已使用
                    afUserCouponDao.updateUserCouponSatusUsedById(repayment.getUserCouponId());

                    // 授权账户可用金额变更
                    if (repayment.getRebateAmount().compareTo(BigDecimal.ZERO) > 0 || repayment.getJfbAmount().compareTo(BigDecimal.ZERO) > 0) {
                        AfUserAccountDo account = new AfUserAccountDo();
                        account.setUserId(repayment.getUserId());
                        account.setJfbAmount(repayment.getJfbAmount().multiply(new BigDecimal(-1)));

                        account.setRebateAmount(repayment.getRebateAmount().multiply(new BigDecimal(-1)));
                        int result = afUserAccountDao.updateUserAccount(account);
                        if (result <= 0) {
                            logger.info("update account error,details:repayNo" + repayment.getRepayNo(), JSON.toJSONString(account));
                            //throw new Exception("update account error,details");
                        }
                    }

                    afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENTCASH, repayment.getRebateAmount(), repayment.getUserId(), repayment.getRid()));

                    // add by luoxiao for 边逛边赚，增加零钱明细
                    afTaskUserService.addTaskUser(repayment.getUserId(),UserAccountLogType.REPAYMENTCASH.getName(), repayment.getRebateAmount().multiply(new BigDecimal(-1)));
                    // end by luoxiao

                    AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
                    temRepayMent.setStatus(AfBorrowCashRepmentStatus.YES.getCode());
                    temRepayMent.setTradeNo(tradeNo);
                    temRepayMent.setRid(repayment.getRid());
                    // 变更还款记录为已还款
                    afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
                    //判断是否需要平账
                    if (isCashOverdue|| allAmount.compareTo(repayAmount) == 0) {
                        Long userId = afBorrowCashDo.getUserId();
                        AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
                        //减少使用额度
                        accountInfo.setUsedAmount(BigDecimalUtil.subtract(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount()));
                        afUserAccountDao.updateOriginalUserAccount(accountInfo);
                        //增加日志
                        AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAYMENTCASH,
                                afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
                        afUserAccountLogDao.addUserAccountLog(accountLog);
                        afBorrowCashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
                        bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
                        try {
                			boolean isBefore = DateUtil.isBefore(new Date(),DateUtil.addDays( afBorrowCashDo.getGmtPlanRepayment(), -1));
                			if (isBefore) {
                				if (assetSideEdspayUtil.isPush(afBorrowCashDo)) {
                					List<ModifiedBorrowInfoVo> modifiedLoanInfo = assetSideEdspayUtil.buildModifiedInfo(afBorrowCashDo,1);
                					boolean result = assetSideEdspayUtil.transModifiedBorrowInfo(modifiedLoanInfo,Constants.ASSET_SIDE_EDSPAY_FLAG, Constants.ASSET_SIDE_FANBEI_FLAG);
                					if (result) {
                						logger.info("trans modified borrowcash Info success,loanId="+afBorrowCashDo.getRid());
                					}else{
                						assetSideEdspayUtil.transFailRecord(afBorrowCashDo, modifiedLoanInfo);
                					}
                				}
                			}
                		} catch (Exception e) {
                			logger.error("preFinishNotifyEds error="+e);
                		}
                        //fmf 增加还款成功为FINSH的时间
                        try {
                            afBorrowCashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
                            bcashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
                        } catch (Exception e) {
                            logger.error("bcashDo.setFinishDate is fail", e);
                        }
                    } else {
                        notRepayMoneyStr = NumberUtil.format2Str(allAmount.subtract(repayAmount));
                    }
                    afBorrowCashService.updateBorrowCash(bcashDo);

                    tempSmsDataMap.put("nowRepayAmountStr", nowRepayAmountStr);
                    tempSmsDataMap.put("notRepayMoneyStr", notRepayMoneyStr);
                    //add by chengkang 待添加还款成功短信 start

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
            try{
                kafkaSync.syncEvent(afBorrowCashDo.getUserId(), KafkaConstants.SYNC_USER_BASIC_DATA,true);
                kafkaSync.syncEvent(afBorrowCashDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);
            }catch (Exception e){
                logger.info("消息同步失败:",e);
            }
            try {
                AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
                if (StringUtils.equals("代扣付款",repayment.getName())) {
                    //if (isCashOverdueOld) {
                        //sendRepaymentBorrowCashOverdueWithHold(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"));
                    //} else {
                        sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"));
                    //}
                } else {
                    if (afBorrowCashDo.getMajiabaoName().contains("borrowSuperman")){
                        sendJKCRRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"), tempSmsDataMap.get("notRepayMoneyStr"));
                    }else {
                        sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"), tempSmsDataMap.get("notRepayMoneyStr"));
                    }
                }
            } catch (Exception e) {
                logger.error("还款成功发送短信异常,userId:" + afBorrowCashDo.getUserId() + ",nowRepayAmount:" + tempSmsDataMap.get("nowRepayAmountStr") + ",notRepayMoney" + tempSmsDataMap.get("notRepayMoneyStr"), e);
            }
            //add by chengkang 待添加还款成功短信 end
            try {
                String riskOrderNo = riskUtil.getOrderNo("tran", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                JSONArray details = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("borrowNo", afBorrowCashDo.getBorrowNo());
                obj.put("amount", afBorrowCashDo.getAmount());
                obj.put("repayment", repayment.getRepaymentAmount());
                obj.put("income", BigDecimal.ZERO);
                obj.put("interest", tempDataMap.get("interest"));
                obj.put("overdueAmount", tempDataMap.get("overdueAmount"));
                obj.put("overdueDay", afBorrowCashDo.getOverdueDay());
                details.add(obj);
                riskUtil.transferBorrowInfo(afBorrowCashDo.getUserId().toString(), "50", riskOrderNo, details);
            } catch (Exception e) {
                logger.error("还款时给风控传输数据出错", e);
            }
            //还款成功,通知同步数据等操作
            logger.info("还款成功,通知同步数据等操作进入repayNo:" + repayment.getRepayNo() + ",borrowId:" + afBorrowCashDo.getRid());

            /**------------------------------------fmai风控提额begin------------------------------------------------*/
            try {
                String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                BigDecimal income = BigDecimalUtil.add(afBorrowCashDo.getPoundage(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate(),afBorrowCashDo.getSumRenewalPoundage());
                //收入添加搭售商品价格
                AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
                if(afBorrowLegalOrderDo!=null&&afBorrowLegalOrderDo.getPriceAmount()!=null) {
                    income = income.add(afBorrowLegalOrderDo.getPriceAmount());
                }
                else {
                    logger.info("未获取到搭售商品信息 cashDo："+ afBorrowCashDo.toString());
                }

                int overdueCount = 0;
                if (StringUtil.equals("Y", afBorrowCashDo.getOverdueStatus())) {
                    overdueCount = 1;
                }
                //11-17号加入,还清了才能调用提额
                if (afBorrowCashDo.getStatus().equals(AfBorrowCashStatus.finsh.getCode())) {
                    logger.info("还完了调用提额接口 ：" + afBorrowCashDo.getBorrowNo());
                    riskUtil.raiseQuota(afBorrowCashDo.getUserId().toString(), afBorrowCashDo.getBorrowNo(), "50", riskOrderNo, afBorrowCashDo.getAmount(), income, afBorrowCashDo.getOverdueDay(), overdueCount, afBorrowCashDo.getOverdueDay(), afBorrowCashDo.getRenewalNum());
                }
                logger.info("没还完不调用提额接口 ：" + afBorrowCashDo.getBorrowNo());
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
                            currAfBorrowCashDo.getRateAmount(),isCashOverdueOld);
                    logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
                } catch (Exception e) {
                    logger.error("向催收平台同步还款信息失败", e);
                }
            } else {
                logger.info("collection consumerRepayment not push,borrowCashId=" + currAfBorrowCashDo.getRid());
            }
            cuiShouUtils.syncCuiShou(repayment);  //新催收线下还款
        }

        return resultValue;
    }

    @Override
    public long dealRepaymentSucess(final String outTradeNo, final String tradeNo,String majiabaoName) {
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
        final HashMap<String, BigDecimal> tempDataMap = new HashMap();
        final HashMap<String, String> tempSmsDataMap = new HashMap();
        tempDataMap.put("interest", BigDecimal.ZERO);
        tempDataMap.put("overdueAmount", BigDecimal.ZERO);
        tempSmsDataMap.put("nowRepayAmountStr", "");
        tempSmsDataMap.put("notRepayMoneyStr", "");
        final String cardNo = userCardNo;
        Boolean isCashOverdueOld = false;
        try{
            Date gmtPlanTime = afBorrowCashDo.getGmtPlanRepayment();
            gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
            Date newDate = new Date();
            newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
            if (StringUtils.equals("代扣付款",repayment.getName())&&gmtPlanTime.getTime() < newDate.getTime()) {
                isCashOverdueOld = true;
            }
        }catch(Exception ex){
            logger.info("dealRepaymentSucess isCashOverdue error", ex);
        }
        final Boolean isCashOverdue = isCashOverdueOld;
        long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                try {
//					AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
//					if(afYibaoOrderDo !=null){
//						if(afYibaoOrderDo.getStatus().intValue() == 1){
//							return 0L;
//						}else{
//							afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),1);
//						}
//					}
                    if (thirdPayUtility.checkSuccess(outTradeNo)) {
                        return 1L;
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
                    tempDataMap.put("interest", interest);
                    tempDataMap.put("overdueAmount", overdueAmount);


                    // 优惠券设置已使用
                    afUserCouponDao.updateUserCouponSatusUsedById(repayment.getUserCouponId());

                    // 授权账户可用金额变更
                    if (repayment.getRebateAmount().compareTo(BigDecimal.ZERO) > 0 || repayment.getJfbAmount().compareTo(BigDecimal.ZERO) > 0) {
                        AfUserAccountDo account = new AfUserAccountDo();
                        account.setUserId(repayment.getUserId());
                        account.setJfbAmount(repayment.getJfbAmount().multiply(new BigDecimal(-1)));

                        account.setRebateAmount(repayment.getRebateAmount().multiply(new BigDecimal(-1)));
                        int result = afUserAccountDao.updateUserAccount(account);
                        if (result <= 0) {
                            logger.info("update account error,details:repayNo" + repayment.getRepayNo(), JSON.toJSONString(account));
                            //throw new Exception("update account error,details");
                        }
                    }

                    afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENTCASH, repayment.getRebateAmount(), repayment.getUserId(), repayment.getRid()));

                    AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
                    temRepayMent.setStatus(AfBorrowCashRepmentStatus.YES.getCode());
                    temRepayMent.setTradeNo(tradeNo);
                    temRepayMent.setRid(repayment.getRid());
                    // 变更还款记录为已还款
                    afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
                    //判断是否需要平账
                    if (isCashOverdue|| allAmount.compareTo(repayAmount) == 0) {
                        Long userId = afBorrowCashDo.getUserId();
                        AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
                        //减少使用额度
                        accountInfo.setUsedAmount(BigDecimalUtil.subtract(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount()));
                        afUserAccountDao.updateOriginalUserAccount(accountInfo);
                        //增加日志
                        AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAYMENTCASH,
                                afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
                        afUserAccountLogDao.addUserAccountLog(accountLog);
                        afBorrowCashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
                        bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
                        try {
                            boolean isBefore = DateUtil.isBefore(new Date(),DateUtil.addDays( afBorrowCashDo.getGmtPlanRepayment(), -1));
                            if (isBefore) {
                                if (assetSideEdspayUtil.isPush(afBorrowCashDo)) {
                                    List<ModifiedBorrowInfoVo> modifiedLoanInfo = assetSideEdspayUtil.buildModifiedInfo(afBorrowCashDo,1);
                                    boolean result = assetSideEdspayUtil.transModifiedBorrowInfo(modifiedLoanInfo,Constants.ASSET_SIDE_EDSPAY_FLAG, Constants.ASSET_SIDE_FANBEI_FLAG);
                                    if (result) {
                                        logger.info("trans modified borrowcash Info success,loanId="+afBorrowCashDo.getRid());
                                    }else{
                                        assetSideEdspayUtil.transFailRecord(afBorrowCashDo, modifiedLoanInfo);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("preFinishNotifyEds error="+e);
                        }
                        //fmf 增加还款成功为FINSH的时间
                        try {
                            afBorrowCashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
                            bcashDo.setFinishDate(DateUtil.formatDateTime(new Date()));
                        } catch (Exception e) {
                            logger.error("bcashDo.setFinishDate is fail", e);
                        }
                    } else {
                        notRepayMoneyStr = NumberUtil.format2Str(allAmount.subtract(repayAmount));
                    }
                    afBorrowCashService.updateBorrowCash(bcashDo);

                    tempSmsDataMap.put("nowRepayAmountStr", nowRepayAmountStr);
                    tempSmsDataMap.put("notRepayMoneyStr", notRepayMoneyStr);
                    //add by chengkang 待添加还款成功短信 start

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
            try{
                kafkaSync.syncEvent(afBorrowCashDo.getUserId(), KafkaConstants.SYNC_USER_BASIC_DATA,true);
                kafkaSync.syncEvent(afBorrowCashDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);
            }catch (Exception e){
                logger.info("消息同步失败:",e);
            }
            try {
                AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
                if (StringUtils.equals("代扣付款",repayment.getName())) {
                    //if (isCashOverdueOld) {
                    //sendRepaymentBorrowCashOverdueWithHold(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"));
                    //} else {
                    sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"));
                    //}
                } else {
                    if (majiabaoName.contains("borrowSuperman")){
                        sendJKCRRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"), tempSmsDataMap.get("notRepayMoneyStr"));
                    }else {
                        sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"), tempSmsDataMap.get("notRepayMoneyStr"));
                    }
//                    sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), tempSmsDataMap.get("nowRepayAmountStr"), tempSmsDataMap.get("notRepayMoneyStr"));
                }
            } catch (Exception e) {
                logger.error("还款成功发送短信异常,userId:" + afBorrowCashDo.getUserId() + ",nowRepayAmount:" + tempSmsDataMap.get("nowRepayAmountStr") + ",notRepayMoney" + tempSmsDataMap.get("notRepayMoneyStr"), e);
            }
            //add by chengkang 待添加还款成功短信 end
            try {
                String riskOrderNo = riskUtil.getOrderNo("tran", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                JSONArray details = new JSONArray();
                JSONObject obj = new JSONObject();
                obj.put("borrowNo", afBorrowCashDo.getBorrowNo());
                obj.put("amount", afBorrowCashDo.getAmount());
                obj.put("repayment", repayment.getRepaymentAmount());
                obj.put("income", BigDecimal.ZERO);
                obj.put("interest", tempDataMap.get("interest"));
                obj.put("overdueAmount", tempDataMap.get("overdueAmount"));
                obj.put("overdueDay", afBorrowCashDo.getOverdueDay());
                details.add(obj);
                riskUtil.transferBorrowInfo(afBorrowCashDo.getUserId().toString(), "50", riskOrderNo, details);
            } catch (Exception e) {
                logger.error("还款时给风控传输数据出错", e);
            }
            //还款成功,通知同步数据等操作
            logger.info("还款成功,通知同步数据等操作进入repayNo:" + repayment.getRepayNo() + ",borrowId:" + afBorrowCashDo.getRid());

            /**------------------------------------fmai风控提额begin------------------------------------------------*/
            try {
                String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                BigDecimal income = BigDecimalUtil.add(afBorrowCashDo.getPoundage(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate(),afBorrowCashDo.getSumRenewalPoundage());
                //收入添加搭售商品价格
                AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
                if(afBorrowLegalOrderDo!=null&&afBorrowLegalOrderDo.getPriceAmount()!=null) {
                    income = income.add(afBorrowLegalOrderDo.getPriceAmount());
                }
                else {
                    logger.info("未获取到搭售商品信息 cashDo："+ afBorrowCashDo.toString());
                }

                int overdueCount = 0;
                if (StringUtil.equals("Y", afBorrowCashDo.getOverdueStatus())) {
                    overdueCount = 1;
                }
                //11-17号加入,还清了才能调用提额
                if (afBorrowCashDo.getStatus().equals(AfBorrowCashStatus.finsh.getCode())) {
                    logger.info("还完了调用提额接口 ：" + afBorrowCashDo.getBorrowNo());
                    riskUtil.raiseQuota(afBorrowCashDo.getUserId().toString(), afBorrowCashDo.getBorrowNo(), "50", riskOrderNo, afBorrowCashDo.getAmount(), income, afBorrowCashDo.getOverdueDay(), overdueCount, afBorrowCashDo.getOverdueDay(), afBorrowCashDo.getRenewalNum());
                }
                logger.info("没还完不调用提额接口 ：" + afBorrowCashDo.getBorrowNo());
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
                            currAfBorrowCashDo.getRateAmount(),isCashOverdueOld);
                    logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
                } catch (Exception e) {
                    logger.error("向催收平台同步还款信息失败", e);
                }
            } else {
                logger.info("collection consumerRepayment not push,borrowCashId=" + currAfBorrowCashDo.getRid());
            }
            cuiShouUtils.syncCuiShou(repayment);  //新催收线下还款
        }

        return resultValue;
    }

    /**
     * 代扣现金贷还款成功短信发送
     *
     * @param mobile
     * @param nowRepayAmountStr
     */
    private boolean sendRepaymentBorrowCashWithHold(String mobile, String nowRepayAmountStr) {
        //模版数据map处理
        Map<String, String> replaceMapData = new HashMap<String, String>();
        replaceMapData.put("nowRepayAmountStr", nowRepayAmountStr);
        return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_SUCCESS.getCode());
    }

    /**
     * 代扣现金贷逾期还款成功短信发送
     *
     * @param mobile
     * @param nowRepayAmountStr
     */
    private boolean sendRepaymentBorrowCashOverdueWithHold(String mobile, String nowRepayAmountStr) {
        //模版数据map处理
        Map<String, String> replaceMapData = new HashMap<String, String>();
        replaceMapData.put("nowRepayAmountStr", nowRepayAmountStr);
        return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_OVERDUE_SUCCESS.getCode());
    }


    /**
     * 用户手动现金贷还款成功短信发送
     *
     * @param mobile
     */
    private boolean sendRepaymentBorrowCashWarnMsg(String mobile, String repayMoney, String notRepayMoney) {
        //模版数据map处理
        Map<String, String> replaceMapData = new HashMap<String, String>();
        replaceMapData.put("repayMoney", repayMoney);
        replaceMapData.put("remainAmount", notRepayMoney);
        if (StringUtil.isNotBlank(notRepayMoney)) {
            String title = "部分还款成功！";
            String content = "本次成功还款&repayMoney元，剩余待还金额&remainAmount元，请继续保持良好的信用习惯哦。";
            content = content.replace("&repayMoney", repayMoney);
            content = content.replace("&remainAmount", notRepayMoney);
            pushService.pushUtil(title, content, mobile);
            return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS_REMAIN.getCode());
        } else {
            String title = "恭喜您，借款已还清！";
            String content = "您的还款已经处理完成，成功还款&repayMoney元。信用分再度升级，给您点个大大的赞！";
            content = content.replace("&repayMoney", repayMoney);
            pushService.pushUtil(title, content, mobile);
            return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_CONFIRM_SUCCESS.getCode());
        }
    }

    /**
     * 用户手动现金贷还款成功短信发送
     *
     * @param mobile
     */
    private boolean sendJKCRRepaymentBorrowCashWarnMsg(String mobile, String repayMoney, String notRepayMoney) {
        //模版数据map处理
        Map<String, String> replaceMapData = new HashMap<String, String>();
        replaceMapData.put("repayMoney", repayMoney);
        replaceMapData.put("remainAmount", notRepayMoney);
        if (StringUtil.isNotBlank(notRepayMoney)) {
            String title = "部分还款成功！";
            String content = "本次成功还款&repayMoney元，剩余待还金额&remainAmount元，请继续保持良好的信用习惯哦。";
            content = content.replace("&repayMoney", repayMoney);
            content = content.replace("&remainAmount", notRepayMoney);
            pushService.pushUtil(title, content, mobile);
            return smsUtil.sendYsSmsConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS_REMAIN.getCode());
        } else {
            String title = "恭喜您，借款已还清！";
            String content = "您的还款已经处理完成，成功还款&repayMoney元。信用分再度升级，给您点个大大的赞！";
            content = content.replace("&repayMoney", repayMoney);
            pushService.pushUtil(title, content, mobile);
            return smsUtil.sendYsSmsConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_CONFIRM_SUCCESS.getCode());
        }
    }


//	private void increaseBorrowCashAccount(AfBorrowCashDo afBorrowCashDo,Long userId){
//		//提升借款额度
//		
//		BigDecimal borrowAmount = afBorrowCashDo.getAmount();
//		AfUserAccountDo accountBorrowDo= afUserAccountDao.getUserAccountInfoByUserId(userId);
//		BigDecimal accountBorrowAoumt = accountBorrowDo.getBorrowCashAmount();
//		AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashMoreAmount.getCode());
//		BigDecimal max = NumberUtil.objToBigDecimalDefault(resourceDo.getValue(), BigDecimal.ZERO);
//		BigDecimal addRate = NumberUtil.objToBigDecimalDefault(resourceDo.getValue1(), BigDecimal.ZERO);
//		BigDecimal addAmount = borrowAmount.multiply(addRate).divide(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
//
//		if(max.compareTo(accountBorrowAoumt)>0&& StringUtils.equals(afBorrowCashDo.getType(), AfBorrowCashType.FOURTEEN.getName())) {
//			AfUserAccountDo accountChange = new AfUserAccountDo();
//			accountChange.setRid(accountBorrowDo.getRid());
//			BigDecimal borrowAccountNew = accountBorrowAoumt.add(addAmount);
//			accountChange.setBorrowCashAmount(borrowAccountNew.compareTo(max)>0?max:borrowAccountNew);
//			afUserAccountDao.updateOriginalUserAccount(accountChange);
//		}
//	}

    private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type, BigDecimal amount, Long userId, Long repaymentId) {
        // 增加account变更日志
        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setAmount(amount);
        accountLog.setUserId(userId);
        accountLog.setRefId(repaymentId + "");
        accountLog.setType(type.getCode());
        return accountLog;
    }

    @Override
    public long dealRepaymentFail(String outTradeNo, String tradeNo, boolean isNeedMsgNotice, String errorMsg, AfRepaymentBorrowCashDo repayment,String majiabaoName) {
        if (repayment == null) {
            repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
        }
        AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(repayment.getBorrowId());
        if (YesNoStatus.YES.getCode().equals(repayment.getStatus())) {
            return 0l;
        }
        long rowNums = dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), repayment);

        if (isNeedMsgNotice) {
            //用户信息及当日还款失败次数校验
            int errorTimes = 0;
            AfUserDo afUserDo = afUserService.getUserById(repayment.getUserId());
            //如果是代扣，不校验次数
            String payType = repayment.getName();
            //模版数据map处理
            Map<String, String> replaceMapData = new HashMap<String, String>();
            replaceMapData.put("errorMsg", errorMsg);
            Boolean isCashOverdue = false;
            try{
                Date gmtPlanTime = afBorrowCashDo.getGmtPlanRepayment();
                gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
                Date newDate = new Date();
                newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
                if (StringUtils.equals("代扣付款",repayment.getName())&&gmtPlanTime.getTime() < newDate.getTime()) {
                    isCashOverdue = true;
                }
            }catch(Exception ex){
                logger.info("dealRepaymentFalse isCashOverdue error", ex);
            }
            //还款失败短信通知
            if (StringUtil.isNotBlank(payType) && payType.indexOf("代扣") > -1) {
                if (isCashOverdue) {
                    logger.info("borrowCash overdue withhold false orverdue,mobile=" + afUserDo.getMobile() + "errorMsg:" + errorMsg);
                } else {
                    smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL.getCode());
                }
            } else {
                errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(repayment.getUserId());
                if (majiabaoName.contains("borrowSuperman")){
                    smsUtil.sendYsSmsConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_JKCR_REPAYMENT_BORROWCASH_FAIL.getCode());
                }else {
                    smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_FAIL.getCode());
                }
                String title = "本次还款支付失败";
                String content = "非常遗憾，本次还款失败：&errorMsg，您可更换银行卡或采用其他还款方式。";
                content = content.replace("&errorMsg", errorMsg);
                pushService.pushUtil(title, content, afUserDo.getMobile());
            }
        }
        return rowNums;
    }

    @Override
    public long dealRepaymentFail(String outTradeNo, String tradeNo, boolean isNeedMsgNotice, String errorMsg, AfRepaymentBorrowCashDo repayment) {
        if (repayment == null) {
            repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
        }
        AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(repayment.getBorrowId());
        if (YesNoStatus.YES.getCode().equals(repayment.getStatus())) {
            return 0l;
        }
        long rowNums = dealChangStatus(outTradeNo, tradeNo, AfBorrowCashRepmentStatus.NO.getCode(), repayment);

        if (isNeedMsgNotice) {
            //用户信息及当日还款失败次数校验
            int errorTimes = 0;
            AfUserDo afUserDo = afUserService.getUserById(repayment.getUserId());
            //如果是代扣，不校验次数
            String payType = repayment.getName();
            //模版数据map处理
            Map<String, String> replaceMapData = new HashMap<String, String>();
            replaceMapData.put("errorMsg", errorMsg);
            Boolean isCashOverdue = false;
            try{
                Date gmtPlanTime = afBorrowCashDo.getGmtPlanRepayment();
                gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
                Date newDate = new Date();
                newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
                if (StringUtils.equals("代扣付款",repayment.getName())&&gmtPlanTime.getTime() < newDate.getTime()) {
                    isCashOverdue = true;
                }
            }catch(Exception ex){
                logger.info("dealRepaymentFalse isCashOverdue error", ex);
            }
            //还款失败短信通知
            if (StringUtil.isNotBlank(payType) && payType.indexOf("代扣") > -1) {
                if (isCashOverdue) {
                    logger.info("borrowCash overdue withhold false orverdue,mobile=" + afUserDo.getMobile() + "errorMsg:" + errorMsg);
                } else {
                    smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL.getCode());
                }
            } else {
                errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(repayment.getUserId());
                if (afBorrowCashDo.getMajiabaoName().contains("borrowSuperman")){
                    smsUtil.sendYsSmsConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_JKCR_REPAYMENT_BORROWCASH_FAIL.getCode());
                }else {
                    smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_FAIL.getCode());
                }
                String title = "本次还款支付失败";
                String content = "非常遗憾，本次还款失败：&errorMsg，您可更换银行卡或采用其他还款方式。";
                content = content.replace("&errorMsg", errorMsg);
                pushService.pushUtil(title, content, afUserDo.getMobile());
            }
        }
        return rowNums;
    }

    long dealChangStatus(String outTradeNo, String tradeNo, String status, AfRepaymentBorrowCashDo repayment) {
//		AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
//		if(afYibaoOrderDo !=null){
//			if(afYibaoOrderDo.getStatus().intValue() == 1){
//				return 1;
//			}else{
//				afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),2);
//			}
//		}
        try {
            if (AfBorrowCashRepmentStatus.NO.getCode().equals(status)) {
                if (thirdPayUtility.checkFail(outTradeNo)) {
                    return 1L;
                }
            }
            logger.info("sync error outTradeNo :" + outTradeNo + ",tradeNo:" + tradeNo + ",status:" + status + ",");
            AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
            temRepayMent.setStatus(status);
            temRepayMent.setTradeNo(tradeNo);
            temRepayMent.setRid(repayment.getRid());
            repayment.setStatus(status);
            repayment.setTradeNo(tradeNo);
            repayment.setRid(repayment.getRid());

            return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
        } catch (Exception e) {
            logger.info("sync error outTradeNo :" + outTradeNo + ",tradeNo:" + tradeNo + ",status:" + status + ",", e);
            return 0;
        }

    }

    @Override
    public AfRepaymentBorrowCashDo getLastRepaymentBorrowCashByBorrowId(Long borrowCashId) {
        return afRepaymentBorrowCashDao.getLastRepaymentBorrowCashByBorrowId(borrowCashId);
    }

    @Override
    public BigDecimal getRepaymentAllAmountByBorrowId(Long borrowId) {
        return afRepaymentBorrowCashDao.getRepaymentAllAmountByBorrowId(borrowId);
    }

    @Override
    public BigDecimal getRepayingTotalAmountByBorrowId(Long borrowId) {
        return afRepaymentBorrowCashDao.getRepayingTotalAmountByBorrowId(borrowId);
    }

    @Override
    public int getRepayingTotalNumByBorrowId(Long borrowId) {
        return afRepaymentBorrowCashDao.getRepayingTotalNumByBorrowId(borrowId);
    }

    @Override
    public String getCurrentLastRepayNo(String orderNoPre) {
        return afRepaymentBorrowCashDao.getCurrentLastRepayNo(orderNoPre);
    }

    @Override
    public String dealOfflineRepaymentSucess(final String repayNo, final String borrowNo, final String repayType, final String repayTime, final BigDecimal repayAmount, final BigDecimal restAmount, final String tradeNo, final String isBalance,final String isAdmin) {
        Date currDate = new Date();
        Date gmtCreate = DateUtil.parseDateTimeShortExpDefault(repayTime, currDate);
        //还款方式解析
        OfflinePayType offlinePayType = OfflinePayType.findPayTypeByCode(repayType);
        //线下还款记录添加
        String name;
        if (isAdmin != null && "Y".equals(isAdmin)){
            name = Constants.BORROW_REPAYMENT_NAME_OFFLINE;//财务线下打款
        }else {
            name = Constants.COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE;//催收线下打款
        }
        //线下还款记录添加
        final AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNoV1(borrowNo);
        final AfRepaymentBorrowCashDo repayment = new AfRepaymentBorrowCashDo(gmtCreate, currDate, "催收平台线下还款", repayNo, repayAmount, repayAmount, afBorrowCashDo.getRid(), repayNo, tradeNo,
                0L, BigDecimal.ZERO, BigDecimal.ZERO, AfBorrowCashRepmentStatus.YES.getCode(), afBorrowCashDo.getUserId(), "", offlinePayType == null ? repayType : offlinePayType.getName(), BigDecimal.ZERO);
        String ret = transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                try {
                    logger.info("dealOfflineRepaymentSucess begin repayNo=" + repayNo + ",borrowNo" + borrowNo + ",repayType" + repayType + ",repayTime" + repayTime
                            + ",repayAmount" + repayAmount + ",restAmount" + restAmount + ",tradeNo" + tradeNo + ",isBalance" + isBalance);
                    AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashInfoByBorrowNoV1(borrowNo);
                    if (afBorrowCashDo == null) {
                        logger.error("dealOfflineRepaymentSucess fail,borrowcash not exist,borrowNo=" + borrowNo);
                        return FanbeiThirdRespCode.BORROW_CASH_NOT_EXISTS.getCode();
                    }
                    //校验是否已经还款完成
                    if (AfBorrowCashStatus.finsh.getCode().equals(afBorrowCashDo.getStatus())) {
                        logger.error("dealOfflineRepaymentSucess fail,borrowcash have finished,id=" + afBorrowCashDo.getRid());
                        return FanbeiThirdRespCode.BORROW_CASH_HAVE_FINISHED.getCode();
                    }

//                    AfRepaymentBorrowCashDo repayment = new AfRepaymentBorrowCashDo(gmtCreate, currDate, name, repayNo, repayAmount, repayAmount, afBorrowCashDo.getRid(), repayNo, tradeNo,
//                            0L, BigDecimal.ZERO, BigDecimal.ZERO, AfBorrowCashRepmentStatus.YES.getCode(), afBorrowCashDo.getUserId(), "", offlinePayType == null ? repayType : offlinePayType.getName(), BigDecimal.ZERO);

                    afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);

                    BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getRateAmount(), afBorrowCashDo.getSumRate());
                    BigDecimal haveRepayAmount = repayAmount.add(afBorrowCashDo.getRepayAmount());

                    //更新对象
                    AfBorrowCashDo bcashDo = new AfBorrowCashDo();
                    bcashDo.setRid(afBorrowCashDo.getRid());
                    bcashDo.setRepayAmount(haveRepayAmount);

                    //本次还款用于处理利息和逾期费的金额，默认为0
                    BigDecimal tempRepayAmount = BigDecimal.ZERO;

                    if (YesNoStatus.YES.getCode().equals(isBalance)) {
                        //平账方式，撤回之前标记的已还利息、已还逾期费。改为本金为主，有额外剩余金额，分别记入利息和逾期费
                        afBorrowCashDo.setRateAmount(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), afBorrowCashDo.getRateAmount()));
                        afBorrowCashDo.setSumRate(BigDecimal.ZERO);

                        afBorrowCashDo.setOverdueAmount(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount()));
                        afBorrowCashDo.setSumOverdue(BigDecimal.ZERO);

                        if (haveRepayAmount.compareTo(afBorrowCashDo.getAmount()) > 0) {
                            //本金外额外剩余金额
                            tempRepayAmount = haveRepayAmount.subtract(afBorrowCashDo.getAmount());
                        }
                    } else {
                        //正常还款非平账方式，直接拿本次还款金额处理利息和手续费
                        tempRepayAmount = repayAmount;
                    }

                    //判断是否能还清利息 同时修改累计利息
                    if (tempRepayAmount.compareTo(afBorrowCashDo.getRateAmount()) > 0) {
                        bcashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), afBorrowCashDo.getRateAmount()));
                        bcashDo.setRateAmount(BigDecimal.ZERO);
                        tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getRateAmount());
                    } else {
                        bcashDo.setSumRate(BigDecimalUtil.add(afBorrowCashDo.getSumRate(), tempRepayAmount));
                        bcashDo.setRateAmount(afBorrowCashDo.getRateAmount().subtract(tempRepayAmount));
                        tempRepayAmount = BigDecimal.ZERO;
                    }
                    // 判断是否能还清滞纳金 同时修改累计滞纳金
                    if (tempRepayAmount.compareTo(afBorrowCashDo.getOverdueAmount()) > 0) {
                        bcashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getOverdueAmount()));
                        bcashDo.setOverdueAmount(BigDecimal.ZERO);
                        tempRepayAmount = BigDecimalUtil.subtract(tempRepayAmount, afBorrowCashDo.getOverdueAmount());
                    } else {
                        bcashDo.setSumOverdue(BigDecimalUtil.add(afBorrowCashDo.getSumOverdue(), tempRepayAmount));
                        bcashDo.setOverdueAmount(afBorrowCashDo.getOverdueAmount().subtract(tempRepayAmount));
                        tempRepayAmount = BigDecimal.ZERO;
                    }

                    if (YesNoStatus.YES.getCode().equals(isBalance) || allAmount.compareTo(haveRepayAmount) <= 0) {
                        if (YesNoStatus.YES.getCode().equals(isBalance)) {
                            logger.info("dealOfflineRepaymentSucess isBalance yes process,borrowCashId=" + afBorrowCashDo.getRid());
                        }
                        //平账处理或者非平账但是已还金额满足欠款
                        Long userId = afBorrowCashDo.getUserId();
                        AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userId);
                        //减少使用额度
                        accountInfo.setUsedAmount(BigDecimalUtil.subtract(accountInfo.getUsedAmount(), afBorrowCashDo.getAmount()));
                        afUserAccountDao.updateOriginalUserAccount(accountInfo);
                        //增加日志
                        AfUserAccountLogDo accountLog = BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.REPAYMENTCASH,
                                afBorrowCashDo.getAmount(), userId, afBorrowCashDo.getRid());
                        afUserAccountLogDao.addUserAccountLog(accountLog);

                        bcashDo.setRateAmount(BigDecimal.ZERO);
                        bcashDo.setOverdueAmount(BigDecimal.ZERO);
                        bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
                        try {
                        	boolean isBefore = DateUtil.isBefore(new Date(), DateUtil.addDays(afBorrowCashDo.getGmtPlanRepayment(), -1));
                        	if (isBefore) {
                        		if (assetSideEdspayUtil.isPush(afBorrowCashDo)) {
                        			List<ModifiedBorrowInfoVo> modifiedLoanInfo = assetSideEdspayUtil.buildModifiedInfo(afBorrowCashDo,1);
                        			boolean result = assetSideEdspayUtil.transModifiedBorrowInfo(modifiedLoanInfo,Constants.ASSET_SIDE_EDSPAY_FLAG, Constants.ASSET_SIDE_FANBEI_FLAG);
                        			if (result) {
                        				logger.info("trans modified borrowCash Info success,loanId="+afBorrowCashDo.getRid());
                        			}else{
                        				assetSideEdspayUtil.transFailRecord(afBorrowCashDo, modifiedLoanInfo);
                        			}
                        		}
                        	}
						} catch (Exception e) {
							logger.error("preFinishNotifyEds error="+e);
						}
                    }
                    afBorrowCashService.updateBorrowCash(bcashDo);

                    return FanbeiThirdRespCode.SUCCESS.getCode();
                } catch (Exception e) {
                    status.setRollbackOnly();
                    logger.info("dealOfflineRepaymentSucess error", e);
                    return FanbeiThirdRespCode.FAILED.getCode();
                }
            }
        });

        if(ret.equals(FanbeiThirdRespCode.SUCCESS.getCode())){
            CuiShouUtils.setAfRepaymentBorrowCashDo(repayment);
            cuiShouUtils.syncCuiShou(repayment);  //新催收线下还款
        }
        
        //---------------------------------begin----------------------------------------------
        //马甲包老借款同步数据   会对逾期的借款还款，向催收平台同步还款信息
        logger.info("向催收平台同步还款信息失败,borrowNo= "+afBorrowCashDo.getBorrowNo());
        Boolean isCashOverdueOld = false;
        try{
            Date gmtPlanTime = afBorrowCashDo.getGmtPlanRepayment();
            gmtPlanTime = DateUtil.parseDate(DateUtil.formatDate(gmtPlanTime));
            Date newDate = new Date();
            newDate = DateUtil.parseDate(DateUtil.formatDate(newDate));
            if (StringUtils.equals("代扣付款",repayment.getName())&&gmtPlanTime.getTime() < newDate.getTime()) {
                isCashOverdueOld = true;
            }
        }catch(Exception ex){
            logger.info("dealRepaymentSucess isCashOverdue error", ex);
        }
        AfBorrowCashDo currAfBorrowCashDo = afBorrowCashDo;
        try {
        	if (DateUtil.compareDate(new Date(), afBorrowCashDo.getGmtPlanRepayment())) {
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
                        currAfBorrowCashDo.getRateAmount(),isCashOverdueOld);
	                logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
	        } else {
	            logger.info("collection consumerRepayment not push,borrowCashId=" + currAfBorrowCashDo.getRid());
	        }
        } catch (Exception e) {
        	logger.error("向催收平台同步还款信息失败,borrowNo= "+currAfBorrowCashDo.getBorrowNo(), e);
        }
        //---------------------------------end----------------------------------------------
        return ret;
    }

    @Override
    public int updateRepaymentBorrowCashName(Long refId) {
        return afRepaymentBorrowCashDao.updateRepaymentBorrowCashName(refId);
    }

    @Override
    public AfRepaymentBorrowCashDo getRepaymentBorrowCashByTradeNo(Long borrowCashId, String tradeNo) {
        return afRepaymentBorrowCashDao.getRepaymentBorrowCashByTradeNo(borrowCashId, tradeNo);
    }

    @Override
    public String getProcessingRepayNo(Long userId) {
        return afRepaymentBorrowCashDao.getProcessingRepayNo(userId);
    }

}
