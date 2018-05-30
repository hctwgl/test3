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
 * @date 2018-05-08 16:02:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfTaskUserDo extends AbstractSerial {


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
     * 任务ID
     */
    private Long taskId;

   /**
    * 任务名称
    */
   private String taskName;

    /**
     * 奖励类型：0:金币；1：现金；2：优惠券
     */
   private Integer rewardType;

    /**
     * 金币数量
     */
    private Long coinAmount;

    /**
     * 现金数量
     */
    private BigDecimal cashAmount;

    /**
     * 优惠券
     */
    private Long couponId;

    /**
     * 奖励是否领取:0：未领取；1：主动领取；2：自动领取；3：金币已经兑换
     */
    private Integer status;

    /**
     * 奖励领取时间
     */
    private Date rewardTime;

}