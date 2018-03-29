package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.dal.dao.AfResourceH5ItemDao;
import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import com.ald.fanbei.api.dal.domain.dto.AfTypeCountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserH5ItmeGoodsDto;
import com.ald.fanbei.api.dal.domain.query.ResourceH5ItemQuery;

/**
 * h5商品资源管理ServiceImpl
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afResourceH5ItemService")
public class AfResourceH5ItemServiceImpl implements AfResourceH5ItemService {

	private static final Logger logger = LoggerFactory
			.getLogger(AfResourceH5ItemServiceImpl.class);

	@Resource
	private AfResourceH5ItemDao afResourceH5ItemDao;

	@Override
	public int saveRecord(AfResourceH5ItemDo t) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.saveRecord(t);
	}

	@Override
	public int updateById(AfResourceH5ItemDo t) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.updateById(t);
	}

	@Override
	public AfResourceH5ItemDo getById(Long id) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.getById(id);
	}

	@Override
	public AfResourceH5ItemDo getByCommonCondition(AfResourceH5ItemDo t) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.getByCommonCondition(t);
	}

	@Override
	public List<AfResourceH5ItemDo> getListByCommonCondition(
			AfResourceH5ItemDo t) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.getListByCommonCondition(t);
	}

	@Override
	public List<AfResourceH5ItemDto> listGoods(ResourceH5ItemQuery query) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.listGoods(query);
	}

	@Override
	public int addResourceH5Item(AfResourceH5ItemDo afResourceH5ItemDo) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.addResourceH5Item(afResourceH5ItemDo);
	}

	@Override
	public int updateResourceH5Item(AfResourceH5ItemDo afResourceH5ItemDo) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.updateResourceH5Item(afResourceH5ItemDo);
	}

	@Override
	public int deleteResourceH5Item(AfResourceH5ItemDo afResourceH5ItemDo) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.deleteResourceH5Item(afResourceH5ItemDo);
	}

	@Override
	public List<Long> listAllGoodsIdByModelIdAndCategoryId(Long modelId,
			Long categoryId) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.listAllGoodsIdByModelIdAndCategoryId(modelId, categoryId);
	}

	@Override
	public void batchAddResourceH5Item(List<AfResourceH5ItemDo> addGoodsList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<AfResourceH5ItemDto> selectByModelId(Long id) {
		// TODO Auto-generated method stub
		return afResourceH5ItemDao.selectByModelId(id);
	}




}