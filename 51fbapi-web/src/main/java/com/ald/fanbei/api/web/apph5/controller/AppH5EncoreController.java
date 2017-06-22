package com.ald.fanbei.api.web.apph5.controller;
 
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfActivityGoodsService;
import com.ald.fanbei.api.biz.service.AfActivityModelService;
import com.ald.fanbei.api.biz.service.AfActivityService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfActivityGoodsDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 
/**
 * @类描述：
 * 返场活动
 * @author 江荣波 2017年6月20日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
@SuppressWarnings("unchecked")
public class AppH5EncoreController extends BaseController {
	
	@Resource
	AfActivityModelService afActivityModelService;
	@Resource
	AfActivityGoodsService afActivityGoodsService;
	@Resource
	AfActivityService afActivityService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	
    String  opennative = "/fanbei-web/opennative?name=";
    
    @RequestMapping(value = "encoreActivityInfo", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String encoreActivityInfo(HttpServletRequest request, ModelMap model) throws IOException {
    	Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 0);
    	if(activityId == 0) {
    		return H5CommonResponse.getNewInstance(false, "请上送活动id").toString();
    	}
    	try{
    		JSONObject jsonObj = new JSONObject();
        	String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.GoodsInfo.getCode();
    		jsonObj.put("notifyUrl", notifyUrl);
    		// 获取活动信息
    		AfActivityDo activityInfo =afActivityService.getActivityById(activityId);
    		if(activityInfo == null) {
    			return H5CommonResponse.getNewInstance(false, "活动信息不存在！id=" + activityId).toString();
    		}
    		jsonObj.put("bannerUrl", activityInfo.getIconUrl());
    		jsonObj.put("currentTime", System.currentTimeMillis());
    		jsonObj.put("validStartTime", activityInfo.getGmtStart().getTime());
    		jsonObj.put("validEndTime", activityInfo.getGmtEnd().getTime());
    		//获取借款分期配置信息
            AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
            JSONArray array = JSON.parseArray(resource.getValue());
            //删除2分期
            if (array == null) {
                throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
            }
            removeSecondNper(array);
            // 查询活动商品信息
    		List<AfActivityGoodsDto> activityGoodsDtoList = afActivityGoodsService.listActivityGoodsByActivityId(activityId);
    		List activityGoodsList  = new ArrayList();
    		for(AfActivityGoodsDto activityGoodsDto : activityGoodsDtoList) {
    			Map<String,Object> activityGoodsInfo = new HashMap<String,Object>();
    			activityGoodsInfo.put("goodsName",activityGoodsDto.getName());
    			activityGoodsInfo.put("specialAmount", activityGoodsDto.getSpecialPrice());
    			activityGoodsInfo.put("rebateAmount", activityGoodsDto.getRebateAmount());
    			activityGoodsInfo.put("saleAmount", activityGoodsDto.getSaleAmount());
    			activityGoodsInfo.put("repertoryCount", activityGoodsDto.getGoodsCount());
    			activityGoodsInfo.put("startTime", activityGoodsDto.getStartTime().getTime());
    			activityGoodsInfo.put("validStartTime", activityGoodsDto.getValidStart().getTime());
    			activityGoodsInfo.put("validEndTime", activityGoodsDto.getValidEnd().getTime());
    			activityGoodsInfo.put("goodsType", "0");
    			activityGoodsInfo.put("goodsIcon", activityGoodsDto.getGoodsIcon());
    			activityGoodsInfo.put("goodsId", activityGoodsDto.getRid());
    			activityGoodsInfo.put("thumbnailIcon", activityGoodsDto.getThumbnailIcon());
    			activityGoodsInfo.put("goodsUrl", activityGoodsDto.getGoodsUrl());
    			// 如果是分期免息商品，则计算分期
				Long goodsId = activityGoodsDto.getRid();
				AfSchemeGoodsDo schemeGoodsDo = null;
				try {
					schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
				} catch(Exception e){
					logger.error(e.toString());
				}
				if(schemeGoodsDo != null){
					AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
					String interestFreeJson = interestFreeRulesDo.getRuleJson();
					JSONArray interestFreeArray = null;
					if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
						interestFreeArray = JSON.parseArray(interestFreeJson);
					}
					List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							activityGoodsDto.getSaleAmount(), resource.getValue1(), resource.getValue2());
					
					if(nperList!= null){
						activityGoodsInfo.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						activityGoodsInfo.put("nperMap", nperMap);
					}
				}
    			activityGoodsList.add(activityGoodsInfo);
    		}
    		jsonObj.put("activityGoodsList", activityGoodsList);
    		// 获取非活动商品
    		List<AfGoodsDo> recommendGoodsDoList = afActivityGoodsService.listRecommendGoodsByActivityId(activityId);
    		List recommendGoodsList = new ArrayList();
    		for(AfGoodsDo goodsDo : recommendGoodsDoList) {
    			Map recommendGoodsInfo = new HashMap();
    			recommendGoodsInfo.put("goodName",goodsDo.getName());
    			recommendGoodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
    			recommendGoodsInfo.put("saleAmount", goodsDo.getSaleAmount());
    			recommendGoodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
    			recommendGoodsInfo.put("goodsId", goodsDo.getRid());
    			recommendGoodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
    			recommendGoodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
    			recommendGoodsInfo.put("goodsType", "0");
    			String tags = goodsDo.getTags();
    			// 如果是分期免息商品，则计算分期
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
					JSONArray interestFreeArray = null;
					if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
						interestFreeArray = JSON.parseArray(interestFreeJson);
					}
					List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							goodsDo.getSaleAmount(), resource.getValue1(), resource.getValue2());
					
					if(nperList!= null){
						recommendGoodsInfo.put("goodsType", "1");
						Map nperMap = nperList.get(nperList.size() - 1);
						recommendGoodsInfo.put("nperMap", nperMap);
					}
				}
    			recommendGoodsList.add(recommendGoodsInfo);
    		}
        	
        	jsonObj.put("recommendGoodsList", recommendGoodsList);
        	
        	return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString(); 
    	} catch (Exception e){
    		return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
    	}
    	
    }
    
    private void removeSecondNper(JSONArray array) {
        if (array == null) {
            return;
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
                it.remove();
                break;
            }
        }

    }
    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }
    
    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }
 
    @Override
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
 
}