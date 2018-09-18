package com.ald.jsd.mgr.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-18 10:34:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class MgrRoleDo extends AbstractSerial {

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
     * 类型  0:超级管理员 1:管理员
     */
    private Integer roleType;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String area;


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
     * 获取类型  0:超级管理员 1:管理员
     *
     * @return 类型  0:超级管理员 1:管理员
     */
    public Integer getRoleType(){
      return roleType;
    }

    /**
     * 设置类型  0:超级管理员 1:管理员
     * 
     * @param roleType 要设置的类型  0:超级管理员 1:管理员
     */
    public void setRoleType(Integer roleType){
      this.roleType = roleType;
    }

    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    public String getRoleName(){
      return roleName;
    }

    /**
     * 设置角色名称
     * 
     * @param roleName 要设置的角色名称
     */
    public void setRoleName(String roleName){
      this.roleName = roleName;
    }

    /**
     * 获取省
     *
     * @return 省
     */
    public String getProvince(){
      return province;
    }

    /**
     * 设置省
     * 
     * @param province 要设置的省
     */
    public void setProvince(String province){
      this.province = province;
    }

    /**
     * 获取市
     *
     * @return 市
     */
    public String getCity(){
      return city;
    }

    /**
     * 设置市
     * 
     * @param city 要设置的市
     */
    public void setCity(String city){
      this.city = city;
    }

    /**
     * 获取区
     *
     * @return 区
     */
    public String getArea(){
      return area;
    }

    /**
     * 设置区
     * 
     * @param area 要设置的区
     */
    public void setArea(String area){
      this.area = area;
    }

}