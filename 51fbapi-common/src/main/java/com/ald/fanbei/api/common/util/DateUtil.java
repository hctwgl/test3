package com.ald.fanbei.api.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *@类描述：日期相关工具类
 *@author 陈金虎 2017年1月16日 下午11:40:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class DateUtil {

    /**
     * milliseconds in a second.
     */
    public static final long   SECOND                      = 1000;

    /**
     * milliseconds in a minute.
     */
    public static final long   MINUTE                      = SECOND * 60;

    /**
     * milliseconds in a hour.
     */
    public static final long   HOUR                        = MINUTE * 60;

    /**
     * milliseconds in a day.
     */
    public static final long   DAY                         = 24 * HOUR;

    /**
     * yyyy-MM
     */
    public static final String MONTH_PATTERN               = "yyyy-MM";

    /**
     * yyyyMMdd
     */
    public static final String DEFAULT_PATTERN             = "yyyyMMdd";

    /**
     * yyyyMMdd
     */
    public static final String DEFAULT_PATTERN_WITH_HYPHEN = "yyyy-MM-dd";

    public static final String DEFAULT_PATTERN_WITH_DOT    = "yyyy.MM.dd";

    public static final String DEFAULT_CHINESE_PATTERN     = "yyyy年MM月dd";
    
    public static final String DEFAULT_CHINESE_FULL_PATTERN = "yyyy年MM月dd日HH点mm分";

    public static final String HOUR_PATTERN          = "yyyyMMddHH";

    /**
     * yyyyMMddHHmmss
     */
    public static final String FULL_PATTERN                = "yyyyMMddHHmmss";

    /**
     * yyyyMMdd HH:mm:ss
     */
    public static final String FULL_STANDARD_PATTERN       = "yyyyMMdd HH:mm:ss";

    /**
     * MM.dd HH:mm
     */
    public static final String FULL_MATCH_PATTERN          = "MM.dd HH:mm";

    /**
     * HH:mm
     */
    public static final String SHORT_MATCH_PATTERN         = "HH:mm";

    /**
     * yyyy-MM-dd HH:mm
     */
    public static final String DATE_TIME_MINUTE            = "yyyy-MM-dd HH:mm";

    /**
     * <pre>
     * yyyy-MM-dd HH:mm:ss
     * </pre>
     */
    public static final String DATE_TIME_SHORT             = "yyyy-MM-dd HH:mm:ss";

    /**
     * <pre>
     * yyyy-MM-dd HH:mm:ss.SSS
     * </pre>
     */
    public static final String DATE_TIME_FULL              = "yyyy-MM-dd HH:mm:ss.SSS";
    
    public static final String NO_END_DATE_FORMAT          = "9999-12-31 23:59:59";
    
    public static final Long NO_END_DATE_TIME              = 253402271999000l;
    
    public static final Date NO_END_DATE                   = new Date(NO_END_DATE_TIME);
    public static final String FINAL_END_DATE_STR		   = "9999-12-31";

    private static Logger      logger                      = LoggerFactory.getLogger(DateUtil.class);
    
    /**
     * Add specified number of days to the given date.
     * 
     * @param date date
     * @param days Int number of days to add
     * @return revised date
     */
    public static Date addDays(final Date date, int days) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);

        return new Date(cal.getTime().getTime());
    }

    public static Date addMins(final Date date, int mins) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, mins);

        return new Date(cal.getTime().getTime());
    }
    
    public static Date getNoEndDate() {
    	SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_SHORT);
    	try {
			return sdf.parse(NO_END_DATE_FORMAT);
		} catch (ParseException e) {
			return new Date();
		}
    }
    
    /**
     * Add specified number of hour to the date given.
     * 
     * @param date Date
     * @param hour Int number of hour to add
     * @return Date
     */
    public static Date addHoures(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }
    /**
     * 返回一个Date默认最大值
     * @return
     */
    public static Date getFinalDate() {
		return parseDate(FINAL_END_DATE_STR, DEFAULT_PATTERN_WITH_HYPHEN);
    }
    /**
     * Add specified number of months to the date given.
     * 
     * @param date Date
     * @param months Int number of months to add
     * @return Date
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }

    /**
     * Get date one day after specified one.
     * 
     * @param date1 Date 1
     * @param date2 Date 2
     * @return true if date1 after date2
     */
    public static boolean afterDay(final Date date1, final Date date2) {
    	return date1.after(date2);
//        return getStartOfDate(date1).after(getStartOfDate(date2));
    }
    
    /**
     * judge the srcDate is between startDate and endDate
     *@param srcDate
     *@param startDate
     *@param endDate
     *@return
     */
    public static boolean isBetweenDateRange(final Date srcDate, final Date startDate, final Date endDate) {
    	if (srcDate != null && startDate != null && endDate != null) {
    		return srcDate.getTime() >= startDate.getTime() && srcDate.getTime() <= endDate.getTime();
    	}
    	return false;
    }

    /**
     * Get date one day before specified one.
     * 
     * @param date1 test date
     * @param date2 date when
     * @return true if date1 is before date2
     */
    public static boolean beforeDay(final Date date1, final Date date2) {
        return getStartOfDate(date1).before(getStartOfDate(date2));
    }
    
    /**
     * long类型的milliseconds转换成Date类型的时间
     * @param time
     * @return
     */
    public static Date convert(long time,Date defaultDate){
        try{
            Date date = new Date(time);
            return date;
        }catch(Exception e){
            return defaultDate;
        }
    }

    /**
     * 转换long类型到时,分,秒,毫秒的格式.
     * 
     * @param time long type
     * @return
     */
    public static String convert(long time) {
        long ms = time % 1000;
        time /= 1000;

        int h = Integer.valueOf("" + (time / 3600));
        int m = Integer.valueOf("" + ((time - h * 3600) / 60));
        int s = Integer.valueOf("" + (time - h * 3600 - m * 60));

        return h + "小时(H)" + m + "分(M)" + s + "秒(S)" + ms + "毫秒(MS)";
    }

    /**
     * 转换long类型到时,分,秒,毫秒的格式.
     * 
     * @param time long type
     * @return
     */
    public static String convertEn(long time) {
        long ms = time % 1000;
        time /= 1000;

        int h = Integer.valueOf("" + (time / 3600));
        int m = Integer.valueOf("" + ((time - h * 3600) / 60));
        int s = Integer.valueOf("" + (time - h * 3600 - m * 60));

        return h + "H" + m + "M" + s + "S" + ms + "MS";
    }

    /**
     * @param aDate
     * @return
     */
    public static String convertDateToString(String pattern, Date aDate) {
        return getDateTime(pattern, aDate);
    }
    
    /**
     * 毫秒转换成时间
     * @param millis
     * @return
     */
    public static Date convertMillisToDate(long millis,Date defaultDate){
        Calendar calendar = Calendar.getInstance();
        try{
            calendar.setTimeInMillis(millis);
            return calendar.getTime();
        }catch(Exception e){
            return defaultDate;
        }
    }

    /**
     * This method generates a string representation of a date/time in the format you specify on input
     * 
     * @param aMask the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @see java.text.SimpleDateFormat
     * @throws ParseException when String doesn't match the expected format
     */
    public static Date convertStringToDate(String aMask, String strDate) {
        SimpleDateFormat df;
        Date date = null;
        df = new SimpleDateFormat(aMask);

        if (logger.isDebugEnabled()) {
            logger.debug("converting '" + strDate + "' to date with mask '" + aMask + "'");
        }

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            logger.error("ParseException: " + pe);
        }

        return date;
    }

    /**
     * @return the current date without time component
     */
    public static Date currentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 获取dayOff天之前（后）的当前时间
     * <p>
     * if < 0 , return dayOff天前的时间
     * <p>
     * if > 0 , return dayOff后的时间
     * 
     * @return
     */
    public static String getCertainDayStr(int dayOff) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayOff);
        return formatDate(calendar.getTime(), DEFAULT_PATTERN);
    }
    
    /**
     * 获取dayOff天之前（后）的当前时间
     * <p>
     * if < 0 , return dayOff天前的时间
     * <p>
     * if > 0 , return dayOff后的时间
     * 
     * @return
     */
    public static Date getCertainDay(int dayOff) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayOff);
        return calendar.getTime();
    }

    public static String getCertainHour(int hourOff) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hourOff);
        return formatDate(calendar.getTime(), HOUR_PATTERN);
    }

    /**
     * Format date as "yyyyMMdd".
     * 
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(final Date date) {
        return formatDate(date, DEFAULT_PATTERN);
    }

    /**
     * Format date as given date format.
     * 
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(final Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * Format date as given date format.
     * 
     * @param timeStamp 毫秒级别时间戳
     * @return 格式化后的日期字符串
     */
    public static String formatDate(long timeStamp, String format) {
    	Date date = new Date(timeStamp);
        return new SimpleDateFormat(format).format(date);
    }

    public static String formatDateTime(Date date) {
        return formatDate(date, DATE_TIME_SHORT);
    }

    
    public static Date getAccuracyTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * <p><code>getAccuracyTimeWithAmount(Calendar.DAY_OF_MONTH, -5)</code>.
     *
     * @param field the calendar field.
     * @param amount the amount of date or time to be added to the field.
     * @return
     */
    public static Date getAccuracyTimeWithAmount(int field, int amount){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(field, amount);
        return calendar.getTime();
    }
    
    /**
     * Format date as "MM月dd日 HH:mm".
     * 
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatFullMatchDate(final Date date) {
        return formatDate(date, FULL_MATCH_PATTERN);
    }

    /**
     * 返回MM月dd日
     * 
     * @param srcDate
     * @return
     */
    public static String formatMonthAndDay(Date srcDate) {
        Calendar cal1 = GregorianCalendar.getInstance();
        cal1.setTime(srcDate);
        SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日");

        return formatter.format(srcDate);
    }

    /**
     * 返回短日期格式
     * 
     * @return [yyyy-mm-dd]
     */
    public static String formatShort(String strDate) {
        String ret = "";
        if (strDate != null && !"1900-01-01 00:00:00.0".equals(strDate) && strDate.indexOf("-") > 0) {
            ret = strDate;
            if (ret.indexOf(" ") > -1) ret = ret.substring(0, ret.indexOf(" "));
        }
        return ret;
    }

    /**
     * 格式化中文日期短日期格式
     * 
     * @param gstrDate 输入欲格式化的日期
     * @return [yyyy年MM月dd日]
     */

    public static String formatShortDateC(Date gstrDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        String pid = formatter.format(gstrDate);
        return pid;
    }

    /**
     * Format date as "HH:mm".
     * 
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatShortMatchDate(final Date date) {
        return formatDate(date, SHORT_MATCH_PATTERN);
    }

    public static Date getCurrentMonday() {
        Calendar cd = Calendar.getInstance();

        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        Date date;
        if (dayOfWeek == 1) {
            date = cd.getTime();
        } else {
            date = addDays(cd.getTime(), 1 - dayOfWeek);
        }

        return getStartOfDate(date);
    }

    /**
     * Return default datePattern (yyyy-MM-dd)
     * 
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        return "yyyy-MM-dd";
    }

    public static String getDateTime(Date date) {
        return formatDate(date, DATE_TIME_SHORT);
    }

    /**
     * This method generates a string representation of a date's date/time in the format you specify on input
     * 
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     * @see java.text.SimpleDateFormat
     */
    public static String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            logger.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    public static String getDateTimeFull(Date date) {
        return formatDate(date, DATE_TIME_FULL);
    }

    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * 返回当前日
     * 
     * @return [dd]
     */

    public static String getDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        Date nowc = new Date();
        String pid = formatter.format(nowc);
        return pid;
    }

    /**
     * 一天的结束时间，【注：只精确到毫秒】
     * 
     * @param date
     * @return
     */
    public static Date getEndOfDate(final Date date) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return new Date(cal.getTime().getTime());
    }
    /**
     * 一天的结束时间，【注：只精确到秒】
     * 
     * @param date
     * @return
     */
    public static Date getEndOfDatePrecisionSecond(final Date date) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return new Date(cal.getTime().getTime());
    }
    /**
     * Return the end of the month based on the date passed as input parameter.
     * 
     * @param date Date
     * @return Date endOfMonth
     */
    public static Date getEndOfMonth(final Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DATE, 0);

        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * Get first day of month.
     * 
     * @param date Date
     * @return Date
     */
    public static Date getFirstOfMonth(final Date date) {
        Date lastMonth = addMonths(date, -1);
        lastMonth = getEndOfMonth(lastMonth);
        return addDays(lastMonth, 1);
    }

    public static Date getMondayBefore4Week() {
        Calendar cd = Calendar.getInstance();

        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        Date date;
        if (dayOfWeek == 1) {
            date = addDays(cd.getTime(), -28);
        } else {
            date = addDays(cd.getTime(), -27 - dayOfWeek);
        }

        return getStartOfDate(date);
    }
    


    /**
     * 返回当前日
     * 
     * @return [dd]
     */

    public static String getDay(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        String pid = formatter.format(date);
        return pid;
    }

    /**
     * 返回当前月份,如果date为null则返回当前月份
     * 
     * @return [MM]
     */

    public static String getMonth(Date date) {
    	if(date == null){
    		date = new Date();
    	}
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        String pid = formatter.format(date);
        return pid;
    }

    /**
     * 返回当前年份,如果date为null则返回当前年份
     * 
     * @return [MM]
     */

    public static String getYear(Date date) {
    	if(date == null){
    		date = new Date();
    	}
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String pid = formatter.format(date);
        return pid;
    }

    /**
     * 返回标准格式的当前时间
     * 
     * @return [yyyy-MM-dd k:mm:ss]
     */

    public static String getNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd H:mm:ss");
        Date nowc = new Date();
        String pid = formatter.format(nowc);
        return pid;
    }
    
    /**
     * 格式化日期
     * @param date
     * @return
     */
    public static Date formatDateToYYYYMMdd(Date date) {
    	if(date==null){
    		return null;
    	}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = sdf.parse(sdf.format(date));
			return date;
		} catch (ParseException e) {
			return null;
		}
    		
    }
    
    


    /**
     * get date time as "yyyyMMddhhmmss"
     * 
     * @return the current date with time component
     */
    public static String getNowYearMonthDay() {
        return formatDate(new Date(), DEFAULT_PATTERN);
    }
    


    /**
     * get date time as "yyyyMMddhhmmss"
     * 
     * @return the current date with time component
     */
    public static String getNowYearMonthDay(Date date) {
        return formatDate(date, DEFAULT_PATTERN);
    }

   /**
    *  
    *@param timeString 年、月、日   eg。“MM”,"YYYY"
    *@param timeLong 时间搓
    *@return
    */
    public static int getTimeYearOrMonthOrDay(String timeString,long timeLong) {
        SimpleDateFormat formatter = new SimpleDateFormat(timeString);
        Date nowc = new Date(timeLong);
       
		int time= NumberUtil.objToIntDefault(formatter.format(nowc), -1);

        return time;
    }
    
    
    
    
    /**
     * 计算2个日前直接相差的天数
     * 
     * @param cal1
     * @param cal2
     * @return
     */
    public static long getNumberOfDaysBetween(Calendar cal1, Calendar cal2) {
        cal1.clear(Calendar.MILLISECOND);
        cal1.clear(Calendar.SECOND);
        cal1.clear(Calendar.MINUTE);
        cal1.clear(Calendar.HOUR_OF_DAY);

        cal2.clear(Calendar.MILLISECOND);
        cal2.clear(Calendar.SECOND);
        cal2.clear(Calendar.MINUTE);
        cal2.clear(Calendar.HOUR_OF_DAY);

        long elapsed = cal2.getTime().getTime() - cal1.getTime().getTime();
        return elapsed / DAY;
    }

    /**
     * 计算两日期之间相差的天数
     * 
     * @param cal1
     * @param cal2
     * @return
     */
    public static long getNumberOfDatesBetween(Date before, Date end) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(before);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(end);
        return getNumberOfDaysBetween(cal1, cal2);
    }
    
    /**
     * 返回两个时间间隔的小时数
     * 
     * @param before 起始时间
     * @param end 终止时间
     * @return 小时数
     */
    public static long getNumberOfHoursBetween(final Date before, final Date end) {
        long millisec = end.getTime() - before.getTime() + 1;
        return millisec / (60 * 60 * 1000);
    }

    /**
     * 返回两个时间间隔的分钟数
     * 
     * @param before 起始时间
     * @param end 终止时间
     * @return 分钟数
     */
    public static long getNumberOfMinuteBetween(final Date before, final Date end) {
        long millisec = end.getTime() - before.getTime();
        return millisec / (60 * 1000);
    }

    public static int getNumberOfMonthsBetween(final Date before, final Date end) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(before);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(end);
        return (cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)) * 12
               + (cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH));
    }

    public static int getNumberOfSecondsBetween(final double end, final double start) {
        if ((end == 0) || (start == 0)) {
            return -1;
        }

        return (int) (Math.abs(end - start) / SECOND);
    }

    public static Date getPreviousMonday() {
        Calendar cd = Calendar.getInstance();

        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
        Date date;
        if (dayOfWeek == 1) {
            date = addDays(cd.getTime(), -7);
        } else {
            date = addDays(cd.getTime(), -6 - dayOfWeek);
        }

        return getStartOfDate(date);
    }

    /**
     * 返回中文格式的当前日期
     * 
     * @return [yyyy-mm-dd]
     */
    public static String getShortNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date nowc = new Date();
        String pid = formatter.format(nowc);
        return pid;
    }

    /**
     * Get start of date.
     * 
     * @param date Date
     * @return Date Date
     */
    public static Date getStartOfDate(final Date date) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return new Date(cal.getTime().getTime());
    }

    /**
     * 返回当前时间24小时制式
     * 
     * @return [H:mm]
     */

    public static String getTimeBykm() {
        SimpleDateFormat formatter = new SimpleDateFormat("H:mm");
        Date nowc = new Date();
        String pid = formatter.format(nowc);
        return pid;
    }

    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }
    
    public static Date getTodayLast() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * 检查日期的合法性
     * 
     * @param sourceDate
     * @return
     */
    public static boolean inFormat(String sourceDate, String format) {
        if (sourceDate == null) {
            return false;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setLenient(false);
            dateFormat.parse(sourceDate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = GregorianCalendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = GregorianCalendar.getInstance();
        cal2.setTime(date2);

        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
               && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && (cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE)));
    }

    /**
     * Compare the two calendars whether they are in the same month.
     * 
     * @param cal1 the first calendar
     * @param cal2 the second calendar
     * @return whether are in the same month
     */
    public static boolean isSameMonth(Calendar cal1, Calendar cal2) {
        return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
               && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
    }

    /**
     * Compare the two dates whether are in the same month.
     * 
     * @param date1 the first date
     * @param date2 the second date
     * @return whether are in the same month
     */
    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar cal1 = GregorianCalendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = GregorianCalendar.getInstance();
        cal2.setTime(date2);
        return isSameMonth(cal1, cal2);
    }

    /**
     * get date time as "yyyyMMddhhmmss"
     * 
     * @return the current date with time component
     */
    public static String now() {
        return formatDate(new Date(), FULL_PATTERN);
    }

    /**
     * change the string to date
     * 
     * @param String
     * @return Date if failed return <code>null</code>
     */
    public static Date parseDate(String date) {
        return parseDate(date, DEFAULT_PATTERN, null);
    }

    /**
     * change the string to date
     * 
     * @param String
     * @param defaultValue
     * @return Date
     */
    public static Date parseDate(String date, String df) {
        return parseDate(date, df, null);
    }

    /**
     * change the string to date
     * 
     * @param String
     * @param df DateFormat
     * @param defaultValue if parse failed return the default value
     * @return Date
     */
    public static Date parseDate(String date, String df, Date defaultValue) {
        if (date == null) {
            return defaultValue;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(df);

        try {
            return formatter.parse(date);
        } catch (ParseException e) {
        }

        return defaultValue;
    }

    private DateUtil() {

    }
    
    /**
     * parse object to date
     * 
     * @param obj
     * @return
     */
    public static Date parseObjToDate(Object obj) {
        Date date = null;
        try {
            date = (Date) obj;
        } catch (Exception e) {
            date = Calendar.getInstance().getTime();
        }
        return date;
    }

    /**
     * 返回yyyyMMdd HH:mm:ss格式日期
     * @return
     */
    public static Date parseDateyyyyMMddHHmmss(String dateStr) {
        if(dateStr == null || dateStr.length()==0){
            return null;
        }
        SimpleDateFormat parser = new SimpleDateFormat(FULL_STANDARD_PATTERN);
        try {
            return parser.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }
    
    /**
     * @Title: compareDate
     * @Description: (日期比较，如果s>=e 返回true 否则返回false)
     * @param s
     * @param e
     * @return boolean
     */
    public static boolean compareDate(Date s, Date e) {
        return s.getTime() >= e.getTime();
    }
    
    public static void main(String[] args) {
        /*
         * System.out.println(DateUtils.getFirstOfMonth(DateUtils.addMonths( DateUtils.currentDate(), -3)));
         * System.out.println(DateUtils.getEndOfMonth(DateUtils.currentDate())); System.out.println("now:" +
         * DateUtils.now());
         */
        /*
         * Calendar cal1 = GregorianCalendar.getInstance(); cal1.add(Calendar.MINUTE, 2);
         * System.out.println(getNumberOfMinuteBetween(new Date(), cal1.getTime()));
         */
        // System.out.println("previous monday:" +
        // DateUtils.getPreviousMonday());
        // System.out.println("current monday:" + DateUtils.getCurrentMonday());
        // System.out.println("monday before 4 weeks:"
        // + DateUtils.getMondayBefore4Week());
//        long time = System.currentTimeMillis();
//        System.out.println(convert(time,null));
//        System.out.println(getNoEndDate().getTime());
//        System.out.println(formatDate(addDays(new Date(), 1), "yyyyMMdd"));
//        System.out.println("formatDate(new Date(), \"yyyyMMdd\"):" + DateUtil.formatDate(new Date(), "yyyyMMdd"));
//        System.out.println(parseDate("2011年11月03", "yyyy年MM月dd"));
        
//        System.out.println(isBetweenDateRange(new Date(), new Date(System.currentTimeMillis()-1), new Date()));
//        System.out.println(new Date());
//        System.out.println(addHoures(new Date(), 1));
    	
    	
    	Date date = new Date(System.currentTimeMillis());
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	Date arrivalStart = DateUtil.getStartOfDate(date);
    	Integer day = 7;
    	Date arrivalEnd = DateUtil.getEndOfDate(date);
		Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
		System.out.println(sdf.format(repaymentDay));
		try {
			date = sdf.parse(sdf.format(date));
			date = DateUtil.addDays(date,1);
			long betweenGmtArrival = DateUtil.getNumberOfDatesBetween(date, DateUtil.getToday());
			System.out.println(betweenGmtArrival);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    }

}
