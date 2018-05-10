package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTaskUserDao;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.biz.service.AfTaskUserService;

import java.util.List;


/**
 * 分类运营位配置ServiceImpl
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 16:02:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTaskUserService")
public class AfTaskUserServiceImpl implements AfTaskUserService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTaskUserServiceImpl.class);
   
    @Resource
    private AfTaskUserDao afTaskUserDao;


    @Override
	public List<AfTaskUserDo> isDailyTaskList(Long userId, List<Long> list){
    	return afTaskUserDao.isDailyTaskList(userId,list);
	}

	@Override
	public List<AfTaskUserDo> isNotDailyTaskList(Long userId, List<Long> list){
		return afTaskUserDao.isNotDailyTaskList(userId,list);
	}

	@Override
	public int updateNotDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.updateNotDailyByTaskIdAndUserId(afTaskUserDo);
	}

	@Override
	public int updateDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo){
		return afTaskUserDao.updateDailyByTaskIdAndUserId(afTaskUserDo);
	}
}