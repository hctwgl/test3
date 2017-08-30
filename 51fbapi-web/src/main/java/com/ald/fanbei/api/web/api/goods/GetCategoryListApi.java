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

import com.ald.fanbei.api.biz.service.AfCategoryService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfCategoryDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfCategoryVo;

/**
 * 
 *@类描述：GetCategoryListApi
 *@author 何鑫 2017年2月17日  21:58:37
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCategoryListApi")
public class GetCategoryListApi implements ApiHandle {

	@Resource
	AfCategoryService afCategoryService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		List<AfCategoryDo> list = afCategoryService.getCategoryList();
		Map<String, Object> data = getCategoryList(list);
		resp.setResponseData(data);
		return resp;
	}

	private Map<String, Object> getCategoryList(List<AfCategoryDo> categoryList){
		Map<String, Object> data = new HashMap<String, Object>();
		List<AfCategoryVo> categoryVoList = new ArrayList<AfCategoryVo>();
		for (AfCategoryDo afCategoryDo : categoryList) {
			AfCategoryVo vo = new AfCategoryVo();
			vo.setRid(afCategoryDo.getRid());
			vo.setName(afCategoryDo.getName());
			categoryVoList.add(vo);
		}
		data.put("categoryList", categoryVoList);
		return data;
	}
}
