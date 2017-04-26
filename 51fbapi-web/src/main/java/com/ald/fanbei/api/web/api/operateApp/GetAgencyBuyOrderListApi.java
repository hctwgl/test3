/**
 * 
 */
package com.ald.fanbei.api.web.api.operateApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfAgentOrderStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.dto.AfAgentOrderDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年4月24日下午3:15:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getAgencyBuyOrderListApi")
public class GetAgencyBuyOrderListApi implements ApiHandle {

	@Resource
	AfAgentOrderService afAgentOrderService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		String status = ObjectUtils.toString(requestDataVo.getParams().get("status"));
		Long pageNo = NumberUtil.objToLongDefault(requestDataVo.getParams().get("status"), 0L);
		AfAgentOrderStatus orderStatus = AfAgentOrderStatus.findRoleTypeByCode(status);

		if (orderStatus == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);

		}
		List<AfAgentOrderDto> agentOrderList = afAgentOrderService.getAgentOrderListByAgentId(context.getUserId(), status);
		List<Object> list = new ArrayList<Object>();

		for (AfAgentOrderDto afAgentOrderDto : agentOrderList) {
			list.add(objectWithAfAgentOrderDto(afAgentOrderDto));
		}
		resp.addResponseData("orderList", list);

		return resp;
	}

	public Map<String, Object> objectWithAfAgentOrderDto(AfAgentOrderDto dto) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("goodsIcon", dto.getGoodsIcon());
		data.put("goodsName", dto.getGoodsName());
		data.put("consignee", dto.getConsignee());
		data.put("gmtCreate", dto.getGmtCreate());
		data.put("orderId", dto.getOrderId());
		return data;
	}

}
