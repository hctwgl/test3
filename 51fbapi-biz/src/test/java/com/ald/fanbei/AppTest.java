package com.ald.fanbei;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.ald.fanbei.api.common.util.Base64;

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

	checkFaceImage("http://f.51fanbei.com/online/ae2968b3b4a49488.png", "肖柯表", "440923198903160550");
    }

    private void checkFaceImage(String faceInamgeUrl, String name, String idcard) {
	try {
	    String authUrl = "http://api.chinadatapay.com/communication/personal/2014";
	    InputStream inputStream = getImage(faceInamgeUrl);
	    String faceBase64 = GetImageStrByInPut(inputStream);
	    // System.out.println(faceBase64);

	    // 设置登陆时要求的信息，用户名和密码
	    NameValuePair[] data = { new NameValuePair("key", "b14e7dbe34394ed2d7f16f4d2c56aa07"), new NameValuePair("name", name), new NameValuePair("idcard", idcard), new NameValuePair("image", faceBase64) };
	    HttpClient httpClient = new HttpClient();
	    // 模拟登陆，按实际服务器端要求选用 Post 或 Get 请求方式
	    PostMethod postMethod = new PostMethod(authUrl);

	    postMethod.setRequestBody(data);
	    System.out.println(httpClient.executeMethod(postMethod));

	    // 打印出返回数据，检验一下是否成功
	    String text = postMethod.getResponseBodyAsString();
	    System.out.println(text);

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private HttpURLConnection httpUrl = null;

    /**
     * 从URL中读取图片,转换成流形式.
     * 
     * @param destUrl
     * @return
     */
    public InputStream getImage(String destUrl) {

	URL url = null;
	InputStream in = null;
	try {
	    url = new URL(destUrl);
	    httpUrl = (HttpURLConnection) url.openConnection();
	    httpUrl.connect();
	    httpUrl.getInputStream();
	    in = httpUrl.getInputStream();
	    return in;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 读取输入流,转换为Base64字符串
     * 
     * @param input
     * @return
     */
    public String GetImageStrByInPut(InputStream input) {
	byte[] data = null;
	// 读取图片字节数组
	try {
	    data = new byte[input.available()];
	    input.read(data);
	    input.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	// 对字节数组Base64编码
	return Base64.encode(data);// 返回Base64编码过的字节数组字符串
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
