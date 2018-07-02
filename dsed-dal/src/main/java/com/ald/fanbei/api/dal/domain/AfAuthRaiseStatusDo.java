package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 贷款业务实体
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-02-06 17:58:14
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAuthRaiseStatusDo extends AbstractSerial {

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
     * 产品类型，BORROW：小额贷，BLD_LOAD：白领贷
     */
    private String prdType;

    /**
     * 认证类型，例如CHSI表示学信网认证
     */
    private String authType;

    /**
     * 提额状态，N表示未提额，Y表示提额，F表示提额失败
     */
    private String raiseStatus;

    /**
     * 提额完成时间（提额失败成功都会更新该时间，未提额该字段为空）
     */
    private Date gmtFinish;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 认证后额度
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
     * 获取产品类型，BORROW：小额贷，BLD_LOAD：白领贷
     *
     * @return 产品类型，BORROW：小额贷，BLD_LOAD：白领贷
     */
    public String getPrdType(){
      return prdType;
    }

    /**
     * 设置产品类型，BORROW：小额贷，BLD_LOAD：白领贷
     * 
     * @param prdType 要设置的产品类型，BORROW：小额贷，BLD_LOAD：白领贷
     */
    public void setPrdType(String prdType){
      this.prdType = prdType;
    }

    /**
     * 获取认证类型，例如CHSI表示学信网认证
     *
     * @return 认证类型，例如CHSI表示学信网认证
     */
    public String getAuthType(){
      return authType;
    }

    /**
     * 设置认证类型，例如CHSI表示学信网认证
     * 
     * @param authType 要设置的认证类型，例如CHSI表示学信网认证
     */
    public void setAuthType(String authType){
      this.authType = authType;
    }

    /**
     * 获取提额状态，N表示未提额，Y表示提额，F表示提额失败
     *
     * @return 提额状态，N表示未提额，Y表示提额，F表示提额失败
     */
    public String getRaiseStatus(){
      return raiseStatus;
    }

    /**
     * 设置提额状态，N表示未提额，Y表示提额，F表示提额失败
     * 
     * @param raiseStatus 要设置的提额状态，N表示未提额，Y表示提额，F表示提额失败
     */
    public void setRaiseStatus(String raiseStatus){
      this.raiseStatus = raiseStatus;
    }

    /**
     * 获取提额完成时间（提额失败成功都会更新该时间，未提额该字段为空）
     *
     * @return 提额完成时间（提额失败成功都会更新该时间，未提额该字段为空）
     */
    public Date getGmtFinish(){
      return gmtFinish;
    }

    /**
     * 设置提额完成时间（提额失败成功都会更新该时间，未提额该字段为空）
     * 
     * @param gmtFinish 要设置的提额完成时间（提额失败成功都会更新该时间，未提额该字段为空）
     */
    public void setGmtFinish(Date gmtFinish){
      this.gmtFinish = gmtFinish;
    }

    /**
     * 获取用户编号
     *
     * @return 用户编号
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户编号
     * 
     * @param userId 要设置的用户编号
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取认证后额度
     *
     * @return 认证后额度
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置认证后额度
     * 
     * @param amount 要设置的认证后额度
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

}