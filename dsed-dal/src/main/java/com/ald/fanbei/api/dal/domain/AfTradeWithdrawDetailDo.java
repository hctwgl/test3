package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 商圈商户提现记录表实体
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-19 09:29:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfTradeWithdrawDetailDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    
    /**
     * 提现表主键
     */
    private Long recordId;

    /**
     * 提现订单主键
     */
    private Long orderId;


    /**
     * 获取主键Id
     *
     * @return id
     */
    public Long getId(){
      return id;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setId(Long id){
      this.id = id;
    }
    
    /**
     * 获取提现表主键
     *
     * @return 提现表主键
     */
    public Long getRecordId(){
      return recordId;
    }

    /**
     * 设置提现表主键
     * 
     * @param recordId 要设置的提现表主键
     */
    public void setRecordId(Long recordId){
      this.recordId = recordId;
    }

    /**
     * 获取提现订单主键
     *
     * @return 提现订单主键
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置提现订单主键
     * 
     * @param orderId 要设置的提现订单主键
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
    }

}