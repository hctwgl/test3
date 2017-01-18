package com.ald.fanbei.api.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 *@类描述：第三方回调接口
 *@author 陈金虎 2017年1月17日 下午6:14:52
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
//TODO 安全相关，同一个ip单位时间内次数做限制
@Controller
@RequestMapping("/third")
public class ThirdController{
	
	
	/**
	 * 支付宝回调接口
	 * @param request
	 * @param response
	 * @return
	 */
    @RequestMapping(value = {"/alipayNotify"}, method = RequestMethod.POST)
    @ResponseBody
	public String alipayNotify(HttpServletRequest request, HttpServletResponse response){
    	return "success";
	}
    
    /**
     * 支付宝退款回调接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/alipayRefundNotify"}, method = RequestMethod.POST)
    @ResponseBody
	public String alipayRefundNotify(HttpServletRequest request, HttpServletResponse response){
    	return "success";
	}
    
    /**
     * app中微信支付回调接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/wxpayNotify"}, method = RequestMethod.POST)
    @ResponseBody
	public String wxpayNotify(HttpServletRequest request, HttpServletResponse response){
    	return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}
    
    
    /**
     * 微信公众号支付回调接口
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/wxpayNotifyForPub"}, method = RequestMethod.POST)
    @ResponseBody
	public String wxpayNotifyForPub(HttpServletRequest request, HttpServletResponse response){
    	return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
	}
    
	
}
