package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.dal.domain.dto.GameGoods;
import com.ald.fanbei.api.dal.domain.dto.GameGoodsGroup;

/**
 * 新人专享Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-22 13:57:28 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupGameService extends ParentService<AfSupGameDo, Long> {

    List<GameGoodsGroup> getGoodsList(String type);

    List<GameGoods> getHotGoodsList(String type);
}
