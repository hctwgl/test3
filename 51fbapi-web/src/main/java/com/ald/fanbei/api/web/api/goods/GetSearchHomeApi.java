/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

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
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：搜呗首页信息
 * @author suweili 2017年2月24日下午5:24:53
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getSearchHomeApi")
public class GetSearchHomeApi implements ApiHandle {
	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> list = getObjectWithResourceDolist(afResourceService.getResourceListByTypeOrderBy(AfResourceType.SEARCH_HOT.getCode()));
		AfResourceDo afResourceDo= afResourceService.getSingleResourceBytype(AfResourceType.RebateDetailedCourse.getCode());
		if(afResourceDo ==null){
			throw new FanbeiException("rebate detailed course is not exist error", FanbeiExceptionCode.RESOURES_H5_ERROR);

		}
	
		data.put("tipIcon", afResourceDo.getValue());
		data.put("tipUrl", afResourceDo.getValue1());
		data.put("rebate", afResourceDo.getValue2());
		data.put("searchList", list);

		resp.setResponseData(data);
		return resp;
	}
	
	
	private List<Object>  getObjectWithResourceDolist(List<AfResourceDo> list) {
		List<Object> rusultList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : list) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("name", afResourceDo.getValue());
			data.put("isHot", afResourceDo.getValue1());
			
			rusultList.add(data);
		}
		
		return rusultList;
	}

}
