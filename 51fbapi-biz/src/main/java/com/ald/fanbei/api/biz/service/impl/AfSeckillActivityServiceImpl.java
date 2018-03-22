package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.biz.service.AfSeckillActivityService;

import java.util.List;


/**
 * 秒杀活动管理ServiceImpl
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2018-03-06 16:58:37
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afSeckillActivityService")
public class AfSeckillActivityServiceImpl extends ParentServiceImpl<AfSeckillActivityDo, Long> implements AfSeckillActivityService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfSeckillActivityServiceImpl.class);
   
    @Resource
    private AfSeckillActivityDao afSeckillActivityDao;
	@Resource
	private AfSeckillActivityGoodsDao afSeckillActivityGoodsDao;
	@Resource
	private AfSeckillActivityOrderDao afSeckillActivityOrderDao;
	@Resource
	AfGoodsPriceDao afGoodsPriceDao;
	@Override
	public BaseDao<AfSeckillActivityDo, Long> getDao() {
		return afSeckillActivityDao;
	}

	@Override
	public AfSeckillActivityDo getActivityByGoodsId(Long goodsId) {
		return afSeckillActivityDao.getActivityByGoodsId(goodsId);
	}

	@Override
	public List<AfSeckillActivityGoodsDo> getActivityGoodsByGoodsId(Long goodsId) {
		return afSeckillActivityGoodsDao.getActivityGoodsByGoodsId(goodsId);
	}

	@Override
	public AfSeckillActivityGoodsDto getActivityPriceByPriceIdAndActId(Long goodsPriceId,Long activityId) {
		return afSeckillActivityGoodsDao.getActivityPriceByPriceIdAndActId(goodsPriceId,activityId);
	}

	@Override
	public int updateActivityGoodsById(AfSeckillActivityGoodsDo afSeckillActivityGoodsDo) {
		return afSeckillActivityGoodsDao.updateActivityGoodsById(afSeckillActivityGoodsDo);
	}

	@Override
	public int saveActivityOrde(AfSeckillActivityOrderDo afSeckillActivityOrderDo) {
		return afSeckillActivityOrderDao.saveRecord(afSeckillActivityOrderDo);
	}

	@Override
	public int updateActivityOrderById(AfSeckillActivityOrderDo afSeckillActivityOrderDo) {
		return afSeckillActivityOrderDao.updateById(afSeckillActivityOrderDo);
	}

	@Override
	public int getSaleCountByActivityIdAndGoodsId(Long activityId, Long goodsId) {
		return afSeckillActivityOrderDao.getSaleCountByActivityIdAndGoodsId(activityId,goodsId);
	}

	@Override
	public AfSeckillActivityOrderDo getActivityOrderByOrderId(Long orderId) {
		return afSeckillActivityOrderDao.getActivityOrderByOrderId(orderId);
	}

	@Override
	public AfSeckillActivityGoodsDo getActivityGoodsByGoodsIdAndActId(Long activityId, Long goodsId) {
		return afSeckillActivityGoodsDao.getActivityGoodsByGoodsIdAndActId(activityId,goodsId);
	}

	@Override
	public AfSeckillActivityGoodsDto getActivityInfoByPriceIdAndActId(Long goodsPriceId, Long activityId) {
		return afSeckillActivityGoodsDao.getActivityInfoByPriceIdAndActId(goodsPriceId,activityId);
	}

	@Override
	public AfSeckillActivityDo getActivityByOrderId(Long orderId) {
		return afSeckillActivityOrderDao.getActivityByOrderId(orderId);
	}

	@Override
	public List<AfSeckillActivityGoodsDto> getActivityPricesByGoodsIdAndActId(Long goodsId, Long activityId) {
		return afSeckillActivityGoodsDao.getActivityPricesByGoodsIdAndActId(goodsId,activityId);
	}

	@Override
	public int getSumCountByGoodsId(Long goodsId) {
		return afGoodsPriceDao.selectSumStock(goodsId);
	}

	@Override
	public AfSeckillActivityOrderDo getActivityOrderByGoodsIdAndActId(Long goodsId, Long activityId, Long userId) {
		return afSeckillActivityOrderDao.getActivityOrderByGoodsIdAndActId(goodsId,activityId,userId);
	}

	@Override
	public AfSeckillActivityDo getActivityById(Long activityId) {
		return afSeckillActivityDao.getActivityById(activityId);
	}
}