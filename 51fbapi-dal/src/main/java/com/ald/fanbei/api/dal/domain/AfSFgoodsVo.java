package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**  
 * @Title: AfSFgoodsVo.java
 * @Package com.ald.fanbei.api.dal.domain
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2018年1月4日 下午2:03:46
 * @version V1.0  
 */
public class AfSFgoodsVo extends AbstractSerial{

	private static final long serialVersionUID = -7708999093678030033L;
	
	private Long id;
	private Long activityId;
	private int orderNumber;
	private String isOrder;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getActivityId() {
		return activityId;
	}
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	public int getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getIsOrder() {
		return isOrder;
	}
	public void setIsOrder(String isOrder) {
		this.isOrder = isOrder;
	}
	@Override
	public String toString() {
		return "AfSFgoodsVo [id=" + id + ", activityId=" + activityId + ", orderNumber=" + orderNumber + ", isOrder="
				+ isOrder + "]";
	}

	
	
	

}
