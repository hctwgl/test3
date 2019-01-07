package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdTotalInfoDao;
import com.ald.fanbei.api.dal.domain.JsdTotalInfoDo;
import com.ald.fanbei.api.biz.service.JsdTotalInfoService;

import java.util.List;


/**
 * ServiceImpl
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2019-01-03 13:49:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdTotalInfoService")
public class JsdTotalInfoServiceImpl extends ParentServiceImpl<JsdTotalInfoDo, Long> implements JsdTotalInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdTotalInfoServiceImpl.class);
   
    @Resource
    private JsdTotalInfoDao jsdTotalInfoDao;

		@Override
	public BaseDao<JsdTotalInfoDo, Long> getDao() {
		return jsdTotalInfoDao;
	}

	@Override
	public int saveAll(List<JsdTotalInfoDo> list){
			return jsdTotalInfoDao.saveAll(list);
	}
}