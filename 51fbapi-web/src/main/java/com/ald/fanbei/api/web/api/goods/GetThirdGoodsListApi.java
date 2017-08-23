/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserSearchDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfSearchGoodsVo;
import com.taobao.api.ApiException;
import com.taobao.api.domain.NTbkItem;

/**
 * 
 * @类描述：搜呗搜索商品
 * @author hexin 2017年2月18日上午11:00:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getThirdGoodsListApi")
public class GetThirdGoodsListApi implements ApiHandle {

	@Resource
	TaobaoApiUtil taobaoApiUtil;
	
	@Resource
	private AfResourceService afResourceService;
	
	@Resource
	private AfUserSearchService afUserSearchService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> buildParams = checkAndbuildParam(requestDataVo);
		try {
			List<NTbkItem> list = taobaoApiUtil.executeTaobaokeSearch(buildParams).getResults();
			String keyword = ObjectUtils.toString(requestDataVo.getParams().get("keywords"), null);
			if(StringUtil.isNotBlank(keyword) && null != context.getUserId()){
				afUserSearchService.addUserSearch(getUserSearchDo(context.getUserId(), keyword));
			}
			final AfResourceDo virtualGoods = afResourceService.getSingleResourceBytype(AfResourceType.VirtualGoodsKeywords.getCode());
			
			
			
			final AfResourceDo resource = afResourceService.getSingleResourceBytype(Constants.RES_THIRD_GOODS_REBATE_RATE);

			
			if (CollectionUtils.isNotEmpty(list)) {
				
				
				List<AfSearchGoodsVo> result = CollectionConverterUtil.convertToListFromList(list, new Converter<NTbkItem, AfSearchGoodsVo>() {
					@Override
					public AfSearchGoodsVo convert(NTbkItem source) {
						System.out.println(source.getTitle());
						if(virtualGoods !=null&&isVirtualWithKey(source.getTitle() ,virtualGoods.getValue())){
							return null;
						}
						if(null == resource){
							return parseToVo(source,BigDecimal.ZERO,BigDecimal.ZERO);
						}else{
							return parseToVo(source,NumberUtil.objToBigDecimalDefault(resource.getValue(), BigDecimal.ZERO),
									NumberUtil.objToBigDecimalDefault(resource.getValue1(), BigDecimal.ZERO));
						}
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
	private boolean isVirtualWithKey(String key, String virtualGoodsValue){
		List<String> virtual = StringUtil.splitToList(virtualGoodsValue,",") ;
		for (String string : virtual) {
			if(key.contains(string)){
				return true;
			}
		}
		return false;
		
	}
	private AfSearchGoodsVo parseToVo(NTbkItem item,BigDecimal minRate,BigDecimal maxRate) {
		BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(item.getZkFinalPrice(), BigDecimal.ZERO);
		BigDecimal minRebateAmount = saleAmount.multiply(minRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		BigDecimal maxRebateAmount = saleAmount.multiply(maxRate).setScale(2,BigDecimal.ROUND_HALF_UP);
		AfSearchGoodsVo vo = new AfSearchGoodsVo();
		vo.setNumId(item.getNumIid()+"");
		vo.setGoodsIcon(item.getPictUrl());
		vo.setGoodsName(item.getTitle());
		vo.setGoodsUrl(item.getItemUrl());
		vo.setRealAmount(new StringBuffer("").append(saleAmount.subtract(maxRebateAmount)).append("~").append(saleAmount.subtract(minRebateAmount)).toString());
		vo.setRebateAmount(new StringBuffer("").append(minRebateAmount).append("~").append(maxRebateAmount).toString());
		vo.setSaleAmount(saleAmount);
		List<String> icons = item.getSmallImages();
		if(icons!=null && icons.size()>0){
			vo.setThumbnailIcon(icons.get(0));
		}else{
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
		Boolean isTmall =  NumberUtil.objToBooleanDefault(params.get("onlyTmall"), null);
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

	private AfUserSearchDo getUserSearchDo(Long userId,String keyword){
		AfUserSearchDo search = new AfUserSearchDo();
		search.setKeyword(keyword);
		search.setUserId(userId);
		return search;
	}
}
