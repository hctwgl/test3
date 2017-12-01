package com.ald.fanbei.api.web.api.bill;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfUserAmountBizType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
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
			String month = ObjectUtils.toString(request.getParameter("month"));
			String year = ObjectUtils.toString(request.getParameter("year"));
			String status = ObjectUtils.toString(request.getParameter("status"));
			// 记录上翻或者下翻
			String operation = ObjectUtils.toString(request.getParameter("operation"));
			Date nowDate = new Date();
			if (month == null && year == null){
				month = DateUtil.getMonth(nowDate);
				year = DateUtil.getMonth(nowDate);
			}
			if (month == null || year == null) {
				logger.error("getMyRepaymentHistoryV1Api month or year is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.error("getMyRepaymentHistoryV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			AfUserAmountQuery query = new AfUserAmountQuery();
			query.setUserId(userId);
			Date strDate = new Date();
			Date endDate = new Date();
			List<AfUserAmountDo> amountList = new ArrayList<AfUserAmountDo>();
			if (StringUtil.isEmpty(status)) {
				// 初始化页面
				// 查询用户最后一条还款记录
				query.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
				List<AfUserAmountDo> queryList = afUserAmountService.getUserAmountByQuery(query);
				if(queryList == null && queryList.size() < 1) {
					return resp;
				}
				AfUserAmountDo firstAmount = queryList.get(0);
				strDate = DateUtil.getFirstOfMonth(firstAmount.getGmtCreate());
				strDate = DateUtil.addHoures(strDate, -12);
				endDate = DateUtil.addMonths(strDate, 4);
				query.setStrDate(strDate);
				query.setEndDate(endDate);
				amountList = afUserAmountService.getUserAmountByQuery(query);
				map.put("amountList", amountList);
				map.put("status", "repayment");
				resp.setResponseData(map);
				return resp;
			}
			// 处理时间
			if (StringUtil.equals("top", operation)) {
				// 上翻 
				endDate = DateUtil.parseDate(year+month, DateUtil.MONTH_SHOT_PATTERN);
				strDate = DateUtil.addMonths(endDate, -4);
			}else if (StringUtil.equals("bottom", operation)) {
				// 下翻
				strDate = DateUtil.parseDate(year+month, DateUtil.MONTH_SHOT_PATTERN);
				endDate = DateUtil.addMonths(strDate, 4);
			}else {
				logger.error("getMyRepaymentHistoryV1Api operation error ,RequestDataVo id =" + requestDataVo.getId() + " ,operation=" + operation);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.PARAM_ERROR);
				return resp;
			}
			query.setStrDate(strDate);
			query.setEndDate(endDate);
			if (StringUtil.equals("repayment", status)) {
				query.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
			}else {
				query.setBizType(AfUserAmountBizType.REFUND.getCode());
			}
			amountList = afUserAmountService.getUserAmountByQuery(query);
			map.put("status", status);
			map.put("amountList", amountList);
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getMyRepaymentHistoryV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
