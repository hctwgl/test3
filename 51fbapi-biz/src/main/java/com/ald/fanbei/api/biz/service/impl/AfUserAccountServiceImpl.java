package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
import com.ald.fanbei.api.dal.domain.dto.AfLimitDetailDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfLimitDetailQuery;
import com.ald.fanbei.api.dal.domain.query.AfUserAccountQuery;

/**
 * 
 * @类描述：
 * 
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

	@Resource
	AfTradeWithdrawRecordService afTradeWithdrawRecordService;
	
	@Resource
	SmsUtil smsUtil;

	@Resource
	AfTaskUserService afTaskUserService;

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
	public List<AfUserAccountDto> getUserAndAccountListWithHasRealName(AfUserAccountQuery query) {
		return afUserAccountDao.getUserAndAccountListWithHasRealName(query);
	}

	@Override
	public AfUserAccountDto getUserInfoByUserId(Long userId) {
		return afUserAccountDao.getUserInfoByUserId(userId);
	}

	@Override
	public int dealUserDelegatePayError(final String merPriv, final Long result) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					if (UserAccountLogType.CASH.getCode().equals(merPriv)) {// 现金借款
						// 借款关闭
						afBorrowDao.updateBorrowStatus(result, BorrowStatus.CLOSED.getCode());
						// 账户还原
						AfBorrowDo borrow = afBorrowDao.getBorrowById(result);
						AfUserAccountDo account = new AfUserAccountDo();
						account.setUcAmount(borrow.getAmount().multiply(new BigDecimal(-1)));
						account.setUsedAmount(borrow.getAmount().multiply(new BigDecimal(-1)));
						account.setUserId(borrow.getUserId());
						afUserAccountDao.updateUserAccount(account);
					} else if (UserAccountLogType.CONSUME.getCode().equals(merPriv)) {// 分期借款
						// 借款关闭
						afBorrowDao.updateBorrowStatus(result, BorrowStatus.CLOSED.getCode());
						// 账户还原
						AfBorrowDo borrow = afBorrowDao.getBorrowById(result);
						AfUserAccountDo account = new AfUserAccountDo();
						account.setUsedAmount(borrow.getAmount().multiply(new BigDecimal(-1)));
						account.setUserId(borrow.getUserId());
						afUserAccountDao.updateUserAccount(account);
					} else if (UserAccountLogType.REBATE_CASH.getCode().equals(merPriv)) {// 提现
						AfCashRecordDo record = afCashRecordDao.getCashRecordById(result);
						record.setStatus("REFUSE");
						afCashRecordDao.updateCashRecord(record);
						//
						AfUserAccountDo updateAccountDo = new AfUserAccountDo();
						updateAccountDo.setRebateAmount(record.getAmount());
						updateAccountDo.setUserId(record.getUserId());
						afUserAccountDao.updateUserAccount(updateAccountDo);

						// add by luoxiao for 边逛边赚，增加零钱明细
						afTaskUserService.addTaskUser(record.getUserId(),UserAccountLogType.CASH_FAILD_REFUND.getName(), record.getAmount());
                        // end by luoxiao
					} else if (UserAccountLogType.BANK_REFUND.getCode().equals(merPriv)) {// 菠萝觅银行卡退款
						AfOrderRefundDo refundInfo = afOrderRefundService.getRefundInfoById(result);
						AfOrderDo orderInfo = afOrderService.getOrderById(refundInfo.getOrderId());
						orderInfo.setStatus(OrderStatus.PAID.getCode());
						afOrderService.updateOrder(orderInfo);

						// 订单退款记录
						refundInfo.setStatus(OrderRefundStatus.FAIL.getCode());
						afOrderRefundService.updateOrderRefund(refundInfo);
						boluomeUtil.pushRefundStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.REFUND_FAIL, orderInfo.getUserId(),
								refundInfo.getAmount(), refundInfo.getRefundNo());
					} else if (UserAccountLogType.BorrowCash.getCode().equals(merPriv)) {
						// 借钱
						
						Long rid = NumberUtil.objToLong(result);
						AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
						// 回调只处理打款中的
						if(!afBorrowCashDo.getStatus().equals("TRANSEDING")){
							return 0;
						}
						
						afBorrowCashDo.setStatus("TRANSEDFAIL");
						afBorrowCashService.updateBorrowCash(afBorrowCashDo);
						
						
						// 如果已经使用的额度大于要恢复的额度 才会给用户增加额度，防止重复回调造成重复给我用户增加额度问题
						AfUserAccountDo userAccountDo = afUserAccountDao.getUserAccountInfoByUserId(afBorrowCashDo.getUserId());
						if(userAccountDo.getUsedAmount().intValue() >= afBorrowCashDo.getAmount().intValue()){
							// 恢复账户额度
							AfUserAccountDo account = new AfUserAccountDo();
							account.setUsedAmount(afBorrowCashDo.getAmount().negate());
							account.setUserId(afBorrowCashDo.getUserId());
							afUserAccountDao.updateUserAccount(account);
							//fmf——增加log记录
							AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
							accountLog.setUserId(afBorrowCashDo.getUserId());
							accountLog.setAmount(afBorrowCashDo.getAmount());
							accountLog.setType("TRANSEDFAIL_USEAMOUNT");
							accountLog.setRefId(afBorrowCashDo.getRid()+"");
							try{
								afUserAccountLogDao.addUserAccountLog(accountLog);
							}catch(Exception e){
								throw new FanbeiException("TRANSEDFAIL_USEAMOUNT "+afBorrowCashDo.getRid()+" is fail,"+e);
							}
						}
						
						//打款失败消息通知用户
						//用户当日打款失败次数
						int applyBorrowCashFailTimes = afBorrowCashService.getCurrDayTransFailTimes(afBorrowCashDo.getUserId());
						smsUtil.sendApplyBorrowTransedFail(userAccountDo.getUserName(),afBorrowCashDo.getCardName(),StringUtil.getLastAppointLengthChar(afBorrowCashDo.getCardNumber(),4),applyBorrowCashFailTimes);

					} else if (UserAccountLogType.NORMAL_BANK_REFUND.getCode().equals(merPriv)) {
						AfOrderRefundDo refundInfo = afOrderRefundService.getRefundInfoById(result);
						AfOrderDo orderInfo = afOrderService.getOrderById(refundInfo.getOrderId());
						afOrderRefundService.dealWithSelfGoodsOrderRefundFail(refundInfo, orderInfo);
					} else if (UserAccountLogType.TRADE_BANK_REFUND.getCode().equals(merPriv)) {
						AfOrderRefundDo refundInfo = afOrderRefundService.getRefundInfoById(result);
						AfOrderDo orderInfo = afOrderService.getOrderById(refundInfo.getOrderId());
						afOrderRefundService.dealWithTradeRefundFail(refundInfo, orderInfo);
					} else if (UserAccountLogType.TRADE_WITHDRAW.getCode().equals(merPriv)) {
						afTradeWithdrawRecordService.dealWithDrawFail(result);
					}
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}

	@Override
	public void updateUserAccountRealNameAndIdNumber(AfUserAccountDto accountDo) {
		afUserAccountDao.updateUserAccountRealNameAndIdNumber(accountDo);
	}

	@Override
	public Integer getCountByIdNumer(String citizenId, Long userId) {
		return afUserAccountDao.getCountByIdNumer(citizenId, userId);
	}

	@Override
	public int updateOriginalUserAccount(AfUserAccountDo afUserAccountDo) {
		return afUserAccountDao.updateOriginalUserAccount(afUserAccountDo);
	}

	@Override
	public AfUserAccountDo getUserAccountInfoByUserName(String userName) {
		return afUserAccountDao.getUserAccountInfoByUserName(userName);
	}

	@Override
	public int updateBorrowCashActivity(int money, List<String> userId) {
		//af_user_account_log添加记录
		for (String string : userId) {
			AfUserAccountLogDo userAccountLog=new AfUserAccountLogDo();
			userAccountLog.setAmount(new BigDecimal(money));
			userAccountLog.setUserId(Long.parseLong(string));
			userAccountLog.setType("BORROWCASH_ACTIVITYS");
			userAccountLog.setRefId(" ");
			try{
				afUserAccountLogDao.addUserAccountLog(userAccountLog);
			}catch(Exception e){
				throw new FanbeiException("addUserAccountLog "+userId+" is fail,"+e);
			}
		}
		return afUserAccountDao.updateBorrowCashActivity(money, userId);
	}

	@Override
	public BigDecimal getAuAmountByUserId(long userId) {
		return afUserAccountDao.getAuAmountByUserId(userId);
	}

	@Override
	public int updateUserAccountByUserId(Long userId,int money) {
		return afUserAccountDao.updateUserAccountByUserId(userId,money);
	}

	@Override
	public AfUserAccountDo findByIdNo(String idNo) {
		
		return afUserAccountDao.findByIdNo(idNo);
	}

	@Override
	public int updateRebateAmount(AfUserAccountDo afUserAccountDo){
		return afUserAccountDao.updateRebateAmount(afUserAccountDo);
	}

}
