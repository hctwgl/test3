package com.ald.fanbei.web.test.common;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.alibaba.fastjson.JSONObject;



/**
 * @类描述：单元测试基类
 * @author zjf 2017年9月26日
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class BaseTest {
	
	/**
	 * 自动生成登陆令牌
	 * @param userName
	 */
	public void init(String userName) {
		String token = UserUtil.generateToken(userName);
		TokenBo tokenBo = new TokenBo();
		tokenBo.setLastAccess(System.currentTimeMillis() + "");
		tokenBo.setToken(token);
		tokenBo.setUserId(userName);
		RedisClient.hmset(userName, tokenBo.getTokenMap());
	}
	
	protected void testApi(String urlString, Map<String,String> params){
		testApi(urlString, params, "15968109556");
	}
	
    protected void testApi(String urlString, Map<String,String> params, String userName){
    	Map<String,String> header = createBaseHeader();
    	header.put(Constants.REQ_SYS_NODE_USERNAME, userName);
    	
    	String signStrPrefix = createSignPrefix(header);
    	String sign = sign(params, signStrPrefix);
    	header.put(Constants.REQ_SYS_NODE_SIGN, sign);
    	
		try {
			httpPost(urlString, JSONObject.toJSONString(params), header);
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
        if (paramList.size() > 0) {
            for (String item : paramList) {
                signStrBefore = signStrBefore + "&" + item + "=" + params.get(item);
            }
        }
        return DigestUtil.getDigestStr(signStrBefore);
    }
    
    private Map<String, String> createBaseHeader() {
    	Map<String, String> head = new HashMap<String,String>();
        head.put(Constants.REQ_SYS_NODE_ID, "a_1234_hwe123");
        head.put(Constants.REQ_SYS_NODE_VERSION, "1");
        head.put(Constants.REQ_SYS_NODE_NETTYPE, "4G");
        head.put(Constants.REQ_SYS_NODE_TIME, String.valueOf(System.currentTimeMillis()));
        return head;
    }
    
    private String createSignPrefix(Map<String,String> header) {
    	return Constants.REQ_SYS_NODE_VERSION + "=" + header.get(Constants.REQ_SYS_NODE_VERSION) + 
		"&"+ Constants.REQ_SYS_NODE_NETTYPE+"=" + header.get(Constants.REQ_SYS_NODE_NETTYPE) + 
		"&"+ Constants.REQ_SYS_NODE_TIME+"=" + header.get(Constants.REQ_SYS_NODE_TIME) + 
		"&"+ Constants.REQ_SYS_NODE_USERNAME+"=" + header.get(Constants.REQ_SYS_NODE_TIME);
    }
    
    private String httpPost(String url, String reqBody, Map<String, String> headers) throws ClientProtocolException, IOException {
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	
    	// headers
    	HttpPost postMethod = new HttpPost(url);
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
            	postMethod.addHeader(entry.getKey(), entry.getValue());
            }
        }
        
        // request body
    	EntityBuilder builder = EntityBuilder.create();
    	builder.setContentEncoding("");
    	builder.setText(reqBody);
        HttpEntity reqEntity = builder.build();
        postMethod.setEntity(reqEntity);
        
        // do
        HttpResponse response = httpClient.execute(postMethod);
        System.out.println(response.getStatusLine());
        String result = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        System.out.println(result);
        return result;
    }
    
}
