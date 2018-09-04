package com.ald.fanbei.api.biz.bo.xgxy;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class XgxyRepayBo {
    private String isFinish;
    private String curPeriod;
    private String unrepayAmount;
    private String unrepayInterestFee;
    private String unrepayOverdueFee;
    private String unrepayServiceFee;
}
