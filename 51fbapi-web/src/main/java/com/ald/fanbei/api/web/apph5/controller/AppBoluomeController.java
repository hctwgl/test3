package com.ald.fanbei.api.web.apph5.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.common.FanbeiContext;
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

@Controller
@RequestMapping("/h5-brand")
public class AppBoluomeController extends BaseController {

    @Resource
    AfUserService afUserService;
    @Resource
    AfShopService afShopService;

    @ResponseBody
    @RequestMapping(value = "/getBrandUrl", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String getBrandUrl(HttpServletRequest request) {
	H5CommonResponse resp = H5CommonResponse.getNewInstance();
	//Calendar calStart = Calendar.getInstance();

	FanbeiWebContext context = new FanbeiWebContext();
	Map<String, String> buildParams = new HashMap<String, String>();
	try {

	    /*
	    String shopId = request.getParameter("shopId");
	    if (StringUtils.isBlank(shopId)) {
		resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.BOLUOME_SHOP_NOT_EXIST.getDesc(), "", null);
		return resp.toString();
	    }
	     */
	    
	    Long userId = -1l;
	    AfUserDo afUser = null;
	    context = doWebCheck(request, false);
	    if (context.isLogin()) {
		afUser = afUserService.getUserByUserName(context.getUserName());
		if (afUser != null) {
		    userId = afUser.getRid();
		}
	    } else {
		resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
		return resp.toString();
	    }

	    /*
	     * AfShopDo shopInfo =
	     * afShopService.getShopById(Long.parseLong(shopId)); if (shopInfo
	     * == null) { resp = H5CommonResponse.getNewInstance(false,
	     * FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null); return
	     * resp.toString(); }
	     * 
	     * String shopUrl = parseBoluomeUrl(shopInfo.getShopUrl());
	     */
	    buildParams.put(BoluomeCore.CUSTOMER_USER_ID, userId + StringUtils.EMPTY);
	    buildParams.put(BoluomeCore.CUSTOMER_USER_PHONE, afUser.getMobile());
	    buildParams.put(BoluomeCore.TIME_STAMP, String.valueOf(new Date().getTime()));

	    String sign = BoluomeCore.buildSignStr(buildParams);
	    buildParams.put(BoluomeCore.SIGN, sign);
	    String paramsStr = BoluomeCore.createLinkString(buildParams);

	    resp = H5CommonResponse.getNewInstance(true, "成功", paramsStr, paramsStr);
	    return resp.toString();
	} catch (Exception e) {
	    logger.error("commitChannelRegister", e);
	    resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
	    return resp.toString();
	}
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
              throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
          }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	return null;
    }
}
