package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfUserDo;

/**
 * 
 * @类描述：用户账号相关信息
 * @author Xiaotianjian 2017年1月19日下午4:04:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserDto extends AfUserDo {
	
	private static final long serialVersionUID = -7305207059853020128L;
	
	private String alipayAccount;//支付宝账号

	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}
	

}
