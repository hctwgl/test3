package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 可视化H5实体
 * 
 * @author 周锐
 * @version 1.0.0 初始化
 * @date 2018-04-09 11:02:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfVisualH5ItemDo extends AbstractSerial {

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
     * 可视化H5id
     */
    private Long visualId;

    /**
     * 类型【BANNER:轮播，BACK:底色，COUPON:优惠券,PUSHING:力推商品，HOTIMAGE:热点图片，CATEGORY:分类】
     */
    private String type;

    /**
     * 二级类型（默认空）
     */
    private String secType;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 扩展值1
     */
    private String value1;

    /**
     * 扩展值2
     */
    private String value2;

    /**
     * 扩展值3
     */
    private String value3;

    /**
     * 扩展值4
     */
    private String value4;


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
     * 获取可视化H5id
     *
     * @return 可视化H5id
     */
    public Long getVisualId(){
      return visualId;
    }

    /**
     * 设置可视化H5id
     * 
     * @param visualId 要设置的可视化H5id
     */
    public void setVisualId(Long visualId){
      this.visualId = visualId;
    }

    /**
     * 获取类型【BANNER:轮播，BACK:底色，COUPON:优惠券,PUSHING:力推商品，HOTIMAGE:热点图片，CATEGORY:分类】
     *
     * @return 类型【BANNER:轮播，BACK:底色，COUPON:优惠券,PUSHING:力推商品，HOTIMAGE:热点图片，CATEGORY:分类】
     */
    public String getType(){
      return type;
    }

    /**
     * 设置类型【BANNER:轮播，BACK:底色，COUPON:优惠券,PUSHING:力推商品，HOTIMAGE:热点图片，CATEGORY:分类】
     * 
     * @param type 要设置的类型【BANNER:轮播，BACK:底色，COUPON:优惠券,PUSHING:力推商品，HOTIMAGE:热点图片，CATEGORY:分类】
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取二级类型（默认空）
     *
     * @return 二级类型（默认空）
     */
    public String getSecType(){
      return secType;
    }

    /**
     * 设置二级类型（默认空）
     * 
     * @param secType 要设置的二级类型（默认空）
     */
    public void setSecType(String secType){
      this.secType = secType;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序
     * 
     * @param sort 要设置的排序
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

    /**
     * 获取扩展值1
     *
     * @return 扩展值1
     */
    public String getValue1(){
      return value1;
    }

    /**
     * 设置扩展值1
     * 
     * @param value1 要设置的扩展值1
     */
    public void setValue1(String value1){
      this.value1 = value1;
    }

    /**
     * 获取扩展值2
     *
     * @return 扩展值2
     */
    public String getValue2(){
      return value2;
    }

    /**
     * 设置扩展值2
     * 
     * @param value2 要设置的扩展值2
     */
    public void setValue2(String value2){
      this.value2 = value2;
    }

    /**
     * 获取扩展值3
     *
     * @return 扩展值3
     */
    public String getValue3(){
      return value3;
    }

    /**
     * 设置扩展值3
     * 
     * @param value3 要设置的扩展值3
     */
    public void setValue3(String value3){
      this.value3 = value3;
    }

    /**
     * 获取扩展值4
     *
     * @return 扩展值4
     */
    public String getValue4(){
      return value4;
    }

    /**
     * 设置扩展值4
     * 
     * @param value4 要设置的扩展值4
     */
    public void setValue4(String value4){
      this.value4 = value4;
    }

}