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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.CouponWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.MoXieResCodeType;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月8日下午8:36:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5FanBeiWebController extends BaseController {
    String  opennative = "/fanbei-web/opennative?name=";

	@Resource
	AfUserDao afUserDao;

	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserCouponDao afUserCouponDao;
	@Resource
	AfResourceDao afResourceDao;
	@Resource
    TokenCacheUtil tokenCacheUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfShopService afShopService;
	@Resource
	private AfUserAuthService afUserAuthService;
	
	/**
	 * 首页弹窗页面
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = { "homepagePop" }, method = RequestMethod.GET)
	public void homepagePop(HttpServletRequest request, ModelMap model) throws IOException {
		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(Constants.RES_APP_POP_IMAGE);
		model.put("redirectUrl", resourceDo.getName());
		doMaidianLog(request,"");
	}

	@RequestMapping(value = { "receiveCoupons" }, method = RequestMethod.GET)
	public void receiveCoupons(HttpServletRequest request, ModelMap model) throws IOException {
		doMaidianLog(request,"");
		
		AfResourceDo resourceDo = afResourceDao.getSingleResourceBytype(AfResourceType.PickedCoupon.getCode());
		String appInfotext = ObjectUtils.toString(request.getParameter("_appInfo"), "").toString();
		JSONObject appInfo = JSON.parseObject(appInfotext);
		String userName = ObjectUtils.toString(appInfo.get("userName"), "");

		AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
		Long userId= -1L;
		if(afUserDo!=null){
			
			userId = afUserDo.getRid();
		}
		String ids = resourceDo.getValue();
		List<AfCouponDto> afCouponList = afCouponService.selectCouponByCouponIds(ids,userId);
		List<Object> list = new ArrayList<Object>();
		for (AfCouponDto afCouponDto : afCouponList) {
			list.add(couponObjectWithAfUserCouponDto(afCouponDto));
		}
	
		model.put("couponList", list);
		model.put("userName", userName);
		logger.info(JSON.toJSONString(model));
	}

	public Map<String, Object> couponObjectWithAfUserCouponDto(AfCouponDto afCouponDo) {

		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("rid", afCouponDo.getRid());
		returnData.put("useRule", afCouponDo.getUseRule());
		returnData.put("limitAmount", afCouponDo.getLimitAmount());
		returnData.put("name", afCouponDo.getName());
		returnData.put("gmtStart", afCouponDo.getGmtStart());
		returnData.put("gmtEnd", afCouponDo.getGmtEnd());
		returnData.put("amount", afCouponDo.getAmount());
		returnData.put("limitCount", afCouponDo.getLimitCount());
//		优惠券类型【MOBILE：话费充值， REPAYMENT：还款, FULLVOUCHER:满减卷,CASH:现金奖励】
		if (StringUtil.equals("MOBILE", afCouponDo.getType())) {
			returnData.put("type", "话费劵");
			
		}else if (StringUtil.equals("REPAYMENT", afCouponDo.getType())) {
			returnData.put("type", "还款劵");
			
		}else if (StringUtil.equals("FULLVOUCHER", afCouponDo.getType())) {
			returnData.put("type", "满减劵");
		}else if (StringUtil.equals("CASH", afCouponDo.getType())){
			returnData.put("type", "现金劵");
		}else{
			returnData.put("type", "会场劵");
		}

		returnData.put("quota", afCouponDo.getQuota());
		returnData.put("quotaAlready", afCouponDo.getQuotaAlready());
		returnData.put("userAlready", afCouponDo.getUserAlready());
		
		
		if(DateUtil.afterDay(DateUtil.addDays(new Date(), 2), afCouponDo.getGmtEnd())){
			returnData.put("status", "Y");
		}else{
			returnData.put("status", "N");
		}
		
	

		return returnData;

	}

	@ResponseBody
	@RequestMapping(value = "/pickCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickCoupon(HttpServletRequest request, ModelMap model) throws IOException {
		
		doMaidianLog(request,"");
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			
			context = doWebCheck(request, false);
			String couponId = ObjectUtils.toString(request.getParameter("couponId"), "").toString();
			AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
			Map<String, Object> returnData = new HashMap<String, Object>();

			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.AppLogin.getCode();
				returnData.put("status", CouponWebFailStatus.UserNotexist.getCode());
				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(), notifyUrl,returnData )
						.toString();
			}

			AfCouponDo couponDo = afCouponService.getCouponById(NumberUtil.objToLongDefault(couponId, 1l));

			if (couponDo == null) {
				returnData.put("status", CouponWebFailStatus.CouponNotExist.getCode());

				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR.getDesc(), "", returnData)
						.toString();
			}

			Integer limitCount = couponDo.getLimitCount();

			Integer myCount = afUserCouponDao.getUserCouponByUserIdAndCouponId(afUserDo.getRid(),
					NumberUtil.objToLongDefault(couponId, 1l));
			if (limitCount <= myCount) {
				returnData.put("status", CouponWebFailStatus.CouponOver.getCode());

				return H5CommonResponse.getNewInstance(false,
						FanbeiExceptionCode.USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR.getDesc(), "", returnData).toString();
			}
			Long totalCount = couponDo.getQuota();
			if(totalCount!=0&&totalCount<=couponDo.getQuotaAlready()){
				returnData.put("status", CouponWebFailStatus.MoreThanCoupon.getCode());

				return H5CommonResponse.getNewInstance(false,
						FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR.getDesc(), "", returnData).toString();
			}

			AfUserCouponDo userCoupon = new AfUserCouponDo();
			userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
			userCoupon.setGmtStart(new Date());
			if(StringUtils.equals(couponDo.getExpiryType(), "R")   ){
				userCoupon.setGmtStart(couponDo.getGmtStart());
				userCoupon.setGmtEnd(couponDo.getGmtEnd());
				if(DateUtil.afterDay(new Date(), couponDo.getGmtEnd())){
					userCoupon.setStatus(CouponStatus.EXPIRE.getCode());
				}

			}else{
				userCoupon.setGmtStart(new Date());
				if(couponDo.getValidDays()==-1){
					userCoupon.setGmtEnd(DateUtil.getFinalDate());
				}else{
					userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
				}
			}
			
			
			
			userCoupon.setSourceType(CouponSenceRuleType.PICK.getCode());
			userCoupon.setStatus(CouponStatus.NOUSE.getCode());
			userCoupon.setUserId(afUserDo.getRid());
			afUserCouponDao.addUserCoupon(userCoupon);
			AfCouponDo couponDoT = new AfCouponDo();
			couponDoT.setRid(couponDo.getRid());
			couponDoT.setQuotaAlready(1);
			afCouponService.updateCouponquotaAlreadyById(couponDoT);
			logger.info("pick coupon success",couponDoT);
			return H5CommonResponse.getNewInstance(true, "领券成功", "", null).toString();

		} catch (Exception e) {
			logger.error("pick coupon error",e);
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}

	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "/pickBoluomeCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickBoluomeCoupon(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}",sceneId, userName);
			if (sceneId == null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc()).toString();
			}
			
			if (StringUtils.isEmpty(userName)) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse
						.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl,null )
						.toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse
						.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl,null )
						.toString();
			}
			
			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
			}
			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(afUserDo.getRid()+StringUtil.EMPTY);
			
			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);
			
			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc()).toString();
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc()).toString();
			}
			
			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			if (!"0".equals(resultJson.getString("code"))) {
				return H5CommonResponse.getNewInstance(false, resultJson.getString("msg")).toString();
			} else if (JSONArray.parseArray(resultJson.getString("data")).size() == 0){
				return H5CommonResponse.getNewInstance(false, "仅限领取一次，请勿重复领取！", null, null).toString();
			}
			return H5CommonResponse.getNewInstance(true, "领券成功", "", null).toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}
	
	/**
	 * 获取菠萝觅跳转地址
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@ResponseBody
	@RequestMapping(value = "/getBrandUrl", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getBrandUrl(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long shopId = NumberUtil.objToLongDefault(request.getParameter("shopId"), null);
			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
			Map<String, String> buildParams = new HashMap<String, String>();
			if (shopId == null) {
				logger.error("shopId is empty");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null).toString();
			}
			
			AfShopDo shopInfo = afShopService.getShopById(shopId);
			if (shopInfo ==  null) {
				logger.error("shopId is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null).toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (StringUtils.isEmpty(userName) || afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse
						.getNewInstance(false, "登陆之后才能进行查看", notifyUrl,null )
						.toString();
			}
			String shopUrl = shopInfo.getShopUrl() + "?";
			
			buildParams.put(BoluomeCore.CUSTOMER_USER_ID, afUserDo.getRid() + StringUtil.EMPTY);
			buildParams.put(BoluomeCore.CUSTOMER_USER_PHONE, afUserDo.getMobile());
			buildParams.put(BoluomeCore.TIME_STAMP, System.currentTimeMillis() + StringUtil.EMPTY);
			
			String sign =  BoluomeCore.buildSignStr(buildParams);
			buildParams.put(BoluomeCore.SIGN, sign);
			String paramsStr = BoluomeCore.createLinkString(buildParams);
			
			return H5CommonResponse.getNewInstance(true, "成功", shopUrl + paramsStr, null).toString();

		} catch (Exception e) {
			logger.error("getBrandUrl , e = {}", e.getMessage());
			return H5CommonResponse.getNewInstance(false, "操作失败", "", null).toString();
		}

	}
	

	
	/**
	 * 获取菠萝觅跳转地址
	 * @param request
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/app/mobileOperator", method = RequestMethod.GET)
	public void mobileOperator(HttpServletRequest request, ModelMap model) throws IOException {
		Boolean processResult = true;
		try {
			String appInfo = request.getParameter("_appInfo");
			Long mobileReqTimeStamp = NumberUtil.objToLongDefault(request.getParameter("mobileReqTimeStamp"),0L);
			Date reqTime = new Date(mobileReqTimeStamp);
			
			String mxcode = request.getParameter("mxcode");
			String userName =  StringUtil.null2Str(JSON.parseObject(appInfo).get("userName"));
			AfUserDo  afUserDo = afUserDao.getUserByUserName(userName);
			
			AfUserAuthDo authDo = new AfUserAuthDo();
			authDo.setUserId(afUserDo.getRid());
			//此字段保存该笔认证申请的发起时间，更新时做校验，防止在更新时，风控对这笔认证已经回调处理成功，造成错误更新
			authDo.setGmtMobile(reqTime);
			
			if(MoXieResCodeType.ONE.getCode().equals(mxcode) || MoXieResCodeType.TWO.getCode().equals(mxcode) ){
				//用户认证处理中
				authDo.setMobileStatus(MobileStatus.WAIT.getCode());
				int updateRowNums = afUserAuthService.updateUserAuthMobileStatusWait(authDo);
				if(updateRowNums==0){
					logger.info("mobileOperator updateUserAuthMobileStatusWait fail, risk happen before.desStatus="+MobileStatus.WAIT.getCode()+"userId="+afUserDo.getRid());
				}
			}else if(MoXieResCodeType.FIFTY.getCode().equals(mxcode)){
				//三方不经过强风控，直接通过backUrl返回api告知用户认证失败
				authDo.setMobileStatus(MobileStatus.NO.getCode());
				int updateRowNums = afUserAuthService.updateUserAuthMobileStatusWait(authDo);
				if(updateRowNums==0){
					logger.info("mobileOperator updateUserAuthMobileStatusWait fail, risk happen before.desStatus="+MobileStatus.NO.getCode()+"userId="+afUserDo.getRid());
				}
				processResult = false;
			}else {
				processResult = false;
			}
			model.put("processResult", processResult);
		} catch (Exception e) {
			logger.error("mobileOperator , e = {}", e.getMessage());
			processResult = false;
			model.put("processResult", processResult);
		}finally{
			doMaidianLog(request, processResult+"");
		}

	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ald.fanbei.api.web.common.BaseController#checkCommonParam(java.lang.
	 * String, javax.servlet.http.HttpServletRequest, boolean)
	 */
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
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ald.fanbei.api.web.common.BaseController#doProcess(com.ald.fanbei.api
	 * .web.common.RequestDataVo, com.ald.fanbei.api.common.FanbeiContext,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	

}