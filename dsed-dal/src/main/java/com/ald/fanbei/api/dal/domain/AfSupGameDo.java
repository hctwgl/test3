package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 游戏充值实体
 * 
 * @author 高继斌_temple
 * @version 1.0.0 初始化
 * @date 2017-11-24 18:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfSupGameDo extends AbstractSerial {

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
     * H5页面游戏展示名称
     */
    private String name;

    /**
     * 游戏编号
     */
    private String code;

    /**
     * 列表显示图标
     */
    private String image;

    /**
     * 游戏属性xml文件
     */
    private String xmlFile;

    /**
     * 
     */
    private String xmlType;

    /**
     * 游戏名称首字母
     */
    private String firstChar;

    /**
     * 是否顶部推荐
     */
    private Integer isHot;

    /**
     * 顶部推荐显示图片
     */
    private String hotImage;

    /**
     * 游戏充值官方折扣
     */
    private BigDecimal officalDiscount;

    /**
     * 游戏充值供应商折扣
     */
    private BigDecimal businessDiscount;

    /**
     * 用户充值返利比例=offical_discount - business_discount
     */
    private BigDecimal userRebate;

    /**
     * 类型：GAME 游戏，AMUSEMENT 娱乐
     */
    private String type;

    /**
     * 子类型：QQ qq会员，VIDEO 视频会员
     */
    private String secType;

    /**
     * 显示顺序
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
     * 获取H5页面游戏展示名称
     *
     * @return H5页面游戏展示名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置H5页面游戏展示名称
     * 
     * @param name 要设置的H5页面游戏展示名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取游戏编号
     *
     * @return 游戏编号
     */
    public String getCode(){
      return code;
    }

    /**
     * 设置游戏编号
     * 
     * @param code 要设置的游戏编号
     */
    public void setCode(String code){
      this.code = code;
    }

    /**
     * 获取列表显示图标
     *
     * @return 列表显示图标
     */
    public String getImage(){
      return image;
    }

    /**
     * 设置列表显示图标
     * 
     * @param image 要设置的列表显示图标
     */
    public void setImage(String image){
      this.image = image;
    }

    /**
     * 获取游戏属性xml文件
     *
     * @return 游戏属性xml文件
     */
    public String getXmlFile(){
      return xmlFile;
    }

    /**
     * 设置游戏属性xml文件
     * 
     * @param xmlFile 要设置的游戏属性xml文件
     */
    public void setXmlFile(String xmlFile){
      this.xmlFile = xmlFile;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getXmlType(){
      return xmlType;
    }

    /**
     * 设置
     * 
     * @param xmlType 要设置的
     */
    public void setXmlType(String xmlType){
      this.xmlType = xmlType;
    }

    /**
     * 获取游戏名称首字母
     *
     * @return 游戏名称首字母
     */
    public String getFirstChar(){
      return firstChar;
    }

    /**
     * 设置游戏名称首字母
     * 
     * @param firstChar 要设置的游戏名称首字母
     */
    public void setFirstChar(String firstChar){
      this.firstChar = firstChar;
    }

    /**
     * 获取是否顶部推荐
     *
     * @return 是否顶部推荐
     */
    public Integer getIsHot(){
      return isHot;
    }

    /**
     * 设置是否顶部推荐
     * 
     * @param isHot 要设置的是否顶部推荐
     */
    public void setIsHot(Integer isHot){
      this.isHot = isHot;
    }

    /**
     * 获取顶部推荐显示图片
     *
     * @return 顶部推荐显示图片
     */
    public String getHotImage(){
      return hotImage;
    }

    /**
     * 设置顶部推荐显示图片
     * 
     * @param hotImage 要设置的顶部推荐显示图片
     */
    public void setHotImage(String hotImage){
      this.hotImage = hotImage;
    }

    /**
     * 获取游戏充值官方折扣
     *
     * @return 游戏充值官方折扣
     */
    public BigDecimal getOfficalDiscount(){
      return officalDiscount;
    }

    /**
     * 设置游戏充值官方折扣
     * 
     * @param officalDiscount 要设置的游戏充值官方折扣
     */
    public void setOfficalDiscount(BigDecimal officalDiscount){
      this.officalDiscount = officalDiscount;
    }

    /**
     * 获取游戏充值供应商折扣
     *
     * @return 游戏充值供应商折扣
     */
    public BigDecimal getBusinessDiscount(){
      return businessDiscount;
    }

    /**
     * 设置游戏充值供应商折扣
     * 
     * @param businessDiscount 要设置的游戏充值供应商折扣
     */
    public void setBusinessDiscount(BigDecimal businessDiscount){
      this.businessDiscount = businessDiscount;
    }

    /**
     * 获取用户充值返利比例=offical_discount - business_discount
     *
     * @return 用户充值返利比例=offical_discount - business_discount
     */
    public BigDecimal getUserRebate(){
      return userRebate;
    }

    /**
     * 设置用户充值返利比例=offical_discount - business_discount
     * 
     * @param userRebate 要设置的用户充值返利比例=offical_discount - business_discount
     */
    public void setUserRebate(BigDecimal userRebate){
      this.userRebate = userRebate;
    }

    /**
     * 获取类型：GAME 游戏，AMUSEMENT 娱乐
     *
     * @return 类型：GAME 游戏，AMUSEMENT 娱乐
     */
    public String getType(){
      return type;
    }

    /**
     * 设置类型：GAME 游戏，AMUSEMENT 娱乐
     * 
     * @param type 要设置的类型：GAME 游戏，AMUSEMENT 娱乐
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取子类型：QQ qq会员，VIDEO 视频会员
     *
     * @return 子类型：QQ qq会员，VIDEO 视频会员
     */
    public String getSecType(){
      return secType;
    }

    /**
     * 设置子类型：QQ qq会员，VIDEO 视频会员
     * 
     * @param secType 要设置的子类型：QQ qq会员，VIDEO 视频会员
     */
    public void setSecType(String secType){
      this.secType = secType;
    }

    /**
     * 获取显示顺序
     *
     * @return 显示顺序
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置显示顺序
     * 
     * @param sort 要设置的显示顺序
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

}