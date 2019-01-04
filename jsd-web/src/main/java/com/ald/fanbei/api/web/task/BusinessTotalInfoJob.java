package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.enums.XgxyBorrowNotifyStatus;
import com.ald.fanbei.api.biz.third.util.CollectionNoticeUtil;
import com.ald.fanbei.api.biz.third.util.JobThreadPoolUtils;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.JsdContractPdfDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;


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
                        String[] arr = resourceDo.getTypeDesc().split(",");
                        for (int i=0;arr.length>i;i++){
                            JsdTotalInfoDo infoDo = new JsdTotalInfoDo();
                            //放款笔数
                            Integer loanNum = jsdBorrowCashService.getLoanNum(arr[i]);
                            infoDo.setLoanNum(loanNum.longValue());
                            //借款申请金额
                            BigDecimal appleAmount = jsdBorrowCashService.getAppleAmount(arr[i]);
                            infoDo.setApplyAmount(appleAmount);
                            //实际出款金额
                            BigDecimal loanAmount = jsdBorrowCashService.getLoanAmount(arr[i]);
                            infoDo.setLoanAmount(loanAmount);
                            //商品搭售金额
                            BigDecimal tyingAmount = jsdBorrowCashService.getTyingAmount(arr[i]);
                            infoDo.setTyingAmount(tyingAmount);
                            //应还款金额
                            BigDecimal repaymentAmount = jsdBorrowCashService.getRepaymentAmount(arr[i]);
                            infoDo.setRepaymentAmount(repaymentAmount);
                            //正常还款金额
                            BigDecimal normalAmount = jsdBorrowCashService.getNormalAmount(arr[i]);
                            infoDo.setNormalAmount(normalAmount);
                            //总还款金额
                            BigDecimal sumRepaymentAmount = jsdBorrowCashRepaymentService.getSumRepaymentAmount(arr[i]);
                            infoDo.setCountRepaymentAmount(sumRepaymentAmount);
                            //应还款笔数
                            Integer repaymentNum = jsdBorrowCashService.getRepaymentNum(arr[i]);
                            infoDo.setRepaymentNum(repaymentNum.longValue());
                            //正常还款笔数

                            //总还款笔数

                            //展期笔数

                            //展期还本

                            //展期费用

                            //在展本金

                            //首逾率

                            //逾期率

                            //未回收率

                            //坏账金额

                            //盈利率


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
