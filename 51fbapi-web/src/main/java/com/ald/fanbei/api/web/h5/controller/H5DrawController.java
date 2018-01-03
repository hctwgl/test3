package com.ald.fanbei.api.web.h5.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserDrawService;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.enums.UserDrawStatus;
import com.ald.fanbei.api.dal.domain.AfUserDrawDo;
import com.ald.fanbei.api.dal.domain.dto.UserDrawInfo;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.alibaba.fastjson.JSONObject;

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

    @Autowired
    AfResourceService afResourceService;

    @Autowired
    AfUserDrawService afUserDrawService;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public String share(HttpServletRequest request, HttpServletResponse response) {

	String phone = request.getParameter("phone");
	if (StringUtils.isNotBlank(phone) && phone.length() != 11) {
	    return H5CommonResponse.getNewInstance(false, "手机号码输入错误").toString();
	}
	String code = request.getParameter("code");
	if (StringUtils.isNotBlank(code)) {
	    return H5CommonResponse.getNewInstance(false, "请求参数错误").toString();
	}

	AfUserDrawDo userDrawDo = afUserDrawService.getByPhone(phone);
	if (userDrawDo != null) {
	    if (userDrawDo.getStatus().equals(UserDrawStatus.NORMAL.getCode())) {
		// 获取微信信息openId、头像、昵称
		String appid = afResourceService.getConfigByTypesAndSecType("ACCESSTOKEN", "WX").getValue();
		String secret = afResourceService.getConfigByTypesAndSecType("ACCESSTOKEN", "WX").getValue1();
		JSONObject wxUserInfo = WxUtil.getUserInfo(appid, secret, code);

		String openId = wxUserInfo.getString("openid");
		String nickName = wxUserInfo.getString("nickname");
		String headImagUrl = wxUserInfo.getString("headimgurl");

		AfUserDrawDo afUserDrawDo = new AfUserDrawDo();
		afUserDrawDo.setRid(userDrawDo.getRid());
		afUserDrawDo.setHeaderImg(headImagUrl);
		afUserDrawDo.setNickName(nickName);
		afUserDrawDo.setOpenId(openId);
		afUserDrawDo.setStatus(UserDrawStatus.SIGNIN.getCode());
		afUserDrawService.updateById(afUserDrawDo);

		return H5CommonResponse.getNewInstance(true, "签到成功").toString();
	    } else {
		return H5CommonResponse.getNewInstance(false, "该手机号码已签到").toString();
	    }
	} else {
	    return H5CommonResponse.getNewInstance(false, "该手机号码未添加到员工库").toString();
	}
    }

    @RequestMapping(value = "/draw/user", method = RequestMethod.POST)
    public String getGoodsList(HttpServletRequest request, HttpServletResponse response) {

	List<UserDrawInfo> users = afUserDrawService.getByStatus(UserDrawStatus.SIGNIN.getCode());

	return H5CommonResponse.getNewInstance(true, "获取抽奖用户成功", "", users).toString();
    }

    @RequestMapping(value = "/draw/result", method = RequestMethod.POST)
    public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {

	String count = request.getParameter("count");
	if (StringUtils.isNotBlank(count)) {
	    return H5CommonResponse.getNewInstance(false, "请求参数错误").toString();
	}
	String code = request.getParameter("code");
	if (StringUtils.isNotBlank(code)) {
	    return H5CommonResponse.getNewInstance(false, "请求参数错误").toString();
	}

	// 获取微信信息openId、头像、昵称
	String appid = afResourceService.getConfigByTypesAndSecType("ACCESSTOKEN", "WX").getValue();
	String secret = afResourceService.getConfigByTypesAndSecType("ACCESSTOKEN", "WX").getValue1();
	JSONObject wxUserInfo = WxUtil.getUserInfo(appid, secret, code);

	String openId = wxUserInfo.getString("openid");
	// 判断是否与数据库保持一致
	if (openId.equals("")) {
	    // 记录code值

	    // 计算中间用户
	    Integer winCount = Integer.parseInt(count);
	    List<UserDrawInfo> users = afUserDrawService.getByStatus(UserDrawStatus.SIGNIN.getCode());

	    return H5CommonResponse.getNewInstance(true, "获取抽奖用户成功", "", users).toString();
	} else {

	    return H5CommonResponse.getNewInstance(false, "非法请求").toString();
	}
    }
}
