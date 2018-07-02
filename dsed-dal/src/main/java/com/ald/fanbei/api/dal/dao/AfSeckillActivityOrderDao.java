package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityOrderDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀活动管理(商品)Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-08 21:35:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSeckillActivityOrderDao extends BaseDao<AfSeckillActivityOrderDo, Long> {


    int getSaleCountByActivityIdAndGoodsId(@Param("activityId") Long activityId, @Param("goodsId") Long goodsId);

    AfSeckillActivityOrderDo getActivityOrderByOrderId(@Param("orderId") Long orderId);

    AfSeckillActivityDo getActivityByOrderId(@Param("orderId") Long orderId);

    AfSeckillActivityOrderDo getActivityOrderByGoodsIdAndActId(@Param("goodsId") Long goodsId, @Param("activityId") Long activityId, @Param("userId") Long userId);

    List<AfSeckillActivityDo> getActivitySaleCountList(Long activityId);
}
