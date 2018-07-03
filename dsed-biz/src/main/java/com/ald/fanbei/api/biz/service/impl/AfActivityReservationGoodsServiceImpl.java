package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfActivityReservationGoodsDao;
import com.ald.fanbei.api.dal.domain.AfActivityReservationGoodsDo;
import com.ald.fanbei.api.biz.service.AfActivityReservationGoodsService;



/**
 * 活动预约商品表ServiceImpl
 * 
 * @author chenqingsong
 * @version 1.0.0 初始化
 * @date 2018-05-22 10:23:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afActivityReservationGoodsService")
public class AfActivityReservationGoodsServiceImpl extends ParentServiceImpl<AfActivityReservationGoodsDo, Long> implements AfActivityReservationGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfActivityReservationGoodsServiceImpl.class);
   
    @Resource
    private AfActivityReservationGoodsDao afActivityReservationGoodsDao;

		@Override
	public BaseDao<AfActivityReservationGoodsDo, Long> getDao() {
		return afActivityReservationGoodsDao;
	}
}