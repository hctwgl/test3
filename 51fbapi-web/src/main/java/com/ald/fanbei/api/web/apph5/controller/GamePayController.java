package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
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
import com.ald.fanbei.api.biz.service.AfSupOrderService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.GameGoods;
import com.ald.fanbei.api.dal.domain.dto.GameGoodsGroup;
import com.ald.fanbei.api.dal.domain.dto.GameOrderInfoDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

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
public class GamePayController extends BaseController {

    @Autowired
    private AfSupGameService afSupGameService;

    @Autowired
    private AfSupOrderService afSupOrderService;

    @Autowired
    private AfUserDao afUserDao;

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public H5CommonResponse getGoodsList(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	FanbeiWebContext context = doWebCheck(request, false);
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
	FanbeiWebContext context = doWebCheck(request, false);
	try {
	    String goodsId = request.getParameter("goodsId");

	    if (StringUtils.isNotBlank(goodsId)) {
		// 查询详情
		AfSupGameDo afSupGameDo = afSupGameService.getById(Long.parseLong(goodsId));
		if (afSupGameDo != null) {
		    data.put("goodsId", goodsId);
		    data.put("xmlType", afSupGameDo.getXmlType());
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
	FanbeiWebContext context = doWebCheck(request, true);
	try {
	    // 验证参数
	    if (StringUtils.isBlank(request.getParameter("goodsId"))) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsId.");
	    }
	    Long goodsId = Long.parseLong(request.getParameter("goodsId"));
	    if (goodsId <= 0) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsId.");
	    }

	    if (StringUtils.isBlank(request.getParameter("actualAmount"))) {
		return H5CommonResponse.getNewInstance(false, "参数错误:actualAmount.");
	    }
	    BigDecimal actualAmount = new BigDecimal(request.getParameter("actualAmount"));
	    if (actualAmount.doubleValue() <= 0) {
		return H5CommonResponse.getNewInstance(false, "参数错误:actualAmount.");
	    }
	    Long couponId = Long.parseLong(request.getParameter("couponId"));
	    String acctType = request.getParameter("acctType");
	    if (StringUtils.isBlank(acctType)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:acctType.");
	    }
	    String gameName = request.getParameter("gameName");
	    String userName = request.getParameter("userName");
	    if (StringUtils.isBlank(userName)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:userName.");
	    }

	    if (StringUtils.isBlank(request.getParameter("goodsNum"))) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsNum.");
	    }
	    Integer goodsNum = Integer.parseInt(request.getParameter("goodsNum"));
	    if (goodsNum <= 0) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsNum.");
	    }
	    String gameType = request.getParameter("gameType");
	    String gameAcct = request.getParameter("gameAcct");
	    String gameArea = request.getParameter("gameArea");
	    String gameSrv = request.getParameter("gameSrv");
	    String userIp = request.getParameter("userIp");

	    // 下单逻辑
	    AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
	    data = afSupOrderService.addSupOrder(afUserDo.getRid(), goodsId, actualAmount, couponId, acctType, gameName, userName, goodsNum, gameType, gameAcct, gameArea, gameSrv, userIp);
	    return H5CommonResponse.getNewInstance(true, "充值订单提交成功", "", data);
	} catch (Exception e) {
	    logger.error("/game/pay/goodsInfo" + context + "error:", e);
	    return H5CommonResponse.getNewInstance(false, "获取游戏信息失败");
	}
    }

    @RequestMapping(value = "/orderInfo", method = RequestMethod.GET)
    public H5CommonResponse getOrderInfo(HttpServletRequest request, HttpServletResponse response) {
	FanbeiWebContext context = doWebCheck(request, true);
	try {
	    String orderNo = request.getParameter("orderNo");
	    if (StringUtils.isNotBlank(orderNo)) {
		// 查询详情
		GameOrderInfoDto orderInfo = afSupOrderService.getOrderInfoByOrderNo(orderNo);
		return H5CommonResponse.getNewInstance(true, "获取订单信息成功", "", orderInfo);
	    } else {
		return H5CommonResponse.getNewInstance(false, "参数错误");
	    }
	} catch (Exception e) {
	    logger.error("/game/pay/goodsInfo" + context + "error:", e);
	    return H5CommonResponse.getNewInstance(false, "获取订单信息失败");
	}
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET)
    public String reciceOrderResult(HttpServletRequest request, HttpServletResponse response) {
	try {

	    String userOrderId = request.getParameter("userOrderId");
	    // 01成功 02失败
	    String status = request.getParameter("status");
	    String mes = request.getParameter("mes");
	    String kminfo = request.getParameter("kminfo");
	    String payoffPriceTotal = request.getParameter("payoffPriceTotal");
	    String sign = request.getParameter("sign");
	    // 获取参数
	    String businessId = request.getParameter("businessId");

	    // 验证businessId
	    String configBusinessId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SUP_BUSINESS_ID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
	    if (!configBusinessId.equals(businessId)) {
		logger.error("/game/pay/callback businessId error:businessId=" + businessId + " ,config key :" + ConfigProperties.get(Constants.CONFKEY_SUP_BUSINESS_ID));
		return "<receive>businessId error</receive>";
	    }

	    return afSupOrderService.processCallbackResult(userOrderId, status, mes, kminfo, payoffPriceTotal, sign);
	} catch (Exception e) {
	    logger.error("/game/pay/callback error:", e);
	    return "<receive>exception error</receive>";
	}
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	return null;
    }
}
