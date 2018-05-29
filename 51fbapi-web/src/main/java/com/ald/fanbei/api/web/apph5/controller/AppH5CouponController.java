package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述： 返场活动
 *
 * @author 江荣波 2017年7月17日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
public class AppH5CouponController extends BaseController {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserService afUserService;
	@Resource
	BoluomeUtil boluomeUtil;

	@Resource
	BizCacheUtil bizCacheUtil;

	private String opennative = "/fanbei-web/opennative?name=";

	private final static int EXPIRE_DAY = 2;

	@RequestMapping(value = "couponCategoryInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String couponCategoryInfo(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			FanbeiWebContext context = doWebCheck(request, false);
			JSONObject jsonObj = new JSONObject();
			// 获取Banner信息
			List<AfResourceDo> bannerInfoList = afResourceService
					.getConfigByTypes(ResourceType.COUPON_CENTER_BANNER.getCode());
			if (bannerInfoList != null && !bannerInfoList.isEmpty()) {
				AfResourceDo bannerInfo = bannerInfoList.get(0);
				String bannerImage = bannerInfo.getValue();
				String bannerUrl = bannerInfo.getValue2();
				String isOpen = bannerInfo.getValue4();
				if ("O".equals(isOpen)) {
					jsonObj.put("bannerImage", bannerImage);
					jsonObj.put("bannerUrl", bannerUrl);
				}
			}
			// 查询所有优惠券分类
			List<AfCouponCategoryDo> afCouponCategoryList = afCouponCategoryService.listAllCouponCategory();
			List<Map<String, Object>> couponCategoryList = new ArrayList<Map<String, Object>>();

			Map<String, Object> allCouponMap = new HashMap<String, Object>();
			List<HashMap<String, Object>> allCouponInfoList = new ArrayList<HashMap<String, Object>>();
			allCouponMap.put("couponInfoList", allCouponInfoList);
			couponCategoryList.add(allCouponMap);
			allCouponMap.put("name", "推荐");
			AfCouponCategoryDo couponCategoryAll = afCouponCategoryService.getCouponCategoryAll();
			if (couponCategoryAll != null) {
				String couponsAll = couponCategoryAll.getCoupons();
				JSONArray arrayAll = (JSONArray) JSONArray.parse(couponsAll);
				for (int i = 0; i < arrayAll.size(); i++) {
					HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
					String couponId = (String) arrayAll.getString(i);
					AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
					if (afCouponDo == null)
						continue;
					couponInfoMap.put("shopUrl", couponCategoryAll.getUrl());
					couponInfoMap.put("couponId", afCouponDo.getRid());
					couponInfoMap.put("name", afCouponDo.getName());
					couponInfoMap.put("useRule", afCouponDo.getUseRule());
					couponInfoMap.put("type", afCouponDo.getType());
					couponInfoMap.put("amount", afCouponDo.getAmount());
					couponInfoMap.put("useRange", afCouponDo.getUseRange());
					couponInfoMap.put("limitAmount", afCouponDo.getLimitAmount());
					Date gmtStart = afCouponDo.getGmtStart();
					if (gmtStart != null) {
						couponInfoMap.put("gmtStart", gmtStart.getTime());
					} else {
						couponInfoMap.put("gmtStart", 0);
					}
					Date gmtEnd = afCouponDo.getGmtEnd();
					if (gmtEnd != null) {
						couponInfoMap.put("gmtEnd", gmtEnd.getTime());
					} else {
						couponInfoMap.put("gmtEnd", 0);
					}

					couponInfoMap.put("currentTime", System.currentTimeMillis());
					if (!context.isLogin()) {
						couponInfoMap.put("isDraw", "Y");
					} else {
						// 获取用户信息
						String userName = context.getUserName();
						AfUserDo user = afUserService.getUserByUserName(userName);
						// 判断是否领取优惠券
						int userCouponCount = afUserCouponService.getUserCouponByUserIdAndCouponId(user.getRid(),
								Long.parseLong(couponId));
						if (userCouponCount < afCouponDo.getLimitCount()) {
							couponInfoMap.put("isDraw", "Y");
						} else {
							couponInfoMap.put("isDraw", "N");
						}
					}
					// 判断优惠券是否领完
					Long quota = afCouponDo.getQuota();
					Integer quotaAlready = afCouponDo.getQuotaAlready();
					if (quota != 0 && quota != -1 && quota.intValue() <= quotaAlready.intValue()) {
						if (!context.isLogin()) {
							couponInfoMap.put("isOver", "N");
						} else {
							couponInfoMap.put("isOver", "Y");
						}
					} else {
						couponInfoMap.put("isOver", "N");
					}
					allCouponInfoList.add(new HashMap<String, Object>(couponInfoMap));
				}
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (AfCouponCategoryDo afCouponCategoryDo : afCouponCategoryList) {
				Map<String, Object> couponCategoryMap = new HashMap<String, Object>();
				couponCategoryMap.put("name", afCouponCategoryDo.getName());
				List<Map<String, Object>> couponInfoList = new ArrayList<Map<String, Object>>();

				String coupons = afCouponCategoryDo.getCoupons();
				JSONArray array = (JSONArray) JSONArray.parse(coupons);

				if (afCouponCategoryDo.getType().equals(1)) {
					// 菠萝蜜
					try {

						for (int i = 0; i < array.size(); i++) {
							HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
							String couponId = (String) array.getString(i);
							AfResourceDo afResourceDo = afResourceService
									.getOpenBoluomeCouponById(Long.parseLong(couponId));
							if (afResourceDo != null) {
								List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil
										.getActivityCouponList(afResourceDo.getValue());
								if(activityCouponList !=null && activityCouponList.size() >0){
								for (BrandActivityCouponResponseBo brandActivityCouponResponseBo : activityCouponList) {
									if (brandActivityCouponResponseBo.getType().equals(1)) {
										couponInfoMap.put("type", "FULLVOUCHER");
									} else if (brandActivityCouponResponseBo.getType().equals(2)) {
										couponInfoMap.put("type", "DISCOUNT");
									}
									couponInfoMap.put("couponType", 1);
									couponInfoMap.put("shopUrl", afCouponCategoryDo.getUrl());
									couponInfoMap.put("couponId", couponId);
									couponInfoMap.put("name", brandActivityCouponResponseBo.getName());
									// couponInfoMap.put("useRule",
									// brandActivityCouponResponseBo.getUseRule());
									couponInfoMap.put("amount", brandActivityCouponResponseBo.getValue());
									// couponInfoMap.put("useRange",
									// brandActivityCouponResponseBo.getUseRange());
									couponInfoMap.put("limitAmount", brandActivityCouponResponseBo.getThreshold());
									try {

										Date gmtStart = dateFormat.parse((afResourceDo.getValue1()));
										if (gmtStart != null) {
											couponInfoMap.put("gmtStart", gmtStart.getTime());
										} else {
											couponInfoMap.put("gmtStart", 0);
										}
										Date gmtEnd = dateFormat.parse((afResourceDo.getValue2()));
										if (gmtEnd != null) {
											couponInfoMap.put("gmtEnd", gmtEnd.getTime());
										} else {
											couponInfoMap.put("gmtEnd", 0);
										}
									} catch (Exception e) {
										e.printStackTrace();
										logger.info("get boluome time error", e);
									}
									couponInfoMap.put("currentTime", System.currentTimeMillis());
									boolean flag = false;

									if (!context.isLogin()) {
										couponInfoMap.put("isDraw", "Y");
									} else {
										// 获取用户信息
										flag = boluomeUtil.isHasCoupon(couponId + "", context.getUserName());
										// 判断是否领取优惠券
										if (flag) {
											couponInfoMap.put("isDraw", "N");
										} else {
											couponInfoMap.put("isDraw", "Y");
										}
									}
									// 判断优惠券是否领完
									if (brandActivityCouponResponseBo.getDistributed() >= brandActivityCouponResponseBo
											.getTotal()) {
										if (!context.isLogin()) {
											couponInfoMap.put("isOver", "N");
										} else {
											couponInfoMap.put("isOver", "Y");
										}
									} else {
										couponInfoMap.put("isOver", "N");
									}
									couponInfoList.add(couponInfoMap);
									allCouponInfoList.add(new HashMap<String, Object>(couponInfoMap));

								}
							    }
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
						logger.info("get boluome coupon error", e);
					}
				} else if (afCouponCategoryDo.getType().equals(0)) {
					for (int i = 0; i < array.size(); i++) {
						HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
						String couponId = (String) array.getString(i);
						AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
						if (afCouponDo == null)
							continue;
						if(StringUtil.isNotBlank(afCouponDo.getType())){
							if(StringUtil.equals("LOAN",afCouponDo.getType())
									||StringUtil.equals("BORROWCASH",afCouponDo.getType())||StringUtil.equals("BORROWBILL",afCouponDo.getType())){
								afCouponDo.setType("REPAYMENT");
							}
							if(StringUtil.equals("DISCOUNT",afCouponDo.getType())){
								continue;
							}
						}
						couponInfoMap.put("couponType", 0);
						couponInfoMap.put("shopUrl", afCouponCategoryDo.getUrl());
						couponInfoMap.put("couponId", afCouponDo.getRid());
						couponInfoMap.put("name", afCouponDo.getName());
						couponInfoMap.put("useRule", afCouponDo.getUseRule());
						couponInfoMap.put("type", afCouponDo.getType());
						couponInfoMap.put("amount", afCouponDo.getAmount());
						couponInfoMap.put("useRange", afCouponDo.getUseRange());
						couponInfoMap.put("limitAmount", afCouponDo.getLimitAmount());
						Date gmtStart = afCouponDo.getGmtStart();
						if (gmtStart != null) {
							couponInfoMap.put("gmtStart", gmtStart.getTime());
						} else {
							couponInfoMap.put("gmtStart", 0);
						}
						Date gmtEnd = afCouponDo.getGmtEnd();
						if (gmtEnd != null) {
							couponInfoMap.put("gmtEnd", gmtEnd.getTime());
						} else {
							couponInfoMap.put("gmtEnd", 0);
						}

						couponInfoMap.put("currentTime", System.currentTimeMillis());
						if (!context.isLogin()) {
							couponInfoMap.put("isDraw", "Y");
						} else {
							// 获取用户信息
							String userName = context.getUserName();
							AfUserDo user = afUserService.getUserByUserName(userName);
							// 判断是否领取优惠券
							int userCouponCount = afUserCouponService.getUserCouponByUserIdAndCouponId(user.getRid(),
									Long.parseLong(couponId));
							if (userCouponCount < afCouponDo.getLimitCount()) {
								couponInfoMap.put("isDraw", "Y");
							} else {
								couponInfoMap.put("isDraw", "N");
							}
						}
						// 判断优惠券是否领完
						Long quota = afCouponDo.getQuota();
						Integer quotaAlready = afCouponDo.getQuotaAlready();
						if (quota != 0 && quota != -1 && quota.intValue() <= quotaAlready.intValue()) {
							if (!context.isLogin()) {
								couponInfoMap.put("isOver", "N");
							} else {
								couponInfoMap.put("isOver", "Y");
							}
						} else {
							couponInfoMap.put("isOver", "N");
						}
						couponInfoList.add(couponInfoMap);
						allCouponInfoList.add(new HashMap<String, Object>(couponInfoMap));
					}

				}

				couponCategoryMap.put("couponInfoList", couponInfoList);
				couponCategoryList.add(couponCategoryMap);
			}
			jsonObj.put("couponCategoryList", couponCategoryList);
			return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", jsonObj).toString();
		} catch (Exception e) {
			e.printStackTrace();
			return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
		}

	}

	@RequestMapping(value = "getMineCouponInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getMineCouponInfo(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			// FIXME
			FanbeiWebContext context = doWebCheck(request, false);
			context = doWebCheck(request, false);
			JSONObject data = new JSONObject();
			String userName = context.getUserName();
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			if (userDo == null) {
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			} else {
				Long userId = userDo.getRid();
				Integer pageNo = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
				String status = ObjectUtils.toString(request.getParameter("status"));

				logger.info("userId=" + userId + ",pageNo=" + pageNo + ",status=" + status);

				AfUserCouponQuery query = new AfUserCouponQuery();
				query.setPageNo(pageNo);
				query.setUserId(userId);
				query.setStatus(status);
				List<AfUserCouponDto> couponList = afUserCouponService.getUserCouponByUser(query);

				List<AfUserCouponVo> couponVoList = new ArrayList<AfUserCouponVo>();
				for (AfUserCouponDto afUserCouponDto : couponList) {
					AfUserCouponVo couponVo = getUserCouponVo(afUserCouponDto);
					Date gmtEnd = couponVo.getGmtEnd();
					// 如果当前时间离到期时间小于48小时,则显示即将过期
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_YEAR, EXPIRE_DAY);
					Date twoDay = cal.getTime();
					if (gmtEnd != null) {
						if (twoDay.after(gmtEnd)) {
							couponVo.setWillExpireStatus("Y");
						} else {
							couponVo.setWillExpireStatus("N");
						}
					} else {
						couponVo.setWillExpireStatus("N");
					}
					// 查询优惠券所在分类
					List<AfCouponCategoryDo> couponCategoryList = afCouponCategoryService
							.getCouponCategoryByCouponId(afUserCouponDto.getCouponId());
					if (couponCategoryList != null && !couponCategoryList.isEmpty()) {
						logger.info("couponCategoryList info=>" + couponCategoryList.toString());
						AfCouponCategoryDo afCouponCategoryDo = couponCategoryList.get(0);
						String shopUrl = afCouponCategoryDo.getUrl();
						couponVo.setShopUrl(shopUrl);
					}
					couponVoList.add(couponVo);
				}

				data.put("pageNo", pageNo);
				data.put("couponList", couponVoList);
			}
			return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", data).toString();

		} catch (Exception e) {
			e.printStackTrace();
			return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
		}

	}

