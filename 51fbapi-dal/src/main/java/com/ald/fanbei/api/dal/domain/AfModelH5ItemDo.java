/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年2月23日下午10:24:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfModelH5ItemDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer rid;
	private Date gmtCreate;
	private Long modelId;//模板id
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String modelType;//在模板中的类型 如：【BANNELl,SUBJECT,CATEGORY,IMAGE,GOODSLIST】
	private String itemIcon;//图标
	private String itemType;//品牌项的类型，如【URL,GOODS】
	private String itemValue;//品牌项值
	private Integer sort;//排序
	private Integer secSort;//排序
	/**
	 * @return the rid
	 */
	public Integer getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Integer rid) {
		this.rid = rid;
	}
	/**
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	/**
	 * @return the modelId
	 */
	public Long getModelId() {
		return modelId;
	}
	/**
	 * @param modelId the modelId to set
	 */
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	/**
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	/**
	 * @return the modelType
	 */
	public String getModelType() {
		return modelType;
	}
	/**
	 * @param modelType the modelType to set
	 */
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	/**
	 * @return the itemIcon
	 */
	public String getItemIcon() {
		return itemIcon;
	}
	/**
	 * @param itemIcon the itemIcon to set
	 */
	public void setItemIcon(String itemIcon) {
		this.itemIcon = itemIcon;
	}
	/**
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	/**
	 * @return the itemValue
	 */
	public String getItemValue() {
		return itemValue;
	}
	/**
	 * @param itemValue the itemValue to set
	 */
	public void setItemValue(String itemValue) {
		this.itemValue = itemValue;
	}
	/**
	 * @return the sort
	 */
	public Integer getSort() {
		return sort;
	}
	/**
	 * @param sort the sort to set
	 */
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	/**
	 * @return the secSort
	 */
	public Integer getSecSort() {
		return secSort;
	}
	/**
	 * @param secSort the secSort to set
	 */
	public void setSecSort(Integer secSort) {
		this.secSort = secSort;
	}
	/**
	 * @return the creator
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * @return the modifier
	 */
	public String getModifier() {
		return modifier;
	}
	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	
	
}
