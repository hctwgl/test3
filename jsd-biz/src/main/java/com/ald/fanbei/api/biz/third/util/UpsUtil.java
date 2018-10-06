package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.ups.UpsAuthSignReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsAuthSignValidReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsCollectBo;
import com.ald.fanbei.api.biz.bo.ups.UpsCollectReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsDelegatePayReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsQueryAuthSignReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsQueryAuthSignRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsQueryTradeReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsQueryTradeRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsQuickPayConfirmReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsQuickPayReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsResendSmsReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsResendSmsRespBo;
import com.ald.fanbei.api.biz.bo.ups.UpsSignReleaseReqBo;
import com.ald.fanbei.api.biz.bo.ups.UpsSignReleaseRespBo;
import com.ald.fanbei.api.biz.service.BeheadBorrowCashService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashReviewStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdUpsLogDao;
import com.ald.fanbei.api.dal.dao.JsdUserBankcardDao;
import com.ald.fanbei.api.dal.dao.JsdUserDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdUpsLogDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 *@类现描述：支付路由,统一支付工具
 *@author chenjinhu 2017年2月18日 下午10:09:44
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("upsUtil")
public class UpsUtil extends AbstractThird {
	
	private static String url = null;
	private static String notifyHost = null;
	
	private static String UPS_PREFFIX = ConfigProperties.get(Constants.CONFKEY_UPS_PREFFIX);
	private static String UPS_MERNO = ConfigProperties.get(Constants.CONFKEY_UPS_MERNO);

	private static String TRADE_STATUE_SUCC = "00";
	private static String TRADE_STATUE_PART_SUCC = "01"; // 部分成功 
	private static String TRADE_STATUE_ALREADY__SUCC = "02"; // 已经成功
	
	private static String TRADE_STATUE_DEAL = "20";

	private static String DEFAULT_CERT_TYPE = "01";  //默认证件类型：身份证
	private static String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALAr3ePl3Y2tQhZmQHgxuAptabWihDHZtn449lyTd3VHcH/LajNx1xgpKnEbN1R295OTZ5kzs5YPIEZsSYgqQYkhHIbDHLoJ9GoLR8vbsgxY0o0s7lSOVM3j/SF8jUz/VD6LtnkRnrmY0e6ktTl6bNkBfqeV7lAaiqcx584T2RiRAgMBAAECgYAmyMGE1qo78pTGEaTH0UpPIV9QWYL45pFCoI+8Ocrmyt99gu7SfJF6BDYPyIoZ0kcW+jCojbVPp+zXFCfsDkF27XrMgCfCPpBTlc3KFjAZZUweGkut9aWBYOS7rFkn4HrfEtXN04Y1QYqcnVGYp8BbRImnuf8kr2WP2odQ+f3A8QJBANzqGPxyRP5pREPuI9duBrA7Iv/SMB4DOCRo/O+ICCPlcvLg90RgPfnBZ+y4kBHDRngilZ6b+F046es6D1PEHo8CQQDMJpy1M6027vf2mAsqn5W1jX05o/r3iC46HJ0vmvJ/1CcfPdNlsZZh9YSpZfNo7acsgqzDvod1mLnL+x1VkubfAkB5Da5zZwp3fqdxseTh/+CaYU1kcYD8cTcqfH1dpGURhoHepXfZeAN+AIU6KkiH80GCQzFJoJ4QN0e3JjGP7T/xAkEAoXfsvFkaKHfMAetx8Y11QLqfEAcFyeCZB3d4T53TLY2kP86LtERIuEQTYFR1uEk3zzmv4caBp15bnd2I7xUYqwJAFtIEAmh3v9tIs0trMeGlkDG8RibSTqSVu6/iuCT1u7bFObKRzGZAPKtO9CDSVI4u8VC5L69jv5uDoXCLCDalOw==";
	
	//orderNo规则  4位业务码  + 4位接口码  + 11位身份标识（手机号或者身份证后11位） + 13位时间戳
    
	//调用ups接口使用（单位：分）
    public static final int KUAIJIE_EXPIRE_MINITES = 15;
    //存储redis使用（单位：秒）（数据缓存时间小于支付订单有效时间，防止订单支付失败（用户临界时间完成支付））
    public static final int KUAIJIE_EXPIRE_SECONDS = 14 * 60;
    
    public static final int KUAIJIE_ONE_MINITE_SECONDS = 60;
    
