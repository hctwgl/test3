package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 借款附带优惠券表实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-14 16:31:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBorrowLegalCashCouponDo extends AbstractSerial {

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
     * 优惠券Id
     */
    private Long couponId;

    /**
     * 起始借款金额
     */
    private BigDecimal startProfitAmount;

    /**
     * 结束借款金额
     */
    private BigDecimal endProfitAmount;

    /**
     * 排序
     */
    private Integer sort;

    private Integer isDelete;

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

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
     * 获取优惠券Id
     *
     * @return 优惠券Id
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券Id
     * 
     * @param couponId 要设置的优惠券Id
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }

    /**
     * 获取起始借款金额
     *
     * @return 起始借款金额
     */
    public BigDecimal getStartProfitAmount(){
      return startProfitAmount;
    }

    /**
     * 设置起始借款金额
     * 
     * @param startProfitAmount 要设置的起始借款金额
     */
    public void setStartProfitAmount(BigDecimal startProfitAmount){
      this.startProfitAmount = startProfitAmount;
    }

    /**
     * 获取结束借款金额
     *
     * @return 结束借款金额
     */
    public BigDecimal getEndProfitAmount(){
      return endProfitAmount;
    }

    /**
     * 设置结束借款金额
     * 
     * @param endProfitAmount 要设置的结束借款金额
     */
    public void setEndProfitAmount(BigDecimal endProfitAmount){
      this.endProfitAmount = endProfitAmount;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序
     * 
     * @param sort 要设置的排序
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

}