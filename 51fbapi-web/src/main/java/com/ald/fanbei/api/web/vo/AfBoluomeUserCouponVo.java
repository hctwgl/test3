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
 * @date 2017-11-14 16:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeUserCouponVo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    private BigDecimal inviteAmount;
    private BigDecimal couponAmount;
    private Long shopId;
    private  List<userReturnBoluomeCouponVo> returnCouponList;
    
   
   
    public BigDecimal getInviteAmount() {
        return inviteAmount;
    }
    public void setInviteAmount(BigDecimal inviteAmount) {
        this.inviteAmount = inviteAmount;
    }
    public BigDecimal getCouponAmount() {
        return couponAmount;
    }
    public void setCouponAmount(BigDecimal couponAmount) {
        this.couponAmount = couponAmount;
    }
    public Long getShopId() {
        return shopId;
    }
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
    public List<userReturnBoluomeCouponVo> getReturnCouponList() {
        return returnCouponList;
    }
    public void setReturnCouponList(List<userReturnBoluomeCouponVo> returnCouponList) {
        this.returnCouponList = returnCouponList;
    }
    
 }