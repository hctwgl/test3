package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUserWithholdDao;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;



/**
 * 用户代扣信息ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-07 10:52:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserWithholdService")
public class AfUserWithholdServiceImpl extends ParentServiceImpl<AfUserWithholdDo, Long> implements AfUserWithholdService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUserWithholdServiceImpl.class);
   
    @Resource
    private AfUserWithholdDao afUserWithholdDao;

		@Override
	public BaseDao<AfUserWithholdDo, Long> getDao() {
		return afUserWithholdDao;
	}

	@Override
	public AfUserWithholdDo getByUserId(Long userId) {
		return afUserWithholdDao.getByUserId(userId);
	}
	
	@Override
	public AfUserWithholdDo getAfUserWithholdDtoByUserId(long userId) {
		return afUserWithholdDao.getAfUserWithholdDtoByUserId(userId);
	}

	@Override
	public int insertAfUserWithholdDto(AfUserWithholdDo afUserWithholdDto) {
		return afUserWithholdDao.insertAfUserWithholdDto(afUserWithholdDto);
	}

	@Override
	public int updateAfUserWithholdDtoByUserId(long userId, Integer IsSwitch) {
		return afUserWithholdDao.updateAfUserWithholdDtoByUserId(userId, IsSwitch);
	}

	@Override
	public int updateAfUserWithholdDo(AfUserWithholdDo afUserWithholdDo) {
		return afUserWithholdDao.updateAfUserWithholdDo(afUserWithholdDo);
	}

	@Override
	public AfUserWithholdDo getWithholdInfo(long userId) {
		return afUserWithholdDao.getWithholdInfo(userId);
	}

	@Override
	public int getCountByUserId(Long userId) {
		return afUserWithholdDao.getCountByUserId(userId);
	}
}