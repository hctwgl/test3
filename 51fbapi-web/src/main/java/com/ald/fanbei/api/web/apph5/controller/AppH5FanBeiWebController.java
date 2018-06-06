/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.JobThreadPoolUtils;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsDoQuery;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;
import com.ald.fanbei.api.dal.domain.supplier.AfSolrSearchResultDo;
import com.ald.fanbei.api.web.api.goods.GetMoreGoodsApi;
import com.ald.fanbei.api.web.vo.AfSearchGoodsVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;

import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.dal.domain.query.HomePageSecKillByBottomGoodsQuery;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
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
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfBorrowLegalOrderLogisticsService afBorrowLegalOrderLogisticsService;
	@Resource
	AfResourceH5ItemService afResourceH5ItemService;
	@Resource
	AfSeckillActivityService afSeckillActivityService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfSubjectGoodsService afSubjectGoodsService;

	private final static int EXPIRE_DAY = 2;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
   @Resource
   JobThreadPoolUtils jobThreadPoolUtils;

	private final static String ASJ_IMAGES = 		   HomePageType.ASJ_IMAGES.getCode();//爱上街顶部图组
	private final static String GUESS_YOU_LIKE_TOP_IMAGE = 		   HomePageType.GUESS_YOU_LIKE_TOP_IMAGE.getCode();//猜你喜欢顶部图

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
			StringBuilder sb = new StringBuilder();
			sb.append("---mobileOperator begin:");
			Map<String, String[]> paramMap = request.getParameterMap();
			for (String key : paramMap.keySet()) {
				String[] values = paramMap.get(key);
				for (String value : values) {
					sb.append("键:" + key + ",值:" + value);
				}
			}
			sb.append("---mobileOperator end");

			logger.info(sb.toString());
			String appInfo = request.getParameter("_appInfo");
			Long mobileReqTimeStamp = NumberUtil.objToLongDefault(request.getParameter("mobileReqTimeStamp"), 0L);
			Date reqTime = new Date(mobileReqTimeStamp);

			String mxcode = request.getParameter("mxcode");
			String userName ="";
			if(appInfo!=null){
				userName = StringUtil.null2Str(JSON.parseObject(appInfo).get("userName"));
			}else{
				userName =request.getParameter("account");
			}
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
			logger.error("mobileOperator error", e);
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

	
	/**
	 * @author chenqiwei
	 * @说明：更多商品
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
	@RequestMapping(value = "/getMoreGoods", method = RequestMethod.POST,  produces = "application/json;charset=utf-8")
	public String getMoreGoods(HttpServletRequest request, ModelMap model) throws IOException {


		FanbeiWebContext context = doWebCheck(request, false);
		Map<String, Object> returnData = new HashMap<String, Object>();
		// 获取用户信息
		AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
		// 用户是否存在
		
		Long userId = null;
		if (afUserDo != null) {
		    	userId = afUserDo.getRid();
		}
		Integer pageNo = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
		String pageFlag = ObjectUtils.toString(request.getParameter("pageFlag"), null);
		
		if(pageFlag == null || pageNo == null){
			logger.error("pageFlag or pageNo is null");
			return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(),
					"", returnData).toString();
	      }
	 
		//更多商品
	try{
		 Map<String, Object> goodsInfo = new HashMap<String, Object>();
		 //更换查询表
		// Map<String, Object> moreGoodsTemp = new HashMap<String, Object>();
		 String cacheKey = CacheConstants.ASJ_HOME_PAGE.ASJ_PAY_SESULT_PAGE_GOODS_PAGENO.getCode()+ ":"+pageFlag+":"+pageNo;
		String cacheKey2 = CacheConstants.ASJ_HOME_PAGE.ASJ_PAY_SESULT_PAGE_GOODS_PAGENO_SECOND.getCode()+ ":"+pageFlag+":"+pageNo;
		String processKey = CacheConstants.ASJ_HOME_PAGE.ASJ_PAY_SESULT_PAGE_GOODS_PAGENO_SECOND_PROCESS_KEY.getCode()+ ":"+pageFlag+":"+pageNo;
		String source = "H5";

		goodsInfo =  (Map<String, Object>) bizCacheUtil.getMap(cacheKey);
		logger.info("getMoreGoods h5"+Thread.currentThread().getName() + "goodsInfo  = "+JSONArray.toJSONString(goodsInfo)+"cacheKey = "+ cacheKey);
		 /*  if(goodsInfo != null){
			   goodsInfo = moreGoodsTemp;
		   }	*/
		   if(goodsInfo == null || goodsInfo.isEmpty()) {

			   boolean isGetLock = bizCacheUtil.getLock30Second(processKey, "1");
			   goodsInfo = (Map<String, Object>) bizCacheUtil.getMap(cacheKey2);
			   logger.info("getMoreGoods h5" + Thread.currentThread().getName() + "isGetLock:" + isGetLock + "goodsInfo= " + JSONArray.toJSONString(goodsInfo) + "cacheKey2 = " + cacheKey2);
			   //调用异步请求加入缓存
			   if (isGetLock) {
				   logger.info("getMoreGoods h5" + Thread.currentThread().getName() + "getMoreGoods h5 is null" + "cacheKey = " + cacheKey);
				   Runnable process = new GetH5MoreGoodsInfo(cacheKey, cacheKey2, null, pageNo, pageFlag, source);
				   jobThreadPoolUtils.asynProcessBusiness(process);
			   }
		   }
		//调用异步请求加入缓存
		if(goodsInfo==null){
			goodsInfo = toGetMoreGoodsInfoMap(null,pageNo,pageFlag,source);
			if(goodsInfo != null) {
				bizCacheUtil.saveMap(cacheKey, goodsInfo, Constants.MINITS_OF_TWO);
				bizCacheUtil.saveMapForever(cacheKey2, goodsInfo);
			}
		}
