/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.biz.util.ActiveRuleEngineUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.Source;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSigninDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月8日下午4:04:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("signinApi")
public class SigninApi implements ApiHandle {

	@Resource
	AfSigninService afSigninService;

	@Resource
	AfResourceService afResourceService;

	@Resource
	ActiveRuleEngineUtil activeRuleEngineUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype("SIGNIN");
		if (afResourceDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);

		}

		Long seriesCount = (long) 1;
		Long totalCount = (long) 0;
		if (afSigninDo == null) {
			afSigninDo = new AfSigninDo();
			totalCount += 1;
			afSigninDo.setSeriesCount(seriesCount);
			afSigninDo.setTotalCount(totalCount);
			afSigninDo.setUserId(userId);
			if (afSigninService.addSignin(afSigninDo) > 0) {
				return resp;
			}

		} else {
			Long cycle = NumberUtil.objToLongDefault(afResourceDo.getValue(), 1);
			Date seriesTime = afSigninDo.getGmtSeries();
			if (DateUtil.isSameDay(new Date(), seriesTime)) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_SIGNIN_AGAIN_ERROR);

			}

			// 当连续签到天数小于循环周期时
			if (DateUtil.isSameDay(DateUtil.getCertainDay(-1), seriesTime) && cycle != afSigninDo.getSeriesCount()) {
				seriesCount = afSigninDo.getSeriesCount() + 1;
			}
																																																																																																																							
			totalCount = afSigninDo.getTotalCount() + 1;
			afSigninDo.setSeriesCount(seriesCount);
			afSigninDo.setTotalCount(totalCount);
			afSigninDo.setUserId(userId);
			Map<String, Object> inputData = new HashMap<String, Object>();
			inputData.put("userId", userId);
			inputData.put("seriesCount", seriesCount.toString());
			if (afSigninService.changeSignin(afSigninDo) > 0) {
				activeRuleEngineUtil.signin(inputData, Source.APP);
				return resp;

			}
		}

		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}

}
