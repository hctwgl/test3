package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * ydm交易记录实体
 * 
 * @author cxk
 * @version 1.0.0 初始化
 * @date 2018-02-27 17:22:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfRecycleTradeDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 
     */
    private Date gmtCreate;

    /**
     * 
     */
    private Date gmtModified;

    /**
     * 消费类型 1：支出 2：收入
     */
    private Integer type;

    /**
     * 当前操作后，账户剩余的总金额
     */
    private BigDecimal remainAmount;

    /**
     * 交易金额(订单金额+返现金额)
     */
    private BigDecimal tradeAmount;

    /**
     * 返现金额
     */
    private BigDecimal returnAmount;

    /**
     * 返现时对应的返现比例
     */
    private BigDecimal ratio;

    /**
     * 交易对应外部的id，af_order_recycle的id，充值则为-1
     */
    private Integer refId;

    /**
     * 创建者
     */
    private String gmtCreator;

    /**
     * 修改者
     */
    private String gmtModifier;


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

    /**
     * 获取消费类型 1：支出 2：收入
     *
     * @return 消费类型 1：支出 2：收入
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置消费类型 1：支出 2：收入
     * 
     * @param type 要设置的消费类型 1：支出 2：收入
     */
    public void setType(Integer type){
      this.type = type;
    }

    /**
     * 获取当前操作后，账户剩余的总金额
     *
     * @return 当前操作后，账户剩余的总金额
     */
    public BigDecimal getRemainAmount(){
      return remainAmount;
    }

    /**
     * 设置当前操作后，账户剩余的总金额
     * 
     * @param remainAmount 要设置的当前操作后，账户剩余的总金额
     */
    public void setRemainAmount(BigDecimal remainAmount){
      this.remainAmount = remainAmount;
    }

    /**
     * 获取交易金额(订单金额+返现金额)
     *
     * @return 交易金额(订单金额+返现金额)
     */
    public BigDecimal getTradeAmount(){
      return tradeAmount;
    }

    /**
     * 设置交易金额(订单金额+返现金额)
     * 
     * @param tradeAmount 要设置的交易金额(订单金额+返现金额)
     */
    public void setTradeAmount(BigDecimal tradeAmount){
      this.tradeAmount = tradeAmount;
    }

    /**
     * 获取返现金额
     *
     * @return 返现金额
     */
    public BigDecimal getReturnAmount(){
      return returnAmount;
    }

    /**
     * 设置返现金额
     * 
     * @param returnAmount 要设置的返现金额
     */
    public void setReturnAmount(BigDecimal returnAmount){
      this.returnAmount = returnAmount;
    }

    /**
     * 获取返现时对应的返现比例
     *
     * @return 返现时对应的返现比例
     */
    public BigDecimal getRatio(){
      return ratio;
    }

    /**
     * 设置返现时对应的返现比例
     * 
     * @param ratio 要设置的返现时对应的返现比例
     */
    public void setRatio(BigDecimal ratio){
      this.ratio = ratio;
    }

    /**
     * 获取交易对应外部的id，af_order_recycle的id，充值则为-1
     *
     * @return 交易对应外部的id，af_order_recycle的id，充值则为-1
     */
    public Integer getRefId(){
      return refId;
    }

    /**
     * 设置交易对应外部的id，af_order_recycle的id，充值则为-1
     * 
     * @param refId 要设置的交易对应外部的id，af_order_recycle的id，充值则为-1
     */
    public void setRefId(Integer refId){
      this.refId = refId;
    }

    /**
     * 获取创建者
     *
     * @return 创建者
     */
    public String getGmtCreator(){
      return gmtCreator;
    }

    /**
     * 设置创建者
     * 
     * @param gmtCreator 要设置的创建者
     */
    public void setGmtCreator(String gmtCreator){
      this.gmtCreator = gmtCreator;
    }

    /**
     * 获取修改者
     *
     * @return 修改者
     */
    public String getGmtModifier(){
      return gmtModifier;
    }

    /**
     * 设置修改者
     * 
     * @param gmtModifier 要设置的修改者
     */
    public void setGmtModifier(String gmtModifier){
      this.gmtModifier = gmtModifier;
    }

}