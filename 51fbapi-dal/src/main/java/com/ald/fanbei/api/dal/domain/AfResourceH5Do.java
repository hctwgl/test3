package com.ald.fanbei.api.dal.domain;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * h5资源管理实体
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:39:09
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfResourceH5Do extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 最后修改者
     */
    private String modifier;

    /**
     * 模板名称, H5页面名称
     */
    private String name;

    /**
     * 页面跳转地址
     */
    private String url;

    /**
     * 启用状态 0启用 1关闭
     */
    private Long status;
    private Long sort;
	/**
	 * 
	 */
	public AfResourceH5Do() {
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
	public AfResourceH5Do(Long id, Date gmtCreate, Date gmtModified,
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
	
	
    


	
 
}