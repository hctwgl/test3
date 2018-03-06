package com.ald.fanbei.api.web.api.resource;

import com.ald.fanbei.api.biz.service.AfRedRainService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：服务中心页面 的提示语
 * @author cfp 2017年9月30日
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getServiceHintsApi")
public class GetServiceHintsApi implements ApiHandle{

	@Resource
	AfResourceService afResourceService;

	private static final String RESOURCE_TYPE = "HINTS";

	private static final String SEC_TYPE = "SERVICE_HINTS";

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String,Object> data = new HashMap<>();
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(RESOURCE_TYPE,SEC_TYPE);
		if(StringUtils.equals("1",afResourceDo.getValue1())){
			data.put("hint",afResourceDo.getValue());
		}else {
			data.put("hint","");
		}

		resp.setResponseData(data);
		return resp;
	}

}
