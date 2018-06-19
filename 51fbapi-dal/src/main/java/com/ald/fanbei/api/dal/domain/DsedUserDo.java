package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市易贷用户信息实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:34
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedUserDo extends AbstractSerial {

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
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码盐值
     */
    private String salt;

    /**
     * 性别 【F：女 ，M：男， U:未知】
     */
    private String gender;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 绑定手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 出生年月日，格式yyyy-MM-dd
     */
    private String birthday;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区/县
     */
    private String county;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 邀请人id
     */
    private Long recommendId;

    /**
     * 登录失败次数
     */
    private Integer failCount;

    /**
     * 邀请码
     */
    private String recommendCode;

    /**
     * NORMAL:正常使用，FROZEN：表示冻结
     */
    private String status;

    /**
     * 注册来源渠道ID
     */
    private String registerChannelId;

    /**
     * 注册来源渠道位置ID
     */
    private String registerChannelPointId;

    /**
     * 马甲包名称,www为app,其余的为马甲包名称
     */
    private String majiabaoName;

    /**
     * 微信小程序用户对应的openid
     */
    private String openId;


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
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUserName(){
      return userName;
    }

    /**
     * 设置用户名
     * 
     * @param userName 要设置的用户名
     */
    public void setUserName(String userName){
      this.userName = userName;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword(){
      return password;
    }

    /**
     * 设置密码
     * 
     * @param password 要设置的密码
     */
    public void setPassword(String password){
      this.password = password;
    }

    /**
     * 获取密码盐值
     *
     * @return 密码盐值
     */
    public String getSalt(){
      return salt;
    }

    /**
     * 设置密码盐值
     * 
     * @param salt 要设置的密码盐值
     */
    public void setSalt(String salt){
      this.salt = salt;
    }

    /**
     * 获取性别 【F：女 ，M：男， U:未知】
     *
     * @return 性别 【F：女 ，M：男， U:未知】
     */
    public String getGender(){
      return gender;
    }

    /**
     * 设置性别 【F：女 ，M：男， U:未知】
     * 
     * @param gender 要设置的性别 【F：女 ，M：男， U:未知】
     */
    public void setGender(String gender){
      this.gender = gender;
    }

    /**
     * 获取昵称
     *
     * @return 昵称
     */
    public String getNick(){
      return nick;
    }

    /**
     * 设置昵称
     * 
     * @param nick 要设置的昵称
     */
    public void setNick(String nick){
      this.nick = nick;
    }

    /**
     * 获取头像
     *
     * @return 头像
     */
    public String getAvatar(){
      return avatar;
    }

    /**
     * 设置头像
     * 
     * @param avatar 要设置的头像
     */
    public void setAvatar(String avatar){
      this.avatar = avatar;
    }

    /**
     * 获取真实姓名
     *
     * @return 真实姓名
     */
    public String getRealName(){
      return realName;
    }

    /**
     * 设置真实姓名
     * 
     * @param realName 要设置的真实姓名
     */
    public void setRealName(String realName){
      this.realName = realName;
    }

    /**
     * 获取绑定手机号
     *
     * @return 绑定手机号
     */
    public String getMobile(){
      return mobile;
    }

    /**
     * 设置绑定手机号
     * 
     * @param mobile 要设置的绑定手机号
     */
    public void setMobile(String mobile){
      this.mobile = mobile;
    }

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    public String getEmail(){
      return email;
    }

    /**
     * 设置邮箱
     * 
     * @param email 要设置的邮箱
     */
    public void setEmail(String email){
      this.email = email;
    }

    /**
     * 获取出生年月日，格式yyyy-MM-dd
     *
     * @return 出生年月日，格式yyyy-MM-dd
     */
    public String getBirthday(){
      return birthday;
    }

    /**
     * 设置出生年月日，格式yyyy-MM-dd
     * 
     * @param birthday 要设置的出生年月日，格式yyyy-MM-dd
     */
    public void setBirthday(String birthday){
      this.birthday = birthday;
    }

    /**
     * 获取省
     *
     * @return 省
     */
    public String getProvince(){
      return province;
    }

    /**
     * 设置省
     * 
     * @param province 要设置的省
     */
    public void setProvince(String province){
      this.province = province;
    }

    /**
     * 获取市
     *
     * @return 市
     */
    public String getCity(){
      return city;
    }

    /**
     * 设置市
     * 
     * @param city 要设置的市
     */
    public void setCity(String city){
      this.city = city;
    }

    /**
     * 获取区/县
     *
     * @return 区/县
     */
    public String getCounty(){
      return county;
    }

    /**
     * 设置区/县
     * 
     * @param county 要设置的区/县
     */
    public void setCounty(String county){
      this.county = county;
    }

    /**
     * 获取详细地址
     *
     * @return 详细地址
     */
    public String getAddress(){
      return address;
    }

    /**
     * 设置详细地址
     * 
     * @param address 要设置的详细地址
     */
    public void setAddress(String address){
      this.address = address;
    }

    /**
     * 获取邀请人id
     *
     * @return 邀请人id
     */
    public Long getRecommendId(){
      return recommendId;
    }

    /**
     * 设置邀请人id
     * 
     * @param recommendId 要设置的邀请人id
     */
    public void setRecommendId(Long recommendId){
      this.recommendId = recommendId;
    }

    /**
     * 获取登录失败次数
     *
     * @return 登录失败次数
     */
    public Integer getFailCount(){
      return failCount;
    }

    /**
     * 设置登录失败次数
     * 
     * @param failCount 要设置的登录失败次数
     */
    public void setFailCount(Integer failCount){
      this.failCount = failCount;
    }

    /**
     * 获取邀请码
     *
     * @return 邀请码
     */
    public String getRecommendCode(){
      return recommendCode;
    }

    /**
     * 设置邀请码
     * 
     * @param recommendCode 要设置的邀请码
     */
    public void setRecommendCode(String recommendCode){
      this.recommendCode = recommendCode;
    }

    /**
     * 获取NORMAL:正常使用，FROZEN：表示冻结
     *
     * @return NORMAL:正常使用，FROZEN：表示冻结
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置NORMAL:正常使用，FROZEN：表示冻结
     * 
     * @param status 要设置的NORMAL:正常使用，FROZEN：表示冻结
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取注册来源渠道ID
     *
     * @return 注册来源渠道ID
     */
    public String getRegisterChannelId(){
      return registerChannelId;
    }

    /**
     * 设置注册来源渠道ID
     * 
     * @param registerChannelId 要设置的注册来源渠道ID
     */
    public void setRegisterChannelId(String registerChannelId){
      this.registerChannelId = registerChannelId;
    }

    /**
     * 获取注册来源渠道位置ID
     *
     * @return 注册来源渠道位置ID
     */
    public String getRegisterChannelPointId(){
      return registerChannelPointId;
    }

    /**
     * 设置注册来源渠道位置ID
     * 
     * @param registerChannelPointId 要设置的注册来源渠道位置ID
     */
    public void setRegisterChannelPointId(String registerChannelPointId){
      this.registerChannelPointId = registerChannelPointId;
    }

    /**
     * 获取马甲包名称,www为app,其余的为马甲包名称
     *
     * @return 马甲包名称,www为app,其余的为马甲包名称
     */
    public String getMajiabaoName(){
      return majiabaoName;
    }

    /**
     * 设置马甲包名称,www为app,其余的为马甲包名称
     * 
     * @param majiabaoName 要设置的马甲包名称,www为app,其余的为马甲包名称
     */
    public void setMajiabaoName(String majiabaoName){
      this.majiabaoName = majiabaoName;
    }

    /**
     * 获取微信小程序用户对应的openid
     *
     * @return 微信小程序用户对应的openid
     */
    public String getOpenId(){
      return openId;
    }

    /**
     * 设置微信小程序用户对应的openid
     * 
     * @param openId 要设置的微信小程序用户对应的openid
     */
    public void setOpenId(String openId){
      this.openId = openId;
    }

}