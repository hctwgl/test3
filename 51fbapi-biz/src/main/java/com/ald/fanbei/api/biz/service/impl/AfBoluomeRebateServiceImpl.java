package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.UserCouponSource;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBoluomeRebateDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketRelationDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeRedpacketThresholdDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeRebateDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketThresholdDo;
import com.ald.fanbei.api.dal.domain.AfRebateDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;

/**
 * 点亮活动新版ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:25 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBoluomeRebateService")
public class AfBoluomeRebateServiceImpl extends ParentServiceImpl<AfBoluomeRebateDo, Long>
		implements AfBoluomeRebateService {

	private static final Logger logger = LoggerFactory.getLogger(AfBoluomeRebateServiceImpl.class);

	@Resource
	private AfBoluomeRebateDao afBoluomeRebateDao;
	@Resource
	AfOrderDao afOrderDao;
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfBoluomeRedpacketThresholdDao thresholdDao;
	@Resource
	AfBoluomeRedpacketDao dao;
	@Resource
	AfBoluomeRedpacketRelationDao relationDao;
	@Resource
	AfUserDao afUserDao;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	AfTaskUserService afTaskUserService;
	

	@Override
	public BaseDao<AfBoluomeRebateDo, Long> getDao() {
		return afBoluomeRebateDao;
	}

	@Resource
	JpushService jpushService;
	@Resource
	BizCacheUtil bizCacheUtil;

	/**
	 * 
	 * @Title: addRedPacket @author qiao @date 2017年11月17日
	 *         下午3:59:57 @Description: the second time light activity some
	 *         logics during the order is finished . @param orderId @param
	 *         userId @throws Exception @throws
	 */
	@Override
	public void addRedPacket(Long orderId, Long userId) throws Exception {
		try {
				// check if this orderId has already been rebated
				int isHave = afBoluomeRebateDao.getRebateNumByOrderId(orderId);
				if (isHave == 0) {
				    String activityTime = null;
				    AfResourceDo startTime = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_TIME");
				    	   if(startTime != null){
				    	       activityTime =   startTime.getValue();
				    	   }
					String log = String.format("addRedPacket || params : orderId = %s , userId = %s", orderId, userId);
					logger.info(log);
					AfBoluomeRebateDo rebateDo = new AfBoluomeRebateDo();

					rebateDo.setOrderId(orderId);
					rebateDo.setUserId(userId);
					// check if its the first time for one specific channel
					int orderTimes = afOrderDao.findFirstOrder(orderId, userId,activityTime);
					log = log + String.format("Middle business params : orderTimes = %s ", orderTimes);
					logger.info(log);
					if (orderTimes == 0) {
    					     int boluomeFinishOrderTimes =  afOrderDao.getCountFinishBoluomeOrderByUserId(userId,null);
    					     if(boluomeFinishOrderTimes == 1){
            					     //邀请有礼记录用户订单id
            					    AfRecommendUserDo  afRecommendUserDo  = afRecommendUserService.getARecommendUserById(userId);
            					     if(afRecommendUserDo != null){
            						 if(afRecommendUserDo.getFirstBoluomeOrder() == null){
            						     afRecommendUserDo.setFirstBoluomeOrder(orderId);
            						     int updateRecommend = afRecommendUserService.updateRecommendUserById(afRecommendUserDo);
            						     log = log + String.format("updateRecommend = %s ", updateRecommend);
         						     logger.info(log);
            						  }
            					      }
            				       }
    					     
						rebateDo.setFirstOrder(1);

						// check if the order times for red packet
						
					 
					      //查询此次活动之后的返利次数。
					       int redOrderTimes = afBoluomeRebateDao.checkOrderTimes(userId,activityTime);
					    
					       //查询活动之前是否下过的菠萝觅订单，有(老用户)，则每次额外加1
//					       int boluomeFinishOrderBeforActivityTime =  afOrderDao.getCountBoluomeOrderByUserIdByActivityTime(userId,activityTime);
//					          if(boluomeFinishOrderBeforActivityTime >= 1 ){
//						       redOrderTimes = redOrderTimes +1 ;
//						  }
						
						//cqw
						log = log + String.format("redOrderTimes = %s ", redOrderTimes);
						logger.info(log);
						//本次加1
						redOrderTimes += 1;
					
						// check the red packet amount
						boolean flag = this.getAmountAndName(rebateDo, redOrderTimes);

						log = log + String.format("flag = %s ", flag);
						logger.info(log);
						if (flag) {
							// insert the table af_boluome_redpacket
							rebateDo.setGmtCreate(new Date());
							rebateDo.setGmtModified(new Date());
							int saveResult = afBoluomeRebateDao.saveRecord(rebateDo);

							log = log + String.format("saveResult = %s ", saveResult);
							logger.info(log);
							if (saveResult > 0) {

								// update the table af_user_account
								AfUserAccountDo accountDo = new AfUserAccountDo();
								accountDo.setUserId(userId);
								accountDo.setRebateAmount(rebateDo.getRebateAmount());
								int updateResult = afUserAccountDao.updateRebateAmount(accountDo);

								log = log + String.format("updateResult = %s ", updateResult);
								logger.info(log);

								if (updateResult > 0) {
									AfUserAccountLogDo logDo = new AfUserAccountLogDo();
									logDo.setAmount(rebateDo.getRebateAmount());
									logDo.setType("REBATE");
									logDo.setGmtCreate(new Date());
									logDo.setUserId(userId);
									logDo.setRefId(orderId.toString());
									int saveLogResult = afUserAccountLogDao.addUserAccountLog(logDo);

									// add by luoxiao for 边逛边赚，增加零钱明细
									afTaskUserService.addTaskUser(userId, UserAccountLogType.REBATE.getName(), rebateDo.getRebateAmount());
									// end by luoxiao

									log = log + String.format("saveLogResult = %s ", saveLogResult);
									logger.info(log);
								}

								// call Jpush for rebate
								String userName = convertToUserName(userId);
								log = log + String.format("userName = %s , rebateAmount = %s", userName,
										rebateDo.getRebateAmount());
								logger.info(log);
//								if (userName != null) {
//									String scence = afBoluomeRebateDao.getScence(orderId);
//									log = log + String.format(" rebateAmount = %s", rebateDo.getRebateAmount());
//									logger.info(log);
//									
//									//get couponId and couponName from afResource
//									AfResourceDo resourceDo = new AfResourceDo();
//									
//									resourceDo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "COUPON_AND_AMOUNT");
//									
//									String couponId = resourceDo.getValue();
//									String twenty = resourceDo.getValue1();
//									String thirty = resourceDo.getValue2();
//									
//									log = log + String.format(" afResource = %s", resourceDo.toString());
//									logger.info(log);
//									
//									//if this user Rebate amount is 20 than send a coupon 
//									if (rebateDo.getRebateAmount().compareTo(new BigDecimal(twenty)) == 0) {
//										if (StringUtil.isNotBlank(couponId)) {
//											
//											//add coupon to user 
//											Long couponIdL = NumberUtil.objToLong(couponId);
//											
//											log = log + String.format(" before grantCoupon parameters userId = %s ,couponId = %s ", userId.toString(),couponId.toString());
//											logger.info(log);
//											
//											afUserCouponService.grantCoupon(userId, couponIdL, UserCouponSource.GG_ACTIVITY.getName(), orderId.toString());
//											
//											log = log + String.format(" before grantCoupon");
//											logger.info(log);
//										}
//									}
//									
//									jpushService.sendRebateMsg(userName, scence, rebateDo.getRebateAmount().compareTo(new BigDecimal(twenty)) == 0?new BigDecimal(thirty):rebateDo.getRebateAmount());
//								}
							}
						}
					}
				}

		} catch (Exception e) {
			logger.error("afBoluomeRebateService.addRedPacket() error :", e);
			throw new Exception();
		} 

	}

	private String convertToUserName(Long userId) {
		AfUserDo userDo = afUserDao.getUserById(userId);
		String userName = "";
		if (userDo != null) {
			userName = userDo.getUserName();
		}
		return userName;
	}

	/**
	 * 
	 * @Title: getAmountAndName @author qiao @date 2017年11月15日
	 *         下午5:13:00 @Description: @param rebateDo @param
	 *         redOrderTimes @return void @throws
	 */
	boolean getAmountAndName(AfBoluomeRebateDo rebateDo, int redOrderTimes) {
		boolean result = false;
		List<AfBoluomeRedpacketThresholdDo> thresholdList = thresholdDao.getAllThreadholds();
		if (thresholdList != null && thresholdList.size() > 0) {
			Long thresholdId = 0L;

			for (AfBoluomeRedpacketThresholdDo thresholdDo : thresholdList) {
				if (redOrderTimes >= thresholdDo.getStart() && redOrderTimes <= thresholdDo.getEnd()) {
					thresholdId = thresholdDo.getRid();
					break;
				}
			}

			Long redpacketId = 0L;
			// get the relationDo
			List<Long> redpacketIdList = relationDao.getRedpacketIdListByThreshold(thresholdId);
			if (redpacketIdList != null && redpacketIdList.size() > 0) {
				int length = redpacketIdList.size();
				int index = new Random().nextInt(length) % (length - 1 + 1) + 1;
				redpacketId = redpacketIdList.get(index - 1);
			}

			if (redpacketId != 0L) {
				AfBoluomeRedpacketDo redpacketDo = dao.getById(redpacketId);
				if (redpacketDo != null) {
					rebateDo.setRebateAmount(redpacketDo.getAmount());
					result = true;
				}
			}

		}
		return result;
	}

	/**
	 * 
	 * @Title: getListByUserId @author qiao @date 2017年11月17日
	 *         下午12:59:03 @Description: 根据用户查所有返利 @param userId @return @throws
	 */
	@Override
	public List<AfBoluomeRebateDo> getListByUserId(Long userId,String startTime) {

		return afBoluomeRebateDao.getListByUserId(userId,startTime);
	}

	/**
	 * 
	 * @Title: getLightShopId @author qiao @date 2017年11月17日
	 *         下午3:59:24 @Description: @param orderId @return @throws
	 */
