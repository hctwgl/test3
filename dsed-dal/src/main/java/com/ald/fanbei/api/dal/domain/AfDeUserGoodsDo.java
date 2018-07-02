package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 双十一砍价实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfDeUserGoodsDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userid;

    /**
     * 商品规格ID
     */
    private Long goodspriceid;

    /**
     * 0:未使用该砍价优惠，1:已使用
     */
    private Integer isbuy;

    /**
     * 被砍价总次数（包含已到底价后砍价次数）
     */
    private Integer cutcount;

    /**
     * 已砍金额
     */
    private BigDecimal cutprice;

    /**
     * 砍至最低底价的完成时间
     */
    private Date gmtCompletetime;

    /**
     * 砍至底价的刀数
     */
    private Integer completecount;


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
     * 获取用户id
     *
     * @return 用户id
     */
    public Long getUserid(){
      return userid;
    }

    /**
     * 设置用户id
     * 
     * @param userid 要设置的用户id
     */
    public void setUserid(Long userid){
      this.userid = userid;
    }

    /**
     * 获取商品规格ID
     *
     * @return 商品规格ID
     */
    public Long getGoodspriceid(){
      return goodspriceid;
    }

    /**
     * 设置商品规格ID
     * 
     * @param goodspriceid 要设置的商品规格ID
     */
    public void setGoodspriceid(Long goodspriceid){
      this.goodspriceid = goodspriceid;
    }

    /**
     * 获取0:未使用该砍价优惠，1:已使用
     *
     * @return 0:未使用该砍价优惠，1:已使用
     */
    public Integer getIsbuy(){
      return isbuy;
    }

    /**
     * 设置0:未使用该砍价优惠，1:已使用
     * 
     * @param isbuy 要设置的0:未使用该砍价优惠，1:已使用
     */
    public void setIsbuy(Integer isbuy){
      this.isbuy = isbuy;
    }

    /**
     * 获取被砍价总次数（包含已到底价后砍价次数）
     *
     * @return 被砍价总次数（包含已到底价后砍价次数）
     */
    public Integer getCutcount(){
      return cutcount;
    }

    /**
     * 设置被砍价总次数（包含已到底价后砍价次数）
     * 
     * @param cutcount 要设置的被砍价总次数（包含已到底价后砍价次数）
     */
    public void setCutcount(Integer cutcount){
      this.cutcount = cutcount;
    }

    /**
     * 获取已砍金额
     *
     * @return 已砍金额
     */
    public BigDecimal getCutprice(){
      return cutprice;
    }

    /**
     * 设置已砍金额
     * 
     * @param cutprice 要设置的已砍金额
     */
    public void setCutprice(BigDecimal cutprice){
      this.cutprice = cutprice;
    }

    /**
     * 获取砍至最低底价的完成时间
     *
     * @return 砍至最低底价的完成时间
     */
    public Date getGmtCompletetime(){
      return gmtCompletetime;
    }

    /**
     * 设置砍至最低底价的完成时间
     * 
     * @param gmtCompletetime 要设置的砍至最低底价的完成时间
     */
    public void setGmtCompletetime(Date gmtCompletetime){
      this.gmtCompletetime = gmtCompletetime;
    }

    /**
     * 获取砍至底价的刀数
     *
     * @return 砍至底价的刀数
     */
    public Integer getCompletecount(){
      return completecount;
    }

    /**
     * 设置砍至底价的刀数
     * 
     * @param completecount 要设置的砍至底价的刀数
     */
    public void setCompletecount(Integer completecount){
      this.completecount = completecount;
    }

}