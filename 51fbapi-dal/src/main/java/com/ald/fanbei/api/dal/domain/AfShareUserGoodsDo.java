package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 新人专享实体
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-11-02 11:16:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfShareUserGoodsDo extends AbstractSerial {

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
     * 商品规格ID
     */
    private Long goodsPriceId;

    /**
     * 0:没有购买过，1:购买成功
     */
    private Integer isBuy;

    /**
     * 订单id
     */
    private Integer orderId;


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
     * 获取商品规格ID
     *
     * @return 商品规格ID
     */
    public Long getGoodsPriceId(){
      return goodsPriceId;
    }

    /**
     * 设置商品规格ID
     * 
     * @param goodsPriceId 要设置的商品规格ID
     */
    public void setGoodsPriceId(Long goodsPriceId){
      this.goodsPriceId = goodsPriceId;
    }

    /**
     * 获取0:没有购买过，1:购买成功
     *
     * @return 0:没有购买过，1:购买成功
     */
    public Integer getIsBuy(){
      return isBuy;
    }

    /**
     * 设置0:没有购买过，1:购买成功
     * 
     * @param isBuy 要设置的0:没有购买过，1:购买成功
     */
    public void setIsBuy(Integer isBuy){
      this.isBuy = isBuy;
    }

    /**
     * 获取订单id
     *
     * @return 订单id
     */
    public Integer getOrderId(){
      return orderId;
    }

    /**
     * 设置订单id
     * 
     * @param orderId 要设置的订单id
     */
    public void setOrderId(Integer orderId){
      this.orderId = orderId;
    }

}