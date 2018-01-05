package com.ald.fanbei.api.web.apph5.controller;

import static org.hamcrest.CoreMatchers.nullValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.CouponWebFailStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSFgoodsVo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**  
 * @Title: AppH5SFSubscribe.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2018年1月2日 上午11:17:14
 * @version V1.0  
 */
@RestController
@RequestMapping(value = "/appH5SF", produces = "application/json;charset=UTF-8")
public class AppH5SFSubscribeController  extends BaseController{
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;
	@Resource
	AfGoodsDoubleEggsUserService afGoodsDoubleEggsUserService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfShopService afShopService;
	
	String opennative = "/fanbei-web/opennative?name=";
	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 *         Long @throws
	 */
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
	/**
	 * 
	* @Title: initHomePage
	* @author qiao
	* @date 2018年1月5日 下午1:39:36
	* @Description: 
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/initHomePage",method = RequestMethod.POST )
	public String initHomePage(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		FanbeiWebContext context = new FanbeiWebContext();
		java.util.Map<String, Object> data = new HashMap<>();
		
		try {
			context = doWebCheck(request, false);
			if (context != null) {

				String userName = context.getUserName();
				//init the userId for the interface : getFivePictures
				Long userId = 0L;
				
				String huoche = "HUOCHE";
				String jipiao = "JIPIAO";

				//get shop id for plane and train 
				AfShopDo huocheDo = new AfShopDo();
				huocheDo.setType(huoche);
				huocheDo = afShopService.getShopInfoBySecTypeOpen(huocheDo);
				if (huocheDo != null) {
					data.put("trainShopId", huocheDo.getRid());
				}
				
				AfShopDo jipiaoDo = new AfShopDo();
				jipiaoDo.setType(huoche);
				jipiaoDo = afShopService.getShopInfoBySecTypeOpen(jipiaoDo);
				if (jipiaoDo != null) {
					data.put("planeShopId", jipiaoDo.getRid());
				}
				
				//if login then 
				if (StringUtil.isNotBlank(userName)) {
					userId = convertUserNameToUserId(userName);
				}
				
				
				//get goods to subscribe 
				List<AfSFgoodsVo> goodsList = afGoodsDoubleEggsService.getFivePictures(userId);
				data.put("goodsList", goodsList);
				logger.info("/appH5SF/initHomePage userId={} , goodsList={} ", new Object[]{userId,goodsList});
				//get configuration from afresource
				AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY", "INIT_HOME_PAGE");
				if (resourceDo != null) {
					data.put("describtion", resourceDo.getValue2());
					data.put("strategy_img", resourceDo.getValue());
					data.put("strategy_redirect_img", resourceDo.getValue1());
				}
				logger.info("/appH5SF/initHomePage userId={} , goodsList={} ,resourceDo = {}", new Object[]{userId,goodsList,resourceDo});
				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
			}
			
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}

	/**
	 * 
	* @Title: getBrandUrl
	* @author qiao
	* @date 2018年1月5日 下午1:39:29
	* @Description: 
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/getBrandUrl",method = RequestMethod.POST )
	public String getBrandUrl(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		java.util.Map<String, Object> data = new HashMap<>();
		
		try {
			context = doWebCheck(request, true);
			if (context != null) {
				
				
				Long shopId = NumberUtil.objToLongDefault(request.getParameter("shopId"), null);

				if (shopId == null) {
				    logger.error("shopId is empty");
				    return H5CommonResponse.getNewInstance(false, "参数shopId为空").toString();
				}

				AfShopDo shopInfo = afShopService.getShopById(shopId);
				if (shopInfo == null) {
				    logger.error("shopId is invalid");
				    return H5CommonResponse.getNewInstance(false, "参数shopId无效").toString();
				}
				String userName = context.getUserName();
				Long userId = convertUserNameToUserId(userName);
				String shopUrl = afShopService.parseBoluomeUrl(shopInfo.getShopUrl(), shopInfo.getPlatformName(), shopInfo.getType(), userId, userName);

				data.put("shopUrl", shopUrl);
				logger.info("/appH5SF/getBrandUrl param: userId={} , shopUrl ={}",userId,shopUrl);
				resultStr = H5CommonResponse.getNewInstance(true, "获取波罗蜜url成功", "", data).toString();
			}
			
		}catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
				return resultStr.toString();
			}
		}catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "获取波罗蜜url失败", "", exception.getMessage()).toString();
			logger.error("获取波罗蜜url失败  e = {} , resultStr = {}", exception, resultStr);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), resultStr);
		}
		return resultStr;
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
