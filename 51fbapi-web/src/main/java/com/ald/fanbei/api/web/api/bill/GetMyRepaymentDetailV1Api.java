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
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.timevale.tgtext.awt.geom.q;

/**
 * 
* @ClassName: GetMyRepaymentDetailV1Api 
* @Description: 用户还款详情列表
* @author yuyue
* @date 2017年11月27日 下午5:31:08 
*
 */
@Component("getMyRepaymentDetailV1Api")
public class GetMyRepaymentDetailV1Api implements ApiHandle{

	@Resource
	AfUserService afUserService;
	
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			String status = ObjectUtils.toString(requestDataVo.getParams().get("status"));
			if (userId == null) {
				logger.error("getMyRepaymentDetailV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.error("getMyRepaymentDetailV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			if (StringUtil.isEmpty(status)) {
				logger.info("getMyRepaymentDetailV1Api status is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			List<AfBorrowBillDto> billList = new ArrayList<AfBorrowBillDto>();
			BigDecimal moneny = new BigDecimal(0);
			String month = "";
			AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
			query.setUserId(userId);
			query.setStatus(BorrowBillStatus.NO.getCode());
			if (StringUtil.equals("overdue", status)) {
				// 获取所有逾期数据
				query.setIsOut(1);
				query.setOverdueStatus("Y");
				moneny = afBorrowBillService.getUserBillMoneyByQuery(query);
				if (moneny.compareTo(new BigDecimal(0)) == 1) {
					billList = afBorrowBillService.getBillListByQuery(query);
					AfBorrowBillDto billDto = billList.get(0);
					month = DateUtil.getMonth(billDto.getGmtOutDay());
				}
			}else if (StringUtil.equals("out", status)){
				// 获取已出未逾期
				query.setIsOut(1);
				query.setOverdueStatus("N");
				moneny = afBorrowBillService.getUserBillMoneyByQuery(query);
				if (moneny.compareTo(new BigDecimal(0)) == 1) {
					billList = afBorrowBillService.getBillListByQuery(query);
					AfBorrowBillDto billDto = billList.get(0);
					month = DateUtil.getMonth(billDto.getGmtOutDay());
					String payDate = DateUtil.formatDate(billDto.getGmtOutDay(), DateUtil.DATE_TIME_FULL);
					map.put("payTime", payDate);
				}
			}else if (StringUtil.equals("notOut", status)) {
				// 先查询是否有本月已出
				Date strOutDay = DateUtil.getFirstOfMonth(new Date());
				strOutDay = DateUtil.addHoures(strOutDay, -12);
				Date endOutDay = DateUtil.addMonths(strOutDay, 1);
				query.setIsOut(1);
				query.setOutDayStr(strOutDay);
				query.setOutDayEnd(endOutDay);
				moneny = afBorrowBillService.getUserBillMoneyByQuery(query);
				if (moneny.compareTo(new BigDecimal(0)) == 0) {
					// 没有本月已出，查询是否有本月未出未还
					query.setIsOut(0);
					query.setStatus("N");
					moneny = afBorrowBillService.getUserBillMoneyByQuery(query);
					if (moneny.compareTo(new BigDecimal(0)) == 1) {
						billList = afBorrowBillService.getBillListByQuery(query);
						month = DateUtil.getMonth(strOutDay);
					}else if (moneny.compareTo(new BigDecimal(0)) == 0) {
						// 没有本月未出，查询下月未出
						strOutDay = DateUtil.addMonths(strOutDay, 1);
						endOutDay = DateUtil.addMonths(strOutDay, 1);
						query.setOutDayStr(strOutDay);
						query.setOutDayEnd(endOutDay);
						query.setIsOut(0);
						query.setStatus("N");
						moneny = afBorrowBillService.getUserBillMoneyByQuery(query);
						if (moneny.compareTo(new BigDecimal(0)) == 1) {
							// 有下月未出未还
							billList = afBorrowBillService.getBillListByQuery(query);
							month = DateUtil.getMonth(strOutDay);
						}
					}
				}else if (moneny.compareTo(new BigDecimal(0)) == 1) {
					// 有本月已出
					strOutDay = DateUtil.addMonths(strOutDay, 1);
					endOutDay = DateUtil.addMonths(strOutDay, 1);
					query.setOutDayStr(strOutDay);
					query.setOutDayEnd(endOutDay);
					query.setIsOut(0);
					query.setStatus("N");
					moneny = afBorrowBillService.getUserBillMoneyByQuery(query);
					if (moneny.compareTo(new BigDecimal(0)) == 1) {
						// 有下月未出未还
						billList = afBorrowBillService.getBillListByQuery(query);
						month = DateUtil.getMonth(strOutDay);
					}
				}
			}
			map.put("billList", billList);
			map.put("moneny", moneny);
			map.put("month", month);
			map.put("status", status);
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getMyRepaymentDetailV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
