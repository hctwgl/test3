package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-28 15:43:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdNoticeRecordDo extends AbstractSerial {

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
     * 用户id
     */
    private Long userId;

    /**
     * 业务类型：还款：repay
     */
    private String type;

    /**
     * 关联id
     */
    private String refId;

    /**
     * 打款状态【SUCCESS:成功 FAIL:失败】
     */
    private String status;

    /**
     * 剩余通知次数
     */
    private String times;

    /**
     * 请求参数
     */
    private String params;


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
     * 获取业务类型：还款：repay
     *
     * @return 业务类型：还款：repay
     */
    public String getType(){
      return type;
    }

    /**
     * 设置业务类型：还款：repay
     * 
     * @param type 要设置的业务类型：还款：repay
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取关联id
     *
     * @return 关联id
     */
    public String getRefId(){
      return refId;
    }

    /**
     * 设置关联id
     * 
     * @param refId 要设置的关联id
     */
    public void setRefId(String refId){
      this.refId = refId;
    }

    /**
     * 获取打款状态【SUCCESS:成功 FAIL:失败】
     *
     * @return 打款状态【SUCCESS:成功 FAIL:失败】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置打款状态【SUCCESS:成功 FAIL:失败】
     * 
     * @param status 要设置的打款状态【SUCCESS:成功 FAIL:失败】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取剩余通知次数
     *
     * @return 剩余通知次数
     */
    public String getTimes(){
      return times;
    }

    /**
     * 设置剩余通知次数
     * 
     * @param times 要设置的剩余通知次数
     */
    public void setTimes(String times){
      this.times = times;
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

}