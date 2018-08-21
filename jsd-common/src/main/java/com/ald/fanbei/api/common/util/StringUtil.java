package com.ald.fanbei.api.common.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @类描述：字符串工具类
 * @author 陈金虎 2017年1月16日 下午11:43:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class StringUtil extends StringUtils {

	private static final String COMMA = ",";

	/**
	 * 通过StringBuffer来组装字符串
	 * 
	 * @param strings
	 * @return
	 */
	public static String appendStrs(Object... strings) {
		StringBuffer sb = new StringBuffer();
		for (Object str : strings) {
			sb.append(str);
		}
		return sb.toString();
	}

	/**
	 * 判断所有传入参数是否非空，当传入参数长度为0，或者任意有一个为空，则返回false，所有都非空，则返回true
	 * 
	 * @param strArr
	 * @return
	 */
	public static boolean isAllNotEmpty(String... strArr) {
		boolean isAllNotEmpty = true;
		if (strArr == null || strArr.length < 1) {
			isAllNotEmpty = false;
			return isAllNotEmpty;
		}

		for (String str : strArr) {
			if (str == null || str.length() == 0) {
				isAllNotEmpty = false;
				break;
			}
		}
		return isAllNotEmpty;
	}

	/**
	 * 把list按分隔符转换成字符串
	 * 
	 * @param strList
	 *            list数据
	 * @param separator
	 *            分隔符
	 * @return
	 */
	public static String turnListToStr(Collection<String> strList, String separator) {
		String result = "";
		if (strList == null || strList.size() < 1) {
			return result;
		}
		if (separator == null) {
			separator = ",";
		}

		for (String item : strList) {
			result = result + separator + item;
		}
		return result.substring(separator.length());
	}

	/**
	 * 把字符串数组按分隔符转化成字符串
	 * 
	 * @param strArr
	 *            字符串数组
	 * @param separator
	 *            分隔符
	 * @return
	 */
	public static String turnArrayToStr(String separator, String... strArr) {
		String result = "";
		if (strArr == null || strArr.length < 1) {
			return result;
		}
		if (separator == null) {
			separator = ",";
		}

		for (String item : strArr) {
			result = result + separator + item;
		}
		return result.substring(separator.length());
	}

	public static String strToSecret(String str, int left, int right) {
		StringBuffer sb = new StringBuffer();
		int len = str.length() - left - right;
		if (len > 0) {
			sb.append(str.substring(0, left));
			for (int i = 0; i < len; i++) {
				sb.append("*");
			}
			sb.append(str.substring(str.length() - right));
		} else {
			return str;
		}
		return sb.toString();
	}

	/**
	 * 把List拼接成String 并且添加分隔符
	 * 
	 * @param <T>
	 * @param strList
	 * @return
	 */
	public static <T> String turnListToStr(List<T> list) {
		String result = StringUtils.EMPTY;
		if (CollectionUtil.isEmpty(list)) {
			return StringUtils.EMPTY;
		}
		for (T item : list) {
			result = result + item + COMMA;
		}
		return result.substring(0, result.length() - 1);
	}

	public static String getNotEmptyString(String str) {
		return StringUtils.isNotEmpty(str) ? str : StringUtils.EMPTY;
	}

	public static String getLastString(String str, int num) {
		int len = str.length();
		if (len <= num) {
			return str;
		} else {
			return str.substring(len - num);
		}
	}

	public static List<String> splitToList(String source, String sep) {
		List<String> result = new ArrayList<String>();
		if (isBlank(source)) {
			return result;
		}
		String[] tempResult = source.split(sep);
		for (String item : tempResult) {
			result.add(item);
		}
		return result;
	}

	/**
	 * @方法描述：将字符串中的emoji符号转换为*
	 * 
	 * @author huyang 2017年4月6日下午12:38:04
	 * @param source
	 *            待处理字符串
	 * @return
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	public static String filterEmoji(String source) {
		if (source != null) {
			Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
					Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
			Matcher emojiMatcher = emoji.matcher(source);
			if (emojiMatcher.find()) {
				source = emojiMatcher.replaceAll("*");
				return source;
			}
			return source;
		}
		return source;
	}

	public static String null2Str(Object str) {
		return (str != null) ? str.toString() : "";
	}

	public static String logisticsInfoDeal(String str) {
		if (str == null || "暂无".equals(str.trim())) {
			return "";
		}
		return str.trim();
	}

	/**
	 * @Title: UrlEncoder
	 * @Description: 字符串编码
	 * @param sStr
	 * @return
	 */
	public final static String UrlEncoder(String sStr) {
		String sReturnCode = "";
		try {
			sReturnCode = URLEncoder.encode(null2Str(sStr), "utf-8");
		} catch (Exception ex) {
		}
		return sReturnCode;
	}

	/**
	 * @Title: UrlDecoder
	 * @Description: 字符串解码
	 * @param sStr
	 * @return
	 */
	public static String UrlDecoder(String sStr) {
		if (isEmpty(sStr)) {
			return "";
		} else {
			String sReturnCode = sStr;
			try {
				sReturnCode = URLDecoder.decode(sStr, "utf-8");
			} catch (Exception e) {
			}
			return sReturnCode;
		}
	}
	
	/**
	 * 判断客户端类型
	 * 
	 * @param flagStr
	 * @return
	 */
	public static String judgeClientDeviceFlag(String sourceStr) {
		String flagStr = "";
		if (isEmpty(sourceStr)) {
			return flagStr;
		}
		try {
			String[] sourceArray = sourceStr.split("_");
			if (sourceArray != null && sourceArray.length > 2 && isNotBlank(sourceArray[1])) {
				flagStr = sourceArray[1];
			}
			return flagStr;
		} catch (Exception e) {
			return flagStr;
		}
	}

	/**
	 * 获取指定字符串最后指定长度信息
	 * 
	 * @param cardNumber
	 * @param lastLength
	 * @return
	 */
	public static String getLastAppointLengthChar(String cardNumber, int lastLength) {
		if (cardNumber == null || cardNumber.length() <= lastLength) {
			return null2Str(cardNumber);
		}
		return cardNumber.substring(cardNumber.length() - lastLength);
	}

	public static String joinListToString(List<String> sourceList, String sep) {
	    	StringBuffer sb = new StringBuffer();
	    	if(sourceList==null || sourceList.size()==0){
	    		return "";
	    	}
	    	for (int i =0;i<sourceList.size();i++) {
	    		if(i==sourceList.size()-1){
	    			sb.append(sourceList.get(i));
	    		}else{
	    			sb.append(sourceList.get(i)).append(sep);
	    		}
			}
	    	return sb.toString();
	}
	 
	public static String getDeviceTailNum(String deviceId) {
		if (StringUtils.isEmpty(deviceId)) {
			throw new IllegalArgumentException("deviceId can't be empty.");
		}
		String deviceIdTail = deviceId.substring(deviceId.length() - 1, deviceId.length());
		char tail = deviceIdTail.charAt(0);
		int tmp = (int) tail;
		int tailNum  = tmp % 10;
		return tailNum + "";
	}

	/**
     * 根据map中数据，替换短信内容
     * 实例，假设原始短信模版中有特殊符&errorMsg,map中有对应key为errorMsg，则替换对应内容到&errorMsg
     * @param extraChar 替换内容中的标识符号，如&
     * @param originMessage 短信模版本
     * @param replaceMap 数据封装map
     * @return
     */
    public static String convertMessageByMapInfo(String extraChar,String originMessage,Map<String,String> replaceMapData){
    	Set<String> keySet = replaceMapData.keySet();
    	for (String tempKey : keySet) {
    		originMessage = originMessage.replaceAll(extraChar+tempKey, replaceMapData.get(tempKey));
		}
    	return originMessage;
    }
	public static void main(String[] args) {
		System.out.println(judgeClientDeviceFlag("i_EF97D566-0A4A-4492-9AEB-38C3A44AD40B_1510372484991_www"));
	}
}
