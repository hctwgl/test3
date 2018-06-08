package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfUserCouponStatus;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.util.RandomUtil;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 *@类描述：AfUserCouponServiceImpl
 *@author 何鑫 2017年1月20日  13:07:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserCouponService")
public class AfUserCouponServiceImpl implements AfUserCouponService{
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Resource
	private AfUserCouponDao afUserCouponDao;
	@Resource
	private AfCouponService afCouponService;
	@Resource
	private AfUserAccountDao afUserAccountDao;
	@Resource
	private AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfUserCouponService afUserCouponService;

	@Resource
	private AfResourceService afResourceService;

	@Override
	public List<AfUserCouponDto> getUserCouponByUser(AfUserCouponQuery query) {
		return afUserCouponDao.getUserCouponByUser(query);
	}

	@Override
	public List<AfUserCouponDto> getUserResevrationCouponList(Long userId) {
		return afUserCouponDao.getUserResevrationCouponList(userId);
	}

	@Override
	public int getUserCouponByUserNouse(Long userId) {
		return afUserCouponDao.getUserCouponByUserNouse(userId);
	}


	@Override
	public int getUserCouponByUserIdAndCouponId(Long userId, Long couponId) {
		return afUserCouponDao.getUserCouponByUserIdAndCouponId(userId, couponId);
	}


	@Override
	public int addUserCoupon(AfUserCouponDo afUserCouponDo) {
		return afUserCouponDao.addUserCoupon(afUserCouponDo);
	}

	
	@Override
	public BigDecimal getUserCouponByInvite(Long userId) {
		return afUserCouponDao.getUserCouponByInvite(userId);
	}
	@Override
	public AfUserCouponDto getUserCouponById(Long rid) {
		return afUserCouponDao.getUserCouponById(rid);
	}

	@Override
	public int updateUserCouponSatusUsedById(Long rid) {
		return afUserCouponDao.updateUserCouponSatusUsedById(rid);
	}

	@Override
	public List<AfUserCouponDto> getUserCouponByUserIdAndType(Long userId,String type,BigDecimal amount) {
		return afUserCouponDao.getUserCouponByUserIdAndType(userId, type, amount);
	}

	
	@Override
	public List<AfUserCouponDto> getUserCouponByType(Long userId, String type) {
		return afUserCouponDao.getUserCouponByType(userId, type);
	}
	
	@Override
	public void grantCoupon(Long userId, Long couponId, String sourceType, String sourceRef) {
		logger.info("grantCoupon, userId={}, couponId={}, sourceType={}", userId, couponId, sourceType, sourceRef);

		AfCouponDo couponDo = afCouponService.getCouponById(couponId);
		if(couponDo == null){
			throw new FanbeiException("no coupon",FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR);
		}
		if(couponDo.getQuota().intValue() > 0 && couponDo.getQuotaAlready() >= couponDo.getQuota().intValue()){
			throw new FanbeiException("no coupon",FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR);
		}
		if(couponDo.getLimitCount() > 0 && afUserCouponDao.getUserCouponByUserIdAndCouponId(userId, couponId) >= couponDo.getLimitCount()){
			throw new FanbeiException("no coupon",FanbeiExceptionCode.USER_GET_COUPON_ERROR);
		}
		if(CouponType.CASH.getCode().equals(couponDo.getType())){
			AfUserAccountDo accountDo = new AfUserAccountDo();
			accountDo.setUserId(userId);
			accountDo.setRebateAmount(couponDo.getAmount());
			int count = afUserAccountDao.updateUserAccount(accountDo);
			if(count > 0){
				AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
				accountLog.setAmount(couponDo.getAmount());
				accountLog.setType(sourceType);
				accountLog.setRefId(sourceRef);
				accountLog.setUserId(userId);
				afUserAccountLogDao.addUserAccountLog(accountLog);
			}
		}else{
			AfUserCouponDo userCoupon = new AfUserCouponDo();
			userCoupon.setCouponId(couponId);
			if("D".equals(couponDo.getExpiryType())){//固定天数
				Date current = new Date();
				userCoupon.setGmtStart(current);
				userCoupon.setGmtEnd(DateUtil.addDays(current, couponDo.getValidDays()));
			}else{//固定时间范围
				userCoupon.setGmtEnd(couponDo.getGmtEnd());
				userCoupon.setGmtStart(couponDo.getGmtStart());
			}
			userCoupon.setSourceType(sourceType);
			userCoupon.setSourceRef(sourceRef);
			userCoupon.setUserId(userId);
			userCoupon.setStatus(CouponStatus.NOUSE.getCode());
			afUserCouponDao.addUserCoupon(userCoupon);
		}
	}

