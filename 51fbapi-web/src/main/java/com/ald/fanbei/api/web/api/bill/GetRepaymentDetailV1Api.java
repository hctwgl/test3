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
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAmountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfUserAmountBizType;
import com.ald.fanbei.api.common.enums.AfUserAmountDetailType;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserAmountLogDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;
import com.sun.mail.handlers.image_gif;

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
			BigDecimal amount = new BigDecimal(0);
			String number = userAmount.getBizOrderNo();;
			String date = DateUtil.formatDate(userAmount.getGmtCreate(), DateUtil.DATE_TIME_SHORT);
			if (userAmount.getBizType() == AfUserAmountBizType.REFUND.getCode()) {
				// 退款详情
				amount = afUserAmountService.getRenfundAmountByAmountId(amountId);
				AfBorrowDto borrow = afUserAmountService.getBorrowDtoByAmountId(amountId);
				map.put("name", borrow.getName());
				map.put("bankAmount", borrow.getBankAmount());
				map.put("priceAmount", borrow.getPriceAmount());
				map.put("nper", borrow.getNper());
				map.put("nperAmount", borrow.getNperAmount());
				map.put("nperRepayment", borrow.getNperRepayment());
			}
			if (userAmount.getBizType() == AfUserAmountBizType.REPAYMENT.getCode()) {
				// 还款详情
				for (AfUserAmountDetailDo detailDo : detailList) {
					if (detailDo.getType() == AfUserAmountDetailType.SHIJIZHIFU.getCode()) {
						amount = detailDo.getAmount();
						break;
					}
				}
				List<AfUserAmountLogDo> amountLogList = afUserAmountService.getAmountLogByAmountId(userAmount.getSourceId());
				map.put("logList", amountLogList);
			}
			map.put("number", number);
			map.put("date", date);
			map.put("amount", amount);
			resp.setResponseData(map);
			return resp;
		} catch (Exception e) {
			logger.error("getRepaymentDetailV1Api error :" , e);
			resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
			return resp;
		}
	}

}
