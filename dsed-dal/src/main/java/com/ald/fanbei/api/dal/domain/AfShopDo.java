package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月23日下午2:13:25
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfShopDo extends AbstractSerial {

	private static final long serialVersionUID = 290027174750143768L;
	
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String name;//商城名称
	private String description;//商城介绍
	private String type;//商城类型 【dianying电影, waimai外卖】
	private String shopUrl;//商城地址
	private String icon;//图片
	private String logo;//logo
	private String latestUseIcon;//最近使用图标
	private String shopActivity; //商城活动
	private BigDecimal commissionAmount;//平台返佣
	private String commissionUnit;//平台返佣单位【RMB人民币,PERCENTAGE百分比】
	private BigDecimal rebateAmount;//用户返利
	private String rebateUnit;//用户返利单位【RMB人民币,PERCENTAGE百分比】
	private String status;//状态【O开启,C关闭】
	private String platformName;//平台名称 BOLUOME
	private String serviceProvider;//供应商编号如抠电影【kou】
	private Long interestFreeId;//免息规则ID
	private Integer latestOnline;//最新上线开启状态 0:关闭 1:开启
	private String newIcon; //改版商城新图icon
	private String newLogo;//改版订单新图logo
	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
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
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the shopUrl
	 */
	public String getShopUrl() {
		return shopUrl;
	}
	/**
	 * @param shopUrl the shopUrl to set
	 */
	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}
	/**
	 * @param logo the logo to set
	 */
	public void setLogo(String logo) {
		this.logo = logo;
	}
	/**
	 * @return the commissionAmount
	 */
	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}
	/**
	 * @param commissionAmount the commissionAmount to set
	 */
	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}
	/**
	 * @return the commissionUnit
	 */
	public String getCommissionUnit() {
		return commissionUnit;
	}
	/**
	 * @param commissionUnit the commissionUnit to set
	 */
	public void setCommissionUnit(String commissionUnit) {
		this.commissionUnit = commissionUnit;
	}
	/**
	 * @return the rebateAmount
	 */
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	/**
	 * @param rebateAmount the rebateAmount to set
	 */
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	/**
	 * @return the rebateUnit
	 */
	public String getRebateUnit() {
		return rebateUnit;
	}
	/**
	 * @param rebateUnit the rebateUnit to set
	 */
	public void setRebateUnit(String rebateUnit) {
		this.rebateUnit = rebateUnit;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the platformName
	 */
	public String getPlatformName() {
		return platformName;
	}
	/**
	 * @param platformName the platformName to set
	 */
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	/**
	 * @return the serviceProvider
	 */
	public String getServiceProvider() {
		return serviceProvider;
	}
	/**
	 * @param serviceProvider the serviceProvider to set
	 */
	public void setServiceProvider(String serviceProvider) {
		this.serviceProvider = serviceProvider;
	}
	/**
	 * @return the interestFreeId
	 */
	public Long getInterestFreeId() {
		return interestFreeId;
	}
	/**
	 * @param interestFreeId the interestFreeId to set
	 */
	public void setInterestFreeId(Long interestFreeId) {
		this.interestFreeId = interestFreeId;
	}
	public String getLatestUseIcon() {
	    return latestUseIcon;
	}
	public void setLatestUseIcon(String latestUseIcon) {
	    this.latestUseIcon = latestUseIcon;
	}
	public String getShopActivity() {
	    return shopActivity;
	}
	public void setShopActivity(String shopActivity) {
	    this.shopActivity = shopActivity;
	}
	public Integer getLatestOnline() {
	    return latestOnline;
	}
	public void setLatestOnline(Integer latestOnline) {
	    this.latestOnline = latestOnline;
	}
	public String getNewIcon() {
	    return newIcon;
	}
	public void setNewIcon(String newIcon) {
	    this.newIcon = newIcon;
	}
	public String getNewLogo() {
	    return newLogo;
	}
	public void setNewLogo(String newLogo) {
	    this.newLogo = newLogo;
	}
	@Override
	public String toString() {
		return "AfShopDo [rid=" + rid + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + ", creator="
				+ creator + ", modifier=" + modifier + ", name=" + name + ", description=" + description + ", type="
				+ type + ", shopUrl=" + shopUrl + ", icon=" + icon + ", logo=" + logo + ", latestUseIcon="
				+ latestUseIcon + ", shopActivity=" + shopActivity + ", commissionAmount=" + commissionAmount
				+ ", commissionUnit=" + commissionUnit + ", rebateAmount=" + rebateAmount + ", rebateUnit=" + rebateUnit
				+ ", status=" + status + ", platformName=" + platformName + ", serviceProvider=" + serviceProvider
				+ ", interestFreeId=" + interestFreeId + ", latestOnline=" + latestOnline + ", newIcon=" + newIcon
				+ ", newLogo=" + newLogo + "]";
	}
	

}
