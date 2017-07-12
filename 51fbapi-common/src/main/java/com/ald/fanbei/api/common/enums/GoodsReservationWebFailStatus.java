/**
 * 
 */
package com.ald.fanbei.api.common.enums;
 
 
/**
 * @类描述：
 * @author chengkang 2017年6月8日下午9:30:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum GoodsReservationWebFailStatus {
     UserNotexist("USER_NOT_EXIST", "用户不存在"), 
     GoodsNotExist("GOODS_NOT_EXIST_ERROR","商品不存在"),
     ReservationTimesOverrun("RESERVATION_TIMES_OVERRUN","预约次数超限"),
     ReservationFail("RESERVATION_FAIL","预约失败"),
     ReservationConfigInvalid("RESERVATION_CONFIG_INVALID","配置无效"),
     ReservationClosed("RESERVATION_CLOSED","活动未开启"),
     ReservationNotStart("RESERVATION_NOT_START","活动未开始"),
     ReservationHaveFinish("RESERVATION_HAVE_FINISH","活动已结束"),
     ReservationActNotExist("RESERVATION_ACT_NOT_EXIST","活动不存在");
    
    private String code;
    private String name;
 
 
    GoodsReservationWebFailStatus(String code, String name) {
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