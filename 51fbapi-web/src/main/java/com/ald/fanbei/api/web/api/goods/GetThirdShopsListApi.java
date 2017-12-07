package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.taobao.NTbkShop;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserSearchDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.taobao.api.ApiException;

/**
 * @date 2017-9-7 17:35:31
 * @author qiaopan
 * @description 搜索淘宝店铺
 *
 */
@Component("getThirdShopsListApi")
public class GetThirdShopsListApi implements ApiHandle {
    @Resource
    TaobaoApiUtil taobaoApiUtil;
    @Resource
    AfResourceService afResourceService;
    @Resource
    private AfUserSearchService afUserSearchService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	Map<String, Object> buildParams = checkAndbuildParam(requestDataVo);
	try {
	    List<NTbkShop> list = taobaoApiUtil.geTbkShopGet(buildParams).getResults();
	    String keyword = ObjectUtils.toString(requestDataVo.getParams().get("keywords"), null);
	    if (StringUtil.isNotBlank(keyword) && null != context.getUserId()) {
		afUserSearchService.addUserSearch(getUserSearchDo(context.getUserId(), keyword));
	    }
	    final AfResourceDo virtualGoods = afResourceService.getSingleResourceBytype(AfResourceType.VirtualShopsKeywords.getCode());

	    List<NTbkShop> result = new ArrayList<>();
	    // 排除虚拟

	    if (CollectionUtils.isNotEmpty(list)) {
		for (NTbkShop shop : list) {
		    if (virtualGoods != null && isVirtualWithKey(shop.getShopTitle(), virtualGoods.getValue())) {
			continue;
		    }
		    result.add(shop);
		}
	    }

	    resp.addResponseData("shopsList", result);
	    resp.addResponseData("pageNo", buildParams.get("pageNo"));
	} catch (ApiException e) {
	    return new ApiHandleResponse("searchGoods failed", FanbeiExceptionCode.FAILED);
	}
	return resp;
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
