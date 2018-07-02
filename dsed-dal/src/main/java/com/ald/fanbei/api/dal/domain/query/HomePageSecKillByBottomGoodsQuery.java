package com.ald.fanbei.api.dal.domain.query;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;

public class HomePageSecKillByBottomGoodsQuery extends Page<HomePageSecKillGoods> {

    private static final long serialVersionUID = 1L;

    private Long userId;
    public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getPageFlag() {
		return pageFlag;
	}
	public void setPageFlag(String pageFlag) {
		this.pageFlag = pageFlag;
	}
	private String pageFlag;


  
   
}
