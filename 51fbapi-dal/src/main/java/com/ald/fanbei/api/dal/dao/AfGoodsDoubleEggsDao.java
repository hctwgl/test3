package com.ald.fanbei.api.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
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

	AfGoodsDoubleEggsDo getByGoodsId(@Param("goodsId")Long goodsId);

	List<Date> getAvalibleDateList();

	List<GoodsForDate> getgoodsByDate(@Param("startDate")Date startDate);

	void updateCountById(@Param("goodsId")Long goodsId);

    

}
