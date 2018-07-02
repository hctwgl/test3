package com.ald.fanbei.api.web.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfRedRainService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfRedRainRoundDo;
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
	AfRedRainService redRainService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		List<AfRedRainRoundDo> rounds = redRainService.fetchTodayRounds();
		Map<String,Object> data = new HashMap<>();
		data.put("rounds", rounds);
		resp.setResponseData(data);
		return resp;
	}

}
