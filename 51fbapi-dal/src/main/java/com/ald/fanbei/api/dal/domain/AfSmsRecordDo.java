package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：短信记录信息
 * @author Xiaotianjian 2017年1月19日下午4:04:13
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSmsRecordDo extends AbstractSerial{
	
	private static final long serialVersionUID = 277018179977655299L;
	
	private Integer rid;
	private Date gmtCreate;
	private Integer templateId;
	private Long userId;//用户id
	private Date gmtModified;
	private String params;//参数，多个逗号隔开
	private String type;//验证码类型 【R.regist注册验证码 F.forget忘记密码】
	private String mobile;//发送手机号码
	private Integer isCheck;//是否已经验证,0:未验证 1：已经验证
	private String result;//返回结果
	
	public Integer getRid() {
		return rid;
	}
	public void setRid(Integer rid) {
		this.rid = rid;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Integer getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Date getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getIsCheck() {
		return isCheck;
	}
	public void setIsCheck(Integer isCheck) {
		this.isCheck = isCheck;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
