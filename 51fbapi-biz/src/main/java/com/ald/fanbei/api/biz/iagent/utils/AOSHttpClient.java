package com.ald.fanbei.api.biz.iagent.utils;


import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;


/**
 * Http客户端
 * 
 * @author chailongjie
 *
 */
public class AOSHttpClient {

	private static Logger logger = Logger.getLogger(AOSHttpClient.class);
	/**
	 * 请求类型
	 */
	public static final class REQUEST_METHOD {
		public static final String POST = "POST";
		public static final String GET = "GET";
	}

	private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 7000;

	static {
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(100);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
	}

	/**
	 * 发起文件上传请求
	 * 
	 * @return
	 */
	@SuppressWarnings("all")
	public static HttpResponseVO upload(HttpRequestVO httpRequestVO) {
		HttpResponseVO httpResponseVO = new HttpResponseVO();
		//CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).
				setConnectionManager(connMgr).
				setDefaultRequestConfig(requestConfig).build();
		try {
			HttpPost httppost = new HttpPost(httpRequestVO.getUri());
			httppost.setConfig(requestConfig);

			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			// mode 和 charset组合解决上传文件名中文乱码问题
			multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE).setCharset(Charset.forName("UTF-8"));
			ContentType textContent = ContentType.create("text/plain", MIME.UTF8_CHARSET);
			Map<String, String> paramMap = httpRequestVO.getParamMap();
			if (AOSUtils.isNotEmpty(paramMap)) {
				Iterator<String> keyIterator = (Iterator) paramMap.keySet().iterator();
				while (keyIterator.hasNext()) {
					String key = (String) keyIterator.next();
					String value = paramMap.get(key);
					multipartEntityBuilder.addTextBody(key, value, textContent);
				}
			}
			Map<String, File> fileMap = httpRequestVO.getFileMap();
			if (AOSUtils.isNotEmpty(fileMap)) {
				Iterator<String> keyIteratorFileMap = (Iterator) fileMap.keySet().iterator();
				while (keyIteratorFileMap.hasNext()) {
					String key = (String) keyIteratorFileMap.next();
					FileBody fileBody = new FileBody(fileMap.get(key));
					multipartEntityBuilder.addPart(key, fileBody);
				}
			}
			HttpEntity httpEntity = multipartEntityBuilder.build();
			httppost.setEntity(httpEntity);
			CloseableHttpResponse httpResponse = null;
			try {
				httpResponse = httpClient.execute(httppost);
				int status = httpResponse.getStatusLine().getStatusCode();
				logger.info("bkl httpResponse status:" + status);
				httpResponseVO.setStatus(String.valueOf(status));
				HttpEntity entity = httpResponse.getEntity();
				String outString = entity != null ? EntityUtils.toString(entity, "utf-8") : null;
				httpResponseVO.setOut(outString);
				if (entity != null) {
					EntityUtils.consume(entity);
				}
			} finally {
				httpResponse.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return httpResponseVO;
	}

	/**
	 * 创建SSL安全连接
	 *
	 * @return
	 */
	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
		SSLConnectionSocketFactory sslsf = null;
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}

				@Override
				public void verify(String host, SSLSocket ssl) throws IOException {
				}

				@Override
				public void verify(String host, X509Certificate cert) throws SSLException {
				}

				@Override
				public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
				}
			});
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		return sslsf;
	}
}
