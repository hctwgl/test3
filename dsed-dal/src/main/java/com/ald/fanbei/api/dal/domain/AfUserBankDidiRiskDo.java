package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 滴滴风控绑卡信息实体
 * 
 * @author xiaotianjian
 * @version 1.0.0 初始化
 * @date 2017-08-14 13:41:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserBankDidiRiskDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userId;

    /**
     * 用户绑卡id
     */
    private Long userBankId;

    /**
     * 绑卡设备uuid
     */
    private String uuid;

    /**
     * 绑卡ip
     */
    private String ip;

    /**
     * 绑卡时纬度
     */
    private BigDecimal lat;

    /**
     * 绑卡时经度
     */
    private BigDecimal lng;

    /**
     * 绑卡wifi_mac
     */
    private String wifiMac;


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
     * 获取用户绑卡id
     *
     * @return 用户绑卡id
     */
    public Long getUserBankId(){
      return userBankId;
    }

    /**
     * 设置用户绑卡id
     * 
     * @param userBankId 要设置的用户绑卡id
     */
    public void setUserBankId(Long userBankId){
      this.userBankId = userBankId;
    }

    /**
     * 获取绑卡设备uuid
     *
     * @return 绑卡设备uuid
     */
    public String getUuid(){
      return uuid;
    }

    /**
     * 设置绑卡设备uuid
     * 
     * @param uuid 要设置的绑卡设备uuid
     */
    public void setUuid(String uuid){
      this.uuid = uuid;
    }

    /**
     * 获取绑卡ip
     *
     * @return 绑卡ip
     */
    public String getIp(){
      return ip;
    }

    /**
     * 设置绑卡ip
     * 
     * @param ip 要设置的绑卡ip
     */
    public void setIp(String ip){
      this.ip = ip;
    }

    /**
     * 获取绑卡时纬度
     *
     * @return 绑卡时纬度
     */
    public BigDecimal getLat(){
      return lat;
    }

    /**
     * 设置绑卡时纬度
     * 
     * @param lat 要设置的绑卡时纬度
     */
    public void setLat(BigDecimal lat){
      this.lat = lat;
    }

    /**
     * 获取绑卡时经度
     *
     * @return 绑卡时经度
     */
    public BigDecimal getLng(){
      return lng;
    }

    /**
     * 设置绑卡时经度
     * 
     * @param lng 要设置的绑卡时经度
     */
    public void setLng(BigDecimal lng){
      this.lng = lng;
    }

    /**
     * 获取绑卡wifi_mac
     *
     * @return 绑卡wifi_mac
     */
    public String getWifiMac(){
      return wifiMac;
    }

    /**
     * 设置绑卡wifi_mac
     * 
     * @param wifiMac 要设置的绑卡wifi_mac
     */
    public void setWifiMac(String wifiMac){
      this.wifiMac = wifiMac;
    }

}