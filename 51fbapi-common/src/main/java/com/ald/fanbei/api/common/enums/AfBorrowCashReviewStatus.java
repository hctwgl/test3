/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年3月28日下午2:39:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfBorrowCashReviewStatus {
	
	apply("APPLY", "申请/待风控审核"), 
	waitfbReview("WAITFBREVIEW", ":待返呗审核通过"),
	refuse("REFUSE", "风控拒绝"),
	
	agree("AGREE", "风控审核同意"),

	fbagree("FBAGREE", "返呗审核同意"),
	fbrefuse("FBREFUSE", "返呗平台审核拒绝");
    
    private String code;
    private String name;


    AfBorrowCashReviewStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static AfBorrowCashReviewStatus findRoleTypeByCode(String code) {
        for (AfBorrowCashReviewStatus roleType : AfBorrowCashReviewStatus.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
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
