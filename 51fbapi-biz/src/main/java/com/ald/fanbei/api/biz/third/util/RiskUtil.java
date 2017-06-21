package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.RiskAddressListDetailBo;
import com.ald.fanbei.api.biz.bo.RiskAddressListReqBo;
import com.ald.fanbei.api.biz.bo.RiskAddressListRespBo;
import com.ald.fanbei.api.biz.bo.RiskModifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskOperatorRespBo;
import com.ald.fanbei.api.biz.bo.RiskRaiseQuotaReqBo;
import com.ald.fanbei.api.biz.bo.RiskRegisterReqBo;
import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.RiskSynBorrowInfoReqBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.WhiteUserRequestBo;
import com.ald.fanbei.api.biz.bo.risk.RiskAuthFactory;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.RSAUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfAuthContactsDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAccountQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：风控审批工具类
 * @author 何鑫 2017年3月22日 11:20:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("riskUtil")
public class RiskUtil extends AbstractThird {
	private static String url = null;
	private static String notifyHost = null;
	private static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";
	// private static String PUBLIC_KEY =
	// "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDV0lcrx+AueWCs701AjdH766SoyAuZQcw3ukJRwnjGWjJp4SaRvzvmmqNKu78J1dPJ5NeMKGRBFR2zRwh3Hz/oBCEOdpEL1oNC+w1EuCVO96V0UOjMi0RBhyk+o4KP4tUlIQWmljhiPcOTdmgcY9QxKxMyO0wNYfzNwWp+ieRmawIDAQAB";
	private static String TRADE_RESP_SUCC = "0000";
	private static String CHANNEL = "51fb";
	private static String SYS_KEY = "01";

	@Resource
	UpsUtil upsUtil;
	@Resource
	RiskUtil riskUtil;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	JpushService jpushService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfUserService afUserService;
	@Resource
	CommitRecordUtil commitRecordUtil;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfAuthContactsService afAuthContactsService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	SmsUtil smsUtil;
	@Resource
	AfBorrowCacheAmountPerdayService afBorrowCacheAmountPerdayService;
	@Resource
	AfOrderDao orderDao;
	@Resource
	TransactionTemplate transactionTemplate;
	@Resource
	AfAgentOrderService afAgentOrderService;
	@Resource
	BoluomeUtil boluomeUtil;
	

	private static String getUrl() {
		if (url == null) {
			url = ConfigProperties.get(Constants.CONFKEY_RISK_URL);
			return url;
		}
		return url;
	}

	private static String getNotifyHost() {
		if (notifyHost == null) {
			notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
			return notifyHost;
		}
		return notifyHost;
	}