    public static final String KUAIJIE_TRADE_HEADER = "kuaijie:tradeno:req:";
    public static final String KUAIJIE_TRADE_RESPONSE_HEADER = "kuaijie:tradeno:resp:";
    public static final String KUAIJIE_TRADE_OBJECT_HEADER = "kuaijie:tradeno:resp:";
    public static final String KUAIJIE_TRADE_BEAN_ID = "kuaijie:tradeno:beanId:";

    @Resource
    GeneratorClusterNo generatorClusterNo;

	@Resource
	JsdUpsLogDao jsdUpsLogDao;
	@Resource
	JsdUserDao jsdUserDao;
	@Resource
	JsdUserBankcardDao jsdUserBankcardDao;
	@Resource
	JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
	@Resource
	JsdBorrowCashDao jsdBorrowCashDao;
	@Resource
	BeheadBorrowCashService beheadBorrowCashService;

	@Autowired
	BizCacheUtil bizCacheUtil;
	
	private static String getNotifyHost(){
		if(notifyHost==null){
			notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
			return notifyHost;
		}
		return notifyHost;
	}
	
	private static String getUpsUrl(){
		if(url==null){
			url = ConfigProperties.get(Constants.CONFKEY_UPS_URL);
			return url;
		}
		return url;
	}
	
	/**
	 * 设置公共参数
	 * @param payRoutBo
	 * @param service
	 * @param orderNo
	 * @param clientType
	 */
	private static void setPubParam(UpsReqBo payRoutBo,String service,String orderNo,String clientType){
		payRoutBo.setVersion("10");
		payRoutBo.setService(service);
		payRoutBo.setMerNo(UPS_MERNO);
		payRoutBo.setOrderNo(orderNo);
		payRoutBo.setClientType(clientType);
		payRoutBo.setMerPriv("");
		payRoutBo.setReqExt("");
	}
	
	/**
	 * 单笔代付
	 * 后台手动审批 调用
	 */
	public void manualJsdDelegatePay(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo){
		
		if(JsdBorrowCashReviewStatus.PASS.name().equals(cashDo.getReviewStatus())){
			logger.error("jsdDelegatePay is already do, borrowNo="+cashDo.getBorrowNo());
			return;
		}

		JsdUserBankcardDo mainCard = jsdUserBankcardDao.getByBankNo(cashDo.getCardNumber());
		cashDo.setReviewStatus(JsdBorrowCashReviewStatus.PASS.name());
		jsdBorrowCashDao.updateReviewStatus(JsdBorrowCashReviewStatus.PASS.name(), cashDo.getRid());
		
		autoJsdDelegatePay(cashDo, orderDo, mainCard);
	}

	/**
	 * 单笔代付
	 * 自动、半自动审批 调用
	 */
	public void autoJsdDelegatePay(final JsdBorrowCashDo cashDo, final JsdBorrowLegalOrderDo orderDo, final JsdUserBankcardDo mainCard) {
		
		final JsdUserDo userDo = jsdUserDao.getById(cashDo.getUserId());
		// 当天借款金额存入缓存
		long currAllamount = bizCacheUtil.incr(Constants.CACHEKEY_BORROW_CURRDAY_ALLAMOUNT, cashDo.getAmount().longValue(), DateUtil.getTodayLast());
		logger.info("currday borrow allamount cache : borrowNo="+cashDo.getBorrowNo()+", currAllamount+"+cashDo.getAmount().longValue()+", currAllamount="+currAllamount);
		
		new Thread() { public void run() {
        	try {
                UpsDelegatePayRespBo upsResult = jsdDelegatePay(cashDo.getArrivalAmount(), userDo.getRealName(), 
                        mainCard.getBankCardNumber(), userDo.getRid().toString(), mainCard.getMobile(),
                        mainCard.getBankName(), mainCard.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
                        PayOrderSource.JSD_LOAN_V2.getCode(), cashDo.getRid().toString(), userDo.getIdNumber());
                cashDo.setTradeNoUps(upsResult.getOrderNo());
                
                if (!upsResult.isSuccess()) {
                	beheadBorrowCashService.dealBorrowFail(cashDo, orderDo, "UPS打款实时反馈失败");
                }else {
                	cashDo.setStatus(JsdBorrowCashStatus.TRANSFERING.name());
                	jsdBorrowCashDao.updateById(cashDo);
                }
            } catch (Exception e) {
            	logger.error(e.getMessage(), e);
            	beheadBorrowCashService.dealBorrowFail(cashDo, orderDo, "UPS打款时发生异常");
            }
        }}.start();
	}
	
