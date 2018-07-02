package com.ald.fanbei.api.web.api.category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Component("getHomeGoodsListApi")
public class GetHomeGoodsListApi implements ApiHandle {

	@Resource
	AfGoodsCategoryService afGoodsCategoryService;

	@Resource
	AfSchemeGoodsService afSchemeGoodsService;

	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;

	@Resource
	AfResourceService afResourceService;

	@Resource
	AfGoodsService afGoodsService;

	@Resource
	AfSeckillActivityService afSeckillActivityService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = new HashMap<String, Object>();
		Long categoryId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("categoryId"), 0l);
		Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
		AfGoodsQuery query = new AfGoodsQuery();
		query.setCategoryId(categoryId);
		query.setPageNo(pageNo);
		query.setPageSize(50);
		List<AfGoodsDo> goodsDoList = null;
		AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(ResourceType.HOME_PAGE.getCode());
		if(StringUtils.equals(afResourceDo.getValue(), YesNoStatus.YES.getCode()) && request.getRequestURL().indexOf("//app")!=-1){
			if(StringUtils.equals(afResourceDo.getValue1(),"N")){
				goodsDoList = afGoodsService.getHomeGoodsByModelId(query);
			}else if(StringUtils.equals(afResourceDo.getValue1(),"Y")){
				goodsDoList = afGoodsService.getHomeCategoryGoodsList(query);
			}
		}else{
			if(StringUtils.equals(afResourceDo.getValue2(),"N")){
				goodsDoList = afGoodsService.getHomeGoodsByModelId(query);
			}else if(StringUtils.equals(afResourceDo.getValue2(),"Y")){
				goodsDoList = afGoodsService.getHomeCategoryGoodsList(query);
			}
		}



		List<Map<String, Object>> goodsInfoList = new ArrayList<Map<String, Object>>();
		// 获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}

//		Iterator<Object> it = array.iterator();
//		while (it.hasNext()) {
//			JSONObject json = (JSONObject) it.next();
//			if (json.getString(Constants.DEFAULT_NPER).equals("2")) { //mark
//				it.remove();
//				break;
//			}
//		}
		//判断商品是否处于活动中
		List<AfSeckillActivityGoodsDo> activityGoodsDos = new ArrayList<>();
		List<Long> goodsIdList = new ArrayList<>();
		for (AfGoodsDo goodsDo : goodsDoList) {
			goodsIdList.add(goodsDo.getRid());
		}
		logger.info("goodsIds111"+goodsIdList.toString());
		if(goodsIdList!=null&&goodsIdList.size()>0){
			activityGoodsDos =afSeckillActivityService.getActivityGoodsByGoodsIds(goodsIdList);
		}
		for (AfGoodsDo goodsDo : goodsDoList) {
			Map<String, Object> goodsInfo = new HashMap<String, Object>();
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
			goodsInfo.put("goodName", goodsDo.getName());
			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
			goodsInfo.put("goodsId", goodsDo.getRid());
			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
			goodsInfo.put("source", goodsDo.getSource());
			goodsInfo.put("saleCount", goodsDo.getSaleCount());
			goodsInfo.put("goodsType", "0");
			// 如果是分期免息商品，则计算分期
			Long goodsId = goodsDo.getRid();
			AfSchemeGoodsDo schemeGoodsDo = null;
			try {
				schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
			} catch (Exception e) {
				logger.error(e.toString());
			}
			JSONArray interestFreeArray = null;
			if (schemeGoodsDo != null) {
				AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService
						.getById(schemeGoodsDo.getInterestFreeId());
				String interestFreeJson = interestFreeRulesDo.getRuleJson();
				if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
					interestFreeArray = JSON.parseArray(interestFreeJson);
				}
			}
			List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray,
					BigDecimal.ONE.intValue(), goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");
			if (nperList != null) {
				Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
				String isFree = (String) nperMap.get("isFree");
				if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
					nperMap.put("freeAmount", nperMap.get("amount"));
				}
				goodsInfo.put("nperMap", nperMap);
			}
			goodsInfoList.add(goodsInfo);
		}
		data.put("goodsInfoList", goodsInfoList);
		resp.setResponseData(data);
		return resp;
	}

}
