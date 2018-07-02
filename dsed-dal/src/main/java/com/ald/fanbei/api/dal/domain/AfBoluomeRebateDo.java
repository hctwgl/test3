package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 点亮活动新版实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:25
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeRebateDo extends AbstractSerial {

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
     * 1 首单
            0 非首单
     */
    private Integer firstOrder;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 菠萝觅订单，奖励金金额
     */
    private BigDecimal rebateAmount;


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
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified 要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取用户id
     *
     * @return 用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户id
     * 
     * @param userId 要设置的用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取1 首单
            0 非首单
     *
     * @return 1 首单
            0 非首单
     */
    public Integer getFirstOrder(){
      return firstOrder;
    }

    /**
     * 设置1 首单
            0 非首单
     * 
     * @param firstOrder 要设置的1 首单
            0 非首单
     */
    public void setFirstOrder(Integer firstOrder){
      this.firstOrder = firstOrder;
    }

    /**
     * 获取订单id
     *
     * @return 订单id
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置订单id
     * 
     * @param orderId 要设置的订单id
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
    }

    /**
     * 获取菠萝觅订单，奖励金金额
     *
     * @return 菠萝觅订单，奖励金金额
     */
    public BigDecimal getRebateAmount(){
      return rebateAmount;
    }

    /**
     * 设置菠萝觅订单，奖励金金额
     * 
     * @param rebateAmount 要设置的菠萝觅订单，奖励金金额
     */
    public void setRebateAmount(BigDecimal rebateAmount){
      this.rebateAmount = rebateAmount;
    }

}