package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**  
 * @Title: afCouponDouble12Vo.java
 * @Package com.ald.fanbei.api.web.vo
 * @Description: 双十二优惠券Vo
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年11月20日
 * @version V1.0  
 */
public class afCouponDouble12Vo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	
	private long id;
	private String name;
	private String threshold;
	private String isShow;// Y:在活动期间；N:不在活动期间
	
	public String getIsShow() {
		return isShow;
	}
	public void setIsShow(String isShow) {
		this.isShow = isShow;
	}
	public afCouponDouble12Vo() {
	}
	public afCouponDouble12Vo(long id, String name, String threshold) {
		this.id = id;
		this.name = name;
		this.threshold = threshold;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThreshold() {
		return threshold;
	}
	public void setThreshold(String threshold) {
		this.threshold = threshold;
	}
}
