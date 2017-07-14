package com.ald.fanbei.api.web.api.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfCounponStatus;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：获取资源配置信息
 * @author chengkang 2017年7月13日上午11:11:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getResourceConfigApi")
public class GetResourceConfigApi implements ApiHandle{

	@Resource
	AfResourceService afResourceService;
	
	//类型（CANCEL_ORDER_REASON：订单取消原因 ORDER_SEARCH_CONDITION：订单筛选条件）
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
        String resourceType = ObjectUtils.toString(requestDataVo.getParams().get("resourceType"),"");
        //参数校验
  		if(StringUtils.isBlank(resourceType)){
  			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
  		}
  		
  		Map<String,Object> map = new HashMap<String,Object>();
  		List<AfResourceDo> afResourceDoList =  afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.CANCEL_ORDER_REASON.getCode());
        if(afResourceDoList!=null && afResourceDoList.size()>0){
        	AfResourceDo afResourceDo = afResourceDoList.get(0);
        	if(AfCounponStatus.O.getCode().equals(afResourceDo.getValue4())){
        		map.put("resources", StringUtil.splitToList(afResourceDo.getValue(), "||"));
        	}
        }else{
        	logger.info("getResourceConfig fail,resource is not found or not open.userId="+userId);
        	map.put("resources", new ArrayList<String>());
        }
        resp.setResponseData(map);
		return resp;
	}

}
