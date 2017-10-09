package com.ald.fanbei.api.web.apph5.controller;
 
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * @类描述：
 * 返场活动
 * @author 江荣波 2017年6月20日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
public class AppH5MaidianController extends BaseController {
	
    @RequestMapping(value = "postMaidianInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String postMaidianInfo(HttpServletRequest request, ModelMap model) throws IOException {
    	try{
    		doWebCheck(request, false);
    		String maidianInfo = request.getParameter("maidianInfo");
        	// 获取埋点标识
    		H5CommonResponse resp = H5CommonResponse.getNewInstance(true,"","",model);
    		doMaidianLog(request, resp,maidianInfo);
    		return resp.toString();
    	} catch (Exception e){
    		return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
    	}
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
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }
 
    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
 
}