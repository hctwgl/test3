package com.ald.fanbei.api.web.api.coupon;

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.CouponWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.druid.util.StringUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component("getPickCouponApi")
public class GetPickCouponApi implements ApiHandle {
    @Resource
    AfUserDao afUserDao;
    @Resource
    AfCouponService afCouponService;
    @Resource
    AfUserCouponDao afUserCouponDao;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        try {
            ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
            Long couponId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("couponId"), 0l);
            //AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
            Map<String, Object> returnData = new HashMap<String, Object>();

            if (couponId==0l) {
                throw new FanbeiException("getPickCouponApi couponId not exist error", FanbeiExceptionCode.REQUEST_PARAM_ILLEGAL);
            }
            Long userId = context.getUserId();
            if (userId == null) {
                throw new FanbeiException("getPickCouponApi userId not exist error", FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
            }
            AfCouponDo couponDo = afCouponService.getCouponById(couponId);
            if (couponDo == null) {
                throw new FanbeiException("getPickCouponApi couponDo not exist error", FanbeiExceptionCode.BORROW_CASH_COUPON_NOT_EXIST_ERROR);
            }

            Integer limitCount = couponDo.getLimitCount();
            Integer myCount = afUserCouponDao.getUserCouponByUserIdAndCouponId(userId,
                    NumberUtil.objToLongDefault(couponId, 1l));
            if (limitCount <= myCount) {
                returnData.put("status", CouponWebFailStatus.CouponOver.getCode());
                throw new FanbeiException("getPickCouponApi couponDo limitCount error", FanbeiExceptionCode.USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR);
            }
            Long totalCount = couponDo.getQuota();
            if (totalCount != -1 && totalCount != 0 && totalCount <= couponDo.getQuotaAlready()) {
                returnData.put("status", CouponWebFailStatus.MoreThanCoupon.getCode());

                throw new FanbeiException("getPickCouponApi couponDo limitCount error", FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR);
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
            userCoupon.setSourceType(CouponSenceRuleType.PICK.getCode());
            userCoupon.setStatus(CouponStatus.NOUSE.getCode());
            userCoupon.setUserId(userId);
            afUserCouponDao.addUserCoupon(userCoupon);
            AfCouponDo couponDoT = new AfCouponDo();
            couponDoT.setRid(couponDo.getRid());
            couponDoT.setQuotaAlready(1);
            afCouponService.updateCouponquotaAlreadyById(couponDoT);
            logger.info("pick coupon success", couponDoT);
            return resp;
        } catch (FanbeiException ex){
            return new ApiHandleResponse(requestDataVo.getId(), ex.getErrorCode());
        } catch (Exception e) {
            logger.error("pick coupon error", e);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED);
        }
    }
}
