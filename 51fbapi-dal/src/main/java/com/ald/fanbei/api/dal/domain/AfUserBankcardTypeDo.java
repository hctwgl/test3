package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 信用卡绑定及订单支付实体
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-05-10 09:49:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserBankcardTypeDo extends AbstractSerial {

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
     * 用户绑定银行卡id
     */
    private Long userBankcardId;

    /**
     * 0 其它
            1 借记卡
            2 信用卡
     */
    private Integer type;

    /**
     * 
     */
    private String validDate;

    /**
     * 
     */
    private String safeCode;


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
     * 获取用户绑定银行卡id
     *
     * @return 用户绑定银行卡id
     */
    public Long getUserBankcardId(){
      return userBankcardId;
    }

    /**
     * 设置用户绑定银行卡id
     * 
     * @param userBankcardId 要设置的用户绑定银行卡id
     */
    public void setUserBankcardId(Long userBankcardId){
      this.userBankcardId = userBankcardId;
    }

    /**
     * 获取0 其它
            1 借记卡
            2 信用卡
     *
     * @return 0 其它
            1 借记卡
            2 信用卡
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置0 其它
            1 借记卡
            2 信用卡
     * 
     * @param type 要设置的0 其它
            1 借记卡
            2 信用卡
     */
    public void setType(Integer type){
      this.type = type;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getValidDate(){
      return validDate;
    }

    /**
     * 设置
     * 
     * @param validDate 要设置的
     */
    public void setValidDate(String validDate){
      this.validDate = validDate;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getSafeCode(){
      return safeCode;
    }

    /**
     * 设置
     * 
     * @param safeCode 要设置的
     */
    public void setSafeCode(String safeCode){
      this.safeCode = safeCode;
    }

}