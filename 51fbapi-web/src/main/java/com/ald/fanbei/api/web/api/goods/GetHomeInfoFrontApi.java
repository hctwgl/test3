package com.ald.fanbei.api.web.api.goods;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;


/**
 * @author Jiang Rongbo
 *
 */
@Component("getHomeInfoFrontApi")
public class GetHomeInfoFrontApi implements ApiHandle {


	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String,Object> data = Maps.newHashMap();	
		try {
			request.getRequestDispatcher("/goods/getHomeInfoV2").forward(request, null);
		} catch (Exception e) {
			logger.error("GetHomeInfoFrontApi.process error=>{}",e.getMessage());
		}
		resp.setResponseData(data);
		return resp;
	}

	
}
