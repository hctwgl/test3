package com.ald.fanbei.api.web.apph5.controller;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
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

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfSupGameService;
import com.ald.fanbei.api.biz.service.AfSupOrderService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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

    @Autowired
    private AfOrderService afOrderService;

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    public H5CommonResponse getGoodsList(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	try {
	    // FanbeiWebContext context = doWebCheck(request, false);
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
	    logger.error("/game/pay/goods error:", e);
	    return H5CommonResponse.getNewInstance(false, e.getMessage());
	}
    }

    @RequestMapping(value = "/goodsInfo", method = RequestMethod.POST)
    public H5CommonResponse getGoodsInfo(HttpServletRequest request, HttpServletResponse response) {
	Map<String, Object> data = new HashMap<String, Object>();
	try {
	    // FanbeiWebContext context = doWebCheck(request, false);
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
	    logger.error("/game/pay/goodsInfo error:", e);
	    return H5CommonResponse.getNewInstance(false, e.getMessage());
	}
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public H5CommonResponse createOrder(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
	Map<String, Object> data = new HashMap<String, Object>();
	try {
	    FanbeiWebContext context = doWebCheck(request, true);

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
	    Long couponId = 0L;
	    if (StringUtils.isNotBlank(request.getParameter("couponId"))) {
		couponId = Long.parseLong(request.getParameter("couponId"));
	    }

	    String acctType = URLDecoder.decode(request.getParameter("acctType"), "utf-8");
	    if (StringUtils.isBlank(acctType)) {
		return H5CommonResponse.getNewInstance(false, "参数错误:acctType.");
	    }
	    String gameName = URLDecoder.decode(request.getParameter("gameName"), "utf-8");
	    String userName = URLDecoder.decode(request.getParameter("userName"), "utf-8");
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

	    if (StringUtils.isBlank(request.getParameter("priceTimes"))) {
		return H5CommonResponse.getNewInstance(false, "参数错误:priceTimes.");
	    }
	    Integer priceTimes = Integer.parseInt(request.getParameter("priceTimes"));
	    if (priceTimes <= 0) {
		return H5CommonResponse.getNewInstance(false, "参数错误:priceTimes.");
	    }

	    if (StringUtils.isBlank(request.getParameter("goodsCount"))) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsCount.");
	    }
	    Integer goodsCount = Integer.parseInt(request.getParameter("goodsCount"));
	    if (priceTimes <= 0) {
		return H5CommonResponse.getNewInstance(false, "参数错误:goodsCount.");
	    }

	    String gameType = "";
	    if (StringUtils.isNotBlank(request.getParameter("gameType")))
		gameType = URLDecoder.decode(request.getParameter("gameType"), "utf-8");

	    String gameAcct = "";
	    if (StringUtils.isNotBlank(request.getParameter("gameAcct")))
		gameAcct = URLDecoder.decode(request.getParameter("gameAcct"), "utf-8");

	    String gameArea = "";
	    if (StringUtils.isNotBlank(request.getParameter("gameArea")))
		gameArea = URLDecoder.decode(request.getParameter("gameArea"), "utf-8");

	    String gameSrv = "";
	    if (StringUtils.isNotBlank(request.getParameter("gameSrv")))
		gameSrv = URLDecoder.decode(request.getParameter("gameSrv"), "utf-8");

	    String userIp = "";
	    if (StringUtils.isNotBlank(request.getParameter("userIp")))
		userIp = URLDecoder.decode(request.getParameter("userIp"), "utf-8");

	    // 下单逻辑
	    AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
	    data = afSupOrderService.addSupOrder(afUserDo.getRid(), goodsId, actualAmount, couponId, acctType, gameName, userName, goodsNum, priceTimes, goodsCount, gameType, gameAcct, gameArea, gameSrv, userIp);
	    if (data != null) {
		return H5CommonResponse.getNewInstance(true, "充值订单提交成功", "", data);
	    } else {
		return H5CommonResponse.getNewInstance(false, "充值订单提交失败", "", null);
	    }
	} catch (Exception e) {
	    logger.error("/game/pay/order error:", e);
	    return H5CommonResponse.getNewInstance(false, e.getMessage());
	}
    }

    @RequestMapping(value = "/order/delete", method = RequestMethod.POST)
    public H5CommonResponse deleteOrder(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
	try {
	    FanbeiWebContext context = doWebCheck(request, true);
	    Long orderId = Long.parseLong(request.getParameter("orderId"));
	    AfUserDo userDo = afUserDao.getUserByUserName(context.getUserName());
	    if (userDo == null)
		throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);

	    afOrderService.deleteOrder(userDo.getRid(), orderId);
	    return H5CommonResponse.getNewInstance(true, "订单删除成功", "", "");
	} catch (Exception e) {
	    logger.error("/game/pay/order error:", e);
	    return H5CommonResponse.getNewInstance(false, e.getMessage());
	}
    }

    @RequestMapping(value = "/orderInfo", method = RequestMethod.POST)
    public H5CommonResponse getOrderInfo(HttpServletRequest request, HttpServletResponse response) {
	try {
	    FanbeiWebContext context = doWebCheck(request, true);
	    String orderNo = request.getParameter("orderNo");
	    if (StringUtils.isNotBlank(orderNo)) {
		// 查询详情
		GameOrderInfoDto orderInfo = afSupOrderService.getOrderInfoByOrderNo(orderNo);
		return H5CommonResponse.getNewInstance(true, "获取订单信息成功", "", orderInfo);
	    } else {
		return H5CommonResponse.getNewInstance(false, "参数错误");
	    }
	} catch (Exception e) {
	    logger.error("/game/pay/orderInfo error:", e);
	    return H5CommonResponse.getNewInstance(false, e.getMessage());
	}
    }

    @RequestMapping(value = "/callback", method = RequestMethod.GET, produces = "application/x-www-form-urlencoded;charset=UTF-8")
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

	    logger.info(String.format("game pay callback:userOrderId %s,status %s, mes %s,kminfo %s,payoffPriceTotal %s,sign %s,businessId %s", userOrderId, status, mes, kminfo, payoffPriceTotal, sign, businessId));

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
	try {
	    RequestDataVo reqVo = new RequestDataVo();

	    JSONObject jsonObj = JSON.parseObject(requestData);
	    reqVo.setId(jsonObj.getString("id"));
	    reqVo.setMethod(request.getRequestURI());
	    reqVo.setSystem(jsonObj);
	    return reqVo;
	} catch (Exception e) {
	    throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
	}
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	return null;
    }
}
