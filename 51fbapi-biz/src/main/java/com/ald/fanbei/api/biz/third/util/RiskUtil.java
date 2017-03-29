package com.ald.fanbei.api.biz.third.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskModifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskOperatorRespBo;
import com.ald.fanbei.api.biz.bo.RiskRegisterReqBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyReqBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyRespBo;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.RSAUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：风控审批工具类
 * @author 何鑫 2017年3月22日 11:20:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 * 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("riskUtil")
public class RiskUtil extends AbstractThird{
	private static String url = null;
	private static String notifyHost = null;
	private static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";
	//private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDV0lcrx+AueWCs701AjdH766SoyAuZQcw3ukJRwnjGWjJp4SaRvzvmmqNKu78J1dPJ5NeMKGRBFR2zRwh3Hz/oBCEOdpEL1oNC+w1EuCVO96V0UOjMi0RBhyk+o4KP4tUlIQWmljhiPcOTdmgcY9QxKxMyO0wNYfzNwWp+ieRmawIDAQAB";
	private static String TRADE_RESP_SUCC = "0000";
	private static String CHANNEL = "51fb";
	private static String SYS_KEY = "01";
	
	@Resource
	AfUserAuthService afUserAuthService;
	
	private static String getUrl(){
		if(url==null){
			url = ConfigProperties.get(Constants.CONFKEY_RISK_URL);
			return url;
		}
		return url;
	}
	
	private static String getNotifyHost(){
		if(notifyHost==null){
			notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
			return notifyHost;
		}
		return notifyHost;
	}
	
	/**
	 * 用户信息同步
	 * @param consumerNo --用户在业务系统中的唯一标识
	 * @param realName --真实姓名
	 * @param phone --手机号
	 * @param idNo --身份证号
	 * @param email --邮箱
	 * @param alipayNo --支付宝账号
	 * @param address --地址
	 * @return
	 */
	public RiskRespBo register(String consumerNo,String realName,String phone,String idNo,
			String email,String alipayNo,String address){
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
		String reqResult = HttpUtil.httpPost(getUrl()+"/modules/api/user/register.htm", reqBo);
		logThird(reqResult, "register", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult,RiskRespBo.class);
		if(riskResp!=null && TRADE_RESP_SUCC.equals(riskResp.getCode())){
			riskResp.setSuccess(true);
			return riskResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
		}
	}
	
	/**
	 * 用户信息修改
	 * @param consumerNo --用户在业务系统中的唯一标识
	 * @param realName --真实姓名
	 * @param phone --手机号
	 * @param idNo --身份证号
	 * @param email --邮箱
	 * @param alipayNo --支付宝账号
	 * @param address --地址
	 * @return
	 */
	public RiskRespBo modify(String consumerNo,String realName,String phone,String idNo,
			String email,String alipayNo,String address,String openId){
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
		String reqResult = HttpUtil.httpPost(getUrl()+"/modules/api/user/modify.htm", reqBo);
		logThird(reqResult, "modify", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.RISK_MODIFY_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult,RiskRespBo.class);
		if(riskResp!=null && TRADE_RESP_SUCC.equals(riskResp.getCode())){
			riskResp.setSuccess(true);
			return riskResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.RISK_MODIFY_ERROR);
		}
	}
	
	/**
	 * 风控审批
	 * @param orderNo
	 * @param consumerNo
	 * @param scene
	 * @return
	 */
	public RiskVerifyRespBo verify(String consumerNo,String scene,String cardNo){
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(getOrderNo("vefy", cardNo.substring(cardNo.length()-4,cardNo.length())));
		reqBo.setConsumerNo(consumerNo);
		reqBo.setChannel(CHANNEL);
		reqBo.setScene(scene);
		JSONObject obj = new JSONObject();
		obj.put("cardNo", cardNo);
		reqBo.setDatas(Base64.encodeString(JSON.toJSONString(obj)));
		reqBo.setReqExt("");
		reqBo.setNotifyUrl(getNotifyHost()+"/third/risk/verify");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.httpPost(getUrl()+"/modules/api/risk/verify.htm", reqBo);
		logThird(reqResult, "verify", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
		RiskVerifyRespBo riskResp = JSONObject.parseObject(reqResult,RiskVerifyRespBo.class);
		if(riskResp!=null && TRADE_RESP_SUCC.equals(riskResp.getCode())){
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			riskResp.setResult(dataObj.getString("result"));
			return riskResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
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
            String value = params.get(key);
            prestr = prestr+value;
            /*if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }*/
        }

        return prestr;
    }
    
    /**
	 * 获取订单号
	 * @param method 接口标识（固定4位）
	 * @param identity 身份标识（固定4位）
	 */
	private static String getOrderNo(String method,String identity){
		if(StringUtil.isBlank(method) || method.length() != 4 || StringUtil.isBlank(identity) || identity.length() != 4){
			throw new FanbeiException(FanbeiExceptionCode.UPS_ORDERNO_BUILD_ERROR);
		}
		return StringUtil.appendStrs(SYS_KEY,method,identity,System.currentTimeMillis());
	}
	
	/**
	 * 上树运营商数据查询
	 * @param consumerNo --用户唯一标识
	 * @param userName --用户名
	 * @return
	 */
	public RiskOperatorRespBo operator(String consumerNo,String userName){
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(getOrderNo("oper", userName.substring(userName.length()-4,userName.length())));
		reqBo.setConsumerNo(consumerNo);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.httpPost(getUrl()+"/modules/api/risk/operator.htm", reqBo);
		logThird(reqResult, "operator", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.RISK_OPERATOR_ERROR);
		}
		RiskOperatorRespBo riskResp = JSONObject.parseObject(reqResult,RiskOperatorRespBo.class);
		if(riskResp!=null && TRADE_RESP_SUCC.equals(riskResp.getCode())){
			riskResp.setSuccess(true);
			JSONObject dataObj = JSON.parseObject(riskResp.getData());
			riskResp.setUrl(dataObj.getString("url"));
			return riskResp;
		}else{
			throw new FanbeiException(FanbeiExceptionCode.RISK_OPERATOR_ERROR);
		}
	}
	
	/**
	 * 上树运营商数据查询异步通知
	 * @param consumerNo --用户唯一标识
	 * @param userName --用户名
	 * @return
	 */
	public int operatorNotify(String code,String data,String msg,String signInfo){
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		logThird(signInfo,"operatorNotify", reqBo);
		if(StringUtil.equals(signInfo, reqBo.getSignInfo())){//验签成功
			JSONObject obj = JSON.parseObject(data);
			String consumerNo = obj.getString("consumerNo");
			String result = obj.getString("result");//10，成功；20，失败；30，用户信息不存在；40，用户信息不符
			if(StringUtil.equals("10", result)){
				AfUserAuthDo auth = new AfUserAuthDo();
				auth.setUserId(NumberUtil.objToLongDefault(consumerNo, 0l));
				auth.setMobileStatus(YesNoStatus.YES.getCode());
				auth.setGmtMobile(new Date());
				return afUserAuthService.updateUserAuth(auth);
			}
		}
		return 0;
	}
	
}
