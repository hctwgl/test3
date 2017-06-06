package com.ald.fanbei.api.web.api.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;


@Component("getTabbarInforApi")
public class GetTabbarInforApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		List<Object> tabbarInfor= getObjectWithResourceDolist(afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeTabbar.getCode()));
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("tabbarInfor", tabbarInfor);
		resp.setResponseData(data);
		return resp;
	}
	
	private List<Object> getObjectWithResourceDolist(List<AfResourceDo> tabbarlist) {
		List<Object> tabbarList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : tabbarlist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("title", afResourceDo.getName());
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleColor", afResourceDo.getValue1());
			Map<String, Object> index = new HashMap<String, Object>();
			index.put(afResourceDo.getSecType(), data);
			tabbarList.add(index);
		}

		return tabbarList;
	}

}
