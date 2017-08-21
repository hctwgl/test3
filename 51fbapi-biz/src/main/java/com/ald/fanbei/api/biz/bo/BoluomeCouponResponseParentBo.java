package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

public class BoluomeCouponResponseParentBo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9211425772460877007L;
	private Integer strategy_id;
	private String  activity_coupons ;
	public Integer getStrategy_id() {
		return strategy_id;
	}
	public void setStrategy_id(Integer strategy_id) {
		this.strategy_id = strategy_id;
	}
	public String getActivity_coupons() {
		return activity_coupons;
	}
	public void setActivity_coupons(String activity_coupons) {
		this.activity_coupons = activity_coupons;
	}


				
}
