package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSupCallbackService;
import com.ald.fanbei.api.biz.service.AfSupGameService;
import com.ald.fanbei.api.biz.service.AfSupOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.MD5Helper;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfSupOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfSupCallbackDo;
import com.ald.fanbei.api.dal.domain.AfSupGameDo;
import com.ald.fanbei.api.dal.domain.AfSupOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;

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
		String signCheck = MD5Helper.md5("businessId" + userOrderId + status + "key");
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
			// 订单信息存在
			if (afSupCallbackDo.getStatus().equals("01")) {
			    // 充值成功(更改订单状态、返利)
			    afOrderService.callbackCompleteOrder(orderInfo);
			} else { // 充值失败（更改订单状态、退款）
			    afOrderService.dealBrandOrderRefund(orderInfo.getRid(), orderInfo.getUserId(), orderInfo.getBankId(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), orderInfo.getActualAmount(), orderInfo.getActualAmount(), orderInfo.getPayType(), orderInfo.getPayTradeNo(), orderInfo.getOrderNo(), "SUP");
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
    public Map<String, Object> addSupOrder(Long userId, Long goodsId, BigDecimal actualAmount, Long couponId, String acctType, String gameName, String userName, Integer goodsNum, String gameType, String gameAcct, String gameArea, String gameSrv, String userIp) {
	// 验证参数须大于零
	if (actualAmount.compareTo(BigDecimal.ZERO) <= 0 || goodsNum <= 0) {
	    throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
	}

	// 获取游戏产品信息
	AfSupGameDo supGameDo = afSupGameService.getById(goodsId);
	if (supGameDo == null) {
	    logger.error("sup game is not exist :" + goodsId);
	    throw new FanbeiException(FanbeiExceptionCode.GAME_IS_NOT_EXIST);
	}

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
	// 验证优惠卷
	BigDecimal checkActualAmount = supGameDo.getBusinessDiscount().multiply(new BigDecimal(goodsNum));
	BigDecimal rebateAmountScale = supGameDo.getOfficalDiscount().subtract(supGameDo.getBusinessDiscount());
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

	if (couponAmount.compareTo(BigDecimal.ZERO) > 0) {
	    afUserCouponService.updateUserCouponSatusUsedById(couponId);
	}

	// 添加订单信息
	AfOrderDo afOrder = new AfOrderDo();
	afOrder.setUserId(userId);
	afOrder.setGoodsPriceId(goodsId);
	afOrder.setActualAmount(actualAmount);
	afOrder.setSaleAmount(actualAmount);
	afOrder.setRebateAmount(rebateAmountScale);
	afOrder.setGmtCreate(new Date());
	afOrder.setGmtPayEnd(DateUtil.addHoures(new Date(), Constants.ORDER_PAY_TIME_LIMIT));
	afOrder.setPriceAmount(actualAmount);
	afOrder.setGoodsIcon(supGameDo.getImage());
	afOrder.setGoodsName(supGameDo.getName());
	afOrder.setGoodsId(goodsId);
	afOrder.setOrderType(OrderType.BOLUOME.getCode());
	afOrder.setOrderNo(generatorClusterNo.getOrderNo(OrderType.SELFSUPPORT));
	afOrder.setCount(goodsNum);
	afOrder.setUserCouponId(couponId);
	AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(userId);
	afOrder.setAuAmount(userAccountInfo.getAuAmount());
	afOrder.setUsedAmount(userAccountInfo.getUsedAmount());
	afOrder.setThirdDetailUrl("");
	afOrderService.createOrder(afOrder);

	Map<String, Object> data = new HashMap<String, Object>();
	data.put("orderId", afOrder.getRid());
	data.put("isEnoughAmount", "Y");
	data.put("isNoneQuota", "N");

	return data;
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
}