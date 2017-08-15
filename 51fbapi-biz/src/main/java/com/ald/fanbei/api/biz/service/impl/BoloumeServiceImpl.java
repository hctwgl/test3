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
	public BoluomeGetDidiRiskInfoRespBo getRiskInfo(String orderId, String type) {
		BoluomeGetDidiRiskInfoRespBo resp = new BoluomeGetDidiRiskInfoRespBo();
		AfOrderDo orderInfo = afOrderService.getThirdOrderInfoByOrderTypeAndOrderNo(OrderType.BOLUOME.getCode(), orderId);
		if (orderInfo == null) {
			throw new FanbeiException(FanbeiExceptionCode.ORDER_NOT_EXIST);
		}
		//获取payInfo
		BoluomeGetDidiRiskInfoPayInfoBo pay_info = getPayInfo(orderInfo);
		//获取绑卡信息
		BoluomeGetDidiRiskInfoCardInfoBo card_info = getCardInfo(orderInfo);
		//获取登陆相关信息
		BoluomeGetDidiRiskInfoLoginInfoBo login_info = getLoginInfo(orderInfo);
		
		if ("PASSAGE_INFO".equals(type)) {
			resp.setCard_info(card_info);
			resp.setLogin_info(login_info);
		} else if("PAY_INFO".equals(type)){
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
	
	private BoluomeGetDidiRiskInfoCardInfoBo getCardInfo(AfOrderDo orderInfo) {
		BoluomeGetDidiRiskInfoCardInfoBo card_info = new BoluomeGetDidiRiskInfoCardInfoBo();
		//当为微信支付，或者额度支付，获取默认滴滴风控银行卡信息
		AfUserBankDidiRiskDo conditionInfo = new AfUserBankDidiRiskDo();
		conditionInfo.setUserId(orderInfo.getUserId());
		if (orderInfo.getBankId() <= 0) {
			conditionInfo.setUserBankId(0l);
		} else {
			conditionInfo.setUserBankId(orderInfo.getBankId());
		}
		AfUserBankDidiRiskDo bankInfo =afUserBankDidiRiskService.getByCommonCondition(conditionInfo);
		//用户id MD5加密
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
		card_info.setSource("app");
		card_info.setStatus("1");
		return card_info;
	}
	
	private BoluomeGetDidiRiskInfoLoginInfoBo getLoginInfo(AfOrderDo orderInfo) {
		BoluomeGetDidiRiskInfoLoginInfoBo login_info = new BoluomeGetDidiRiskInfoLoginInfoBo();
		Long userId = orderInfo.getUserId();
		AfUserDo userInfo = afUserService.getUserById(userId);
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
		String loginWifiMacKey = Constants.CACHEKEY_USER_LOGIN_WIFI_MAC+orderInfo.getUserId();
		Object wifiMac = bizCacheUtil.getObject(loginWifiMacKey);
		login_info.setWifi_mac(wifiMac != null ? wifiMac.toString() : StringUtils.EMPTY);
		return login_info;
	}

}
