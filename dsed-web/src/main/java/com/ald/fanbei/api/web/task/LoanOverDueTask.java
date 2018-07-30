package com.ald.fanbei.api.web.task;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.enums.DsedLoanPeriodStatus;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.XgxyOverdueBo;
import com.ald.fanbei.api.biz.service.DsedLoanOverdueLogService;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;
import com.ald.fanbei.api.biz.service.DsedUserContactsService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanOverdueLogDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.DsedUserContactsDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;


/**
 *@类描述：借钱逾期利息，逾期手续费计算 每日零点执行一次
 *@author jilong
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class LoanOverDueTask {
    Logger logger = LoggerFactory.getLogger(LoanOverDueTask.class);

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

    @Resource
    GetHostIpUtil getHostIpUtil;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_XGXY_NOTICE_HOST);
    
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 0/5 * * * ?")
    public void laonDueJob(){
        try{
        	String curHostIp = getHostIpUtil.getIpAddress();
        	logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
        	if(StringUtils.equals(getHostIpUtil.getIpAddress(), NOTICE_HOST)){
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
                        //通知催收逾期人员通讯录
                       collectionPush(loanDos);
                    }

                }
                logger.info("laonDueJob run end,time=" + new Date());
        	}
        } catch (Exception e){
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
                logger.info("calcuOverdueRecords do dsedLoanDo, loanId="+dsedLoanDo.getLoanId());
                if(loanOverdueLogService.getLoanOverDueLogByNow(String.valueOf(dsedLoanDo.getRid()))!=null){
                   continue;
                }
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
                //发送补偿通知到西瓜信用
                overDueNotice(dsedLoanDo);
                addUserContancts(dsedLoanDo.getUserId());

            } catch (Exception e) {
                logger.error("LoanOverDueTask calcuOverdueRecords error, legal loanId="+dsedLoanDo.getLoanId());
            }

        }


   }
   void addUserContancts(Long userId){
       DsedUserDo userDo=userService.getById(userId);
       String contacts=xgxyUtil.getUserContactsInfo(userDo.getOpenId());
       if(StringUtils.isNotBlank(contacts)){
           List<DsedUserContactsDo> userContactsDo= contactsService.getUserContactsByUserId(String.valueOf(userId));
           DsedUserContactsDo contactsDo=new DsedUserContactsDo();
            contactsDo.setUserId(String.valueOf(userId));
            contactsDo.setContactsMobile(contacts);
            if(userContactsDo.size()==0){
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
           data.put("caseName","dsed_"+dsedLoanDo.getNper()+"/"+dsedLoanDo.getPeriods());
           data.put("planRepaymenTime", DateUtil.formatDateTime(dsedLoanDo.getGmtPlanRepay()));
           BigDecimal currentAmount = BigDecimalUtil.add(dsedLoanDo.getAmount(), dsedLoanDo.getRepaidOverdueAmount(),dsedLoanDo.getRepaidInterestFee(), dsedLoanDo.getRepaidServiceFee()).subtract(dsedLoanDo.getRepayAmount());//应还金额
           data.put("residueAmount", String.valueOf(BigDecimalUtil.add(currentAmount,dsedLoanDo.getOverdueAmount(),dsedLoanDo.getInterestFee(),dsedLoanDo.getOverdueAmount(),dsedLoanDo.getServiceFee())));
           data.put("principal", String.valueOf(currentAmount));
           data.put("overdueAmount", String.valueOf(dsedLoanDo.getOverdueAmount()));
           data.put("nper", String.valueOf(dsedLoanDo.getNper()));
           data.put("userId", String.valueOf(userDo.getRid()));
           data.put("realName",userDo.getRealName());
           data.put("idNumber",userDo.getIdNumber());
           data.put("payTime", DateUtil.formatDateTime(dsedLoanDo.getGmtArrival()));
           data.put("phoneNumber",userDo.getMobile());
           data.put("address",userDo.getAddress());
           data.put("userName",userDo.getMobile());
           data.put("periods", String.valueOf(dsedLoanDo.getPeriods()));
           data.put("productName","XGXY");
           data.put("borrowAmount",String.valueOf(dsedLoanDo.getAmount()));
           data.put("appName","dsed");
           data.put("repaymentPeriod","1");
           data.put("type",dsedLoanDo.getStatus());
           data.put("repayAmount",String.valueOf(dsedLoanDo.getRepayAmount()));
           data.put("amount",String.valueOf(dsedLoanDo.getAmount()));
           StringBuffer sb = new StringBuffer();
           List<DsedLoanPeriodsDo> list=dsedLoanPeriodsService.getLoanPeriodsByLoanId(dsedLoanDo.getLoanId());
           for(DsedLoanPeriodsDo dsedLoan : list){
               if(StringUtil.equals(dsedLoan.getStatus(),DsedLoanPeriodStatus.FINISHED.name())){
                   sb.append(dsedLoan.getNper()).append(",");
               }
           }
           if(sb.length() > 0){
               sb = sb.deleteCharAt(sb.length()-1);
           }
           data.put("havePaied",sb.toString());
           data.put("overdueDay",String.valueOf(dsedLoanDo.getOverdueDays()));
           data.put("overdueAmount",String.valueOf(BigDecimalUtil.add(dsedLoanDo.getOverdueAmount(),dsedLoanDo.getRepaidOverdueAmount())));
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


   private DsedLoanOverdueLogDo buildLoanOverdueLog(Long periodsId,BigDecimal currentAmount,BigDecimal interest,Long userId){
       DsedLoanOverdueLogDo overdueLog = new DsedLoanOverdueLogDo();
       overdueLog.setPeriodsId(periodsId);
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
