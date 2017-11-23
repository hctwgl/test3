package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfShareGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfShareGoodsDto;



/**
 * 新人专享Dao
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-11-02 11:16:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfShareGoodsDao extends BaseDao<AfShareGoodsDo, Long> {


    List<AfShareGoodsDto> getShareGoods();

	List<AfShareGoodsDo> getAllGoodsList();

	Integer getCountByGoodsId(Long goodsId);

    


}
