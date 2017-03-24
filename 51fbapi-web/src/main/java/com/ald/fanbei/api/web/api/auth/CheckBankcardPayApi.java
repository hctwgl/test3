package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsAuthPayConfirmRespBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：银行卡支付时短信验证
 *@author hexin 2017年2月28日 下午4:03:21
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkBankcardPayApi")
public class CheckBankcardPayApi implements ApiHandle {

	@Resource
	private AfOrderService afOrderService;
	@Resource 
	private AfRepaymentService afRepaymentService;
	@Resource
	private UpsUtil upsUtil;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String tradeNo = ObjectUtils.toString(requestDataVo.getParams().get("tradeNo"));
		String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
		String outTradeNo = ObjectUtils.toString(requestDataVo.getParams().get("outTradeNo"));
		String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
		String cardNo = ObjectUtils.toString(requestDataVo.getParams().get("cardNo"));
		UpsAuthPayConfirmRespBo upsResult = upsUtil.authPayConfirm(tradeNo, Base64.decodeToString(cardNo), context.getUserId()+"", verifyCode, outTradeNo, "02");
		
		if(!upsResult.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BANK_CARD_PAY_SMS_ERR);
		}
		if(PayOrderSource.ORDER.getCode().equals(type)){
			afOrderService.dealMobileChargeOrder(outTradeNo, upsResult.getTradeNo());
		}else{
			afRepaymentService.dealRepaymentSucess(outTradeNo, tradeNo);
		}
		return resp;
	}
}
