package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.biz.bo.FacePlusCardRespBo;


/**
 * 
 * @类描述：face++信息返回值
 * @author xiaotianjian 2017年7月23日下午10:53:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FacePlusCardVo{

	private String address;//住址
	private String birthday;//生日，下分年月日，都是一个字符串
	private String gender;//性别（男/女）
	private String citizen_id;//身份证号
	private String name;//姓名
	private String nation;//民族(汉字)
	private String agency;//签发机关
	private String valid_date_begin;//开始有效日期
	private String valid_date_end;//截止有效日期
	
	public FacePlusCardVo(FacePlusCardRespBo bo) {
		this.address = bo.getAddress();
		this.birthday = bo.getRealBirthDay();
		this.gender = bo.getGender();
		this.citizen_id = bo.getId_card_number();
		this.name = bo.getName();
		this.nation = bo.getRace();
		this.agency = bo.getIssued_by();
		this.valid_date_begin = bo.getValid_date_begin();
		this.valid_date_end = bo.getValid_date_end();
		
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
	 * @return the citizen_id
	 */
	public String getCitizen_id() {
		return citizen_id;
	}
	/**
	 * @param citizen_id the citizen_id to set
	 */
	public void setCitizen_id(String citizen_id) {
		this.citizen_id = citizen_id;
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
	 * @return the valid_date_begin
	 */
	public String getValid_date_begin() {
		return valid_date_begin;
	}
	/**
	 * @param valid_date_begin the valid_date_begin to set
	 */
	public void setValid_date_begin(String valid_date_begin) {
		this.valid_date_begin = valid_date_begin;
	}
	/**
	 * @return the valid_date_end
	 */
	public String getValid_date_end() {
		return valid_date_end;
	}
	/**
	 * @param valid_date_end the valid_date_end to set
	 */
	public void setValid_date_end(String valid_date_end) {
		this.valid_date_end = valid_date_end;
	}
	
	

	
}
