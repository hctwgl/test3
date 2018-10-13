package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author yinxiangyu
 * @version 1.0.0 初始化
 * @date 2018-10-13 17:46:16
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdOfflineOverdueRemoveDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间时间
     */
    private String gmtCreate;

    /**
     * 线下还款时间
     */
    private String gmtRepay;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 减免逾期记录id，多个用","隔开
     */
    private String overdueLogId;

    /**
     * 减免逾期费
     */
    private BigDecimal removeOverdue;

    /**
     * 新逾期费
     */
    private BigDecimal newOverdue;

    /**
     * 还款后的待还金额
     */
    private BigDecimal currentAmount;


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
     * 获取创建时间时间
     *
     * @return 创建时间时间
     */
    public String getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间时间
     * 
     * @param gmtCreate 要设置的创建时间时间
     */
    public void setGmtCreate(String gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取线下还款时间
     *
     * @return 线下还款时间
     */
    public String getGmtRepay(){
      return gmtRepay;
    }

    /**
     * 设置线下还款时间
     * 
     * @param gmtRepay 要设置的线下还款时间
     */
    public void setGmtRepay(String gmtRepay){
      this.gmtRepay = gmtRepay;
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
     * 获取减免逾期记录id，多个用","隔开
     *
     * @return 减免逾期记录id，多个用","隔开
     */
    public String getOverdueLogId(){
      return overdueLogId;
    }

    /**
     * 设置减免逾期记录id，多个用","隔开
     * 
     * @param overdueLogId 要设置的减免逾期记录id，多个用","隔开
     */
    public void setOverdueLogId(String overdueLogId){
      this.overdueLogId = overdueLogId;
    }

    /**
     * 获取减免逾期费
     *
     * @return 减免逾期费
     */
    public BigDecimal getRemoveOverdue(){
      return removeOverdue;
    }

    /**
     * 设置减免逾期费
     * 
     * @param removeOverdue 要设置的减免逾期费
     */
    public void setRemoveOverdue(BigDecimal removeOverdue){
      this.removeOverdue = removeOverdue;
    }

    /**
     * 获取新逾期费
     *
     * @return 新逾期费
     */
    public BigDecimal getNewOverdue(){
      return newOverdue;
    }

    /**
     * 设置新逾期费
     * 
     * @param newOverdue 要设置的新逾期费
     */
    public void setNewOverdue(BigDecimal newOverdue){
      this.newOverdue = newOverdue;
    }

    /**
     * 获取还款后的待还金额
     *
     * @return 还款后的待还金额
     */
    public BigDecimal getCurrentAmount(){
      return currentAmount;
    }

    /**
     * 设置还款后的待还金额
     * 
     * @param currentAmount 要设置的还款后的待还金额
     */
    public void setCurrentAmount(BigDecimal currentAmount){
      this.currentAmount = currentAmount;
    }

}