package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * 
 * @类描述：用户接口调用限制
 * 
 * @author huyang 2017年4月26日下午3:05:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserApiCallLimitDo extends AbstractSerial {

	/**
	 * @含义： @值含义：
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private Long userId;
	private Integer callNum = 0;// 已经调用次数
	private String disableStatus = "N";// 禁用状态 Y 禁用 N 不禁用
	private String type;// 接口标识 TONGDUN 同盾 YOUDUN 有盾

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getCallNum() {
		return callNum;
	}

	public void setCallNum(Integer callNum) {
		this.callNum = callNum;
	}

	public String getDisableStatus() {
		return disableStatus;
	}

	public void setDisableStatus(String disableStatus) {
		this.disableStatus = disableStatus;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
