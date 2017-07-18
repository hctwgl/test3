package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类描述：
 *@author Xiaotianjian 2017年1月20日上午10:09:09
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfResourceDo extends AbstractSerial {
	
	private static final long serialVersionUID = -5566198924650170281L;
	
	private Long rid;//用户id
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String dataType;//配置类型，S:系统配置,B:业务配置
	private String type;//配置类型，即配置的KEY，用于定位配置；所有字母大写，多个字母中间用下划线“_”分割；如：用户白名单类型USER_WHITE_LIST
	private String typeDesc;//配置类型描述，如针对TYPE=USER_WHITE_LIST该值可描述为：用户白名单列表
	private String name;//名称
	private String value;//值
	private String description;//描述
	private String secType;//类型，可针对某一类型的配置做分类
	private String value1;//扩展值1
	private String value2;//扩展值2
	private String value3;//扩展值3
	private String value4;//扩展值4
	private Long sort;//排序

	
	/**
	 * @return the value4
	 */
	public String getValue4() {
		return value4;
	}
	/**
	 * @param value4 the value4 to set
	 */
	public void setValue4(String value4) {
		this.value4 = value4;
	}
	
	/**
	 * @return the sort
	 */
	public Long getSort() {
		return sort;
	}
	/**
	 * @param sort the sort to set
	 */
	public void setSort(Long sort) {
		this.sort = sort;
	}
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeDesc() {
		return typeDesc;
	}
	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSecType() {
		return secType;
	}
	public void setSecType(String secType) {
		this.secType = secType;
	}
	public String getValue1() {
		return value1;
	}
	public void setValue1(String value1) {
		this.value1 = value1;
	}
	public String getValue2() {
		return value2;
	}
	public void setValue2(String value2) {
		this.value2 = value2;
	}
	public String getValue3() {
		return value3;
	}
	public void setValue3(String value3) {
		this.value3 = value3;
	}



}
