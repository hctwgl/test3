package com.ald.fanbei.api.web.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;

@Controller
public class TestController {
	
	@Resource
	SmsUtil smsUtil;

	/**
	 * 新h5页面处理，针对前端开发新的h5页面时请求的处理
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = { "/h5/app/*_new", "/h5/app/sys/*_new","/h5/app/goods/*_new", "/h5/app/mine/*_new", "/h5/app/order/*_new" }, method = RequestMethod.GET)
	public String newVmPage(Model model, HttpServletRequest request,HttpServletResponse response) throws IOException {
		String returnUrl = request.getRequestURI().replace("/h5/", "");
		return returnUrl;
	}

	@RequestMapping(value = { "/test1" }, method = RequestMethod.GET, produces = "application/json;charset=utf-8")
	public String goodsRequest(HttpServletRequest request, HttpServletResponse response)throws IOException {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");

		// String reportId = TongdunUtil.applyPreloan("362525198601022112",
		// "陈金虎", "15958119936", "410228573@qq.com");
		// ER2017012122013411346564
//		TongdunResultBo result = TongdunUtil.queryPreloan("ER2017012121595110613362");
//
//		System.out.println("-----reportId---" + 11 + ",result=" + result);
		smsUtil.sendRegistVerifyCode("15958119936");

//		SmsUtil.sendSms("15958119936", "验证码:1234");
		
		return "succ";
	}

	// TongdunUtil
}
