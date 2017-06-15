/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年6月6日下午1:46:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSchemeDo extends AbstractSerial {

	private static final long serialVersionUID = 7692174599987660595L;
	
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private String name;//优惠计划名称
	private String isOpen;//【N：禁用 Y：启用】
	private Long interestFreeId;//免息规则id
	private String tag;//标签名
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
	 * @return the isOpen
	 */
	public String getIsOpen() {
		return isOpen;
	}
	/**
	 * @param isOpen the isOpen to set
	 */
	public void setIsOpen(String isOpen) {
		this.isOpen = isOpen;
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
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	

}
