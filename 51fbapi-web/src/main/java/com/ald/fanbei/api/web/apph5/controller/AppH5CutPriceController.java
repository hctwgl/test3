package com.ald.fanbei.api.web.apph5.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsCouponService;
import com.ald.fanbei.api.biz.service.de.AfDeGoodsService;
import com.ald.fanbei.api.biz.service.de.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfDeGoodsDo;
import com.ald.fanbei.api.dal.domain.AfDeUserGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.UserDeGoods;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: AppH5CutPriceController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:27:46
 *
 */
@RestController
@RequestMapping(value="/activity/de", produces = "application/json;charset=UTF-8")
public class AppH5CutPriceController extends BaseController {

	@Resource
	AfUserService afUserService;
	@Resource
	AfDeGoodsService afDeGoodsService;
	@Resource
	AfDeGoodsCouponService afDeGoodsCouponService;
	@Resource
	AfDeUserCutInfoService afDeUserCutInfoService;
	@Resource
	AfDeUserGoodsService afDeUserGoodsService;

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @Title: share @Description: 砍价接口 @param request @param response @return
	 * String @throws
	 */
    @RequestMapping(value = "/share", method = RequestMethod.POST)
	public String share(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long goodsPriceId = NumberUtil.objToLong(request.getParameter("goodsPriceId"));
			logger.info("activity/de/share params: userName ={} , goodsPriceId = {}", userName, goodsPriceId);
			Long userId = convertUserNameToUserId(userName);
			//查处iphonex的goodsPriceId
			AfDeGoodsDo iphoneDo = new AfDeGoodsDo();
			iphoneDo.setType(1);
			AfDeGoodsDo iphoneDoo = afDeGoodsService.getByCommonCondition(iphoneDo);
			
			// 查处改用户的所有的砍价商品
			AfDeUserGoodsDo goodsDo = new AfDeUserGoodsDo();
			goodsDo.setUserid(userId);
			goodsDo.setGoodspriceid(goodsPriceId);

			// 判断是否是

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "分享砍价商品失败").toString();
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activity/de/share" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			logger.error("/activity/de/share" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "分享砍价商品失败").toString();
		}

		return resultStr;
	}

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
	public String getGoodsList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, true);
			String userName = context.getUserName();
			Long userId = convertUserNameToUserId(userName);

			List<UserDeGoods> userDeGoodsList = afDeGoodsService.getUserDeGoodsList(userId);
			data.put("goodsList", userDeGoodsList);

			data.put("endTime", System.currentTimeMillis() / 1000 + 10000);
			data.put("totalCount", "100");

			return H5CommonResponse.getNewInstance(true, "查询成功", "", data).toString();
		} catch (Exception e) {
			logger.error("/activity/de/goods" + context + "error = {}", e);
			return H5CommonResponse.getNewInstance(false, "获取砍价商品列表失败").toString();
		}
	}

	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 * Long @throws
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
