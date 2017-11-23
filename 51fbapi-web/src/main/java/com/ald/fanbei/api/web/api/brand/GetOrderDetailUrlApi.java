/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取菠萝觅订单详情
 * @author xiaotianjian 2017年3月25日下午8:54:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderDetailUrlApi")
public class GetOrderDetailUrlApi implements ApiHandle {

	@Resource
	AfOrderService afOrderService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		Map<String, Object> params = requestDataVo.getParams();
		
		
		Long orderId = NumberUtil.objToLongDefault(params.get("orderId"), null);
		
		if (orderId ==  null) {
			logger.error("orderId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		if (orderInfo == null || !orderInfo.getOrderType().equals(OrderType.BOLUOME.getCode())) {
			logger.error("orderId is invalid or orderInfo don't belong to boluome ");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		Map<String, String> buildParams = new HashMap<String, String>();
		
		String detailUrl = orderInfo.getThirdDetailUrl();
		
		buildParams.put(BoluomeCore.CUSTOMER_USER_ID, context.getUserId()+StringUtils.EMPTY);
		buildParams.put(BoluomeCore.CUSTOMER_USER_PHONE, context.getMobile());
		buildParams.put(BoluomeCore.TIME_STAMP, requestDataVo.getSystem().get(Constants.REQ_SYS_NODE_TIME) + StringUtils.EMPTY);
		
		String sign =  BoluomeCore.buildSignStr(buildParams);
		buildParams.put(BoluomeCore.SIGN, sign);
		
		String paramsStr = BoluomeCore.createLinkString(buildParams);
		
		resp.addResponseData("detailUrl", detailUrl+"?"+paramsStr);
		return resp;
	}

}
