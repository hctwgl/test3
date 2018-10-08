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
 public class MgrMenuDo extends AbstractSerial {

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
     * 父菜单id
     */
    private Long parentId;

    /**
     * 类型   1:系统 2：父菜单  3：菜单（叶子节点）
     */
    private Integer type;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 描述
     */
    private String menuDesc;

    /**
     * 菜单地址
     */
    private String menuUrl;

    /**
     * 菜单图标
     */
    private String menuIcon;

    /**
     * 是否展示
     */
    private Integer isShow;

    /**
     * 排序
     */
    private Integer sort;


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
     * 获取父菜单id
     *
     * @return 父菜单id
     */
    public Long getParentId(){
      return parentId;
    }

    /**
     * 设置父菜单id
     * 
     * @param parentId 要设置的父菜单id
     */
    public void setParentId(Long parentId){
      this.parentId = parentId;
    }

    /**
     * 获取类型   1:系统 2：父菜单  3：菜单（叶子节点）
     *
     * @return 类型   1:系统 2：父菜单  3：菜单（叶子节点）
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置类型   1:系统 2：父菜单  3：菜单（叶子节点）
     * 
     * @param type 要设置的类型   1:系统 2：父菜单  3：菜单（叶子节点）
     */
    public void setType(Integer type){
      this.type = type;
    }

    /**
     * 获取菜单名称
     *
     * @return 菜单名称
     */
    public String getMenuName(){
      return menuName;
    }

    /**
     * 设置菜单名称
     * 
     * @param menuName 要设置的菜单名称
     */
    public void setMenuName(String menuName){
      this.menuName = menuName;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getMenuDesc(){
      return menuDesc;
    }

    /**
     * 设置描述
     * 
     * @param menuDesc 要设置的描述
     */
    public void setMenuDesc(String menuDesc){
      this.menuDesc = menuDesc;
    }

    /**
     * 获取菜单地址
     *
     * @return 菜单地址
     */
    public String getMenuUrl(){
      return menuUrl;
    }

    /**
     * 设置菜单地址
     * 
     * @param menuUrl 要设置的菜单地址
     */
    public void setMenuUrl(String menuUrl){
      this.menuUrl = menuUrl;
    }

    /**
     * 获取菜单图标
     *
     * @return 菜单图标
     */
    public String getMenuIcon(){
      return menuIcon;
    }

    /**
     * 设置菜单图标
     * 
     * @param menuIcon 要设置的菜单图标
     */
    public void setMenuIcon(String menuIcon){
      this.menuIcon = menuIcon;
    }

    /**
     * 获取是否展示
     *
     * @return 是否展示
     */
    public Integer getIsShow(){
      return isShow;
    }

    /**
     * 设置是否展示
     * 
     * @param isShow 要设置的是否展示
     */
    public void setIsShow(Integer isShow){
      this.isShow = isShow;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序
     * 
     * @param sort 要设置的排序
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

}