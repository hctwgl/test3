package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 债权推送查询上限记录表实体
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-11 18:10:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfRecordMaxDo extends AbstractSerial {

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
     * 业务类型:pushEdspay 推送   queryEdspay 查询
     */
    private String eventType;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;


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
     * 获取业务类型:pushEdspay 推送   queryEdspay 查询
     *
     * @return 业务类型:pushEdspay 推送   queryEdspay 查询
     */
    public String getEventType(){
      return eventType;
    }

    /**
     * 设置业务类型:pushEdspay 推送   queryEdspay 查询
     * 
     * @param eventType 要设置的业务类型:pushEdspay 推送   queryEdspay 查询
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

}