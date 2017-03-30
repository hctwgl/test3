package com.ald.fanbei.api.biz.service;

import java.util.Date;

/**
 * 
 *@类描述：
 *@author 何鑫 2017年2月27日  上午9:53:32
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JpushService {

	/**
	 * 【邀请注册成功】温馨提示
	 * @param userName
	 * @param name
	 */
	public void userInviteSuccess(String userName,String mobile);
	
	/**
	 * 【手机充值失败】温馨提示
	 * @param userName
	 * @param mobile
	 * @param date
	 */
	public void chargeMobileError(String userName,String mobile,Date date);
	
	/**
	 * 【手机充值成功】温馨提示
	 * @param userName
	 * @param mobile
	 */
	public void chargeMobileSucc(String userName,String mobile,Date date);
	
	/**
	 * 【账单已还清】温馨提示
	 * @param userName
	 * @param year
	 * @param month
	 */
	public void repayBillSuccess(String userName,String year, String month);
	
	/**
	 * 【余额提现打款中】温馨提示
	 * @param userName
	 */
	public void getCashExtracting(String userName);
	
	/**
	 * 签到奖励
	 * @param userName
	 */
	public void getSignCycle(String userName);
	
	/**
	 * 手机充值退款失败
	 * @param userName
	 * @param date
	 */
	public void refundMobileError(String userName,Date date);
	
	/**
	 * 【淘宝消费借款打款中】温馨提示
	 * @param userName
	 * @param name
	 */
	public void dealBorrowConsumeTransfer(String userName,String name);
	
	/**
	 * 【现金借款打款中】温馨提示
	 * @param userName
	 * @param date
	 */
	public void dealBorrowCashTransfer(String userName,Date date);
	
	/**
	 * 【借钱申请成功提示】温馨提示
	 * @param userName
	 * @param date
	 */
	public void dealBorrowCashApplySuccss(String userName,Date date);
	/**
	 * 【借钱申请失败提示】温馨提示
	 * @param userName
	 * @param date
	 */
	public void dealBorrowCashApplyFail(String userName,Date date);
	
}
