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

	/**
	 * 主键Id
	 */
	private Long rid;


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
	 * 排序
	 */
	private Integer sort;
	/**
	 * 启用状态 0启用 1关闭
	 */
	private Long status;
	private String tag;


}
