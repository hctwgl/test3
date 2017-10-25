package com.ald.fanbei.api.web.apph5.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsReservationService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponWebFailStatus;
import com.ald.fanbei.api.common.enums.GoodsReservationWebFailStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述:iphone8-预约
 * @author fanmanfu 创建时间：2017年9月8日 上午10:49:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/activity")
public class H5ReservationActivityController extends BaseController {

	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfGoodsService afGoodsService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private AfUserService afUserService;
	@Resource
	private SmsUtil smsUtil;
	@Resource
	private AfGoodsReservationService afGoodsReservationService;
	@Resource
	private AfOrderService orderService;
	@Resource
	private AfUserCouponService afUserCouponService;
	@Resource
	private AfCouponService afCouponService;

	
	@RequestMapping(value = "/getReservationCoupons", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getReservationCoupons(RequestDataVo requestDataVo, HttpServletRequest request, Model model) {
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(AfResourceType.ReservationActivity.getCode(), AfResourceType.Iphone8ReservationActivity.getCode());
		String dateStamp = DateUtil.formatDate(new Date(), "MM-dd");
		String couponAmount = ObjectUtils.toString(request.getParameter("couponAmount"), "").toString();
		Map<String, Object> json = (Map<String, Object>) JSONObject.parse(resource.getValue3());
		Long couponId1 = Long.parseLong(StringUtil.null2Str(json.get("couponId1")));
		Long couponId2 = Long.parseLong(StringUtil.null2Str(json.get("couponId2")));
		Long couponId3 = Long.parseLong(StringUtil.null2Str(json.get("couponId3")));
		Integer couponCount = Integer.parseInt(StringUtil.null2Str(json.get("couponCount")));
		
		//删除缓存
		//String s=(String) bizCacheUtil.getObject(Constants.RESERVATION_IPHONEX_COUPON_STATUS + couponAmount + "_" + dateStamp);
		//bizCacheUtil.delCache(Constants.RESERVATION_IPHONEX_RESERVATION_COUNT);
		
		AfCouponDo couponDo = null;
		// 获取用户登录信息
		AfUserDo userDo = null;
		try {
			String appInfo = getAppInfo(request.getHeader("Referer"));
			String userName = StringUtil.null2Str(JSON.parseObject(appInfo).get("userName"));
			userDo = afUserService.getUserByUserName(userName);
			model.addAttribute("userName", userName);
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, JSON.toJSONString(model)));
			//userDo = afUserService.getUserByUserName("18557515753");
		} catch (Exception e) {
			logger.info("userDo is fail" + e);
		}
		if (userDo == null) {
			return H5CommonResponse.getNewInstance(false, GoodsReservationWebFailStatus.UserNotexist.getName()).toString();
		}
		// 判断优惠券是否发完
		String couponStatus = bizCacheUtil.getObject(Constants.RESERVATION_IPHONEX_COUPON_STATUS + couponAmount + "_" + dateStamp) == null ? "Y" : bizCacheUtil.getObject(Constants.RESERVATION_IPHONEX_COUPON_STATUS + couponAmount + "_" + dateStamp).toString();
		if (couponStatus.equals("N")) {
			// 优惠券已发完；
			return H5CommonResponse.getNewInstance(false, CouponWebFailStatus.COUPONCONTEXT3.getName()).toString();
		}
		if (couponAmount.equals("80")) {
			List<AfUserCouponDto> list1 = afUserCouponService.getUserCouponListByUserIdAndCouponId(userDo.getRid(), couponId1);
			if (list1.size() > 0 && list1 != null) {
				return H5CommonResponse.getNewInstance(false, CouponWebFailStatus.COUPONCONTEXT1.getName()).toString();
			}

			couponDo = afCouponService.getCoupon(couponId1);
			if (couponDo.getQuota() > 0) {
				sendCoupons(userDo, couponDo, couponCount, Constants.RESERVATION_IPHONEX_COUPON1_COUNT + couponAmount + "_" + dateStamp, Constants.RESERVATION_IPHONEX_COUPON_STATUS + couponAmount + "_" + dateStamp, Constants.RESERVATION_IPHONEX_SUM_COUNT + dateStamp);
				return H5CommonResponse.getNewInstance(true, CouponWebFailStatus.COUPONCONTEXT2.getName()).toString();
			} else {
				return H5CommonResponse.getNewInstance(false, CouponWebFailStatus.COUPONCONTEXT3.getName()).toString();
			}
		} else if (couponAmount.equals("100")) {
			List<AfUserCouponDto> list2 = afUserCouponService.getUserCouponListByUserIdAndCouponId(userDo.getRid(), couponId2);
			if (list2.size() > 0 && list2 != null) {
				return H5CommonResponse.getNewInstance(false, CouponWebFailStatus.COUPONCONTEXT1.getName()).toString();
			}

			couponDo = afCouponService.getCoupon(couponId2);
			if (couponDo.getQuota() > 0) {
				sendCoupons(userDo, couponDo, couponCount, Constants.RESERVATION_IPHONEX_COUPON2_COUNT + couponAmount + "_" + dateStamp, Constants.RESERVATION_IPHONEX_COUPON_STATUS + couponAmount + "_" + dateStamp, Constants.RESERVATION_IPHONEX_SUM_COUNT + dateStamp);
				return H5CommonResponse.getNewInstance(true, CouponWebFailStatus.COUPONCONTEXT2.getName()).toString();
			} else {
				return H5CommonResponse.getNewInstance(false, CouponWebFailStatus.COUPONCONTEXT3.getName()).toString();
			}
		} else if (couponAmount.equals("150")) {
			List<AfUserCouponDto> list3 = afUserCouponService.getUserCouponListByUserIdAndCouponId(userDo.getRid(), couponId3);
			if (list3.size() > 0 && list3 != null) {
				return H5CommonResponse.getNewInstance(false, CouponWebFailStatus.COUPONCONTEXT1.getName()).toString();
			}

			couponDo = afCouponService.getCoupon(couponId3);
			if (couponDo.getQuota() > 0) {
				sendCoupons(userDo, couponDo, couponCount, Constants.RESERVATION_IPHONEX_COUPON3_COUNT + couponAmount + "_" + dateStamp, Constants.RESERVATION_IPHONEX_COUPON_STATUS + couponAmount + "_" + dateStamp, Constants.RESERVATION_IPHONEX_SUM_COUNT + dateStamp);
				return H5CommonResponse.getNewInstance(true, CouponWebFailStatus.COUPONCONTEXT2.getName()).toString();
			} else {
				return H5CommonResponse.getNewInstance(false, CouponWebFailStatus.COUPONCONTEXT3.getName()).toString();
			}
		} else {
			return H5CommonResponse.getNewInstance(true, CouponWebFailStatus.COUPONCONTEXT4.getName()).toString();
		}
	}

	private void sendCoupons(AfUserDo userDo, AfCouponDo couponDo, Integer couponCount, String Key1, String key2, String key3) {
		couponDo.setQuota(1l);
		couponDo.setQuotaAlready(1);
		if (afCouponService.updateCouponquotaById(couponDo) > 0)
			try {
				AfUserCouponDo userCoupon = new AfUserCouponDo();
				userCoupon.setCouponId(couponDo.getRid());
				userCoupon.setGmtCreate(new Date());
				userCoupon.setGmtStart(couponDo.getGmtStart());
				userCoupon.setGmtEnd(couponDo.getGmtEnd());
				userCoupon.setUserId(userDo.getRid());
				userCoupon.setStatus("NOUSE");
				userCoupon.setSourceType(CouponSenceRuleType.RESERVATION.getCode());
				if (afUserCouponService.addUserCoupon(userCoupon) > 0) {
					logger.info("addUserCoupon is SUCCESS, userId=" + userDo.getRid());
					long incr = bizCacheUtil.incr(Key1);
					// 设置当日优惠券已领完状态
					if (incr >= couponCount) {
						bizCacheUtil.saveObject(key2, "N", 60 * 60 * 24);
					}
					
				}
			} catch (Exception e) {
				bizCacheUtil.saveObject(key3, (bizCacheUtil.getObject(key3) == null ? 0 : Integer.parseInt(bizCacheUtil.getObject(key3).toString()) - 1), 60 * 60 * 24);
				logger.error("get coupon is fail, userId：" + userDo.getRid() + ",", e);
			}
	}

	@RequestMapping(value = "/getActivityGood", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getActivityGood(HttpServletRequest request, Model model) {

		doMaidianLog(request, H5CommonResponse.getNewInstance(true, JSON.toJSONString(model)));
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(AfResourceType.ReservationActivity.getCode(), AfResourceType.Iphone8ReservationActivity.getCode());
		Map<String, Object> jsonObjRes = (Map<String, Object>) JSONObject.parse(resource.getValue3());
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startTime = null;
		Date endTime = null;
		Integer count = null;
		String getStatus1 = "N";
		String getStatus2 = "N";
		String getStatus3 = "N";
		Long couponId1 = Long.parseLong(StringUtil.null2Str(jsonObjRes.get("couponId1")));
		Long couponId2 = Long.parseLong(StringUtil.null2Str(jsonObjRes.get("couponId2")));
		Long couponId3 = Long.parseLong(StringUtil.null2Str(jsonObjRes.get("couponId3")));
		try {
			startTime = parser.parse(StringUtil.null2Str(jsonObjRes.get("startTime")));
			endTime = parser.parse(StringUtil.null2Str(jsonObjRes.get("endTime")));
			count = Integer.parseInt(StringUtil.null2Str(jsonObjRes.get("sumCouponCount")));
		} catch (ParseException e) {
			logger.info("get startTime is fail" + e);
		}

		Map map = new HashMap();
		Date date = new Date();
		String status = "FAIL";
		String loginStatus = "N";
		// 判断活动是否开始
		if (DateUtil.compareDate(date, startTime)) {

			// 查询预约状态
			try {
				AfUserDo userDo = null;
				String appInfo = getAppInfo(request.getHeader("Referer"));
				String userName = StringUtil.null2Str(JSON.parseObject(appInfo).get("userName"));
				userDo = afUserService.getUserByUserName(userName);
				if (userDo != null) {
					loginStatus = "Y";
					long goodsId = Long.parseLong(StringUtil.null2Str(jsonObjRes.get("goodsId")));
					List<AfOrderDo> order = orderService.getStatusByGoodsAndUserId(goodsId, userDo.getRid());
					if (order != null && order.size() > 0) {
						status = "SUCCESS";
						model.addAttribute("userName", userName);
					}
				}
				// 判断是否领过券
				List<AfUserCouponDto> list1 = afUserCouponService.getUserCouponListByUserIdAndCouponId(userDo.getRid(), couponId1);
				if (list1.size() > 0 && list1 != null) {
					getStatus1 = "Y";
				}
				List<AfUserCouponDto> list2 = afUserCouponService.getUserCouponListByUserIdAndCouponId(userDo.getRid(), couponId2);
				if (list2.size() > 0 && list2 != null) {
					getStatus2 = "Y";
				}
				List<AfUserCouponDto> list3 = afUserCouponService.getUserCouponListByUserIdAndCouponId(userDo.getRid(), couponId3);
				if (list3.size() > 0 && list3 != null) {
					getStatus3 = "Y";
				}

			} catch (Exception e) {
				logger.info("getActivityGoods is fail" + e);
			}

			// 得到预约数量
			try {
				String str = (String) bizCacheUtil.getObject(Constants.RESERVATION_IPHONEX_RESERVATION_COUNT);
				if (StringUtil.isNotBlank(str)) {
					int couponCount = Integer.parseInt(str);
					count += couponCount;
				}
			} catch (Exception e) {
				logger.info("couponCount is null" + e);
			}
			Date currentDate = new Date();
			
			map.put("currentDate", currentDate);
			map.put("getStatus80", getStatus1);
			map.put("getStatus100", getStatus2);
			map.put("getStatus150", getStatus3);
			map.put("couponCount", count);// 每天预约的数量
			map.put("status", status);// 预约状态
			map.put("loginStatus", loginStatus);// 登录状态

		}
		doMaidianLog(request, H5CommonResponse.getNewInstance(true, JSON.toJSONString(model)));
		return JsonUtil.toJSONString(map);
	}

	private static String getAppInfo(String url) {
		if (StringUtil.isBlank(url)) {
			return null;
		}
		String result = "";
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}

					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			List<String> _appInfo = params.get("_appInfo");
			if (_appInfo != null && _appInfo.size() > 0) {
				result = _appInfo.get(0);
			}
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
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
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
