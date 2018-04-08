package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 智能电核表实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-03-27 16:57:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfIagentResultDo extends AbstractSerial {

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
     * 更新时间
     */
    private Date gmtModified;


    /**
     * 电核订单号
     */
    private Long workId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 电核结果
     */
    private String workResult;

    /**
     * 录音url
     */
    private String audioUrl;
    private String orderNo;
    private String orderType;
    private String checkState;
    private String checkResult;

    public String getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getCheckState() {
        return checkState;
    }

    public void setCheckState(String checkState) {
        this.checkState = checkState;
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
     * 获取更新时间
     *
     * @return 更新时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置更新时间
     * 
     * @param gmtModified 要设置的更新时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }


    /**
     * 获取电核订单号
     *
     * @return 电核订单号
     */
    public Long getWorkId(){
      return workId;
    }

    /**
     * 设置电核订单号
     * 
     * @param workId 要设置的电核订单号
     */
    public void setWorkId(Long workId){
      this.workId = workId;
    }

    /**
     * 获取订单ID
     *
     * @return 订单ID
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置订单ID
     * 
     * @param orderId 要设置的订单ID
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
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
     * 获取电核结果
     *
     * @return 电核结果
     */
    public String getWorkResult(){
      return workResult;
    }

    /**
     * 设置电核结果
     * 
     * @param workResult 要设置的电核结果
     */
    public void setWorkResult(String workResult){
      this.workResult = workResult;
    }

    /**
     * 获取录音url
     *
     * @return 录音url
     */
    public String getAudioUrl(){
      return audioUrl;
    }

    /**
     * 设置录音url
     * 
     * @param audioUrl 要设置的录音url
     */
    public void setAudioUrl(String audioUrl){
      this.audioUrl = audioUrl;
    }

}