/**
 * 
 */
package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：话费充值下单
 * @author 何鑫 2017年2月16日下午14:15:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("mobileChargeApi")
public class MobileChargeApi implements ApiHandle {

	@Resource
	AfUserCouponService afUserCouponService;

	@Resource
	private AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
		String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
		BigDecimal money = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("amount")),BigDecimal.ZERO);
		BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
		AfUserCouponDto coupon =  afUserCouponService.getUserCouponById(couponId);
		if(null != coupon &&!coupon.getStatus().equals(CouponStatus.NOUSE.getCode())){
			throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
		}
		//TODO:支付流程
		if(afOrderService.createMobileChargeOrder(context.getUserName(),userId, coupon, money, mobile, rebateAmount)<=0){
			throw new FanbeiException(FanbeiExceptionCode.FAILED);
		}
		return resp;
	}
}
