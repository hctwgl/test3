package com.ald.jsd.mgr.enums;

/**
 * 通用审批状态枚举
 * @author zhujiangfeng
 *
 */
public enum CommonReviewStatus {
	WAIT("待审批"), 
	PASS("通过"), 
	REFUSE("拒绝");
	
	public String desc;
	
	CommonReviewStatus(String desc){
		this.desc = desc;
	}
}
