/**
 * 
 */
package com.ald.fanbei.api.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *@类PriceUtil.java 的实现描述：
 *@author 陈金虎 2016年10月26日 下午4:35:28
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class PriceUtil {
	public static int bigdecimalToInt(BigDecimal value){
		if(value == null){
			return 0;
		}
		return value.intValue();
	}
	

	public static int bigdecimalToInt(String valueStr){
		BigDecimal value = new BigDecimal(valueStr);
		return value.intValue();
	}
	
	public static int bigdecimalToInt(Integer valueStr){
		BigDecimal value = new BigDecimal(valueStr);
		return value.intValue();
	}
	
	/**
	 * 获取小数部分
	 * @return
	 */
	public static String getDecimal(BigDecimal value){
		String valueStr = value.setScale(2, RoundingMode.HALF_UP) + "";
		return valueStr.substring(valueStr.length()-2, valueStr.length());
	}
	
	/**
	 * 获取小数部分
	 * @return
	 */
	public static String getDecimal(String valueString){
		BigDecimal value = new BigDecimal(valueString);
		String valueStr = value.setScale(2, RoundingMode.HALF_UP) + "";
		return valueStr.substring(valueStr.length()-2, valueStr.length());
	}
	
	/**
	 * 获取小数部分
	 * @return
	 */
	public static String getDecimal(Integer valueInt){
		BigDecimal value = new BigDecimal(valueInt);
		String valueStr = value.setScale(2, RoundingMode.HALF_UP) + "";
		return valueStr.substring(valueStr.length()-2, valueStr.length());
	}
	
	/**
	 * 获取小数部分
	 * @return
	 */
	public static BigDecimal divideForPrice(BigDecimal v1,int v2Int){
		BigDecimal v2 = new BigDecimal(v2Int);
		v1 = v1==null?new BigDecimal(0):v1;
		v2 = v2==null?new BigDecimal(0):v2;
		v1 = v1.divide(v2,1, RoundingMode.HALF_UP);
		return v1;
	}
	
	/**
	 * 获取折扣
	 * @return
	 */
	public static BigDecimal getDiscount(BigDecimal v1){
		v1 = v1.divide(new BigDecimal(10),1, RoundingMode.HALF_UP);
		return v1;
	}
	
	/**
	 * 获取去掉无效数字的折扣数字
	 * @param v1
	 * @return
	 */
	public static String getDiscountRemoveInvalidNumber(BigDecimal v1){
		v1 = v1.divide(new BigDecimal(10),1, RoundingMode.HALF_UP);
		
		 String valueStr=v1.toString();
		 if(valueStr.indexOf(".") > 0){
		     //正则表达
			 valueStr = valueStr.replaceAll("0+?$", "");
			 valueStr = valueStr.replaceAll("[.]$", "");
		   }
		return valueStr;
		
	}
	
	public static String getKeepTwoDecimalNumber(String v1){
		BigDecimal v2= new BigDecimal(v1);
		 String valueStr   =  v2.setScale(2,BigDecimal.ROUND_HALF_UP).toString();   
		return valueStr;
		
	}
	public static String getKeepTwoDecimalBigDecimal(BigDecimal v1){
		if(v1 == null){
			return "0.00";
		}
		 String valueStr   =  v1.setScale(2,BigDecimal.ROUND_HALF_UP).toString();   
		return valueStr;
		
	}

	
	public static void main(String[] args) {
		System.out.println(getDiscount(new BigDecimal(70)));
	}

}
