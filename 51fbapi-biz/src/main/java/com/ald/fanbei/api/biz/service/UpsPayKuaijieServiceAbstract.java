package com.ald.fanbei.api.biz.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.ald.fanbei.api.biz.bo.UpsCollectBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.alibaba.fastjson.JSON;

public abstract class UpsPayKuaijieServiceAbstract extends BaseService {

    @Autowired
    protected BizCacheUtil bizCacheUtil;
    @Autowired
    protected AfUserBankcardDao afUserBankcardDao;
    @Autowired
    protected UpsUtil upsUtil;
    @Autowired
    protected AfResourceService afResourceService;
    @Autowired
    protected AfTradeCodeInfoService afTradeCodeInfoService;

    protected abstract void quickPayConfirmPre(String payTradeNo);

    protected abstract void sendFailMessage(String payTradeNo, Long userId, String errorMsg);

    protected abstract void roolbackRepamentStatus(String payTradeNo);

    /**
     * 
     * TODO 快捷支付确认付款
     * 
     * @author gaojb
     * @Time 2018年4月2日 下午2:49:58
     * @param map
     * @param payTradeNo
     */
    protected void doUpsPay(Map<String, Object> map, String payTradeNo, String smsCode) {
	// 获取缓存中的支付信息
	Object cacheObject = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_HEADER + payTradeNo);
	if (cacheObject != null) {
	    // 调用支付业务
	    UpsCollectBo upsCollectBo = (UpsCollectBo) cacheObject;
	    doUpsPay(map, BankPayChannel.KUAIJIE.getCode(), upsCollectBo.getCardId(), payTradeNo, upsCollectBo.getAmount(), Long.valueOf(upsCollectBo.getUserNo()), upsCollectBo.getRealName(), upsCollectBo.getCertNo(), smsCode);

	} else {
	    // 未获取到缓存数据，支付订单过期
	    throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
	}
    }

    /**
     * 
     * TODO 调用ups支付方法（代扣和快捷）
     * 
     * @author gaojb
     * @Time 2018年4月2日 下午2:50:24
     * @param map
     * @param bankPayType
     * @param cardId
     * @param repayment
     * @param billIdList
     * @param payTradeNo
     * @param actualAmount
     * @param userId
     * @param realName
     * @param idNumber
     */
    protected void doUpsPay(Map<String, Object> map, String bankPayType, Long cardId, String payTradeNo, BigDecimal actualAmount, Long userId, String realName, String idNumber, String smsCode) {
	// 获取用户绑定银行卡信息
	AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);

	// 调用ups进行支付
	UpsCollectRespBo respBo;
	if (BankPayChannel.DAIKOU.getCode().equals(bankPayType)) { // 代付
	    respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", realName, bank.getMobile(), bank.getBankCode(), bank.getCardNumber(), idNumber, Constants.DEFAULT_PAY_PURPOSE, "还款", "02", UserAccountLogType.REPAYMENT.getCode());
	} else { // 确认快捷支付
	    quickPayConfirmPre(payTradeNo);
	    respBo = upsUtil.quickPayConfirm(payTradeNo, String.valueOf(userId), smsCode, bank.getCardNumber(), bank.getBankCode(), "02", UserAccountLogType.REPAYMENT.getCode());
	}

	// 处理支付结果
	if (!respBo.isSuccess()) {
	    // 调用ups接口失败，回滚业务数据
	    roolbackRepamentStatus(payTradeNo);
	    String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());

	    try {
		// 业务失败短信通知
		sendFailMessage(payTradeNo, userId, errorMsg);
	    } catch (Exception e) {
		logger.error("doUpsPay sendMessage error:" + e);
	    }

	    throw new FanbeiException(errorMsg);
	} else {
	    // 移除缓存数据
	    bizCacheUtil.delCache(UpsUtil.KUAIJIE_TRADE_HEADER + payTradeNo);
	    bizCacheUtil.delCache(UpsUtil.KUAIJIE_TRADE_RESPONSE_HEADER + payTradeNo);
	    bizCacheUtil.delCache(UpsUtil.KUAIJIE_TRADE_OBJECT_HEADER + payTradeNo);
	}

	map.put("resp", respBo);
    }

    /**
     * 
     * TODO 进行快捷支付，获取验证短息码
     * 
     * @author gaojb
     * @Time 2018年4月2日 下午2:50:58
     * @param map
     * @param bankPayType
     * @param cardId
     * @param repayment
     * @param billIdList
     * @param payTradeNo
     * @param actualAmount
     * @param userId
     * @param realName
     * @param idNumber
     */
    protected void sendKuaiJieSms(Map<String, Object> map, String bankPayType, Long cardId, String payTradeNo, BigDecimal actualAmount, Long userId, String realName, String idNumber, String payBizObject) {
	// //获取缓存中的还款信息
	// String repaymentHash =
	// String.valueOf(repayment.toString().hashCode());
	// Object upsCollectRespBoObject =
	// bizCacheUtil.getObject(repaymentHash);
	// if (upsCollectRespBoObject != null) {
	// UpsCollectRespBo upsCollectResp = (UpsCollectRespBo)
	// upsCollectRespBoObject;
	// if ((new Date().getTime() - upsCollectResp.getTradeTime())/1000 <=
	// UpsUtil.KUAIJIE_ONE_MINITE_SECONDS) {
	// // 一分钟内存在，则直接返回上次响应
	// map.put("resp", upsCollectResp);
	// return;
	// }
	// else{
	// //重新发送验证码
	// upsUtil.quickPayResendSms(upsCollectResp.getTradeNo());
	// }
	// }

	// 申请发送支付确认短信
	AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
	UpsCollectRespBo respBo = (UpsCollectRespBo) upsUtil.quickPay(payTradeNo, actualAmount, userId + "", realName, bank.getMobile(), 
		bank.getBankCode(), bank.getCardNumber(), idNumber, Constants.DEFAULT_PAY_PURPOSE, "还款", "02", UserAccountLogType.REPAYMENT.getCode(), 
		afResourceService.getCashProductName());

	// 处理支付结果
	if (!respBo.isSuccess()) {
	    // 获取短信码失败
	    String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
	    throw new FanbeiException(errorMsg);
	} else {
	    // 添加数据到redis缓存
	    UpsCollectBo upsCollectBo = new UpsCollectBo(cardId, payTradeNo, actualAmount, userId + "", realName, bank.getMobile(), 
		    bank.getBankCode(), bank.getCardNumber(), idNumber, Constants.DEFAULT_PAY_PURPOSE, "还款", "02", UserAccountLogType.REPAYMENT.getCode(), bankPayType, afResourceService.getCashProductName());
	    // 支付请求数据
	    bizCacheUtil.saveObject(UpsUtil.KUAIJIE_TRADE_HEADER + payTradeNo, JSON.toJSONString(upsCollectBo), UpsUtil.KUAIJIE_EXPIRE_SECONDS);
	    // 支付响应数据
	    bizCacheUtil.saveObject(UpsUtil.KUAIJIE_TRADE_RESPONSE_HEADER + payTradeNo, JSON.toJSONString(respBo), UpsUtil.KUAIJIE_EXPIRE_SECONDS);
	    // 支付相关业务数据（由子类业务处理）
	    bizCacheUtil.saveObject(UpsUtil.KUAIJIE_TRADE_OBJECT_HEADER + payTradeNo, payBizObject, UpsUtil.KUAIJIE_EXPIRE_SECONDS);
	    // 返回结果
	    map.put("resp", respBo);
	}
    }

    /**
     * 
     * 获取缓存的支付相关信息
     * 
     * @author gaojb
     * @Time 2018年4月3日 下午2:34:06
     * @param payTradeNo
     * @return
     */
    protected UpsCollectBo getTradeCollectBo(String payTradeNo) {
	Object cacheObject = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_HEADER + payTradeNo);
	if (cacheObject != null) {
	    return (UpsCollectBo) JSON.parseObject(cacheObject.toString(), UpsCollectBo.class);
	}
	return null;
    }

    /**
     * 
     * 获取缓存的支付结果信息
     * 
     * @author gaojb
     * @Time 2018年4月3日 下午2:34:33
     * @param payTradeNo
     * @return
     */
    protected UpsCollectRespBo getTradeResponse(String payTradeNo) {
	// 支付响应数据
	Object cacheObject = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_RESPONSE_HEADER + payTradeNo);
	if (cacheObject != null) {
	    return (UpsCollectRespBo) JSON.parseObject(cacheObject.toString(), UpsCollectRespBo.class);
	}
	return null;
    }

    /**
     * 
     * 获取缓存的业务数据
     * 
     * @author gaojb
     * @Time 2018年4月3日 下午2:34:55
     * @param payTradeNo
     * @return
     */
    protected <T> T getPayBizObject(String payTradeNo, Class<T> clazz) {
	// 支付相关业务数据（由子类业务处理）
	Object cacheObject = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_OBJECT_HEADER + payTradeNo);
	if (cacheObject != null) {
	    return JSON.parseObject(cacheObject.toString(), clazz);
	}
	return null;
    }
}
