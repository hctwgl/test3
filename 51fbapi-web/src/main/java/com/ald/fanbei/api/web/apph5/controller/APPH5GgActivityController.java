package com.ald.fanbei.api.web.apph5.controller;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5GgActivity;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBoluomeUserCouponVo;
import com.ald.fanbei.api.web.vo.userReturnBoluomeCouponVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: APPH5GgActivityController
 * @Description: 吃玩住行活动
 * @author chenqiwei
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年11月14日 下午2:07:40
 *
 */
@RestController
@RequestMapping(value = "/h5GgActivity", produces = "application/json;charset=UTF-8")
public class APPH5GgActivityController extends BaseController {
    @Resource
    AfUserService afUserService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    TongdunUtil tongdunUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource 
    AfBoluomeUserCouponService afBoluomeUserCouponService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfShopService afShopService;
    @Resource
    BoluomeUtil boluomeUtil;
    
    
    
    @RequestMapping(value = "/commitRegisterLogin", method = RequestMethod.POST)
    public String bouomeActivityRegisterLogin(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
   	String resultStr = "";
   	String referer = request.getHeader("referer");  
   	doMaidianLog(request, H5CommonResponse.getNewInstance(true, "calling"),referer,"calling h5GgActivity commitRegisterLogin");
   	try {
   	    String moblie = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
   	    String inviteer = ObjectUtils.toString(request.getParameter("inviteer"), "").toString();
   	    String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
   	    String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
   	    String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();
   	    String token = ObjectUtils.toString(request.getParameter("token"), "").toString();

   	    AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
   	    logger.info("h5GgActivity commitRegisterLogin eUserDo",eUserDo,moblie); 
   	    if (eUserDo != null) {
   		logger.error("h5GgActivity commitRegisterLogin user regist account exist",moblie);
   		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "Register", null).toString();

   	    }
   	    AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(moblie, SmsType.REGIST.getCode());
   	    if (smsDo == null) {
   		logger.error("h5GgActivity commitRegisterLogin sms record is empty",moblie);
   		resultStr = H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "Register", null).toString();
   		return resultStr;
   	    }

   	    String realCode = smsDo.getVerifyCode();
   	    if (!StringUtils.equals(verifyCode, realCode)) {
   		logger.error("h5GgActivity commitRegisterLogin verifyCode is invalid",moblie);
   		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "Register", null).toString();
   		return resultStr;
   	    }
   	    if (smsDo.getIsCheck() == 1) {
   		logger.error("h5GgActivity commitRegisterLogin verifyCode is already invalid",moblie);
   		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "Register", null).toString();
   		return resultStr;
   	    }
   	    // 判断验证码是否过期
   	    if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
   		logger.error("h5GgActivity commitRegisterLogin user regist sms overdue",moblie);
   		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "Register", null).toString();
   		return resultStr;

   	    }
   	    try {
   		tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), moblie, moblie, "");
   	    } catch (Exception e) {
   		logger.error("h5GgActivity commitRegisterLogin tongtun fengkong error",moblie);
   		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "Register", null).toString();
   		return resultStr;
   	    }

   	    // 更新为已经验证
   	    afSmsRecordService.updateSmsIsCheck(smsDo.getRid());

   	    String salt = UserUtil.getSalt();
   	    String password = UserUtil.getPassword(passwordSrc, salt);

   	    AfUserDo userDo = new AfUserDo();
   	    userDo.setSalt(salt);
   	    userDo.setUserName(moblie);
   	    userDo.setMobile(moblie);
   	    userDo.setNick("");
   	    userDo.setPassword(password);
   	    userDo.setRecommendId(0l);
   	    //邀请码
   	    if (!StringUtils.isBlank(recommendCode)) {
   		AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
   		userDo.setRecommendId(userRecommendDo.getRid());
   	    }
   	    logger.info("h5GgActivity commitRegisterLogin userDo",userDo,moblie);
   	    int result = afUserService.addUser(userDo);
   	    logger.info("h5GgActivity commitRegisterLogin result",result,moblie);
   	    Long invteLong = Constants.INVITE_START_VALUE + userDo.getRid();
   	    String inviteCode = Long.toString(invteLong, 36);
   	    userDo.setRecommendCode(inviteCode);
   	    afUserService.updateUser(userDo);
   	    // 获取邀请分享地址
   	    String appDownLoadUrl = "";
