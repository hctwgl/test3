package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 仲裁实体
 * 
 * @author fanmanfu
 * @version 1.0.0 初始化
 * @date 2018-04-19 17:01:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfArbitrationDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    /**
     * 批次号
     */
    private String batchNo;
    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 案件订单编号
     */
    private String loanBillNo;

    /**
     * 进程编码
     */
    private String processCode;

    /**
     * 案件进程
     */
    private String process;

    /**
     * 状态编码
     */
    private String statusCode;

    /**
     * 状态
     */
    private String status;

    /**
     * 处理结果
     */
    private String message;

    /**
     * 
     */
    private String payVoucher;

    /**
     * 扩展值1
     */
    private String value1;

    /**
     * 扩展值2
     */
    private String value2;

    /**
     * 扩展值2
     */
    private String value3;

    /**
     * 扩展值2
     */
    private String value4;

    /**
     * 扩展值2
     */
    private String value5;

    /**
     * 扩展值2
     */
    private String value6;

    /**
     * 扩展值2
     */
    private String value7;
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
     * 获取案件订单编号
     *
     * @return 案件订单编号
     */
    public String getLoanBillNo(){
      return loanBillNo;
    }

    /**
     * 设置案件订单编号
     * 
     * @param loanBillNo 要设置的案件订单编号
     */
    public void setLoanBillNo(String loanBillNo){
      this.loanBillNo = loanBillNo;
    }

    /**
     * 获取进程编码
     *
     * @return 进程编码
     */
    public String getProcessCode(){
      return processCode;
    }

    /**
     * 设置进程编码
     * 
     * @param processCode 要设置的进程编码
     */
    public void setProcessCode(String processCode){
      this.processCode = processCode;
    }

    /**
     * 获取案件进程
     *
     * @return 案件进程
     */
    public String getProcess(){
      return process;
    }

    /**
     * 设置案件进程
     * 
     * @param process 要设置的案件进程
     */
    public void setProcess(String process){
      this.process = process;
    }

    /**
     * 获取状态编码
     *
     * @return 状态编码
     */
    public String getStatusCode(){
      return statusCode;
    }

    /**
     * 设置状态编码
     * 
     * @param statusCode 要设置的状态编码
     */
    public void setStatusCode(String statusCode){
      this.statusCode = statusCode;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态
     * 
     * @param status 要设置的状态
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取处理结果
     *
     * @return 处理结果
     */
    public String getMessage(){
      return message;
    }

    /**
     * 设置处理结果
     * 
     * @param message 要设置的处理结果
     */
    public void setMessage(String message){
      this.message = message;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getPayVoucher(){
      return payVoucher;
    }

    /**
     * 设置
     * 
     * @param payVoucher 要设置的
     */
    public void setPayVoucher(String payVoucher){
      this.payVoucher = payVoucher;
    }

    /**
     * 获取扩展值1
     *
     * @return 扩展值1
     */
    public String getValue1(){
      return value1;
    }

    /**
     * 设置扩展值1
     * 
     * @param value1 要设置的扩展值1
     */
    public void setValue1(String value1){
      this.value1 = value1;
    }

    /**
     * 获取扩展值2
     *
     * @return 扩展值2
     */
    public String getValue2(){
      return value2;
    }

    /**
     * 设置扩展值2
     * 
     * @param value2 要设置的扩展值2
     */
    public void setValue2(String value2){
      this.value2 = value2;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getValue5() {
        return value5;
    }

    public void setValue5(String value5) {
        this.value5 = value5;
    }

    public String getValue6() {
        return value6;
    }

    public void setValue6(String value6) {
        this.value6 = value6;
    }

    public String getValue7() {
        return value7;
    }

    public void setValue7(String value7) {
        this.value7 = value7;
    }
}