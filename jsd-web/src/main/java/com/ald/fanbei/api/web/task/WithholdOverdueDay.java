package com.ald.fanbei.api.web.task;


import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("withholdOverdueDay")
public class WithholdOverdueDay {
    Logger logger = LoggerFactory.getLogger(WithholdJob.class);

    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;



    public void  withhold(Map<String,String> config,Date bengin) {
        try {
            logger.info("withhold overdue run start,time=" + new Date());
            String cardType=config.get("cardType");
            String failCount=config.get("failCount");
            JSONObject overdueSection=JSONObject.fromObject(config.get("overdueSection"));
            String minSection= (String) overdueSection.get("minSection");
            String maxSection= (String) overdueSection.get("maxSection");
            Date now=DateUtil.getStartOfDate(new Date());
            Date endTime=DateUtil.addDays(now,Integer.parseInt(minSection)==0?0:(-Integer.parseInt(minSection)+1));
            Date startTime=DateUtil.addDays(DateUtil.getStartOfDate(new Date()),-(Integer.parseInt(maxSection)));
            int totalRecord = jsdBorrowCashService.getBorrowCashByOverdueCountBySection(startTime,endTime);
            if (totalRecord == 0) {
                logger.info("withholdOverdue run finished,Loan Due size is 0.time=" + new Date());
            } else {
                List<JsdBorrowCashDo> borrowCashDos = jsdBorrowCashService.getBorrowCashOverdueBySection(startTime,endTime);
                //锁住代扣还款用户
                jsdBorrowCashRepaymentService.lockBorrowList(borrowCashDos);
                jsdBorrowCashRepaymentService.dealWithhold(borrowCashDos,cardType);
            }
            logger.info("withhold  overdue run end,time=" + new Date());
        } catch (Exception e) {
            logger.error("withholdOverdue  error, case=", e);
        }



    }





}
