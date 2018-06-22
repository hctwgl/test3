package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.vo.DsedLoanPeriodsVo;
import com.ald.fanbei.api.web.vo.DsedLoanVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yanghailong
 * @Description: 白领贷借款信息
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2018年1月22日
 */
@Component("dsedGetLoanInfoApi")
public class GetLoanInfoApi implements H5Handle {

    @Resource
    DsedLoanService dsedLoanService;
    @Resource
    DsedLoanPeriodsService dsedLoanPeriodsService;
    @Resource
    DsedUserService dsedUserService;


    @Override
    public H5HandleResponse process(Context context) {

        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        try {
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
            loanVo.setIsPeriod(loanDo.getPeriods() > 1 ? "Y" : "N");
            loanVo.setStatus(loanDo.getStatus());
            if (dsedLoanPeriodList.size() != 0) {
                dsedLoanPeriodList.forEach(dsedLoanPeriodsDo -> dsedLoanPeriodsVos.add(buildLoanPeriodsVo(dsedLoanPeriodsDo)));
                loanVo.setDsedLoanPeriodsVoList(dsedLoanPeriodsVos);
            }
            resp.setResponseData(loanVo);

        } catch (Exception e) {
            logger.error("/loanInfoApi error = {}", e.getStackTrace());
            resp.setResponseData("获取借款信息失败");
        }

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
        dsedLoanPeriodsVo.setUnrepayAmount(BigDecimalUtil.add(dsedLoanPeriodsDo.getAmount(), dsedLoanPeriodsDo.getInterestFee(),
                dsedLoanPeriodsDo.getServiceFee(), dsedLoanPeriodsDo.getOverdueAmount()));
        return dsedLoanPeriodsVo;
    }

}
