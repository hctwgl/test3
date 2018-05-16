package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
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
 public class AfTaskUserDo extends AbstractSerial {

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
     * 任务ID
     */
    private Long taskId;

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
     * 任务奖励领取状态
     */
    private Integer status;


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
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户ID
     * 
     * @param userId 要设置的用户ID
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取任务ID
     *
     * @return 任务ID
     */
    public Long getTaskId(){
      return taskId;
    }

    /**
     * 设置任务ID
     * 
     * @param taskId 要设置的任务ID
     */
    public void setTaskId(Long taskId){
      this.taskId = taskId;
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
     * 获取优惠券
     *
     * @return 优惠券
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券
     * 
     * @param couponId 要设置的优惠券
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}