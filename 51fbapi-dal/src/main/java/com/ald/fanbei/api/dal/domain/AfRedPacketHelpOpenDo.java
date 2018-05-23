package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfRedPacketHelpOpenDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 所属总红包id
     */
    private Long redPacketTotalId;

    /**
     * 红包所属用户id
     */
    private Long userId;

    /**
     * 微信好友openId
     */
    private String openId;

    /**
     * 微信好友头像
     */
    private String friendAvatar;

    /**
     * 微信好友昵称
     */
    private String friendNick;

    /**
     * 红包金额
     */
    private BigDecimal amount;

    /**
     * 计算红包金额使用的折扣比率
     */
    private BigDecimal discountRate;


    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 添加者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;


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
     * @param rid
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取所属总红包id
     *
     * @return 所属总红包id
     */
    public Long getRedPacketTotalId(){
      return redPacketTotalId;
    }

    /**
     * 设置所属总红包id
     * 
     * @param redPacketTotalId 要设置的所属总红包id
     */
    public void setRedPacketTotalId(Long redPacketTotalId){
      this.redPacketTotalId = redPacketTotalId;
    }

    /**
     * 获取红包所属用户id
     *
     * @return 红包所属用户id
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置红包所属用户id
     * 
     * @param userId 要设置的红包所属用户id
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取微信好友openId
     *
     * @return 微信好友openId
     */
    public String getOpenId(){
      return openId;
    }

    /**
     * 设置微信好友openId
     * 
     * @param openId 要设置的微信好友openId
     */
    public void setOpenId(String openId){
      this.openId = openId;
    }

    /**
     * 获取微信好友头像
     *
     * @return 微信好友头像
     */
    public String getFriendAvatar(){
      return friendAvatar;
    }

    /**
     * 设置微信好友头像
     * 
     * @param friendAvatar 要设置的微信好友头像
     */
    public void setFriendAvatar(String friendAvatar){
      this.friendAvatar = friendAvatar;
    }

    /**
     * 获取微信好友昵称
     *
     * @return 微信好友昵称
     */
    public String getFriendNick(){
      return friendNick;
    }

    /**
     * 设置微信好友昵称
     * 
     * @param friendNick 要设置的微信好友昵称
     */
    public void setFriendNick(String friendNick){
      this.friendNick = friendNick;
    }

    /**
     * 获取红包金额
     *
     * @return 红包金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置红包金额
     * 
     * @param amount 要设置的红包金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取计算红包金额使用的折扣比率
     *
     * @return 计算红包金额使用的折扣比率
     */
    public BigDecimal getDiscountRate(){
      return discountRate;
    }

    /**
     * 设置计算红包金额使用的折扣比率
     * 
     * @param discountRate 要设置的计算红包金额使用的折扣比率
     */
    public void setDiscountRate(BigDecimal discountRate){
      this.discountRate = discountRate;
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
     * 获取修改时间
     *
     * @return 修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置修改时间
     * 
     * @param gmtModified 要设置的修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取添加者
     *
     * @return 添加者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置添加者
     * 
     * @param creator 要设置的添加者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取修改者
     *
     * @return 修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置修改者
     * 
     * @param modifier 要设置的修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

}