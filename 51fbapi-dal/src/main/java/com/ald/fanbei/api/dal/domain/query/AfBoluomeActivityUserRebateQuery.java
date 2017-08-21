package com.ald.fanbei.api.dal.domain.query;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfBoluomeActivityUserRebateQuery  extends AbstractSerial{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1343233084284529875L;
	//最后绑定关系时间
  	Date lastTime;
  	 //最后下单时间
  	Date orderTime;
  	Long userId;
  	Long refUserId;
  	Long fanLiRecordTime;
  	
	public Long getFanLiRecordTime() {
		return fanLiRecordTime;
	}

	public void setFanLiRecordTime(Long fanLiRecordTime) {
		this.fanLiRecordTime = fanLiRecordTime;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRefUserId() {
		return refUserId;
	}

	public void setRefUserId(Long refUserId) {
		this.refUserId = refUserId;
	}

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	



	
}
