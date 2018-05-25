package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfActivityReservationGoodsUserDo;
import java.util.*;

/**
 * 用户预定预售商品表Service
 * 
 * @author chenqingsong
 * @version 1.0.0 初始化
 * @date 2018-05-22 09:12:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfActivityReservationGoodsUserService extends ParentService<AfActivityReservationGoodsUserDo, Long>{

    /*
     * 用户预售商品列表
     */
    List<AfActivityReservationGoodsUserDo> getUserActivityReservationGoodsList(Long userId, Long activityId);

    /*
     * 活动预售商品列表
     */
    List<AfActivityReservationGoodsUserDo> getActivityReservationGoodsList(Map<String, Object> map);
}
