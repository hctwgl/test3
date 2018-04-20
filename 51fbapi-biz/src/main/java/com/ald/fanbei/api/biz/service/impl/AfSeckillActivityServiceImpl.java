package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSeckillActivityService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsPriceDao;
import com.ald.fanbei.api.dal.dao.AfSeckillActivityDao;
import com.ald.fanbei.api.dal.dao.AfSeckillActivityGoodsDao;
import com.ald.fanbei.api.dal.dao.AfSeckillActivityOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfActGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.dal.domain.query.AfSeckillActivityQuery;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillByActivityModelQuery;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillByBottomGoodsQuery;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillQuery;


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

	@Override
	public AfSeckillActivityDo getStartActivityByPriceId(Long goodsPriceId) {
		return afSeckillActivityDao.getStartActivityByPriceId(goodsPriceId);
	}

	@Override
	public AfSeckillActivityDo getStartActivityByGoodsId(Long goodsId) {
		return afSeckillActivityDao.getStartActivityByGoodsId(goodsId);
	}

	@Override
	public List<AfSeckillActivityGoodsDo> getActivityGoodsByGoodsIds(List<Long> goodsIdList) {
		return afSeckillActivityGoodsDao.getActivityGoodsByGoodsIds(goodsIdList);
	}

	@Override
	public List<AfSeckillActivityDo> getActivityList(AfSeckillActivityQuery query) {
		return afSeckillActivityDao.getActivityList(query);
	}

	@Override
	public List<AfActGoodsDto> getActivityGoodsByGoodsIdsAndActId(List<Long> goodsIdList, Long activityId) {
		return afSeckillActivityGoodsDao.getActivityGoodsByGoodsIdsAndActId(goodsIdList,activityId);
	}

	@Override
	public List<HomePageSecKillGoods> getHomePageSecKillGoods(Long userId, String activityName, Integer activityDay, Integer pageNo) {
	    
	    Date activityDate = DateUtil.addDays(new Date(), activityDay);
	    HomePageSecKillQuery homePageSecKillQuery = new HomePageSecKillQuery();
	    homePageSecKillQuery.setActivityName(activityName);
	    homePageSecKillQuery.setDateStart(DateUtil.getStartOfDate(activityDate));
	    homePageSecKillQuery.setDateEnd(DateUtil.getEndOfDate(activityDate));
	    homePageSecKillQuery.setUserId(userId);
	    homePageSecKillQuery.setPageNo(pageNo);
	    homePageSecKillQuery.setPageSize(20);
	    return afSeckillActivityGoodsDao.getHomePageSecKillGoods(homePageSecKillQuery);
	}

	@Override
	public List<HomePageSecKillGoods> getHomePageSecKillGoodsByConfigureResourceH5(Long userId,
			List<Long> goodsIdList) {
		// TODO Auto-generated method stub
		return afSeckillActivityGoodsDao.getHomePageSecKillGoodsByConfigureResourceH5(userId,goodsIdList);
	}

	@Override
	public Map<String, Object> getHomePageSecKillGoodsByActivityModel(
			Long userId, String tag, Integer type, Long tabId,Integer pageNo) {
		 Map<String, Object> homePageSecKillGoods = new HashMap<String, Object>();
		HomePageSecKillByActivityModelQuery homePageSecKillByActivityModelQuery = new HomePageSecKillByActivityModelQuery();
		// TODO Auto-generated method stub
		homePageSecKillByActivityModelQuery.setUserId(userId);
		homePageSecKillByActivityModelQuery.setPageNo(pageNo);
		homePageSecKillByActivityModelQuery.setTag(tag);
		homePageSecKillByActivityModelQuery.setType(type);
		homePageSecKillByActivityModelQuery.setTabId(tabId);
		homePageSecKillByActivityModelQuery.setPageSize(20);
		 List<HomePageSecKillGoods> homePageSecKillGoodsList =  afSeckillActivityGoodsDao.getHomePageSecKillGoodsByActivityModel(homePageSecKillByActivityModelQuery);
		 homePageSecKillByActivityModelQuery.setFull(true);
		 homePageSecKillGoods.put("query", homePageSecKillByActivityModelQuery);
		 homePageSecKillGoods.put("goodsList", homePageSecKillGoodsList);
		 return   homePageSecKillGoods;
	
	}

	@Override
	public Map<String, Object> getMoreGoodsByBottomGoodsTable(
			Long userId, Integer pageNo, String pageFlag) {
		// TODO Auto-generated method stub
	     Map<String, Object> homePageSecKillGoods = new HashMap<String, Object>();
		HomePageSecKillByBottomGoodsQuery homePageSecKillByBottomGoodsQuery = new HomePageSecKillByBottomGoodsQuery();
		// TODO Auto-generated method stub
		homePageSecKillByBottomGoodsQuery.setUserId(userId);
		homePageSecKillByBottomGoodsQuery.setPageNo(pageNo);
		homePageSecKillByBottomGoodsQuery.setPageFlag(pageFlag);
		homePageSecKillByBottomGoodsQuery.setPageSize(20);
		List<HomePageSecKillGoods> homePageSecKillGoodsList =  afSeckillActivityGoodsDao.getMoreGoodsByBottomGoodsTable(homePageSecKillByBottomGoodsQuery);
		homePageSecKillByBottomGoodsQuery.setFull(true);
		homePageSecKillGoods.put("query", homePageSecKillByBottomGoodsQuery);
	    homePageSecKillGoods.put("goodsList", homePageSecKillGoodsList);
	    return   homePageSecKillGoods;
	}

	@Override
	public Integer getSecKillGoodsStock(Long goodsId, Long activityId) {
		return afSeckillActivityGoodsDao.getSecKillGoodsStock(goodsId,activityId);
	}
}