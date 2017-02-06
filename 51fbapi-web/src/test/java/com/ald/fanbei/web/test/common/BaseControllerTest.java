package com.ald.fanbei.web.test.common;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.struts.mock.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.web.multipart.MultipartFile;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;



/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年2月6日下午2:13:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@ContextConfiguration(locations = { "classpath*:spring-test.xml" })
public class BaseControllerTest extends AbstractJUnit4SpringContextTests {
	

	protected MockHttpServletRequest request = new MockHttpServletRequest();
    
//    protected static final String HTTPHOST       = "http://localhost:8070";
    protected static final String HTTPHOST       = "http://localhost";

	public static final String CHARSET        = "utf-8";

    //public static final String HTTPHOST       = "http://acsinc.aliapp.com:80/";
//    public static final String PostAddr      = HTTPHOST + "app/acs.htm";

	private static CloseableHttpClient                httpClient = null;
    
    static {
        try {
            httpClient = HttpClients.custom().build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected JSONObject createBasePost(){
    	JSONObject jo = new JSONObject();
    	jo.put("id", "1asdfasdf");
    	
    	Map<String, String> system = new HashMap<String, String>();
    	system.put("appVersion", "1.0-1");
    	system.put("userId", "1437");
    	system.put("sign", "ss");
    	system.put("time", Long.toString(System.currentTimeMillis()));
    	system.put("netType", "4G");
    	jo.put("system", system);
    	
    	return jo;
    }
    
    protected Map<String, String> createBaseHeader() {
    	Map<String,String> head = new HashMap<String,String>();
        head.put("id", "a_1234_hwe123");
        head.put("appVersion", "1");
        head.put("userName", "18672349815");
        head.put("time", "1450230780000");
        head.put("sign", "145asd023sdf078we0000");
        head.put("netType", "4G");
        return head;
    }

    protected String httpGet(String url, HashMap<String, String> params) throws ClientProtocolException, IOException {
        return this.httpGet(url, params, null);
    }

    protected String httpGet(String url, HashMap<String, String> params, HashMap<String, String> headers)
                                                                                                         throws ClientProtocolException,
                                                                                                         IOException {
        return this.http(url, params, headers, "get", null);
    }
    
    
    public static String httpPost(String url, String param, Map<String, String> headers) {
    	
        String body = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            StringEntity reqEntity = new StringEntity(param, "UTF-8");
            reqEntity.setContentType("application/json;charset=utf-8");
            reqEntity.setContentEncoding("utf-8");
            httpPost.setEntity(reqEntity);
            if (headers != null && headers.size() > 0) {
                for (String header : headers.keySet()) {
                    httpPost.addHeader(header, headers.get(header));
                }
            }
            HttpResponse httpresponse = httpClient.execute(httpPost);
            HttpEntity entity = httpresponse.getEntity();
           
            body = EntityUtils.toString(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }
    
    
    protected String httpPost(String url, HashMap<String, String> params, Object file) throws ClientProtocolException,
                                                                                    IOException {
        return this.httpPost(url, params, new HashMap<String, String>(), file);
    }

    protected String httpPost(String url, HashMap<String, String> params) throws ClientProtocolException, IOException {
        return this.httpPost(url, params, new HashMap<String, String>(), null);
    }

    protected String httpPost(String url, HashMap<String, String> params, HashMap<String, String> headers, Object file)
                                                                                                                     throws ClientProtocolException,
                                                                                                                     IOException {
        return this.http(url, params, headers, "post", file);
    }

    private String http(String url, HashMap<String, String> params, HashMap<String, String> headers, String method,
    		Object file) throws ClientProtocolException, IOException {
        if (StringUtils.isBlank(method) || (!method.equalsIgnoreCase("get") && !method.equalsIgnoreCase("post"))) {
            return "method is not get or post";
        }
        HttpClientBuilder builder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = builder.build();
        List<NameValuePair> query = new ArrayList<NameValuePair>();
        if (params == null) {
            params = new HashMap<String, String>();
        }
        params.put("from", "junit");
        long date = System.currentTimeMillis();
        params.put("requestId", String.valueOf(date / 1000L));

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                query.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        String sign = StringUtils.EMPTY;
        try {
        	
        	query.add(new BasicNameValuePair("sign", sign));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 要传递的参数
        url = url + "?" + URLEncodedUtils.format(query, CharEncoding.UTF_8);
        HttpUriRequest request = new HttpGet(url);
        if (method.equalsIgnoreCase("post")) {
            request = new HttpPost(url);
        }
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        HttpResponse response = null;
        if (method.equalsIgnoreCase("post")) {
            HttpPost postMethod = new HttpPost();
            postMethod.setURI(request.getURI());
            if (file != null) {
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                if (file instanceof File) {
                    File files = (File) file;
                    entityBuilder.addBinaryBody("file", files);
                } else if (file instanceof MultipartFile) {
                    MultipartFile files = (MultipartFile) file;
                    InputStreamBody inputStreamBody = new InputStreamBody(files.getInputStream(),
                                                                          files.getOriginalFilename());
                    entityBuilder.addPart("file", inputStreamBody);
                } else {
                    logger.warn("the file type is unsupport exception");
                    throw new RuntimeException("the file type is unsupport exception");
                }
                HttpEntity reqEntity = entityBuilder.build();
                postMethod.setEntity(reqEntity);
            }
            
            response = httpClient.execute(postMethod);
        } else {
            response = httpClient.execute(request);
        }
        HttpEntity entity = response.getEntity();
        System.out.println(response.getStatusLine());
        // 显示结果
        if (!url.contains("preview")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));

            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } else {
            BufferedInputStream bi = new BufferedInputStream(entity.getContent());
            FileOutputStream fo = new FileOutputStream(new File("D:/picture/" + new Date().getTime() + ".jpg"));
            int readByte = 0;
            while ((readByte = bi.read()) != -1) {
                fo.write(readByte);
            }
            bi.close();
            fo.close();
            return null;
        }
    }
    
    protected RequestDataVo initRequestDataVo(){
    	RequestDataVo requestDataVo = new RequestDataVo();
    	requestDataVo.setId("w_cjh_" + System.currentTimeMillis());
    	requestDataVo.setMethod("/test/test");
    	requestDataVo.setParams(new HashMap<String,Object>());
    	requestDataVo.setSystem(createBaseHeader1());
    	return requestDataVo;
    }
    
    protected FanbeiContext initAppContext(){
    	FanbeiContext appContext = new FanbeiContext();
    	appContext.setAppVersion(132);
    	appContext.setNick("cjh");
    	appContext.setUserId(328l);
    	appContext.setUserName("chenjinhu");
    	return appContext;
    }
    

    
    private Map<String, Object> createBaseHeader1() {
    	Map<String,Object> head = new HashMap<String,Object>();
        head.put("appVersion", "1");
        head.put("userName", "18672349815");
        head.put("time", "1450230780000");
        head.put("sign", "145asd023sdf078we0000");
        head.put("netType", "4G");
        return head;
    }
    
    public void testApi(String urlString,Map<String,String> params){
      
        String resp = BaseControllerTest.httpPost(urlString, JSONObject.toJSONString(params),createBaseHeader());
        System.out.println("resp=" + resp);
        
    }
  
}
