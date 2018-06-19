package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 信用卡绑定及订单支付实体
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:36:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedBankDo extends AbstractSerial {

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
     * 银行代码
     */
    private String bankCode;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行图标
     */
    private String bankIcon;

    /**
     * 是否有效【Y:有效，N：失效】,针对后续变更为不可用的银行卡需要把状态置为N
     */
    private String isValid;

    /**
     * DAISHOU:代收(代扣); KUAIJIE(快捷有短验);KUAIJIE_NO_SMS(快捷无短验);XIEYI(协议)
     */
    private String payType;

    /**
     * 
     */
    private Integer color;


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
     * 获取银行代码
     *
     * @return 银行代码
     */
    public String getBankCode(){
      return bankCode;
    }

    /**
     * 设置银行代码
     * 
     * @param bankCode 要设置的银行代码
     */
    public void setBankCode(String bankCode){
      this.bankCode = bankCode;
    }

    /**
     * 获取银行名称
     *
     * @return 银行名称
     */
    public String getBankName(){
      return bankName;
    }

    /**
     * 设置银行名称
     * 
     * @param bankName 要设置的银行名称
     */
    public void setBankName(String bankName){
      this.bankName = bankName;
    }

    /**
     * 获取银行图标
     *
     * @return 银行图标
     */
    public String getBankIcon(){
      return bankIcon;
    }

    /**
     * 设置银行图标
     * 
     * @param bankIcon 要设置的银行图标
     */
    public void setBankIcon(String bankIcon){
      this.bankIcon = bankIcon;
    }

    /**
     * 获取是否有效【Y:有效，N：失效】,针对后续变更为不可用的银行卡需要把状态置为N
     *
     * @return 是否有效【Y:有效，N：失效】,针对后续变更为不可用的银行卡需要把状态置为N
     */
    public String getIsValid(){
      return isValid;
    }

    /**
     * 设置是否有效【Y:有效，N：失效】,针对后续变更为不可用的银行卡需要把状态置为N
     * 
     * @param isValid 要设置的是否有效【Y:有效，N：失效】,针对后续变更为不可用的银行卡需要把状态置为N
     */
    public void setIsValid(String isValid){
      this.isValid = isValid;
    }

    /**
     * 获取DAISHOU:代收(代扣); KUAIJIE(快捷有短验);KUAIJIE_NO_SMS(快捷无短验);XIEYI(协议)
     *
     * @return DAISHOU:代收(代扣); KUAIJIE(快捷有短验);KUAIJIE_NO_SMS(快捷无短验);XIEYI(协议)
     */
    public String getPayType(){
      return payType;
    }

    /**
     * 设置DAISHOU:代收(代扣); KUAIJIE(快捷有短验);KUAIJIE_NO_SMS(快捷无短验);XIEYI(协议)
     * 
     * @param payType 要设置的DAISHOU:代收(代扣); KUAIJIE(快捷有短验);KUAIJIE_NO_SMS(快捷无短验);XIEYI(协议)
     */
    public void setPayType(String payType){
      this.payType = payType;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Integer getColor(){
      return color;
    }

    /**
     * 设置
     * 
     * @param color 要设置的
     */
    public void setColor(Integer color){
      this.color = color;
    }

}