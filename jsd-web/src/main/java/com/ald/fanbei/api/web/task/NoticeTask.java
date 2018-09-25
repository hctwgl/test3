package com.ald.fanbei.api.web.task;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.third.util.CollectionNoticeUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdNoticeType;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.alibaba.fastjson.JSONObject;

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
    private JsdNoticeRecordService jsdNoticeRecordService;


    @Resource
    private CollectionNoticeUtil collectionNoticeUtil;



    @Resource
    private XgxyUtil xgxyUtil;

    ExecutorService executor = Executors.newFixedThreadPool(10);

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_TASK_ACTIVE_HOST);

    @SuppressWarnings("unchecked")
	@Scheduled(cron = "0 0/5 * * * ?")
    public void notice() {
    	try {
    		String curHostIp = GetHostIpUtil.getIpAddress();
        	logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            if(StringUtils.equals(GetHostIpUtil.getIpAddress(), NOTICE_HOST)){
                logger.info("start notice task， time="+new Date());
                List<JsdNoticeRecordDo> noticeRecordDos = jsdNoticeRecordService.getAllFailNoticeRecord();
                if(noticeRecordDos.size()==0){
                    logger.info("noticeJob run finished,noticeJob size is 0.time=" + new Date());
                }else {
                    for (final JsdNoticeRecordDo recordDo : noticeRecordDos) {
                        if("0".equals(recordDo.getTimes())){
                            logger.info("jsd notice record more max count"+recordDo);
                            continue;
                        }
                      
                        if (StringUtils.equals(recordDo.getTimes(), "5") && (StringUtils.equals(recordDo.getType(), JsdNoticeType.REPAY.code) ||
                                StringUtils.equals(recordDo.getType(), JsdNoticeType.COLLECT_RECONCILIATION.code) )) {
                            if(StringUtils.isBlank(recordDo.getParams())){
                                jsdNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else{
                                updateNoticeRecord(recordDo, xgxyUtil.repayNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
                            }
                            continue;
                        }
                        if (StringUtils.equals(recordDo.getTimes(), "5") && (StringUtils.equals(recordDo.getType(), JsdNoticeType.RENEW.code))) {
                            if(StringUtils.isBlank(recordDo.getParams())){
                                jsdNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else{
                                updateNoticeRecord(recordDo, xgxyUtil.jsdRenewalNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
                            }
                            continue;
                        }
                        if (StringUtils.equals(recordDo.getTimes(), "5") && (StringUtils.equals(recordDo.getType(), JsdNoticeType.BIND.code))) {
                            if(StringUtils.isBlank(recordDo.getParams())){
                                jsdNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else{
                                updateNoticeRecord(recordDo, xgxyUtil.bindBackNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
                            }
                            continue;
                        }
                        if(StringUtils.equals(recordDo.getTimes(), "5") && (StringUtils.equals(recordDo.getType(), JsdNoticeType.OVERDUE.code)
                                || StringUtils.equals(recordDo.getType(), JsdNoticeType.DELEGATEPAY.code))){
                            if(StringUtils.isBlank(recordDo.getParams())){
                                jsdNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else {
                                updateNoticeRecord(recordDo, xgxyUtil.borrowNoticeRequest(JSONObject.parseObject(recordDo.getParams(),XgxyBorrowNoticeBo.class)));
                            }
                            continue;
                        }
                        if (StringUtils.equals(recordDo.getTimes(), "5") && (StringUtils.equals(recordDo.getType(), JsdNoticeType.COLLECT_RENEW.code))) {
                            if(StringUtils.isBlank(recordDo.getParams())){
                                jsdNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
                            }else{
                                updateNoticeRecord(recordDo, collectionNoticeUtil.collectRenewal(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
                            }
                            continue;
                        }
                        if(StringUtils.isBlank(all_noticedfail_moreonce.get(recordDo.getRid()))){
                            Runnable thread = new Runnable(){
                                public void run(){
                                    nextNotice(recordDo);
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

     @SuppressWarnings("unchecked")
	void nextNotice(JsdNoticeRecordDo recordDo){
         all_noticedfail_moreonce.put(recordDo.getRid(),recordDo.getTimes());
         try {
             Thread.sleep(1000*60*request_times[Integer.parseInt(recordDo.getTimes())-1]);
             if(StringUtils.equals(recordDo.getType(), JsdNoticeType.DELEGATEPAY.code) || StringUtils.equals(recordDo.getType(), JsdNoticeType.OVERDUE.code)){
                 updateNoticeRecord(recordDo, xgxyUtil.borrowNoticeRequest(JSONObject.parseObject(recordDo.getParams(),XgxyBorrowNoticeBo.class)));
             }else if((StringUtils.equals(recordDo.getType(), JsdNoticeType.REPAY.code) || StringUtils.equals(recordDo.getType(), JsdNoticeType.COLLECT_RECONCILIATION.code))) {
                 updateNoticeRecord(recordDo, xgxyUtil.repayNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
             }else if(StringUtils.equals(recordDo.getType(), JsdNoticeType.RENEW.code)){
                 updateNoticeRecord(recordDo, xgxyUtil.jsdRenewalNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
             }else if(StringUtils.equals(recordDo.getType(), JsdNoticeType.BIND.code)){
                 updateNoticeRecord(recordDo, xgxyUtil.bindBackNoticeRequest(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
             }else if(StringUtils.equals(recordDo.getType(), JsdNoticeType.COLLECT_RENEW.code)){
                 updateNoticeRecord(recordDo, collectionNoticeUtil.collectRenewal(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
             }else if(StringUtils.equals(recordDo.getType(), JsdNoticeType.OVERDUEREPAY.code)){
                 updateNoticeRecord(recordDo, collectionNoticeUtil.consumerRepayment(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
             }else if(StringUtils.equals(recordDo.getType(), JsdNoticeType.COLLECT.code)){
                 updateNoticeRecord(recordDo, collectionNoticeUtil.consumerRepayment(JSONObject.parseObject(recordDo.getParams(),HashMap.class)));
             }
         } catch (Exception e) {
             logger.info("dsed notice is fail"+recordDo);
         }finally {
             all_noticedfail_moreonce.remove(recordDo.getRid());
         }
     }


    void updateNoticeRecord(JsdNoticeRecordDo recordDo,Boolean noticeStatus){
        if(noticeStatus){
            jsdNoticeRecordService.updateNoticeRecordStatus(buildRecord(recordDo));
        }else {
            jsdNoticeRecordService.updateNoticeRecordTimes(buildRecord(recordDo));
        }
    }


    JsdNoticeRecordDo buildRecord(JsdNoticeRecordDo recordDo){
        JsdNoticeRecordDo buildRecord=new JsdNoticeRecordDo();
        buildRecord.setRid(recordDo.getRid());
        buildRecord.setTimes(String.valueOf(Integer.parseInt(recordDo.getTimes())-1));
        if(StringUtils.isNotBlank(recordDo.getParams())){
            buildRecord.setParams(recordDo.getParams());
        }
        return buildRecord;
    }




}
