package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 拆红包活动，用户总红包实体
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfRedPacketTotalDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 所属用户id
     */
    private Long userId;

    /**
     * 红包金额
     */
    private BigDecimal amount;

    /**
     * 是否提现，0表示未提现，1表示提现
     */
    private Integer isWithdraw;

    /**
     * 提现时间
     */
    private Date gmtWithdraw;


    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 添加者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;


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
     * @param rid
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取所属用户id
     *
     * @return 所属用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置所属用户id
     * 
     * @param userId 要设置的所属用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取红包金额
     *
     * @return 红包金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置红包金额
     * 
     * @param amount 要设置的红包金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取是否提现，0表示未提现，1表示提现
     *
     * @return 是否提现，0表示未提现，1表示提现
     */
    public Integer getIsWithdraw(){
      return isWithdraw;
    }

    /**
     * 设置是否提现，0表示未提现，1表示提现
     * 
     * @param isWithdraw 要设置的是否提现，0表示未提现，1表示提现
     */
    public void setIsWithdraw(Integer isWithdraw){
      this.isWithdraw = isWithdraw;
    }

    /**
     * 获取提现时间
     *
     * @return 提现时间
     */
    public Date getGmtWithdraw(){
      return gmtWithdraw;
    }

    /**
     * 设置提现时间
     * 
     * @param gmtWithdraw 要设置的提现时间
     */
    public void setGmtWithdraw(Date gmtWithdraw){
      this.gmtWithdraw = gmtWithdraw;
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
     * 获取添加者
     *
     * @return 添加者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置添加者
     * 
     * @param creator 要设置的添加者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取修改者
     *
     * @return 修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置修改者
     * 
     * @param modifier 要设置的修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

}