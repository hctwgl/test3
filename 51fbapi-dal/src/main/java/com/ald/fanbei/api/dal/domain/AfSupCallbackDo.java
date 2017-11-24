package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 游戏充值实体
 * 
 * @author 高继斌_temple
 * @version 1.0.0 初始化
 * @date 2017-11-24 16:56:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfSupCallbackDo extends AbstractSerial {

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
     * 关联af_order表的order_no
     */
    private String orderNo;

    /**
     * 01成功 02失败
     */
    private String status;

    /**
     * 充值结果说明
     */
    private String mes;

    /**
     * 卡密信息
     */
    private String kminfo;

    /**
     * 结算总金额
     */
    private BigDecimal payoffPriceTotal;

    /**
     * 回调参数签名
     */
    private String sign;

    /**
     * 程序验签计算产生的签名
     */
    private String signCheck;

    /**
     * 验签结果1 通过 0未通过
     */
    private Integer result;


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
     * 获取关联af_order表的order_no
     *
     * @return 关联af_order表的order_no
     */
    public String getOrderNo(){
      return orderNo;
    }

    /**
     * 设置关联af_order表的order_no
     * 
     * @param orderNo 要设置的关联af_order表的order_no
     */
    public void setOrderNo(String orderNo){
      this.orderNo = orderNo;
    }

    /**
     * 获取01成功 02失败
     *
     * @return 01成功 02失败
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置01成功 02失败
     * 
     * @param status 要设置的01成功 02失败
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取充值结果说明
     *
     * @return 充值结果说明
     */
    public String getMes(){
      return mes;
    }

    /**
     * 设置充值结果说明
     * 
     * @param mes 要设置的充值结果说明
     */
    public void setMes(String mes){
      this.mes = mes;
    }

    /**
     * 获取卡密信息
     *
     * @return 卡密信息
     */
    public String getKminfo(){
      return kminfo;
    }

    /**
     * 设置卡密信息
     * 
     * @param kminfo 要设置的卡密信息
     */
    public void setKminfo(String kminfo){
      this.kminfo = kminfo;
    }

    /**
     * 获取结算总金额
     *
     * @return 结算总金额
     */
    public BigDecimal getPayoffPriceTotal(){
      return payoffPriceTotal;
    }

    /**
     * 设置结算总金额
     * 
     * @param payoffPriceTotal 要设置的结算总金额
     */
    public void setPayoffPriceTotal(BigDecimal payoffPriceTotal){
      this.payoffPriceTotal = payoffPriceTotal;
    }

    /**
     * 获取回调参数签名
     *
     * @return 回调参数签名
     */
    public String getSign(){
      return sign;
    }

    /**
     * 设置回调参数签名
     * 
     * @param sign 要设置的回调参数签名
     */
    public void setSign(String sign){
      this.sign = sign;
    }

    /**
     * 获取程序验签计算产生的签名
     *
     * @return 程序验签计算产生的签名
     */
    public String getSignCheck(){
      return signCheck;
    }

    /**
     * 设置程序验签计算产生的签名
     * 
     * @param signCheck 要设置的程序验签计算产生的签名
     */
    public void setSignCheck(String signCheck){
      this.signCheck = signCheck;
    }

    /**
     * 获取验签结果1 通过 0未通过
     *
     * @return 验签结果1 通过 0未通过
     */
    public Integer getResult(){
      return result;
    }

    /**
     * 设置验签结果1 通过 0未通过
     * 
     * @param result 要设置的验签结果1 通过 0未通过
     */
    public void setResult(Integer result){
      this.result = result;
    }

}