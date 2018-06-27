package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.bo.XgxyOverdueBo;
import com.ald.fanbei.api.biz.bo.XgxyPayBo;
import com.ald.fanbei.api.biz.bo.XgxyRepayBo;
import com.ald.fanbei.api.biz.bo.XgxyRepayReqBo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
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
 * @author jilong
 * @version
 * @类描述：通知任务
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class NoticeTask {
    Logger logger = LoggerFactory.getLogger(NoticeTask.class);

    private Map<Long,String> all_noticedfail_moreonce=new HashMap<>();

    private final int[] request_times={480,120,30,10};

    @Resource
    private DsedNoticeRecordService dsedNoticeRecordService;

    @Resource
    private DsedLoanService dsedLoanService;

    @Resource
    private DsedLoanPeriodsService dsedLoanPeriodsService;

    @Resource
    private DsedLoanRepaymentService dsedLoanRepaymentService;


    @Resource
    private XgxyUtil xgxyUtil;

    @Scheduled(cron = "0 0/5  * * * ?")
    public void notice() {
        logger.info("start notice task， time="+new Date());
        List<DsedNoticeRecordDo> noticeRecordDos = dsedNoticeRecordService.getAllFailNoticeRecord();
        if(noticeRecordDos.size()==0){
            logger.info("noticeJob run finished,noticeJob size is 0.time=" + new Date());
        }else {
            for (final DsedNoticeRecordDo recordDo : noticeRecordDos) {
                if("0".equals(recordDo.getTimes())){
                    logger.info("dsed notice record more max count"+recordDo);
                    continue;
                }
                DsedLoanDo loanDo=null;
                DsedLoanRepaymentDo loanRepaymentDo=null;
                DsedLoanPeriodsDo periodsDo=null;
                if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.PAY.code)) {
                    loanDo=dsedLoanService.getById(Long.valueOf(recordDo.getRefId()));
                    if(loanDo==null || loanDo.getIsDelete()==1){
                        dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                    }else {
                        updateNoticeRecord(recordDo,xgxyUtil.payNoticeRequest(buildePayBo(loanDo)));
                    }
                    continue;
                }
                if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.REPAY.code)) {
                    loanRepaymentDo = dsedLoanRepaymentService.getById(Long.valueOf(recordDo.getRefId()));
                    if(loanRepaymentDo==null|| loanRepaymentDo.getIsDelete()==1){
                        dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                    }else {
                        updateNoticeRecord(recordDo, xgxyUtil.rePayNoticeRequest(buildRepauBo(loanRepaymentDo)));
                    }
                    continue;
                }
                if(StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUE.code)){
                    periodsDo=dsedLoanPeriodsService.getById(Long.valueOf(recordDo.getRefId()));
                    if(periodsDo==null || periodsDo.getIsDelete()==1){
                        dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                    }else {
                        updateNoticeRecord(recordDo, xgxyUtil.overDueNoticeRequest(buildOverdue(periodsDo)));
                    }
                    continue;
                }
                if(StringUtils.isBlank(all_noticedfail_moreonce.get(recordDo.getRid()))){
                    DsedLoanDo finalLoanDo = loanDo;
                    DsedLoanRepaymentDo finalLoanRepaymentDo = loanRepaymentDo;
                    DsedLoanPeriodsDo finalPeriodsDo = periodsDo;
                    Thread thread = new Thread(){
                        public void run(){
                            nextNotice(recordDo, finalLoanDo, finalLoanRepaymentDo, finalPeriodsDo);
                        }
                    };
                    thread.start();
                }
            }
        }
        logger.info("end notice tasktime="+new Date());
    }

     void nextNotice(DsedNoticeRecordDo recordDo,DsedLoanDo loanDo,DsedLoanRepaymentDo loanRepaymentDo,DsedLoanPeriodsDo periodsDo){
         try {
             all_noticedfail_moreonce.put(recordDo.getRid(),"true");
             Thread.sleep(1000*60*request_times[Integer.parseInt(recordDo.getTimes())-1]);
             if(StringUtils.equals(recordDo.getType(), "PAY")&&loanDo!=null){
                 updateNoticeRecord(recordDo, xgxyUtil.payNoticeRequest(buildePayBo(loanDo)));
             }else if(StringUtils.equals(recordDo.getType(), "REPAY")&&loanRepaymentDo!=null) {
                 updateNoticeRecord(recordDo, xgxyUtil.rePayNoticeRequest(buildRepauBo(loanRepaymentDo)));
             }else if(periodsDo!=null) {
                 updateNoticeRecord(recordDo, xgxyUtil.overDueNoticeRequest(buildOverdue(periodsDo)));
             }
             all_noticedfail_moreonce.remove(recordDo.getRid());
         } catch (InterruptedException e) {
             logger.info("dsed notice is fail"+recordDo);
         }

     }



    void updateNoticeRecord(DsedNoticeRecordDo recordDo,Boolean noticeStatus){
        if(noticeStatus){
            dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
        }else {
            dsedNoticeRecordService.updateNoticeRecordTimes(buildRecord(recordDo));
        }
    }


    DsedNoticeRecordDo buildRecord(DsedNoticeRecordDo recordDo){
        DsedNoticeRecordDo buildRecord=new DsedNoticeRecordDo();
        buildRecord.setRid(recordDo.getRid());
        buildRecord.setTimes(String.valueOf(Integer.parseInt(recordDo.getTimes())-1));
        return buildRecord;
    }

    HashMap<String,String>  buildRepauBo(DsedLoanRepaymentDo loanRepaymentDo){
       DsedLoanDo loanDo = dsedLoanService.getById(loanRepaymentDo.getLoanId());
       HashMap<String,String> data = new HashMap<String,String>();
       List<HashMap<String,String>> borrowBillDetails = new ArrayList<HashMap<String,String>>();
       HashMap<String,String> details = new HashMap<String,String>();
       data.put("amount", String.valueOf(loanRepaymentDo.getActualAmount()));
       data.put("borrowNo",loanDo.getLoanNo());
       data.put("status","REPAYSUCCESS");
       data.put("tradeNo",loanRepaymentDo.getTradeNo());
       String[] repayPeriodsIds = loanRepaymentDo.getRepayPeriods().split(",");
       for (int i = 0; i < repayPeriodsIds.length; i++) {
           // 获取分期信息
           DsedLoanPeriodsDo loanPeriodsDo = dsedLoanPeriodsService.getById(Long.parseLong(repayPeriodsIds[i]));
           if(loanPeriodsDo!=null){	// 提前还款,已出账的分期借款,还款金额=分期本金+手续费+利息（+逾期费）
               if(loanRepaymentDo.getPreRepayStatus().equals("Y")) {	// 提前还款
                   details.put("isFinish","Y");
               }else if(loanRepaymentDo.getPreRepayStatus().equals("N")) {		// 按期还款（部分还款）
                   if(StringUtil.equals("FINISHED",loanPeriodsDo.getStatus())){
                       details.put("isFinish","Y");
                   }else {
                       details.put("isFinish","N");
                   }
               }
               details.put("curPeriod",loanPeriodsDo.getNper().toString());
               BigDecimal amount = BigDecimalUtil.add(loanPeriodsDo.getAmount(),
                       loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
                       loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
                       loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
                       .subtract(loanPeriodsDo.getRepayAmount());
               details.put("unrepayAmount",amount.toString());
               details.put("unrepayInterestFee",loanPeriodsDo.getInterestFee().toString());
               details.put("unrepayOverdueFee",loanPeriodsDo.getOverdueAmount().toString());
               details.put("unrepayServiceFee",loanPeriodsDo.getServiceFee().toString());
           }
           borrowBillDetails.add(details);
           data.put("borrowBillDetails",JSONObject.toJSONString(borrowBillDetails));
           details.clear();
       }
       return data;
    }
   XgxyPayBo buildePayBo(DsedLoanDo loanDo){
       XgxyPayBo payBo=new XgxyPayBo();
       payBo.setTrade(loanDo.getTradeNoOut());
       payBo.setBorrowNo(String.valueOf(loanDo.getRid()));
       payBo.setGmtArrival(loanDo.getGmtArrival());
       payBo.setReason(loanDo.getRemark());
       payBo.setStatus(loanDo.getStatus());
       return payBo;
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
