package com.ald.fanbei.api.web.h5.api.dsed;

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
import com.ald.fanbei.api.web.vo.DsedLoanPeriodsVo;
import com.ald.fanbei.api.web.vo.DsedLoanVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yanghailong
 * @Description: 白领贷借款信息
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2018年1月22日
 */
@Component("dsedGetLoanInfoApi")
public class GetLoanInfoApi implements DsedH5Handle {

    @Resource
    DsedLoanService dsedLoanService;
    @Resource
    DsedLoanPeriodsService dsedLoanPeriodsService;
    @Resource
    DsedUserService dsedUserService;


    @Override
    public DsedH5HandleResponse process(Context context) {

        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        Long userId = context.getUserId();
        if (userId == null || userId <= 0) {
            throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
        }

        // 借款信息
        DsedLoanDo loanDo = dsedLoanService.getByUserId(userId);
        if (loanDo == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_DETAIL_NOT_EXIST_ERROR);
        }

        // 当前待还分期信息
        List<DsedLoanPeriodsDo> dsedLoanPeriodList = dsedLoanPeriodsService.getLoanPeriodsByLoanId(loanDo.getRid());

        DsedUserDo userDo = dsedUserService.getById(context.getUserId());
        if (userDo == null) {
            throw new FanbeiException("dsedGetLoanInfoApi userDo is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        DsedLoanVo loanVo = new DsedLoanVo();
        List<DsedLoanPeriodsVo> dsedLoanPeriodsVos = new ArrayList<>();
        loanVo.setBorrowNo(loanDo.getLoanNo());
        loanVo.setStatus(loanDo.getStatus());
        if (dsedLoanPeriodList.size() != 0) {
            dsedLoanPeriodList.forEach(dsedLoanPeriodsDo -> dsedLoanPeriodsVos.add(buildLoanPeriodsVo(dsedLoanPeriodsDo)));
            loanVo.setBorrowBillDetails(dsedLoanPeriodsVos);
        }
        resp.setData(loanVo);

        return resp;
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
        dsedLoanPeriodsVo.setUnrepayAmount(BigDecimalUtil.add(dsedLoanPeriodsDo.getAmount(), dsedLoanPeriodsDo.getInterestFee(),
                dsedLoanPeriodsDo.getServiceFee(), dsedLoanPeriodsDo.getOverdueAmount()));
        return dsedLoanPeriodsVo;
    }

}
