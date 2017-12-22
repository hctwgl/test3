package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.dal.domain.dto.GameGoods;
import com.ald.fanbei.api.dal.domain.dto.GameGoodsGroup;

/**
 * 游戏充值Dao
 * 
 * @author 高继斌_temple
 * @version 1.0.0 初始化
 * @date 2017-11-24 18:48:13 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupGameDao extends BaseDao<AfSupGameDo, Long> {

    List<GameGoodsGroup> getGameGoodsList(@Param("invelomentType") String invelomentType);

    List<GameGoodsGroup> getAmusementGoodsList(@Param("invelomentType") String invelomentType);

    List<GameGoods> getHotGoodsList(@Param("type") String type, @Param("invelomentType") String invelomentType);

}
