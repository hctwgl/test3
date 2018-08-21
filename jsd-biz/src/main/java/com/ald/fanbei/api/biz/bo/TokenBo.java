package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;
import java.util.Map;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 * 
 *@类描述：用户token类
 *@author 陈金虎 2017年1月17日 上午12:03:09
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TokenBo extends AbstractSerial {
    
	private static final long serialVersionUID = 3393914391649941067L;
	private String userId;
    private String token;
    private String lastAccess;//最后访问时间戳
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(String lastAccess) {
		this.lastAccess = lastAccess;
	}

	public Map<byte[], byte[]> getTokenMap(){
        Map<byte[], byte[]> result = new HashMap<byte[], byte[]>();
        result.put("userId".getBytes(), this.userId.getBytes());
        result.put("token".getBytes(), this.token.getBytes());
        result.put("lastAccess".getBytes(), this.lastAccess.getBytes());
        return result;
    }
    
    public void setToken(Map<byte[], byte[]> tokenMap){
        if(tokenMap == null){
            return ;
        }
        this.setUserId(new String(tokenMap.get("userId".getBytes())));
        this.setToken(new String(tokenMap.get("token".getBytes())));
        this.setLastAccess(new String(tokenMap.get("lastAccess".getBytes())));
    }
    
}
