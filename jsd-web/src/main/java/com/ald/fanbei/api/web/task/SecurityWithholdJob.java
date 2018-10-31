package com.ald.fanbei.api.web.task;


import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


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
    private WithholdJob withholdJob;


//    @Scheduled(cron = "0/5 * * * * ?")
    public void laonDueJob(){
        try{
        	logger.info("--------------- securityWithholdJob run start,time=" + new Date());
            JsdResourceDo resDo = jsdResourceService.getByTypeAngSecType(ResourceType.OVERDUE.getCode(), ResourceSecType.OVERDUE_JOB_INTERNAL_UIDS.getCode());
            String userIds = resDo.getValue();
            Date bengin = DateUtil.getTodayLast();
            List<JsdBorrowCashDo> borrowCashDos= borrowCashService.getBorrowCashRepayByUserIds(userIds.substring(0,userIds.length()-1),bengin);
            withholdJob.dealWithhold(borrowCashDos);
            logger.info("--------------- securityWithholdJob run end,time=" + new Date());
        } catch (Exception e){
            logger.error("securityWithholdJob  error, case=",e);
        }
    }


}
