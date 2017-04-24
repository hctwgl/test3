/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;

/**
 * @类描述：
 * @author suweili 2017年3月27日下午9:01:41
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRepaymentBorrowCashService")
public class AfRepaymentBorrowCashServiceImpl extends BaseService implements AfRepaymentBorrowCashService {

	@Resource
	AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfBorrowCashService afBorrowCashService ;
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
	AfUserService afUserService;
	
	@Resource
	UpsUtil upsUtil;
	
	@Override
	public int addRepaymentBorrowCash(AfRepaymentBorrowCashDo afRepaymentBorrowCashDo) {
		return afRepaymentBorrowCashDao.addRepaymentBorrowCash(afRepaymentBorrowCashDo);
	}


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


	
	private AfRepaymentBorrowCashDo buildRepayment(BigDecimal jfbAmount,BigDecimal repaymentAmount,String repayNo,Date gmtCreate,BigDecimal actualAmount,
			AfUserCouponDto coupon,BigDecimal rebateAmount, Long borrowId, Long cardId,String payTradeNo,String name,Long userId){
		AfRepaymentBorrowCashDo repay = new AfRepaymentBorrowCashDo();
		repay.setActualAmount(actualAmount);
		repay.setBorrowId(borrowId);;
		repay.setJfbAmount(jfbAmount);
		repay.setPayTradeNo(payTradeNo);
		repay.setRebateAmount(rebateAmount);
		repay.setRepaymentAmount(repaymentAmount);
		repay.setRepayNo(repayNo);
		repay.setGmtCreate(gmtCreate);
		repay.setStatus(AfBorrowCashRepmentStatus.APPLY.getCode());
		if(null != coupon){
			repay.setUserCouponId(coupon.getRid());
			repay.setCouponAmount(coupon.getAmount());
		}
		repay.setName(name);
		repay.setUserId(userId);
		if(cardId==-2){
			repay.setCardNumber("");
			repay.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		}else if(cardId==-1){
			repay.setCardNumber("");
			repay.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		}else{
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			repay.setCardNumber(bank.getCardNumber());
			repay.setCardName(bank.getBankName());
		}
		return repay;
	}


	@Override
	public Map<String, Object> createRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount,
			AfUserCouponDto coupon, BigDecimal rebateAmount, Long borrow, Long cardId, Long userId, String clientIp,
			AfUserAccountDo afUserAccountDo) {
		Date now = new Date();
		String repayNo = generatorClusterNo.getRepaymentBorrowCashNo(now);
		final String payTradeNo=repayNo;
		//新增还款记录
		String name =Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
		
		final AfRepaymentBorrowCashDo repayment = buildRepayment(jfbAmount,repaymentAmount, repayNo, now, actualAmount,coupon, 
				rebateAmount, borrow, cardId, payTradeNo,name,userId);
		Map<String,Object> map = new HashMap<String,Object>();
		afRepaymentBorrowCashDao.addRepaymentBorrowCash(repayment);
		if(cardId==-1){//微信支付
			map = UpsUtil.buildWxpayTradeOrder(payTradeNo, userId, name, actualAmount, PayOrderSource.REPAYMENTCASH.getCode());
		}else if(cardId>0){//银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
			UpsCollectRespBo respBo = upsUtil.collect(payTradeNo,actualAmount, userId+"", afUserAccountDo.getRealName(), bank.getMobile(), 
					bank.getBankCode(), bank.getCardNumber(), afUserAccountDo.getIdNumber(), 
					Constants.DEFAULT_PAY_PURPOSE, name, "02",UserAccountLogType.REPAYMENTCASH.getCode());
			if(respBo.isSuccess()){
				dealChangStatus(payTradeNo,"",AfBorrowCashRepmentStatus.PROCESS.getCode(),repayment.getRid());
			}else{
				dealRepaymentFail(payTradeNo,"");
			}
			map.put("resp", respBo);
		}else if(cardId==-2){//余额支付
			dealRepaymentSucess(repayment.getPayTradeNo(), "");
		}
		map.put("refId", repayment.getRid());
		map.put("type", UserAccountLogType.REPAYMENTCASH.getCode());

		return map;
	}


	
	@Override
	public long dealRepaymentSucess(final String outTradeNo, final String tradeNo) {
		
		return transactionTemplate.execute(new TransactionCallback<Long>() {

			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
					if(YesNoStatus.YES.getCode().equals(repayment.getStatus())){
						return 0l;
					}
					AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
					temRepayMent.setStatus(AfBorrowCashRepmentStatus.YES.getCode());
					temRepayMent.setTradeNo(tradeNo);
					temRepayMent.setRid(repayment.getRid());
					//变更还款记录为已还款
					afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
					
					
					AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(repayment.getBorrowId());
					BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount());
//					BigDecimal showAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
					AfBorrowCashDo bcashDo = new AfBorrowCashDo();
					bcashDo.setRid(afBorrowCashDo.getRid());
					BigDecimal repayAllAmount = afRepaymentBorrowCashDao.getRepaymentAllAmountByBorrowId(repayment.getBorrowId());
					if(allAmount.compareTo(repayAllAmount)==0){
						bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
					}
					bcashDo.setRepayAmount(repayAllAmount);

//					bcashDo.setRepayAmount(repayment.getRepaymentAmount());
//					if(showAmount.compareTo(repayment.getRepaymentAmount())==0){
//						bcashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
//					}
					afBorrowCashService.updateBorrowCash(bcashDo);
					//优惠券设置已使用
					afUserCouponDao.updateUserCouponSatusUsedById(repayment.getUserCouponId());
					
					//授权账户可用金额变更
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(repayment.getUserId());
					
					account.setRebateAmount(repayment.getRebateAmount().multiply(new BigDecimal(-1)));
					afUserAccountDao.updateUserAccount(account);
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.REPAYMENTCASH,repayment.getRebateAmount(),repayment.getUserId(), repayment.getRid()));
					return 1l;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealRepaymentSucess error",e);
					
					
					return 0l;
				}
			}
		});
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
	public long dealRepaymentFail(String outTradeNo, String tradeNo) {
		AfRepaymentBorrowCashDo repayment = afRepaymentBorrowCashDao.getRepaymentByPayTradeNo(outTradeNo);
		if(YesNoStatus.YES.getCode().equals(repayment.getStatus())){
			return 0l;
		}
	
		return dealChangStatus(outTradeNo,tradeNo,AfBorrowCashRepmentStatus.NO.getCode(),repayment.getRid());
	}
	
	long dealChangStatus(String outTradeNo, String tradeNo,String status,Long rid){
		AfRepaymentBorrowCashDo temRepayMent = new AfRepaymentBorrowCashDo();
		temRepayMent.setStatus(status);
		temRepayMent.setTradeNo(tradeNo);
		temRepayMent.setRid(rid);
		
		return afRepaymentBorrowCashDao.updateRepaymentBorrowCash(temRepayMent);
	}


	
	@Override
	public AfRepaymentBorrowCashDo getLastRepaymentBorrowCashByBorrowId(Long borrowCashId) {
		return afRepaymentBorrowCashDao.getLastRepaymentBorrowCashByBorrowId(borrowCashId);
	}


	
	@Override
	public BigDecimal getRepaymentAllAmountByBorrowId(Long borrowId) {
		return afRepaymentBorrowCashDao.getRepaymentAllAmountByBorrowId(borrowId);
	}
}