//   	    AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
//   	    if (resourceCodeDo != null) {
//   		appDownLoadUrl = resourceCodeDo.getValue();
//   	    }
   	    resultStr = H5CommonResponse.getNewInstance(true, "注册成功", appDownLoadUrl, null).toString();
   	    doMaidianLog(request, H5CommonResponse.getNewInstance(true, "注册成功"),"/h5GgActivity/commitRegisterLogin success",moblie);
   	    // save token to cache
            String  token1 = UserUtil.generateToken(moblie);
   	    String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + moblie;
   	    CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, moblie, Constants.SECOND_OF_HALF_HOUR_INT);
   	    CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
   	    bizCacheUtil.saveObject(tokenKey, token1, Constants.SECOND_OF_HALF_HOUR);
   	  
   	    if(StringUtil.isNotEmpty(inviteer)){
   		AfBoluomeUserCouponDo  afBoluomeUserCouponDo = new AfBoluomeUserCouponDo();
   		afBoluomeUserCouponDo.setUserId(NumberUtil.objToLong(inviteer));
   		afBoluomeUserCouponDo.setRefId(NumberUtil.objToLong(moblie));
   		afBoluomeUserCouponDo.setGmtCreate(new Date());
   		afBoluomeUserCouponDo.setChannel(H5GgActivity.REGISTER.getCode());
   		int saveResult = afBoluomeUserCouponService.saveRecord(afBoluomeUserCouponDo);
   		if(saveResult == 1){
   		 logger.info("h5GgActivity commitRegisterLogin saveResult success",afBoluomeUserCouponDo);  
   		}else{
   		 logger.info("h5GgActivity commitRegisterLogin saveResult fial",afBoluomeUserCouponDo);  
   		}
   	    }
   	    
   	    return resultStr;

   	} catch (FanbeiException e) {
   	    logger.error("commitRegister fanbei exception" + e.getMessage());
   	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "Register", null).toString();
   	    return resultStr;
   	} catch (Exception e) {
   	    logger.error("commitRegister exception", e);
   	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "Register", null).toString();
   	    return resultStr;
   	} finally {

   	}

       }

    @RequestMapping(value = "/returnCoupon", method = RequestMethod.POST)
    public String returnCoupon(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
	//String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
	FanbeiWebContext context = new FanbeiWebContext();
	String resultStr = "";
	try{
	    	 context = doWebCheck(request, false);
	    	 Long userId = convertUserNameToUserId(context.getUserName());
	 	 List<userReturnBoluomeCouponVo> returnCouponList = new ArrayList<userReturnBoluomeCouponVo>();
		 AfBoluomeUserCouponVo vo = new AfBoluomeUserCouponVo(); 
	        //未登录时初始化一些数据
		 AfShopDo shopDo = new AfShopDo();
	    	       shopDo.setType(H5GgActivity.WAIMAI.getCode());
	    	       AfShopDo shop = afShopService.getShopInfoBySecTypeOpen(shopDo);
	    	       if(shop != null){
	    	 	vo.setShopId(shop.getRid());
	    	       }
	    	 BigDecimal couponAmount = new BigDecimal(String.valueOf(0));
	    	 BigDecimal inviteAmount = new BigDecimal(String.valueOf(0));
	    	 vo.setInviteAmount(inviteAmount);
	    	 vo.setCouponAmount(couponAmount);
	 	 vo.setReturnCouponList(returnCouponList);
	    	 if(userId == null){
	    	     return H5CommonResponse.getNewInstance(true, "获取返券列表成功",null,vo).toString();
	    	 }
	    
	    	 //登录时返回数据
    	    	 AfBoluomeUserCouponDo  queryUserCoupon = new AfBoluomeUserCouponDo();
    	    	 queryUserCoupon.setUserId(userId);
    	    	 queryUserCoupon.setChannel(H5GgActivity.RECOMMEND.getCode());
    	    	
    	    	 List<AfBoluomeUserCouponDo> userCouponList =  afBoluomeUserCouponService.getUserCouponListByUerIdAndChannel(queryUserCoupon);
    	    	 for(AfBoluomeUserCouponDo userCoupon:userCouponList){
    	    	     	   long couponId =  userCoupon.getCouponId();
    	    	     	   
        	    	   userReturnBoluomeCouponVo returnCouponVo = new userReturnBoluomeCouponVo();
        	    	   returnCouponVo.setRegisterTime(DateUtil.formatDateForPatternWithHyhen(userCoupon.getGmtCreate()));
        	    	   //被邀请人没有订单：未消费; 有订单且订单没有一个是完成的(或者有订单且用户优惠券记录的优惠券id等于0)：未完成。 有完成的订单(或者有订单且用户优惠券记录的优惠券id大于0)：
        	    	   long refUserId =   userCoupon.getRefId();
        	    	   AfOrderDo order  = new AfOrderDo();
        	    	   order.setUserId(refUserId);
        	    	   int queryCount =   afOrderService.getOrderCountByStatusAndUserId(order);
        	    	   if(queryCount<=0){
        	    	       returnCouponVo.setStatus("未消费");
        	    	   }
        	    	   if(queryCount >0){
        	    	       if(couponId<=0){
        	    		returnCouponVo.setStatus("未完成");
        	    	       }else{
        	    		returnCouponVo.setStatus("已完成");
        	    	       }
        	    	   }
        	    	 if(couponId<=0){
        	    	     returnCouponVo.setReward("0");
        	    	 }else{
        	    	     AfResourceDo rDo =  afResourceService.getResourceByResourceId(couponId);
        	    	     if(rDo != null){
        	    		returnCouponVo.setReward(rDo.getName());
        	    	     }
        	    	  //通过af_resoource 获取url，再调用菠萝觅接口,获取对应金额
      	    	     	   try{
      	    	     	       	AfResourceDo afResourceDo = afResourceService.getResourceByResourceId(couponId);
  				if(afResourceDo != null){
  				List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil.getActivityCouponList(afResourceDo.getValue());
  				BrandActivityCouponResponseBo bo = activityCouponList.get(0);
          			BigDecimal money = new BigDecimal(String.valueOf(bo.getValue()));
          			couponAmount = couponAmount.add(money);
  				}
      	    	     	   }catch(Exception e){
      	    	     	       logger.error("get coluome activity inviteAmount error", e.getStackTrace());
      	    	     	   }
        	    	 }
        	    	returnCouponList.add(returnCouponVo);
        	     } 
    	    	 
    	    	       //获取外卖商城id
    	    	  
    	    	       //好友借钱邀请者得到的奖励总和  inviteAmount
    	    	       
    	    	 	vo.setReturnCouponList(returnCouponList);
    	    	 	vo.setInviteAmount(inviteAmount);
    	    	        vo.setCouponAmount(couponAmount);
    	    	        resultStr =  H5CommonResponse.getNewInstance(true, "获取返券列表成功",null,vo).toString();
	 	}catch(Exception e){
	 	 logger.error("/h5GgActivity/returnCoupon" + context + "error = {}", e.getStackTrace());
		 resultStr = H5CommonResponse.getNewInstance(false, "获取返券列表失败").toString();
	   }
	return resultStr;
	
    }
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 *         Long @throws
	 */
	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;
	}
	

}
