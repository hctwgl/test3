/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.BrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.BrandCouponResponseBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBrandCouponVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：获取菠萝觅优惠券列表
 * @author xiaotianjian 2017年3月28日下午3:40:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBrandCouponListApi")
public class GetBrandCouponListApi implements ApiHandle {

	private static final String AVAILABLE_COUPON = "availableCoupons";
	private static final String USED_COUPON = "usedCoupons";
	private static final String EXPIRED_COUPON = "expiredCoupons";
	private static final String DATA = "data";
	private static final String NEXT_PAGE_INDEX = "nextPageIndex";
	private static final String COUPON_LIST = "couponList"; 
	
	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		
		Integer type = NumberUtil.objToIntDefault(requestDataVo.getParams().get("type"), null);
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), null);
		Integer pageSize = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageSize"), 20);
		
		if (type == null) {
			logger.error("type is empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		BrandCouponRequestBo bo = new BrandCouponRequestBo();
		bo.setUserId(context.getUserId() + StringUtils.EMPTY);
		bo.setType(type);
		bo.setPageIndex(pageNo);
		bo.setPageSize(pageSize);
		try {
			String resultString = HttpUtil.doHttpPost(ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/api/promotion/get_coupon_list", JSONObject.toJSONString(bo));
			JSONObject resultJson = JSONObject.parseObject(resultString);
			if (resultJson == null || !"0".equals(resultJson.getString("code"))) {
				resp.addResponseData(COUPON_LIST, new ArrayList<Object>());
				resp.addResponseData("nextPageNo", 0);
			} else {
				Map<String,Object> resultMap = parseBrandCoupon(resultJson, type);
				resp.addResponseData(COUPON_LIST, resultMap.get(COUPON_LIST));
				resp.addResponseData("nextPageNo", resultMap.get(NEXT_PAGE_INDEX));
			}
			resp.addResponseData("pageNo", pageNo);
			return resp;
		} catch (Exception e) {
			return new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.FAILED);
		}
	}
	
	private Map<String,Object> parseBrandCoupon(JSONObject resultJson, Integer type) {
		Map<String,Object> result = new HashMap<String,Object>();
		List<AfBrandCouponVo> voList = new ArrayList<AfBrandCouponVo>();
		Integer nextPageNo = null;
		if (type == 1) {
			JSONObject data = resultJson.getJSONObject(DATA);
			String availableCouponStr = data.getString(AVAILABLE_COUPON);
			nextPageNo = data.getInteger(NEXT_PAGE_INDEX);
			List<BrandCouponResponseBo> availabeCouponlList = JSONObject.parseArray(availableCouponStr, BrandCouponResponseBo.class);
			List<AfBrandCouponVo> availabeCouponVolList = parseBrandCouponVo(availabeCouponlList, "NOUSE");
			if (CollectionUtils.isNotEmpty(availabeCouponVolList)) {
				voList.addAll(availabeCouponVolList);
			}
		} else if (type == 2){
			JSONObject data = resultJson.getJSONObject(DATA);
			String usedCouponStr = data.getString(USED_COUPON);
			nextPageNo = data.getInteger(NEXT_PAGE_INDEX);
			List<BrandCouponResponseBo> usedCouponList = JSONObject.parseArray(usedCouponStr, BrandCouponResponseBo.class);
			List<AfBrandCouponVo> usedCouponVoList = parseBrandCouponVo(usedCouponList, "USED");
			if (CollectionUtils.isNotEmpty(usedCouponVoList)) {
				voList.addAll(usedCouponVoList);
			}
		} else if (type == 3) {
			JSONObject data = resultJson.getJSONObject(DATA);
			String expiredCouponStr = data.getString(EXPIRED_COUPON);
			nextPageNo = data.getInteger(NEXT_PAGE_INDEX);
			List<BrandCouponResponseBo> expiredCouponList = JSONObject.parseArray(expiredCouponStr, BrandCouponResponseBo.class);
			List<AfBrandCouponVo> expiredCouponVoList = parseBrandCouponVo(expiredCouponList, "EXPIRE");
			if (CollectionUtils.isNotEmpty(expiredCouponVoList)) {
				voList.addAll(expiredCouponVoList);
			}
		} else if (type == 4) {
			JSONObject data = resultJson.getJSONObject(DATA);
			String availableCouponStr = data.getString(AVAILABLE_COUPON);
			String usedCouponStr = data.getString(USED_COUPON);
			String expiredCouponStr = data.getString(EXPIRED_COUPON);
			nextPageNo = data.getInteger(NEXT_PAGE_INDEX);
			List<BrandCouponResponseBo> availabeCouponlList = JSONObject.parseArray(availableCouponStr, BrandCouponResponseBo.class);
			List<AfBrandCouponVo> availabeCouponVolList = parseBrandCouponVo(availabeCouponlList, "NOUSE");
			
			if (CollectionUtils.isNotEmpty(availabeCouponVolList)) {
				voList.addAll(availabeCouponVolList);
			}
			List<BrandCouponResponseBo> usedCouponList = JSONObject.parseArray(usedCouponStr, BrandCouponResponseBo.class);
			List<AfBrandCouponVo> usedCouponVoList = parseBrandCouponVo(usedCouponList, "USED");
			if (CollectionUtils.isNotEmpty(usedCouponVoList)) {
				voList.addAll(usedCouponVoList);
			}
			
			List<BrandCouponResponseBo> expiredCouponList = JSONObject.parseArray(expiredCouponStr, BrandCouponResponseBo.class);
			List<AfBrandCouponVo> expiredCouponVoList = parseBrandCouponVo(expiredCouponList, "EXPIRE");
			if (CollectionUtils.isNotEmpty(expiredCouponVoList)) {
				voList.addAll(expiredCouponVoList);
			}
		}
		result.put(COUPON_LIST, voList);
		result.put(NEXT_PAGE_INDEX, nextPageNo);
		return result;
	}
	
	private List<AfBrandCouponVo> parseBrandCouponVo(List<BrandCouponResponseBo> boList, final String status) {
		if (CollectionUtils.isEmpty(boList)) {
			return null;
		}
		return CollectionConverterUtil.convertToListFromList(boList, new Converter<BrandCouponResponseBo, AfBrandCouponVo>() {
			@Override
			public AfBrandCouponVo convert(BrandCouponResponseBo source) {
				return parseDoToVo(source, status);
			}
		});
		
		
	}
	
	private AfBrandCouponVo parseDoToVo(BrandCouponResponseBo bo, String status) {
		AfBrandCouponVo vo = new AfBrandCouponVo();
		vo.setName(bo.getTitle());
		int type = bo.getType();
		
		if (type == 2 || type == 4) {
			vo.setMaxAmount(bo.getValue());
			vo.setAmount(bo.getDiscount());
		} else if (type == 1 || type == 3) {
			vo.setAmount(bo.getValue());
		}
		vo.setLimitAmount(bo.getThreshold());
		vo.setGmtStart(new Date(bo.getSts().longValue() * 1000));
		vo.setGmtEnd(new Date(bo.getEts().longValue() * 1000 ));
		vo.setType(bo.getType());
		vo.setStatus(status);
		return vo;
	}
	
	public static void main(String[] args) {
		System.out.println(UUID.randomUUID());
		Map<String,String> map = new HashMap<String,String>();
		map.put("userId", "68885");
		map.put("type", "1");
		map.put("pageIndex", "1");
		map.put("pageSize", "10");
		String str = HttpUtil.httpPost(ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/promotion/get_coupon_list", null);
		System.out.println(str);
	}

}
