package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市E贷用户绑定的银行卡实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:50
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class DsedUserBankcardDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userId;

    /**
     * 银行预留手机号
     */
    private String mobile;

    /**
     * 银行编号
     */
    private String bankCode;

    /**
     * 银行卡号
     */
    private String bankCardNumber;

    /**
     * 是否为主卡【Y:主卡,N:非主卡】
     */
    private String isMain;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 绑定状态【N:new新建,B:bind绑定,U:unbind解绑】
     */
    private String status;

    private String safeCode;

    private String validDate;



}