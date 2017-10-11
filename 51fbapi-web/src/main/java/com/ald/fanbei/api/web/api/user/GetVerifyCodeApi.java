package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月19日下午7:51:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getVerifyCodeApi")
public class GetVerifyCodeApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	TongdunUtil tongdunUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
		String typeParam = ObjectUtils.toString(requestDataVo.getParams().get("type"));
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));

		if (StringUtils.isBlank(mobile) || StringUtils.isBlank(typeParam)) {
			logger.error("verifyCode or type is empty mobile = " + mobile + " type = " + typeParam);
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}

		if (!CommonUtil.isMobile(mobile)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_INVALID_MOBILE_NO);
		}
		
		AfUserDo afUserDo = null;
		SmsType type = SmsType.findByCode(typeParam);
		if(SmsType.REGIST.equals(type) || SmsType.MOBILE_BIND.equals(type)) {// 除了注册和更换手机号功能，其余须检查手机号是否存在
		}else {
			afUserDo = afUserService.getUserByUserName(mobile);
			if (afUserDo == null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
			}
		}
		
		boolean resultSms = false;
		switch (type) {
		case REGIST:// 注册短信
			if (context.getAppVersion() >= 340) {
				if (StringUtils.isBlank(blackBox)) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);

				}
				// todo 这里面放同盾代码,下面是示例
				tongdunUtil.getRegistResult(requestDataVo.getId(), blackBox, CommonUtil.getIpAddr(request), mobile,
						mobile, "", "", "");
			}

			afUserDo = afUserService.getUserByUserName(mobile);
			if (afUserDo != null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_HAS_REGIST_ERROR);
			}
			resultSms = smsUtil.sendRegistVerifyCode(mobile);
			break;
		case FORGET_PASS:// 忘记密码
			resultSms = smsUtil.sendForgetPwdVerifyCode(mobile, afUserDo.getRid());
			break;
		case LOGIN:
			resultSms = smsUtil.sendLoginVerifyCode(mobile,afUserDo.getRid());
			break;
		case MOBILE_BIND:// 更换手机号
			resultSms = smsUtil.sendMobileBindVerifyCode(mobile,afUserDo.getRid());
			break;
		default:
			logger.error("type is invalid,type = " + typeParam);
			break;
		}
		
		if (!resultSms) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_SEND_SMS_ERROR);
		}
		
		return resp;
	}
	
}
