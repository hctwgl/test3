/**
 * 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

/**
 * @类描述：
 * @author suweili 2017年3月1日下午9:54:22
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBankUserBankDto extends AfUserBankcardDo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String bankIcon;
	/**
	 * @return the bankIcon
	 */
	public String getBankIcon() {
		return bankIcon;
	}
	/**
	 * @param bankIcon the bankIcon to set
	 */
	public void setBankIcon(String bankIcon) {
		this.bankIcon = bankIcon;
	}

}
