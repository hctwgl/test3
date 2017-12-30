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

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
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
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

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
	
	@Resource
	private AfBorrowService afBorrowService;
	
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
			BigDecimal money = new BigDecimal(-1);
			List<AfBorrowBillDto> billList = new ArrayList<AfBorrowBillDto>();
			AfUserOutDayDo userOutDayDo = afUserOutDayDao.getUserOutDayByUserId(userId);
			if (userOutDayDo == null || userOutDayDo.getId() == null) {
				userOutDayDo = new AfUserOutDayDo();
				userOutDayDo.setOutDay(10);
				userOutDayDo.setPayDay(20);
			}
			// 计算所属账期
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DATE,1);
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
			int billCount = afBorrowBillService.countBillByQuery(query);
			if (billCount < 1) {
				map.put("status", "noBill");
				resp.setResponseData(map);
				return resp;
			}
			// 获取未入账账单
			List<AfBorrowDto> borrowList = afBorrowService.getUserNotInBorrow(userId);
			// 未入账笔数
			int notInCount = afBorrowService.getUserNotInBorrowCount(userId);
			// 未入账金额
			BigDecimal notInMoney = afBorrowService.getUserNotInBorrowMoney(userId);
			map.put("borrowList", borrowList);
			map.put("notInCount", notInCount);
			map.put("notInMoney", notInMoney);
			query.setIsOut(1);
			query.setStatus(BorrowBillStatus.NO.getCode());
			int outBillCount = afBorrowBillService.countBillByQuery(query);
			if (outBillCount > 0) {
				// 计算账期
				Date end = DateUtil.addDays(strOutDay, userOutDayDo.getOutDay() - 2);
				Date str = DateUtil.addMonths(strOutDay, -1);
				str = DateUtil.addDays(str, userOutDayDo.getOutDay() - 1);
				map.put("str", DateUtil.formatAndMonthAndDay(str));
				map.put("end", DateUtil.formatAndMonthAndDay(end));
				// 有已出未还账单，获取金额
				money = afBorrowBillService.getUserBillMoneyByQuery(query);
				map.put("money", money);
				billList = afBorrowBillService.getBillListByQuery(query);
				map.put("billList", billList);
				query.setOverdueStatus("Y");
				// 逾期笔数
				int overdueBillCount = afBorrowBillService.countBillByQuery(query);
				if (overdueBillCount > 0) {
					map.put("status", "overdue");
					// 有逾期的情况
					map.put("overdueBillCount", overdueBillCount);
					// 总逾期费
					BigDecimal overdeuMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
					map.put("overdeuMoney", overdeuMoney);
					// 逾期利息
					BigDecimal overdeuInterest = afBorrowBillService.getUserOverdeuInterestByQuery(query);
					map.put("overdeuInterest", overdeuInterest);
				}else {
					// 没有逾期的情况
					map.put("status", "out");
					// 最后还款日期
					Date lastPayDay = afBorrowBillService.getLastPayDayByUserId(userId);
					map.put("lastPayDay", DateUtil.formatAndMonthAndDay(lastPayDay));
				}
				resp.setResponseData(map);
				return resp;
			}
			// 没有已出账单
			query.setIsOut(0);
			int notOutBillCount = afBorrowBillService.countBillByQuery(query);
			if (notOutBillCount > 0) {
				// 查询未出
				map.put("status", "notOut");
				money = afBorrowBillService.getUserBillMoneyByQuery(query);
				map.put("money", money);
				billList = afBorrowBillService.getBillListByQuery(query);
				map.put("billList", billList);
				// 出账日
				Date outDate = DateUtil.addDays(strOutDay, userOutDayDo.getOutDay() - 1);
				map.put("outDay", DateUtil.formatAndMonthAndDay(outDate));
				resp.setResponseData(map);
				return resp;
			}else {
				map.put("status", "finsh");
				resp.setResponseData(map);
				return resp;
			}
		} catch (Exception e) {
			logger.error("getBillListByMonthAndYearApi error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
