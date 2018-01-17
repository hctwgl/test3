package com.ald.fanbei.api.web.api.bill;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfUserAmountBizType;
import com.ald.fanbei.api.common.enums.AfUserAmountDetailType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAmountDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAmountQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
* @ClassName: GetRepaymentCalendarV1Api 
* @Description: 退还款记录日历
* @author yuyue
* @date 2017年12月1日 下午1:21:49 
*
 */
@Component("getRepaymentCalendarV1Api")
public class GetRepaymentCalendarV1Api implements ApiHandle{

	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserAmountService afUserAmountService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			String year = ObjectUtils.toString(requestDataVo.getParams().get("year"));
			String status = ObjectUtils.toString(requestDataVo.getParams().get("status"));
			if (year == null) {
				year = DateUtil.getYear(new Date());
			}
			if (userId == null) {
				logger.error("getRepaymentCalendarV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.error("getRepaymentCalendarV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			AfUserAmountQuery query = new AfUserAmountQuery();
			query.setUserId(userId);
			query.setYear(year);
			if (StringUtil.equals("repayment", status)) {
				query.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
			}else {
				query.setBizType(AfUserAmountBizType.REFUND.getCode());
			}
			List<String> monthList = afUserAmountService.getMonthInYearByQuery(query);
			map.put("monthList", monthList);
			map.put("status", status);
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getRepaymentCalendarV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
