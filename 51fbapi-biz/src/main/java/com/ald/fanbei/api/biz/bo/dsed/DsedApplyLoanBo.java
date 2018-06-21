package com.ald.fanbei.api.biz.bo.dsed;

import java.math.BigDecimal;

public class DsedApplyLoanBo {

	public DsedApplyLoanBo() {
		this.reqParam = new ReqParam();
	}
	
	public ReqParam reqParam;
	
	/* carrier区域，业务处理中继参数 */
	public Long userId;
	public String userName;
	public BigDecimal auAmount;
	public String realName;
	public String idNumber;

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
