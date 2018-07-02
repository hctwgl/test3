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
 public class AfVisualH5Do extends AbstractSerial {

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
     * H5页面名称
     */
    private String name;

    /**
     * 页面跳转地址
     */
    private String url;

    /**
     * 页面跳转名称
     */
    private String urlName;

    /**
     * 状态【START：启动, FORBIDDEN:禁用】
     */
    private String status;

    /**
     * 是否搜索【Y：是，N：否】
     */
    private String isSearch;


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
     * 获取H5页面名称
     *
     * @return H5页面名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置H5页面名称
     * 
     * @param name 要设置的H5页面名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取页面跳转地址
     *
     * @return 页面跳转地址
     */
    public String getUrl(){
      return url;
    }

    /**
     * 设置页面跳转地址
     * 
     * @param url 要设置的页面跳转地址
     */
    public void setUrl(String url){
      this.url = url;
    }

    /**
     * 获取页面跳转名称
     *
     * @return 页面跳转名称
     */
    public String getUrlName(){
      return urlName;
    }

    /**
     * 设置页面跳转名称
     * 
     * @param urlName 要设置的页面跳转名称
     */
    public void setUrlName(String urlName){
      this.urlName = urlName;
    }

    /**
     * 获取状态【START：启动, FORBIDDEN:禁用】
     *
     * @return 状态【START：启动, FORBIDDEN:禁用】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态【START：启动, FORBIDDEN:禁用】
     * 
     * @param status 要设置的状态【START：启动, FORBIDDEN:禁用】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取是否搜索【Y：是，N：否】
     *
     * @return 是否搜索【Y：是，N：否】
     */
    public String getIsSearch(){
      return isSearch;
    }

    /**
     * 设置是否搜索【Y：是，N：否】
     * 
     * @param isSearch 要设置的是否搜索【Y：是，N：否】
     */
    public void setIsSearch(String isSearch){
      this.isSearch = isSearch;
    }

}