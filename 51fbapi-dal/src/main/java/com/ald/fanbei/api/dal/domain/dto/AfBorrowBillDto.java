package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;

/**
 * 
 *@类描述：AfBorrowBillDto
 *@author 何鑫 2017年2月21日  22:44:39
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowBillDto extends AfBorrowBillDo{

	private static final long serialVersionUID = -8484667096639796716L;

	private String borrowType;

	public String getBorrowType() {
		return borrowType;
	}

	public void setBorrowType(String borrowType) {
		this.borrowType = borrowType;
	}
}
