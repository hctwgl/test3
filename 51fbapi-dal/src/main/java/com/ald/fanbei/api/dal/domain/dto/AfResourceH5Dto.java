package com.ald.fanbei.api.dal.domain.dto;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfResourceH5Do;



/**
 * @author Jingru
 * @version 创建时间：2018年3月21日 下午5:39:58
 */
public class AfResourceH5Dto extends AfResourceH5Do{

private static final long serialVersionUID = -9058221367468975525L;

private Long id;

private Date gmtCreate;

private Date gmtModified;

private String creator;

private String modifier;

private String name;

private String url;

private Long status;
private Long sort;

/**
 * @return the id
 */
public Long getId() {
	return id;
}

/**
 * @param id the id to set
 */
public void setId(Long id) {
	this.id = id;
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
 * @return the url
 */
public String getUrl() {
	return url;
}

/**
 * @param url the url to set
 */
public void setUrl(String url) {
	this.url = url;
}


/**
 * @return the status
 */
public Long getStatus() {
	return status;
}

/**
 * @param status the status to set
 */
public void setStatus(Long status) {
	this.status = status;
}

/**
 * 
 */
public AfResourceH5Dto() {
	super();
	// TODO Auto-generated constructor stub
}

/**
 * @param id
 * @param gmtCreate
 * @param gmtModified
 * @param creator
 * @param modifier
 * @param name
 * @param url
 * @param status
 * @param sort
 */
public AfResourceH5Dto(Long id, Date gmtCreate, Date gmtModified,
		String creator, String modifier, String name, String url,
		Long status, Long sort) {
	super();
	this.id = id;
	this.gmtCreate = gmtCreate;
	this.gmtModified = gmtModified;
	this.creator = creator;
	this.modifier = modifier;
	this.name = name;
	this.url = url;
	this.status = status;
	this.sort = sort;
}

public void setItemList(List<AfResourceH5ItemDto> itemList) {
	// TODO Auto-generated method stub
	
}

}