//	@Override
//	public Long getLightShopId(Long orderId) {
//		return afBoluomeRebateDao.getLightShopId(orderId);
//	}

	/**
	 * 
	 * @Title: getRebateList @author qiao @date 2017年11月17日
	 *         下午3:59:30 @Description: @param userId @return @throws
	 */
	@Override
	public List<AfRebateDo> getRebateList(Long userId, String startTime) {

		List<AfRebateDo> listRebateDo = afBoluomeRebateDao.getRebateList(userId, startTime);

		for (AfRebateDo do1 : listRebateDo) {
			String date = "";
			try {
				date = StringdateToString(do1.getConsumeTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			do1.setConsumeTime(date);
		}
		return listRebateDo;
	}

	public static String StringdateToString(String time) throws ParseException {
		SimpleDateFormat formatter;
		formatter = new SimpleDateFormat("yyyy-MM-dd");
		String ctime = formatter.format(formatter.parse(time));

		return ctime;
	}

	@Override
	public AfBoluomeRebateDo getLastUserRebateByUserId(Long userId) {
		// TODO Auto-generated method stub
		return afBoluomeRebateDao.getLastUserRebateByUserId(userId);
	}

	@Override
	public int getRebateCount(Long shopId, Long userId,String activityTime) {
		return afBoluomeRebateDao.getRebateCount(shopId, userId,activityTime);

	}

	@Override
	public AfBoluomeRebateDo getMaxUserRebateByStartIdAndEndIdAndUserId(Long startId, Long endId, Long userId) {
		// TODO Auto-generated method stub
		return afBoluomeRebateDao.getMaxUserRebateByStartIdAndEndIdAndUserId(startId, endId, userId);
	}

	@Override
	public int getCountByUserIdAndFirstOrder(Long userId, int firstOrder,String oneYuanTime) {
	    // TODO Auto-generated method stub
	    	return afBoluomeRebateDao.getCountByUserIdAndFirstOrder(userId,firstOrder,oneYuanTime);
	}


}