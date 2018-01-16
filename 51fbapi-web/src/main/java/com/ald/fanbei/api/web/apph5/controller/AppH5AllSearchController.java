package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.InterestFreeUitl;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserSearchDo;
import com.ald.fanbei.api.dal.domain.query.AfGoodsDoQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfSearchGoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ald.fanbei.api.common.util.Converter;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
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

	@Resource
	AfUserService afUserService;

	@RequestMapping(value = "/searchGoods", method = RequestMethod.POST)
	public String get(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			// parameters from
			String keyword = ObjectUtils.toString(request.getParameter("keywords"), null);
			Integer pageNo = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
			String sort = ObjectUtils.toString(request.getParameter("sort"), null);

			if (StringUtils.isBlank(keyword)) {
				throw new FanbeiException("keyword can't be empty", FanbeiExceptionCode.PARAM_ERROR);
			}

			FanbeiWebContext context = new FanbeiWebContext();
			String userName = context.getUserName();

			// add history
			if (StringUtil.isNotBlank(keyword) && StringUtil.isNotBlank(userName)) {
				Long userId = convertUserNameToUserId(userName);
				if (userId != null) {
					afUserSearchService.addUserSearch(getUserSearchDo(userId, keyword));
				}
			}

			// --------------------------------------begin
			// selfSupport--------------------------------------
			AfGoodsDoQuery query = new AfGoodsDoQuery();
			query.setKeyword(keyword);
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
			query.setPageNo(pageNo);
			query.setPageSize(20);

			List<AfSearchGoodsVo> goodsList = new ArrayList<>();

			// get selfSupport goods
			List<AfGoodsDo> orgSelfGoodlist = afGoodsService.getAvaliableSelfGoods(query);
			logger.info("/appH5Goods/searchGoods orgSelfGoodlist.size = {}", orgSelfGoodlist.size());
			int totalCount = query.getTotalCount();
			int totalPage = query.getTotalPage();

			for (AfGoodsDo goodsDo : orgSelfGoodlist) {
				AfSearchGoodsVo vo = convertFromSelfToVo(goodsDo);
				goodsList.add(vo);
			}
			data.put("goodsList", goodsList);
			data.put("totalCount", totalCount);
			data.put("totalPage", totalPage);
			
			logger.info("/appH5Goods/searchGoods orgSelfGoodlist.size = {},goodsList = {}", orgSelfGoodlist.size(),goodsList.size());
			return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

	private AfSearchGoodsVo convertFromSelfToVo(AfGoodsDo goodsDo) {
		AfSearchGoodsVo goodsVo = new AfSearchGoodsVo();
		if (goodsDo != null) {
			goodsVo.setGoodsIcon(goodsDo.getGoodsIcon());
			goodsVo.setGoodsName(goodsDo.getName());
			goodsVo.setGoodsUrl(goodsDo.getGoodsUrl());
			goodsVo.setSource("SELFSUPPORT");

			goodsVo.setNperMap(getNper(goodsDo.getSaleAmount()));

			goodsVo.setNumId(goodsDo.getRid().toString());
			goodsVo.setRealAmount(goodsDo.getSaleAmount().toString());
			goodsVo.setRebateAmount(goodsDo.getRebateAmount().toString());
			goodsVo.setThumbnailIcon(goodsDo.getThumbnailIcon());

		}
		return goodsVo;
	}

	public Map<String, Object> getNper(BigDecimal saleAmount) {
		Map<String, Object> result = new HashMap<>();
		// 获取借款分期配置信息
		AfResourceDo res = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(res.getValue());

		final AfResourceDo resource = afResourceService.getSingleResourceBytype(Constants.RES_THIRD_GOODS_REBATE_RATE);
		List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, null, BigDecimal.ONE.intValue(),
				saleAmount, resource.getValue1(), resource.getValue2());
		if (nperList != null) {
			Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
			return nperMap;
		}
		return result;
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

	private AfSearchGoodsVo convertFromTaobaoToVo(NTbkItem item, BigDecimal minRate, BigDecimal maxRate) {
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(item.getZkFinalPrice(), BigDecimal.ZERO);
		BigDecimal minRebateAmount = saleAmount.multiply(minRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal maxRebateAmount = saleAmount.multiply(maxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
		AfSearchGoodsVo vo = new AfSearchGoodsVo();
		vo.setNumId(item.getNumIid() + "");
		vo.setGoodsIcon(item.getPictUrl());
		vo.setGoodsName(item.getTitle());
		vo.setGoodsUrl(item.getItemUrl());
		vo.setSource("TAOBAO");

		vo.setNperMap(getNper(new BigDecimal(item.getZkFinalPrice())));

		vo.setVolume(item.getVolume());
		vo.setRealAmount(new StringBuffer("").append(saleAmount.subtract(maxRebateAmount)).toString());
		vo.setRebateAmount(new StringBuffer("").append(maxRebateAmount).toString());
		vo.setSaleAmount(saleAmount);
		List<String> icons = item.getSmallImages();
		if (icons != null && icons.size() > 0) {
			vo.setThumbnailIcon(icons.get(0));
		} else {
			vo.setThumbnailIcon(item.getPictUrl());
		}
		return vo;
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
