package com.ald.fanbei.api.dal.domain;

import java.util.Date;
import java.util.List;
/**
 * 
* @ClassName: AfGoodsBuffer
* @Description: TODO(这里用一句话描述这个类的作用)
* @author qiao
* @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
* @date 2017年12月8日 下午2:23:17
*
 */
public class AfGoodsBuffer {
	private Date startTime;

	private List<GoodsForDate> goodsListForDate;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public List<GoodsForDate> getGoodsListForDate() {
		return goodsListForDate;
	}

	public void setGoodsListForDate(List<GoodsForDate> goodsListForDate) {
		this.goodsListForDate = goodsListForDate;
	}

	@Override
	public String toString() {
		return "AfGoodsBuffer [startTime=" + startTime + ", goodsListForDate=" + goodsListForDate + "]";
	}

}
