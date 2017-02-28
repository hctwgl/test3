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
import com.ald.fanbei.api.biz.service.AfSigninService;
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

/**
 * @类描述：
 * @author suweili 2017年2月7日下午5:16:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getSigninInfoApi")
public class GetSigninInfoApi implements ApiHandle {

	@Resource
	AfSigninService  afSigninService;
	@Resource
	AfCouponSceneService afCouponSceneService;


	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
        AfCouponSceneDo afCouponSceneDo = afCouponSceneService.getCouponSceneByType(CouponSenceRuleType.SIGNIN.getCode());
        if(afCouponSceneDo==null){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED); 

        }
        Integer seriesTotal = 1;
        
        List<CouponSceneRuleBo> ruleBoList=   afCouponSceneService.getRules(CouponSenceRuleType.SIGNIN.getCode(), "signin");
        
        if(ruleBoList.size()==0){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED); 

        }

        CouponSceneRuleBo ruleBo = ruleBoList.get(0);
 	   seriesTotal= NumberUtil.objToIntDefault(ruleBo.getCondition(), 1) ;
       Map<String, Object> data = new HashMap<String, Object>();
		data.put("cycle", seriesTotal);
    	data.put("ruleSignin",ObjectUtils.toString(afCouponSceneDo.getDescription(), "").toString()  );

        
        if (afSigninDo==null) {
        	data.put("seriesCount", 0);
        	data.put("isSignin", "T");
        	
		}else{
        	data.put("seriesCount", afSigninDo.getSeriesCount());

			Date seriesTime = afSigninDo.getGmtSeries();
			if(DateUtil.isSameDay(new Date(), seriesTime)){
	        	data.put("isSignin", "F");
	        	
			}else{
				if(!DateUtil.isSameDay(DateUtil.getCertainDay(-1),seriesTime)){
		        	data.put("seriesCount", 0);

				}
	        	data.put("isSignin", "T");
			}
			
			
		}

        resp.setResponseData(data);

		return resp;
	}

}
