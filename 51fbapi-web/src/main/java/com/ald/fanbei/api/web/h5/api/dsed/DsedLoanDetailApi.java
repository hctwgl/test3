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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 贷款发起前确认
 *
 * @author ZJF
 */
@NeedLogin
@Component("dsedLoanDetailApi")
public class DsedLoanDetailApi implements DsedH5Handle {

    @Resource
    private DsedLoanPeriodsService dsedLoanPeriodsService;

    @Resource
    private DsedLoanService dsedLoanService;

    @Resource
    private DsedUserBankcardService dsedUserBankcardService;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "");

        String prdType = context.getData("prdType").toString();
        prdType = "DSED_LOAN";
        BigDecimal amount = new BigDecimal(context.getData("amount").toString());
        int periods = Integer.valueOf(context.getData("periods").toString());

        DsedLoanDo dsedLoanDo = dsedLoanService.resolveLoan(amount, context.getUserId(), periods, null, prdType);
        resp.setData(dsedLoanDo);

        return resp;
    }

}
