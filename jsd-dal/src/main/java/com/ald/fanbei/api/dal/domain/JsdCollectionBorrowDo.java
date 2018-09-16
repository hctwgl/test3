package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdCollectionBorrowDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 
     */
    private Long borrowId;

    /**
     * NOTICED-已入催，WAIT_FINISH - 待审核平账，MANUAL_FINISHED -已强制平账，NORMAL_FINISHED-全额结清，RENEWALED-已续期
     */
    private String status;

    /**
     * 发起结清的请求者
     */
    private String requester;

    /**
     * 请求平账的原因，递增修改
     */
    private String requestReason;

    /**
     * 审批者
     */
    private String reviewer;

    /**
     * 审批备注
     */
    private String reviewRemark;

    /**
     * 审批状态
     */
    private String reviewStatus;

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;


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
    public Long getBorrowId(){
      return borrowId;
    }

    /**
     * 设置
     * 
     * @param borrowId 要设置的
     */
    public void setBorrowId(Long borrowId){
      this.borrowId = borrowId;
    }

    /**
     * 获取NOTICED-已入催，WAIT_FINISH - 待审核平账，MANUAL_FINISHED -已强制平账，NORMAL_FINISHED-全额结清，RENEWALED-已续期
     *
     * @return NOTICED-已入催，WAIT_FINISH - 待审核平账，MANUAL_FINISHED -已强制平账，NORMAL_FINISHED-全额结清，RENEWALED-已续期
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置NOTICED-已入催，WAIT_FINISH - 待审核平账，MANUAL_FINISHED -已强制平账，NORMAL_FINISHED-全额结清，RENEWALED-已续期
     * 
     * @param status 要设置的NOTICED-已入催，WAIT_FINISH - 待审核平账，MANUAL_FINISHED -已强制平账，NORMAL_FINISHED-全额结清，RENEWALED-已续期
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取发起结清的请求者
     *
     * @return 发起结清的请求者
     */
    public String getRequester(){
      return requester;
    }

    /**
     * 设置发起结清的请求者
     * 
     * @param requester 要设置的发起结清的请求者
     */
    public void setRequester(String requester){
      this.requester = requester;
    }

    /**
     * 获取请求平账的原因，递增修改
     *
     * @return 请求平账的原因，递增修改
     */
    public String getRequestReason(){
      return requestReason;
    }

    /**
     * 设置请求平账的原因，递增修改
     * 
     * @param requestReason 要设置的请求平账的原因，递增修改
     */
    public void setRequestReason(String requestReason){
      this.requestReason = requestReason;
    }

    /**
     * 获取审批者
     *
     * @return 审批者
     */
    public String getReviewer(){
      return reviewer;
    }

    /**
     * 设置审批者
     * 
     * @param reviewer 要设置的审批者
     */
    public void setReviewer(String reviewer){
      this.reviewer = reviewer;
    }

    /**
     * 获取审批备注
     *
     * @return 审批备注
     */
    public String getReviewRemark(){
      return reviewRemark;
    }

    /**
     * 设置审批备注
     * 
     * @param reviewRemark 要设置的审批备注
     */
    public void setReviewRemark(String reviewRemark){
      this.reviewRemark = reviewRemark;
    }

    /**
     * 获取审批状态
     *
     * @return 审批状态
     */
    public String getReviewStatus(){
      return reviewStatus;
    }

    /**
     * 设置审批状态
     * 
     * @param reviewStatus 要设置的审批状态
     */
    public void setReviewStatus(String reviewStatus){
      this.reviewStatus = reviewStatus;
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

}