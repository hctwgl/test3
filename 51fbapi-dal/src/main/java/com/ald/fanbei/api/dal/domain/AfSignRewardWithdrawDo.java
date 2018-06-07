package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 分类运营位配置实体
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfSignRewardWithdrawDo extends AbstractSerial {

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
     * 提现条件，0:10元；1：30元；2：50元；3：100元
     */
    private Integer withdrawType;

    /**
     * 提现金额
     */
    private BigDecimal withdrawAmount;

    /**
     * 用户ID
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
     * @param
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
     * 获取提现条件，0:10元；1：30元；2：50元；3：100元
     *
     * @return 提现条件，0:10元；1：30元；2：50元；3：100元
     */
    public Integer getWithdrawType(){
      return withdrawType;
    }

    /**
     * 设置提现条件，0:10元；1：30元；2：50元；3：100元
     * 
     * @param withdrawType 要设置的提现条件，0:10元；1：30元；2：50元；3：100元
     */
    public void setWithdrawType(Integer withdrawType){
      this.withdrawType = withdrawType;
    }

    /**
     * 获取提现金额
     *
     * @return 提现金额
     */
    public BigDecimal getWithdrawAmount(){
      return withdrawAmount;
    }

    /**
     * 设置提现金额
     * 
     * @param withdrawAmount 要设置的提现金额
     */
    public void setWithdrawAmount(BigDecimal withdrawAmount){
      this.withdrawAmount = withdrawAmount;
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



}