/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年3月28日下午2:39:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum RiskReviewStatus {
	
	APPLY("APPLY", "申请/待风控审核"), 
	WAITFBREVIEW("WAITFBREVIEW", ":待爱上街审核通过"),
	REFUSE("REFUSE", "风控拒绝审核不通过"),
	
	AGREE("AGREE", "风控审核同意"),

	FBAGREE("FBAGREE", "爱上街审核同意"),
	FBREFUSE("FBREFUSE", "爱上街平台审核拒绝");
    
    private String code;
    private String name;


    RiskReviewStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static RiskReviewStatus findRoleTypeByCode(String code) {
        for (RiskReviewStatus roleType : RiskReviewStatus.values()) {
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
