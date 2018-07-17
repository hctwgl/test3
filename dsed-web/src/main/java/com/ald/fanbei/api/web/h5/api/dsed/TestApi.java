package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.DsedLoanPeriodsDao;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component("testApi")
public class TestApi implements DsedH5Handle {

    @Resource
    private DsedLoanPeriodsDao dsedLoanPeriodsDao;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        String id = ObjectUtils.toString(context.getData("id"), null);
        DsedLoanPeriodsDo periodsDo=new DsedLoanPeriodsDo();
        periodsDo.setRid(Long.valueOf(id));
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,-1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime();
        periodsDo.setGmtPlanRepay(date);
        dsedLoanPeriodsDao.updateById(periodsDo);
        return resp;
    }

}
