package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 逾期债权推送的现金贷拓展表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-03-27 16:33:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfBorrowCashOverduePushDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 逾期借款编号
     */
    private String borrowNo;

    /**
     * 原借款id
     */
    private Long borrowId;

    /**
     * 虚拟借钱年化利率
     */
    private BigDecimal borrowRate;

    /**
     * 虚拟借款时间
     */
    private Date gmtArrival;

    /**
     * 虚拟预计还款时间
     */
    private Date gmtPlanRepayment;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;

    private String repayName;
    private String repayAcct;
    private String bankName;
    private String idNumber;


}