package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市易贷借款还款表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:45:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
 public class DsedLoanRepaymentDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;


    private int isDelete;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 借款编号
     */
    private Long loanId;

    /**
     * 还款名称
     */
    private String name;

    /**
     * 还款金额
     */
    private BigDecimal repayAmount;

    /**
     * 实际付款金额
     */
    private BigDecimal actualAmount;

    /**
     * 还款状态【'APPLY'-新建状态, 'SUCC'-还款成功, 'FAIL'-还款失败, 'PROCESSING'-处理中，CLOSED-关闭】
     */
    private String status;

    /**
     * 我方交易流水号
     */
    private String tradeNo;

    /**
     * 三方资金交易流水
     */
    private String tradeNoOut;

    /**
     * 用户优惠券Id
     */
    private Long userCouponId;

    /**
     * 优惠券金额
     */
    private BigDecimal couponAmount;

    /**
     * 使用的返利余额
     */
    private BigDecimal userAmount;

    /**
     * 是否提前还款，"Y"表示提前还款，"N"表示非提前还款
     */
    private String preRepayStatus;

    /**
     * 产品类型
     */
    private String prdType;

    /**
     * 该还款对应的期数信息(提前还款 期数id用逗号隔开)
     */
    private String repayPeriods;

    /**
     * 银行卡号
     */
    private String bankCardNumber;

    /**
     * 银行卡名称
     */
    private String bankCardName;

    /**
     * 还款备注
     */
    private String remark;

    /**
     * 还款渠道：ONLINE-线上主动还款，COLLECT-催收平台还款，ADMIN-管理后台财务还款，AUTO_REPAY-自动还款（代扣）
     */
    private String repayChannel;

    private Integer errorCode;

    private String errorMessage;


}