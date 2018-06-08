package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 金币兑换余额比例实体
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-21 17:54:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfTaskCoinChangeProportionDo extends AbstractSerial {

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
     * 兑换日期
     */
    private Date changeDate;

    /**
     * 兑换比例
     */
    private BigDecimal changeProportion;


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
     * 获取兑换日期
     *
     * @return 兑换日期
     */
    public Date getChangeDate(){
      return changeDate;
    }

    /**
     * 设置兑换日期
     * 
     * @param changeDate 要设置的兑换日期
     */
    public void setChangeDate(Date changeDate){
      this.changeDate = changeDate;
    }

    /**
     * 获取兑换比例
     *
     * @return 兑换比例
     */
    public BigDecimal getChangeProportion(){
      return changeProportion;
    }

    /**
     * 设置兑换比例
     * 
     * @param changeProportion 要设置的兑换比例
     */
    public void setChangeProportion(BigDecimal changeProportion){
      this.changeProportion = changeProportion;
    }

}