	private AfUserCouponVo getUserCouponVo(AfUserCouponDto afUserCouponDto) {
		AfUserCouponVo couponVo = new AfUserCouponVo();
		couponVo.setAmount(afUserCouponDto.getAmount());
		couponVo.setGmtEnd(afUserCouponDto.getGmtEnd());
		couponVo.setGmtStart(afUserCouponDto.getGmtStart());
		couponVo.setLimitAmount(afUserCouponDto.getLimitAmount());
		couponVo.setName(afUserCouponDto.getName());
		couponVo.setStatus(afUserCouponDto.getStatus());
		couponVo.setUseRule(afUserCouponDto.getUseRule());
		couponVo.setType(afUserCouponDto.getType());
		couponVo.setUseRange(afUserCouponDto.getUseRange());
		return couponVo;
	}

	@RequestMapping(value = "activityCouponInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String activityCouponInfo(HttpServletRequest request, ModelMap model) throws IOException {
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		FanbeiWebContext context = new FanbeiWebContext();
		context = doWebCheck(request, false);
		JSONObject jsonObj = new JSONObject();
		// 获取活动优惠券组Id
		String groupId = ObjectUtils.toString(request.getParameter("groupId"), null).toString();
		if (groupId == null) {
			throw new FanbeiException("groupId can't be null or empty.");
		}

		List<Map<String, Object>> couponList = Lists.newArrayList();
		try {
			// 判断用户是否登录
			boolean isLogin = false;
			String userName = context.getUserName();
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			Long userId = 0l;
			if (userDo != null) {
				isLogin = true;
				userId = userDo.getRid();
			}

			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryById(groupId);
			String coupons = couponCategory.getCoupons();
			JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
			if (couponCategory.getType().equals(0)) {
				for (int i = 0; i < couponsArray.size(); i++) {
					HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
					String couponId = (String) couponsArray.getString(i);
					AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
					couponInfoMap.put("shopUrl", couponCategory.getUrl());
					couponInfoMap.put("couponId", afCouponDo.getRid());
					couponInfoMap.put("name", afCouponDo.getName());
					couponInfoMap.put("useRule", afCouponDo.getUseRule());
					couponInfoMap.put("type", afCouponDo.getType());
					couponInfoMap.put("amount", afCouponDo.getAmount());
					couponInfoMap.put("useRange", afCouponDo.getUseRange());
					couponInfoMap.put("limitAmount", afCouponDo.getLimitAmount());
					couponInfoMap.put("drawStatus", "N");
					couponInfoMap.put("isFinish", "Y");
					if(afCouponDo.getQuota() == -1 || afCouponDo.getQuota() > afCouponDo.getQuotaAlready()){
						couponInfoMap.put("isFinish", "N");
					}
					if (isLogin) {
						int count = afUserCouponService.getUserCouponByUserIdAndCouponId(userId,
								Long.parseLong(couponId));
						if (count > 0) {
							couponInfoMap.put("drawStatus", "Y");
						}
					}
					Date gmtStart = afCouponDo.getGmtStart();
					if (gmtStart != null) {
						couponInfoMap.put("gmtStart", gmtStart.getTime());
					} else {
						couponInfoMap.put("gmtStart", 0);
					}
					Date gmtEnd = afCouponDo.getGmtEnd();
					if (gmtEnd != null) {
						couponInfoMap.put("gmtEnd", gmtEnd.getTime());
					} else {
						couponInfoMap.put("gmtEnd", 0);
					}
					couponList.add(couponInfoMap);
				}
			} else if (couponCategory.getType().equals(1)) {
				// 菠萝蜜
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				for (int i = 0; i < couponsArray.size(); i++) {
					HashMap<String, Object> couponInfoMap = new HashMap<String, Object>();
					String couponId = (String) couponsArray.getString(i);

					AfResourceDo afResourceDo = afResourceService.getOpenBoluomeCouponById(Long.parseLong(couponId));
					if (afResourceDo != null) {
						List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil
								.getActivityCouponList(afResourceDo.getValue());

						for (BrandActivityCouponResponseBo brandActivityCouponResponseBo : activityCouponList) {
							if (brandActivityCouponResponseBo.getType().equals(1)) {
								couponInfoMap.put("type", "FULLVOUCHER");
							} else if (brandActivityCouponResponseBo.getType().equals(2)) {
								couponInfoMap.put("type", "DISCOUNT");
							}
							couponInfoMap.put("couponType", 1);
							couponInfoMap.put("shopUrl", couponCategory.getUrl());
							couponInfoMap.put("couponId", couponId);
							couponInfoMap.put("name", brandActivityCouponResponseBo.getName());
							couponInfoMap.put("amount", brandActivityCouponResponseBo.getValue());
							couponInfoMap.put("limitAmount", brandActivityCouponResponseBo.getThreshold());
							couponInfoMap.put("drawStatus", "N");
							if (isLogin) {
								boolean flag = boluomeUtil.isHasCoupon(couponId + "", context.getUserName());
								if (flag) {
									couponInfoMap.put("drawStatus", "Y");
								}
							}
							try {

								Date gmtStart = dateFormat.parse((afResourceDo.getValue1()));
								if (gmtStart != null) {
									couponInfoMap.put("gmtStart", gmtStart.getTime());
								} else {
									couponInfoMap.put("gmtStart", 0);
								}
								Date gmtEnd = dateFormat.parse((afResourceDo.getValue2()));
								if (gmtEnd != null) {
									couponInfoMap.put("gmtEnd", gmtEnd.getTime());
								} else {
									couponInfoMap.put("gmtEnd", 0);
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.info("get boluome time error", e);
							}
						}
						couponList.add(couponInfoMap);
					}
				}
			}
		} catch (Exception e) {
			logger.error("activityCouponInfo error", e);
			resp = H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString());
			return resp.toString();
		} finally {
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis(),
					context.getUserName());
		}

