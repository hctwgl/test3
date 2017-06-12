package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSubjectGoodsService;
import com.ald.fanbei.api.biz.service.AfSubjectService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSubjectDo;
import com.ald.fanbei.api.dal.domain.query.AfSubjectGoodsQuery;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
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
	
	@Resource
	AfResourceService afResourceService;
	
	@Resource
	AfSubjectGoodsService afSubjectGoodsService;
	
	@Resource
	AfModelH5ItemService  afModelH5ItemService;
	
	@Resource
	AfSubjectService afSubjectService;
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "mainActivityInfo", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String mainActivityInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 主会场接口
		
		// FanbeiWebContext context = new FanbeiWebContext();
		//context = doWebCheck(request, false);
		JSONObject jsonObj = new JSONObject();
		
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
			qualityGoodsInfo.put("thumbnailIcon",qualityGoods.getThumbnailIcon());
			qualityGoodsList.add(qualityGoodsInfo);
		}
		jsonObj.put("qualityGoodsList",qualityGoodsList);
		H5CommonResponse resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
		return resp.toString();
	}
	
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "partActivityInfo", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String partActivityInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 分会场接口
		FanbeiWebContext context = new FanbeiWebContext();
		//context = doWebCheck(request, false);
		String modelId = ObjectUtils.toString(request.getParameter("modelId"), null);
		if(modelId == null || "".equals(modelId)) {
			return H5CommonResponse.getNewInstance(false, "模版id不能为空！").toString();
		}
		
		JSONObject jsonObj = new JSONObject();
		// 根据modelId查询banner信息
		List<AfModelH5ItemDo> bannerList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(Long.parseLong(modelId), "BANNER");
		if(bannerList != null && bannerList.size() > 0){
			AfModelH5ItemDo bannerInfo = bannerList.get(0);
			jsonObj.put("bannerImage", bannerInfo.getItemIcon());
		} else {
			return H5CommonResponse.getNewInstance(false, "banner信息为空").toString();
		}
		// 查询会场下所有二级会场
		List<AfModelH5ItemDo> subjectList =  afModelH5ItemService.getModelH5ItemListByModelIdAndModelType(Long.parseLong(modelId), "SUBJECT");
		List<Map> activityList = new ArrayList<Map>();
		if(subjectList == null || subjectList.size() == 0){
			return H5CommonResponse.getNewInstance(false, "分会场信息为空").toString(); 
		}
		for(AfModelH5ItemDo subjectDo : subjectList) {
			Map activityInfoMap = new HashMap();
			String subjectId = subjectDo.getItemValue();
			// 查询会场信息
			AfSubjectDo subjectInfo = afSubjectService.getSubjectInfoById(subjectId);
			if(subjectInfo == null) {
				return H5CommonResponse.getNewInstance(false, "会场不存在id=" + subjectId).toString();
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
				activityGoodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
				activityGoodsInfo.put("activityName", activityName);
				activityGoodsList.add(activityGoodsInfo);
			}
			activityInfoMap.put("activityGoodsList", activityGoodsList);
			activityList.add(activityInfoMap);
		}
	
		jsonObj.put("activityList", activityList);
		// 获取精品推荐商品
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
			qualityGoodsInfo.put("thumbnailIcon",qualityGoods.getThumbnailIcon());
			qualityGoodsList.add(qualityGoodsInfo);
		}
		jsonObj.put("qualityGoodsList",qualityGoodsList);
		H5CommonResponse resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
		return resp.toString();
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "subjectGoodsInfo", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String categoryGoodsInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 分会场接口
		FanbeiWebContext context = new FanbeiWebContext();
		//context = doWebCheck(request, false);
		
		String subjectId = ObjectUtils.toString(request.getParameter("subjectId"), null);
		
		AfSubjectGoodsQuery  query = buildAfSubjectGoodsQuery(request);
		
		if(subjectId == null || "".equals(subjectId)) {
			return H5CommonResponse.getNewInstance(false, "会场id不能为空！").toString();
		}
		List<AfGoodsDo> goodsList = afSubjectGoodsService.listAllSubjectGoods(query);
		
		JSONObject jsonObj = new JSONObject();
		
		List<Map> subjectGoodsList = new ArrayList<Map>();
		for(AfGoodsDo goodsDo : goodsList) {
			Map subjectGoodsInfo = new HashMap();
			subjectGoodsInfo.put("goodName", goodsDo.getName());
			subjectGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
			subjectGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
			subjectGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
			subjectGoodsInfo.put("goodsId", goodsDo.getRid());
			subjectGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
			subjectGoodsInfo.put("thumbnailIcon",goodsDo.getThumbnailIcon());
			subjectGoodsList.add(subjectGoodsInfo);
		}
		jsonObj.put("subjectGoodsList", subjectGoodsList);
		H5CommonResponse resp = H5CommonResponse.getNewInstance(true, "成功", "", jsonObj);
		return resp.toString();
	}
	
	
	private AfSubjectGoodsQuery buildAfSubjectGoodsQuery(HttpServletRequest request) {
		AfSubjectGoodsQuery query = new AfSubjectGoodsQuery();
		String subjectId = ObjectUtils.toString(request.getParameter("subjectId"), null);
		Integer currentPage = NumberUtil.objToIntDefault(request.getParameter("currentPage"), 1);
		query.setSubjectId(Long.parseLong(subjectId));
		query.setPageNo(currentPage);
		query.setPageSize(20);
		query.setFull(false);
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
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		return null;
	}
}