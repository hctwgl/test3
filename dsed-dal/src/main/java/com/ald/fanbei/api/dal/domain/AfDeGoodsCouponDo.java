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
 public class AfDeGoodsCouponDo extends AbstractSerial {

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
     * 优惠劵Id
     */
    private Integer couponid;

    /**
     * 获取优惠卷的条件
     */
    private Integer threshold;


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
     * 获取优惠劵Id
     *
     * @return 优惠劵Id
     */
    public Integer getCouponid(){
      return couponid;
    }

    /**
     * 设置优惠劵Id
     * 
     * @param couponid 要设置的优惠劵Id
     */
    public void setCouponid(Integer couponid){
      this.couponid = couponid;
    }

    /**
     * 获取获取优惠卷的条件
     *
     * @return 获取优惠卷的条件
     */
    public Integer getThreshold(){
      return threshold;
    }

    /**
     * 设置获取优惠卷的条件
     * 
     * @param threshold 要设置的获取优惠卷的条件
     */
    public void setThreshold(Integer threshold){
      this.threshold = threshold;
    }

}