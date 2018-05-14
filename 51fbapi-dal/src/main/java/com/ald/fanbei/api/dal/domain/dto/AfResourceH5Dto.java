package com.ald.fanbei.api.dal.domain.dto;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import lombok.Getter;
import lombok.Setter;


/**
 * @author Jingru
 * @version 创建时间：2018年3月21日 下午5:39:58
 */
@Setter
@Getter
public class AfResourceH5Dto extends AfResourceH5Do{

private static final long serialVersionUID = -9058221367468975525L;

	private List itemList;

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
