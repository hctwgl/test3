package com.ald.fanbei.api.web.h5.controller;

import java.util.HashMap;
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
import com.ald.fanbei.api.biz.service.de.AfDeRandomPropertyService;
import com.ald.fanbei.api.biz.service.de.AfDeUserCutInfoService;
import com.ald.fanbei.api.biz.service.de.AfDeUserGoodsService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @ClassName: H5CutPriceController
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:28:04
 *
 */
@RestController
@RequestMapping(value="/activityH5/de", produces = "application/json;charset=UTF-8")
public class H5CutPriceController extends H5Controller {

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
	@Resource
	AfDeRandomPropertyService afDeRandomPropertyService;

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @Title: cutPrice 
	 * @Description: 砍价接口 
	 * @param requst 
	 * @param response 
	 * @return
	 *         String 返回类型
	 * @throws
	 */
	@RequestMapping(value = "/cutPrice", method = RequestMethod.POST)
	public String cutPrice(HttpServletRequest requst, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(requst, true);

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "没有登录").toString();
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/activityH5/cutPrice" + context + "login error ");
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return resultStr;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}
}
