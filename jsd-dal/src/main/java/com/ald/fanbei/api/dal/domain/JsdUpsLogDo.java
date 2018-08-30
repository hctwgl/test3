package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-30 10:47:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdUpsLogDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userId;

    /**
     * 银行卡号
     */
    private String bankCardNumber;

    /**
     * 银行编号
     */
    private String bankCode;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 业务类型
     */
    private String type;

    /**
     * 关联id
     */
    private String refId;

    /**
     * 打款状态【NEW:新建 SUCCESS:成功 FAIL:失败】
     */
    private String status;

    /**
     * 金额
     */
    private BigDecimal amount;


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

    /**
     * 获取银行卡号
     *
     * @return 银行卡号
     */
    public String getBankCardNumber(){
      return bankCardNumber;
    }

    /**
     * 设置银行卡号
     * 
     * @param bankCardNumber 要设置的银行卡号
     */
    public void setBankCardNumber(String bankCardNumber){
      this.bankCardNumber = bankCardNumber;
    }

    /**
     * 获取银行编号
     *
     * @return 银行编号
     */
    public String getBankCode(){
      return bankCode;
    }

    /**
     * 设置银行编号
     * 
     * @param bankCode 要设置的银行编号
     */
    public void setBankCode(String bankCode){
      this.bankCode = bankCode;
    }

    /**
     * 获取接口名称
     *
     * @return 接口名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置接口名称
     * 
     * @param name 要设置的接口名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取订单编号
     *
     * @return 订单编号
     */
    public String getOrderNo(){
      return orderNo;
    }

    /**
     * 设置订单编号
     * 
     * @param orderNo 要设置的订单编号
     */
    public void setOrderNo(String orderNo){
      this.orderNo = orderNo;
    }

    /**
     * 获取业务类型
     *
     * @return 业务类型
     */
    public String getType(){
      return type;
    }

    /**
     * 设置业务类型
     * 
     * @param type 要设置的业务类型
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取关联id
     *
     * @return 关联id
     */
    public String getRefId(){
      return refId;
    }

    /**
     * 设置关联id
     * 
     * @param refId 要设置的关联id
     */
    public void setRefId(String refId){
      this.refId = refId;
    }

    /**
     * 获取打款状态【NEW:新建 SUCCESS:成功 FAIL:失败】
     *
     * @return 打款状态【NEW:新建 SUCCESS:成功 FAIL:失败】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置打款状态【NEW:新建 SUCCESS:成功 FAIL:失败】
     * 
     * @param status 要设置的打款状态【NEW:新建 SUCCESS:成功 FAIL:失败】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取金额
     *
     * @return 金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置金额
     * 
     * @param amount 要设置的金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

}