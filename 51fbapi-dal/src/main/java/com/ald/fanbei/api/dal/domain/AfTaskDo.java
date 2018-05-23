package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 分类运营位配置实体
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 14:44:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfTaskDo extends AbstractSerial {

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
     * 用户层级：0：所有用户；1：新用户；2:消费分期强风控通过用户；3:仅购物一次用户；4:忠实购物用户；5:消费分期强风控通过用户并且未购物
     */
    private Integer userLevel;

    /**
     * icon图片地址
     */
    private String iconUrl;

    /**
     * 任务类型：browse:浏览；shopping：购物；share：分享；verified：实名；strong_risk：强风控通过;loan_market_access：借贷超市注册
     */
    private String taskType;

    /**
     * 子任务类型
     */
    private String taskSecType;

    /**
     * 
     */
    private String taskName;

    /**
     * 任务条件
     */
    private String taskCondition;

    /**
     * 每日更新：0：不更新；1：更新
     */
    private Integer isDailyUpdate;

    /**
     * 0:金币；1：现金；2：优惠券
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
     * 优惠券ID
     */
    private Long couponId;

   /**
    * 优惠券类型
    */
   private String couponType;

    /**
     * 是否启用：0：禁用；1：启用
     */
    private Integer isOpen;

    /**
     * 是否删除
     */
    private String isDelete;

    /**
     * 创建人
     */
    private String creater;

    /**
     * 修改人
     */
    private String modifier;

   /**
    * 任务有效期：开始时间
    */
   private Date taskBeginTime;

   /**
    * 任务有效期：结束时间
    */
   private Date taskEndTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AfTaskDo afTaskDo = (AfTaskDo) o;
        return Objects.equals(rid, afTaskDo.rid) &&
                Objects.equals(taskName, afTaskDo.taskName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(rid, taskName);
    }
}