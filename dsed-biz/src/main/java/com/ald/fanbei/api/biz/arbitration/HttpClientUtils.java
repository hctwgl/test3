package com.ald.fanbei.api.biz.arbitration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http请求类
 * 
 * @Title： 
 * @Author： alvin.wyh
 * @Date： 2017年3月10日 上午8:11:38
 * @Description：
 */
public class HttpClientUtils {

	/**
	 * 发送json流数据
	 * @param url
	 * @param jsonParam
	 * @return
	 * @throws Exception
	 */
	public static String postWithJSON(String url, String jsonParam) throws Exception {
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient client = HttpClients.createDefault();
		String respContent = null;
		StringEntity entity = new StringEntity(jsonParam,"utf-8");//解决中文乱码问题
		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);
		HttpResponse resp = client.execute(httpPost);
		if(resp.getStatusLine().getStatusCode() == 200) {
			HttpEntity he = resp.getEntity();
			respContent = EntityUtils.toString(he,"UTF-8");
		}
		return respContent;
	}
	
	public static String postWithString(String url, String stringParam) throws Exception {
    	
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient client = HttpClients.createDefault();
		String respContent = null;
		StringEntity entity = new StringEntity(stringParam,"utf-8");//解决中文乱码问题
		entity.setContentEncoding("UTF-8");
		entity.setContentType("text");
		httpPost.setEntity(entity);
		HttpResponse resp = client.execute(httpPost);
		if(resp.getStatusLine().getStatusCode() == 200) {
			HttpEntity he = resp.getEntity();
			respContent = EntityUtils.toString(he,"UTF-8");
		}
		return respContent;
    }
	
}