
/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年2月22日上午10:46:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfResourceSecType {
		ResourceValue1MainImage("MAIN_IMAGE", "主图"),
		ResourceValue1OtherImage("OTHER_IMAGE", "副图"),
		
		H5_URL("H5_URL", "跳转普通H5"),
		GOODS_ID("GOODS_ID", "跳转商品详情"),
		creditScoreAmount("CREDIT_SCORE_AMOUNT","征信等级"),
		NAVIGATION_MOBILE_CHARGE("NAVIGATION_MOBILE_CHARGE","手机充值"),
		NAVIGATION_BOLUOME("NAVIGATION_BOLUOME", "菠萝蜜手机充值"),
	

		//活动相关
	    OppoReservationActivity("OPPO_RESERVATION_ACTIVITY", "OPPO预约活动"),
    
	    //借钱模块
		BaseBankRate("BASE_BANK_RATE", "央行基准利率"),
		BorrowCashRange("BORROW_CASH_RANGE", "借款金额"),
		BorrowCashBaseBankDouble("BORROW_CASH_BASE_BANK_DOUBLE", "借钱最高倍数"),
		BorrowCashPoundage("BORROW_CASH_POUNDAGE", "借钱手续费率（日）"),
		BorrowCashOverduePoundage("BORROW_CASH_OVERDUE_POUNDAGE", "借钱逾期手续费率（日）"),
		BorrowCashDay("BORROW_CASH_DAY", "借钱时间"),
		borrowCashLender("BORROW_CASH_LENDER", "分期借款出借人信息"),
		borrowCashLenderForCash("BORROW_CASH_LENDER_FOR_CASH", "现金借款借款出借人信息"),
		borrowCashSupuerSwitch("BORROW_CASH_SUPUER_SWITCH", "借款超级开关"),
		borrowCashTotalAmount("BORROW_CASH_TOTAL_SWITCH","每日借款借款总金额"),
		borrowCashShowMoney("BORROW_CASH_SHOW_MONEY","显示已放款总额"),
		borrowCashShowNum("BORROW_CASH_SHOW_NUM","显示已放款笔数"),
		borrowConsume("BORROW_CONSUME","消费分期利率设置"),
		borrowConsumeOverdue("BORROW_CONSUME_OVERDUE","消费分期逾期利率"),
		AppRebateRate("APP_REBATE_RATE","返利比例"),
		borrowCashMoreAmount("BORROW_CASH_MORE_AMOUNT","借钱可提升最高额度以及每次可提升金额"),
        MODEL_URL("MODEL_URL", "本地模板H5"),
        RejectTimePeriod("REJECT_TIME_PERIOD","拒绝借款期后指定周期内不可在本平台发起借款"),
		RejectPageBannerUrl("REJECT_PAGE_BANNER_URL","拒绝借款期后不通过页面内图对应地址"),
		SelfSupportGoodsPaytypes("SELFSUPPORT_GOODS_PAYTYPES","自营商品支付方式相关配置"),

		borrowRiskMostAmount("BORROW_RISK_MOST_AMOUNT", "风控允许的最大可提升额度"),
		//短信
		SMS_RISK_SUCCESS("SMS_RISK_SUCCESS","强风控通过短信"),
		SMS_RISK_FAIL("SMS_RISK_FAIL","强风控未通过短信"),
		SMS_BORROW_RISK_QUALIFIED("SMS_BORROW_RISK_QUALIFIED","用户借钱风控审核中状态被技术干预后用户符合借钱条件"),
		SMS_BORROW_RISK_NOT_QUALIFIED("SMS_BORROW_RISK_NOT_QUALIFIED","用户借钱风控审核中状态被技术干预后用户不符合借钱条件"),
		SMS_BORROW_PAY_MONEY_FAIL("SMS_BORROW_PAY_MONEY_FAIL","借钱审核通过但是打款失败"),
		SMS_REPAYMENT_CONFIRM_FAIL("SMS_REPAYMENT_CONFIRM_FAIL","还款处理后确认用户还款失败"),
		SMS_REPAYMENT_CONFIRM_SUCCESS("SMS_REPAYMENT_CONFIRM_SUCCESS","还款处理后确认用户还款成功"),
		SMS_BORROW_AUDIT("SMS_BORROW_AUDIT","借款审核通过"),
		SMS_REPAYMENT_SUCCESS("SMS_REPAYMENT_SUCCESS","还款成功"),
		SMS_REPAYMENT_SUCCESS_REMAIN("SMS_REPAYMENT_SUCCESS_REMAIN","部分还款成功"),
		SMS_LIMIT("SMS_LIMIT","短信限制");
		
	 	private String    code;
		private String name;
	
		AfResourceSecType(String code, String name) {
			this.code = code;
			this.name = name;
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

