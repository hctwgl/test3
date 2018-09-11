package com.ald.fanbei.api.biz.bo;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class RepaymentBo {

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 公司编号, 由token解析得到
     */
    private String companyId;

    /**
     * 还款总额
     */
    private String totalAmount;

    /**
     * 还款流水
     */
    private String repaymentNo;

    /**
     * 还款类型 10 app还款 20线下还款
     */
    private String type;

    /**
     * 还款时间
     */
    private String repayTime;

    /**
     * 还款账号
     */
    private String repaymentAcc;

    /**
     * 还款详情分配
     */
    private String details;



    public RepaymentBo() {
        // TODO Auto-generated constructor stub
    }

    public RepaymentBo(String orderNo, String companyId, String totalAmount, String repaymentNo, String type,
                       String repayTime, String repaymentAcc, String details) {
        super();
        this.orderNo = orderNo;
        this.companyId = companyId;
        this.totalAmount = totalAmount;
        this.repaymentNo = repaymentNo;
        this.type = type;
        this.repayTime = repayTime;
        this.repaymentAcc = repaymentAcc;
        this.details = details;
    }

}