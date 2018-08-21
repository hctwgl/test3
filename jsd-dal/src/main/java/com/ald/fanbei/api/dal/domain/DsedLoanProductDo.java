package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市e贷借款产品表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:46
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class DsedLoanProductDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 最低限制金额
     */
    private BigDecimal minAmount;

    /**
     * 最高限制金额
     */
    private BigDecimal maxAmount;

    /**
     * 产品说明
     */
    private String desz;

    /**
     * 产品参数配置
     */
    private String conf;

    /**
     * 产品可借期数
     */
    private Integer periods;

    /**
     * 产品标识
     */
    private String prdType;



    /**
     * 到期还款提醒天数
     */
    private Integer remindDay;

    /**
     * 开关状态：Y-启用中，N-未启用
     */
    private String switch_;



}