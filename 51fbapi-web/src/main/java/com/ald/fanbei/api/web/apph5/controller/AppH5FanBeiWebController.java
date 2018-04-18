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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderLogisticsService;
import com.ald.fanbei.api.biz.service.AfBusinessAccessRecordsService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfLoanSupermarketService;
import com.ald.fanbei.api.biz.service.AfOrderLogisticsService;
import com.ald.fanbei.api.biz.service.AfPopupsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.CouponWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.MoXieResCodeType;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.ThirdPartyLinkType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.dal.domain.AfPopupsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
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
	String opennative = "/fanbei-web/opennative?name=";

	@Resource
	AfUserDao afUserDao;
	@Resource
	AfPopupsService afPopupsService;
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
	@Resource
	AfLoanSupermarketService afLoanSupermarketService;
	@Resource
	AfBusinessAccessRecordsService afBusinessAccessRecordsService;
	@Resource
	AfOrderLogisticsService afOrderLogisticsService;
	@Resource
	BizCacheUtil bizCacheUtil;

	@Resource
	AfBorrowLegalOrderLogisticsService afBorrowLegalOrderLogisticsService;

	/**
	 * 首页弹窗页面
	 * 
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = { "homepagePop" }, method = RequestMethod.GET)
	public void homepagePop(HttpServletRequest request, ModelMap model) throws IOException {
		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(Constants.RES_APP_POP_IMAGE);
		model.put("redirectUrl", resourceDo.getValue2());
		doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
	}

	@RequestMapping(value = { "receiveCoupons" }, method = RequestMethod.GET)
	public void receiveCoupons(HttpServletRequest request, ModelMap model) throws IOException {
		doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));

		AfResourceDo resourceDo = afResourceDao.getSingleResourceBytype(AfResourceType.PickedCoupon.getCode());
		String appInfotext = ObjectUtils.toString(request.getParameter("_appInfo"), "").toString();
		JSONObject appInfo = JSON.parseObject(appInfotext);
		String userName = ObjectUtils.toString(appInfo.get("userName"), "");

		AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
		Long userId = -1L;
		if (afUserDo != null) {

			userId = afUserDo.getRid();
		}
		String ids = resourceDo.getValue();
		List<AfCouponDto> afCouponList = afCouponService.selectCouponByCouponIds(ids, userId);
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
		// 优惠券类型【MOBILE：话费充值， REPAYMENT：还款, FULLVOUCHER:满减卷,CASH:现金奖励】
		if (StringUtil.equals("MOBILE", afCouponDo.getType())) {
			returnData.put("type", "话费劵");

		} else if (StringUtil.equals("REPAYMENT", afCouponDo.getType())) {
			returnData.put("type", "还款劵");

		} else if (StringUtil.equals("FULLVOUCHER", afCouponDo.getType())) {
			returnData.put("type", "满减劵");
		} else if (StringUtil.equals("CASH", afCouponDo.getType())) {
			returnData.put("type", "现金劵");
		} else {
			returnData.put("type", "会场劵");
		}

		returnData.put("quota", afCouponDo.getQuota());
		returnData.put("quotaAlready", afCouponDo.getQuotaAlready());
		returnData.put("userAlready", afCouponDo.getUserAlready());

		if (DateUtil.afterDay(DateUtil.addDays(new Date(), 2), afCouponDo.getGmtEnd())) {
			returnData.put("status", "Y");
		} else {
			returnData.put("status", "N");
		}

		return returnData;

	}

	@ResponseBody
	@RequestMapping(value = "/pickCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickCoupon(HttpServletRequest request, ModelMap model) throws IOException {

		doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			String couponId = ObjectUtils.toString(request.getParameter("couponId"), "").toString();
			AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
			Map<String, Object> returnData = new HashMap<String, Object>();

			if (StringUtils.isEmpty(couponId)) {
				throw new IllegalArgumentException("couponId can't be null");
			}
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				returnData.put("status", CouponWebFailStatus.UserNotexist.getCode());
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(),
						notifyUrl, returnData).toString();
			}
			AfCouponDo couponDo = afCouponService.getCouponById(NumberUtil.objToLongDefault(couponId, 1l));
			if (couponDo == null) {
				returnData.put("status", CouponWebFailStatus.CouponNotExist.getCode());

				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR.getDesc(),
						"", returnData).toString();
			}

			Integer limitCount = couponDo.getLimitCount();
			Integer myCount = afUserCouponDao.getUserCouponByUserIdAndCouponId(afUserDo.getRid(),
					NumberUtil.objToLongDefault(couponId, 1l));
			if (limitCount <= myCount) {
				returnData.put("status", CouponWebFailStatus.CouponOver.getCode());

				return H5CommonResponse.getNewInstance(false,
						FanbeiExceptionCode.USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR.getDesc(), "", returnData)
						.toString();
			}
			Long totalCount = couponDo.getQuota();
			if (totalCount != -1 && totalCount != 0 && totalCount <= couponDo.getQuotaAlready()) {
				returnData.put("status", CouponWebFailStatus.MoreThanCoupon.getCode());

				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR.getDesc(),
						"", returnData).toString();
			}

			AfUserCouponDo userCoupon = new AfUserCouponDo();
			userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
			userCoupon.setGmtStart(new Date());
			if (StringUtils.equals(couponDo.getExpiryType(), "R")) {
				userCoupon.setGmtStart(couponDo.getGmtStart());
				userCoupon.setGmtEnd(couponDo.getGmtEnd());
				if (DateUtil.afterDay(new Date(), couponDo.getGmtEnd())) {
					userCoupon.setStatus(CouponStatus.EXPIRE.getCode());
				}
			} else {
				userCoupon.setGmtStart(new Date());
				if (couponDo.getValidDays() == -1) {
					userCoupon.setGmtEnd(DateUtil.getFinalDate());
				} else {
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
			logger.info("pick coupon success", couponDoT);
			return H5CommonResponse.getNewInstance(true, "领券成功", "", null).toString();
		} catch (Exception e) {
			logger.error("pick coupon error", e);
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}
	}

	/**
	 * @author qiao
	 * @说明：逛逛活动点亮过程中的领券
	 * @param: @param
	 *             request
	 * @param: @param
	 *             model
	 * @param: @return
	 * @param: @throws
	 *             IOException
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/pickBoluomeCouponV1", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickBoluomeCouponV1(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
			FanbeiWebContext context = new FanbeiWebContext();
			context = doWebCheck(request, false);
			String userName = context.getUserName();
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}", sceneId, userName);
			if (sceneId == null) {
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc())
						.toString();
			}

			if (StringUtils.isEmpty(userName)) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "没有登录", notifyUrl, null).toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "没有登录", notifyUrl, null).toString();
			}

			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
			}
			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);

			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc())
						.toString();
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc())
						.toString();
			}

			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
					resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			String code = resultJson.getString("code");

			if ("10222".equals(code) || "10206".equals(code) || "11206".equals(code)) {
				return H5CommonResponse.getNewInstance(true, "您已领过优惠券，快去使用吧~").toString();
			} else if ("10305".equals(code)) {
				return H5CommonResponse.getNewInstance(true, "您下手慢了哦，优惠券已领完，下次再来吧").toString();
			} else if (!"0".equals(code)) {
				return H5CommonResponse.getNewInstance(true, resultJson.getString("msg")).toString();
			}
			// 存入缓存
			bizCacheUtil.saveObject("boluome:coupon:" + resourceInfo.getRid() + afUserDo.getUserName(), "Y",
					2 * Constants.SECOND_OF_ONE_MONTH);
			return H5CommonResponse.getNewInstance(true, "恭喜您领券成功").toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}

	/**
	 * @author qiao
	 * @说明：进去场景
	 * @param: @param
	 *             request
	 * @param: @param
	 *             model
	 * @param: @return
	 * @param: @throws
	 *             IOException
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/getBrandUrlV1", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getBrandUrlV1(HttpServletRequest request, ModelMap model) throws IOException {
		try {

			Long shopId = NumberUtil.objToLongDefault(request.getParameter("shopId"), null);
			FanbeiWebContext context = doWebCheck(request, true);

			if (context.isLogin()) {
				String userName = context.getUserName();
				if (shopId == null) {
					logger.error("shopId is empty");
					return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null)
							.toString();
				}

				AfShopDo shopInfo = afShopService.getShopById(shopId);
				if (shopInfo == null) {
					logger.error("shopId is invalid");
					return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null)
							.toString();
				}
				AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
				if (StringUtils.isEmpty(userName) || afUserDo == null) {
					String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
							+ H5OpenNativeType.AppLogin.getCode();
					return H5CommonResponse.getNewInstance(false, "登陆之后才能进行查看", notifyUrl, null).toString();
				}
				
				String shopUrl = afShopService.parseBoluomeUrl(shopInfo.getShopUrl(), shopInfo.getPlatformName(), shopInfo.getType(), afUserDo.getRid(), afUserDo.getMobile());
				logger.info("getBrandUrlV1"+shopUrl);
				return H5CommonResponse.getNewInstance(true, "成功", shopUrl , null).toString();
			} else {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆之后才能进行查看", notifyUrl, null).toString();
			}
		} catch (FanbeiException e) {
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
					+ H5OpenNativeType.AppLogin.getCode();
			return H5CommonResponse.getNewInstance(false, "登陆之后才能进行查看", notifyUrl, null).toString();
		} catch (Exception e) {
			logger.error("getBrandUrl , e = {}", e.getMessage());
			return H5CommonResponse.getNewInstance(false, "操作失败", "", null).toString();
		}

	}

	// 根据测试，线上环境区别地址
	private String parseBoluomeUrl(String baseUrl) {
		String type = baseUrl.substring(baseUrl.lastIndexOf("/") + 1, baseUrl.length());
		if ("didi".equals(type)) {
			type = "yongche/" + type;
		}
		return ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/" + type + "?";
	}

	@ResponseBody
	@RequestMapping(value = "/pickBoluomeCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickBoluomeCoupon(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}", sceneId, userName);
			if (sceneId == null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc())
						.toString();
			}

			if (StringUtils.isEmpty(userName)) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl, null).toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl, null).toString();
			}

			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
			}
			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);

			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc())
						.toString();
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc())
						.toString();
			}

			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
					resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			String code = resultJson.getString("code");
			// 10222代表已经一天只能领取一张
			if ("10222".equals(code)) {
				return H5CommonResponse.getNewInstance(false, "今日已领取，请明日再来！", null, null).toString();
			} else if (!"0".equals(code)) {
				return H5CommonResponse.getNewInstance(false, resultJson.getString("msg")).toString();
			}
			return H5CommonResponse.getNewInstance(true, "领券成功，有效期3天", "", null).toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}

	/**
	 * @author qiao @说明： 领券优惠券
	 * @param: @param
	 *             request
	 * @param: @param
	 *             model
	 * @param: @return
	 * @param: @throws
	 *             IOException
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/pickBoluomeCouponForWeb", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickBoluomeCouponForWeb(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}", sceneId, userName);
			if (sceneId == null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc())
						.toString();
			}

			if (StringUtils.isEmpty(userName)) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl, null).toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl, null).toString();
			}

			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
			}
			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);

			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc())
						.toString();
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc())
						.toString();
			}

			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
					resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			String code = resultJson.getString("code");
			// 10222代表已经一天只能领取一张
			if ("10222".equals(code)) {
				return H5CommonResponse.getNewInstance(false, "今日已领取，请明日再来！", null, null).toString();
			} else if (!"0".equals(code)) {
				return H5CommonResponse.getNewInstance(false, resultJson.getString("msg")).toString();
			}
			return H5CommonResponse.getNewInstance(true, "领券成功，有效期3天", "", null).toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}

	/**
	 * @author qiao @说明： 领券优惠券
	 * @param: @param
	 *             request
	 * @param: @param
	 *             model
	 * @param: @return
	 * @param: @throws
	 *             IOException
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/pickBoluomeCouponForApp", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickBoluomeCouponForApp(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}", sceneId, userName);
			if (sceneId == null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc())
						.toString();
			}

			if (StringUtils.isEmpty(userName)) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl, null).toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆后才能领取优惠券", notifyUrl, null).toString();
			}

			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
			}
			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);

			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc())
						.toString();
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc())
						.toString();
			}

			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
					resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			String code = resultJson.getString("code");
			// 10222代表已经一天只能领取一张
			if ("10222".equals(code)) {
				return H5CommonResponse.getNewInstance(false, "今日已领取，请明日再来！", null, null).toString();
			} else if (!"0".equals(code)) {
				return H5CommonResponse.getNewInstance(false, resultJson.getString("msg")).toString();
			}
			return H5CommonResponse.getNewInstance(true, "领券成功，有效期3天", "", null).toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}

	/*
	 * 一键领取红包
	 */
	@ResponseBody
	@RequestMapping(value = "/pickSysAndBoluomeCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickSysAndBoluomeCoupon(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			// 检查是否登录
			FanbeiWebContext context = doWebCheck(request, false);
			Map<String, Object> returnData = new HashMap<String, Object>();
			// 获取用户信息
			AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
			// 用户是否存在
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				returnData.put("status", CouponWebFailStatus.UserNotexist.getCode());
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(),
						notifyUrl, returnData).toString();
			}
			// 获取优惠券配置信息
			List<AfResourceDo> afResourceList = afResourceService
					.getConfigByTypes(AfResourceType.NewUserCouponGift.getCode());
			if (afResourceList == null || afResourceList.isEmpty()) {
				return H5CommonResponse.getNewInstance(false, "请配置新手礼包优惠券信息").toString();
			}
			AfResourceDo afResourceDo = afResourceList.get(0);
			String couponIdValue = afResourceDo.getValue();
			String[] couponIdAndTypes = couponIdValue.split(",");
			String couponNotExist = "";// 用户优惠券不存在
			String couponPickOver = "";// 优惠券已领取完
			String couponMoreThanLimitCount = "";// 优惠券个数超过最大领券个数
			String success = "";// 成功
			String pickBrandCouponNotStart = "";// 领取活动还未开始,敬请期待
			String pickBrandCouponDateEnd = "";// 活动已经结束,请期待下一次活动
			String haveAlreadyReceived = "";// 今日已领取，请明日再来
			String msg = "";// 原因：resultJson.getString("msg")
			String result = "";
			boolean flag = true;
			for (String couponIdAndType : couponIdAndTypes) {// 本地系统的红包领取
				String[] coupontInfos = couponIdAndType.split(":");
				if (coupontInfos.length == 1) {
					String couponId = coupontInfos[0];
					// 查询优惠券信息
					AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
					if (afCouponDo == null) {
						continue;
					}
					String name = afCouponDo.getName();
					// 优惠券是否存在
					AfCouponDo couponDo = afCouponService.getCouponById(NumberUtil.objToLongDefault(couponId, 1l));
					if (couponDo == null) {
						// 优惠券不存在
						if (StringUtils.isEmpty(couponNotExist)) {
							couponNotExist = name + "优惠券不存在;";
						} else {
							couponNotExist = name + "," + couponNotExist;
						}
						continue;
					}
					// 判断优惠券个数是否超过最大领券个数
					Integer limitCount = couponDo.getLimitCount();
					Integer myCount = afUserCouponDao.getUserCouponByUserIdAndCouponId(afUserDo.getRid(),
							NumberUtil.objToLongDefault(couponId, 1l));
					if (limitCount <= myCount) {
						// 优惠券个数超过最大领券个数
						if (StringUtils.isEmpty(couponMoreThanLimitCount)) {
							couponMoreThanLimitCount = name + "优惠券个数超过最大领券个数;";
						} else {
							couponMoreThanLimitCount = name + "," + couponMoreThanLimitCount;
						}
						continue;
					}
					// 判断优惠券是否已领取完
					Long totalCount = couponDo.getQuota();
					if (totalCount != -1 && totalCount != 0 && totalCount <= couponDo.getQuotaAlready()) {
						// 优惠券已领取完
						if (StringUtils.isEmpty(couponPickOver)) {
							couponPickOver = name + "优惠券已领取完;";
						} else {
							couponPickOver = name + "," + couponPickOver;
						}
						continue;
					}

					AfUserCouponDo userCoupon = new AfUserCouponDo();
					userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
					userCoupon.setGmtStart(new Date());
					if (StringUtils.equals(couponDo.getExpiryType(), "R")) {// range固定时间范围
						userCoupon.setGmtStart(couponDo.getGmtStart());
						userCoupon.setGmtEnd(couponDo.getGmtEnd());
						if (DateUtil.afterDay(new Date(), couponDo.getGmtEnd())) {
							userCoupon.setStatus(CouponStatus.EXPIRE.getCode());
						}
					} else {// days固定天数
						userCoupon.setGmtStart(new Date());
						if (couponDo.getValidDays() == -1) {
							userCoupon.setGmtEnd(DateUtil.getFinalDate());
						} else {
							userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
						}
					}
					userCoupon.setSourceType(CouponSenceRuleType.PICK.getCode());
					userCoupon.setStatus(CouponStatus.NOUSE.getCode());
					userCoupon.setUserId(afUserDo.getRid());
					afUserCouponDao.addUserCoupon(userCoupon);// 插入
																// af_user_coupon
																// 表
					AfCouponDo couponDoT = new AfCouponDo();
					couponDoT.setRid(couponDo.getRid());
					couponDoT.setQuotaAlready(1);
					afCouponService.updateCouponquotaAlreadyById(couponDoT);
					logger.info("pick coupon success", couponDoT);
					// 反馈结果集
					if (StringUtils.isEmpty(success)) {
						success = name + "优惠券领取成功！";
					} else {
						success = name + "," + success;
					}
				} else {// 第三方平台的红包领取
					String couponId = coupontInfos[0];
					AfResourceDo resourceDo = afResourceService.getResourceByResourceId(Long.parseLong(couponId));
					if (resourceDo == null) {// 请求参数错误
						logger.error("couponSceneId is invalid");
						continue;
					}
					String name = resourceDo.getName();
					PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
					bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);

					Date gmtStart = DateUtil.parseDate(resourceDo.getValue1(), DateUtil.DATE_TIME_SHORT);// 活动开始时间
					Date gmtEnd = DateUtil.parseDate(resourceDo.getValue2(), DateUtil.DATE_TIME_SHORT);// 活动结束时间

					if (DateUtil.beforeDay(new Date(), gmtStart)) {// 领取活动还未开始,敬请期待
						if (StringUtils.isEmpty(pickBrandCouponNotStart)) {
							pickBrandCouponNotStart = name + "领取活动还未开始,敬请期待;";
						} else {
							pickBrandCouponNotStart = name + "," + pickBrandCouponNotStart;
						}
						continue;
					}
					if (DateUtil.afterDay(new Date(), gmtEnd)) {// 活动已经结束,请期待下一次活动
						if (StringUtils.isEmpty(pickBrandCouponDateEnd)) {
							pickBrandCouponDateEnd = name + "活动已经结束,请期待下一次活动;";
						} else {
							pickBrandCouponDateEnd = name + "," + pickBrandCouponDateEnd;
						}
						continue;
					}

					String resultString = HttpUtil.doHttpPostJsonParam(resourceDo.getValue(),
							JSONObject.toJSONString(bo));
					logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
							resultString);
					JSONObject resultJson = JSONObject.parseObject(resultString);
					String code = resultJson.getString("code");
					// 10222代表已经一天只能领取一张
					if ("10222".equals(code)) {// 今日已领取，请明日再来
						if (StringUtils.isEmpty(haveAlreadyReceived)) {
							haveAlreadyReceived = name + "优惠券今日已领取，请明日再来;";
						} else {
							haveAlreadyReceived = name + "," + haveAlreadyReceived;
						}
						continue;
					} else if (!"0".equals(code)) {// 原因：resultJson.getString("msg")
						if (StringUtils.isEmpty(msg)) {
							msg = name + "优惠券领取失败;";
						} else {
							msg = name + "," + msg;
						}
						continue;
					} else if ("0".equals(code)) {
						if (StringUtils.isEmpty(success)) {
							success = name + "优惠券领取成功！";
						} else {
							success = name + "," + success;
						}
					}
				}
			}

			if (success != "") {
				result = FanbeiExceptionCode.SUCCESS.getDesc();
			} else if (couponPickOver != "" || haveAlreadyReceived != "" || couponMoreThanLimitCount != "") {
				result = FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR.getDesc();
				flag = false;
			} else if (pickBrandCouponDateEnd != "") {
				result = FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc();
				flag = false;
			} else if (pickBrandCouponNotStart != "") {
				result = FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc();
				flag = false;
			} else if (couponNotExist != "") {
				result = FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc();
				flag = false;
			}
			return H5CommonResponse.getNewInstance(flag, result, "", null).toString();
		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}
	}

	/**
	 * 获取菠萝觅跳转地址
	 * 
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
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null)
						.toString();
			}

			AfShopDo shopInfo = afShopService.getShopById(shopId);
			if (shopInfo == null) {
				logger.error("shopId is invalid");
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null)
						.toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (StringUtils.isEmpty(userName) || afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "登陆之后才能进行查看", notifyUrl, null).toString();
			}
			String shopUrl = shopInfo.getShopUrl() + "?";

			buildParams.put(BoluomeCore.CUSTOMER_USER_ID, afUserDo.getRid() + StringUtil.EMPTY);
			buildParams.put(BoluomeCore.CUSTOMER_USER_PHONE, afUserDo.getMobile());
			buildParams.put(BoluomeCore.TIME_STAMP, System.currentTimeMillis() + StringUtil.EMPTY);

			String sign = BoluomeCore.buildSignStr(buildParams);
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
	 * 
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
			Long mobileReqTimeStamp = NumberUtil.objToLongDefault(request.getParameter("mobileReqTimeStamp"), 0L);
			Date reqTime = new Date(mobileReqTimeStamp);

			String mxcode = request.getParameter("mxcode");
			String userName = StringUtil.null2Str(JSON.parseObject(appInfo).get("userName"));
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);

			AfUserAuthDo authDo = new AfUserAuthDo();
			authDo.setUserId(afUserDo.getRid());
			// 此字段保存该笔认证申请的发起时间，更新时做校验，防止在更新时，风控对这笔认证已经回调处理成功，造成错误更新
			authDo.setGmtMobile(reqTime);

			if (MoXieResCodeType.ONE.getCode().equals(mxcode) || MoXieResCodeType.TWO.getCode().equals(mxcode)) {
				// 用户认证处理中
				authDo.setMobileStatus(MobileStatus.WAIT.getCode());
				int updateRowNums = afUserAuthService.updateUserAuthMobileStatusWait(authDo);
				if (updateRowNums == 0) {
					logger.info("mobileOperator updateUserAuthMobileStatusWait fail, risk happen before.desStatus="
							+ MobileStatus.WAIT.getCode() + "userId=" + afUserDo.getRid());
				}
				processResult = true;
			} else if (MoXieResCodeType.FIFTY.getCode().equals(mxcode)) {
				// 三方不经过强风控，直接通过backUrl返回api告知用户认证失败
				authDo.setMobileStatus(MobileStatus.NO.getCode());
				int updateRowNums = afUserAuthService.updateUserAuthMobileStatusWait(authDo);
				if (updateRowNums == 0) {
					logger.info("mobileOperator updateUserAuthMobileStatusWait fail, risk happen before.desStatus="
							+ MobileStatus.NO.getCode() + "userId=" + afUserDo.getRid());
				}
				processResult = false;
			} else {
				processResult = false;
			}
			model.put("processResult", processResult);
		} catch (Exception e) {
			logger.error("mobileOperator , e = {}", e.getMessage());
			processResult = false;
			model.put("processResult", processResult);
		} finally {
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, processResult + ""));
		}

	}

	/**
	 * 获取物流信息
	 * 
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = {
			"/getOrderLogistics" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getOrderLogistics(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext context = null;
		try {
			long orderId = NumberUtil.strToLong(request.getParameter("orderId").toString());
			long isOutTraces = NumberUtil.strToLong(request.getParameter("traces") == null ? String.valueOf(0)
					: request.getParameter("traces").toString());
			AfOrderLogisticsBo afOrderLogisticsBo = afOrderLogisticsService.getOrderLogisticsBo(orderId, isOutTraces);
			if (afOrderLogisticsBo != null) {
				return H5CommonResponse.getNewInstance(true, "", "", afOrderLogisticsBo).toString();
			} else {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.LOGISTICS_NOT_EXIST.getErrorMsg())
						.toString();
			}

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.toString()).toString();
		}
	}

	/**
	 * 获取物流信息
	 * 
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = {
			"/getLegalOrderLogistics" }, method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getLegalOrderLogistics(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			long orderId = NumberUtil.strToLong(request.getParameter("orderId").toString());
			long isOutTraces = NumberUtil.strToLong(request.getParameter("traces") == null ? String.valueOf(0)
					: request.getParameter("traces").toString());
			AfOrderLogisticsBo afOrderLogisticsBo = afBorrowLegalOrderLogisticsService.getLegalOrderLogisticsBo(orderId,
					isOutTraces);
			if (afOrderLogisticsBo != null) {
				return H5CommonResponse.getNewInstance(true, "", "", afOrderLogisticsBo).toString();
			} else {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.LOGISTICS_NOT_EXIST.getErrorMsg())
						.toString();
			}

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.toString()).toString();
		}
	}

	/**
	 * 第三方链接跳转，记录pv，uv
	 * 
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = { "thirdPartyLink" }, method = RequestMethod.GET)
	public void thirdPartyLink(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext context = null;
		try {
			String linkType = request.getParameter("linkType");
			// app端借贷超市banner跳转进来
			if (ThirdPartyLinkType.APP_LOAN_BANNER.getCode().equals(linkType)) {
				context = doWebCheckNoAjax(request, true);
				String lsmNo = request.getParameter("lsmNo");
				AfLoanSupermarketDo afLoanSupermarket = afLoanSupermarketService.getLoanSupermarketByLsmNo(lsmNo);
				AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
				if (afLoanSupermarket != null && StringUtil.isNotBlank(afLoanSupermarket.getLinkUrl())) {
					String accessUrl = afLoanSupermarket.getLinkUrl();
					accessUrl = accessUrl.replaceAll("\\*", "\\&");
					logger.info("贷款超市app点击banner请求发起正常，地址：" + accessUrl + "-id:" + afLoanSupermarket.getId() + "-名称:"
							+ afLoanSupermarket.getLsmName() + "-userId:" + afUserDo.getRid());
					String sysModeId = JSON.parseObject(context.getAppInfo()).getString("id");
					String channel = getChannel(sysModeId);
					String extraInfo = "sysModeId=" + sysModeId + ",appVersion=" + context.getAppVersion() + ",lsmName="
							+ afLoanSupermarket.getLsmName() + ",accessUrl=" + accessUrl;
					AfBusinessAccessRecordsDo afBusinessAccessRecordsDo = new AfBusinessAccessRecordsDo();
					afBusinessAccessRecordsDo.setUserId(afUserDo.getRid());
					afBusinessAccessRecordsDo.setSourceIp(CommonUtil.getIpAddr(request));
					afBusinessAccessRecordsDo
							.setRefType(AfBusinessAccessRecordsRefType.LOANSUPERMARKET_BANNER.getCode());
					afBusinessAccessRecordsDo.setRefId(afLoanSupermarket.getId());
					afBusinessAccessRecordsDo.setExtraInfo(extraInfo);
					// afBusinessAccessRecordsDo.setRemark(ThirdPartyLinkType.APP_LOAN_BANNER.getCode());
					afBusinessAccessRecordsDo.setChannel(channel);
					afBusinessAccessRecordsService.saveRecord(afBusinessAccessRecordsDo);
					model.put("redirectUrl", accessUrl);
				} else {
					logger.error(
							"贷款超市app点击banner请求发起异常-贷款超市不存在或跳转链接为空，lsmNo：" + lsmNo + "-userId:" + afUserDo.getRid());
					model.put("redirectUrl", "/static/error404.html");
				}
			} else if (ThirdPartyLinkType.HOME_POPUP_WND.getCode().equals(linkType)) {
				context = doWebCheckNoAjax(request, true);
				AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
				String id = request.getParameter("popupsId");
				AfPopupsDo afPopupsDo = afPopupsService.selectPopups(Long.valueOf(id).longValue());
				if (afPopupsDo != null && StringUtil.isNotBlank(afPopupsDo.getUrl())) {
					String sysModeId = JSON.parseObject(context.getAppInfo()).getString("id");
					String channel = getChannel(sysModeId);
					String extraInfo = "sysModeId=" + sysModeId + ",appVersion=" + context.getAppVersion() + ",Name="
							+ afPopupsDo.getName() + ",accessUrl=" + afPopupsDo.getUrl();
					AfBusinessAccessRecordsDo afBusinessAccessRecordsDo = new AfBusinessAccessRecordsDo();
					afBusinessAccessRecordsDo.setUserId(afUserDo.getRid());
					afBusinessAccessRecordsDo.setSourceIp(CommonUtil.getIpAddr(request));
					afBusinessAccessRecordsDo
							.setRefType(AfBusinessAccessRecordsRefType.LOANSUPERMARKET_BANNER.getCode());
					afBusinessAccessRecordsDo.setRefId(afPopupsDo.getId());
					afBusinessAccessRecordsDo.setExtraInfo(extraInfo);
					afBusinessAccessRecordsDo.setRemark(ThirdPartyLinkType.HOME_POPUP_WND.getCode());
					afBusinessAccessRecordsDo.setChannel(channel);
					afBusinessAccessRecordsDo.setRedirectUrl(afPopupsDo.getUrl());
					afBusinessAccessRecordsService.saveRecord(afBusinessAccessRecordsDo);
					int count = afPopupsDo.getClickAmount() + 1;
					afPopupsDo.setClickAmount(count);
					afPopupsService.updatePopups(afPopupsDo);
					model.put("redirectUrl", afPopupsDo.getUrl());
				} else {
					logger.error("首页极光推送跳转失败，popupsId：" + id + "-userId:" + afUserDo.getRid());
					model.put("redirectUrl", "/static/error404.html");
				}
			} else if (ThirdPartyLinkType.H5_LOAN_BANNER.getCode().equals(linkType)
					|| ThirdPartyLinkType.H5_LOAN_LIST.getCode().equals(linkType)) { // h5端借贷超市
				String lsmNo = request.getParameter("lsmNo");
				AfLoanSupermarketDo afLoanSupermarket = afLoanSupermarketService.getLoanSupermarketByLsmNo(lsmNo);
				if (afLoanSupermarket != null && StringUtil.isNotBlank(afLoanSupermarket.getLinkUrl())) {
					String accessUrl = afLoanSupermarket.getLinkUrl();
					accessUrl = accessUrl.replaceAll("\\*", "\\&");
					doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
					model.put("redirectUrl", accessUrl);
				}
			} else {
				logger.error("借贷超市linkType类型不对");
				model.put("redirectUrl", "/static/error404.html");
			}

		} catch (Exception e) {
			logger.error("贷款超市点击第三方链接请求发起异常,异常信息:{}", e);
			model.put("redirectUrl", "/static/error404.html");
		}
	}

	private String getChannel(String sysModeId) {
		if (sysModeId != null) {
			int lastIndex = sysModeId.lastIndexOf("_");
			if (lastIndex != -1) {
				String lasterStr = sysModeId.substring(++lastIndex);
				if (NumberUtils.isNumber(lasterStr)) {
					return "www"; // 早期不是www后缀，兼容旧版本
				} else {
					return lasterStr;
				}
			}
		}
		return "";
	}
	
	
	/**
	 * 
	* @Title: isLogin
	* @author chenqiwei
	* @date 2018年1月23日 上午10:43:05
	* @Description: 是否登录
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "isLogin", method = RequestMethod.POST,  produces = "application/json;charset=utf-8")
	@ResponseBody
	public String initTigerMachine(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		String result = "";

		try {
			context = doWebCheck(request, true);
//			Long userId = convertUserNameToUserId(context.getUserName());
			result = H5CommonResponse.getNewInstance(true, "已登录", "", data).toString();

		} catch (FanbeiException e) {
		    if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)){
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return  H5CommonResponse.getNewInstance(false, "没有登录", "", null).toString();
			}
		} 
		
		catch (Exception e) {
			logger.error("/fanbei-web/isLogin error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取登录信息失败", null, "").toString();
		}
		return result;
	}
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
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
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
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
