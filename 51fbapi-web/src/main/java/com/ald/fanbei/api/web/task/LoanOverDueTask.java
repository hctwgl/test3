package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.bo.XgxyOverdueBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


/**
 *@类描述：借钱逾期利息，逾期手续费计算 每日零点执行一次
 *@author jilong
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class LoanOverDueTask {
    Logger logger = LoggerFactory.getLogger(NoticeTask.class);

    @Resource
    private DsedLoanService dsedLoanService;


    @Resource
    private DsedLoanPeriodsService dsedLoanPeriodsService;

    @Resource
    private DsedLoanRepaymentService dsedLoanRepaymentService;

    @Resource
    private DsedLoanOverdueLogService loanOverdueLogService;

    @Resource
    private  DsedNoticeRecordService noticeRecordService;

    @Resource
    private DsedUserService userService;

    @Resource
    private XgxyUtil xgxyUtil;

    @Resource
    private CollectionSystemUtil collectionSystemUtil;

    @Resource
    private DsedUserContactsService contactsService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void laonDueJob(){
        try{
            int pageSize = 200;
            int totalRecord = dsedLoanPeriodsService.getLoanOverdueCount();
            int totalPageNum =totalRecord/pageSize+1;
            if (totalRecord == 0) {
                logger.info("laonDueJob run finished,Loan Due size is 0.time=" + new Date());
            }else {
                logger.info("laonDueJob run start,time=" + new Date());
                List<DsedLoanPeriodsDto>  loanDos;
                for(int i = 0; i < totalPageNum; i++){
                    loanDos=dsedLoanPeriodsService.getLoanOverdue(totalPageNum*i,pageSize);
                    //计算逾期
                    this.calcuOverdueRecords(loanDos);
                    //通知催收
                    collectionPush(loanDos);
                }

            }
            logger.info("laonDueJob run end,time=" + new Date());
        }catch (Exception e){
            logger.error("laonDueJob  error, case=",e);
        }
    }
    /**
     * 解析当前借款需付逾期费
     *
     * @param loanDos
     */
   void calcuOverdueRecords(List<DsedLoanPeriodsDto> loanDos){
        for(DsedLoanPeriodsDto dsedLoanDo:loanDos){
            try {
                BigDecimal currentAmount = BigDecimalUtil.add(dsedLoanDo.getAmount(), dsedLoanDo.getRepaidOverdueAmount(),dsedLoanDo.getRepaidInterestFee(), dsedLoanDo.getRepaidServiceFee()).subtract(dsedLoanDo.getRepayAmount());// 当前本金
                DsedLoanRepaymentDo loanRepaymentDo=dsedLoanRepaymentService.getProcessingRepayment(dsedLoanDo.getLoanId(),dsedLoanDo.getNper());
                if(loanRepaymentDo!=null){
                    currentAmount = BigDecimalUtil.add(dsedLoanDo.getAmount(), dsedLoanDo.getRepaidOverdueAmount(),dsedLoanDo.getRepaidInterestFee(), dsedLoanDo.getRepaidServiceFee()).subtract(dsedLoanDo.getRepayAmount()).subtract(loanRepaymentDo.getRepayAmount());// 当前本金
                }
                if (currentAmount.compareTo(BigDecimal.ZERO) == 0) {
                    continue;
                }
                if(null==dsedLoanDo.getGmtArrival()&&dsedLoanDo.getArrivalAmount()==0){
                    continue;
                }
                BigDecimal oldOverdueAmount = dsedLoanDo.getOverdueAmount();//当前逾期
                BigDecimal newOverdueAmount = currentAmount.multiply(dsedLoanDo.getOverdueRate().divide(new BigDecimal(360),6,BigDecimal.ROUND_HALF_UP)).setScale(2,BigDecimal.ROUND_HALF_UP);
                dsedLoanDo.setOverdueAmount(oldOverdueAmount.add(newOverdueAmount));
                dsedLoanDo.setOverdueDays(dsedLoanDo.getOverdueDays()+1);
                dsedLoanDo.setOverdueStatus("Y");
                dsedLoanPeriodsService.updateById(dsedLoanDo);
                DsedLoanDo newloanDo = new DsedLoanDo();
                newloanDo.setRid(dsedLoanDo.getLoanId());
                newloanDo.setOverdueDays(dsedLoanDo.getOverdueDays());
                newloanDo.setOverdueAmount(dsedLoanDo.getOverdueAmount());
                dsedLoanService.updateByLoanId(newloanDo);
                //新增逾期日志
                loanOverdueLogService.addLoanOverdueLog(buildLoanOverdueLog(dsedLoanDo.getRid(), currentAmount, newOverdueAmount, dsedLoanDo.getUserId()));
                //发送通知
                overDueNotice(dsedLoanDo);
                addUserContancts(dsedLoanDo.getUserId());

            } catch (Exception e) {
                logger.error("LoanOverDueTask calcuOverdueRecords error, legal loanId=",dsedLoanDo.getLoanId());
            }

        }


   }
   void addUserContancts(Long userId){
       DsedUserDo userDo=userService.getById(userId);
       String contacts=xgxyUtil.getUserContactsInfo(userDo.getOpenId());
       if(StringUtils.isNotBlank(contacts)){
           DsedUserContactsDo userContactsDo= contactsService.getUserContactsByUserId(String.valueOf(userId)).get(0);
           DsedUserContactsDo contactsDo=new DsedUserContactsDo();
            contactsDo.setUserId(String.valueOf(userId));
            contactsDo.setContactsMobile(contacts);
            if(userContactsDo==null){
                 contactsService.saveRecord(contactsDo);
            }else {
                contactsService.updateByUserId(contactsDo);
            }
       }

   }
   void  collectionPush(List<DsedLoanPeriodsDto> dsedLoanDos){
       List<Map<String,String>> datas=new ArrayList<>();
       for(DsedLoanPeriodsDto dsedLoanDo:dsedLoanDos){
           DsedUserDo userDo=userService.getById(dsedLoanDo.getUserId());
           Map<String,String> data=new HashMap<>();
           data.put("dataId", String.valueOf(dsedLoanDo.getRid()));
           data.put("caseName",dsedLoanDo.getLoanNo());
           data.put("planRepaymenTime", String.valueOf(dsedLoanDo.getGmtPlanRepay()));
           BigDecimal currentAmount = BigDecimalUtil.add(dsedLoanDo.getAmount(), dsedLoanDo.getRepaidOverdueAmount(),dsedLoanDo.getRepaidInterestFee(), dsedLoanDo.getRepaidServiceFee()).subtract(dsedLoanDo.getRepayAmount());// 当前本金
           data.put("residueAmount", String.valueOf(currentAmount.add(dsedLoanDo.getOverdueAmount())));
           data.put("principal", String.valueOf(currentAmount));
           data.put("overdueAmount", String.valueOf(dsedLoanDo.getOverdueAmount()));
           data.put("nper", String.valueOf(dsedLoanDo.getNper()));
           data.put("userId", String.valueOf(userDo.getRid()));
           data.put("realName",userDo.getRealName());
           data.put("idNumber",userDo.getIdNumber());
           data.put("payTime", String.valueOf(dsedLoanDo.getGmtArrival()));
           data.put("phoneNumber",userDo.getMobile());
           data.put("address",userDo.getAddress());
           data.put("userName",userDo.getUserName());
           data.put("productName","XGXY");
           datas.add(data);
       }
        collectionSystemUtil.noticeCollect(datas);
    }

   void  overDueNotice(DsedLoanPeriodsDto loanOverDue){
       DsedNoticeRecordDo noticeRecordDo=new DsedNoticeRecordDo();
       noticeRecordDo.setUserId(loanOverDue.getUserId());
       noticeRecordDo.setRefId(String.valueOf(loanOverDue.getRid()));
       noticeRecordDo.setType(DsedNoticeType.OVERDUE.code);
       noticeRecordService.addNoticeRecord(noticeRecordDo);
       if(xgxyUtil.overDueNoticeRequest(buildOverdue(loanOverDue))){
           noticeRecordDo.setRid(noticeRecordDo.getRid());
           noticeRecordDo.setGmtModified(new Date());
           noticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
       }
   }


   private DsedLoanOverdueLogDo buildLoanOverdueLog(Long loanId,BigDecimal currentAmount,BigDecimal interest,Long userId){
       DsedLoanOverdueLogDo overdueLog = new DsedLoanOverdueLogDo();
       overdueLog.setPeriodsId(loanId);
       overdueLog.setCurrentAmount(currentAmount);
       overdueLog.setInterest(interest);
       overdueLog.setUserId(userId);
       return overdueLog;
   }
    XgxyOverdueBo buildOverdue(DsedLoanPeriodsDo periodsDo){
        XgxyOverdueBo overdueBo=new XgxyOverdueBo();
        overdueBo.setBorrowNo(periodsDo.getLoanNo());
        overdueBo.setCurPeriod(String.valueOf(periodsDo.getNper()));
        overdueBo.setOverdueDays(periodsDo.getOverdueDays());
        overdueBo.setTradeNo(overdueBo.getTradeNo());
        return overdueBo;
    }

}
