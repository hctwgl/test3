/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
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
import com.ald.fanbei.api.common.util.StringUtil;
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
		logger.info(JSON.toJSONString(model));
	}

	@RequestMapping(value = { "register" }, method = RequestMethod.GET)
	public void register(HttpServletRequest request, ModelMap model) throws IOException {
		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(AfResourceType.RegisterProtocol.getCode());
		String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + resourceDo.getValue();

		model.put("registerRule", notifyUrl);
		logger.info(JSON.toJSONString(model));
	}

	@ResponseBody
	@RequestMapping(value = "getRegisterSmsCode", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getRegisterSmsCode(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			String mobile = ObjectUtils.toString(request.getParameter("mobile"), "").toString();

			AfUserDo afUserDo = afUserService.getUserByUserName(mobile);

			if (afUserDo != null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_HAS_REGIST_ERROR.getDesc(), "", null).toString();
			}
			boolean resultReg = smsUtil.sendRegistVerifyCode(mobile);
			if (!resultReg) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_SEND_SMS_ERROR.getDesc(), "", null).toString();
			}

			return H5CommonResponse.getNewInstance(true, "成功", "", null).toString();

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}

	}

	@ResponseBody
	@RequestMapping(value = "commitRegister", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String commitRegister(HttpServletRequest request, ModelMap model) throws IOException {
		
		String reqData = "";
		
		try {
			String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
			String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
			String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
			String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();
			reqData = StringUtil.appendStrs("web commitRegister" + mobile , ",", verifyCode,",",passwordSrc,",",recommendCode);

			AfUserDo eUserDo = afUserService.getUserByUserName(mobile);
			if (eUserDo != null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null).toString();

			}
			AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
			if (smsDo == null) {
				logger.error("sms record is empty");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
			}

			String realCode = smsDo.getVerifyCode();
			if (!StringUtils.equals(verifyCode, realCode)) {
				logger.error("verifyCode is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
			}
			if (smsDo.getIsCheck() == 1) {
				logger.error("verifyCode is already invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
			}
			// 判断验证码是否过期
			if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();

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
			// TODO 优化邀请码规则
			String inviteCode = Long.toString(invteLong, 36);
			userDo.setRecommendCode(inviteCode);
			afUserService.updateUser(userDo);

			// 获取邀请分享地址
			AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
			String appDownLoadUrl = "";
			if (resourceCodeDo != null) {
				appDownLoadUrl = resourceCodeDo.getValue();
			}
			return H5CommonResponse.getNewInstance(true, "成功", appDownLoadUrl, null).toString();

		}catch(FanbeiException e){
			logger.error("commitRegister fanbei exception"+e.getMessage());
			return H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
		} catch (Exception e) {
			logger.error("commitRegister exception",e);
			return H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
		}finally{
			logger.info(reqData);
		}

	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = { "channelRegister" }, method = RequestMethod.GET)
	public void channelRegister(HttpServletRequest request, ModelMap model) throws IOException {
		String channelCode = ObjectUtils.toString(request.getParameter("channelCode"), "").toString();
		String pointCode = ObjectUtils.toString(request.getParameter("pointCode"), "").toString();
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

	}

	@ResponseBody
	@RequestMapping(value = "commitChannelRegister", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String commitChannelRegister(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
			String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
			String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
			String channelCode = ObjectUtils.toString(request.getParameter("channelCode"), "").toString();
			String pointCode = ObjectUtils.toString(request.getParameter("pointCode"), "").toString();

			
			AfPromotionChannelPointDo pcp = afPromotionChannelPointService.getPoint(channelCode, pointCode);
			if (pcp == null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_CHANNEL_NOTEXIST.getDesc(), "", null).toString();
			}

			AfUserDo eUserDo = afUserService.getUserByUserName(mobile);
			if (eUserDo != null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null).toString();

			}
			AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
			if (smsDo == null) {
				logger.error("sms record is empty");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
			}

			String realCode = smsDo.getVerifyCode();
			if (!StringUtils.equals(verifyCode, realCode)) {
				logger.error("verifyCode is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
			}
			if (smsDo.getIsCheck() == 1) {
				logger.error("verifyCode is already invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
			}
			// 判断验证码是否过期
			if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();

			}
			try {
				tongdunUtil.getPromotionResult(request.getSession().getId(),channelCode,pointCode,CommonUtil.getIpAddr(request),mobile, mobile, "");
			} catch (Exception e) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
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
			// TODO 优化邀请码规则
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
			return H5CommonResponse.getNewInstance(true, "成功", appDownLoadUrl, null).toString();

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}

	}
	

}
