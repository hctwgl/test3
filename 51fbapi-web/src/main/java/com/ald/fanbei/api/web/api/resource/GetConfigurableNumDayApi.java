package com.ald.fanbei.api.web.api.resource;

import com.ald.fanbei.api.biz.service.AfRedRainService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：获取小贷借钱天数(数据库配置)
 * @author ZJF 2017年9月30日
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getConfigurableNumDayApi")
public class GetConfigurableNumDayApi implements ApiHandle{

	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String,Object> data = new HashMap<>();
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
		String firstDay = "";
		String secondDay = "";
		if(null != afResourceDo){
			firstDay = afResourceDo.getTypeDesc().split(",")[0];
			secondDay = afResourceDo.getTypeDesc().split(",")[1];
			data.put("firstDay", firstDay);
			data.put("secondDay", secondDay);
		}
		resp.setResponseData(data);
		return resp;
	}

}
