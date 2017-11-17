package com.ald.fanbei.api.web.api.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.jsoup.helper.DataUtil;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.mysql.fabric.xmlrpc.base.Data;

/**
 * 
* @ClassName: GetBillListByMonthAndYearApi 
* @Description: 获取某期的账单的bill列表
* @author yuyue
* @date 2017年11月16日 下午6:47:11 
*
 */
@Component("getBillListByMonthAndYearApi")
public class GetBillListByMonthAndYearApi implements ApiHandle{

	@Resource
	private AfUserService afUserService;
	
	@Resource
	private AfUserAuthService afUserAuthService;
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
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
			
			AfBorrowBillQuery query = new AfBorrowBillQuery();
			query.setUserId(userId);
			query.setOutDayStr(strOutDay);
			query.setOutDayEnd(endOutDay);
			query.setStatus("N");
			BigDecimal money = afBorrowBillService.getUserBillMoneyByQuery(query);
			
			resp.setResponseData(map);
		} catch (Exception e) {
			logger.error("getBillListByMonthAndYearApi error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
		return resp;
	}

}
