package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;
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
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.SubmitSuperBindBankcardParam;
import com.alibaba.fastjson.JSON;

/**
 *@类现描述：绑卡并支付订单
 *@author ZJF 2018年4月09日
 *@since 4.1.2
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitSuperBindBankcardApi")
@Validator("submitSuperBindBankcardParam")
public class SubmitSuperBindBankcardApi implements ApiHandle {

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
		final SubmitSuperBindBankcardParam param = (SubmitSuperBindBankcardParam) requestDataVo.getParamObj();
		
		final AfOrderDo order = afOrderService.getOrderById(param.orderId);
		afOrderService.checkOrderValidity(order);
		
		// 快捷支付 = 签约 + 支付
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
				AfUserAccountDo userAccDB = afUserAccountService.getUserAccountByUserId(context.getUserId());
				AfUserAccountDo userAccForUpdate = new AfUserAccountDo();
				
				if(userAccDB.getPassword() == null) { //支付密码为空，则此次请求需设置支付密码
					if(StringUtils.isEmpty(param.payPwd)) {
						throw new FanbeiException(FanbeiExceptionCode.BINDCARD_PAY_PWD_MISS);
					} else {
						String salt = UserUtil.getSalt();
						String newPwd = UserUtil.getPassword(param.payPwd, salt);
						userAccForUpdate.setUserId(context.getUserId());
						userAccForUpdate.setSalt(salt);
						userAccForUpdate.setPassword(newPwd);
					}
				}
				
				if(userAccDB.getRealName() == null) { //真实姓名为空，则此次请求需存入身份证信息
					if(StringUtils.isEmpty(param.realName)) {
						throw new FanbeiException(FanbeiExceptionCode.BINDCARD_REALINFO_MISS);
					} else {
						userAccForUpdate.setUserId(context.getUserId());
						userAccForUpdate.setRealName(param.realName);
						userAccForUpdate.setIdNumber(param.idNumber);
					}
				}
				
				if(userAccForUpdate.getUserId() != null) { // 可选更新用户账户信息
					afUserAccountService.updateUserAccount(userAccForUpdate);
				}
				
				// 设置卡状态为可用
				AfUserBankcardDo bank = afUserBankcardService.getUserBankcardById(param.bankCardId);
				bank.setStatus(BankcardStatus.BIND.getCode());
				afUserBankcardService.updateUserBankcard(bank);
				
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
					throw new FanbeiException(FanbeiExceptionCode.ORDER_PAY_FAIL);
				}
				return 1;
			}
		});
		
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		return resp;
	}

}
