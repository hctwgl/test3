package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 定向广告规则实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-05-17 22:38:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAdvertiseDo extends AbstractSerial {

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
     * 广告规则名称
     */
    private String name;

    /**
     * 位置编码
     */
    private String positionCode;

    /**
     * 广告图片地址
     */
    private String image;

    /**
     * 广告url地址
     */
    private String url;

    /**
     * 1:H5_URL 普通H5地址(2同); 2:NAVIGATION_H5  H5(1同); 3:BRAND  菠萝觅专场(4同); 4:NAVIGATION_BOLUOME  跳到菠萝蜜的h5页面(3同); 5:NAVIGATION_CATEGORY 分类(主页); 6:CATEGORY_ID 类目(具体类目); 7:SEARCH_TAG 搜索tag页; 8:COUPON_LIST 优惠券列表; 9:GOODS_ID 商品详情; 10: NAVIGATION_BORROW 借钱
     */
    private String urlType;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 点击次数
     */
    private Long clickCount;

    /**
     * 每日投放数量
     */
    private Long dayCount;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * Y 开启  N关闭
     */
    private String status;

    /**
     * 
     */
    private String remark;

    /**
     * json格式，保存所有广告标签条件
     */
    private String tagConditions;

    /**
     * 推荐规则查询条件
     */
    private String queryConditions;


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
     * 获取广告规则名称
     *
     * @return 广告规则名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置广告规则名称
     * 
     * @param name 要设置的广告规则名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取位置编码
     *
     * @return 位置编码
     */
    public String getPositionCode(){
      return positionCode;
    }

    /**
     * 设置位置编码
     * 
     * @param positionCode 要设置的位置编码
     */
    public void setPositionCode(String positionCode){
      this.positionCode = positionCode;
    }

    /**
     * 获取广告图片地址
     *
     * @return 广告图片地址
     */
    public String getImage(){
      return image;
    }

    /**
     * 设置广告图片地址
     * 
     * @param image 要设置的广告图片地址
     */
    public void setImage(String image){
      this.image = image;
    }

    /**
     * 获取广告url地址
     *
     * @return 广告url地址
     */
    public String getUrl(){
      return url;
    }

    /**
     * 设置广告url地址
     * 
     * @param url 要设置的广告url地址
     */
    public void setUrl(String url){
      this.url = url;
    }

    /**
     * 获取1:H5_URL 普通H5地址(2同); 2:NAVIGATION_H5  H5(1同); 3:BRAND  菠萝觅专场(4同); 4:NAVIGATION_BOLUOME  跳到菠萝蜜的h5页面(3同); 5:NAVIGATION_CATEGORY 分类(主页); 6:CATEGORY_ID 类目(具体类目); 7:SEARCH_TAG 搜索tag页; 8:COUPON_LIST 优惠券列表; 9:GOODS_ID 商品详情; 10: NAVIGATION_BORROW 借钱
     *
     * @return 1:H5_URL 普通H5地址(2同); 2:NAVIGATION_H5  H5(1同); 3:BRAND  菠萝觅专场(4同); 4:NAVIGATION_BOLUOME  跳到菠萝蜜的h5页面(3同); 5:NAVIGATION_CATEGORY 分类(主页); 6:CATEGORY_ID 类目(具体类目); 7:SEARCH_TAG 搜索tag页; 8:COUPON_LIST 优惠券列表; 9:GOODS_ID 商品详情; 10: NAVIGATION_BORROW 借钱
     */
    public String getUrlType(){
      return urlType;
    }

    /**
     * 设置1:H5_URL 普通H5地址(2同); 2:NAVIGATION_H5  H5(1同); 3:BRAND  菠萝觅专场(4同); 4:NAVIGATION_BOLUOME  跳到菠萝蜜的h5页面(3同); 5:NAVIGATION_CATEGORY 分类(主页); 6:CATEGORY_ID 类目(具体类目); 7:SEARCH_TAG 搜索tag页; 8:COUPON_LIST 优惠券列表; 9:GOODS_ID 商品详情; 10: NAVIGATION_BORROW 借钱
     * 
     * @param urlType 要设置的1:H5_URL 普通H5地址(2同); 2:NAVIGATION_H5  H5(1同); 3:BRAND  菠萝觅专场(4同); 4:NAVIGATION_BOLUOME  跳到菠萝蜜的h5页面(3同); 5:NAVIGATION_CATEGORY 分类(主页); 6:CATEGORY_ID 类目(具体类目); 7:SEARCH_TAG 搜索tag页; 8:COUPON_LIST 优惠券列表; 9:GOODS_ID 商品详情; 10: NAVIGATION_BORROW 借钱
     */
    public void setUrlType(String urlType){
      this.urlType = urlType;
    }

    /**
     * 获取开始时间
     *
     * @return 开始时间
     */
    public Date getStartTime(){
      return startTime;
    }

    /**
     * 设置开始时间
     * 
     * @param startTime 要设置的开始时间
     */
    public void setStartTime(Date startTime){
      this.startTime = startTime;
    }

    /**
     * 获取结束时间
     *
     * @return 结束时间
     */
    public Date getEndTime(){
      return endTime;
    }

    /**
     * 设置结束时间
     * 
     * @param endTime 要设置的结束时间
     */
    public void setEndTime(Date endTime){
      this.endTime = endTime;
    }

    /**
     * 获取点击次数
     *
     * @return 点击次数
     */
    public Long getClickCount(){
      return clickCount;
    }

    /**
     * 设置点击次数
     * 
     * @param clickCount 要设置的点击次数
     */
    public void setClickCount(Long clickCount){
      this.clickCount = clickCount;
    }

    /**
     * 获取每日投放数量
     *
     * @return 每日投放数量
     */
    public Long getDayCount(){
      return dayCount;
    }

    /**
     * 设置每日投放数量
     * 
     * @param dayCount 要设置的每日投放数量
     */
    public void setDayCount(Long dayCount){
      this.dayCount = dayCount;
    }

    /**
     * 获取优先级
     *
     * @return 优先级
     */
    public Integer getPriority(){
      return priority;
    }

    /**
     * 设置优先级
     * 
     * @param priority 要设置的优先级
     */
    public void setPriority(Integer priority){
      this.priority = priority;
    }

    /**
     * 获取Y 开启  N关闭
     *
     * @return Y 开启  N关闭
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置Y 开启  N关闭
     * 
     * @param status 要设置的Y 开启  N关闭
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置
     * 
     * @param remark 要设置的
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

    /**
     * 获取json格式，保存所有广告标签条件
     *
     * @return json格式，保存所有广告标签条件
     */
    public String getTagConditions(){
      return tagConditions;
    }

    /**
     * 设置json格式，保存所有广告标签条件
     * 
     * @param tagConditions 要设置的json格式，保存所有广告标签条件
     */
    public void setTagConditions(String tagConditions){
      this.tagConditions = tagConditions;
    }

    /**
     * 获取推荐规则查询条件
     *
     * @return 推荐规则查询条件
     */
    public String getQueryConditions(){
      return queryConditions;
    }

    /**
     * 设置推荐规则查询条件
     * 
     * @param queryConditions 要设置的推荐规则查询条件
     */
    public void setQueryConditions(String queryConditions){
      this.queryConditions = queryConditions;
    }

}