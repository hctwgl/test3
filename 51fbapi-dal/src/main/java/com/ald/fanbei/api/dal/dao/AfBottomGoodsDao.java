package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBottomGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfBottomGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfBottomGoodsQuery;

import java.util.List;

/**
 *
 *
 * @author wangli
 * @date 2018/4/11 8:20
 */
public interface AfBottomGoodsDao extends BaseDao<AfBottomGoodsDo, Long> {

    /**
     * 根据页面标识查找商品
     *
     * @author wangli
     * @date 2018/4/11 10:26
     */
    List<AfBottomGoodsDto> findGoodsByPageFlag(AfBottomGoodsQuery query);
}
