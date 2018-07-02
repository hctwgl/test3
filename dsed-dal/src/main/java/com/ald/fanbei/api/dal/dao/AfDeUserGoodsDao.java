package com.ald.fanbei.api.dal.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfDeUserGoodsInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfDeUserGoodsQuery;

/**
 * 双十一砍价Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfDeUserGoodsDao extends BaseDao<AfDeUserGoodsDo, Long> {

    AfDeUserGoodsInfoDto getUserGoodsInfo(AfDeUserGoodsDo afDeUserGoodsDo);

    List<AfDeUserGoodsDto> getAfDeUserGoogsList(AfDeUserGoodsQuery queryGoods);
    /**
     * 获取用户可以购买的商品类型的价格
     * 
     * @author gaojb
     * @Time 2017年10月24日 下午3:56:40
     * @param userId
     * @param goodsPriceId
     * @return
     */
    AfDeUserGoodsDo getUserGoodsPrice(@Param("userId") Long userId, @Param("goodsPriceId") Long goodsPriceId);

    /**
     * 更新砍价商品是否已经购买
     * @author gaojb
     * @Time 2017年10月24日 下午5:37:53
     * @param id
     * @param isBuy
     * @return
     */
    int updateIsBuyById(@Param("rid") Long id, @Param("isbuy") Integer isBuy);

    AfDeUserGoodsInfoDto getUserCutPrice(AfDeUserGoodsDo afDeUserGoodsDo);
}
