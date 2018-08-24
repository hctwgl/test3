package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 极速贷资源配置实体
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-24 10:37:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class JsdResourceDo extends AbstractSerial {

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
     * 配置类型，S:系统配置,B:业务配置
     */
    private String dataType;

    /**
     * 配置类型，即配置的KEY，用于定位配置；所有字母大写，多个字母中间用下划线“_”分割；如：用户白名单类型USER_WHITE_LIST
     */
    private String type;

    /**
     * 配置类型描述，如针对TYPE=USER_WHITE_LIST该值可描述为：用户白名单列表
     */
    private String typeDesc;

    /**
     * 名称
     */
    private String name;

    /**
     * 
     */
    private String value;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型，可针对某一类型的配置做分类
     */
    private String secType;

    /**
     * 扩展值1
     */
    private String value1;

    /**
     * 扩展值2
     */
    private String value2;

    /**
     * 扩展值3
     */
    private String value3;

    /**
     * 扩展值4
     */
    private String value4;

    /**
     * 扩展值5
     */
    private String value5;

    /**
     * 资源图片1
     */
    private String pic1;

    /**
     * 资源图片2
     */
    private String pic2;

    /**
     * 
     */
    private Integer sort;

    /**
     * 有效开始时间
     */
    private Date effectiveStartTime;

    /**
     * 有效结束时间
     */
    private Date effectiveEndTime;

    /**
     * 是否可用，1：不可用，0：有效
     */
    private Integer isEnable;

    /**
     * 
     */
    private String env;


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
     * 获取配置类型，S:系统配置,B:业务配置
     *
     * @return 配置类型，S:系统配置,B:业务配置
     */
    public String getDataType(){
      return dataType;
    }

    /**
     * 设置配置类型，S:系统配置,B:业务配置
     * 
     * @param dataType 要设置的配置类型，S:系统配置,B:业务配置
     */
    public void setDataType(String dataType){
      this.dataType = dataType;
    }

    /**
     * 获取配置类型，即配置的KEY，用于定位配置；所有字母大写，多个字母中间用下划线“_”分割；如：用户白名单类型USER_WHITE_LIST
     *
     * @return 配置类型，即配置的KEY，用于定位配置；所有字母大写，多个字母中间用下划线“_”分割；如：用户白名单类型USER_WHITE_LIST
     */
    public String getType(){
      return type;
    }

    /**
     * 设置配置类型，即配置的KEY，用于定位配置；所有字母大写，多个字母中间用下划线“_”分割；如：用户白名单类型USER_WHITE_LIST
     * 
     * @param type 要设置的配置类型，即配置的KEY，用于定位配置；所有字母大写，多个字母中间用下划线“_”分割；如：用户白名单类型USER_WHITE_LIST
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取配置类型描述，如针对TYPE=USER_WHITE_LIST该值可描述为：用户白名单列表
     *
     * @return 配置类型描述，如针对TYPE=USER_WHITE_LIST该值可描述为：用户白名单列表
     */
    public String getTypeDesc(){
      return typeDesc;
    }

    /**
     * 设置配置类型描述，如针对TYPE=USER_WHITE_LIST该值可描述为：用户白名单列表
     * 
     * @param typeDesc 要设置的配置类型描述，如针对TYPE=USER_WHITE_LIST该值可描述为：用户白名单列表
     */
    public void setTypeDesc(String typeDesc){
      this.typeDesc = typeDesc;
    }

    /**
     * 获取名称
     *
     * @return 名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置名称
     * 
     * @param name 要设置的名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getValue(){
      return value;
    }

    /**
     * 设置
     * 
     * @param value 要设置的
     */
    public void setValue(String value){
      this.value = value;
    }

    /**
     * 获取描述
     *
     * @return 描述
     */
    public String getDescription(){
      return description;
    }

    /**
     * 设置描述
     * 
     * @param description 要设置的描述
     */
    public void setDescription(String description){
      this.description = description;
    }

    /**
     * 获取类型，可针对某一类型的配置做分类
     *
     * @return 类型，可针对某一类型的配置做分类
     */
    public String getSecType(){
      return secType;
    }

    /**
     * 设置类型，可针对某一类型的配置做分类
     * 
     * @param secType 要设置的类型，可针对某一类型的配置做分类
     */
    public void setSecType(String secType){
      this.secType = secType;
    }

    /**
     * 获取扩展值1
     *
     * @return 扩展值1
     */
    public String getValue1(){
      return value1;
    }

    /**
     * 设置扩展值1
     * 
     * @param value1 要设置的扩展值1
     */
    public void setValue1(String value1){
      this.value1 = value1;
    }

    /**
     * 获取扩展值2
     *
     * @return 扩展值2
     */
    public String getValue2(){
      return value2;
    }

    /**
     * 设置扩展值2
     * 
     * @param value2 要设置的扩展值2
     */
    public void setValue2(String value2){
      this.value2 = value2;
    }

    /**
     * 获取扩展值3
     *
     * @return 扩展值3
     */
    public String getValue3(){
      return value3;
    }

    /**
     * 设置扩展值3
     * 
     * @param value3 要设置的扩展值3
     */
    public void setValue3(String value3){
      this.value3 = value3;
    }

    /**
     * 获取扩展值4
     *
     * @return 扩展值4
     */
    public String getValue4(){
      return value4;
    }

    /**
     * 设置扩展值4
     * 
     * @param value4 要设置的扩展值4
     */
    public void setValue4(String value4){
      this.value4 = value4;
    }

    /**
     * 获取扩展值5
     *
     * @return 扩展值5
     */
    public String getValue5(){
      return value5;
    }

    /**
     * 设置扩展值5
     * 
     * @param value5 要设置的扩展值5
     */
    public void setValue5(String value5){
      this.value5 = value5;
    }

    /**
     * 获取资源图片1
     *
     * @return 资源图片1
     */
    public String getPic1(){
      return pic1;
    }

    /**
     * 设置资源图片1
     * 
     * @param pic1 要设置的资源图片1
     */
    public void setPic1(String pic1){
      this.pic1 = pic1;
    }

    /**
     * 获取资源图片2
     *
     * @return 资源图片2
     */
    public String getPic2(){
      return pic2;
    }

    /**
     * 设置资源图片2
     * 
     * @param pic2 要设置的资源图片2
     */
    public void setPic2(String pic2){
      this.pic2 = pic2;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置
     * 
     * @param sort 要设置的
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

    /**
     * 获取有效开始时间
     *
     * @return 有效开始时间
     */
    public Date getEffectiveStartTime(){
      return effectiveStartTime;
    }

    /**
     * 设置有效开始时间
     * 
     * @param effectiveStartTime 要设置的有效开始时间
     */
    public void setEffectiveStartTime(Date effectiveStartTime){
      this.effectiveStartTime = effectiveStartTime;
    }

    /**
     * 获取有效结束时间
     *
     * @return 有效结束时间
     */
    public Date getEffectiveEndTime(){
      return effectiveEndTime;
    }

    /**
     * 设置有效结束时间
     * 
     * @param effectiveEndTime 要设置的有效结束时间
     */
    public void setEffectiveEndTime(Date effectiveEndTime){
      this.effectiveEndTime = effectiveEndTime;
    }

    /**
     * 获取是否可用，1：不可用，0：有效
     *
     * @return 是否可用，1：不可用，0：有效
     */
    public Integer getIsEnable(){
      return isEnable;
    }

    /**
     * 设置是否可用，1：不可用，0：有效
     * 
     * @param isEnable 要设置的是否可用，1：不可用，0：有效
     */
    public void setIsEnable(Integer isEnable){
      this.isEnable = isEnable;
    }

    /**
     * 获取
     *
     * @return 
     */
    public String getEnv(){
      return env;
    }

    /**
     * 设置
     * 
     * @param env 要设置的
     */
    public void setEnv(String env){
      this.env = env;
    }

}