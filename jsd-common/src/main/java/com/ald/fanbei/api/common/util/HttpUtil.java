package com.ald.fanbei.api.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.*;

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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于apache httpclient包，支持http1.1
 * httpClient默认的ResponseHandler会自动处理所有http协议下状态码，例如302自动跳转。也会自动编码响应字节流，包括解压报文。
 * @attention 此类的任何属性运行时不可变，方可保证线程安全
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

	/**
	 * 发送HTTPS的POST请求，并且忽略证书验证,将参数放置到BODY里边
	 *
	 * @param urlString
	 * @param query
	 * @return
	 */
	public static String doHttpsPostIgnoreCert(String urlString, String query) {

		ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);
		try {
			URL url = new URL(urlString);
            /*
             * use ignore host name verifier
             */
			HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

			// Prepare SSL Context
			TrustManager[] tm = { ignoreCertificationTrustManger };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 设置doOutput属性为true表示将使用此urlConnection写入数据
			connection.setDoOutput(true);
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			connection.setSSLSocketFactory(ssf);

			OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
			// 把数据写入请求的Body
			out.write(query);
			out.flush();
			out.close();

			InputStream reader = connection.getInputStream();
			byte[] bytes = new byte[512];
			int length = reader.read(bytes);

			do {
				buffer.write(bytes, 0, length);
				length = reader.read(bytes);
			} while (length > 0);

			reader.close();
			connection.disconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("error = ",ex);
		} finally {
		}
		String repString = new String(buffer.toByteArray());
		return repString;
	}

	/**
	 * 忽视证书HostName
	 */
	private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {

		public boolean verify(String s, SSLSession sslsession) {
			log.debug("WARNING: Hostname is not matched for cert.");
			return true;
		}
	};


	/**
	 * Ignore Certification
	 */
	private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

		private X509Certificate[] certificates;

		public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = certificates;
				log.debug("init at checkClientTrusted");
			}
		}

		public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
			if (this.certificates == null) {
				this.certificates = ax509certificate;
				log.debug("init at checkServerTrusted");
			}
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};






	public static String post(String url){
		return post(url,null);
	}
	public static String post(String url, Object reqData){
		return (String)post(url, reqData, false);
	}
	public static Object post(String url, Object reqData, boolean isRaw){
		Args.notBlank(url, "HTTP request url");
		long start = System.currentTimeMillis();
		CloseableHttpClient httpclient;
		CloseableHttpResponse resp = null;
		Object respObj = null;
		try {
			if(url.startsWith("https")) {
				httpclient = HttpUtil.httpsclient;
			}else {
				httpclient = HttpUtil.httpclient;
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
			log.info("POST - " + url + ", PARAMS=" + reqData + ", RESP=" + (respObj == null? "":respObj) + ", TIME=" + (System.currentTimeMillis() - start));
			try {
				if(resp != null)resp.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
}
