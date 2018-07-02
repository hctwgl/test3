/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月29日下午1:18:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("pickBrandCouponApi")
public class PickBrandCouponApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long couponSceneId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("couponSceneId"), null);
			if (couponSceneId == null) {
				logger.error("couponSceneId is empty");
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
			}
			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(couponSceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
			}
			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(context.getUserId()+StringUtils.EMPTY);
			
			String status = resourceInfo.getValue3();
			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);
			
			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START);
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END);
			}
			
			AfUserAuthDo authInfo = afUserAuthService.getUserAuthInfoByUserId(context.getUserId());
			if (status.equals("REAL") && "N".equals(authInfo.getRealnameStatus())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_REAL_NAME);
			}
			
			String resultString = HttpUtil.doHttpPost(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			
			JSONObject resultJson = JSONObject.parseObject(resultString);
			if (!"0".equals(resultJson.getString("code"))) {
				new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
			}
			return resp;
		} catch (Exception e) {
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
		}
	}

}
