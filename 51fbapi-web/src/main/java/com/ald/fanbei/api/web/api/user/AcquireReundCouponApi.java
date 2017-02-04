/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午5:58:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("acquireReundCouponApi")
public class AcquireReundCouponApi implements ApiHandle {


	@Resource
	private AfUserCouponService afUserCouponService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
        Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(params.get("couponId")), 0);
        Integer limitCount = NumberUtil.objToIntDefault(ObjectUtils.toString(params.get("limitCount")), 0);

        
        if (couponId == 0) {
			throw new FanbeiException("coupon id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
       Integer count = afUserCouponService.getUserCouponByUserIdAndCouponId(userId, couponId);
       if(count>=limitCount){
			throw new FanbeiException("coupon limitCount more than get count", FanbeiExceptionCode.USER_GET_COUPON_ERROR);
       }
       
       
       
		return resp;
	}

}
