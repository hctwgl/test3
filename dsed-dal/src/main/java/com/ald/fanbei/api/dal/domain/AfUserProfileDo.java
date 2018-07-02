package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 用户关联账号实体
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-01-24 16:04:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserProfileDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 
     */
    private String gmtCreate;

    /**
     * 
     */
    private String gmtModified;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 支付宝账号
     */
    private String account;


    /**
     * 账户类型
     */
    private String type;


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
    public String getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置
     * 
     * @param gmtCreate 要设置的
     */
    public void setGmtCreate(String gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    public String getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(String gmtModified) {
        this.gmtModified = gmtModified;
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
     * 获取支付宝账号
     *
     * @return 支付宝账号
     */
    public String getAccount(){
      return account;
    }

    /**
     * 设置支付宝账号
     * 
     * @param account 要设置的支付宝账号
     */
    public void setAccount(String account){
      this.account = account;
    }


    /**
     * 获取账户类型
     *
     * @return 账户类型
     */
    public String getType(){
      return type;
    }

    /**
     * 设置账户类型
     * 
     * @param type 要设置的账户类型
     */
    public void setType(String type){
      this.type = type;
    }

}