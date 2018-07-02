package com.ald.fanbei.api.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
import com.ald.fanbei.api.dal.domain.AfSFgoodsVo;
import com.ald.fanbei.api.dal.domain.GoodsForDate;

/**
 * 双蛋活动Dao
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-12-07 13:40:05
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsDoubleEggsDao extends BaseDao<AfGoodsDoubleEggsDo, Long> {

	List<AfGoodsDoubleEggsDo> getByGoodsId(@Param("goodsId")Long goodsId);
	AfGoodsDoubleEggsDo getByDoubleGoodsId(@Param("doubleGoodsId")Long doubleGoodsId);
	

	List<Date> getAvalibleDateList(@Param("startTime")String startTime);

	List<GoodsForDate> getgoodsByDate(@Param("startDate")Date startDate, @Param("tag")String tag);

	void updateCountById(@Param("goodsId")Long goodsId);
	
	List<AfSFgoodsVo> getFivePictures(@Param("userId") Long userId);
	
	int shouldOnlyAp(@Param("goodsId")Long goodsId);

	List<GoodsForDate> getGoodsListByActivityId(@Param("activityId")Long activityId);
	Long getCurrentDoubleGoodsId(@Param("goodsId")Long goodsId);
	Integer getAlreadyCount(@Param("goodsId")Long goodsId);


}
