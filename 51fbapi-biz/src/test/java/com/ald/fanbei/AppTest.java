package com.ald.fanbei;

import com.ald.fanbei.AppTest;
import com.alibaba.fastjson.JSON;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
		    //username=system&
		    //password=alapay2017

		    //dnsUrl=https://yupsadmin.51fanbei.com&orderNos=hq20171002205034152581

		// 登陆 Url
	            String loginUrl = "https://yupsadmin.51fanbei.com/system/user/login.htm";
	            // 需登陆后访问的 Url
	            String dataUrl = "https://yupsadmin.51fanbei.com/modules/manage/tpp/batch/query/trade.htm?dnsUrl=https://yupsadmin.51fanbei.com&orderNos=hq20171002205034152581,hq20171002205034152581";

	            HttpClient httpClient = new HttpClient();
	            // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
	            PostMethod postMethod = new PostMethod(loginUrl);

	            // 设置登陆时要求的信息，用户名和密码
	            NameValuePair[] data = { new NameValuePair("username", "gaojibin"),
	                    new NameValuePair("password", "123456") };
	            postMethod.setRequestBody(data);
	            try {
	                // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
	                httpClient.getParams().setCookiePolicy(
	                        CookiePolicy.BROWSER_COMPATIBILITY);
	                httpClient.executeMethod(postMethod);
	                // 获得登陆后的 Cookie
	                Cookie[] cookies = httpClient.getState().getCookies();
	                StringBuffer tmpcookies = new StringBuffer();
	                for (Cookie c : cookies) {
	                    tmpcookies.append(c.toString() + ";");
	                }

	                // 进行登陆后的操作
	                GetMethod getMethod = new GetMethod(dataUrl);
	                // 每次访问需授权的网址时需带上前面的 cookie 作为通行证
	                getMethod.setRequestHeader("cookie", tmpcookies.toString());
	                httpClient.executeMethod(getMethod);
	                // 打印出返回数据，检验一下是否成功
	                String text = getMethod.getResponseBodyAsString();
	                System.out.println(text);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }


	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Rigourous Test :-)
	 */
	public void testApp() {
		assertTrue(true);
	}
	
	public static void main(String[] args) {
		System.out.println(("wef:afwe:saofe".split(":")).length);
	}
}
