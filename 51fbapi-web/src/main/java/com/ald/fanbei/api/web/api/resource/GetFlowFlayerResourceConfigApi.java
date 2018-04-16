package com.ald.fanbei.api.web.api.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**  
 * @Title: GetFlowFlayerResourceConfigApi.java
 * @Package com.ald.fanbei.api.web.api.resource
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2018年2月10日 下午4:34:33
 * @version V1.0  
 */
@Component("getFlowFlayerResourceConfigApi")
public class GetFlowFlayerResourceConfigApi implements ApiHandle{
	@Resource
	AfResourceService afResourceService;

	@Resource
	BizCacheUtil bizCacheUtil;

	@Resource
	RiskUtil riskUtil;

	@Resource
	AfUserAuthStatusService afUserAuthStatusService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String resourceType = ObjectUtils.toString(requestDataVo.getParams().get("resourceType"),"");
        String secType = ObjectUtils.toString(requestDataVo.getParams().get("secType"),"");
        //参数校验
  		if(StringUtils.isBlank(resourceType) || StringUtils.isBlank(secType)){
  			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
  		}
  		if(context.getUserId() != null && context.getUserId() > 0){
			AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(context.getUserId(), "ONLINE");
			if(afUserAuthStatusDo!=null&&afUserAuthStatusDo.getStatus().equals("Y")) {
				if (StringUtil.isEmpty(bizCacheUtil.hget("Lease_Score", context.getUserId().toString()))) {
					riskUtil.updateRentScore(context.getUserId().toString());
					bizCacheUtil.hset("Lease_Score", context.getUserId().toString(), DateUtil.getNow(), DateUtil.getTodayLast());
				}
			}
		}
  		Map<String,Object> map = new HashMap<String,Object>();
  		List<AfResourceDo> batchAfResourceDo =  afResourceService.getFlowFlayerResourceConfig(resourceType,secType);
  		logger.info("getFlowFlayerResourceConfigApi result = {}",batchAfResourceDo == null ? "" :batchAfResourceDo.toString());
  		map.put("resources", batchAfResourceDo);
        resp.setResponseData(map);
		return resp;
	}


}
