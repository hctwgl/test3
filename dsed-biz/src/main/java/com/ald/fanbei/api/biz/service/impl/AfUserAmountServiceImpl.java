package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAmountQuery;
import com.timevale.tgtext.awt.geom.q;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author honghzengpei 2017/11/22 14:29
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAmountService")
public class AfUserAmountServiceImpl implements AfUserAmountService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");

    @Resource
    AfUserAmountDao afUserAmountDao;
    @Resource
    AfUserAmountLogDao afUserAmountLogDao;
    @Resource
    AfUserAmountDetailDao afUserAmountDetailDao;
    @Resource
    AfBorrowBillService afBorrowBillService;
    @Resource
    AfOrderService afOrderService;

    @Resource
    AfOrderRefundDao afOrderRefundDao;
    @Resource
    AfBorrowBillDao afBorrowBillDao;

    @Resource
    AfRepaymentDao afRepaymentDao;

    @Resource
    AfBorrowService afBorrowService;

    @Resource
    TransactionTemplate transactionTemplate;

    @Resource
    RiskUtil riskUtil;

    @Override
    public void addUseAmountDetail(final AfRepaymentDo afRepaymentDo) {

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try{
                    _addUserAmountDetail(afRepaymentDo);
                    status.flush();
                }
                catch (Exception e){
                    logger.error("add userAmount error",e);
                    status.setRollbackOnly();
                }
            }
        });
    }

    private void _addUserAmountDetail(AfRepaymentDo afRepaymentDo){
        BigDecimal total = afRepaymentDo.getRepaymentAmount();  //总金额
        List<Long> billIdList = CollectionConverterUtil.convertToListFromArray(afRepaymentDo.getBillIds().split(","), new Converter<String, Long>() {
            @Override
            public Long convert(String source) {
                return Long.parseLong(source);
            }
        });
        List<AfBorrowBillDo> afBorrowBillDoList = afBorrowBillService.getBorrowBillByIds(billIdList);
        BigDecimal principleAmount = BigDecimal.ZERO;        //本金
        BigDecimal poundageAmount = BigDecimal.ZERO;        //手续费
        BigDecimal overdueInterestAmount = BigDecimal.ZERO;       //逾期利息

        BigDecimal yohuijuang = BigDecimal.ZERO;   //优惠卷
        BigDecimal yuer = BigDecimal.ZERO;
        BigDecimal zifu = BigDecimal.ZERO;

        zifu = afRepaymentDo.getActualAmount();
        yohuijuang = afRepaymentDo.getCouponAmount();
        yuer = afRepaymentDo.getRebateAmount().add(afRepaymentDo.getJfbAmount());

        for(AfBorrowBillDo afBorrowBillDo : afBorrowBillDoList){
            principleAmount =principleAmount.add(afBorrowBillDo.getPrincipleAmount());
            overdueInterestAmount =overdueInterestAmount.add(afBorrowBillDo.getOverdueInterestAmount().add(afBorrowBillDo.getOverduePoundageAmount()));
            poundageAmount = poundageAmount.add(afBorrowBillDo.getPoundageAmount().add(afBorrowBillDo.getInterestAmount()));
        }

        if(yohuijuang ==null){
            yohuijuang = BigDecimal.ZERO;
        }

        if(yuer==null){
            yuer =BigDecimal.ZERO;
        }
        if(zifu ==null){
            zifu =BigDecimal.ZERO;
        }

        AfUserAmountDo afUserAmountDo = new AfUserAmountDo();
        afUserAmountDo.setAmount(afRepaymentDo.getRepaymentAmount());
        afUserAmountDo.setBizOrderNo(afRepaymentDo.getRepayNo());
        afUserAmountDo.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
        afUserAmountDo.setSourceId(afRepaymentDo.getRid());
        afUserAmountDo.setUserId(afRepaymentDo.getUserId());
        afUserAmountDo.setStatus(AfUserAmountProcessStatus.NEW.getCode());
        afUserAmountDo.setRemark(afRepaymentDo.getName());
        afUserAmountDao.addUserAmount(afUserAmountDo);
        addUserAmountLog(afRepaymentDo,AfUserAmountProcessStatus.NEW);

        //if(BigDecimal.ZERO.compareTo(principleAmount) !=0){
        afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),principleAmount,1, AfUserAmountDetailType.BENJIN));
        //}
        //if(BigDecimal.ZERO.compareTo(poundageAmount) !=0){
        afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),poundageAmount,1,AfUserAmountDetailType.FENQISHOUXUFEI));
        //}
        //if(BigDecimal.ZERO.compareTo(overdueInterestAmount) !=0){
        afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),overdueInterestAmount,1,AfUserAmountDetailType.YUQILIXI));
        //}

        //if(BigDecimal.ZERO.compareTo(yohuijuang) !=0){
        afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),BigDecimal.ZERO.subtract( yohuijuang),1,AfUserAmountDetailType.YOUHUIJUANGDIKOU));
        //}
        //if(BigDecimal.ZERO.compareTo(yuer) !=0){
        afUserAmountDetailDao.addUserAmountDetail( buildAmountDetail(afUserAmountDo.getId(),BigDecimal.ZERO.subtract( yuer),1,AfUserAmountDetailType.ZHANGHUYUERDIKOU));
        //}
        //if(BigDecimal.ZERO.compareTo(zifu) !=0){
        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(),BigDecimal.ZERO.subtract( zifu),1,AfUserAmountDetailType.SHIJIZHIFU));
        //}
    }



    @Override
    public void addUserAmountLog(AfRepaymentDo afRepaymentDo, AfUserAmountProcessStatus afUserAmountProcessStatus) {
        AfUserAmountLogDo afUserAmountLogDo = new AfUserAmountLogDo();
        afUserAmountLogDo.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
        afUserAmountLogDo.setSourceId(afRepaymentDo.getRid());
        afUserAmountLogDo.setStatus(afUserAmountProcessStatus.getCode());
        afUserAmountLogDao.addUserAmountLog(afUserAmountLogDo);
    }

    @Override
    public void updateUserAmount(AfUserAmountProcessStatus afUserAmountProcessStatus, AfRepaymentDo afRepaymentDo) {
        addUserAmountLog(afRepaymentDo,afUserAmountProcessStatus);
        AfUserAmountDo afUserAmountDo = new AfUserAmountDo();
        afUserAmountDo.setStatus(afUserAmountProcessStatus.getCode());
        afUserAmountDo.setSourceId(afRepaymentDo.getRid());
        afUserAmountDo.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
        afUserAmountDao.updateUserAmountStatus(afUserAmountDo);
    }

    private AfUserAmountDetailDo buildAmountDetail(long userAmountId, BigDecimal amount, int count, AfUserAmountDetailType type){
        AfUserAmountDetailDo afUserAmountDetailDo = new AfUserAmountDetailDo();
        afUserAmountDetailDo.setUserAmountId(userAmountId);
        afUserAmountDetailDo.setAmount(amount);
        afUserAmountDetailDo.setCount(count);
        afUserAmountDetailDo.setType(type.getCode());
        afUserAmountDetailDo.setTitle(type.getName());
        afUserAmountDetailDo.setRemark("");
        return afUserAmountDetailDo;
    }



    //订单退款生成记录
    public int refundOrder(final long orderId) {
        try {
            return transactionTemplate.execute(new TransactionCallback<Integer>() {
                @Override
                public Integer doInTransaction(TransactionStatus status) {
                    try {
                        return _refundOrder(orderId);
                    } catch (Exception e) {
                        logger.error("add refundDetail error:", e);
                        thirdLog.error("add refundDetail error", e);
                        return 0;
                    }
                }
            });
        }catch (Exception e){
            logger.error("add refundDetail error:", e);
            thirdLog.error("add refundDetail error", e);
            return  0;
        }
    }

    private int _refundOrder(long orderId){
        AfOrderDo afOrderDo = afOrderService.getOrderById(orderId);

        if (afOrderDo == null) {
            logger.info("add refundDetail order is null orderId ="+ orderId);
            return 0;
        }
        logger.info("add refundDetail orderStatus ="+ afOrderDo.getStatus());
        if (!(afOrderDo.getStatus().equals(OrderStatus.CLOSED.getCode()) || afOrderDo.getStatus().equals(OrderStatus.DEAL_REFUNDING.getCode()))) {
            return 0;
        }
        AfOrderRefundDo afOrderRefundDo = afOrderRefundDao.getOrderRefundByOrderId(orderId);
        if (afOrderRefundDo == null || !(afOrderRefundDo.getStatus().equals(OrderRefundStatus.FINISH.getCode()) || afOrderRefundDo.getStatus().equals(OrderRefundStatus.REFUNDING.getCode()))) {
            logger.info("add refundDetail afOrderRefundDo error");
            try{
                logger.info("add refundDetail afOrderRefundDo status"+ afOrderRefundDo.getStatus());
            }
            catch (Exception e){
                logger.info("add refundDetail afOrderRefundDo is null");
                e.printStackTrace();
            }
            return 0;
        }

        AfBorrowDo borrowInfo = afBorrowService.getBorrowByOrderId(orderId);
        if (borrowInfo == null) {
            logger.info("add refundDetail borrowInfo is null");
            return 0;
        }
        List<AfBorrowBillDo> repaymentedBillList = afBorrowBillDao.getBillListByBorrowIdAndStatus(borrowInfo.getRid(),
                BorrowBillStatus.YES.getCode());
        HashMap<String, BigDecimal> map = calculateRepaymentAndCouponAmount(borrowInfo.getRid());
        BigDecimal bankPay = afOrderDo.getBankAmount();                 //直接支付
        BigDecimal backBenJin = map.get("benjin");                  //本金
        BigDecimal fenqishouxufei = map.get("feiqishouxufei");    //分期手续费
        BigDecimal yuqilixi = map.get("yuqilixi");                  //逾期利息
        BigDecimal youhuijuan = map.get("coupon");



        AfUserAmountDo afUserAmountDo = new AfUserAmountDo();
        afUserAmountDo.setAmount(map.get("repayment").add(bankPay));
        afUserAmountDo.setBizOrderNo(afOrderRefundDo.getRefundNo());  //随机生成
        afUserAmountDo.setBizType(AfUserAmountBizType.REFUND.getCode());
        afUserAmountDo.setSourceId(afOrderRefundDo.getRid());
        afUserAmountDo.setUserId(afOrderRefundDo.getUserId());
        afUserAmountDo.setStatus(AfUserAmountProcessStatus.SUCCESS.getCode());
        afUserAmountDo.setRemark(afOrderDo.getGoodsName());
        if(afUserAmountDo.getRemark() ==null || afUserAmountDo.getRemark().length() ==0){
            afUserAmountDo.setRemark(afOrderDo.getShopName());
        }
        afUserAmountDao.addUserAmount(afUserAmountDo);
//        addUserAmountLog(afRepaymentDo,AfUserAmountProcessStatus.NEW);


        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(), backBenJin, repaymentedBillList.size(), AfUserAmountDetailType.BENJIN));

        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(), fenqishouxufei, repaymentedBillList.size(), AfUserAmountDetailType.FENQISHOUXUFEI));


        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(), yuqilixi, 1, AfUserAmountDetailType.YUQILIXI));

        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(), BigDecimal.ZERO.subtract(youhuijuan), 1, AfUserAmountDetailType.YOUHUIJUANGDIKOU));

