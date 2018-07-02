package com.ald.fanbei.web.test.risk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.ald.fanbei.api.common.enums.AuthType;
import com.ald.fanbei.api.common.enums.RiskRaiseResult;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.web.test.common.AccountOfTester;
import com.ald.fanbei.web.test.common.BaseTest;
import com.ald.fanbei.web.test.common.HttpUtil;
import com.alibaba.fastjson.JSON;

public class RiskCallbackMockTest extends BaseTest {
	private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";

	String urlBase = "http://localhost:8080";
//	String urlBase = "http://atestapp.51fanbei.com:8080";
	
	/* 4.1.2 版本风控主动回调接口测试区 */
	@Test
	public void querySecAuthInfo() {
		String url = urlBase + "/third/risk/auth/querySecAuthInfo";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("consumerNo", AccountOfTester.朱玲玲.userId.toString());
		params.put("signInfo", SignUtil.sign(createLinkString(params), PRIVATE_KEY));
		
		System.out.println(JSON.toJSONString(params));
		String resp = HttpUtil.post(url, params);
		System.out.println(resp);
		
	}
	
	@Test
	public void syncStrongRiskByForcePush() {
		String url = urlBase + "/third/risk/auth/syncStrongRiskByForcePush";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("code", "0000");
		params.put("msg", "success");
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("result", RiskRaiseResult.PASS.getCode());
		dataMap.put("scene", SceneType.CASH.getCode());
		dataMap.put("amount", new BigDecimal(3000));
		dataMap.put("totalAmount", new BigDecimal(5000));
		dataMap.put("consumerNo", AccountOfTester.朱玲玲.userId.toString());
		dataMap.put("orderNo", "jsd88976446464645");
		
		params.put("data", JSON.toJSONString(dataMap));
		params.put("signInfo", SignUtil.sign(createLinkString(params), PRIVATE_KEY));
		
		System.out.println(JSON.toJSONString(params));
		String resp = HttpUtil.post(url, params);
		System.out.println(resp);
		
	}
	
	@Test
	public void syncSecAuthByForcePush() {
		String url = urlBase + "/third/risk/auth/syncSecAuthByForcePush";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("code", "0000");
		params.put("msg", "success");
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("scene", AuthType.ALIPAY.getCode());
		result.put("result", RiskRaiseResult.PASS.getCode());
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("results", new Map[] {result});
		dataMap.put("amount", new BigDecimal(3500));
		dataMap.put("totalAmount", new BigDecimal(5500));
		dataMap.put("consumerNo", AccountOfTester.朱玲玲.userId.toString());
		dataMap.put("orderNo", "jsd88976446464645");
		
		params.put("data", JSON.toJSONString(dataMap));
		params.put("signInfo", SignUtil.sign(createLinkString(params), PRIVATE_KEY));
		
		System.out.println(JSON.toJSONString(params));
		String resp = HttpUtil.post(url, params);
		System.out.println(resp);
		
	}
	/* 4.1.2 版本风控主动回调接口测试区 */
	
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			prestr = prestr + value;

		}
		return prestr;
	}
	
}
