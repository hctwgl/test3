package com.ald.fanbei.api.biz.bo.loan;

import java.math.BigDecimal;

public class ApplyLoanBo {
	
	public ApplyLoanBo() {
		this.reqParam = new ReqParam();
	}
	
	public ReqParam reqParam;
	
	/* carrier区域，业务处理中继参数 */
	public Long userId;
	public String userName;
	public BigDecimal auAmount;
	
	public static class ReqParam{
		public String prdType;
		public BigDecimal amount;
		public int periods;
		public String remark;
		public String loanRemark;
		public String repayRemark;
		public String payPwd;
		public String latitude;
		public String longitude;
		public String province;
		public String city;
		public String county;
		public String address;
		public String blackBox;
		public String bqsBlackBox;
		public Long couponId;
		
		public String ip;
		public String appType;
		public String appName;
	}
	
}
