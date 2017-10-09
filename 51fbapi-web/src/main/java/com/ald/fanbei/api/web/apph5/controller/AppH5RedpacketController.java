package com.ald.fanbei.api.web.apph5.controller;
 
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.redpacket.IRedpacketService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
 
/**
 * @类描述 h5红包雨-申请命中
 * @author ZJF 2017年09月30日 
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/redpacket/")
public class AppH5RedpacketController extends BaseController {
	
	@Resource
	IRedpacketService redpacketService;
	
    @RequestMapping(value = "applyHit", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String applyHit(HttpServletRequest request) throws IOException {
    	H5CommonResponse resp = null;
    	FanbeiWebContext context;
    	try{
    		context = doWebCheck(request, true);
    		
    		AfCouponDo coupon = redpacketService.apply();
    		if(coupon == null) {
//    			resp = H5CommonResponse.getNewInstance(true, "", coupon);
    		}else {
//    			resp = H5CommonResponse.terseFail();
    		}
    		doLog(request, resp, context.getAppInfo(), 0, context.getUserName());
    	} catch (Exception e){
    		logger.error(e.getMessage(), e);
    		resp = H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString());
    	}
		return resp.toString();
    }
    
    
    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }
 
    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }
    
    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
 
}