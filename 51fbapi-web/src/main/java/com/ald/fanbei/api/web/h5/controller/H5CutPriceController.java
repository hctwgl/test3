package com.ald.fanbei.api.web.h5.controller;

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
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @ClassName: H5CutPriceController
 * @Description: 双十一砍价 H5
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
	public String cutPrice(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		try {
			String userIdStr = request.getParameter("userId");
			String openId = request.getParameter("openId");
			String nickName = request.getParameter("nickName");
			String headImagUrl = request.getParameter("headImgUrl");
			if (StringUtil.isAllNotEmpty(userIdStr,openId,nickName,headImagUrl)) {
				
				
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "砍价失败").toString();
			logger.error("/activity/de/share error = {}", e.getStackTrace());
			
		} catch (Exception e) {
			logger.error("/activity/de/share error = {}", e.getStackTrace());
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
