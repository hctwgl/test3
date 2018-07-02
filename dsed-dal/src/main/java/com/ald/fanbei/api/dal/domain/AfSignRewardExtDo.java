package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 分类运营位配置实体
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
 public class AfSignRewardExtDo extends AbstractSerial {

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
     * 用户ID
     */
    private Long userId;

    /**
     * 首次参与时间
     */
    private Date firstDayParticipation;

    /**
     * 周期天数
     */
    private Integer cycleDays;

    /**
     * 签到提醒：0：未开启提醒；1：开启提醒
     */
    private Integer isOpenRemind;


    private BigDecimal amount;

    private Integer isDelete;

}