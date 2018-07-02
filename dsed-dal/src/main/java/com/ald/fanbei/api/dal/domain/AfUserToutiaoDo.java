package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-05 16:39:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserToutiaoDo extends AbstractSerial {

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
     * 广告计划id 
     */
    private String aid;

    /**
     * 广告创意id
     */
    private String cid;

    /**
     * 广告投放位置
     */
    private String csite;

    /**
     * 广告样式:2=小图模式 3=大图模式 4=组图模式 5=视频模式  
     */
    private Integer ctype;

    /**
     * 用户终端的eth0接口的MAC地址，去除分隔符“:”,md5sum摘要
     */
    private String mac;

    /**
     * iOS IDFA适用 iOS6及以上
     */
    private String idfa;

    /**
     * 用户终端AndroidID，md5加密
     */
    private String androidid;

    /**
     * 用户终端AndroidID
     */
    private String androidid1;

    /**
     * 用户终端的IMEI
     */
    private String imei;

    /**
     * 用户终端的UUID 
     */
    private String uuid;

    /**
     * openudid,不加密
     */
    private String openudid;

    /**
     * iOS UDID,md5加密
     */
    private String udid;

    /**
     * 操作系统：0–Android 1–iOS 2– WP 3-Others 
     */
    private Integer os;

    /**
     * 用户终端公共ip
     */
    private String ip;

    /**
     * 客户端触发监控的时间
     */
    private String ts;

    /**
     * 回调地址
     */
    private String callbackUrl;

    /**
     * 是否激活
     */
    private Integer active;




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
     * 获取广告计划id 
     *
     * @return 广告计划id 
     */
    public String getAid(){
      return aid;
    }

    /**
     * 设置广告计划id 
     * 
     * @param aid 要设置的广告计划id 
     */
    public void setAid(String aid){
      this.aid = aid;
    }

    /**
     * 获取广告创意id
     *
     * @return 广告创意id
     */
    public String getCid(){
      return cid;
    }

    /**
     * 设置广告创意id
     * 
     * @param cid 要设置的广告创意id
     */
    public void setCid(String cid){
      this.cid = cid;
    }

    /**
     * 获取广告投放位置
     *
     * @return 广告投放位置
     */
    public String getCsite(){
      return csite;
    }

    /**
     * 设置广告投放位置
     * 
     * @param csite 要设置的广告投放位置
     */
    public void setCsite(String csite){
      this.csite = csite;
    }

    /**
     * 获取广告样式:2=小图模式 3=大图模式 4=组图模式 5=视频模式  
     *
     * @return 广告样式:2=小图模式 3=大图模式 4=组图模式 5=视频模式  
     */
    public Integer getCtype(){
      return ctype;
    }

    /**
     * 设置广告样式:2=小图模式 3=大图模式 4=组图模式 5=视频模式  
     * 
     * @param ctype 要设置的广告样式:2=小图模式 3=大图模式 4=组图模式 5=视频模式  
     */
    public void setCtype(Integer ctype){
      this.ctype = ctype;
    }

    /**
     * 获取用户终端的eth0接口的MAC地址，去除分隔符“:”,md5sum摘要
     *
     * @return 用户终端的eth0接口的MAC地址，去除分隔符“:”,md5sum摘要
     */
    public String getMac(){
      return mac;
    }

    /**
     * 设置用户终端的eth0接口的MAC地址，去除分隔符“:”,md5sum摘要
     * 
     * @param mac 要设置的用户终端的eth0接口的MAC地址，去除分隔符“:”,md5sum摘要
     */
    public void setMac(String mac){
      this.mac = mac;
    }

    /**
     * 获取iOS IDFA适用 iOS6及以上
     *
     * @return iOS IDFA适用 iOS6及以上
     */
    public String getIdfa(){
      return idfa;
    }

    /**
     * 设置iOS IDFA适用 iOS6及以上
     * 
     * @param idfa 要设置的iOS IDFA适用 iOS6及以上
     */
    public void setIdfa(String idfa){
      this.idfa = idfa;
    }

    /**
     * 获取用户终端AndroidID，md5加密
     *
     * @return 用户终端AndroidID，md5加密
     */
    public String getAndroidid(){
      return androidid;
    }

    /**
     * 设置用户终端AndroidID，md5加密
     * 
     * @param androidid 要设置的用户终端AndroidID，md5加密
     */
    public void setAndroidid(String androidid){
      this.androidid = androidid;
    }

    /**
     * 获取用户终端AndroidID
     *
     * @return 用户终端AndroidID
     */
    public String getAndroidid1(){
      return androidid1;
    }

    /**
     * 设置用户终端AndroidID
     * 
     * @param androidid1 要设置的用户终端AndroidID
     */
    public void setAndroidid1(String androidid1){
      this.androidid1 = androidid1;
    }

    /**
     * 获取用户终端的IMEI
     *
     * @return 用户终端的IMEI
     */
    public String getImei(){
      return imei;
    }

    /**
     * 设置用户终端的IMEI
     * 
     * @param imei 要设置的用户终端的IMEI
     */
    public void setImei(String imei){
      this.imei = imei;
    }

    /**
     * 获取用户终端的UUID 
     *
     * @return 用户终端的UUID 
     */
    public String getUuid(){
      return uuid;
    }

    /**
     * 设置用户终端的UUID 
     * 
     * @param uuid 要设置的用户终端的UUID 
     */
    public void setUuid(String uuid){
      this.uuid = uuid;
    }

    /**
     * 获取openudid,不加密
     *
     * @return openudid,不加密
     */
    public String getOpenudid(){
      return openudid;
    }

    /**
     * 设置openudid,不加密
     * 
     * @param openudid 要设置的openudid,不加密
     */
    public void setOpenudid(String openudid){
      this.openudid = openudid;
    }

    /**
     * 获取iOS UDID,md5加密
     *
     * @return iOS UDID,md5加密
     */
    public String getUdid(){
      return udid;
    }

    /**
     * 设置iOS UDID,md5加密
     * 
     * @param udid 要设置的iOS UDID,md5加密
     */
    public void setUdid(String udid){
      this.udid = udid;
    }

    /**
     * 获取操作系统：0–Android 1–iOS 2– WP 3-Others 
     *
     * @return 操作系统：0–Android 1–iOS 2– WP 3-Others 
     */
    public Integer getOs(){
      return os;
    }

    /**
     * 设置操作系统：0–Android 1–iOS 2– WP 3-Others 
     * 
     * @param os 要设置的操作系统：0–Android 1–iOS 2– WP 3-Others 
     */
    public void setOs(Integer os){
      this.os = os;
    }

    /**
     * 获取用户终端公共ip
     *
     * @return 用户终端公共ip
     */
    public String getIp(){
      return ip;
    }

    /**
     * 设置用户终端公共ip
     * 
     * @param ip 要设置的用户终端公共ip
     */
    public void setIp(String ip){
      this.ip = ip;
    }

    /**
     * 获取客户端触发监控的时间
     *
     * @return 客户端触发监控的时间
     */
    public String getTs(){
      return ts;
    }

    /**
     * 设置客户端触发监控的时间
     * 
     * @param ts 要设置的客户端触发监控的时间
     */
    public void setTs(String ts){
      this.ts = ts;
    }

    /**
     * 获取回调地址
     *
     * @return 回调地址
     */
    public String getCallbackUrl(){
      return callbackUrl;
    }

    /**
     * 设置回调地址
     * 
     * @param callbackUrl 要设置的回调地址
     */
    public void setCallbackUrl(String callbackUrl){
      this.callbackUrl = callbackUrl;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }
}