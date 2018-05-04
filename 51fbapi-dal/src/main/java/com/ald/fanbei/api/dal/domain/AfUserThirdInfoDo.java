package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-04 09:20:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfUserThirdInfoDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户在第三方的id
     */
    private String thirdId;

    /**
     * 第三方类型，WX：微信
     */
    private String thirdType;

    /**
     * 第三方信息
     */
    private String thirdInfo;


    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 添加者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;


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
     * 获取用户在第三方的id
     *
     * @return 用户在第三方的id
     */
    public String getThirdId(){
      return thirdId;
    }

    /**
     * 设置用户在第三方的id
     * 
     * @param thirdId 要设置的用户在第三方的id
     */
    public void setThirdId(String thirdId){
      this.thirdId = thirdId;
    }

    /**
     * 获取第三方类型，WX：微信
     *
     * @return 第三方类型，WX：微信
     */
    public String getThirdType(){
      return thirdType;
    }

    /**
     * 设置第三方类型，WX：微信
     * 
     * @param thirdType 要设置的第三方类型，WX：微信
     */
    public void setThirdType(String thirdType){
      this.thirdType = thirdType;
    }

    /**
     * 获取第三方信息
     *
     * @return 第三方信息
     */
    public String getThirdInfo(){
      return thirdInfo;
    }

    /**
     * 设置第三方信息
     * 
     * @param thirdInfo 要设置的第三方信息
     */
    public void setThirdInfo(String thirdInfo){
      this.thirdInfo = thirdInfo;
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
     * 获取修改时间
     *
     * @return 修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置修改时间
     * 
     * @param gmtModified 要设置的修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取添加者
     *
     * @return 添加者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置添加者
     * 
     * @param creator 要设置的添加者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取修改者
     *
     * @return 修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置修改者
     * 
     * @param modifier 要设置的修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

}