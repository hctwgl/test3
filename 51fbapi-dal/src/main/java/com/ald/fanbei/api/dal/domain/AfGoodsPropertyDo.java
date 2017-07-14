package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-13 20:40:57 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsPropertyDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键Id
	 */
	private Long rid;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

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
	 * 商品id
	 */
	private Long goodsId;

	/**
	 * 属性Id
	 */
	private String name;

	/**
	 * 排序,数字越大越靠前排序
	 */
	private Long sort;

	/**
	 * 获取创建时间
	 *
	 * @return 创建时间
	 */
	public Date getGmtCreate() {
		return gmtCreate;
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
	 * 获取最后修改时间
	 *
	 * @return 最后修改时间
	 */
	public Date getGmtModified() {
		return gmtModified;
	}

	/**
	 * 设置最后修改时间
	 * 
	 * @param gmtModified
	 *            要设置的最后修改时间
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	/**
	 * 获取创建者
	 *
	 * @return 创建者
	 */
	public String getCreator() {
		return creator;
	}

	/**
	 * 设置创建者
	 * 
	 * @param creator
	 *            要设置的创建者
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 获取最后修改者
	 *
	 * @return 最后修改者
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * 设置最后修改者
	 * 
	 * @param modifier
	 *            要设置的最后修改者
	 */
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	/**
	 * 获取商品id
	 *
	 * @return 商品id
	 */
	public Long getGoodsId() {
		return goodsId;
	}

	/**
	 * 设置商品id
	 * 
	 * @param goodsId
	 *            要设置的商品id
	 */
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	/**
	 * 获取属性Id
	 *
	 * @return 属性Id
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置属性Id
	 * 
	 * @param name
	 *            要设置的属性Id
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取排序,数字越大越靠前排序
	 *
	 * @return 排序,数字越大越靠前排序
	 */
	public Long getSort() {
		return sort;
	}

	/**
	 * 设置排序,数字越大越靠前排序
	 * 
	 * @param sort
	 *            要设置的排序,数字越大越靠前排序
	 */
	public void setSort(Long sort) {
		this.sort = sort;
	}

}