	/**
	 * 用户信息同步
	 * 
	 * @param consumerNo
	 *            --用户在业务系统中的唯一标识
	 * @param realName
	 *            --真实姓名
	 * @param phone
	 *            --手机号
	 * @param idNo
	 *            --身份证号
	 * @param email
	 *            --邮箱
	 * @param alipayNo
	 *            --支付宝账号
	 * @param address
	 *            --地址
	 * @return
	 */
	public RiskRespBo register(String consumerNo, String realName, String phone, String idNo, String email,
			String alipayNo, String address) {
		RiskRegisterReqBo reqBo = new RiskRegisterReqBo();
		reqBo.setConsumerNo(consumerNo);
		reqBo.setRealName(realName);
		reqBo.setPhone(phone);
		reqBo.setIdNo(idNo);
		reqBo.setEmail(email);
		reqBo.setAlipayNo(alipayNo);
		reqBo.setAddress(address);
		reqBo.setChannel(CHANNEL);
		reqBo.setReqExt("");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		reqBo.setRealName(RSAUtil.encrypt(PRIVATE_KEY, realName));
		reqBo.setPhone(RSAUtil.encrypt(PRIVATE_KEY, phone));
		reqBo.setIdNo(RSAUtil.encrypt(PRIVATE_KEY, idNo));
		reqBo.setEmail(RSAUtil.encrypt(PRIVATE_KEY, email));
		String reqResult = HttpUtil.post(getUrl() + "/modules/api/user/register.htm", reqBo);
		logThird(reqResult, "register", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
	}

	
	/**
	 * 老用户批量同步
	 * 
	 * @return
	 */
	public void batchRegister(int pageSize, String userName) {
		int count = afUserAccountService.getUserAccountCountWithHasRealName();
		int pageCount = (int) Math.ceil(count / pageSize) + 1;
		logger.info("batchRegister begin,pageCount=" + pageCount);
		for (int j = 1; j <= pageCount; j++) {
			AfUserAccountQuery query = new AfUserAccountQuery();
			query.setPageNo(j);
			query.setPageSize(pageSize);
			List<AfUserAccountDto> list = afUserAccountService.getUserAndAccountListWithHasRealName(query);
			for (int i = 0; i < list.size(); i++) {
				AfUserAccountDto accountDto = list.get(i);
				RiskRegisterReqBo reqBo = new RiskRegisterReqBo();
				reqBo.setConsumerNo(accountDto.getUserId() + "");
				reqBo.setRealName(accountDto.getRealName());
				reqBo.setPhone(accountDto.getMobile());
				reqBo.setIdNo(accountDto.getIdNumber());
				reqBo.setEmail(accountDto.getEmail());
				reqBo.setAlipayNo(accountDto.getAlipayAccount());
				reqBo.setAddress(accountDto.getAddress());
				reqBo.setChannel(CHANNEL);
				reqBo.setReqExt("");
				reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
				reqBo.setRealName(RSAUtil.encrypt(PRIVATE_KEY, accountDto.getRealName()));
				reqBo.setPhone(RSAUtil.encrypt(PRIVATE_KEY, accountDto.getMobile()));
				reqBo.setIdNo(RSAUtil.encrypt(PRIVATE_KEY, accountDto.getIdNumber()));
				reqBo.setEmail(RSAUtil.encrypt(PRIVATE_KEY, accountDto.getEmail()));
				String reqResult = HttpUtil.post(getUrl() + "/modules/api/user/register.htm", reqBo);
				logThird(reqResult, "register", reqBo);
			}
		}
	}

	/**
	 * 用户信息修改
	 * 
	 * @param consumerNo
	 *            --用户在业务系统中的唯一标识
	 * @param realName
	 *            --真实姓名
	 * @param phone
	 *            --手机号
	 * @param idNo
	 *            --身份证号
	 * @param email
	 *            --邮箱
	 * @param alipayNo
	 *            --支付宝账号
	 * @param address
	 *            --地址
	 * @return
	 */
	public RiskRespBo modify(String consumerNo, String realName, String phone, String idNo, String email,
			String alipayNo, String address, String openId) {
		RiskModifyReqBo reqBo = new RiskModifyReqBo();
		reqBo.setConsumerNo(consumerNo);
		reqBo.setRealName(realName);
		reqBo.setPhone(phone);
		reqBo.setIdNo(idNo);
		reqBo.setEmail(email);
		reqBo.setAlipayNo(alipayNo);
		reqBo.setAddress(address);
		reqBo.setOpenId(openId);
		reqBo.setChannel(CHANNEL);
		reqBo.setReqExt("");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		reqBo.setRealName(RSAUtil.encrypt(PRIVATE_KEY, realName));
		reqBo.setPhone(RSAUtil.encrypt(PRIVATE_KEY, phone));
		reqBo.setIdNo(RSAUtil.encrypt(PRIVATE_KEY, idNo));
		reqBo.setEmail(RSAUtil.encrypt(PRIVATE_KEY, email));
		String reqResult = HttpUtil.post(getUrl() + "/modules/api/user/modify.htm", reqBo);
		logThird(reqResult, "modify", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_MODIFY_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_MODIFY_ERROR);
		}
	}

