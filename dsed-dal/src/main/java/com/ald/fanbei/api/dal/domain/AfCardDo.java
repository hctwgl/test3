package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
/**  
 * @Title: AfCardDo.java
 * @Package com.ald.fanbei.api.dal.domain
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年11月17日 上午11:33:39
 * @version V1.0  
 */
public class AfCardDo extends AbstractSerial{

	private static final long serialVersionUID = 6316311540173227311L;

	private Long rid;

	/**
	 * 活动id【boluome_activity_id】
	 */
	private Long activityId;

	/**
	 * 物件名称
	 */
	private String name;

	/**
	 * 物件icon地址
	 */
	private String image;


	/**
	 * 关联表ID type为CARD 则为商城id
	 */
	private Long shopId;

	/**
	 * 排序 数值越大越靠前
	 */
	private Long sort;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getSort() {
		return sort;
	}

	public void setSort(Long sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "AfCardDo [rid=" + rid + ", activityId=" + activityId + ", name=" + name + ", image=" + image
				+ ", shopId=" + shopId + ", sort=" + sort + "]";
	}


}
