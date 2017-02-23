package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.dal.dao.AfRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;

/**

 *@类描述：
 *@author hexin 2017年2月22日下午14:48:49
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRepaymentService")
public class AfRepaymentServiceImpl extends BaseService implements AfRepaymentService{

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
	
	@Override
	public int createRepayment(final BigDecimal repaymentAmount,
			final BigDecimal actualAmount,final AfUserCouponDto coupon,
			final BigDecimal rebateAmount,final String billIds,final Long cardId,final Long userId,final BigDecimal principleAmount) {
		final Date now = new Date();
		final String repayNo = generatorClusterNo.getRepaymentNo(now);
		//TODO 支付流程
		final String payTradeNo="",tradeNo="";
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					//新增还款记录
					AfRepaymentDo repayment = buildRepayment(repaymentAmount, repayNo, now, actualAmount,coupon, 
							rebateAmount, billIds, cardId, payTradeNo, tradeNo);
					afRepaymentDao.addRepayment(repayment);
					//变更账单状态
					afBorrowBillService.updateBorrowBillStatusByIds(billIds, BorrowBillStatus.YES.getCode());
					//判断该期是否还清，如已还清，更新total_bill 状态
					Map<String,Integer> map = afBorrowService.getCurrentYearAndMonth("",new Date());
					int year = map.get("year"),month = map.get("month");
					int count = afBorrowBillService.getUserMonthlyBillNotpayCount(year, month, userId);
					if(count==0){
						afBorrowBillService.updateTotalBillStatus(year, month, userId, BorrowBillStatus.YES.getCode());
					}
					if(null != coupon){
						//优惠券设置已使用
						afUserCouponDao.updateUserCouponSatusUsedById(coupon.getRid());
					}
					//获取现金借款还款本金
					AfBorrowBillDo cashBill = afBorrowBillService.getBillAmountByCashIds(billIds);
					BigDecimal cashAmount = cashBill==null?BigDecimal.ZERO:cashBill.getPrincipleAmount();
					//授权账户可用金额变更，以及变更日志
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(userId);
					account.setUcAmount(cashAmount.multiply(new BigDecimal(-1)));
					account.setUsedAmount(principleAmount.multiply(new BigDecimal(-1)));
					account.setRebateAmount(rebateAmount.multiply(new BigDecimal(-1)));
					afUserAccountDao.updateUserAccount(account);					
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENT,principleAmount,userId, billIds));
					return 1;
				} catch (Exception e) {
					logger.info("createRepayment error:", e);
					status.setRollbackOnly();
					return 0;
				}
			}
		});
		
	}

	@Override
	public String getCurrentLastRepayNo(Date current) {
		return afRepaymentDao.getCurrentLastRepayNo(current);
	}

	private AfRepaymentDo buildRepayment(BigDecimal repaymentAmount,String repayNo,Date gmtCreate,BigDecimal actualAmount,
			AfUserCouponDto coupon,BigDecimal rebateAmount, String billIds, Long cardId,String payTradeNo,String tradeNo){
		AfRepaymentDo repay = new AfRepaymentDo();
		repay.setActualAmount(actualAmount);
		repay.setBillIds(billIds);
		repay.setCardId(cardId);
		repay.setPayTradeNo(payTradeNo);
		repay.setRebateAmount(rebateAmount);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setTradeNo(tradeNo);
		if(null != coupon){
			repay.setUserCouponId(coupon.getRid());
			repay.setCouponAmount(coupon.getAmount());
		}
		return repay;
	}
	
	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type,BigDecimal amount,Long userId,String ids){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(ids);
		accountLog.setType(type.getCode());
		return accountLog;
	}
}
