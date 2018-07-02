package com.ald.fanbei.api.dal.domain.query;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;

public class HomePageSecKillByActivityModelQuery extends Page<HomePageSecKillGoods> {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String activityName;
    private Date dateStart;
    private Date dateEnd;
    private String tag;
    private Integer type;
    private Long tabId;


    public Long getUserId() {
	return userId;
    }

    public void setUserId(Long userId) {
	this.userId = userId;
    }

    public String getActivityName() {
	return activityName;
    }

    public void setActivityName(String activityName) {
	this.activityName = activityName;
    }

    public Date getDateStart() {
	return dateStart;
    }

    public void setDateStart(Date dateStart) {
	this.dateStart = dateStart;
    }

    public Date getDateEnd() {
	return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
	this.dateEnd = dateEnd;
    }

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getTabId() {
		return tabId;
	}

	public void setTabId(Long tabId) {
		this.tabId = tabId;
	}

   
}
