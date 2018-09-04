package com.ald.fanbei.api.biz.bo.jsd;

import java.math.BigDecimal;

public class TrialBeforeBorrowBo {
	
	public Req req = new Req();
	public Resp resp = new Resp();
	
	public static class Req{
		public String openId; 	
		public String productNo;
		public String amount; 	
		public String term;		
		public String unit; 	
		public String isTying;	
		public String tyingType;
	}
	
	public static class Resp {
		public String totalAmount; 	
		public String arrivalAmount;
		public String interestRate; 	
		public String interestAmount;		
		public String serviceRate; 	
		public String serviceAmount;	
		public String overdueRate; 	
		public BigDecimal[] billAmount;
		public String remark; 	
		public String totalDiffFee;		
		public String sellServiceFee; 	
		public String sellInterestFee;	
	}
}
