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
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;

/**
 * 
* @ClassName: GetMyRepaymentV1Api 
* @Description: 用户还款页面
* @author yuyue
* @date 2017年11月27日 下午3:04:05 
*
 */
@Component("getMyRepaymentV1Api")
public class GetMyRepaymentV1Api implements ApiHandle{

	@Resource
	AfUserService afUserService;
	
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Resource
	AfUserCouponService afUserCouponService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			if (userId == null) {
				logger.error("getMyRepaymentV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.error("getMyRepaymentV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
			// 获取所有逾期数据
			query.setUserId(userId);
			query.setIsOut(1);
			query.setOverdueStatus("Y");
			query.setStatus(BorrowBillStatus.NO.getCode());
			BigDecimal overdueMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
			if (overdueMoney.compareTo(new BigDecimal(0)) == 1) {
				List<Long> overdueBills = afBorrowBillService.getBillIdListByQuery(query);
				map.put("overdueBills", overdueBills);
			}
			map.put("overdueMoney", overdueMoney);
			// 获取已出未逾期
			query.setOverdueStatus("N");
			BigDecimal outMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
			map.put("outMoney", outMoney);
			if (outMoney.compareTo(new BigDecimal(0)) == 1) {
				List<AfBorrowBillDto> billList = afBorrowBillService.getBillListByQuery(query);
				List<Long> outBills = afBorrowBillService.getBillIdListByQuery(query);
				AfBorrowBillDto billDto = billList.get(0);
				String month = DateUtil.getMonth(billDto.getGmtOutDay());
				map.put("month", month);
				map.put("outBills", outBills);
			}
			// 获取下月未出账单
			// 先查询是否有本月已出
			Date strOutDay = DateUtil.getFirstOfMonth(new Date());
			strOutDay = DateUtil.addHoures(strOutDay, -12);
			Date endOutDay = DateUtil.addMonths(strOutDay, 1);
			query.setIsOut(1);
			query.setOutDayStr(strOutDay);
			query.setOutDayEnd(endOutDay);
			query.setOverdueStatus(null);
			BigDecimal notOutMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
			if (notOutMoney.compareTo(new BigDecimal(0)) == 0) {
				// 没有本月已出，查询是否有本月未出未还
				query.setIsOut(0);
				query.setStatus("N");
				notOutMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
				if (notOutMoney.compareTo(new BigDecimal(0)) == 1) {
					List<Long> notOutBills = afBorrowBillService.getBillIdListByQuery(query);
					String month = DateUtil.getMonth(strOutDay);
					map.put("notOutBills", notOutBills);
					map.put("nextMonth", month);
				}else if (notOutMoney.compareTo(new BigDecimal(0)) == 0) {
					// 没有本月未出，查询下月未出
					strOutDay = DateUtil.addMonths(strOutDay, 1);
					endOutDay = DateUtil.addMonths(strOutDay, 1);
					query.setOutDayStr(strOutDay);
					query.setOutDayEnd(endOutDay);
					query.setIsOut(0);
					query.setStatus("N");
					notOutMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
					if (notOutMoney.compareTo(new BigDecimal(0)) == 1) {
						List<Long> notOutBills = afBorrowBillService.getBillIdListByQuery(query);
						String month = DateUtil.getMonth(strOutDay);
						map.put("notOutBills", notOutBills);
						map.put("nextMonth", month);
					}
				}
			}else if (notOutMoney.compareTo(new BigDecimal(0)) == 1) {
				// 有本月已出
				strOutDay = DateUtil.addMonths(strOutDay, 1);
				endOutDay = DateUtil.addMonths(strOutDay, 1);
				query.setOutDayStr(strOutDay);
				query.setOutDayEnd(endOutDay);
				query.setIsOut(0);
				query.setStatus("N");
				notOutMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
				if (notOutMoney.compareTo(new BigDecimal(0)) == 1) {
					// 有下月未出未还
					List<Long> notOutBills = afBorrowBillService.getBillIdListByQuery(query);
					String month = DateUtil.getMonth(strOutDay);
					map.put("notOutBills", notOutBills);
					map.put("nextMonth", month);
				}
			}
			map.put("notOutMoney", notOutMoney);
			// 获取用户余额
			AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
			map.put("rebateAmount", userAccountDo.getRebateAmount());
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getMyRepaymentV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
