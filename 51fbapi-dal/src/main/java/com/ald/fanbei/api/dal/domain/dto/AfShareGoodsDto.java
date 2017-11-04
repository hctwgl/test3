package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;

/**
 * 新人专享Dto
 * @author yanghailong
 *
 */
public class AfShareGoodsDto extends AfGoodsDo {
	
	
	private static final long serialVersionUID = 1L;
	
	 /**
     * 主键Rid
     */
    private Long rid;
	 /**
     * 立减金额
     */
    private BigDecimal decreasePrice;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public BigDecimal getDecreasePrice() {
		return decreasePrice;
	}

	public void setDecreasePrice(BigDecimal decreasePrice) {
		this.decreasePrice = decreasePrice;
	}


}
