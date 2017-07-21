/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfPromotionChannelPointService;
import com.ald.fanbei.api.biz.service.AfPromotionChannelService;
import com.ald.fanbei.api.biz.service.AfPromotionLogsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.AfPromotionChannelDo;
import com.ald.fanbei.api.dal.domain.AfPromotionChannelPointDo;
import com.ald.fanbei.api.dal.domain.AfPromotionLogsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月28日上午11:50:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/user/")
public class AppH5UserContorler extends BaseController {

	// @Resource
	// AfUserAccountDao afUserAccountDao;
	@Resource
	AfUserAccountService afUserAccountService;

	@Resource
	AfUserService afUserService;

	@Resource
	AfCouponDao afCouponDao;

	@Resource
	AfUserCouponDao afUserCouponDao;

	@Resource
	AfResourceService afResourceService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfSmsRecordService afSmsRecordService;

	@Resource
	AfPromotionChannelPointService afPromotionChannelPointService;
	@Resource
	AfPromotionChannelService afPromotionChannelService;
	@Resource
	AfPromotionLogsService afPromotionLogsService;
	@Resource
	TongdunUtil tongdunUtil;
	
	
	
	@RequestMapping(value = { "invitationGift" }, method = RequestMethod.GET)
	public void invitationGift(HttpServletRequest request, ModelMap model) throws IOException {

		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		AfUserDo afUserDo = afUserService.getUserByUserName(userName);
		model.put("avatar", afUserDo.getAvatar());
		model.put("userName", afUserDo.getUserName());
		model.put("recommendCode", afUserDo.getRecommendCode());
		model.put("mobile", afUserDo.getMobile());
		doMaidianLog(request,JSON.toJSONString(model));
	}

	@RequestMapping(value = { "register" }, method = RequestMethod.GET)
	public void register(HttpServletRequest request, ModelMap model) throws IOException {
		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(AfResourceType.RegisterProtocol.getCode());
		String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + resourceDo.getValue();

		model.put("registerRule", notifyUrl);
		doMaidianLog(request,JSON.toJSONString(model));
	}

	@ResponseBody
	@RequestMapping(value = "getRegisterSmsCode", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getRegisterSmsCode(HttpServletRequest request, ModelMap model) throws IOException {
		Calendar calStart = Calendar.getInstance();
		String resultStr = "";
		try {
			String mobile = ObjectUtils.toString(request.getParameter("mobile"), "").toString();
			String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
			try {
				tongdunUtil.getPromotionResult(token,null,null,CommonUtil.getIpAddr(request),mobile, mobile, "");
			} catch (Exception e) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			
			AfUserDo afUserDo = afUserService.getUserByUserName(mobile);

			if (afUserDo != null) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_HAS_REGIST_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			boolean resultReg = smsUtil.sendRegistVerifyCode(mobile);
			if (!resultReg) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_SEND_SMS_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}

			resultStr = H5CommonResponse.getNewInstance(true, "成功", "", null).toString();
			return resultStr;

		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
			return resultStr;
		}finally{
			doLog(request, resultStr,"", Calendar.getInstance().getTimeInMillis()-calStart.getTimeInMillis(),request.getParameter("mobile"));
		}

	}

	@ResponseBody
	@RequestMapping(value = "commitRegister", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String commitRegister(HttpServletRequest request, ModelMap model) throws IOException {
		Calendar calStart = Calendar.getInstance();
		String resultStr = "";
		
		try {
			String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
			String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
			String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
			String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();
			String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
			
			
			AfUserDo eUserDo = afUserService.getUserByUserName(mobile);
			if (eUserDo != null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null).toString();

			}
			AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
			if (smsDo == null) {
				logger.error("sms record is empty");
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}

			String realCode = smsDo.getVerifyCode();
			if (!StringUtils.equals(verifyCode, realCode)) {
				logger.error("verifyCode is invalid");
				resultStr =  H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			if (smsDo.getIsCheck() == 1) {
				logger.error("verifyCode is already invalid");
				resultStr =  H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			// 判断验证码是否过期
			if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
				resultStr =  H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();
				return resultStr;

			}
			try {
				tongdunUtil.getPromotionResult(token,null,null,CommonUtil.getIpAddr(request),mobile, mobile, "");
			} catch (Exception e) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}

			// 更新为已经验证
			afSmsRecordService.updateSmsIsCheck(smsDo.getRid());

			String salt = UserUtil.getSalt();
			String password = UserUtil.getPassword(passwordSrc, salt);

			AfUserDo userDo = new AfUserDo();
			userDo.setSalt(salt);
			userDo.setUserName(mobile);
			userDo.setMobile(mobile);
			userDo.setNick("");
			userDo.setPassword(password);
	        userDo.setRecommendId(0l);
			if (!StringUtils.isBlank(recommendCode)) {
				AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
				userDo.setRecommendId(userRecommendDo.getRid());
			}
			afUserService.addUser(userDo);

			Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
			String inviteCode = Long.toString(invteLong, 36);
			userDo.setRecommendCode(inviteCode);
			afUserService.updateUser(userDo);

			// 获取邀请分享地址
			AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
			String appDownLoadUrl = "";
			if (resourceCodeDo != null) {
				appDownLoadUrl = resourceCodeDo.getValue();
			}
			resultStr = H5CommonResponse.getNewInstance(true, "成功", appDownLoadUrl, null).toString();
			
			// 注册成功给用户发送注册短信
//			smsUtil.sendRegisterSuccessSms(userDo.getUserName());
			return resultStr;

		}catch(FanbeiException e){
			logger.error("commitRegister fanbei exception"+e.getMessage());
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
			return resultStr;
		} catch (Exception e) {
			logger.error("commitRegister exception",e);
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
			return resultStr;
		}finally{
			doLog(request, resultStr,"", Calendar.getInstance().getTimeInMillis()-calStart.getTimeInMillis(),request.getParameter("registerMobile"));
		}

	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		return null;
	}

