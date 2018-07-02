package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.bo.dsed.DsedApplyLoanBo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.DsedApplyLoanParam;
import com.ald.fanbei.api.web.vo.DsedLoanPeriodsVo;
import com.ald.fanbei.api.web.vo.DsedLoanVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 发起贷款申请
 *
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.ApplyLegalBorrowCashV2Api}
 */
@Component("dsedApplyLoanApi")
@Validator("dsedApplyLoanParam")
public class DsedApplyLoanApi implements DsedH5Handle {

    @Resource
    private DsedLoanService dsedLoanService;
    @Resource
    private DsedUserService dsedUserService;
    @Resource
    private DsedLoanPeriodsService dsedLoanPeriodsService;

    @Override
    public DsedH5HandleResponse process(Context context) {

        DsedApplyLoanBo bo = new DsedApplyLoanBo();
        map((DsedApplyLoanParam) context.getParamEntity(), bo);

        bo.reqParam.ip = context.getClientIp();
        bo.userId = context.getUserId();
        bo.userName = context.getUserName();
        bo.idNumber = String.valueOf(context.getIdNumber());
        bo.realName = String.valueOf(context.getRealName());
        dsedLoanService.doLoan(bo);
        DsedLoanVo dsedLoanVo = getDsedInfo(bo.userId);
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功", dsedLoanVo);
        return resp;
    }

    public void map(DsedApplyLoanParam p, DsedApplyLoanBo bo) {
        DsedApplyLoanBo.ReqParam rp = bo.reqParam;
        rp.amount = p.amount;
        rp.prdType = "DSED_LOAN";
        rp.periods = p.period;
        rp.remark = p.remark;
        rp.loanRemark = p.loanRemark;
        rp.repayRemark = p.repayRemark;
    }

    public DsedLoanVo getDsedInfo(Long userId){
        // 借款信息
        DsedLoanDo loanDo = dsedLoanService.getByUserId(userId);
        if (loanDo == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_DETAIL_NOT_EXIST_ERROR);
        }

        // 当前待还分期信息
        List<DsedLoanPeriodsDo> dsedLoanPeriodList = dsedLoanPeriodsService.getLoanPeriodsByLoanId(loanDo.getRid());

        DsedLoanVo loanVo = new DsedLoanVo();
        List<DsedLoanPeriodsVo> dsedLoanPeriodsVos = new ArrayList<>();
        loanVo.setBorrowNo(loanDo.getLoanNo());
        loanVo.setStatus(loanDo.getStatus());
        loanVo.setServiceRate(loanDo.getServiceRate());
        loanVo.setInterestRate(loanDo.getInterestRate());
        loanVo.setTotalInterestFee(loanDo.getTotalInterestFee());
        loanVo.setTotalServiceFee(loanDo.getTotalServiceFee());
        loanVo.setBorrowNo(loanDo.getLoanNo());
        loanVo.setArrivalAmount(loanDo.getArrivalAmount());
        if (dsedLoanPeriodList.size() != 0) {
            dsedLoanPeriodList.forEach(dsedLoanPeriodsDo -> dsedLoanPeriodsVos.add(buildLoanPeriodsVo(dsedLoanPeriodsDo)));
            loanVo.setBorrowBillDetails(dsedLoanPeriodsVos);
        }
        return loanVo;
    }


    public DsedLoanPeriodsVo buildLoanPeriodsVo(DsedLoanPeriodsDo dsedLoanPeriodsDo) {
        DsedLoanPeriodsVo dsedLoanPeriodsVo = new DsedLoanPeriodsVo();
        dsedLoanPeriodsVo.setAmount(dsedLoanPeriodsDo.getAmount());
        dsedLoanPeriodsVo.setCurPeriod(dsedLoanPeriodsDo.getNper());
        dsedLoanPeriodsVo.setGmtPlanRepay(dsedLoanPeriodsDo.getGmtPlanRepay());
        dsedLoanPeriodsVo.setStatus(dsedLoanPeriodsDo.getStatus());
        dsedLoanPeriodsVo.setTotalAmount(BigDecimalUtil.add(dsedLoanPeriodsDo.getAmount(), dsedLoanPeriodsDo.getInterestFee(),
                dsedLoanPeriodsDo.getServiceFee(), dsedLoanPeriodsDo.getOverdueAmount(), dsedLoanPeriodsDo.getRepaidInterestFee(),
                dsedLoanPeriodsDo.getRepaidServiceFee(), dsedLoanPeriodsDo.getRepayAmount(), dsedLoanPeriodsDo.getRepaidOverdueAmount()
        ));
        dsedLoanPeriodsVo.setTotalPeriod(dsedLoanPeriodsDo.getPeriods());
        dsedLoanPeriodsVo.setUnrepayInterestFee(dsedLoanPeriodsDo.getInterestFee());
        dsedLoanPeriodsVo.setUnrepayOverdueFee(dsedLoanPeriodsDo.getOverdueAmount());
        dsedLoanPeriodsVo.setUnrepayServiceFee(dsedLoanPeriodsDo.getServiceFee());
        dsedLoanPeriodsVo.setInterestFee(dsedLoanPeriodsDo.getInterestFee().add(dsedLoanPeriodsDo.getRepaidInterestFee()));
        dsedLoanPeriodsVo.setServiceFee(dsedLoanPeriodsDo.getServiceFee().add(dsedLoanPeriodsDo.getRepaidServiceFee()));
        dsedLoanPeriodsVo.setUnrepayAmount(BigDecimalUtil.add(dsedLoanPeriodsDo.getAmount(), dsedLoanPeriodsDo.getInterestFee(),
                dsedLoanPeriodsDo.getServiceFee(), dsedLoanPeriodsDo.getOverdueAmount()));
        return dsedLoanPeriodsVo;
    }

}
