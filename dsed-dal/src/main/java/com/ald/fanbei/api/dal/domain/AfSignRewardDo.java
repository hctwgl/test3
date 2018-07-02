package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 分类运营位配置实体
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-05-07 13:51:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfSignRewardDo extends AbstractSerial {

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
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 0:自己签到；1：朋友帮签；2：补签
     */
    private Integer type;

    /**
     * 奖励状态：0：未提取；1：已提现
     */
    private Integer status;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 帮签用户ID
     */
    private Long friendUserId;

    private Integer isDelete;

    private Date time;



}