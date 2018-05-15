package com.ald.fanbei.api.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *@类描述：数字处理类
 *@author 陈金虎 2017年1月16日 下午11:42:34
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class NumberUtil {

    private static final Logger logger = LoggerFactory.getLogger(NumberUtil.class);
    /**
     * 汉语中数字大写
     */
    private static final String[] CN_UPPER_NUMBER = { "零", "壹", "贰", "叁", "肆",
            "伍", "陆", "柒", "捌", "玖" };
    /**
     * 汉语中货币单位大写，这样的设计类似于占位符
     */
    private static final String[] CN_UPPER_MONETRAY_UNIT = { "分", "角", "元",
            "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾",
            "佰", "仟" };
    /**
     * 特殊字符：整
     */
    private static final String CN_FULL = "整";
    /**
     * 特殊字符：负
     */
    private static final String CN_NEGATIVE = "负";
    /**
     * 金额的精度，默认值为2
     */
    private static final int MONEY_PRECISION = 2;
    /**
     * 特殊字符：零元整
     */
    private static final String CN_ZEOR_FULL = "零元" + CN_FULL;
    /**
     * 把输入的金额转换为汉语中人民币的大写
     *
     * @param numberOfMoney
     *            输入的金额
     * @return 对应的汉语大写
     */
    public static String number2CNMontrayUnit(BigDecimal numberOfMoney) {
        StringBuffer sb = new StringBuffer();
        // -1, 0, or 1 as the value of this BigDecimal is negative, zero, or
        // positive.
        int signum = numberOfMoney.signum();
        // 零元整的情况
        if (signum == 0) {
            return CN_ZEOR_FULL;
        }
        //这里会进行金额的四舍五入
        long number = numberOfMoney.movePointRight(MONEY_PRECISION)
                .setScale(0, 4).abs().longValue();
        // 得到小数点后两位值
        long scale = number % 100;
        int numUnit = 0;
        int numIndex = 0;
        boolean getZero = false;
        // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
        if (!(scale > 0)) {
            numIndex = 2;
            number = number / 100;
            getZero = true;
        }
        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            number = number / 10;
            getZero = true;
        }
        int zeroSize = 0;
        while (true) {
            if (number <= 0) {
                break;
            }
            // 每次获取到最后一个数
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (!(getZero)) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                }
                if (numIndex == 2) {
                    if (number > 0) {
                        sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                    }
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                }
                getZero = true;
            }
            // 让number每次都去掉最后一个数
            number = number / 10;
            ++numIndex;
        }
        // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
        if (!(scale > 0)) {
            sb.append(CN_FULL);
        }
        return sb.toString();
    }

    /**
     * 无默认返回，返回null
     * 
     * @param source
     * @return
     */
    public static Long strToLong(String source, Long defValue) {
        if (StringUtils.isBlank(source)) return defValue;
        if (!StringUtils.isNumeric(source)) return defValue;
        return Long.parseLong(source);
    }

    /**
     * 无默认返回
     * <p>
     * 若obj为null，则返回null
     * 
     * @param source
     * @return
     */
    public static Long strToLong(String source) {
        if (StringUtils.isBlank(source)) return null;
        if (!StringUtils.isNumeric(source)) return null;
        return Long.parseLong(source);
    }

    public static Long objToLongDefault(Object obj, long defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static Long objToLongDefault(Object obj, Long defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static Boolean objToBooleanDefault(Object obj, Boolean defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Boolean.parseBoolean(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 方法说明: 判断输入的数值是否为空或者0
     * @param num
     * @return
     */
    public static boolean isNullOrZero(Integer num){
    	if(num==null || num==0){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * 无默认返回
     * <p>
     * 若obj为null，则返回null
     * 
     * @param obj
     * @return
     */
    public static Long objToLong(Object obj) {
        if (null == obj) return null;
        try {
            return Long.parseLong(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static int objToIntDefault(Object obj, int defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static Integer objToIntDefault(Object obj, Integer defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static int objToPageIntDefault(Object obj, int defaultValue) {
        if (null == obj) return defaultValue;
        try {
        	int pageNum = Integer.parseInt(obj.toString()); 
            return pageNum == 0 ? 1 : pageNum;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static Long objToPageLongDefault(Object obj, Long defaultValue) {
        if (null == obj) return defaultValue;
        try {
        	Long pageNum = Long.parseLong(obj.toString()); 
            return pageNum == 0L ? 1L : pageNum;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float objToFloatDefault(Object obj, float defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Float.parseFloat(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    public static double objToDoubleDefault(Object obj, double defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static Double objToDoubleDefault(Object obj, Double defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return Double.parseDouble(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static BigDecimal objToBigDecimalDefault(Object obj, BigDecimal defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static BigDecimal objToBigDecimalZeroToDefault(Object obj, BigDecimal defaultValue) {
        if (null == obj) return defaultValue;
        try {
        	BigDecimal objValue = new BigDecimal(obj.toString());
        	if(objValue.compareTo(BigDecimal.ZERO)==0){
        		return defaultValue;
        	}
            return objValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * 无默认返回
     * <p>
     * 若obj为null，则返回null
     * 
     * @param obj
     * @return
     */
    public static Integer objToInteger(Object obj) {
        if (null == obj) return null;
        try {
            return Integer.parseInt(obj.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isIntIllegal(String str) {
        boolean isIllegal = false;
        if (!NumberUtils.isNumber(str)) {
            return true;
        }
        int sensor = Integer.valueOf(str);
        if (sensor <= 0) {
            isIllegal = true;
        }
        return isIllegal;
    }

    /**
     * 从string转为Integer，并规定了范围
     * @param str 需要转换的数字
     * @param minNumber 最小范围
     * @param maxNumber 最大范围
     * @return 成功返回Ineger，失败返回null
     */
    public static Integer str2Integer(String str, int minNumber, int maxNumber){
		Integer lType = 0;
		if (StringUtils.isBlank(str) || !NumberUtils.isNumber(str)) {// 校验是否是数字
			return null;
		}
		else{
			lType = Integer.parseInt(str);
			if(lType < minNumber || lType > maxNumber){
				return null;
			}		
		}
		return lType;
    }
    
    /**
     * 保留两位小数
     * @param d
     * @return
     */
    public static String format2Str(BigDecimal d) {
 	    if(d == null){
 	    	return "0.00";
 	    }
         DecimalFormat df = new DecimalFormat("0.00");
         String ds = df.format(d);
         return ds;
     }
    
    /**
     * 比较数字是否在指定两个数字范围呢
     * @param compareNum 需要转换的数字
     * @param minNumber 最小范围
     * @param maxNumber 最大范围
     * @return 成功返回Ineger，失败返回null
     */
    public static boolean between2Number(Number compareNum, Number minNumber, Number maxNumber){
		if (compareNum.longValue() > maxNumber.longValue() || compareNum.longValue() < minNumber.longValue()) {
			return false;
		}
		return true;
    }
    
    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseNumber(String text, Class<T> targetClass) {
        if (StringUtils.isBlank(text) || null == targetClass) {
            logger.info("text string or target class must not be null or empty");
            return null;
        }
        String trimmed = trimAllWhitespace(text);

        if (targetClass.equals(Byte.class)) {
            return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte.valueOf(trimmed));
        } else if (targetClass.equals(Short.class)) {
            return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short.valueOf(trimmed));
        } else if (targetClass.equals(Integer.class)) {
            return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed) : Integer.valueOf(trimmed));
        } else if (targetClass.equals(Long.class)) {
            return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long.valueOf(trimmed));
        } else if (targetClass.equals(BigInteger.class)) {
            return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed) : new BigInteger(trimmed));
        } else if (targetClass.equals(Float.class)) {
            return (T) Float.valueOf(trimmed);
        } else if (targetClass.equals(Double.class)) {
            return (T) Double.valueOf(trimmed);
        } else if (targetClass.equals(BigDecimal.class) || targetClass.equals(Number.class)) {
            return (T) new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException("Cannot convert String [" + text + "] to target class ["
                                               + targetClass.getName() + "]");
        }
    }

    /**
     * Determine whether the given value String indicates a hex number, i.e. needs to be passed into
     * {@code Integer.decode} instead of {@code Integer.valueOf} (etc).
     */
    private static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }

    /**
     * Decode a {@link java.math.BigInteger} from a {@link String} value. Supports decimal, hex and octal notation.
     * 
     * @see BigInteger#BigInteger(String, int)
     */
    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;

        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }

        // Handle radix specifier, if present.
        if (value.startsWith("0x", index) || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index) && value.length() > 1 + index) {
            index++;
            radix = 8;
        }

        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }

    private static String trimAllWhitespace(String str) {
        if (!(str != null && str.length() > 0)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        int index = 0;
        while (sb.length() > index) {
            if (Character.isWhitespace(sb.charAt(index))) {
                sb.deleteCharAt(index);
            } else {
                index++;
            }
        }
        return sb.toString();
    }
    
    public static boolean isValidRangeForInteger(Integer obj, int min, int max) {
		return (obj != null && obj >= min && obj <= max);
	}
    
    public static boolean isNotValidRangeForInteger(Integer obj, int min, int max) {
		return (obj == null || obj < min || obj > max);
	}
    
    public static boolean isValidForLong(Long obj) {
		return (obj != null && obj >= 0);
	}
    
    public static boolean isNotValidForLong(Long obj) {
		return (obj == null || obj < 0);
	}
    
    public static boolean isValidForInteger(Integer obj) {
		return (obj != null && obj >= 0);
	}
    
    public static boolean isNotValidForInteger(Integer obj) {
		return (obj == null || obj < 0);
	}
    

    public static BigDecimal objToBigDecimalDivideOnehundredDefault(Object obj, BigDecimal defaultValue) {
        if (null == obj) return defaultValue;
        try {
            return new BigDecimal(obj.toString()).divide(BigDecimal.valueOf(100.00), 2, BigDecimal.ROUND_DOWN);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    
    /**
     * 利息计算
     * @param amount
     * @param rate
     * @param days
     * @return
     */
    public static BigDecimal getSumInterestsByAmountAndRate(BigDecimal amount,BigDecimal rate,Integer days){
    	BigDecimal interests = BigDecimal.valueOf(0);
    	try {
    		interests = amount.multiply(rate).multiply(BigDecimal.valueOf(days)).divide(BigDecimal.valueOf(36000),2 , RoundingMode.HALF_UP);
		} catch (Exception e) {
			return BigDecimal.valueOf(0);
		}
    	return interests;
    }
    
    /**
     * 方法说明: 判断输入的数值是否为空或者0
     * @param num
     * @return
     */
    public static boolean isNullOrZeroOrNegative(BigDecimal num){
    	if(num==null || BigDecimal.ZERO.compareTo(num)>=0){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    /**
     * 方法说明: 判断输入的数值是否为空或者0
     * @param num
     * @return
     */
    public static boolean isNullOrZeroOrNegative(Long num){
    	if(num==null || num<=0){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    
    public static void main(String[] args) {
		System.out.println(getSumInterestsByAmountAndRate(BigDecimal.valueOf(1000),BigDecimal.valueOf(9),10));
        double money = 2020004.0099999999;
        BigDecimal numberOfMoney = new BigDecimal(money);
        String s = number2CNMontrayUnit(numberOfMoney);
        System.out.println("你输入的金额为：【"+ money +"】   #--# [" +s.toString()+"]");
	}
    
    /**
     * 方法说明: 判断输入的数值是否为空
     * @param num
     * @return
     */
    public static boolean isNull(BigDecimal num){
    	if(num==null){
    		return true;
    	}else{
    		return false;
    	}
    }

}
