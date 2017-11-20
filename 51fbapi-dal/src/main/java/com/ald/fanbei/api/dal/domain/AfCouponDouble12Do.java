package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 双十二实体
 * 
 * @author yanghailong_temple
 * @version 1.0.0 初始化
 * @date 2017-11-17 11:28:44
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfCouponDouble12Do extends AbstractSerial {

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
     * 优惠券实时可领数量（每领取减一）
     */
    private Long count;

    /**
     * 优惠券Id
     */
    private Long couponid;

    /**
     * 抢券开始时间
     */
    private Date starttime;

    /**
     * 抢券结束时间
     */
    private Date endtime;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 优惠券数量（每天有额定数量）
     */
    private Long total;

    public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
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
     * 获取优惠券实时可领数量（每领取减一）
     *
     * @return 优惠券实时可领数量（每领取减一）
     */
    public Long getCount(){
      return count;
    }

    /**
     * 设置优惠券实时可领数量（每领取减一）
     * 
     * @param count 要设置的优惠券实时可领数量（每领取减一）
     */
    public void setCount(Long count){
      this.count = count;
    }

    /**
     * 获取优惠券Id
     *
     * @return 优惠券Id
     */
    public Long getCouponid(){
      return couponid;
    }

    /**
     * 设置优惠券Id
     * 
     * @param couponid 要设置的优惠券Id
     */
    public void setCouponid(Long couponid){
      this.couponid = couponid;
    }

    /**
     * 获取抢券开始时间
     *
     * @return 抢券开始时间
     */
    public Date getStarttime(){
      return starttime;
    }

    /**
     * 设置抢券开始时间
     * 
     * @param starttime 要设置的抢券开始时间
     */
    public void setStarttime(Date starttime){
      this.starttime = starttime;
    }

    /**
     * 获取抢券结束时间
     *
     * @return 抢券结束时间
     */
    public Date getEndtime(){
      return endtime;
    }

    /**
     * 设置抢券结束时间
     * 
     * @param endtime 要设置的抢券结束时间
     */
    public void setEndtime(Date endtime){
      this.endtime = endtime;
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