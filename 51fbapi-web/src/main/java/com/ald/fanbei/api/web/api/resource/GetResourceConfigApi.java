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
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfAppResourceVo;

/**
 * @类描述：获取资源配置信息
 * @author chengkang 2017年7月13日上午11:11:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getResourceConfigApi")
public class GetResourceConfigApi implements ApiHandle{

	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String resourceType = ObjectUtils.toString(requestDataVo.getParams().get("resourceType"),"");
        //参数校验
  		if(StringUtils.isBlank(resourceType)){
  			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
  		}
  		
  		Map<String,Object> map = new HashMap<String,Object>();
  		List<AfResourceDo> batchAfResourceDo =  afResourceService.getResourceHomeListByTypeOrderBy(resourceType);
        
  		List<AfAppResourceVo> batchResourceVo =convertToAfAppResourceVos(batchAfResourceDo);
  		
  		map.put("resources", batchResourceVo);
        resp.setResponseData(map);
		return resp;
	}

	private List<AfAppResourceVo> convertToAfAppResourceVos(List<AfResourceDo> batchAfResourceDo){
		List<AfAppResourceVo> batchResourceVo = new ArrayList<AfAppResourceVo>();
		for (AfResourceDo tempResource : batchAfResourceDo) {
			if(AfCounponStatus.O.getCode().equals(tempResource.getValue4())){
				batchResourceVo.add(new AfAppResourceVo(StringUtil.null2Str(tempResource.getValue1()), StringUtil.null2Str(tempResource.getValue2()), StringUtil.null2Str(tempResource.getValue3())));
			}
		}
		return batchResourceVo;
	}
}
