/**
 * 
 */
package com.ald.fanbei.api.common.enums;


/**
 * @类描述：
 * @author suweili 2017年3月23日上午11:58:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum CouponWebFailStatus {
	 UserNotexist("USER_NOT_EXIST", "用户不存在"), 
	 CouponOver("OVER", "优惠券个数超过最大领券个数"),
	 MoreThanCoupon("MORE_THAN", "优惠券已领取完"),
	 CouponNotExist("COUPON_NOT_EXIST", "优惠券不存在");
    
    private String code;
    private String name;


    CouponWebFailStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
