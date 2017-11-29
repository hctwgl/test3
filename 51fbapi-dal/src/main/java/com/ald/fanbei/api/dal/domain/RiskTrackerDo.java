package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 风控调用追踪实体
 * 
 * @author 任春雷
 * @version 1.0.0 初始化
 * @date 2017-11-07 21:36:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class RiskTrackerDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 跟踪id
     */
    private String trackId;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 请求结果
     */
    private String result;

    /**
     * 
     */
    private Date gmtCreate;


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
     * 获取跟踪id
     *
     * @return 跟踪id
     */
    public String getTrackId(){
      return trackId;
    }

    /**
     * 设置跟踪id
     * 
     * @param trackId 要设置的跟踪id
     */
    public void setTrackId(String trackId){
      this.trackId = trackId;
    }

    /**
     * 获取请求路径
     *
     * @return 请求路径
     */
    public String getUrl(){
      return url;
    }

    /**
     * 设置请求路径
     * 
     * @param url 要设置的请求路径
     */
    public void setUrl(String url){
      this.url = url;
    }

    /**
     * 获取请求参数
     *
     * @return 请求参数
     */
    public String getParams(){
      return params;
    }

    /**
     * 设置请求参数
     * 
     * @param params 要设置的请求参数
     */
    public void setParams(String params){
      this.params = params;
    }

    /**
     * 获取请求结果
     *
     * @return 请求结果
     */
    public String getResult(){
      return result;
    }

    /**
     * 设置请求结果
     * 
     * @param result 要设置的请求结果
     */
    public void setResult(String result){
      this.result = result;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置
     * 
     * @param gmtCreate 要设置的
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

}