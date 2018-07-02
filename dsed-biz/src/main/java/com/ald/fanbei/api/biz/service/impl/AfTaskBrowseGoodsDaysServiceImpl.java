package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.AfTaskBrowseGoodsDaysDao;
import com.ald.fanbei.api.dal.domain.AfTaskBrowseGoodsDaysDo;
import com.ald.fanbei.api.biz.service.AfTaskBrowseGoodsDaysService;



/**
 * 持续完成浏览商品数量的天数ServiceImpl
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-16 21:12:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTaskBrowseGoodsDaysService")
public class AfTaskBrowseGoodsDaysServiceImpl implements AfTaskBrowseGoodsDaysService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTaskBrowseGoodsDaysServiceImpl.class);
   
    @Resource
    private AfTaskBrowseGoodsDaysDao afTaskBrowseGoodsDaysDao;


	@Override
	public int addTaskBrowseGoodsDays(AfTaskBrowseGoodsDaysDo afTaskBrowseGoodsDaysDo) {
		return afTaskBrowseGoodsDaysDao.addTaskBrowseGoodsDays(afTaskBrowseGoodsDaysDo);
	}

	@Override
	public int updateTaskBrowseGoodsDays(Long userId, Integer continueDays) {
		return afTaskBrowseGoodsDaysDao.updateTaskBrowseGoodsDays(userId, continueDays);
	}

	@Override
	public AfTaskBrowseGoodsDaysDo isUserAttend(Long userId){
		return afTaskBrowseGoodsDaysDao.isUserAttend(userId);
	}

	@Override
	public AfTaskBrowseGoodsDaysDo isCompletedTaskYestaday(Long userId){
		return afTaskBrowseGoodsDaysDao.isCompletedTaskYestaday(userId);
	}

	@Override
	public AfTaskBrowseGoodsDaysDo isCompletedTaskToday(Long userId) {
		return afTaskBrowseGoodsDaysDao.isCompletedTaskToday(userId);
	}
}