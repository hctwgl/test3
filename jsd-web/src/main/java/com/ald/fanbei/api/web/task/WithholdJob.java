package com.ald.fanbei.api.web.task;


import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class WithholdJob {

    Logger logger = LoggerFactory.getLogger(WithholdJob.class);

    @Resource
    private WithholdCurrentDay withholdCurrentDay;

    @Resource
    private WithholdOverdueDay withholdOverdueDay;

    @Resource
    JsdResourceService jsdResourceService;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_TASK_ACTIVE_HOST);

    private static String SWITCH = "open";

    ExecutorService executor = Executors.newFixedThreadPool(8);


    @Scheduled(cron = "0 0/1 * * * ?")
    public void withhold() {
        Date date = new Date(System.currentTimeMillis());
        Date bengin = DateUtil.getEndOfDate(date);
        JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceSecType.WITHHOLD_JOB_CONFIG.getCode());
        if(resourceDo != null && SWITCH.equals(resourceDo.getValue())){
            String curHostIp = GetHostIpUtil.getIpAddress();
            logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            if (StringUtils.equals(GetHostIpUtil.getIpAddress(), NOTICE_HOST)) {
               String currentTime= DateUtil.formatDate(new Date(),"HH:mm");
               Map<String,String> config= (Map<String, String>) JSON.parse(resourceDo.getValue2());
               excutorWithholdCurrent(config,currentTime,bengin);
                JSONObject overdueSection=JSONObject.fromObject(config.get("overdueSection"));
                BigDecimal minSection=new BigDecimal(String.valueOf(overdueSection.get("minSection")));
                BigDecimal maxSection=new BigDecimal(String.valueOf(overdueSection.get("maxSection")));
                if(minSection.compareTo(BigDecimal.ZERO)<0||maxSection.compareTo(BigDecimal.ZERO)<=0
                 ||minSection.compareTo(maxSection)>0 ){
                    logger.info("withhold overdue section is zero or null");
                }else {
                    excutorWithholdOverdue(config,currentTime,bengin);
                }
            }

        }

    }

    void excutorWithholdCurrent(Map<String,String> config,String currentTime,Date bengin){
        Collection currentWithholdTime=JSONArray.toCollection(JSONArray.fromObject(config.get("currentWithholdTime")),List.class);
        if(currentWithholdTime.size()!=0){
            Iterator currentIterator=currentWithholdTime.iterator();
            while (currentIterator.hasNext()){
                String withholdTime= String.valueOf(currentIterator.next());
                if(currentTime.equals(withholdTime)){

                }
                Runnable threadC= new Runnable() {
                    @Override
                    public void run() {
                        withholdCurrentDay.withhold(config,bengin);
                    }
                };
                executor.submit(threadC);

            }
        }
    }

    void excutorWithholdOverdue(Map<String,String> config,String currentTime,Date bengin){
        Collection overdueWithholdTime=JSONArray.toCollection(JSONArray.fromObject(config.get("overdueWithholdTime")),List.class);
        if(overdueWithholdTime.size()!=0){
            Iterator overdueIterator=overdueWithholdTime.iterator();
            while (overdueIterator.hasNext()){
                String withholdTime= String.valueOf(overdueIterator.next());
                if(currentTime.equals(withholdTime)){
                    Runnable threadO= new Runnable() {
                        @Override
                        public void run() {
                            withholdOverdueDay.withhold(config,bengin);
                        }
                    };
                    executor.submit(threadO);
                }


            }
        }
       logger.info("withhold overdue job end!");

    }



}
