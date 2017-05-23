
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

	//借钱模块
		BaseBankRate("BASE_BANK_RATE", "央行基准利率"),
		BorrowCashRange("BORROW_CASH_RANGE", "借款金额"),
		BorrowCashBaseBankDouble("BORROW_CASH_BASE_BANK_DOUBLE", "借钱最高倍数"),
		BorrowCashPoundage("BORROW_CASH_POUNDAGE", "借钱手续费率（日）"),
		BorrowCashOverduePoundage("BORROW_CASH_OVERDUE_POUNDAGE", "借钱逾期手续费率（日）"),
		BorrowCashDay("BORROW_CASH_DAY", "借钱时间"),
		borrowCashLender("BORROW_CASH_LENDER", "出借人信息"),
		borrowCashSupuerSwitch("BORROW_CASH_SUPUER_SWITCH", "借款超级开关"),
		borrowCashTotalAmount("BORROW_CASH_TOTAL_SWITCH","每日借款借款总金额"),
		borrowCashShowMoney("BORROW_CASH_SHOW_MONEY","显示已放款总额"),
		borrowCashShowNum("BORROW_CASH_SHOW_NUM","显示已放款笔数"),
		borrowConsume("BORROW_CONSUME","消费分期利率设置"),
		borrowConsumeOverdue("BORROW_CONSUME_OVERDUE","消费分期逾期利率"),
		
		borrowCashMoreAmount("BORROW_CASH_MORE_AMOUNT","借钱可提升最高额度以及每次可提升金额"),

		
	MODEL_URL("MODEL_URL", "本地模板H5");



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

