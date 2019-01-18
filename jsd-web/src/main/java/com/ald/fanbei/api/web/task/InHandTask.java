package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.dingding.DingdingUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRenewalDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRepaymentDao;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.dto.InHandTaskDto;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *@类描述：查询七天内状态是处理中的借款
 *@author yafei.li
 * @Date 2019-1-11
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class InHandTask {
    Logger logger = LoggerFactory.getLogger(InHandTask.class);

    @Resource
    KafkaTemplate kafkaTemplate;
    @Resource
    JsdBorrowCashDao jsdBorrowCashDao;
    @Resource
    JsdBorrowCashRepaymentDao jsdBorrowCashRepaymentDao;
    @Resource
    JsdBorrowCashRenewalDao jsdBorrowCashRenewalDao;
    @Resource
    JsdBorrowLegalOrderRepaymentDao jsdBorrowLegalOrderRepaymentDao;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_TASK_ACTIVE_HOST);

    @Scheduled(cron = "0 0/5 * * * ?")
    public void InHand(){
        try{
            String curHostIp = GetHostIpUtil.getIpAddress();
            logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            if(StringUtils.equals(curHostIp, NOTICE_HOST)){
                logger.info("InHandTask run start,time=" + new Date());
                List<InHandTaskDto> list = jsdBorrowCashDao.getInHand();
                List<InHandTaskDto> list2 = jsdBorrowCashRenewalDao.getInHand();
                List<InHandTaskDto> list3 = jsdBorrowCashRepaymentDao.getInHand();
                List<InHandTaskDto> list4 = jsdBorrowLegalOrderRepaymentDao.getInHand();
                list.addAll(list2);
                list.addAll(list3);
                list.addAll(list4);
                if (null == list || list.size() == 0){
                    logger.info("InHandTask is no data");
                } else {
                    kafkaTemplate.send(ConfigProperties.get(Constants.KAFKA_ALD_UPS_STATUS_REQUST), JSON.toJSONString(list));
                }
                logger.info("InHandTask run end,time=" + new Date());
            }
        } catch (Exception e){
            DingdingUtil.sendMessageByJob(NOTICE_HOST +"，查询处理中借款定时器执行失败！",true);
            logger.error("InHandTask  error, case=",e);
        }
    }
}
