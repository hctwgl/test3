/**
 * 
 */
package com.ald.fanbei.api.common.enums;
 
/**
 * @类描述：
 * 活动商品预约状态
 * @author chengkang 2017年6月8日下午4:00:57
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfGoodsReservationStatus {
    SUCCESS("SUCCESS", "预约成功"), 
    FAIL("FAIL", "预约失败"),
    CANCEL("CANCEL", "取消预约");
    
    private String code;
    private String name;
 
 
    AfGoodsReservationStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }
 
    public static CouponStatus findRoleTypeByCode(String code) {
        for (CouponStatus roleType : CouponStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
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