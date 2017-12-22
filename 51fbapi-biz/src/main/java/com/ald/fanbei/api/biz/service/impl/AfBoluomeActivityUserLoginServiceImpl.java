package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserLoginDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserLoginService;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:39:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityUserLoginService")
public class AfBoluomeActivityUserLoginServiceImpl extends ParentServiceImpl<AfBoluomeActivityUserLoginDo, Long> implements AfBoluomeActivityUserLoginService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityUserLoginServiceImpl.class);
   
    @Resource
    private AfBoluomeActivityUserLoginDao afBoluomeActivityUserLoginDao;

        		@Override
        	public BaseDao<AfBoluomeActivityUserLoginDo, Long> getDao() {
        		return afBoluomeActivityUserLoginDao;
        	}

		@Override
		public Integer getBindingNum(Long activityId, Long refUserId) {
			
			return afBoluomeActivityUserLoginDao.getBindingNum(activityId,refUserId);
		}

		@Override
		public List<AfBoluomeActivityUserLoginDo> getByRefUserIdAndActivityId(Long userId,Long activityId) {
		    // TODO Auto-generated method stub
		    return afBoluomeActivityUserLoginDao.getByRefUserIdAndActivityId(userId,activityId);
		}
}