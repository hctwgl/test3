package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 额度拆分多场景分期额度记录实体
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:57:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserAccountSenceDo extends AbstractSerial {

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
     * ONLINE 线上分期，TRAIN线下培训
     */
    private String scene;

    /**
     * 用户id
            
     */
    private Long userId;

    /**
     * 未通过原因，json格式，表示哪些认证信息需要重新提交。
     */
    private BigDecimal auAmount;

    /**
     * 
     */
    private BigDecimal usedAmount;

    /**
     * 
     */
    private BigDecimal freezeAmount;


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
     * 获取ONLINE 线上分期，TRAIN线下培训
     *
     * @return ONLINE 线上分期，TRAIN线下培训
     */
    public String getScene(){
      return scene;
    }

    /**
     * 设置ONLINE 线上分期，TRAIN线下培训
     * 
     * @param scene 要设置的ONLINE 线上分期，TRAIN线下培训
     */
    public void setScene(String scene){
      this.scene = scene;
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
     * 获取未通过原因，json格式，表示哪些认证信息需要重新提交。
     *
     * @return 未通过原因，json格式，表示哪些认证信息需要重新提交。
     */
    public BigDecimal getAuAmount(){
      return auAmount;
    }

    /**
     * 设置未通过原因，json格式，表示哪些认证信息需要重新提交。
     * 
     * @param auAmount 要设置的未通过原因，json格式，表示哪些认证信息需要重新提交。
     */
    public void setAuAmount(BigDecimal auAmount){
      this.auAmount = auAmount;
    }

    /**
     * 获取
     *
     * @return 
     */
    public BigDecimal getUsedAmount(){
      return usedAmount;
    }

    /**
     * 设置
     * 
     * @param usedAmount 要设置的
     */
    public void setUsedAmount(BigDecimal usedAmount){
      this.usedAmount = usedAmount;
    }

    /**
     * 获取
     *
     * @return 
     */
    public BigDecimal getFreezeAmount(){
      return freezeAmount;
    }

    /**
     * 设置
     * 
     * @param freezeAmount 要设置的
     */
    public void setFreezeAmount(BigDecimal freezeAmount){
      this.freezeAmount = freezeAmount;
    }

}