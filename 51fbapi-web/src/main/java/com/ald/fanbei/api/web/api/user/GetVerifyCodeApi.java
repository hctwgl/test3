package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.TongdunEventEnmu;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
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

import java.util.Date;

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
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	BaiQiShiUtils baiQiShiUtils;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String requestId = requestDataVo.getId();
		String majiabaoName = requestId.substring(requestId.lastIndexOf("_") + 1, requestId.length());
		String mobile = ObjectUtils.toString(requestDataVo.getParams().get("mobile"));
		String typeParam = ObjectUtils.toString(requestDataVo.getParams().get("type"));
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		String smsType = "";
		if (StringUtils.isBlank(mobile) || StringUtils.isBlank(typeParam)) {
			logger.error("verifyCode or type is empty mobile = " + mobile + " type = " + typeParam);
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}

		if (!CommonUtil.isMobile(mobile)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_INVALID_MOBILE_NO);
		}

		if (majiabaoName.contains("borrowSuperman")){
			smsType = "YS";
		}
		AfUserDo afUserDo = null;
		SmsType type = SmsType.findByCode(typeParam);
		if(SmsType.REGIST.equals(type) || SmsType.MOBILE_BIND.equals(type)) {// 除了注册和更换手机号功能，其余须检查手机号是否存在
		}else if (SmsType.QUICK_LOGIN.equals(type)){//快速注册
		}
		else {
			afUserDo = afUserService.getUserByUserName(mobile);
			if (afUserDo == null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
			}
		}
		AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, type.getCode());//查看短信60秒内是否发过
		if (null != smsDo && null != smsDo.getGmtCreate() && 0 == smsDo.getIsCheck()){
			if (!DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_SIXTY))) {
				throw new FanbeiException("invalid Sms or email", FanbeiExceptionCode.USER_REGIST_SMS_LESSDUE);
			}
		}
		boolean resultSms = false;
		switch (type) {
		case REGIST:// 注册短信
			if (context.getAppVersion() >= 340) {
				if (StringUtils.isBlank(blackBox)) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);

				}
				try {
					baiQiShiUtils.getRegistResult(requestDataVo.getId(),bqsBlackBox,CommonUtil.getIpAddr(request),mobile,"","","","");
				}catch (Exception e){
					logger.error("getVerifyCodeApi baiQiShiUtils getRegistResult error => {}",e.getMessage());
				}
				// todo 这里面放同盾代码,下面是示例
				tongdunUtil.getRegistResult(requestDataVo.getId(), blackBox, CommonUtil.getIpAddr(request), mobile,
						mobile, "", "", "");

			}

			afUserDo = afUserService.getUserByUserName(mobile);
			if (afUserDo != null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_HAS_REGIST_ERROR);
			}
			if ("YS".equals(smsType)){
				resultSms = smsUtil.sendJKCRRegistVerifyCode(mobile);
			}else {
				resultSms = smsUtil.sendRegistVerifyCode(mobile);
			}
			break;
		case FORGET_PASS:// 忘记密码
			if ("YS".equals(smsType)){
				resultSms = smsUtil.sendJKCRForgetPwdVerifyCode(mobile);
			}else {
				resultSms = smsUtil.sendForgetPwdVerifyCode(mobile, afUserDo.getRid());
			}
			break;
		case QUICK_SET_PWD:// 设置快速登录密码
			if ("YS".equals(smsType)){
				resultSms = smsUtil.sendJKCRSetQuickPwdVerifyCode(mobile);
			}else {
				resultSms = smsUtil.sendSetQuickPwdVerifyCode(mobile, afUserDo.getRid());
			}
			break;
		case LOGIN:
			if ("YS".equals(smsType)){
				resultSms = smsUtil.sendJKCRLoginVerifyCode(mobile,afUserDo.getRid());
			}else {
				resultSms = smsUtil.sendLoginVerifyCode(mobile,afUserDo.getRid());
			}
			break;
		case QUICK_LOGIN:
			afUserDo = afUserService.getUserByUserName(mobile);
			if (null != afUserDo){//快速登录
				resp.addResponseData("code",1000);
				if ("YS".equals(smsType)){
					resultSms = smsUtil.sendJKCRQuickLoginVerifyCode(mobile,afUserDo.getRid());
				}else {
					resultSms = smsUtil.sendQuickLoginVerifyCode(mobile,afUserDo.getRid());
				}
				break;
			}
			if (context.getAppVersion() >= 340) {//快速注册
				if (StringUtils.isBlank(blackBox)) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
				}
				try {
					baiQiShiUtils.getRegistResult(requestDataVo.getId(),bqsBlackBox,CommonUtil.getIpAddr(request),mobile,"","","","");
				}catch (Exception e){
					logger.error("getVerifyCodeApi baiQiShiUtils getRegistResult error => {}",e.getMessage());
				}
				// todo 这里面放同盾代码,下面是示例
				tongdunUtil.getRegistResult(requestDataVo.getId(), blackBox, CommonUtil.getIpAddr(request), mobile,
						mobile, "", "", "");

			}
			if ("YS".equals(smsType)){
				resultSms = smsUtil.sendJKCRQuickRegistVerifyCode(mobile);
			}else {
				resultSms = smsUtil.sendQuickRegistVerifyCode(mobile);
			}
			resp.addResponseData("code",1146);
			break;
		case MOBILE_BIND:// 更换手机号
			if ("YS".equals(smsType)){
				resultSms = smsUtil.sendJKCRMobileBindVerifyCode(mobile,SmsType.MOBILE_BIND,1l);
			}else {
				resultSms = smsUtil.sendMobileBindVerifyCode(mobile,SmsType.MOBILE_BIND,1l);
			}
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
