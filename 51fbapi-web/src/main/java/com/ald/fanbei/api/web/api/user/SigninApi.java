/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.service.AfCouponSceneService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.ald.fanbei.api.dal.domain.AfSigninDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	CouponSceneRuleEnginerUtil activeRuleEngineUtil;
	@Resource
	AfCouponSceneService afCouponSceneService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
		 AfCouponSceneDo afCouponSceneDo = afCouponSceneService.getCouponSceneByType(CouponSenceRuleType.SIGNIN.getCode());
	        if(afCouponSceneDo==null){
	        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED); 
	        }
	        Integer cycle = 1;
	        List<CouponSceneRuleBo> ruleBoList=   afCouponSceneService.getRules(CouponSenceRuleType.SIGNIN.getCode(), "signin");
	        
	        if(ruleBoList.size()==0){
	        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED); 

	        }

	        CouponSceneRuleBo ruleBo = ruleBoList.get(0);
	        cycle= NumberUtil.objToIntDefault(ruleBo.getCondition(), 1) ;
	 	   
		Integer seriesCount =  1;
		Integer totalCount =  0;
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

			if ( afSigninService.changeSignin(afSigninDo) > 0) {
				if(seriesCount == cycle){
					activeRuleEngineUtil.signin(userId);
				}
			
				return resp;

			}
		}

		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
	}

}
