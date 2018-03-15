/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *
 *@类描述：有得卖 回收业务 页面访问
 *@author weiqingeng 2018年2月27日  09:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRecycleViewDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;

	private Long uid;//用户uid
	private Integer qty;//访问次数
	private Integer type;//访问类型 访问类型 1：回收 2：返现

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}