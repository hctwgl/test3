package com.ald.jsd.mgr.enums;

public enum CollectionBorrowStatus {
	NOTICED("已入催"), 
	WAIT_FINISH("待审核平"), 
	MANUAL_FINISHED("管理员强制平账"), 
	NORMAL_FINISHED("全额结清"),
	RENEWALED("已续期"), 
	COLLECT_FINISHED("催收平账");
	
	public String desc;
	
	CollectionBorrowStatus(String desc){
		this.desc = desc;
	}
}
