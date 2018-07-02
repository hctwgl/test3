package com.ald.fanbei.api.web.apph5.controller;


import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.*;
import com.ald.fanbei.api.web.common.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;


@Controller
@RequestMapping("/fanbei-web/activity")
public class APPH5BrandChannelController extends BaseController {

	@Resource
	AfResourceH5Service afResourceH5Service;
	@Resource
	AfResourceH5ItemService afResourceH5ItemService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGoodsService afGoodsService;

	/**
	 * 品牌频道
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getBrandChannel", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBrandChannel(HttpServletRequest request,HttpServletResponse response) {
		H5CommonResponse resp = null;
		String tag = ObjectUtils.toString(request.getParameter("tag"), null);
		Map<String,Object> data = new HashMap<String,Object>();
		StringBuffer params = new StringBuffer();
		List<AfResourceH5Dto> list = afResourceH5Service.selectByStatus(tag);
		for(AfResourceH5Dto afResourceH5Dto : list){
			List<AfResourceH5ItemDto> itemList = afResourceH5ItemService.selectByModelId(afResourceH5Dto.getRid());
			for(AfResourceH5ItemDto afResourceH5ItemDto : itemList){
				if(!StringUtils.isBlank(afResourceH5ItemDto.getValue2())){
					List ids = new ArrayList();
					String[] array = afResourceH5ItemDto.getValue2().split(",");
					for(int i=0;i<array.length;i++){
						ids.add(array[i]);
					}
					List<AfGoodsDo> goodslist = afGoodsService.getGoodsListByGoodsId(ids);
					afResourceH5ItemDto.setGoodsList(getGoodsList(goodslist));
				}
			}
			afResourceH5Dto.setItemList(itemList);
		}
		data.put("resourceH5List",list);
		resp = H5CommonResponse.getNewInstance(true, "成功","",data);
		return resp.toString();

	}

	public List<Map<String,Object>> getGoodsList(List<AfGoodsDo> list) {
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		for (AfGoodsDo goodsDo : list) {
			Map<String, Object> goodsInfo = new HashMap<String, Object>();
			goodsInfo.put("goodName", goodsDo.getName());
			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
			goodsInfo.put("goodsId", goodsDo.getRid());
			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
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
				AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
				String interestFreeJson = interestFreeRulesDo.getRuleJson();
				if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
					interestFreeArray = JSON.parseArray(interestFreeJson);
				}
			}
			List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(), goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(), goodsId,"0");
			if (nperList != null) {
				goodsInfo.put("goodsType", "1");
				Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
				String isFree = (String) nperMap.get("isFree");
				if (InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
					nperMap.put("freeAmount", nperMap.get("amount"));
				}
				goodsInfo.put("nperMap", nperMap);
			}
			goodsList.add(goodsInfo);
		}
		return goodsList;
	}


	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

}
