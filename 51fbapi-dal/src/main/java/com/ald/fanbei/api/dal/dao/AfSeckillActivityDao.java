package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.dal.domain.query.AfSeckillActivityQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

import java.util.List;

/**
 * 秒杀活动管理Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-06 16:58:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSeckillActivityDao extends BaseDao<AfSeckillActivityDo, Long> {

    AfSeckillActivityDo getActivityByGoodsId(Long goodsId);

    AfSeckillActivityDo getActivityById(Long activityId);

    AfSeckillActivityDo getStartActivityByPriceId(Long goodsPriceId);

    AfSeckillActivityDo getStartActivityByGoodsId(Long goodsId);

    List<AfSeckillActivityDo> getActivityList(AfSeckillActivityQuery query);


    List<String> getActivityListByName(@Param("name") String name, @Param("gmtStart") Date gmtStart, @Param("gmtEnd") Date gmtEnd);
}
