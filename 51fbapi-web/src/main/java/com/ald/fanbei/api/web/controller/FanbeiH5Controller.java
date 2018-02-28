package com.ald.fanbei.api.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.context.ContextImpl;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.chain.impl.InterceptorChain;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5BaseController;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.common.impl.H5HandleFactory;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.ald.fanbei.api.web.validator.intercept.ValidationInterceptor;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;



/**
 *
 *@类描述：FanbeiH5Controller
 *@author 江荣波 2018年1月17日 下午6:15:06
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class FanbeiH5Controller extends H5BaseController {

	@Resource
	H5HandleFactory h5HandleFactory;

	@Resource
	AfUserService afUserService;
	
	@Resource
	InterceptorChain interceptorChain;
	
    @RequestMapping(value ="/h5/**",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String h5Request(HttpServletRequest request, HttpServletResponse response) throws IOException{
        request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        return this.processRequest(request);
    }


	@Override
	public Context parseRequestData(HttpServletRequest request) {
        try {
        	return buildContext(request);
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
	}

	private Context buildContext(HttpServletRequest request) {
		
		ContextImpl.Builder builder = new ContextImpl.Builder();
		String method = request.getRequestURI();
        String appInfo =  request.getParameter("_appInfo");
        if(StringUtils.isEmpty(appInfo)) {
        	// 从请求头获取_appInfo
        	String referer = request.getHeader("Referer");
        	if(StringUtils.isNotBlank(referer)) {
        		appInfo = getAppInfo(referer);
        	}
        }
        if(StringUtils.isNotEmpty(appInfo)) {
        	JSONObject _appInfo = JSONObject.parseObject(appInfo);
            String userName = _appInfo.getString("userName");
            String id = _appInfo.getString("id");
            Integer appVersion = _appInfo.getInteger("appVersion");
            
            Map<String,Object> systemsMap = (Map)JSON.parse(appInfo); 
            AfUserDo userInfo = afUserService.getUserByUserName(userName);
            Long userId = userInfo == null ? null : userInfo.getRid();
            builder.method(method)
	     	   .userId(userId)
	     	   .userName(userName)
	     	   .appVersion(appVersion)
	     	   .systemsMap(systemsMap)
	     	   .id(id);
        }
        
        Map<String,Object> dataMaps = Maps.newHashMap();
        
        wrapRequest(request,dataMaps);
        builder.dataMap(dataMaps);
        
        logger.info("request method=>{},params=>{}",method,JSON.toJSONString(dataMaps));
       
        String clientIp = CommonUtil.getIpAddr(request);
        builder.clientIp(clientIp);
        Context context = builder.build();
		return context;
	}

	private String getAppInfo(String referer) {
		
		String appInfo = StringUtils.EMPTY;
		try {
			Map<String, List<String>> params = Maps.newHashMap();
			String[] urlParts = referer.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}

					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			List<String> _appInfo = params.get("_appInfo");
			if (_appInfo != null && _appInfo.size() > 0) {
				appInfo = _appInfo.get(0);
			}
			return appInfo;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
	}

	private void wrapRequest(HttpServletRequest request, Map<String, Object> dataMaps) {
		
		Enumeration<String> paramNames =  request.getParameterNames();
		while(paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if(!StringUtils.equals("_appInfo", paramName)) {
				String objVal = request.getParameter(paramName);
				dataMaps.put(paramName, objVal);
			}
		}
	}


	@Override
	public BaseResponse doProcess(Context context) {
		interceptorChain.execute(context);
        H5Handle methodHandle = h5HandleFactory.getHandle(context.getMethod());
        
        H5HandleResponse handelResult;
        try {
            handelResult = methodHandle.process(context);
            int resultCode = handelResult.getResult().getCode();
            if(resultCode != 1000){
                logger.info(context.getId() + " err,Code=" + resultCode);
            }
            return handelResult;
        }catch(FanbeiException e){
        	logger.error("app exception",e);
        	throw e;
		} catch (Exception e) {
            logger.error("sys exception",e);
            throw new FanbeiException("sys exception",FanbeiExceptionCode.SYSTEM_ERROR);
        }
	}


	


}