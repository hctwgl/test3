package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 *  收银台相关配置表实体
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-10-16 09:46:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfCheckoutCounterDo extends AbstractSerial {

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
     * 场景类型
     */
    private String sceneType;

    /**
     * 分期支付状态【N:关闭，Y:开通】
     */
    private String installmentStatus;

    /**
     * 组合支付状态【N:关闭，Y:开通】
     */
    private String cppayStatus;

    /**
     * 微信支付状态【N:关闭，Y:开通】
     */
    private String wxpayStatus;

    /**
     * 银行卡支付状态【N:关闭，Y:开通】
     */
    private String bankpayStatus;
    /**
     * 支付宝支付状态【N:关闭，Y:开通】
     */
    private String alipayStatus;

    /**
     * 商城类型【DIANYING电影, WAIMAI外卖】
     */
    private String sceneSecondType;

    /**
     * 信用支付
     */
    private String creditStatus;
    /**
     * 商城名称
     */
    private String name;
    
    private String temporaryAmountStatus;
    


    public String getTemporaryAmountStatus() {
		return temporaryAmountStatus;
	}

	public void setTemporaryAmountStatus(String temporaryAmountStatus) {
		this.temporaryAmountStatus = temporaryAmountStatus;
	}

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
     * 获取场景类型
     *
     * @return 场景类型
     */
    public String getSceneType(){
      return sceneType;
    }

    /**
     * 设置场景类型
     * 
     * @param sceneType 要设置的场景类型
     */
    public void setSceneType(String sceneType){
      this.sceneType = sceneType;
    }

    /**
     * 获取分期支付状态【N:关闭，Y:开通】
     *
     * @return 分期支付状态【N:关闭，Y:开通】
     */
    public String getInstallmentStatus(){
      return installmentStatus;
    }

    /**
     * 设置分期支付状态【N:关闭，Y:开通】
     * 
     * @param installmentStatus 要设置的分期支付状态【N:关闭，Y:开通】
     */
    public void setInstallmentStatus(String installmentStatus){
      this.installmentStatus = installmentStatus;
    }

    /**
     * 获取组合支付状态【N:关闭，Y:开通】
     *
     * @return 组合支付状态【N:关闭，Y:开通】
     */
    public String getCppayStatus(){
      return cppayStatus;
    }

    /**
     * 设置组合支付状态【N:关闭，Y:开通】
     * 
     * @param cppayStatus 要设置的组合支付状态【N:关闭，Y:开通】
     */
    public void setCppayStatus(String cppayStatus){
      this.cppayStatus = cppayStatus;
    }

    /**
     * 获取微信支付状态【N:关闭，Y:开通】
     *
     * @return 微信支付状态【N:关闭，Y:开通】
     */
    public String getWxpayStatus(){
      return wxpayStatus;
    }

    /**
     * 设置微信支付状态【N:关闭，Y:开通】
     * 
     * @param wxpayStatus 要设置的微信支付状态【N:关闭，Y:开通】
     */
    public void setWxpayStatus(String wxpayStatus){
      this.wxpayStatus = wxpayStatus;
    }

    /**
     * 获取银行卡支付状态【N:关闭，Y:开通】
     *
     * @return 银行卡支付状态【N:关闭，Y:开通】
     */
    public String getBankpayStatus(){
      return bankpayStatus;
    }

    /**
     * 设置银行卡支付状态【N:关闭，Y:开通】
     * 
     * @param bankpayStatus 要设置的银行卡支付状态【N:关闭，Y:开通】
     */
    public void setBankpayStatus(String bankpayStatus){
      this.bankpayStatus = bankpayStatus;
    }

    /**
     * 获取商城类型【DIANYING电影, WAIMAI外卖】
     *
     * @return 商城类型【DIANYING电影, WAIMAI外卖】
     */
    public String getSceneSecondType(){
      return sceneSecondType;
    }

    /**
     * 设置商城类型【DIANYING电影, WAIMAI外卖】
     * 
     * @param sceneSecondType 要设置的商城类型【DIANYING电影, WAIMAI外卖】
     */
    public void setSceneSecondType(String sceneSecondType){
      this.sceneSecondType = sceneSecondType;
    }

    /**
     * 获取商城名称
     *
     * @return 商城名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置商城名称
     * 
     * @param name 要设置的商城名称
     */
    public void setName(String name){
      this.name = name;
    }

    public String getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    public String getAlipayStatus() {
        return alipayStatus;
    }

    public void setAlipayStatus(String alipayStatus) {
        this.alipayStatus = alipayStatus;
    }
}