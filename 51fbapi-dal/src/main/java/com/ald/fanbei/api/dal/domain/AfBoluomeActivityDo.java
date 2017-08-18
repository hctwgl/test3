package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:35:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBoluomeActivityDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
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
     * 创建者
     */
    private String creator;

    /**
     * 最后修改者
     */
    private String modifier;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 状态：【O:Open 关闭 C:Close：开启】
     */
    private String status;

    /**
     * 活动规则描述
     */
    private String description;

    /**
     * 活动开始时间
     */
    private Date gmtStart;

    /**
     * 活动结束时间
     */
    private Date gmtEnd;


    /**
     * 获取主键Id
     *
     * @return id
     */

    

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
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
     * 获取创建者
     *
     * @return 创建者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建者
     * 
     * @param creator 要设置的创建者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取最后修改者
     *
     * @return 最后修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置最后修改者
     * 
     * @param modifier 要设置的最后修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取活动名称
     *
     * @return 活动名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置活动名称
     * 
     * @param name 要设置的活动名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取状态：【O:Open 关闭 C:Close：开启】
     *
     * @return 状态：【O:Open 关闭 C:Close：开启】
     */
    public String getStatus(){
      return status;
    }

    /**
     * 设置状态：【O:Open 关闭 C:Close：开启】
     * 
     * @param status 要设置的状态：【O:Open 关闭 C:Close：开启】
     */
    public void setStatus(String status){
      this.status = status;
    }

    /**
     * 获取活动规则描述
     *
     * @return 活动规则描述
     */
    public String getDescription(){
      return description;
    }

    /**
     * 设置活动规则描述
     * 
     * @param description 要设置的活动规则描述
     */
    public void setDescription(String description){
      this.description = description;
    }

    /**
     * 获取活动开始时间
     *
     * @return 活动开始时间
     */
    public Date getGmtStart(){
      return gmtStart;
    }

    /**
     * 设置活动开始时间
     * 
     * @param gmtStart 要设置的活动开始时间
     */
    public void setGmtStart(Date gmtStart){
      this.gmtStart = gmtStart;
    }

    /**
     * 获取活动结束时间
     *
     * @return 活动结束时间
     */
    public Date getGmtEnd(){
      return gmtEnd;
    }

    /**
     * 设置活动结束时间
     * 
     * @param gmtEnd 要设置的活动结束时间
     */
    public void setGmtEnd(Date gmtEnd){
      this.gmtEnd = gmtEnd;
    }

}