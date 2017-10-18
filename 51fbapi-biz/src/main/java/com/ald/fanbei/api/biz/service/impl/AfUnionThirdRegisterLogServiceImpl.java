package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUnionThirdRegisterLogDao;
import com.ald.fanbei.api.dal.domain.AfUnionThirdRegisterLogDo;
import com.ald.fanbei.api.biz.service.AfUnionThirdRegisterLogService;



/**
 * '联合注册接口日志表ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-10-05 15:56:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUnionThirdRegisterLogService")
public class AfUnionThirdRegisterLogServiceImpl extends ParentServiceImpl<AfUnionThirdRegisterLogDo, Long> implements AfUnionThirdRegisterLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUnionThirdRegisterLogServiceImpl.class);
   
    @Resource
    private AfUnionThirdRegisterLogDao afUnionThirdRegisterLogDao;

		@Override
	public BaseDao<AfUnionThirdRegisterLogDo, Long> getDao() {
		return afUnionThirdRegisterLogDao;
	}
}