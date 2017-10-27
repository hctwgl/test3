package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * '第三方-上树请求记录实体
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2017-08-21 09:28:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfOrderLogisticsDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 订单号
     */
    private Long orderId;


    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 物流公司编号
     */
    private String shipperCode;

    /**
     * 物流公司名称
     */
    private String shipperName;

    /**
     * 物流单据号
     */
    private String logisticCode;

    /**
     * 物流明细
     */
    private String traces;

    /**
     * 物流状态(物流状态: 0-无轨迹，1-已揽收，2-在途中 201-到达派件城市，3-签收,4-问题件)
     */
    private Integer state;


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
     * 获取订单号
     *
     * @return 订单号
     */
    public Long getOrderId(){
      return orderId;
    }

    /**
     * 设置订单号
     * 
     * @param orderId 要设置的订单号
     */
    public void setOrderId(Long orderId){
      this.orderId = orderId;
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
     * 获取物流公司编号
     *
     * @return 物流公司编号
     */
    public String getShipperCode(){
      return shipperCode;
    }

    /**
     * 设置物流公司编号
     * 
     * @param shipperCode 要设置的物流公司编号
     */
    public void setShipperCode(String shipperCode){
      this.shipperCode = shipperCode;
    }

    /**
     * 获取物流公司名称
     *
     * @return 物流公司名称
     */
    public String getShipperName(){
      return shipperName;
    }

    /**
     * 设置物流公司名称
     * 
     * @param shipperName 要设置的物流公司名称
     */
    public void setShipperName(String shipperName){
      this.shipperName = shipperName;
    }

    /**
     * 获取物流单据号
     *
     * @return 物流单据号
     */
    public String getLogisticCode(){
      return logisticCode;
    }

    /**
     * 设置物流单据号
     * 
     * @param logisticCode 要设置的物流单据号
     */
    public void setLogisticCode(String logisticCode){
      this.logisticCode = logisticCode;
    }

    /**
     * 获取物流明细
     *
     * @return 物流明细
     */
    public String getTraces(){
      return traces;
    }

    /**
     * 设置物流明细
     * 
     * @param traces 要设置的物流明细
     */
    public void setTraces(String traces){
      this.traces = traces;
    }

    /**
     * 获取物流状态(物流状态: 0-无轨迹，1-已揽收，2-在途中 201-到达派件城市，3-签收,4-问题件)
     *
     * @return 物流状态(物流状态: 0-无轨迹，1-已揽收，2-在途中 201-到达派件城市，3-签收,4-问题件)
     */
    public Integer getState(){
      return state;
    }

    /**
     * 设置物流状态(物流状态: 0-无轨迹，1-已揽收，2-在途中 201-到达派件城市，3-签收,4-问题件)
     * 
     * @param state 要设置的物流状态(物流状态: 0-无轨迹，1-已揽收，2-在途中 201-到达派件城市，3-签收,4-问题件)
     */
    public void setState(Integer state){
      this.state = state;
    }

}