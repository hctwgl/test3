package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceH5Service;
import com.ald.fanbei.api.dal.dao.AfResourceH5Dao;
import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import com.ald.fanbei.api.dal.domain.query.AfResourceH5Query;




/**
 * h5资源管理ServiceImpl
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:39:09
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afResourceH5Service")
public class AfResourceH5ServiceImpl implements AfResourceH5Service {
	
    private static final Logger logger = LoggerFactory.getLogger(AfResourceH5ServiceImpl.class);
   
    @Resource
    private AfResourceH5Dao afResourceH5Dao;

	@Override
	public List<AfResourceH5Do> listResourceH5(AfResourceH5Query query) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.listResourceH5(query);
	}

	@Override
	public AfResourceH5Do getResourceH5ById(Long resourceH5Id) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.getResourceH5ById(resourceH5Id);
	}

	@Override
	public int deleteResourceH5(AfResourceH5Do resourceH5) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.deleteResourceH5(resourceH5);
	}

	@Override
	public int addResourceH5(AfResourceH5Do resourceH5) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.addResourceH5(resourceH5);
	}

	@Override
	public int updateResourceH5(AfResourceH5Do resourceH5) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.updateResourceH5(resourceH5);
	}

	@Override
	public int editResourceH5(AfResourceH5Do resourceH5Do) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.editResourceH5(resourceH5Do);
	}

	@Override
	public String editResourceH5Status(AfResourceH5Do resourceH5Do) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.editResourceH5Status(resourceH5Do);
	}

	@Override
	public int deleteById(AfResourceH5Do resourceH5Do) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.deleteById(resourceH5Do);
	}

	@Override
	public void updateById(AfResourceH5Do afResourceH5Do) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AfResourceH5Do> getListByCommonCondition(AfResourceH5Do queryDo) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.getListByCommonCondition(queryDo);
	}

	@Override
	public void saveRecord(AfResourceH5Do afResourceH5Do) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AfResourceH5Do getById(Long resourceH5Id) {
		// TODO Auto-generated method stub
		return afResourceH5Dao.getById(resourceH5Id);
	}

	@Override
	public List<AfResourceH5Dto> selectByStatus() {
		// TODO Auto-generated method stub
		return afResourceH5Dao.selectByStatus();
	}

	@Override
	public void clearCache() {
		// TODO Auto-generated method stub
		
	}

	

}