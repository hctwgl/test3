package com.ald.fanbei.api.dal.domain;

import java.util.Date;


import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author chefeipeng 2017年9月5日下午16:56:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfPopupsDo extends AbstractSerial {

	private Long id;
	
	private String isDelete;
	
	private String creator;
	
	private String modifier;
	
	private Date gmtCreate;
	
	private Date gmtModified;
	
	private String userType;
	
	private Integer clickAmount;

	private String url;
	
	private String imgUrl;
	
	private String name;
	
	private String type;
	
	private String flag;

	public Long getId() {
		return id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Integer getClickAmount() {
		return clickAmount;
	}

	public void setClickAmount(Integer clickAmount) {
		this.clickAmount = clickAmount;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	

}
