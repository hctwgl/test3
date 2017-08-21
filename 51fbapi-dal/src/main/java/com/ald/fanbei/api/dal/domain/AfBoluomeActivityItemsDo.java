package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:20 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBoluomeActivityItemsDo extends AbstractSerial {

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
	 * 修改时间
	 */
	private Date gmtModified;

	/**
	 * 活动id【boluome_activity_id】
	 */
	private Long boluomeActivityId;

	/**
	 * 物件名称
	 */
	private String name;

	/**
	 * 物件icon地址
	 */
	private String iconUrl;

	/**
	 * 物件类型【CARD:卡片】
	 */
	private String type;

	/**
	 * 关联表ID type为CARD 则为商城id
	 */
	private Long refId;

	/**
	 * 排序 数值越大越靠前
	 */
	private Long sort;

	private String status;
	private String ruleJson;

	private int num;

	/**
	 * 获取主键Id
	 *
	 * @return id
	 */

	private String logo;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * 获取创建时间
	 *
	 * @return 创建时间
	 */
	public Date getGmtCreate() {
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
	 * @param gmtCreate
	 *            要设置的创建时间
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	/**
	 * 获取修改时间
	 *
	 * @return 修改时间
	 */
	public Date getGmtModified() {
		return gmtModified;
	}

	/**
	 * 设置修改时间
	 * 
	 * @param gmtModified
	 *            要设置的修改时间
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	/**
	 * 获取活动id【boluome_activity_id】
	 *
	 * @return 活动id【boluome_activity_id】
	 */
	public Long getBoluomeActivityId() {
		return boluomeActivityId;
	}

	/**
	 * 设置活动id【boluome_activity_id】
	 * 
	 * @param boluomeActivityId
	 *            要设置的活动id【boluome_activity_id】
	 */
	public void setBoluomeActivityId(Long boluomeActivityId) {
		this.boluomeActivityId = boluomeActivityId;
	}

	/**
	 * 获取物件名称
	 *
	 * @return 物件名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置物件名称
	 * 
	 * @param name
	 *            要设置的物件名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取物件icon地址
	 *
	 * @return 物件icon地址
	 */
	public String getIconUrl() {
		return iconUrl;
	}

	/**
	 * 设置物件icon地址
	 * 
	 * @param iconUrl
	 *            要设置的物件icon地址
	 */
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	/**
	 * 获取物件类型【CARD:卡片】
	 *
	 * @return 物件类型【CARD:卡片】
	 */
	public String getType() {
		return type;
	}

	/**
	 * 设置物件类型【CARD:卡片】
	 * 
	 * @param type
	 *            要设置的物件类型【CARD:卡片】
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 获取关联表ID type为CARD 则为商城id
	 *
	 * @return 关联表ID type为CARD 则为商城id
	 */
	public Long getRefId() {
		return refId;
	}

	/**
	 * 设置关联表ID type为CARD 则为商城id
	 * 
	 * @param refId
	 *            要设置的关联表ID type为CARD 则为商城id
	 */
	public void setRefId(Long refId) {
		this.refId = refId;
	}

	/**
	 * 获取排序 数值越大越靠前
	 *
	 * @return 排序 数值越大越靠前
	 */
	public Long getSort() {
		return sort;
	}

	/**
	 * 设置排序 数值越大越靠前
	 * 
	 * @param sort
	 *            要设置的排序 数值越大越靠前
	 */
	public void setSort(Long sort) {
		this.sort = sort;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRuleJson() {
		return ruleJson;
	}

	public void setRuleJson(String ruleJson) {
		this.ruleJson = ruleJson;
	}

}