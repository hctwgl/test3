package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfResourceH5Do;

/**
 * @author Jingru
 * @version 创建时间：2018年3月22日 下午3:14:29
 * @Description 类描述
 */
public class ResourceH5ItemQuery extends Page<AfResourceH5Do>{
	private static final long serialVersionUID = 1901653005377600830L;
	
	private Long resourceId;
	
	private String type;
	
	private String goodsName;
	
	private String categoryName;
	private Long sort;

	/**
	 * @return the resourceId
	 */
	public Long getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the tag
	 */
	public Long getSort() {
		return sort;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setSort(String tag) {
		this.sort = sort;
	}

	public String getCategoryName() {
		
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	/**
	 * @return the resourceId
	 */
	public Long getresourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setresourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
