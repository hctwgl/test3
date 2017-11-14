package com.ald.fanbei.api.web.api.withhold;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/** 
 * @类描述:
 * @author fanmanfu 创建时间：2017年11月14日 上午11:00:37 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller("homeScrollBarApi")
public class homeScrollBarController implements ApiHandle {  //HOMEPAGE_TOP_SCROLLBAR

	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		List<AfResourceDo> resourceListByType = afResourceService.getResourceListByType(Constants.HOMEPAGE_TOP_SCROLLBAR);
		List<String> scrollBars = new ArrayList<String>();
		if(resourceListByType != null && resourceListByType.size()>0) {
			for (AfResourceDo afResourceDo : resourceListByType) {
				scrollBars.add(afResourceDo.getDescription());
			}
		}
		resp.setResponseData(scrollBars);
		return null;
	}

}
