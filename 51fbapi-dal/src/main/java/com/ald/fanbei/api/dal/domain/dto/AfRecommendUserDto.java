package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;

import java.math.BigDecimal;
import java.util.Date;

public class AfRecommendUserDto extends AfRecommendUserDo {
 
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String  riskStatus;  //基础认证状态【A:未认证，N:认证失败，Y:已通过认证】
    private int  isloan;      //是否己借款0 没有 ，1 己借款

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
  
   
    
}
