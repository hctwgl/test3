package com.ald.fanbei.api.web.api.brand;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：品牌订单进行支付
 * @author xiaotianjian 2017年3月27日上午10:53:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("payOrderV1Api")
public class PayOrderV1Api implements ApiHandle {

	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	private AfOrderService afOrderService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfUserBankcardService afUserBankcardService;
	@Resource
	BoluomeUtil boluomeUtil;
	@Resource
	RiskUtil riskUtil;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfBorrowBillService afBorrowBillService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), null);
		Long payId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("payId"), null);
		Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), null);
		String type = ObjectUtils.toString(requestDataVo.getParams().get("type"), OrderType.BOLUOME.getCode()).toString();

		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();

		if (orderId == null || payId == null) {
			logger.error("orderId is empty or payId is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		// TODO获取用户订单
		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);

		if (orderInfo == null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}

		if (orderInfo.getStatus().equals(OrderStatus.DEALING.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_PAY_DEALING);
		}

		if (orderInfo.getStatus().equals(OrderStatus.PAID.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_HAS_PAID);
		}

		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		if (payId >= 0) {
			String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
			if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}

		String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
		String ipAddress = CommonUtil.getIpAddr(request);

		try {
			BigDecimal saleAmount = orderInfo.getSaleAmount();
			if (StringUtils.equals(type, OrderType.AGENTBUY.getCode()) || StringUtils.equals(type, OrderType.SELFSUPPORT.getCode())) {
				saleAmount = orderInfo.getActualAmount();
			}
			if (payId == 0 && (StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode()) || nper == null)) {
				nper = orderInfo.getNper();
			}
			Map<String, Object> result = afOrderService.payBrandOrder(payId, orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getGoodsName(), saleAmount, nper, appName, ipAddress);
			String success = result.get("success").toString();
			if (StringUtils.isNotBlank(success) && Boolean.parseBoolean(success)) {
//				dealWithPayOrderRiskFailed(result, resp);
				if (StringUtils.equals(type, OrderType.BOLUOME.getCode()) && payId.intValue() == 0) {
					riskUtil.payOrderChangeAmount(orderInfo.getRid());
				}
			}
			resp.setResponseData(result);
			
		} catch (FanbeiException exception) {
			return new ApiHandleResponse(requestDataVo.getId(), exception.getErrorCode());
		} catch (Exception e) {
			logger.error("pay order failed e = {}", e);
			resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		return resp;
	}
	
//	/**
//	 * 处理风控逾期订单处理
//	 * @param result
//	 * @param resp
//	 */
//	private void dealWithPayOrderRiskFailed(Map<String, Object> result, ApiHandleResponse resp) {
//		String success = result.get("success").toString();
//		//如果代付，风控支付是不通过的，找出其原因
//		if (StringUtils.isNotBlank(success) && !Boolean.parseBoolean(success)) {
//			String verifyBoStr = (String) result.get("verifybo");
//			RiskVerifyRespBo riskResp = JSONObject.parseObject(verifyBoStr, RiskVerifyRespBo.class);
//			String rejectCode = riskResp.getRejectCode();
//			RiskErrorCode erorrCode = RiskErrorCode.findRoleTypeByCode(rejectCode);
//			switch (erorrCode) {
//			case AUTH_AMOUNT_LIMIT:
//				throw new FanbeiException("pay order failed", FanbeiExceptionCode.RISK_AUTH_AMOUNT_LIMIT);
//			case OVERDUE_BORROW:
//			{
//				String borrowNo = riskResp.getBorrowNo();
//				AfBorrowDo borrowInfo = afBorrowService.getBorrowInfoByBorrowNo(borrowNo);
//				Long billId = afBorrowBillService.getOverduedAndNotRepayBill(borrowInfo.getRid());
//				resp.setResult(new AppResponse(FanbeiExceptionCode.RISK_BORROW_OVERDUED));
//				resp.addResponseData("billId", billId == null ? 0 : billId);
//			}
//				break;
//			case OVERDUE_BORROW_CASH:
//				resp.setResult(new AppResponse(FanbeiExceptionCode.RISK_BORROW_CASH_OVERDUED));
//				break;
//			case OTHER_RULE:
//				resp.setResult(new AppResponse(FanbeiExceptionCode.RISK_OTHER_RULE));
//			default:
//				break;
//			}
//		}
//	}
}
