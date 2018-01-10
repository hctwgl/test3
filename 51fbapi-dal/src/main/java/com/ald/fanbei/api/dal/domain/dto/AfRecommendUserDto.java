package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;

import java.math.BigDecimal;
import java.util.Date;

public class AfRecommendUserDto extends AfRecommendUserDo {
 
    private String  riskStatus;  //基础认证状态【A:未认证，N:认证失败，Y:已通过认证】
    private int  isloan;      //是否己借款0 没有 ，1 己借款
    private int  isShoppingRebate;     //商城购物是否给邀请者返利 0：未返利 1：已返利
    public String getRiskStatus() {
        return riskStatus;
    }
    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }
    public int getIsloan() {
        return isloan;
    }
    public void setIsloan(int isloan) {
        this.isloan = isloan;
    }
    public int getIsShoppingRebate() {
        return isShoppingRebate;
    }
    public void setIsShoppingRebate(int isShoppingRebate) {
        this.isShoppingRebate = isShoppingRebate;
    }
   
    
}
