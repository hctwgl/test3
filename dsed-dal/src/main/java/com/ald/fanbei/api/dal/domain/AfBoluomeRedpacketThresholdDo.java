package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 点亮活动新版实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeRedpacketThresholdDo extends AbstractSerial {

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
     * 起始范围单数 (<= 判断)
     */
    private Integer start;

    /**
     * 结束范围单数(>= 判断)
     */
    private Integer end;


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
     * 获取起始范围单数 (<= 判断)
     *
     * @return 起始范围单数 (<= 判断)
     */
    public Integer getStart(){
      return start;
    }

    /**
     * 设置起始范围单数 (<= 判断)
     * 
     * @param start 要设置的起始范围单数 (<= 判断)
     */
    public void setStart(Integer start){
      this.start = start;
    }

    /**
     * 获取结束范围单数(>= 判断)
     *
     * @return 结束范围单数(>= 判断)
     */
    public Integer getEnd(){
      return end;
    }

    /**
     * 设置结束范围单数(>= 判断)
     * 
     * @param end 要设置的结束范围单数(>= 判断)
     */
    public void setEnd(Integer end){
      this.end = end;
    }

}