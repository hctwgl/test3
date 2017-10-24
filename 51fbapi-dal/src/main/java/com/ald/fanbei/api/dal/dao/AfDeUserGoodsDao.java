package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;

/**
 * 双十一砍价Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfDeUserGoodsDao extends BaseDao<AfDeUserGoodsDo, Long> {

    /**
     * 获取用户可以购买的商品类型的价格
     * @author gaojb
     * @Time 2017年10月24日 下午3:56:40
     * @param userId
     * @param goodsPriceId
     * @return
     */
    AfDeUserGoodsDo getUserGoodsPrice(@Param("userId") Long userId, @Param("goodsPriceId") Long goodsPriceId);

}
