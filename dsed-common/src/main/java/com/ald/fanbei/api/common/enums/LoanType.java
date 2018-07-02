package com.ald.fanbei.api.common.enums;

/**
 * @类描述：贷款产品类型
 * @author Jiang Rongbo 2017年2月7日下午2:34:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum LoanType {

	CASH("CASH","小额贷"),
	BLD_LOAN("BLD_LOAN", "白领贷");

    private String code;
    private String name;
    

    LoanType(String code, String name) {
        this.code = code;
        this.name = name;
    }

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}


}
