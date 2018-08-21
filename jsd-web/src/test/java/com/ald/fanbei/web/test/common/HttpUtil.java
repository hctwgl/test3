package com.ald.fanbei.web.test.common;

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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于apache httpclient包，支持http1.1
 * 
 * @author ZJF
 * @since 1.8
 */
public class HttpUtil {
	
	private static Logger log = LoggerFactory.getLogger(HttpUtil.class);
	
	private static RequestConfig requestConfig;
	private static SSLConnectionSocketFactory sslsf;
	
	private static CloseableHttpClient httpsclient;
	private static CloseableHttpClient httpclient;
	
	static {
		try {
			requestConfig = RequestConfig.custom()  
		              .setConnectTimeout(3000).setConnectionRequestTimeout(1000)  
		              .setSocketTimeout(3000).setCircularRedirectsAllowed(true).build();
			
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
	        
	        httpsclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).setSSLSocketFactory(sslsf).build();
	        httpclient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
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
		
		CloseableHttpClient httpclient = null;
		try {
			if(url.startsWith("https")) {
				httpclient = HttpUtil.httpsclient;
			}else {
				httpclient = HttpUtil.httpclient;
			}
			
        	String requestStr = null;
        	if(data == null){
        		requestStr = "";
        	}else if(data instanceof Map){
        		@SuppressWarnings("unchecked")
				Set<Entry<Object,Object>> entrys = ((Map<Object,Object>)data).entrySet();
        		List<NameValuePair> params = new ArrayList<>();
            	for(Entry<Object,Object> entry : entrys){
            		params.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
            	}
            	requestStr = EntityUtils.toString(new UrlEncodedFormEntity(params, Consts.UTF_8));
        	}else{
        		requestStr = data.toString();
        	}
        	
        	url = TextUtils.isBlank(requestStr)?url:url.concat((url.contains("?")?requestStr:"?".concat(requestStr)));
        	HttpGet httpget = new HttpGet(url);
            
            CloseableHttpResponse resp = httpclient.execute(httpget);
            if(isRaw) {
        		return EntityUtils.toByteArray(resp.getEntity());
        	}else {
        		return EntityUtils.toString(resp.getEntity());
        	}
        } catch (Exception e) {
        	// TODO 捕获因超时的报错
        	log.error(e.getMessage(), e);
			throw new IllegalStateException(e);
		}
	}
	
	public static String pget(String url){
		// TODO
		return null;
	}
	
	
	public static String post(String url){
		return post(url,null);
	}
	public static String post(String url, Object data){
		return (String)post(url, data, false);
	}
	/**
	 * @param url 
	 * @param data 支持 Map 类型，String类型， byte[]数组
	 * @param responseIsRaw 指示响应结果是否是byte[]数组，否则为String
	 * @return
	 */
	public static Object post(String url, Object data, boolean responseIsRaw){
		Args.notBlank(url, "HTTP request url");
		
		CloseableHttpClient httpclient = null;
		try {
			if(url.startsWith("https")) {
				httpclient = HttpUtil.httpsclient;
			}else {
				httpclient = HttpUtil.httpclient;
			}
		
        	HttpEntity reqEntity = null;
        	if(data == null){
        		reqEntity = new StringEntity("");
        	}else if(data instanceof Map){
				@SuppressWarnings("unchecked")
				Set<Entry<Object,Object>> entrys = ((Map<Object,Object>)data).entrySet();
        		List<NameValuePair> params = new ArrayList<>();
            	for(Entry<Object,Object> entry : entrys){
            		params.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
            	}
            	reqEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        	}else if(data instanceof byte[]){
        		reqEntity = new ByteArrayEntity((byte[])data);
			}else{
				reqEntity = new StringEntity(data.toString());
        	}
        	
        	HttpPost httppost = new HttpPost(url);
        	httppost.setEntity(reqEntity);
            
        	CloseableHttpResponse resp = httpclient.execute(httppost);
        	if(responseIsRaw) {
        		return EntityUtils.toByteArray(resp.getEntity());
        	}else {
        		return EntityUtils.toString(resp.getEntity());
        	}
           
        } catch (Exception e) {
        	// TODO 捕获因超时的报错
        	log.error(e.getMessage(), e);
			throw new IllegalStateException(e);
		}
	}
	
	public static String ppost(String url){
		// TODO
		return null;
	}
	
}
