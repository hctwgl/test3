package com.ald.fanbei.api.web.controller;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.context.ContextImpl;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.server.webapp.AutoConfig;
import com.ald.fanbei.api.web.chain.impl.InterceptorChain;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.common.impl.H5HandleFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;


/**
 *
 *@类描述：FanbeiH5Controller
 *@author 郭帅强 2018年1月17日 下午6:15:06
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class DsedH5Controller extends DsedBaseController {

	@Resource
	H5HandleFactory h5HandleFactory;

	@Resource
	AfUserService afUserService;
	
	@Resource
	InterceptorChain interceptorChain;
	
    @RequestMapping(value ="/dsed/**",method = RequestMethod.POST,produces="application/json;charset=utf-8")
    @ResponseBody
    public String h5Request(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) throws IOException{
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
        String data =  request.getParameter("data");
        if(StringUtils.isNotEmpty(data)) {
			String decryptData = AesUtil.decryptFromBase64(data,"testC1b6x@6aH$2dlw");
        	JSONObject dataInfo = JSONObject.parseObject(decryptData);
			Long userId = Long.parseLong(dataInfo.get("userId").toString()) ;
//            Map<String,Object> systemsMap = (Map)JSON.parse(decryptData);
			Map<String,Object> systemsMap =JSON.parseObject(decryptData);
            builder.method(method)
	     	   .userId(userId)
	     	   .systemsMap(systemsMap);
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