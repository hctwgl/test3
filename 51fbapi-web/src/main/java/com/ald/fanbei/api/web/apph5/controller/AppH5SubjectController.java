package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfModelH5Service;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfSubjectGoodsService;
import com.ald.fanbei.api.biz.service.AfSubjectService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.ActivityType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfModelH5Do;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSubjectDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfSubjectGoodsQuery;
import com.ald.fanbei.api.dal.domain.query.AfSubjectGoodsQueryV1;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 /* 
 *@类现描述：会场相关接口
 *@author 江荣波 2017年6月3日 下午5:56:53
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5SubjectController  extends BaseController{
	
	String  opennative = "/fanbei-web/opennative?name=";
	
	@Resource
	AfResourceService afResourceService;
	
	@Resource
	AfSubjectGoodsService afSubjectGoodsService;
	
	@Resource
	AfModelH5ItemService  afModelH5ItemService;
	
	@Resource
	AfSubjectService afSubjectService;
	
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	
	@Resource
	AfGoodsService afGoodsService;
	
	@Resource
	AfCouponService afCouponService;
	
	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfModelH5Service afModelH5Service;
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "mainActivityInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String mainActivityInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		FanbeiWebContext context = new FanbeiWebContext();
		// 主会场接口
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		JSONObject jsonObj = new JSONObject();
		try{
			//context = doWebCheck(request, false);
			// 数据埋点
			request.setAttribute("context", context);
			doMaidianLog(request,H5CommonResponse.getNewInstance(true,"succ"));
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.GoodsInfo.getCode();
			jsonObj.put("notifyUrl", notifyUrl);
			// 获取会场商品题目文案
			List<AfResourceDo> goodsTitleList =  afResourceService.getConfigByTypes("ACTIVITY_GOODS_TITLE");
			if(goodsTitleList != null && goodsTitleList.size() > 0){
				AfResourceDo goodsTitleInfo = goodsTitleList.get(0);
				jsonObj.put("goodTitle", goodsTitleInfo.getValue());
			} else {
				jsonObj.put("goodTitle", "精品推荐"); //默认文案
			}
			
			// 获取会场URL信息，需要在af_resource表中维护
			List<Map> mainActivityList = new ArrayList<Map>();
			List<AfResourceDo> activityUrls =  afResourceService.getConfigByTypes("MAIN_ACTIVITY_URL");
			for(AfResourceDo activityUrl : activityUrls) {
				Map mainActivityInfo = new HashMap();
				mainActivityInfo.put("activityUrl", activityUrl.getValue());
				mainActivityInfo.put("sort", activityUrl.getSort());
				mainActivityList.add(mainActivityInfo);
			}
			jsonObj.put("mainActivityList",mainActivityList);
			//获取借款分期配置信息
	        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	        JSONArray array = JSON.parseArray(resource.getValue());
	        //删除2分期
	        if (array == null) {
	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	        }
	        //removeSecondNper(array);
			List<AfGoodsDo>  qualityGoodsDoList = afSubjectGoodsService.listQualitySubjectGoods();
			List<Map> qualityGoodsList = new ArrayList<Map>();
			for(AfGoodsDo qualityGoods : qualityGoodsDoList) {
				Map qualityGoodsInfo = new HashMap();
				qualityGoodsInfo.put("goodName", qualityGoods.getName());
				qualityGoodsInfo.put("rebateAmount", qualityGoods.getRebateAmount());
				qualityGoodsInfo.put("saleAmount", qualityGoods.getSaleAmount());
				qualityGoodsInfo.put("goodsIcon", qualityGoods.getGoodsIcon());
				qualityGoodsInfo.put("goodsId", qualityGoods.getRid());
				qualityGoodsInfo.put("goodsUrl", qualityGoods.getGoodsUrl());
				qualityGoodsInfo.put("priceAmount", qualityGoods.getPriceAmount());
				qualityGoodsInfo.put("thumbnailIcon",qualityGoods.getThumbnailIcon());
				qualityGoodsInfo.put("remark",qualityGoods.getRemark());
				qualityGoodsInfo.put("goodsType", "0");
				String tags = qualityGoods.getTags();
				// 如果是分期免息商品，则计算分期
				if(tags != null && tags.contains("INTEREST_FREE")){
					Long goodsId = qualityGoods.getRid();
					AfSchemeGoodsDo schemeGoodsDo = null;
					try {
						schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
					} catch(Exception e){
						logger.error(e.toString());
					}
					JSONArray interestFreeArray = null;
					if(schemeGoodsDo != null){
						AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
						String interestFreeJson = interestFreeRulesDo.getRuleJson();
						if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
							interestFreeArray = JSON.parseArray(interestFreeJson);
						}
					}
					List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							qualityGoods.getSaleAmount(), resource.getValue1(), resource.getValue2());
					
					if(nperList!= null){
						qualityGoodsInfo.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						qualityGoodsInfo.put("nperMap", nperMap);
					}
					
				}
				qualityGoodsList.add(qualityGoodsInfo);
			}
			jsonObj.put("qualityGoodsList",qualityGoodsList);
			resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
			
		}catch(FanbeiException e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
			logger.error("请求失败"+context,e);
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
			logger.error("请求失败"+context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	
	private void removeSecondNper(JSONArray array) {
        if (array == null) {
            return;
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {//mark
                it.remove();
                break;
            }
        }

    }
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "partActivityInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String partActivityInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 分会场接口
		FanbeiWebContext context = new FanbeiWebContext();
		
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		try {
			context = doWebCheck(request, false);
			String modelId = ObjectUtils.toString(request.getParameter("modelId"), null);
			if(modelId == null || "".equals(modelId)) {
				resp = H5CommonResponse.getNewInstance(false, "模版id不能为空！");
				return resp.toString();
			}
			// 数据埋点
			request.setAttribute("context", context);
			doMaidianLog(request,H5CommonResponse.getNewInstance(true,"succ"));
			
			//获取借款分期配置信息
	        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	        JSONArray array = JSON.parseArray(resource.getValue());
	        //删除2分期
	        if (array == null) {
	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	        }
	        //removeSecondNper(array);
			
			JSONObject jsonObj = new JSONObject();
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.GoodsInfo.getCode();
			
			// 根据modelId 取优惠券信息
			List<AfCouponDto> couponList = afCouponService.getCouponByActivityIdAndType(Long.parseLong(modelId),
					ActivityType.ACTIVITY_TEMPLATE.getCode());
			String userName = context.getUserName();
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			
			for(AfCouponDto couponDto : couponList) {
				// 判断用户是否领
				if(userDo == null) {
					couponDto.setUserAlready(0);
				} else {
					int pickCount = afUserCouponService.getUserCouponByUserIdAndCouponId(userDo.getRid(), couponDto.getRid());
					couponDto.setUserAlready(pickCount);
				}
			}
			jsonObj.put("couponList", couponList);
						
			jsonObj.put("notifyUrl", notifyUrl);
			// 根据modelId查询banner信息
			List<AfModelH5ItemDo> bannerList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(Long.parseLong(modelId), "BANNER");
			if(bannerList != null && bannerList.size() > 0){
				AfModelH5ItemDo bannerInfo = bannerList.get(0);
				jsonObj.put("bannerImage", bannerInfo.getItemIcon());
			} else {
				resp = H5CommonResponse.getNewInstance(false, "banner信息为空");
				return resp.toString();
			}
			// 查询会场下所有二级会场
			List<AfModelH5ItemDo> subjectList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(Long.parseLong(modelId), "SUBJECT");
			List<Map> activityList = new ArrayList<Map>();
			if(subjectList == null || subjectList.size() == 0){
				resp = H5CommonResponse.getNewInstance(false, "分会场信息为空"); 
				return resp.toString();
			}
			AfModelH5ItemDo subjectH5ItemDo = subjectList.get(0);
			String secSubjectId = subjectH5ItemDo.getItemValue();
			AfSubjectDo  parentSubjectDo = afSubjectService.getSubjectInfoById(secSubjectId);
			Long parentId = parentSubjectDo.getParentId();
			String subjectName  = parentSubjectDo.getName();
			jsonObj.put("modelName", subjectName); // 主会场名称
			
			for(AfModelH5ItemDo subjectDo : subjectList) {
				Map activityInfoMap = new HashMap();
				String subjectId = subjectDo.getItemValue();
				// 查询会场信息
				AfSubjectDo subjectInfo = afSubjectService.getSubjectInfoById(subjectId);
				if(subjectInfo == null) {
					resp = H5CommonResponse.getNewInstance(false, "会场不存在id=" + subjectId);
					return resp.toString();
				}
				activityInfoMap.put("name", subjectInfo.getName());
				activityInfoMap.put("subjectId", subjectInfo.getId());
				// 获取一级会场名称
				AfSubjectDo parentSubjectInfo = afSubjectService.getParentSubjectInfoById(subjectId);
				String activityName = "";
				if(parentSubjectInfo != null){
					activityName = parentSubjectInfo.getName();
				}
				
				// 查询会场下所有商品信息
				AfSubjectGoodsQuery query = new AfSubjectGoodsQuery();
				query.setSubjectId(Long.parseLong(subjectId));
				List<AfGoodsDo> subjectGoodsList = afSubjectGoodsService.listAllSubjectGoods(query);
				List<Map> activityGoodsList  = new ArrayList<Map>();
				for(AfGoodsDo goodsDo : subjectGoodsList) {
					Map activityGoodsInfo = new HashMap();
					activityGoodsInfo.put("goodName",goodsDo.getName());
					activityGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
					activityGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
					activityGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
					activityGoodsInfo.put("goodsId", goodsDo.getRid());
					activityGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
					activityGoodsInfo.put("source", goodsDo.getSource());
					activityGoodsInfo.put("priceAmount", goodsDo.getPriceAmount());
					activityGoodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
					activityGoodsInfo.put("remark", goodsDo.getRemark());
					activityGoodsInfo.put("activityName", activityName);
					// 如果是分期免息商品，则计算分期
					AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsDo.getRid());
					JSONArray interestFreeArray = null;
			        if(null != afSchemeGoodsDo){
						Long goodsId = goodsDo.getRid();
						AfSchemeGoodsDo  schemeGoodsDo = null;
						try {
							schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
						} catch(Exception e){
							logger.error(e.toString());
						}
						
						if(schemeGoodsDo != null){
							AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
							String interestFreeJson = interestFreeRulesDo.getRuleJson();
							if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
								interestFreeArray = JSON.parseArray(interestFreeJson);
							}
						}
						
					}
			        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
					
					if(nperList!= null){
						activityGoodsInfo.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						activityGoodsInfo.put("nperMap", nperMap);
					}
					activityGoodsList.add(activityGoodsInfo);
				}
				activityInfoMap.put("activityGoodsList", activityGoodsList);
				activityList.add(activityInfoMap);
			}
		
			jsonObj.put("activityList", activityList);
			// 获取精品推荐商品
			List<AfGoodsDo>  qualityGoodsDoList = afSubjectGoodsService.listQualitySubjectGoodsByParentId(parentId);
			List<Map> qualityGoodsList = new ArrayList<Map>();
			for(AfGoodsDo qualityGoods : qualityGoodsDoList) {
				Map qualityGoodsInfo = new HashMap();
				qualityGoodsInfo.put("goodName", qualityGoods.getName());
				qualityGoodsInfo.put("rebateAmount", qualityGoods.getRebateAmount());
				qualityGoodsInfo.put("saleAmount", qualityGoods.getSaleAmount());
				qualityGoodsInfo.put("goodsIcon", qualityGoods.getGoodsIcon());
				qualityGoodsInfo.put("goodsId", qualityGoods.getRid());
				qualityGoodsInfo.put("goodsUrl", qualityGoods.getGoodsUrl());
				qualityGoodsInfo.put("source", qualityGoods.getSource());
				qualityGoodsInfo.put("priceAmount", qualityGoods.getPriceAmount());
				qualityGoodsInfo.put("thumbnailIcon",qualityGoods.getThumbnailIcon());
				qualityGoodsInfo.put("remark",qualityGoods.getRemark());
				qualityGoodsInfo.put("goodsType", "0");
				
				// 如果是分期免息商品，则计算分期
				AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(qualityGoods.getRid());
				JSONArray interestFreeArray = null;
		        if(null != afSchemeGoodsDo){
					Long goodsId = qualityGoods.getRid();
					AfSchemeGoodsDo  schemeGoodsDo = null;
					try {
						schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
					} catch(Exception e){
						logger.error(e.toString());
					}
					
					if(schemeGoodsDo != null){
						AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
						String interestFreeJson = interestFreeRulesDo.getRuleJson();
						if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
							interestFreeArray = JSON.parseArray(interestFreeJson);
						}
					}
				}
		        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
		        		qualityGoods.getSaleAmount(), resource.getValue1(), resource.getValue2());
				
				if(nperList!= null){
					qualityGoodsInfo.put("goodsType", "1");
					Map nperMap = nperList.get(nperList.size() - 1);
					qualityGoodsInfo.put("nperMap", nperMap);
				}
				
				qualityGoodsList.add(qualityGoodsInfo);
			}
			jsonObj.put("qualityGoodsList",qualityGoodsList);
			resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
		}catch(FanbeiException e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
			logger.error("请求失败"+context,e);
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
			logger.error("请求失败"+context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "partActivityInfoV1", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String partActivityInfoV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 分会场接口
		FanbeiWebContext context = new FanbeiWebContext();
		
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		try {
			context = doWebCheck(request, false);
			String modelId = ObjectUtils.toString(request.getParameter("modelId"), null);
			if(modelId == null || "".equals(modelId)) {
				resp = H5CommonResponse.getNewInstance(false, "模版id不能为空！");
				return resp.toString();
			}
			// 数据埋点
			request.setAttribute("context", context);
			doMaidianLog(request,H5CommonResponse.getNewInstance(true,"succ"));
			
			//获取借款分期配置信息
	        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	        JSONArray array = JSON.parseArray(resource.getValue());
	        //删除2分期
	        if (array == null) {
	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	        }
	        //removeSecondNper(array);
			
			JSONObject jsonObj = new JSONObject();
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.GoodsInfo.getCode();
			
			// 根据modelId 取优惠券信息
			List<AfCouponDto> couponList = afCouponService.getCouponByActivityIdAndType(Long.parseLong(modelId),
					ActivityType.ACTIVITY_TEMPLATE.getCode());
			String userName = context.getUserName();
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			
			for(AfCouponDto couponDto : couponList) {
				// 判断用户是否领
				if(userDo == null) {
					couponDto.setUserAlready(0);
				} else {
					int pickCount = afUserCouponService.getUserCouponByUserIdAndCouponId(userDo.getRid(), couponDto.getRid());
					couponDto.setUserAlready(pickCount);
				}
			}
			jsonObj.put("couponList", couponList);
						
			jsonObj.put("notifyUrl", notifyUrl);
			// 根据modelId查询banner信息
			List<AfModelH5ItemDo> bannerList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(Long.parseLong(modelId), "BANNER");
			if(bannerList != null && bannerList.size() > 0){
				AfModelH5ItemDo bannerInfo = bannerList.get(0);
				jsonObj.put("bannerImage", bannerInfo.getItemIcon());
			} else {
				resp = H5CommonResponse.getNewInstance(false, "banner信息为空");
				return resp.toString();
			}
			// 查询会场下所有二级会场
			List<AfModelH5ItemDo> subjectList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelTypeSortById(Long.parseLong(modelId), "SUBJECT");
			List<Map> activityList = new ArrayList<Map>();
			if(subjectList == null || subjectList.size() == 0){
				resp = H5CommonResponse.getNewInstance(false, "分会场信息为空"); 
				return resp.toString();
			}
			AfModelH5ItemDo subjectH5ItemDo = subjectList.get(0);
			String secSubjectId = subjectH5ItemDo.getItemValue();
			AfSubjectDo  parentSubjectDo = afSubjectService.getSubjectInfoById(secSubjectId);
			Long parentId = parentSubjectDo.getParentId();
			String subjectName  = parentSubjectDo.getName();
			jsonObj.put("modelName", subjectName); // 主会场名称
			
			for(AfModelH5ItemDo subjectDo : subjectList) {
				Map activityInfoMap = new HashMap();
				String subjectId = subjectDo.getItemValue();
				// 查询会场信息
				AfSubjectDo subjectInfo = afSubjectService.getSubjectInfoById(subjectId);
				if(subjectInfo == null) {
					resp = H5CommonResponse.getNewInstance(false, "会场不存在id=" + subjectId);
					return resp.toString();
				}
				activityInfoMap.put("name", subjectInfo.getName());
				activityInfoMap.put("subjectId", subjectInfo.getId());
				// 获取一级会场名称
				AfSubjectDo parentSubjectInfo = afSubjectService.getParentSubjectInfoById(subjectId);
				String activityName = "";
				if(parentSubjectInfo != null){
					activityName = parentSubjectInfo.getName();
				}
				
				// 查询会场下所有商品信息
				List<AfGoodsDo> subjectGoodsList = afSubjectGoodsService.listAllSubjectGoodsV1(subjectId);
				List<Map> activityGoodsList  = new ArrayList<Map>();
				for(AfGoodsDo goodsDo : subjectGoodsList) {
					Map activityGoodsInfo = new HashMap();
					activityGoodsInfo.put("goodName",goodsDo.getName());
					activityGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
					activityGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
					activityGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
					activityGoodsInfo.put("goodsId", goodsDo.getRid());
					activityGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
					activityGoodsInfo.put("source", goodsDo.getSource());
					activityGoodsInfo.put("priceAmount", goodsDo.getPriceAmount());
					activityGoodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
					activityGoodsInfo.put("remark", goodsDo.getRemark());
					activityGoodsInfo.put("activityName", activityName);
					// 如果是分期免息商品，则计算分期
					AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsDo.getRid());
					JSONArray interestFreeArray = null;
			        if(null != afSchemeGoodsDo){
						Long goodsId = goodsDo.getRid();
						AfSchemeGoodsDo  schemeGoodsDo = null;
						try {
							schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
						} catch(Exception e){
							logger.error(e.toString());
						}
						
						if(schemeGoodsDo != null){
							AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
							String interestFreeJson = interestFreeRulesDo.getRuleJson();
							if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
								interestFreeArray = JSON.parseArray(interestFreeJson);
							}
						}
						
					}
			        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
					
					if(nperList!= null){
						activityGoodsInfo.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						activityGoodsInfo.put("nperMap", nperMap);
					}
					activityGoodsList.add(activityGoodsInfo);
				}
				activityInfoMap.put("activityGoodsList", activityGoodsList);
				activityList.add(activityInfoMap);
			}
		
			jsonObj.put("activityList", activityList);
			// 获取精品推荐商品
			List<AfGoodsDo>  qualityGoodsDoList = afSubjectGoodsService.listQualitySubjectGoodsByParentId(parentId);
			List<Map> qualityGoodsList = new ArrayList<Map>();
			for(AfGoodsDo qualityGoods : qualityGoodsDoList) {
				Map qualityGoodsInfo = new HashMap();
				qualityGoodsInfo.put("goodName", qualityGoods.getName());
				qualityGoodsInfo.put("rebateAmount", qualityGoods.getRebateAmount());
				qualityGoodsInfo.put("saleAmount", qualityGoods.getSaleAmount());
				qualityGoodsInfo.put("goodsIcon", qualityGoods.getGoodsIcon());
				qualityGoodsInfo.put("goodsId", qualityGoods.getRid());
				qualityGoodsInfo.put("goodsUrl", qualityGoods.getGoodsUrl());
				qualityGoodsInfo.put("source", qualityGoods.getSource());
				qualityGoodsInfo.put("priceAmount", qualityGoods.getPriceAmount());
				qualityGoodsInfo.put("thumbnailIcon",qualityGoods.getThumbnailIcon());
				qualityGoodsInfo.put("remark",qualityGoods.getRemark());
				qualityGoodsInfo.put("goodsType", "0");
				
				// 如果是分期免息商品，则计算分期
				AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(qualityGoods.getRid());
				JSONArray interestFreeArray = null;
		        if(null != afSchemeGoodsDo){
					Long goodsId = qualityGoods.getRid();
					AfSchemeGoodsDo  schemeGoodsDo = null;
					try {
						schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
					} catch(Exception e){
						logger.error(e.toString());
					}
					
					if(schemeGoodsDo != null){
						AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
						String interestFreeJson = interestFreeRulesDo.getRuleJson();
						if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
							interestFreeArray = JSON.parseArray(interestFreeJson);
						}
					}
				}
		        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
		        		qualityGoods.getSaleAmount(), resource.getValue1(), resource.getValue2());
				
				if(nperList!= null){
					qualityGoodsInfo.put("goodsType", "1");
					Map nperMap = nperList.get(nperList.size() - 1);
					qualityGoodsInfo.put("nperMap", nperMap);
				}
				
				qualityGoodsList.add(qualityGoodsInfo);
			}
			jsonObj.put("qualityGoodsList",qualityGoodsList);
			resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
		}catch(FanbeiException e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
			logger.error("请求失败"+context,e);
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
			logger.error("请求失败"+context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "partActivityInfoByTag", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String partActivityInfoByTag(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 分会场接口
		FanbeiWebContext context = new FanbeiWebContext();
		
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		try {
			context = doWebCheck(request, false);
			String tag = ObjectUtils.toString(request.getParameter("tag"), null);
			if(tag == null || "".equals(tag)) {
				resp = H5CommonResponse.getNewInstance(false, "tag不能为空！");
				return resp.toString();
			}
			// 数据埋点
			request.setAttribute("context", context);
			doMaidianLog(request,H5CommonResponse.getNewInstance(true,"succ"));
			
			//获取借款分期配置信息
	        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	        JSONArray array = JSON.parseArray(resource.getValue());
	        //删除2分期
	        if (array == null) {
	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	        }
	        //removeSecondNper(array);
			
			JSONObject jsonObj = new JSONObject();
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.GoodsInfo.getCode();
			//根据tag获取modelId
			Long modelId = 0L;
			String type = "PARTACTIVITY_H5_TEMPLATE";
			AfModelH5Do afModelH5Do = afModelH5Service.getByTagAndType(type,tag);
			if(afModelH5Do != null){
			    modelId = afModelH5Do.getRid();
			}
			
			// 根据modelId 取优惠券信息
			List<AfCouponDto> couponList = afCouponService.getCouponByActivityIdAndType(modelId,
					ActivityType.ACTIVITY_TEMPLATE.getCode());
			String userName = context.getUserName();
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			
			for(AfCouponDto couponDto : couponList) {
				// 判断用户是否领
				if(userDo == null) {
					couponDto.setUserAlready(0);
				} else {
					int pickCount = afUserCouponService.getUserCouponByUserIdAndCouponId(userDo.getRid(), couponDto.getRid());
					couponDto.setUserAlready(pickCount);
				}
			}
			jsonObj.put("couponList", couponList);
						
			jsonObj.put("notifyUrl", notifyUrl);
			// 根据modelId查询banner信息
			List<AfModelH5ItemDo> bannerList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(modelId, "BANNER");
			if(bannerList != null && bannerList.size() > 0){
				AfModelH5ItemDo bannerInfo = bannerList.get(0);
				jsonObj.put("bannerImage", bannerInfo.getItemIcon());
			} else {
				resp = H5CommonResponse.getNewInstance(false, "banner信息为空");
				return resp.toString();
			}
			// 查询会场下所有二级会场
			List<AfModelH5ItemDo> subjectList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelTypeSortById(modelId, "SUBJECT");
			List<Map> activityList = new ArrayList<Map>();
			if(subjectList == null || subjectList.size() == 0){
				resp = H5CommonResponse.getNewInstance(false, "分会场信息为空"); 
				return resp.toString();
			}
			AfModelH5ItemDo subjectH5ItemDo = subjectList.get(0);
			String secSubjectId = subjectH5ItemDo.getItemValue();
			AfSubjectDo  parentSubjectDo = afSubjectService.getSubjectInfoById(secSubjectId);
			Long parentId = parentSubjectDo.getParentId();
			String subjectName  = parentSubjectDo.getName();
			jsonObj.put("modelName", subjectName); // 主会场名称
			
			for(AfModelH5ItemDo subjectDo : subjectList) {
				Map activityInfoMap = new HashMap();
				String subjectId = subjectDo.getItemValue();
				// 查询会场信息
				AfSubjectDo subjectInfo = afSubjectService.getSubjectInfoById(subjectId);
				if(subjectInfo == null) {
					resp = H5CommonResponse.getNewInstance(false, "会场不存在id=" + subjectId);
					return resp.toString();
				}
				activityInfoMap.put("name", subjectInfo.getName());
				activityInfoMap.put("subjectId", subjectInfo.getId());
				// 获取一级会场名称
				AfSubjectDo parentSubjectInfo = afSubjectService.getParentSubjectInfoById(subjectId);
				String activityName = "";
				if(parentSubjectInfo != null){
					activityName = parentSubjectInfo.getName();
				}
				
				// 查询会场下所有商品信息
				List<AfGoodsDo> subjectGoodsList = afSubjectGoodsService.listAllSubjectGoodsV1(subjectId);
				List<Map> activityGoodsList  = new ArrayList<Map>();
				for(AfGoodsDo goodsDo : subjectGoodsList) {
					Map activityGoodsInfo = new HashMap();
					activityGoodsInfo.put("goodName",goodsDo.getName());
					activityGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
					activityGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
					activityGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
					activityGoodsInfo.put("goodsId", goodsDo.getRid());
					activityGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
					activityGoodsInfo.put("source", goodsDo.getSource());
					activityGoodsInfo.put("priceAmount", goodsDo.getPriceAmount());
					activityGoodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
					activityGoodsInfo.put("remark", goodsDo.getRemark());
					activityGoodsInfo.put("activityName", activityName);
					// 如果是分期免息商品，则计算分期
					AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsDo.getRid());
					JSONArray interestFreeArray = null;
			        if(null != afSchemeGoodsDo){
						Long goodsId = goodsDo.getRid();
						AfSchemeGoodsDo  schemeGoodsDo = null;
						try {
							schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
						} catch(Exception e){
							logger.error(e.toString());
						}
						
						if(schemeGoodsDo != null){
							AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
							String interestFreeJson = interestFreeRulesDo.getRuleJson();
							if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
								interestFreeArray = JSON.parseArray(interestFreeJson);
							}
						}
						
					}
			        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
					
					if(nperList!= null){
						activityGoodsInfo.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						activityGoodsInfo.put("nperMap", nperMap);
					}
					activityGoodsList.add(activityGoodsInfo);
				}
				activityInfoMap.put("activityGoodsList", activityGoodsList);
				activityList.add(activityInfoMap);
			}
		
			jsonObj.put("activityList", activityList);
			// 获取精品推荐商品
			List<AfGoodsDo>  qualityGoodsDoList = afSubjectGoodsService.listQualitySubjectGoodsByParentId(parentId);
			List<Map> qualityGoodsList = new ArrayList<Map>();
			for(AfGoodsDo qualityGoods : qualityGoodsDoList) {
				Map qualityGoodsInfo = new HashMap();
				qualityGoodsInfo.put("goodName", qualityGoods.getName());
				qualityGoodsInfo.put("rebateAmount", qualityGoods.getRebateAmount());
				qualityGoodsInfo.put("saleAmount", qualityGoods.getSaleAmount());
				qualityGoodsInfo.put("goodsIcon", qualityGoods.getGoodsIcon());
				qualityGoodsInfo.put("goodsId", qualityGoods.getRid());
				qualityGoodsInfo.put("goodsUrl", qualityGoods.getGoodsUrl());
				qualityGoodsInfo.put("source", qualityGoods.getSource());
				qualityGoodsInfo.put("priceAmount", qualityGoods.getPriceAmount());
				qualityGoodsInfo.put("thumbnailIcon",qualityGoods.getThumbnailIcon());
				qualityGoodsInfo.put("remark",qualityGoods.getRemark());
				qualityGoodsInfo.put("goodsType", "0");
				
				// 如果是分期免息商品，则计算分期
				AfSchemeGoodsDo afSchemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(qualityGoods.getRid());
				JSONArray interestFreeArray = null;
		        if(null != afSchemeGoodsDo){
					Long goodsId = qualityGoods.getRid();
					AfSchemeGoodsDo  schemeGoodsDo = null;
					try {
						schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
					} catch(Exception e){
						logger.error(e.toString());
					}
					
					if(schemeGoodsDo != null){
						AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
						String interestFreeJson = interestFreeRulesDo.getRuleJson();
						if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
							interestFreeArray = JSON.parseArray(interestFreeJson);
						}
					}
				}
		        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
		        		qualityGoods.getSaleAmount(), resource.getValue1(), resource.getValue2());
				
				if(nperList!= null){
					qualityGoodsInfo.put("goodsType", "1");
					Map nperMap = nperList.get(nperList.size() - 1);
					qualityGoodsInfo.put("nperMap", nperMap);
				}
				
				qualityGoodsList.add(qualityGoodsInfo);
			}
			jsonObj.put("qualityGoodsList",qualityGoodsList);
			resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
		}catch(FanbeiException e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
			logger.error("请求失败"+context,e);
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
			logger.error("请求失败"+context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "subjectGoodsInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String subjectGoodsInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 分会场接口
		FanbeiWebContext context = new FanbeiWebContext();
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		try {
			//context = doWebCheck(request, false);
			String subjectId = ObjectUtils.toString(request.getParameter("subjectId"), null);
			if(subjectId == null || "".equals(subjectId)) {
				resp = H5CommonResponse.getNewInstance(false, "会场id不能为空！");
				return resp.toString();
			}
			AfSubjectGoodsQuery  query = buildAfSubjectGoodsQuery(request);
			//  数据埋点
			request.setAttribute("context", context);
			doMaidianLog(request,resp);
			//获取借款分期配置信息
	        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	        JSONArray array = JSON.parseArray(resource.getValue());
	        //删除2分期
	        if (array == null) {
	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	        }
	        //removeSecondNper(array);
			List<AfGoodsDo> goodsList = afSubjectGoodsService.listAllSubjectGoods(query);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("totalCount", query.getTotalCount());
			String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.GoodsInfo.getCode();
			jsonObj.put("notifyUrl", notifyUrl);
			List<Map> subjectGoodsList = new ArrayList<Map>();
			for(AfGoodsDo goodsDo : goodsList) {
				Map subjectGoodsInfo = new HashMap();
				subjectGoodsInfo.put("goodName", goodsDo.getName());
				subjectGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
				subjectGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
				subjectGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
				subjectGoodsInfo.put("goodsId", goodsDo.getRid());
				subjectGoodsInfo.put("priceAmount", goodsDo.getPriceAmount());
				subjectGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
				subjectGoodsInfo.put("thumbnailIcon",goodsDo.getThumbnailIcon());
				subjectGoodsInfo.put("remark",goodsDo.getRemark());
				subjectGoodsInfo.put("goodsType", "0");
				subjectGoodsList.add(subjectGoodsInfo);
				String tags = goodsDo.getTags();
				// 如果是分期免息商品，则计算分期
				if(tags != null && tags.contains("INTEREST_FREE")){
					Long goodsId = goodsDo.getRid();
					AfSchemeGoodsDo schemeGoodsDo = null;
					try {
						schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
					} catch(Exception e){
						logger.error(e.toString());
					}
					JSONArray interestFreeArray = null;
					if(schemeGoodsDo != null){
						AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
						String interestFreeJson = interestFreeRulesDo.getRuleJson();
						if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
							interestFreeArray = JSON.parseArray(interestFreeJson);
						}
					}
					List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
					
					if(nperList!= null){
						subjectGoodsInfo.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						subjectGoodsInfo.put("nperMap", nperMap);
					}
					
				}
			}
			jsonObj.put("subjectGoodsList", subjectGoodsList);
			resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
		}catch(FanbeiException e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
			logger.error("请求失败"+context,e);
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
			logger.error("请求失败"+context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	
	@RequestMapping(value = "qualityGoodsStatistics", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String qualityGoodsStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 统计精品推荐商品点击量
		FanbeiWebContext context = new FanbeiWebContext();
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		try{
			//context = doWebCheck(request, false);
			String goodsId = ObjectUtils.toString(request.getParameter("goodsId"), null);
			if(goodsId == null || "".equals(goodsId)) {
				return H5CommonResponse.getNewInstance(false, "商品Id不能为空！").toString();
			}
			AfGoodsDo goodsInfo = afGoodsService.getGoodsById(Long.parseLong(goodsId));
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, goodsInfo.toString()));
			resp = H5CommonResponse.getNewInstance(true, "成功");
		}catch(FanbeiException e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", e.getErrorCode().getDesc());
			logger.error("请求失败"+context,e);
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
			logger.error("请求失败"+context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	@RequestMapping(value = "categoryGoodsStatistics", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String categoryGoodsStatistics(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 统计二级会场点击量
		Calendar calStart = Calendar.getInstance();
		FanbeiWebContext context = new FanbeiWebContext();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		try{
			String subjectId = ObjectUtils.toString(request.getParameter("subjectId"), null);
			if(subjectId == null || "".equals(subjectId)) {
				return H5CommonResponse.getNewInstance(false, "会场Id不能为空！").toString();
			}
			AfSubjectDo subjectInfo = afSubjectService.getSubjectInfoById(subjectId);
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, subjectInfo.toString()));
			resp = H5CommonResponse.getNewInstance(true, "成功");
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "请求失败", "", "");
			logger.error("请求失败",e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	
	private AfSubjectGoodsQuery buildAfSubjectGoodsQuery(HttpServletRequest request) {
		AfSubjectGoodsQuery query = new AfSubjectGoodsQuery();
		String subjectId = ObjectUtils.toString(request.getParameter("subjectId"), null);
		Integer currentPage = NumberUtil.objToIntDefault(request.getParameter("currentPage"), 1);
		query.setSubjectId(Long.parseLong(subjectId));
		query.setPageNo(currentPage);
		query.setPageSize(20);
		query.setFull(true);
		return query;
	}
	

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
  	  try {
          RequestDataVo reqVo = new RequestDataVo();
          
          JSONObject jsonObj = JSON.parseObject(requestData);
          reqVo.setId(jsonObj.getString("id"));
          reqVo.setMethod(request.getRequestURI());
          reqVo.setSystem(jsonObj);
          return reqVo;
      } catch (Exception e) {
          throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
      }
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		return null;
	}
}