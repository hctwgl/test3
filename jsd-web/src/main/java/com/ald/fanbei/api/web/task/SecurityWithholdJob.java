package com.ald.fanbei.api.web.task;


import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *@类描述：借钱逾期利息，逾期手续费计算 每日零点执行一次
 *@author jilong
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
@Conditional(SecurityWithholdJobCondition.class)
public class SecurityWithholdJob {
    Logger logger = LoggerFactory.getLogger(SecurityWithholdJob.class);

    @Resource
    private JsdBorrowCashService borrowCashService;

    @Resource
    private JsdResourceService jsdResourceService;
    ;

    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    private WithholdCurrentDay withholdCurrentDay;

    @Resource
    private WithholdOverdueDay withholdOverdueDay;

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;
    ExecutorService executor = Executors.newFixedThreadPool(8);



    private static String SWITCH = "open";

    @Scheduled(cron = "0 0/1 * * * ?")
    public void laonDueJob(){
        logger.info("--------------- securityWithholdJob run start,time=" + new Date());
        try{
            JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceSecType.WITHHOLD_JOB_CONFIG_YF.getCode());
            if(resourceDo != null && SWITCH.equals(resourceDo.getValue())){
                JsdResourceDo resDo = jsdResourceService.getByTypeAngSecType(ResourceType.WITHHOLD.getCode(), ResourceSecType.WITHHOLD_JOB_INTERNAL_UIDS.getCode());
                String userIds = resDo.getValue();
                Date bengin = DateUtil.getTodayLast();
                Map<String,String> config= (Map<String, String>) JSON.parse(resourceDo.getValue2());
                logger.info("withhold current job start!");
                Collection currentWithholdTime=JSONArray.toCollection(JSONArray.fromObject(config.get("currentWithholdTime")),List.class);
                String cardType=config.get("cardType");
                String failCount=config.get("failCount");
                String currentTime= DateUtil.formatDate(new Date(),"HH:mm");
                if(currentWithholdTime.size()!=0){
                    Iterator currentIterator=currentWithholdTime.iterator();
                    while (currentIterator.hasNext()){
                        String withholdTime= String.valueOf(currentIterator.next());
                        Runnable threadC= new Runnable() {
                            @Override
                            public void run() {
                                List<JsdBorrowCashDo> borrowCashDos = jsdBorrowCashService.getTodayBorrowCashRepayByUserIds(userIds,currentTime);
                                jsdBorrowCashRepaymentService.dealWithhold(borrowCashDos,cardType,failCount);
                            }
                        };
                        executor.submit(threadC);
                    }
                }
                logger.info("withhold current job end!");

                JSONObject overdueSection=JSONObject.fromObject(config.get("overdueSection"));
                Integer minSection=Integer.parseInt((String) overdueSection.get("minSection"));
                Integer maxSection= Integer.parseInt((String) overdueSection.get("maxSection"));
                if(minSection<0||maxSection<=0
                        ||minSection.compareTo(maxSection)>0){
                    logger.info("withhold overdue section is zero or null");
                }else {
                    logger.info("withhold overdue job start!");
                    Collection overdueWithholdTime=JSONArray.toCollection(JSONArray.fromObject(config.get("overdueWithholdTime")),List.class);
                    Date now=DateUtil.getStartOfDate(new Date());
                    Date endTime=DateUtil.addDays(now,minSection==0?0:(-minSection+1));
                    Date startTime=DateUtil.addDays(DateUtil.getStartOfDate(new Date()),-(maxSection));
                    if(overdueWithholdTime.size()!=0){
                        Iterator overdueIterator=overdueWithholdTime.iterator();
                        while (overdueIterator.hasNext()){
                            String withholdTime= String.valueOf(overdueIterator.next());
                            Runnable threadO= new Runnable() {
                                @Override
                                public void run() {
                                    List<JsdBorrowCashDo> borrowCashDos = jsdBorrowCashService.getOverSectionBorrowCashRepayByUserIds(userIds,startTime,endTime);
                                    jsdBorrowCashRepaymentService.dealWithhold(borrowCashDos,cardType,failCount);
                                }
                            };
                            executor.submit(threadO);

                        }
                    }
                    logger.info("withhold overdue job end!");

                }
            }
            logger.info("--------------- securityWithholdJob run end,time=" + new Date());
        } catch (Exception e){
            logger.error("securityWithholdJob  error, case=",e);
        }
    }


}