//
//			   Map<String, Object> goodsListMap = afSeckillActivityService.getMoreGoodsByBottomGoodsTable(userId,pageNo,pageFlag,"H5");
//		 List<HomePageSecKillGoods> goodsList = (List<HomePageSecKillGoods>) goodsListMap.get("goodsList");
//		// List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getMoreGoodsByBottomGoodsTable(userId,pageNo,pageFlag);
//		 List<Map<String, Object>> moreGoodsInfoList = getGoodsInfoList(goodsList,null,null);
//
//		     String imageUrl = "";
//		     String type = "";
//		     String content = "";
//		     List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,GUESS_YOU_LIKE_TOP_IMAGE);
//		     if(recommendList != null && recommendList.size() >0){
//		    	 AfResourceH5ItemDo recommend = recommendList.get(0);
//		    	 imageUrl = recommend.getValue3();
//		    	 type = recommend.getValue4();
//		    	 content = recommend.getValue1();
//
//		     }
//				if(StringUtil.isNotEmpty(imageUrl) && moreGoodsInfoList != null && moreGoodsInfoList.size()>0){
//					 HomePageSecKillByBottomGoodsQuery homePageSecKillGoods = (HomePageSecKillByBottomGoodsQuery)goodsListMap.get("query");
//					 if(homePageSecKillGoods != null){
//						 int pageSize = homePageSecKillGoods.getPageSize();
//						 int size = goodsList.size();
//						 if(pageSize > size){
//							 goodsInfo.put("nextPageNo",-1);
//						 }else{
//							 goodsInfo.put("nextPageNo",pageNo+1);
//						 }
//						 goodsInfo.put("imageUrl",imageUrl);
//						 goodsInfo.put("type",type);
//						 goodsInfo.put("content",content);
//						 goodsInfo.put("moreGoodsList", moreGoodsInfoList);
//					 }
//				}
//				 bizCacheUtil.saveMap(cacheKey, goodsInfo, Constants.MINITS_OF_TWO);
//		   }
		     
			 if (!goodsInfo.isEmpty()) {
					returnData.put("moreGoodsInfo", goodsInfo);
				}
		 }catch(Exception e){
			 logger.error("h5 get moreGoodsInfo goodsInfo error "+ e);
		 }
			
			return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),
					"", returnData).toString();
		
	}
	class GetH5MoreGoodsInfo implements Runnable {

		protected  final Logger logger = LoggerFactory.getLogger(GetH5MoreGoodsInfo.class);

		private String source;
		private String  pageFlag;
		private Integer pageNo;
		private Long userId;
		private String  firstKey;
		private String  secondKey;
		@Resource
		BizCacheUtil bizCacheUtil;
		GetH5MoreGoodsInfo(String firstKey,String secondKey,Long userId,Integer pageNo,String pageFlag,String source) {

			this.source = source;
			this.pageFlag = pageFlag;
			this.pageNo = pageNo;
			this.userId = userId;
			this.firstKey = firstKey;
			this.secondKey = secondKey;
		}
		@Override
		public void run() {
			logger.info("pool:getMoreGoods h5"+Thread.currentThread().getName() + "GetMoreGoodsInfo");
			try{
				GetMoreGoodsInfoMap( firstKey,secondKey , userId, pageNo, pageFlag,source);

			}catch (Exception e){
				logger.error("pool:getMoreGoods h5 error for" + e);
			}
		}
	}
	private Map<String, Object> GetMoreGoodsInfoMap(String firstKey , String secondKey ,Long userId,Integer pageNo,String pageFlag,String source) {
		//获取所有活动
		Map<String, Object> goodsInfo =  toGetMoreGoodsInfoMap(userId, pageNo, pageFlag, source);
		if(goodsInfo != null) {
			bizCacheUtil.saveMap(firstKey, goodsInfo, Constants.MINITS_OF_TWO);
			bizCacheUtil.saveMapForever(secondKey, goodsInfo);
		}
		return null;
	}
	Map<String, Object> toGetMoreGoodsInfoMap(Long userId,Integer pageNo,String pageFlag,String source ){
		Map<String, Object> goodsInfo = new HashMap<String, Object>();
		userId = null; // 不查，且放入缓存会有问题。
		Map<String, Object> goodsListMap = afSeckillActivityService.getMoreGoodsByBottomGoodsTable(userId,pageNo,pageFlag,"H5");
		 List<HomePageSecKillGoods> goodsList = (List<HomePageSecKillGoods>) goodsListMap.get("goodsList");
		// List<HomePageSecKillGoods> goodsList = afSeckillActivityService.getMoreGoodsByBottomGoodsTable(userId,pageNo,pageFlag);
		 List<Map<String, Object>> moreGoodsInfoList = getGoodsInfoList(goodsList,null,null);

		     String imageUrl = "";
		     String type = "";
		     String content = "";
		     List<AfResourceH5ItemDo>  recommendList =  afResourceH5ItemService.getByTagAndValue2(ASJ_IMAGES,GUESS_YOU_LIKE_TOP_IMAGE);
		     if(recommendList != null && recommendList.size() >0){
		    	 AfResourceH5ItemDo recommend = recommendList.get(0);
		    	 imageUrl = recommend.getValue3();
		    	 type = recommend.getValue4();
		    	 content = recommend.getValue1();

		     }
				if(StringUtil.isNotEmpty(imageUrl) && moreGoodsInfoList != null && moreGoodsInfoList.size()>0){
					 HomePageSecKillByBottomGoodsQuery homePageSecKillGoods = (HomePageSecKillByBottomGoodsQuery)goodsListMap.get("query");
					 if(homePageSecKillGoods != null){
						 int pageSize = homePageSecKillGoods.getPageSize();
						 int size = goodsList.size();
						 if(pageSize > size){
							 goodsInfo.put("nextPageNo",-1);
						 }else{
							 goodsInfo.put("nextPageNo",pageNo+1);
						 }
						 goodsInfo.put("imageUrl",imageUrl);
						 goodsInfo.put("type",type);
						 goodsInfo.put("content",content);
						 goodsInfo.put("moreGoodsList", moreGoodsInfoList);
					 }
				}
				return goodsInfo;
	}


	private List<Map<String, Object>> getGoodsInfoList(List<HomePageSecKillGoods> list,String tag,AfResourceH5ItemDo afResourceH5ItemDo){
		List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		if (array == null) {
		    throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}

		for (HomePageSecKillGoods homePageSecKillGoods : list) {
		    Map<String, Object> goodsInfo = new HashMap<String, Object>();
		    String  rebateAmount =   homePageSecKillGoods.getRebateAmount().toString();
		    String priceAmount =  homePageSecKillGoods.getPriceAmount().toString();
		    String saleAmount  = homePageSecKillGoods.getSaleAmount().toString();
		    //如果大于等于8位，直接截取前8位。否则判断小数点后面的是否是00,10.分别去掉两位，一位
		    if(null == homePageSecKillGoods.getActivityAmount()){
		    	 goodsInfo.put("activityAmount", homePageSecKillGoods.getActivityAmount());
		    }else{
		    	 goodsInfo.put("activityAmount", substringAmount(homePageSecKillGoods.getActivityAmount().toString()));
		    }
		    
		    goodsInfo.put("goodsName", homePageSecKillGoods.getGoodName());
		    goodsInfo.put("rebateAmount",  substringAmount(rebateAmount));
		    goodsInfo.put("saleAmount",    substringAmount(saleAmount));
		    goodsInfo.put("priceAmount",   substringAmount(priceAmount));
		   
		    goodsInfo.put("goodsIcon", homePageSecKillGoods.getGoodsIcon());
		    goodsInfo.put("goodsId", homePageSecKillGoods.getGoodsId());
		    goodsInfo.put("goodsUrl", homePageSecKillGoods.getGoodsUrl());
		    goodsInfo.put("goodsType", "0");
		    goodsInfo.put("subscribe", homePageSecKillGoods.getSubscribe());
		    goodsInfo.put("volume", homePageSecKillGoods.getVolume());
		    goodsInfo.put("total", homePageSecKillGoods.getTotal());	
		    goodsInfo.put("source", homePageSecKillGoods.getSource()); 
		    
		    // 如果是分期免息商品，则计算分期
		    Long goodsId = homePageSecKillGoods.getGoodsId();
		    JSONArray interestFreeArray = null;
		    if (homePageSecKillGoods.getInterestFreeId() != null) {
			AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(homePageSecKillGoods.getInterestFreeId().longValue());
			String interestFreeJson = interestFreeRulesDo.getRuleJson();
			if (StringUtil.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
			    interestFreeArray = JSON.parseArray(interestFreeJson);
			}
		    }
		    
		    BigDecimal  showAmount =  homePageSecKillGoods.getSaleAmount();
			   if(null != homePageSecKillGoods.getActivityAmount()){
				   showAmount = homePageSecKillGoods.getActivityAmount();
			   }
		    
		    List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(), 
		    		showAmount, resource.getValue1(), resource.getValue2(), goodsId, "0");
		    if (nperList != null) {
			goodsInfo.put("goodsType", "1");
			Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
			String isFree = (String) nperMap.get("isFree");
			if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
				//不影响其他业务，次处加
				Object oAmount =  nperMap.get("amount");
				String amount = "";
				if(oAmount != null){
					amount = oAmount.toString();
				}
				nperMap.put("amount",substringAmount(amount));
			    nperMap.put("freeAmount",substringAmount(amount));
			}
			goodsInfo.put("nperMap", nperMap);
		     //更换content和type可跳转商品详情
		   }
		    goodsList.add(goodsInfo);
		}
		return goodsList;
	}

	/**
	 * @author hqj
	 * @说明：商品详情页优惠券
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
	@RequestMapping(value = "/getDetailCouponList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getDetailCouponList(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext context = doWebCheck(request, false);
		Long goodsId = NumberUtil.objToLongDefault(request.getParameter("goodsId"), null);
		List<AfUserCouponDto> list = new ArrayList<AfUserCouponDto>();
		try{
			// 获取用户信息
			Long userId = null;
			String userName = context.getUserName();
			if(StringUtil.isNotBlank(userName)) {
				AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
				if (afUserDo != null) {
					userId = afUserDo.getRid();
				}
			}
			AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
			Long brandId = afGoodsDo.getBrandId();
			Long categoryId = afGoodsDo.getCategoryId();
			List<AfUserCouponDto> userCouponList = new ArrayList<>();

			List<AfSubjectGoodsDo> subjectGoods = null;
			//获取所有优惠券
			if(userId==null){
				userCouponList = afUserCouponService.getUserAllCoupon();

			}else {
				userCouponList = afUserCouponService.getUserAllCouponByUserId(userId);
			}
			if(userCouponList!=null&&userCouponList.size()>0){
				for(AfUserCouponDto afUserCouponDto:userCouponList){
					String expiryType = afUserCouponDto.getExpiryType();
					Date gmtStart = afUserCouponDto.getGmtStart();
					Date gmtEnd = afUserCouponDto.getGmtEnd();
					int validDays = afUserCouponDto.getValidDays();
					int is_global = afUserCouponDto.getIsGlobal();
					String goodsIds = afUserCouponDto.getGoodsIds();
					if(StringUtil.isBlank(expiryType)){
						continue;
					}else {
						if(StringUtil.equals("D",expiryType)&&validDays<=0){
							continue;
						}else if(StringUtil.equals("R",expiryType)&&(gmtStart==null||gmtEnd==null||gmtEnd.getTime()<=(new Date()).getTime())){
							continue;
						}
					}
					if(is_global==0){
						list.add(afUserCouponDto);
					}else if(is_global==2&&StringUtil.isNotBlank(goodsIds)){
						goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
						if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(goodsId))){//当前商品id被包含在券信息的商品id集合里面
							list.add(afUserCouponDto);
						}
					}else if(is_global==3&&StringUtil.isNotBlank(goodsIds)){
						goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
						if(subjectGoods==null){
							subjectGoods = afSubjectGoodsService.getSubjectGoodsByGoodsId(goodsId);
						}
						if(subjectGoods!=null&&subjectGoods.size()>0){
							for(AfSubjectGoodsDo afSubjectGoodsDo : subjectGoods){
								String subjectId = afSubjectGoodsDo.getSubjectId();
								if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(subjectId))){//当前会场id被包含在券信息的会场id集合里面
									list.add(afUserCouponDto);
									break;
								}
							}
						}
					}else if(is_global==4&&StringUtil.isNotBlank(goodsIds)){
						goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
						if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(categoryId))){//当前分类id被包含在券信息的分类id集合里面
							list.add(afUserCouponDto);
						}
					}else if(is_global==5&&StringUtil.isNotBlank(goodsIds)){
						goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
						if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(brandId))){//当前品牌id被包含在券信息的品牌id集合里面
							list.add(afUserCouponDto);
						}
					}
				}
			}
		}catch (Exception e){
			logger.error("getCouponList error for " + e);
		}
		return H5CommonResponse.getNewInstance(true, "", "", list).toString();


	}

	/**
	 * @author hqj
	 * @说明：商品详情页服务信息
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
	@RequestMapping(value = "/getSaleServiceInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getSaleServiceInfo(HttpServletRequest request, ModelMap model) throws IOException {
		FanbeiWebContext context = doWebCheck(request, false);
		String serviceInfo = "";
		try{
			AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(Constants.COUPON_SALESERVICE);
			if(afResourceDo!=null){
				String value = afResourceDo.getValue();
				if(StringUtil.isNotBlank(value)){
					serviceInfo = value;
				}
			}
		}catch (Exception e){
			logger.error("getCouponList error for " + e);
		}
		return H5CommonResponse.getNewInstance(true, "", "", serviceInfo).toString();
	}

	/**
	 * @author hqj
	 * @说明：我的优惠券-h5接口
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
	@RequestMapping(value = "/getMineCouponList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getMineCouponList(HttpServletRequest request, ModelMap model) throws IOException {
		H5CommonResponse resp = H5CommonResponse.getNewInstance();FanbeiWebContext context = new FanbeiWebContext();
		try{
			context = doWebCheck(request,true);
			String userName = context.getUserName();
			//userName = "18314896619";
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			Long userId = userDo.getRid();
			Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(request.getParameter("pageNo")), 1);
			String status = ObjectUtils.toString(request.getParameter("status"),"");
			Map<String, Object> data = new HashMap<String, Object>();
			// 获取领券中心URL add by jrb
			List<AfResourceDo>  resourceList = afResourceService.getConfigByTypes(ResourceType.NEWCOUPON_CENTER_URL.getCode());
			if(resourceList != null && !resourceList.isEmpty()) {
				AfResourceDo resourceDo = resourceList.get(0);
				String couponCenterUrl = resourceDo.getValue();
				String isShow = resourceDo.getValue1();
				if("Y".equals(isShow)) {
					data.put("couponCenterUrl", couponCenterUrl);
				}
			}
			logger.info("userId=" + userId + ",status=" + status);
			List<AfUserCouponDto> couponList = afUserCouponService.getH5UserCouponByUser(userId,status);

			List<AfUserCouponVo> couponVoList = new ArrayList<AfUserCouponVo>();
			for (AfUserCouponDto afUserCouponDto : couponList) {
				AfUserCouponVo couponVo = getUserCouponVo(afUserCouponDto);
				Date gmtEnd = couponVo.getGmtEnd();
				// 如果当前时间离到期时间小于48小时,则显示即将过期
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, EXPIRE_DAY);
				Date twoDay = cal.getTime();
				if(gmtEnd != null){
					if(twoDay.after(gmtEnd)) {
						couponVo.setWillExpireStatus("Y");
					} else {
						couponVo.setWillExpireStatus("N");
					}
				} else {
					couponVo.setWillExpireStatus("N");
				}
				// 查询优惠券所在分类
				List <AfCouponCategoryDo> couponCategoryList = afCouponCategoryService.getCouponCategoryByCouponId(afUserCouponDto.getCouponId());
				if(couponCategoryList != null && !couponCategoryList.isEmpty()) {
					logger.info("couponCategoryList info=>" + couponCategoryList.toString());
					AfCouponCategoryDo afCouponCategoryDo = couponCategoryList.get(0);
					String shopUrl = afCouponCategoryDo.getUrl();
					couponVo.setShopUrl(shopUrl);
				}
				couponVoList.add(couponVo);
			}
			data.put("couponList", couponVoList);
			return H5CommonResponse.getNewInstance(true, "", "", data).toString();
		} catch(FanbeiException e){
			String opennative = "/fanbei-web/opennative?name=";
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+ H5OpenNativeType.AppLogin.getCode();
			return H5CommonResponse
					.getNewInstance(false, "登陆之后才能进行查看", notifyUrl,null )
					.toString();
		}catch (Exception e){
			logger.error("getMineCouponList error", e);
			return H5CommonResponse.getNewInstance(false, "预约失败").toString();
		}
	}

	/**
	 * @author hqj
	 * @说明：我的优惠券点击跳转商品列表-h5接口
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
	@RequestMapping(value = "/getCouponGoodsList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String getCouponGoodsList(HttpServletRequest request, ModelMap model) throws IOException {
		H5CommonResponse resp = H5CommonResponse.getNewInstance();FanbeiWebContext context = new FanbeiWebContext();
		try{
			context = doWebCheck(request,false);
			//userName = "18314896619";
			Map<String, Object> data = new HashMap<String, Object>();
			Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(request.getParameter("pageNo")), 1);
			Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("couponId")), 0);
			String sort = ObjectUtils.toString(request.getParameter("sort"), null);
			AfCouponDo afCouponDo = afCouponService.getCouponById(couponId);
			if(afCouponDo==null){
				return H5CommonResponse.getNewInstance(true, "", "", data).toString();
			}
			int isGlobal = afCouponDo.getIsGlobal();
			String goodsIds = "";
			if(StringUtil.isNotBlank(afCouponDo.getGoodsIds())){
				goodsIds = afCouponDo.getGoodsIds();
				String[] goodsIdStr = goodsIds.replace("，",",").split(",");
				List<Long> goodsIdsList = CollectionConverterUtil.convertToListFromArray(goodsIds.split(","), new Converter<String, Long>() {
					@Override
					public Long convert(String source) {
						return Long.parseLong(source);
					}
				});
				AfGoodsDoQuery query = new AfGoodsDoQuery();
				List<Long> goodsIdList = new ArrayList<>();
				List<AfGoodsDo> afGoodsDos = new ArrayList<AfGoodsDo>();
				if(StringUtil.isNotBlank(goodsIds)){
					if(isGlobal==1){//H5模板
						Long activityId = afCouponDo.getActivityId();
						String activityType = afCouponDo.getActivityType();
						if(StringUtil.equals("H5_TEMPLATE",activityType)&&activityId!=null){
							goodsIdList = afGoodsService.getGoodsisGlobal1(activityId);
						}
						query.setGoodsIds(goodsIdList);
					}else if(isGlobal==2){//按照商品
						goodsIdList = goodsIdsList;
						query.setGoodsIds(goodsIdList);
					}else if(isGlobal==3){
						goodsIdList = afGoodsService.getGoodsisGlobal3(goodsIdsList);
						query.setGoodsIds(goodsIdList);
					}else if(isGlobal==4){
						query.setCategoryIds(goodsIdsList);
					}else if(isGlobal==5){
						query.setBrandIds(goodsIdsList);
					}
				}

				if (StringUtil.isNotBlank(sort)) {
					// set query.sort
					if (sort.contains("des")) {
						query.setSort("desc");
					} else {
						query.setSort("asc");
					}
					// get sortword
					if (sort.contains("price")) {
						query.setSortword("sale_amount");
					} else {
						query.setSortword("sale_count");
					}
				}
				query.setFull(true);
				query.setPageSize(20);
				List<AfSearchGoodsVo> goodsList = new ArrayList<AfSearchGoodsVo>();
				// get selfSupport goods
				List<AfGoodsDo> orgSelfGoodlist = new ArrayList<AfGoodsDo>();
				Integer totalCount = null;
				Integer totalPage = null;
				query.setPageNo(pageNo);
				orgSelfGoodlist = afGoodsService.getAvaliableSelfGoods(query);
				totalCount = query.getTotalCount();
				totalPage = query.getTotalPage();

				logger.info("/appH5Goods/searchGoods orgSelfGoodlist.size = {}", orgSelfGoodlist.size());
				List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();

				if(goodsIdList.size()==0){
					for (AfGoodsDo goodsDo : orgSelfGoodlist) {
						goodsIdList.add(goodsDo.getRid());
					}
				}
				if(CollectionUtils.isNotEmpty(goodsIdList)){
					activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
				}
				for (AfGoodsDo goodsDo : orgSelfGoodlist) {
					AfSearchGoodsVo vo = convertFromSelfToVo(goodsDo,activityGoodsDos);
					goodsList.add(vo);
				}

				data.put("goodsList", goodsList);
				data.put("totalCount", totalCount);
				data.put("totalPage", totalPage);
			}
			return H5CommonResponse.getNewInstance(true, "", "", data).toString();
		}catch (Exception e){
			logger.error("getCouponGoodsList error", e);
			return H5CommonResponse.getNewInstance(false, "操作失败").toString();
		}
	}

	private AfSearchGoodsVo convertFromSelfToVo(AfGoodsDo goodsDo,List<AfSeckillActivityGoodsDo> activityGoodsDos) {
		AfSearchGoodsVo goodsVo = new AfSearchGoodsVo();
		if (goodsDo != null) {
			for (AfSeckillActivityGoodsDo activityGoodsDo : activityGoodsDos) {
				if(activityGoodsDo.getGoodsId().equals(goodsDo.getRid())){
					goodsDo.setSaleAmount(activityGoodsDo.getSpecialPrice());
					BigDecimal secKillRebAmount = goodsDo.getSaleAmount().multiply(goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
					if(goodsDo.getRebateAmount().compareTo(secKillRebAmount)>0){
						goodsDo.setRebateAmount(secKillRebAmount);
					}
					break;
				}
			}
			goodsVo.setGoodsIcon(goodsDo.getGoodsIcon());
			goodsVo.setGoodsName(goodsDo.getName());
			goodsVo.setGoodsUrl(goodsDo.getGoodsUrl());
			goodsVo.setSource("SELFSUPPORT");

			goodsVo.setNperMap(getNper(goodsDo.getSaleAmount(),goodsDo.getRid()));

			goodsVo.setNumId(goodsDo.getRid().toString());
			goodsVo.setRealAmount(goodsDo.getSaleAmount().toString());
			goodsVo.setRebateAmount(goodsDo.getRebateAmount().toString());
			goodsVo.setThumbnailIcon(goodsDo.getThumbnailIcon());

		}
		return goodsVo;
	}

	public Map<String, Object> getNper(BigDecimal saleAmount,Long goodsid) {
		Map<String, Object> result = new HashMap<>();
		// 获取借款分期配置信息
		AfResourceDo res = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(res.getValue());
		if (goodsid != null && goodsid >0l) {
			array = afResourceService.checkNper(goodsid,"0",array);
		}
		final AfResourceDo resource = afResourceService.getSingleResourceBytype(Constants.RES_THIRD_GOODS_REBATE_RATE);
		List<Map<String, Object>> nperList = com.ald.fanbei.api.common.util.InterestFreeUitl.getConsumeList(array, null, BigDecimal.ONE.intValue(),
				saleAmount, resource.getValue1(), resource.getValue2());
		if (nperList != null) {
			Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
			return nperMap;
		}
		return result;
	}

	private AfUserCouponVo getUserCouponVo(AfUserCouponDto afUserCouponDto){
		AfUserCouponVo couponVo = new AfUserCouponVo();
		couponVo.setAmount(afUserCouponDto.getAmount());
		couponVo.setGmtEnd(afUserCouponDto.getGmtEnd());
		couponVo.setGmtStart(afUserCouponDto.getGmtStart());
		couponVo.setLimitAmount(afUserCouponDto.getLimitAmount());
		couponVo.setName(afUserCouponDto.getName());
		couponVo.setStatus(afUserCouponDto.getStatus());
		couponVo.setUseRule(afUserCouponDto.getUseRule());
		if(StringUtil.isNotBlank(afUserCouponDto.getType())){
			if(StringUtil.equals("LOAN",afUserCouponDto.getType())
					||StringUtil.equals("BORROWCASH",afUserCouponDto.getType())||StringUtil.equals("BORROWBILL",afUserCouponDto.getType())){
				afUserCouponDto.setType("REPAYMENT");
			}
		}
		couponVo.setType(afUserCouponDto.getType());
		couponVo.setUseRange(afUserCouponDto.getUseRange());
		couponVo.setShopUrl(afUserCouponDto.getShopUrl());
		couponVo.setRid(afUserCouponDto.getCouponId());
		couponVo.setIsGlobal(afUserCouponDto.getIsGlobal());
		couponVo.setDiscount(afUserCouponDto.getDiscount());
		couponVo.setActivityType(afUserCouponDto.getActivityType());
		return couponVo;
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
	private  BigDecimal substringAmount(String amount) {
		BigDecimal substringAmount = new BigDecimal(0);
		try{
		//判断小数点后面的是否是00,10.分别去掉两位，一位。去掉之后大于等于8位，则截取前8位。
		 String tempNumber = "0";
		 String afterNumber =  amount.substring(amount.indexOf(".")+1,amount.length());
		 if("00".equals(afterNumber)){
			 tempNumber = amount.substring(0,amount.length()-3);
		 } else if("10".equals(afterNumber)){
			 tempNumber = amount.substring(0,amount.length()-1);
		 }else{
			 tempNumber = amount;
		 }
		 if(tempNumber.length() >8 ){
			 tempNumber =  tempNumber.substring(0,8);
			 String t = tempNumber.substring(tempNumber.length()-1, tempNumber.length());
			 if(".".equals(t)){
				 tempNumber =  tempNumber.substring(0,tempNumber.length()-1);
			 }
		 }
		 substringAmount = new BigDecimal(tempNumber);
	 
		}catch(Exception e){
			logger.error("substringAmount error"+e);
		}
		return substringAmount;
	}

}
