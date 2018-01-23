package com.ald.fanbei.api.web.h5.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserDrawService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.UserDrawStatus;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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

    @Autowired
    AfResourceDao afResourceDao;

    @Resource
    BizCacheUtil bizCacheUtil;

    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public String share(HttpServletRequest request, HttpServletResponse response) {
	// 验证请求参数
	String phone = request.getParameter("phone");
	if (StringUtils.isBlank(phone) || phone.length() != 11) {
	    return H5CommonResponse.getNewInstance(false, "手机号码输入错误").toString();
	}

	String openId = request.getParameter("openid");
	if (StringUtils.isBlank(openId)) {
	    return H5CommonResponse.getNewInstance(false, "参数错误openId").toString();
	}
	String nickName = request.getParameter("nickname");
	if (StringUtils.isBlank(nickName)) {
	    return H5CommonResponse.getNewInstance(false, "参数错误nickName").toString();
	}
	String headImagUrl = request.getParameter("headimgurl");
	if (StringUtils.isBlank(headImagUrl)) {
	    return H5CommonResponse.getNewInstance(false, "参数错误headImagUrl").toString();
	}

	AfUserDrawDo userDrawDo = afUserDrawService.getByPhone(phone);
	if (userDrawDo != null) {
	    if (userDrawDo.getStatus().equals(UserDrawStatus.NORMAL.getCode())) {
		AfUserDrawDo openIdUser = afUserDrawService.getByOpenId(openId);
		if (openIdUser == null || openIdUser.getPhone().equals(phone)) {
		    // 获取微信信息成功
		    AfUserDrawDo afUserDrawDo = new AfUserDrawDo();
		    afUserDrawDo.setRid(userDrawDo.getRid());
		    afUserDrawDo.setHeaderImg(headImagUrl);
		    afUserDrawDo.setNickName(nickName);
		    afUserDrawDo.setOpenId(openId);
		    afUserDrawDo.setStatus(UserDrawStatus.SIGNIN.getCode());
		    afUserDrawService.updateById(afUserDrawDo);

		    return H5CommonResponse.getNewInstance(true, String.format("恭喜 %s 签到成功", userDrawDo.getName())).toString();
		} else {
		    return H5CommonResponse.getNewInstance(false, String.format("微信帐号已签号码  %s", openIdUser.getPhone())).toString();
		}
	    } else {
		return H5CommonResponse.getNewInstance(false, "该手机号码已签到").toString();
	    }
	} else {
	    return H5CommonResponse.getNewInstance(false, "手机号码未入员工库").toString();
	}
    }

    @RequestMapping(value = "/draw/user", method = RequestMethod.POST)
    public String getGoodsList(HttpServletRequest request, HttpServletResponse response) {

	List<UserDrawInfo> users = afUserDrawService.getByStatus(UserDrawStatus.SIGNIN.getCode());
	return H5CommonResponse.getNewInstance(true, "获取抽奖用户成功", "", users).toString();
    }

    @RequestMapping(value = "/draw/result", method = RequestMethod.POST)
    public String goodsInfo(HttpServletRequest request, HttpServletResponse response) {
	// 验证请求参数
	String count = request.getParameter("count");
	if (StringUtils.isBlank(count)) {
	    return H5CommonResponse.getNewInstance(false, "请求参数错误").toString();
	}
	Integer winCount = Integer.parseInt(count);
	if (winCount <= 0) {
	    return H5CommonResponse.getNewInstance(false, "请求参数错误").toString();
	}
	String code = request.getParameter("code");
	if (StringUtils.isBlank(code)) {
	    return H5CommonResponse.getNewInstance(false, "请求参数错误").toString();
	}

	// // 控制抽奖接口调用频率
	// String drawFrequencyKey = "Draw:Result:Runing";
	// if (bizCacheUtil.getObject(drawFrequencyKey) == null) {
	// // 添加控制锁
	// bizCacheUtil.saveObject(drawFrequencyKey, 1,
	// Constants.SECOND_OF_TEN);
	// } else {
	// return H5CommonResponse.getNewInstance(false,
	// "请稍后发起请求result").toString();
	// }

	// 获取配置信息
	AfResourceDo afResourceDo = afResourceService.getWechatConfig();
	String appid = afResourceDo.getValue();
	String secret = afResourceDo.getValue1();

	String openId = "";
	String configOpenId = afResourceDo.getValue2();
	String codeKey = "DRAW:CODE:" + code;
	if (bizCacheUtil.getObject(codeKey) == null) {
	    // 获取微信信息openId
	    JSONObject wxUserInfo = WxUtil.getUserInfo(appid, secret, code);
	    openId = wxUserInfo.getString("openid");
	    if (StringUtils.isBlank(openId)) {
		return H5CommonResponse.getNewInstance(false, "非法请求code").toString();
	    }
	}

	// 判断是否与数据库保持一致
	if ("DEFAULT".equals(configOpenId) || openId.equals(configOpenId) || bizCacheUtil.getObject(codeKey) != null) {
	    // 记录code信息
	    bizCacheUtil.saveObject(codeKey, code, Constants.SECOND_OF_ONE_DAY);

	    // 获取参与抽奖用户数据
	    List<UserDrawInfo> users = afUserDrawService.getByStatus(UserDrawStatus.SIGNIN.getCode());
	    winCount = winCount < users.size() ? winCount : users.size();
	    if (winCount > 0) {
		// 计算中奖用户
		List<Integer> winIndex = new ArrayList<Integer>(winCount);
		Random random = new Random();
		do {
		    int index = random.nextInt(users.size());
		    if (!winIndex.contains(index))
			winIndex.add(index);
		} while (winIndex.size() < winCount);

		List<UserDrawInfo> winUsers = new ArrayList<UserDrawInfo>(winCount);
		for (int i = 0; i < winCount; i++) {
		    winUsers.add(users.get(winIndex.get(i)));
		}

		// 更新中奖用户状态
		afUserDrawService.updateWinUserStatus(UserDrawStatus.WIN.getCode(), winUsers);

		return H5CommonResponse.getNewInstance(true, "获取抽奖用户成功", "", winUsers).toString();
	    } else {
		return H5CommonResponse.getNewInstance(true, "所有用户均中奖").toString();
	    }
	} else {
	    return H5CommonResponse.getNewInstance(false, "非法请求openId").toString();
	}
    }
}
