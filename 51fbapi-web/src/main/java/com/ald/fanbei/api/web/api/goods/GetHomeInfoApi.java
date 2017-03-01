/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author suweili
 *
 */
@Component("getHomeInfoApi")
public class GetHomeInfoApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> bannerList = getObjectWithResourceDolist(
				afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeBanner.getCode()));
		List<Object> bannerSecList = getObjectWithResourceDolist(
				afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeSecondBanner.getCode()));
		List<Object> one2OneList = getObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneImage.getCode()));
		List<Object> one2ManyList = getOne2ManyObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToMany.getCode()));
		List<Object> one2TwoList = getOne2ManyObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToTwo.getCode()));

		data.put("bannerList", bannerList);
		data.put("bannerSecList", bannerSecList);
		data.put("one2ManyList", one2ManyList);
		data.put("one2TwoList", one2TwoList);
		data.put("one2OneList", one2OneList);

		resp.setResponseData(data);
		return resp;
	}

	private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
		List<Object> bannerList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : bannerResclist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getSecType());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			bannerList.add(data);
		}

		return bannerList;
	}

	private List<Object> getOne2ManyObjectWithResourceDolist(List<AfResourceDo> resclist) {
		List<Object> bannerList = new ArrayList<Object>();
		String value4 = "-1";
		Map<String, Object> oneData = new HashMap<String, Object>();
		List<Map<String, Object>> manyData = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < resclist.size(); i++) {
			AfResourceDo afResourceDo = resclist.get(i);
			if (!StringUtil.equals(value4, afResourceDo.getValue4()) ) {
				// 将多个对象加入数组中
				if (!StringUtil.equals(value4, "-1") ) {
					addListDataWithResource(oneData, manyData, bannerList);
				}
				value4 = afResourceDo.getValue4();
			}

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getValue1());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());
			if (StringUtil.equals(afResourceDo.getSecType(), AfResourceSecType.ResourceValue1MainImage.getCode())) {
				oneData = data;
			} else {
				manyData.add(data);
			}
		}
		if (manyData.size() > 0 || oneData.size() > 0) {
			addListDataWithResource(oneData, manyData, bannerList);
		}

		return bannerList;
	}

	private void addListDataWithResource(Map<String, Object> oneData, List<Map<String, Object>> manyData,
			List<Object> bannerList) {
		Map<String, Object> listData = new HashMap<String, Object>();
		List<Map<String, Object>> manyTemData = new ArrayList<Map<String, Object>>(manyData);

		Map<String, Object> oneTemData = new HashMap<String, Object>();
		oneTemData.putAll(oneData);

		listData.put("manyEntity", manyTemData);
		listData.put("oneEntity", oneTemData);
		bannerList.add(listData);
		manyData.clear();
		oneData.clear();
	}

}
