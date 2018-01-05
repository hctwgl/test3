package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;

/**
 * 
 * @类描述：
 * @author caowu 2017年1月5日下午3:15:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserAuthStatusDo extends AbstractSerial{

	private static final long serialVersionUID = 3837124026464656200L;

	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String scene;//CASH 现金贷场景，ONLINE 线上分期，TRAIN线下培训
	private String status;//Y已认证 N未认证 C认证失败
	private Long userId;//用户id
	private String causeReason;//未通过原因

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public String getScene() {
		return scene;
	}

	public void setScene(String scene) {
		this.scene = scene;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCauseReason() {
		return causeReason;
	}

	public void setCauseReason(String causeReason) {
		this.causeReason = causeReason;
	}
}
