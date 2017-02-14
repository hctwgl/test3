/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.web.h5.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类WxGoodsController.java 的实现描述：微信h5接口,商品相关相关微信h5接口
 *@author 陈金虎 2016年10月23日 上午9:46:33
 *@注意：本内容仅限于杭州喜马拉雅家居有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/")
public class H5Controller extends BaseController{
	
	/**
	 * 新h5页面处理，针对前端开发新的h5页面时请求的处理
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = {"app/goods/*_new","test","app/goods/*"}, method = RequestMethod.GET)
    public String newVmPage(Model model,HttpServletRequest request, HttpServletResponse response) throws IOException{
        String returnUrl = request.getRequestURI();
        return returnUrl;
    }

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
