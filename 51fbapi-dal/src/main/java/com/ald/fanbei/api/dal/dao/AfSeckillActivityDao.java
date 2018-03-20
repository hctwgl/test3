package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityDto;

/**
 * 秒杀活动管理Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-06 16:58:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSeckillActivityDao extends BaseDao<AfSeckillActivityDo, Long> {

    AfSeckillActivityDto getActivityByGoodsId(Long goodsId);
}