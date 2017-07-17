package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author 江荣波 2017年7月13日上午11:47:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfCouponCategoryDo extends AbstractSerial{

	private static final long serialVersionUID = 5566947920375407089L;

	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String isDelete;
	private String name;
	private String coupons;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCoupons() {
		return coupons;
	}
	public void setCoupons(String coupons) {
		this.coupons = coupons;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	
}
