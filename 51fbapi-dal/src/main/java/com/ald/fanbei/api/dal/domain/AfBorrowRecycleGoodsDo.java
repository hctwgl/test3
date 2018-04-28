package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 回收商品表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-28 14:08:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBorrowRecycleGoodsDo extends AbstractSerial {

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
     * 创建者
     */
    private String creator;

    /**
     * 最后修改者
     */
    private String modifier;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品属性组合，json字符串
     */
    private String propertyValue;

    /**
     * 商品图标
     */
    private String goodsImg;

    /**
     * 商品状态【OPEN:开启，CLOSE:关闭】
     */
    private String status;


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
     * 获取创建者
     *
     * @return 创建者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建者
     * 
     * @param creator 要设置的创建者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取最后修改者
     *
     * @return 最后修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置最后修改者
     * 
     * @param modifier 要设置的最后修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取商品名称
     *
     * @return 商品名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置商品名称
     * 
     * @param name 要设置的商品名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取商品属性组合，json字符串
     *
     * @return 商品属性组合，json字符串
     */
    public String getPropertyValue(){
      return propertyValue;
    }

    /**
     * 设置商品属性组合，json字符串
     * 
     * @param propertyValue 要设置的商品属性组合，json字符串
     */
    public void setPropertyValue(String propertyValue){
      this.propertyValue = propertyValue;
    }

    /**
     * 获取商品图标
     *
     * @return 商品图标
     */
    public String getGoodsImg(){
      return goodsImg;
    }

    /**
     * 设置商品图标
     * 
     * @param goodsImg 要设置的商品图标
     */
    public void setGoodsImg(String goodsImg){
      this.goodsImg = goodsImg;
    }

    /**
     * 获取商品状态【OPEN:开启，CLOSE:关闭】
     *
     * @return 商品状态【OPEN:开启，CLOSE:关闭】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置商品状态【OPEN:开启，CLOSE:关闭】
     * 
     * @param status 要设置的商品状态【OPEN:开启，CLOSE:关闭】
     */
    public void setStatus(String status){
      this.status = status;
    }

}