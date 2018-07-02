package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 点亮活动新版实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-17 11:51:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeActivityMsgIndexDo extends AbstractSerial {

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
    private Long userId;

    /**
     * 该用户优惠券记录最大id
     */
    private Long couponIndex;

    /**
     * 该用户返利记录最大id
     */
    private Long rebateIndex;


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
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户id
     * 
     * @param userId 要设置的用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取该用户优惠券记录最大id
     *
     * @return 该用户优惠券记录最大id
     */
    public Long getCouponIndex(){
      return couponIndex;
    }

    /**
     * 设置该用户优惠券记录最大id
     * 
     * @param couponIndex 要设置的该用户优惠券记录最大id
     */
    public void setCouponIndex(Long couponIndex){
      this.couponIndex = couponIndex;
    }

    /**
     * 获取该用户返利记录最大id
     *
     * @return 该用户返利记录最大id
     */
    public Long getRebateIndex(){
      return rebateIndex;
    }

    /**
     * 设置该用户返利记录最大id
     * 
     * @param rebateIndex 要设置的该用户返利记录最大id
     */
    public void setRebateIndex(Long rebateIndex){
      this.rebateIndex = rebateIndex;
    }

}