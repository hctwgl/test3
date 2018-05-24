package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfActivityReservationGoodsDo;
import org.apache.ibatis.annotations.Param;

/**
 * 活动预约商品表Dao
 * 
 * @author chenqingsong
 * @version 1.0.0 初始化
 * @date 2018-05-22 10:23:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityReservationGoodsDao extends BaseDao<AfActivityReservationGoodsDo, Long> {

    AfActivityReservationGoodsDo getActivityReservationGoodsInfo(@Param("activityId") Long activityId, @Param("goodsId") Long userId);

}
