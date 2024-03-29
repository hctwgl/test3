package com.ald.fanbei.api.web.task;


import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.dingding.DingdingUtil;
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


    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    private WithholdCurrentDay withholdCurrentDay;

    @Resource
    private WithholdOverdueDay withholdOverdueDay;

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;
    ExecutorService executor = Executors.newFixedThreadPool(8);

    public static String WEBHOOK_TOKEN = "https://oapi.dingtalk.com/robot/send?access_token=25e746714401a5f51249ffe4b9796325ee83e38d76f7e9122a7202c4835b6968";


    private static String SWITCH = "open";

    @Scheduled(cron = "0 0/5 * * * ?")
    public void laonDueJob(){
        logger.info("--------------- securityWithholdJob run start,time=" + new Date());
        try{
            JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceSecType.WITHHOLD_JOB_CONFIG_YF.getCode());
            if(resourceDo != null && SWITCH.equals(resourceDo.getValue())){
                JsdResourceDo resDo = jsdResourceService.getByTypeAngSecType(ResourceType.WITHHOLD.getCode(), ResourceSecType.WITHHOLD_JOB_INTERNAL_UIDS.getCode());
                String userIds = resDo.getValue();
                Date bengin = DateUtil.getTodayLast();
                String currentTime= DateUtil.formatDate(new Date(),"HH:mm");
                Map<String,String> config= (Map<String, String>) JSON.parse(resourceDo.getValue2());
                Collection currentWithholdTime=JSONArray.toCollection(JSONArray.fromObject(config.get("currentWithholdTime")),List.class);
                String cardType=config.get("cardType");
                String failCount=config.get("failCount");
                if(currentWithholdTime.size()!=0){
                    Iterator currentIterator=currentWithholdTime.iterator();
                    while (currentIterator.hasNext()){
                        String withholdTime= String.valueOf(currentIterator.next());
                        Runnable threadC= new Runnable() {
                            @Override
                            public void run() {
                                List<JsdBorrowCashDo> borrowCashDos = jsdBorrowCashService.getTodayBorrowCashRepayByUserIds(userIds,bengin);
                                jsdBorrowCashRepaymentService.lockBorrowList(borrowCashDos);
                                jsdBorrowCashRepaymentService.dealWithhold(borrowCashDos,cardType);
                            }
                        };
                        executor.submit(threadC);
                    }
                }

                JSONObject overdueSection=JSONObject.fromObject(config.get("overdueSection"));
                Integer minSection=Integer.parseInt((String) overdueSection.get("minSection"));
                Integer maxSection= Integer.parseInt((String) overdueSection.get("maxSection"));
                if(minSection<0||maxSection<=0
                        ||minSection.compareTo(maxSection)>0){
                    logger.info("withhold overdue section is zero or null");
                }else {
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
                                    jsdBorrowCashRepaymentService.lockBorrowList(borrowCashDos);
                                    jsdBorrowCashRepaymentService.dealWithhold(borrowCashDos,cardType);
                                }
                            };
                            executor.submit(threadO);

                        }
                    }

                }
            }
        } catch (Exception e){
            DingdingUtil.sendMessageByJob("代扣定时器执行失败！",true);
            logger.error("securityWithholdJob  error, case=",e);
        }
    }


}
