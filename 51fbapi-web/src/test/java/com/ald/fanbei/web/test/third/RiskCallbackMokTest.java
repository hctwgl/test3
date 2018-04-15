package com.ald.fanbei.web.test.third;

import org.junit.Test;

import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.web.test.common.AccountOfTester;
import com.ald.fanbei.web.test.common.BaseTest;

public class RiskCallbackMokTest extends BaseTest {
	String urlBase = "http://localhost:8080";
//	String urlBase = "http://atestapp.51fanbei.com:8080";
	
	/* 4.1.2 版本风控主动回调接口测试区 */
	@Test
	public void querySecAuthInfo() {
		String url = urlBase + "/third/ups/querySecAuthInfo?";
		String orderNo = "jsd2316453216546546";
		String reqExt = "154";
		
		String reqStr = "orderNo=" + orderNo + "&merPriv=" + merPriv + "&tradeState=" + tradeState +"&reqExt="+reqExt;
		url += reqStr;
		
		testH5(url, null, AccountOfTester.朱玲玲.mobile ,true);
	}
	
	/* 4.1.2 版本风控主动回调接口测试区 */
	
}
