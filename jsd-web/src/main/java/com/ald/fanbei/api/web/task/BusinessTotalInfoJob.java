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
                            Integer loanNum = 0;
                            if (StringUtils.equals(arr[i],"all")){
                                //放款笔数
                                loanNum = getLoanNum(arr[i]);
                                //借款申请金额

                                //实际出款金额

                                //商品搭售金额

                                //应还款金额

                                //正常还款金额

                                //总还款金额

                                //应还款金额

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
                            }else {

                            }
                            infoDo.setLoanNum(loanNum.longValue());
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

    /**
     * 放款笔数(期限)
     */
    public Integer getLoanNum(String nper){
        if(StringUtils.equals(nper,"all")){
            jsdBorrowCashService.getBorrowCashByTodayCount()
        }else {

        }
        return 0;
    }

    /**
     * 放款笔数(全部)
     */
    public void getLoanNumByAll(){

    }


}
