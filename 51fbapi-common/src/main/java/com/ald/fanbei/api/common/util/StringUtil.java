package com.ald.fanbei.api.common.util;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 *@类描述：字符串工具类
 *@author 陈金虎 2017年1月16日 下午11:43:50
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class StringUtil extends StringUtils{
    
	private static final String COMMA = ",";
	
    /**
     * 通过StringBuffer来组装字符串
     *@param strings
     *@return
     */
    public static String appendStrs(Object... strings) {
        StringBuffer sb = new StringBuffer();
        for (Object str : strings) {
            sb.append(str);
        }
        return sb.toString();
    }
    
    /**
     * 把list按分隔符转换成字符串
     * 
     *@param strList list数据
     *@param separator 分隔符
     *@return
     */
    public static String turnListToStr(Collection<String> strList,String separator){
        String result = "";
        if(strList == null || strList.size() < 1){
            return result;
        }
        if(separator == null){
            separator = ",";
        }
        
        for(String item:strList){
            result = result + separator + item;
        }
        return result.substring(separator.length());
    }
    
    /**
     * 把字符串数组按分隔符转化成字符串
     *@param strArr 字符串数组
     *@param separator 分隔符
     *@return
     */
    public static String turnArrayToStr(String separator,String ...strArr){
        String result = "";
        if(strArr == null || strArr.length < 1){
            return result;
        }
        if(separator == null){
            separator = ",";
        }
        
        for(String item:strArr){
            result = result + separator + item;
        }
        return result.substring(separator.length());
    }
    
    public static String strToSecret(String str,int left,int right) {
        StringBuffer sb = new StringBuffer();
        int len = str.length()-left-right;
        if(len>0){
        	sb.append(str.substring(0, left));
        	for (int i = 0; i < len; i++) {
        		sb.append("*");
			}
        	sb.append(str.substring(str.length()-right));
        }else{
        	return str;
        }
        return sb.toString();
    }
    
    /**
     * 把List拼接成String 并且添加分隔符
     * @param <T>
     * @param strList
     * @return
     */
    public static <T> String turnListToStr(List<T> list){
        String result = StringUtils.EMPTY;
        if (CollectionUtil.isEmpty(list)) {
        	return StringUtils.EMPTY;
        } 
        for(T item:list){
            result = result + item + COMMA;
        }
        return result.substring(0, result.length()-1);
    }
    
    public static String getNotEmptyString(String str) {
    	return StringUtils.isNotEmpty(str) ? str : StringUtils.EMPTY;
    }
    
    public static String getLastString(String str,int num){
    	int len = str.length();
    	if(len<=num){
    		return str;
    	}else{
    		return str.substring(len-num);
    	}
    }
}
