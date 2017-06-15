package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author 江荣波 2017年2月17日上午9:47:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSubjectGoodsDo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5566947920375407089L;

	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private String subjectId;
	private Integer goodsId;
	private Long interestFreeId;
	private String couponId;
	private Long parentSubjectId;
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
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public Integer getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Integer goodsId) {
		this.goodsId = goodsId;
	}
	public Long getInterestFreeId() {
		return interestFreeId;
	}
	public void setInterestFreeId(Long interestFreeId) {
		this.interestFreeId = interestFreeId;
	}
	public String getCouponId() {
		return couponId;
	}
	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}
	public Long getParentSubjectId() {
		return parentSubjectId;
	}
	public void setParentSubjectId(Long parentSubjectId) {
		this.parentSubjectId = parentSubjectId;
	}
	
}
