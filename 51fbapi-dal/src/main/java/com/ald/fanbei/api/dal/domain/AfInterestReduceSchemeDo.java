package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 降息实体
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-03-29 13:41:22
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfInterestReduceSchemeDo extends AbstractSerial {

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
     * 修改人
     */
    private String modifier;

    /**
     * 优惠计划名称
     */
    private String name;

    /**
     * 【N：禁用 Y：启用】
     */
    private String isOpen;

    /**
     * 免息规则id
     */
    private Long interestReduceId;

    /**
     * 规则类型：0：商品；2：分类；1:品牌
     */
    private Integer ruleType;

    /**
     * 标签名
     */
    private String tag;

    /**
     * 生效开始时间
     */
    private Date gmtStart;

    /**
     * 生效结束时间
     */
    private Date gmtEnd;

    /**
     * 规则描述，用于展示
     */
    private String descr;


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

    /**
     * 获取优惠计划名称
     *
     * @return 优惠计划名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置优惠计划名称
     * 
     * @param name 要设置的优惠计划名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取【N：禁用 Y：启用】
     *
     * @return 【N：禁用 Y：启用】
     */
    public String getIsOpen(){
      return isOpen;
    }

    /**
     * 设置【N：禁用 Y：启用】
     * 
     * @param isOpen 要设置的【N：禁用 Y：启用】
     */
    public void setIsOpen(String isOpen){
      this.isOpen = isOpen;
    }

    /**
     * 获取免息规则id
     *
     * @return 免息规则id
     */
    public Long getInterestReduceId(){
      return interestReduceId;
    }

    /**
     * 设置免息规则id
     * 
     * @param interestReduceId 要设置的免息规则id
     */
    public void setInterestReduceId(Long interestReduceId){
      this.interestReduceId = interestReduceId;
    }

    /**
     * 获取规则类型：0：商品；2：分类；1:品牌
     *
     * @return 规则类型：0：商品；2：分类；1:品牌
     */
    public Integer getRuleType(){
      return ruleType;
    }

    /**
     * 设置规则类型：0：商品；2：分类；1:品牌
     * 
     * @param ruleType 要设置的规则类型：0：商品；2：分类；1:品牌
     */
    public void setRuleType(Integer ruleType){
      this.ruleType = ruleType;
    }

    /**
     * 获取标签名
     *
     * @return 标签名
     */
    public String getTag(){
      return tag;
    }

    /**
     * 设置标签名
     * 
     * @param tag 要设置的标签名
     */
    public void setTag(String tag){
      this.tag = tag;
    }

    /**
     * 获取生效开始时间
     *
     * @return 生效开始时间
     */
    public Date getGmtStart(){
      return gmtStart;
    }

    /**
     * 设置生效开始时间
     * 
     * @param gmtStart 要设置的生效开始时间
     */
    public void setGmtStart(Date gmtStart){
      this.gmtStart = gmtStart;
    }

    /**
     * 获取生效结束时间
     *
     * @return 生效结束时间
     */
    public Date getGmtEnd(){
      return gmtEnd;
    }

    /**
     * 设置生效结束时间
     * 
     * @param gmtEnd 要设置的生效结束时间
     */
    public void setGmtEnd(Date gmtEnd){
      this.gmtEnd = gmtEnd;
    }

    /**
     * 获取规则描述，用于展示
     *
     * @return 规则描述，用于展示
     */
    public String getDescr(){
      return descr;
    }

    /**
     * 设置规则描述，用于展示
     * 
     * @param descr 要设置的规则描述，用于展示
     */
    public void setDescr(String descr){
      this.descr = descr;
    }

}