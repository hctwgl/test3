package com.ald.fanbei.api.web.task;


import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.SecurityReaderUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *@类描述：借钱逾期利息，逾期手续费计算 每日零点执行一次
 *@author jilong
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class SecurityLoanOverDueJob {
    Logger logger = LoggerFactory.getLogger(SecurityLoanOverDueJob.class);



    @Resource
    private JsdBorrowCashService borrowCashService;

    @Resource
    private LoanOverDueJob loanOverDueJob;

    @Resource
    GetHostIpUtil getHostIpUtil;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_XGXY_NOTICE_HOST);

    @Scheduled(cron = "0/5 * * * * ?")
    public void laonDueJob(){
        String curHostIp = getHostIpUtil.getIpAddress();
        logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
         try{
                if(StringUtils.equals(getHostIpUtil.getIpAddress(), NOTICE_HOST)) {
                    String userIds = SecurityReaderUtil.getProperties();
                    List<JsdBorrowCashDo> borrowCashDo = borrowCashService.getBorrowCashOverdueByUserIds(userIds.substring(0, userIds.length() - 1));
                    loanOverDueJob.dealOverdueRecords(borrowCashDo);
                    logger.info("securityLoanOverDueJob run end,time=" + new Date());
                }
        } catch (Exception e){
            logger.error("securityLoanOverDueJob  error, case=",e);
        }
    }


}
