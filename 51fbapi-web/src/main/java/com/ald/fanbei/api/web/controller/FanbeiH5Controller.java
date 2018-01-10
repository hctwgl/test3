package com.ald.fanbei.api.web.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.context.ContextImpl;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5BaseController;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.common.impl.H5HandleFactory;



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
        	ContextImpl.Builder builder = new ContextImpl.Builder();
        	
            String method = request.getRequestURI();
            
            String appInfo = request.getParameter("_appInfo");
            
            
            
            
            builder.method(method);
            
            Context context = builder.build();
            
            
            
            /*
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
             */
            return context;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
	}

	@Override
	public BaseResponse doProcess(Context context) {
        H5Handle methodHandel = h5HandleFactory.getHandle(context.getMethod());
      
        H5HandleResponse handelResult;
        try {
            handelResult = methodHandel.process(context);
            int resultCode = handelResult.getResult().getCode();
            if(resultCode != 1000){
                //logger.info(requestDataVo.getId() + " err,Code=" + resultCode);
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