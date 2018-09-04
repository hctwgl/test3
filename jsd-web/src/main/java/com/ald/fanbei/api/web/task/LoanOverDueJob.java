package com.ald.fanbei.api.web.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashOverdueLogService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashOverdueLogDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;


/**
 *@类描述：借钱逾期利息，逾期手续费计算 每日零点执行一次
 *@author jilong
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class LoanOverDueJob {
    Logger logger = LoggerFactory.getLogger(LoanOverDueJob.class);



    @Resource
    private XgxyUtil xgxyUtil;

    @Resource
    private CollectionSystemUtil collectionSystemUtil;

    @Resource
    private JsdBorrowCashService borrowCashService;

    @Resource
    private JsdBorrowCashOverdueLogService jsdBorrowCashOverdueLogService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    GetHostIpUtil getHostIpUtil;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_XGXY_NOTICE_HOST);

    @Scheduled(cron = "0/5 * * * * ?")
    public void laonDueJob(){
        try{
        	String curHostIp = getHostIpUtil.getIpAddress();
        	logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            String userIds = "";
            String envType = ConfigProperties.get(Constants.CONF_KEY_INVELOMENT_TYPE);
//            if(StringUtils.equals(getHostIpUtil.getIpAddress(), NOTICE_HOST)){
        		int pageSize = 200;
                int totalRecord = borrowCashService.getBorrowCashOverdueCount();
                int totalPageNum = (totalRecord + pageSize - 1) / pageSize;
                if (totalRecord == 0) {
                    logger.info("borrowCashDueJob run finished,Loan Due size is 0.time=" + new Date());
                }else {
                    logger.info("borrowCashDueJob run start,time=" + new Date());
                    if (Constants.INVELOMENT_TYPE_ONLINE.equals(envType) || Constants.INVELOMENT_TYPE_TEST.equals(envType)) {
                        List<JsdBorrowCashDo> borrowCashDos=borrowCashService.getBorrowCashOverdueByUserIds(userIds);
                        //计算逾期
                        this.calcuOverdueRecords(borrowCashDos);
                        //TODO 通知催收逾期人员通讯录
                    }else if(Constants.INVELOMENT_TYPE_PRE_ENV.equals(envType)){
                        for(int i = 0; i < totalPageNum; i++){
                            List<JsdBorrowCashDo> borrowCashDos=borrowCashService.getBorrowCashOverdue(totalPageNum*i,pageSize);
                            //计算逾期
                            this.calcuOverdueRecords(borrowCashDos);
                            //TODO 通知催收逾期人员通讯录
                        }
                    }


                }
                logger.info("borrowCashDueJob run end,time=" + new Date());
//        	}
        } catch (Exception e){
            logger.error("borrowCashDueJob  error, case=",e);
        }
    }
    /**
     * 解析当前借款需付逾期费
     *
     * @param
     */
   void calcuOverdueRecords(List<JsdBorrowCashDo> jsdBorrowCashDos){
        for(JsdBorrowCashDo jsdBorrowCashDo:jsdBorrowCashDos){
            try {
                logger.info("calcuOverdueRecords do borrowCashDueJob, borrowCashId="+jsdBorrowCashDo.getRid());
                if(jsdBorrowCashOverdueLogService.getBorrowCashOverDueLogByNow(String.valueOf(jsdBorrowCashDo.getRid()))>0){
                   continue;
                }
                BigDecimal currentAmount = BigDecimalUtil.add(jsdBorrowCashDo.getAmount(), jsdBorrowCashDo.getSumOverdue(),jsdBorrowCashDo.getSumRate(), jsdBorrowCashDo.getSumRenewalPoundage()).subtract(jsdBorrowCashDo.getRepayAmount());// 当前本金
                JsdBorrowCashRepaymentDo borrowCashRepaymentDo=jsdBorrowCashRepaymentService.getLastRepaymentBorrowCashByBorrowId(jsdBorrowCashDo.getRid());
                if(borrowCashRepaymentDo != null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(borrowCashRepaymentDo.getStatus())) {
                    currentAmount = BigDecimalUtil.add(jsdBorrowCashDo.getAmount(), jsdBorrowCashDo.getOverdueAmount(), jsdBorrowCashDo.getRateAmount(), jsdBorrowCashDo.getPoundage(), jsdBorrowCashDo.getSumRate(), jsdBorrowCashDo.getSumOverdue(), jsdBorrowCashDo.getSumRenewalPoundage()).subtract(jsdBorrowCashDo.getRepayAmount()).subtract(borrowCashRepaymentDo.getRepaymentAmount());// 当前本金
                }
                if (currentAmount.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                BigDecimal oldOverdueAmount = jsdBorrowCashDo.getOverdueAmount();//当前逾期
                BigDecimal newOverdueAmount = currentAmount.multiply(jsdBorrowCashDo.getOverdueRate().divide(new BigDecimal(360),6,BigDecimal.ROUND_HALF_UP)).setScale(2,BigDecimal.ROUND_HALF_UP);
                jsdBorrowCashDo.setOverdueAmount(oldOverdueAmount.add(newOverdueAmount));
                jsdBorrowCashDo.setOverdueDay(jsdBorrowCashDo.getOverdueDay()+1);
                jsdBorrowCashDo.setOverdueStatus("Y");
                borrowCashService.updateById(jsdBorrowCashDo);
                //新增逾期日志
                jsdBorrowCashOverdueLogService.saveRecord(buildLoanOverdueLog(jsdBorrowCashDo.getRid(), currentAmount, newOverdueAmount, jsdBorrowCashDo.getUserId()));
                //TODO 发送补偿通知到西瓜信用
//                xgxyUtil.overDueNoticeRequest(map);
            } catch (Exception e) {
                logger.error("LoanOverDueTask calcuOverdueRecords error, legal loanId="+jsdBorrowCashDo.getRid());
            }
        }

   }

   Map<String,String> buildOvardue(){
        return null;
   }
   private JsdBorrowCashOverdueLogDo buildLoanOverdueLog(Long borrowId,BigDecimal currentAmount,BigDecimal interest,Long userId){
       JsdBorrowCashOverdueLogDo overdueLog = new JsdBorrowCashOverdueLogDo();
       overdueLog.setBorrowId(borrowId);
       overdueLog.setCurrentAmount(currentAmount);
       overdueLog.setInterest(interest);
       overdueLog.setUserId(userId);
       return overdueLog;
   }


}
