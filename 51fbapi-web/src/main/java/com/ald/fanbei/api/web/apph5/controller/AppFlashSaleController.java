package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @类描述：
 * 
 * @author :maqiaopan
 * @version ：2017年6月13日 下午5:02:28
 * @注意：本内容仅限于杭州喜马拉雅家居有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/flash")
public class AppFlashSaleController extends BaseController {

	@Resource
	AfUserService afUserService;
	@Resource
	AfSignInActivityService afSignInActivityService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfSigninService afSigninService;
	@Resource
	JpushService jpushService;
	@Resource
	CouponSceneRuleEnginerUtil activeRuleEngineUtil;
	@Resource
	AfCouponSceneService afCouponSceneService;
	@Resource
	TransactionTemplate transactionTemplate;


	@Resource
	AfGoodsPriceService afGoodsPriceService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfActivityGoodsService afActivityGoodsService;

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfUserGoodsSmsService afUserGoodsSmsService;


	@RequestMapping(value = "/getFlashSaleGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ApiHandleResponse GetFlashSaleGoods(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String,Object> data = new HashMap<String,Object>();
		List<Object> topBannerList = new ArrayList<Object>();
		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		int count = 0;
		//正式环境和预发布环境区分，banner轮播图展示
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			//新版,旧版,banner图不一样
			String homeBanner = AfResourceType.LimitedPurchaseBanner.getCode();
			topBannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderBy(homeBanner));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
			//新版,旧版,banner图不一样
			String homeBanner = AfResourceType.LimitedPurchaseBanner.getCode();
			topBannerList = getObjectWithResourceDolist(
					afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(homeBanner));
		}
		data.put("BannerList",topBannerList);
		//商品展示
		AfGoodsQuery query = getCheckParam(requestDataVo);
		List<AfEncoreGoodsDto> list = afGoodsService.selectFlashSaleGoods(query);
		List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
		//获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
				it.remove();
				break;
			}
		}
		for(AfEncoreGoodsDto goodsDo : list) {
			Map<String, Object> goodsInfo = new HashMap<String, Object>();
			goodsInfo.put("goodName",goodsDo.getName());
			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
			goodsInfo.put("goodsId", goodsDo.getRid());
			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
			goodsInfo.put("goodsType", "0");
			// 如果是分期免息商品，则计算分期
			Long goodsId = goodsDo.getRid();
			AfActivityGoodsDo afActivityGoodsDo = afActivityGoodsService.getActivityGoodsByGoodsIdAndType(goodsDo.getRid());
			if(null != afActivityGoodsDo){
				count = new Long(afActivityGoodsDo.getInitialCount()).intValue();
			}else{
				count = 0;
			}
			Integer total = afGoodsPriceService.selectSumStock(goodsId);
			goodsInfo.put("total",total+count);
			Integer volume = afOrderService.selectSumCountByGoodsId(goodsId);
			goodsInfo.put("volume",volume+count);
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
					interestFreeArray = JSON.parseArray(interestFreeJson);
				}
			}
			List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
					goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
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
		data.put("GoodsList",goodsList);
		resp.setResponseData(data);
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

	private AfGoodsQuery getCheckParam(RequestDataVo requestDataVo){
		Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
		AfGoodsQuery query = new AfGoodsQuery();
		query.setPageNo(pageNo);
		return query;
	}


	@RequestMapping(value = "/getBookingRushGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ApiHandleResponse GetBookingRushGoods(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String,Object> data = new HashMap<String,Object>();
		Long userId = context.getUserId();
		//商品展示
		AfGoodsQuery query = getCheckParam(requestDataVo);
		List<AfEncoreGoodsDto> list = afGoodsService.selectBookingRushGoods(query);
		List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
		//获取借款分期配置信息
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
		JSONArray array = JSON.parseArray(resource.getValue());
		if (array == null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
		}
		Iterator<Object> it = array.iterator();
		while (it.hasNext()) {
			JSONObject json = (JSONObject) it.next();
			if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
				it.remove();
				break;
			}
		}
		for(AfEncoreGoodsDto goodsDo : list) {
			Map<String, Object> goodsInfo = new HashMap<String, Object>();
			goodsInfo.put("goodName",goodsDo.getName());
			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
			goodsInfo.put("goodsId", goodsDo.getRid());
			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
			goodsInfo.put("goodsType", "0");
			//是否预约
			AfUserGoodsSmsDo afUserGoodsSmsDo = new AfUserGoodsSmsDo();

			afUserGoodsSmsDo.setGoodsId(goodsDo.getRid());
			afUserGoodsSmsDo.setUserId(userId);
			AfUserGoodsSmsDo afUserGoodsSms = afUserGoodsSmsService.selectByGoodsIdAndUserId(afUserGoodsSmsDo);
			if(null != afUserGoodsSms){
				goodsInfo.put("reserve","Y");
			}else{
				goodsInfo.put("reserve","N");
			}
			// 如果是分期免息商品，则计算分期
			Long goodsId = goodsDo.getRid();
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
					interestFreeArray = JSON.parseArray(interestFreeJson);
				}
			}
			List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
					goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
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
		data.put("GoodsList",goodsList);
		resp.setResponseData(data);
		return resp;
	}




	@RequestMapping(value = "/checkGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ApiHandleResponse CheckGoods(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long goodsId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("goodsId"),0l);
		Integer sumCount = afGoodsPriceService.selectSumStock(goodsId);
		if(null == sumCount || sumCount == 0){
			throw new FanbeiException(FanbeiExceptionCode.SOLD_OUT);
		}
		resp.setResponseData(goodsId);
		return resp;
	}


	@RequestMapping(value = "/reserveGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public ApiHandleResponse ReserveGoods(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long goodsId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("goodsId"),0l);
		String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"), "").toString();
		AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
		if(null != afGoodsDo){
			goodsName = afGoodsDo.getName();
		}
		AfUserGoodsSmsDo afUserGoodsSmsDo = new AfUserGoodsSmsDo();
		afUserGoodsSmsDo.setGoodsId(goodsId);
		afUserGoodsSmsDo.setUserId(userId);
		afUserGoodsSmsDo.setIsDelete(0l);
		afUserGoodsSmsDo.setGoodsName(goodsName);
		AfUserGoodsSmsDo afUserGoodsSms = afUserGoodsSmsService.selectByGoodsIdAndUserId(afUserGoodsSmsDo);
		if(null != afUserGoodsSms){
			throw new FanbeiException(FanbeiExceptionCode.GOODS_HAVE_BEEN_RESERVED);
		}
		try{
			int flag = afUserGoodsSmsService.insertByGoodsIdAndUserId(afUserGoodsSmsDo);
			if(flag <= 0){
				throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
			}
		}catch (Exception e){
			throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
		}
		return resp;
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
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

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
