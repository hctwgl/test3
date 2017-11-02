/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年5月8日下午3:19:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfResourceLogDo extends AbstractSerial {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String type;
	private String secType;
	private String oldJson;
	private String modifyJson;

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
	 * @return the secType
	 */
	public String getSecType() {
		return secType;
	}
	/**
	 * @param secType the secType to set
	 */
	public void setSecType(String secType) {
		this.secType = secType;
	}
	/**
	 * @return the oldJson
	 */
	public String getOldJson() {
		return oldJson;
	}
	/**
	 * @param oldJson the oldJson to set
	 */
	public void setOldJson(String oldJson) {
		this.oldJson = oldJson;
	}
	/**
	 * @return the modifyJson
	 */
	public String getModifyJson() {
		return modifyJson;
	}
	/**
	 * @param modifyJson the modifyJson to set
	 */
	public void setModifyJson(String modifyJson) {
		this.modifyJson = modifyJson;
	}
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
	
	
}
