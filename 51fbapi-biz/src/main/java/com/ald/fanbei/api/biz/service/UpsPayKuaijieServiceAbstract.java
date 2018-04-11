package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.UpsCollectBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.newFundNotifyReqBo;
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

@Service
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

    protected abstract void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo);

    protected abstract void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject);

    protected abstract void kuaijieConfirmPre(String payTradeNo, String bankChannel, String payBizObject);

    protected abstract Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo);

    protected abstract void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo);

    /**
     * 
     * TODO 快捷支付确认付款
     * 
     * @author gaojb
     * @Time 2018年4月2日 下午2:49:58
     * @param map
     * @param payTradeNo
     */
    public Map<String, Object> doUpsPay(String payTradeNo, String smsCode) {
	// 获取缓存中的支付信息
	UpsCollectBo upsCollectBo = getTradeCollectBo(payTradeNo);
	String payBizObject = getPayBizObject(payTradeNo);
	if (upsCollectBo != null && StringUtils.isNotBlank(payBizObject)) {
	    // 调用支付业务
	    return doUpsPay(BankPayChannel.KUAIJIE.getCode(), upsCollectBo.getCardId(), payTradeNo, upsCollectBo.getAmount(), Long.valueOf(upsCollectBo.getUserNo()), 
		    upsCollectBo.getRealName(), upsCollectBo.getCertNo(), smsCode, payBizObject, upsCollectBo.getPurpose(), upsCollectBo.getRemark(), 
		    upsCollectBo.getMerPriv());

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
    protected Map<String, Object> doUpsPay(String bankPayType, Long cardId, String payTradeNo, BigDecimal actualAmount, Long userId,
	    String realName, String idNumber, String smsCode, String payBizObject, String purpose, String remark, String merPriv) {
	// 获取用户绑定银行卡信息
	AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);

	// 调用ups进行支付
	UpsCollectRespBo respBo;
	if (BankPayChannel.KUAIJIE.getCode().equals(bankPayType)) { // 确认快捷支付
	    kuaijieConfirmPre(payTradeNo, bankPayType, payBizObject);
	    respBo = upsUtil.quickPayConfirm(payTradeNo, String.valueOf(userId), smsCode, "02", UserAccountLogType.REPAYMENT.getCode());
	} else { // 代扣
	    daikouConfirmPre(payTradeNo, bankPayType, payBizObject);
	    respBo = upsUtil.collect(payTradeNo, actualAmount, userId + "", realName, bank.getMobile(), bank.getBankCode(), bank.getCardNumber(), 
		    idNumber, purpose, remark, "02", merPriv);
	}

	// 处理支付结果
	if (!respBo.isSuccess()) {
	    // 调用ups接口失败，回滚业务数据
	    String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
	    roolbackBizData(payTradeNo, payBizObject, errorMsg, respBo);
	    clearCache(payTradeNo);
	    throw new FanbeiException(errorMsg);
	} else {
	    Map<String, Object> resultMap = upsPaySuccess(payTradeNo, bankPayType, payBizObject, respBo);
	    clearCache(payTradeNo);
	    return resultMap;
	}
    }

    private void clearCache(String payTradeNo) {
	// 移除缓存数据(确认后则清空缓存无论调用ups是否成功)
	bizCacheUtil.delCache(UpsUtil.KUAIJIE_TRADE_HEADER + payTradeNo);
	bizCacheUtil.delCache(UpsUtil.KUAIJIE_TRADE_RESPONSE_HEADER + payTradeNo);
	bizCacheUtil.delCache(UpsUtil.KUAIJIE_TRADE_OBJECT_HEADER + payTradeNo);
	bizCacheUtil.delCache(UpsUtil.KUAIJIE_TRADE_BEAN_ID + payTradeNo);

    }

    /**
     * 
     * TODO 进行快捷支付，获取验证短息码
     * 
     * @author gaojb
     * @Time 2018年4月2日 下午2:50:58
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
    protected Map<String, Object> sendKuaiJieSms(Long cardId, String payTradeNo, BigDecimal actualAmount, Long userId, String realName, 
	    String idNumber, String payBizObject, String beanName, String purpose, String remark, String merPriv) {
	// 申请发送支付确认短信
	AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
	UpsCollectRespBo respBo = (UpsCollectRespBo) upsUtil.quickPay(payTradeNo, actualAmount, userId + "", realName, bank.getMobile(), 
		bank.getBankCode(), bank.getCardNumber(), idNumber, purpose, remark, "02", merPriv, afResourceService.getCashProductName());

	// 处理支付结果
	if (!respBo.isSuccess()) {
	    // 获取短信码失败
	    String errorMsg = afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode());
	    throw new FanbeiException(errorMsg);
	} else {
	    // 添加数据到redis缓存
	    UpsCollectBo upsCollectBo = new UpsCollectBo(cardId, payTradeNo, actualAmount, userId + "", realName, bank.getMobile(), bank.getBankCode(), 
		    bank.getCardNumber(), idNumber, Constants.DEFAULT_PAY_PURPOSE, remark, "02", merPriv, BankPayChannel.KUAIJIE.getCode(), 
		    afResourceService.getCashProductName());
	    // 支付请求对应的处理bean
	    bizCacheUtil.saveObject(UpsUtil.KUAIJIE_TRADE_BEAN_ID + payTradeNo, beanName, UpsUtil.KUAIJIE_EXPIRE_SECONDS);
	    // 支付请求数据
	    bizCacheUtil.saveObject(UpsUtil.KUAIJIE_TRADE_HEADER + payTradeNo, JSON.toJSONString(upsCollectBo), UpsUtil.KUAIJIE_EXPIRE_SECONDS);
	    // 支付响应数据
	    bizCacheUtil.saveObject(UpsUtil.KUAIJIE_TRADE_RESPONSE_HEADER + payTradeNo, JSON.toJSONString(respBo), UpsUtil.KUAIJIE_EXPIRE_SECONDS);
	    // 支付相关业务数据（由子类业务处理）
	    bizCacheUtil.saveObject(UpsUtil.KUAIJIE_TRADE_OBJECT_HEADER + payTradeNo, payBizObject, UpsUtil.KUAIJIE_EXPIRE_SECONDS);

	    quickPaySendSmmSuccess(payTradeNo, payBizObject, respBo);
	}

	Map<String, Object> resultMap = new HashMap<String, Object>();
	resultMap.put("resp", respBo);
	return resultMap;
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

    /**
     * 
     * 获取缓存的业务数据
     * 
     * @author gaojb
     * @Time 2018年4月3日 下午2:34:55
     * @return
     */
    protected String getPayBizObject(String payTradeNo) {
	// 支付相关业务数据（由子类业务处理）
	Object cacheObject = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_OBJECT_HEADER + payTradeNo);
	if (cacheObject != null) {
	    return cacheObject.toString();
	} else {
	    return null;
	}
    }
}
