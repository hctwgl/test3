package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsAuthPayRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.RepaymentStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.WxOrderSource;
import com.ald.fanbei.api.dal.dao.AfRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
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
	
	@Resource
	private JpushService pushService;
	
	@Resource
	private AfUserService afUserService;
	
	@Resource
	private UpsUtil upsUtil;
	
	@Override
	public Map<String,Object> createRepayment(BigDecimal repaymentAmount,
			BigDecimal actualAmount,AfUserCouponDto coupon,
			BigDecimal rebateAmount,String billIds,Long cardId,Long userId,AfBorrowBillDo billDo) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRepaymentNo(now);
		String payTradeNo=repayNo;
		//新增还款记录
		String name =billDo.getName();
		if(billDo.getCount()>1){
			name=new StringBuffer(billDo.getBillYear()).append("年")
					.append(billDo.getBillMonth()).append("月账单").toString();
		}
		AfRepaymentDo repayment = buildRepayment(repaymentAmount, repayNo, now, actualAmount,coupon, 
				rebateAmount, billIds, cardId, payTradeNo,name,userId);
		afRepaymentDao.addRepayment(repayment);
		Map<String,Object> map;
		if(cardId<0){//微信支付
			map = upsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, repaymentAmount, WxOrderSource.REPAYMENT.getCode());
		}else{
			map = new HashMap<String,Object>();
			UpsAuthPayRespBo respBo = new UpsAuthPayRespBo();
			respBo.setTradeState("00");
			respBo.setTradeNo("10000000000000");
			respBo.setOrderNo(payTradeNo);
			map.put("resp", respBo);
		}
		map.put("refId", repayment.getRid());
		map.put("type", UserAccountLogType.REPAYMENT.getCode());
		return map;
	}

	@Override
	public String getCurrentLastRepayNo(Date current) {
		return afRepaymentDao.getCurrentLastRepayNo(current);
	}

	private AfRepaymentDo buildRepayment(BigDecimal repaymentAmount,String repayNo,Date gmtCreate,BigDecimal actualAmount,
			AfUserCouponDto coupon,BigDecimal rebateAmount, String billIds, Long cardId,String payTradeNo,String name,Long userId){
		AfRepaymentDo repay = new AfRepaymentDo();
		repay.setActualAmount(actualAmount);
		repay.setBillIds(billIds);
		repay.setCardId(cardId);
		repay.setPayTradeNo(payTradeNo);
		repay.setRebateAmount(rebateAmount);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(RepaymentStatus.NEW.getCode());
		if(null != coupon){
			repay.setUserCouponId(coupon.getRid());
			repay.setCouponAmount(coupon.getAmount());
		}
		repay.setName(name);
		repay.setUserId(userId);
		return repay;
	}
	
	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type,BigDecimal amount,Long userId,Long repaymentId){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(repaymentId+"");
		accountLog.setType(type.getCode());
		return accountLog;
	}

	@Override
	public AfRepaymentDo getRepaymentById(Long rid) {
		return afRepaymentDao.getRepaymentById(rid);
	}

	@Override
	public long dealRepaymentSucess(String outTradeNo, String tradeNo) {
		AfRepaymentDo repayment = afRepaymentDao.getRepaymentByPayTradeNo(outTradeNo);
		//变更还款记录为已还款
		afRepaymentDao.updateRepayment(RepaymentStatus.YES.getCode(),tradeNo, repayment.getRid());
		AfBorrowBillDo billDo = afBorrowBillService.getBillAmountByIds(repayment.getBillIds());
		AfUserDo userDo = afUserService.getUserById(repayment.getUserId());
		//变更账单 借款表状态
		afBorrowBillService.updateBorrowBillStatusByIds(repayment.getBillIds(), BorrowBillStatus.YES.getCode(),repayment.getRid());
		//判断该期是否还清，如已还清，更新total_bill 状态
		int count = afBorrowBillService.getUserMonthlyBillNotpayCount(billDo.getBillYear(), billDo.getBillMonth(), userDo.getRid());
		if(count==0){
			afBorrowBillService.updateTotalBillStatus(billDo.getBillYear(), billDo.getBillMonth(), userDo.getRid(), BorrowBillStatus.YES.getCode());
			pushService.repayBillSuccess(userDo.getUserName(), billDo.getBillYear()+"", String.format("%02d", billDo.getBillMonth()));
		}
		//优惠券设置已使用
		afUserCouponDao.updateUserCouponSatusUsedById(repayment.getUserCouponId());
		//获取现金借款还款本金
		AfBorrowBillDo cashBill = afBorrowBillService.getBillAmountByCashIds(repayment.getBillIds());
		BigDecimal cashAmount = cashBill==null?BigDecimal.ZERO:cashBill.getPrincipleAmount();
		//授权账户可用金额变更，以及变更日志
		AfUserAccountDo account = new AfUserAccountDo();
		account.setUserId(repayment.getUserId());
		account.setUcAmount(cashAmount.multiply(new BigDecimal(-1)));
		account.setUsedAmount(billDo.getPrincipleAmount().multiply(new BigDecimal(-1)));
		account.setRebateAmount(repayment.getRebateAmount().multiply(new BigDecimal(-1)));
		afUserAccountDao.updateUserAccount(account);					
		afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENT,billDo.getPrincipleAmount(),repayment.getUserId(), repayment.getRid()));
		return 0;
	}
}
