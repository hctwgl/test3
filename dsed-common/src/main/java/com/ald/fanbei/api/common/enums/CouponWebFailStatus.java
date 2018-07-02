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
	 COUPONCONTEXT1("COUPONCONTEXT1","您已经领取，别太贪心哦!"),
	 COUPONCONTEXT2("COUPONCONTEXT2","恭喜您已经领取成功，快去使用吧!"),
	 COUPONCONTEXT3("COUPONCONTEXT3","今日优惠券已发放完毕，明日再来领取吧!"),
	 COUPONCONTEXT4("COUPONCONTEXT4","该优惠券暂时未发放!"),
	 COUPONCONTEXT5("COUPONCONTEXT5","您已经领取过了，快去使用吧!"),
	 COUPONCONTEXT6("COUPONCONTEXT6","正在领取中，请稍后!"),
	 COUPONCONTEXT7("COUPONCONTEXT7","领取成功!"),
	 COUPONCONTEXT8("COUPONCONTEXT8","活动已经结束"),
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
