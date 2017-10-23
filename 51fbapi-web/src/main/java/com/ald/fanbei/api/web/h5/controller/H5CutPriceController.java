package com.ald.fanbei.api.web.h5.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * <p>
 * Title:H5CutPriceController
 * <p>
 * Description:
 * <p>
 * 
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017-10-23 11:42:07
 *
 */
@RestController
@RequestMapping("/activityH5/de")
public class H5CutPriceController extends H5Controller {
	
	/**
	 * 
	* @Title: cutPrice
	* @Description: 砍价接口
	* @param requst
	* @param response
	* @return String    返回类型
	* @throws
	 */
	@RequestMapping(value = "/cutPrice" ,method = RequestMethod.POST,produces = "text/html;charset = UTF-8")
	public String cutPrice(HttpServletRequest requst, HttpServletResponse response){
		String resultStr = "";
		
		
		return resultStr;
	}
	
	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}
}
