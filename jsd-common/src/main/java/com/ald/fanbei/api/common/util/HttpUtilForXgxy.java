package com.ald.fanbei.api.common.util;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 区别，增加了定制消息头
 * @see {@link HttpUtil}
 */
public class HttpUtilForXgxy {
	private static Logger log = LoggerFactory.getLogger(HttpUtilForXgxy.class);
	
	private static RequestConfig requestConfig;
	private static SSLConnectionSocketFactory sslsf;
	
	private static CloseableHttpClient httpsclient;
	private static CloseableHttpClient httpclient;
	
	static {
		try {
			requestConfig = RequestConfig.custom()
		              .setConnectTimeout(60000).setConnectionRequestTimeout(55000)
		              .setSocketTimeout(60000).setCircularRedirectsAllowed(true).build();
			
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {  
	            @Override  
	            public void checkClientTrusted(X509Certificate[] chain,  
	                    String authType) throws CertificateException {
	            }  
	            @Override  
	            public void checkServerTrusted(X509Certificate[] chain,  
	                    String authType) throws CertificateException {  
	            }  
	            @Override  
	            public X509Certificate[] getAcceptedIssuers() {  
	                return null;  
	            }  
	        };
	        ctx.init(null, new TrustManager[]{tm}, new java.security.SecureRandom());  
	        sslsf = new SSLConnectionSocketFactory(ctx);
	        
	        List<Header> headers = new ArrayList<>();
	        headers.add(new BasicHeader("content-type", "application/json"));
	        headers.add(new BasicHeader("charset", "utf-8"));
	        
	        httpsclient = HttpClients.custom().setDefaultHeaders(headers).setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslsf).build();
	        httpclient = HttpClients.custom().setDefaultHeaders(headers).setDefaultRequestConfig(requestConfig).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}  
	}
	
	public static String get(String url){
		return get(url,null);
	}
	public static String get(String url, Object data){
		return (String)get(url, data, false);
	}
	public static Object get(String url, Object data, boolean isRaw){
		Args.notBlank(url, "HTTP request url");
		
		CloseableHttpClient httpclient;
		CloseableHttpResponse resp = null;
		try {
			if(url.startsWith("https")) {
				httpclient = HttpUtilForXgxy.httpsclient;
			}else {
				httpclient = HttpUtilForXgxy.httpclient;
			}
			
        	String requestStr = null;
        	if(data == null){
        		requestStr = "";
        	}else if(data instanceof Map){
        		@SuppressWarnings("unchecked")
				Set<Entry<Object,Object>> entrys = ((Map<Object,Object>)data).entrySet();
        		List<NameValuePair> params = new ArrayList<>();
        		Object value;
            	for(Entry<Object,Object> entry : entrys){
            		params.add(new BasicNameValuePair(entry.getKey().toString(), (value = entry.getValue()) != null?value.toString():""));
            	}
            	requestStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
        	}else{
        		requestStr = data.toString();
        	}
        	
        	url = TextUtils.isBlank(requestStr)?url:url.concat((url.contains("?")?requestStr:"?".concat(requestStr)));
        	HttpGet httpget = new HttpGet(url);
            
            resp = httpclient.execute(httpget);
            if(isRaw) {
        		return EntityUtils.toByteArray(resp.getEntity());
        	}else {
        		return EntityUtils.toString(resp.getEntity());
        	}
        } catch (Exception e) {
			throw new IllegalStateException(e);
		}finally {
			try {
				if(resp != null)resp.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	
	public static String post(String url){
		return post(url,null);
	}
	public static String post(String url, Object reqData){
		return (String)post(url, reqData, reqData+"", false);
	}
	public static String post(String url, Object reqData, String reqDataForLog){
		return (String)post(url, reqData, reqDataForLog, false);
	}
	public static Object post(String url, Object reqData, String reqDataForLog, boolean isRaw){
		Args.notBlank(url, "HTTP request url");
		long start = System.currentTimeMillis();
		CloseableHttpClient httpclient;
		CloseableHttpResponse resp = null;
		Object respObj = null;
		try {
			if(url.startsWith("https")) {
				httpclient = HttpUtilForXgxy.httpsclient;
			}else {
				httpclient = HttpUtilForXgxy.httpclient;
			}
		
        	HttpEntity reqEntity = null;
        	if(reqData == null){
        		reqEntity = new StringEntity("");
        	}else if(reqData instanceof Map){
				@SuppressWarnings("unchecked")
				Set<Entry<Object,Object>> entrys = ((Map<Object,Object>)reqData).entrySet();
        		List<NameValuePair> params = new ArrayList<>();
        		Object value;
            	for(Entry<Object,Object> entry : entrys){
            		params.add(new BasicNameValuePair(entry.getKey().toString(), (value = entry.getValue()) != null?value.toString():""));
            	}
            	reqEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        	}else if(reqData instanceof byte[]){
        		reqEntity = new ByteArrayEntity((byte[])reqData);
			}else{
				reqEntity = new StringEntity(reqData.toString());
        	}
        	
        	HttpPost httppost = new HttpPost(url);
        	httppost.setEntity(reqEntity);
            
        	resp = httpclient.execute(httppost);
        	if(isRaw) {
        		respObj = EntityUtils.toByteArray(resp.getEntity());
        	}else {
        		respObj = EntityUtils.toString(resp.getEntity());
        	}
        	return respObj;
           
        } catch (Exception e) {
			throw new IllegalStateException(e);
		}finally {
			log.info("POST - " + url + ", PARAMS=" + reqDataForLog + ", RESP=" + (respObj == null? "":respObj) + ", TIME=" + (System.currentTimeMillis() - start));
			try {
				if(resp != null)resp.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
}
