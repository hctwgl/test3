package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBoluomeActivityResultService;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityResultDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityResultDo;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:33
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityResultService")
public class AfBoluomeActivityResultServiceImpl extends ParentServiceImpl<AfBoluomeActivityResultDo, Long> implements AfBoluomeActivityResultService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityResultServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityResultDao afBoluomeActivityResultDao;

		@Override
	public BaseDao<AfBoluomeActivityResultDo, Long> getDao() {
		return afBoluomeActivityResultDao;
	}

		@Override
		public boolean isGetSuperPrize(Long userId, Long activityId) {
			boolean result = false;
			int intResult ;
			List<AfBoluomeActivityResultDo> list = new  ArrayList<AfBoluomeActivityResultDo>();
			list = afBoluomeActivityResultDao.isGetSuperPrize(userId,activityId);
			intResult = list.size();
			if (intResult > 0 ) {
				result = true;
			}
			return result;
		}
}