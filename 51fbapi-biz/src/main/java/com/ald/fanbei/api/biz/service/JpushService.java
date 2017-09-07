package com.ald.fanbei.api.biz.service;

import java.util.Date;

/**
 * 
 * @类描述：
 * @author 何鑫 2017年2月27日 上午9:53:32
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JpushService {

	/**
	 * 【邀请注册成功】温馨提示
	 * 
	 * @param userName
	 * @param name
	 */
	public void userInviteSuccess(String userName, String mobile);

	/**
	 * 【手机充值失败】温馨提示
	 * 
	 * @param userName
	 * @param mobile
	 * @param date
	 */
	public void chargeMobileError(String userName, String mobile, Date date);

	/**
	 * 【手机充值成功】温馨提示
	 * 
	 * @param userName
	 * @param mobile
	 */
	public void chargeMobileSucc(String userName, String mobile, Date date);

	/**
	 * 【账单已还清】温馨提示
	 * 
	 * @param userName
	 * @param year
	 * @param month
	 */
	public void repayBillSuccess(String userName, String year, String month);

	/**
	 * 【余额提现打款中】温馨提示
	 * 
	 * @param userName
	 */
	public void getCashExtracting(String userName);

	/**
	 * 签到奖励
	 * 
	 * @param userName
	 */
	public void getSignCycle(String userName);

	/**
	 * 手机充值退款失败
	 * 
	 * @param userName
	 * @param date
	 */
	public void refundMobileError(String userName, Date date);

	/**
	 * 【淘宝消费借款打款中】温馨提示
	 * 
	 * @param userName
	 * @param name
	 */
	public void dealBorrowConsumeTransfer(String userName, String name);

	/**
	 * 【现金借款打款中】温馨提示
	 * 
	 * @param userName
	 * @param date
	 */
	public void dealBorrowCashTransfer(String userName, Date date);

	/**
	 * 【借钱申请成功提示】温馨提示
	 * 
	 * @param userName
	 * @param date
	 */
	public void dealBorrowCashApplySuccss(String userName, Date date);

	/**
	 * 【借钱申请失败提示】温馨提示
	 * 
	 * @param userName
	 * @param date
	 */
	public void dealBorrowCashApplyFail(String userName, Date date);

	/**
	 * 【续借成功提示】温馨提示
	 * 
	 * @param userName
	 * @param date
	 */
	public void repayRenewalSuccess(String userName);

	/**
	 * 【续借失败提示】温馨提示
	 * 
	 * @param userName
	 * @param date
	 */
	public void repayRenewalFail(String userName);
	/**
	 * 代付申请失败
	 */
	public void dealBorrowApplyFail(String userName,Date date);

	/**
	 * 游戏分享注册成功,获得抽奖机会
	 * @param userName
	 */
	public void gameShareSuccess(String userName);
	
	/**
	 *  强风控通过推送
	 */
	public void strongRiskSuccess(String userName);

	/**
	 *  强风控失败推送
	 */
	public void strongRiskFail(String userName);
	
	
	/**
	 *  运营商认证推送  
	 */
	public void mobileRiskSuccess(String userName);

	/**
	 *  运营商认证失败推送
	 */
	public void mobileRiskFail(String userName);
	
	/**
	 * 给指定的用户推送首页弹窗
	 * @param userName
	 */
	public void pushHeaderImage(String userName);
	
	/**
	 * 给所有用户推送首页弹窗
	 * @param userName
	 */
	public void pushAllHeaderImage();
	
	/**
	 *  公积金认证推送  
	 */
	public void fundRiskSuccess(String userName);

	/**
	 *  公积金认证未通过推送
	 */
	public void fundRiskFail(String userName);	
	/**
	 *  公积金认证失败推送
	 */
	public void fundRiskFault(String userName);		
	/**
	 *  社保认证推送  
	 */
	public void socialSecurityRiskSuccess(String userName);

	/**
	 *  社保认证未通过推送
	 */
	public void socialSecurityRiskFail(String userName);		
	/**
	 *  社保认证失败推送
	 */
	public void socialSecurityRiskFault(String userName);		
	/**
	 *  信用卡认证推送  
	 */
	public void creditCardRiskSuccess(String userName);

	/**
	 *  信用卡认证未通过推送
	 */
	public void creditCardRiskFail(String userName);
	
	/**
	 *  信用卡认证失败推送
	 */
	public void creditCardRiskFault(String userName);	
	
	/**
	 *  支付宝认证成功推送  
	 */
	public void alipayRiskSuccess(String userName);

	/**
	 *  支付宝认证未通过推送
	 */
	public void alipayRiskFail(String userName);	
	/**
	 *  支付宝认证失败推送
	 */
	public void alipayRiskFault(String userName);

	void jPushByType(int jumpType, String type,String userName);
	//注册新用户弹窗
	void jPushCoupon(String type,String userName);

}
