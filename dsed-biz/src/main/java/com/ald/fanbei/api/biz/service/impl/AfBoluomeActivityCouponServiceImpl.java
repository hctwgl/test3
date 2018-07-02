package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityCouponDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityCouponDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityCouponService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityCouponService")
public class AfBoluomeActivityCouponServiceImpl extends ParentServiceImpl<AfBoluomeActivityCouponDo, Long> implements AfBoluomeActivityCouponService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityCouponServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityCouponDao afBoluomeActivityCouponDao;

		@Override
	public BaseDao<AfBoluomeActivityCouponDo, Long> getDao() {
		return afBoluomeActivityCouponDao;
	}
}