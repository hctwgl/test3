/**
 * 
 */
package com.ald.fanbei.api.web.api.operateApp;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.dto.AfAgentOrderDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年4月24日下午5:08:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAgencyBuyOrderInfoApi")
public class GetAgencyBuyOrderInfoApi implements ApiHandle {

	@Resource
	AfAgentOrderService afAgentOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), 0L);
		AfAgentOrderDto dto = afAgentOrderService.getAgentOrderInfoById(orderId);
		resp.addResponseData("orderInfo", dto);
		return resp;
	}

}
