package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfActivityReservationGoodsDo;
import com.ald.fanbei.api.dal.domain.AfActivityReservationGoodsUserDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户预定预售商品表Dao
 * 
 * @author chenqingsong
 * @version 1.0.0 初始化
 * @date 2018-05-22 09:12:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityReservationGoodsUserDao extends BaseDao<AfActivityReservationGoodsUserDo, Long> {


    List<AfActivityReservationGoodsUserDo> getUserActivityReservationGoodsList( @Param("userId") long userId, @Param("activityId") Long activityId);

    List<AfActivityReservationGoodsUserDo> getActivityReservationGoodsList(Map<String, Object> map);

    List<AfGoodsDo> getReservationGoodsList(Map<String, Object> map);

    /*
     * 更新预约商品数量
     */
    int updateReservationInfo(@Param("goodsId") Long goodsId, @Param("userId") Long userId, @Param("type") int type);
}
