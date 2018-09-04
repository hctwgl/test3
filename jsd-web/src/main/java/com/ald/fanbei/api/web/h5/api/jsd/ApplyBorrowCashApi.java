package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo;
import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.ApplyBorrowCashReq;
import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.JsdGoodsInfoBo;
import com.ald.fanbei.api.biz.bo.ups.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.service.impl.JsdResourceServiceImpl.ResourceRateInfoBo;
import com.ald.fanbei.api.biz.third.util.OriRateUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：申请借钱
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBorrowCashApi")
@Validator("applyBorrowCashReq")
public class ApplyBorrowCashApi implements JsdH5Handle {

    // [start] 依赖注入
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdUserBankcardService jsdUserBankcardService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    
    @Resource
    UpsUtil upsUtil;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    OriRateUtil oriRateUtil;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    // [end]
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	this.lock(context.getUserId());
    	try {
	        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
	        
	        ApplyBorrowCashBo bo = new ApplyBorrowCashBo();
	        ApplyBorrowCashReq req = bo.req = (ApplyBorrowCashReq)context.getParamEntity();
	        bo.userId = context.getUserId();
        	
        	BigDecimal oriRate = jsdBorrowCashService.getRiskOriRate(req.openId);
            JsdUserBankcardDo mainCard = jsdUserBankcardService.getByBankNo(req.bankNo);
           
            final JsdBorrowCashDo cashDo = buildBorrowCashDo(bo, mainCard, oriRate); 		// 主借款
            final JsdBorrowLegalOrderDo orderDo = buildBorrowLegalOrder(bo);				// 搭售商品订单
            final JsdBorrowLegalOrderCashDo orderCashDo = buildBorrowLegalOrderCashDo(bo);	// 订单借款

            transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus arg0) {
                    jsdBorrowCashService.saveRecord(cashDo);
                    
                    Long borrowId = cashDo.getRid();
                    orderDo.setBorrowId(borrowId);
                    jsdBorrowLegalOrderService.saveRecord(orderDo);
                    
                    Long orderId = orderDo.getRid();
                    orderCashDo.setBorrowId(borrowId);
                    orderCashDo.setBorrowLegalOrderId(orderId);
                    jsdBorrowLegalOrderCashService.saveRecord(orderCashDo);
                    return borrowId;
                }
            });
            
            try {
                UpsDelegatePayRespBo upsResult = upsUtil.jsdDelegatePay(req.amount, context.getRealName(), 
                        mainCard.getBankCardNumber(), context.getUserId().toString(), mainCard.getMobile(),
                        mainCard.getBankName(), mainCard.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
                        "JSD_LOAN", cashDo.getRid().toString(), context.getIdNumber());
                
                if (!upsResult.isSuccess()) {
                    cashDo.setStatus(JsdBorrowCashStatus.CLOSED.name());
                    cashDo.setRemark("UPS实时打款失败");
                    orderDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.name());
                    orderDo.setClosedDetail("transed fail");
                    orderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.name());
                    this.transUpdate(cashDo, orderDo, orderCashDo);
                }else {
                	cashDo.setStatus(JsdBorrowCashStatus.TRANSFERING.name());
                	jsdBorrowCashService.updateById(cashDo);
                }
            	
                return resp;
            } catch (Exception e) {
                logger.error("apply legal borrow cash  error", e);
                cashDo.setStatus(JsdBorrowCashStatus.CLOSED.name());// 关闭借款
                cashDo.setRemark("Exception when delegatePay!");
                orderDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.name());// 关闭搭售商品订单
                orderCashDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.name());// 关闭搭售商品借款
                this.transUpdate(cashDo, orderDo, orderCashDo);
                
                throw new FanbeiException(FanbeiExceptionCode.DELEGATEPAY_DIRECT_FAIL);
            }
        } finally {
            this.unLock(context.getUserId());
        }
    }

    /**
     * 构建 主借款
     * @return
     */
    private JsdBorrowCashDo buildBorrowCashDo(ApplyBorrowCashBo bo, JsdUserBankcardDo mainCard, BigDecimal riskRateDaily) {
    	ApplyBorrowCashReq req = bo.req;
    	
    	ResourceRateInfoBo rateInfo = jsdResourceService.getRateInfo(req.term);
    	
        BigDecimal interestRateDaily = rateInfo.interestRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
        BigDecimal serviceRateDaily = rateInfo.serviceRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
        
        BigDecimal serviceAmount = req.amount.multiply(serviceRateDaily).multiply(new BigDecimal(req.term));
        BigDecimal interestAmount = req.amount.multiply(interestRateDaily).multiply(new BigDecimal(req.term));
        
        JsdBorrowCashDo afBorrowCashDo = new JsdBorrowCashDo();
        afBorrowCashDo.setAmount(req.amount);
        afBorrowCashDo.setArrivalAmount(req.amount);// 到账金额和借款金额相同
        afBorrowCashDo.setCardName(mainCard.getBankName());
        afBorrowCashDo.setCardNumber(mainCard.getBankCardNumber());
        afBorrowCashDo.setType(req.term);
        afBorrowCashDo.setStatus(JsdBorrowCashStatus.APPLY.name());
        afBorrowCashDo.setUserId(bo.userId);
        afBorrowCashDo.setRateAmount(interestAmount);
        afBorrowCashDo.setPoundage(serviceAmount);
        afBorrowCashDo.setPoundageRate(serviceRateDaily.setScale(2));
        afBorrowCashDo.setRiskDailyRate(riskRateDaily);
        afBorrowCashDo.setProductNo(req.productNo);
        afBorrowCashDo.setTradeNoXgxy(req.borrowNo);
        afBorrowCashDo.setBorrowNo(generatorClusterNo.getLoanNo(new Date()));
        afBorrowCashDo.setRepayPrinciple(BigDecimal.ZERO);
        return afBorrowCashDo;
    }
    
    /**
     * 构建 商品订单
     * @return
     */
    private JsdBorrowLegalOrderDo buildBorrowLegalOrder(ApplyBorrowCashBo bo) {
    	JsdGoodsInfoBo goodsBo = bo.req.jsdGoodsInfoBo;
    	
        JsdBorrowLegalOrderDo afBorrowLegalOrderDo = new JsdBorrowLegalOrderDo();
        afBorrowLegalOrderDo.setUserId(bo.userId);
        afBorrowLegalOrderDo.setPriceAmount(new BigDecimal(goodsBo.goodsPrice));
        afBorrowLegalOrderDo.setGoodsName(goodsBo.goodsName);
        afBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderStatus.UNPAID.getCode());
        String orderCashNo = generatorClusterNo.getOrderNo(OrderType.LEGAL);
        afBorrowLegalOrderDo.setOrderNo(orderCashNo);
        return afBorrowLegalOrderDo;
    }
    
    /**
     * 构建 商品借款 
     * @return
     */
    private JsdBorrowLegalOrderCashDo buildBorrowLegalOrderCashDo(ApplyBorrowCashBo bo) {
    	ApplyBorrowCashReq req = bo.req;
    	JsdGoodsInfoBo goodsBo = req.jsdGoodsInfoBo;
    	
        JsdBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = new JsdBorrowLegalOrderCashDo();
        afBorrowLegalOrderCashDo.setAmount(new BigDecimal(goodsBo.goodsPrice));
        afBorrowLegalOrderCashDo.setType(req.term);
        afBorrowLegalOrderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.APPLYING.getCode());
        afBorrowLegalOrderCashDo.setUserId(bo.userId);
        afBorrowLegalOrderCashDo.setPoundageRate(BigDecimal.ZERO);
        afBorrowLegalOrderCashDo.setInterestRate(BigDecimal.ZERO);
        afBorrowLegalOrderCashDo.setBorrowRemark(req.loanRemark);
        afBorrowLegalOrderCashDo.setRefundRemark(req.repayRemark);
        afBorrowLegalOrderCashDo.setInterestAmount(BigDecimal.ZERO);
        afBorrowLegalOrderCashDo.setPoundageAmount(new BigDecimal(10));
        afBorrowLegalOrderCashDo.setOverdueAmount(BigDecimal.ZERO);
        afBorrowLegalOrderCashDo.setOverdueStatus(YesNoStatus.NO.getCode());
        afBorrowLegalOrderCashDo.setCashNo(generatorClusterNo.geBorrowLegalOrderCashNo(new Date()));
        return afBorrowLegalOrderCashDo;
    }

    private void transUpdate(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo, final JsdBorrowLegalOrderCashDo orderCashDo) {
    	transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus ts) {
                jsdBorrowCashService.updateById(cashDo);
                jsdBorrowLegalOrderCashService.updateById(orderCashDo);
                jsdBorrowLegalOrderService.updateById(orderDo);
                return "success";
            }
        });
    }
    
    private void lock(Long userId) {
    	String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
        if (!bizCacheUtil.getLock30Second(lockKey, "1")) {
        	throw new FanbeiException(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
        }
    }
    private void unLock(Long userId) {
    	String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
    	bizCacheUtil.delCache(lockKey);
    }
}
