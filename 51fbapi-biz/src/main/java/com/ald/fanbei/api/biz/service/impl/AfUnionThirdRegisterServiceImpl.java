package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUnionThirdRegisterDao;
import com.ald.fanbei.api.dal.domain.AfUnionThirdRegisterDo;
import com.ald.fanbei.api.biz.service.AfUnionThirdRegisterService;



/**
 * '联合注册成功日志表ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-10-05 10:18:05
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUnionThirdRegisterService")
public class AfUnionThirdRegisterServiceImpl extends ParentServiceImpl<AfUnionThirdRegisterDo, Long> implements AfUnionThirdRegisterService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUnionThirdRegisterServiceImpl.class);
   
    @Resource
    private AfUnionThirdRegisterDao afUnionThirdRegisterDao;

		@Override
	public BaseDao<AfUnionThirdRegisterDo, Long> getDao() {
		return afUnionThirdRegisterDao;
	}
}