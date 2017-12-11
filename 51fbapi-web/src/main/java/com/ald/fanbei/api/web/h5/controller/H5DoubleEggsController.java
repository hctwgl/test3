package com.ald.fanbei.api.web.h5.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsUserService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsBuffer;
import com.ald.fanbei.api.dal.domain.AfGoodsForSecondKill;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.GoodsForDate;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.vo.AfCouponDouble12Vo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * @Title: H5DoubleEggsController.java
 * @Package com.ald.fanbei.api.web.h5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月11日 下午3:54:15
 * @version V1.0
 */
@RestController
@RequestMapping(value = "/H5DoubleEggs", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
public class H5DoubleEggsController extends H5Controller {
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;

	@Resource
	AfGoodsDoubleEggsUserService afGoodsDoubleEggsUserService;
	@Resource
	AfCategoryService afCategoryService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;

	@RequestMapping(value = "/initCoupons", method = RequestMethod.POST)
	public String couponHomePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		String result = "";

		try {

			// 未登录初始化数据
			String tag = "_DOUBLE_EGGS_";
			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
			String coupons = couponCategory.getCoupons();
			JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);

			List<AfCouponDouble12Vo> couponVoList = new ArrayList<AfCouponDouble12Vo>();

			for (int i = 0; i < couponsArray.size(); i++) {
				String couponId = (String) couponsArray.getString(i);
				AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
				if (afCouponDo != null) {
					AfCouponDouble12Vo afCouponDouble12Vo = new AfCouponDouble12Vo();
					afCouponDouble12Vo.setId(afCouponDo.getRid());
					afCouponDouble12Vo.setName(afCouponDo.getName());
					afCouponDouble12Vo.setThreshold(afCouponDo.getUseRule());
					afCouponDouble12Vo.setAmount(afCouponDo.getAmount());
					afCouponDouble12Vo.setLimitAmount(afCouponDo.getLimitAmount());

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// 当前时间
					Date currentTime = new Date();

					AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("DOUBLE_EGGS",
							"COUPON_TIME");
					if (afResourceDo == null) {
						return H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
					}
					String[] times = afResourceDo.getValue3().split(",");

					if (currentTime.before(dateFormat.parse(times[0]))) {
						// 2017-12-5 10:00号之前
						afCouponDouble12Vo.setIsShow("N");// 活动未开始
					}

					if (afCouponDouble12Vo.getIsShow() == null) {
						for (int j = 0; j < times.length - 1; j = j + 2) {
							if (afCouponDouble12Vo.getIsShow() == null) {
								if (currentTime.after(dateFormat.parse(times[times.length - 1]))) {
									afCouponDouble12Vo.setIsShow("E");// 活动已结束
								}
							}
							if (afCouponDouble12Vo.getIsShow() == null) {
								if (currentTime.after(dateFormat.parse(times[j]))
										&& currentTime.before(dateFormat.parse(times[j + 1]))) {
									afCouponDouble12Vo.setIsShow("Y");// 在活动时间内
								}
							}
							if (afCouponDouble12Vo.getIsShow() == null) {
								if (currentTime.after(dateFormat.parse(times[j + 1]))
										&& currentTime.before(dateFormat.parse(times[j + 2]))) {
									afCouponDouble12Vo.setIsShow("N");// 活动未开始
								}
							}
						}
					}

					afCouponDouble12Vo.setIsGet("N");// 未领取

					if (afCouponDo.getQuota() > afCouponDo.getQuotaAlready()) {
						afCouponDouble12Vo.setIshas("Y");// 优惠券还有
					} else {
						afCouponDouble12Vo.setIshas("N");// 优惠券已领完
					}
					couponVoList.add(afCouponDouble12Vo);
				}
			}

			logger.info(JSON.toJSONString(couponVoList));
			data.put("couponList", couponVoList);
			result = H5CommonResponse.getNewInstance(true, "获取优惠券列表成功", null, data).toString();

		} catch (Exception e) {
			logger.error("/appH5DoubleEggs/initCoupons error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取优惠券列表失败", null, "").toString();
		}
		return result;
	}

	
	@RequestMapping(value = "/getSecondKillGoodsList")
	public String getSecondKillGoodsList(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {

			Date now = new Date() ;
			
			String log = "/appH5DoubleEggs/getSecondKillGoodsList";
			
			List<Date> dateList = afGoodsDoubleEggsService.getAvalibleDateList();
			if (CollectionUtil.isNotEmpty(dateList)) {
				
				log = log + String.format("middle params dateList.size() = %s", dateList.size());
				logger.info(log);
				
				AfGoodsForSecondKill afGoodsForSecondKill = new AfGoodsForSecondKill();
				List<AfGoodsBuffer> goodsList = new ArrayList<>();
				
				for (Date startDate : dateList) {
					
					List<GoodsForDate> goodsListForDate = afGoodsDoubleEggsService.getGOodsByDate(startDate);
					if (CollectionUtil.isNotEmpty(goodsListForDate)) {
						
						AfGoodsBuffer goodsBuffer = new AfGoodsBuffer();
						
					
						
						log = log + String.format("goodsListForDate=%s ,startDate = %s",goodsListForDate.toString() ,startDate);
						logger.info(log);
						
						goodsBuffer.setStartTime(startDate);
						goodsBuffer.setGoodsListForDate(goodsListForDate);
						
					}
				}
				
				java.util.Map<String, Object> data = new HashMap<>();
				
				log = log + String.format("goodsList = %s",goodsList.toString());
				logger.info(log);
				
				afGoodsForSecondKill.setGoodsList(goodsList);
				afGoodsForSecondKill.setServiceDate(new Date());
				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();

				
			}
			result = H5CommonResponse.getNewInstance(false, "初始化失败").toString();

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}
}
