package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.vo.MgrBorrowInfoAnalysisVo;
import com.ald.fanbei.api.biz.vo.MgrDashboardInfoVo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashAnalysisService;
import com.ald.jsd.mgr.biz.service.MgrBorrowCashService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * 极速贷ServiceImpl
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("jsdBorrowCashAnalysisService")
public class MgrBorrowCashAnalysisServiceImpl implements MgrBorrowCashAnalysisService {

    @Resource
    private MgrBorrowCashService mgrBorrowCashService;
    @Resource
    private MgrUserAuthService mgrUserAuthService;

    @Override
    public MgrBorrowInfoAnalysisVo getBorrowInfoAnalysis(Integer days) {
        List<JsdBorrowCashDo> jsdBorrowCashDoList = mgrBorrowCashService.getBorrowCashByDays(days);
        MgrBorrowInfoAnalysisVo mgrBorrowInfoAnalysisVo = new MgrBorrowInfoAnalysisVo();
        BigDecimal totalLoanAmount = BigDecimal.ZERO;
        BigDecimal returnedRate = BigDecimal.ZERO;//回款率
        BigDecimal returnAmount = BigDecimal.ZERO;//回款金额
        BigDecimal overdueAmount = BigDecimal.ZERO;//逾期金额
        BigDecimal dueAmount = BigDecimal.ZERO;//到期金额
        Integer borrowMans = 0;//放款人数
        BigDecimal repeatBorrowRate = BigDecimal.ZERO;//复借率
        BigDecimal overdueRate = BigDecimal.ZERO;//逾期率
        BigDecimal profitRate = BigDecimal.ZERO;//不良率
        BigDecimal riskPassRate = BigDecimal.ZERO;//认证通过率
        BigDecimal borrowPassRate = BigDecimal.ZERO;//借款通过率
        for (JsdBorrowCashDo borrow : jsdBorrowCashDoList) {
            totalLoanAmount = totalLoanAmount.add(borrow.getAmount());
            borrowMans++;
            if (StringUtils.equals(borrow.getStatus(), "FINSH")) {
                returnAmount.add(borrow.getRepayAmount());
            }
            if (StringUtils.equals(borrow.getOverdueStatus(), "Y")) {
                overdueAmount.add(borrow.getAmount().subtract(borrow.getRepayAmount().subtract(borrow.getSumRepaidInterest())
                        .subtract(borrow.getSumRepaidOverdue()).subtract(borrow.getSumRepaidPoundage())));
            }
            if (borrow.getGmtPlanRepayment().before(new Date())) {
                dueAmount.add(borrow.getAmount());
            }
        }
        int applyBorrowCashNum = mgrBorrowCashService.getApplyBorrowCashByDays(days);//申请借款人数
        if (dueAmount != BigDecimal.ZERO){
            overdueRate = overdueAmount.divide(dueAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
            returnedRate = returnAmount.divide(dueAmount).setScale(2,BigDecimal.ROUND_HALF_UP);//回款金额
        }
        if (applyBorrowCashNum != 0){
            borrowPassRate = new BigDecimal(borrowMans).divide(new BigDecimal(applyBorrowCashNum));//借款通过人数
        }
        if (totalLoanAmount != BigDecimal.ZERO){
            profitRate = (returnAmount.subtract(overdueAmount)).divide(totalLoanAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
        }
        int allUserNum = mgrUserAuthService.getPassPersonNumByStatusAndDays("", days);
        int paseUserNum = mgrUserAuthService.getPassPersonNumByStatusAndDays("Y", days);
        if (allUserNum  != 0){
            riskPassRate = new BigDecimal(paseUserNum).divide(new BigDecimal(allUserNum)).setScale(4, BigDecimal.ROUND_HALF_UP);
        }
        mgrBorrowInfoAnalysisVo.setRiskPassRate(riskPassRate);
        mgrBorrowInfoAnalysisVo.setTotalLoanAmount(totalLoanAmount);
        mgrBorrowInfoAnalysisVo.setBorrowMans(borrowMans);
        mgrBorrowInfoAnalysisVo.setBorrowPassRate(borrowPassRate);
        mgrBorrowInfoAnalysisVo.setReturnedRate(returnedRate);
        mgrBorrowInfoAnalysisVo.setOverdueRate(overdueRate);
        mgrBorrowInfoAnalysisVo.setProfitRate(profitRate);
        return mgrBorrowInfoAnalysisVo;
    }

    @Override
    public MgrDashboardInfoVo getBorrowInfoDashboard() {
        return null;
    }

}
