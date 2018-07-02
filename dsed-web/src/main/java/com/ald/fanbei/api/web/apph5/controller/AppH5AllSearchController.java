package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.supplier.AfSearchItemService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.InterestFreeUitl;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.query.AfGoodsDoQuery;
import com.ald.fanbei.api.dal.domain.supplier.AfSolrSearchResultDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfSearchGoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	AfResourceService afResourceService;
	@Resource
	AfUserSearchService afUserSearchService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfSearchItemService afSearchItemService;

	@Resource
	AfSeckillActivityService afSeckillActivityService;
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
			context = doWebCheck(request,false);
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
			query.setPageSize(20);
			List<AfSearchGoodsVo> goodsList = new ArrayList<AfSearchGoodsVo>();
			// get selfSupport goods
			//查询分词搜索开关
			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("SOLR_SERVER_KEY", "SOLR_SERVER_KEY");
			AfSolrSearchResultDo solrSearchResult = null;
			if (StringUtils.equals(resourceDo.getValue(), "Y")) {//从solr服务器搜索
				Integer pageSize = 20;
				solrSearchResult = afSearchItemService.getSearchList(keyword,pageNo,pageSize);
				logger.info("/appH5Goods/searchGoods from solrServer with resultData = {}", solrSearchResult);
			}
			List<AfGoodsDo> orgSelfGoodlist = new ArrayList<AfGoodsDo>();
			Integer totalCount = null;
			Integer totalPage = null;
			if (solrSearchResult == null) {//从数据库搜索
				query.setPageNo(pageNo);
				orgSelfGoodlist = afGoodsService.getAvaliableSelfGoods(query);
				totalCount = query.getTotalCount();
				totalPage = query.getTotalPage();
			}else {
				List<Long> goodsIds = solrSearchResult.getGoodsIds();
				if (StringUtil.isNotBlank(sort)) {
					query.setGoodsIds(goodsIds);
					orgSelfGoodlist = afGoodsService.getAvaliableSelfGoodsForSort(query);
				}else {					
					for (int i = 0; i < goodsIds.size(); i++) {
						query.setGoodsId(goodsIds.get(i));
						AfGoodsDo goodsDo = afGoodsService.getAvaliableSelfGoodsBySolr(query);
						if(null != goodsDo){
							orgSelfGoodlist.add(goodsDo);
						}
					}
				}
				totalCount = solrSearchResult.getTotalCount();
				totalPage = solrSearchResult.getTotalPage();
			}

			logger.info("/appH5Goods/searchGoods orgSelfGoodlist.size = {}", orgSelfGoodlist.size());
			List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
			List<Long> goodsIdList = new ArrayList<>();

			for (AfGoodsDo goodsDo : orgSelfGoodlist) {
				goodsIdList.add(goodsDo.getRid());
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
			
			logger.info("/appH5Goods/searchGoods orgSelfGoodlist.size = {},goodsList = {}", orgSelfGoodlist.size(),goodsList.size());
			return H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
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
