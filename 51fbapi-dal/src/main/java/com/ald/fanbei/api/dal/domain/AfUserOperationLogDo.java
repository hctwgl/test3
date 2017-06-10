package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类现描述：用户特殊操作日志类型Do
 * @author chengkang 2017年6月4日 下午4:27:00
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserOperationLogDo extends AbstractSerial {
	private static final long serialVersionUID = -6505092172977081401L;
	
	private Long id;
	private Date gmtCreate;
	private Long userId;
	private String type;
	private String refType;
	private String refId;
	
	
	public AfUserOperationLogDo() {
		super();
	}
	
	
	public AfUserOperationLogDo(Long userId, String type) {
		super();
		this.userId = userId;
		this.type = type;
	}
	
	public AfUserOperationLogDo(Long userId, String type, String refType,
			String refId) {
		super();
		this.userId = userId;
		this.type = type;
		this.refType = refType;
		this.refId = refId;
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	
	
}
