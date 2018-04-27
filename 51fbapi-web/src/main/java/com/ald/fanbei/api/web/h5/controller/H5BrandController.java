package com.ald.fanbei.api.web.h5.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBrandService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.biz.service.AfResourceH5Service;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBrandDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

@Controller
@RequestMapping("/category")
public class H5BrandController extends BaseController {
	
	@Resource
	private AfBrandService afBrandService;
	@Resource
	private AfGoodsService afGoodsService;
	@Resource 
	private AfResourceService afResourceService;
	@Resource
    AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfResourceH5Service afResourceH5Service;
	@Resource
	AfResourceH5ItemService afResourceH5ItemService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	
	@RequestMapping(value="/H5brandResult",method=RequestMethod.POST,produces="application/json;charset=UTF-8")
	@ResponseBody
	public String brandResult(HttpServletRequest request ,HttpServletResponse response){
		FanbeiH5Context context = new FanbeiH5Context();
		try {
		//	context = doH5Check(request, false);
			Long brandId = NumberUtil.objToLong(request.getParameter("brandId"));
			int pageNo = NumberUtil.objToIntDefault(request.getParameter("pageNo"), 1);
			logger.info("/category/H5brandResult params: id:" + request.getHeader(Constants.REQ_SYS_NODE_ID) + "  brandId:" + brandId);
			if (brandId == null){
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getErrorMsg(), "", null).toString();
			}
			// 1 query info of the brand
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) bizCacheUtil.getMap("ASJbrandResult"+brandId);
			if (data == null){
				data = new HashMap<String, Object>();
				AfBrandDo brandInfo = (AfBrandDo) bizCacheUtil.getObject("barndInfo"+brandId);
				if (brandInfo == null){
					brandInfo = afBrandService.getById(brandId);
					bizCacheUtil.saveObject("barndInfo"+brandId, brandInfo, Constants.SECOND_OF_HALF_DAY);
				}
				// 2 query goods of the brand with volume of top5
				List<HomePageSecKillGoods> brandGoodsList = afGoodsService.getAllByBrandIdAndVolume(brandId);
				if (brandGoodsList == null){
					return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.BRAND_GOODS_IS_EMPTY.getErrorMsg(), "", null).toString();
				}
				// handle the goods data 
				List<HomePageSecKillGoods> starGoodsList = new ArrayList<HomePageSecKillGoods>();
				List<HomePageSecKillGoods> otherGoodsList = new ArrayList<HomePageSecKillGoods>();
				for (int i =0;i < brandGoodsList.size(); i++){
					if (i < 5){
						starGoodsList.add(brandGoodsList.get(i));
					}else{
						otherGoodsList.add(brandGoodsList.get(i));
					}
				}
				// do page logic
				List<HomePageSecKillGoods> allGoodsList = doPage(pageNo, otherGoodsList);
				
			//	List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
				//获取借款分期配置信息
				AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
				JSONArray array = JSON.parseArray(resource.getValue());
				if (array == null) {
				    throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
				}
				List<Map<String,Object>> list1 = HandleData( resource,array, starGoodsList);
				List<Map<String,Object>> list2= HandleData( resource,array, allGoodsList);
				data.put("brandInfo",brandInfo);
				data.put("starGoodsList",list1);
				data.put("otherGoodsList",list2);
				data.put("pageNo", pageNo);
				bizCacheUtil.saveMap("ASJbrandResult"+brandId, data, Constants.SECOND_OF_ONE_MINITS);
			}
			return H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.BRAND_RESULT_INIT_SUCCESS.getErrorMsg(), "", data).toString();
		} catch (Exception e) {
			String result =  H5CommonResponse.getNewInstance(false, "品牌结果页初始化失败..", "", e.getMessage()).toString();
			logger.error("/category/H5brandResult 初始化数据失败  e = {} , resultStr = {}", e, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
			return result;
		}
	}
	
	private List<HomePageSecKillGoods> doPage(int pageNo, List<HomePageSecKillGoods> goodList) {
    	// 不够一页
    	int pageSize = 30;
    	if (goodList.size() <= pageSize){
    		return goodList;
    	}
    	// 多页
    	int startNumber = (pageNo-1) * pageSize;
    	int endNumber = pageNo * pageSize -1;
    	int listSize = goodList.size();
    	ArrayList<HomePageSecKillGoods> list = new ArrayList<HomePageSecKillGoods>();
    	if (endNumber <= listSize -1){
    		return list;
    	}
    	if (listSize -1 < startNumber){
    		int number = listSize/pageNo;
    		startNumber = number * pageSize;
    		for (int i = startNumber;i<listSize;i++){
    			list.add(goodList.get(i));
    		}
    		return list;
    	}
    	for (int i = startNumber;i < listSize;i++){
    		list.add(goodList.get(i));
    	}
		return list;
	}
	
	private  List<Map<String, Object>>  HandleData(AfResourceDo resource, JSONArray array,
			List<HomePageSecKillGoods> starGoodsList) {
		 List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
		 for(HomePageSecKillGoods goodsDo : starGoodsList) {
	            Map<String, Object> goodsInfo = new HashMap<String, Object>();
	            String url = "";
	            goodsInfo.put("goodName",goodsDo.getGoodName());
	            goodsInfo.put("rebateAmount",goodsDo.getRebateAmount());
	            goodsInfo.put("saleAmount",goodsDo.getSaleAmount());
				if (goodsDo.getActivityAmount() != null){
					goodsInfo.put("saleAmount",goodsDo.getActivityAmount());          	
					}
	            goodsInfo.put("priceAmount",goodsDo.getPriceAmount());
	            url = goodsDo.getGoodsIcon();
	            goodsInfo.put("goodsIcon",url);
	            goodsInfo.put("goodsId",goodsDo.getGoodsId());
	            goodsInfo.put("goodsUrl",goodsDo.getGoodsUrl());
	            goodsInfo.put("goodsType", "0");
	            goodsInfo.put("source", goodsDo.getSource());
	            // 如果是分期免息商品，则计算分期
	            Long goodsId = goodsDo.getGoodsId();
	            AfSchemeGoodsDo schemeGoodsDo = null;
	            try {
	                schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
	            } catch(Exception e){
	                logger.error(e.toString());
	            }
	            JSONArray interestFreeArray = null;
	            if(schemeGoodsDo != null){
	                AfInterestFreeRulesDo interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
	                String interestFreeJson = interestFreeRulesDo.getRuleJson();
	                if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
	                    interestFreeArray = JSON.parseArray(interestFreeJson);// 免息规则
	                }
	            }
	            List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
	                    goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2(),goodsId,"0");
	            if(nperList!= null){
	                goodsInfo.put("goodsType", "1");
	                Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
	                String isFree = (String)nperMap.get("isFree");
	                if(InterestfreeCode.NO_FREE.getCode().equals(isFree)) {
	                    nperMap.put("freeAmount", nperMap.get("amount"));
	                }
	                goodsInfo.put("nperMap", nperMap);
	            }
	            goodsList.add(goodsInfo);
	        }
		 return goodsList;
	}


	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(),
					FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
