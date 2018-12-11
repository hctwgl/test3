package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.ApplyBorrowCashReq;
import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.JsdGoodsInfoBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo.TrialBeforeBorrowResp;
import com.ald.fanbei.api.biz.bo.ups.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.service.BeheadBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowVersionType;
import com.ald.fanbei.api.common.enums.JsdBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;

/**
 * @author GSQ 2017年3月25日下午1:06:18
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
    JsdUserBankcardService jsdUserBankcardService;
    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    
    @Resource
    UpsUtil upsUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    BeheadBorrowCashService beheadBorrowCashService;
    // [end]
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	this.lock(context.getUserId());
    	try {
	        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
	        ApplyBorrowCashReq cashReq = (ApplyBorrowCashReq)context.getParamEntity();
        	
	        jsdBorrowCashService.checkCanBorrow(context.getUserId(), cashReq.amount);
	        
            JsdUserBankcardDo mainCard = jsdUserBankcardService.getByBankNo(cashReq.bankNo,context.getUserId());
           
            TrialBeforeBorrowBo trialBo = new TrialBeforeBorrowBo();
	    	trialBo.req = new TrialBeforeBorrowReq(cashReq.openId, cashReq.amount, cashReq.term, cashReq.unit);
			trialBo.userId = context.getUserId();
			trialBo.riskDailyRate = jsdBorrowCashService.getRiskDailyRate(cashReq.openId,cashReq.term,cashReq.unit);
        	
        	if("Y".equals(cashReq.isTying) && BorrowVersionType.BEHEAD.name().equals(cashReq.tyingType)){
        		// 砍头模式
        		beheadBorrowCashService.applyBeheadBorrowCash(cashReq, mainCard, trialBo);
        		return resp;
        	}
        	else if("Y".equals(cashReq.isTying) && BorrowVersionType.SELL.name().equals(cashReq.tyingType)){
        		// 赊销模式
        		jsdBorrowCashService.resolve(trialBo);
        		final JsdBorrowCashDo cashDo = buildBorrowCashDo(cashReq, mainCard, trialBo); 				// 主借款
        		final JsdBorrowLegalOrderDo orderDo = buildBorrowLegalOrder(cashReq, context.getUserId(), trialBo);	// 搭售商品订单
        		final JsdBorrowLegalOrderCashDo orderCashDo = buildBorrowLegalOrderCashDo(cashReq, trialBo);// 订单借款
        		
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
        		
        		new Thread() { public void run() {
        			try {
        				UpsDelegatePayRespBo upsResult = upsUtil.jsdDelegatePay(cashDo.getArrivalAmount(), context.getRealName(), 
        						mainCard.getBankCardNumber(), context.getUserId().toString(), mainCard.getMobile(),
        						mainCard.getBankName(), mainCard.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
        						PayOrderSource.JSD_LOAN.getCode(), cashDo.getRid().toString(), context.getIdNumber());
        				cashDo.setTradeNoUps(upsResult.getOrderNo());
        				
        				if (!upsResult.isSuccess()) {
        					jsdBorrowCashService.dealBorrowFail(cashDo, orderDo, orderCashDo, "UPS打款实时反馈失败");
        				}else {
        					cashDo.setStatus(JsdBorrowCashStatus.TRANSFERING.name());
        					jsdBorrowCashService.updateById(cashDo);
        				}
        			} catch (Exception e) {
        				logger.error(e.getMessage(), e);
        				jsdBorrowCashService.dealBorrowFail(cashDo, orderDo, orderCashDo, "UPS打款时发生异常");
        			}
        		}}.start();
        	}
        	else {
        		throw new BizException("applyBorrowCashApi type error, isTying="+cashReq.isTying+", tyingType"+cashReq.tyingType);
        	}
	    	
            
            return resp;
        } finally {
            this.unLock(context.getUserId());
        }
    }

    /**
     * 构建 主借款(赊销)
     * @return
     */
    private JsdBorrowCashDo buildBorrowCashDo(ApplyBorrowCashReq cashReq, JsdUserBankcardDo mainCard, TrialBeforeBorrowBo trialBo) {
    	TrialBeforeBorrowReq trialReq = trialBo.req;
    	TrialBeforeBorrowResp trialResp = trialBo.resp;
    	
        JsdBorrowCashDo afBorrowCashDo = new JsdBorrowCashDo();
        afBorrowCashDo.setAmount(new BigDecimal( trialResp.borrowAmount) );
        afBorrowCashDo.setArrivalAmount(new BigDecimal( trialResp.arrivalAmount ));// 到账金额和借款金额相同
        afBorrowCashDo.setCardName(mainCard.getBankName());
        afBorrowCashDo.setCardNumber(mainCard.getBankCardNumber());
        afBorrowCashDo.setType(trialReq.term);
        afBorrowCashDo.setStatus(JsdBorrowCashStatus.APPLY.name());
        afBorrowCashDo.setUserId(trialBo.userId);
        afBorrowCashDo.setInterestAmount(new BigDecimal(trialResp.interestAmount));
        afBorrowCashDo.setPoundageAmount(new BigDecimal(trialResp.serviceAmount));
        afBorrowCashDo.setPoundageRate(new BigDecimal(trialResp.serviceRate));
        afBorrowCashDo.setInterestRate(new BigDecimal(trialResp.interestRate));
        afBorrowCashDo.setOverdueRate(new BigDecimal(trialResp.overdueYearRate));
        afBorrowCashDo.setRiskDailyRate(trialBo.riskDailyRate);
        afBorrowCashDo.setProductNo(trialReq.productNo);
        afBorrowCashDo.setTradeNoXgxy(cashReq.borrowNo);
        afBorrowCashDo.setBorrowNo(generatorClusterNo.getLoanNo(new Date()));
        afBorrowCashDo.setRepayPrinciple(BigDecimal.ZERO);
        afBorrowCashDo.setVersion(BorrowVersionType.SELL.name());
        afBorrowCashDo.setBorrowRemark(cashReq.loanRemark);
        afBorrowCashDo.setRepayRemark(cashReq.repayRemark);
        afBorrowCashDo.setReviewStatus(JsdBorrowCashReviewStatus.PASS.name()); // 赊销无审批
        return afBorrowCashDo;
    }
    
    /**
     * 构建 商品订单(赊销)
     * @return
     */
    private JsdBorrowLegalOrderDo buildBorrowLegalOrder(ApplyBorrowCashReq cashReq, Long userId, TrialBeforeBorrowBo trialBo) {
    	JsdGoodsInfoBo goodsBo = cashReq.goodsInfo;
    	TrialBeforeBorrowResp resp = trialBo.resp;
    	
        JsdBorrowLegalOrderDo afBorrowLegalOrderDo = new JsdBorrowLegalOrderDo();
        afBorrowLegalOrderDo.setUserId(userId);
        afBorrowLegalOrderDo.setPriceAmount(new BigDecimal(resp.totalDiffFee));
        afBorrowLegalOrderDo.setGoodsName(goodsBo.goodsName);
        afBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderStatus.UNPAID.getCode());
        String orderCashNo = generatorClusterNo.getOrderNo(new Date());
        afBorrowLegalOrderDo.setOrderNo(orderCashNo);
        return afBorrowLegalOrderDo;
    }
    
    /**
     * 构建 商品借款 (赊销)
     * @return
     */
    private JsdBorrowLegalOrderCashDo buildBorrowLegalOrderCashDo(ApplyBorrowCashReq cashReq, TrialBeforeBorrowBo trialBo) {
    	TrialBeforeBorrowResp resp = trialBo.resp;
    	
        JsdBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = new JsdBorrowLegalOrderCashDo();
        afBorrowLegalOrderCashDo.setAmount(new BigDecimal(resp.totalDiffFee));
        afBorrowLegalOrderCashDo.setType(cashReq.term);
        afBorrowLegalOrderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.APPLYING.getCode());
        afBorrowLegalOrderCashDo.setUserId(trialBo.userId);
        afBorrowLegalOrderCashDo.setPoundageRate(resp.sellServiceRate);
        afBorrowLegalOrderCashDo.setInterestRate(resp.sellInterestRate);
        afBorrowLegalOrderCashDo.setOverdueRate(resp.sellOverdueRate.multiply(new BigDecimal(360)));
        afBorrowLegalOrderCashDo.setBorrowRemark(cashReq.loanRemark);
        afBorrowLegalOrderCashDo.setRefundRemark(cashReq.repayRemark);
        afBorrowLegalOrderCashDo.setInterestAmount(new BigDecimal(resp.sellInterestFee));
        afBorrowLegalOrderCashDo.setPoundageAmount(new BigDecimal(resp.sellServiceFee));
        afBorrowLegalOrderCashDo.setOverdueAmount(BigDecimal.ZERO);
        afBorrowLegalOrderCashDo.setOverdueStatus(YesNoStatus.NO.getCode());
        afBorrowLegalOrderCashDo.setCashNo(generatorClusterNo.getBorrowLegalOrderCashNo(new Date()));
        return afBorrowLegalOrderCashDo;
    }

    private void lock(Long userId) {
    	String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
        if (!bizCacheUtil.getLock30Second(lockKey, "1")) {
        	throw new BizException(BizExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
        }
    }
    private void unLock(Long userId) {
    	String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
    	bizCacheUtil.delCache(lockKey);
    }
}
