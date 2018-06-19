package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市易贷短信验证码记录实体
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:41:19
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedCodeRecordDo extends AbstractSerial {

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
     * 验证码类型 【R.regist注册验证码 F.forget忘记密码，M:绑定手机号,P:设置支付密码,E:邮箱验证码,Q:快捷登录/快捷注册,S:设置快捷登录密码,】
     */
    private String type;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 发送手机号或者邮箱
     */
    private String sendAccount;

    /**
     * 验证码
     */
    private String verifyCode;

    /**
     * 返回结果
     */
    private String result;

    /**
     * 是否已经验证,0:未验证 1：已经验证
     */
    private Integer isCheck;

    /**
     * 
     */
    private Integer failCount;

    /**
     * 客户端的外网ip
     */
    private String ip;


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
     * 获取验证码类型 【R.regist注册验证码 F.forget忘记密码，M:绑定手机号,P:设置支付密码,E:邮箱验证码,Q:快捷登录/快捷注册,S:设置快捷登录密码,】
     *
     * @return 验证码类型 【R.regist注册验证码 F.forget忘记密码，M:绑定手机号,P:设置支付密码,E:邮箱验证码,Q:快捷登录/快捷注册,S:设置快捷登录密码,】
     */
    public String getType(){
      return type;
    }

    /**
     * 设置验证码类型 【R.regist注册验证码 F.forget忘记密码，M:绑定手机号,P:设置支付密码,E:邮箱验证码,Q:快捷登录/快捷注册,S:设置快捷登录密码,】
     * 
     * @param type 要设置的验证码类型 【R.regist注册验证码 F.forget忘记密码，M:绑定手机号,P:设置支付密码,E:邮箱验证码,Q:快捷登录/快捷注册,S:设置快捷登录密码,】
     */
    public void setType(String type){
      this.type = type;
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
     * 获取发送手机号或者邮箱
     *
     * @return 发送手机号或者邮箱
     */
    public String getSendAccount(){
      return sendAccount;
    }

    /**
     * 设置发送手机号或者邮箱
     * 
     * @param sendAccount 要设置的发送手机号或者邮箱
     */
    public void setSendAccount(String sendAccount){
      this.sendAccount = sendAccount;
    }

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    public String getVerifyCode(){
      return verifyCode;
    }

    /**
     * 设置验证码
     * 
     * @param verifyCode 要设置的验证码
     */
    public void setVerifyCode(String verifyCode){
      this.verifyCode = verifyCode;
    }

    /**
     * 获取返回结果
     *
     * @return 返回结果
     */
    public String getResult(){
      return result;
    }

    /**
     * 设置返回结果
     * 
     * @param result 要设置的返回结果
     */
    public void setResult(String result){
      this.result = result;
    }

    /**
     * 获取是否已经验证,0:未验证 1：已经验证
     *
     * @return 是否已经验证,0:未验证 1：已经验证
     */
    public Integer getIsCheck(){
      return isCheck;
    }

    /**
     * 设置是否已经验证,0:未验证 1：已经验证
     * 
     * @param isCheck 要设置的是否已经验证,0:未验证 1：已经验证
     */
    public void setIsCheck(Integer isCheck){
      this.isCheck = isCheck;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Integer getFailCount(){
      return failCount;
    }

    /**
     * 设置
     * 
     * @param failCount 要设置的
     */
    public void setFailCount(Integer failCount){
      this.failCount = failCount;
    }

    /**
     * 获取客户端的外网ip
     *
     * @return 客户端的外网ip
     */
    public String getIp(){
      return ip;
    }

    /**
     * 设置客户端的外网ip
     * 
     * @param ip 要设置的客户端的外网ip
     */
    public void setIp(String ip){
      this.ip = ip;
    }

}