package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeActivityCouponDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
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
     * 创建者
     */
    private String creator;

    /**
     * 最后修改者
     */
    private String modifier;

    /**
     * 菠萝觅活动id
     */
    private Long boluomeActivityId;

    /**
     * 优惠券id,终极大奖券
     */
    private Long couponId;

    /**
     * 菠萝觅优惠券id
     */
    private Long resourceId;


    /**
     * 获取主键Id
     *
     * @return id
     */


    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
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
     * 获取创建者
     *
     * @return 创建者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建者
     * 
     * @param creator 要设置的创建者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取最后修改者
     *
     * @return 最后修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置最后修改者
     * 
     * @param modifier 要设置的最后修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取菠萝觅活动id
     *
     * @return 菠萝觅活动id
     */
    public Long getBoluomeActivityId(){
      return boluomeActivityId;
    }

    /**
     * 设置菠萝觅活动id
     * 
     * @param boluomeActivityId 要设置的菠萝觅活动id
     */
    public void setBoluomeActivityId(Long boluomeActivityId){
      this.boluomeActivityId = boluomeActivityId;
    }

    /**
     * 获取优惠券id,终极大奖券
     *
     * @return 优惠券id,终极大奖券
     */
    public Long getCouponId(){
      return couponId;
    }

    /**
     * 设置优惠券id,终极大奖券
     * 
     * @param couponId 要设置的优惠券id,终极大奖券
     */
    public void setCouponId(Long couponId){
      this.couponId = couponId;
    }

    /**
     * 获取菠萝觅优惠券id
     *
     * @return 菠萝觅优惠券id
     */
    public Long getResourceId(){
      return resourceId;
    }

    /**
     * 设置菠萝觅优惠券id
     * 
     * @param resourceId 要设置的菠萝觅优惠券id
     */
    public void setResourceId(Long resourceId){
      this.resourceId = resourceId;
    }

}