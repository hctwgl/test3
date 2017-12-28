package com.ald.fanbei.api.web.h5.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.web.common.H5CommonResponse;

/**
 * 
 * @ClassName: H5DrawController
 * @Description: 年会抽奖
 * @author gaojb
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年12月27日 下午4:28:04
 *
 */
@RestController
@RequestMapping(value = "/party", produces = "application/json;charset=UTF-8")
public class H5DrawController extends H5Controller {

    /**
     * 
     * @Title: share @Description: 砍价接口 @param request @param response @return
     *         String @throws
     */
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public String share(HttpServletRequest request, HttpServletResponse response) {
	String resultStr = H5CommonResponse.getNewInstance(false, "砍价分享失败").toString();

	String userName = request.getParameter("userId");
	String goodsPriceIdStr = request.getParameter("goodsPriceId");
	String openId = request.getParameter("openId");
	String nickName = request.getParameter("nickName");
	String headImagUrl = request.getParameter("headImgUrl");

	return resultStr;
    }

    @RequestMapping(value = "/draw/user", method = RequestMethod.POST)
    public String getGoodsList(HttpServletRequest request, HttpServletResponse response) {

	return "";
    }

    /**
     * 
     * @Title: goodsInfo @Description: 获取商品砍价详情 @param requst @param response @return
     *         String 返回类型 @throws
     */
    @RequestMapping(value = "/draw/result", method = RequestMethod.POST)
    public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {

	return "";
    }
}
