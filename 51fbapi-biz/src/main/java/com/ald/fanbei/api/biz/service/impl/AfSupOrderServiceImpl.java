package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSupCallbackService;
import com.ald.fanbei.api.biz.service.AfSupOrderService;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.MD5Helper;
import com.ald.fanbei.api.dal.dao.AfSupOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
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

    @Override
    public BaseDao<AfSupOrderDo, Long> getDao() {
	return afSupOrderDao;
    }

    @Override
    public String processCallbackResult(String userOrderId, String status, String mes, String kminfo, String payoffPriceTotal, String sign) {

	try {
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
		// 验签通过，处理订单信息（事物）

		return "<receive>ok</receive>";
	    } else {
		return "<receive>sing error</receive>";
	    }
	} catch (Exception e) {
	    logger.error("/game/pay/callback processCallbackResult error:", e);
	    return "<receive>exception error</receive>";
	}
    }
}