		jsonObj.put("couponInfoList", couponList);
		resp = H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", jsonObj);
		return resp.toString();
	}

	/**
	 * 领取优惠券
	 * @param request
	 * @param model
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "pickActivityCoupon", method= RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickActivityCoupon(HttpServletRequest request, ModelMap model){
		doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		String key = "";
		try {
			Map<String, Object> data = Maps.newHashMap();

			FanbeiWebContext context = doWebCheck(request, false);
			AfUserDo afUserDo = afUserService.getUserByUserName(context.getUserName());
			String couponId = ObjectUtils.toString(request.getParameter("couponId"), "").toString();
			if (StringUtils.isEmpty(couponId)) {
				throw new IllegalArgumentException("couponId can't be null");
			}

			AfCouponDo couponDo = afCouponService.getCouponById(NumberUtil.objToLongDefault(couponId, 1l));
			if (couponDo == null) {
				data.put("status", CouponWebFailStatus.CouponNotExist.getCode());
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR.getDesc(),
						"", data).toString();
			}

//			if(new Date().before(couponDo.getGmtStart())){
//				data.put("status", CouponWebFailStatus.COUPONCONTEXT4.getCode());
//				return H5CommonResponse.getNewInstance(false, "活动暂未开始", null, data).toString();
//			}
//			if(new Date().after(couponDo.getGmtEnd())){
//				data.put("status", CouponWebFailStatus.COUPONCONTEXT8.getCode());
//				return H5CommonResponse.getNewInstance(false, "活动已结束", null, data).toString();
//			}

			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("status", CouponWebFailStatus.UserNotexist.getCode());
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(),
						notifyUrl, data).toString();
			}

			key = Constants.CACHKEY_GET_COUPON_LOCK + ":" + afUserDo.getRid() + ":" + couponId;
			boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 10);
			if(isNotLock){
				Integer limitCount = couponDo.getLimitCount();
				Integer myCount = afUserCouponService.getUserCouponByUserIdAndCouponId(afUserDo.getRid(),
						NumberUtil.objToLongDefault(couponId, 1l));
				if (limitCount <= myCount) {
					data.put("status", CouponWebFailStatus.COUPONCONTEXT5.getCode());

					return H5CommonResponse.getNewInstance(false,
							FanbeiExceptionCode.USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR.getDesc(), "", data)
							.toString();
				}
				Long totalCount = couponDo.getQuota();
				if (totalCount != -1 && totalCount != 0 && totalCount <= couponDo.getQuotaAlready()) {
					data.put("status", CouponWebFailStatus.COUPONCONTEXT3.getCode());

					return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR.getDesc(),
							"", data).toString();
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
				afUserCouponService.addUserCoupon(userCoupon);
				AfCouponDo couponDoT = new AfCouponDo();
				couponDoT.setRid(couponDo.getRid());
				couponDoT.setQuotaAlready(1);
				afCouponService.updateCouponquotaAlreadyById(couponDoT);

				data.put("couponCondititon", couponDo.getLimitAmount());
				data.put("status", CouponWebFailStatus.COUPONCONTEXT7.getCode());
				logger.info("pick coupon success", couponDoT);
				return H5CommonResponse.getNewInstance(true, "领取成功", "", data).toString();
			}
			else{
				data.put("status", CouponWebFailStatus.COUPONCONTEXT6.getCode());
				return H5CommonResponse.getNewInstance(false, "正在领取中，请稍后", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("pick coupon error", e);
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}
		finally {
			bizCacheUtil.delCache(key);
		}
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
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
		return null;
	}

}