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
 public class AfShareGoodsDo extends AbstractSerial {

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
     * 商品规则
     */
    private Long goodsId;

    /**
     * 立减金额
     */
    private BigDecimal decreasePrice;

    /**
     * 活动的类型【FRESHMAN_SHARE:上架】
     */
    private String type;


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
     * 获取商品规则
     *
     * @return 商品规则
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置商品规则
     * 
     * @param goodsId 要设置的商品规则
     */
    public void setGoodsId(Long goodsId){
      this.goodsId = goodsId;
    }

    /**
     * 获取立减金额
     *
     * @return 立减金额
     */
    public BigDecimal getDecreasePrice(){
      return decreasePrice;
    }

    /**
     * 设置立减金额
     * 
     * @param decreasePrice 要设置的立减金额
     */
    public void setDecreasePrice(BigDecimal decreasePrice){
      this.decreasePrice = decreasePrice;
    }

    /**
     * 获取活动的类型【FRESHMAN_SHARE:上架】
     *
     * @return 活动的类型【FRESHMAN_SHARE:上架】
     */
    public String getType(){
      return type;
    }

    /**
     * 设置活动的类型【FRESHMAN_SHARE:上架】
     * 
     * @param type 要设置的活动的类型【FRESHMAN_SHARE:上架】
     */
    public void setType(String type){
      this.type = type;
    }

}