package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.bo.XgxyOverdueBo;
import com.ald.fanbei.api.biz.bo.XgxyPayBo;
import com.ald.fanbei.api.biz.bo.XgxyRepayBo;
import com.ald.fanbei.api.biz.bo.XgxyRepayReqBo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
import com.alibaba.fastjson.JSONObject;
import net.sf.json.JSONArray;
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
    private CollectionSystemUtil collectionSystemUtil;

    @Resource
    private XgxyUtil xgxyUtil;

//    @Scheduled(cron = "0/5 * * * * ?")
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
                        updateNoticeRecord(recordDo, xgxyUtil.dsedRePayNoticeRequest(dsedLoanRepaymentService.buildData(loanRepaymentDo)));
                    }
                    continue;
                }
                if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUEREPAY.code)) {
                    loanRepaymentDo = dsedLoanRepaymentService.getById(Long.valueOf(recordDo.getRefId()));
                    if(loanRepaymentDo==null|| loanRepaymentDo.getIsDelete()==1){
                        dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                    }else {
                        updateNoticeRecord(recordDo, collectionSystemUtil.dsedRePayNoticeRequest(dsedLoanRepaymentService.buildData(loanRepaymentDo)));
                    }
                    continue;
                }
                if(StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUEREPAY.code)){
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
         all_noticedfail_moreonce.put(recordDo.getRid(),"true");
         try {
             Thread.sleep(1000*60*request_times[Integer.parseInt(recordDo.getTimes())-1]);
             if(StringUtils.equals(recordDo.getType(), DsedNoticeType.PAY.code)&&loanDo!=null){
                 updateNoticeRecord(recordDo, xgxyUtil.payNoticeRequest(buildePayBo(loanDo)));
             }else if(StringUtils.equals(recordDo.getType(), DsedNoticeType.REPAY.code)&&loanRepaymentDo!=null) {
                 updateNoticeRecord(recordDo, xgxyUtil.dsedRePayNoticeRequest(dsedLoanRepaymentService.buildData(loanRepaymentDo)));
             }else if(StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUE.code)&&periodsDo!=null) {
                 updateNoticeRecord(recordDo, xgxyUtil.overDueNoticeRequest(buildOverdue(periodsDo)));
             }else if(StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUEREPAY.code)&&loanRepaymentDo!=null){
                 updateNoticeRecord(recordDo, collectionSystemUtil.dsedRePayNoticeRequest(dsedLoanRepaymentService.buildData(loanRepaymentDo)));
             }
         } catch (Exception e) {
             logger.info("dsed notice is fail"+recordDo);
         }finally {
             all_noticedfail_moreonce.remove(recordDo.getRid());
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

   XgxyPayBo buildePayBo(DsedLoanDo loanDo){
       XgxyPayBo  payBo = new XgxyPayBo();
       payBo.setTrade(loanDo.getTradeNoOut());
       payBo.setBorrowNo(loanDo.getLoanNo());
       payBo.setReason(loanDo.getRemark());
       payBo.setStatus(loanDo.getStatus());
       payBo.setGmtArrival(loanDo.getGmtArrival());
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
