package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀活动管理(商品)Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-07 10:25:58
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSeckillActivityGoodsDao extends BaseDao<AfSeckillActivityGoodsDo, Long> {


    List<AfSeckillActivityGoodsDo> getActivityGoodsByGoodsId(Long goodsId);

    AfSeckillActivityGoodsDto getActivityPriceByPriceIdAndActId(@Param("goodsPriceId")Long goodsPriceId,@Param("activityId")Long activityId);

    int updateActivityGoodsById(AfSeckillActivityGoodsDo afSeckillActivityGoodsDo);

    AfSeckillActivityGoodsDo getStartActivityPriceByPriceId(Long priceId);

    AfSeckillActivityGoodsDo getActivityGoodsByGoodsIdAndActId(@Param("activityId") Long activityId, @Param("goodsId") Long goodsId);
}
