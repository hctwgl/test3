package com.ald.fanbei.api.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;




/**
 * 
 *@类描述：公共类
 *@author 陈金虎 2017年1月16日 下午11:45:31
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CommonUtil {
	

    protected static Logger   logger           = LoggerFactory.getLogger(CommonUtil.class);
    
    static Random random = new Random();
    static String[] charactors = new String[]{"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z","0","1","2","3","4","5","6","7","8","9"}; 
    
    /**
     * 随机获取一个openIm密码
     * @return
     */
    public static String getRandomOpenImPassword(){
        String uuid = UUID.randomUUID().toString();
        return DigestUtil.MD5_16(uuid);
    }
    
    /**
     * 获取num位的一个随机字符
     *@param num
     *@return
     */
    public static String getRandomCharacter(int num){
    	String result = "";
    	for(int i = 0 ; i < num ;i ++){
    		result = result + charactors[random.nextInt(36)];
    	}
    	return result;
    }
    
    /**
     * 随机获取一个openIm用户名
     * @return
     */
    public static String getRandomOpenImUserId(){
        String uuid = UUID.randomUUID().toString();
        return DigestUtils.md5Hex(uuid);
    }
    
    public static List<String> turnStringToList(String str,String separator){
        if(StringUtils.isBlank(str)){
            return null;
        }
        if(StringUtils.isBlank(separator)){
            separator = ",";
        }
        String[] strArr = str.split(separator);
        List<String> result = new ArrayList<String>();
        for(String item:strArr){
            if(StringUtils.isNotBlank(item)){
                result.add(item);
            }
        }
        return result;
    }
    
    public static String calculateSha256(String srcStr){
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(srcStr.getBytes());
            
            return getDigestStr(md.digest());
        }catch(NoSuchAlgorithmException e){
            throw new FanbeiException(FanbeiExceptionCode.CALCULATE_SHA_256_ERROR,e);
        }
    }
    
    private static String getDigestStr(byte[] origBytes) { 
        String tempStr = null; 
        StringBuilder stb = new StringBuilder(); 
        for (int i = 0; i < origBytes.length; i++) { 
            tempStr = Integer.toHexString(origBytes[i] & 0xff); 
            if (tempStr.length() == 1) { 
                stb.append("0"); 
            } 
            stb.append(tempStr);

        } 
        return stb.toString(); 
    }
    
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    /**
     * 手机号验证
     * 
     * @param  str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) { 
        boolean b = false; 
    	if(StringUtil.isBlank(str)){
    		return b;
    	}
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        Matcher m = p.matcher(str);
        b = m.matches(); 
        return b;
    }
    
    /**
     * 正则表达式校验邮箱
     * @param emaile 待匹配的邮箱
     * @return 匹配成功返回true 否则返回false;
     */
    public static boolean isEmail(String email){
        boolean b = false; 

        String RULE_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
        //正则表达式的模式
        Pattern p = Pattern.compile(RULE_EMAIL);
        //正则表达式的匹配器
        Matcher m = p.matcher(email);
        b = m.matches();
        //进行正则匹配
        return b;
    }
    
    public static int getRandomNum(int num){
        return random.nextInt(num);
    }
    
    public static void sleepMilliSeconds(long milliSeconds){
    	try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			logger.error("sleep error");
		}
    }
    
    /**
     * 获取固定位数(size位)的随机数字
     *@param length
     *@return
     */
    public static String getRandomNumber(int size){
        String ramdomNum = "";
        for(int i = 0 ; i < size ;i ++){
        	ramdomNum = ramdomNum + random.nextInt(10);
        }
    	return ramdomNum;
    }
    
    /**
     * 过滤掉emoji字符
     * @param source 原字符
     * @return 过滤后的字符串
     */
    public static String filterEmoji(String source) {  
    	if(StringUtils.isNotBlank(source)){  
            return source.replaceAll("[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]", "").trim();  
    	}else{  
            return source;  
        }  
    }  
    
    /**
     * 过滤特殊字符
     * @param source 原字符串
     * @return 过滤后的字符串
     */
    public static String filterSpecial(String source) {  
    	if(StringUtils.isNotBlank(source)){  
            return source.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5.，,。？“”]+", "").trim();  
    	}else{  
            return source;  
        }  
    }
    
    // 使用STRING BUFFER 来组装字符串，效率更高
    public static String appendStrs(String... strs) {
        StringBuffer sb = new StringBuffer();
        for (Object str : strs) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    
    public static void main(String[] args) {
//        System.out.println(getRandomCharacter(200));
//        System.out.println(isMobile("14948572856"));
        System.out.println(isEmail("2323@qq.com"));
//        System.out.println(isMobileMa("18672349815"));
//        System.out.println(getRandomNum(2));
//        System.out.println(filterEmoji("This is a smiley \uD83C\uDFA6 face\uD860\uDD5D \uD860\uDE07 \uD860\uDEE2 \uD863\uDCCA \uD863\uDCCD \uD863\uDCD2 \uD867\uDD98 "));
    }
}
