package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


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

    @Scheduled(cron = "* 0/5 * * * ?")
    public boolean laonDueJob(){


        try{
            int pageSize = 200;
            int totalRecord = dsedLoanService.getLoanOverdueCount();
            int totalPageNum =totalRecord/pageSize+1;
            if (totalRecord == 0) {
                logger.info("laonDueJob run finished,LoanList size is 0.time=" + new Date());
                return false;
            }
            logger.info("laonDueJob run start,time=" + new Date());
            List<DsedLoanDo>  loanDos;
            for(int i = 0; i < totalPageNum; i++){
                loanDos=dsedLoanService.getLoanOverdue(totalPageNum*i,pageSize);
                this.calcuOverdueRecords(loanDos);
            }

        }catch (Exception e){

        }
        return true;
    }
   void calcuOverdueRecords(List<DsedLoanDo> loanDos){

        for(DsedLoanDo dsedLoanDo:loanDos){
            try {
                BigDecimal overdueAmount = this.resolveOverdueAmount(overdueRate, now, borrowCashDo, limitOverdueAmountResource);
                if(overdueAmount.compareTo(BigDecimal.ZERO) > 0) {
                    this.update(borrowCashDo, overdueAmount);
                }
            } catch (Exception e) {
                logger.error("borrowCashOverdueJob calcuOverdueRecords error, legal borrowCashId=", borrowCashDo.getRid());
            }

        }
   }



    /**
     * 解析当前借款需付逾期费
     *
     * @param dsedLoanDo
     */
    private BigDecimal resolveOverdueAmount(DsedLoanDo dsedLoanDo) {

        return BigDecimal.valueOf(0);
    }


}
