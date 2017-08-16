/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfActivityGoodsDo;
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
	
	@Resource
	AfActivityGoodsService afActivityGoodsService;
	
	private FanbeiContext contextApp;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		contextApp = context;
		Map<String, Object> data = new HashMap<String, Object>();
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		List<Object> bannerList = new ArrayList<Object>();
		//正式环境和预发布环境区分
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			bannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeBanner.getCode()));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
			bannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.HomeBanner.getCode()));
		}
		List<Object> bannerSecList = new ArrayList<Object>();
		if(context.getAppVersion() >= 363){
			bannerSecList = getObjectWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.HomeSecondBanner.getCode()));
		}
		List<Object> one2OneList = getObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneImage.getCode()));
		
		List<Object> one2ManyList = getOne2ManyObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToMany.getCode()));
		
		List<Object> one2TwoList = getOne2ManyObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToTwo.getCode()));
		
		List<Object> one2TwoList2 = getOne2ManyObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeOneToTwo2.getCode()));
		
		List<Object> homeActivityList = getOne2ManyObjectWithResourceDolist(
				afResourceService.getOneToManyResourceOrderByBytype(AfResourceType.HomeActivity.getCode()));
		
		List<Object> navigationList = getObjectWithResourceDolist(
				afResourceService.getHomeIndexListByOrderby(AfResourceType.HomeNavigation.getCode()));

		data.put("bannerList", bannerList);
		data.put("bannerSecList", bannerSecList);
		data.put("homeActivityList",homeActivityList);
		data.put("one2ManyList", one2ManyList);
		data.put("one2TwoList", one2TwoList);
		data.put("one2OneList", one2OneList);
		data.put("navigationList", navigationList);
		data.put("one2TwoList2",one2TwoList2);
		
		

		resp.setResponseData(data);
		return resp;
	}

	private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
		List<Object> bannerList = new ArrayList<Object>();
		
		for (AfResourceDo afResourceDo : bannerResclist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			if(afResourceDo.getType().equals(AfResourceType.HomeNavigation.getCode())){
				data.put("type", afResourceDo.getSecType());
				// 对首页充值的版本兼容修改
				if (contextApp.getAppVersion() <= 365 && afResourceDo.getSecType().equals(AfResourceSecType.NAVIGATION_BOLUOME.getCode())){
					data.put("type", AfResourceSecType.NAVIGATION_MOBILE_CHARGE.getCode());
				}
			}else{
				data.put("type", afResourceDo.getValue1());
			}
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
			// 1+2 模式新增时间字段的判断处理
			if(AfResourceType.HomeOneToTwo2.getCode().equals(afResourceDo.getType())){
				if("GOODS_ID".equals(afResourceDo.getValue1())){
					Long goodsId = NumberUtil.objToLong(afResourceDo.getValue2());
					AfActivityGoodsDo activityGoodsDo = afActivityGoodsService.getActivityGoodsByGoodsId(goodsId);
					if(activityGoodsDo != null){
						
						data.put("startTime", activityGoodsDo.getStartTime());
						data.put("validStart", activityGoodsDo.getValidStart());
						data.put("validEnd", activityGoodsDo.getValidEnd());
						data.put("currentTime", new Date());	
					}	
				}
				
			}
			
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
