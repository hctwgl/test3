package com.ald.fanbei.api.web.api.legalborrowV2;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderLogisticsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

@Component("getLegalOrderLogisticsV2Api")
public class GetLegalOrderLogisticsV2Api implements ApiHandle {
	@Autowired
	AfBorrowLegalOrderLogisticsService afBorrowLegalOrderLogisticsService;

	/**
	 * 查询物流信息
	 * 
	 * @param requestDataVo
	 * @param context
	 * @param request
	 * @return
	 */
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		try {
			Map<String, Object> params = requestDataVo.getParams();
			long orderId = NumberUtil.strToLong(params.get("orderId").toString());
			long isOutTraces = NumberUtil
					.strToLong(params.get("traces") == null ? String.valueOf(0) : params.get("traces").toString());
			AfOrderLogisticsBo afOrderLogisticsBo = afBorrowLegalOrderLogisticsService.getLegalOrderLogisticsBo(orderId,
					isOutTraces);
			if (afOrderLogisticsBo == null) {
				resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.LOGISTICS_NOT_EXIST);
			} else {
				resp.addResponseData("logistics", afOrderLogisticsBo);
			}
		} catch (Exception e) {
			logger.error("getOrderLogisticsApi :", e);
			resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		}
		return resp;
	}

}
