package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 首页频道表实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-04-12 17:58:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfHomePageChannelDo extends AbstractSerial {

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
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 频道名
     */
    private String name;

    /**
     * 0禁用,1启用
     */
    private Integer status;

    /**
     * 排序字段值越大越靠前
     */
    private Integer sort;


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
     * 获取频道名
     *
     * @return 频道名
     */
    public String getName(){
      return name;
    }

    /**
     * 设置频道名
     * 
     * @param name 要设置的频道名
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取0禁用,1启用
     *
     * @return 0禁用,1启用
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置0禁用,1启用
     * 
     * @param status 要设置的0禁用,1启用
     */
    public void setStatus(Integer status){
      this.status = status;
    }

    /**
     * 获取排序字段值越大越靠前
     *
     * @return 排序字段值越大越靠前
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序字段值越大越靠前
     * 
     * @param sort 要设置的排序字段值越大越靠前
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

}