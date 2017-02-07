package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午1:41:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfActivityDo extends AbstractSerial{
	
	private static final long serialVersionUID = 8443117928246080930L;
	
	private Date gmtEnd;
	private String status;
	private Date gmtModified;
	private String type;
	private Date gmtStart;
	private String creator;
	private Integer rid;
	private String modifier;
	private Integer quota;
	private Date gmtCreate;
	private String ruleJson;
	private String description;
	private String name;
	private Integer quotaAlready;
	private String h5Url;
	public Date getGmtEnd() {
		return gmtEnd;
	}
	public void setGmtEnd(Date gmtEnd) {
		this.gmtEnd = gmtEnd;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getGmtStart() {
		return gmtStart;
	}
	public void setGmtStart(Date gmtStart) {
		this.gmtStart = gmtStart;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Integer getRid() {
		return rid;
	}
	public void setRid(Integer rid) {
		this.rid = rid;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public Integer getQuota() {
		return quota;
	}
	public void setQuota(Integer quota) {
		this.quota = quota;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getRuleJson() {
		return ruleJson;
	}
	public void setRuleJson(String ruleJson) {
		this.ruleJson = ruleJson;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getQuotaAlready() {
		return quotaAlready;
	}
	public void setQuotaAlready(Integer quotaAlready) {
		this.quotaAlready = quotaAlready;
	}
	public String getH5Url() {
		return h5Url;
	}
	public void setH5Url(String h5Url) {
		this.h5Url = h5Url;
	}
	
	
}
