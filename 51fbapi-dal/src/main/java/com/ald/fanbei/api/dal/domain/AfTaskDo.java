package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 分类运营位配置实体
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 14:44:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
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
     * 任务类型
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
     * 是否启用：0：禁用；1：启用
     */
    private Integer isOpen;

    /**
     * 创建人
     */
    private String creater;

    /**
     * 修改人
     */
    private String modifier;


    /**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid(){
      return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtCreate 要设置的创建时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取修改时间
     *
     * @return 修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置修改时间
     * 
     * @param gmtModified 要设置的修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取用户层级：0：所有用户；1：新用户；2:消费分期强风控通过用户；3:仅购物一次用户；4:忠实购物用户；5:消费分期强风控通过用户并且未购物
     *
     * @return 用户层级：0：所有用户；1：新用户；2:消费分期强风控通过用户；3:仅购物一次用户；4:忠实购物用户；5:消费分期强风控通过用户并且未购物
     */
    public Integer getUserLevel(){
      return userLevel;
    }

    /**
     * 设置用户层级：0：所有用户；1：新用户；2:消费分期强风控通过用户；3:仅购物一次用户；4:忠实购物用户；5:消费分期强风控通过用户并且未购物
     * 
     * @param userLevel 要设置的用户层级：0：所有用户；1：新用户；2:消费分期强风控通过用户；3:仅购物一次用户；4:忠实购物用户；5:消费分期强风控通过用户并且未购物
     */
    public void setUserLevel(Integer userLevel){
      this.userLevel = userLevel;
    }

    /**
     * 获取icon图片地址
     *
     * @return icon图片地址
     */
    public String getIconUrl(){
      return iconUrl;
    }

    /**
     * 设置icon图片地址
     * 
     * @param iconUrl 要设置的icon图片地址
     */
    public void setIconUrl(String iconUrl){
      this.iconUrl = iconUrl;
    }

    /**
     * 获取任务类型
     *
     * @return 任务类型
     */
    public String getTaskType(){
      return taskType;
    }

    /**
     * 设置任务类型
     * 
     * @param taskType 要设置的任务类型
     */
    public void setTaskType(String taskType){
      this.taskType = taskType;
    }

    /**
     * 获取子任务类型
     *
     * @return 子任务类型
     */
    public String getTaskSecType(){
      return taskSecType;
    }

    /**
     * 设置子任务类型
     * 
     * @param taskSecType 要设置的子任务类型
     */
    public void setTaskSecType(String taskSecType){
      this.taskSecType = taskSecType;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getTaskName(){
      return taskName;
    }

    /**
     * 设置
     * 
     * @param taskName 要设置的
     */
    public void setTaskName(String taskName){
      this.taskName = taskName;
    }

    /**
     * 获取任务条件
     *
     * @return 任务条件
     */
    public String getTaskCondition(){
      return taskCondition;
    }

    /**
     * 设置任务条件
     * 
     * @param taskCondition 要设置的任务条件
     */
    public void setTaskCondition(String taskCondition){
      this.taskCondition = taskCondition;
    }

    /**
     * 获取每日更新：0：不更新；1：更新
     *
     * @return 每日更新：0：不更新；1：更新
     */
    public Integer getIsDailyUpdate(){
      return isDailyUpdate;
    }

    /**
     * 设置每日更新：0：不更新；1：更新
     * 
     * @param isDailyUpdate 要设置的每日更新：0：不更新；1：更新
     */
    public void setIsDailyUpdate(Integer isDailyUpdate){
      this.isDailyUpdate = isDailyUpdate;
    }

    /**
     * 获取0:金币；1：现金；2：优惠券
     *
     * @return 0:金币；1：现金；2：优惠券
     */
    public Integer getRewardType(){
      return rewardType;
    }

    /**
     * 设置0:金币；1：现金；2：优惠券
     * 
     * @param rewardType 要设置的0:金币；1：现金；2：优惠券
     */
    public void setRewardType(Integer rewardType){
      this.rewardType = rewardType;
    }

    /**
     * 获取金币数量
     *
     * @return 金币数量
     */
    public Long getCoinAmount(){
      return coinAmount;
    }

    /**
     * 设置金币数量
     * 
     * @param coinAmount 要设置的金币数量
     */
    public void setCoinAmount(Long coinAmount){
      this.coinAmount = coinAmount;
    }

    /**
     * 获取现金数量
     *
     * @return 现金数量
     */
    public BigDecimal getCashAmount(){
      return cashAmount;
    }

    /**
     * 设置现金数量
     * 
     * @param cashAmount 要设置的现金数量
     */
    public void setCashAmount(BigDecimal cashAmount){
      this.cashAmount = cashAmount;
    }

    /**
     * 获取优惠券ID
     *
     * @return 优惠券ID
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券ID
     * 
     * @param couponId 要设置的优惠券ID
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }


    /**
     * 获取是否启用：0：禁用；1：启用
     *
     * @return 是否启用：0：禁用；1：启用
     */
    public Integer getIsOpen(){
      return isOpen;
    }

    /**
     * 设置是否启用：0：禁用；1：启用
     * 
     * @param isOpen 要设置的是否启用：0：禁用；1：启用
     */
    public void setIsOpen(Integer isOpen){
      this.isOpen = isOpen;
    }

    /**
     * 获取创建人
     *
     * @return 创建人
     */
    public String getCreater(){
      return creater;
    }

    /**
     * 设置创建人
     * 
     * @param creater 要设置的创建人
     */
    public void setCreater(String creater){
      this.creater = creater;
    }

    /**
     * 获取修改人
     *
     * @return 修改人
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置修改人
     * 
     * @param modifier 要设置的修改人
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

}