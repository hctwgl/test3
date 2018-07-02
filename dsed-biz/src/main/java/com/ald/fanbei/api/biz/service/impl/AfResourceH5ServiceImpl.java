package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfResourceH5Service;
import com.ald.fanbei.api.dal.dao.AfResourceH5Dao;
import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * h5资源管理ServiceImpl
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:39:09 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afResourceH5Service")
public class AfResourceH5ServiceImpl implements AfResourceH5Service {

	 @Resource
	 AfResourceH5Dao afResourceH5Dao;

	@Override
	public List<AfResourceH5Dto> selectByStatus(String tag){
		return afResourceH5Dao.selectByStatus(tag);
	}

	@Override
	public AfResourceH5Dto getByPageFlag(String pageFlag) {
		return afResourceH5Dao.getByPageFlag(pageFlag);
	}

}