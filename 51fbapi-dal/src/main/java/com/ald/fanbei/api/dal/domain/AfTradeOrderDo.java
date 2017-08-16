package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * 商圈商户提现记录表实体
 *
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-19 09:30:13
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
     * 状态：NEW:新建 REFUNDING:退款中 REFUND:退款 EXTRACT:提现 EXTRACTING:提现中
     */
    private String status;


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
     * 获取状态：NEW:新建 REFUNDING:退款中 REFUND:退款 EXTRACT:提现 EXTRACTING:提现中
     *
     * @return 状态：NEW:新建 REFUNDING:退款中 REFUND:退款 EXTRACT:提现 EXTRACTING:提现中
     */
    public String getStatus(){
        return status;
    }

    /**
     * 设置状态：NEW:新建 REFUNDING:退款中 REFUND:退款 EXTRACT:提现 EXTRACTING:提现中
     *
     * @param status 要设置的状态：NEW:新建 REFUNDING:退款中 REFUND:退款 EXTRACT:提现 EXTRACTING:提现中
     */
    public void setStatus(String status){
        this.status = status;
    }

}