	@RequestMapping(value = { "channelRegister" }, method = RequestMethod.GET)
	public void channelRegister(HttpServletRequest request, ModelMap model) throws IOException {
		String channelCode = ObjectUtils.toString(request.getParameter("channelCode"), "").toString();
		String pointCode = ObjectUtils.toString(request.getParameter("pointCode"), "").toString();
		try{
			if (StringUtils.isBlank(channelCode) || StringUtils.isBlank(pointCode)) {
				throw new FanbeiException("缺少参数！");
			} else {
				AfPromotionChannelPointDo pcp = afPromotionChannelPointService.getPoint(channelCode, pointCode);
				if (pcp == null) {
					throw new FanbeiException("推广渠道不存在！");
				} else {
					AfPromotionChannelDo pc = afPromotionChannelService.getById(pcp.getChannelId());
					model.put("copyright", pc.getCopyright());
					model.put("sessionId", request.getSession().getId());
					model.put("channelCode", pc.getCode());
					model.put("pointCode", pcp.getCode());
					model.put("style", pcp.getStyle());
					model.put("tdHost", "https://fp.fraudmetrix.cn");
					logger.info(JSON.toJSONString(model));
	
					afPromotionChannelPointService.addVisit(pcp.getId());
	
					AfPromotionLogsDo afPromotionLogsDo = new AfPromotionLogsDo();
					afPromotionLogsDo.setChannelId(pc.getId());
					afPromotionLogsDo.setPointId(pcp.getId());
					afPromotionLogsDo.setIp(CommonUtil.getIpAddr(request));
					afPromotionLogsService.addAfPromotionLogs(afPromotionLogsDo);
				}
			}
		}finally{
			doLog(request, "", null, 0l, channelCode);
		}

	}

	@ResponseBody
	@RequestMapping(value = "commitChannelRegister", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String commitChannelRegister(HttpServletRequest request, ModelMap model) throws IOException {
		Calendar calStart = Calendar.getInstance();
		String resultStr = "";
		try {
			String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
			String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
			String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
			String channelCode = ObjectUtils.toString(request.getParameter("channelCode"), "").toString();
			String pointCode = ObjectUtils.toString(request.getParameter("pointCode"), "").toString();
			String token = ObjectUtils.toString(request.getParameter("token"), "").toString();


			AfPromotionChannelPointDo pcp = afPromotionChannelPointService.getPoint(channelCode, pointCode);
			if (pcp == null) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_CHANNEL_NOTEXIST.getDesc(), "", null).toString();
				return resultStr;
			}

			AfUserDo eUserDo = afUserService.getUserByUserName(mobile);
			if (eUserDo != null) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null).toString();
				return resultStr;

			}
			AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
			if (smsDo == null) {
				logger.error("sms record is empty");
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}

			String realCode = smsDo.getVerifyCode();
			if (!StringUtils.equals(verifyCode, realCode)) {
				logger.error("verifyCode is invalid");
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			if (smsDo.getIsCheck() == 1) {
				logger.error("verifyCode is already invalid");
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			// 判断验证码是否过期
			if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();
				return resultStr;

			}
			try {
				tongdunUtil.getPromotionResult(token,channelCode,pointCode,CommonUtil.getIpAddr(request),mobile, mobile, "");
			} catch (Exception e) {
				resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
				return resultStr;
			}
			
			// 更新为已经验证
			afSmsRecordService.updateSmsIsCheck(smsDo.getRid());

			String salt = UserUtil.getSalt();
			String password = UserUtil.getPassword(passwordSrc, salt);

			AfUserDo userDo = new AfUserDo();
			userDo.setSalt(salt);
			userDo.setUserName(mobile);
			userDo.setMobile(mobile);
			userDo.setNick("");
			userDo.setPassword(password);
			userDo.setRegisterChannelId(pcp.getChannelId());
			userDo.setRegisterChannelPointId(pcp.getId());
			userDo.setRecommendId(0l);
			afUserService.addUser(userDo);

			Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
			String inviteCode = Long.toString(invteLong, 36);
			userDo.setRecommendCode(inviteCode);
			afUserService.updateUser(userDo);
			
			// 获取下载app地址
			AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
			String appDownLoadUrl = "";
			if (resourceCodeDo != null) {
				appDownLoadUrl = resourceCodeDo.getValue();
			}
			afPromotionChannelPointService.addRegister(pcp.getId());
			resultStr = H5CommonResponse.getNewInstance(true, "成功", appDownLoadUrl, null).toString();
			// 注册成功,发送注册短信
//			smsUtil.sendRegisterSuccessSms(userDo.getUserName());
			return resultStr;

		} catch (Exception e) {
			logger.error("commitChannelRegister",e);
			resultStr = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
			return resultStr;
		}finally{
			doLog(request, resultStr,"", Calendar.getInstance().getTimeInMillis()-calStart.getTimeInMillis(),request.getParameter("registerMobile"));
		}

	}
	

}
