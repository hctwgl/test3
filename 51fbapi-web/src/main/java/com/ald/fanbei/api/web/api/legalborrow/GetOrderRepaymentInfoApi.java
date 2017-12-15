package com.ald.fanbei.api.web.api.legalborrow;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author 郭帅强 2017年12月12日 16:46:23
 * @类描述：判断是否是新版借款页面
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderRepaymentInfoApi")
public class GetOrderRepaymentInfoApi implements ApiHandle {

	@Resource
	private AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Resource
	private AfBorrowCashService afBorrowCashService;

	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		Map<String, Object> data = new HashMap<>();
		
		
		
		resp.setResponseData(data);
		return resp;
	}
}
