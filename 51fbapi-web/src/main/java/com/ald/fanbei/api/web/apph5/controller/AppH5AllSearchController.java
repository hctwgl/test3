package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserSearchDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfSearchGoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.domain.NTbkItem;



/**
 * @Title: AppH5AllSearchController.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月18日 下午6:10:04
 * @version V1.0
 */
@RestController
@RequestMapping(value = "/appH5Goods", produces = "application/json;charset=UTF-8")
public class AppH5AllSearchController extends BaseController {

	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Resource
	AfResourceService afResourceService;

	@Resource
	AfUserSearchService afUserSearchService;

	@Resource
	AfGoodsService afGoodsService;

	@RequestMapping(value = "/searchGoods", method = RequestMethod.POST)
	public String get(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			//parameters from 
			
			
			
			
			
			

			result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

	private boolean isVirtualWithKey(String key, String virtualGoodsValue) {
		List<String> virtual = StringUtil.splitToList(virtualGoodsValue, ",");
		for (String string : virtual) {
			if (key.contains(string)) {
				return true;
			}
		}
		return false;

	}

	private AfSearchGoodsVo parseToVo(NTbkItem item, BigDecimal minRate, BigDecimal maxRate) {
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(item.getZkFinalPrice(), BigDecimal.ZERO);
		BigDecimal minRebateAmount = saleAmount.multiply(minRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal maxRebateAmount = saleAmount.multiply(maxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		AfSearchGoodsVo vo = new AfSearchGoodsVo();
		vo.setNumId(item.getNumIid() + "");
		vo.setGoodsIcon(item.getPictUrl());
		vo.setGoodsName(item.getTitle());
		vo.setGoodsUrl(item.getItemUrl());
		vo.setVolume(item.getVolume());
		vo.setRealAmount(new StringBuffer("").append(saleAmount.subtract(maxRebateAmount)).append("~")
				.append(saleAmount.subtract(minRebateAmount)).toString());
		vo.setRebateAmount(new StringBuffer("").append(minRebateAmount).append("~").append(maxRebateAmount).toString());
		vo.setSaleAmount(saleAmount);
		List<String> icons = item.getSmallImages();
		if (icons != null && icons.size() > 0) {
			vo.setThumbnailIcon(icons.get(0));
		} else {
			vo.setThumbnailIcon(item.getPictUrl());
		}
		return vo;
	}

	private Map<String, Object> checkAndbuildParam(RequestDataVo requestDataVo) {
		Map<String, Object> buildParams = new HashMap<String, Object>();
		Map<String, Object> params = requestDataVo.getParams();
		String q = ObjectUtils.toString(params.get("keywords"), null);
		String sort = ObjectUtils.toString(params.get("sort"), null);
		Long minAmount = NumberUtil.objToLongDefault(params.get("minAmount"), null);
		Long maxAmount = NumberUtil.objToLongDefault(params.get("maxAmount"), null);
		Boolean isTmall = NumberUtil.objToBooleanDefault(params.get("onlyTmall"), null);
		Long pageNo = NumberUtil.objToPageLongDefault(params.get("pageNo"), 1L);

		if (q == null) {
			throw new FanbeiException("q can't be empty", FanbeiExceptionCode.PARAM_ERROR);
		}
		buildParams.put("q", q);
		buildParams.put("pageNo", pageNo);
		if (sort != null) {
			buildParams.put("sort", sort);
		}
		if (minAmount != null) {
			buildParams.put("startPrice", minAmount);
		}
		if (maxAmount != null) {
			buildParams.put("endPrice", maxAmount);
		}
		if (isTmall != null) {
			buildParams.put("isTmall", isTmall);
		}
		return buildParams;

	}

	private AfUserSearchDo getUserSearchDo(Long userId, String keyword) {
		AfUserSearchDo search = new AfUserSearchDo();
		search.setKeyword(keyword);
		search.setUserId(userId);
		return search;
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

}
