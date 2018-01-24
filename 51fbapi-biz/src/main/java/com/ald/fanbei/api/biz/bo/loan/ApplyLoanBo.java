package com.ald.fanbei.api.biz.bo.loan;

import java.math.BigDecimal;

public class ApplyLoanBo {
	
	public ApplyLoanBo() {
		this.reqParam = new ReqParam();
	}
	
	public ReqParam reqParam;
	
	/* carrier区域，业务处理中继参数 */
	public Long userId;
	
	public static class ReqParam{
		public String prdType; 
		public BigDecimal amount;
		public int periods;
		public String repayType;
		public String loan_remark;
		public String repay_remark;
		public Long cardId;
		public String pwd;
		public String latitude;
		public String longitude;
		public String province;
		public String city;
		public String county;
		public String address;
		public String blackBox;
		public Long couponId;
	}
	
}
