package com.ald.fanbei.api.biz.service.impl;


import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.dal.dao.AfResourceH5ItemDao;
import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;


/**
 * h5商品资源管理ServiceImpl
 *
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afResourceH5ItemService")
public class AfResourceH5ItemServiceImpl implements AfResourceH5ItemService {

	@Resource
	AfResourceH5ItemDao afResourceH5ItemDao;

	@Override
	public List<AfResourceH5ItemDto> selectByModelId(Long modelId){
		return afResourceH5ItemDao.selectByModelId(modelId);
	}

	@Override

	public AfResourceH5ItemDo getByTagAndType(String tag, String type) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.getByTagAndType(tag,type);
	}

	@Override
	public List<AfResourceH5ItemDo> getByTag(String tag) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.getByTag(tag);
	}


	public List<AfResourceH5ItemDo> findListByModelTagAndSort(String tag, Integer sort) {
		return afResourceH5ItemDao.findListByModelTagAndSort(tag, sort);
	}

	@Override
	public List<AfResourceH5ItemDo> getByTagAndValue2(String tag, String type) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.getByTagAndValue2(tag,type);
	}


}