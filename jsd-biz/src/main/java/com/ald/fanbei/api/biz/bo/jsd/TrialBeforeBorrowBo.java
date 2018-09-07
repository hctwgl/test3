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
		@NotNull
		public String goodsPrice;
		
		public String isTying;	
		public String tyingType;
	}
	
	public static class TrialBeforeBorrowResp {
		/**
		 * 用户总应还金额
		 */
		public String totalAmount;
		
		/**
		 * 借款实借金额
		 */
		public String borrowAmount;
		
		/**
		 * 借款到账到账金额
		 */
		public String arrivalAmount;
		/**
		 * 借款利息利率
		 */
		public String interestRate;
		/**
		 * 借款利息
		 */
		public String interestAmount;
		/**
		 * 借款服务费利率
		 */
		public String serviceRate;
		/**
		 * 借款服务费
		 */
		public String serviceAmount;
		/**
		 * 借款逾期费率
		 */
		public String overdueRate;
		
		public BigDecimal[] billAmount;
		public String remark;
		
		/**
		 * 利润差 又称 商品金额，会已10取整
		 */
		public String totalDiffFee;
		/**
		 * 商品服务费
		 */
		public String sellServiceFee;
		/**
		 * 商品利息
		 */
		public String sellInterestFee;
	}
}
