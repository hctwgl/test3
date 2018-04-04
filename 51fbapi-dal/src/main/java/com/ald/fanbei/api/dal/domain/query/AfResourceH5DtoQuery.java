package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;


/**
 * @author Jingru
 * @version 创建时间：2018年3月22日 上午10:00:51
 * @Description 类描述
 */
public class AfResourceH5DtoQuery extends Page<AfResourceH5Dto> {

	private static final long serialVersionUID = 1901653005377600830L;
	
	private String name;//页面名称

	private String type;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	

}
