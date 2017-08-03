package com.ald.fanbei.api.biz.bo;

import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

public class BoluomeCouponResponseParentBo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9211425772460877007L;
	private Integer strategy_id;
	private List<BoluomeCouponResponseBo> listCoupon ;
	public Integer getStrategy_id() {
		return strategy_id;
	}
	public void setStrategy_id(Integer strategy_id) {
		this.strategy_id = strategy_id;
	}
	public List<BoluomeCouponResponseBo> getListCoupon() {
		return listCoupon;
	}
	public void setListCoupon(List<BoluomeCouponResponseBo> listCoupon) {
		this.listCoupon = listCoupon;
	}
				
}
