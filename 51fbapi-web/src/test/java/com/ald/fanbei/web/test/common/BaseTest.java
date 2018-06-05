package com.ald.fanbei.web.test.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



/**
 * @类描述：单元测试基类
 * @author zjf 2017年9月26日
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class BaseTest {
	
	private TokenBo tokenBo;
	
	/**
	 * 自动向服务端注入登陆令牌
	 */
	public TokenBo init(String userName) {
		String token = UserUtil.generateToken(userName);
		tokenBo = new TokenBo();
		tokenBo.setLastAccess(System.currentTimeMillis() + "");
		tokenBo.setToken(token);
		tokenBo.setUserId(userName);
		RedisClient.hmset(Constants.CACHEKEY_USER_TOKEN+userName, tokenBo.getTokenMap());
		return tokenBo;
	}
	
    protected void testH5(String urlString, Map<String,String> params, String userName, boolean needLogin){
    	testH5(urlString, params, userName, needLogin, this.tokenBo);
    }
    
	/**
	 * @param urlString
	 * @param params
	 * @param userName
	 * @param needLogin 参考web-main.xml中的beforeLogin标签
	 */
    protected void testApi(String urlString, Map<String,String> params, String userName, boolean needLogin){
    	Map<String,String> header = createBaseHeader();
    	header.put(Constants.REQ_SYS_NODE_USERNAME, userName);
    	
    	String signStrPrefix = createSignPrefix(header);
    	if(needLogin) {
    		signStrPrefix += tokenBo.getToken();
    	}
    	String sign = sign(params, signStrPrefix);
    	header.put(Constants.REQ_SYS_NODE_SIGN, sign);
    	
		try {
			httpPost(urlString, JSONObject.toJSONString(params), header, "api");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
	 * @param urlString
	 * @param params
	 * @param userName
	 * @param needLogin 参考web-main.xml中的beforeLogin标签
     * @throws UnsupportedEncodingException 
	 */
    protected void testH5(String urlString, Map<String,String> params, String userName, boolean needLogin, TokenBo tokenBo){
    	try {
	    	Map<String,String> header = createBaseHeader();
	    	header.put(Constants.REQ_SYS_NODE_USERNAME, userName);
	    	
	    	String signStrPrefix = createSignPrefix(header);
	    	if(needLogin) {
	    		signStrPrefix += tokenBo.getToken();
	    	}
	    	String sign = sign(null, signStrPrefix);
	    	header.put(Constants.REQ_SYS_NODE_SIGN, sign);
    	
	    	StringBuilder referer = new StringBuilder();
			referer.append(urlString).append("?_appInfo").append("=").append(URLEncoder.encode(JSON.toJSONString(header), "UTF-8"));
			System.out.println("H5 _appInfo =" + JSON.toJSONString(header));
			header.put("Referer", referer.toString());
	    	
			httpPost(urlString, urlJoin(params), header, "h5");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    protected String genPayPwd(String srcPwd, String salt) {
    	return UserUtil.getPassword(srcPwd, salt);
    }
    
    private String sign(Map<String,String> params ,String signStrPrefix) {
    	List<String> paramList = new ArrayList<String>();
        if (params != null && params.size() > 0) {
            paramList.addAll(params.keySet());
            Collections.sort(paramList);
        }
        String signStrBefore = signStrPrefix;
        for (String item : paramList) {
        	String value = params.get(item);
        	if(value != null) {
        		signStrBefore = signStrBefore + "&" + item + "=" + value;
        	}
        }
        System.out.println("String before sign:"+signStrBefore);
        return DigestUtil.getDigestStr(signStrBefore);
    }
    
    private Map<String, String> createBaseHeader() {
    	Map<String, String> head = new HashMap<String,String>();
        head.put(Constants.REQ_SYS_NODE_ID, "i_E647D820-D623-4CCC-9C14-1EA2E4E1C0A0_1508923514481_borrowSuperman");
        head.put(Constants.REQ_SYS_NODE_VERSION, "407");
        head.put(Constants.REQ_SYS_NODE_NETTYPE, "4G");
        head.put(Constants.REQ_SYS_NODE_TIME, String.valueOf(System.currentTimeMillis()));
        return head;
    }
    
    private String createSignPrefix(Map<String,String> header) {
    	return Constants.REQ_SYS_NODE_VERSION + "=" + header.get(Constants.REQ_SYS_NODE_VERSION) + 
		"&"+ Constants.REQ_SYS_NODE_NETTYPE+"=" + header.get(Constants.REQ_SYS_NODE_NETTYPE) + 
		"&"+ Constants.REQ_SYS_NODE_TIME+"=" + header.get(Constants.REQ_SYS_NODE_TIME) + 
		"&"+ Constants.REQ_SYS_NODE_USERNAME+"=" + header.get(Constants.REQ_SYS_NODE_USERNAME);
    }
    
    private static String urlJoin(Map<String, String> params) throws UnsupportedEncodingException {
    	if(params == null) return "";
    	
    	StringBuilder res = new StringBuilder("");
    	Set<Entry<String, String>> entrySet = params.entrySet();
    	for (Entry<String, String> entry : entrySet) {
    		res.append("&" + URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
    	return res.substring(1);
    }
    
    private String httpPost(String url, String reqBody, Map<String, String> headers, String type) throws Exception {
    	CloseableHttpClient httpClient = null;
    	
    	if(url.contains("https")) {
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
	        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(ctx);
	        
	        httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
    	}else {
    		httpClient = HttpClients.createDefault();
    	}
    	
    	// headers
    	HttpPost postMethod = new HttpPost(url);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
            	postMethod.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        // request body
    	EntityBuilder builder = EntityBuilder.create();
    	builder.setContentEncoding("UTF-8");
    	
    	if("api".equals(type)) {
    		builder.setContentType(ContentType.APPLICATION_JSON);
    	}else {
    		builder.setContentType(ContentType.APPLICATION_FORM_URLENCODED);
    	}
    	
    	builder.setText(reqBody);
        HttpEntity reqEntity = builder.build();
        postMethod.setEntity(reqEntity);
        
        // do
        HttpResponse response = httpClient.execute(postMethod);
        System.out.println("Response statusLine:"+response.getStatusLine());
        String result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        try {
        	result = JSON.toJSONString(JSON.parseObject(result), true);
        }catch (Exception e) {
        	e.printStackTrace();
		}
        System.out.println("Response result:"+result);
        return result;
    }
    
}
