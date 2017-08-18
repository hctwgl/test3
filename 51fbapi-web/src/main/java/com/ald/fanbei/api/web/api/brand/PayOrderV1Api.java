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
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
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
		String isCombinationPay = ObjectUtils.toString(requestDataVo.getParams().get("isCombinationPay"), "").toString();
		
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
				//自营或代买订单记录支付失败原因
				if (OrderType.getNeedRecordPayFailCodes().contains(orderInfo.getOrderType())){
					AfOrderDo currUpdateOrder = new AfOrderDo();
					currUpdateOrder.setRid(orderInfo.getRid());
					currUpdateOrder.setPayStatus(PayStatus.NOTPAY.getCode());
					currUpdateOrder.setStatus(OrderStatus.PAYFAIL.getCode());
					//支付失败
					//boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_FAIL, orderInfo.getUserId(), orderInfo.getActualAmount());
					currUpdateOrder.setStatusRemark(Constants.PAY_ORDER_PASSWORD_ERROR);
					afOrderService.updateOrder(currUpdateOrder);
				}
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}

		String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
		String ipAddress = CommonUtil.getIpAddr(request);

		try {
			BigDecimal saleAmount = orderInfo.getSaleAmount();
			if (StringUtils.equals(type, OrderType.AGENTBUY.getCode()) || StringUtils.equals(type, OrderType.SELFSUPPORT.getCode()) || StringUtils.equals(type, OrderType.TRADE.getCode())) {
				saleAmount = orderInfo.getActualAmount();
			}
			if (payId == 0 && (StringUtils.equals(orderInfo.getOrderType(), OrderType.SELFSUPPORT.getCode()) || StringUtils.equals(orderInfo.getOrderType(), OrderType.TRADE.getCode()) || nper == null)) {
				nper = orderInfo.getNper();
			}
			
			String payType = PayType.AGENT_PAY.getCode();
			    //代付
			if (payId < 0) {
				payType = PayType.WECHAT.getCode();
			} else if (payId > 0) {
				payType = PayType.BANK.getCode();
				//银行卡
			}
			
			if (StringUtil.equals(YesNoStatus.YES.getCode(), isCombinationPay)) {
				payType = PayType.COMBINATION_PAY.getCode();
				//组合
			}
			
			Map<String, Object> result = afOrderService.payBrandOrder(payId, payType, orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getGoodsName(), saleAmount, nper, appName, ipAddress);
			
			Object success = result.get("success");
			Object payStatus = result.get("status");
			if (success != null) {
				if (Boolean.parseBoolean(success.toString())) {
					//判断是否菠萝觅，如果是菠萝觅,额度支付成功，则推送成功消息，银行卡支付,则推送支付中消息
					if (StringUtils.equals(type, OrderType.BOLUOME.getCode()) ) {
						if (payId.intValue() == 0) {
							riskUtil.payOrderChangeAmount(orderInfo.getRid());
						} else if (payId > 0 &&  PayStatus.DEALING.getCode().equals(payStatus.toString())) {
							boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_DEALING, orderInfo.getUserId(), orderInfo.getActualAmount());
						}
					}
				} else {
					FanbeiExceptionCode errorCode = (FanbeiExceptionCode) result.get("errorCode");
					ApiHandleResponse response = new ApiHandleResponse(requestDataVo.getId(), errorCode);
					response.setResponseData(result);
					return response;
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
	
}
