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
 public class MgrRoleMenuDo extends AbstractSerial {

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
     * 角色id
     */
    private Long roleId;

    /**
     * 角色类型
     */
    private Integer roleType;

    /**
     * 菜单id
     */
    private Long menuId;


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
     * 获取角色id
     *
     * @return 角色id
     */
    public Long getRoleId(){
      return roleId;
    }

    /**
     * 设置角色id
     * 
     * @param roleId 要设置的角色id
     */
    public void setRoleId(Long roleId){
      this.roleId = roleId;
    }

    /**
     * 获取角色类型
     *
     * @return 角色类型
     */
    public Integer getRoleType(){
      return roleType;
    }

    /**
     * 设置角色类型
     * 
     * @param roleType 要设置的角色类型
     */
    public void setRoleType(Integer roleType){
      this.roleType = roleType;
    }

    /**
     * 获取菜单id
     *
     * @return 菜单id
     */
    public Long getMenuId(){
      return menuId;
    }

    /**
     * 设置菜单id
     * 
     * @param menuId 要设置的菜单id
     */
    public void setMenuId(Long menuId){
      this.menuId = menuId;
    }

}