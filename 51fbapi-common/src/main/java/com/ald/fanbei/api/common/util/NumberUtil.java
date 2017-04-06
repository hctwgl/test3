package com.ald.fanbei.api.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;

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
    
    public static void main(String[] args) {
		System.out.println(between2Number(new Long(100),10l,200));
	}

}
