package com.ald.fanbei.api.web.h5.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsCategoryService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsUserService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.SpringFestivalActivityEnum;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsBuffer;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.GoodsForDate;
import com.ald.fanbei.api.dal.domain.dto.SecondKillDateVo;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
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
@RequestMapping(value = "/H5DoubleEggs", produces = "application/json;charset=UTF-8")
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
	AfGoodsCategoryService afGoodsCategoryService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfActivityService afActivityService;

	/**
	 * @Title: initCoupons
	 * @Description: 优惠券初始化
	 * @return String
	 * @author chenqiwei
	 * @data 2017年12月7日
	 */
	@RequestMapping(value = "/initCoupons", method = RequestMethod.POST)
	public String couponHomePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		String result = "";

		try {

			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));

			if (activityId == null) {
				return H5CommonResponse.getNewInstance(false, "参数会场id获取失败").toString();
			}

			// 未登录初始化数据
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);

			AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
			String coupons = couponCategory.getCoupons();
			if (StringUtil.isBlank(coupons)) {
				return H5CommonResponse.getNewInstance(false, "改会场没有优惠券").toString();
			}
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

					// new way to get Field isShow

					AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY",
							"START_END_TIME");
					if (afResourceDo == null) {
						return H5CommonResponse.getNewInstance(false, "获取活动时间失败").toString();
					}

					String startTime = afResourceDo.getValue();
					String endTime = afResourceDo.getValue1();

					if (currentTime.before(dateFormat.parse(startTime))) {
						afCouponDouble12Vo.setIsShow("N");// 活动未开始
					}

					if (currentTime.after(dateFormat.parse(endTime))) {
						afCouponDouble12Vo.setIsShow("E");// 活动已经结束
					}

					String tenMinute = startTime.split(" ")[1];
					String currentHourMinute = DateUtil.convertDateToString(DateUtil.SHORT_MATCH_PATTERN, currentTime);

					if (currentHourMinute.compareTo(tenMinute) < 0) {
						afCouponDouble12Vo.setIsShow("N");// 活动未开始
					} else {
						afCouponDouble12Vo.setIsShow("Y");// 在活动时间内
					}

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

	@RequestMapping(value = "/getDateList", method = RequestMethod.POST)
	public String getDateList(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {

			// get tag from activityId then get goods from different tag
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			if (activityId == null) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			// find the name from activityId
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);
			if (StringUtil.isBlank(tag)) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			// get dateList start from the config of specific activity
			List<Date> dateListe = afActivityService.getDateListByName(tag);
			List<SecondKillDateVo> dateList = new ArrayList<SecondKillDateVo>();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if(CollectionUtil.isNotEmpty(dateListe)){
				for(Date date:dateListe){
					String dateStr = sdf.format(date);
					SecondKillDateVo vo = new SecondKillDateVo();
					vo.setStartDate(dateStr);
					vo.setStartTime(date);
					
					
					Date temNow = DateUtil.formatDateToYYYYMMdd(new Date());
					if (DateUtil.afterDay(date, temNow)) {
						vo.setStatus(0);
					}else if (DateUtil.beforeDay(date, temNow)){
						vo.setStatus(2);
					}else {
						vo.setStatus(1);
					}
					
					dateList.add(vo);
					
					
				}
			}

			java.util.Map<String, Object> data = new HashMap<>();

			data.put("dateList", dateList);
			data.put("serviceDate", new Date());

			return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();

		} catch (

		Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

	@RequestMapping(value = "/getSecondKillGoodsList", method = RequestMethod.POST)
	public String getSecondKillGoodsList(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {

			// get tag from activityId then get goods from different tag
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			if (activityId == null) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			// find the name from activityId
			String tag = SpringFestivalActivityEnum.findTagByActivityId(activityId);
			if (StringUtil.isBlank(tag)) {
				return H5CommonResponse.getNewInstance(false, "没有配置此分会场！").toString();
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			String paramDate = request.getParameter("startDate");

			String log = String.format("/h5DoubleEggs/getSecondKillGoodsList parameter : activityId = %d", activityId);

			Date startDate = new Date();

			if (StringUtil.isNotBlank(paramDate)) {
				startDate = sdf.parse(paramDate);
			}

			// get dateList start from the config of specific activity
			List<Date> dateList = afActivityService.getDateListByName(tag);
			if (CollectionUtil.isNotEmpty(dateList)) {

				log = log + String.format("middle params dateList.size() = %s", dateList.size());
				logger.info(log);

				if (startDate.before(dateList.get(0))) {
					startDate = dateList.get(0);
				}

				// AfGoodsForSecondKill afGoodsForSecondKill = new
				// AfGoodsForSecondKill();
				List<AfGoodsBuffer> goodsList = new ArrayList<>();

				// for (Date startDate : dateList) {

				List<GoodsForDate> goodsListForDate = afGoodsDoubleEggsService.getGOodsByDate(startDate, tag);
				if (CollectionUtil.isNotEmpty(goodsListForDate)) {

					AfGoodsBuffer goodsBuffer = new AfGoodsBuffer();

					log = log + String.format("goodsListForDate=%s ,startDate = %s", goodsListForDate.toString(),
							startDate);
					logger.info(log);

					if (startDate != null) {

						Date temStartDate = DateUtil.formatDateToYYYYMMdd(startDate);
						Date temNow = DateUtil.formatDateToYYYYMMdd(new Date());
						if (DateUtil.afterDay(temStartDate, temNow)) {
							goodsBuffer.setStatus(0);
						} else if (DateUtil.beforeDay(temStartDate, temNow)) {
							goodsBuffer.setStatus(2);
						} else {
							goodsBuffer.setStatus(1);
						}

						// format to the fix form

						goodsBuffer.setStartDate(sdf.format(startDate));
						goodsBuffer.setStartTime(startDate);
						goodsBuffer.setGoodsListForDate(goodsListForDate);
						goodsList.add(goodsBuffer);

					}

				}
				// }

				java.util.Map<String, Object> data = new HashMap<>();

				data.put("goodsList", goodsList);
				data.put("serviceDate", new Date());

				log = log + String.format("goodsList = %s", goodsList.toString());
				logger.info(log);

				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
				return result;

			}
			result = H5CommonResponse.getNewInstance(false, "初始化失败").toString();

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

	/**
	 * 
	 * @Title: getOnSaleGoods @author qiwei @date 2017年12月7日
	 *         下午2:04:40 @Description: 获得特卖商品（点击tap进行不同的跳转） @param
	 *         request @param response @return @return String @throws
	 */
	@RequestMapping(value = "/getOnSaleGoods", method = RequestMethod.POST)
	public String getOnSaleGoods(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();

			Long subjectId = NumberUtil.objToLong(request.getParameter("secondCategoryId"));
			if (subjectId == null) {
				return H5CommonResponse.getNewInstance(false, "参数异常", "", data).toString();
			}
			List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();

			// AfGoodsCategoryDo afGoodsCategoryDo = new AfGoodsCategoryDo();
			// afGoodsCategoryDo =
			// afGoodsCategoryService.getParentDirectoryByName("SHUANG_DAN");

			// Long primaryCategoryId = afGoodsCategoryDo.getId();
			// Long categoryId = secondCategoryId;
			// 初始化时查该parentId下的该categoryId 的商品
			// List<AfGoodsDo> afGoodsList =
			// afGoodsService.listGoodsListByPrimaryCategoryIdAndCategoryId(primaryCategoryId,categoryId);
			List<AfGoodsDo> afGoodsList = afGoodsService.listGoodsListBySubjectId(subjectId);
			if (afGoodsList.size() > 0) {
				// 获取借款分期配置信息
				AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
						Constants.RES_BORROW_CONSUME);
				JSONArray array = JSON.parseArray(resource.getValue());
				// 删除2分期
				if (array == null) {
					throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
				}
				// removeSecondNper(array);

				for (AfGoodsDo goodsDo : afGoodsList) {
					Map<String, Object> goodsInfo = new HashMap<String, Object>();
					goodsInfo.put("goodName", goodsDo.getName());
					goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
					goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
					goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
					goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
					goodsInfo.put("goodsId", goodsDo.getRid());
					goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
					goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
					goodsInfo.put("source", goodsDo.getSource());
					goodsInfo.put("goodsType", "0");
					goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
					// 如果是分期免息商品，则计算分期
					Long goodsId = goodsDo.getRid();
					AfSchemeGoodsDo schemeGoodsDo = null;
					try {
						schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
					} catch (Exception e) {
						logger.error(e.toString());
					}
					JSONArray interestFreeArray = null;
					if (schemeGoodsDo != null) {
						AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService
								.getById(schemeGoodsDo.getInterestFreeId());
						String interestFreeJson = interestFreeRulesDo.getRuleJson();
						if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
							interestFreeArray = JSON.parseArray(interestFreeJson);
						}
					}
					List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray,
							BigDecimal.ONE.intValue(), goodsDo.getSaleAmount(), resource.getValue1(),
							resource.getValue2(),goodsId,"0");
					if (nperList != null) {
						goodsInfo.put("goodsType", "1");
						Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
						String isFree = (String) nperMap.get("isFree");
						if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
							nperMap.put("freeAmount", nperMap.get("amount"));
						}
						goodsInfo.put("nperMap", nperMap);
					}

					goodsList.add(goodsInfo);
				}

			}
			data.put("goodsList", goodsList);
			result = H5CommonResponse.getNewInstance(true, "获取特卖商品成功", "", data).toString();
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "获取特卖商品失败", "", exception.getMessage()).toString();
			logger.error("获取特卖商品失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

}
