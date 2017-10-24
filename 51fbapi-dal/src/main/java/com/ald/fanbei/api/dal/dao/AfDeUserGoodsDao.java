package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserGoodsQuery;

/**
 * 双十一砍价Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfDeUserGoodsDao extends BaseDao<AfDeUserGoodsDo, Long> {

    AfDeUserGoodsInfoDto getGoodsInfo(AfDeUserGoodsDo afDeUserGoodsDo);

    List<AfDeUserGoodsDo> getAfDeUserGoogsList(AfDeUserGoodsQuery queryGoods);

}
