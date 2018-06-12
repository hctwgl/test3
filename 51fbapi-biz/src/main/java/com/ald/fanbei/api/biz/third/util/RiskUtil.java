package com.ald.fanbei.api.biz.third.util;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.*;
import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.rebate.RebateContext;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.bkl.BklUtils;
import com.ald.fanbei.api.biz.util.*;
import com.ald.fanbei.api.common.VersionCheckUitl;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.dto.*;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.dbunit.util.Base64;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ald.fanbei.api.biz.bo.assetpush.AssetPushType;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.FanbeiBorrowBankInfoBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.RepaymentPlan;
import com.ald.fanbei.api.biz.auth.executor.AuthCallbackManager;
import com.ald.fanbei.api.biz.bo.AuthCallbackBo;
import com.ald.fanbei.api.biz.bo.DredgeWhiteCollarLoanReqBo;
import com.ald.fanbei.api.biz.bo.RiskAddressListDetailBo;
import com.ald.fanbei.api.biz.bo.RiskAddressListReqBo;
import com.ald.fanbei.api.biz.bo.RiskAddressListRespBo;
import com.ald.fanbei.api.biz.bo.RiskCreditRequestBo;
import com.ald.fanbei.api.biz.bo.RiskModifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskNotifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskOperatorRespBo;
import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.RiskOverdueOrderBo;
import com.ald.fanbei.api.biz.bo.RiskQueryOverdueOrderReqBo;
import com.ald.fanbei.api.biz.bo.RiskQueryOverdueOrderRespBo;
import com.ald.fanbei.api.biz.bo.RiskQuotaReqBo;
import com.ald.fanbei.api.biz.bo.RiskQuotaRespBo;
import com.ald.fanbei.api.biz.bo.RiskRaiseQuotaReqBo;
import com.ald.fanbei.api.biz.bo.RiskRegisterReqBo;
import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.RiskSynBorrowInfoReqBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaReqBo;
import com.ald.fanbei.api.biz.bo.RiskVirtualProductQuotaRespBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.WhiteUserRequestBo;
import com.ald.fanbei.api.biz.bo.risk.RiskAuthFactory;
import com.ald.fanbei.api.biz.bo.risk.RiskLoginRespBo;
import com.ald.fanbei.api.biz.rebate.RebateContext;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfAuthContactsService;
import com.ald.fanbei.api.biz.service.AfAuthRaiseStatusService;
import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserVirtualAccountService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.AsyLoginService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.VersionCheckUitl;
import com.ald.fanbei.api.common.enums.AfBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.AuthType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.LoanType;
import com.ald.fanbei.api.common.enums.MobileStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.OrderTypeSecSence;
import com.ald.fanbei.api.common.enums.OrderTypeThirdSence;
import com.ald.fanbei.api.common.enums.PayStatus;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.enums.RiskAuthStatus;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.enums.SupplyCertifyStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.common.enums.UserAuthSceneStatus;
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
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfBorrowExtendDao;
import com.ald.fanbei.api.dal.dao.AfInterimAuDao;
import com.ald.fanbei.api.dal.dao.AfInterimDetailDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfAuthContactsDo;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfBorrowExtendDo;
import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.AfInterimDetailDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserVirtualAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfOrderSceneAmountDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAccountQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.Maps;

