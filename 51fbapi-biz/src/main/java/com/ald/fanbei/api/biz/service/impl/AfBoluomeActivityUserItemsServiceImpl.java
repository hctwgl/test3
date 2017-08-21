package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserItemsDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityUserItemsService")
public class AfBoluomeActivityUserItemsServiceImpl extends ParentServiceImpl<AfBoluomeActivityUserItemsDo, Long> implements AfBoluomeActivityUserItemsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityUserItemsServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityUserItemsDao afBoluomeActivityUserItemsDao;

		@Override
	public BaseDao<AfBoluomeActivityUserItemsDo, Long> getDao() {
		return afBoluomeActivityUserItemsDao;
	}

		@Override
		public List<Long> getItemsByActivityIdUserId(Long activityId, Long userId) {
			return afBoluomeActivityUserItemsDao.getItemsByActivityIdUserId(activityId,userId);
			
		}

		@Override
		public void deleteByRid(Long rid) {
			afBoluomeActivityUserItemsDao.deleteByRid(rid);
			
		}

		@Override
		public Integer geFakeJoin(Long activityId) {
			return afBoluomeActivityUserItemsDao.geFakeJoin(activityId);
		}

		@Override
		public Integer getFakeFinal(Long activityId) {
			return afBoluomeActivityUserItemsDao.getFakeFinal(activityId);
		}
}