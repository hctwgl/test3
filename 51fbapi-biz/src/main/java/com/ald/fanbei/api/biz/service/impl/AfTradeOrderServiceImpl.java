package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.InterestFreeJsonBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.AfTradeOrderService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderDto;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderStatisticsDto;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 商圈订单扩展表ServiceImpl
 *
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afTradeOrderService")
public class AfTradeOrderServiceImpl extends ParentServiceImpl<AfTradeOrderDo, Long> implements AfTradeOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AfTradeOrderServiceImpl.class);

    @Resource
    private AfTradeOrderDao afTradeOrderDao;
    @Resource
    private AfTradeBusinessInfoDao afTradeBusinessInfoDao;
    @Resource
    private AfTradeWithdrawRecordDao afTradeWithdrawRecordDao;
    @Resource
    private AfTradeWithdrawDetailDao afTradeWithdrawDetailDao;
    @Resource
    private UpsUtil upsUtil;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    private AfOrderDao afOrderDao;
    @Resource
    private AfBorrowDao afBorrowDao;
    @Resource
    private AfResourceDao afResourceDao;
    @Resource
    private AfBorrowBillDao afBorrowBillDao;
    @Resource
    private AfRepaymentDao afRepaymentDao;
    @Resource
    private AfUserBankcardDao afUserBankcardDao;
    @Resource
    private AfUserAccountDao afUserAccountDao;
    @Resource
    private AfOrderRefundDao afOrderRefundDao;
    @Resource
    private AfUserAccountLogDao afUserAccountLogDao;

    @Override
    public BaseDao<AfTradeOrderDo, Long> getDao() {
        return afTradeOrderDao;
    }

    @Override
    public BigDecimal getCanWithDrawMoney(Long businessId) {
        AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
        Date now = new Date();
        Date canWithDrawDate;
        if (now.compareTo(DateUtil.getWithDrawOfDate(now)) > 0) {
            canWithDrawDate = DateUtil.addDays(DateUtil.getWithDrawOfDate(now), 0 - infoDo.getWithdrawCycle());
        } else {
            canWithDrawDate = DateUtil.addDays(DateUtil.getWithDrawOfDate(now), 0 - infoDo.getWithdrawCycle() - 1);
        }
        return afTradeOrderDao.getCanWithDrawMoney(businessId, canWithDrawDate);
    }

    @Override
    public BigDecimal getCannotWithDrawMoney(Long businessId) {
        AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
        Date now = new Date();
        Date cannotWithDrawDate;
        if (now.compareTo(DateUtil.getWithDrawOfDate(now)) > 0) {
            cannotWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(now), 0 - infoDo.getWithdrawCycle());
        } else {
            cannotWithDrawDate = DateUtil.addDays(DateUtil.getEndOfDate(now), 0 - infoDo.getWithdrawCycle() - 1);
        }

        return afTradeOrderDao.getCannotWithDrawMoney(businessId, cannotWithDrawDate);
    }

    @Override
    public AfTradeOrderStatisticsDto payOrderInfo(Long businessId, Date startDate, Date endDate) {
        return afTradeOrderDao.payOrderInfo(businessId, startDate, endDate);
    }

    @Override
    public List<String> orderGridDate(Long businessId, Date startOfDate, Date endOfDate, String orderStatus) {
        return afTradeOrderDao.orderGridDate(businessId, startOfDate, endOfDate, orderStatus);
    }

    @Override
    public List<AfTradeOrderDto> orderGrid(Long businessId, Date startOfDate, Date endOfDate, String orderStatus) {
        return afTradeOrderDao.orderGrid(businessId, startOfDate, endOfDate, orderStatus);
    }

    @Override
    public List<String> refundGridDate(Long businessId, Date startOfDate, Date endOfDate, String refundStatus) {
        return afTradeOrderDao.refundGridDate(businessId, startOfDate, endOfDate, refundStatus);
    }

    @Override
    public List<AfTradeOrderDto> refundGrid(Long businessId, Date startDate, Date endDate, String refundStatus) {
        return afTradeOrderDao.refundGrid(businessId, startDate, endDate, refundStatus);
    }

    @Override
    public boolean withdraw(Long businessId) {
        try {
            AfTradeBusinessInfoDo infoDo = afTradeBusinessInfoDao.getByBusinessId(businessId);
            Date now = new Date();
            //改为13点可以提现，前一天13点之前的
            Date canWithDrawDate;
            if (now.compareTo(DateUtil.getWithDrawOfDate(now)) > 0) {
                canWithDrawDate = DateUtil.addDays(DateUtil.getWithDrawOfDate(now), 0 - infoDo.getWithdrawCycle());
            } else {
                canWithDrawDate = DateUtil.addDays(DateUtil.getWithDrawOfDate(now), 0 - infoDo.getWithdrawCycle() - 1);
            }
            List<AfTradeOrderDto> orderList = afTradeOrderDao.getCanWithDrawList(businessId, canWithDrawDate);
            List<Long> ids = new ArrayList<>();
            BigDecimal withDrawMoney = BigDecimal.ZERO;
            for (AfTradeOrderDto order : orderList) {
                ids.add(order.getId());
                withDrawMoney = withDrawMoney.add(order.getActualAmount());
            }
            afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.EXTRACTING.getCode());

            AfTradeWithdrawRecordDo recordDo = new AfTradeWithdrawRecordDo();
            recordDo.setBusinessId(businessId);
            recordDo.setAmount(withDrawMoney);
            recordDo.setCardCode(infoDo.getCardCode());
            recordDo.setCardName(infoDo.getCardBank());
            recordDo.setCardNumber(infoDo.getCardNo());
            recordDo.setStatus(TradeWithdrawRecordStatus.TRANSEDING.getCode());
            recordDo.setType("CASH");
            recordDo.setCardUserName(infoDo.getCardName());
            afTradeWithdrawRecordDao.saveRecord(recordDo);

            for (AfTradeOrderDto order : orderList) {
                AfTradeWithdrawDetailDo detailDo = new AfTradeWithdrawDetailDo();
                detailDo.setOrderId(order.getId());
                detailDo.setRecordId(recordDo.getId());
                afTradeWithdrawDetailDao.saveRecord(detailDo);
            }

            UpsDelegatePayRespBo tempUpsResult = upsUtil.delegatePay(withDrawMoney, infoDo.getCardName(), infoDo.getCardNo(), businessId.toString(), infoDo.getCardPhone(),
                    infoDo.getCardBank(), infoDo.getCardCode(), Constants.DEFAULT_REFUND_PURPOSE, "02", UserAccountLogType.TRADE_WITHDRAW.getCode(),
                    recordDo.getId() + StringUtils.EMPTY, infoDo.getCardIdnumber());
            logger.info("trade withdraw upsResult = {}", tempUpsResult);

            if (!tempUpsResult.isSuccess()) {
                recordDo.setStatus(TradeWithdrawRecordStatus.CLOSED.getCode());
                afTradeWithdrawRecordDao.updateById(recordDo);
                afTradeOrderDao.updateStatusByIds(ids, TradeOrderStatus.NEW.getCode());
                logger.info("trade withdraw upsResult is false");
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.error("trade withdraw error, exception={}", e);
            return false;
        }

    }

    @Override
    public boolean refund(Long businessId, Long orderId) {
        try {
            AfTradeOrderDo tradeOrderDo = afTradeOrderDao.getById(orderId);
            if (!businessId.equals(tradeOrderDo.getBusinessId())) {
                return false;
            }

            tradeOrderDo = new AfTradeOrderDo();
            tradeOrderDo.setOrderId(orderId);
            tradeOrderDo.setStatus(TradeOrderStatus.REFUNDING.getCode());
            tradeOrderDo.setGmtModified(new Date());
            afTradeOrderDao.updateById(tradeOrderDo);

            // 生成退款记录
            String refundNo = generatorClusterNo.getRefundNo(new Date());

            // 订单状态为退款中
            AfOrderDo orderDo = afOrderDao.getOrderById(orderId);
            orderDo.setStatus(OrderStatus.DEAL_REFUNDING.getCode());
            orderDo.setGmtModified(new Date());
            afOrderDao.updateOrder(orderDo);

            AfBorrowDo borrowInfo = afBorrowDao.getBorrowByOrderIdAndStatus(orderId, BorrowStatus.TRANSED.getCode());
            // 重新需要生成账单的金额
            BigDecimal borrowAmount = calculateBorrowAmountByBackTime(borrowInfo.getRid(), new Date(), orderDo.getActualAmount(), true);
            logger.info("dealBrandOrderRefund borrowAmount = {}", borrowAmount);

            // 更新借款状态
            afBorrowDao.updateBorrowStatus(borrowInfo.getRid(), BorrowStatus.FINISH.getCode());

            // 更新账单信息
            afBorrowBillDao.updateNotRepayedBillStatus(borrowInfo.getRid(), BorrowBillStatus.CLOSE.getCode());

            AfUserBankcardDo cardInfo = afUserBankcardDao.getUserMainBankcardByUserId(orderDo.getUserId());
            AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(orderDo.getUserId());
            if (borrowAmount.compareTo(BigDecimal.ZERO) < 0) {

                AfOrderRefundDo refundDo = new AfOrderRefundDo();
                refundDo.setOrderId(orderId);
                refundDo.setOrderNo(orderDo.getOrderNo());
                refundDo.setAmount(orderDo.getActualAmount());
                refundDo.setActualAmount(borrowAmount.abs());
                refundDo.setUserId(orderDo.getUserId());
                refundDo.setStatus(OrderRefundStatus.REFUNDING.getCode());
                refundDo.setType(PayType.AGENT_PAY.getCode());
                refundDo.setRefundNo(refundNo);
                refundDo.setContent("商户点击退款");
                afOrderRefundDao.saveRecord(refundDo);

                UpsDelegatePayRespBo tempUpsResult = upsUtil.delegatePay(borrowAmount.abs(), accountInfo.getRealName(), cardInfo.getCardNumber(), String.valueOf(accountInfo.getUserId()), cardInfo.getMobile(),
                        cardInfo.getBankName(), cardInfo.getBankCode(), Constants.DEFAULT_REFUND_PURPOSE, "02", UserAccountLogType.TRADE_BANK_REFUND.getCode(),
                        refundDo.getRid() + StringUtils.EMPTY, accountInfo.getIdNumber());
                logger.info("trade bank refund upsResult = {}", tempUpsResult);

                if (!tempUpsResult.isSuccess()) {
                    refundDo.setStatus(OrderRefundStatus.FAIL.getCode());
                    refundDo.setPayTradeNo(tempUpsResult.getOrderNo());
                    afOrderRefundDao.updateOrderRefund(refundDo);
                    throw new FanbeiException("reund error");
                } else {
                    refundDo.setPayTradeNo(tempUpsResult.getOrderNo());
                    afOrderRefundDao.updateOrderRefund(refundDo);
                }
            } else if (borrowAmount.compareTo(BigDecimal.ZERO) > 0) {

                AfOrderRefundDo refundDo = new AfOrderRefundDo();
                refundDo.setOrderId(orderId);
                refundDo.setOrderNo(orderDo.getOrderNo());
                refundDo.setAmount(orderDo.getActualAmount());
                refundDo.setActualAmount(BigDecimal.ZERO);
                refundDo.setUserId(orderDo.getUserId());
                refundDo.setStatus(OrderRefundStatus.FINISH.getCode());
                refundDo.setType(PayType.AGENT_PAY.getCode());
                refundDo.setRefundNo(refundNo);
                refundDo.setContent("代买代付退款生成新账单");
                afOrderRefundDao.saveRecord(refundDo);

                dealBorrowAndBill(orderDo.getUserId(), accountInfo.getUserName(), borrowAmount, borrowInfo.getName(), borrowInfo.getNper() - borrowInfo.getNperRepayment(), orderDo.getRid(),
                        orderDo.getOrderNo(), orderDo.getBorrowRate(), orderDo.getInterestFreeJson());

                AfOrderDo updateOrder = new AfOrderDo();
                updateOrder.setRid(orderId);
                updateOrder.setStatus(OrderStatus.CLOSED.getCode());
                updateOrder.setGmtModified(new Date());
                afOrderDao.updateOrder(updateOrder);

                tradeOrderDo.setStatus(TradeOrderStatus.REFUND.getCode());
                tradeOrderDo.setGmtModified(new Date());
                afTradeOrderDao.updateById(tradeOrderDo);

            }

            // 更新账户金额
            afUserAccountDao.changeAmount(orderDo.getUserId(), calculateUsedAmount(borrowInfo));
            // 增加Account记录
            afUserAccountLogDao.addUserAccountLog(buildUserAccountLogDo(UserAccountLogType.AP_REFUND, borrowInfo.getAmount(), orderDo.getUserId(), borrowInfo.getRid()));

            logger.info("trade refund comlete");
            return true;
        } catch (Exception e) {
            logger.error("trade refund error, exception={}", e);
            return false;
        }
    }

    private BigDecimal calculateBorrowAmountByBackTime(Long borrowId, Date gmtBack, BigDecimal refundAmount, boolean refundByUser) {
        // 借款金额+借款金额*退款日利率*（退款日期-借款日期+1）*（0,1）- 退款金额- 已还账单和 + 优惠和
        logger.info(" calculateBorrowRefundAmount begin borrowId = {}", borrowId);
        AfBorrowDo borrowInfo = afBorrowDao.getBorrowById(borrowId);
        // 借款金额
        BigDecimal borrowAmount = borrowInfo.getAmount();

        Calendar c1 = Calendar.getInstance();
        c1.setTime(borrowInfo.getGmtCreate());
        Calendar c2 = Calendar.getInstance();
        c2.setTime(new Date());
        // 借款天数
        long days = DateUtil.getNumberOfDaysBetween(c1, c2) + 1;

        AfResourceDo refundResourceInfo = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_REFUND_RATE);
        // 退款利率
        BigDecimal refundRate = new BigDecimal(refundResourceInfo.getValue());
        // 已经还账单和-优惠和
        BigDecimal repaymentAndCouponAmount = calculateRepaymentAndCouponAmount(borrowId);

        return BigDecimalUtil.add(borrowAmount, refundByUser ? BigDecimalUtil.multiply(borrowAmount, new BigDecimal(days), refundRate) : BigDecimal.ZERO,
                BigDecimalUtil.multiply(refundAmount, new BigDecimal("-1")), BigDecimalUtil.multiply(repaymentAndCouponAmount, new BigDecimal("-1")));
    }

    /**
     * 计算还款金额以及优惠金额
     *
     * @param borrowId
     * @return
     */
    private BigDecimal calculateRepaymentAndCouponAmount(Long borrowId) {
        List<AfBorrowBillDo> repaymentedBillList = afBorrowBillDao.getBillListByBorrowIdAndStatus(borrowId, BorrowBillStatus.YES.getCode());
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (CollectionUtils.isEmpty(repaymentedBillList)) {
            // 没有还款记录
            return totalAmount;
        } else {
            logger.info("billList = {}", repaymentedBillList);
            List<Long> repaymentIds = CollectionConverterUtil.convertToListFromList(repaymentedBillList, new Converter<AfBorrowBillDo, Long>() {
                @Override
                public Long convert(AfBorrowBillDo source) {
                    return source.getRepaymentId();
                }
            });
            logger.info("repaymentIds = {}", repaymentIds);
            List<Long> billIds = CollectionConverterUtil.convertToListFromList(repaymentedBillList, new Converter<AfBorrowBillDo, Long>() {
                @Override
                public Long convert(AfBorrowBillDo source) {
                    return source.getRid();
                }
            });

            List<AfRepaymentDo> repaymentList = afRepaymentDao.getRepaymentListByIds(repaymentIds);
            for (AfRepaymentDo repayment : repaymentList) {
                List<Long> repaymentBillLists = CollectionConverterUtil.convertToListFromArray(repayment.getBillIds().split(","), new Converter<String, Long>() {
                    @Override
                    public Long convert(String source) {
                        return Long.parseLong(source);
                    }
                });
                for (Long billId : billIds) {
                    if (repaymentBillLists.contains(billId)) {
                        AfBorrowBillDo billInfo = getBillFromList(repaymentedBillList, billId);
                        if (repayment.getUserCouponId() == 0l) {
                            // 没有优惠券,则按照账单金额来
                            totalAmount = billInfo.getBillAmount();
                            continue;
                        } else {
                            // 有优惠券
                            if (repaymentBillLists.indexOf(billId) != repaymentBillLists.size() - 1) {
                                // 不是最后一个记录，则按照百分比计算
                                logger.info(" is not last one");
                                totalAmount = BigDecimalUtil.add(totalAmount, calculateRepaymentCouponAmount(repayment, billInfo));
                                continue;
                            } else {
                                // 如果是最后一个，则先减去前面的还款记录
                                BigDecimal tempAmount = BigDecimal.ZERO;
                                logger.info(" is last one");
                                List<AfBorrowBillDo> tempBillList = afBorrowBillDao.getBillListByIds(repaymentBillLists);
                                // 只有一个
                                if (repaymentBillLists.size() == 1) {
                                    totalAmount = calculateRepaymentCouponAmount(repayment, billInfo);
                                } else {
                                    for (int i = 0; i < repaymentBillLists.size() - 1; i++) {
                                        AfBorrowBillDo tempBillInfo = getBillFromList(tempBillList, repaymentBillLists.get(i));
                                        tempAmount = BigDecimalUtil.add(tempAmount, calculateRepaymentCouponAmount(repayment, tempBillInfo));
                                    }
                                    BigDecimal finalRepaymentActualAmount = BigDecimalUtil.add(repayment.getActualAmount(),
                                            BigDecimalUtil.divide(repayment.getJfbAmount(), new BigDecimal("100")), repayment.getRebateAmount());
                                    totalAmount = BigDecimalUtil.add(totalAmount, BigDecimalUtil.subtract(finalRepaymentActualAmount, tempAmount));
                                }
                                continue;
                            }
                        }
                    }
                }
            }
            return totalAmount;
        }

    }

    private AfBorrowBillDo getBillFromList(List<AfBorrowBillDo> billList, Long billId) {
        if (CollectionUtils.isEmpty(billList)) {
            return null;
        }
        for (AfBorrowBillDo billInfo : billList) {
            if (billInfo.getRid().equals(billId)) {
                return billInfo;
            }
        }
        return null;
    }

    /**
     * 计算该笔账单在还款中的实际还款金额
     *
     * @param repayment
     * @param billInfo
     * @return
     */
    private BigDecimal calculateRepaymentCouponAmount(AfRepaymentDo repayment, AfBorrowBillDo billInfo) {
        logger.info("calculateRepaymentCouponAmount begin  repayment = {}, billInfo = {}", new Object[]{repayment, billInfo});
        BigDecimal couponAmount = repayment.getCouponAmount();
        BigDecimal rate = BigDecimalUtil.divide(billInfo.getBillAmount(), repayment.getRepaymentAmount());
        BigDecimal result = billInfo.getBillAmount().subtract(BigDecimalUtil.multiply(rate, couponAmount));
        logger.info("rate = {}, billAmount = {} repaymentAmount = {} result = {}", new Object[]{rate, billInfo.getBillAmount(), repayment.getRepaymentAmount(), result});
        return result;
    }

    /**
     * 生成新的借款和账单
     */
    private void dealBorrowAndBill(Long userId, String userName, BigDecimal amount, String name, Integer nper, Long orderId, String orderNo, String borrowRate,
                                   String interestFreeJson) {
        Integer freeNper = 0;
        List<InterestFreeJsonBo> interestFreeList = StringUtils.isEmpty(interestFreeJson) ? null : JSONObject.parseArray(interestFreeJson, InterestFreeJsonBo.class);
        if (CollectionUtils.isNotEmpty(interestFreeList)) {
            for (InterestFreeJsonBo bo : interestFreeList) {
                if (bo.getNper().equals(nper)) {
                    freeNper = bo.getFreeNper();
                    break;
                }
            }
        }
        AfBorrowDo borrow = new AfBorrowDo();
        Date currDate = new Date();
        borrow.setAmount(amount);
        borrow.setType(BorrowType.TOCONSUME.getCode());
        borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
        borrow.setStatus(BorrowStatus.TRANSED.getCode());// 默认转账成功
        borrow.setName(name);
        borrow.setUserId(userId);
        borrow.setNper(nper);
        borrow.setNperAmount(BigDecimal.ZERO);
        borrow.setCardNumber(StringUtils.EMPTY);
        borrow.setCardName("代付");
        borrow.setRemark(name);
        borrow.setOrderId(orderId);
        borrow.setOrderNo(orderNo);
        borrow.setBorrowRate(borrowRate);
        borrow.setCalculateMethod(BorrowCalculateMethod.DENG_BEN_DENG_XI.getCode());
        borrow.setGmtCreate(new Date());
        borrow.setFreeNper(freeNper);
        // 新增借款信息
        afBorrowDao.addBorrow(borrow);

        // 新增借款日志
        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setAmount(amount);
        accountLog.setUserId(userId);
        accountLog.setRefId(borrow.getRid() + "");
        accountLog.setType(UserAccountLogType.CONSUME.getCode());
        afUserAccountLogDao.addUserAccountLog(accountLog);

        List<AfBorrowBillDo> billList = buildBorrowBillForNewInterest(borrow);

        afBorrowDao.addBorrowBill(billList);

    }

    /**
     * @param borrow 借款信息
     * @return
     */
    private List<AfBorrowBillDo> buildBorrowBillForNewInterest(AfBorrowDo borrow) {
        List<AfBorrowBillDo> list = new ArrayList<AfBorrowBillDo>();
        Date now = new Date();// 当前时间
        Integer nper = borrow.getNper();
        Integer freeNper = 0;
        String borrowRate = borrow.getBorrowRate();
        BigDecimal money = borrow.getAmount();// 借款金额

        // 拿到日利率快照Bo
        BorrowRateBo borrowRateBo = BorrowRateBoUtil.parseToBoFromDataTableStr(borrowRate);

        // 每期本金
        BigDecimal principleAmount = money.divide(new BigDecimal(borrow.getNper()), 2, RoundingMode.DOWN);
        // 第一期本金
        BigDecimal firstPrincipleAmount = getFirstPrincipleAmount(money, principleAmount, nper);
        // 每期利息
        BigDecimal interestAmount = money.multiply(borrowRateBo.getRate()).divide(Constants.DECIMAL_MONTH_OF_YEAR, 2, RoundingMode.CEILING);
        // 每期手续费
        BigDecimal poundageAmount = BigDecimalUtil.getPerPoundage(money, borrow.getNper(), borrowRateBo.getPoundageRate(), borrowRateBo.getRangeBegin(), borrowRateBo.getRangeEnd(),
                freeNper);

        for (int i = 1; i <= nper; i++) {
            AfBorrowBillDo bill = new AfBorrowBillDo();
            bill.setUserId(borrow.getUserId());
            bill.setBorrowId(borrow.getRid());
            bill.setBorrowNo(borrow.getBorrowNo());
            bill.setName(borrow.getName());
            bill.setGmtBorrow(borrow.getGmtCreate());
            Map<String, Integer> timeMap = getCurrentYearAndMonth(now);
            bill.setBillYear(timeMap.get(Constants.DEFAULT_YEAR));
            bill.setBillMonth(timeMap.get(Constants.DEFAULT_MONTH));
            bill.setNper(borrow.getNper());
            bill.setBillNper(i);
            if (i <= freeNper) {
                bill.setInterestAmount(BigDecimal.ZERO);
                bill.setIsFreeInterest(YesNoStatus.YES.getCode());
                bill.setPoundageAmount(BigDecimal.ZERO);
            } else {
                bill.setInterestAmount(interestAmount);
                bill.setIsFreeInterest(YesNoStatus.NO.getCode());
                bill.setPoundageAmount(poundageAmount);
            }
            if (i == 1) {
                bill.setPrincipleAmount(firstPrincipleAmount);
            } else {
                bill.setPrincipleAmount(principleAmount);
            }
            bill.setBillAmount(BigDecimalUtil.add(bill.getInterestAmount(), bill.getPoundageAmount(), bill.getPrincipleAmount()));
            bill.setStatus(BorrowBillStatus.NO.getCode());
            bill.setType(BorrowType.CONSUME.getCode());
            list.add(bill);
            now = DateUtil.addMonths(now, 1);
        }
        return list;
    }

    /**
     * 获取第一期分期金额
     *
     * @param amount          本金
     * @param principleAmount 每期金额
     * @param nper            分期数
     * @return
     */
    private BigDecimal getFirstPrincipleAmount(BigDecimal amount, BigDecimal principleAmount, Integer nper) {
        // 剩余期数本金之和
        BigDecimal tempAmount = principleAmount.multiply(new BigDecimal(nper - 1));
        return amount.subtract(tempAmount);
    }

    private Map<String, Integer> getCurrentYearAndMonth(Date now) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        // 账单日
        Date startDate = DateUtil.addDays(DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(now)),
                NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_CREATE_TIME), 10) - 1);
        if (now.before(startDate)) {// 账单日前面
            startDate = DateUtil.addMonths(startDate, -1);
        }
        int billYear = 0, billMonth = 0;
        String[] billDay = DateUtil.formatDate(startDate, DateUtil.MONTH_PATTERN).split("-");
        if (billDay.length == 2) {
            billYear = NumberUtil.objToIntDefault(billDay[0], 0);
            billMonth = NumberUtil.objToIntDefault(billDay[1], 0);
        }
        map.put(Constants.DEFAULT_YEAR, billYear);
        map.put(Constants.DEFAULT_MONTH, billMonth);
        return map;
    }

    /**
     * 获取应该退多少额度
     *
     * @param borrowInfo
     * @return
     */
    private BigDecimal calculateUsedAmount(AfBorrowDo borrowInfo) {
        List<AfBorrowBillDo> repaymentedBillList = afBorrowBillDao.getBillListByBorrowIdAndStatus(borrowInfo.getRid(), BorrowBillStatus.YES.getCode());
        if (CollectionUtils.isEmpty(repaymentedBillList)) {
            return borrowInfo.getAmount();
        } else {
            if (borrowInfo.getNper().equals(borrowInfo.getNperRepayment())) {
                // 如果已经还清
                return borrowInfo.getAmount();
            } else {
                // 算还了几期金额
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (AfBorrowBillDo billInfo : repaymentedBillList) {
                    totalAmount = BigDecimalUtil.add(totalAmount, billInfo.getPrincipleAmount());
                }
                return borrowInfo.getAmount().subtract(totalAmount);
            }
        }
    }

    private AfUserAccountLogDo buildUserAccountLogDo(UserAccountLogType logType, BigDecimal amount, Long userId, Long orderId) {
        // 增加account变更日志
        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setAmount(amount);
        accountLog.setUserId(userId);
        accountLog.setRefId(orderId + "");
        accountLog.setType(logType.getCode());
        return accountLog;
    }
}