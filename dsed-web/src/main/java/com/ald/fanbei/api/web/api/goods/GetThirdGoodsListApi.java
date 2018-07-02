/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
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
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfSearchGoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    @Resource
    private AfGoodsService afGoodsService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	Map<String, Object> buildParams = checkAndbuildParam(requestDataVo);
	try {
	    List<NTbkItem> list = taobaoApiUtil.executeTaobaokeSearch(buildParams).getResults();
	    String keyword = ObjectUtils.toString(requestDataVo.getParams().get("keywords"), null);
	    if (StringUtil.isNotBlank(keyword) && null != context.getUserId()) {
		afUserSearchService.addUserSearch(getUserSearchDo(context.getUserId(), keyword));
	    }
	    final AfResourceDo virtualGoods = afResourceService.getSingleResourceBytype(AfResourceType.VirtualGoodsKeywords.getCode());

	    final AfResourceDo resource = afResourceService.getSingleResourceBytype(Constants.RES_THIRD_GOODS_REBATE_RATE);


	    if (CollectionUtils.isNotEmpty(list)) {

		List<AfSearchGoodsVo> result = CollectionConverterUtil.convertToListFromList(list, new Converter<NTbkItem, AfSearchGoodsVo>() {
		    @Override
		    public AfSearchGoodsVo convert(NTbkItem source) {
			// System.out.println(source.getTitle());
			if (virtualGoods != null && isVirtualWithKey(source.getTitle(), virtualGoods.getValue())) {
			    return null;
			}
			if (null == resource) {
			    return parseToVo(source, BigDecimal.ZERO, BigDecimal.ZERO);
			} else {
			    return parseToVo(source, NumberUtil.objToBigDecimalDefault(resource.getValue(), BigDecimal.ZERO), NumberUtil.objToBigDecimalDefault(resource.getValue1(), BigDecimal.ZERO));
			}
		    }
		});

		// 判断是否有自建商品存在，如果有，替换数据
		if (CollectionUtils.isNotEmpty(result)) {

		    for (AfSearchGoodsVo afSearchGoodsVo : result) {
			AfGoodsDo query = afGoodsService.checkIsSelfBuild(afSearchGoodsVo.getNumId());
			if (query != null && StringUtil.isNotEmpty(query.getNumId())) {
			    afSearchGoodsVo.setGoodsIcon(query.getGoodsIcon());
			    afSearchGoodsVo.setGoodsName(query.getName());
			    afSearchGoodsVo.setGoodsUrl(query.getGoodsUrl());
			    afSearchGoodsVo.setRealAmount(query.getSaleAmount().toString());
			    afSearchGoodsVo.setRebateAmount(query.getRebateAmount().toString());
			    afSearchGoodsVo.setSaleAmount(query.getPriceAmount());
			    afSearchGoodsVo.setThumbnailIcon(query.getThumbnailIcon());
			}

			// 获取借款分期配置信息
			AfResourceDo res = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
			JSONArray array = JSON.parseArray(res.getValue());
			// 删除2分期
			if (array == null) {
			    throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
			}
			removeSecondNper(array);

			for (AfSearchGoodsVo goodsInfo : result) {
			    List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, null, BigDecimal.ONE.intValue(), goodsInfo.getSaleAmount(), resource.getValue1(), resource.getValue2(),0l,"0");
			    if (nperList != null) {
				Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
				goodsInfo.setNperMap(nperMap);
			    }
			}

			// 爬取商品开关
			AfResourceDo isWorm = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE, Constants.THIRD_GOODS_IS_WORM_SECTYPE);
			if (null != isWorm) {
			    resp.addResponseData("isWorm", isWorm.getValue());
			} else {
			    resp.addResponseData("isWorm", 0);
			}

			resp.addResponseData("goodsList", result);
			resp.addResponseData("pageNo", buildParams.get("pageNo"));
		    }
		}
	    }
	} catch (ApiException e) {
	    return new ApiHandleResponse("searchGoods failed", FanbeiExceptionCode.FAILED);
	}
	return resp;
    }

    private void removeSecondNper(JSONArray array) {
	if (array == null) {
	    return;
	}
//	Iterator<Object> it = array.iterator();
//	while (it.hasNext()) {
//	    JSONObject json = (JSONObject) it.next();
//	    if (json.getString(Constants.DEFAULT_NPER).equals("2")) {//mark
//		it.remove();
//		break;
//	    }
//	}

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
	BigDecimal saleAmount = NumberUtil.objToBigDecimalDefault(item.getZkFinalPrice(),  BigDecimal.ZERO);
	BigDecimal minRebateAmount = saleAmount.multiply(minRate).setScale(2, BigDecimal.ROUND_HALF_UP);
	BigDecimal maxRebateAmount = saleAmount.multiply(maxRate).setScale(2, BigDecimal.ROUND_HALF_UP);
	AfSearchGoodsVo vo = new AfSearchGoodsVo();
	vo.setNumId(item.getNumIid() + "");
	vo.setGoodsIcon(item.getPictUrl());
	vo.setGoodsName(item.getTitle());
	vo.setGoodsUrl(item.getItemUrl());
	vo.setVolume(item.getVolume());
	vo.setRealAmount(new StringBuffer("").append(saleAmount.subtract(maxRebateAmount)).append("~").append(saleAmount.subtract(minRebateAmount)).toString());
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
}
