package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityDto;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;

import java.util.List;

/**
 * 秒杀活动管理Service
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-06 16:58:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSeckillActivityService extends ParentService<AfSeckillActivityDo, Long>{

    AfSeckillActivityDto getActivityByGoodsId(Long goodsId);

    List<AfSeckillActivityGoodsDo> getActivityGoodsByGoodsId(Long goodsId);

    AfSeckillActivityGoodsDto getActivityPriceByPriceId(Long goodsPriceId);

    int updateActivityGoodsById(AfSeckillActivityGoodsDo afSeckillActivityGoodsDo);

    int saveActivityOrde(AfSeckillActivityOrderDo afSeckillActivityOrderDo);

    int updateActivityOrderById(AfSeckillActivityOrderDo afSeckillActivityOrderDo);

    int getSaleCountByActivityIdAndGoodsId(Long activityId, Long goodsId);

    AfSeckillActivityGoodsDo getStartActivityPriceByPriceId(Long priceId);

    AfSeckillActivityOrderDo getActivityOrderByOrderId(Long orderId);
}
