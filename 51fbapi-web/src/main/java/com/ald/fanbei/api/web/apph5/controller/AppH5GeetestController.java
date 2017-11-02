package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.web.api.geetest.GeetestLib;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述： 返场活动
 * 
 * @author 江荣波 2017年7月17日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
public class AppH5GeetestController extends BaseController {

	private static final String GEETEST_ID = "3563a2cd373d064e8b01b57c969cc326";

	private static final String GEETEST_KEY = "56e76fe5e8978e5035bc6afe214ea198";
	
	private static final String CLIENT_TYPE = "client_type";
	
	private static final String USER_ID = "user_id";
	
	private static final String IP_ADDRESS = "ip_address";
	
	@Resource
	BizCacheUtil bizCacheUtil;

	@RequestMapping(value = "getGeetestCode", method = RequestMethod.GET)
	public void getGeetestCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		String clientIp = CommonUtil.getIpAddr(request);
		GeetestLib gtSdk = new GeetestLib(GEETEST_ID, GEETEST_KEY, true);
		String respInfo = "{}";
		String userId = UUID.randomUUID().toString();
		// 自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>();
		param.put(USER_ID, userId); 
		param.put(CLIENT_TYPE, "web"); 
		param.put(IP_ADDRESS, clientIp);
		// 进行验证预处理
		int gtServerStatus = gtSdk.preProcess(param);
		bizCacheUtil.saveObject(gtSdk.gtServerStatusSessionKey + "_" + userId, gtServerStatus);
		respInfo = gtSdk.getResponseStr();
		JSONObject jsonObj = JSONObject.parseObject(respInfo);
		jsonObj.put("userId", userId);
		
		PrintWriter out = response.getWriter();
		out.println(jsonObj.toJSONString());
	}

	@RequestMapping(value = "verifyGeetestCode", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String verifyGeetestCode(HttpServletRequest request, ModelMap model) throws IOException {
		String clientIp = CommonUtil.getIpAddr(request);
		GeetestLib gtSdk = new GeetestLib(GEETEST_ID, GEETEST_KEY, true);

		String challenge = request.getParameter(GeetestLib.FN_GEETEST_CHALLENGE);
		String validate = request.getParameter(GeetestLib.FN_GEETEST_VALIDATE);
		String seccode = request.getParameter(GeetestLib.FN_GEETEST_SECCODE);
		
		String userId = request.getParameter("userId");
		if(StringUtils.isEmpty(userId)) {
			throw new FanbeiException("userId can't be empty.");
		}
		Integer gtServerStatusCode = (Integer) bizCacheUtil.getObject(gtSdk.gtServerStatusSessionKey + "_" + userId);
		JSONObject data = new JSONObject();
		
		// 自定义参数,可选择添加
		HashMap<String, String> param = new HashMap<String, String>();
		param.put(USER_ID, userId); 
		param.put(CLIENT_TYPE, "web"); 
		param.put(IP_ADDRESS, clientIp);
		int gtResult = 0;
		if (gtServerStatusCode != null && gtServerStatusCode == 1) {
			gtResult = gtSdk.enhencedValidateRequest(challenge, validate, seccode, param);
			logger.info("AppH5GeetestController.verifyGeetestCode geetest enhenced verify result => {}",gtResult);
		} else {
			// gt-server非正常情况下，进行failback模式验证
			gtResult = gtSdk.failbackValidateRequest(challenge, validate, seccode);
			logger.info("AppH5GeetestController.verifyGeetestCode geetest failback verify result => {}",gtResult);
		}
		try {
			if (gtResult == 1) {
				// 验证成功
				data.put("status", "success");
				data.put("version", gtSdk.getVersionInfo());

			} else {
				// 验证失败
				data.put("status", "fail");
				data.put("version", gtSdk.getVersionInfo());
			}
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error("AppH5GeetestController.verifyGeetestCode error,message=>{}",e.getMessage());
			return H5CommonResponse.getNewInstance(false, "请求失败", e.getMessage(), data).toString();
		}
		return H5CommonResponse.getNewInstance(true, "请求成功", "", data).toString();
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
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
		return null;
	}

}