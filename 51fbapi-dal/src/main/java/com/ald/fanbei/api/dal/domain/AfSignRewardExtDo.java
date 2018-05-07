package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 分类运营位配置实体
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfSignRewardExtDo extends AbstractSerial {

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
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 首次参与时间
     */
    private Date firstDayParticipation;

    /**
     * 周期天数
     */
    private Integer cycleDays;

    /**
     * 签到提醒：0：未开启提醒；1：开启提醒
     */
    private Integer isOpenRemind;


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
     * 获取修改时间
     *
     * @return 修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置修改时间
     * 
     * @param gmtModified 要设置的修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户ID
     * 
     * @param userId 要设置的用户ID
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取首次参与时间
     *
     * @return 首次参与时间
     */
    public Date getFirstDayParticipation(){
      return firstDayParticipation;
    }

    /**
     * 设置首次参与时间
     * 
     * @param firstDayParticipation 要设置的首次参与时间
     */
    public void setFirstDayParticipation(Date firstDayParticipation){
      this.firstDayParticipation = firstDayParticipation;
    }

    /**
     * 获取周期天数
     *
     * @return 周期天数
     */
    public Integer getCycleDays(){
      return cycleDays;
    }

    /**
     * 设置周期天数
     * 
     * @param cycleDays 要设置的周期天数
     */
    public void setCycleDays(Integer cycleDays){
      this.cycleDays = cycleDays;
    }

    /**
     * 获取签到提醒：0：未开启提醒；1：开启提醒
     *
     * @return 签到提醒：0：未开启提醒；1：开启提醒
     */
    public Integer getIsOpenRemind(){
      return isOpenRemind;
    }

    /**
     * 设置签到提醒：0：未开启提醒；1：开启提醒
     * 
     * @param isOpenRemind 要设置的签到提醒：0：未开启提醒；1：开启提醒
     */
    public void setIsOpenRemind(Integer isOpenRemind){
      this.isOpenRemind = isOpenRemind;
    }

}