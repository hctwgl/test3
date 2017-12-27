package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 年会抽奖实体
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2017-12-27 16:31:00
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserDrawDo extends AbstractSerial {

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
     * 手机号
     */
    private String phone;

    /**
     * 姓名
     */
    private String name;

    /**
     * 0未签到，1 未中奖，2中奖
     */
    private Integer status;

    /**
     * 微信用户在公众号内的openid
     */
    private String openId;

    /**
     * 微信昵称
     */
    private String nickName;

    /**
     * 微信头像
     */
    private String headerImg;

    /**
     * 奖品组
     */
    private String group;


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
     * 获取手机号
     *
     * @return 手机号
     */
    public String getPhone(){
      return phone;
    }

    /**
     * 设置手机号
     * 
     * @param phone 要设置的手机号
     */
    public void setPhone(String phone){
      this.phone = phone;
    }

    /**
     * 获取姓名
     *
     * @return 姓名
     */
    public String getName(){
      return name;
    }

    /**
     * 设置姓名
     * 
     * @param name 要设置的姓名
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取0未签到，1 未中奖，2中奖
     *
     * @return 0未签到，1 未中奖，2中奖
     */
    public Integer getStatus(){
      return status;
    }

    /**
     * 设置0未签到，1 未中奖，2中奖
     * 
     * @param status 要设置的0未签到，1 未中奖，2中奖
     */
    public void setStatus(Integer status){
      this.status = status;
    }

    /**
     * 获取微信用户在公众号内的openid
     *
     * @return 微信用户在公众号内的openid
     */
    public String getOpenId(){
      return openId;
    }

    /**
     * 设置微信用户在公众号内的openid
     * 
     * @param openId 要设置的微信用户在公众号内的openid
     */
    public void setOpenId(String openId){
      this.openId = openId;
    }

    /**
     * 获取微信昵称
     *
     * @return 微信昵称
     */
    public String getNickName(){
      return nickName;
    }

    /**
     * 设置微信昵称
     * 
     * @param nickName 要设置的微信昵称
     */
    public void setNickName(String nickName){
      this.nickName = nickName;
    }

    /**
     * 获取微信头像
     *
     * @return 微信头像
     */
    public String getHeaderImg(){
      return headerImg;
    }

    /**
     * 设置微信头像
     * 
     * @param headerImg 要设置的微信头像
     */
    public void setHeaderImg(String headerImg){
      this.headerImg = headerImg;
    }

    /**
     * 获取奖品组
     *
     * @return 奖品组
     */
    public String getGroup(){
      return group;
    }

    /**
     * 设置奖品组
     * 
     * @param group 要设置的奖品组
     */
    public void setGroup(String group){
      this.group = group;
    }

}