package com.ald.fanbei.api.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Function: 时间通用工具 Date: 2019年1月8日 上午10:57:22 <br/>
 *
 * @author zhangxinxing
 * @version 1.0
 * @Copyright
 */
public class timeUtil {
	/**
	 * 判断时间是不是今天
	 * 
	 * @param date
	 * @return 是返回true，不是返回false
	 */
	public static boolean isNow(Date date) {
		// 当前时间
		Date now = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		// 获取今天的日期
		String nowDay = sf.format(now);

		// 对比的时间
		String day = sf.format(date);

		return day.equals(nowDay);

	}
	
	
	public static boolean isYesterday(Date date) {
		// 当前时间
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
		// 查询时间
		String queryDay = sf.format(date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date tdate = calendar.getTime();
		
		// 昨天时间
		String day = sf.format(tdate);
		System.out.println(day.equals(queryDay));
		return day.equals(queryDay);

	}
	
}
