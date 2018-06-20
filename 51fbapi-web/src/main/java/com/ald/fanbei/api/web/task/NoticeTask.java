package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private XgxyUtil xgxyUtil;

    @Scheduled(cron = "* 0/5 * * * ?")
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
                final DsedLoanDo loanDo=dsedLoanService.getById(Long.valueOf(recordDo.getRefId()));
                if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.PAY.code)) {
                    updateNoticeRecord(recordDo,xgxyUtil.payNoticeRequest(loanDo));
                    continue;
                }
                if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.REPAY.code)) {
                    updateNoticeRecord(recordDo, xgxyUtil.rePayNoticeRequest(loanDo));
                    continue;
                }
                if(StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUE.code)){
                    final DsedLoanPeriodsDo periodsDo=dsedLoanPeriodsService.getById(Long.valueOf(recordDo.getRefId()));
                    updateNoticeRecord(recordDo, true);
                    continue;
                }
                if(StringUtils.isBlank(all_noticedfail_moreonce.get(recordDo.getRid()))){
                    Thread thread = new Thread(){
                        public void run(){
                            nextNotice(recordDo,loanDo);
                        }
                    };
                    thread.start();
                }
                logger.info("end notice tasktime="+new Date());
            }
        }

    }

     void nextNotice(DsedNoticeRecordDo recordDo,DsedLoanDo loanDo){
         try {
             all_noticedfail_moreonce.put(recordDo.getRid(),"true");
             Thread.sleep(1000*60*request_times[Integer.parseInt(recordDo.getTimes())-1]);
             if(StringUtils.equals(recordDo.getType(), "PAY")){
                 updateNoticeRecord(recordDo, xgxyUtil.payNoticeRequest(loanDo));
             }else if(StringUtils.equals(recordDo.getType(), "REPAY")) {
                 updateNoticeRecord(recordDo, xgxyUtil.rePayNoticeRequest(loanDo));
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




    boolean payNotice(DsedLoanDo loanDo){
        xgxyUtil.payNoticeRequest(loanDo);
        return true;
    }

    boolean rePayNotice(DsedLoanDo loanDo){
        xgxyUtil.rePayNoticeRequest(loanDo);
        return true;
    }

}
