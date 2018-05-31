package com.ald.fanbei;

import com.ald.fanbei.api.common.CacheConstants;
import com.ald.fanbei.api.common.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

public class Test {
    public static void main(String[] args) {
        String activityName = "AAA";
        // 活动时间配置
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        String startHourKey = CacheConstants.CACHE_KEY_ACTIVITY_START_HOUR_ARRAY + activityName;
       // String[] activityStartHourArray = (String[]) bizCacheUtil.getObject(startHourKey);

        System.out.println(currentHour);
        System.out.println(startHourKey);
    }
}
