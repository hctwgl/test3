/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfSearchGoodsVo;
import com.taobao.api.ApiException;
import com.taobao.api.domain.NTbkItem;

/**
 * 
 * @类描述：搜呗搜索商品
 * @author xiaotianjian 2017年2月9日上午9:47:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("searchGoodsOfSearchBayApi")
public class SearchGoodsOfSearchBayApi implements ApiHandle {

	@Resource
	TaobaoApiUtil taobaoApiUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> buildParams = checkAndbuildParam(requestDataVo);
		try {
			List<NTbkItem> list = taobaoApiUtil.executeTaobaokeSearch(buildParams).getResults();
			if (CollectionUtils.isNotEmpty(list)) {
				List<AfSearchGoodsVo> result = CollectionConverterUtil.convertToListFromList(list, new Converter<NTbkItem, AfSearchGoodsVo>() {
					@Override
					public AfSearchGoodsVo convert(NTbkItem source) {
						return parseToVo(source);
					}
				});
				resp.addResponseData("goodsList", result);
				resp.addResponseData("pageNo", buildParams.get("pageNo"));
			}
		} catch (ApiException e) {
			return new ApiHandleResponse("searchGoods failed", FanbeiExceptionCode.FAILED);
		}
		return resp;
	}
	
	private AfSearchGoodsVo parseToVo(NTbkItem item) {
		AfSearchGoodsVo vo = new AfSearchGoodsVo();
		vo.setNumIid(item.getNumIid());
		vo.setPictUrl(item.getPictUrl());
		vo.setRebateStart("0.1");
		vo.setReservePrice("20.00");
		vo.setTitle(item.getTitle());
		vo.setVolume(item.getVolume());
		return vo;
	}
	
	private Map<String, Object> checkAndbuildParam(RequestDataVo requestDataVo) {
		Map<String, Object> buildParams = new HashMap<String, Object>();
		Map<String, Object> params = requestDataVo.getParams();
		String q = ObjectUtils.toString(params.get("q"), null);
		String sort = ObjectUtils.toString(params.get("sort"), null);
		Long startPrice = NumberUtil.objToLongDefault(params.get("startPrice"), null);
		Long endPrice = NumberUtil.objToLongDefault(params.get("endPrice"), null);
		Boolean isTmall =  NumberUtil.objToBooleanDefault(params.get("isTmall"), null);
		Long pageNo = NumberUtil.objToPageLongDefault(params.get("pageNo"), 1L);
		
		if (q == null) {
			throw new FanbeiException("q can't be empty", FanbeiExceptionCode.PARAM_ERROR);
		}
		buildParams.put("q", q);
		buildParams.put("pageNo", pageNo);
		if (sort != null) {
			buildParams.put("sort", sort);
		}
		if (startPrice != null) {
			buildParams.put("startPrice", startPrice);
		}
		if (endPrice != null) {
			buildParams.put("endPrice", endPrice);
		}
		if (isTmall != null) {
			buildParams.put("isTmall", isTmall);
		}
		return buildParams;
		
	}


}
