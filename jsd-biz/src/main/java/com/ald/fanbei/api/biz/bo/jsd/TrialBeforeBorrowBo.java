package com.ald.fanbei.api.biz.bo.jsd;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

/**
 * @author zhujiangfeng
 *	试算参数类
 */
public class TrialBeforeBorrowBo {
	
	public TrialBeforeBorrowReq req;
	public TrialBeforeBorrowResp resp;
	/**
	 * 分层日利率
	 */
	public BigDecimal riskDailyRate;
	public Long userId;
	
	@Component("trialBeforeBorrowReq")
	public static class TrialBeforeBorrowReq{
		@NotNull
		public String openId;
		@NotNull
		public String productNo;
		@NotNull
		@DecimalMin("0")
		public BigDecimal amount; 	
		@NotNull
		public String term;		
		@NotNull
		public String unit;
		
		public String isTying;	
		public String tyingType;
		
		/**
		 * 兼容字段，对应term
		 */
		public String nper;
		
		public TrialBeforeBorrowReq() {}
		
		/**
		 * 基本入参
		 */
		public TrialBeforeBorrowReq(String openId, BigDecimal amount, String term, String unit) {
			this.openId = openId;
			this.amount = amount;
			this.term = term;
			this.unit = unit;
		}
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
		 * 借款逾期费率日化
		 */
		public String overdueRate;
		/**
		 * 借款逾期费率年化
		 */
		public String overdueYearRate;
		
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
		 * 商品服务费率
		 */
		public BigDecimal sellServiceRate;
		/**
		 * 商品利息
		 */
		public String sellInterestFee;
		/**
		 * 商品利息费率
		 */
		public BigDecimal sellInterestRate;
		/**
		 * 商品逾期费率
		 */
		public BigDecimal sellOverdueRate;

		public BigDecimal riskDailyRate;

	}
}
