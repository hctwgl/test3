package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.UpsLogStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfCashRecordDao;
import com.ald.fanbei.api.dal.dao.AfUpsLogDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfLimitDetailDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfLimitDetailQuery;
import com.ald.fanbei.api.dal.domain.query.AfUserAccountQuery;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:05:08
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAccountService")
public class AfUserAccountServiceImpl implements AfUserAccountService {

	@Resource
	AfUserAccountDao afUserAccountDao;
	
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Resource
	AfBorrowDao afBorrowDao;
	
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	
	@Resource
	private AfCashRecordDao afCashRecordDao;
	
	@Resource
	AfOrderService afOrderService;
	
	@Resource
	AfUserBankcardService afUserBankcardService;
	
	@Resource
	AfOrderRefundService afOrderRefundService;
	
	@Resource
	AfUpsLogDao afUpsLogDao;
	
	@Resource
	BoluomeUtil boluomeUtil;
	
	@Resource
	AfBorrowCashService afBorrowCashService;
	
	@Override
	public AfUserAccountDo getUserAccountByUserId(Long userId) {
		return afUserAccountDao.getUserAccountInfoByUserId(userId);
	}

	@Override
	public int addUserAccount(AfUserAccountDo accountDo) {
		return afUserAccountDao.addUserAccount(accountDo);
	}
	
	@Override
	public int updateUserAccount(AfUserAccountDo afUserAccountDo) {
		return afUserAccountDao.updateUserAccount(afUserAccountDo);
	}

	@Override
	public AfUserAccountDto getUserAndAccountByUserId(Long userId) {
		return afUserAccountDao.getUserAndAccountByUserId(userId);
	}

	@Override
	public int addUserAccountLog(AfUserAccountLogDo afUserAccountLogDo) {
		return afUserAccountLogDao.addUserAccountLog(afUserAccountLogDo);
	}

	@Override
	public List<AfLimitDetailDto> getLimitDetailList(AfLimitDetailQuery query) {
		return afUserAccountLogDao.getLimitDetailList(query);
	}

	@Override
	public int getUserAccountCountWithHasRealName() {
		return afUserAccountDao.getUserAccountCountWithHasRealName();
	}

	@Override
	public List<AfUserAccountDto> getUserAndAccountListWithHasRealName(
			AfUserAccountQuery query) {
		return afUserAccountDao.getUserAndAccountListWithHasRealName(query);
	}

	@Override
	public int dealUserDelegatePayError(final String merPriv,final Long result) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					if(UserAccountLogType.CASH.getCode().equals(merPriv)){//现金借款
						//借款关闭
						afBorrowDao.updateBorrowStatus(result, BorrowStatus.CLOSED.getCode());
						//账户还原
						AfBorrowDo borrow = afBorrowDao.getBorrowById(result);
						AfUserAccountDo account = new AfUserAccountDo();
						account.setUcAmount(borrow.getAmount().multiply(new BigDecimal(-1)));
						account.setUsedAmount(borrow.getAmount().multiply(new BigDecimal(-1)));
						account.setUserId(borrow.getUserId());
						afUserAccountDao.updateUserAccount(account);
					}else if(UserAccountLogType.CONSUME.getCode().equals(merPriv)){//分期借款
						//借款关闭
						afBorrowDao.updateBorrowStatus(result, BorrowStatus.CLOSED.getCode());
						//账户还原
						AfBorrowDo borrow = afBorrowDao.getBorrowById(result);
						AfUserAccountDo account = new AfUserAccountDo();
						account.setUsedAmount(borrow.getAmount().multiply(new BigDecimal(-1)));
						account.setUserId(borrow.getUserId());
						afUserAccountDao.updateUserAccount(account);
					}else if(UserAccountLogType.REBATE_CASH.getCode().equals(merPriv)){//提现
						AfCashRecordDo record = afCashRecordDao.getCashRecordById(result);
						record.setStatus("REFUSE");
						afCashRecordDao.updateCashRecord(record);
						//
						AfUserAccountDo updateAccountDo = new AfUserAccountDo();
						updateAccountDo.setRebateAmount(record.getAmount());
						updateAccountDo.setUserId(record.getUserId());
						afUserAccountDao.updateUserAccount(updateAccountDo);
					} else if (UserAccountLogType.BANK_REFUND.getCode().equals(merPriv)) {//菠萝觅银行卡退款
	        			AfOrderDo orderInfo = afOrderService.getOrderById(result);
	        			orderInfo.setStatus(OrderStatus.PAID.getCode());
	        			afOrderService.updateOrder(orderInfo);
	        			
	        			AfUserBankcardDo cardInfo = afUserBankcardService.getUserBankcardById(orderInfo.getBankId());
	        			
	        			//订单退款记录
	        			AfOrderRefundDo refundInfo = afOrderRefundService.getOrderRefundByOrderId(result);
	    				refundInfo.setStatus(OrderRefundStatus.FAIL.getCode());
	    				afOrderRefundService.updateOrderRefund(refundInfo);
	    				
	        			//ups打款记录
	        			afUpsLogDao.addUpsLog(BuildInfoUtil.buildUpsLog(cardInfo.getBankName(), cardInfo.getCardNumber(), "delegatePay", orderInfo.getOrderNo(), 
	        					result+StringUtils.EMPTY, merPriv, orderInfo.getUserId() + StringUtils.EMPTY, UpsLogStatus.FAIL.getCode()));
	        			
	        			boluomeUtil.pushRefundStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.REFUND_FAIL, orderInfo.getUserId(), refundInfo.getAmount());
	        		} else if(UserAccountLogType.BorrowCash.getCode().equals(merPriv)){
	        			//借钱
	        			Long rid = NumberUtil.objToLong(result);
	        			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
	        			afBorrowCashDo.setStatus("TRANSEDFAIL");
	        			afBorrowCashService.updateBorrowCash(afBorrowCashDo);
	        		}
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}

}
