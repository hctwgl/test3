package com.ald.fanbei.api.common.util;


import java.util.Random;
import java.util.UUID;


/**
 * 
 *@类描述：用户工具类
 *@author 陈金虎 2017年1月16日 下午11:44:10
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UserUtil {
    static Random random = new Random();
    
	public static String getPassword(String passwordSrc,String salt){
        return DigestUtil.digestString(passwordSrc, salt);
	}
	
	public static String getSalt(){
        byte[] saltBytes = DigestUtil.generateSalt(DigestUtil.DEFAULT_BYTES_SIZE);
        String salt = DigestUtil.encodeHex(saltBytes);
        return salt;
	}
	
	
	
	
    public static String generateToken(String userName){
    	StringBuffer sb = new StringBuffer();
    	sb.append(userName).append("_").append(UUID.randomUUID().toString()).append(DigestUtil.MD5_16(userName+random.nextInt(Integer.MAX_VALUE))).append(System.currentTimeMillis());
        return CommonUtil.calculateSha256(sb.toString());
    }
    
    
	
    
  
    
}
