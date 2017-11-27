package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfSupCallbackService;
import com.ald.fanbei.api.biz.service.AfSupOrderService;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.MD5Helper;
import com.ald.fanbei.api.dal.dao.AfSupOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfSupCallbackDo;
import com.ald.fanbei.api.dal.domain.AfSupOrderDo;

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

    @Resource
    private AfSupOrderDao afSupOrderDao;
    @Autowired
    private AfSupCallbackService afSupCallbackService;
    @Autowired
    private AfOrderService afOrderService;

    @Override
    public BaseDao<AfSupOrderDo, Long> getDao() {
	return afSupOrderDao;
    }

    @Override
    public String processCallbackResult(String userOrderId, String status, String mes, String kminfo, String payoffPriceTotal, String sign) {

	try {
	    // 幂等处理
	    AfSupCallbackDo afSupCallbackDoExist = afSupCallbackService.getByOrderNo(userOrderId);
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
		    //签名验证通过
		    AfOrderDo orderInfo = afOrderService.getOrderByOrderNo(userOrderId);
		    if (orderInfo != null) {
			//订单信息存在
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
}