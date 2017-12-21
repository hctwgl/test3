package com.ald.fanbei.api.web.apph5.controller;

import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserSearchService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**  
 * @Title: AppH5AllSearchController.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月18日 下午6:10:04
 * @version V1.0  
 */
@RestController
@RequestMapping(value = "/appH5Goods", produces = "application/json;charset=UTF-8")
public class AppH5AllSearchController extends BaseController{


    @Resource
    TaobaoApiUtil taobaoApiUtil;

    @Resource
    AfResourceService afResourceService;

    @Resource
    AfUserSearchService afUserSearchService;

    @Resource
    AfGoodsService afGoodsService;
	
	
	
	@RequestMapping(value = "/searchGoods",method = RequestMethod.POST )
	public String get(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			java.util.Map<String, Object> data = new HashMap<>();
			
			
			result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
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
