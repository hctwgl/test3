package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author 江荣波 2017年2月20日上午9:47:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfAbTestDeviceDo extends AbstractSerial{

	private static final long serialVersionUID = 5566947920375407089L;

	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String isDelete;
	private Long userId;
	private String deviceNum;
//	private String deviceNumber;
//	private Date loginDate;
	
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
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getDeviceNum() {
		return deviceNum;
	}
	public void setDeviceNum(String deviceNum) {
		this.deviceNum = deviceNum;
	}
	
//	public String getDeviceNumber() {
//	    return deviceNumber;
//	}
//	public void setDeviceNumber(String deviceNumber) {
//	    this.deviceNumber = deviceNumber;
//	}
//	public Date getLoginDate() {
//	    return loginDate;
//	}
//	public void setLoginDate(Date loginDate) {
//	    this.loginDate = loginDate;
//	}
//	
	

}
