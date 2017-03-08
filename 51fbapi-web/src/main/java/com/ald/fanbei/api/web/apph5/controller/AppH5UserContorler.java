/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
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
@RequestMapping("/ala-web/user/")
public class AppH5UserContorler extends BaseController {

	// @Resource
	// AfUserAccountDao afUserAccountDao;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserDao afUserDao;

	@Resource
	AfCouponDao afCouponDao;
	
	@Resource
	AfUserCouponDao afUserCouponDao;

	@Resource
	AfResourceDao afResourceDao;
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfSmsRecordService afSmsRecordService;

	@RequestMapping(value = { "receiveCoupons" }, method = RequestMethod.GET)
	public void receiveCoupons(HttpServletRequest request, ModelMap model) throws IOException {
		AfResourceDo resourceDo = afResourceDao.getSingleResourceBytype(AfResourceType.PickedCoupon.getCode());

		String ids = resourceDo.getValue();
		List<AfCouponDo> afCouponList = afCouponDao.selectCouponByCouponIds(ids);
		List<Object> list = new ArrayList<Object>();
		for (AfCouponDo afCouponDo : afCouponList) {
			list.add(couponObjectWithAfUserCouponDto(afCouponDo));
		}
		model.put("notifyUrl", "http://192.168.96.35:8088/app/user/ala-web/pickCoupon");
		model.put("couponList", list);
		logger.info(JSON.toJSONString(model));
	}
	public Map<String, Object> couponObjectWithAfUserCouponDto(AfCouponDo afCouponDo){
		
		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("rid", afCouponDo.getRid());
		returnData.put("useRule", afCouponDo.getUseRule());
		returnData.put("limitAmount", afCouponDo.getLimitAmount());
		returnData.put("name", afCouponDo.getName());
		returnData.put("gmtStart", afCouponDo.getGmtStart());
		returnData.put("gmtEnd", afCouponDo.getGmtEnd());
		returnData.put("amount", afCouponDo.getAmount());
		returnData.put("limitCount", afCouponDo.getLimitCount());
		returnData.put("type", afCouponDo.getType());

		return returnData;

	}

	@RequestMapping(value = { "invitationGift" }, method = RequestMethod.GET)
	public void invitationGift(HttpServletRequest request, ModelMap model) throws IOException {

		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
		model.put("avatar", afUserDo.getAvatar());
		model.put("userName", afUserDo.getUserName());
		model.put("recommendCode", afUserDo.getRecommendCode());
		model.put("mobile", afUserDo.getMobile());
		logger.info(JSON.toJSONString(model));
	}

	@RequestMapping(value = { "register" }, method = RequestMethod.GET)
	public void register(HttpServletRequest request, ModelMap model) throws IOException {
		AfResourceDo resourceDo = afResourceDao.getSingleResourceBytype(AfResourceType.RegisterProtocol.getCode());
		model.put("registerRule", resourceDo.getValue());
		logger.info(JSON.toJSONString(model));
	}
    @ResponseBody
	@RequestMapping(value = "getRegisterSmsCode", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getRegisterSmsCode(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			String mobile = ObjectUtils.toString(request.getParameter("mobile"), "").toString();

			AfUserDo afUserDo = afUserDao.getUserByUserName(mobile);

			if (afUserDo != null) {
				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_HAS_REGIST_ERROR.getDesc(), "", null)
						.toString();
			}
			boolean resultReg = smsUtil.sendRegistVerifyCode(mobile);
			if (!resultReg) {
				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_SEND_SMS_ERROR.getDesc(), "", null).toString();
			}

			return H5CommonResponse.getNewInstance(true, "成功", "", null).toString();

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}

	}
    @ResponseBody
   	@RequestMapping(value = "/pickCoupon", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
   	public String pickCoupon(HttpServletRequest request, ModelMap model) throws IOException {
   		try {
   			String couponId = ObjectUtils.toString(request.getParameter("couponId"), "").toString();
   			
   			
   			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
   			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);

   			if (afUserDo == null) {
   				return H5CommonResponse
   						.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(), "", null)
   						.toString();
   			}

   			AfCouponDo couponDo = afCouponDao.getCouponById(NumberUtil.objToLongDefault(couponId, 1l));
   			
   			
   			if (couponDo == null) {
   				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR.getDesc(), "", null)
   						.toString();
   			}
   			
   			Integer limitCount = couponDo.getLimitCount();
   			
   			Integer myCount = afUserCouponDao.getUserCouponByUserIdAndCouponId(afUserDo.getRid(), NumberUtil.objToLongDefault(couponId, 1l));
   			if(limitCount<=myCount){
   				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR.getDesc(), "", null)
   						.toString();
   			}
   		
   			
   			AfUserCouponDo userCoupon = new AfUserCouponDo();
			userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
			userCoupon.setGmtStart(new Date());
			if(couponDo.getValidDays()==-1){
				userCoupon.setGmtEnd(DateUtil.getFinalDate());
			}else{
				userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
			}
			userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
			userCoupon.setSourceType(CouponSenceRuleType.PICK.getCode());
			userCoupon.setStatus(CouponStatus.NOUSE.getCode());
			userCoupon.setUserId(afUserDo.getRid());
			afUserCouponDao.addUserCoupon(userCoupon);
   			
   			return H5CommonResponse.getNewInstance(true, "成功", "", null).toString();

   		} catch (Exception e) {
   			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
   		}

   	}
    
    
    @ResponseBody
	@RequestMapping(value = "commitRegister", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String commitRegister(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			String mobile = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
			String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
			String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
			String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();

			AfUserDo eUserDo = afUserDao.getUserByUserName(mobile);
			if (eUserDo != null) {
				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null)
						.toString();

			}
			AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(mobile, SmsType.REGIST.getCode());
			if (smsDo == null) {
				logger.error("sms record is empty");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
			}
	      
			String realCode = smsDo.getVerifyCode();
			if (!StringUtils.equals(verifyCode, realCode)) {
				logger.error("verifyCode is invalid");
				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null)
						.toString();
			}
			if(smsDo.getIsCheck() == 1){
				logger.error("verifyCode is already invalid");
				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null)
						.toString();
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

			afUserDao.addUser(userDo);

			Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
			// TODO 优化邀请码规则
			String inviteCode = Long.toString(invteLong, 36);
			userDo.setRecommendCode(inviteCode);
			if (!StringUtils.isBlank(recommendCode)) {
				AfUserDo userRecommendDo = afUserDao.getUserByRecommendCode(recommendCode);
				userDo.setRecommendId(userRecommendDo.getRid());
				;
			}
			afUserDao.updateUser(userDo);

			AfUserAccountDo account = new AfUserAccountDo();
			account.setUserId(userDo.getRid());
			account.setUserName(userDo.getUserName());
			afUserAccountService.addUserAccount(account);

			return H5CommonResponse.getNewInstance(true, "成功", "", null).toString();

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
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

}
