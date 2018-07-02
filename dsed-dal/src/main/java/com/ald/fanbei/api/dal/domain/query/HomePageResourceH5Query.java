package com.ald.fanbei.api.dal.domain.query;

import java.util.Date;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
//首页ResourceH5配置的商品较少，不进行分页
public class HomePageResourceH5Query extends HomePageSecKillGoods {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String activityName;
    private Date dateStart;
    private Date dateEnd;

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

    @Override
    public String toString() {
	return "HomePageSecKillQuery [userId=" + userId + ", activityName=" + activityName + ", dateStart=" + dateStart + ", dateEnd=" + dateEnd + "]";
    }
}
