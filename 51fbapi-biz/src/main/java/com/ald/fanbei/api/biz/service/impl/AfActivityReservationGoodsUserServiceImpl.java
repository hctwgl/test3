package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfActivityReservationGoodsUserDao;
import com.ald.fanbei.api.dal.domain.AfActivityReservationGoodsUserDo;
import com.ald.fanbei.api.biz.service.AfActivityReservationGoodsUserService;

import java.util.List;
import java.util.Map;


/**
 * 用户预定预售商品表ServiceImpl
 * 
 * @author chenqingsong
 * @version 1.0.0 初始化
 * @date 2018-05-22 09:12:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afActivityReservationGoodsUserService")
public class AfActivityReservationGoodsUserServiceImpl extends ParentServiceImpl<AfActivityReservationGoodsUserDo, Long> implements AfActivityReservationGoodsUserService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfActivityReservationGoodsUserServiceImpl.class);
   
    @Resource
    private AfActivityReservationGoodsUserDao afActivityReservationGoodsUserDao;

	@Override
	public BaseDao<AfActivityReservationGoodsUserDo, Long> getDao() {
		return afActivityReservationGoodsUserDao;
	}

	@Override
	public List<AfActivityReservationGoodsUserDo> getUserActivityReservationGoodsList(Long userId, Long activityId) {
		return afActivityReservationGoodsUserDao.getUserActivityReservationGoodsList(userId, activityId);
	}

	@Override
	public List<AfActivityReservationGoodsUserDo> getActivityReservationGoodsList(Map<String, Object> map) {
		return afActivityReservationGoodsUserDao.getActivityReservationGoodsList(map);
	}
}