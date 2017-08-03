package com.ald.fanbei.api.web.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：getCallCenterInfoApi
 *@author 何鑫 2017年1月20日  17:01:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCallCenterInfoApi")
public class GetCallCenterInfoApi implements ApiHandle{

	@Resource
	private AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);

		List<AfResourceDo> resourceList = afResourceService.getResourceListByType(Constants.RES_APP_CALL_CENTER_MESSAGE);
        Map<String, Object> data = getResourceMap(resourceList);
        resp.setResponseData(data);
		return resp;
	}
	
	private Map<String, Object> getResourceMap(List<AfResourceDo> resourceList){
		String qqNumber = StringUtil.EMPTY,qqGroupNumber = StringUtil.EMPTY,
				wxNumber = StringUtil.EMPTY,onlineTime = StringUtil.EMPTY,telNumber = StringUtil.EMPTY;
		for (AfResourceDo afResourceDo : resourceList) {
        	if(Constants.RES_APP_CALL_CENTER_MESSAGE_QQ.equals(afResourceDo.getSecType())){
        		qqNumber = afResourceDo.getValue();
        	}else if(Constants.RES_APP_CALL_CENTER_MESSAGE_QQGROUP.equals(afResourceDo.getSecType())){
        		qqGroupNumber = afResourceDo.getValue();
        	}else if(Constants.RES_APP_CALL_CENTER_MESSAGE_TEL.equals(afResourceDo.getSecType())){
        		telNumber = afResourceDo.getValue();
        	}else if(Constants.RES_APP_CALL_CENTER_MESSAGE_ONLINE.equals(afResourceDo.getSecType())){
        		onlineTime = afResourceDo.getValue();
        	}else if(Constants.RES_APP_CALL_CENTER_MESSAGE_WX.equals(afResourceDo.getSecType())){
        		wxNumber = afResourceDo.getValue();
        	}
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("qqNumber", qqNumber);
        data.put("qqGroupNumber", qqGroupNumber);
        data.put("wxNumber", wxNumber);
        data.put("serviceTime", onlineTime);
        data.put("serviceTel", telNumber);
		return data;
	}
}
