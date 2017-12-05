package com.ald.fanbei;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import jiazhiyi.web.com.CommOrderQry;
import jiazhiyi.web.com.CommodityPrices;
import jiazhiyi.web.com.OrderEntity;
import jiazhiyi.web.com.OrderReceive;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.SAXReader;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.common.util.Base64;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

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

	try {
	    System.out.println(String.format("sendOrderToSup :acctType %s,businessId %s,gameAcct %s,gameName %s,gameSrv %s,gameType %s,goodsId %s,goodsNum %s,supKey %s, callback %s, userName %s,orderNo %s", 
		    "1", "2", "3", "4", "5", "6", "7", "8", "9","10", "11", "12"));
	    
	    
	    // System.out.println(CommodityPrices.QueryCommodity("Num10428",
	    // "2d477a24ec9c4d4ba65403f031cd5d9f", "wy001jk"));

	    // System.out.println(CommOrderQry.QueryOrder("Num10428", "132",
	    // "2d477a24ec9c4d4ba65403f031cd5d9f", "9.8"));

//	    System.out.println(URLEncoder.encode("QQ%E5%8F%B7", "utf-8"));
//	    System.out.println(URLDecoder.decode("QQ%E5%8F%B7", "utf-8"));
//	    System.out.println(URLDecoder.decode("QQ%E5%8F%B7", "ISO-8859-1"));
//	    System.out.println(URLDecoder.decode("QQ号", "utf-8"));
//	    System.out.println(URLDecoder.decode("QQ号", "ascii"));
	    
//	    OrderEntity orderEntity = new OrderEntity();
//	    orderEntity.setAcctType("网易通行证账号");
//	    orderEntity.setBusinessId("Num10428");
//	    orderEntity.setGameAcct("");
//	    orderEntity.setGameArea("");
//	    orderEntity.setGameName("网易");
//	    orderEntity.setGameSrv("");
//	    orderEntity.setGameType("帐号直充");
//	    orderEntity.setGoodsId("wy001jk");
//	    orderEntity.setGoodsNum(10);
//	    orderEntity.setKey("2d477a24ec9c4d4ba65403f031cd5d9f");
//	    orderEntity.setNoticeUrl("http://1it9109157.51mypc.cn:14348/game/pay/callback");
//	    orderEntity.setOrderArea("");
//	    orderEntity.setOrderIp("");
//	    orderEntity.setUserName("00000000@e2p.com");
//	    orderEntity.setUserOrderId("140");
//	    String result = OrderReceive.SendOrder(orderEntity);
//	    System.out.println(result);
//
//	    Document document = DocumentHelper.parseText(result);
//	    System.out.println(document.selectSingleNode("/root/result").getStringValue());
//	    System.out.println(document.selectSingleNode("/root/mes").getStringValue());

	} catch (Exception e) { // TODO Auto-generated
	    e.printStackTrace();
	}

	// System.out.println("Y");
	// checkFaceImage("http://f.51fanbei.com/online/668fca35fba9c82f.png",
	// "储震", "341024199606099713");
	// checkFaceImage("http://f.51fanbei.com/online/4d57c29ab87c3fbf.png",
	// "李燕红", "341227199403058014");

	// checkFaceImage("http://f.51fanbei.com/online/5d905c674ccef945.png",
	// "刘伟强", "510225197501245058");

	// checkFaceImage("http://f.51fanbei.com/online/b494b88c72460160.png",
	// "胡等坤", "500236199712034054");
	// checkFaceImage("http://f.51fanbei.com/online/8d5183ca501fe0d2.png",
	// "赵磊", "61040219841209271X");

	// checkFaceImage("http://f.51fanbei.com/online/1cc56d5e6e068fae.png",
	// "黄前城", "330326199504197917");
	// checkFaceImage("http://f.51fanbei.com/online/8deef08d73203171.png",
	// "王健", "320324197612276213");
	// checkFaceImage("http://f.51fanbei.com/online/aa6f92b43b493e31.png",
	// "李玉宝", "532130198801261717");
	// checkFaceImage("http://f.51fanbei.com/online/98f3de36a4a2641b.png",
	// "李正军", "430426199203238710");
	// checkFaceImage("http://f.51fanbei.com/online/6cb088ae186bd94a.png",
	// "于博", "610502198207041230");

	// checkFaceImage("http://f.51fanbei.com/online/ca569ba837f191a9.png",
	// "祁少波", "441900197503060878");
	// checkFaceImage("http://f.51fanbei.com/online/16b94c1eeaa11aab.png",
	// "巴桑", "542221197609100020");

	// checkAirPlane("441424199202036957","441424199202036957");

	// checkPhoneDuration("13588163648");
	// checkPhoneDuration("18801738656");
	// checkPhoneDuration("15157102876");
	// checkPhoneDuration("18067922681");
	// checkPhoneDuration("15967165359");
	// checkPhoneDuration("15268106659");
	// checkPhoneDuration("13867172134");
	// checkPhoneDuration("15068724729");
	// checkPhoneDuration("13505810747");
	// checkPhoneDuration("18637961802");
	// checkPhoneDuration("18801089151");

	// checkPhoneDuration("13682592018");
	// checkPhoneDuration("17636330654");
	// checkPhoneDuration("17681546001");
	// checkPhoneDuration("18579183626");
	// checkPhoneDuration("18873635615");
	// checkPhoneDuration("18975997303");
	// checkPhoneDuration("18996286965");
	// checkPhoneDuration("13811493733");
	// checkPhoneDuration("15840117000");

    }

    private void checkFaceImage(String faceInamgeUrl, String name, String idcard) {
	try {
	    String authUrl = "http://api.chinadatapay.com/communication/personal/2014";
	    String faceBase64 = getImage(faceInamgeUrl);
	    System.out.println(faceBase64);

	    System.out.println(name);

	    // 设置登陆时要求的信息，用户名和密码
	    NameValuePair[] data = { new NameValuePair("key", "b14e7dbe34394ed2d7f16f4d2c56aa07"), new NameValuePair("name", name), new NameValuePair("idcard", idcard), new NameValuePair("image", faceBase64) };
	    HttpClient httpClient = new HttpClient();
	    httpClient.getParams().setContentCharset("UTF-8");
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

    private void checkAirPlane(String cardno, String code) {
	try {
	    String authUrl = "http://api.chinadatapay.com/government/traffic/605";

	    // 设置登陆时要求的信息，用户名和密码
	    NameValuePair[] data = { new NameValuePair("key", "610acc78fa244ed6c2c433146fa062ad"), new NameValuePair("cardno", cardno), new NameValuePair("keys", code) };
	    HttpClient httpClient = new HttpClient();
	    httpClient.getParams().setContentCharset("UTF-8");
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

    private void checkPhoneDuration(String mobile) {
	try {
	    System.out.println(mobile);
	    String authUrl = "http://api.chinadatapay.com/communication/message/1913";
	    // String authUrl = "http://v.juhe.cn/mobileOnline/query";

	    // 设置登陆时要求的信息，用户名和密码
	    NameValuePair[] data = { new NameValuePair("key", "125dab531ddc6f1a41c682a142d0e42c"), new NameValuePair("mobile", mobile) };
	    // NameValuePair[] data = { new NameValuePair("key",
	    // "941d4966c69e506c1adff2c9c6ef6148"), new NameValuePair("mobile",
	    // mobile) };
	    HttpClient httpClient = new HttpClient();
	    httpClient.getParams().setContentCharset("UTF-8");
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

    /**
     * 从URL中读取图片,转换成流形式.
     * 
     * @param destUrl
     * @return
     */
    public String getImage(String destUrl) {

	HttpURLConnection httpUrl = null;
	URL url = null;
	InputStream input = null;
	try {
	    url = new URL(destUrl);
	    httpUrl = (HttpURLConnection) url.openConnection();
	    httpUrl.connect();
	    httpUrl.getInputStream();
	    input = httpUrl.getInputStream();

	    byte[] data = new byte[input.available()];
	    input.read(data);
	    input.close();

	    // 对字节数组Base64编码
	    return Base64.encode(data);// 返回Base64编码过的字节数组字符串
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    httpUrl.disconnect();
	}
	return null;
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
