package com.ald.fanbei.api.biz.service;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
import com.ald.fanbei.api.dal.domain.AfSFgoodsVo;
import com.ald.fanbei.api.dal.domain.GoodsForDate;

/**
 * 双蛋活动Service
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-12-07 13:40:05
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsDoubleEggsService extends ParentService<AfGoodsDoubleEggsDo, Long>{

	List<AfGoodsDoubleEggsDo> getByGoodsId(Long goodsId);
	AfGoodsDoubleEggsDo getByDoubleGoodsId(Long goodsId);
	
	List<Date> getAvalibleDateList(String beginningDate);

	List<GoodsForDate> getGOodsByDate(Date startDate, String tag);

	void updateCountById(Long goodsId);
	
	List<AfSFgoodsVo> getFivePictures(Long userId);
	boolean shouldOnlyAp(Long goodsId);

	List<GoodsForDate> getGoodsListByActivityId(Long activityId);
	Long getCurrentDoubleGoodsId(Long goodsId);
	Integer getAlreadyCount(Long goodsId);

}
