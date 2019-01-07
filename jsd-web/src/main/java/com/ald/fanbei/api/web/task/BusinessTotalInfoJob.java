package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 *@类描述： 每日1点执行一次
 *@author jilong
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class BusinessTotalInfoJob {
    Logger logger = LoggerFactory.getLogger(BusinessTotalInfoJob.class);

    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    JsdTotalInfoService jsdTotalInfoService;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_TASK_ACTIVE_HOST);

    @Scheduled(cron = "0 10 1 * * ?")
    public void laonDueJob(){
        try{
            String curHostIp = GetHostIpUtil.getIpAddress();
            logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            if(StringUtils.equals(GetHostIpUtil.getIpAddress(), NOTICE_HOST)){
                try{
                    JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(),ResourceSecType.JSD_RATE_INFO.name());
                    if(null != resourceDo && StringUtils.isNotBlank(resourceDo.getTypeDesc())){
                        List<JsdTotalInfoDo> list = new ArrayList<>();
                        String[] arr = resourceDo.getTypeDesc().split(",");
                        for (int i=0;arr.length>i;i++){
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, -1);
                            String date = DateUtil.formatDate(calendar.getTime(),DateUtil.DEFAULT_PATTERN_WITH_HYPHEN);
                            JsdTotalInfoDo infoDo = new JsdTotalInfoDo();
                            //放款笔数
                            Integer loanNum = jsdBorrowCashService.getLoanNum(arr[i],date);
                            infoDo.setLoanNum(loanNum.longValue());
                            //借款申请金额
                            BigDecimal appleAmount = jsdBorrowCashService.getAppleAmount(arr[i],date);
                            infoDo.setApplyAmount(appleAmount);
                            //实际出款金额
                            BigDecimal loanAmount = jsdBorrowCashService.getLoanAmount(arr[i],date);
                            infoDo.setLoanAmount(loanAmount);
                            //商品搭售金额
                            BigDecimal tyingAmount = jsdBorrowCashService.getTyingAmount(arr[i],date);
                            infoDo.setTyingAmount(tyingAmount);
                            //应还款金额
                            BigDecimal repaymentAmount = jsdBorrowCashService.getRepaymentAmount(arr[i],date);
                            infoDo.setRepaymentAmount(repaymentAmount);
                            //正常还款金额
                            BigDecimal normalAmount = jsdBorrowCashService.getNormalAmount(arr[i],date);
                            infoDo.setNormalAmount(normalAmount);
                            //总还款金额
                            BigDecimal sumRepaymentAmount = jsdBorrowCashRepaymentService.getSumRepaymentAmount(arr[i],date);
                            infoDo.setCountRepaymentAmount(sumRepaymentAmount);
                            //应还款笔数
                            Integer repaymentNum = jsdBorrowCashService.getRepaymentNum(arr[i],date);
                            infoDo.setRepaymentNum(repaymentNum.longValue());
                            //正常还款笔数
                            Integer normalNum = jsdBorrowCashService.getNormalNum(arr[i],date);
                            infoDo.setNormalNum(normalNum.longValue());
                            //总还款笔数
                            Integer sumRepaymentNum = jsdBorrowCashService.getSumRepaymentNum(arr[i],date);
                            infoDo.setCountRepaymentNum(sumRepaymentNum.longValue());
                            //展期笔数

                            //展期还本

                            //展期费用

                            //在展本金

                            //首逾率

                            //逾期率

                            //未回收率

                            //坏账金额

                            //盈利率

                            list.add(infoDo);
                        }
                        if(list.size()>0){
                            jsdTotalInfoService.saveAll(list);
                        }
                    }
                }catch (Exception e){
                    logger.info("error = ",e);
                    e.getMessage();
                }

            }
        } catch (Exception e){
            logger.error("borrowCashDueJob  error, case=",e);
        }
    }






}
