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
 public class BoluomeActivityPoPupVo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

   private String couponToPop;
   private String couponImage;
   private String couponAmount;
   private String couponUrl;
   private String rebateToPop;
   private String rebateImage;
   private String rebateAmount;
   private String rebateUrl;
   private String sceneName;
   
    public String getCouponToPop() {
        return couponToPop;
    }
    public void setCouponToPop(String couponToPop) {
        this.couponToPop = couponToPop;
    }
    public String getCouponImage() {
        return couponImage;
    }
    public void setCouponImage(String couponImage) {
        this.couponImage = couponImage;
    }
    public String getCouponAmount() {
        return couponAmount;
    }
    public void setCouponAmount(String couponAmount) {
        this.couponAmount = couponAmount;
    }
    public String getCouponUrl() {
        return couponUrl;
    }
    public void setCouponUrl(String couponUrl) {
        this.couponUrl = couponUrl;
    }
    public String getRebateToPop() {
        return rebateToPop;
    }
    public void setRebateToPop(String rebateToPop) {
        this.rebateToPop = rebateToPop;
    }
    public String getRebateImage() {
        return rebateImage;
    }
    public void setRebateImage(String rebateImage) {
        this.rebateImage = rebateImage;
    }
    public String getRebateAmount() {
        return rebateAmount;
    }
    public void setRebateAmount(String rebateAmount) {
        this.rebateAmount = rebateAmount;
    }
    public String getRebateUrl() {
        return rebateUrl;
    }
    public void setRebateUrl(String rebateUrl) {
        this.rebateUrl = rebateUrl;
    }
    public String getSceneName() {
        return sceneName;
    }
    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }
       
   
   
   
 }