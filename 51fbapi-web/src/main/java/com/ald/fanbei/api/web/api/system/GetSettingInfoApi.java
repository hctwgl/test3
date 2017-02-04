/**
 * 
 */
package com.ald.fanbei.api.web.api.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ResourceHomeType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author suweili
 *
 */
@Component("getSettingInfoApi")

public class GetSettingInfoApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		List<AfResourceDo> list = afResourceService.getConfigByTypes(ResourceHomeType.ResourceTypeSet.getCode());
		Map<String, Object> data = new HashMap<String, Object>();
		for (AfResourceDo afResourceDo : list) {
			if(StringUtils.equals(afResourceDo.getSecType(), "COMMON_PROBLEM")){
				
				data.put("commonProblem",afResourceDo.getValue() );
			}else if(StringUtils.equals(afResourceDo.getSecType(), "USING_HELP")){
				data.put("usingHelp", afResourceDo.getValue());
				
			}
		}
		resp.setResponseData(data);

		return resp;
	}

}
