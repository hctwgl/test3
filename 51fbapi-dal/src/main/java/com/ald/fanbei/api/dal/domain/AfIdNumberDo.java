/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午6:31:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfIdNumberDo extends AbstractSerial {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;
	
	private Long userId;
	private String address;
	private String citizenId;
	private String gender;
	private String nation;
	private String name;
	private String validDateBegin;
	private String validDateEnd;
	private String birthday;
	private String agency;
	private String idFrontUrl;
	private String idBehindUrl;
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
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the citizenId
	 */
	public String getCitizenId() {
		return citizenId;
	}
	/**
	 * @param citizenId the citizenId to set
	 */
	public void setCitizenId(String citizenId) {
		this.citizenId = citizenId;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * @return the nation
	 */
	public String getNation() {
		return nation;
	}
	/**
	 * @param nation the nation to set
	 */
	public void setNation(String nation) {
		this.nation = nation;
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
	 * @return the validDateBegin
	 */
	public String getValidDateBegin() {
		return validDateBegin;
	}
	/**
	 * @param validDateBegin the validDateBegin to set
	 */
	public void setValidDateBegin(String validDateBegin) {
		this.validDateBegin = validDateBegin;
	}
	/**
	 * @return the validDateEnd
	 */
	public String getValidDateEnd() {
		return validDateEnd;
	}
	/**
	 * @param validDateEnd the validDateEnd to set
	 */
	public void setValidDateEnd(String validDateEnd) {
		this.validDateEnd = validDateEnd;
	}
	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}
	/**
	 * @param birthday the birthday to set
	 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	/**
	 * @return the agency
	 */
	public String getAgency() {
		return agency;
	}
	/**
	 * @param agency the agency to set
	 */
	public void setAgency(String agency) {
		this.agency = agency;
	}
	/**
	 * @return the idFrontUrl
	 */
	public String getIdFrontUrl() {
		return idFrontUrl;
	}
	/**
	 * @param idFrontUrl the idFrontUrl to set
	 */
	public void setIdFrontUrl(String idFrontUrl) {
		this.idFrontUrl = idFrontUrl;
	}
	/**
	 * @return the idBehindUrl
	 */
	public String getIdBehindUrl() {
		return idBehindUrl;
	}
	/**
	 * @param idBehindUrl the idBehindUrl to set
	 */
	public void setIdBehindUrl(String idBehindUrl) {
		this.idBehindUrl = idBehindUrl;
	}
	
	
}
