package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.service.AfPromotionChannelPointService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ClientTypeEnum;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfPromotionChannelPointDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月19日下午1:48:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("quickRegisterPwdApi")
public class QuickRegisterPwdApi implements ApiHandle {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSmsRecordService afSmsRecordService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfPromotionChannelPointService afPromotionChannelPointService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	RiskUtil riskUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String requestId = requestDataVo.getId();
		String majiabaoName = requestId.substring(requestId.lastIndexOf("_") + 1, requestId.length());
		String userName = context.getUserName();
//		String passwordSrc = ObjectUtils.toString(requestDataVo.getParams().get("password"));
		String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
		String nick = ObjectUtils.toString(requestDataVo.getParams().get("nick"), null);
		String recommendCode = ObjectUtils.toString(requestDataVo.getParams().get("recommendCode"), null);//邀请码
		String registerChannelPointId = ObjectUtils.toString(requestDataVo.getParams().get("channelPointId"), null);

		//风控可信异步通知
		String ip = CommonUtil.getIpAddr(request);
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
		String phoneType = ObjectUtils.toString(requestDataVo.getParams().get("phoneType"));
		String networkType = ObjectUtils.toString(requestDataVo.getParams().get("networkType"));
		String osType = ObjectUtils.toString(requestDataVo.getParams().get("osType"));
		
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);

		if (afUserDo != null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_HAS_REGIST_ERROR);
		}
		/*if (StringUtil.isBlank(passwordSrc)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}*/
		AfUserDo userDo = new AfUserDo();
		// 判断邀请码是否为空
		if (StringUtil.isNotEmpty(recommendCode)) {
			AfUserDo recommendUserDo = afUserService.getUserByRecommendCode(recommendCode);
			if (recommendUserDo == null) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CODE_NOT_EXIST);
			} else {
				// 写入邀请人邀请码
				userDo.setRecommendId(recommendUserDo.getRid());
			}

		}
		String salt = UserUtil.getSalt();
//		String password = UserUtil.getPassword(passwordSrc, salt);
		userDo.setSalt(salt);
		userDo.setUserName(userName);
		userDo.setMobile(userName);
		userDo.setNick(nick);
//		userDo.setPassword(password);
//		 userDo.setRegisterChannelId(registerChannelId);
		if (registerChannelPointId != null) {
			AfPromotionChannelPointDo channelPointDo = afPromotionChannelPointService.getPoint("Andriod", registerChannelPointId);
			AfPromotionChannelPointDo channelPointDoIOS = afPromotionChannelPointService.getPoint("IOS", registerChannelPointId);
			//根据设备来源区分处理
			ClientTypeEnum clientType = StringUtil.judgeClientType(requestDataVo.getId());
			String clientTypeCode = clientType!=null?clientType.getCode():"";
			if(ClientTypeEnum.ANDROID.getCode().equals(clientTypeCode)){
				if (channelPointDo != null) {
					userDo.setRegisterChannelPointId(channelPointDo.getId());
					userDo.setRegisterChannelId(channelPointDo.getChannelId());
				} else if (channelPointDoIOS != null) {
					userDo.setRegisterChannelPointId(channelPointDoIOS.getId());
					userDo.setRegisterChannelId(channelPointDoIOS.getChannelId());
					logger.warn("setRegisterPwdApi setRegisterChannelId type not match,source is android,set into ios.registerChannelPointId="+registerChannelPointId);
				}
			}else if(ClientTypeEnum.IOS.getCode().equals(clientTypeCode)){
				if (channelPointDoIOS != null) {
					userDo.setRegisterChannelPointId(channelPointDoIOS.getId());
					userDo.setRegisterChannelId(channelPointDoIOS.getChannelId());
				} else if ( channelPointDo!= null) {
					userDo.setRegisterChannelPointId(channelPointDo.getId());
					userDo.setRegisterChannelId(channelPointDo.getChannelId());
					logger.warn("setRegisterPwdApi setRegisterChannelId type not match,source is ios,set into android.registerChannelPointId="+registerChannelPointId);
				}
			}else{
				//走默认之前的处理
				if (channelPointDo != null) {
					userDo.setRegisterChannelPointId(channelPointDo.getId());
					userDo.setRegisterChannelId(channelPointDo.getChannelId());
				} else if (channelPointDoIOS != null) {
					userDo.setRegisterChannelPointId(channelPointDoIOS.getId());
					userDo.setRegisterChannelId(channelPointDoIOS.getChannelId());
				}
				logger.warn("setRegisterPwdApi setRegisterChannelId source is not found,set into default.registerChannelPointId="+registerChannelPointId);
			}
		}
		smsUtil.checkSmsByMobileAndType(context.getUserName(),verifyCode, SmsType.QUICK_LOGIN);//短信验证码判断
		userDo.setRecommendId(0l);
		if (!StringUtils.isBlank(recommendCode)) {
			AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
			userDo.setRecommendId(userRecommendDo.getRid());
		}
		userDo.setMajiabaoName(majiabaoName);
		long userId = afUserService.toAddUser(userDo,"quickRegister");

		Long invteLong = Constants.INVITE_START_VALUE + userId;
		String inviteCode = Long.toString(invteLong, 36);
		userDo.setRecommendCode(inviteCode);
		afUserService.updateUser(userDo);

		// 注册完成,给用户发送注册成功的短信
		// smsUtil.sendRegisterSuccessSms(userDo.getUserName());

		afUserDo = afUserService.getUserByUserName(userName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String registerTime = sdf.format(new Date(System.currentTimeMillis()));
		//风控可信异步通知
		if (context.getAppVersion() >= 381) {
			riskUtil.verifyASyRegister(ObjectUtils.toString(afUserDo.getRid(), ""), userName, blackBox, uuid,
					registerTime, ip, phoneType, networkType, osType,Constants.EVENT_RIGISTER_ASY,bqsBlackBox);
		}
		return resp;
	}

}
