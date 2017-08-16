package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：领券场景规则json选项
 * @author xiaotianjian 2017年2月7日下午1:38:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CouponSceneRuleBo extends AbstractSerial{
	
	private static final long serialVersionUID = -6682895776430310225L;
	
	private String condition;   //条件，如：针对签到，condition值为连续签到的次数,如果condition=-1则表示无条件
	private Long couponId;      //优惠券id
	private Long resourceId;      //优惠券id

	private String ext1;		//扩展字段1
	private String ext2;		//扩展字段2
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	/**
	 * @return the resourceId
	 */
	public Long getResourceId() {
		return resourceId;
	}
	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}
	
}
