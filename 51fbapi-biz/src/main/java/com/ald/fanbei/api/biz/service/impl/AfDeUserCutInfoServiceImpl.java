package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfDeUserCutInfoDao;
import com.ald.fanbei.api.dal.domain.AfDeUserCutInfoDo;
import com.ald.fanbei.api.dal.domain.query.AfDeUserCutInfoQuery;
import com.ald.fanbei.api.biz.service.de.AfDeUserCutInfoService;




/**
 * 双十一砍价ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afDeUserCutInfoService")
public class AfDeUserCutInfoServiceImpl extends ParentServiceImpl<AfDeUserCutInfoDo, Long> implements AfDeUserCutInfoService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfDeUserCutInfoServiceImpl.class);
   
    @Resource
    private AfDeUserCutInfoDao afDeUserCutInfoDao;

		@Override
	public BaseDao<AfDeUserCutInfoDo, Long> getDao() {
		return afDeUserCutInfoDao;
	}

		@Override
	public List<AfDeUserCutInfoDo> getAfDeUserCutInfoList(AfDeUserCutInfoQuery queryCutInfo) {
		    // TODO Auto-generated method stub
		 return afDeUserCutInfoDao.getAfDeUserCutInfoList(queryCutInfo);
	}
}