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
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSchemeGoodsService;
import com.ald.fanbei.api.biz.service.AfShareGoodsService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.CouponActivityType;
import com.ald.fanbei.api.common.enums.CouponCateGoryType;
import com.ald.fanbei.api.common.enums.CouponWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestFreeRulesDo;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfShareGoodsDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfFreshmanGoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**  
 * @Title: AppH5FreshmanShare.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: 新人专项活动
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年11月1日 下午1:31:31
 * @version V1.0  
 */
@RestController
@RequestMapping(value = "/activity/freshmanShare", produces = "application/json;charset=UTF-8")
public class AppH5FreshmanShare extends BaseController{
	
	
	@Resource
	AfUserService afUserService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfShareGoodsService afShareGoodsService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfInterestFreeRulesService afInterestFreeRulesService;
	@Resource
	AfModelH5ItemService afModelH5ItemService;
	@Resource 
	AfSchemeGoodsService afSchemeGoodsService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfCouponCategoryService afCouponCategoryService;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	String opennative = "/fanbei-web/opennative?name=";
	
	/**
	 * 
	* @Title: share
	* @Description: 主页面
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/homePage", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String homePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		String result = "";
		try {
			doWebCheck(request, false);
			
			String cacheKey =  "freshman:share:homePage:goodsList";  
		 
			List<AfFreshmanGoodsVo> resultList =  bizCacheUtil.getObjectList(cacheKey);
			List<AfFreshmanGoodsVo> list = new ArrayList<AfFreshmanGoodsVo>();
			if (resultList  == null) {
			  List<AfShareGoodsDto> shareGoods =  afShareGoodsService.getShareGoods();
			
			   if(shareGoods!=null){
				for (AfShareGoodsDto afShareGoodsDto : shareGoods) {
					AfFreshmanGoodsVo afFreshmanGoodsVo = new AfFreshmanGoodsVo();
					afFreshmanGoodsVo.setNumId(String.valueOf(afShareGoodsDto.getRid()));
					afFreshmanGoodsVo.setDecreasePrice(afShareGoodsDto.getDecreasePrice());
					afFreshmanGoodsVo.setSaleAmount(afShareGoodsDto.getPriceAmount().toString());
					afFreshmanGoodsVo.setRealAmount(afShareGoodsDto.getSaleAmount().toString());
					afFreshmanGoodsVo.setRebateAmount(afShareGoodsDto.getRebateAmount().toString());
					afFreshmanGoodsVo.setGoodsName(afShareGoodsDto.getName());
					afFreshmanGoodsVo.setGoodsIcon(afShareGoodsDto.getGoodsIcon());
					afFreshmanGoodsVo.setThumbnailIcon(afShareGoodsDto.getThumbnailIcon());
					afFreshmanGoodsVo.setGoodsUrl(afShareGoodsDto.getGoodsDetail().split(";")[0]);
					afFreshmanGoodsVo.setOpenId(afShareGoodsDto.getOpenId());
					afFreshmanGoodsVo.setSource(afShareGoodsDto.getSource());
					list.add(afFreshmanGoodsVo);
				}
			}
			
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	                JSONArray array = JSON.parseArray(resource.getValue());
	        
        	        //删除2分期
        	        if (array == null) {
        	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        	        }
        	        //removeSecondNper(array);
			
			for (AfFreshmanGoodsVo goodsInfo : list) {
                          List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, null, BigDecimal.ONE.intValue(),
                          new BigDecimal(goodsInfo.getSaleAmount()), resource.getValue1(), resource.getValue2(),goodsInfo.getGoodsId(),"0");
                          if (nperList != null) {
                          Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                         goodsInfo.setNperMap(nperMap);
                          }
                       }
			resultList = list;
			bizCacheUtil.saveObjectListExpire(cacheKey, list, Constants.SECOND_OF_TEN_MINITS);
	}
			
			
			logger.info(JSON.toJSONString(resultList));
			data.put("goodsList", resultList);
			result = H5CommonResponse.getNewInstance(true, "获取商品列表成功", null, data).toString();
			
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("/activity/freshmanShareH5/homePage error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取商品列表失败", null, "").toString();
		}
		
		return result;
	}
	@ResponseBody
	@RequestMapping(value = "/pickCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickCoupon(HttpServletRequest request, ModelMap model) throws IOException {

		doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			AfUserDo afUserDo = afUserService.getUserByUserName(context.getUserName());
			Map<String, Object> returnData = new HashMap<String, Object>();

			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				returnData.put("status", CouponWebFailStatus.UserNotexist.getCode());
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(),
						notifyUrl, returnData).toString();
			}
			
                       //1. if没有通过强风控，跳转认证，银行卡。。
			
			  AfUserAuthDo afUserAuthDo  = afUserAuthService.getUserAuthInfoByUserId(afUserDo.getRid());
			  if(!"Y".equals(afUserAuthDo.getBankcardStatus())){
			      String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
					+ H5OpenNativeType.DoScanId.getCode();
			      returnData.put("status", H5OpenNativeType.DoScanId.getCode());
			      return H5CommonResponse.getNewInstance(false,"请完成身份证认证",notifyUrl, returnData).toString();
	        	   }
			  if(!"Y".equals(afUserAuthDo.getRiskStatus())){
			      String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
					+ H5OpenNativeType.DoPromoteBasic.getCode();
			      returnData.put("status", H5OpenNativeType.DoPromoteBasic.getCode());
			      return H5CommonResponse.getNewInstance(false, "请完成基础认证",notifyUrl, returnData).toString();
			  }
			  //1.若是老用户不可领取
			  int count = afOrderService.getOldUserOrderAmount(afUserDo.getRid());
			  if(count >0){
			      returnData.put("status", FanbeiExceptionCode.NO_NEW_USER.getCode());
			      return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.NO_NEW_USER.getDesc(),
					"", returnData).toString();
			  }
			
			String tag = CouponCateGoryType._EXCLUSIVE_CREDIT_.getCode();
			String sourceType = CouponActivityType.EXCLUSIVE_CREDIT.getCode();
			
		        int countNum =  afUserCouponService.getUserCouponByUserIdAndCouponCource(afUserDo.getRid(), sourceType);
		        //2.该用户是否拥有该类型优惠券
		        if(countNum >0){
		          returnData.put("status",FanbeiExceptionCode.USER_GET_TO_COUPON_CENTER.getCode());
		          return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_GET_TO_COUPON_CENTER.getDesc(),
						"", returnData).toString();
			}
		       
			String msg =  afUserCouponService.sentUserCouponGroup(afUserDo.getRid(), tag,sourceType);
			if("LEAD_END".equals(msg)){
			    returnData.put("status",FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR.getCode());
			    return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_PICK_OVER_ERROR.getDesc(),
					"", returnData).toString();
			}
			return H5CommonResponse.getNewInstance(true, "领取成功，可前往我的优惠券中查看", "", null).toString();
		} catch (Exception e) {
			logger.error("pick coupon error", e);
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}
	}

	
	/**
	 * 
	* @Title: explosiveGoods
	* @Description: 首单爆品
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@ResponseBody
	@RequestMapping(value = "/explosiveGoods", method = RequestMethod.POST,produces = "text/html;charset=UTF-8")
	public String explosiveGoods(HttpServletRequest request, HttpServletResponse response) {
		
		String result = "";
		try {
			doWebCheck(request, false);
			String cacheKey =  "freshman:share:explosiveGoods";  
			List<Map<String,Object>> itemList = bizCacheUtil.getObjectList(cacheKey);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			if (itemList  == null) {
			
			//查询爆款信息
			List<AfModelH5ItemDo>  afModelH5ItemList = afModelH5ItemService.getModelH5ItemCategoryByModelTag(CouponActivityType.FIRST_SINGLE.getCode());
			List<AfCouponDo> couponList = new ArrayList<AfCouponDo>();
			//List<AfCouponDo> list = afCouponService.getByActivityType(CouponActivityType.FIRST_SINGLE.getCode());
			AfCouponCategoryDo  couponCategory  = afCouponCategoryService.getCouponCategoryByTag(CouponCateGoryType._FIRST_SINGLE_.getCode());
			if(couponCategory != null){
			    	String coupons = couponCategory.getCoupons();
				JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
				for (int i = 0; i < couponsArray.size(); i++) {
					String couponId = (String) couponsArray.getString(i);
					AfCouponDo couponDo = afCouponService.getCouponById(Long.parseLong(couponId));
					if (couponDo != null) {
					    couponList.add(couponDo);
					}
				  }
			}
			
			for(AfModelH5ItemDo afModelH5ItemDo:afModelH5ItemList){
			    String itemName = afModelH5ItemDo.getItemValue();
			    Map<String, Object> data = new HashMap<String, Object>();
			    List<Map<String,Object>> goodsList = new ArrayList<Map<String,Object>>();
			    List<AfGoodsDo> afGoodsList = afGoodsService.getGoodsByModelId( Long.valueOf(afModelH5ItemDo.getRid()));
			  
			    
			    if(afGoodsList.size()>0){
				//获取借款分期配置信息
			        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
			        JSONArray array = JSON.parseArray(resource.getValue());
			        //删除2分期
			        if (array == null) {
			            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
			        }
			       // removeSecondNper(array);
				
			        //商品展示信息要去掉最大的可用优惠券金额，
				for(AfGoodsDo goodsDo : afGoodsList) {
				        //获取商
				         //遍历list,查询满足条件优惠券。（大于满减，且最大优惠金额）
				        BigDecimal temp = new BigDecimal(0.00);
				        BigDecimal couponLimitAmount = new BigDecimal(0.00);
				        BigDecimal couponAmount = new BigDecimal(0.00);
				      
				        for(int i=0;i<couponList.size();i++){
				            if(goodsDo.getSaleAmount().compareTo(couponList.get(i).getLimitAmount())>0 || goodsDo.getSaleAmount().compareTo(couponList.get(i).getLimitAmount())==0){
				        	temp = couponList.get(i).getAmount();
				        	if(temp.compareTo(couponAmount)>0 ){
				        	    couponAmount = temp;
				        	    couponLimitAmount =  couponList.get(i).getLimitAmount();
				        	}
				            }
				        }
				        BigDecimal afterUseCouponAmount = goodsDo.getSaleAmount().subtract(couponAmount);
				        
		    			Map<String, Object> goodsInfo = new HashMap<String, Object>();
		    			goodsInfo.put("goodName",goodsDo.getName());
		    			goodsInfo.put("rebateAmount", goodsDo.getRebateAmount());
		    			goodsInfo.put("saleAmount", goodsDo.getSaleAmount());
		    			goodsInfo.put("afterUseCouponAmount",afterUseCouponAmount);
		    			goodsInfo.put("couponLimitAmount",couponLimitAmount);
		    			goodsInfo.put("couponAmount",couponAmount);
		    			goodsInfo.put("priceAmount", goodsDo.getPriceAmount());
		    			goodsInfo.put("goodsIcon", goodsDo.getGoodsIcon());
		    			goodsInfo.put("goodsId", goodsDo.getRid());
		    			goodsInfo.put("goodsUrl", goodsDo.getGoodsUrl());
		    			goodsInfo.put("thumbnailIcon", goodsDo.getThumbnailIcon());
		    			goodsInfo.put("source", goodsDo.getSource());
		    			goodsInfo.put("goodsType", "0");
		    			goodsInfo.put("remark", StringUtil.null2Str(goodsDo.getRemark()));
		    			// 如果是分期免息商品，则计算分期
		    			Long goodsId = goodsDo.getRid();
						AfSchemeGoodsDo  schemeGoodsDo = null;
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
					        //商品展示信息要去掉最大的可用优惠券金额，getSaleAmount() - 优惠券金额 = afterUseCouponAmount
						List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
							afterUseCouponAmount, resource.getValue1(), resource.getValue2(),goodsId,"0");
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
			 }
			         data.put("goodsList", goodsList);
				 data.put("itemName",itemName);
				 list.add(data);
				 itemList = list;
				bizCacheUtil.saveObjectListExpire(cacheKey, itemList,  Constants.SECOND_OF_TEN_MINITS);
				 
			       
			 }
		    }
			 
			result = H5CommonResponse.getNewInstance(true, "获取首单爆款商品成功", "", itemList).toString();
			} catch (Exception exception) {
				result = H5CommonResponse.getNewInstance(false, "获取首单爆款商品失败", "", exception.getMessage()).toString();
				logger.error("获取特卖商品失败  e = {} , resultStr = {}", exception, result);
				doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),result);
			}
			return result;
	}
	
	
	
	/**
	 * 
	* @Title: isNew
	* @Description: 验证是否是新用户
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/isNew", method = RequestMethod.POST)
	public String isNew(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		
		try {
				context = doWebCheck(request, false);
				String userName = context.getUserName();
				Long userId = convertUserNameToUserId(userName);
				if (null != userId){
				/*//根据用户名查询用户信息
				AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
				logger.info(JSON.toJSONString(userInfo));*/
				//查询用户订单数
				int oldUserOrderAmount = afOrderService.getOldUserOrderAmount(userId);
				if(oldUserOrderAmount==0){
					data.put("isNew", "Y");//新用户
				}else{
					data.put("isNew", "N");//老用户
				}
				logger.info(JSON.toJSONString(data));
				
				resultStr = H5CommonResponse.getNewInstance(true, "验证是否是新用户成功", null, data).toString();
				return resultStr;
			}else{
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			logger.error("/activity/freshmanShare/isNew" + context + "login error ");
			resultStr = H5CommonResponse.getNewInstance(false, "没有登录", null, data).toString();
			}
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
					|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			logger.error("/activity/freshmanShare/isNew" + context + "login error ");
			resultStr = H5CommonResponse.getNewInstance(false, "没有登录", null, data).toString();
			}
		}catch (Exception e) {
			logger.error("/activity/freshmanShare/isNew" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "验证是否是新用户失败").toString();
			return resultStr;
		}
		
		return resultStr;
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
	

	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;
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
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
