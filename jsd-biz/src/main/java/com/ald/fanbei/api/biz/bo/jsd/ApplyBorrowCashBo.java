package com.ald.fanbei.api.biz.bo.jsd;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Component;

public class ApplyBorrowCashBo {

	public Long userId;
    public ApplyBorrowCashReq req;
	
	@Component("applyBorrowCashReq")
	public static class ApplyBorrowCashReq{
		@NotNull
		public String openId;
		@NotNull
		public String productNo;
		@NotNull
		public String borrowNo;
		@NotNull
		public BigDecimal amount;
		@NotNull
		public String term;
		@NotNull
		public String unit;
		@NotNull
		public String bankNo;
		
		public String loanRemark;
		public String repayRemark;
		
	    public String isTying;
	    public String tyingType;
	    public JsdGoodsInfoBo jsdGoodsInfoBo;
	}
    
    public static class JsdGoodsInfoBo {
        public String goodsName;
        public String goodsPrice;
        public String goodsImage;
    }
}
