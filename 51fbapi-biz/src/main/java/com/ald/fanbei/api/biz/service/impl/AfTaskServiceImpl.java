package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfTaskDao;
import com.ald.fanbei.api.dal.domain.AfTaskDo;
import com.ald.fanbei.api.biz.service.AfTaskService;

import java.util.List;


/**
 * 分类运营位配置ServiceImpl
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 14:44:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afTaskService")
public class AfTaskServiceImpl  implements AfTaskService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfTaskServiceImpl.class);
   
    @Resource
    private AfTaskDao afTaskDao;

    @Override
	public List<AfTaskDo> getTaskListByUserIdAndUserLevel(Long userId, String userLevel){
		return afTaskDao.getTaskListByUserIdAndUserLevel(userId,userLevel);
	}

}