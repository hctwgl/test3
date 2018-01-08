package com.ald.fanbei.api.web.api.floatingWindow;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 *@类描述：获取漂浮窗信息
 *@author 曹武 2017年12月4日  15:22:23
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("floatInfo")
public class floatInfoApi implements ApiHandle{

	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		//Long userId = context.getUserId();
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndValue("SUSPENSION_FRAME_SETTING","0");
		Map<String,Object> newMap = new HashMap<String,Object>();
		if(afResourceDo!=null){
			newMap.put("name",afResourceDo.getName());
			newMap.put("pic1",afResourceDo.getPic1());
			newMap.put("pic2",afResourceDo.getPic2());
		}
		resp.setResponseData(newMap);
		return resp;
	}

	
}
