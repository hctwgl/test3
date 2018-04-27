package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 分类运营位配置实体
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-04-11 19:59:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfCategoryOprationDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 一级分类id
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 运营位图片url
     */
    private String imgUrl;

    /**
     * 跳转链接url
     */
    private String linkUrl;


    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 状态 1：启用,2:禁用
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
     * 获取一级分类id
     *
     * @return 一级分类id
     */
    public Long getCategoryId(){
      return categoryId;
    }

    /**
     * 设置一级分类id
     * 
     * @param categoryId 要设置的一级分类id
     */
    public void setCategoryId(Long categoryId){
      this.categoryId = categoryId;
    }

    /**
     * 获取分类名称
     *
     * @return 分类名称
     */
    public String getCategoryName(){
      return categoryName;
    }

    /**
     * 设置分类名称
     * 
     * @param categoryName 要设置的分类名称
     */
    public void setCategoryName(String categoryName){
      this.categoryName = categoryName;
    }

    /**
     * 获取运营位图片url
     *
     * @return 运营位图片url
     */
    public String getImgUrl(){
      return imgUrl;
    }

    /**
     * 设置运营位图片url
     * 
     * @param imgUrl 要设置的运营位图片url
     */
    public void setImgUrl(String imgUrl){
      this.imgUrl = imgUrl;
    }

    /**
     * 获取跳转链接url
     *
     * @return 跳转链接url
     */
    public String getLinkUrl(){
      return linkUrl;
    }

    /**
     * 设置跳转链接url
     * 
     * @param linkUrl 要设置的跳转链接url
     */
    public void setLinkUrl(String linkUrl){
      this.linkUrl = linkUrl;
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
     * 获取状态 1：启用,2:禁用
     *
     * @return 状态 1：启用,2:禁用
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置状态 1：启用,2:禁用
     * 
     * @param status 要设置的状态 1：启用,2:禁用
     */
    public void setStatus(Integer status){
      this.status = status;
    }

}