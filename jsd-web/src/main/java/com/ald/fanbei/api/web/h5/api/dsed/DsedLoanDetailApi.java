package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.ald.fanbei.api.web.vo.DsedLoanDetailVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 贷款发起前确认
 *
 * @author ZJF
 */
@Component("dsedLoanDetailApi")
public class DsedLoanDetailApi implements DsedH5Handle {

    @Resource
    private DsedLoanService dsedLoanService;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");

        String prdType = "DSED_LOAN";
        BigDecimal amount = new BigDecimal(context.getData("amount").toString());
        int period = Integer.valueOf(context.getData("period").toString());

        DsedLoanDo dsedLoanDo = dsedLoanService.resolveLoan(amount, context.getUserId(), period, null, prdType);
        DsedLoanDetailVo dsedLoanDetailVo = DsedLoanDetailVo.gen(period,dsedLoanDo.getServiceRate(),dsedLoanDo.getInterestRate(),dsedLoanDo.getOverdueRate(),
        dsedLoanDo.getArrivalAmount(),dsedLoanDo.getTotalServiceFee(),dsedLoanDo.getTotalInterestFee());
        resp.setData(dsedLoanDetailVo);

        return resp;
    }

}
