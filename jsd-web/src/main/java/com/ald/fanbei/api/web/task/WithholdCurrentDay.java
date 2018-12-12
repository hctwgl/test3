package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component("withholdCurrentDay")
public class WithholdCurrentDay {
    Logger logger = LoggerFactory.getLogger(WithholdJob.class);

    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;


    public void  withhold(Map<String,String> config,Date bengin) {
        try {
            logger.info("withhold current run start,time=" + new Date());
            String cardType=config.get("cardType");
            String failCount=config.get("failCount");
            int totalRecord = jsdBorrowCashService.getBorrowCashByTodayCount(bengin);
            if (totalRecord == 0) {
                logger.info("withhold run finished,Loan Due size is 0.time=" + new Date());
            } else {
                logger.info("withhold run start,time=" + new Date());
                List<JsdBorrowCashDo> borrowCashDos = jsdBorrowCashService.getBorrowCashByToday(bengin);
                jsdBorrowCashRepaymentService.lockBorrowList(borrowCashDos);
                jsdBorrowCashRepaymentService.dealWithhold(borrowCashDos,cardType);
            }
            logger.info("withhold current run end,time=" + new Date());
        } catch (Exception e) {
            logger.error("withhold  error, case=", e);
        }
    }



}