	@Override
	public void grantCouponForRedRain(Long userId, Long couponId, String sourceType, String sourceRef) {
		logger.info("grantCouponForRedRain, userId={}, couponId={}, sourceType={}", userId, couponId, sourceType, sourceRef);

		AfCouponDo couponDo = afCouponService.getCouponById(couponId);
		if(couponDo == null){
			throw new FanbeiException("no coupon",FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR);
		}

//		红包雨送卷不必校验配额
//		if(couponDo.getQuota().intValue() > 0 && couponDo.getQuotaAlready() >= couponDo.getQuota().intValue()){
//			throw new FanbeiException("no coupon",FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR);
//		}

//		已经和产品与运营确认，此处不必拦截
//		if(couponDo.getLimitCount() > 0 && afUserCouponDao.getUserCouponByUserIdAndCouponId(userId, couponId) >= couponDo.getLimitCount()){
//			throw new FanbeiException("no coupon",FanbeiExceptionCode.USER_GET_COUPON_ERROR);
//		}

		if(CouponType.CASH.getCode().equals(couponDo.getType())){
			AfUserAccountDo accountDo = new AfUserAccountDo();
			accountDo.setUserId(userId);
			accountDo.setRebateAmount(couponDo.getAmount());
			int count = afUserAccountDao.updateUserAccount(accountDo);
			if(count > 0){
				AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
				accountLog.setAmount(couponDo.getAmount());
				accountLog.setType(sourceType);
				accountLog.setRefId(sourceRef);
				accountLog.setUserId(userId);
				try { //此处抛出异常不影响整体逻辑，放行
					afUserAccountLogDao.addUserAccountLog(accountLog);
				}catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}else{
			AfUserCouponDo userCoupon = new AfUserCouponDo();
			userCoupon.setCouponId(couponId);
			if("D".equals(couponDo.getExpiryType())){//固定天数
				Date current = new Date();
				userCoupon.setGmtStart(current);
				userCoupon.setGmtEnd(DateUtil.addDays(current, couponDo.getValidDays()));
			}else{//固定时间范围
				userCoupon.setGmtEnd(couponDo.getGmtEnd());
				userCoupon.setGmtStart(couponDo.getGmtStart());
			}
			userCoupon.setSourceType(sourceType);
			userCoupon.setSourceRef(sourceRef);
			userCoupon.setUserId(userId);
			userCoupon.setStatus(CouponStatus.NOUSE.getCode());
			afUserCouponDao.addUserCoupon(userCoupon);
		}
	}

	
	public int sentFirstAuthShoppingUserCoupon(AfOrderDo afOrder){
	        Long count = 0L;
		HashMap map = afOrderService.getCountPaidOrderByUserAndOrderType(afOrder.getUserId(),afOrder.getOrderType());
		
		try {
			count = (Long) map.get("count");
			if (count > 1)
				return 0;
		        } catch (Exception e) {
			  logger.error("sentFirstAuthShoppingUserCoupon error userId=" + afOrder.getUserId());
		    }
		
		String tag = "_FIRST_SHOPPING_";
		String sourceType = "FIRST_SHOPPING";
		String log = String.format("sentUserCouponGroup for first auth shopping afOrder = %s", JSONObject.toJSONString(afOrder));
		logger.info(log);
		
		 int countNum =  afUserCouponService.getUserCouponByUserIdAndCouponCource(afOrder.getUserId(), sourceType);
		    //该用户是否拥有该类型优惠券
		 if(countNum >0){
		         return 0;   
	        }
	        String msg = sentUserCouponGroup(afOrder.getUserId(),tag,sourceType);
	        log =log + String.format("msg = %s", msg);
		logger.info(log);
		return 1;
		
		
	}
	  public String sentUserCouponGroup(Long userId,String tag,String sourceType){
		//给该用户送优惠券
	        String MsgCode = "";
	        String log = String.format("sentUserCouponGroup start userId = %s", userId);
		logger.info(log);
	        log =log + String.format("sourceType = %s", sourceType);
	        logger.info(log);
	    try{
		AfCouponCategoryDo  couponCategory  = afCouponCategoryService.getCouponCategoryByTag(tag);
		if(couponCategory != null){
		    	String coupons = couponCategory.getCoupons();
			JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
			for (int i = 0; i < couponsArray.size(); i++) {
				String couponId = (String) couponsArray.getString(i);
				AfCouponDo couponDo = afCouponService.getCouponById(Long.parseLong(couponId));
				if (couponDo != null) {
				    //赠送优惠券
				        //Integer limitCount = couponDo.getLimitCount();
				        //有一个优惠券不符合要求就不送    
				    
					Integer myCount = afUserCouponService.getUserCouponByUserIdAndCouponId(userId,couponDo.getRid());
					if (1 <= myCount) {
					    //continue;
					    MsgCode = "LEAD";
					    log =log + String.format("MsgCode = %s", MsgCode);
					    logger.info(log);
					    return MsgCode;
					}
					Long totalCount = couponDo.getQuota();
					if (totalCount != -1 && totalCount != 0 && totalCount <= couponDo.getQuotaAlready()) {
					   // continue;
					    MsgCode = "LEAD_END";
					    log =log + String.format("MsgCode = %s", MsgCode);
					    logger.info(log);
					    return MsgCode;
					}
					
					AfUserCouponDo userCoupon = new AfUserCouponDo();
					userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
					userCoupon.setGmtStart(new Date());
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
					userCoupon.setSourceType(sourceType);
					userCoupon.setStatus(CouponStatus.NOUSE.getCode());
					userCoupon.setUserId(userId);
					afUserCouponService.addUserCoupon(userCoupon);
					AfCouponDo couponDoT = new AfCouponDo();
					couponDoT.setRid(couponDo.getRid());
					couponDoT.setQuotaAlready(1);
					afCouponService.updateCouponquotaAlreadyById(couponDoT);
					MsgCode = "SUCCESS";
					log =log + String.format("MsgCode = %s", MsgCode);
					logger.info(log);
			       }
			  }
		  }
	        }catch (Exception e) {
			logger.info("sent user couponGroup:", e);
	    }
		return MsgCode;
     }
		
	

	@Override
	public List<AfUserCouponDto> getUserAcgencyCouponByAmount(Long userId, BigDecimal amount) {
		return afUserCouponDao.getUserAcgencyCouponByAmount(userId, amount);
	}

	@Override
	public List<AfUserCouponDto> getSpecialGoodsCouponByAmount(Long userId, BigDecimal amount) {
		return afUserCouponDao.getSpecialGoodsCouponByAmount(userId, amount);
	}


	@Override
	public int updateUserCouponSatusNouseById(Long rid) {
		return afUserCouponDao.updateUserCouponSatusNouseById(rid);
	}


	@Override
	public int updateUserCouponSatusExpireById(Long rid) {
		return afUserCouponDao.updateUserCouponSatusExpireById(rid);
	}


	@Override
	public int getUserAcgencyCountByAmount(Long userId, BigDecimal amount, Long goodsId) {
		return afUserCouponDao.getUserAcgencyCountByAmount(userId, amount, goodsId);
	}

	@Override

	public List<AfUserCouponDto> getUserCouponByUserIdAndSourceType(Long userId,
			String sourceType) {
		
		return afUserCouponDao.getUserCouponByUserIdAndSourceType(userId, sourceType);
	}

	

	public AfUserCouponDo getUserCouponByDo(AfUserCouponDo afUserCouponDo) {
		return afUserCouponDao.getUserCouponByDo(afUserCouponDo);
	}

	@Override
	public AfUserCouponDto getSubjectUserCouponByAmountAndCouponId(Long userId,
			BigDecimal actualAmount, String couponId) {
		return afUserCouponDao.getSubjectUserCouponByAmountAndCouponId(userId, actualAmount, couponId);
	}

	@Override
	public List<AfUserCouponDto> getUserCouponListByUserIdAndCouponId(Long userId, Long couponId) {
		return afUserCouponDao.getUserCouponListByUserIdAndCouponId(userId, couponId);
	}

	@Override
	public List<AfUserCouponDto> getActivitySpecialCouponByAmount(Long userId, BigDecimal amount,Long activityId, String activityType) {
		return afUserCouponDao.getActivitySpecialCouponByAmount(userId, amount,activityId,activityType);
	}

	@Override
	public Integer getUserCouponByUserIdAndCouponCource(Long userId, String sourceType) {
	    // TODO Auto-generated method stub
	    	return afUserCouponDao.getUserCouponByUserIdAndCouponCource(userId,sourceType);
	}

	@Override
	public List<AfUserCouponDto> getUserAllAcgencyCouponByAmount(Long userId, BigDecimal actualAmount) {
		return afUserCouponDao.getUserAllAcgencyCouponByAmount(userId,actualAmount);
	}

	@Override
	public List<AfUserCouponDto> getUserAllCoupon() {
		return afUserCouponDao.getUserAllCoupon();
	}

	@Override
	public List<AfUserCouponDto> getUserAllCouponByUserId(Long userId) {
		return afUserCouponDao.getUserAllCouponByUserId(userId);
	}

	@Override
	public List<AfUserCouponDto> getH5UserCouponByUser(Long userId, String status) {
		return afUserCouponDao.getH5UserCouponByUser(userId,status);
	}


	@Override
	public AfUserCouponDo sendActivityCouponByCouponGroupRandom(Long userId, String couponSenceRuleType, AfResourceDo resourceDo){
		try {
			if(null != resourceDo) {
				String value = resourceDo.getValue();
				String[] activityPeriodTime = value.split(",");
				Date startTime = DateUtil.parseDateTimeShortExpDefault(activityPeriodTime[0], null);
				Date endTime = DateUtil.parseDateTimeShortExpDefault(activityPeriodTime[1], null);
				if (null != startTime && null != endTime) {
					Date now = new Date();
					// 在活动期间
					if (DateUtil.compareDate(now, startTime) && DateUtil.compareDate(endTime, now)) {
						String couponGroupId = resourceDo.getValue1();
						if (StringUtils.isNotEmpty(couponGroupId)) {
							AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryById(couponGroupId);
							if (null != couponCategory) {
								String coupons = couponCategory.getCoupons();
								JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
								int size = couponsArray.size();
								int index = RandomUtil.getRandomInt(size - 1);
								Long couponId = Long.parseLong(couponsArray.getString(index));

								AfCouponDo couponDo = afCouponService.getCouponById(couponId);
								AfUserCouponDo userCoupon = new AfUserCouponDo();
								userCoupon.setCouponId(couponDo.getRid());
								userCoupon.setGmtCreate(new Date());
								userCoupon.setGmtStart(couponDo.getGmtStart());
								userCoupon.setGmtEnd(couponDo.getGmtEnd());
								userCoupon.setUserId(userId);
								userCoupon.setStatus(AfUserCouponStatus.NOUSE.getCode());
								userCoupon.setSourceType(couponSenceRuleType);
								afUserCouponService.addUserCoupon(userCoupon);
								AfCouponDo couponDoT = new AfCouponDo();
								couponDoT.setRid(couponDo.getRid());
								couponDoT.setQuotaAlready(1);
								afCouponService.updateCouponquotaAlreadyById(couponDoT);

								return userCoupon;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("random send coupon error.", e);
		}

		return null;
	}

	@Override
	public AfUserCouponDo sendActivityCouponByGoods(Long userId, String couponSenceRuleType, AfResourceDo resourceDo, Long goodsId){
		try {
			if(null != resourceDo) {
				String value = resourceDo.getValue();
				String[] activityPeriodTime = value.split(",");
				Date startTime = DateUtil.parseDateTimeShortExpDefault(activityPeriodTime[0], null);
				Date endTime = DateUtil.parseDateTimeShortExpDefault(activityPeriodTime[1], null);
				if (null != startTime && null != endTime) {
					Date now = new Date();
					// 在活动期间
					if (DateUtil.compareDate(now, startTime) && DateUtil.compareDate(endTime, now)) {
						String[] goodsArr = resourceDo.getValue1().split(",");
						String[] couponArr = resourceDo.getValue().split(",");
						if (null != goodsArr && goodsArr.length > 0) {
							Long couponId = null;
							for(int i = 0; i < goodsArr.length; i++){
								if(goodsId == Long.valueOf(goodsArr[i].toString())){
									couponId = Long.valueOf(couponArr[i].toString());
									break;
								}
							}
							if (null != couponId) {
								AfCouponDo couponDo = afCouponService.getCouponById(couponId);
								AfUserCouponDo userCoupon = new AfUserCouponDo();
								userCoupon.setCouponId(couponDo.getRid());
								userCoupon.setGmtCreate(new Date());
								userCoupon.setGmtStart(couponDo.getGmtStart());
								userCoupon.setGmtEnd(couponDo.getGmtEnd());
								userCoupon.setUserId(userId);
								userCoupon.setStatus(AfUserCouponStatus.NOUSE.getCode());
								userCoupon.setSourceType(couponSenceRuleType);
								afUserCouponService.addUserCoupon(userCoupon);
								AfCouponDo couponDoT = new AfCouponDo();
								couponDoT.setRid(couponDo.getRid());
								couponDoT.setQuotaAlready(1);
								afCouponService.updateCouponquotaAlreadyById(couponDoT);

								return userCoupon;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("random send coupon error.", e);
		}

		return null;
	}


	@Override
	public AfUserCouponDto getUserCouponAfterPaidSuccess(Long userId){
		return afUserCouponDao.getUserCouponAfterPaidSuccess(userId);
	}

	@Override
	public List<AfUserCouponDto> getUserCouponByTypeV1(Long userId, String type, String repaymentType) {
		return afUserCouponDao.getUserCouponByTypeV1(userId,type,repaymentType);
	}

	@Override
	public List<AfUserCouponDto> getUserBillCouponByUserIdAndType(Long userId, String code, BigDecimal repayAmount) {
		return afUserCouponDao.getUserBillCouponByUserIdAndType(userId,code,repayAmount);
	}

	@Override
	public List<AfUserCouponDto> getUserOldAllAcgencyCouponByAmount(Long userId, BigDecimal actualAmount) {
		return afUserCouponDao.getUserOldAllAcgencyCouponByAmount(userId,actualAmount);
	}

}