	/**
	 * 风控审批
	 * 
	 * @param orderNo
	 * @param consumerNo
	 * @param scene
	 * @return
	 */
	@Deprecated
	public RiskVerifyRespBo verify2(String consumerNo, String scene, String cardNo) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(getOrderNo("vefy", cardNo.substring(cardNo.length() - 4, cardNo.length())));
		reqBo.setConsumerNo(consumerNo);
		reqBo.setChannel(CHANNEL);
		reqBo.setScene(scene);
		JSONObject obj = new JSONObject();
		obj.put("cardNo", cardNo);
		reqBo.setDatas(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setReqExt("");
		reqBo.setNotifyUrl(getNotifyHost() + "/third/risk/verify");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.post(getUrl() + "/modules/api/risk/verify.htm", reqBo);
		logThird(reqResult, "verify", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			riskResp.setResult(dataObj.getString("result"));
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
	}

	/**
	 * 用户认证时调用强风控接口，进行信息同步
	 * 
	 * @return
	 */
	public RiskRespBo registerStrongRisk(String consumerNo, String event, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String riskOrderNo) {
		Object directoryCache = bizCacheUtil.getObject(Constants.CACHEKEY_USER_CONTACTS + consumerNo);
		String directory = "";
		if (directoryCache != null) {
			directory = directoryCache.toString();
		}
		AfResourceDo oldUserInfo = afResourceService.getSingleResourceBytype(Constants.RES_OLD_USER_ID);
		int userId = Integer.parseInt(oldUserInfo.getValue());
		int consumerId = Integer.parseInt(consumerNo);
		if ("ALL".equals(event) && !StringUtil.equals(afUserAuthDo.getRiskStatus(), RiskStatus.SECTOR.getCode()) && consumerId <= userId) {
			event = "REAUTH";
		}
		
		logger.info("registerStrongRisk directory= {}", directory);
		RiskRegisterStrongReqBo reqBo = RiskAuthFactory.createRiskDo(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory, getNotifyHost());
		logger.info("registerStrongRisk reqBo= {}", reqBo);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String content = JSONObject.toJSONString(reqBo);
		commitRecordUtil.addRecord("registerStrongRisk", consumerNo, content, url);

		String reqResult = HttpUtil.post(getUrl() + "/modules/api/user/registerAndRisk.htm", reqBo);
		
		logThird(reqResult, "registerAndRisk", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
	}
	
	/**
	 * 弱风控审批
	 * 
	 * @param orderNo
	 * @param consumerNo
	 * @param scene
	 * @return
	 */
	public RiskVerifyRespBo verifyNew(String consumerNo, String borrowNo, String scene, String cardNo, String appName, String ipAddress, String blackBox, String orderNo, String phone, BigDecimal amount, BigDecimal poundage, String time) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(orderNo);
		reqBo.setConsumerNo(consumerNo);
		reqBo.setBorrowNo(borrowNo);
		reqBo.setChannel(CHANNEL);
		reqBo.setScene(scene);

		JSONObject obj = new JSONObject();
		obj.put("cardNo", cardNo);
		obj.put("appName", appName);
		obj.put("ipAddress", ipAddress);
		obj.put("blackBox", blackBox);
		reqBo.setDatas(Base64.encodeString(JSON.toJSONString(obj)));
		
		JSONObject eventObj = new JSONObject();
		eventObj.put("event", Constants.EVENT_FINANCE_LIMIT_WEAK);
		eventObj.put("phone", phone);
		eventObj.put("realAmount", amount);//实际交易金额
		eventObj.put("poundage ", poundage); //手续费
		eventObj.put("time", time);
		reqBo.setEventInfo(JSON.toJSONString(eventObj));
		
		reqBo.setReqExt("");

		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/weakRiskVerify.htm";
		
		String content = JSONObject.toJSONString(reqBo);
		commitRecordUtil.addRecord("weakverify", borrowNo, content, url);

		String reqResult = HttpUtil.post(url, reqBo);

		logThird(reqResult, "weakRiskVerify", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}

		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			String result = dataObj.getString("result");
			riskResp.setSuccess(true);
			riskResp.setResult(result);
			riskResp.setConsumerNo(consumerNo);
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
	}

	/**
	 * 风控提额
	 * @param consumerNo
	 * @param scene
	 * @param orderNo
	 * @param amount
	 * @param income
	 * @param overdueDay
	 * @param borrowCount
	 * @return
	 */
	public RiskVerifyRespBo raiseQuota(String consumerNo,String borrowNo, String scene, String orderNo, BigDecimal amount, BigDecimal income, Long overdueDay, int overdueCount) {
		RiskRaiseQuotaReqBo reqBo = new RiskRaiseQuotaReqBo();
		reqBo.setOrderNo(orderNo);
		reqBo.setEventType(Constants.EVENT_FINANCE_COUNT);
		reqBo.setConsumerNo(consumerNo);
		reqBo.setScene(scene);

		JSONObject obj = new JSONObject();
		obj.put("borrowNo", borrowNo);
		obj.put("amount", amount);
		obj.put("income", income);
		obj.put("overdueDays", overdueDay);
		obj.put("overdueCount", overdueCount);

		reqBo.setDetails(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setReqExt("");

		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/user/action/raiseQuota.htm";
		
		String content = JSONObject.toJSONString(reqBo);
		
		String reqResult = HttpUtil.post(url, reqBo);
		
		commitRecordUtil.addRecord("raiseQuota", consumerNo, content, url);
		logThird(reqResult, "raiseQuota", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			BigDecimal au_amount = new BigDecimal(dataObj.getString("amount"));
			Long consumerNum = Long.parseLong(obj.getString("consumerNo"));
			AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNum);
  			if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0) {
  				AfUserAccountDo accountDo = new AfUserAccountDo();
  				accountDo.setUserId(consumerNum);
  				accountDo.setAuAmount(au_amount);
  				afUserAccountService.updateUserAccount(accountDo);
  			}
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
	}
	
	public RiskVerifyRespBo transferBorrowInfo(String consumerNo, String scene, String orderNo, JSONArray details) {
		RiskSynBorrowInfoReqBo reqBo = new RiskSynBorrowInfoReqBo();
		reqBo.setOrderNo(orderNo);
//		reqBo.setEventType(Constants.EVENT_FINANCE_COUNT);
		reqBo.setConsumerNo(consumerNo);
		reqBo.setScene(scene);

//		JSONObject obj = new JSONObject();
//		obj.put("borrowNo", borrowNo);
//		obj.put("amount", amount);
//		obj.put("orderTime", orderTime);
//		obj.put("income", income);
//		obj.put("overdueAmount", overdueAmount);
//		obj.put("overdueDay", overdueDay);
//		obj.put("overdueCount", overdueCount);
//		reqBo.setDetails(Base64.encodeString(JSON.toJSONString(obj)));
		
		reqBo.setDetails(Base64.encodeString(JSON.toJSONString(details)));
		reqBo.setReqExt("");

		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/repayment.htm";
		
		String content = JSONObject.toJSONString(reqBo);
		
		String reqResult = HttpUtil.post(url, reqBo);
		
		commitRecordUtil.addRecord("transferBorrow", consumerNo, content, url);
		logThird(reqResult, "transferBorrow", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			riskResp.setResult(dataObj.getString("result"));
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
	}
	
	/**
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @param orderNo
	 *            --
	 * @return 
	 */
	public long payOrder(final AfBorrowDo borrow, final String orderNo, final String result) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {

				try {
					// 增加事务
//					RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
//					reqBo.setCode(code);
//					reqBo.setData(data);
//					reqBo.setMsg(msg);
//					reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

					// 添加一个根据风控号查找记录的方法
					AfOrderDo orderInfo = orderDao.getOrderInfoByRiskOrderNo(orderNo);
					// 如果风控审核结果是不成功则关闭订单，修改订单状态是支付中

					logger.info("risk_result =" + result);
					AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
					if (!result.equals("10")) {
						orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
						orderInfo.setStatus(OrderStatus.CLOSED.getCode());
						logger.info("updateOrder orderInfo = {}", orderInfo);
						int re = orderDao.updateOrder(orderInfo);
						// 审批不通过时，让额度还原到以前
						
						// TODO:额度增加，而非减少
						BigDecimal usedAmount = orderInfo.getActualAmount().multiply(BigDecimal.valueOf(-1));
						afBorrowService.dealAgentPayClose(userAccountInfo, usedAmount, orderInfo.getRid());
						if(StringUtils.equals(orderInfo.getOrderType(), OrderType.AGENTBUY.getCode())) {
							AfAgentOrderDo afAgentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderInfo.getRid());
							afAgentOrderDo.setClosedReason("风控审批失败");
							afAgentOrderDo.setGmtClosed(new Date());
							afAgentOrderService.updateAgentOrder(afAgentOrderDo);
							
							//添加关闭订单释放优惠券
							if(afAgentOrderDo.getCouponId()>0){
					            AfUserCouponDo couponDo =	afUserCouponService.getUserCouponById(afAgentOrderDo.getCouponId());
					
					            if(couponDo!=null&&couponDo.getGmtEnd().after(new Date())){
					            		couponDo.setStatus(CouponStatus.NOUSE.getCode());
					            		afUserCouponService.updateUserCouponSatusNouseById(afAgentOrderDo.getCouponId());
					            }
					            else if(couponDo !=null &&couponDo.getGmtEnd().before(new Date())){
					        		couponDo.setStatus(CouponStatus.EXPIRE.getCode());
					        		afUserCouponService.updateUserCouponSatusExpireById(afAgentOrderDo.getCouponId());
					        	}
							}
						}
						jpushService.dealBorrowApplyFail(userAccountInfo.getUserName(), new Date());
						return new Long(String.valueOf(re));
					}

					// 在风控审批通过后额度不变生成账单
					afBorrowService.dealAgentPayBorrowAndBill(borrow, userAccountInfo.getUserId(),userAccountInfo.getUserName(), orderInfo.getActualAmount());
					// 审批通过时
					orderInfo.setPayStatus(PayStatus.PAYED.getCode());
					orderInfo.setStatus(OrderStatus.PAID.getCode());
					// 关闭订单时间和原因的更新
					
					logger.info("updateOrder orderInfo = {}", orderInfo);
					orderDao.updateOrder(orderInfo);
					
//					if (StringUtils.equals(orderInfo.getOrderType(), OrderType.BOLUOME.getCode())) {
//						boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getSaleAmount());
//					}
				} catch (Exception e) {
					logger.info("asyPayOrder error:" + e);
					status.setRollbackOnly();
					throw e;
				}
				return 1l;
			}

		});
	}

	/**
	 * @方法描述：实名认证时风控异步审核
	 * 
	 * @author fumeiai 2017年6月7日  14:47:50
	 * 
	 * @return
	 */
	public int asyRegisterStrongRisk(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "asyRegisterStrongRisk", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			logger.info("asyRegisterStrongRisk reqBo.getSignInfo()" + reqBo.getSignInfo());
			JSONObject obj = JSON.parseObject(data);
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);
			Long consumerNo = Long.parseLong(obj.getString("consumerNo"));
			String result = obj.getString("result");
			 
			if (StringUtils.equals("10", result)) {
				AfUserAuthDo authDo = new AfUserAuthDo();
      			authDo.setUserId(consumerNo);
      			authDo.setRiskStatus(RiskStatus.YES.getCode());
      			afUserAuthService.updateUserAuth(authDo);
      			
      			/*如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，以老的额度为准，不做变更
                                                否则把用户的额度设置成分控返回的额度*/
      			AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNo);
      			if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0) {
      				AfUserAccountDo accountDo = new AfUserAccountDo();
      				accountDo.setUserId(consumerNo);
      				accountDo.setAuAmount(au_amount);
      				afUserAccountService.updateUserAccount(accountDo);
      			}
			} else if (StringUtils.equals("30", result)) {
				AfUserAuthDo authDo = new AfUserAuthDo();
      			authDo.setUserId(consumerNo);
      			authDo.setRiskStatus(RiskStatus.NO.getCode());
      			afUserAuthService.updateUserAuth(authDo);
      			
      			/*如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，以老的额度为准，不做变更
                                                否则把用户的额度设置成分控返回的额度*/
      			AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNo);
      			if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0) {
      				AfUserAccountDo accountDo = new AfUserAccountDo();
      				accountDo.setUserId(consumerNo);
      				accountDo.setAuAmount(BigDecimal.ZERO);
      				afUserAccountService.updateUserAccount(accountDo);
      			}
			}
		}
		return 0;
	}


	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			prestr = prestr + value;
			/*
			 * if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符 prestr = prestr + key
			 * + "=" + value; } else { prestr = prestr + key + "=" + value +
			 * "&"; }
			 */
		}

		return prestr;
	}

	/**
	 * 获取订单号
	 * 
	 * @param method
	 *            接口标识（固定4位）
	 * @param identity
	 *            身份标识（固定4位）
	 */
	public String getOrderNo(String method, String identity) {
		if (StringUtil.isBlank(method) || method.length() != 4 || StringUtil.isBlank(identity)
				|| identity.length() != 4) {
			throw new FanbeiException(FanbeiExceptionCode.UPS_ORDERNO_BUILD_ERROR);
		}
		return StringUtil.appendStrs(SYS_KEY, method, identity, System.currentTimeMillis());
	}

	/**
	 * 上树运营商数据查询
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @param userName
	 *            --用户名
	 * @return
	 */
	public RiskOperatorRespBo operator(String consumerNo, String userName) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(getOrderNo("oper", userName.substring(userName.length() - 4, userName.length())));
		reqBo.setConsumerNo(consumerNo);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.post(getUrl() + "/modules/api/risk/operator.htm", reqBo);
		logThird(reqResult, "operator", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_OPERATOR_ERROR);
		}
		RiskOperatorRespBo riskResp = JSONObject.parseObject(reqResult, RiskOperatorRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			riskResp.setUrl(dataObj.getString("url"));
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_OPERATOR_ERROR);
		}
	}

	/**
	 * 上树运营商数据查询异步通知
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @param userName
	 *            --用户名
	 * @return
	 */
	public int operatorNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "operatorNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功

			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {
				// TODO 不做任何更新
				return 0;
			}
			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtMobile(new Date());
			if (StringUtil.equals("10", result)) {
				auth.setMobileStatus(YesNoStatus.YES.getCode());
			} else {
				auth.setMobileStatus(YesNoStatus.NO.getCode());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	/**
	 * @方法描述：¬ 用户联系人同步
	 * 
	 * 
	 * @author huyang 2017年4月5日上午11:41:55
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * 
	 * @return
	 * @throws Exception
	 *             ¬ 用户联系人同步
	 * 
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	public RiskAddressListRespBo addressContactsPrimaries(String consumerNo, AfAuthContactsDo detail) {

		RiskAddressListReqBo reqBo = new RiskAddressListReqBo();
		reqBo.setConsumerNo(consumerNo);
		if (detail == null) {
			return null;
		}
		List<RiskAddressListDetailBo> detailBos = new ArrayList<RiskAddressListDetailBo>();
		RiskAddressListDetailBo bo = new RiskAddressListDetailBo();
		bo.setNickname(StringUtil.filterEmoji(detail.getFriendNick()));
		bo.setPhone(detail.getFriendPhone().replaceAll(" ", ""));
		bo.setRelation(detail.getRelation());
		detailBos.add(bo);

		String uuid = UUID.randomUUID().toString();
		reqBo.setOrderNo(getOrderNo("addr", uuid.substring(uuid.length() - 4, uuid.length())));
		reqBo.setCount(detailBos.size() + "");
		reqBo.setDetails(JSON.toJSONString(detailBos));
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.post(getUrl() + "/modules/api/user/action/linkman/remove.htm", reqBo);
		// 测试
		// String reqResult = HttpUtil.post("http://60.190.230.35:52637" +
		// "/modules/api/user/action/linkman/remove.htm", reqBo);

		logThird(reqResult, "addressContactsPrimaries", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_ADDRESSLIST_PRIMARIES_ERROR);
		}
		RiskAddressListRespBo riskResp = JSONObject.parseObject(reqResult, RiskAddressListRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_ADDRESSLIST_PRIMARIES_ERROR);
		}
	}

	/**
	 * @方法描述：同步用户通讯录集合
	 * 
	 * @author fumeiai 2017年4月21日上午11:08:55
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @param data
	 *            --通讯录信息，格式为
	 *            张三:15888881111&15811234444,李四:15888881111&15811234444
	 * @return
	 * @throws Exception
	 *             传入更新的通讯录为空
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	public RiskAddressListRespBo addressListPrimaries(String consumerNo, String data) {

		RiskAddressListReqBo reqBo = new RiskAddressListReqBo();
		String uuid = UUID.randomUUID().toString();
		reqBo.setOrderNo(getOrderNo("addr", uuid.substring(uuid.length() - 4, uuid.length())));
		reqBo.setConsumerNo(consumerNo);
		reqBo.setData(StringUtil.filterEmoji(data));
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		// http://arc.edushi.erongyun.net "http://60.190.230.35:52637"
		String reqResult = HttpUtil.post(getUrl() + "/modules/api/user/action/directory/remove.htm", reqBo);
		logThird(reqResult, "addressListPrimaries", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_ADDRESSLIST_PRIMARIES_ERROR);
		}
		RiskAddressListRespBo riskResp = JSONObject.parseObject(reqResult, RiskAddressListRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_ADDRESSLIST_PRIMARIES_ERROR);
		}
	}

	/**
	 * @方法描述：用户白名单信息同步
	 * 
	 * @author fumeiai 2017年5月11日上午11:21
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @return
	 * @throws Exception
	 * 
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	public int addwhiteUser(Long consumerNo) {
		AfUserAccountDto afUserAccountDto = afUserAccountService.getUserInfoByUserId(consumerNo);
		WhiteUserRequestBo reqBo = new WhiteUserRequestBo();
		reqBo.setConsumerNo(consumerNo.toString());
		reqBo.setRealName(afUserAccountDto.getRealName());
		reqBo.setPhone(afUserAccountDto.getMobile());
		reqBo.setIdNo(afUserAccountDto.getIdNumber());
		reqBo.setGrantAmount(afUserAccountDto.getAuAmount().toString());
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		reqBo.setRealName(RSAUtil.encrypt(PRIVATE_KEY, afUserAccountDto.getRealName()));
		reqBo.setPhone(RSAUtil.encrypt(PRIVATE_KEY, afUserAccountDto.getMobile()));
		reqBo.setIdNo(RSAUtil.encrypt(PRIVATE_KEY, afUserAccountDto.getIdNumber()));
		// String reqResult =
		// HttpUtil.post("http://192.168.96.139:80/modules/api/user/whiteuser.htm",
		// reqBo);
		String reqResult = HttpUtil.post(getUrl() + "/modules/api/user/whiteuser.htm", reqBo);
		logThird(reqResult, "addwhiteUser", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.ADD_WHITE_USER_PRIMARIES_ERROR);
		}
		JSONObject result = JSONObject.parseObject(reqResult);
		if (result.get("code").equals("0000")) {
			return 1;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_ADDRESSLIST_PRIMARIES_ERROR);
		}
	}
	/*
	 * 风控审批
	 * 
	 * @param orderNo
	 * @param consumerNo
	 * @param scene
	 * @return
	 */
	public RiskVerifyRespBo verify(String consumerNo, String scene, String cardNo, String appName, String ipAddress,
			String blackBox, String borrowId, String notifyUrl, String orderNo) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(orderNo);
		reqBo.setConsumerNo(consumerNo);
		reqBo.setChannel(CHANNEL);
		reqBo.setScene(scene);

		JSONObject obj = new JSONObject();
		obj.put("cardNo", cardNo);
		obj.put("appName", appName);
		obj.put("ipAddress", ipAddress);
		obj.put("blackBox", blackBox);

		reqBo.setDatas(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setReqExt("");

		// 给一个默认值
		if (StringUtil.isBlank(notifyUrl)) {
			notifyUrl = "/third/risk/verify";
		}
		reqBo.setNotifyUrl(getNotifyHost() + notifyUrl);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/examine/verify.htm";
		String content = JSONObject.toJSONString(reqBo);
		commitRecordUtil.addRecord("verify", borrowId, content, url);

		String reqResult = HttpUtil.post(url, reqBo);

		logThird(reqResult, "verify", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			riskResp.setResult(dataObj.getString("result"));
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
	}

	/**
	 * 
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @param userName
	 *            --用户名
	 * @return
	 */

	public long asyPayOrder(final String code, final String data, final String msg, final String signInfo) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {

			@Override
			public Long doInTransaction(TransactionStatus status) {

				try {
					// 增加事务
					RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
					reqBo.setCode(code);
					reqBo.setData(data);
					reqBo.setMsg(msg);
					reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

					logThird(signInfo, "asyPayOrder", reqBo);
					if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验证签名成功
						logger.info("reqBo.getSignInfo()" + reqBo.getSignInfo());
						JSONObject obj = JSON.parseObject(data);
						String orderNo = obj.getString("orderNo");

						// 添加一个根据风控号查找记录的方法
						AfOrderDo orderInfo = orderDao.getOrderInfoByRiskOrderNo(orderNo);
						// 如果风控审核结果是不成功则关闭订单，修改订单状态是支付中
						JSONObject object = JSON.parseObject(data);

						logger.info("risk_result =" + object.get("result").toString());
						AfUserAccountDo userAccountInfo = afUserAccountService
								.getUserAccountByUserId(orderInfo.getUserId());
						if (!object.get("result").toString().equals("10")) {
							orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
							orderInfo.setStatus(OrderStatus.CLOSED.getCode());
							logger.info("updateOrder orderInfo = {}", orderInfo);
							int re = orderDao.updateOrder(orderInfo);
							// 审批不通过时，让额度还原到以前
							
							// TODO:额度增加，而非减少
							BigDecimal usedAmount = orderInfo.getActualAmount().multiply(BigDecimal.valueOf(-1));
							afBorrowService.dealAgentPayClose(userAccountInfo, usedAmount, orderInfo.getRid());
							if(StringUtils.equals(orderInfo.getOrderType(), OrderType.AGENTBUY.getCode())) {
								AfAgentOrderDo afAgentOrderDo = afAgentOrderService
										.getAgentOrderByOrderId(orderInfo.getRid());
								afAgentOrderDo.setClosedReason("风控审批失败");
								afAgentOrderDo.setGmtClosed(new Date());
								afAgentOrderService.updateAgentOrder(afAgentOrderDo);
								
								
								//添加关闭订单释放优惠券
								if(afAgentOrderDo.getCouponId()>0){
						            AfUserCouponDo couponDo =	afUserCouponService.getUserCouponById(afAgentOrderDo.getCouponId());
						
						            if(couponDo!=null&&couponDo.getGmtEnd().after(new Date())){
						            		couponDo.setStatus(CouponStatus.NOUSE.getCode());
						            		afUserCouponService.updateUserCouponSatusNouseById(afAgentOrderDo.getCouponId());
						            }
						            else if(couponDo !=null &&couponDo.getGmtEnd().before(new Date())){
						        		couponDo.setStatus(CouponStatus.EXPIRE.getCode());
						        		afUserCouponService.updateUserCouponSatusExpireById(afAgentOrderDo.getCouponId());
						        	}
								}
							}
							jpushService.dealBorrowApplyFail(userAccountInfo.getUserName(), new Date());
							return new Long(String.valueOf(re));
						}
//						// 在风控审批通过后额度不变生成账单
//						afBorrowService.dealAgentPayConsumeRisk(userAccountInfo, orderInfo.getActualAmount(),
//								orderInfo.getGoodsName(), orderInfo.getNper(), orderInfo.getRid(),
//								orderInfo.getOrderNo(), null);
						
						// 在风控审批通过后额度不变生成账单
						afBorrowService.dealAgentPayBorrowAndBill(userAccountInfo.getUserId(),userAccountInfo.getUserName(), orderInfo.getActualAmount(),
								orderInfo.getGoodsName(), orderInfo.getNper(), orderInfo.getRid(),orderInfo.getOrderNo(),orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson());


						// 审批通过时
						orderInfo.setPayStatus(PayStatus.PAYED.getCode());
						orderInfo.setStatus(OrderStatus.PAID.getCode());
						// 关闭订单时间和原因的更新

						logger.info("updateOrder orderInfo = {}", orderInfo);
						orderDao.updateOrder(orderInfo);
						
						if (StringUtils.equals(orderInfo.getOrderType(), OrderType.BOLUOME.getCode())) {
							boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getSaleAmount());
						}
						// TODO:返回值
						return 1L;
					}
				} catch (Exception e) {
					logger.info("asyPayOrder error:" + e);
					status.setRollbackOnly();
					throw e;
				}

				return 1L;
			}

		});
	}

	/**
	 * 风控异步审核
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @param userName
	 *            --用户名
	 * @return
	 */
	public int asyVerify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "asyVerify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			logger.info("reqBo.getSignInfo()" + reqBo.getSignInfo());
			JSONObject obj = JSON.parseObject(data);
			String orderNo = obj.getString("orderNo");
			Long consumerNo = Long.parseLong(obj.getString("consumerNo"));
			String result = obj.getString("result");//
			AfBorrowCashDo cashDo = new AfBorrowCashDo();
			// cashDo.setRishOrderNo(orderNo);
			Date currDate = new Date();

			AfUserDo afUserDo = afUserService.getUserById(consumerNo);
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByRishOrderNo(orderNo);
			cashDo.setRid(afBorrowCashDo.getRid());

			AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(consumerNo);

			List<String> whiteIdsList = new ArrayList<String>();
			int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
			// 判断是否在白名单里面
			AfResourceDo whiteListInfo = afResourceService
					.getSingleResourceBytype(Constants.APPLY_BRROW_CASH_WHITE_LIST);
			logger.info("whiteListInfo===" + whiteListInfo);
			if (whiteListInfo != null) {
				whiteIdsList = CollectionConverterUtil.convertToListFromArray(whiteListInfo.getValue3().split(","),
						new Converter<String, String>() {
							@Override
							public String convert(String source) {
								return source.trim();
							}
						});
			}

			logger.info("whiteIdsList=" + whiteIdsList + ",userName=" + afUserDo.getUserName() + ",isContain="
					+ whiteIdsList.contains(afUserDo.getUserName()));
			if (whiteIdsList.contains(afUserDo.getUserName()) || StringUtils.equals("10", result)) {

				jpushService.dealBorrowCashApplySuccss(afUserDo.getUserName(), currDate);
				String bankNumber = card.getCardNumber();
				String lastBank = bankNumber.substring(bankNumber.length()-4);
				
				smsUtil.sendBorrowCashCode(afUserDo.getUserName(),lastBank);
				// 审核通过
				cashDo.setGmtArrival(currDate);
				cashDo.setStatus(AfBorrowCashStatus.transeding.getCode());
				AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(consumerNo);
				// 打款
				UpsDelegatePayRespBo upsResult = upsUtil.delegatePay(afBorrowCashDo.getArrivalAmount(),
						userDto.getRealName(), card.getCardNumber(), consumerNo + "", card.getMobile(),
						card.getBankName(), card.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
						UserAccountLogType.BorrowCash.getCode(), afBorrowCashDo.getRid() + "");
				cashDo.setReviewStatus(AfBorrowCashReviewStatus.agree.getCode());
				Integer day = NumberUtil
						.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
				Date arrivalStart = DateUtil.getStartOfDate(currDate);
				Date repaymentDay = DateUtil.addDays(arrivalStart, day);
				cashDo.setGmtPlanRepayment(repaymentDay);

				if (!upsResult.isSuccess()) {
					logger.info("upsResult error:" + FanbeiExceptionCode.BANK_CARD_PAY_ERR);
					cashDo.setStatus(AfBorrowCashStatus.transedfail.getCode());
				}
				cashDo.setCardNumber(card.getCardNumber());
				cashDo.setCardName(card.getBankName());
				afBorrowCashService.updateBorrowCash(cashDo);
				addTodayTotalAmount(currentDay, afBorrowCashDo.getAmount());
			} else if (StringUtils.equals("30", result)) {
				cashDo.setStatus(AfBorrowCashStatus.closed.getCode());
				cashDo.setReviewStatus(AfBorrowCashReviewStatus.refuse.getCode());
				cashDo.setReviewDetails(AfBorrowCashReviewStatus.refuse.getName());
				jpushService.dealBorrowCashApplyFail(afUserDo.getUserName(), currDate);
			} else {
				cashDo.setReviewStatus(AfBorrowCashReviewStatus.waitfbReview.getCode());
			}

			return afBorrowCashService.updateBorrowCash(cashDo);
		}
		return 0;
	}

	/**
	 * 增加当天审核的金额
	 * 
	 * @param day
	 * @param amount
	 */
	private void addTodayTotalAmount(int day, BigDecimal amount) {
		AfBorrowCacheAmountPerdayDo amountCurrentDay = new AfBorrowCacheAmountPerdayDo();
		amountCurrentDay.setDay(day);
		amountCurrentDay.setAmount(amount);
		afBorrowCacheAmountPerdayService.updateBorrowCacheAmount(amountCurrentDay);
	}

	/**
	 * @方法描述：查询用户额度
	 * 
	 * @author maqiaopan 2017年5月25日 14:30:28
	 * 
	 * @param consumerNo
	 *            --用户唯一标识
	 * @return BigDecimal
	 * @throws Exception
	 * 
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	public BigDecimal queryAmount(Long consumerNo) {
		BigDecimal bigDecimal = BigDecimal.ZERO;
		Map<String, String> params = new HashMap<>();
		params.put("consumerNo", consumerNo.toString());
		String signInfo = SignUtil.sign(createLinkString(params), PRIVATE_KEY);
		params.put("signInfo", signInfo);

		String reqResult = HttpUtil.post(getUrl() + "modules/api/risk/verify.htm", params);
		logThird(reqResult, "queryAmount", params);

		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.QUERY_GRANT_AMOUNT_ERROR);
		}
		JSONObject result = JSONObject.parseObject(reqResult);
		String data = result.getString("data");
		JSONObject amount = JSONObject.parseObject(data);
		String grantAmount = amount.getString("amount");
		bigDecimal.add(new BigDecimal(grantAmount));

		return bigDecimal;

	}
	public void payOrderChangeAmount(Long rid) throws InterruptedException{
		
		AfOrderDo orderInfo = orderDao.getOrderById(rid);
		logger.info("payOrderChangeAmount orderInfo = {}", orderInfo);
		Thread.sleep(2000l);
		if (orderInfo!=null &&StringUtils.equals(orderInfo.getOrderType(), OrderType.BOLUOME.getCode())) {
			boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getSaleAmount());
		}
	}
}
