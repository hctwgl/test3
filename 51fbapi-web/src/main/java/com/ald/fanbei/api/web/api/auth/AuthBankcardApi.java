package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAuthYdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：银行卡认证（添加银行卡）,用户在完成人脸识别之后可以绑定银行卡，绑定银行卡时需要调用第三方认证，直接调用支付通道（融都开发那套）相关接口
 *@author chenjinhu 2017年2月17日 下午4:16:29
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authBankcardApi")
public class AuthBankcardApi implements ApiHandle {

	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfAuthYdService afAuthYdService;
	@Resource
	private SmsUtil smsUtil;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
//		String cardNumber = (String)requestDataVo.getParams().get("cardNumber");
		String mobile = (String)requestDataVo.getParams().get("mobile");
		String verifyCode = (String)requestDataVo.getParams().get("verifyCode");
		//验证短信
		smsUtil.checkSmsByMobileAndType(mobile, verifyCode, SmsType.BANK_CARD);
		//银行卡四要素验证
//		AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(context.getUserId());
//		String authResult = YoudunUtil.fourItemCheck(userAccount.getRealName(), userAccount.getIdNumber(), cardNumber, mobile);
		
//		AfAuthYdDo authYd = new AfAuthYdDo();
//		authYd.setAuthParam(StringUtil.appendStrs(userAccount.getRealName(),"|",userAccount.getIdNumber(),"|",cardNumber,"|",mobile));
//		authYd.setAuthResult(authResult);
//		authYd.setUserId(context.getUserId());
//		authYd.setType("BANK_CARD");
//		afAuthYdService.addAuthYd(authYd);
		
//		JSONObject resultJson = JSONObject.parseObject(authResult);
//		JSONObject header = resultJson.getJSONObject("header");
//		JSONObject body = resultJson.getJSONObject("body");
//		
//		if( header == null || !StringUtil.equals(header.getString("ret_code"), "000000") || body == null || !StringUtil.equals(body.getString("status"), "1")){
//			throw new FanbeiException(FanbeiExceptionCode.AUTH_CARD_ERROR);
//		}
		
		//TODO 绑卡
		
		//更新userAuth记录
		return resp;
	}

}
