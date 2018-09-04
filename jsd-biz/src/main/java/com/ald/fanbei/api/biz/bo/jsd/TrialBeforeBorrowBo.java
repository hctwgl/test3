package com.ald.fanbei.api.biz.bo.jsd;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

public class TrialBeforeBorrowBo {
	
	public TrialBeforeBorrowReq req;
	public TrialBeforeBorrowResp resp;
	
	@Component("trialBeforeBorrowReq")
	public static class TrialBeforeBorrowReq{
		@NotNull
		public String openId;
		@NotNull
		public String productNo;
		@NotNull
		public String amount; 	
		@NotNull
		public String term;		
		@NotNull
		public String unit;
		
		public String isTying;	
		public String tyingType;
	}
	
	public static class TrialBeforeBorrowResp {
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
