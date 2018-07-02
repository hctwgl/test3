package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 频道配置表实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-04-12 17:59:56
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfHomePageChannelConfigureDo extends AbstractSerial {

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
     * 0:轮播；1:导航;2:运营位
     */
    private Integer configureType;

    /**
     * 
     */
    private String jumpUrl;

    /**
     * 跳转类型。1:品牌；2:类目；3:H5
     */
    private Integer jumpType;

    /**
     * 图片链接
     */
    private String imageUrl;

    /**
     * 频道id
     */
    private Integer channelId;

    /**
     * 0禁用,1启用
     */
    private Integer status;

    /**
     * 排序字段值越大越靠前
     */
    private Integer sort;

    /**
     * 位置。0:上。1:下左,2下中，3下右
     */
    private Integer position;

    /**
     * 导航名
     */
    private String name;


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
     * 获取0:轮播；1:导航;2:运营位
     *
     * @return 0:轮播；1:导航;2:运营位
     */
    public Integer getConfigureType(){
      return configureType;
    }

    /**
     * 设置0:轮播；1:导航;2:运营位
     * 
     * @param configureType 要设置的0:轮播；1:导航;2:运营位
     */
    public void setConfigureType(Integer configureType){
      this.configureType = configureType;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getJumpUrl(){
      return jumpUrl;
    }

    /**
     * 设置
     * 
     * @param jumpUrl 要设置的
     */
    public void setJumpUrl(String jumpUrl){
      this.jumpUrl = jumpUrl;
    }

    /**
     * 获取跳转类型。1:品牌；2:类目；3:H5
     *
     * @return 跳转类型。1:品牌；2:类目；3:H5
     */
    public Integer getJumpType(){
      return jumpType;
    }

    /**
     * 设置跳转类型。1:品牌；2:类目；3:H5
     * 
     * @param jumpType 要设置的跳转类型。1:品牌；2:类目；3:H5
     */
    public void setJumpType(Integer jumpType){
      this.jumpType = jumpType;
    }

    /**
     * 获取图片链接
     *
     * @return 图片链接
     */
    public String getImageUrl(){
      return imageUrl;
    }

    /**
     * 设置图片链接
     * 
     * @param imageUrl 要设置的图片链接
     */
    public void setImageUrl(String imageUrl){
      this.imageUrl = imageUrl;
    }

    /**
     * 获取频道id
     *
     * @return 频道id
     */
    public Integer getChannelId(){
      return channelId;
    }

    /**
     * 设置频道id
     * 
     * @param channelId 要设置的频道id
     */
    public void setChannelId(Integer channelId){
      this.channelId = channelId;
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

    /**
     * 获取位置。0:上。1:下左,2下中，3下右
     *
     * @return 位置。0:上。1:下左,2下中，3下右
     */
    public Integer getPosition(){
      return position;
    }

    /**
     * 设置位置。0:上。1:下左,2下中，3下右
     * 
     * @param position 要设置的位置。0:上。1:下左,2下中，3下右
     */
    public void setPosition(Integer position){
      this.position = position;
    }

    /**
     * 获取导航名
     *
     * @return 导航名
     */
    public String getName(){
      return name;
    }

    /**
     * 设置导航名
     * 
     * @param name 要设置的导航名
     */
    public void setName(String name){
      this.name = name;
    }

}