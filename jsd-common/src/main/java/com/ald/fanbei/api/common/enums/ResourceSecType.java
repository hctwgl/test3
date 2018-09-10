/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年2月22日上午10:46:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum ResourceSecType {
		ResourceValue1MainImage("MAIN_IMAGE", "主图"),
		ResourceValue1OtherImage("OTHER_IMAGE", "副图"),
		
		H5_URL("H5_URL", "跳转普通H5"),
		GOODS_ID("GOODS_ID", "跳转商品详情"),
		creditScoreAmount("CREDIT_SCORE_AMOUNT","征信等级"),
		NAVIGATION_MOBILE_CHARGE("NAVIGATION_MOBILE_CHARGE","手机充值"),
		NAVIGATION_BACKGROUND("NAVIGATION_BACKGROUND","快速导航背景"),
		NAVIGATION_BOLUOME("NAVIGATION_BOLUOME", "菠萝蜜手机充值"),
		NAVIGATION_CATEGORY("NAVIGATION_CATEGORY", "商品分类"),
		INTEREST_RATE("INTEREST_RATE","利率（年化）"),


		//活动相关
	    OppoReservationActivity("OPPO_RESERVATION_ACTIVITY", "OPPO预约活动"),
	    ASSET_PUSH_RECEIVE("EDSPAY","债权实时债权接收方"),
	    ASSET_PUSH_WHITE("ASSET_PUSH_WHITE","债权实时推送白名单"),
		BKL_WHITE_LIST_CONF("BKL_WHITE_LIST_CONF","百可录电核白名单配置"),
		BKL_CONF_SWITCH("BKL_CONF_SWITCH","百可录电核开关"),
		ORDER_MOBILE_VERIFY_SET("ORDER_MOBILE_VERIFY_SET","自营订单电核规则配置"),
	    //借钱模块
		BaseBankRate("BASE_BANK_RATE", "央行基准利率"),
		BorrowCashRange("BORROW_CASH_RANGE", "借款金额"),
		BorrowCashBaseBankDouble("BORROW_CASH_BASE_BANK_DOUBLE", "借钱最高倍数"),
		BorrowCashPoundage("BORROW_CASH_POUNDAGE", "借钱手续费率（日）"),
		BorrowCashOverduePoundage("BORROW_CASH_OVERDUE_POUNDAGE", "借钱逾期手续费率（日）"),
		BorrowCashDay("BORROW_CASH_DAY", "借钱时间"),
		NewBorrowCashDay("BORROW_CASH_DAY_NEW", "新借钱时间"),
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
		BORROW_CASH_COMPANY_NAME("BORROW_CASH_COMPANY_NAME","借款公司名称"),
		BORROW_CASH_INFO_LEGAL("BORROW_CASH_INFO_LEGAL","借款利率信息"),
	    BORROW_RECYCLE_INFO_LEGAL_NEW("BORROW_RECYCLE_INFO_LEGAL_NEW","新回收利率信息"),
	    BORROW_CASH_INFO_LEGAL_NEW("BORROW_CASH_INFO_LEGAL_NEW","新借款利率信息"),
		borrowRiskMostAmount("BORROW_RISK_MOST_AMOUNT", "风控允许的最大可提升额度"),
		AUTH_FUND_SWITCH("AUTH_FUND_SWITCH","公积金认证开关"),
		BORROW_CASH_SWITCH("BORROW_CASH_SWITCH","极速贷开关"),
		BORROW_CASH_MJB_SWITCH("BORROW_CASH_MJB_SWITCH","马甲包版本控制开关"),
		//短信
		SMS_RISK_SUCCESS("SMS_RISK_SUCCESS","强风控通过短信"),
		SMS_RISK_FAIL("SMS_RISK_FAIL","强风控未通过短信"),
		SMS_RISK_NEED_AUDIT("SMS_RISK_NEED_AUDIT","强风控需要人审短信"),
		SMS_BORROW_RISK_QUALIFIED("SMS_BORROW_RISK_QUALIFIED","用户借钱风控审核中状态被技术干预后用户符合借钱条件"),
		SMS_BORROW_RISK_NOT_QUALIFIED("SMS_BORROW_RISK_NOT_QUALIFIED","用户借钱风控审核中状态被技术干预后用户不符合借钱条件"),
		SMS_BORROW_PAY_MONEY_FAIL("SMS_BORROW_PAY_MONEY_FAIL","借钱审核通过但是打款失败"),
		SMS_REPAYMENT_CONFIRM_FAIL("SMS_REPAYMENT_CONFIRM_FAIL","还款处理后确认用户还款失败"),
		SMS_REPAYMENT_CONFIRM_SUCCESS("SMS_REPAYMENT_CONFIRM_SUCCESS","还款处理后确认用户还款成功"),
		SMS_BORROW_AUDIT("SMS_BORROW_AUDIT","借款审核通过"),
		SMS_JKCR_BORROW_AUDIT("SMS_JKCR_BORROW_AUDIT","借款超人借款审核通过"),
		SMS_LOAN_AUDIT("SMS_LOAN_AUDIT","白领贷借款审核通过"),
		SMS_REPAYMENT_SUCCESS("SMS_REPAYMENT_SUCCESS","还款成功"),
		SMS_RECYCLE_REPAYMENT_SUCCESS("SMS_RECYCLE_REPAYMENT_SUCCESS","回收还款成功"),
		SMS_REPAYMENT_SUCCESS_REMAIN("SMS_REPAYMENT_SUCCESS_REMAIN","部分还款成功"),
		SMS_RECYCLE_REPAYMENT_SUCCESS_REMAIN("SMS_RECYCLE_REPAYMENT_SUCCESS_REMAIN","回收部分还款成功"),
		SMS_LIMIT("SMS_LIMIT","短信限制"),
		IS_USE_IMG("IS_USE_IMG","底部菜单栏是否使用图片"),
		ASJ_IS_USE_IMG("ASJ_IS_USE_IMG","底部菜单栏是否使用图片"),

		SMS_REPAYMENT_BORROWCASH_FAIL("SMS_REPAYMENT_BORROWCASH_FAIL","用户发起现金借贷还款失败通知用户"),
		SMS_JKCR_REPAYMENT_BORROWCASH_FAIL("SMS_JKCR_REPAYMENT_BORROWCASH_FAIL","借款超人用户发起现金借贷还款失败通知用户"),
		SMS_RECYCLE_REPAYMENT_BORROWCASH_FAIL("SMS_RECYCLE_REPAYMENT_BORROWCASH_FAIL","用户发起回收还款失败通知用户"),
		SMS_RENEWAL_DETAIL_FAIL("SMS_RENEWAL_DETAIL_FAIL","用户发起续借失败通知用户"),
		SMS_JKCR_RENEWAL_DETAIL_FAIL("SMS_JKCR_RENEWAL_DETAIL_FAIL","借款超人用户发起续借失败通知用户"),
		SMS_RENEWAL_DETAIL_SUCCESS("SMS_RENEWAL_DETAIL_SUCCESS","用户发起续借成功通知用户"),
		SMS_REPAYMENT_BORROWCASH_WITHHOLD_SUCCESS("SMS_REPAYMENT_BORROWCASH_WITHHOLD_SUCCESS","自动代扣现金贷还款成功短信提示"),
	    SMS_REPAYMENT_BORROWCASH_WITHHOLD_OVERDUE_SUCCESS("SMS_REPAYMENT_BORROWCASH_WITHHOLD_OVERDUE_SUCCESS","逾期代扣现金贷还款成功短信提示"),
	    SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL("SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL","自动代扣现金贷还款失败短信提示"),
		SMS_REPAYMENT_BORROWBILL_FAIL("SMS_REPAYMENT_BORROWBILL_FAIL","用户发起消费分期还款失败短信提示"),
		SMS_REPAYMENT_BORROWBILL_WITHHOLD_SUCCESS("SMS_REPAYMENT_BORROWBILL_WITHHOLD_SUCCESS","自动代扣消费分期还款成功短信提示"),
		SMS_REPAYMENT_BORROWBILL_WITHHOLD_FAIL("SMS_REPAYMENT_BORROWBILL_WITHHOLD_FAIL","自动代扣消费分期还款失败短信提示"),
		SMS_APPLY_BORROWCASH_TRANSED_FAIL("SMS_APPLY_BORROWCASH_TRANSED_FAIL","现金借贷放款失败通知用户"),
		SMS_EDSPAY_LOAN_TIMEOUT("SMS_EDSPAY_LOAN_TIMEOUT","浙商实时放款超时通知钱包相关人员"),
		SMS_MOBILE_OPERATE_FAIL("SMS_MOBILE_OPERATE_FAIL","运营商认证异步失败通知用户"),
		FUND_SIDE_BORROW_CASH_ONOFF("FUND_SIDE_BORROW_CASH_ONOFF", "打款时引入资金方配置开关"),
		ASSET_SIDE_CONFIG_BANK_INFOS("ASSET_SIDE_CONFIG_BANK_INFOS", "资产方债权对应开户行信息配置"),
		
		BORROWCASH_BANK_INFOS("BORROWCASH_BANK_INFOS", "极速贷债权对应开户行信息配置"),
		BORROW_BANK_INFOS("BORROW_BANK_INFOS", "分期债权对应开户行信息配置"),
		LOAN_BANK_INFOS("LOAN_BANK_INFOS", "白领贷债权对应开户行信息配置"),
		
		YIXIN_AFU_SEARCH("YIXIN_AFU_SEARCH","宜信阿福查询限制配置"),
		RISK_POUNDAGE_USERNAME_LIST("RISK_POUNDAGE_USERNAME_LIST","用户分层利率从风控直接取的手机号配置"),
		SMS_BANK_PAY_ORDER_FAIL("SMS_BANK_PAY_ORDER_FAIL","下单涉及银行卡支付失败短信提示"),

		BORROW_CASH_OVERDUE_POUNDAGE("BORROW_CASH_OVERDUE_POUNDAGE","借钱逾期手续费率（日）"),

		BORROW_CASH_AMOUNT_CHANNEL_ERROR("BORROW_CASH_AMOUNT_CHANNEL_ERROR","用户借钱时渠道包问题给用户的短信内容"),
		//芝麻信用认证相关配置 芝麻信用认证相关控制
		//v:开放Y/N v1:展示 0文字1数 v2:Y严格认证 N默认通过v3:分界app版本v4:老用户重认时间界限
		ZHIMA_VERIFY_RULE_CONFIG("ZHIMA_VERIFY_RULE_CONFIG","芝麻信用认证相关配置"),
		BUBBLE_AUTH_RULE_CONFIG("BUBBLE_AUTH_RULE_CONFIG","芝麻信用认证相关配置"),
		ZHIMA_VERIFY_APP_POP_IMAGE("ZHIMA_VERIFY_APP_POP_IMAGE","芝麻信用认证首页弹窗配置"),
		BORROW_SUPERMAN_POPUP_SWITCH("BORROW_SUPERMAN_POPUP_SWITCH", "借款超人弹窗开关"),
		ORDER_WEAK_VERIFY_VIP_CONFIG("ORDER_WEAK_VERIFY_VIP_CONFIG","消费分期弱风控权限包相关配置"),

		// 天天拆红包配置
		OPEN_REDPACKET_DEVELOPER_CONFIG("DEVELOPER_CONFIG", "开发人员配置项"),
		OPEN_REDPACKET_OPERATOR_CONFIG("OPERATOR_CONFIG", "运营人员配置项"),
	
		OVERDUE_JOB_INTERNAL_UIDS("OVERDUE_JOB_INTERNAL_UIDS", "公司内部专用定时器用户名单");

	 	private String code;
		private String name;
	
		ResourceSecType(String code, String name) {
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

