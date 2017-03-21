package com.ald.fanbei.api.biz.third.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRegisterReqBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.RiskVerifyReqBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：开心果手机充值工具类
 * @author 何鑫 2017年2月16日 11:20:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("riskUtil")
public class RiskUtil extends AbstractThird{
	private static String url = null;
	private static String notifyHost = null;
	private static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";
	private static String TRADE_RESP_SUCC = "0000";
	
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
	public static RiskRespBo register(String consumerNo,String realName,String phone,String idNo,
			String email,String alipayNo,String address){
		RiskRegisterReqBo reqBo = new RiskRegisterReqBo();
		reqBo.setConsumerNo(consumerNo);
		reqBo.setRealName(realName);
		reqBo.setPhone(phone);
		reqBo.setIdNo(idNo);
		reqBo.setEmail(email);
		reqBo.setAlipayNo(alipayNo);
		reqBo.setAddress(address);
		reqBo.setChannel("");
		reqBo.setReqExt("");
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.httpPost(url, reqBo);
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
	
	public static RiskRespBo verify(String orderNo,String consumerNo,String scene){
		RiskVerifyReqBo reqBo = new RiskVerifyReqBo();
		reqBo.setOrderNo(orderNo);
		reqBo.setConsumerNo(consumerNo);
		reqBo.setChannel("");
		reqBo.setScene(scene);
		reqBo.setDatas("");
		reqBo.setReqExt("");
		reqBo.setNotifyUrl(getNotifyHost());
		reqBo.setSignInfo(SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY));
		String reqResult = HttpUtil.httpPost(url, reqBo);
		logThird(reqResult, "verify", reqBo);
		if(StringUtil.isBlank(reqResult)){
			throw new FanbeiException(FanbeiExceptionCode.RISK_VERIFY_ERROR);
		}
		RiskRespBo riskResp = JSONObject.parseObject(reqResult,RiskRespBo.class);
		if(riskResp!=null && TRADE_RESP_SUCC.equals(riskResp.getCode())){
			riskResp.setSuccess(true);
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
}