	/**
	 * 单笔代付
	 *
	 * @param amount 金额
	 * @param realName 真实姓名
	 * @param cardNo 银行卡号
	 * @param userNo 用户在商户的唯一标识
	 * @param phone 用户在商户的唯一标识
	 * @param bankName 银行名称
	 * @param bankCode 银行编号
	 * @param purpose 用途
	 * @param clientType 客户端类型
	 */
	public UpsDelegatePayRespBo jsdDelegatePay(BigDecimal amount,String realName,String cardNo,String userNo,
												String phone,String bankName,String bankCode,String purpose,String clientType,String merPriv,String reqExt,String idNumber){
		amount = setActualAmount(amount);
		String orderNo = getOrderNo("dpay", phone.substring(phone.length()-4,phone.length()));
		UpsDelegatePayReqBo reqBo = new UpsDelegatePayReqBo();
		setPubParam(reqBo,"delegatePay",orderNo,clientType);
		reqBo.setMerPriv(merPriv);
		reqBo.setReqExt(reqExt);
		reqBo.setAmount(amount.toString());
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setUserNo(UPS_PREFFIX + userNo);
		reqBo.setCertNo(idNumber);
		reqBo.setPhone(phone);
		reqBo.setBankName(bankName);
		reqBo.setBankCode(bankCode);
		reqBo.setPurpose(purpose);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/delegatePay");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		jsdUpsLogDao.saveRecord(buildDsedUpsLog(bankCode, cardNo, "delegatePay", orderNo, reqExt, merPriv, userNo));
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			UpsDelegatePayRespBo authSignResp = new UpsDelegatePayRespBo();
			authSignResp.setSuccess(false);
			return authSignResp;
		}
		UpsDelegatePayRespBo authSignResp = JSONObject.parseObject(reqResult,UpsDelegatePayRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null &&(StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)||StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_DEAL))){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			UpsDelegatePayRespBo authSignResp1 = new UpsDelegatePayRespBo();
			authSignResp1.setSuccess(false);
			return authSignResp1;
		}
	}

	
	/**
	 * 单笔交易查询
	 * 
	 * @param tradeType 交易类型
	 * @param tradeNo 交易订单号
	 * @param clientType 客户端类型
	 */
	public UpsQueryTradeRespBo queryTrade(String tradeType,String tradeNo,String clientType){
		String orderNo = getOrderNo("qtra", tradeNo.substring(tradeNo.length()-4,tradeNo.length()));
		UpsQueryTradeReqBo reqBo = new UpsQueryTradeReqBo();
		setPubParam(reqBo,"queryTrade",orderNo,clientType);
		reqBo.setTradeNo(tradeNo);
		reqBo.setTradeType(tradeType);
		
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.UPS_QUERY_TRADE_ERROR);
		}
		UpsQueryTradeRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryTradeRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new BizException(BizExceptionCode.BANK_CARD_PAY_ERR);
		}
	}
	
	/**
	 * 签约(包含四要素验证)
	 * 
	 * @param realName
	 * @param mobile
	 * @param idNumber
	 * @param cardNumber
	 * @param clientType
	 * @return
	 */
	public UpsAuthSignRespBo authSign(String userNo,String realName,String mobile,String idNumber,String cardNumber,String clientType,String bankCode,String cardType,
									  String validDate,String safeCode){
		String orderNo = getOrderNo("sign", mobile.substring(mobile.length()-4,mobile.length()));
		UpsAuthSignReqBo reqBo = new UpsAuthSignReqBo();
		setPubParam(reqBo,"authSign",orderNo,clientType);
		reqBo.setUserNo(UPS_PREFFIX+userNo);
		reqBo.setBankCode(bankCode);
		reqBo.setRealName(realName);
		reqBo.setPhone(mobile);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(idNumber);
		reqBo.setCardNo(cardNumber);
		reqBo.setCardType(cardType);
		reqBo.setValidDate(validDate);
		reqBo.setCvv2(safeCode);
		reqBo.setReturnUrl(getNotifyHost() + "/third/ups/authSignReturn");
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/authSignNotify");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		jsdUpsLogDao.saveRecord(buildDsedUpsLog(bankCode, cardNumber, "authSign", orderNo, "", "JSD_LOAN", userNo));
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
		
		UpsAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignRespBo.class);
		/**
		 * 关于交易状态（tradeState）
			部分成功：
 			只要有一个渠道成功就算作部分成功
 			如果成功的不是宝付，那么交易状态描述里面会带上宝付错误信息；
 			如果成功的为宝付，那么交易状态描述不会显示错误信息（防止后面如果增加多渠道时错误信息不知道显示哪一个）
			已签约：
 			用户的银行卡在当前支付路由中所有渠道中都有签约记录时返回，用于解决试运行和生产环境连同一套数据库时重复签约问题
		 */
		if(authSignResp != null && authSignResp.getTradeState()!=null && (StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)|| StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_PART_SUCC) || StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_ALREADY__SUCC))){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new BizException(BizExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
	}
	
	/**
	 * 签约短信验证
	 * @param verifyCode 短信验证
	 * @param clientType
	 */
	public UpsAuthSignValidRespBo authSignValid(String userNo,String cardNo,String verifyCode,String clientType){
		String orderNo = getOrderNo("asva", cardNo.substring(cardNo.length()-4,cardNo.length()));
		UpsAuthSignValidReqBo reqBo = new UpsAuthSignValidReqBo();
		setPubParam(reqBo,"authSignValid",orderNo,clientType);
		reqBo.setUserNo(UPS_PREFFIX+userNo);
		reqBo.setCardNo(cardNo);
		reqBo.setSmsCode(verifyCode);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/authSignValidNotify");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		jsdUpsLogDao.saveRecord(buildDsedUpsLog("", cardNo, "authSignValid", orderNo, verifyCode, "smsCode", userNo));
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
		UpsAuthSignValidRespBo authSignResp = JSONObject.parseObject(reqResult,UpsAuthSignValidRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new BizException(BizExceptionCode.UPS_AUTH_SIGN_ERROR);
		}
	}
	
	public UpsQueryAuthSignRespBo queryAuthSign(String tradeNo,String contractNo,String realName,String idNumber,String cardNo,String startDate,String endDate,String clientType){
		String orderNo = getOrderNo("qasi", tradeNo.substring(tradeNo.length()-4,tradeNo.length()));
		UpsQueryAuthSignReqBo reqBo = new UpsQueryAuthSignReqBo();
		setPubParam(reqBo,"queryAuthSign",orderNo,clientType);
		reqBo.setTradeNo(tradeNo);
		reqBo.setContractNo(contractNo);
		reqBo.setCertNo(idNumber);
		reqBo.setCardNo(cardNo);
		reqBo.setStartDate(startDate);
		reqBo.setEndDate(endDate);
		
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.UPS_QUERY_AUTH_SIGN_ERROR);
		}
		UpsQueryAuthSignRespBo authSignResp = JSONObject.parseObject(reqResult,UpsQueryAuthSignRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && StringUtil.equals(authSignResp.getTradeState(), TRADE_STATUE_SUCC)){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new BizException(BizExceptionCode.BANK_CARD_PAY_ERR);
		}
		
	}

	/**
	 * 单笔代收
	 * 
	 * @param amount --交易金额
	 * @param userNo --用户唯一标识
	 * @param realName --真实姓名
	 * @param phone  --预留手机号
	 * @param bankCode --银行代码
	 * @param cardNo --银行卡号
	 * @param certNo --身份证号
	 * @param purpose --用途
	 * @param remark --
	 * @param clientType
	 */
    public UpsCollectRespBo collect(String orderNo, BigDecimal amount, String userNo, String realName, String phone, String bankCode, String cardNo, String certNo, String purpose, String remark, String clientType, String merPriv) {
        amount = setActualAmount(amount);
		UpsCollectReqBo reqBo = new UpsCollectReqBo();
		setPubParam(reqBo,"collect",orderNo,clientType);
		reqBo.setMerPriv(merPriv);
		reqBo.setAmount(amount.toString());
		reqBo.setUserNo(UPS_PREFFIX + userNo);
		reqBo.setRealName(realName);
		reqBo.setPhone(phone);
		reqBo.setBankCode(bankCode);
		reqBo.setCardNo(cardNo);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(certNo);
		reqBo.setPurpose(purpose);
		reqBo.setRemark(remark.trim());
		reqBo.setReturnUrl("");
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/collect");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
			jsdUpsLogDao.saveRecord(buildDsedUpsLog(bankCode, cardNo, "collect", orderNo, "", merPriv, userNo));
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.UPS_COLLECT_ERROR);
		}
		UpsCollectRespBo authSignResp = JSONObject.parseObject(reqResult,UpsCollectRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && (
				TRADE_STATUE_SUCC.equals(authSignResp.getTradeState())||TRADE_STATUE_DEAL.equals(authSignResp.getTradeState()))){
			authSignResp.setSuccess(true);
			return authSignResp;
		} else {
			authSignResp.setSuccess(false);
			return authSignResp;
		}
    }
		
	/**
	 * 快捷支付
	 * 
	 * @param amount --交易金额
	 * @param userNo --用户唯一标识
	 * @param realName --真实姓名
	 * @param phone  --预留手机号
	 * @param bankCode --银行代码
	 * @param cardNo --银行卡号
	 * @param certNo --身份证号
	 * @param purpose --用途
	 * @param remark --
	 * @param clientType
	 */
	public UpsCollectRespBo quickPay(String orderNo,BigDecimal amount,String userNo,String realName,String phone,String bankCode,
			String cardNo,String certNo,String purpose,String remark,String clientType,String merPriv,String productName,String safeCode ,String validDate){
		amount = setActualAmount(amount);
		UpsQuickPayReqBo reqBo = new UpsQuickPayReqBo();
		setPubParam(reqBo,"quickPay",orderNo,clientType);
		reqBo.setMerPriv(merPriv);
		reqBo.setAmount(amount.toString());
		reqBo.setUserNo(UPS_PREFFIX + userNo);
		reqBo.setPhone(phone);
		reqBo.setRealName(realName);
		reqBo.setCardNo(cardNo);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(certNo);
		reqBo.setProductName(productName);
		reqBo.setExpiredTime(String.valueOf( KUAIJIE_EXPIRE_MINITES));		
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/collect");
		reqBo.setCvv2(safeCode);
		reqBo.setValidDate(validDate);
		reqBo.setSmsFlag("1");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		jsdUpsLogDao.saveRecord(buildDsedUpsLog(bankCode, cardNo, "quickPay", orderNo, "", merPriv, userNo));
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.UPS_COLLECT_ERROR);
		}
		UpsCollectRespBo authSignResp = JSONObject.parseObject(reqResult,UpsCollectRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && (
				TRADE_STATUE_SUCC.equals(authSignResp.getTradeState())||TRADE_STATUE_DEAL.equals(authSignResp.getTradeState()))){
			authSignResp.setSuccess(true);
			return authSignResp;
		} else {
			authSignResp.setSuccess(false);
			return authSignResp;
		}
	}
	
	/**
	 * 短信重发
	 *
	 * @param orderNo -- 订单编号
	 */
    public UpsResendSmsRespBo quickPayResendSms(String payTradeNo,String orderNo) {
    	Object cacheObject = bizCacheUtil.getObject(UpsUtil.KUAIJIE_TRADE_HEADER + payTradeNo);
    	if (cacheObject != null) {
    	    UpsCollectBo upsCollectBo =  JSON.parseObject(cacheObject.toString(), UpsCollectBo.class);;
    	    UpsResendSmsReqBo reqBo = new UpsResendSmsReqBo();
    	    setPubParam(reqBo, "quickPayResendCode", orderNo, upsCollectBo.getClientType());
    	    reqBo.setOldOrderNo(payTradeNo);
    	    reqBo.setTradeType("pay_order");
    	    reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
			jsdUpsLogDao.saveRecord(buildDsedUpsLog(upsCollectBo.getBankCode(), upsCollectBo.getCardNo(), "quickPayResendCode", payTradeNo, "", upsCollectBo.getMerPriv(), upsCollectBo.getUserNo()));
    	    String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
    	    if (StringUtil.isBlank(reqResult)) {
    		throw new BizException(BizExceptionCode.UPS_QUICKPAY_RESEND_CODE_ERROR);
    	    }
    	    
    	    UpsResendSmsRespBo authSignResp = JSONObject.parseObject(reqResult, UpsResendSmsRespBo.class);
    	    if (authSignResp != null && authSignResp.getTradeState() != null && (TRADE_STATUE_SUCC.equals(authSignResp.getTradeState()) || TRADE_STATUE_DEAL.equals(authSignResp.getTradeState()))) {
    		authSignResp.setSuccess(true);
    		return authSignResp;
    	    } else {
    		authSignResp.setSuccess(false);
    		return authSignResp;
    	    }
    	} else {
    	    throw new BizException(BizExceptionCode.UPS_CACHE_EXPIRE);
    	}
    }
	
	
	/**
	 * 快捷支付确认支付
	 * 
	 */
	public UpsCollectRespBo quickPayConfirm(String oldTradeNo,String userNo,String smsCode,String clientType, String merPriv){
		String tradeNo = generatorClusterNo.getLoanNo(new Date());
		UpsQuickPayConfirmReqBo reqBo = new UpsQuickPayConfirmReqBo();
		setPubParam(reqBo,"quickPayConfirm",tradeNo,clientType);
		reqBo.setOldOrderNo(oldTradeNo);
		reqBo.setSmsCode(smsCode);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		jsdUpsLogDao.saveRecord(buildDsedUpsLog("", "", "quickPayConfirm", tradeNo, "", merPriv, userNo));
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.UPS_QUICK_PAY_CONFIRM_ERROR);
		}
		UpsCollectRespBo authSignResp = JSONObject.parseObject(reqResult,UpsCollectRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null && (
				TRADE_STATUE_SUCC.equals(authSignResp.getTradeState())||TRADE_STATUE_DEAL.equals(authSignResp.getTradeState()))){
			authSignResp.setSuccess(true);
			return authSignResp;
		} else {
			authSignResp.setSuccess(false);
			return authSignResp;
		}
	}

	public UpsSignReleaseRespBo signRelease(String userNo,String bankCode,String realName,String phone,
			String certNo,String cardNo,String clientType){
		String orderNo = getOrderNo("sire", phone.substring(phone.length()-4,phone.length()));
		UpsSignReleaseReqBo reqBo = new UpsSignReleaseReqBo();
		setPubParam(reqBo,"signRelease",orderNo,clientType);
		reqBo.setUserNo(UPS_PREFFIX + userNo);
		reqBo.setBankCode(bankCode);
		reqBo.setRealName(realName);
		reqBo.setPhone(phone);
		reqBo.setCertType(DEFAULT_CERT_TYPE);
		reqBo.setCertNo(certNo);
		reqBo.setCardNo(cardNo);
		reqBo.setNotifyUrl(getNotifyHost() + "/third/ups/signRelease");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		jsdUpsLogDao.saveRecord(buildDsedUpsLog(bankCode, cardNo, "signRelease", orderNo, "", "", userNo));
		String reqResult = HttpUtil.post(getUpsUrl(), reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new BizException(BizExceptionCode.SIGN_RELEASE_ERROR);
		}
		UpsSignReleaseRespBo authSignResp = JSONObject.parseObject(reqResult,UpsSignReleaseRespBo.class);
		if(authSignResp != null && authSignResp.getTradeState()!=null  && TRADE_STATUE_SUCC.equals(authSignResp.getTradeState())){
			authSignResp.setSuccess(true);
			return authSignResp;
		}else{
			throw new BizException(BizExceptionCode.SIGN_RELEASE_ERROR);
		}
	}
	
	/**
	 * 获取订单号
	 * @param method 接口标识（固定4位）
	 * @param identity 身份标识（固定4位）
	 */
	private static String getOrderNo(String method,String identity){
		if(StringUtil.isBlank(method) || method.length() != 4 || StringUtil.isBlank(identity) || identity.length() != 4){
			throw new BizException(BizExceptionCode.UPS_ORDERNO_BUILD_ERROR);
		}
		if(StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE),
				Constants.INVELOMENT_TYPE_TEST)){
			return StringUtil.appendStrs(UPS_PREFFIX, method,identity,"t" + (System.currentTimeMillis()+"").substring(4));
		}
		return StringUtil.appendStrs(UPS_PREFFIX, method,identity,System.currentTimeMillis());
	}
	
	private static BigDecimal setActualAmount(BigDecimal amount){
		if(StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_TEST)){
			amount = amount.setScale(2,BigDecimal.ROUND_HALF_UP);
		}else{
			amount = amount.setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		return amount;
	}

	/** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            if(params.get(key)!=null) {
				String value = params.get(key);
				prestr = prestr + value;
            /*if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }*/
			}
        }
		prestr = prestr.replaceAll(" ", "");
        return prestr;
    }

	private static JsdUpsLogDo buildDsedUpsLog(String bankCode, String cardNumber, String name, String orderNo, String refId, String type, String userNo){
		JsdUpsLogDo log = new JsdUpsLogDo();
		log.setBankCode(bankCode);
		log.setBankCardNumber(cardNumber);
		log.setName(name);
		log.setOrderNo(orderNo);
		log.setRefId(refId);
		log.setType(type);
		log.setUserId(NumberUtil.objToLongDefault(userNo, 0l));
		return log;
	}
}
