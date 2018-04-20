/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年3月30日下午2:49:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfBorrowLegalRepaymentStatus {
	APPLY("A", "新建状态"), 
	PROCESS("P","处理中"),
	NO("N", "还款失败"),
	YES("Y", "还款成功"),
	SMS("S","快捷支付获取短信");
    
    private String code;
    private String name;

    AfBorrowLegalRepaymentStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
