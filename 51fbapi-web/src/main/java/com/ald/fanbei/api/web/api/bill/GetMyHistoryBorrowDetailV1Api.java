package com.ald.fanbei.api.web.api.bill;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
* @ClassName: GetMyHistoryBorrowDetailV1Api 
* @Description: 获取历史账单详情页面 
* @author yuyue
* @date 2017年12月5日 下午7:00:20 
*
 */
@Component("getMyHistoryBorrowDetailV1Api")
public class GetMyHistoryBorrowDetailV1Api implements ApiHandle{

	@Resource
	private AfUserService afUserService;
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfUserOutDayDao afUserOutDayDao;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			int inMonth = NumberUtil.objToIntDefault(requestDataVo.getParams().get("billMonth"), 0);
			int inYear = NumberUtil.objToIntDefault(requestDataVo.getParams().get("billYear"), 0);
			if (inMonth == 0 || inYear == 0) {
				logger.info("getBillListByMonthAndYearApi billMonth or billYear is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
				return resp;
			}
			if (userId == null) {
				logger.info("getBillListByMonthAndYearApi userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.info("getBillListByMonthAndYearApi user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			AfUserOutDayDo userOutDayDo = afUserOutDayDao.getUserOutDayByUserId(userId);
			if (userOutDayDo == null || userOutDayDo.getId() == null) {
				userOutDayDo = new AfUserOutDayDo();
				userOutDayDo.setOutDay(10);
				userOutDayDo.setPayDay(20);
			}
			// 计算所属账期
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MONTH,inMonth - 1);
			calendar.set(Calendar.YEAR,inYear);
			Date strOutDay = DateUtil.getFirstOfMonth(calendar.getTime());
			strOutDay = DateUtil.addHoures(strOutDay, -12);
			Date endOutDay = DateUtil.addMonths(strOutDay, 1);
			calendar.setTime(endOutDay);
			calendar.add(Calendar.SECOND, -1);
			endOutDay = calendar.getTime();
			// 展示逻辑上先已出，再未出
			AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
			query.setUserId(userId);
			query.setOutDayStr(strOutDay);
			query.setOutDayEnd(endOutDay);
			query.setStatus(BorrowBillStatus.YES.getCode());
			List<AfBorrowBillDto> billList = afBorrowBillService.getBillListByQuery(query);
			Date end = DateUtil.addDays(strOutDay, userOutDayDo.getOutDay() - 2);
			Date str = DateUtil.addMonths(strOutDay, -1);
			str = DateUtil.addDays(str, userOutDayDo.getOutDay() - 1);
			map.put("str", DateUtil.formatShortDateC(str));
			map.put("end", DateUtil.formatShortDateC(end));
			map.put("billList", billList);
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getBillListByMonthAndYearApi error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
