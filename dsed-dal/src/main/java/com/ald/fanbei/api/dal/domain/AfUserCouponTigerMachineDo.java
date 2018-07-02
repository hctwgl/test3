package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 老虎机用户领券次数实体
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-01-05 16:20:41
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserCouponTigerMachineDo extends AbstractSerial {

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
     * 每天领取次数0，1；每天初始化为1
     */
    private Integer dailyTime;

    /**
     * 购物领取次数，
     */
    private Integer shopingTime;

    /**
     * 用户id
     */
    private Long userId;


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
     * 获取每天领取次数0，1；每天初始化为1
     *
     * @return 每天领取次数0，1；每天初始化为1
     */
    public Integer getDailyTime(){
      return dailyTime;
    }

    /**
     * 设置每天领取次数0，1；每天初始化为1
     * 
     * @param dailyTime 要设置的每天领取次数0，1；每天初始化为1
     */
    public void setDailyTime(Integer dailyTime){
      this.dailyTime = dailyTime;
    }

    /**
     * 获取购物领取次数，
     *
     * @return 购物领取次数，
     */
    public Integer getShopingTime(){
      return shopingTime;
    }

    /**
     * 设置购物领取次数，
     * 
     * @param shopingTime 要设置的购物领取次数，
     */
    public void setShopingTime(Integer shopingTime){
      this.shopingTime = shopingTime;
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

}