package com.ald.fanbei.api.biz.service;


import com.ald.fanbei.api.dal.domain.AfBottomGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfBottomGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfBottomGoodsQuery;

import java.util.List;

/**
 * 页面底部商品service
 *
 * @author wangli
 * @date 2018/4/10 15:30
 */
public interface AfBottomGoodsService extends ParentService<AfBottomGoodsDo, Long> {

    /**
     * 根据页面标识查找商品
     *
     * @author wangli
     * @date 2018/4/11 10:25
     */
    List<AfBottomGoodsDto> findGoodsByPageFlag(AfBottomGoodsQuery query);

}
