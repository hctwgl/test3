/*package com.ald.fanbei.web.test.risk;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;

import com.ald.fanbei.api.biz.bo.RiskOperatorNotifyReqBo;
import com.ald.fanbei.api.common.util.SignUtil;
import com.alibaba.fastjson.JSONObject;

public class RiskBaseTest {
	protected static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";

	protected static String HOST = "https://yapp.51fanbei.com";
	
	public void submitRequest(String uri,String code, String msg, Map<String, Object> dataMap) throws UnsupportedEncodingException {
		
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(HOST + uri);
		
		String data = JSONObject.toJSONString(dataMap);
		RiskOperatorNotifyReqBo reqBo = new RiskOperatorNotifyReqBo();
		reqBo.setCode(code);
		reqBo.setData(data);
		reqBo.setMsg(msg);
		String signInfo = SignUtil.sign(createLinkString(reqBo), PRIVATE_KEY);

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("code", code));
		nameValuePairs.add(new BasicNameValuePair("msg", msg));
		nameValuePairs.add(new BasicNameValuePair("data", data));

		nameValuePairs.add(new BasicNameValuePair("signInfo", signInfo));

		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			client.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Test
	public void mockServerSupplementAuth() {
		String url = "/third/risk/supplementAuth.htm";
		MockServerClient mockClient = new MockServerClient("127.0.0.1", 8089);
		mockClient
				.when(request().withPath(url).withMethod("POST")
						.withHeader("Content-Type", "application/x-www-form-urlencoded")
						.withQueryStringParameter("code", "1").withQueryStringParameter("data", "1")
						.withQueryStringParameter("msg", "1").withQueryStringParameter("signInfo", "1"))
				.respond(response().withStatusCode(200));
	}

	

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
*/