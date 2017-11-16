package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

/**
 * 用户优惠券上实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2017-11-15 14:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class BoluomeActivityInviteCeremonyVo extends BoluomeActivityInviteFriendVo {

    private static final long serialVersionUID = 1L;

    private String spePreference;
    private String couponAmount;
    public String getSpePreference() {
        return spePreference;
    }
    public void setSpePreference(String spePreference) {
        this.spePreference = spePreference;
    }
    public String getCouponAmount() {
        return couponAmount;
    }
    public void setCouponAmount(String couponAmount) {
        this.couponAmount = couponAmount;
    }
    
    
 }