/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
        AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(AfResourceType.SIGNIN.getCode());
        Map<String, Object> data = new HashMap<String, Object>();
        if(afResourceDo==null){
        	return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED); 

        }
        if (afSigninDo==null) {
        	data.put("seriesCount", 0);
        	data.put("isSignin", "T");
        	
		}else{
			Date seriesTime = afSigninDo.getGmtSeries();
			if(DateUtil.isSameDay(new Date(), seriesTime)){
	        	data.put("isSignin", "F");
			}else{
				if(DateUtil.isSameDay(DateUtil.getCertainDay(-1),seriesTime)){
		        	data.put("seriesCount", afSigninDo.getSeriesCount());
				}else{
		        	data.put("seriesCount", 0);
				}
	        	data.put("isSignin", "T");
			}
		}
		data.put("cycle", afResourceDo.getValue());
    	data.put("ruleSignin", afResourceDo.getValue3());

        resp.setResponseData(data);

		return resp;
	}

}
