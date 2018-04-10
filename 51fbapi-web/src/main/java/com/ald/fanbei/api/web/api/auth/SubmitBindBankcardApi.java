package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.SubmitBindBankcardParam;
import com.alibaba.fastjson.JSON;

/**
 *@类现描述：绑卡并支付订单
 *@author ZJF 2018年4月09日
 *@since 4.1.2
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitBindBankcardApi")
@Validator("submitBindBankcardParam")
public class SubmitBindBankcardApi implements ApiHandle {

	@Resource
	private AfUserBankcardService afUserBankcardService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfOrderService afOrderService;
	@Resource
	private TransactionTemplate transactionTemplate;
	
	@Resource
	private UpsUtil upsUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, final FanbeiContext context, final HttpServletRequest request) {
		final SubmitBindBankcardParam param = (SubmitBindBankcardParam) requestDataVo.getParamObj();
		final Map<String, Object> result = new HashMap<String, Object>();
		
		final AfOrderDo order;
		/* 请求包含订单 */
		if(param.orderId != null && (order = afOrderService.getOrderById(param.orderId)) != null) { 
			try {
				afOrderService.checkOrderValidity(order);
			}catch (FanbeiException e) {
				logger.warn("afOrderService.checkOrderValidity, result=" + e.getErrorCode().getErrorMsg());
				result.put("isOrderPaySucc", false);
				result.put("orderPayTipMsg", e.getErrorCode().getErrorMsg());
				
				// TODO UPS 绑卡
				// TODO DB绑卡
			}
			
			// 快捷支付 = 签约 + 支付
			try {
				final PayType payType = afOrderService.resolvePayType(param.bankCardId, param.isCombinationPay);
				final String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
				final BigDecimal finalSaleAmount;
	            if (StringUtils.equals(param.orderType, OrderType.AGENTBUY.getCode()) 
	        		|| StringUtils.equals(param.orderType, OrderType.SELFSUPPORT.getCode()) 
	        		|| StringUtils.equals(param.orderType, OrderType.TRADE.getCode()) 
	        		|| StringUtils.equals(param.orderType, OrderType.LEASE.getCode())) {
	            	finalSaleAmount = order.getActualAmount();
	            }else {
	            	finalSaleAmount = order.getSaleAmount();
	            }
	            
	            transactionTemplate.execute(new TransactionCallback<Integer>() {
					@Override
					public Integer doInTransaction(TransactionStatus status) {
						Map<String, Object> payResult = afOrderService.payBrandOrder(
								context.getUserName(), 
								param.bankCardId,
								payType.getCode(), 
								param.orderId,
								context.getUserId(), 
								order.getOrderNo(), 
								order.getThirdOrderNo(), 
								order.getGoodsName(),
								finalSaleAmount, 
								param.orderNper,
								appName, 
								CommonUtil.getIpAddr(request));
			            
						if(!Boolean.parseBoolean(payResult.get("success").toString())) {
							logger.error("afOrderService.payBrandOrder error,res = ", JSON.toJSONString(payResult));
							throw new FanbeiException("afOrderService.payBrandOrder error");
						}
						return 1;
					}
				});
	            
			}catch (Exception e) {
				throw new FanbeiException(FanbeiExceptionCode.ORDER_PAY_FAIL, e);
			}
			
			result.put("isOrderPaySucc", true);
			result.put("orderPayTipMsg", "支付成功");
			
			// TODO DB绑卡
		} 
		/* 无订单，只绑卡 */
		else {
			// TODO UPS 绑卡
			// TODO DB绑卡
		}
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		resp.setResponseData(result);

		return resp;
	}

}
