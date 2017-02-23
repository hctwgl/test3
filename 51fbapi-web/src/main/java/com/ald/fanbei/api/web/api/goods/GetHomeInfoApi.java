/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
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
		List<Object> bannerList = getObjectWithResourceDolist(afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeBanner.getCode()));
		List<Object> bannerSecList = getObjectWithResourceDolist(afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeSecondBanner.getCode()));
		List<Object> one2OneList = getObjectWithResourceDolist(afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeOneImage.getCode()));
		List<Object> one2ManyList = getOne2ManyObjectWithResourceDolist(afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeOneToMany.getCode()));
		List<Object> one2TwoList = getOne2ManyObjectWithResourceDolist(afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeOneToTwo.getCode()));

		data.put("bannerList", bannerList);
		data.put("bannerSecList", bannerSecList);
		data.put("one2ManyList", one2ManyList);
		data.put("one2TwoList", one2TwoList);
		data.put("one2OneList", one2OneList);
		
		resp.setResponseData(data);
		return resp;
	}

	
	

	private List<Object>  getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
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
	private List<Object>  getOne2ManyObjectWithResourceDolist(List<AfResourceDo> resclist) {
		List<Object> bannerList = new ArrayList<Object>();
		Long sort=  -1L;
		Map<String, Object> oneData = new HashMap<String, Object>();
		List<Map<String, Object>> manyData = new ArrayList<Map<String, Object>>();
		
		for (int i = 0; i < resclist.size(); i++) {
			AfResourceDo afResourceDo = resclist.get(i);
			if(sort!=afResourceDo.getSort()){
				//将多个对象加入数组中
				if(sort !=-1){
					addListDataWithResource(oneData, manyData, bannerList);
				}
				sort =afResourceDo.getSort();				
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getSecType());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getValue4());
			if(StringUtil.equals(afResourceDo.getValue1(), AfResourceSecType.ResourceValue1MainImage.getCode())){
				oneData = data;
			}else{
				manyData.add(data);
			}
		}
		if(manyData.size()>0||oneData.size()>0){
			addListDataWithResource(oneData, manyData, bannerList);
		}
		
		return bannerList;
	}
	
	private void addListDataWithResource(Map<String,Object> oneData, List<Map<String, Object>> manyData,List<Object> bannerList){
		Map<String, Object> listData = new HashMap<String, Object>();
		List<Map<String, Object>> manyTemData = new ArrayList<Map<String, Object>>(manyData) ;
//		Collections.sort(manyTemData, new Comparator<Map<String, Object>>() {
//			@Override
//			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//				String sort1 = ObjectUtils.toString(o1.get("sort"), "").toString();
//				String sort2 = ObjectUtils.toString(o2.get("sort"), "").toString();
//				return NumberUtil.objToInteger(sort1)-NumberUtil.objToInteger(sort2);
//			}
//		});
		
		
		Map<String, Object> oneTemData =new HashMap<String, Object>();
		oneTemData.putAll(oneData);
				
		listData.put("manyEntity", manyTemData);
		listData.put("oneEntity", oneTemData);
		bannerList.add(listData);
		manyData.clear();
		oneData.clear();
	}

}
