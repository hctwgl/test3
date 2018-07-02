package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 分期商品管理实体
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-09-21 11:07:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAdInstalmentsDo extends AbstractSerial {

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
     * 创建人
     */
    private String creator;

    /**
     * 最后修改人
     */
    private String modifier;

    /**
     * 广告位名称
     */
    private String name;

    /**
     * 商品链接
     */
    private String pictureUrl;

    /**
     * 是否开启：0 关闭，1 开启
     */
    private Integer status;

    /**
     * 跳转链接
     */
    private String detailUrl;

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
     * 获取最后修改人
     *
     * @return 最后修改人
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置最后修改人
     * 
     * @param modifier 要设置的最后修改人
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取广告位名称
     *
     * @return 广告位名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置广告位名称
     * 
     * @param name 要设置的广告位名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取商品链接
     *
     * @return 商品链接
     */
    public String getPictureUrl(){
      return pictureUrl;
    }

    /**
     * 设置商品链接
     * 
     * @param pictureUrl 要设置的商品链接
     */
    public void setPictureUrl(String pictureUrl){
      this.pictureUrl = pictureUrl;
    }

    /**
     * 获取是否开启：0 关闭，1 开启
     *
     * @return 是否开启：0 关闭，1 开启
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置是否开启：0 关闭，1 开启
     * 
     * @param status 要设置的是否开启：0 关闭，1 开启
     */
    public void setStatus(Integer status){
      this.status = status;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}