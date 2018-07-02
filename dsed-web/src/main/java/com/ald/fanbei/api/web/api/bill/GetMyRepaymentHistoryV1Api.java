package com.ald.fanbei.api.web.api.bill;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
			String month = ObjectUtils.toString(requestDataVo.getParams().get("month"));
			String year = ObjectUtils.toString(requestDataVo.getParams().get("year"));
			String status = ObjectUtils.toString(requestDataVo.getParams().get("status"));
			// 记录上翻或者下翻
			String operation = ObjectUtils.toString(requestDataVo.getParams().get("operation"));
			Date nowDate = new Date();
			if (StringUtil.isEmpty(month) && StringUtil.isEmpty(year)){
				month = DateUtil.getMonth(nowDate);
				year = DateUtil.getYear(nowDate);
			}
			if (month == null || year == null) {
				logger.error("getMyRepaymentHistoryV1Api month or year is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			if (StringUtil.isEmpty(status)) {
				logger.error("getMyRepaymentHistoryV1Api status is null ,RequestDataVo id =" + requestDataVo.getId());
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
			if (StringUtil.equals(status, "repayment")) {
				query.setBizType(AfUserAmountBizType.REPAYMENT.getCode());
			}else {
				query.setBizType(AfUserAmountBizType.REFUND.getCode());
			}
			if (StringUtil.isEmpty(operation)) {
				// 初始化页面
				// 查询用户最后一条记录
				List<AfUserAmountDo> queryList = afUserAmountService.getUserAmountByQuery(query);
				if(queryList == null || queryList.size() < 1) {
					resp.setResponseData(map);
					return resp;
				}
				AfUserAmountDo firstAmount = queryList.get(0);
				endDate = DateUtil.getFirstOfMonth(firstAmount.getGmtCreate());
				endDate = DateUtil.addMonths(endDate, 1);
				endDate = DateUtil.addHoures(endDate, -12);
				strDate = DateUtil.addMonths(endDate, -12);
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(endDate);
//				calendar.add(Calendar.SECOND, -1);
//				endDate = calendar.getTime();
				query.setStrDate(strDate);
				query.setEndDate(endDate);
				amountList = afUserAmountService.getUserAmountByQuery(query);
				List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
				Integer strMonth = Integer.parseInt(DateUtil.getMonth(strDate));
				Integer strYear = Integer.parseInt(DateUtil.getYear(strDate));
				for (int i = 0; i < 12; i++) {
					Map<String, Object> timeMap = new HashMap<String, Object>();
					if (strMonth + i - 12 > 0) {
						timeMap.put("month", strMonth + i - 12);
						timeMap.put("year", strYear + 1);
						// 用于排序
						timeMap.put("int", strMonth + i);
						List<AfUserAmountDo> list2 = new ArrayList<AfUserAmountDo>();
						timeMap.put("amountList", list2);
						list.add(timeMap);
					}else {
						timeMap.put("month", strMonth + i);
						timeMap.put("year", strYear);
						timeMap.put("int", strMonth + i);
						List<AfUserAmountDo> list2 = new ArrayList<AfUserAmountDo>();
						timeMap.put("amountList", list2);
						list.add(timeMap);
					}
				}
				for (AfUserAmountDo amountDo : amountList) {
					for (Map<String, Object> map2 : list) {
						if ((Integer)map2.get("month") == Integer.parseInt(DateUtil.getMonth(amountDo.getGmtCreate()))) {
							List<AfUserAmountDo> list2 = (List) map2.get("amountList");
							list2.add(amountDo);
						}
					}
				}
				
				Collections.sort(list,new Comparator<Map<String, Object>>(){
					public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
						if((Integer)arg0.get("int") > (Integer)arg1.get("int")) return -1;
						if((Integer)arg0.get("int") < (Integer)arg0.get("int")) return 1;
						return 0;
					}
				});
				
				map.put("status", status);
				map.put("list", list);
				resp.setResponseData(map);
				return resp;
			}
			// 处理时间
			if (StringUtil.equals("top", operation)) {
				// 上翻 
				strDate = DateUtil.parseDate(year+month, DateUtil.MONTH_SHOT_PATTERN);
				strDate = DateUtil.addMonths(strDate, 1);
				endDate = DateUtil.addMonths(strDate, 12);
			}else {
				// 下翻 bottom
				endDate = DateUtil.parseDate(year+month, DateUtil.MONTH_SHOT_PATTERN);
				strDate = DateUtil.addMonths(endDate, -23);
			}
			query.setStrDate(strDate);
			query.setEndDate(endDate);
			amountList = afUserAmountService.getUserAmountByQuery(query);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Integer strMonth = Integer.parseInt(DateUtil.getMonth(strDate));
			Integer strYear = Integer.parseInt(DateUtil.getYear(strDate));
			for (int i = 0; i < 12; i++) {
				Map<String, Object> timeMap = new HashMap<String, Object>();
				if (strMonth + i - 12 > 0) {
					timeMap.put("month", strMonth + i - 12);
					timeMap.put("year", strYear + 1);
					timeMap.put("int", strMonth + i);
					List<AfUserAmountDo> list2 = new ArrayList<AfUserAmountDo>();
					timeMap.put("amountList", list2);
					list.add(timeMap);
				}else {
					timeMap.put("month", strMonth + i);
					timeMap.put("year", strYear);
					timeMap.put("int", strMonth + i);
					List<AfUserAmountDo> list2 = new ArrayList<AfUserAmountDo>();
					timeMap.put("amountList", list2);
					list.add(timeMap);
				}
			}
			for (AfUserAmountDo amountDo : amountList) {
				for (Map<String, Object> map2 : list) {
					if ((Integer)map2.get("month") == Integer.parseInt(DateUtil.getMonth(amountDo.getGmtCreate()))) {
						List<AfUserAmountDo> list2 = (List) map2.get("amountList");
						list2.add(amountDo);
					}
				}
			}
			
			Collections.sort(list,new Comparator<Map<String, Object>>(){
				public int compare(Map<String, Object> arg0, Map<String, Object> arg1) {
					if((Integer)arg0.get("int") > (Integer)arg1.get("int")) return -1;
					if((Integer)arg0.get("int") < (Integer)arg1.get("int")) return 1;
					return 0;
				}
			});
			map.put("status", status);
			map.put("list", list);
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getMyRepaymentHistoryV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
