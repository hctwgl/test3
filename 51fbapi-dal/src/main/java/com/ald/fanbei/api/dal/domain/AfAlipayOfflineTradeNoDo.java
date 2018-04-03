package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 支付宝线下转账流水号实体
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-03-22 16:42:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAlipayOfflineTradeNoDo extends AbstractSerial {

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
     * 支付宝流水号
     */
    private String payserialnum;

    /**
     * 用户userid
     */
    private Long userId;


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

    public String getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 获取支付宝流水号
     *
     * @return 支付宝流水号
     */
    public String getPayserialnum(){
      return payserialnum;
    }

    /**
     * 设置支付宝流水号
     * 
     * @param payserialnum 要设置的支付宝流水号
     */
    public void setPayserialnum(String payserialnum){
      this.payserialnum = payserialnum;
    }

    /**
     * 获取用户userid
     *
     * @return 用户userid
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置用户userid
     * 
     * @param userId 要设置的用户userid
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

}