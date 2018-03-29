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
 public class AfInterestReduceGoodsDo extends AbstractSerial {

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
     * 降息方案id
     */
    private Long interestReduceSchemeId;

    /**
     * 商品id；品牌id；分类id；具体根据scheme表的rule_type（ps：这么变态设计是需求问题导致）
     */
    private Long goodsId;

    /**
     * 免息规则id
     */
    private Long interestReduceRulesId;


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
     * 获取降息方案id
     *
     * @return 降息方案id
     */
    public Long getInterestReduceSchemeId(){
      return interestReduceSchemeId;
    }

    /**
     * 设置降息方案id
     * 
     * @param interestReduceSchemeId 要设置的降息方案id
     */
    public void setInterestReduceSchemeId(Long interestReduceSchemeId){
      this.interestReduceSchemeId = interestReduceSchemeId;
    }

    /**
     * 获取商品id；品牌id；分类id；具体根据scheme表的rule_type（ps：这么变态设计是需求问题导致）
     *
     * @return 商品id；品牌id；分类id；具体根据scheme表的rule_type（ps：这么变态设计是需求问题导致）
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置商品id；品牌id；分类id；具体根据scheme表的rule_type（ps：这么变态设计是需求问题导致）
     * 
     * @param goodsId 要设置的商品id；品牌id；分类id；具体根据scheme表的rule_type（ps：这么变态设计是需求问题导致）
     */
    public void setGoodsId(Long goodsId){
      this.goodsId = goodsId;
    }

    /**
     * 获取免息规则id
     *
     * @return 免息规则id
     */
    public Long getInterestReduceRulesId(){
      return interestReduceRulesId;
    }

    /**
     * 设置免息规则id
     * 
     * @param interestReduceRulesId 要设置的免息规则id
     */
    public void setInterestReduceRulesId(Long interestReduceRulesId){
      this.interestReduceRulesId = interestReduceRulesId;
    }

}