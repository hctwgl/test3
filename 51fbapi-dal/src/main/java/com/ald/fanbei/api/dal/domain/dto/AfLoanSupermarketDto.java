/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @类描述：
 * @author chefeipeng 2017年6月21日下午2:23:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLoanSupermarketDto extends AfLoanSupermarketDo {




	private Long id;
	private String lsmNo;
	private String iconUrl;
	private Date gmtCreate;
	private Date gmtModified;
	private Long createUserId;
	private Long modifyUserId;
	private String lsmName;
	private String lsmIntro;
	private String linkUrl;
	private String label;
	private Integer orderNo;
	private Integer status;
	private Integer isDelete;
	private String marketPoint;
	private String remake;

	public String getMarketPoint() {
		return marketPoint;
	}

	public void setMarketPoint(String marketPoint) {
		this.marketPoint = marketPoint;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getLsmNo() {
		return lsmNo;
	}
	public void setLsmNo(String lsmNo) {
		this.lsmNo = lsmNo;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public Long getModifyUserId() {
		return modifyUserId;
	}
	public void setModifyUserId(Long modifyUserId) {
		this.modifyUserId = modifyUserId;
	}
	public String getLsmName() {
		return lsmName;
	}
	public void setLsmName(String lsmName) {
		this.lsmName = lsmName;
	}
	public String getLsmIntro() {
		return lsmIntro;
	}
	public void setLsmIntro(String lsmIntro) {
		this.lsmIntro = lsmIntro;
	}
	public String getLinkUrl() {
		return linkUrl;
	}
	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	public String getRemake() {
		return remake;
	}
	public void setRemake(String remake) {
		this.remake = remake;
	}

	
}
