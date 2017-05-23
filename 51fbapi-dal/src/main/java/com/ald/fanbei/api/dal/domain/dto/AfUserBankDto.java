package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

/**
 * 
 *@类描述：AfUserBankDto
 *@author 何鑫 2017年3月1日  11:30:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserBankDto extends AfUserBankcardDo{

	private static final long serialVersionUID = 1727766268814549070L;

	private String realName;
	
	private String idNumber;

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}
	
}
