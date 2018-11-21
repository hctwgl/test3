package com.ald.fanbei.api.web.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.RepayRequestBo;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.RepayType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;

@Component
public class WithholdJob {

    Logger logger = LoggerFactory.getLogger(WithholdJob.class);

    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Resource
    private JsdUserService jsdUserService;

    @Resource
    private JsdUserBankcardService jsdUserBankcardService;

    @Resource
    GeneratorClusterNo generatorClusterNo;

    @Resource
    JsdResourceService jsdResourceService;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_TASK_ACTIVE_HOST);

    private static String SWITCH = "open";

    ExecutorService executor = Executors.newFixedThreadPool(10);




    @Scheduled(cron = "0 50 23 * * ?")
    public void withhold() {
        JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.getCode(), ResourceSecType.WITHHOLD_JOB_CONFIG.getCode());
        if(resourceDo != null && SWITCH.equals(resourceDo.getValue())){
            String curHostIp = GetHostIpUtil.getIpAddress();
            logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            try {
                if (StringUtils.equals(GetHostIpUtil.getIpAddress(), NOTICE_HOST)) {
                    Date bengin = DateUtil.getTodayLast();
                    int pageSize = 200;
                    int totalRecord = jsdBorrowCashService.getBorrowCashByBeforeTodayCount(bengin);
                    int totalPageNum = (totalRecord + pageSize - 1) / pageSize;
                    if (totalRecord == 0) {
                        logger.info("withhold run finished,Loan Due size is 0.time=" + new Date());
                    } else {
                        logger.info("withhold run start,time=" + new Date());
                        for (int i = 0; i < totalPageNum; i++) {
                            List<JsdBorrowCashDo> borrowCashDos = jsdBorrowCashService.getBorrowCashByBeforeToday(pageSize * i, pageSize,bengin);
                            dealWithhold(borrowCashDos);
                        }
                    }
                    logger.info("withhold run end,time=" + new Date());
                }
            } catch (Exception e) {
                logger.error("withhold  error, case=", e);
            }
        }

    }

    void  dealWithhold(List<JsdBorrowCashDo> borrowCashDos){
        for(JsdBorrowCashDo jsdBorrowCashDo:borrowCashDos){
            JsdBorrowCashRepaymentDo borrowCashRepaymentDo=jsdBorrowCashRepaymentService.getLastRepaymentBorrowCashByBorrowId(jsdBorrowCashDo.getRid());
            if(borrowCashRepaymentDo != null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(borrowCashRepaymentDo.getStatus())) {
                logger.info("withhold fail,Loan is processing,borrowId=" + jsdBorrowCashDo.getRid());
                continue;
            }
            RepayRequestBo bo=new RepayRequestBo();
            BigDecimal sumAmount = BigDecimalUtil.add(jsdBorrowCashDo.getAmount(), jsdBorrowCashDo.getSumRepaidOverdue(),jsdBorrowCashDo.getSumRepaidInterest(), jsdBorrowCashDo.getSumRepaidPoundage(),
                    jsdBorrowCashDo.getOverdueAmount(),jsdBorrowCashDo.getPoundageAmount(),jsdBorrowCashDo.getInterestAmount()).subtract(jsdBorrowCashDo.getRepayAmount());// 当前剩余还款

            JsdBorrowLegalOrderCashDo borrowLegalOrderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashDateBeforeToday(jsdBorrowCashDo.getRid());
            if(borrowLegalOrderCashDo!=null){
                BigDecimal orderAmount = BigDecimalUtil.add(borrowLegalOrderCashDo.getAmount(), borrowLegalOrderCashDo.getSumRepaidInterest(), borrowLegalOrderCashDo.getSumRepaidPoundage(),
                        borrowLegalOrderCashDo.getOverdueAmount(),borrowLegalOrderCashDo.getInterestAmount(),borrowLegalOrderCashDo.getPoundageAmount()).subtract(borrowLegalOrderCashDo.getRepaidAmount());// 当前剩余还款
                sumAmount=sumAmount.add(orderAmount);
            }
            JsdUserDo userDo=jsdUserService.getById(jsdBorrowCashDo.getUserId());
            bo.amount=sumAmount;
            bo.borrowId=jsdBorrowCashDo.getRid();
            bo.userId = jsdBorrowCashDo.getUserId();
            bo.borrowNo = jsdBorrowCashDo.getBorrowNo();
            bo.period = "1";
            bo.userDo = userDo;
            bo.name = Constants.DEFAULT_WITHHOLD_NAME_BORROW_CASH;
            JsdUserBankcardDo userBankcardDo=jsdUserBankcardService.getMainBankByUserId(jsdBorrowCashDo.getUserId());
            bo.bankNo=userBankcardDo.getBankCardNumber();
            bo.repayType = RepayType.WITHHOLD.name();
            Runnable thread= new Runnable() {
                @Override
                public void run() {
                    jsdBorrowCashRepaymentService.repay(bo,RepayType.WITHHOLD.getCode());
                }
            };
            executor.submit(thread);
        }

    }
}
