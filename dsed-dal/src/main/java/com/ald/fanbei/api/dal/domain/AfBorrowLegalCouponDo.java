package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 合规优惠券记录实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-04-13 13:58:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBorrowLegalCouponDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;

    /**
     * 
     */
    private Long userId;

    /**
     * 对应af_borrow_cash.id
     */
    private Long borrowId;

    /**
     * 续期Id
     */
    private Long renewalId;

    /**
     * 优惠券id
     */
    private Long couponId;


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
     * 获取
     *
     * @return 
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置
     * 
     * @param gmtCreate 要设置的
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置
     * 
     * @param gmtModified 要设置的
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置
     * 
     * @param userId 要设置的
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取对应af_borrow_cash.id
     *
     * @return 对应af_borrow_cash.id
     */
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置对应af_borrow_cash.id
     * 
     * @param borrowId 要设置的对应af_borrow_cash.id
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取续期Id
     *
     * @return 续期Id
     */
    public Long getRenewalId(){
      return renewalId;
    }

    /**
     * 设置续期Id
     * 
     * @param renewalId 要设置的续期Id
     */
    public void setRenewalId(Long renewalId){
      this.renewalId = renewalId;
    }

    /**
     * 获取优惠券id
     *
     * @return 优惠券id
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券id
     * 
     * @param couponId 要设置的优惠券id
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }

}