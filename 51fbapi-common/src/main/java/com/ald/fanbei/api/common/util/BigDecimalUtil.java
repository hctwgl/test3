/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *@类描述：金额相关计算
 *@author 何鑫 2017年1月18日  12:51:33
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BigDecimalUtil {
	
	public static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");
	
	/**
	 * 加法,保留小数点两位
	 * 
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal add(BigDecimal v1,BigDecimal v2){
		v1 = v1==null?new BigDecimal(0):v1;
		v2 = v2==null?new BigDecimal(0):v2;
		v1 = v1.add(v2).setScale(2, RoundingMode.HALF_UP);
		return v1;
	}
	

	/**
	 * 多个加法想家
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static BigDecimal add(BigDecimal... array){
		BigDecimal result = BigDecimal.ZERO;
		if (array == null || array.length == 0) {
			return BigDecimal.ZERO;
		}
		for (int i = 0; i < array.length; i++) {
			result = add(result, array[i]);
		}
		return result;
	}
	
	/**
	 * 加法,保留小数点两位
	 * 
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal add(double v1,double v2){
		BigDecimal value = new BigDecimal(v1).add(new BigDecimal(v2)).setScale(2, RoundingMode.HALF_UP);
		return value;
	}
	
	/**
	 * 减法v1-v2
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal subtract(BigDecimal v1,BigDecimal v2){
		v1 = v1==null?new BigDecimal(0):v1;
		v2 = v2==null?new BigDecimal(0):v2;
		v1 = v1.subtract(v2).setScale(2, RoundingMode.HALF_UP);
		return v1;
	}
	
	/**
	 * 减法v1-v2
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal subtract(double v1,double v2){
		BigDecimal value = new BigDecimal(v1).subtract(new BigDecimal(v2)).setScale(2, RoundingMode.HALF_UP);
		return value;
	}
	
	/**
	 * 乘法 v1*v2
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal multiply(double v1,double v2){
		BigDecimal value = new BigDecimal(v1).multiply(new BigDecimal(v2)).setScale(2, RoundingMode.HALF_UP);
		return value;
	}
	
	/**
	 * 乘法 v1*v2
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal multiply(BigDecimal v1,BigDecimal v2){
		v1 = v1==null?new BigDecimal(0):v1;
		v2 = v2==null?new BigDecimal(0):v2;
		v1 = v1.multiply(v2).setScale(2, RoundingMode.HALF_UP);
		return v1;
	}
	
	/**
	 * 多个Bigdecimal相乘
	 * @param array
	 * @return
	 */
	public static BigDecimal multiply(BigDecimal... array){
		BigDecimal result = BigDecimal.ONE;
		if (array == null || array.length == 0) {
			return BigDecimal.ZERO;
		}
		for (int i = 0; i < array.length; i++) {
			result = multiply(result, array[i]);
		}
		return result;
	}
	
	/**
	 * 除法 v1/v2(v2为0时未抛异常，注意不传空)
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal divide(double v1,double v2){
		BigDecimal value = new BigDecimal(v1).divide(new BigDecimal(v2),2, RoundingMode.HALF_UP);
		return value;
	}
	
	/**
	 * 除法 v1/v2(v2为0时未抛异常，注意不传空)
	 *@param v1
	 *@param v2
	 *@return
	 */
	public static BigDecimal divide(BigDecimal v1,BigDecimal v2){
		v1 = v1==null?new BigDecimal(0):v1;
		v2 = v2==null?new BigDecimal(0):v2;
		v1 = v1.divide(v2,2, RoundingMode.HALF_UP);
		return v1;
	}
	
	/**
	 * 计算分期还款金额
	 * @param v1 --借款本金
	 * @param num --分期数
	 * @param monthRate --月利率
	 * @param poundage --总还款手续费
	 * @return
	 */
	public static BigDecimal getConsumeAmount(BigDecimal amount,int num,BigDecimal monthRate,BigDecimal poundage){
		amount = amount==null?new BigDecimal(0):amount;
		monthRate = monthRate ==null?new BigDecimal(0):monthRate;
		poundage = poundage ==null?new BigDecimal(0):poundage;
		BigDecimal v1 = (amount.multiply(monthRate).multiply((BigDecimal.ONE.add(monthRate)).pow(num)))
				.divide((BigDecimal.ONE.add(monthRate)).pow(num).subtract(BigDecimal.ONE),6,RoundingMode.HALF_UP).add(poundage.divide(new BigDecimal(num),2,RoundingMode.HALF_UP));
		return v1;
	}
	
	/**
	 * 计算总账单金额
	 * @param v1 --借款本金
	 * @param num --分期数
	 * @param monthRate --月利率
	 * @param poundage --总还款手续费
	 * @return
	 */
	public static BigDecimal getConsumeTotalAmount(BigDecimal amount,int num,BigDecimal monthRate,BigDecimal poundage){
		amount = amount==null?new BigDecimal(0):amount;
		monthRate = monthRate ==null?new BigDecimal(0):monthRate;
		poundage = poundage ==null?new BigDecimal(0):poundage;
		BigDecimal v1 = (new BigDecimal(num).multiply(amount).multiply(monthRate).multiply((BigDecimal.ONE.add(monthRate)).pow(num)))
				.divide((BigDecimal.ONE.add(monthRate)).pow(num).subtract(BigDecimal.ONE),6,RoundingMode.HALF_UP).add(poundage);
		return v1;
	}
	
	/**
	 * 获取总手续费
	 * @param amount --借款本金
	 * @param num --分期期数
	 * @param poundageRate --手续费率
	 * @param min --手续费下限
	 * @param max --手续费上限
	 * @return
	 */
	public static BigDecimal getTotalPoundage(BigDecimal amount,int num,BigDecimal poundageRate,BigDecimal min,BigDecimal max){
		amount = amount==null?new BigDecimal(0):amount;
		poundageRate = poundageRate ==null?new BigDecimal(0):poundageRate;
		BigDecimal v1 = amount.multiply(poundageRate);
		if(min.compareTo(v1)>0){
			v1 = min;
		}else if(v1.compareTo(max)>0){
			v1 = max;
		}
		return v1;
	}
	
	public static int getCreditScore(BigDecimal zmScore,BigDecimal fqzScore,BigDecimal tdScore,
			BigDecimal zmRate,BigDecimal fqzRate,BigDecimal tzRate){
		BigDecimal v1 = zmScore.divide(new BigDecimal(950),4,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(10)).multiply(zmRate);
		BigDecimal v2 = fqzScore.multiply(new BigDecimal(0.1)).multiply(fqzRate);
		BigDecimal v3 = (new BigDecimal(100).subtract(tdScore)).multiply(new BigDecimal(0.1)).multiply(tzRate);
		return v1.add(v2).add(v3).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
	}
}
