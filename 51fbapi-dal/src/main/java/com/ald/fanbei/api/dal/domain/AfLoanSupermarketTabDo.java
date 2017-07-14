package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 借款超市标签实体
 * 
 * @author shencheng
 * @version 1.0.0 初始化
 * @date 2017-07-04 19:23:46
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfLoanSupermarketTabDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    
    /**
     * 标签名称
     */
    private String name;

    /**
     * 标签别名
     */
    private String alias;

    /**
     * 排序号
     */
    private Integer sort;


    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 修改人
     */
    private String modifier;


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
     * 获取标签名称
     *
     * @return 标签名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置标签名称
     * 
     * @param name 要设置的标签名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取标签别名
     *
     * @return 标签别名
     */
    public String getAlias(){
      return alias;
    }

    /**
     * 设置标签别名
     * 
     * @param alias 要设置的标签别名
     */
    public void setAlias(String alias){
      this.alias = alias;
    }

    /**
     * 获取排序号
     *
     * @return 排序号
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序号
     * 
     * @param sort 要设置的排序号
     */
    public void setSort(Integer sort){
      this.sort = sort;
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
     * 获取创建人
     *
     * @return 创建人
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建人
     * 
     * @param creator 要设置的创建人
     */
    public void setCreator(String creator){
      this.creator = creator;
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