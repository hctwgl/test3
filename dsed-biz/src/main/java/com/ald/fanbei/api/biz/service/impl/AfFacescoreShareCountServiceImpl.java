package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfFacescoreShareCountDao;
import com.ald.fanbei.api.dal.domain.AfFacescoreShareCountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.biz.service.AfFacescoreShareCountService;
import com.ald.fanbei.api.biz.service.AfUserService;



/**
 * 颜值测试红包分享次数记录实体类ServiceImpl
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-19 09:39:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afFacescoreShareCountService")
public class AfFacescoreShareCountServiceImpl extends ParentServiceImpl<AfFacescoreShareCountDo, Long> implements AfFacescoreShareCountService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfFacescoreShareCountServiceImpl.class);
   
    @Resource
    private AfFacescoreShareCountDao afFacescoreShareCountDao;
    @Resource
    private AfUserService afUserService;
		@Override
	public BaseDao<AfFacescoreShareCountDo, Long> getDao() {
		return afFacescoreShareCountDao;
	}

		@Override
		public void dealWithShareCount(Long userId) {
			AfUserDo userDo = afUserService.getUserById(userId);
			if(userDo == null){
				return ;
			}
			AfFacescoreShareCountDo shareCountDo = afFacescoreShareCountDao.getByUserId(userId);
			if (shareCountDo == null){
				AfFacescoreShareCountDo shareCountDo2 = new AfFacescoreShareCountDo(userId,1);
				afFacescoreShareCountDao.addRecord(shareCountDo2);
			}else{
				shareCountDo.setCount(shareCountDo.getCount()+1);
				afFacescoreShareCountDao.updateById(shareCountDo);
			}
		}

		@Override
		public AfFacescoreShareCountDo getByUserId(Long userId) {
			return afFacescoreShareCountDao.getByUserId(userId);
		}
}