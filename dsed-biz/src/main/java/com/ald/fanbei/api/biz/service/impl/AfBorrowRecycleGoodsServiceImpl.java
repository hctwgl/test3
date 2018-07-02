package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.AfBorrowRecycleGoodsBo;
import com.ald.fanbei.api.biz.third.util.yibaopay.JsonUtils;
import com.ald.fanbei.api.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowRecycleGoodsDao;
import com.ald.fanbei.api.dal.domain.AfBorrowRecycleGoodsDo;
import com.ald.fanbei.api.biz.service.AfBorrowRecycleGoodsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 回收商品表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-04-28 14:08:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowRecycleGoodsService")
public class AfBorrowRecycleGoodsServiceImpl extends ParentServiceImpl<AfBorrowRecycleGoodsDo, Long> implements AfBorrowRecycleGoodsService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowRecycleGoodsServiceImpl.class);
   
    @Resource
    private AfBorrowRecycleGoodsDao afBorrowRecycleGoodsDao;

		@Override
	public BaseDao<AfBorrowRecycleGoodsDo, Long> getDao() {
		return afBorrowRecycleGoodsDao;
	}

	@Override
	public List<AfBorrowRecycleGoodsBo> getAllRecycleGoodsInfos() {
		List<AfBorrowRecycleGoodsDo> afBorrowRecycleGoodsDos=afBorrowRecycleGoodsDao.getAllRecycleGoods();
		List<AfBorrowRecycleGoodsBo> boList=new ArrayList<>();
 		for(AfBorrowRecycleGoodsDo recycleGoodsDo:afBorrowRecycleGoodsDos){
			AfBorrowRecycleGoodsBo bo=new AfBorrowRecycleGoodsBo();
			bo.setId(recycleGoodsDo.getRid());
			bo.setName(recycleGoodsDo.getName());
			bo.setGoodsImg(recycleGoodsDo.getGoodsImg());
			Map<String,Object> mapPropertyValue=JsonUtils.fromJsonString(recycleGoodsDo.getPropertyValue(),Map.class);
			List<Object> listMap= (List<Object>) mapPropertyValue.get("propertyValue");
			bo.setPropertyValue(listMap);
			boList.add(bo);
		}
		return boList;
	}

	@Override
	public AfBorrowRecycleGoodsDo getRecycleGoodsById(Long recycleId) {
		return afBorrowRecycleGoodsDao.getRecycleGoodsById(recycleId);
	}


}