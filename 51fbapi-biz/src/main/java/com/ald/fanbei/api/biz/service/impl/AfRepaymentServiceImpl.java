package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.pay.ThirdPayUtility;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.fenqicuishou.FenqiCuishouUtil;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.RepaymentStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author hexin 2017年2月22日下午14:48:49
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRepaymentService")
public class AfRepaymentServiceImpl extends BaseService implements AfRepaymentService {

    @Resource
    GeneratorClusterNo generatorClusterNo;

    @Resource
    AfRepaymentDao afRepaymentDao;

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    AfBorrowService afBorrowService;

    @Resource
    AfUserAccountDao afUserAccountDao;

    @Resource
    AfUserAccountLogDao afUserAccountLogDao;

    @Resource
    AfUserCouponDao afUserCouponDao;

    @Resource
    private JpushService pushService;

    @Resource
    private AfUserService afUserService;

    @Resource
    private AfUserBankcardDao afUserBankcardDao;

    @Resource
    AfUserBankcardService afUserBankcardService;

    @Resource
    AfOrderDao afOrderDao;

    @Resource
    UpsUtil upsUtil;
    @Resource
    RiskUtil riskUtil;

    @Resource
    YiBaoUtility yiBaoUtility;
    @Resource
    AfYibaoOrderDao afYibaoOrderDao;

    @Resource
    RedisTemplate redisTemplate;

    @Resource
    AfRepaymentDetalDao afRepaymentDetalDao;

    @Resource
    ThirdPayUtility thirdPayUtility;


    @Resource
    AfBorrowBillDao afBorrowBillDao;
    @Resource
    SmsUtil smsUtil;
    @Resource
    FenqiCuishouUtil fenqiCuishouUtil;

    @Resource
    AfUserAmountDetailDao afUserAmountDetailDao;
    @Resource
    AfUserAmountLogDao afUserAmountLogDao;
    @Resource
    AfUserAmountDao afUserAmountDao;

    @Resource
    AfUserAmountService afUserAmountService;

    @Resource
    AfInterimAuDao afInterimAuDao;
    @Resource
    AfInterimDetailDao afInterimDetailDao;

