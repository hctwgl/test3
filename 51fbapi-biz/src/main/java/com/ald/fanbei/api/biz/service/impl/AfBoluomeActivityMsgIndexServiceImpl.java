package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityMsgIndexDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityMsgIndexDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityMsgIndexService;



/**
 * 点亮活动新版ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-17 11:51:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityMsgIndexService")
public class AfBoluomeActivityMsgIndexServiceImpl extends ParentServiceImpl<AfBoluomeActivityMsgIndexDo, Long> implements AfBoluomeActivityMsgIndexService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityMsgIndexServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityMsgIndexDao afBoluomeActivityMsgIndexDao;

		@Override
	public BaseDao<AfBoluomeActivityMsgIndexDo, Long> getDao() {
		return afBoluomeActivityMsgIndexDao;
	}

		@Override
	public AfBoluomeActivityMsgIndexDo getByUserId(Long userId) {
		    // TODO Auto-generated method stub
		return afBoluomeActivityMsgIndexDao.getByUserId(userId);
	}
}