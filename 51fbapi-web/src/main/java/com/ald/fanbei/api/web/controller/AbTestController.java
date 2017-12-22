package com.ald.fanbei.api.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;

import com.ald.fanbei.api.common.AbTestUrl;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



/**
 *
 *@类描述：AbTestController
 *@author 江荣波 2017年1月17日 下午6:15:06
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public abstract class AbTestController extends BaseController {

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// 灰度测试
		abTest(requestDataVo,context, httpServletRequest);
		
        ApiHandle methodHandel = apiHandleFactory.getApiHandle(requestDataVo.getMethod());
        ApiHandleResponse handelResult;
        try {
        	if(methodHandel != null) {
        		  handelResult = methodHandel.process(requestDataVo,context, httpServletRequest);
                  int resultCode = handelResult.getResult().getCode();
                  if(resultCode != 1000){
                      logger.info(requestDataVo.getId() + " err,Code=" + resultCode);
                  }
        	} else {
        		handelResult = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        		handelResult.setResponseData(requestDataVo.getParams());
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

	protected abstract void abTest(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest);

	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		if (StringUtils.isBlank(reqData)) {
			throw new FanbeiException("param is null",FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
		}
		return reqData;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();
            String method = request.getRequestURI();
            reqVo.setMethod(method);
            reqVo.setId(request.getHeader(Constants.REQ_SYS_NODE_ID));
            String appVersion = request.getHeader(Constants.REQ_SYS_NODE_VERSION);
            String netType = request.getHeader(Constants.REQ_SYS_NODE_NETTYPE);
            String userName = request.getHeader(Constants.REQ_SYS_NODE_USERNAME);
            String sign = request.getHeader(Constants.REQ_SYS_NODE_SIGN);
            String time = request.getHeader(Constants.REQ_SYS_NODE_TIME);

            Map<String,Object> system = new HashMap<String,Object>();
            
            system.put(Constants.REQ_SYS_NODE_VERSION, appVersion);
            system.put(Constants.REQ_SYS_NODE_NETTYPE, netType);
            system.put(Constants.REQ_SYS_NODE_USERNAME, userName);
            system.put(Constants.REQ_SYS_NODE_SIGN, sign);
            system.put(Constants.REQ_SYS_NODE_TIME, time);
            
            reqVo.setSystem(system);
            
            

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setParams((jsonObj == null || jsonObj.isEmpty()) ? new HashMap<String,Object>() : jsonObj);

            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
	}
}