package com.ald.fanbei.api.biz.util;

import java.math.BigDecimal;

import org.springframework.util.StringUtils;

import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.dal.domain.AfUpsLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserBankDidiRiskDo;
import com.ald.fanbei.api.dal.domain.AfUserVirtualAccountDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月31日下午3:53:45
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BuildInfoUtil {
	
	public static AfUpsLogDo buildUpsLog(String bankCode,String cardNumber,String name,String orderNo,String refId,String type,String userNo, String status){
    	AfUpsLogDo log = new AfUpsLogDo();
    	log.setBankCode(bankCode);
    	log.setCardNumber(cardNumber);
    	log.setName(name);
    	log.setOrderNo(orderNo);
    	log.setRefId(refId);
    	log.setType(type);
    	log.setUserId(NumberUtil.objToLongDefault(userNo, 0l));
    	log.setStatus(status);
    	return log;
    }
	
	public static AfOrderRefundDo buildOrderRefundDo(String refundNo, BigDecimal amount, BigDecimal actualAmount, Long userId,Long orderId,String orderNo,OrderRefundStatus refundStatus,PayType type,String accountNumber,String accountName,String content,String refundSource,String payTradeNo){
		AfOrderRefundDo orderRefundInfo = new AfOrderRefundDo();
		orderRefundInfo.setAmount(amount);
		orderRefundInfo.setActualAmount(actualAmount);
		orderRefundInfo.setUserId(userId);
		orderRefundInfo.setOrderId(orderId);
		orderRefundInfo.setOrderNo(orderNo);
		orderRefundInfo.setStatus(refundStatus.getCode());
		orderRefundInfo.setRefundNo(refundNo);
		orderRefundInfo.setType(type.getCode());
		orderRefundInfo.setAccountNumber(accountNumber);
		orderRefundInfo.setAccountName(StringUtils.isEmpty(accountName) ? type.getName() : accountName);
		orderRefundInfo.setContent(content);
		orderRefundInfo.setResource(refundSource);
		orderRefundInfo.setPayTradeNo(payTradeNo);
		return orderRefundInfo;
	}
	
	public static AfUserAccountLogDo buildUserAccountLogDo(UserAccountLogType logType,BigDecimal amount,Long userId,Long orderId){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(orderId+"");
		accountLog.setType(logType.getCode());
		return accountLog;
	}
	
	public static AfUserAccountLogDo buildUserAccountLogDo(UserAccountLogType logType,BigDecimal amount,Long userId,String orderId){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(orderId);
		accountLog.setType(logType.getCode());
		return accountLog;
	}
	
	public static AfUserVirtualAccountDo buildUserVirtualAccountDo(Long userId, BigDecimal amount, BigDecimal totalAmount,Long orderId, String orderNo, String virtualCode){
		//增加虚拟商品使用日志
		AfUserVirtualAccountDo virtualAccountInfo = new AfUserVirtualAccountDo();
		virtualAccountInfo.setAmount(amount);
		virtualAccountInfo.setOrderId(orderId);
		virtualAccountInfo.setOrderNo(orderNo);
		virtualAccountInfo.setYear(DateUtil.getCurrentYear());
		virtualAccountInfo.setMonth(DateUtil.getCurrentMonth());
		virtualAccountInfo.setCode(virtualCode);
		virtualAccountInfo.setUserId(userId);
		virtualAccountInfo.setAmount(amount);
		virtualAccountInfo.setTotalAmount(totalAmount);
		return virtualAccountInfo;
	}
	
	public static AfUserBankDidiRiskDo buildUserBankDidiRiskInfo(String ip, BigDecimal lat, BigDecimal lng, Long userId, Long userBankId, String uuid,String wifiMac){
		AfUserBankDidiRiskDo userBankDidiRiskInfo = new AfUserBankDidiRiskDo();
		userBankDidiRiskInfo.setIp(ip);
		userBankDidiRiskInfo.setLat(lat);
		userBankDidiRiskInfo.setLng(lng);
		userBankDidiRiskInfo.setUserId(userId);
		userBankDidiRiskInfo.setUserBankId(userBankId);
		userBankDidiRiskInfo.setUuid(uuid);
		userBankDidiRiskInfo.setWifiMac(wifiMac);
		return userBankDidiRiskInfo;
	}

}