package com.ald.fanbei.api.web.apph5.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfFacescoreShareCountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller("/fanbei_api/faceScore")
public class AppH5FaceScoreUserShareController extends BaseController {
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfFacescoreShareCountService faceScoreShareCountService;
	
	@ResponseBody
	@RequestMapping(value = "/appShareCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String shareCount(HttpServletRequest request, HttpServletResponse response){
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			Long userId = -1l;
			AfUserDo afUser = null;
			if (context.isLogin()) {
				afUser = afUserService.getUserByUserName(context.getUserName());
				if (afUser != null) {
					userId = afUser.getRid();
					faceScoreShareCountService.dealWithShareCount(userId);
				}
			}
		} catch (Exception e) {
			logger.error("/fanbei_api/faceScore/appShareCount...fail");
			e.printStackTrace();
		}
		return H5CommonResponse.getNewInstance(true, "分享成功!", "", null).toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/H5ShareCount", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String h5shareCount(HttpServletRequest request, HttpServletResponse response){
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			Long userId = -1l;
			AfUserDo afUser = null;
			if (context.isLogin()) {
				afUser = afUserService.getUserByUserName(context.getUserName());
				if (afUser != null) {
					userId = afUser.getRid();
					faceScoreShareCountService.dealWithShareCount(userId);
				}
			}
		} catch (Exception e) {
			logger.error("/fanbei_api/faceScore/h5ShareCount...fail");
			e.printStackTrace();
		}
		return H5CommonResponse.getNewInstance(true, "分享成功!", "", null).toString();
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
			throw new FanbeiException("参数格式错误" + e.getMessage(),
					FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
