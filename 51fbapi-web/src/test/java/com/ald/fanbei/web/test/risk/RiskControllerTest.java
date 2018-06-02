/*package com.ald.fanbei.web.test.risk;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.mockserver.client.server.MockServerClient;

import com.google.common.collect.Maps;

public class RiskControllerTest extends RiskBaseTest{

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

	
	
	@Test
	public void testSupplementAuth() throws ClientProtocolException, IOException {

		String uri = "/third/risk/supplementAuth.htm";
		String code = "0000";
		String msg = "success";
		Map<String,Object> dataMap = Maps.newHashMap();
		dataMap.put("orderNo", "01zxin40151520324958729");
		dataMap.put("consumerNo", "18637963078");
		dataMap.put("authItem", "zhengxin");
		dataMap.put("result", "10");
		submitRequest(uri, code, msg, dataMap);
	}
	
	
	@Test
	public void testBldCallback() throws ClientProtocolException, IOException {

		String uri = "/third/risk/dredgeWhiteCollarLoan.htm";
		String code = "0000";
		String msg = "请求成功";
		Map<String,Object> dataMap = Maps.newHashMap();
		dataMap.put("amount", "1000");
		dataMap.put("consumerNo", "3847534");
		dataMap.put("offlineAmount", "0");
		dataMap.put("onlineAmount", "5000");
		dataMap.put("orderNo", "01loan21281520478986713");
		dataMap.put("result", "30");
		dataMap.put("scene", "23");
		dataMap.put("totalAmount", "1000");
		dataMap.put("whiteCollarAmount", "0");
		submitRequest(uri, code, msg, dataMap);
	}
	
}
*/