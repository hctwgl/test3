/**
 * 
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfShopVo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月26日下午11:17:22
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBrandListApi")
public class GetBrandListApi implements ApiHandle {
         public static final int PAGE_SIZE = 50;
	@Resource
	AfShopService afShopService;
	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 1);
		final int appVersion = context.getAppVersion();
		AfShopQuery query = new AfShopQuery();
		query.setFull(false);
		query.setPageNo(pageNo);
		query.setFull(false);
		query.setPageSize(PAGE_SIZE);
		List<AfShopDo> shopList = afShopService.getShopList(query);
		List<AfShopVo> resultList = new ArrayList<AfShopVo>();
		if (CollectionUtil.isNotEmpty(shopList)) {
			resultList = CollectionConverterUtil.convertToListFromList(shopList, new Converter<AfShopDo, AfShopVo>() {
				@Override
				public AfShopVo convert(AfShopDo source) {
					return parseDoToVo(source,appVersion);
				}
			});
		}
		 String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		 List<AfResourceDo> bannerList1 = new ArrayList<AfResourceDo>();
		//线上为开启状态
		 if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
		 bannerList1 = afResourceService
				.getResourceHomeListByTypeOrderBy(AfResourceType.GGHomeTopBanner.getCode());
		 }
		 else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
		//预发不区分状态
		 bannerList1 = afResourceService
				.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.GGHomeTopBanner.getCode());
		 }
		List<Object> bannerList = getObjectWithResourceDolist(bannerList1);
		resp.addResponseData("bannerList", bannerList);
		resp.addResponseData("shopList", resultList);
		resp.addResponseData("pageNo", pageNo);
		return resp;
	}
	private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
		List<Object> bannerList = new ArrayList<Object>();
		
		for (AfResourceDo afResourceDo : bannerResclist) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("imageUrl", afResourceDo.getValue());
		data.put("titleName", afResourceDo.getName());
		data.put("type", afResourceDo.getValue1());
		data.put("content", afResourceDo.getValue2());
		data.put("sort", afResourceDo.getSort());
		
		bannerList.add(data);
		
		}
		return bannerList;
		}
	private AfShopVo parseDoToVo(AfShopDo shopInfo,int appVersion) {
		AfShopVo vo = new AfShopVo();
		vo.setRid(shopInfo.getRid());
		vo.setName(shopInfo.getName());
		vo.setRebateAmount(shopInfo.getRebateAmount());
		vo.setRebateUnit(shopInfo.getRebateUnit());
		//版本判断,大于3.9.0的版本，用新图
		if(appVersion>390){
		    vo.setIcon(shopInfo.getNewIcon());
		}else{
		    vo.setIcon(shopInfo.getIcon());
		}
		vo.setType(shopInfo.getType());
		vo.setShopUrl(shopInfo.getShopUrl());
		vo.setLatestUseIcon(shopInfo.getLatestUseIcon());
		vo.setShopActivity(shopInfo.getShopActivity());
		vo.setLatestOnline(shopInfo.getLatestOnline());
		vo.setDescription(shopInfo.getDescription());
		return vo;
	}

}
