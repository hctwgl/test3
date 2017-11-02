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
 public class AfDeUserCutInfoDo extends AbstractSerial {

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
     * 用户与砍价商品记录的id
     */
    private Long usergoodsid;

    /**
     * 砍价次序
     */
    private Integer cutindex;

    /**
     * 本次砍价金额
     */
    private BigDecimal cutprice;

    /**
     * 
     */
    private String openid;

    /**
     * 
     */
    private String nickname;

    /**
     * 
     */
    private String headimgurl;

    /**
     * 本次砍价后商品剩余价格
     */
    private BigDecimal remainprice;


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
     * 获取用户与砍价商品记录的id
     *
     * @return 用户与砍价商品记录的id
     */
    public Long getUsergoodsid(){
      return usergoodsid;
    }

    /**
     * 设置用户与砍价商品记录的id
     * 
     * @param usergoodsid 要设置的用户与砍价商品记录的id
     */
    public void setUsergoodsid(Long usergoodsid){
      this.usergoodsid = usergoodsid;
    }

    /**
     * 获取砍价次序
     *
     * @return 砍价次序
     */
    public Integer getCutindex(){
      return cutindex;
    }

    /**
     * 设置砍价次序
     * 
     * @param cutindex 要设置的砍价次序
     */
    public void setCutindex(Integer cutindex){
      this.cutindex = cutindex;
    }

    /**
     * 获取本次砍价金额
     *
     * @return 本次砍价金额
     */
    public BigDecimal getCutprice(){
      return cutprice;
    }

    /**
     * 设置本次砍价金额
     * 
     * @param cutprice 要设置的本次砍价金额
     */
    public void setCutprice(BigDecimal cutprice){
      this.cutprice = cutprice;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getOpenid(){
      return openid;
    }

    /**
     * 设置
     * 
     * @param openid 要设置的
     */
    public void setOpenid(String openid){
      this.openid = openid;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getNickname(){
      return nickname;
    }

    /**
     * 设置
     * 
     * @param nickname 要设置的
     */
    public void setNickname(String nickname){
      this.nickname = nickname;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getHeadimgurl(){
      return headimgurl;
    }

    /**
     * 设置
     * 
     * @param headimgurl 要设置的
     */
    public void setHeadimgurl(String headimgurl){
      this.headimgurl = headimgurl;
    }

    /**
     * 获取本次砍价后商品剩余价格
     *
     * @return 本次砍价后商品剩余价格
     */
    public BigDecimal getRemainprice(){
      return remainprice;
    }

    /**
     * 设置本次砍价后商品剩余价格
     * 
     * @param remainprice 要设置的本次砍价后商品剩余价格
     */
    public void setRemainprice(BigDecimal remainprice){
      this.remainprice = remainprice;
    }

}