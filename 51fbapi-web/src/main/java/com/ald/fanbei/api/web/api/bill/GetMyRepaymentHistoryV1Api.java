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
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
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
@Component("getMyRepaymentV1Api")
public class GetMyRepaymentHistoryV1Api implements ApiHandle{

	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			int page = NumberUtil.objToIntDefault(requestDataVo.getParams().get("page"), 0);
			int pageSize = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageSize"), 0);
			String status = ObjectUtils.toString(request.getParameter("status"));
			if (userId == null) {
				logger.error("getMyHistoryBorrowV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.error("getMyHistoryBorrowV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			if (StringUtil.isEmpty(status)) {
				status = "repayment";
			}
			if (StringUtil.equals("repayment", status)) {
				// 获取还款记录
				
			}
			
			
			
			List<AfBorrowBillDo> monthBills = afBorrowBillService.getUserAllMonthBill(userId,page,pageSize);
			if (monthBills == null || monthBills.size() < 1) {
				return resp;
			}
			Map<Integer, List<AfBorrowBillDo>> yearMap = new HashMap<Integer, List<AfBorrowBillDo>>();
			for (AfBorrowBillDo bill : monthBills) {
				// 设置账单年月
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(bill.getGmtOutDay());
				bill.setBillMonth(calendar.get(Calendar.MONTH) + 1);
				bill.setBillYear(calendar.get(Calendar.YEAR));
				// 查询对应账单日的起始时间
				calendar.set(Calendar.DATE, 1);
		        calendar.set(Calendar.HOUR_OF_DAY, 0);
		        calendar.set(Calendar.MINUTE, 0);
		        calendar.set(Calendar.SECOND, 0);
		        calendar.set(Calendar.MILLISECOND, 0);
		        Date strDate = calendar.getTime();
		        calendar.add(Calendar.MONTH, 1);
		        calendar.add(Calendar.SECOND, -1);
		        Date endDate = calendar.getTime();
		        
		        AfBorrowBillQuery query = new AfBorrowBillQuery();
		        query.setUserId(userId);
		        query.setIsOut(1);
		        query.setOutDayStr(strDate);
		        query.setOutDayEnd(endDate);
		        // 查询是否有未结清的账单
		        query.setStatus(BorrowBillStatus.NO.getCode());
		        int noBillCount = afBorrowBillService.countBillByQuery(query);
		        if (noBillCount > 0) {
					// 有未还账单
		        	bill.setStatus(BorrowBillStatus.NO.getCode());
				}else {
					// 所有账单已结清
		        	bill.setStatus(BorrowBillStatus.YES.getCode());
				}
		        // 查询是否有逾期账单
		        query.setStatus(null);
		        query.setOverdueStatus("Y");
		        int overdueBillCount = afBorrowBillService.countBillByQuery(query);
		        // 设置逾期天数
		        bill.setOverdueDays(overdueBillCount);
		        
		        // 添加到map中
				if (yearMap.containsKey(bill.getBillYear())) {
					List<AfBorrowBillDo> billList = yearMap.get(bill.getBillYear());
					billList.add(bill);
					yearMap.put(bill.getBillYear(), billList);
				}else {
					List<AfBorrowBillDo> billList = new ArrayList<AfBorrowBillDo>();
					billList.add(bill);
					yearMap.put(bill.getBillYear(), billList);
				}
			}
			// 组装返回值
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			for(Integer key : yearMap.keySet()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("year", key);
				map.put("bills", yearMap.get(key));
				list.add(map);
			}
			resp.setResponseData(list);
			return resp;
		} catch (Exception e) {
			logger.error("getMyHistoryBorrowV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
