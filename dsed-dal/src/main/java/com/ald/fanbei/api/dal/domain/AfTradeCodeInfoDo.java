package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 交易响应码配置信息实体
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-12-19 17:26:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfTradeCodeInfoDo extends AbstractSerial {

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
     * 创建人
     */
    private String creator;

    /**
     * 最后修改人
     */
    private String modifier;

    /**
     * Y:启用 N:禁用
     */
    private String status;

    /**
     * 爱上街系统响应码
     */
    private String respCode;

    /**
     * 爱上街系统响应描述
     */
    private String respDesc;

    /**
     * 三方交易响应码集合串
     */
    private String tradeCode;


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
     * 获取创建人
     *
     * @return 创建人
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建人
     * 
     * @param creator 要设置的创建人
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取最后修改人
     *
     * @return 最后修改人
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置最后修改人
     * 
     * @param modifier 要设置的最后修改人
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取Y:启用 N:禁用
     *
     * @return Y:启用 N:禁用
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置Y:启用 N:禁用
     * 
     * @param status 要设置的Y:启用 N:禁用
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取爱上街系统响应码
     *
     * @return 爱上街系统响应码
     */
    public String getRespCode(){
      return respCode;
    }

    /**
     * 设置爱上街系统响应码
     * 
     * @param respCode 要设置的爱上街系统响应码
     */
    public void setRespCode(String respCode){
      this.respCode = respCode;
    }

    /**
     * 获取爱上街系统响应描述
     *
     * @return 爱上街系统响应描述
     */
    public String getRespDesc(){
      return respDesc;
    }

    /**
     * 设置爱上街系统响应描述
     * 
     * @param respDesc 要设置的爱上街系统响应描述
     */
    public void setRespDesc(String respDesc){
      this.respDesc = respDesc;
    }

    /**
     * 获取三方交易响应码集合串
     *
     * @return 三方交易响应码集合串
     */
    public String getTradeCode(){
      return tradeCode;
    }

    /**
     * 设置三方交易响应码集合串
     * 
     * @param tradeCode 要设置的三方交易响应码集合串
     */
    public void setTradeCode(String tradeCode){
      this.tradeCode = tradeCode;
    }

}