package com.ald.fanbei.api.web.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.XgxyOverdueBo;
import com.ald.fanbei.api.biz.bo.XgxyPayBo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;

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

    ExecutorService executor = Executors.newFixedThreadPool(10);

    @Resource
    GetHostIpUtil getHostIpUtil;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_XGXY_NOTICE_HOST);

    @Scheduled(cron = "0 0/5 * * * ?")
    public void notice() {
    	try {
    		String curHostIp = getHostIpUtil.getIpAddress();
        	logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            if(StringUtils.equals(getHostIpUtil.getIpAddress(), NOTICE_HOST)){
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
                        final DsedLoanRepaymentDo loanRepaymentDo=dsedLoanRepaymentService.getById(Long.valueOf(recordDo.getRefId()));
                        final DsedLoanPeriodsDo periodsDo=dsedLoanPeriodsService.getById(Long.valueOf(recordDo.getRefId()));
                        if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.PAY.code)) {
                            if(loanDo==null || loanDo.getIsDelete()==1){
                                dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else {
                                updateNoticeRecord(recordDo,xgxyUtil.payNoticeRequest(buildePayBo(loanDo)));
                            }
                            continue;
                        }
                        if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.REPAY.code)) {
                            if(loanRepaymentDo==null|| loanRepaymentDo.getIsDelete()==1){
                                dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else {
                                updateNoticeRecord(recordDo, xgxyUtil.dsedRePayNoticeRequest(dsedLoanRepaymentService.buildData(loanRepaymentDo)));
                            }
                            continue;
                        }
                        if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUEREPAY.code)) {
                            updateNoticeRecord(recordDo, collectionSystemUtil.dsedRePayNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
                            continue;
                        }
                        if (StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.COLLECT.code)) {
                            updateNoticeRecord(recordDo, collectionSystemUtil.noticeRiskCollect(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
                            continue;
                        }
                        if(StringUtils.equals(recordDo.getTimes(), "5") && StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUE.code)){
                            if(periodsDo==null || periodsDo.getIsDelete()==1){
                                dsedNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else {
                                updateNoticeRecord(recordDo, xgxyUtil.overDueNoticeRequest(buildOverdue(periodsDo)));
                            }
                            continue;
                        }
                        if(StringUtils.isBlank(all_noticedfail_moreonce.get(recordDo.getRid()))){
                            Runnable thread = new Runnable(){
                                public void run(){
                                    nextNotice(recordDo, loanDo, loanRepaymentDo, periodsDo);
                                }
                            };
                            executor.execute(thread);
                        }
                    }
                }
                logger.info("end notice tasktime="+new Date());
            }
    	}catch (Exception e) {
    		logger.error(e.getMessage(), e);
		}
    }

     void nextNotice(DsedNoticeRecordDo recordDo,DsedLoanDo loanDo,DsedLoanRepaymentDo loanRepaymentDo,DsedLoanPeriodsDo periodsDo){
         all_noticedfail_moreonce.put(recordDo.getRid(),recordDo.getTimes());
         try {
             Thread.sleep(1000*60*request_times[Integer.parseInt(recordDo.getTimes())-1]);
             if(StringUtils.equals(recordDo.getType(), DsedNoticeType.PAY.code)&&loanDo!=null){
                 updateNoticeRecord(recordDo, xgxyUtil.payNoticeRequest(buildePayBo(loanDo)));
             }else if(StringUtils.equals(recordDo.getType(), DsedNoticeType.REPAY.code)&&loanRepaymentDo!=null) {
                 updateNoticeRecord(recordDo, xgxyUtil.dsedRePayNoticeRequest(dsedLoanRepaymentService.buildData(loanRepaymentDo)));
             }else if(StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUE.code)&&periodsDo!=null) {
                 updateNoticeRecord(recordDo, xgxyUtil.overDueNoticeRequest(buildOverdue(periodsDo)));
             }else if(StringUtils.equals(recordDo.getType(), DsedNoticeType.OVERDUEREPAY.code)){
                 updateNoticeRecord(recordDo, collectionSystemUtil.dsedRePayNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
             }else if(StringUtils.equals(recordDo.getType(), DsedNoticeType.COLLECT.code)){
                 updateNoticeRecord(recordDo, collectionSystemUtil.noticeRiskCollect(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
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
