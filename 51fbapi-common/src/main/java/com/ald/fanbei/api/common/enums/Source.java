package com.ald.fanbei.api.common.enums;

/**
 * 
 * @类描述：来源
 * @author xiaotianjian 2017年2月7日下午1:50:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum Source {

	APP("A", "app端"), OTHER("O", "其他");
    
    private Source(String code, String description) {
		this.code = code;
		this.description = description;
	}

	private String code; 
	private String description;
	
    public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static Source findOrderSourceByCode(int code) {
        for (Source roleType : Source.values()) {
            if (roleType.getCode().equals(code)) {
                return roleType;
            }
        }
        return null;
    }

}
