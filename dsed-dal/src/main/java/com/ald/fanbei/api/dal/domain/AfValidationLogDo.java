package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 
 * @类描述：用户相关信息
 * @author chefeipeng 2017年9月22日下午4:04:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfValidationLogDo extends AbstractSerial {
	private static final long serialVersionUID = 3700535013696929582L;
	
	private Long rid;
	private Date gmtCreate;
	private Long userId;
	private String type;
	private String result;
	
	public AfValidationLogDo(Long userId, String type, String result) {
		this.userId = userId;
		this.type = type;
		this.result = result;
	}
	
	public AfValidationLogDo() {}
	
	public enum AfValidationLogType{
		NULL,
		PAY_PWD,
		ID_CARD;
	}

	public Long getUserId() {
		return userId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}





	

	

}
