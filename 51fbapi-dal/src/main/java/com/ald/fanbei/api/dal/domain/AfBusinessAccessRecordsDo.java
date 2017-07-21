package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 业务访问记录实体
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-19 16:26:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBusinessAccessRecordsDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    

    /**
     * 创建访问时间
     */
    private Date gmtCreate;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 请求来源ip
     */
    private String sourceIp;

    /**
     * 关联业务类型【LOANSUPERMARKET:借款超市访问】
     */
    private String refType;

    /**
     * 关联业务对应id
     */
    private Long refId;

    /**
     * 额外信息
     */
    private String extraInfo;

    /**
     * 备注说明
     */
    private String remark;


    public AfBusinessAccessRecordsDo() {
		super();
	}

    
	public AfBusinessAccessRecordsDo(Long userId, String sourceIp,
			String refType, Long refId, String extraInfo) {
		super();
		this.userId = userId;
		this.sourceIp = sourceIp;
		this.refType = refType;
		this.refId = refId;
		this.extraInfo = extraInfo;
	}


	/**
     * 获取主键Id
     *
     * @return id
     */
    public Long getId(){
      return id;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setId(Long id){
      this.id = id;
    }
    

    /**
     * 获取创建访问时间
     *
     * @return 创建访问时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建访问时间
     * 
     * @param gmtCreate 要设置的创建访问时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
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
     * 获取请求来源ip
     *
     * @return 请求来源ip
     */
    public String getSourceIp(){
      return sourceIp;
    }

    /**
     * 设置请求来源ip
     * 
     * @param sourceIp 要设置的请求来源ip
     */
    public void setSourceIp(String sourceIp){
      this.sourceIp = sourceIp;
    }

    /**
     * 获取关联业务类型【LOANSUPERMARKET:借款超市访问】
     *
     * @return 关联业务类型【LOANSUPERMARKET:借款超市访问】
     */
    public String getRefType(){
      return refType;
    }

    /**
     * 设置关联业务类型【LOANSUPERMARKET:借款超市访问】
     * 
     * @param refType 要设置的关联业务类型【LOANSUPERMARKET:借款超市访问】
     */
    public void setRefType(String refType){
      this.refType = refType;
    }

    /**
     * 获取关联业务对应id
     *
     * @return 关联业务对应id
     */
    public Long getRefId(){
      return refId;
    }

    /**
     * 设置关联业务对应id
     * 
     * @param refId 要设置的关联业务对应id
     */
    public void setRefId(Long refId){
      this.refId = refId;
    }

    /**
     * 获取额外信息
     *
     * @return 额外信息
     */
    public String getExtraInfo(){
      return extraInfo;
    }

    /**
     * 设置额外信息
     * 
     * @param extraInfo 要设置的额外信息
     */
    public void setExtraInfo(String extraInfo){
      this.extraInfo = extraInfo;
    }

    /**
     * 获取备注说明
     *
     * @return 备注说明
     */
    public String getRemark(){
      return remark;
    }

    /**
     * 设置备注说明
     * 
     * @param remark 要设置的备注说明
     */
    public void setRemark(String remark){
      this.remark = remark;
    }

}