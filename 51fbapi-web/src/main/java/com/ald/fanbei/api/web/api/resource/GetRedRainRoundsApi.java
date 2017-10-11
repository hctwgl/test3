package com.ald.fanbei.api.web.api.resource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.redpacket.IRedRainService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：获取当日的红包雨场次配置信息，客户端按照配置指示，到点弹出红包雨页面
 * @author ZJF 2017年9月30日
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRedRainRoundsApi")
public class GetRedRainRoundsApi implements ApiHandle{

	@Resource
	IRedRainService redRainService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
  		
        // TODO resp.setResponseData(redpacketService.getRounds());
		
		return resp;
	}

}
