package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-13 20:41:39 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfPropertyValueDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键Id
	 */
	private Long rid;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

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
	 * 属性id
	 */
	private Long propertyId;

	/**
	 * 属性值
	 */
	private String value;

	/**
	 * 排序值,数值越大,越靠前
	 */
	private Long sort;

	/**
	 * 获取创建时间
	 *
	 * @return 创建时间
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}

	/**
	 * 设置创建时间
	 * 
	 * @param gmtCreate
	 *            要设置的创建时间
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * 获取最后修改时间
	 *
	 * @return 最后修改时间
	 */
	public Date getGmtModified() {
		return gmtModified;
	}

	/**
	 * 设置最后修改时间
	 * 
	 * @param gmtModified
	 *            要设置的最后修改时间
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	/**
	 * 获取创建者
	 *
	 * @return 创建者
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * 设置创建者
	 * 
	 * @param creator
	 *            要设置的创建者
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 获取最后修改者
	 *
	 * @return 最后修改者
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * 设置最后修改者
	 * 
	 * @param modifier
	 *            要设置的最后修改者
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 获取属性id
	 *
	 * @return 属性id
	 */
	public Long getPropertyId() {
		return propertyId;
	}

	/**
	 * 设置属性id
	 * 
	 * @param propertyId
	 *            要设置的属性id
	 */
	public void setPropertyId(Long propertyId) {
		this.propertyId = propertyId;
	}

	/**
	 * 获取属性值
	 *
	 * @return 属性值
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 设置属性值
	 * 
	 * @param value
	 *            要设置的属性值
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 获取排序值,数值越大,越靠前
	 *
	 * @return 排序值,数值越大,越靠前
	 */
	public Long getSort() {
		return sort;
	}

	/**
	 * 设置排序值,数值越大,越靠前
	 * 
	 * @param sort
	 *            要设置的排序值,数值越大,越靠前
	 */
	public void setSort(Long sort) {
		this.sort = sort;
	}

}