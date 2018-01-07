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
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfUserAmountBizType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserAmountDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
* @ClassName: GetRepaymentDetailV1Api 
* @Description: 退还款详情页面
* @author yuyue
* @date 2017年11月28日 下午4:24:58 
*
 */
@Component("getRepaymentDetailV1Api")
public class GetRepaymentDetailV1Api implements ApiHandle{

	@Resource
	AfUserService afUserService;
	
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@Resource
	AfUserAmountService afUserAmountService;
	
	@Resource
	AfOrderService afOrderService;
	
	@Resource
	AfBorrowService afBorrowService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		try {
			Long userId = context.getUserId();
			Long amountId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("amountId"), 0L);
			if (amountId.equals(0L)) {
				logger.error("getRepaymentDetailV1Api amountId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			if (userId == null) {
				logger.error("getRepaymentDetailV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.REQUEST_PARAM_ERROR);
				return resp;
			}
			AfUserDo afUserDo = afUserService.getUserById(userId);
			if (afUserDo == null || afUserDo.getRid() == null) {
				logger.error("getRepaymentDetailV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
				return resp;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			AfUserAmountDo userAmount = afUserAmountService.getUserAmountById(amountId);
			if (userAmount == null) {
				logger.error("getRepaymentDetailV1Api userAmount is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId + " ,amountId=" + amountId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AMOUNT_IS_NULL);
				return resp;
			}
			List<AfUserAmountDetailDo> detailList = afUserAmountService.getAmountDetailByAmountId(amountId);
			if (detailList == null || detailList.size() < 1) {
				logger.error("getRepaymentDetailV1Api detailList is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId + " ,amountId=" + amountId);
				resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.AMOUNT_IS_NULL);
				return resp;
			}
			BigDecimal amount = new BigDecimal(0);
			String number = userAmount.getBizOrderNo();
			String date = DateUtil.formatDate(userAmount.getGmtCreate(), DateUtil.DATE_TIME_SHORT);
			// 计算系统减免
			amount = afUserAmountService.getRenfundAmountByAmountId(amountId);
			map.put("detailList", detailList);
			if (userAmount.getBizType() == AfUserAmountBizType.REFUND.getCode()) {
				AfBorrowDto borrow = afUserAmountService.getBorrowDtoByAmountId(amountId);
				// 订单内容
				Integer nperRepayment = afBorrowService.countNperRepaymentByBorrowId(borrow.getRid());
				map.put("name", borrow.getName());
				map.put("bankAmount", borrow.getBankAmount());
				map.put("priceAmount", borrow.getSaleAmount());
				map.put("nper", borrow.getNper());
				map.put("nperAmount", borrow.getNperAmount());
				map.put("nperRepayment", nperRepayment);
			}
			if (userAmount.getBizType() == AfUserAmountBizType.REPAYMENT.getCode()) {
				if (amount.compareTo(new BigDecimal(0)) == 1) {
					AfUserAmountDetailDo _amount = new AfUserAmountDetailDo();
					_amount.setAmount(amount);
					_amount.setType(7);
					_amount.setTitle("系统减免");
					detailList.add(_amount);
				}
				// 计算实际还款金额（自行支付+余额抵扣+优惠卷抵扣）
				BigDecimal repaymentAmount = afUserAmountService.getRepaymentAmountByAmountId(amountId);
				AfUserAmountDetailDo repaymentAmountDo = new AfUserAmountDetailDo();
				// 产品说实际还款金额用整数展示
				repaymentAmountDo.setAmount(repaymentAmount.abs());
				repaymentAmountDo.setType(8);
				repaymentAmountDo.setTitle("实际还款金额");
				detailList.add(repaymentAmountDo);
				// 还款日志
				List<AfUserAmountLogDo> amountLogList = afUserAmountService.getAmountLogByAmountId(userAmount.getSourceId());
				map.put("logList", amountLogList);
			}
			map.put("number", number);
			map.put("date", date);
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getRepaymentDetailV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
