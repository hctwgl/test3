package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 持续完成浏览商品数量的天数实体
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-16 21:12:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfTaskBrowseGoodsDaysDo extends AbstractSerial {

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
     * 上次任务完成时间
     */
    private Date lastCompleteTaskDate;

    /**
     * 已经持续天数
     */
    private Integer continueDays;


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
     * 获取上次任务完成时间
     *
     * @return 上次任务完成时间
     */
    public Date getLastCompleteTaskDate(){
      return lastCompleteTaskDate;
    }

    /**
     * 设置上次任务完成时间
     * 
     * @param lastCompleteTaskDate 要设置的上次任务完成时间
     */
    public void setLastCompleteTaskDate(Date lastCompleteTaskDate){
      this.lastCompleteTaskDate = lastCompleteTaskDate;
    }

    /**
     * 获取已经持续天数
     *
     * @return 已经持续天数
     */
    public Integer getContinueDays(){
      return continueDays;
    }

    /**
     * 设置已经持续天数
     * 
     * @param continueDays 要设置的已经持续天数
     */
    public void setContinueDays(Integer continueDays){
      this.continueDays = continueDays;
    }

}