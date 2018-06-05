package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfUserCouponStatus;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.druid.util.StringUtils;
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
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.dto.AfActGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.dal.domain.query.AfSeckillActivityQuery;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillByActivityModelQuery;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillByBottomGoodsQuery;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.biz.service.AfSeckillActivityService;

import java.util.Date;
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

	@Resource
	private AfActivityReservationGoodsDao afActivityReservationGoodsDao;

	@Resource
	private AfActivityReservationGoodsUserDao afActivityReservationGoodsUserDao;

	@Resource
	private AfResourceService afResourceService;

	@Resource
	private AfUserCouponService afUserCouponService;

	@Resource
	private AfOrderDao orderDao;

	@Resource
	private SmsUtil smsUtil;

	@Resource
	private AfCouponDao afCouponDao;

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
			Long userId, Integer pageNo, String pageFlag,String source) {
		// TODO Auto-generated method stub
	     Map<String, Object> homePageSecKillGoods = new HashMap<String, Object>();
		HomePageSecKillByBottomGoodsQuery homePageSecKillByBottomGoodsQuery = new HomePageSecKillByBottomGoodsQuery();
		// TODO Auto-generated method stub
		homePageSecKillByBottomGoodsQuery.setUserId(userId);
		homePageSecKillByBottomGoodsQuery.setPageNo(pageNo);
		homePageSecKillByBottomGoodsQuery.setPageFlag(pageFlag);
		if("H5".equals(source)){
			homePageSecKillByBottomGoodsQuery.setPageSize(30);
		}else{
			homePageSecKillByBottomGoodsQuery.setPageSize(20);
		}

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

	@Override
	public List<HomePageSecKillGoods> getHomePageSecKillGoodsById(Long userId, Long activityId) {
		return afSeckillActivityGoodsDao.getHomePageSecKillGoodsById(userId, activityId);
	}

	@Override
	public List<String> getActivityListByName(String name, Date gmtStart, Date gmtEnd){
		return afSeckillActivityDao.getActivityListByName(name, gmtStart, gmtEnd);
	}

	@Override
	public List<AfSeckillActivityGoodsDto> getActivityGoodsByActivityId(Long activityId) {
		return afSeckillActivityGoodsDao.getActivityGoodsByActivityId(activityId);
	}

	@Override
	public AfSeckillActivityDo getSaleInfoByGoodsIdAndActId(Long activityId, Long goodsId) {
		return afSeckillActivityGoodsDao.getSaleInfoByGoodsIdAndActId(activityId,goodsId);
	}

	@Override
	public List<AfSeckillActivityDo> getActivityGoodsCountList(Long activityId) {
		return afSeckillActivityGoodsDao.getActivityGoodsCountList(activityId);
	}

	@Override
	public List<AfSeckillActivityDo> getActivitySaleCountList(Long activityId) {
		return afSeckillActivityOrderDao.getActivitySaleCountList(activityId);
	}

	@Override
	public void updateUserActivityGoodsInfo(AfOrderDo orderInfo) {
		logger.info("updateUserActivityGoodsInfo UserId: " + orderInfo.getUserId() + " goodsId: " + orderInfo.getGoodsId());
		//获取资源信息
		AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.ACTIVITY_RESERVATION_GOODS);

		if(null != resourceInfo){
			Long activityId = Long.valueOf(resourceInfo.getValue());
			//获取活动信息
			AfSeckillActivityDo afSeckillActivityDo = afSeckillActivityDao.getActivityById( activityId);

			if(null != afSeckillActivityDo){
				long date = new Date().getTime();
				AfActivityReservationGoodsUserDo  afActivityReservationGoodsUserDo = new AfActivityReservationGoodsUserDo();
				afActivityReservationGoodsUserDo.setUserId(orderInfo.getUserId());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId",orderInfo.getUserId() );
				map.put("activityId",activityId );
				map.put("goodsId",orderInfo.getGoodsId() );

				//付定金
				if(afSeckillActivityDo.getGmtStart().getTime() <= date  && date <= afSeckillActivityDo.getGmtEnd().getTime()){

					//查询预售活动商品
					List<AfActivityReservationGoodsUserDo>  AfActivityReservationGoodsUserDoList = afActivityReservationGoodsUserDao.getActivityReservationGoodsList(map);
					if(null != AfActivityReservationGoodsUserDoList &&  AfActivityReservationGoodsUserDoList.size() > 0){
						logger.info("updateUserActivityGoodsInfo payReservationAmount userId: " + orderInfo.getUserId() );
						AfActivityReservationGoodsUserDo afActivityReservationGoodsUserDo1 = AfActivityReservationGoodsUserDoList.get(0);
						Long couponId = afActivityReservationGoodsUserDo1.getCouponId();
						Long userId = orderInfo.getUserId();
                        AfCouponDo couponDo = afCouponDao.getCouponById(couponId);
						for(int i = 0; i < orderInfo.getCount(); i++){
                            //未购买时添加，购买时更新购买数量
                            afActivityReservationGoodsUserDo.setGoodsId(afActivityReservationGoodsUserDo1.getRid());
                            Long userReservationId = afActivityReservationGoodsUserDo1.getUserReservationId();
                            Integer goodsCount = afActivityReservationGoodsUserDo1.getGoodsCount();
                            if(null !=  userReservationId &&  afActivityReservationGoodsUserDo1.getLimitCount() > goodsCount){
                                afActivityReservationGoodsUserDao.updateReservationInfo( afActivityReservationGoodsUserDo1.getRid(), orderInfo.getUserId(), 1);
                            }else if(null == userReservationId){
                            	if(i == 0){
									afActivityReservationGoodsUserDo.setCouponId(Long.valueOf(couponId));
									afActivityReservationGoodsUserDo.setGoodsCount(orderInfo.getCount());
									Date nowTime = new Date();
									afActivityReservationGoodsUserDo.setGmtCreate(nowTime);
									afActivityReservationGoodsUserDo.setGmtModified(nowTime);
									afActivityReservationGoodsUserDao.saveRecord(afActivityReservationGoodsUserDo);
								}
                            }

                            //送优惠券
                            if (couponDo != null) {
                                AfUserCouponDo userCoupon = new AfUserCouponDo();

								if (StringUtils.equals(couponDo.getExpiryType(), "R")) {
									userCoupon.setGmtStart(couponDo.getGmtStart());
									userCoupon.setGmtEnd(couponDo.getGmtEnd());
									if (DateUtil.afterDay(new Date(), couponDo.getGmtEnd())) {
										userCoupon.setStatus(CouponStatus.EXPIRE.getCode());
									}
								} else {
									userCoupon.setGmtStart(new Date());
									if (couponDo.getValidDays() == -1) {
										userCoupon.setGmtEnd(DateUtil.getFinalDate());
									} else {
										userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
									}
								}

                                userCoupon.setCouponId(couponDo.getRid());
                                userCoupon.setGmtCreate(new Date());
                              //  userCoupon.setGmtStart(couponDo.getGmtStart());
                               // userCoupon.setGmtEnd(couponDo.getGmtEnd());
                                userCoupon.setUserId(userId);
                                userCoupon.setStatus(AfUserCouponStatus.NOUSE.getCode());
                                userCoupon.setSourceType(CouponSenceRuleType.SHARE_ACTIVITY.getCode());
                                afUserCouponService.addUserCoupon(userCoupon);
                                AfCouponDo couponDoT = new AfCouponDo();
                                couponDoT.setRid(couponDo.getRid());
                                couponDoT.setQuotaAlready(1);
                                afCouponDao.updateCouponquotaAlreadyById(couponDoT);
                            }
                        }
						//修改订单状态为已完成
						/*logger.info("updateUserActivityGoodsInfo updateOrderStatus rid: " + orderInfo.getRid() );
						orderDao.updateOrderStatus(orderInfo.getRid());*/
						/*//获取资源信息

						AfResourceDo resourceInfo1 = afResourceService.getConfigByTypesAndSecType(Constants.SMS_TEMPLATE, Constants.SMS_ACTIVITY_RESERVATION_GOODS);
						if(resourceInfo1 != null){
							String content = resourceInfo1.getValue();
							//发送短信
							String mobile = orderInfo.getConsigneeMobile();
							logger.info("sendSMS mobile:" + mobile + "  content: " + content);
							smsUtil.sendSmsToDhstAishangjie(mobile, content);
						}*/
					}
					//付售价
				}else {
					logger.info("updateUserActivityGoodsInfo payEndAmount userId: " + orderInfo.getUserId() );

					//添加指定商品券
//					AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(Constants.TAC_ACTIVITY);
//					afUserCouponService.sendActivityCouponByGoods(orderInfo.getUserId(),CouponSenceRuleType.SELFSUPPORT_PAID.getCode(), resourceDo, orderInfo.getGoodsId());

					AfResourceDo resourceDo1 = afResourceService.getSingleResourceBytype(Constants.TAC_ACTIVITY);
					afUserCouponService.sendActivityCouponByCouponGroupRandom(orderInfo.getUserId(),CouponSenceRuleType.SELFSUPPORT_PAID.getCode(), resourceDo1);

					//查询预售活动商品
					List<AfActivityReservationGoodsUserDo>  AfActivityReservationGoodsUserDoList = afActivityReservationGoodsUserDao.getActivityReservationGoodsList(map);
					if(null != AfActivityReservationGoodsUserDoList &&  AfActivityReservationGoodsUserDoList.size() > 0){
						AfActivityReservationGoodsUserDo afActivityReservationGoodsUserDo1 = AfActivityReservationGoodsUserDoList.get(0);
						//查询是否已经购买, 当购买数量大于0时 更新购买数量
						afActivityReservationGoodsUserDo.setGoodsId(afActivityReservationGoodsUserDo1.getRid());
						if(afActivityReservationGoodsUserDo1.getGoodsCount() > 0){
							for(int i = 0; i < orderInfo.getCount(); i++){
								afActivityReservationGoodsUserDao.updateReservationInfo( afActivityReservationGoodsUserDo1.getRid(), orderInfo.getUserId(), 2);
							}
						}
					}
				}
			}
		}
	}
}