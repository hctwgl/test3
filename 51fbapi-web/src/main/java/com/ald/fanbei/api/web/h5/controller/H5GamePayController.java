package com.ald.fanbei.api.web.h5.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfSupGameService;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.MD5Helper;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.dal.domain.dto.GameGoods;
import com.ald.fanbei.api.dal.domain.dto.GameGoodsGroup;
import com.ald.fanbei.api.web.common.H5CommonResponse;

/**
 * 
 * @ClassName: H5CutPriceController
 * @Description: 双十一砍价 H5
 * @author qiao
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年10月23日 下午4:28:04
 *
 */
@RestController
@RequestMapping(value = "/game/pay", produces = "application/json;charset=UTF-8")
public class H5GamePayController extends H5Controller {

    @Autowired
    private AfSupGameService afSupGameService;

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public H5CommonResponse getGoodsList(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiH5Context context = doH5Check(request, false);
	try {
	    String type = request.getParameter("type");
	    // 查询列表
	    if (StringUtils.isNotBlank(type)) {
		List<GameGoods> hotList = afSupGameService.getHotGoodsList(type);
		List<GameGoodsGroup> groupList = afSupGameService.getGoodsList(type);

		data.put("hotList", hotList);
		data.put("groupList", groupList);

		return H5CommonResponse.getNewInstance(true, "查询成功", "", data);
	    } else {
		return H5CommonResponse.getNewInstance(false, "参数错误");
	    }
	} catch (Exception e) {
	    logger.error("/game/pay/goods" + context + "error:", e);
	    return H5CommonResponse.getNewInstance(false, "获取游戏列表失败");
	}
    }

    @RequestMapping(value = "/goodsInfo", method = RequestMethod.POST)
    public H5CommonResponse getGoodsInfo(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiH5Context context = doH5Check(request, false);
	try {
	    String goodsId = request.getParameter("goodsId");

	    if (StringUtils.isNotBlank(goodsId)) {
		// 查询详情
		AfSupGameDo afSupGameDo = afSupGameService.getById(Long.parseLong(goodsId));
		if (afSupGameDo != null) {
		    data.put("goodsId", goodsId);
		    data.put("content", afSupGameDo.getXmlFile());

		    return H5CommonResponse.getNewInstance(true, "查询成功", "", data);
		}
	    }
	    return H5CommonResponse.getNewInstance(false, "参数错误");
	} catch (Exception e) {
	    logger.error("/game/pay/goodsInfo" + context + "error:", e);
	    return H5CommonResponse.getNewInstance(false, "获取游戏信息失败");
	}
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public H5CommonResponse createOrder(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiH5Context context = doH5Check(request, false);
	try {
	    // 验证参数
	    String goodsId = request.getParameter("goodsId");
	    if (StringUtils.isBlank(goodsId)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsId.");
	    }
	    String actualAmount = request.getParameter("actualAmount");
	    if (StringUtils.isBlank(actualAmount)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:actualAmount.");
	    }
	    String couponId = request.getParameter("couponId");
	    String acctType = request.getParameter("acctType");
	    if (StringUtils.isBlank(acctType)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:acctType.");
	    }
	    String gameName = request.getParameter("gameName");
	    if (StringUtils.isBlank(gameName)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:gameName.");
	    }
	    String userName = request.getParameter("userName");
	    if (StringUtils.isBlank(userName)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:userName.");
	    }
	    String goodsNum = request.getParameter("goodsNum");
	    if (StringUtils.isBlank(goodsNum)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsNum.");
	    }
	    String gameType = request.getParameter("gameType");
	    String gameAcct = request.getParameter("gameAcct");
	    String gameArea = request.getParameter("gameArea");
	    String gameSrv = request.getParameter("gameSrv");
	    String userIp = request.getParameter("userIp");

	    // 下单逻辑

	    return H5CommonResponse.getNewInstance(true, "充值请求提交成功", "", data);
	} catch (Exception e) {
	    logger.error("/game/pay/goodsInfo" + context + "error:", e);
	    return H5CommonResponse.getNewInstance(false, "获取游戏信息失败");
	}
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String reciceOrderResult(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiH5Context context = doH5Check(request, false);
	try {
	    // 获取参数
	    String businessId = request.getParameter("businessId");
	    String userOrderId = request.getParameter("userOrderId");
	    // 01成功 02失败
	    String status = request.getParameter("status");
	    String mes = request.getParameter("mes");
	    String kminfo = request.getParameter("kminfo");
	    String payoffPriceTotal = request.getParameter("payoffPriceTotal");
	    String sign = request.getParameter("sign");
	    // 验签
	    String signCheck = MD5Helper.md5(businessId + userOrderId + status + "key");
	    if (sign.equals(signCheck)) {

		// 记录回调结果
		// 更新订单状态

		return "<receive>ok</receive>";
	    } else {
		return "<receive>sign error</receive>";
	    }
	} catch (Exception e) {

	    logger.error("/game/pay/callback" + context + "error:", e);
	    return "<receive>exception error</receive>";
	}
    }
}
