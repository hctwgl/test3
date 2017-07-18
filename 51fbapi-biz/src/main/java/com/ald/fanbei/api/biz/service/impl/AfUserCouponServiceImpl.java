package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;

/**
 * 
 *@类描述：AfUserCouponServiceImpl
 *@author 何鑫 2017年1月20日  13:07:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserCouponService")
public class AfUserCouponServiceImpl implements AfUserCouponService{

	@Resource
	private AfUserCouponDao afUserCouponDao;
	@Resource
	private AfCouponService afCouponService;
	@Resource
	private AfUserAccountDao afUserAccountDao;
	@Resource
	private AfUserAccountLogDao afUserAccountLogDao;
	
	@Override
	public List<AfUserCouponDto> getUserCouponByUser(AfUserCouponQuery query) {
		return afUserCouponDao.getUserCouponByUser(query);
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
			userCoupon.setUserId(userId);
			userCoupon.setStatus(CouponStatus.NOUSE.getCode());
			afUserCouponDao.addUserCoupon(userCoupon);
		}
	}


	@Override
	public List<AfUserCouponDto> getUserAcgencyCouponByAmount(Long userId, BigDecimal amount) {
		return afUserCouponDao.getUserAcgencyCouponByAmount(userId, amount);
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

}
