package com.ald.fanbei.api.web.api.agencybuy;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年5月29日下午9:37:33
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("payAgencyOrderV1Api")
public class PayAgencyOrderV1Api implements ApiHandle {
	@Resource
	RiskUtil riskUtil;
	@Resource
	private AfOrderService afOrderService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("pwd"), "").toString();

		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"), null);
		String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
		String ipAddress = CommonUtil.getIpAddr(request);
		Long payId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("payId"), 0l);
		String isCombinationPay = ObjectUtils.toString(requestDataVo.getParams().get("isCombinationPay"), "").toString();
//		Integer appVersion = context.getAppVersion();

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
		String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
		if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
			//自营或代买订单记录支付失败原因
			if (OrderType.getNeedRecordPayFailCodes().contains(orderInfo.getOrderType())){
				AfOrderDo currUpdateOrder = new AfOrderDo();
				currUpdateOrder.setRid(orderInfo.getRid());
				currUpdateOrder.setPayStatus(PayStatus.NOTPAY.getCode());
				currUpdateOrder.setStatus(OrderStatus.PAYFAIL.getCode());
				currUpdateOrder.setStatusRemark(Constants.PAY_ORDER_PASSWORD_ERROR);
				afOrderService.updateOrder(currUpdateOrder);
			}
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}
		
//		String payType = PayType.AGENT_PAY.getCode();
//		if (payId > 0) {// 有银行卡ID 表示组合支付
//			payType = PayType.COMBINATION_PAY.getCode();
//		}
		String payType = PayType.AGENT_PAY.getCode();
		if (payId < 0) {
			payType = PayType.WECHAT.getCode();
		} else if (payId > 0) {
			payType = PayType.BANK.getCode();
		}
		
		if (StringUtil.equals(YesNoStatus.YES.getCode(), isCombinationPay)) {
			payType = PayType.COMBINATION_PAY.getCode();
		}		
		
		afOrderService.payBrandOrder(context.getUserName(), payId, payType, orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getGoodsName(), orderInfo.getActualAmount(), orderInfo.getNper(), appName, ipAddress);

		// String success = result.get("success").toString();
		// if (StringUtils.isNotBlank(success) && !Boolean.parseBoolean(success)) {
		// dealWithPayOrderRiskFailed(result, resp);
		// }
		return resp;
	}

	/**
	 * 处理风控逾期借款或者分期处理
	 * 
	 * @param result
	 * @param resp
	 */
//	private void dealWithPayOrderRiskFailed(Map<String, Object> result, ApiHandleResponse resp) {
//		String success = result.get("success").toString();
//		// 如果代付，风控支付是不通过的，找出其原因
//		if (StringUtils.isNotBlank(success) && !Boolean.parseBoolean(success)) {
//			String verifyBoStr = (String) result.get("verifybo");
//			RiskVerifyRespBo riskResp = JSONObject.parseObject(verifyBoStr, RiskVerifyRespBo.class);
//			String rejectCode = riskResp.getRejectCode();
//			RiskErrorCode erorrCode = RiskErrorCode.findRoleTypeByCode(rejectCode);
//			switch (erorrCode) {
//			case AUTH_AMOUNT_LIMIT:
//				throw new FanbeiException("pay order failed", FanbeiExceptionCode.RISK_AUTH_AMOUNT_LIMIT);
//			case OVERDUE_BORROW: {
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
