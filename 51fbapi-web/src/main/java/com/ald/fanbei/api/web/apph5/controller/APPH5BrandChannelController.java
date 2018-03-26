package com.ald.fanbei.api.web.apph5.controller;


import com.ald.fanbei.api.biz.service.AfResourceH5Service;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import com.ald.fanbei.api.web.common.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Controller
@RequestMapping("/fanbei-web/activity")
public class APPH5BrandChannelController extends BaseController {


	String opennative = "/fanbei-web/opennative?name=";

	@Resource
	AfResourceH5Service afResourceH5Service;

	/**
	 * 品牌频道
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/getBrandChannel", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String GetFlashSaleGoods(HttpServletRequest request,HttpServletResponse response) {
		H5CommonResponse resp = null;
		Map<String,Object> data = new HashMap<String,Object>();
		List<AfResourceH5Do> list = afResourceH5Service.selectByStatus();
		for(AfResourceH5Do afResourceH5Do : list){

		}



		resp = H5CommonResponse.getNewInstance(true, "成功","",data);
		return resp.toString();

	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
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

}
