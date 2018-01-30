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
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
* @ClassName: GetBorrowDetailV1Api 
* @Description: 获取账单消费明细——分期详情
* @author yuyue
* @date 2017年11月22日 下午4:37:26 
*
 */

@Component("getBorrowDetailV1Api")
public class GetBorrowDetailV1Api implements ApiHandle{

	@Resource
	private AfUserService afUserService;
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfBorrowService afBorrowService;
	
	@Resource
	private AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			AfUserDo afUserDo = afUserService.getUserById(userId);
			Long billId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("billId"), 0L);
			if (billId == null || billId.equals(0L)) {
				logger.info("getBorrowDetailV1Api billId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
				return resp;
			}
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.info("getBorrowDetailV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			AfBorrowBillDo queryBillDo = afBorrowBillService.getBorrowBillById(billId);
			if (queryBillDo == null || queryBillDo.getRid() == null) {
				logger.info("getBorrowDetailV1Api bill is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId + " ,billId=" + billId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.BORROW_BILL_NOT_EXIST_ERROR);
				return resp;
			}
			AfBorrowDo queryBorrowDo = afBorrowService.getBorrowById(queryBillDo.getBorrowId());
			if (queryBorrowDo == null || queryBorrowDo.getRid() == null) {
				logger.info("getBorrowDetailV1Api borrow is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId + " ,billId=" + billId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.BORROW_BILL_NOT_EXIST_ERROR);
				return resp;
			}
			AfOrderDo queryOrderDo = afOrderService.getOrderInfoByIdWithoutDeleted(queryBorrowDo.getOrderId(),userId);
			if (queryOrderDo == null || queryOrderDo.getRid() == null) {
				logger.info("getBorrowDetailV1Api borrow is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId + " ,billId=" + billId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.ORDER_NOT_EXIST);
				return resp;
			}
			List<AfBorrowBillDo> billList = afBorrowBillService.getAllBorrowBillByBorrowId(queryBorrowDo.getRid());
			map.put("billList", billList);
			map.put("orderDetail", queryOrderDo);
			map.put("amount", queryBorrowDo.getAmount());
			map.put("borrowId", queryBorrowDo.getRid());
			// 手续费
			BigDecimal interest = afBorrowBillService.getInterestByBorrowId(queryBorrowDo.getRid());
			map.put("interest", interest);
			// 逾期利息
			BigDecimal overdueInterest = afBorrowBillService.getOverdueInterestByBorrowId(queryBorrowDo.getRid());
			map.put("overdueInterest", overdueInterest);
			map.put("borrowNo", queryBillDo.getBorrowNo());
			map.put("gmtBorrow", queryBillDo.getGmtBorrow());
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getBorrowDetailV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
