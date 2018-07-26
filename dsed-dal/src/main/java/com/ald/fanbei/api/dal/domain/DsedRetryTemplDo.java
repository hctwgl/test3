package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 重试模板实体
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-16 16:28:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class DsedRetryTemplDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 业务方id
     */
    private String busId;

    /**
     * 业务类型:pushEdspayRetry 推送重试   queryEdspayRetry 查询重试
     */
    private String eventType;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 下次重试时间
     */
    private Date gmtNext;

    /**
     * 重试次数
     */
    private Integer times;

    /**
     * 状态Y 处理成功 N处理失败需重试 M到达最大次数
     */
    private String state;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 业务内容
     */
    private String content;


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
     * 获取业务方id
     *
     * @return 业务方id
     */
    public String getBusId(){
      return busId;
    }

    /**
     * 设置业务方id
     * 
     * @param busId 要设置的业务方id
     */
    public void setBusId(String busId){
      this.busId = busId;
    }

    /**
     * 获取业务类型:pushEdspayRetry 推送重试   queryEdspayRetry 查询重试
     *
     * @return 业务类型:pushEdspayRetry 推送重试   queryEdspayRetry 查询重试
     */
    public String getEventType(){
      return eventType;
    }

    /**
     * 设置业务类型:pushEdspayRetry 推送重试   queryEdspayRetry 查询重试
     * 
     * @param eventType 要设置的业务类型:pushEdspayRetry 推送重试   queryEdspayRetry 查询重试
     */
    public void setEventType(String eventType){
      this.eventType = eventType;
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
     * 获取下次重试时间
     *
     * @return 下次重试时间
     */
    public Date getGmtNext(){
      return gmtNext;
    }

    /**
     * 设置下次重试时间
     * 
     * @param gmtNext 要设置的下次重试时间
     */
    public void setGmtNext(Date gmtNext){
      this.gmtNext = gmtNext;
    }

    /**
     * 获取重试次数
     *
     * @return 重试次数
     */
    public Integer getTimes(){
      return times;
    }

    /**
     * 设置重试次数
     * 
     * @param times 要设置的重试次数
     */
    public void setTimes(Integer times){
      this.times = times;
    }

    /**
     * 获取状态Y 处理成功 N处理失败需重试 M到达最大次数
     *
     * @return 状态Y 处理成功 N处理失败需重试 M到达最大次数
     */
    public String getState(){
      return state;
    }

    /**
     * 设置状态Y 处理成功 N处理失败需重试 M到达最大次数
     * 
     * @param state 要设置的状态Y 处理成功 N处理失败需重试 M到达最大次数
     */
    public void setState(String state){
      this.state = state;
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
     * 获取业务内容
     *
     * @return 业务内容
     */
    public String getContent(){
      return content;
    }

    /**
     * 设置业务内容
     * 
     * @param content 要设置的业务内容
     */
    public void setContent(String content){
      this.content = content;
    }

}