//        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(), bankPay, 1, AfUserAmountDetailType.ZHIJIEZHIFU));
        afUserAmountDetailDao.addUserAmountDetail(buildAmountDetail(afUserAmountDo.getId(), afUserAmountDo.getAmount(), 1, AfUserAmountDetailType.ZHIJIEZHIFU));

        return 1;
    }


    private HashMap  calculateRepaymentAndCouponAmount(Long borrowId) {
        List<AfBorrowBillDo> repaymentedBillList = afBorrowBillDao.getBillListByBorrowIdAndStatus(borrowId,
                BorrowBillStatus.YES.getCode());
        BigDecimal totalAmount = BigDecimal.ZERO;       //总还款金额
        BigDecimal couponAmount = BigDecimal.ZERO;      //优惠
        BigDecimal benjin = BigDecimal.ZERO;            //本金
        BigDecimal feiqishouxufei = BigDecimal.ZERO;    //分期手续费
        BigDecimal yuqilixi = BigDecimal.ZERO;          //逾期利息

        HashMap<String ,BigDecimal> ret = new HashMap<>();

        if (CollectionUtils.isEmpty(repaymentedBillList)) {
            // 没有还款记录

        } else {
            logger.info("billList = {}", repaymentedBillList);
            List<Long> repaymentIds = CollectionConverterUtil.convertToListFromList(repaymentedBillList,
                    new Converter<AfBorrowBillDo, Long>() {
                        @Override
                        public Long convert(AfBorrowBillDo source) {
                            return source.getRepaymentId();
                        }
                    });
            logger.info("repaymentIds = {}", repaymentIds);
            List<Long> billIds = CollectionConverterUtil.convertToListFromList(repaymentedBillList,
                    new Converter<AfBorrowBillDo, Long>() {
                        @Override
                        public Long convert(AfBorrowBillDo source) {
                            return source.getRid();
                        }
                    });

            List<AfRepaymentDo> repaymentList = afRepaymentDao.getRepaymentListByIds(repaymentIds);
            for (AfRepaymentDo repayment : repaymentList) {
                List<Long> repaymentBillLists = CollectionConverterUtil
                        .convertToListFromArray(repayment.getBillIds().split(","), new Converter<String, Long>() {
                            @Override
                            public Long convert(String source) {
                                return Long.parseLong(source);
                            }
                        });
                List<AfBorrowBillDo> listDo = afBorrowBillService.getBorrowBillByIds(repaymentBillLists);
                BigDecimal allAmount = BigDecimal.ZERO;
                for (AfBorrowBillDo afBorrowBillDo : listDo){
                    allAmount = allAmount.add(afBorrowBillDo.getBillAmount());
                }
                for (Long billId : billIds) {
                    if (!repaymentBillLists.contains(billId)){
                        continue;
                    }
                    if (repaymentBillLists.contains(billId)) {
                        AfBorrowBillDo billInfo = getBillFromList(repaymentedBillList, billId);
                        if(billInfo == null) continue;
                        totalAmount = BigDecimalUtil.add(totalAmount,calculateRepaymentCouponAmount1(repayment,billInfo,allAmount));
                        //totalAmount = BigDecimalUtil.add(totalAmount, billInfo.getBillAmount());
                        benjin = BigDecimalUtil.add(benjin, billInfo.getPrincipleAmount());
                        feiqishouxufei =BigDecimalUtil.add(feiqishouxufei, billInfo.getPoundageAmount().add(billInfo.getInterestAmount()));
                        yuqilixi =BigDecimalUtil.add(yuqilixi, billInfo.getOverdueInterestAmount().add(billInfo.getOverduePoundageAmount()));
                        if (repayment.getUserCouponId() == 0l) {
                            // 没有优惠券,则按照账单金额来

                            continue;
                        } else {
                            // 有优惠券

                            couponAmount = BigDecimalUtil.add(couponAmount, calculateRepaymentCouponAmount(repayment, billInfo));
                            continue;
                        }
                    }
                }
            }
        }


        ret.put("repayment",totalAmount);
        ret.put("coupon",couponAmount);
        ret.put("benjin",benjin);
        ret.put("feiqishouxufei",feiqishouxufei);
        ret.put("yuqilixi",yuqilixi);
        return ret;
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
//        BigDecimal result = billInfo.getBillAmount().subtract(BigDecimalUtil.multiply(rate, couponAmount));
        BigDecimal result = BigDecimalUtil.multiply(rate, couponAmount);
        logger.info("rate = {}, billAmount = {} repaymentAmount = {} result = {}", new Object[]{rate, billInfo.getBillAmount(), repayment.getRepaymentAmount(), result});
        return result;
    }

	@Override
	public List<AfUserAmountDo> getAmountByUserIdAndType(Long userId, int type, int page, int pageSize) {
		int begin = (page - 1) * pageSize;
		return afUserAmountDao.getAmountByUserIdAndType(userId,type,begin,pageSize);
	}

	@Override
	public List<AfUserAmountDetailDo> getAmountDetailByAmountId(Long amountId) {
		return afUserAmountDao.getAmountDetailByAmountId(amountId);
	}

	@Override
	public BigDecimal getRenfundAmountByAmountId(Long amountId) {
		return afUserAmountDao.getRenfundAmountByAmountId(amountId);
	}

	@Override
	public AfUserAmountDo getUserAmountById(Long amountId) {
		return afUserAmountDao.getUserAmountById(amountId);
	}

	@Override
	public AfBorrowDto getBorrowDtoByAmountId(Long amountId) {
		return afUserAmountDao.getBorrowDtoByAmountId(amountId);
	}

	@Override
	public List<AfUserAmountLogDo> getAmountLogByAmountId(Long sourceId) {
		return afUserAmountLogDao.getAmountLogByAmountId(sourceId);
	}

	@Override
	public List<AfUserAmountDo> getUserAmountByQuery(AfUserAmountQuery query) {
		return afUserAmountDao.getUserAmountByQuery(query);
	}

	@Override
	public List<String> getMonthInYearByQuery(AfUserAmountQuery query) {
		return afUserAmountDao.getMonthInYearByQuery(query);
	}

	@Override
	public BigDecimal getRepaymentAmountByAmountId(Long amountId) {
		return afUserAmountDao.getRepaymentAmountByAmountId(amountId);
	}


    private BigDecimal calculateRepaymentCouponAmount(AfRepaymentDo repayment, AfBorrowBillDo billInfo,BigDecimal allAmount) {
        logger.info("calculateRepaymentCouponAmount begin  repayment = {}, billInfo = {}", new Object[]{repayment, billInfo});
        BigDecimal couponAmount = repayment.getCouponAmount();
        BigDecimal rate = BigDecimalUtil.divide(billInfo.getBillAmount(), repayment.getRepaymentAmount());
//        BigDecimal result = billInfo.getBillAmount().subtract(BigDecimalUtil.multiply(rate, couponAmount));

        BigDecimal actualAmount = BigDecimalUtil.add(repayment.getActualAmount(),repayment.getRebateAmount());
        BigDecimal result = billInfo.getBillAmount().multiply(actualAmount);

        result = BigDecimalUtil.divide(result,allAmount);

        result = result.subtract(BigDecimalUtil.multiply(rate, couponAmount));

        logger.info("rate = {}, billAmount = {} repaymentAmount = {} result = {}", new Object[]{rate, billInfo.getBillAmount(), repayment.getRepaymentAmount(), result});
        return result;
    }


    /**
     * 计算该笔账单在还款中的实际还款金额
     *
     * @param repayment
     * @param billInfo
     * @return
     */
    private BigDecimal calculateRepaymentCouponAmount1(AfRepaymentDo repayment, AfBorrowBillDo billInfo,BigDecimal allAmount) {
        logger.info("calculateRepaymentCouponAmount begin  repayment = {}, billInfo = {}", new Object[]{repayment, billInfo});
        //BigDecimal rate1 = BigDecimalUtil.divide(billInfo.getBillAmount(),allAmount);
        BigDecimal actualAmount = BigDecimalUtil.add(repayment.getActualAmount(),repayment.getRebateAmount());
        BigDecimal result = billInfo.getBillAmount().multiply(actualAmount);
        result = BigDecimalUtil.divide(result,allAmount);
        logger.info("rate = {}, billAmount = {} repaymentAmount = {} result = {}", new Object[]{ billInfo.getBillAmount(), repayment.getRepaymentAmount(), result});
        return result;
    }

}
