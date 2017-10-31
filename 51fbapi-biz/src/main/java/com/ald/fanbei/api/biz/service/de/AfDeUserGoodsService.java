package com.ald.fanbei.api.biz.service.de;

import java.util.List;

import com.ald.fanbei.api.biz.service.ParentService;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserGoodsQuery;

/**
 * 双十一砍价Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public interface AfDeUserGoodsService extends ParentService<AfDeUserGoodsDo, Long> {
    /**
     * 获取用户可以购买的商品类型的价格
     * 
     * @author gaojb
     * @Time 2017年10月24日 下午3:56:40
     * @param userId
     * @param goodsPriceId
     * @return
     */
    AfDeUserGoodsDo getUserGoodsPrice(Long userId, Long goodsPriceId);    
    
    int updateIsBuyById( Long id,  Integer isBuy);
    
    List<AfDeUserGoodsDto> getAfDeUserGoogsList(AfDeUserGoodsQuery queryGoods);
    
    AfDeUserGoodsInfoDto getUserGoodsInfo(AfDeUserGoodsDo afDeUserGoodsDo);

    AfDeUserGoodsInfoDto getUserCutPrice(AfDeUserGoodsDo afDeUserGoodsDo);
}
