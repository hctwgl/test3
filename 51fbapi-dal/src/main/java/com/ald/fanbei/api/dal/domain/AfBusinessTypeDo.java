package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 商户基础信息实体
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2017-07-26 19:32:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBusinessTypeDo extends AbstractSerial {

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
     * 类目名称
     */
    private String name;

    /**
     * 类目级别【1：一级类目 2：二级类目】
     */
    private Integer level;

    /**
     * 父类目id,一级类目为0
     */
    private Long parentId;

    /**
     * 是否显示 【N：不显示 Y：显示】
     */
    private String isShow;

    /**
     * 排序，数字越大越靠前
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
     * 获取类目名称
     *
     * @return 类目名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置类目名称
     * 
     * @param name 要设置的类目名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取类目级别【1：一级类目 2：二级类目】
     *
     * @return 类目级别【1：一级类目 2：二级类目】
     */
    public Integer getLevel(){
      return level;
    }

    /**
     * 设置类目级别【1：一级类目 2：二级类目】
     * 
     * @param level 要设置的类目级别【1：一级类目 2：二级类目】
     */
    public void setLevel(Integer level){
      this.level = level;
    }

    /**
     * 获取父类目id,一级类目为0
     *
     * @return 父类目id,一级类目为0
     */
    public Long getParentId(){
      return parentId;
    }

    /**
     * 设置父类目id,一级类目为0
     * 
     * @param parentId 要设置的父类目id,一级类目为0
     */
    public void setParentId(Long parentId){
      this.parentId = parentId;
    }

    /**
     * 获取是否显示 【N：不显示 Y：显示】
     *
     * @return 是否显示 【N：不显示 Y：显示】
     */
    public String getIsShow(){
      return isShow;
    }

    /**
     * 设置是否显示 【N：不显示 Y：显示】
     * 
     * @param isShow 要设置的是否显示 【N：不显示 Y：显示】
     */
    public void setIsShow(String isShow){
      this.isShow = isShow;
    }

    /**
     * 获取排序，数字越大越靠前
     *
     * @return 排序，数字越大越靠前
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序，数字越大越靠前
     * 
     * @param sort 要设置的排序，数字越大越靠前
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

}