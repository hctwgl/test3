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
 public class JsdCollectionRepaymentDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 借款者uid
     */
    private Long userId;

    /**
     * 流水号
     */
    private String tradeNo;

    /**
     * 借款者的还款账户，一般为手机号
     */
    private String account;

    /**
     * 借款者真实姓名
     */
    private String realName;

    /**
     * 还款方式，直接记录中文还款名称

     */
    private String repayWay;

    /**
     * 
     */
    private Long borrowId;

    /**
     * 还款凭证，图片存取base64编码字符串
     */
    private String repayCert;

    /**
     * 还款金额
     */
    private BigDecimal repayAmount;

    /**
     * 请求还款的催人人员
     */
    private String requester;

    /**
     * 审批者
     */
    private String reviewer;

    /**
     * 
     */
    private String reviewStatus;

    /**
     * 
     */
    private String reviewRemark;

    /**
     * 审批成功后对应还款表id
     */
    private Long repaymentId;

    /**
     * 
     */
    private Date gmtRepay;

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
     * 获取借款者uid
     *
     * @return 借款者uid
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置借款者uid
     * 
     * @param userId 要设置的借款者uid
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取流水号
     *
     * @return 流水号
     */
    public String getTradeNo(){
      return tradeNo;
    }

    /**
     * 设置流水号
     * 
     * @param tradeNo 要设置的流水号
     */
    public void setTradeNo(String tradeNo){
      this.tradeNo = tradeNo;
    }

    /**
     * 获取借款者的还款账户，一般为手机号
     *
     * @return 借款者的还款账户，一般为手机号
     */
    public String getAccount(){
      return account;
    }

    /**
     * 设置借款者的还款账户，一般为手机号
     * 
     * @param account 要设置的借款者的还款账户，一般为手机号
     */
    public void setAccount(String account){
      this.account = account;
    }

    /**
     * 获取借款者真实姓名
     *
     * @return 借款者真实姓名
     */
    public String getRealName(){
      return realName;
    }

    /**
     * 设置借款者真实姓名
     * 
     * @param realName 要设置的借款者真实姓名
     */
    public void setRealName(String realName){
      this.realName = realName;
    }

    /**
     * 获取还款方式，直接记录中文还款名称

     *
     * @return 还款方式，直接记录中文还款名称

     */
    public String getRepayWay(){
      return repayWay;
    }

    /**
     * 设置还款方式，直接记录中文还款名称

     * 
     * @param repayWay 要设置的还款方式，直接记录中文还款名称

     */
    public void setRepayWay(String repayWay){
      this.repayWay = repayWay;
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
     * 获取还款凭证，图片存取base64编码字符串
     *
     * @return 还款凭证，图片存取base64编码字符串
     */
    public String getRepayCert(){
      return repayCert;
    }

    /**
     * 设置还款凭证，图片存取base64编码字符串
     * 
     * @param repayCert 要设置的还款凭证，图片存取base64编码字符串
     */
    public void setRepayCert(String repayCert){
      this.repayCert = repayCert;
    }

    /**
     * 获取还款金额
     *
     * @return 还款金额
     */
    public BigDecimal getRepayAmount(){
      return repayAmount;
    }

    /**
     * 设置还款金额
     * 
     * @param repayAmount 要设置的还款金额
     */
    public void setRepayAmount(BigDecimal repayAmount){
      this.repayAmount = repayAmount;
    }

    /**
     * 获取请求还款的催人人员
     *
     * @return 请求还款的催人人员
     */
    public String getRequester(){
      return requester;
    }

    /**
     * 设置请求还款的催人人员
     * 
     * @param requester 要设置的请求还款的催人人员
     */
    public void setRequester(String requester){
      this.requester = requester;
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
     * 获取
     *
     * @return 
     */
    public String getReviewStatus(){
      return reviewStatus;
    }

    /**
     * 设置
     * 
     * @param reviewStatus 要设置的
     */
    public void setReviewStatus(String reviewStatus){
      this.reviewStatus = reviewStatus;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getReviewRemark(){
      return reviewRemark;
    }

    /**
     * 设置
     * 
     * @param reviewRemark 要设置的
     */
    public void setReviewRemark(String reviewRemark){
      this.reviewRemark = reviewRemark;
    }

    /**
     * 获取审批成功后对应还款表id
     *
     * @return 审批成功后对应还款表id
     */
    public Long getRepaymentId(){
      return repaymentId;
    }

    /**
     * 设置审批成功后对应还款表id
     * 
     * @param repaymentId 要设置的审批成功后对应还款表id
     */
    public void setRepaymentId(Long repaymentId){
      this.repaymentId = repaymentId;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtRepay(){
      return gmtRepay;
    }

    /**
     * 设置
     * 
     * @param gmtRepay 要设置的
     */
    public void setGmtRepay(Date gmtRepay){
      this.gmtRepay = gmtRepay;
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