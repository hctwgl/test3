package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoCardInfoBo;
import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoLoginInfoBo;
import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoPayInfoBo;
import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoRespBo;
import com.ald.fanbei.api.biz.bo.IPTransferBo;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserBankDidiRiskService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.BoluomeService;
import com.ald.fanbei.api.biz.third.util.IPTransferUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BoluomePayType;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserBankDidiRiskDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年8月10日下午5:58:32
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("boluomeService")
public class BoloumeServiceImpl implements BoluomeService {

	@Resource
	AfUserLoginLogService afUserLoginLogService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserBankDidiRiskService afUserBankDidiRiskService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	IPTransferUtil iPTransferUtil;
	
	@Override
	public BoluomeGetDidiRiskInfoRespBo getRiskInfo(String orderId, String type, Long userId) {
		BoluomeGetDidiRiskInfoRespBo resp = new BoluomeGetDidiRiskInfoRespBo();
		AfUserDo userInfo = afUserService.getUserById(userId);
		if (userInfo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
		}
		if ("PASSAGE_INFO".equals(type)) {
			//获取绑卡信息
			BoluomeGetDidiRiskInfoCardInfoBo card_info = getCardInfo(type,null,userInfo);
			//获取登陆相关信息
			BoluomeGetDidiRiskInfoLoginInfoBo login_info = getLoginInfo(userInfo);
			resp.setCard_info(card_info);
			resp.setLogin_info(login_info);
		} else if("PAY_INFO".equals(type)){
			AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(OrderType.BOLUOME.getCode(), orderId);
			if (orderInfo == null) {
				throw new FanbeiException(FanbeiExceptionCode.ORDER_NOT_EXIST);
			}
			//获取绑卡信息
			BoluomeGetDidiRiskInfoCardInfoBo card_info = getCardInfo(type,orderInfo,userInfo);
			//获取payInfo
			BoluomeGetDidiRiskInfoPayInfoBo pay_info = getPayInfo(orderInfo);
			resp.setCard_info(card_info);
			resp.setPay_Info(pay_info);
		}
		
		return resp;
	}
	
	private BoluomeGetDidiRiskInfoPayInfoBo getPayInfo(AfOrderDo orderInfo) {
		BoluomeGetDidiRiskInfoPayInfoBo pay_info = new BoluomeGetDidiRiskInfoPayInfoBo();
		pay_info.setLat(orderInfo.getLat());
		pay_info.setLng(orderInfo.getLng());
		if (orderInfo.getBankId() < 0) {
			pay_info.setPay_type(BoluomePayType.WECHAT.getCode());
		} else {
			pay_info.setPay_type(BoluomePayType.DEBITCARD.getCode());
		}
		pay_info.setTime(orderInfo.getGmtPay().getTime());
		return pay_info;
	}
	
	private BoluomeGetDidiRiskInfoCardInfoBo getCardInfo(String type, AfOrderDo orderInfo, AfUserDo userInfo) {
		BoluomeGetDidiRiskInfoCardInfoBo card_info = new BoluomeGetDidiRiskInfoCardInfoBo();
		//当为微信支付，或者额度支付，获取默认滴滴风控银行卡信息
		AfUserBankDidiRiskDo conditionInfo = new AfUserBankDidiRiskDo();
		Long userId = userInfo.getRid();
		Long bankId = 0l;
		//当PAY_INFO 一定为bankId
		if ("PAY_INFO".equals(type)){
			bankId = orderInfo.getBankId();
		}
		conditionInfo.setUserBankId(bankId <= 0 ? 0l : bankId);
		AfUserBankDidiRiskDo bankInfo =afUserBankDidiRiskService.getByCommonCondition(conditionInfo);
		//当用户没有绑卡，给个默认值
		if (bankInfo == null) {
			AfUserLoginLogDo loginInfo = afUserLoginLogService.getUserLastLoginInfo(userInfo.getUserName());
			//用户id MD5加密
			card_info.setPeople_id(DigestUtil.MD5(userId + StringUtils.EMPTY));
			//用户id + 银行卡id MD5加密
			card_info.setCard_id(DigestUtil.MD5(userId + "0" + StringUtils.EMPTY));
			card_info.setChannel(BoluomePayType.DEBITCARD.getCode());
			card_info.setDeviceid(loginInfo.getUuid());
			//转换ip
			IPTransferBo ipResult = iPTransferUtil.parseIpToLatAndLng(loginInfo.getLoginIp());
			card_info.setIp(loginInfo.getLoginIp());
			card_info.setLat(ipResult.getLatitude());
			card_info.setLng(ipResult.getLongitude());
			String loginWifiMacKey = Constants.CACHEKEY_USER_LOGIN_WIFI_MAC+userInfo.getRid();
			Object wifiMac = bizCacheUtil.getObject(loginWifiMacKey);
			card_info.setWifi_mac(wifiMac != null ? wifiMac.toString() : StringUtils.EMPTY);
			card_info.setTime(loginInfo.getGmtCreate().getTime());
		} else {
			card_info.setPeople_id(DigestUtil.MD5(bankInfo.getUserId() + StringUtils.EMPTY));
			//用户id + 银行卡id MD5加密
			card_info.setCard_id(DigestUtil.MD5(bankInfo.getUserId() + bankInfo.getUserBankId() + StringUtils.EMPTY));
			card_info.setChannel(BoluomePayType.DEBITCARD.getCode());
			card_info.setDeviceid(bankInfo.getUuid());
			card_info.setIp(bankInfo.getIp());
			card_info.setLat(bankInfo.getLat());
			card_info.setLng(bankInfo.getLng());
			card_info.setWifi_mac(bankInfo.getWifiMac());
			card_info.setTime(bankInfo.getGmtCreate().getTime());
		}
		card_info.setSource("app");
		card_info.setStatus("1");
		return card_info;
	}
	
	private BoluomeGetDidiRiskInfoLoginInfoBo getLoginInfo(AfUserDo userInfo) {
		BoluomeGetDidiRiskInfoLoginInfoBo login_info = new BoluomeGetDidiRiskInfoLoginInfoBo();
		AfUserLoginLogDo userLoginInfo = afUserLoginLogService.getUserLastLoginInfo(userInfo.getUserName());
		String ip = userLoginInfo.getLoginIp();
		//转换ip
		IPTransferBo ipResult = iPTransferUtil.parseIpToLatAndLng(ip);
		login_info.setDeviceid(userLoginInfo.getUuid());
		login_info.setIp(userLoginInfo.getLoginIp());
		login_info.setLat(ipResult.getLatitude());
		login_info.setLat(ipResult.getLongitude());
		login_info.setSource("app");
		login_info.setTime(userLoginInfo.getGmtCreate().getTime());
		String loginWifiMacKey = Constants.CACHEKEY_USER_LOGIN_WIFI_MAC+userInfo.getRid();
		Object wifiMac = bizCacheUtil.getObject(loginWifiMacKey);
		login_info.setWifi_mac(wifiMac != null ? wifiMac.toString() : StringUtils.EMPTY);
		return login_info;
	}

}