/**
 * @author 何鑫 2017年3月22日 11:20:23
 * @类描述：风控审批工具类
 * @类描述：风控审批工具类
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
	@Autowired
	RiskRequestProxy requestProxy;
	@Autowired
	RebateContext rebateContext;
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
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
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
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfBorrowDao afBorrowDao;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfUserVirtualAccountService afUserVirtualAccountService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;

	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AsyLoginService asyLoginService;
	@Resource
	AfTradeCodeInfoService afTradeCodeInfoService;
	@Resource
	NumberWordFormat numberWordFormat;
	@Resource
	AfBorrowExtendDao afBorrowExtendDao;
	@Resource
	AfIagentResultDao iagentResultDao;
	@Resource
	AfInterimAuDao afInterimAuDao;
	@Resource
	AfInterimDetailDao afInterimDetailDao;
	@Resource
	AfUserAccountSenceDao afUserAccountSenceDao;

	@Resource
	AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;
	@Resource
	AfBorrowCashDao afBorrowCashDao;
	@Resource
	AfBorrowBillService afBorrowBillService;
	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;
	@Resource
	AfAssetSideInfoService afAssetSideInfoService;

	@Resource
	AuthCallbackManager authCallbackManager;

	@Resource
	AfAuthRaiseStatusService afAuthRaiseStatusService;

	@Resource
	BklUtils bklUtils;

	@Resource
	AfGoodsService afGoodsService;

	@Resource
	AfGoodsCategoryDao afGoodsCategoryDao;

	@Resource
	AfIdNumberDao idNumberDao;

	@Autowired
	KafkaSync kafkaSync;
	@Resource
	AfUserSeedService afUserSeedService;
	
	@Resource
	AfTradeSettleOrderService afTradeSettleOrderService;
	
	@Resource
	AfTradeBusinessInfoService afTradeBusinessInfoService;

	@Resource
	AfBklService afBklService;

	@Resource
	JdbcTemplate loanJdbcTemplate;
	@Resource
	AfInterimAuService afInterimAuService;
	@Resource
	AfBorrowPushService afBorrowPushService;

	public static String getUrl() {
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
	@Deprecated
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
		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/register.htm", reqBo);
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
	@Deprecated
	public void batchRegister(int pageSize, String userName) {
		int count = afUserAccountService.getUserAccountCountWithHasRealName();
		int pageCount = (int) Math.ceil(count / pageSize) + 1;
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
				String reqResult = requestProxy.post(getUrl() + "/modules/api/user/register.htm", reqBo);
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
		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/modify.htm", reqBo);
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
		String reqResult = requestProxy.post(getUrl() + "/modules/api/risk/verify.htm", reqBo);
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
	public RiskRespBo registerStrongRisk(String consumerNo, String event, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo,
			String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum,
			String riskOrderNo, String bqsBlackBox) {
		Object directoryCache = bizCacheUtil.getObject(Constants.CACHEKEY_USER_CONTACTS + consumerNo);
		String directory = "";
		if (directoryCache != null) {
			directory = directoryCache.toString();
		}
		AfResourceDo oldUserInfo = afResourceService.getSingleResourceBytype(Constants.RES_OLD_USER_ID);
		Long userId = Long.parseLong(oldUserInfo.getValue());
		Long consumerId = Long.parseLong(consumerNo);
		// if ("ALL".equals(event) &&
		// !StringUtil.equals(afUserAuthDo.getRiskStatus(),
		// RiskStatus.SECTOR.getCode()) && consumerId <= userId) {
		// event = "REAUTH";
		// }

		RiskRegisterStrongReqBo reqBo = RiskAuthFactory.createRiskDo(consumerNo, event, riskOrderNo, afUserDo,
				afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory,
				getNotifyHost(), bqsBlackBox);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		// String content = JSONObject.toJSONString(reqBo);
		// try {
		// commitRecordUtil.addRecord("registerStrongRisk", consumerNo, content,
		// url);
		// } catch (Exception e) {
		// logger.error("field too long，registerStrongRisk insert commitRecord
		// fail,consumerNo="+consumerNo);
		// }

		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/registerAndRisk.htm", reqBo);

		logThird(reqResult, "registerAndRisk", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			riskResp.setSuccess(false);
			return riskResp;
		}
	}

	/**
	 * 用户认证时调用强风控接口，进行信息同步(新版本)
	 *
	 * @return
	 */
	public RiskRespBo registerStrongRiskV1(String consumerNo, String event, AfUserDo afUserDo,
			AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox,
			String cardNum, String riskOrderNo, String bqsBlackBox, String riskScene, String directory) {
		AfResourceDo oldUserInfo = afResourceService.getSingleResourceBytype(Constants.RES_OLD_USER_ID);
		Long userId = Long.parseLong(oldUserInfo.getValue());
		Long consumerId = Long.parseLong(consumerNo);
		// 关闭REAUTH事件
		// if ("ALL".equals(event) &&
		// !StringUtil.equals(afUserAuthDo.getBasicStatus(),
		// RiskStatus.SECTOR.getCode()) && consumerId <= userId) {
		// event = "REAUTH";
		// }

		RiskRegisterStrongReqBo reqBo = RiskAuthFactory.createRiskDoV1(consumerNo, event, riskOrderNo, afUserDo,
				afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory,
				getNotifyHost(), bqsBlackBox, riskScene);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		// String content = JSONObject.toJSONString(reqBo);
		// try {
		// commitRecordUtil.addRecord("registerStrongRisk", consumerNo, content,
		// url);
		// } catch (Exception e) {
		// logger.error("field too long，registerStrongRisk insert commitRecord
		// fail,consumerNo="+consumerNo);
		// }

		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/registerAndRisk.htm", reqBo);

		logThird(reqResult, "registerAndRisk", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);

			return riskResp;
		} else {
			riskResp.setSuccess(false);
			return riskResp;
		}
	}

	/**
	 * 开通白领贷
	 * 
	 * @param extUserInfo
	 *
	 * @return
	 */
	public RiskRespBo dredgeWhiteCollarLoan(String consumerNo, String event, AfUserDo afUserDo,
			AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox,
			String cardNum, String riskOrderNo, String bqsBlackBox, String riskScene, String directory,
			Map<String, Object> extUserInfo,String selectedType,String address,String censusRegister) {

		DredgeWhiteCollarLoanReqBo reqBo = RiskAuthFactory.createDredgeWhiteCollarLoanBo(consumerNo, event, riskOrderNo,
				afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY,
				directory, getNotifyHost(), bqsBlackBox, riskScene, extUserInfo,selectedType,address,censusRegister);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/registerAndRisk.htm", reqBo);

		logThird(reqResult, "registerAndRisk", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			riskResp.setSuccess(false);
			return riskResp;
		}
	}

	/**
	 * 弱风控审批
	 *
	 * @param consumerNo
	 *            用户ID
	 * @param borrowNo
	 *            借款编号
	 * @param borrowType
	 *            借款类型
	 * @param scene
	 *            场景
	 * @param cardNo
	 *            卡号
	 * @param appName
	 *            APP名称
	 * @param ipAddress
	 *            ip地址
	 * @param blackBox
	 * @param orderNo
	 *            订单编号
	 * @param phone
	 *            手机号
	 * @param amount
	 *            订单金额
	 * @param poundage
	 *            手续费
	 * @param time
	 *            时间
	 * @param productName
	 *            商品名称
	 * @param virtualCode
	 *            商品编号 增加里那个字段
	 * @param SecSence
	 *            二级场景
	 * @param ThirdSence
	 *            三级场景 三级场景
	 * @param orderid
	 *            订单号
	 * @return
	 */
	public RiskVerifyRespBo weakRiskForXd(String consumerNo, String borrowNo, String borrowType, String scene,
			String cardNo, String appName, String ipAddress, String blackBox, String orderNo, String phone,
			BigDecimal amount, BigDecimal poundage, String time, String productName, String virtualCode,
			String SecSence, String ThirdSence, long orderid, String cardName, AfBorrowDo borrow, String payType,
			HashMap<String, HashMap> riskDataMap, String bqsBlackBox, AfOrderDo orderDo) {
		//TODO modify by chengkang start  待测试完成后进行删除
		Integer weakFlag = 0 ;
		Integer softWeakFlag = 0;
		AfResourceDo vipGoodsResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.WEAK_VERIFY_VIP_CONFIG.getCode(), AfResourceSecType.ORDER_WEAK_VERIFY_VIP_CONFIG.getCode());
	    if(vipGoodsResourceDo!=null && StringUtil.isNotBlank(vipGoodsResourceDo.getValue5())){
	    	String[] controlFlag = vipGoodsResourceDo.getValue5().split(",");
	    	//0 默认代码正常往下走  1：直接通过  2：直接拒绝
	    	//弱风控控制
	    	weakFlag = NumberUtil.objToIntDefault(controlFlag[0], 0);
	    	//软弱风控控制
	    	softWeakFlag = NumberUtil.objToIntDefault(controlFlag[1], 0);
	    }
	    logger.info("RiskUtil weakRiskForXd,scene="+scene+",weakFlag="+weakFlag+",softWeakFlag="+softWeakFlag);
	    if("40".equals(scene)){
	    	if(weakFlag == 1){
	    		RiskVerifyRespBo verybo = new RiskVerifyRespBo();
	    		verybo.setSuccess(true);
	    		verybo.setPassWeakRisk(true);
	    		verybo.setResult("10");
	    		return verybo;
	    	}
	    	if(weakFlag == 2){
	    		RiskVerifyRespBo verybo = new RiskVerifyRespBo();
	    		verybo.setSuccess(false);
	    		verybo.setPassWeakRisk(false);
	    		verybo.setResult("20");
	    		return verybo;
	    	}
	    }
	    if("44".equals(scene)){
	    	if(softWeakFlag == 1){
	    		RiskVerifyRespBo verybo = new RiskVerifyRespBo();
	    		verybo.setSuccess(true);
	    		verybo.setPassWeakRisk(true);
	    		verybo.setResult("10");
	    		return verybo;
	    	}
	    	if(softWeakFlag == 2){
	    		RiskVerifyRespBo verybo = new RiskVerifyRespBo();
	    		verybo.setSuccess(false);
	    		verybo.setPassWeakRisk(false);
	    		verybo.setResult("20");
	    		return verybo;
	    	}
	    }
		//modify by chengkang end 
		
		AfUserAuthDo userAuth = afUserAuthService.getUserAuthInfoByUserId(Long.parseLong(consumerNo));
		if (orderDo != null) {
			//如果为订单对软弱风控的调用，则订单号加上特殊标识，避免风控系统订单重复问题 alter by chengkang start
		    if("44".equals(scene)){
		    	riskDataMap.get("summaryOrderData").put("orderNo", orderDo.getOrderNo()+Constants.SOFT_WEAK_VERIFY_ORDER_NO_FLAG);
		    }
		    //alter by chengkang start
		    
			// 获取不同场景的强风控认证
			if (StringUtils.equals(OrderType.TRADE.getCode(), orderDo.getOrderType())) {
				// 商圈认证
				AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.selectAfUserAuthStatusByCondition(
						Long.parseLong(consumerNo), orderDo.getSecType(), YesNoStatus.YES.getCode());
				if (afUserAuthStatusDo == null) {
					userAuth.setRiskStatus(YesNoStatus.NO.getCode());
				} else {
					userAuth.setRiskStatus(afUserAuthStatusDo.getStatus());
				}
			} else {
				// 线上分期认证
				AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.selectAfUserAuthStatusByCondition(
						Long.parseLong(consumerNo), UserAccountSceneType.ONLINE.getCode(), YesNoStatus.YES.getCode());
				if (afUserAuthStatusDo == null) {
					userAuth.setRiskStatus(YesNoStatus.NO.getCode());
				} else {
					userAuth.setRiskStatus(afUserAuthStatusDo.getStatus());
				}
			}
		}
		if (!"Y".equals(userAuth.getRiskStatus())) {
			throw new FanbeiException(FanbeiExceptionCode.AUTH_ALL_AUTH_ERROR);
		}
		
		return this.weakRisk(consumerNo, borrowNo, borrowType, scene, cardNo, appName, ipAddress, blackBox, orderNo, phone, amount, poundage, time, productName, virtualCode, SecSence, ThirdSence, orderid, cardName, borrow, payType, riskDataMap, bqsBlackBox, orderDo);
	}
	
	/**
	 * 弱风控审批
	 *
	 * @param consumerNo
	 *            用户ID
	 * @param borrowNo
	 *            借款编号
	 * @param borrowType
	 *            借款类型
	 * @param scene
	 *            场景
	 * @param cardNo
	 *            卡号
	 * @param appName
	 *            APP名称
	 * @param ipAddress
	 *            ip地址
	 * @param blackBox
	 * @param orderNo
	 *            订单编号
	 * @param phone
	 *            手机号
	 * @param amount
	 *            订单金额
	 * @param poundage
	 *            手续费
	 * @param time
	 *            时间
	 * @param productName
	 *            商品名称
	 * @param virtualCode
	 *            商品编号 增加里那个字段
	 * @param SecSence
	 *            二级场景
	 * @param ThirdSence
	 *            三级场景 三级场景
	 * @param orderid
	 *            订单号
	 * @return
	 */
	public RiskVerifyRespBo weakRisk(String consumerNo, String borrowNo, String borrowType, String scene,
			String cardNo, String appName, String ipAddress, String blackBox, String orderNo, String phone,
			BigDecimal amount, BigDecimal poundage, String time, String productName, String virtualCode,
			String SecSence, String ThirdSence, long orderid, String cardName, AfBorrowDo borrow, String payType,
			HashMap<String, HashMap> riskDataMap, String bqsBlackBox, AfOrderDo orderDo) {
		boolean isblack = afResourceService.getBlackList();
		if (isblack){
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(orderNo);
		reqBo.setConsumerNo(consumerNo);
		reqBo.setBorrowNo(borrowNo);
		reqBo.setBorrowType(borrowType);
		reqBo.setChannel(CHANNEL);
		reqBo.setScene(scene);

		JSONObject obj = new JSONObject();
		obj.put("cardNo", cardNo);
		obj.put("appName", appName);
		obj.put("ipAddress", ipAddress);
		obj.put("blackBox", blackBox);
		obj.put("bqsBlackBox", bqsBlackBox);

		reqBo.setDatas(Base64.encodeString(JSON.toJSONString(obj)));

		JSONObject eventObj = new JSONObject();
		eventObj.put("event", Constants.EVENT_FINANCE_LIMIT_WEAK);
		eventObj.put("phone", phone);
		eventObj.put("realAmount", amount);// 实际交易金额
		eventObj.put("poundage", poundage); // 手续费
		eventObj.put("time", time);
		eventObj.put("virtualCode", virtualCode);
		eventObj.put("productName", productName);
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		String uuid = "";
		if (requestAttributes != null) {
			String id = requestAttributes.getRequest().getHeader(Constants.REQ_SYS_NODE_ID);
			String array[] = id == null ? null : id.split("_");
			uuid = array == null || array.length < 2 ? "" : array[1];
		}
		eventObj.put("uuid", uuid);
		// String id = request.getParameter("id");
		// 增加3个参数，配合风控策略的改变
		String codeForSecond = null;
		String codeForThird = null;
		String codeForSecSceneBasis = null;
		String codeForThirdSceneBasis = null;
		codeForSecond = OrderTypeSecSence.getCodeByNickName(SecSence);
		codeForThird = OrderTypeThirdSence.getCodeByNickName(ThirdSence);
		//二级类型
		codeForSecSceneBasis = SecSence;
		//三级类型(区分逛逛和商圈)
		
		if(SecSence!=null && SecSence.equals("BOLUOME")){
			codeForThirdSceneBasis = ThirdSence;
		}
		if(SecSence!=null &&SecSence.equals("TRADE")){
			codeForThirdSceneBasis  =  afOrderService.getTradeBusinessNameByOrderId(orderid);
		}

		Integer dealAmount = getDealAmount(Long.parseLong(consumerNo), SecSence);
		eventObj.put("dealAmount", dealAmount);
		eventObj.put("SecSence", codeForSecond == null ? "" : codeForSecond);
		eventObj.put("ThirdSence", codeForThird == null ? "" : codeForThird);
		
		eventObj.put("secSceneBasis", codeForSecSceneBasis == null ? "" : codeForSecSceneBasis);
		eventObj.put("thirdSceneBasis", codeForThirdSceneBasis == null ? "" : codeForThirdSceneBasis);
		reqBo.setEventInfo(JSON.toJSONString(eventObj));
		// 12-13 弱风控加入用户借款信息
		HashMap summaryData = riskDataMap.get("summaryData");
		if (summaryData == null) {
			summaryData = new HashMap();
			summaryData.put("hourBetweenVerifyBorrow", "0");
			summaryData.put("rateAfter4Day", "0");
			summaryData.put("frequency", "0");
			summaryData.put("rateBorrow7d", "0");
			summaryData.put("rateOverdue", "0");
			summaryData.put("totalAmount", "0");
			summaryData.put("countOverdue", "0");
			summaryData.put("lastAmount", "0");
			summaryData.put("lastOverdueDay", "0");
			summaryData.put("maxOverdueDay", "0");
		}

		reqBo.setSummaryData(JSON.toJSONString(summaryData));
		// 12-13 弱风控加入订单信息
		HashMap summaryOrderData = new HashMap();
		if (orderid > 0) {
			summaryOrderData = riskDataMap.get("summaryOrderData");

		}
		if (borrow != null && orderDo != null) {
			summaryOrderData.put("calculateMethod", borrow.getCalculateMethod());
			summaryOrderData.put("freeNper", borrow.getFreeNper());
			summaryOrderData.put("nperAmount", borrow.getNperAmount());
			summaryOrderData.put("cardNumber", cardNo);
			summaryOrderData.put("cardName", cardName);
			summaryOrderData.put("payType", payType);
			List<AfOrderSceneAmountDto> list = orderDao.getSceneAmountByOrderId(orderid);
			summaryOrderData.put("sceneAmount", list);
			summaryOrderData.put("bankAmount", orderDo.getBankAmount());
			summaryOrderData.put("borrowAmount", orderDo.getBorrowAmount());
			summaryOrderData.put("actualAmount", orderDo.getActualAmount());
			summaryOrderData.put("saleAmount", orderDo.getSaleAmount());
			summaryOrderData.put("priceAmount", orderDo.getPriceAmount());

			logger.info("summaryOrderData.get(\"orderType\")) value is :" + summaryOrderData.get("orderType"));
			if(OrderType.BOLUOME.getCode().equals(summaryOrderData.get("orderType")))
			{
			    // 构造查询参数
			    Map<String, String> params = new HashMap<String, String>();
			    params.put(BoluomeCore.ORDER_ID, summaryOrderData.get("thirdOrderNo").toString());
			    params.put(BoluomeCore.TIME_STAMP, String.valueOf(System.currentTimeMillis() / 1000));
			    String detailsUrl;
			    BoluomeOrderResponseDto orderResponse=null;
			    try {
				detailsUrl = BoluomeCore.buildOrderDetailsQueryUrl(params);
    				// 查询订单详情
    				String response = HttpUtil.doGet(detailsUrl, 100);
    				orderResponse = JSON.parseObject(response, BoluomeOrderResponseDto.class);
			    } catch (UnsupportedEncodingException e) {
				logger.error("weakRisk boluome order details error:",e);
			    }
				//获取菠萝觅订单详情
			    if(orderResponse!=null)
			    summaryOrderData.put("boluomeDetails", orderResponse.getData());
			    else {
				    summaryOrderData.put("boluomeDetails", "");
			    }
			   }
			//额度占比
			BigDecimal uaAmount = new BigDecimal(0);
			BigDecimal uaAmountUsed = new BigDecimal(0);
			BigDecimal uaAmountTemp = new BigDecimal(0);
			BigDecimal uaAmountTempUsed = new BigDecimal(0);


			AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService.getByUserIdAndScene("ONLINE",orderDo.getUserId());
			if (afUserAccountSenceDo != null){
				uaAmount = afUserAccountSenceDo.getAuAmount();
				uaAmountUsed = afUserAccountSenceDo.getUsedAmount();
			}
			AfInterimAuDo afInterimAuDo= afInterimAuService.getAfInterimAuByUserId(orderDo.getUserId());
			if(afInterimAuDo != null && afInterimAuDo.getGmtFailuretime().getTime()>= new Date().getTime()){
				uaAmountTemp = afInterimAuDo.getInterimAmount();
				uaAmountTempUsed = afInterimAuDo.getInterimUsed();
			}
			BigDecimal totalAmount = uaAmount.subtract(uaAmountUsed).add(uaAmountTemp).subtract(uaAmountTempUsed);
			BigDecimal borrowAmount = amount;
			borrowAmount=borrowAmount == null?BigDecimal.ZERO:borrowAmount;
			if (totalAmount.compareTo(BigDecimal.ZERO)>0){
				summaryOrderData.put("unpayedCanUseRatio",borrowAmount.divide(totalAmount,2,BigDecimal.ROUND_HALF_UP));
				BigDecimal userThisOrderSum = borrowAmount.compareTo(uaAmount.subtract(uaAmountUsed))<=0?BigDecimal.ZERO:borrowAmount.subtract(uaAmount.subtract(uaAmountUsed));
				summaryOrderData.put("userThisOrderSum",userThisOrderSum);
				summaryOrderData.put("userTemporaryLimitRatio",uaAmountTemp.compareTo(BigDecimal.ZERO)>0?(userThisOrderSum.add(uaAmountTempUsed).divide(uaAmountTemp,2,BigDecimal.ROUND_HALF_UP)):BigDecimal.ZERO);
			}
			String thisOrderUnanimous = (String)summaryOrderData.get("thisOrderUnanimous");
			summaryOrderData.put("thisOrderUnanimous",thisOrderUnanimous.equals(orderDo.getConsigneeMobile())?"true":"false");
			summaryOrderData.put("gpsUnanimous",(summaryOrderData.get("gpsUnanimous")==null||"".equals(summaryOrderData.get("gpsUnanimous")))?"true":((String)summaryOrderData.get("gpsUnanimous")).contains(orderDo.getProvince())?"true":"false");

		}

		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("WEAK_RISK","FIRST_ORDER_THRESHOLD");
		String firstBigOrderDate = afOrderService.getUserFirstBigOrderDate(Long.parseLong(consumerNo),Integer.parseInt(afResourceDo.getValue()));
		summaryOrderData.put("firstBigStrong",firstBigOrderDate);
		reqBo.setOrderInfo(JSON.toJSONString(summaryOrderData));
		reqBo.setReqExt("");

		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url =  getUrl() + "/modules/api/risk/weakRiskVerify.htm";

		// String url = "http://192.168.110.16:8080" +
		// "/modules/api/risk/weakRiskVerify.htm";
		String reqResult = requestProxy.post(url, reqBo);
		try{
			logger.info("sync kafka data consumerNo:"+consumerNo);
			kafkaSync.syncEvent(Long.parseLong(consumerNo), KafkaConstants.SYNC_SCENE_WEEK,true);
		}catch (Exception e){

		}

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
			riskResp.setVirtualCode(dataObj.getString("virtualCode"));
			riskResp.setVirtualQuota(dataObj.getBigDecimal("virtualQuota"));
			riskResp.setRejectCode(dataObj.getString("rejectCode"));
			riskResp.setBorrowNo(dataObj.getString("borrowNo"));
			if(StringUtils.equals(RiskVerifyRespBo.RISK_SUCC_CODE, result)) {
				riskResp.setPassWeakRisk(true);
				try{
					if(scene.equals("50")){//借钱才走这个逻辑
						AfUserDo userDo= afUserService.getUserById( Long.parseLong(consumerNo));
						Integer data= loanJdbcTemplate.queryForObject("SELECT COUNT(1) from af_borrow_cash a left join af_user b on a.user_id=b.id where b.user_name='"+userDo.getUserName()+"' and a.`status` in ('TRANSED','TRANSEDING')",Integer.class);
						if(data>0){
							logger.info("loan on koudaixianjin username:"+userDo.getUserName());
							riskResp.setPassWeakRisk(false);
							riskResp.setResult("30");
							riskResp.setRejectCode("104");
						}
					}

				}catch (Exception e){
					logger.info("loan on koudaixianjin error:",e);
				}

			}
			else {
				riskResp.setPassWeakRisk(false);
			}

			return riskResp;
		} else {
			if(riskResp!=null){
				try{
					String risk_error_type="risk_error_type_"+riskResp.getCode();
					afResourceDo= afResourceService.getSingleResourceBytype(risk_error_type);

					if(afResourceDo!=null){
						throw new FanbeiException(afResourceDo.getValue(),true);
					}
				}catch (Exception e){
					throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
				}

			}
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
	}
	

	/**
	 * @return
	 * @author qiaopan 获得当天有效借款订单数
	 */
	private Integer getDealAmount(Long userId, String orderType) {
		Integer result = afOrderService.getDealAmount(userId, orderType);
		if (result == null) {
			result = 0;
		}
		return result;
	}

	public static void main(String[] args) {
		String s = "eyJtYXhPdmVyZHVlRGF5IjowLCJhbW91bnQiOjEwMDAuMDAsImJvcnJvd05vIjoiZGsyMDE4MDMw\nNjIwMjE0OTAwMzE3IiwicmVwYXlDb3VudCI6MSwib3ZlcmR1ZURheXMiOjAsImluY29tZSI6MjMu\nMzMsIm92ZXJkdWVDb3VudCI6MH0=";
		System.out.println(Base64.decodeToString(s));
	}
	
	/**
	 * 风控提额
	 *
	 * @param consumerNo
	 * @param scene
	 * @param orderNo
	 * @param amount
	 * @param income
	 * @param overdueDay
	 * @return
	 */
	public RiskVerifyRespBo raiseQuota(String consumerNo, String borrowNo, String scene, String orderNo,
			BigDecimal amount, BigDecimal income, Long overdueDay, int overdueCount, Long maxOverdueDay,
			int repayCount) {
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

		obj.put("maxOverdueDay", maxOverdueDay);
		obj.put("repayCount", repayCount);

		reqBo.setDetails(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setReqExt("");

		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/user/action/raiseQuota.htm";

		String reqResult = requestProxy.post(url, reqBo);

		logThird(reqResult, "raiseQuota", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			Long userId = Long.parseLong(consumerNo);
			
			BigDecimal au_amount = BigDecimal.ZERO;
			
			if(SceneType.BLD_LOAN.getCode().equals(scene)) {
				au_amount = new BigDecimal(dataObj.getString("whiteCollarAmount"));
				BigDecimal totalamount = new BigDecimal(dataObj.getString("totalAmount"));
				afUserAccountSenceService.raiseQuota(userId, SceneType.BLD_LOAN, au_amount, totalamount);
			}else {
				au_amount = new BigDecimal(dataObj.getString("amount"));
				BigDecimal totalamount = new BigDecimal(dataObj.getString("totalAmount"));
				AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(Long.parseLong(consumerNo));
				// 强风控未通过，则不经额度处理
				if (afUserAuthDo==null || !RiskStatus.YES.getCode().equals(afUserAuthDo.getRiskStatus())) {
					au_amount = BigDecimal.ZERO;
				}

				AfUserAuthStatusDo afUserAuthStatusOnline = afUserAuthStatusService
						.getAfUserAuthStatusByUserIdAndScene(Long.parseLong(consumerNo), SceneType.ONLINE.getName());
				String limitAmount = dataObj.getString("onlineAmount");
				if (StringUtil.equals(limitAmount, "") || limitAmount == null)
					limitAmount = "0";
				BigDecimal onlineAmount = new BigDecimal(limitAmount);
				if (afUserAuthStatusOnline == null || !UserAuthSceneStatus.YES.getCode().equals(afUserAuthStatusOnline.getStatus())) {
					onlineAmount = BigDecimal.ZERO;
				}

				AfUserAuthStatusDo afUserAuthStatusTrain = afUserAuthStatusService
						.getAfUserAuthStatusByUserIdAndScene(Long.parseLong(consumerNo), SceneType.TRAIN.getName());
				limitAmount = dataObj.getString("offlineAmount");
				if (StringUtil.equals(limitAmount, "") || limitAmount == null)
					limitAmount = "0";
				BigDecimal offlineAmount = new BigDecimal(limitAmount);
				if (afUserAuthStatusTrain == null || !UserAuthSceneStatus.YES.getCode().equals(afUserAuthStatusTrain.getStatus())) {
					offlineAmount = BigDecimal.ZERO;
				}

				Long consumerNum = Long.parseLong(consumerNo);
				AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNum);
				updateUserScenceAmount(userAccountDo, consumerNum, au_amount, onlineAmount, offlineAmount,totalamount);
				
			}
			
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
	}

	public RiskVerifyRespBo transferBorrowInfo(String consumerNo, String scene, String orderNo, JSONArray details) {
		RiskSynBorrowInfoReqBo reqBo = new RiskSynBorrowInfoReqBo();
		reqBo.setOrderNo(orderNo);
		reqBo.setConsumerNo(consumerNo);
		reqBo.setScene(scene);

		reqBo.setDetails(Base64.encodeString(JSON.toJSONString(details)));
		reqBo.setReqExt("");

		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/repayment.htm";

		String reqResult = requestProxy.post(url, reqBo);

		// String content = JSONObject.toJSONString(reqBo);
		// try {
		// commitRecordUtil.addRecord("transferBorrow", consumerNo, content,
		// url);
		// } catch (Exception e) {
		// logger.error("field too long，transferBorrow insert commitRecord
		// fail,consumerNo="+consumerNo);
		// }
		logThird(reqResult, "transferBorrow", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_QUOTA_ERROR);
		}
	}

	/**
	 * @param borrow
	 *            借款信息
	 * @param orderNo
	 *            订单编号
	 * @param verifybo
	 *            风控返回结果
	 * @return
	 */
	public Map<String, Object> payOrder(final Map<String, Object> resultMap, final AfBorrowDo borrow,
			final String orderNo, RiskVerifyRespBo verifybo, final Map<String, Object> virtualMap, AfOrderDo orderInfo)
			throws FanbeiException {
		String result = verifybo.getResult();

		logger.info("payOrder:borrow=" + borrow + ",orderNo=" + orderNo + ",result=" + result);
		// 添加一个根据风控号查找记录的方法
		// AfOrderDo orderInfo = orderDao.getOrderInfoByRiskOrderNo(orderNo);
		// 如果风控审核结果是不成功则关闭订单，修改订单状态是支付中
		logger.info("risk_result =" + result);
		AfUserAccountDo userAccountInfo = afUserAccountService.getUserAccountByUserId(orderInfo.getUserId());
		if (!result.equals("10")) {
			resultMap.put("success", false);
			resultMap.put("verifybo", JSONObject.toJSONString(verifybo));
			resultMap.put("errorCode", FanbeiExceptionCode.RISK_VERIFY_ERROR);

			// 如果不是因为逾期还款给拒绝的，直接关闭订单
			// String rejectCode = verifybo.getRejectCode();
			// if (StringUtils.isNotBlank(rejectCode)
			// && !rejectCode.equals(RiskErrorCode.OVERDUE_BORROW.getCode())
			// &&
			// !rejectCode.equals(RiskErrorCode.OVERDUE_BORROW_CASH.getCode()))
			// {
			orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
			orderInfo.setStatus(OrderStatus.CLOSED.getCode());
			orderInfo.setClosedDetail("系统关闭");
			// maqiaopan 2017-9-8 10:54:15风控拒绝原因字段添加
			String rejectCode = verifybo.getRejectCode();
			orderInfo.setClosedReason("风控审批不通过");
			if (StringUtils.isNotBlank(rejectCode)) {
				orderInfo.setClosedReason("风控审批不通过" + rejectCode);
			}

			orderInfo.setGmtClosed(new Date());
			logger.info("updateOrder orderInfo = {}", orderInfo);
			if (StringUtils.equals(orderInfo.getOrderType(), OrderType.AGENTBUY.getCode())) {
				AfAgentOrderDo afAgentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderInfo.getRid());
				afAgentOrderDo.setClosedReason("风控审批失败");
				afAgentOrderDo.setGmtClosed(new Date());
				afAgentOrderService.updateAgentOrder(afAgentOrderDo);

				// 添加关闭订单释放优惠券
				if (afAgentOrderDo.getCouponId() > 0) {
					AfUserCouponDo couponDo = afUserCouponService.getUserCouponById(afAgentOrderDo.getCouponId());

					if (couponDo != null && couponDo.getGmtEnd().after(new Date())) {
						couponDo.setStatus(CouponStatus.NOUSE.getCode());
						afUserCouponService.updateUserCouponSatusNouseById(afAgentOrderDo.getCouponId());
					} else if (couponDo != null && couponDo.getGmtEnd().before(new Date())) {
						couponDo.setStatus(CouponStatus.EXPIRE.getCode());
						afUserCouponService.updateUserCouponSatusExpireById(afAgentOrderDo.getCouponId());
					}
				}
				// orderDao.updateOrder(orderInfo);
			}
			// if (StringUtils.equals(orderInfo.getOrderType(),
			// OrderType.TRADE.getCode())) {
			orderDao.updateOrder(orderInfo);
			// }
			jpushService.dealBorrowApplyFail(userAccountInfo.getUserName(), new Date());
			// }
			return resultMap;
		}
		// resultMap.put("success", true);

		orderInfo.setPayStatus(PayStatus.PAYED.getCode());
		orderInfo.setStatus(OrderStatus.PAID.getCode());
		orderInfo.setPayType(PayType.AGENT_PAY.getCode());
		// 是虚拟商品
		String virtualCode = afOrderService.getVirtualCode(virtualMap);
		if (StringUtils.isNotBlank(virtualCode)) {
			AfUserVirtualAccountDo virtualAccountInfo = BuildInfoUtil.buildUserVirtualAccountDo(orderInfo.getUserId(),
					orderInfo.getActualAmount(), afOrderService.getVirtualAmount(virtualMap), orderInfo.getRid(),
					orderInfo.getOrderNo(), virtualCode);
			// 增加虚拟商品记录
			afUserVirtualAccountService.saveRecord(virtualAccountInfo);
		}
//		if (borrow.getNper() == 1) {
//			borrow.setNperAmount(borrow.getAmount());
//		}
		// 新增借款信息
		afBorrowDao.addBorrow(borrow);

		AfBorrowExtendDo afBorrowExtendDo = new AfBorrowExtendDo();
		afBorrowExtendDo.setId(borrow.getRid());
		afBorrowExtendDo.setInBill(0);
		afBorrowExtendDao.addBorrowExtend(afBorrowExtendDo);

		/**
		 * modify by hongzhengpei
		 */
		if (VersionCheckUitl.getVersion().intValue() >= VersionCheckUitl.VersionZhangDanSecond) {
			if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())
					|| orderInfo.getOrderType().equals(OrderType.BOLUOME.getCode())) {
				afBorrowService.updateBorrowStatus(borrow, userAccountInfo.getUserName(), userAccountInfo.getUserId());
				afBorrowService.dealAgentPayBorrowAndBill(borrow, userAccountInfo.getUserId(),
						userAccountInfo.getUserName(), orderInfo.getActualAmount(), PayType.AGENT_PAY.getCode(),
						orderInfo.getOrderType());
				
				// 支付成功生成结算单 add by luoxiao on 2018-03-30 start
			        try{
			          List<AfTradeBusinessInfoDto> businessInfoList = afTradeBusinessInfoService.getByOrderId(orderInfo.getRid());
			          if(null != businessInfoList && !businessInfoList.isEmpty()){
			            Long businessId = businessInfoList.get(0).getBusinessId();
			            Integer withDrawCycleDays = businessInfoList.get(0).getWithdrawCycle();
			            createSettlementOrder(orderInfo, userAccountInfo.getRealName(), businessId, withDrawCycleDays);
			          }
			        }catch(Exception e){
			          logger.error("createSettlementOrder error.", e);
			        }
			        // end by luoxiao
				
				AfResourceDo assetPushResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
				AssetPushType assetPushType = JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue()), AssetPushType.class);
				Boolean flag=true;
				//新增白名单逻辑
				AfResourceDo pushWhiteResource = afResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_WHITE.getCode());
				if (pushWhiteResource != null) {
					//白名单开启
					String[] whiteUserIdStrs = pushWhiteResource.getValue3().split(",");
					Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
					if(!Arrays.asList(whiteUserIds).contains(orderInfo.getUserId())){
						//不在白名单不推送
						flag=false;
					}
				}
				Boolean bankIsMaintaining = bankIsMaintaining(assetPushResource);
				if (StringUtil.equals(YesNoStatus.NO.getCode(), assetPushResource.getValue3())
						&&((orderInfo.getOrderType().equals(OrderType.TRADE.getCode())&&StringUtil.equals(assetPushType.getTrade(), YesNoStatus.YES.getCode()))||(orderInfo.getOrderType().equals(OrderType.BOLUOME.getCode())&&StringUtil.equals(assetPushType.getBoluome(), YesNoStatus.YES.getCode())))&&flag&&!bankIsMaintaining) {
					//钱包未满额，商圈类型开关或boluome开关开启时推送给钱包
					List<EdspayGetCreditRespBo> pushEdsPayBorrowInfos = pushEdsPayBorrowInfo(borrow);
					AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
					//债权实时推送
					boolean resp = assetSideEdspayUtil.borrowCashCurPush(pushEdsPayBorrowInfos, afAssetSideInfoDo.getAssetSideFlag(),Constants.ASSET_SIDE_FANBEI_FLAG);
					if (resp) {
						logger.info("borrowCurPush suceess,debtType=trade/boluome,orderNo="+pushEdsPayBorrowInfos.get(0).getOrderNo());
						//记录push表
						AfBorrowPushDo borrowPush = buildBorrowPush(borrow.getRid(),pushEdsPayBorrowInfos.get(0).getApr(), pushEdsPayBorrowInfos.get(0).getManageFee());
						afBorrowPushService.saveOrUpdate(borrowPush);
					}
				}
			} else if (orderInfo.getOrderType().equals(OrderType.AGENTBUY.getCode())) {
				afBorrowService.updateBorrowStatus(borrow, userAccountInfo.getUserName(), userAccountInfo.getUserId());
			} else if (orderInfo.getOrderType().equals(OrderType.SELFSUPPORT.getCode())) {
				afBorrowService.updateBorrowStatus(borrow, userAccountInfo.getUserName(), userAccountInfo.getUserId());
			}
		} else {
			// 在风控审批通过后额度不变生成账单
			afBorrowService.updateBorrowStatus(borrow, userAccountInfo.getUserName(), userAccountInfo.getUserId());
			afBorrowService.dealAgentPayBorrowAndBill(borrow, userAccountInfo.getUserId(),
					userAccountInfo.getUserName(), orderInfo.getActualAmount(), PayType.AGENT_PAY.getCode(),
					orderInfo.getOrderType());
		}
		// 更新拆分场景使用额度
		updateUsedAmount(orderInfo, borrow);
		logger.info("payOrder bklUtils submitBklInfo orderInfo ="+JSONObject.toJSONString(orderInfo));
		if ((orderInfo.getOrderType().equals(OrderType.SELFSUPPORT.getCode()))) {
			//新增白名单逻辑
			try {
				try {
					String bklResult = afBklService.isBklResult(orderInfo);
					if (bklResult.equals("v2")){//需电核
						logger.info("payOrder bklUtils submitBklInfo result isBklResult v2 orderInfo ="+JSON.toJSONString(orderInfo));
						afBklService.submitBklInfo(orderInfo,"分期付款",orderInfo.getActualAmount());
						if (orderInfo.getIagentStatus()==null)
							orderInfo.setIagentStatus("C");
					}else if (bklResult.equals("v1")){//不需电核
						logger.info("payOrder bklUtils submitBklInfo result isBklResult v1 orderInfo ="+JSON.toJSONString(orderInfo));

						if (orderInfo.getIagentStatus()==null)
							orderInfo.setIagentStatus("A");
						afOrderService.updateIagentStatusByOrderId(orderInfo.getRid(),orderInfo.getIagentStatus());

					}
				}catch (Exception e){
					logger.error("payOrder bklUtils submitBklInfo error",e);
				}
			}catch (Exception e){
				logger.error("payOrder bklUtils submitBklInfo error",e);
			}
		}
		logger.info("updateOrder orderInfo = {}", orderInfo);
		orderDao.updateOrder(orderInfo);
		if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())) {
			logger.error("TRADE Rebate process");
			// 商圈订单发送，付款成功短信
			smsUtil.sendTradePaid(userAccountInfo.getUserId(), userAccountInfo.getUserName(), new Date(),
					orderInfo.getActualAmount());
			// 商圈订单付款后直接进行返利,并且将订单修改集中
			rebateContext.rebate(orderInfo);
		}

		resultMap.put("success", true);
		return resultMap;
	}
	/**
	   * 生成线下商圈结算单，
	   * 和产品王鲁迪沟通过，线下商圈暂时只生成一条结算单（因为订单没有结束时间，无法算每期结算金额），按照原模式结算
	   * @param afOrderDo
	   * @param userName
	   * @param businessId
	   */
	  private void createSettlementOrder(AfOrderDo afOrderDo, String userName, Long businessId, int withDrawCycleDays){
	    AfTradeSettleOrderDo settleOrderDo = new AfTradeSettleOrderDo();
	    settleOrderDo.setBalanceAmount(afOrderDo.getActualAmount());
	    settleOrderDo.setBatchDelayDays(withDrawCycleDays);
	    settleOrderDo.setBatchMonth(afOrderDo.getNper());
	    settleOrderDo.setBusinessId(businessId);
	    settleOrderDo.setGmtCreate(new Date());
	    settleOrderDo.setCreator(userName);
	    settleOrderDo.setDetails("线下商圈结算订单-" + userName + "-批次1");
	    settleOrderDo.setExtractableDate(getExtractableDate(withDrawCycleDays, afOrderDo.getGmtCreate()));
	    settleOrderDo.setIsDelete(0);
	    settleOrderDo.setBusinessName(afOrderDo.getShopName());
	    settleOrderDo.setOrderId(afOrderDo.getRid());
	    settleOrderDo.setOrderNo(afOrderDo.getOrderNo());
	    settleOrderDo.setStatus(AfTradeSettleOrderStatus.EXTRACTABLE.getCode());
	    settleOrderDo.setGmtModified(new Date());

	    afTradeSettleOrderService.createSettleOrder(settleOrderDo);
	  }

	  /**
	     * 获取租房分期结算可提取时间
	     * @param batchDelayDays
	     * @return
	     */
	  private Date getExtractableDate(int batchDelayDays, Date gmtCreate) {
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(gmtCreate);

	    String dateTimeString = DateUtil.formatDateForPatternWithHyhen(gmtCreate) + " 23:59:59.999";
	    Date extractableDate = DateUtil.parseDate(dateTimeString, DateUtil.DATE_TIME_FULL);

	    int gmtCreateHour = calendar.get(Calendar.HOUR_OF_DAY);
	    if(gmtCreateHour > 12){
	      extractableDate = DateUtil.addDays(extractableDate, 1);
	    }

	    if(batchDelayDays > 1){
	      extractableDate = DateUtil.addDays(extractableDate, batchDelayDays - 1);
	    }

	    return extractableDate;
	  }


	public List<EdspayGetCreditRespBo> pushEdsPayBorrowInfo(final AfBorrowDo borrow) {
		List<EdspayGetCreditRespBo> creditRespBos = new ArrayList<EdspayGetCreditRespBo>();
		EdspayGetCreditRespBo creditRespBo = new EdspayGetCreditRespBo();
		//获取开户行信息
		FanbeiBorrowBankInfoBo bankInfo = assetSideEdspayUtil.getAssetSideBankInfo(assetSideEdspayUtil.getAssetSideBankInfo());
		//借款人平台逾期信息
		AfUserBorrowCashOverdueInfoDto overdueInfoByUserId = afBorrowCashDao.getOverdueInfoByUserId(borrow.getUserId());
		//获取资产方的分润利率
		AfAssetSideInfoDo afAssetSideInfoDo = afAssetSideInfoService.getByFlag(Constants.ASSET_SIDE_EDSPAY_FLAG);
		AfBorrowDto borrowDto = afBorrowService.getBorrowInfoById(borrow.getRid());
		//消费分期的还款计划
		List<RepaymentPlan> repaymentPlans=new ArrayList<RepaymentPlan>();
		List<AfBorrowBillDo> afBorrowBillDos = afBorrowBillService.getAllBorrowBillByBorrowId(borrow.getRid());
		Date lastBorrowBillGmtPayTime=null;
		for (int i = 0; i < afBorrowBillDos.size(); i++) {
			RepaymentPlan repaymentPlan = new RepaymentPlan();
			repaymentPlan.setRepaymentNo(afBorrowBillDos.get(i).getRid()+"");
			repaymentPlan.setRepaymentTime(DateUtil.getSpecSecondTimeStamp(afBorrowBillDos.get(i).getGmtPayTime()));
			repaymentPlan.setRepaymentDays(DateUtil.getNumberOfDayBetween(borrowDto.getGmtCreate(), afBorrowBillDos.get(i).getGmtPayTime()));
			repaymentPlan.setRepaymentAmount(afBorrowBillDos.get(i).getPrincipleAmount());
			repaymentPlan.setRepaymentInterest(afBorrowBillDos.get(i).getInterestAmount());
			repaymentPlan.setRepaymentPeriod(afBorrowBillDos.get(i).getBillNper()-1);
			repaymentPlans.add(repaymentPlan);
			if (i == afBorrowBillDos.size() - 1) {
				lastBorrowBillGmtPayTime= afBorrowBillDos.get(i).getGmtPayTime();
			}
		}
		Integer nper = borrowDto.getNper();//分期数
        if (nper == 5 || nper == 11) {//兼容租房5期与11期，作为6期与12期的利率
        	nper++;
        }
		//获取消费分期协议年化利率配置
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.borrowConsume.getCode());
		BigDecimal borrowRate=BigDecimal.ZERO;
		JSONArray array= new JSONArray();
		if (afResourceDo!=null&& afResourceDo.getValue3()!=null) {
			array= JSONObject.parseArray(afResourceDo.getValue3());
			for (int i = 0; i < array.size(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				Integer confNper= (Integer) jsonObject.get("nper");
				if (nper == confNper) {
					borrowRate=(BigDecimal) jsonObject.get("rate");
					break;
				}
			}
		}
		creditRespBo.setOrderNo(borrow.getBorrowNo());
		creditRespBo.setUserId(borrow.getUserId());
		creditRespBo.setName(borrowDto.getRealName());
		creditRespBo.setCardId(borrowDto.getIdNumber());
		creditRespBo.setMobile(borrowDto.getUserName());
		creditRespBo.setBankNo("");
		creditRespBo.setAcctName(bankInfo.getAcctName());
		creditRespBo.setMoney(borrowDto.getAmount());
		creditRespBo.setApr(BigDecimalUtil.multiply(borrowRate, new BigDecimal(100)));
		creditRespBo.setTimeLimit((int) DateUtil.getNumberOfDayBetween(borrowDto.getGmtCreate(), lastBorrowBillGmtPayTime));
		creditRespBo.setLoanStartTime(DateUtil.getSpecSecondTimeStamp(borrowDto.getGmtCreate()));
		creditRespBo.setPurpose("个人消费");
		creditRespBo.setRepaymentStatus(0);
		creditRespBo.setRepayName(bankInfo.getRepayName());
		creditRespBo.setRepayAcct(bankInfo.getRepayAcct());
		creditRespBo.setRepayAcctBankNo(bankInfo.getRepayAcctBankNo());
		creditRespBo.setRepayAcctType(bankInfo.getRepayAcctType());
		creditRespBo.setIsRepayAcctOtherBank(bankInfo.getIsRepayAcctOtherBank());
		creditRespBo.setManageFee(afAssetSideInfoDo.getAnnualRate());
		creditRespBo.setRepaymentSource("工资收入");
		creditRespBo.setDebtType(AfAssetPackageBusiType.BORROW.getCode());
		creditRespBo.setIsPeriod(1);
		creditRespBo.setTotalPeriod(borrowDto.getNper());
		creditRespBo.setLoanerType(0);
		creditRespBo.setOverdueTimes(overdueInfoByUserId.getOverdueNums());
		creditRespBo.setOverdueAmount(overdueInfoByUserId.getOverdueAmount());
		creditRespBo.setRepaymentPlans(repaymentPlans);
		creditRespBo.setIsCur(0);
		creditRespBos.add(creditRespBo);
		return creditRespBos;
	}



	/**
	 * 更新拆分场景使用额度
	 * 
	 * @param orderInfo
	 * @param borrow
	 */
	public void updateUsedAmount(AfOrderDo orderInfo, AfBorrowDo borrow) {
		// 获取临时额度
		AfInterimAuDo afInterimAuDo = afInterimAuDao.getByUserId(orderInfo.getUserId());
		// 判断商圈订单
		if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())) {
			// 教育培训订单
			if (orderInfo.getSecType().equals(UserAccountSceneType.TRAIN.getCode())) {
				// 增加培训使用额度
				afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.TRAIN.getCode(), orderInfo.getUserId(),
						borrow.getAmount());
			}
		} else { // 线上分期订单
			AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService
					.getByUserIdAndType(UserAccountSceneType.ONLINE.getCode(), orderInfo.getUserId());
			// 获取可使用额度
			BigDecimal useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount())
					.subtract(afUserAccountSenceDo.getFreezeAmount());
			// 修改用户账户信息
			if (useableAmount.compareTo(borrow.getAmount()) == -1) {
				BigDecimal usedInterim = BigDecimal.ZERO;
				if (useableAmount.compareTo(BigDecimal.ZERO) == 1) {
					// 增加线上使用额度
					afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(), orderInfo.getUserId(),
							useableAmount);
					// 增加临时使用额度
					usedInterim = borrow.getAmount().subtract(useableAmount);
					afInterimAuDao.updateInterimUsed(orderInfo.getUserId(), usedInterim);
				} else {
					// 增加临时使用额度
					usedInterim = borrow.getAmount();
					afInterimAuDao.updateInterimUsed(orderInfo.getUserId(), usedInterim);
				}
				// 增加临时额度使用记录
				AfInterimDetailDo afInterimDetailDo = new AfInterimDetailDo();
				afInterimDetailDo.setAmount(usedInterim.multiply(new BigDecimal(-1)));
				afInterimDetailDo.setInterimUsed(afInterimAuDo.getInterimUsed().add(usedInterim));
				afInterimDetailDo.setType(1);
				afInterimDetailDo.setOrderId(orderInfo.getRid());
				afInterimDetailDo.setUserId(orderInfo.getUserId());
				afInterimDetailDao.addAfInterimDetail(afInterimDetailDo);
			} else {
				// 增加线上使用额度
				afUserAccountSenceDao.updateUsedAmount(UserAccountSceneType.ONLINE.getCode(), orderInfo.getUserId(),
						borrow.getAmount());
			}
		}
	}

	/**
	 * @return
	 * @方法描述：实名认证时风控异步审核
	 * @author fumeiai 2017年6月7日 14:47:50
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
			String orderNo = obj.getString("orderNo");

			AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(consumerNo);
			// 风控异步回调的话，以第一次异步回调成功为准
			if (!StringUtil.equals(afUserAuthDo.getRiskStatus(), RiskStatus.NO.getCode())
					&& !StringUtil.equals(afUserAuthDo.getRiskStatus(), RiskStatus.YES.getCode())
					|| orderNo.contains("sdrz")) {
				if (StringUtils.equals("10", result)) {
					AfUserAuthDo authDo = new AfUserAuthDo();
					authDo.setUserId(consumerNo);
					authDo.setRiskStatus(RiskStatus.YES.getCode());
					authDo.setBasicStatus("Y");
					authDo.setGmtBasic(new Date(System.currentTimeMillis()));
					authDo.setGmtRisk(new Date(System.currentTimeMillis()));
					afUserAuthService.updateUserAuth(authDo);

					/*
					 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，
					 * 否则不做变更。 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
					 */
					AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNo);
					if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0
							|| userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
						AfUserAccountDo accountDo = new AfUserAccountDo();
						accountDo.setUserId(consumerNo);
						accountDo.setAuAmount(au_amount);
						afUserAccountService.updateUserAccount(accountDo);
					}
					jpushService.strongRiskSuccess(userAccountDo.getUserName());
					smsUtil.sendRiskSuccess(userAccountDo.getUserName());
				} else if (StringUtils.equals("30", result)) {
					AfUserAuthDo authDo = new AfUserAuthDo();
					authDo.setUserId(consumerNo);
					authDo.setRiskStatus(RiskStatus.NO.getCode());
					authDo.setBasicStatus("N");
					authDo.setGmtBasic(new Date(System.currentTimeMillis()));
					authDo.setGmtRisk(new Date(System.currentTimeMillis()));
					afUserAuthService.updateUserAuth(authDo);

					/*
					 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，则将额度变更为已使用额度。
					 * 否则把用户的额度设置成分控返回的额度
					 */
					AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNo);
					if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0) {
						AfUserAccountDo accountDo = new AfUserAccountDo();
						accountDo.setUserId(consumerNo);
						accountDo.setAuAmount(BigDecimal.ZERO);
						afUserAccountService.updateUserAccount(accountDo);
					} else {
						AfUserAccountDo accountDo = new AfUserAccountDo();
						accountDo.setUserId(consumerNo);
						accountDo.setAuAmount(userAccountDo.getUsedAmount());
						afUserAccountService.updateUserAccount(accountDo);
					}
					jpushService.strongRiskFail(userAccountDo.getUserName());
					smsUtil.sendRiskFail(userAccountDo.getUserName());
				}
			}

		}
		return 0;
	}

	/**
	 * @return
	 * @方法描述：实名认证时风控异步审核(新版本3.9.7以上) @author chefeipeng 2017年6月7日 14:47:50
	 */
	public int asyRegisterStrongRiskV1(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "asyRegisterStrongRiskV1", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			logger.info("asyRegisterStrongRiskV1 reqBo.getSignInfo()" + reqBo.getSignInfo());
			JSONObject obj = JSON.parseObject(data);
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			limitAmount = obj.getString("onlineAmount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal onlineAmount = new BigDecimal(limitAmount);
			limitAmount = obj.getString("offlineAmount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal offlineAmount = new BigDecimal(limitAmount);

			Long consumerNo = Long.parseLong(obj.getString("consumerNo"));
			String result = obj.getString("result");
			String orderNo = obj.getString("orderNo");
			String scene = obj.getString("scene");

			AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(consumerNo);
			// 风控异步回调的话，以第一次异步回调成功为准
			AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNo);
			if (StringUtils.equals("10", result)) {
				if (SceneType.CASH.getCode().equals(scene)) {
					// 小额贷强风控认证成功，更新总额度
					String totalAmount = obj.getString("totalAmount");
					AfUserAccountSenceDo totalAccountSenceDo = afUserAccountSenceService.buildAccountScene(consumerNo,
							"LOAN_TOTAL", totalAmount);
					afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);
					
					if (!StringUtil.equals(afUserAuthDo.getBasicStatus(), RiskStatus.NO.getCode())
							&& !StringUtil.equals(afUserAuthDo.getBasicStatus(), RiskStatus.YES.getCode())
							|| orderNo.contains("sdrz")) {
						AfUserAuthDo authDo = new AfUserAuthDo();
						authDo.setUserId(consumerNo);
						authDo.setRiskStatus(RiskStatus.YES.getCode());
						authDo.setBasicStatus(RiskStatus.YES.getCode());
						authDo.setGmtBasic(new Date(System.currentTimeMillis()));
						authDo.setGmtRisk(new Date(System.currentTimeMillis()));
						afUserAuthService.updateUserAuth(authDo);
						updateUserScenceAmount(userAccountDo, consumerNo, au_amount, new BigDecimal(0),
								new BigDecimal(0));
					}
				} else if (SceneType.ONLINE.getCode().equals(scene)) {
					AfUserAuthStatusDo afUserAuthStatusDo = new AfUserAuthStatusDo();
					afUserAuthStatusDo.setGmtModified(new Date());
					afUserAuthStatusDo.setScene(SceneType.findSceneTypeByCode(scene).getName());
					afUserAuthStatusDo.setUserId(consumerNo);
					afUserAuthStatusDo.setStatus(UserAuthSceneStatus.YES.getCode());
					afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
					updateUserScenceAmount(userAccountDo, consumerNo, new BigDecimal(0), onlineAmount,
							new BigDecimal(0));
				} else if (SceneType.TRAIN.getCode().equals(scene)) {
					AfUserAuthStatusDo afUserAuthStatusDo = new AfUserAuthStatusDo();
					afUserAuthStatusDo.setGmtModified(new Date());
					afUserAuthStatusDo.setScene(SceneType.findSceneTypeByCode(scene).getName());
					afUserAuthStatusDo.setUserId(consumerNo);
					afUserAuthStatusDo.setStatus(UserAuthSceneStatus.YES.getCode());
					afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
					updateUserScenceAmount(userAccountDo, consumerNo, new BigDecimal(0), new BigDecimal(0),
							offlineAmount);
				}

				jpushService.strongRiskSuccess(userAccountDo.getUserName());
				smsUtil.sendRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtils.equals("30", result)) {
				if (SceneType.CASH.getCode().equals(scene)) {
					AfUserAuthDo authDo = new AfUserAuthDo();
					authDo.setUserId(consumerNo);
					if (!StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
						authDo.setRiskStatus(RiskStatus.NO.getCode());
					}
					authDo.setBasicStatus("N");
					authDo.setGmtBasic(new Date(System.currentTimeMillis()));
					authDo.setGmtRisk(new Date(System.currentTimeMillis()));
					afUserAuthService.updateUserAuth(authDo);

					/*
					 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，则将额度变更为已使用额度。
					 * 否则把用户的额度设置成分控返回的额度
					 */
					// 这里修改逻辑永远以风控为准
					if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0) {
						AfUserAccountDo accountDo = new AfUserAccountDo();
						accountDo.setUserId(consumerNo);
						accountDo.setAuAmount(au_amount);
						afUserAccountService.updateUserAccount(accountDo);
					} else {
						AfUserAccountDo accountDo = new AfUserAccountDo();
						accountDo.setUserId(consumerNo);
						accountDo.setAuAmount(au_amount);
						afUserAccountService.updateUserAccount(accountDo);
					}
				} else if (SceneType.ONLINE.getCode().equals(scene) || SceneType.TRAIN.getCode().equals(scene)) {
					AfUserAuthStatusDo afUserAuthStatusDo = new AfUserAuthStatusDo();
					afUserAuthStatusDo.setGmtModified(new Date());
					afUserAuthStatusDo.setScene(SceneType.findSceneTypeByCode(scene).getName());
					afUserAuthStatusDo.setUserId(consumerNo);
					afUserAuthStatusDo.setStatus(UserAuthSceneStatus.FAILED.getCode());
					afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
				}
				jpushService.strongRiskFail(userAccountDo.getUserName());
				smsUtil.sendRiskFail(userAccountDo.getUserName());
			}
		}
		return 0;
	}

	/**
	 * 
	 * @方法描述：开通白领贷异步回调
	 * @author rongbo 2017年6月7日 14:47:50
	 */
	public int asyDredgeWhiteCollarLoan(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "asyDredgeWhiteCollarLoan", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			logger.info("asyDredgeWhiteCollarLoan reqBo.getSignInfo()" + reqBo.getSignInfo());
			JSONObject obj = JSON.parseObject(data);
			String whiteCollarAmount = obj.getString("whiteCollarAmount");
			if (StringUtil.equals(whiteCollarAmount, "") || whiteCollarAmount == null) {
				whiteCollarAmount = "0";
			}
			String totalAmount = obj.getString("totalAmount");

			if (StringUtil.equals(totalAmount, "") || totalAmount == null) {
				totalAmount = "0";
			}
			Long consumerNo = Long.parseLong(obj.getString("consumerNo"));
			String result = obj.getString("result");
			String scene = obj.getString("scene");
			AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(consumerNo);
			
			AfUserAuthStatusDo afUserAuthStatusDo = new AfUserAuthStatusDo();
			afUserAuthStatusDo.setGmtCreate(new Date());
			afUserAuthStatusDo.setGmtModified(new Date());
			afUserAuthStatusDo.setScene(SceneType.findSceneTypeByCode(scene).getName());
			afUserAuthStatusDo.setUserId(consumerNo);
			if (StringUtils.equals("10", result)) {
				afUserAuthStatusDo.setStatus(UserAuthSceneStatus.YES.getCode());
				afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
				// 更新白领贷额度和总额度
				AfUserAccountSenceDo bldAccountSenceDo = afUserAccountSenceService.buildAccountScene(consumerNo,
						SceneType.BLD_LOAN.getName(), whiteCollarAmount);
				AfUserAccountSenceDo totalAccountSenceDo = afUserAccountSenceService.buildAccountScene(consumerNo,
						SceneType.LOAN_TOTAL.getName(), totalAmount);
				afUserAccountSenceService.saveOrUpdateAccountSence(bldAccountSenceDo);
				afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);

				// 处理已认证，未提额的补充认证
				processAuthedNotRaiseAuth(consumerNo,LoanType.BLD_LOAN.getCode());

				jpushService.strongRiskSuccess(userAccountDo.getUserName());
				smsUtil.sendRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtils.equals("30", result)) {
				afUserAuthStatusDo.setStatus(UserAuthSceneStatus.FAILED.getCode());
				afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
				
				jpushService.strongRiskFail(userAccountDo.getUserName());
				smsUtil.sendRiskFail(userAccountDo.getUserName());
			} /*else if (StringUtils.equals("20", result)) {//人审
				smsUtil.sendRiskNeedAudit(userAccountDo.getUserName());
			}*/
		}
		return 0;
	}

	private void processAuthedNotRaiseAuth(Long userId,String scene) {
		AfUserAuthDo userAuthInfo = afUserAuthService.getUserAuthInfoByUserId(userId);
		Map<String, String> authInfoMap = Maps.newHashMap();
		authInfoMap.put(AuthType.ALIPAY.getCode(), userAuthInfo.getAlipayStatus());
		authInfoMap.put(AuthType.CARDEMAIL.getCode(), userAuthInfo.getCreditStatus());
		authInfoMap.put(AuthType.ZHENGXIN.getCode(), userAuthInfo.getZhengxinStatus());
		authInfoMap.put(AuthType.BANK.getCode(), userAuthInfo.getOnlinebankStatus());
		authInfoMap.put(AuthType.FUND.getCode(), userAuthInfo.getFundStatus());
		authInfoMap.put(AuthType.INSURANCE.getCode(), userAuthInfo.getJinpoStatus());
		for (Map.Entry<String, String> entry : authInfoMap.entrySet()) {
			String authType = entry.getKey();
			String authStatus = entry.getValue();
			if (StringUtils.equals(authStatus, "Y")) {
				AfAuthRaiseStatusDo authRaiseStatusDo = new AfAuthRaiseStatusDo();
				authRaiseStatusDo.setAuthType(authType);
				authRaiseStatusDo.setPrdType(scene);
				authRaiseStatusDo.setUserId(userId);
				authRaiseStatusDo = afAuthRaiseStatusService.getByCommonCondition(authRaiseStatusDo);
				if(authRaiseStatusDo == null || !StringUtils.equals("Y", authRaiseStatusDo.getRaiseStatus())) {
					AuthCallbackBo authCallbackBo = new AuthCallbackBo("", ObjectUtils.toString(userId), authType,
							RiskAuthStatus.SUCCESS.getCode(),scene);
					authCallbackManager.execute(authCallbackBo);
				}
			}
		}

	}

	private void updateUserScenceAmount(AfUserAccountDo userAccountDo, Long consumerNo, BigDecimal au_amount,
			BigDecimal onlineAmount, BigDecimal offlineAmount) {
		/*
		 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，否则不做变更。
		 * 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
		 */
		if (au_amount.compareTo(new BigDecimal(0)) > 0) {
			AfUserAccountDo accountDo = new AfUserAccountDo();
			accountDo.setUserId(consumerNo);
			accountDo.setAuAmount(au_amount);
			afUserAccountService.updateUserAccount(accountDo);
			// }
		}

		if (onlineAmount.compareTo(new BigDecimal(0)) > 0) {
			// AfUserAccountSenceDo afUserAccountOnlineDo =
			// afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(),
			// consumerNo);
			// if(afUserAccountOnlineDo!=null) {
			// if
			// (afUserAccountOnlineDo.getUsedAmount().compareTo(BigDecimal.ZERO)
			// == 0 ||
			// afUserAccountOnlineDo.getUsedAmount().compareTo(au_amount) < 0) {
			// afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.ONLINE.getCode(),
			// consumerNo, onlineAmount);
			// }
			// }
			// else{
			afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.ONLINE.getCode(), consumerNo,
					onlineAmount);
			// }
		}
		if (offlineAmount.compareTo(new BigDecimal(0)) > 0) {
			// AfUserAccountSenceDo afUserAccountOfflineDo =
			// afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.TRAIN.getCode(),
			// consumerNo);
			// if(afUserAccountOfflineDo!=null) {
			// if
			// (afUserAccountOfflineDo.getUsedAmount().compareTo(BigDecimal.ZERO)
			// == 0 ||
			// afUserAccountOfflineDo.getUsedAmount().compareTo(au_amount) < 0)
			// {
			// afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.TRAIN.getCode(),
			// consumerNo, offlineAmount);
			// }
			// }
			// else
			// {
			afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.TRAIN.getCode(), consumerNo,
					offlineAmount);
			// }
		}
	}

	private void updateUserScenceAmount(AfUserAccountDo userAccountDo, Long consumerNo, BigDecimal au_amount,
										BigDecimal onlineAmount, BigDecimal offlineAmount,BigDecimal totalAmount) {
		/*
		 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，否则不做变更。
		 * 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
		 */
		if (au_amount.compareTo(new BigDecimal(0)) > 0) {
			AfUserAccountDo accountDo = new AfUserAccountDo();
			accountDo.setUserId(consumerNo);
			accountDo.setAuAmount(au_amount);
			afUserAccountService.updateUserAccount(accountDo);
			// }
		}

		if (onlineAmount.compareTo(new BigDecimal(0)) > 0) {
			// AfUserAccountSenceDo afUserAccountOnlineDo =
			// afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(),
			// consumerNo);
			// if(afUserAccountOnlineDo!=null) {
			// if
			// (afUserAccountOnlineDo.getUsedAmount().compareTo(BigDecimal.ZERO)
			// == 0 ||
			// afUserAccountOnlineDo.getUsedAmount().compareTo(au_amount) < 0) {
			// afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.ONLINE.getCode(),
			// consumerNo, onlineAmount);
			// }
			// }
			// else{
			afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.ONLINE.getCode(), consumerNo,
					onlineAmount);
			// }
		}
		if (offlineAmount.compareTo(new BigDecimal(0)) > 0) {
			// AfUserAccountSenceDo afUserAccountOfflineDo =
			// afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.TRAIN.getCode(),
			// consumerNo);
			// if(afUserAccountOfflineDo!=null) {
			// if
			// (afUserAccountOfflineDo.getUsedAmount().compareTo(BigDecimal.ZERO)
			// == 0 ||
			// afUserAccountOfflineDo.getUsedAmount().compareTo(au_amount) < 0)
			// {
			// afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.TRAIN.getCode(),
			// consumerNo, offlineAmount);
			// }
			// }
			// else
			// {
			afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.TRAIN.getCode(), consumerNo,
					offlineAmount);
			// }
		}

		if (totalAmount.compareTo(new BigDecimal(0)) > 0) {
			afUserAccountSenceService.updateUserSceneAuAmount(UserAccountSceneType.LOAN_TOTAL.getCode(), consumerNo,
					totalAmount);
		}
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
		String reqResult = requestProxy.post(getUrl() + "/modules/api/risk/operator.htm", reqBo);
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
	 * --用户唯一标识 --用户名
	 * 
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
			if (StringUtil.equals("50", result)) {// 50是定时任务 推送超时
				// 不做任何更新
				return 0;
			}
			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtMobile(new Date());
			AfUserAccountDo accountInfo = afUserAccountService.getUserAccountByUserId(Long.parseLong(consumerNo));
			if (StringUtil.equals("10", result)) {
				auth.setMobileStatus(MobileStatus.YES.getCode());
				jpushService.mobileRiskSuccess(accountInfo.getUserName());
				// 成功时将运营商的失效状态改变
				syncOperator(Long.parseLong(consumerNo));

			} else {
				auth.setMobileStatus(MobileStatus.NO.getCode());
				// 推送打开,且短信推送
				jpushService.mobileRiskFail(accountInfo.getUserName());
				smsUtil.sendMobileOperateFail(accountInfo.getUserName());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	/**
	 * @param consumerNo
	 *            --用户唯一标识
	 * @return
	 * @throws Exception
	 *             ? 用户联系人同步
	 * @方法描述：? 用户联系人同步
	 * @author huyang 2017年4月5日上午11:41:55
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
		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/action/linkman/remove.htm", reqBo);
		// 测试
		// String reqResult = requestProxy.post("http://60.190.230.35:52637" +
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
	 * @param consumerNo
	 *            --用户唯一标识
	 * @param data
	 *            --通讯录信息，格式为
	 *            张三:15888881111&15811234444,李四:15888881111&15811234444
	 * @return
	 * @throws Exception
	 *             传入更新的通讯录为空
	 * @方法描述：同步用户通讯录集合
	 * @author fumeiai 2017年4月21日上午11:08:55
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
		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/action/directory/remove.htm", reqBo);
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
	 * @param consumerNo
	 *            --用户唯一标识
	 * @return
	 * @throws Exception
	 * @方法描述：用户白名单信息同步
	 * @author fumeiai 2017年5月11日上午11:21
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
		// requestProxy.post("http://192.168.96.139:80/modules/api/user/whiteuser.htm",
		// reqBo);
		String reqResult = requestProxy.post(getUrl() + "/modules/api/user/whiteuser.htm", reqBo);
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
	 * 
	 * @param consumerNo
	 * 
	 * @param scene
	 * 
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
		// String content = JSONObject.toJSONString(reqBo);
		// try {
		// commitRecordUtil.addRecord("verify", borrowId, content, url);
		// } catch (Exception e) {
		// logger.error("field too long，verify insert commitRecord
		// fail,consumerNo="+consumerNo);
		// }
		String reqResult = requestProxy.post(url, reqBo);

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
	 * --用户唯一标识 --用户名
	 * 
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
						JSONObject obj = JSON.parseObject(data);
						String orderNo = obj.getString("orderNo");

						// 添加一个根据风控号查找记录的方法
						AfOrderDo orderInfo = orderDao.getOrderInfoByRiskOrderNo(orderNo);
						// 如果风控审核结果是不成功则关闭订单，修改订单状态是支付中
						JSONObject object = JSON.parseObject(data);

						AfUserAccountDo userAccountInfo = afUserAccountService
								.getUserAccountByUserId(orderInfo.getUserId());
						if (!object.get("result").toString().equals("10")) {
							orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
							orderInfo.setStatus(OrderStatus.CLOSED.getCode());
							orderInfo.setClosedDetail("系统关闭");
							// maqiaopan 2017-9-8 10:54:15风控拒绝原因字段添加
							String rejectCode = object.get("result").toString();
							orderInfo.setClosedReason("风控审批不通过");
							if (StringUtils.isNotBlank(rejectCode)) {
								orderInfo.setClosedReason("风控审批不通过" + rejectCode);
							}
							orderInfo.setGmtClosed(new Date());
							logger.info("updateOrder orderInfo = {}", orderInfo);
							int re = orderDao.updateOrder(orderInfo);
							// 审批不通过时，让额度还原到以前

							// TODO:额度增加，而非减少
							BigDecimal usedAmount = orderInfo.getActualAmount().multiply(BigDecimal.valueOf(-1));
							afBorrowService.dealAgentPayClose(userAccountInfo, usedAmount, orderInfo.getRid());
							if (StringUtils.equals(orderInfo.getOrderType(), OrderType.AGENTBUY.getCode())) {
								AfAgentOrderDo afAgentOrderDo = afAgentOrderService
										.getAgentOrderByOrderId(orderInfo.getRid());
								afAgentOrderDo.setClosedReason("风控审批失败");
								afAgentOrderDo.setGmtClosed(new Date());
								afAgentOrderService.updateAgentOrder(afAgentOrderDo);

								// 添加关闭订单释放优惠券
								if (afAgentOrderDo.getCouponId() > 0) {
									AfUserCouponDo couponDo = afUserCouponService
											.getUserCouponById(afAgentOrderDo.getCouponId());

									if (couponDo != null && couponDo.getGmtEnd().after(new Date())) {
										couponDo.setStatus(CouponStatus.NOUSE.getCode());
										afUserCouponService
												.updateUserCouponSatusNouseById(afAgentOrderDo.getCouponId());
									} else if (couponDo != null && couponDo.getGmtEnd().before(new Date())) {
										couponDo.setStatus(CouponStatus.EXPIRE.getCode());
										afUserCouponService
												.updateUserCouponSatusExpireById(afAgentOrderDo.getCouponId());
									}
								}
							}
							jpushService.dealBorrowApplyFail(userAccountInfo.getUserName(), new Date());
							return new Long(String.valueOf(re));
						}

						// 在风控审批通过后额度不变生成账单
						afBorrowService.dealAgentPayBorrowAndBill(userAccountInfo.getUserId(),
								userAccountInfo.getUserName(), orderInfo.getActualAmount(), orderInfo.getGoodsName(),
								orderInfo.getNper(), orderInfo.getRid(), orderInfo.getOrderNo(),
								orderInfo.getBorrowRate(), orderInfo.getInterestFreeJson(), false);

						// 审批通过时
						orderInfo.setPayStatus(PayStatus.PAYED.getCode());
						orderInfo.setStatus(OrderStatus.PAID.getCode());
						// 关闭订单时间和原因的更新

						logger.info("updateOrder orderInfo = {}", orderInfo);
						orderDao.updateOrder(orderInfo);
						if (orderInfo.getOrderType().equals(OrderType.TRADE.getCode())) {
							logger.error("TRADE Rebate process");
							// 商圈订单发送，付款成功短信
							smsUtil.sendTradePaid(userAccountInfo.getUserId(), userAccountInfo.getUserName(),
									new Date(), orderInfo.getActualAmount());
							// 商圈订单付款后直接进行返利,并且将订单修改集中
							rebateContext.rebate(orderInfo);
						}
						if (StringUtils.equals(orderInfo.getOrderType(), OrderType.BOLUOME.getCode())) {
							boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderType(),
									orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC,
									orderInfo.getUserId(), orderInfo.getSaleAmount(), orderInfo.getSecType());
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
	 * --用户唯一标识 --用户名
	 * 
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
			if (whiteListInfo != null) {
				whiteIdsList = CollectionConverterUtil.convertToListFromArray(whiteListInfo.getValue3().split(","),
						new Converter<String, String>() {
							@Override
							public String convert(String source) {
								return source.trim();
							}
						});
			}

			if (whiteIdsList.contains(afUserDo.getUserName()) || StringUtils.equals("10", result)) {

				jpushService.dealBorrowCashApplySuccss(afUserDo.getUserName(), currDate);
				String bankNumber = card.getCardNumber();
				String lastBank = bankNumber.substring(bankNumber.length() - 4);

				smsUtil.sendBorrowCashCode(afUserDo.getUserName(), lastBank);
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
				// Integer day = NumberUtil
				// .objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(),
				// 7);
				Integer day = borrowTime(afBorrowCashDo.getType());
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
	 * @param consumerNo
	 *            --用户唯一标识
	 * @return BigDecimal
	 * @throws Exception
	 * @方法描述：查询用户额度
	 * @author maqiaopan 2017年5月25日 14:30:28
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	public BigDecimal queryAmount(Long consumerNo) {
		BigDecimal bigDecimal = BigDecimal.ZERO;
		Map<String, String> params = new HashMap<>();
		params.put("consumerNo", consumerNo.toString());
		String signInfo = SignUtil.sign(createLinkString(params), PRIVATE_KEY);
		params.put("signInfo", signInfo);

		String reqResult = requestProxy.post(getUrl() + "modules/api/risk/verify.htm", params);
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

	public void payOrderChangeAmount(Long rid){
		AfOrderDo orderInfo = orderDao.getOrderById(rid);
		logger.info("payOrderChangeAmount orderInfo = {}", orderInfo);
		if (orderInfo != null && StringUtils.equals(orderInfo.getOrderType(), OrderType.BOLUOME.getCode())) {
			boluomeUtil.pushPayStatus(orderInfo.getRid(), orderInfo.getOrderType(), orderInfo.getOrderNo(),
					orderInfo.getThirdOrderNo(), PushStatus.PAY_SUC, orderInfo.getUserId(), orderInfo.getSaleAmount(),
					orderInfo.getSecType());
		}
	}

	/**
	 * 获取虚拟商品可使用额度
	 *
	 * @param consumerNo
	 *            用户id
	 * @return
	 */
	public RiskVirtualProductQuotaRespBo virtualProductQuota(String consumerNo, String businessType, String productCode,
			String productCodeId) {
		RiskVirtualProductQuotaReqBo reqBo = new RiskVirtualProductQuotaReqBo();
		reqBo.setConsumerNo(consumerNo);

		JSONObject obj = new JSONObject();
		obj.put("businessType", businessType.replaceAll("\r|\n", ""));
		obj.put("productCode", productCode.replaceAll("\r|\n", ""));
		obj.put("productCodeId", productCodeId.replaceAll("\r|\n", ""));

		logThird(obj, "virtualProductQuota obj", obj);
		reqBo.setDetails(Base64.encodeString(JSON.toJSONString(obj)).replaceAll("\r|\n", ""));
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = requestProxy.post(getUrl() + "/modules/api/risk/virtualProductQuota.htm", reqBo);

		logThird(reqResult, "virtualProductQuota", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.VIRTUAL_PRODUCT_QUOTA_ERROR);
		}
		RiskVirtualProductQuotaRespBo riskResp = JSONObject.parseObject(reqResult, RiskVirtualProductQuotaRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			// VirtualProductQuota data = riskResp.getData();
			// if (data != null) {
			// //JSONObject json = JSONObject.parseObject(data);
			// //如果有code,没有amount 那么默认100
			// riskResp.setAmount(json.getBigDecimal("amount"));
			// riskResp.setVirtualCode(json.getString("virtualCode"));
			// }
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.VIRTUAL_PRODUCT_QUOTA_ERROR);
		}
	}

	/**
	 * 查询用户借钱或者分期订单
	 *
	 * @param consumerNo
	 * @return
	 */
	public RiskQueryOverdueOrderRespBo queryOverdueOrder(String consumerNo) {
		RiskQueryOverdueOrderReqBo reqBo = new RiskQueryOverdueOrderReqBo();
		reqBo.setConsumerNo(consumerNo);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = requestProxy.post(getUrl() + "/modules/api/risk/queryOverdueOrder.htm", reqBo);
		// String reqResult = requestProxy.post("http://192.168.110.22:80" +
		// "/modules/api/risk/queryOverdueOrder.htm", reqBo);

		logThird(reqResult, "queryOverdueOrder", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.QUERY_OVERDUE_ORDER_ERROR);
		}
		RiskQueryOverdueOrderRespBo riskResp = JSONObject.parseObject(reqResult, RiskQueryOverdueOrderRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			riskResp.setSuccess(true);
			String data = riskResp.getData();
			if (StringUtils.isNotBlank(data)) {
				JSONObject json = JSONObject.parseObject(data);
				riskResp.setRejectCode(json.getString("rejectCode"));
				riskResp.setBorrowNo(json.getString("borrowNo"));
			}
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.QUERY_OVERDUE_ORDER_ERROR);
		}
	}

	/**
	 * 批量化同步逾期订单
	 *
	 * @param orderNo
	 * @param boList
	 * @return
	 */
	public RiskRespBo batchSychronizeOverdueBorrow(String orderNo, List<RiskOverdueBorrowBo> boList) {
		RiskOverdueOrderBo reqBo = new RiskOverdueOrderBo();
		reqBo.setOrderNo(orderNo);
		reqBo.setDetails(Base64.encodeString(JSONObject.toJSONString(boList)));
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/overdueOrder.htm";
		// 石桂红本地
		// String url = "http://192.168.110.22:80" +
		// "/modules/api/risk/overdueOrder.htm";
		String reqResult = requestProxy.post(url, reqBo);

		logThird(reqResult, "overDued", reqBo);
		RiskRespBo riskResp = JSONObject.parseObject(reqResult, RiskRespBo.class);
		return riskResp;
	}

	/**
	 * 魔蝎公积金第三方数据查询异步通知
	 *
	 * @return
	 */
	public int fundNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "fundNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {
				return 0;// 不做任何更新
			}
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtFund(new Date(System.currentTimeMillis()));
			AfUserAccountDo userAccountDo = afUserAccountService
					.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			if (StringUtil.equals("10", result)) {
				auth.setFundStatus(YesNoStatus.YES.getCode());
				/*
				 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，
				 * 否则不做变更。 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
				 */
				if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0
						|| userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
					auth.setRiskStatus(RiskStatus.YES.getCode());
					AfUserAccountDo accountDo = new AfUserAccountDo();
					accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
					accountDo.setAuAmount(au_amount);
					afUserAccountService.updateUserAccount(accountDo);
				}
				jpushService.fundRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtil.equals("20", result)) {// 20是认证未通过 风控返回错误
				auth.setFundStatus(YesNoStatus.NO.getCode());
				jpushService.fundRiskFail(userAccountDo.getUserName());
			} else if (StringUtil.equals("21", result)) {// 21是认证失败 魔蝎返回错误
				auth.setFundStatus("A");
				jpushService.fundRiskFault(userAccountDo.getUserName());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	/**
	 * 51公积金认证风控异步通知
	 * @return
	 */
	public int newFundNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "newFundNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {
				return 0;// 不做任何更新
			}
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtFund(new Date(System.currentTimeMillis()));
			AfUserAccountDo userAccountDo = afUserAccountService
					.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			if (StringUtil.equals("10", result)) {
				auth.setFundStatus(YesNoStatus.YES.getCode());
				/*
				 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，
				 * 否则不做变更。 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
				 */
				if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0
						|| userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
					auth.setRiskStatus(RiskStatus.YES.getCode());
					AfUserAccountDo accountDo = new AfUserAccountDo();
					accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
					accountDo.setAuAmount(au_amount);
					afUserAccountService.updateUserAccount(accountDo);
				}
				jpushService.fundRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtil.equals("20", result)) {// 20是认证未通过 ，风控返回错误
				auth.setFundStatus(YesNoStatus.NO.getCode());
				jpushService.fundRiskFail(userAccountDo.getUserName());
			} else if (StringUtil.equals("21", result)) {// 21是认证失败,51公积金返回错误
				auth.setFundStatus("A");
				jpushService.fundRiskFault(userAccountDo.getUserName());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	/**
	 * 魔蝎社保第三方数据查询异步通知
	 *
	 * --用户唯一标识 --用户名
	 * 
	 * @return
	 */
	public int socialSecurityNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo, "socialSecurityNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {// 不做任何更新
				return 0;
			}
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtJinpo(new Date(System.currentTimeMillis()));

			AfUserAccountDo userAccountDo = afUserAccountService
					.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			if (StringUtil.equals("10", result)) {
				auth.setJinpoStatus(YesNoStatus.YES.getCode());
				/*
				 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，
				 * 否则不做变更。 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
				 */
				if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0
						|| userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
					auth.setRiskStatus(RiskStatus.YES.getCode());
					AfUserAccountDo accountDo = new AfUserAccountDo();
					accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
					accountDo.setAuAmount(au_amount);
					afUserAccountService.updateUserAccount(accountDo);
				}
				jpushService.socialSecurityRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtil.equals("20", result)) {// 20是认证未通过 风控返回错误
				auth.setJinpoStatus(YesNoStatus.NO.getCode());
				jpushService.socialSecurityRiskFail(userAccountDo.getUserName());
			} else if (StringUtil.equals("21", result)) {// 21是认证失败 魔蝎返回错误
				auth.setJinpoStatus("A");
				jpushService.socialSecurityRiskFault(userAccountDo.getUserName());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	/**
	 * 魔蝎信用卡第三方数据查询异步通知
	 *
	 * @param code
	 *            --用户唯一标识
	 * @param msg
	 *            --用户名
	 * @return
	 */
	public int creditCardNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logger.info(createLinkString(reqBo));
		logThird(signInfo, "creditCardNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {// 不做任何更新
				return 0;
			}
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtCredit(new Date(System.currentTimeMillis()));
			AfUserAccountDo userAccountDo = afUserAccountService
					.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));

			if (StringUtil.equals("10", result)) {
				auth.setCreditStatus(YesNoStatus.YES.getCode());
				/*
				 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，
				 * 否则不做变更。 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
				 */
				if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0
						|| userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
					auth.setRiskStatus(RiskStatus.YES.getCode());
					AfUserAccountDo accountDo = new AfUserAccountDo();
					accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
					accountDo.setAuAmount(au_amount);
					afUserAccountService.updateUserAccount(accountDo);
				}
				jpushService.creditCardRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtil.equals("20", result)) {// 20是认证未通过 风控返回错误
				auth.setCreditStatus(YesNoStatus.NO.getCode());
				jpushService.creditCardRiskFail(userAccountDo.getUserName());
			} else if (StringUtil.equals("21", result)) {// 21是认证失败 魔蝎返回错误
				auth.setCreditStatus("A");
				jpushService.creditCardRiskFault(userAccountDo.getUserName());
			}

			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	/**
	 * 魔蝎支付宝第三方数据查询异步通知
	 *
	 * @param msg
	 *            --用户唯一标识
	 * @param code
	 *            --用户名
	 * @return
	 */
	public int alipayNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logger.info(createLinkString(reqBo));
		logThird(signInfo, "alipayNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {// 不做任何更新
				return 0;
			}
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtAlipay(new Date(System.currentTimeMillis()));
			AfUserAccountDo userAccountDo = afUserAccountService
					.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));

			if (StringUtil.equals("10", result)) {
				auth.setAlipayStatus(YesNoStatus.YES.getCode());
				/*
				 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，
				 * 否则不做变更。 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
				 */
				if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0
						|| userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
					auth.setRiskStatus(RiskStatus.YES.getCode());
					AfUserAccountDo accountDo = new AfUserAccountDo();
					accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
					accountDo.setAuAmount(au_amount);
					afUserAccountService.updateUserAccount(accountDo);
				}
				jpushService.alipayRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtil.equals("20", result)) {// 20是认证未通过 风控返回错误
				auth.setAlipayStatus(YesNoStatus.NO.getCode());
				jpushService.alipayRiskFail(userAccountDo.getUserName());
			} else if (StringUtil.equals("21", result)) {// 21是认证失败 魔蝎返回错误
				auth.setAlipayStatus("A");
				jpushService.alipayRiskFault(userAccountDo.getUserName());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	/**
	 * 获取用户分层利率
	 *
	 * @param consumerNo
	 *            用户ID
	 * @return
	 */
	public RiskVerifyRespBo getUserLayRate(String consumerNo, JSONObject params) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setConsumerNo(consumerNo);
		if (params != null) {
			AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(Long.parseLong(consumerNo));
			JSONObject eventObj = new JSONObject();
			eventObj.put("appName", params.get("appName") + "");
			eventObj.put("cardNo", card == null ? "" : card.getCardNumber());
			eventObj.put("blackBox", params.get("blackBox") == null ? "" : params.get("blackBox"));
			eventObj.put("ipAddress", params.get("ipAddress") == null ? "" : params.get("ipAddress"));
			eventObj.put("bqsBlackBox", params.get("bqsBlackBox") == null ? "" : params.get("bqsBlackBox"));
			reqBo.setEventInfo(JSONObject.toJSONString(eventObj));
		}
		HashMap summaryData = afBorrowDao.getUserSummary(Long.parseLong(consumerNo));
		reqBo.setSummaryData(JSON.toJSONString(summaryData));
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/userRate.htm";
		// String url =
		// "http://192.168.110.22:80/modules/api/risk/userRate.htm";
		String reqResult = requestProxy.post(url, reqBo);
		logThird(reqResult, "getUserLayRate", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_USERLAY_RATE_ERROR);
		}

		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			String result = dataObj.getString("result");
			riskResp.setSuccess(true);
			riskResp.setResult(result);
			riskResp.setConsumerNo(consumerNo);
			riskResp.setPoundageRate(dataObj.getString("rate"));
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_USERLAY_RATE_ERROR);
		}
	}

	/**
	 * 获取用户分层利率
	 *
	 * @param consumerNo
	 *            用户ID
	 * @return
	 */
	public RiskVerifyRespBo getUserLayRate(String consumerNo, JSONObject params, String borrowType) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setConsumerNo(consumerNo);
		reqBo.setBorrowType(borrowType);
		if (params != null) {
			AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(Long.parseLong(consumerNo));
			JSONObject eventObj = new JSONObject();
			eventObj.put("appName", params.get("appName") + "");
			eventObj.put("cardNo", card == null ? "" : card.getCardNumber());
			// eventObj.put("blackBox", params.get("blackBox") == null ? "" :
			// params.get("blackBox"));
			eventObj.put("ipAddress", params.get("ipAddress") == null ? "" : params.get("ipAddress"));
			// eventObj.put("bqsBlackBox", params.get("bqsBlackBox") == null ?
			// "" : params.get("bqsBlackBox"));
			reqBo.setEventInfo(JSONObject.toJSONString(eventObj));
		}
		HashMap summaryData = afBorrowDao.getUserSummary(Long.parseLong(consumerNo));
		reqBo.setSummaryData(JSON.toJSONString(summaryData));
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/userRate.htm";
		// String url =
		// "http://192.168.110.22:80/modules/api/risk/userRate.htm";
		String reqResult = requestProxy.post(url, reqBo);
		logThird(reqResult, "getUserLayRate", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_USERLAY_RATE_ERROR);
		}

		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		riskResp.setOrderNo(reqBo.getOrderNo());
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			String result = dataObj.getString("result");
			riskResp.setSuccess(true);
			riskResp.setResult(result);
			riskResp.setConsumerNo(consumerNo);
			riskResp.setPoundageRate(dataObj.getString("rate"));
			return riskResp;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.RISK_USERLAY_RATE_ERROR);
		}
	}

	/**
	 * 获取租赁用户分层得分
	 *
	 * @param consumerNo
	 *            用户ID
	 * @return
	 */
	public Integer getRentScore(String consumerNo, JSONObject params) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setConsumerNo(consumerNo);
		if (params != null) {
			AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(Long.parseLong(consumerNo));
			JSONObject eventObj = new JSONObject();
			eventObj.put("appName", params.get("appName") + "");
			eventObj.put("cardNo", card == null ? "" : card.getCardNumber());
			eventObj.put("blackBox", params.get("blackBox") == null ? "" : params.get("blackBox"));
			eventObj.put("ipAddress", params.get("ipAddress") == null ? "" : params.get("ipAddress"));
			String riskOrderNo = riskUtil.getOrderNo("rent", card.getCardNumber().substring(card.getCardNumber().length() - 4, card.getCardNumber().length()));
			reqBo.setOrderNo(riskOrderNo);
			reqBo.setEventInfo(JSONObject.toJSONString(eventObj));
		}
		HashMap summaryData = afBorrowDao.getUserSummary(Long.parseLong(consumerNo));
		reqBo.setSummaryData(JSON.toJSONString(summaryData));
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/getRentScore.htm";
		// String url =
		// "http://192.168.110.22:80/modules/api/risk/getRentScore.htm";
		String reqResult = requestProxy.post(url, reqBo);
		logThird(reqResult, "getRentScore", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			return 0;
		}

		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			Integer result = dataObj.getInteger("score");
			if(result==null)
				return 0;
			
			return result;
		} else {
			return 0;
		}
	}

	/**
	 * 更新租赁用户分层得分
	 *
	 * @param consumerNo
	 *            用户ID
	 * @return
	 */
	public boolean updateRentScore(String consumerNo) {
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setConsumerNo(consumerNo);
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(Long.parseLong(consumerNo));
		String riskOrderNo = riskUtil.getOrderNo("rent", card.getCardNumber().substring(card.getCardNumber().length() - 4, card.getCardNumber().length()));
		reqBo.setOrderNo(riskOrderNo);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/risk/updateRentScore.htm";
		// String url =
		// "http://192.168.110.22:80/modules/api/risk/updateRentScore.htm";
		String reqResult = requestProxy.post(url, reqBo);
		logThird(reqResult, "updateRentScore", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			return false;
		}

		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult, RiskVerifyRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 登录可信验证码
	 *
	 * @param consumerNo
	 * @param consumerNo
	 * @return
	 */
	public boolean verifySynLogin(String consumerNo, String phone, String blackBox, String deviceUuid, String loginType,
			String loginTime, String ip, String phoneType, String networkType, String osType, String bqsBlackBox) {

		// RiskTrustReqBo reqBo = new RiskTrustReqBo();
		Map<String, String> map = new HashMap<String, String>();
		map.put("consumerNo", consumerNo);
		map.put("eventType", Constants.EVENT_LOGIN_SYN);
		map.put("orderNo", generatorClusterNo.getRiskLoginNo(new Date()));
		JSONObject obj = new JSONObject();
		obj.put("phone", phone);
		obj.put("blackBox", blackBox);
		obj.put("bqsBlackBox", bqsBlackBox);
		obj.put("deviceUuid", deviceUuid);
		obj.put("loginType", loginType);
		obj.put("loginTime", loginTime);
		// obj.put("imei", imei);
		obj.put("ip", ip);
		obj.put("phoneType", phoneType);
		obj.put("networkType", networkType);
		obj.put("osType", osType);
		map.put("details", Base64.encodeString(JSON.toJSONString(obj)));
		// Map<String,String> reqBo = new HashMap<String, String>();
		map.put("signInfo", SignUtil.sign(createLinkString(map), PRIVATE_KEY));
		String url = getUrl() + "/modules/api/event/syn/login.htm";
		// String url = "http://192.168.110.16:8080" +
		// "/modules/api/risk/weakRiskVerify.htm";
		String reqResult = requestProxy.post(url, map);

		logThird(reqResult, "verifySynLogin", map);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_SYN_LOGIN_VERIFY_ERROR);
		}

		RiskLoginRespBo riskResp = JSONObject.parseObject(reqResult, RiskLoginRespBo.class);
		if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getCode())) {
			JSONObject jsonObj = JSON.parseObject(riskResp.getData());
			if ("1".equals(jsonObj.getString("isTrust"))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 风控异步登录
	 *
	 * @param consumerNo
	 * @param phone
	 * @param blackBox
	 * @param deviceUuid
	 * @param loginType
	 * @param loginTime
	 * @param ip
	 * @param phoneType
	 * @param networkType
	 * @param osType
	 * @return
	 */
	public void verifyASyLogin(String consumerNo, String phone, String blackBox, String deviceUuid, String loginType,
			String loginTime, String ip, String phoneType, String networkType, String osType, String result,
			String event, String bqsBlackBox) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("consumerNo", consumerNo);
		map.put("eventType", event);
		map.put("orderNo", generatorClusterNo.getRiskLoginNo(new Date()));
		JSONObject obj = new JSONObject();
		obj.put("phone", phone);
		obj.put("blackBox", blackBox);
		obj.put("bqsBlackBox", bqsBlackBox);
		obj.put("deviceUuid", deviceUuid);
		obj.put("loginResult", result);
		obj.put("loginType", loginType);
		obj.put("loginTime", loginTime);
		// obj.put("imei", imei);
		obj.put("ip", ip);
		obj.put("phoneType", phoneType);
		obj.put("networkType", networkType);
		obj.put("osType", osType);
		map.put("details", Base64.encodeString(JSON.toJSONString(obj)));
		map.put("signInfo", SignUtil.sign(createLinkString(map), PRIVATE_KEY));
		String url = getUrl() + "/modules/api/event/asy/login.htm";
		asyLoginService.excute(map, url, "asyLoginVerify");
	}

	/**
	 * 风控异步注册通知
	 *
	 * @param consumerNo
	 * @param phone
	 * @param blackBox
	 * @param deviceUuid
	 * @param ip
	 * @param phoneType
	 * @param networkType
	 * @param osType
	 * @param event
	 */
	public void verifyASyRegister(String consumerNo, String phone, String blackBox, String deviceUuid,
			String registerTime, String ip, String phoneType, String networkType, String osType, String event,
			String bqsBlackBox) {

		Map<String, String> map = new HashMap<String, String>();
		map.put("consumerNo", consumerNo);
		map.put("eventType", event);
		map.put("orderNo", generatorClusterNo.getRiskLoginNo(new Date()));
		JSONObject obj = new JSONObject();
		obj.put("phone", phone);
		obj.put("blackBox", blackBox);
		obj.put("bqsBlackBox", bqsBlackBox);
		obj.put("deviceUuid", deviceUuid);
		obj.put("registerResult", "1");
		obj.put("registerTime", registerTime);
		// obj.put("imei", imei);
		obj.put("ip", ip);
		obj.put("phoneType", phoneType);
		obj.put("networkType", networkType);
		obj.put("osType", osType);
		map.put("details", Base64.encodeString(JSON.toJSONString(obj)));
		map.put("signInfo", SignUtil.sign(createLinkString(map), PRIVATE_KEY));
		String url = getUrl() + "/modules/api/event/asy/register.htm";
		asyLoginService.excute(map, url, "asyRegisterVerify");
	}

	/**
	 * 判断用户是否可以使用信用支付
	 *
	 * @param riskCreditBo
	 * @param userName
	 * @param orderNo
	 * @return
	 */
	public String creditPayment(String userId, String orderNo) {
		try {
			// region 信用支付白名单
			List<AfResourceDo> afResourceList = afResourceService.getConfigByTypes("credit_test_user");
			if (afResourceList.size() > 0) {
				if (afResourceList.get(0).getValue() != null
						&& afResourceList.get(0).getValue().contains(String.valueOf(userId))) {
					return "Y";
				}
			}
			// endregion
			RiskCreditRequestBo reqBo = new RiskCreditRequestBo();
			reqBo.setConsumerNo(userId);
			reqBo.setOrderNo(orderNo);
			reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
			String url = getUrl() + "/modules/api/risk/creditPayment.htm";
			String reqResult = requestProxy.post(url, reqBo);

			logThird(reqResult, "creditPayment", reqBo);
			if (StringUtil.isBlank(reqResult)) {
				// 获取风控接口失败
				return "N";
			}
			JSONObject riskResp = JSONObject.parseObject(reqResult);
			if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getString("code"))) {
				return "Y";
			} else {
				return "N";
			}
		} catch (Exception e) {
			logger.error("creditPayment", e);
			return "N";
		}

	}

	public void applyReportNotify(String code, String data, String msg, String signInfo) {
		JSONObject obj = JSON.parseObject(data);
		String consumerNo = obj.getString("consumerNo");
		logger.info("RiskUtil.applyReportNotify consumerNo={}", consumerNo);
		AfUserAuthDo auth = new AfUserAuthDo();
		auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
		auth.setGmtZhengxin(new Date());
		auth.setZhengxinStatus(SupplyCertifyStatus.WAITQUERY.getCode());
		afUserAuthService.updateUserAuth(auth);
	}

	public void createTaskNotify(String code, String data, String msg, String signInfo) {
		JSONObject obj = JSON.parseObject(data);
		String consumerNo = obj.getString("consumerNo");
		logger.info("RiskUtil.createTaskNotify consumerNo={}", consumerNo);
		// 如果认证状态为Y，则不修改状态
		Long userId = Long.parseLong(consumerNo);
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		if(authDo != null && StringUtils.equals("Y", authDo.getZhengxinStatus())) {
			return;
		}
		AfUserAuthDo auth = new AfUserAuthDo();
		auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
		auth.setGmtZhengxin(new Date());
		auth.setZhengxinStatus(SupplyCertifyStatus.WAIT.getCode());
		afUserAuthService.updateUserAuth(auth);

	}

	/**
	 * 用户临时额度获取
	 *
	 * @param userId
	 *            用户id
	 * @return 用户临时额度, 返回0为没有额度
	 */
	public BigDecimal userTempQuota(Long userId) {
		HashMap<String, String> reqBo = new HashMap<String, String>();
		try {
			// 获取风控单号
			AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
			String cardNo = card.getCardNumber();
			String riskOrderNo = riskUtil.getOrderNo("tmqa", cardNo.substring(cardNo.length() - 4, cardNo.length()));
			// 风控申请临时额度参数
			reqBo.put("consumerNo", userId.toString());
			reqBo.put("orderNo", riskOrderNo);

			// 获取details信息
			JSONObject details = new JSONObject();
			// 现金贷最小还款时间至今天数
			Integer loanMinRepayDay = afInterimAuDao.getLoanMinRepayDay(userId);
			loanMinRepayDay = loanMinRepayDay == null ? 0 : loanMinRepayDay;
			details.put("loanMinRepayDay", loanMinRepayDay);
			// 消费分期最小还款时间至今天数
			Integer consumeMinRepayDay = afInterimAuDao.getConsumeMinRepayDay(userId);
			consumeMinRepayDay = consumeMinRepayDay == null ? 0 : consumeMinRepayDay;
			details.put("consumeMinRepayDay", consumeMinRepayDay);
			// 现金贷续借次数
			details.put("loanRenewCount", afInterimAuDao.getLoanRenewCount(userId));
			// 发生当天借款当天还款行为次数
			details.put("borrowRepayCount", afInterimAuDao.getBorrowRepayCount(userId));
			// 消费分期已出账单数
			details.put("consumeBillCount", afInterimAuDao.getConsumeBillCount(userId));
			// 累计现金贷次数 累计现金贷金额
			HashMap sumLoan = afInterimAuDao.getSumLoan(userId);
			details.put("sumLoanCount", sumLoan.get("sumLoanCount"));
			details.put("sumLoanAmount", sumLoan.get("sumLoanAmount"));
			// 累计分期次数 累计分期次数
			HashMap sumConsume = afInterimAuDao.getSumConsume(userId);
			details.put("sumConsumeCount", sumConsume.get("sumConsumeCount"));
			details.put("sumConsumeAmount", sumConsume.get("sumConsumeAmount"));

			reqBo.put("details", Base64.encodeString(JSON.toJSONString(details)));
			reqBo.put("signInfo", SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

			String url = getUrl() + "/modules/api/risk/userTempQuota.htm";
			String reqResult = requestProxy.post(url, reqBo);

			JSONObject riskResp = JSONObject.parseObject(reqResult);
			if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getString("code"))) {
				JSONObject data = JSONObject.parseObject(riskResp.getString("data"));
				return data.getBigDecimal("amount");
			} else {
				return new BigDecimal(0);
			}
		} catch (Exception e) {
			logger.error("userTempQuota", e, reqBo);
			return new BigDecimal(0);
		}
	}

	/**
	 * 提交认证信息时,数据有效性检查
	 * 
	 * @param userId
	 *            用户id
	 * @param riskScene
	 *            审核场景
	 * @return 返回过期的数据,如果异常也返回null
	 */
	public JSONObject authDataCheck(Long userId, String riskScene) {
		HashMap<String, String> reqBo = new HashMap<String, String>();
		JSONObject data = new JSONObject();// 返回结果
		try {
			// 获取风控单号
			// AfUserBankcardDo card =
			// afUserBankcardService.getUserMainBankcardByUserId(userId);
			// String cardNo = card.getCardNumber();
			// String riskOrderNo = riskUtil.getOrderNo("tmqa",
			// cardNo.substring(cardNo.length() - 4, cardNo.length()));
			// reqBo.put("orderNo", riskOrderNo);
			// 风控申请临时额度参数
			reqBo.put("consumerNo", userId.toString());
			reqBo.put("scene", riskScene);
			reqBo.put("signInfo", SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

			String url = getUrl() + "/modules/api/risk/checkData.htm";
			String reqResult = requestProxy.post(url, reqBo);
			JSONObject riskResp = JSONObject.parseObject(reqResult);
			if (riskResp != null && TRADE_RESP_SUCC.equals(riskResp.getString("code"))) {
				String failureData = riskResp.getString("data");
				if (failureData != null && !"".equals(failureData)) {
					data.put("success", "55");// 有过期数据
				} else {
					data.put("success", "0");// 无过期数据
				}
				data.put("failureData", failureData);
				return data;
			} else {
				data.put("success", "1");// 失败
				return data;
			}
		} catch (Exception e) {
			logger.error("authDataCheck", e, reqBo);
			data.put("success", "2");// 程序异常
			return data;
		}
	}

	public int taskFinishNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logger.info(createLinkString(reqBo));
		logThird(signInfo, "zhengxin taskFinishNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {// 不做任何更新
				return 0;
			}
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtZhengxin(new Date());
			AfUserAccountDo userAccountDo = afUserAccountService
					.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));

			if (StringUtil.equals("10", result)) {
				auth.setZhengxinStatus(YesNoStatus.YES.getCode());
				/*
				 * 如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，
				 * 否则不做变更。 如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度
				 */
				if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0
						|| userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
					auth.setRiskStatus(RiskStatus.YES.getCode());
					AfUserAccountDo accountDo = new AfUserAccountDo();
					accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
					accountDo.setAuAmount(au_amount);
					afUserAccountService.updateUserAccount(accountDo);
				}
				jpushService.zhengxinRiskSuccess(userAccountDo.getUserName());
			} else if (StringUtil.equals("20", result)) {// 20是认证未通过 风控返回错误
				auth.setZhengxinStatus(YesNoStatus.NO.getCode());
				jpushService.zhengxinRiskFail(userAccountDo.getUserName());
			} else if (StringUtil.equals("21", result)) {// 21是认证失败 魔蝎返回错误
				auth.setZhengxinStatus("A");
				jpushService.zhengxinRiskFault(userAccountDo.getUserName());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	public int chsiNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logger.info(createLinkString(reqBo));
		logThird(signInfo, "chsiNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if (StringUtil.equals("50", result)) {// 不做任何更新
				return 0;
			}
			String limitAmount = obj.getString("amount");
			if (StringUtil.equals(limitAmount, "") || limitAmount == null)
				limitAmount = "0";
			BigDecimal au_amount = new BigDecimal(limitAmount);

			AfUserAuthDo auth = new AfUserAuthDo();
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtChsi(new Date(System.currentTimeMillis()));
			AfUserAccountDo userAccountDo = afUserAccountService
					.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));

            if (StringUtil.equals("10", result)) {
                auth.setChsiStatus(YesNoStatus.YES.getCode());
				/*如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，否则不做变更。
				     如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度*/
                if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0 || userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
                    auth.setRiskStatus(RiskStatus.YES.getCode());
                    AfUserAccountDo accountDo = new AfUserAccountDo();
                    accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
                    accountDo.setAuAmount(au_amount);
                    afUserAccountService.updateUserAccount(accountDo);
                }
                jpushService.chsiRiskSuccess(userAccountDo.getUserName());
            } else if (StringUtil.equals("20", result)) {//20是认证未通过 风控返回错误
                auth.setChsiStatus(YesNoStatus.NO.getCode());
                jpushService.chsiRiskFail(userAccountDo.getUserName());
            } else if (StringUtil.equals("21", result)) {//21是认证失败 魔蝎返回错误
                auth.setChsiStatus("A");
                jpushService.chsiRiskFault(userAccountDo.getUserName());
            }
            return afUserAuthService.updateUserAuth(auth);
        }
        return 0;
    }
    
    public int authGxbNotify(String code, String data, String msg, String signInfo) {
        RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
        reqBo.setCode(code);
        reqBo.setData(data);
        reqBo.setMsg(msg);
        reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
        logThird(signInfo, "authGxbNotify", reqBo);
        if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
            JSONObject obj = JSON.parseObject(data);
            String consumerNo = obj.getString("consumerNo");
            String result = obj.getString("result");// 10，成功；20，失败；30，用户信息不存在；40，用户信息不符
            if (StringUtil.equals("50", result)) {
                return 0;//不做任何更新
            }
            String limitAmount = obj.getString("amount");
            if (StringUtil.equals(limitAmount, "") || limitAmount == null)
                limitAmount = "0";
            BigDecimal au_amount = new BigDecimal(limitAmount);

            AfUserAuthDo auth = new AfUserAuthDo();
            auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
            auth.setGmtFund(new Date(System.currentTimeMillis()));
            AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
            if (StringUtil.equals("10", result)) {
                auth.setFundStatus(YesNoStatus.YES.getCode());
				/*如果用户已使用的额度>0(说明有做过消费分期、并且未还或者未还完成)的用户，当已使用额度小于风控返回额度，则变更，否则不做变更。
                                                如果用户已使用的额度=0，则把用户的额度设置成分控返回的额度*/
                if (userAccountDo.getUsedAmount().compareTo(BigDecimal.ZERO) == 0 || userAccountDo.getUsedAmount().compareTo(au_amount) < 0) {
                    auth.setRiskStatus(RiskStatus.YES.getCode());
                    AfUserAccountDo accountDo = new AfUserAccountDo();
                    accountDo.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
                    accountDo.setAuAmount(au_amount);
                    afUserAccountService.updateUserAccount(accountDo);
                }
                jpushService.ecommerceRiskSuccess(userAccountDo.getUserName());
            } else if (StringUtil.equals("20", result)) {//20是认证未通过 ，风控返回错误
                auth.setEcommerceStatus(YesNoStatus.NO.getCode());
                jpushService.ecommerceRiskFail(userAccountDo.getUserName());
            } else if (StringUtil.equals("21", result)) {//21是认证失败，公信宝返回失败
                auth.setEcommerceStatus("A");
                jpushService.ecommerceRiskFault(userAccountDo.getUserName());
            }
            return afUserAuthService.updateUserAuth(auth);
        }
        return 0;
    }

	public void syncOpenId(Long userId, String openId) {
		try {
			HashMap map = new HashMap();
			map.put("consumerNo", String.valueOf(userId));
			map.put("openId", openId);
			map.put("signInfo", SignUtil.sign(createLinkString(map), PRIVATE_KEY));
			String url = getUrl() + "/modules/api/risk/updateOpenId.htm";
			requestProxy.post(url, map);
		} catch (Exception e) {
			logger.error("syncOpenId error:", e);
		}
	}

	/**
	 * 获取用户分层利率
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getRiskOriRate(Long userId, JSONObject param) {
		List<AfResourceDo> borrowConfigList = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(borrowConfigList);
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal serviceRate = bankRate.multiply(bankDouble).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6,
				RoundingMode.HALF_UP);
		BigDecimal poundageRate = new BigDecimal(rate.get("poundage").toString());

		Object poundageRateCash = bizCacheUtil.getObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId);
		if (poundageRateCash != null) {
			poundageRate = new BigDecimal(poundageRateCash.toString());
		} else {
			try {
				RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString(), param);
				String poundage = riskResp.getPoundageRate();
				if (!StringUtils.isBlank(riskResp.getPoundageRate())) {
					logger.info("comfirmBorrowCash get user poundage rate from risk: consumerNo="
							+ riskResp.getConsumerNo() + ",poundageRate=" + poundage);
					bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_RATE + userId, poundage,
							Constants.SECOND_OF_ONE_MONTH);
					bizCacheUtil.saveObject(Constants.RES_BORROW_CASH_POUNDAGE_TIME + userId, new Date(),
							Constants.SECOND_OF_ONE_MONTH);
				}
			} catch (Exception e) {
				logger.info(userId + "从风控获取分层用户额度失败：" + e);
			}
		}
		// 计算原始利率
		BigDecimal oriRate = serviceRate.add(poundageRate);
		return oriRate;
	}

	/**
	 * 获取用户分层利率
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getRiskOriRate(Long userId, JSONObject param, String borrowType) {

		BigDecimal oriRate = BigDecimal.valueOf(0.001);
		try {
			RiskVerifyRespBo riskResp = riskUtil.getUserLayRate(userId.toString(), param, borrowType);
			String poundage = riskResp.getPoundageRate();
			if(StringUtils.isBlank(poundage)) {
				poundage = "0.001";
			}
			oriRate = new BigDecimal(poundage);
		} catch (Exception e) {
			logger.info(userId + "从风控获取分层用户额度失败：" + e);
		}
		return oriRate;
	}

	public Map<String, Object> getObjectWithResourceDolist(List<AfResourceDo> list) {
		Map<String, Object> data = new HashMap<String, Object>();

		for (AfResourceDo afResourceDo : list) {
			if (StringUtils.equals(afResourceDo.getType(), AfResourceType.borrowRate.getCode())) {
				if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashRange.getCode())) {

					data.put("maxAmount", afResourceDo.getValue());
					data.put("minAmount", afResourceDo.getValue1());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.BorrowCashBaseBankDouble.getCode())) {
					data.put("bankDouble", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.BorrowCashPoundage.getCode())) {
					data.put("poundage", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.BorrowCashOverduePoundage.getCode())) {
					data.put("overduePoundage", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BaseBankRate.getCode())) {
					data.put("bankRate", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashSupuerSwitch.getCode())) {
					data.put("supuerSwitch", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashLenderForCash.getCode())) {
					data.put("lender", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashTotalAmount.getCode())) {
					data.put("amountPerDay", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(),
						AfResourceSecType.borrowCashShowNum.getCode())) {
					data.put("nums", afResourceDo.getValue());
				}
			} else {
				if (StringUtils.equals(afResourceDo.getType(), AfResourceSecType.BorrowCashDay.getCode())) {
					data.put("borrowCashDay", afResourceDo.getValue());
				}
			}
		}

		return data;

	}

	public JSONObject getPayCaptal(AfBorrowCashDo afBorrowCashDo, String scene, BigDecimal amountBorrow) {
		// 查卡号，用于调用风控接口
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(afBorrowCashDo.getUserId());
		String cardNo = card.getCardNumber();
		String orderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));

		HashMap summaryData = afBorrowDao.getUserSummaryForCapital(afBorrowCashDo.getUserId());
		BigDecimal amount1 = afBorrowLegalOrderCashService.calculateRestAmount(afBorrowCashDo.getRid());
		if (summaryData != null) {
			summaryData.put("allDays", summaryData.get("allDays") + "");
			summaryData.put("allDays1", summaryData.get("allDays1") + "");
			summaryData.put("borrowAmout", amountBorrow.toString());
			summaryData.put("over", afBorrowCashDo.getOverdueDay() > 0 ? "true" : "false");
			/*
			 * BigDecimal sumRenewalPoundage =
			 * afBorrowCashDo.getSumRenewalPoundage(); BigDecimal poundage =
			 * afBorrowCashDo.getPoundage(); BigDecimal amount =
			 * afBorrowCashDo.getAmount(); BigDecimal overdueAmount =
			 * afBorrowCashDo.getOverdueAmount(); BigDecimal sumOverdue =
			 * afBorrowCashDo.getSumOverdue(); BigDecimal rateAmount =
			 * afBorrowCashDo.getRateAmount(); BigDecimal sumRate =
			 * afBorrowCashDo.getSumRate(); BigDecimal repayAmount =
			 * afBorrowCashDo.getRepayAmount();
			 * 
			 * AfBorrowLegalOrderCashDo orderCashDo =
			 * afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(
			 * afBorrowCashDo.getRid()); /*summaryData.put("amout1",(orderCashDo
			 * != null?sumRenewalPoundage.add(poundage).add(amount).add(
			 * overdueAmount).add(sumOverdue).add(rateAmount).add(sumRate).
			 * subtract(repayAmount)
			 * :amount.add(overdueAmount).add(sumOverdue).add(rateAmount).add(
			 * sumRate).subtract(repayAmount))+"");
			 */
			summaryData.put("amout1", amount1 + "");
			summaryData.put("borrowAmout", (amountBorrow.toString()) + "");
			// summaryData.put("orderNo",orderNo);
			summaryData.put("consumerNo", afBorrowCashDo.getUserId() + "");
			summaryData.put("signInfo", SignUtil.sign(createLinkString(summaryData), PRIVATE_KEY));

		}

		String url = getUrl() + "/modules/api/xj/renew.htm";
		logger.info("summaryData 1233= " + summaryData + "url = " + url);
		String reqResult = requestProxy.post(url, summaryData);

		logThird(reqResult, "transferBorrow", summaryData);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_CAPTIL_ERROR);
		}
		JSONObject riskResp = JSONObject.parseObject(reqResult);
		if (!"100".equals(riskResp.getString("code"))) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_CAPTIL_ERROR);
		}
		double money = Double.parseDouble(riskResp.getJSONObject("data").getString("money"));
		if (money == 0||  money== amount1.doubleValue()) {
			// riskResp.getJSONObject("data").put("money",amount1+"");
			throw new FanbeiException(FanbeiExceptionCode.RISK_FORBIDDEN_ERROR);
		}
		return riskResp;
	}

	/**
	 * 回调时将af_user_auth_status中运营商过期状态改变
	 */
	public void syncOperator(Long userId) {
		List<AfUserAuthStatusDo> afUserAuthStatusDos = afUserAuthStatusService.selectAfUserAuthStatusByUserId(userId);
		if (afUserAuthStatusDos != null && afUserAuthStatusDos.size() > 0) {
			for (AfUserAuthStatusDo afUserAuthStatusDo : afUserAuthStatusDos) {
				if (afUserAuthStatusDo != null) {
					String causeReason = afUserAuthStatusDo.getCauseReason();
					if (causeReason != null && !"".equals(causeReason)) {
						JSONArray jsonArray = JSON.parseArray(causeReason);
						boolean judge = true;
						for (int i = 0; i < jsonArray.size(); i++) {
							if (judge) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String authItem = jsonObject.getString("auth");
								if ("operator".equals(authItem)) {
									jsonObject.put("status", "Y");
									jsonArray.remove(i);
									jsonArray.add(jsonObject);
									afUserAuthStatusDo.setCauseReason(jsonArray.toString());
									afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
									judge = false;
								}
							}
						}

					}
				}
			}
		}

	}

	/**
	 * 借款时间
	 *
	 * @return
	 */
	public int borrowTime(final String type) {
		Integer day;
		if (isNumeric(type)) {
			day = Integer.parseInt(type);
		} else {
			day = numberWordFormat.parse(type.toLowerCase());
		}
		return day;
	}

	/**
	 * 是否是数字字符串
	 * 
	 * @param type
	 * @return
	 */
	private boolean isNumeric(String type) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(type);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 推送公积金信息给风控
	 * @param data
	 * @param userId
	 * @return
	 */
	public RiskQuotaRespBo newFundInfoNotify(String data, String userId,String orderSn) {
		RiskQuotaRespBo riskResp = null;
		newFundNotifyReqBo reqBo = new newFundNotifyReqBo();
		reqBo.setConsumerNo(userId);
		Map<String, Object> detailsMap = Maps.newHashMap();
		detailsMap.put("source", "51fund");
		detailsMap.put("item", "fund");
		detailsMap.put("data", data);
		String details = JSONObject.toJSONString(detailsMap);
		// 生成Base64编码
		String detailsBase64 = Base64.encodeString(details);
		reqBo.setDetails(detailsBase64);
		String temp = String.valueOf(System.currentTimeMillis());
		reqBo.setOrderNo(getOrderNo("fund", temp.substring(temp.length() - 4, temp.length())));
		reqBo.setOrderSn(orderSn);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String url = getUrl() +  "/modules/api/thrid/receiveData.htm";
//		String url = "http://122.224.88.51:18080" +  "/modules/api/thrid/receiveData.htm";
		logger.info("newFundInfoNotify url = {},params = {}",url,JSONObject.toJSONString(reqBo));
		String reqResult = requestProxy.post(url, reqBo);
		logger.info("newFundInfoNotify result = {}", reqResult);
		logThird(reqResult, "newFundInfoNotify", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			logger.info("newFundInfoNotify fail ,result is null");
			throw new FanbeiException(FanbeiExceptionCode.RISK_NEWFUND_NOTIFY_ERROR);
		}
		try {
			riskResp = JSON.parseObject(reqResult, RiskQuotaRespBo.class);
			logger.info("newFundInfoNotify success");
		} catch (Exception e) {
			e.printStackTrace();
			throw new FanbeiException(FanbeiExceptionCode.RISK_RESPONSE_DATA_ERROR);
		}
		return riskResp;
	}


	/**
	 * 网银通知推送
	 * 
	 * @param code
	 * @param data
	 * @param msg
	 * @param signInfo
	 * @return
	 */
	public int onlinebankNotify(String code, String data, String msg, String signInfo) {
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logger.info(createLinkString(reqBo));
		logThird(signInfo, "onlinebankNotify", reqBo);
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			logger.info("onlinebankNotify process user account");
			AfUserAuthDo auth = new AfUserAuthDo();
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");
			auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
			auth.setGmtOnlinebank(new Date(System.currentTimeMillis()));
			if (StringUtil.equals("10", result)) {
				auth.setOnlinebankStatus(YesNoStatus.YES.getCode());
			} else {
				auth.setOnlinebankStatus(YesNoStatus.NO.getCode());
			}
			return afUserAuthService.updateUserAuth(auth);
		}
		return 0;
	}

	public int supplementAuthNotify(String code, String data, String msg, String signInfo,String scene) {

		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logger.info(createLinkString(reqBo));
		logThird(signInfo, "supplement auth notify", reqBo);
		
		logger.info("risk sign=>{}",signInfo);
		logger.info("fanbei sign=>{}",reqBo.getSignInfo());
		if (StringUtil.equals(signInfo, reqBo.getSignInfo())) {// 验签成功
			logger.info("supplement auth checksign success");
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String authItem = obj.getString("authItem");
			String orderNo = obj.getString("orderNo");
			String result = obj.getString("result");
			AuthCallbackBo callbackBo = new AuthCallbackBo(orderNo, consumerNo, authItem, result,scene);
			authCallbackManager.execute(callbackBo);
		}
		return 0;
	}

	/**
	 * 风控补充认证提额接口
	 *
	 * @param consumerNo
	 *            用户ID
	 * @return
	 */
	public RiskQuotaRespBo userSupplementQuota(String consumerNo, String[] scenes, String sceneType) {
		RiskQuotaReqBo reqBo = new RiskQuotaReqBo();
		reqBo.setConsumerNo(consumerNo);
		Map<String, Object> detailsMap = Maps.newHashMap();
		detailsMap.put("sceneType", sceneType);
		detailsMap.put("scenes", scenes);
		String details = JSONObject.toJSONString(detailsMap);
		// 生成Base64编码
		String detailsBase64 = Base64.encodeString(details);
		reqBo.setDetails(detailsBase64);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl() + "/modules/api/thrid/userSupplementQuota.htm";

		String reqResult = requestProxy.post(url, reqBo);
		logThird(reqResult, "userSupplementQuota", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_AMOUNT_ERROR);
		}

		RiskQuotaRespBo riskResp = null;
		try {
			riskResp = JSON.parseObject(reqResult, RiskQuotaRespBo.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FanbeiException(FanbeiExceptionCode.RISK_RESPONSE_DATA_ERROR);
		}
		return riskResp;
	}

	/**
	 * 冒泡补充认证提额接口
	 *
	 * @param consumerNo
	 *            用户ID
	 * @return
	 */
	public RiskQuotaRespBo userReplenishQuota(String consumerNo, String[] scenes, String sceneType) {
		RiskQuotaReqBo reqBo = new RiskQuotaReqBo();
		reqBo.setConsumerNo(consumerNo);
		Map<String, Object> detailsMap = Maps.newHashMap();
		detailsMap.put("sceneType", sceneType);
		detailsMap.put("scenes", scenes);
		String details = JSONObject.toJSONString(detailsMap);
		// 生成Base64编码
		String detailsBase64 = Base64.encodeString(details);
		reqBo.setDetails(detailsBase64);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));

		String url = getUrl()+ "/modules/api/thrid/userReplenishQuota.htm";

		String reqResult = requestProxy.post(url, reqBo);
		logThird(reqResult, "userReplenishQuota", reqBo);
		if (StringUtil.isBlank(reqResult)) {
			throw new FanbeiException(FanbeiExceptionCode.RISK_RAISE_AMOUNT_ERROR);
		}

		RiskQuotaRespBo riskResp = null;
		try {
			riskResp = JSON.parseObject(reqResult, RiskQuotaRespBo.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FanbeiException(FanbeiExceptionCode.RISK_RESPONSE_DATA_ERROR);
		}
		return riskResp;
	}

	private Boolean bankIsMaintaining(AfResourceDo assetPushResource) {
		Boolean bankIsMaintaining=false;
		if (null != assetPushResource && StringUtil.isNotBlank(assetPushResource.getValue4())) {
			String[] split = assetPushResource.getValue4().split(",");
			String maintainStart = split[0];
			String maintainEnd = split[1];
			Date maintainStartDate =DateUtil.parseDate(maintainStart,DateUtil.DATE_TIME_SHORT);
			Date gmtCreateEndDate =DateUtil.parseDate(maintainEnd,DateUtil.DATE_TIME_SHORT);
			 bankIsMaintaining = DateUtil.isBetweenDateRange(new Date(),maintainStartDate,gmtCreateEndDate);

		}
		return bankIsMaintaining;
	}

	private AfBorrowPushDo buildBorrowPush(Long rid, BigDecimal apr,BigDecimal manageFee) {
		AfBorrowPushDo borrowPush =new AfBorrowPushDo();
		Date now = new Date();
		borrowPush.setGmtCreate(now);
		borrowPush.setGmtModified(now);
		borrowPush.setBorrowId(rid);
		borrowPush.setBorrowRate(apr);
		borrowPush.setProfitRate(manageFee);
		return borrowPush;
	}
}
