package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;


/**
 * 
 * @类描述：
 * @author hexin 2017年2月22日下午16:12:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum UserAccountLogType {

	CASH("CASH", "取现"), 
	JIFENBAO("JIFENBAO", "集分宝取现"), 
	BorrowCash("BORROWCASH", "借钱"),
	BorrowCash_Fail("BORROWCASH_FAIL", "借钱,打款失败退款"), 

	LOAN("LOAN", "贷款"),
	DSED_LOAN("DSED_LOAN", "贷款"),

	AU_SCORE("AU_SCORE", "授权分数"),
	AU_AMOUNT("AU_AMOUNT", "授权金额"),
	FREEZE("FREEZE", "冻结金额"),
	REBATE("REBATE", "返利"), 
	SIGN("SIGN", "签到"),
	REGIST("REGIST", "新注册"),
	CONSUME("CONSUME", "分期"),
	
	BORROWAP("BORROWAP", "代买支付"),
	
	WX_REFUND("WX_REFUND","微信退款"),
	AP_REFUND("AP_REFUND", "代付退款"),
	CP_REFUND("CP_REFUND","组合支付退款"),
	CREDIT_CARD_REFUND("CREDTT_CARD_REFUND", "信用卡支付退款"),
	CP_PAY_FAIL("CP_PAY_FAIL","组合失败退款额度"),
	BANK_REFUND("BANK_REFUND", "菠萝觅银行卡退款"),
	AGENT_BUY_BANK_REFUND("AGENT_BUY_BANK_REFUND", "代买银行卡退款"),
	NORMAL_BANK_REFUND("NORMAL_BANK_REFUND", "普通银行卡退款"),
	REPAYMENT("REPAYMENT", "还款"),
	REPAYMENTCASH("REPAYMENTCASH", "借钱还款"),
	REPAY_CASH_LEGAL("REPAY_CASH_LEGAL", "借钱合规还款"),
	RENEWAL_PAY("RENEWAL_PAY", "续期支付"),
	REPAYMENT_OUT("REPAYMENT_OUT", "还款多余"),
	SETTLEMENT_PAY("SETTLEMENT_PAY", "结算单划账"),

	REBATE_CASH("REBATE_CASH", "返利现金"),
	DOUBLE_REBATE_CASH("DOUBLE_REBATE_CASH", "双倍返利现金"),
	REBATE_JFB("REBATE_JFB", "返利集分宝"),
    AUTHNAME("AUTHNAME", "实名认证"),
	SELFSUPPORT("SELFSUPPORT","自营商品"),
	BOLUOME("BOLUOME", "菠萝觅"),
    TRADE_BANK_REFUND("TRADE_BANK_REFUND","商圈商户退款"), 
    TRADE_WITHDRAW("TRADE_WITHDRAW", "商圈商户取现"),

	REFUND("REFUND", "退款"),
	SEND_CASH_COUPON("SEND_CASH_COUPON", "现金优惠券"),
	CASH_FAILD_REFUND("CASH_FAILD_REFUND", "取现失败退款"),
	WITHDRAW_TO_REBATE("WITHDRAW_TO_REBATE", "提现到余额"),
	RECOMMEND_USER("RECOMMEND_USER", "邀请有礼"),
	RECOMMEND_RISK("RECOMMEND_RISK", "信用审核通过");

	private String code;
	private String name;

	private static Map<String, UserAccountLogType> codeRoleTypeMap = null;

	UserAccountLogType(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public static UserAccountLogType findRoleTypeByCode(String code) {
		for (UserAccountLogType roleType : UserAccountLogType.values()) {
			if (roleType.getCode().equals(code)) {
				return roleType;
			}
		}
		return null;
	}

	public static Map<String, UserAccountLogType> getCodeRoleTypeMap() {
		if (codeRoleTypeMap != null && codeRoleTypeMap.size() > 0) {
			return codeRoleTypeMap;
		}
		codeRoleTypeMap = new HashMap<String, UserAccountLogType>();
		for (UserAccountLogType item : UserAccountLogType.values()) {
			codeRoleTypeMap.put(item.getCode(), item);
		}
		return codeRoleTypeMap;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