    public void testbackDetail() {
        AfRepaymentDo afRepaymentDo = afRepaymentDao.getRepaymentById(94901l);
        afUserAmountService.addUseAmountDetail(afRepaymentDo);
        afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.PROCESS, afRepaymentDo);
        afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.SUCCESS, afRepaymentDo);
    }


    @Resource
    private AfTradeCodeInfoService afTradeCodeInfoService;

    public Map<String, Object> createRepaymentYiBao(final BigDecimal jfbAmount, BigDecimal repaymentAmount,
                                                    final BigDecimal actualAmount, AfUserCouponDto coupon,
                                                    BigDecimal rebateAmount, String billIds, final Long cardId, final Long userId, final AfBorrowBillDo billDo, final String clientIp,
                                                    final AfUserAccountDo afUserAccountDo) {
        Date now = new Date();
        String repayNo = generatorClusterNo.getRepaymentNo(now);
        final String payTradeNo = repayNo;
        //新增还款记录
        String name = Constants.DEFAULT_REPAYMENT_NAME + billDo.getName();
        if (billDo.getCount() > 1) {
            name = new StringBuffer(Constants.DEFAULT_REPAYMENT_NAME).append(billDo.getBillYear() + "").append("年")
                    .append(billDo.getBillMonth()).append("月账单").toString();
        } else if (BorrowType.CASH.getCode().equals(billDo.getType())) {
            name += billDo.getBorrowNo();
        }
        final AfRepaymentDo repayment = buildRepayment(jfbAmount, repaymentAmount, repayNo, now, actualAmount, coupon,
                rebateAmount, billIds, cardId, payTradeNo, name, userId);
        Map<String, Object> map = new HashMap<String, Object>();
        List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(billIds.split(","), new Converter<String, Long>() {
            @Override
            public Long convert(String source) {
                return Long.parseLong(source);
            }
        });
        if (cardId == -1 || cardId == -3) {//微信支付 或 支付宝
            Map<String, String> map1;
            if (cardId == -1) {
                map1 = thirdPayUtility.createOrder(actualAmount, payTradeNo, userId, ThirdPayTypeEnum.WXPAY, PayOrderSource.REPAYMENT);
            } else {
                map1 = thirdPayUtility.createOrder(actualAmount, payTradeNo, userId, ThirdPayTypeEnum.ZFBPAY, PayOrderSource.REPAYMENT);
            }
            for (String key : map1.keySet()) {
                map.put(key, map1.get(key));
            }
            afRepaymentDao.addRepayment(repayment);
            afUserAmountService.addUseAmountDetail(repayment);
            afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.PROCESS, repayment);
        } else if (cardId > 0) {//银行卡支付
            AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
            repayment.setStatus(RepaymentStatus.PROCESS.getCode());
            afRepaymentDao.addRepayment(repayment);
            afUserAmountService.addUseAmountDetail(repayment);
            afBorrowBillService.updateBorrowBillStatusByBillIdsAndStatus(billIdList, BorrowBillStatus.DEALING.getCode());
            UpsCollectRespBo respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(),
                    bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(),
                    Constants.DEFAULT_PAY_PURPOSE, name, "02", UserAccountLogType.REPAYMENT.getCode());

            afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.PROCESS, repayment);
            if (!respBo.isSuccess()) {
                afBorrowBillService.updateBorrowBillStatusByBillIdsAndStatus(billIdList, BorrowBillStatus.NO.getCode());
                afRepaymentDao.updateRepayment(RepaymentStatus.FAIL.getCode(), null, repayment.getRid());
                afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.FAIL, repayment);
                try {
                    //还款失败短信通知
                    sendFailMessage(userId, afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode()), repayment.getName());
                } catch (Exception e) {
                    logger.error("BorrowCash sendMessage but addMsg error for:" + e);
                }
                throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
            }
            map.put("resp", respBo);
        } else if (cardId == -2) {//余额支付
            afRepaymentDao.addRepayment(repayment);
            afUserAmountService.addUseAmountDetail(repayment);
            afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.PROCESS, repayment);
            dealRepaymentSucess(repayment.getPayTradeNo(), "", true);
        }
        map.put("refId", repayment.getRid());
        map.put("type", UserAccountLogType.REPAYMENT.getCode());
        return map;

    }


    @Override
    public Map<String, Object> createRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount,
                                               final BigDecimal actualAmount, AfUserCouponDto coupon,
                                               BigDecimal rebateAmount, String billIds, final Long cardId, final Long userId, final AfBorrowBillDo billDo, final String clientIp,
                                               final AfUserAccountDo afUserAccountDo) {
        Date now = new Date();
        String repayNo = generatorClusterNo.getRepaymentNo(now);
        final String payTradeNo = repayNo;

        //新增还款记录
        String name = Constants.DEFAULT_REPAYMENT_NAME + billDo.getName();
        if (billDo.getCount() > 1) {
            name = new StringBuffer(Constants.DEFAULT_REPAYMENT_NAME).append(billDo.getBillYear() + "").append("年")
                    .append(billDo.getBillMonth()).append("月账单").toString();
        } else if (BorrowType.CASH.getCode().equals(billDo.getType())) {
            name += billDo.getBorrowNo();
        }
        if (StringUtil.equals("sysJob", clientIp)) {
            name = "代扣付款";
        }
        final AfRepaymentDo repayment = buildRepayment(jfbAmount, repaymentAmount, repayNo, now, actualAmount, coupon,
                rebateAmount, billIds, cardId, payTradeNo, name, userId);
        Map<String, Object> map = new HashMap<String, Object>();
        List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(billIds.split(","), new Converter<String, Long>() {
            @Override
            public Long convert(String source) {
                return Long.parseLong(source);
            }
        });


        if (cardId == -1) {//微信支付
            afRepaymentDao.addRepayment(repayment);
            //修改账单状态
            map = UpsUtil.buildWxpayTradeOrderRepayment(payTradeNo, userId, name, actualAmount, PayOrderSource.REPAYMENT.getCode(), true);
        } else if (cardId > 0) {//银行卡支付
            AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
            repayment.setStatus(RepaymentStatus.PROCESS.getCode());
            afRepaymentDao.addRepayment(repayment);
            afUserAmountService.addUseAmountDetail(repayment);
            afBorrowBillService.updateBorrowBillStatusByBillIdsAndStatus(billIdList, BorrowBillStatus.DEALING.getCode());
            UpsCollectRespBo respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", afUserAccountDo.getRealName(), bank.getMobile(),
                    bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(),
                    Constants.DEFAULT_PAY_PURPOSE, name, "02", UserAccountLogType.REPAYMENT.getCode());

            afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.PROCESS, repayment);
            if (!respBo.isSuccess()) {
                AfRepaymentDo currRepayment = afRepaymentDao.getRepaymentById(repayment.getRid());
                if (!RepaymentStatus.YES.getCode().equals(currRepayment.getStatus())) {
                    afBorrowBillService.updateBorrowBillStatusByBillIdsAndStatus(billIdList, BorrowBillStatus.NO.getCode());
                    afRepaymentDao.updateRepayment(RepaymentStatus.FAIL.getCode(), null, repayment.getRid());
                    afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.FAIL, repayment);
                } else {
                    logger.info("createRepayment ups response fail,bug syn have process success.repayNo:" + repayNo + ",repaymentId:" + repayment.getRid());
                }
                try {
                    //还款失败短信通知
                    sendFailMessage(userId, afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode()), currRepayment.getName());
                } catch (Exception e) {
                    logger.error("BorrowCash sendMessage but addMsg error for:" + e);
                }
                throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
            }
            map.put("resp", respBo);
        } else if (cardId == -2) {//余额支付
            afRepaymentDao.addRepayment(repayment);
            //addRepaymentyDetail(totalAmount,repaymentAmount,repayment.getRid());
            repayment.setStatus(RepaymentStatus.PROCESS.getCode());
            afUserAmountService.addUseAmountDetail(repayment);
            afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.PROCESS, repayment);
            try {
                if (StringUtil.equals("sysJob", clientIp)) {
                    //处理代扣短信
                    //AfUserDo afUserDo = afUserService.getUserById(userId);
                    sendSuccessMessage(userId, payTradeNo);
                }
            } catch (Exception e) {
                logger.error("BorrowCash sendMessage error for:" + e);
            }
            dealRepaymentSucess(repayment.getPayTradeNo(), "", true);
        }
        map.put("refId", repayment.getRid());
        map.put("type", UserAccountLogType.REPAYMENT.getCode());
        return map;
    }

    /**
     * 消费分期还款失败短信通知
     */
    private boolean sendFailMessage(Long userId, String errorMsg, String payType) {
        AfUserDo afUserDo = afUserService.getUserById(userId);
        int errorTimes = 0;
        //模版数据map处理
        Map<String, String> replaceMapData = new HashMap<String, String>();
        replaceMapData.put("errorMsg", errorMsg);
        try {
            if (StringUtil.isNotBlank(payType) && payType.indexOf("代扣") > -1) {
                //代扣还款失败短信通知
                smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWBILL_WITHHOLD_FAIL.getCode());
            } else {
                errorTimes = afRepaymentDao.getCurrDayRepayErrorTimes(userId);
                //用户手动还款失败短信通知
                smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWBILL_FAIL.getCode());
            }
        } catch (Exception e) {
            logger.error("sendRepaymentBorrowBillFail exception:" + e);
        }
        return true;
    }

    /**
     * 消费分期还款成功短信通知
     */
    private boolean sendSuccessMessage(Long userId, String payType) {
        AfUserDo afUserDo = afUserService.getUserById(userId);
        //模版数据map处理
        Date date = DateUtil.addMonths(new Date(), -1);
        String month = DateUtil.getMonth(date);
        Map<String, String> replaceMapData = new HashMap<String, String>();
        replaceMapData.put("month", month);
        try {
            if (StringUtil.isNotBlank(payType) && payType.indexOf("代扣") > -1) {
                //自动代扣还款成功短信通知
                return smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWBILL_WITHHOLD_SUCCESS.getCode());
            }
        } catch (Exception e) {
            logger.error("sendRepaymentBorrowBillSuccess exception:", e);
        }
        return false;
    }

    private void addRepaymentyDetail(BigDecimal totalAmount, BigDecimal actualAmount, Long refId) {
        // 返写到返现里的钱
        if (totalAmount.compareTo(actualAmount) > 0) {
            BigDecimal bd = totalAmount.subtract(actualAmount);
            AfRepaymentDetalDo afRepaymentDetalDo = new AfRepaymentDetalDo();
            afRepaymentDetalDo.setRepaymentId(refId);
            afRepaymentDetalDo.setTotalAmount(totalAmount);
            afRepaymentDetalDo.setAmount(bd);
            afRepaymentDetalDao.addRepaymentDetal(afRepaymentDetalDo);
        }
    }


    @Override
    public String getCurrentLastRepayNo(String orderNoPre) {
        return afRepaymentDao.getCurrentLastRepayNo(orderNoPre);
    }

    private AfRepaymentDo buildRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, String repayNo, Date gmtCreate, BigDecimal actualAmount,
                                         AfUserCouponDto coupon, BigDecimal rebateAmount, String billIds, Long cardId, String payTradeNo, String name, Long userId) {
        AfRepaymentDo repay = new AfRepaymentDo();
        repay.setActualAmount(actualAmount);
        repay.setBillIds(billIds);
        repay.setPayTradeNo(payTradeNo);
        repay.setRebateAmount(rebateAmount);
        repay.setRepaymentAmount(repaymentAmount);
        repay.setRepayNo(repayNo);
        repay.setGmtCreate(gmtCreate);
        repay.setJfbAmount(jfbAmount);
        repay.setStatus(RepaymentStatus.NEW.getCode());
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
        } else {
            AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
            repay.setCardNumber(bank.getCardNumber());
            repay.setCardName(bank.getBankName());
        }
        return repay;
    }

    private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type, BigDecimal amount, Long userId, Long repaymentId) {
        //增加account变更日志
        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setAmount(amount);
        accountLog.setUserId(userId);
        accountLog.setRefId(repaymentId + "");
        accountLog.setType(type.getCode());
        return accountLog;
    }

    @Override
    public AfRepaymentDo getRepaymentById(Long rid) {
        return afRepaymentDao.getRepaymentById(rid);
    }

    @Override
    public long dealRepaymentSucess(final String outTradeNo, final String tradeNo, boolean isNeedNoticeMsg) {

        final String key = outTradeNo + "_success_repay";
        long count = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 30, TimeUnit.SECONDS);
        if (count != 1) {
            return -1;
        }

        final AfRepaymentDo repayment = afRepaymentDao.getRepaymentByPayTradeNo(outTradeNo);

        Long result = transactionTemplate.execute(new TransactionCallback<Long>() {

            @Override
            public Long doInTransaction(TransactionStatus status) {
                try {

//					AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
//					if(afYibaoOrderDo !=null){
//						if(afYibaoOrderDo.getStatus().intValue() == 1){
//							return 1L;
//						}
//						else{
//							afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(),1);
//						}
//					}
                    if (thirdPayUtility.checkSuccess(outTradeNo)) {
                        return 1L;
                    }

                    logger.info("updateBorrowBillStatusByIds repayment  = {}", repayment);
                    if (YesNoStatus.YES.getCode().equals(repayment.getStatus())) {
                        return 0l;
                    }
                    // 变更还款记录为已还款
                    afRepaymentDao.updateRepayment(RepaymentStatus.YES.getCode(), tradeNo, repayment.getRid());
                    AfBorrowBillDo billDo = afBorrowBillService.getBillAmountByIds(repayment.getBillIds());
                    AfUserDo userDo = afUserService.getUserById(repayment.getUserId());
                    // 变更账单 借款表状态
                    afBorrowBillService.updateBorrowBillStatusByIds(repayment.getBillIds(), BorrowBillStatus.YES.getCode(), repayment.getRid(),
                            repayment.getCouponAmount(), repayment.getJfbAmount(), repayment.getRebateAmount());
                    // 判断该期是否还清，如已还清，更新total_bill 状态
                    int count = afBorrowBillService.getUserMonthlyBillNotpayCount(billDo.getBillYear(), billDo.getBillMonth(), userDo.getRid());
                    if (count == 0) {
                        afBorrowBillService.updateTotalBillStatus(billDo.getBillYear(), billDo.getBillMonth(), userDo.getRid(), BorrowBillStatus.YES.getCode());
                        pushService.repayBillSuccess(userDo.getUserName(), billDo.getBillYear() + "", String.format("%02d", billDo.getBillMonth()));


                    } else {
                        afBorrowBillService.updateTotalBillStatus(billDo.getBillYear(), billDo.getBillMonth(), userDo.getRid(), BorrowBillStatus.PART.getCode());
                    }

//					dealWithRaiseAmount(repayment.getBillIds());
                    // 优惠券设置已使用
                    afUserCouponDao.updateUserCouponSatusUsedById(repayment.getUserCouponId());
                    // 获取现金借款还款本金
                    AfBorrowBillDo cashBill = afBorrowBillService.getBillAmountByCashIds(repayment.getBillIds());
                    BigDecimal cashAmount = cashBill == null ? BigDecimal.ZERO : cashBill.getPrincipleAmount();
                    // 授权账户可用金额变更
                    AfUserAccountDo account = new AfUserAccountDo();
                    account.setUserId(repayment.getUserId());
                    logger.info("repayment=" + repayment);
                    account.setJfbAmount(repayment.getJfbAmount().multiply(new BigDecimal(-1)));

                    account.setUcAmount(cashAmount.multiply(new BigDecimal(-1)));

                    AfBorrowBillDo houseBill = afBorrowBillDao.getBillHouseAmountByIds(StringUtil.splitToList(repayment.getBillIds(), ","));
                    BigDecimal houseAmount = houseBill == null ? BigDecimal.ZERO : houseBill.getPrincipleAmount();
                    BigDecimal backAmount = billDo.getPrincipleAmount().subtract(houseAmount);
                    //获取临时额度
                    AfInterimAuDo afInterimAuDo = afInterimAuDao.getByUserId(repayment.getUserId());
                    if (afInterimAuDo == null) {
                        afInterimAuDo = new AfInterimAuDo();
                        afInterimAuDo.setInterimAmount(new BigDecimal(0));
                        afInterimAuDo.setInterimUsed(new BigDecimal(0));
                    }
                    //判断临时额度是否使用
                    if (afInterimAuDo.getInterimUsed().compareTo(BigDecimal.ZERO) == 1) {
                        //还款金额是否大于使用的临时额度
                        BigDecimal backInterim = BigDecimal.ZERO;
                        if (afInterimAuDo.getInterimUsed().compareTo(backAmount) >= 0) {
                            //还临时额度
                            backInterim = backAmount;
                            afInterimAuDao.updateInterimUsed(repayment.getUserId(), backInterim.multiply(new BigDecimal(-1)));
                        } else {
                            //先还临时额度再还使用额度
                            backInterim = afInterimAuDo.getInterimUsed();
                            afInterimAuDao.updateInterimUsed(repayment.getUserId(), backInterim.multiply(new BigDecimal(-1)));
                            account.setUsedAmount(backAmount.subtract(backInterim).multiply(new BigDecimal(-1)));
                        }
                        //增加临时额度使用记录
                        AfInterimDetailDo afInterimDetailDo = new AfInterimDetailDo();
                        afInterimDetailDo.setAmount(backInterim);
                        afInterimDetailDo.setInterimUsed(afInterimAuDo.getInterimUsed().subtract(backInterim));
                        afInterimDetailDo.setType(2);
                        afInterimDetailDo.setOrderId(new Long(0));
                        afInterimDetailDo.setUserId(repayment.getUserId());
                        afInterimDetailDao.addAfInterimDetail(afInterimDetailDo);
                    } else {
                        account.setUsedAmount(backAmount.multiply(new BigDecimal(-1)));
                    }
//					account.setUsedAmount(billDo.getPrincipleAmount().multiply(new BigDecimal(-1)));
                    account.setRebateAmount(repayment.getRebateAmount().multiply(new BigDecimal(-1)));
                    logger.info("account=" + account);
                    int result = afUserAccountDao.updateUserAccount(account);
                    if (result <= 0) {
                        logger.info("update account error,details:repayNo" + repayment.getRepayNo(), JSON.toJSONString(account));
                        //throw new Exception("update account error,details");
                    }
//					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENT, billDo.getPrincipleAmount(), repayment.getUserId(), repayment.getRid()));
                    afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENT, backAmount, repayment.getUserId(), repayment.getRid()));
                    dealWithRaiseAmount(repayment.getUserId(), repayment.getBillIds());

                    //还款成功同步逾期订单
                    dealWithSynchronizeOverdueOrder(repayment.getUserId(), repayment.getBillIds());

//					AfRepaymentDetalDo afRepaymentDetalDo = afRepaymentDetalDao.getRepaymentDetalByTypeAndId(repayment.getRid(),1);
//					if(afRepaymentDetalDo !=null){
//						//回写返利
//						AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
//						afUserAccountDo.setRebateAmount(afRepaymentDetalDo.getAmount());
//						afUserAccountDo.setUserId(repayment.getUserId());
//						afUserAccountDao.updateRebateAmount(afUserAccountDo);
//						afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENT_OUT, afRepaymentDetalDo.getAmount(), repayment.getUserId(), repayment.getRid()));
//					}


                    afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.SUCCESS, repayment);
                    fenqiCuishouUtil.postReapymentMoney(repayment.getRid());
                    return 1l;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    logger.info("dealRepaymentSucess error = {}", e);
                    return 0l;
                } finally {
                    redisTemplate.delete(key);
                }
            }
        });

        if (result == 1 && isNeedNoticeMsg) {
            //回调成功发送还款成功短信
            sendSuccessMessage(repayment.getUserId(), repayment.getName());
        }

        return result;
    }

    /**
     * 处理风控传输数据和提额逻辑
     *
     * @param billIds
     */
    private void dealWithRaiseAmount(Long userId, String billIds) {
        logger.info("dealWithRaiseAmount begin , dealWithRaiseAmount = ");
        if (StringUtils.isBlank(billIds)) {
            return;
        }
        AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
        String cardNo = StringUtils.EMPTY;
        if (card != null) {
            cardNo = card.getCardNumber();
        } else {
            cardNo = System.currentTimeMillis() + StringUtils.EMPTY;
        }

        JSONArray details = new JSONArray();
        String tranOrderNo = riskUtil.getOrderNo("tran", cardNo.substring(cardNo.length() - 4, cardNo.length()));

        String[] billIdArray = billIds.split(",");
        List<Long> borrowIds = new ArrayList<>();
        for (String billId : billIdArray) {
            AfBorrowBillDo billDo = afBorrowBillService.getBorrowBillById(Long.parseLong(billId));
            AfBorrowDo afBorrow = afBorrowService.getBorrowById(billDo.getBorrowId());

            JSONObject obj = new JSONObject();
            obj.put("borrowNo", afBorrow.getBorrowNo());
            obj.put("amount", afBorrow.getAmount());
            obj.put("repayment", billDo.getBillAmount());
            obj.put("income", billDo.getPoundageAmount());
            obj.put("interest", billDo.getInterestAmount());
            obj.put("overdueAmount", BigDecimalUtil.add(billDo.getOverdueInterestAmount(), billDo.getOverduePoundageAmount()));
            obj.put("overdueDay", billDo.getOverdueDays());
            details.add(obj);

            //还完该借款的所有期数
            if (afBorrow.getNper().equals(afBorrow.getNperRepayment())) {

                BigDecimal income = afBorrowBillService.getSumIncomeByBorrowId(billDo.getBorrowId());
                Long sumOverdueDay = afBorrowBillService.getSumOverdueDayByBorrowId(billDo.getBorrowId());
                int overdueCount = afBorrowBillService.getSumOverdueCountByBorrowId(billDo.getBorrowId());
//				int borrowCount = afBorrowService.getBorrowNumByUserId(billDo.getUserId());
                if (!borrowIds.contains(afBorrow.getRid())) {
                    logger.info("call raiseQuota first：" + afBorrow.getRid());
                    borrowIds.add(afBorrow.getRid());
                    String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                    try {
                        riskUtil.raiseQuota(afBorrow.getUserId().toString(), afBorrow.getBorrowNo(), "40", riskOrderNo, afBorrow.getAmount(), income, sumOverdueDay, overdueCount);
                    } catch (Exception e) {
                        logger.error("风控提额失败", e);
                    }
                } else {
                    logger.info("call raiseQuota more then once：" + afBorrow.getRid());
                }

                afBorrowService.updateBorrowStatus(afBorrow.getRid(), BorrowStatus.FINISH.getCode());

            }
        }

        try {
            riskUtil.transferBorrowInfo(userId.toString(), "60", tranOrderNo, details);
        } catch (Exception e) {
            logger.error("还款时给风控传输数据出错", e);
        }
    }

    /**
     * 同步风控订单
     *
     * @param userId
     * @param billIds
     */
    private void dealWithSynchronizeOverdueOrder(Long userId, String billIds) {
        logger.info("dealWithSynchronizeOverdueOrder userId = {}, billIds ={} ", userId, billIds);
        if (StringUtils.isBlank(billIds)) {
            return;
        }
        AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
        String cardNo = StringUtils.EMPTY;
        if (card != null) {
            cardNo = card.getCardNumber();
        } else {
            cardNo = System.currentTimeMillis() + StringUtils.EMPTY;
        }
        String orderNo = riskUtil.getOrderNo("over", cardNo.substring(cardNo.length() - 4, cardNo.length()));
        List<RiskOverdueBorrowBo> boList = new ArrayList<RiskOverdueBorrowBo>();
        String[] billIdArray = billIds.split(",");
        for (String billId : billIdArray) {
            AfBorrowBillDo borrowBillInfo = afBorrowBillService.getBorrowBillById(Long.parseLong(billId));
            AfBorrowDo borrowInfo = afBorrowService.getBorrowInfoByBorrowNo(borrowBillInfo.getBorrowNo());
            //查询需要批量更新的账单
            AfBorrowBillDo billDo = afBorrowBillService.getOverduedAndNotRepayBill(borrowBillInfo.getBorrowId(), Long.parseLong(billId));
            try {
                //如果为空 则代表没有其余的逾期订单，否则还有其余逾期订单
                if (billDo == null) {
                    boList.add(parseOverduedBorrowBo(borrowBillInfo.getBorrowNo(), 0, borrowInfo.getOverdueNum()));
                } else {
                    boList.add(parseOverduedBorrowBo(billDo.getBorrowNo(), billDo.getOverdueDays(), billDo.getNper()));
                }
            } catch (Exception e) {
                logger.error("同步逾期订单失败", e);
            }
        }
        riskUtil.batchSychronizeOverdueBorrow(orderNo, boList);
    }

    private RiskOverdueBorrowBo parseOverduedBorrowBo(String borrowNo, Integer overdueDay, Integer overduetimes) {
        RiskOverdueBorrowBo bo = new RiskOverdueBorrowBo();
        bo.setBorrowNo(borrowNo);
        bo.setOverdueDays(overdueDay);
        bo.setOverdueTimes(overduetimes);
        return bo;
    }

    @Override
    public int dealRepaymentFail(final String outTradeNo, final String tradeNo, boolean isNeedNoticeMsg, String errorWarnMsg) {
        final AfRepaymentDo repayment = afRepaymentDao.getRepaymentByPayTradeNo(outTradeNo);

        Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus status) {
                try {
                    logger.info("dealRepaymentFail repayment  = {}", repayment);
                    if (repayment == null) {
                        return 0;
                    }

//					AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
//					if(afYibaoOrderDo !=null) {
//						if (afYibaoOrderDo.getStatus().intValue() == 1) {
//							return 1;
//						} else {
//							afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 2);
//						}
//					}
                    if (thirdPayUtility.checkFail(outTradeNo)) {
                        return 1;
                    }

                    if (YesNoStatus.YES.getCode().equals(repayment.getStatus()) || YesNoStatus.NO.getCode().equals(repayment.getStatus())) {
                        return 0;
                    }

                    // 账单字符串转成账单集合
                    String billIds = repayment.getBillIds();
                    List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(billIds.split(","), new Converter<String, Long>() {
                        @Override
                        public Long convert(String source) {
                            return Long.parseLong(source);
                        }
                    });
                    // 变更账单状态
                    afBorrowBillService.updateBorrowBillStatusByBillIdsAndStatus(billIdList, BorrowBillStatus.NO.getCode());

                    // 变更还款记录未还款状态
                    afRepaymentDao.updateRepayment(RepaymentStatus.FAIL.getCode(), tradeNo, repayment.getRid());

                    afUserAmountService.updateUserAmount(AfUserAmountProcessStatus.FAIL, repayment);

                    return 1;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    logger.info("dealRepaymentFail error = {}", e);
                    return 0;
                }
            }
        });

        if (result == 1 && isNeedNoticeMsg) {
            //还款失败短信通知
            sendFailMessage(repayment.getUserId(), errorWarnMsg, repayment.getName());
        }
        return result;
    }

    @Override
    public int dealSelfSupportOrBoluomeFail(final String outTradeNo, final String tradeNo) {

        try {
            // 根据付款编号，找到订单
            AfOrderDo orderDo = afOrderDao.getOrderInfoByPayOrderNo(outTradeNo);
            if (orderDo == null) {
                return 0;
            }

            logger.info("dealSelfSupportOrBoluomeFail orderDo  = {}", orderDo);
            if (YesNoStatus.YES.getCode().equals(orderDo.getPayStatus()) || YesNoStatus.NO.getCode().equals(orderDo.getPayStatus()) || OrderStatus.CLOSED.getCode().equals(orderDo.getStatus()) || OrderStatus.FINISHED.getCode().equals(orderDo.getStatus())) {
                return 0;
            }

            orderDo.setGmtModified(new Date());
            orderDo.setPayStatus(PayStatus.NOTPAY.getCode());
            orderDo.setTradeNo(tradeNo);
            orderDo.setStatus(OrderStatus.NEW.getCode());
            // 变更还款记录未还款状态
            afOrderDao.updateOrder(orderDo);

            return 1;
        } catch (Exception e) {

            logger.info("dealRepaymentFail error = {}", e);
            return 0;
        }


    }

    @Override
    public int updateRepaymentName(Long refId) {
        return afRepaymentDao.updateRepaymentName(refId);
    }

}
