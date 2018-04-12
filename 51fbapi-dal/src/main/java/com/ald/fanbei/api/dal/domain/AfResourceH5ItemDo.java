package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *h5商品资源管理实体
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfResourceH5ItemDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    

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
     * 模板id
     */
    private Long modelId;

    /**
     * 在模板中的类型 如：【BANNER,CATEGORY,GOODSLIST】
     */
    private String type;


    /**
     * 跳转链接
     */
    private String value1;

    /**
     * 商品id
     */
    private String value2;

    /**
     * 图片链接
     */
    private String value3;

    /**
     * 
     */
    private String value4;

    /**
     * 排序
     */
    private Integer sort;


    /**
     * 获取主键Id
     *
     * @return id
     */
    public Long getId(){
      return id;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setId(Long id){
      this.id = id;
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
     * 获取模板id
     *
     * @return 模板id
     */
    public Long getModelId(){
      return modelId;
    }

    /**
     * 设置模板id
     * 
     * @param modelId 要设置的模板id
     */
    public void setModelId(Long modelId){
      this.modelId = modelId;
    }

    /**
     * 获取在模板中的类型 如：【BANNER,CATEGORY,GOODSLIST】
     *
     * @return 在模板中的类型 如：【BANNER,CATEGORY,GOODSLIST】
     */
    public String getType(){
      return type;
    }

    /**
     * 设置在模板中的类型 如：【BANNER,CATEGORY,GOODSLIST】
     * 
     * @param type 要设置的在模板中的类型 如：【BANNER,CATEGORY,GOODSLIST】
     */
    public void setType(String type){
      this.type = type;
    }

   
    /**
     * 获取跳转链接
     *
     * @return 跳转链接
     */
    public String getValue1(){
      return value1;
    }

    /**
     * 设置跳转链接
     * 
     * @param value1 要设置的跳转链接
     */
    public void setValue1(String value1){
      this.value1 = value1;
    }

    /**
     * 获取商品id
     *
     * @return 商品id
     */
    public String getValue2(){
      return value2;
    }

    /**
     * 设置商品id
     * 
     * @param value2 要设置的商品id
     */
    public void setValue2(String value2){
      this.value2 = value2;
    }

    /**
     * 获取图片链接
     *
     * @return 图片链接
     */
    public String getValue3(){
      return value3;
    }

    /**
     * 设置图片链接
     * 
     * @param value3 要设置的图片链接
     */
    public void setValue3(String value3){
      this.value3 = value3;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getValue4(){
      return value4;
    }

    /**
     * 设置
     * 
     * @param value4 要设置的
     */
    public void setValue4(String value4){
      this.value4 = value4;
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

}