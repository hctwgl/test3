/**
 * 
 */
package com.ald.fanbei.api.web.api.agencybuy;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.RiskErrorCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.AppResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * @author suweili 2017年5月29日下午9:37:33
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("payAgencyOrderV1Api")
public class PayAgencyOrderV1Api implements ApiHandle {
	@Resource
	private AfOrderService afOrderService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("pwd"), "").toString();

		Long orderId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("orderId"),null);
		String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
		String ipAddress = CommonUtil.getIpAddr(request);
		AfOrderDo orderInfo = afOrderService.getOrderById(orderId);
		
		if (orderInfo ==  null) {
			logger.error("orderId is invalid");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		if (orderInfo.getStatus().equals(OrderStatus.DEALING.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_PAY_DEALING);
		}
		
		if (orderInfo.getStatus().equals(OrderStatus.PAID.getCode())) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ORDER_HAS_PAID);
		}
//		try {
			AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
			String inputOldPwd = UserUtil.getPassword(payPwd, userAccountInfo.getSalt());
			if (!StringUtils.equals(inputOldPwd, userAccountInfo.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
			
			Map<String,Object> result = afOrderService.payBrandOrder(0l, orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getGoodsName(),orderInfo.getActualAmount() , orderInfo.getNper(),appName,ipAddress);
			String success = result.get("success").toString();
			if (StringUtils.isBlank(success) && !Boolean.getBoolean(success)) {
				dealWithPayOrderRiskFailed(result, resp);
			}
			resp.setResponseData(result);

//		} catch (Exception e) {
//			logger.error("pay Agency  order failed e = {}", e);
//
//			resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
//
//		}
		return resp;

	}
	
	private void dealWithPayOrderRiskFailed(Map<String, Object> result, ApiHandleResponse resp) {
		String success = result.get("success").toString();
		//如果代付，风控支付是不通过的，找出其原因
		if (StringUtils.isBlank(success) && !Boolean.getBoolean(success)) {
			String verifyBoStr = (String) result.get("verifybo");
			RiskVerifyRespBo riskResp = JSONObject.parseObject(verifyBoStr, RiskVerifyRespBo.class);
			String rejectCode = riskResp.getRejectCode();
			RiskErrorCode erorrCode = RiskErrorCode.findRoleTypeByCode(rejectCode);
			switch (erorrCode) {
			case AUTH_AMOUNT_LIMIT:
				throw new FanbeiException("pay order failed", FanbeiExceptionCode.RISK_AUTH_AMOUNT_LIMIT);
			case OVERDUE_BORROW:
			{
				String borrowNo = riskResp.getBorrowNo();
				AfBorrowDo borrowInfo = afBorrowService.getBorrowInfoByBorrowNo(borrowNo);
				Long billId = afBorrowBillService.getOverduedAndNotRepayBill(borrowInfo.getRid());
				resp.setResult(new AppResponse(FanbeiExceptionCode.RISK_BORROW_OVERDUED));
				resp.addResponseData("billId", billId == null ? 0 : billId);
			}
				break;
			case OVERDUE_BORROW_CASH:
				resp.setResult(new AppResponse(FanbeiExceptionCode.RISK_BORROW_CASH_OVERDUED));
				break;
			case OTHER_RULE:
				resp.setResult(new AppResponse(FanbeiExceptionCode.RISK_OTHER_RULE));
			default:
				break;
			}
		}
	}

}
