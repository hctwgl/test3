package com.ald.fanbei.api.web.api.bill;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfUserAmountBizType;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfUserAmountQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
* @ClassName: GetMyRepaymentV1Api 
* @Description: 获取用户退还款记录 
* @author yuyue
* @date 2017年11月27日 下午2:13:59 
*
 */
@Component("getMyRepaymentHistoryV1Api")
public class GetMyRepaymentHistoryV1Api implements ApiHandle{

	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserAmountService afUserAmountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			int month = NumberUtil.objToIntDefault(requestDataVo.getParams().get("month"), 0);
			int year = NumberUtil.objToIntDefault(requestDataVo.getParams().get("year"), 0);
			String status = ObjectUtils.toString(request.getParameter("status"));
			// 记录上翻或者下翻
			String operation = ObjectUtils.toString(request.getParameter("operation"));
			Date nowDate = new Date();
			if (month == 0 && year == 0){
				month = NumberUtil.objToInteger(DateUtil.getMonth(nowDate));
				year = NumberUtil.objToInteger(DateUtil.getYear(nowDate));
			}
			if (month == 0 || year == 0) {
				logger.error("getMyHistoryBorrowV1Api month or year is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.error("getMyRepaymentV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			AfUserAmountQuery query = new AfUserAmountQuery();
			query.setUserId(userId);
			if (StringUtil.isEmpty(status)) {
				// 初始化页面
				status = "repayment";
				// 查询用户最后一条退还款记录
				List<AfUserAmountDo> amountList = afUserAmountService.getUserAmountByQuery(query);
			}

//			resp.setResponseData(amountList);
			return resp;
		} catch (Exception e) {
			logger.error("getMyRepaymentV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
