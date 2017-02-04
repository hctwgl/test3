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

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ResourceHomeType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
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
		List<AfResourceDo> list = afResourceService.getHomeConfigByAllTypes();
		Map<String, Object> data = homeInfoDataWithAfResourceDoList(list);
		resp.setResponseData(data);
		return resp;
	}

	public Map<String, Object> homeInfoDataWithAfResourceDoList(List<AfResourceDo> AfResourceDoList) {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> homeBannerList = new ArrayList<Object>();
		List<Object> homeToolsList = new ArrayList<Object>();
		List<Object> homeCouponList = new ArrayList<Object>();
		List<Object> homeOneToManyList = new ArrayList<Object>();
		List<Object> homeOneToTwoList = new ArrayList<Object>();
		List<Object> homeOneToOneList = new ArrayList<Object>();

		for (AfResourceDo afResourceDo : AfResourceDoList) {
			if (StringUtils.equals(afResourceDo.getType(), ResourceHomeType.ResourceHomeTypeBanner.getCode())) {
				homeBannerList.add(getHomeBannerObjectWithAfResourceDo(afResourceDo));
			} else if (StringUtils.equals(afResourceDo.getType(), ResourceHomeType.ResourceHomeTypeTools.getCode())) {
				homeToolsList.add(getHomeToolsObjectWithAfResourceDo(afResourceDo));
			} else if (StringUtils.equals(afResourceDo.getType(), ResourceHomeType.ResourceHomeTypeCoupon.getCode())) {
				homeCouponList.add(getHomeCouponObjectWithAfResourceDo(afResourceDo));
			} else if (StringUtils.equals(afResourceDo.getType(),
					ResourceHomeType.ResourceHomeTypeOneToMany.getCode())) {
				homeOneToManyList.add(getHomeOneToManyObjectWithAfResourceDo(afResourceDo));
			} else if (StringUtils.equals(afResourceDo.getType(),
					ResourceHomeType.ResourceHomeTypeOneToTwo.getCode())) {
				homeOneToTwoList.add(getHomeOneToTwoObjectWithAfResourceDo(afResourceDo));
			} else if (StringUtils.equals(afResourceDo.getType(),
					ResourceHomeType.ResourceHomeTypeOneToOne.getCode())) {
				homeOneToOneList.add(getHomeOneToOneObjectWithAfResourceDo(afResourceDo));
			}
		}
		data.put("homeBannerList", homeBannerList);
		data.put("homeToolsList", homeToolsList);
		data.put("homeCouponList", homeCouponList);
		data.put("homeOneToManyList", homeOneToManyList);
		data.put("homeOneToTwoList", homeOneToTwoList);
		data.put("homeOneToOneList", homeOneToOneList);

		return data;

	}

	private Map<String, Object> getHomeBannerObjectWithAfResourceDo(AfResourceDo afResourceDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("imageUrl", afResourceDo.getValue());
		data.put("titleName", afResourceDo.getName());
		data.put("type", afResourceDo.getSecType());
		data.put("comment", afResourceDo.getValue1());
		data.put("sort", afResourceDo.getSort());

		return data;
	}

	private Map<String, Object> getHomeToolsObjectWithAfResourceDo(AfResourceDo afResourceDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("iconUrl", afResourceDo.getValue());
		data.put("toolsTitle", afResourceDo.getName());
		data.put("toolsType", afResourceDo.getSecType());
		data.put("sort", afResourceDo.getSort());

		return data;
	}

	private Map<String, Object> getHomeCouponObjectWithAfResourceDo(AfResourceDo afResourceDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("imageUrl", afResourceDo.getValue());
		data.put("titleName", afResourceDo.getName());
		data.put("type", afResourceDo.getSecType());
		data.put("sort", afResourceDo.getSort());

		return data;
	}

	private Map<String, Object> getHomeOneToManyObjectWithAfResourceDo(AfResourceDo afResourceDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> oneData = new HashMap<String, Object>();
		List<Object> manyData = new ArrayList<Object>();

		oneData.put("imageUrl", afResourceDo.getValue());
		oneData.put("titleName", afResourceDo.getName());
		oneData.put("type", afResourceDo.getSecType());
		oneData.put("comment", afResourceDo.getValue1());
		data.put("oneProduct", oneData);
		data.put("manyProduct", manyData);
		
		return data;
	}

	private Map<String, Object> getHomeOneToTwoObjectWithAfResourceDo(AfResourceDo afResourceDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("imageUrl", afResourceDo.getValue());
		data.put("titleName", afResourceDo.getName());
		data.put("type", afResourceDo.getSecType());
		data.put("comment", afResourceDo.getValue1());
		data.put("sort", afResourceDo.getSort());

		return data;
	}

	private Map<String, Object> getHomeOneToOneObjectWithAfResourceDo(AfResourceDo afResourceDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("imageUrl", afResourceDo.getValue());
		data.put("titleName", afResourceDo.getName());
		data.put("type", afResourceDo.getSecType());
		data.put("comment", afResourceDo.getValue1());
		data.put("sort", afResourceDo.getSort());
		return data;
	}

	

}
