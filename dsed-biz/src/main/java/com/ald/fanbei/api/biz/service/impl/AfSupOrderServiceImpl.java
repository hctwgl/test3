package com.ald.fanbei.api.biz.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import jiazhiyi.web.com.OrderEntity;
import jiazhiyi.web.com.OrderReceive;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSupCallbackService;
import com.ald.fanbei.api.biz.service.AfSupGameService;
import com.ald.fanbei.api.biz.service.AfSupOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.MD5Helper;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.OrderSecType;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfSupOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfSupCallbackDo;
import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.dal.domain.AfSupOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.dto.GameOrderInfoDto;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * 新人专享ServiceImpl
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-22 13:57:29 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afSupOrderService")
public class AfSupOrderServiceImpl extends ParentServiceImpl<AfSupOrderDo, Long> implements AfSupOrderService {

    private static final Logger logger = LoggerFactory.getLogger(AfSupOrderServiceImpl.class);
    
    @Autowired
    private AfUserAccountSenceService afUserAccountSenceService;

    @Override
    public BaseDao<AfSupOrderDo, Long> getDao() {
	return afSupOrderDao;
    }

    @Override
    public String processCallbackResult(String userOrderId, String status, String mes, String kminfo, String payoffPriceTotal, String sign) {

	try {
	    // 幂等处理
	    AfSupCallbackDo afSupCallbackDoExist = afSupCallbackService.getCompleteByOrderNo(userOrderId);
	    if (afSupCallbackDoExist == null) {
		// 计算签名
		String businessId = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SUP_BUSINESS_ID), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String supKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SUP_BUSINESS_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		String signCheck = MD5Helper.md5(businessId + userOrderId + status + supKey);
		// 记录回调数据
		AfSupCallbackDo afSupCallbackDo = new AfSupCallbackDo();
		afSupCallbackDo.setKminfo(kminfo);
		afSupCallbackDo.setMes(mes);
		afSupCallbackDo.setOrderNo(userOrderId);
		afSupCallbackDo.setPayoffPriceTotal(new BigDecimal(payoffPriceTotal));
		afSupCallbackDo.setSign(sign);
		afSupCallbackDo.setSignCheck(signCheck);
		afSupCallbackDo.setStatus(status);
		if (sign.equals(signCheck)) {
		    afSupCallbackDo.setResult(1);
		} else {
		    afSupCallbackDo.setResult(0);
		}
		afSupCallbackService.saveRecord(afSupCallbackDo);

		if (afSupCallbackDo.getResult() == 1) {
		    // 签名验证通过
		    AfOrderDo orderInfo = afOrderService.getOrderByOrderNo(userOrderId);
		    if (orderInfo != null) {
			GameOrderInfoDto supOrderDo = afSupOrderDao.getOrderInfoByOrderNo(userOrderId);
			AfUserDo userDo = afUserDao.getUserById(orderInfo.getUserId());
			// 订单信息存在
			if (afSupCallbackDo.getStatus().equals("01")) {
			    // 充值成功(更改订单状态、返利)
			    afOrderService.callbackCompleteOrder(orderInfo);
			    smsUtil.sendGamePayResultToUser(userDo.getUserName(), supOrderDo.getGoodsName(), "成功");
			} else { // 充值失败（更改订单状态、退款）
			    afSupOrderDao.updateMsgByOrder(orderInfo.getOrderNo(), mes);
			    afOrderService.dealBrandOrderRefund(orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getBankId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getActualAmount(), orderInfo.getActualAmount(), orderInfo.getPayType(), orderInfo.getPayTradeNo(), orderInfo.getOrderNo(), "SUP");
			    smsUtil.sendGamePayResultToUser(userDo.getUserName(), supOrderDo.getGoodsName(), "失败");
			}
			return "<receive>ok</receive>";
		    } else {
			return "<receive>orderNo error</receive>";
		    }
		} else {
		    return "<receive>sign error</receive>";
		}
	    } else {
		return "<receive>ok</receive>";
	    }
	} catch (Exception e) {
	    logger.error("/game/pay/callback processCallbackResult error:", e);
	    return "<receive>exception error</receive>";
	}
    }

    @Override
    public Map<String, Object> addSupOrder(final Long userId, final Long goodsId, final BigDecimal actualAmount, final Long couponId, final String acctType, final String gameName, final String userName, final Integer goodsNum, final Integer priceTimes, final Integer goodsCount, final String gameType, final String gameAcct, final String gameArea, final String gameSrv, final String userIp) throws Exception {
	// 验证参数须大于零
	if (actualAmount.compareTo(BigDecimal.ZERO) <= 0 || goodsNum <= 0) {
	    throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
	}

	// 获取游戏产品信息
	final AfSupGameDo supGameDo = afSupGameService.getById(goodsId);
	if (supGameDo == null) {
	    logger.error("sup game is not exist :" + goodsId);
	    throw new FanbeiException(FanbeiExceptionCode.GAME_IS_NOT_EXIST);
	}
	// 验证传递参数信息
	final String gameCode = getGameCodeValue(gameName, gameType, gameAcct, gameSrv, gameArea, supGameDo.getXmlFile());

	// 获取优惠卷信息
	BigDecimal couponAmount = new BigDecimal(0);
	if (couponId > 0) {
	    AfUserCouponDto couponDo = afUserCouponService.getUserCouponById(couponId);
	    if (couponDo.getGmtEnd().before(new Date()) || StringUtils.equals(couponDo.getStatus(), CouponStatus.EXPIRE.getCode())) {
		logger.error("coupon end less now :" + couponId);
		throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
	    }
	    couponAmount = couponDo.getAmount();
	}
	final BigDecimal couponAmountFinal = couponAmount;
	// 验证优惠卷
	BigDecimal checkActualAmount = supGameDo.getOfficalDiscount().multiply(new BigDecimal(goodsNum));
	final BigDecimal rebateAmountScale = supGameDo.getOfficalDiscount().subtract(supGameDo.getBusinessDiscount());
	checkActualAmount = checkActualAmount.subtract(couponAmount);
	if (checkActualAmount.compareTo(BigDecimal.ZERO) <= 0) {
	    logger.error("checkActualAmount less than zero:couponId " + couponId + " ,goodsId:" + goodsId + " ,checkActualAmount:" + checkActualAmount);
	    throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
	}
	// 验证客户端传递金额参数
	if (actualAmount.compareTo(checkActualAmount) != 0) {
	    logger.error("checkActualAmount actualAmount " + actualAmount + " ,checkActualAmount:" + checkActualAmount);
	    throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
	}

	// 通过事物添加订单信息
	final AfOrderDo afOrder = new AfOrderDo();
	Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
	    @Override
	    public Integer doInTransaction(TransactionStatus status) {
		try {
		    if (couponAmountFinal.compareTo(BigDecimal.ZERO) > 0) {
			afUserCouponService.updateUserCouponSatusUsedById(couponId);
		    }
		    // 添加订单信息
		    afOrder.setUserId(userId);
		    afOrder.setGoodsPriceId(goodsId);
		    // 记录订单实际支付金额
		    afOrder.setActualAmount(actualAmount);
		    // 记录订单原始金额
		    afOrder.setSaleAmount(actualAmount.add(couponAmountFinal));
		    afOrder.setCouponAmount(couponAmountFinal);
		    afOrder.setRebateAmount(rebateAmountScale.multiply(actualAmount));
		    afOrder.setGmtCreate(new Date());
		    afOrder.setGmtPayEnd(DateUtil.addHoures(new Date(), Constants.ORDER_PAY_TIME_LIMIT));
		    afOrder.setPriceAmount(actualAmount);
		    afOrder.setGoodsIcon(supGameDo.getImage());
		    afOrder.setGoodsName(supGameDo.getName());
		    afOrder.setGoodsId(goodsId);
		    afOrder.setOrderType(OrderType.BOLUOME.getCode());
		    afOrder.setOrderNo(generatorClusterNo.getOrderNo(OrderType.BOLUOME));
		    afOrder.setThirdOrderNo(afOrder.getOrderNo());
		    afOrder.setCount(1);
		    afOrder.setUserCouponId(couponId);
		    AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), NumberUtil.objToLongDefault(userId, 0l));
		    //AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
		    if (afUserAccountSenceDo != null) {
        		    afOrder.setAuAmount(afUserAccountSenceDo.getAuAmount());
        		    afOrder.setUsedAmount(afUserAccountSenceDo.getUsedAmount());
		    }
		    afOrder.setThirdDetailUrl(getOrderDetailsUrl(afOrder.getOrderNo()));
		    afOrder.setSecType(OrderSecType.SUP_GAME.getCode());
		    afOrderService.createOrder(afOrder);
		    // 添加订单相关游戏充值信息
		    AfSupOrderDo supOrderDo = new AfSupOrderDo();
		    supOrderDo.setAcctType(acctType);
		    supOrderDo.setGameAcct(gameAcct);
		    supOrderDo.setGameArea(gameArea);
		    supOrderDo.setGameName(gameName);
		    supOrderDo.setGameSrv(gameSrv);
		    supOrderDo.setGameType(gameType);
		    supOrderDo.setGoodsCode(gameCode);
		    supOrderDo.setGoodsId(goodsId);
		    supOrderDo.setGoodsNum(goodsNum);
		    supOrderDo.setGoodsCount(goodsCount);
		    supOrderDo.setOrderNo(afOrder.getOrderNo());
		    supOrderDo.setUserIp(userIp);
		    supOrderDo.setUserName(userName);
		    afSupOrderDao.saveRecord(supOrderDo);

		    return 1;
		} catch (Exception e) {
		    status.setRollbackOnly();
		    logger.info("dealMobileChargeOrder error:", e);
		    return 0;
		}
	    }
	});

	Map<String, Object> data = new HashMap<String, Object>();
	if (result == 1) {
	    data.put("orderNo", afOrder.getOrderNo());
	    data.put("plantform", OrderType.BOLUOME.getCode());
	    return data;
	} else {
	    return null;
	}
    }

    private String getOrderDetailsUrl(String orderNo) {
	// 路径+参数信息
	return ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + "/fanbei-web/activity/gameOrderDetail?orderNo=" + orderNo;
    }

    @Override
    public String sendOrderToSup(String orderNo, String goodsId, String userName, String gameName, String gameAcct, String gameArea, String gameType, String acctType, Integer goodsNum, String gameSrv, String orderIp) {
	// 构造充值对象
	OrderEntity orderEntity = new OrderEntity();
	orderEntity.setAcctType(acctType);
	orderEntity.setBusinessId(AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SUP_BUSINESS_ID), ConfigProperties.get(Constants.CONFKEY_AES_KEY)));
	orderEntity.setGameAcct(gameAcct);
	orderEntity.setGameArea(gameArea);
	orderEntity.setGameName(gameName);
	orderEntity.setGameSrv(gameSrv);
	orderEntity.setGameType(gameType);
	orderEntity.setGoodsId(goodsId);
	orderEntity.setGoodsNum(goodsNum);
	String supKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_SUP_BUSINESS_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
	orderEntity.setKey(supKey);
	orderEntity.setNoticeUrl(ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + "/game/pay/callback");
	orderEntity.setOrderIp(orderIp);
	orderEntity.setUserName(userName);
	orderEntity.setUserOrderId(orderNo);
	// 提交充值信息
	try {
	    logger.info(String.format("sendOrderToSup :acctType %s,businessId %s,gameAcct %s,gameName %s,gameSrv %s,gameType %s,goodsId %s,goodsNum %s,supKey %s, callback %s, userName %s,orderNo %s", acctType, orderEntity.getBusinessId(), gameAcct, gameName, gameSrv, gameType, goodsId, goodsNum, supKey, orderEntity.getNoticeUrl(), userName, orderNo));
	    return OrderReceive.SendOrder(orderEntity);
	} catch (UnsupportedEncodingException e) {
	    logger.error("sendOrderToSup UnsupportedEncodingException", e);

	    return "";
	}
    }

    @Override
    public AfSupOrderDo getByOrderNo(String orderNo) {

	return afSupOrderDao.getByOrderNo(orderNo);
    }

    @Override
    public Integer updateMsgByOrder(String orderNo, String msg) {

	return afSupOrderDao.updateMsgByOrder(orderNo, msg);
    }

    @Override
    public GameOrderInfoDto getOrderInfoByOrderNo(String orderNo) {
	GameOrderInfoDto orderInfoDto = afSupOrderDao.getOrderInfoByOrderNo(orderNo);
	orderInfoDto.setPlantform(OrderType.BOLUOME.getCode());
	return orderInfoDto;
    }

    @Override
    public String getGameCodeValue(String gameName, String gameType, String gameAcct, String gameSrv, String gameArea, String xmlContent) throws Exception {
	// 验证游戏信息
	String gameCode = "";
	Document document = DocumentHelper.parseText(xmlContent);
	String nodePath = String.format("/root/deposititem/games/game[@name='%s']", gameName);

	Element game = (Element) document.selectSingleNode(nodePath);
	if (game != null) {
	    Attribute needGameAcct = game.attribute("needGameAcct");
	    if (needGameAcct != null) {
		if ("1".equals(needGameAcct.getValue()) && StringUtils.isBlank(gameAcct)) {
		    throw new FanbeiException("游戏账号不可为空", FanbeiExceptionCode.PARAM_ERROR);
		}
	    }

	    // 获取游戏的code属性
	    Attribute code = game.attribute("code");
	    if (code != null) {
		gameCode = code.getValue();
	    }
	} else {
	    throw new FanbeiException("游戏名称错误", FanbeiExceptionCode.GAME_IS_ILLEGAL);
	}
	// 验证充值类型
	if (checkNodeNameAttrinbute(document, nodePath + "/types/type") && StringUtils.isBlank(gameType)) {
	    throw new FanbeiException("未传充值类型参数", FanbeiExceptionCode.PARAM_ERROR);
	}

	// 验证充值服
	if (checkNodeNameAttrinbute(document, nodePath + "/areas") && StringUtils.isBlank(gameSrv)) {
	    throw new FanbeiException("未传游戏服参数", FanbeiExceptionCode.PARAM_ERROR);
	}

	// 验证充值区
	if (checkNodeNameAttrinbute(document, nodePath + "/areas/area") && StringUtils.isBlank(gameArea)) {
	    throw new FanbeiException("未传游戏区参数", FanbeiExceptionCode.PARAM_ERROR);
	}

	if (StringUtils.isBlank(gameCode)) {
	    // 获取游戏的code属性
	    String gameCodePath = nodePath + String.format("/types/type[@name='%s']", gameType);
	    Element codeNode = (Element) document.selectSingleNode(gameCodePath);
	    Attribute codeAttribute = codeNode.attribute("code");
	    if (codeAttribute != null) {
		gameCode = codeAttribute.getValue();
	    } else {
		throw new FanbeiException("获取游戏CODE失败", FanbeiExceptionCode.PARAM_ERROR);
	    }
	}

	return gameCode;
    }

    /**
     * 验证游戏充值业务参数传递状态
     * 
     * @author gaojb
     * @Time 2017年12月4日 上午10:57:02
     * @param document
     * @param nodePath
     * @return
     */
    private boolean checkNodeNameAttrinbute(Document document, String nodePath) {
	Element firstNode = (Element) document.selectSingleNode(nodePath);
	if (firstNode != null) {
	    Attribute nameAttibute = firstNode.attribute("name");
	    if (nameAttibute != null && !"".equals(nameAttibute.getValue())) {
		return true;
	    }
	}

	return false;
    }

    @Resource
    private AfSupOrderDao afSupOrderDao;
    @Autowired
    private AfSupCallbackService afSupCallbackService;
    @Autowired
    private AfOrderService afOrderService;

    @Autowired
    private AfUserCouponService afUserCouponService;
    @Autowired
    private AfUserAccountService afUserAccountService;
    @Autowired
    private AfResourceService afResourceService;
    @Autowired
    private AfSupGameService afSupGameService;
    @Autowired
    private GeneratorClusterNo generatorClusterNo;

    @Autowired
    private AfUserDao afUserDao;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private SmsUtil smsUtil;
}