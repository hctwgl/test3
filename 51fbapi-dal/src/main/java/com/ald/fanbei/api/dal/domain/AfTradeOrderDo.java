package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * 商圈订单扩展表实体
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfTradeOrderDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 
     */
    private Date gmtModified;


    /**
     * 商户id
     */
    private Long businessId;

    /**
     * 退款类型：U:商户退款，P:系统退款
     */
    private String refundType;

    /**
     * 退款状态：NEW:新建 REFUNDING:退款中 FAIL:退款失败 FINISH:退款完成
     */
    private String refundStatus;

    /**
     * 
     */
    private Double refundAmount;


    /**
     * 获取订单ID
     *
     * @return 订单ID
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置订单ID
     * 
     * @param orderId 要设置的订单ID
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
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
     * 获取商户id
     *
     * @return 商户id
     */
    public Long getBusinessId(){
      return businessId;
    }

    /**
     * 设置商户id
     * 
     * @param businessId 要设置的商户id
     */
    public void setBusinessId(Long businessId){
      this.businessId = businessId;
    }

    /**
     * 获取退款类型：U:商户退款，P:系统退款
     *
     * @return 退款类型：U:商户退款，P:系统退款
     */
    public String getRefundType(){
      return refundType;
    }

    /**
     * 设置退款类型：U:商户退款，P:系统退款
     * 
     * @param refundType 要设置的退款类型：U:商户退款，P:系统退款
     */
    public void setRefundType(String refundType){
      this.refundType = refundType;
    }

    /**
     * 获取退款状态：NEW:新建 REFUNDING:退款中 FAIL:退款失败 FINISH:退款完成
     *
     * @return 退款状态：NEW:新建 REFUNDING:退款中 FAIL:退款失败 FINISH:退款完成
     */
    public String getRefundStatus(){
      return refundStatus;
    }

    /**
     * 设置退款状态：NEW:新建 REFUNDING:退款中 FAIL:退款失败 FINISH:退款完成
     * 
     * @param refundStatus 要设置的退款状态：NEW:新建 REFUNDING:退款中 FAIL:退款失败 FINISH:退款完成
     */
    public void setRefundStatus(String refundStatus){
      this.refundStatus = refundStatus;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Double getRefundAmount(){
      return refundAmount;
    }

    /**
     * 设置
     * 
     * @param refundAmount 要设置的
     */
    public void setRefundAmount(Double refundAmount){
      this.refundAmount = refundAmount;
    }

}