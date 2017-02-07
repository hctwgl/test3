package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午1:38:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ActivityRuleBo extends AbstractSerial{
	
	private static final long serialVersionUID = 5343944867317335587L;
	
	private String condition;
	private Long couponId;
	private String endDate;
	
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
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
}
