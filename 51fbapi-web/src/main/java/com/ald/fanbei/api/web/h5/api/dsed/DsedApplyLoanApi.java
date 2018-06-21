package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.bo.dsed.DsedApplyLoanBo;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.DsedApplyLoanParam;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 发起贷款申请
 *
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.ApplyLegalBorrowCashV2Api}
 */
@Component("dsedApplyLoanApi")
@Validator("dsedApplyLoanParam")
public class DsedApplyLoanApi implements H5Handle {

    @Resource
    private DsedLoanService dsedLoanService;
    @Resource
    private DsedUserService userService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);

        DsedApplyLoanBo bo = new DsedApplyLoanBo();
        map((DsedApplyLoanParam) context.getParamEntity(), bo);

        bo.reqParam.ip = context.getClientIp();
        String reqId = context.getId();
        bo.reqParam.appType = reqId.startsWith("i") ? "alading_ios" : "alading_and";
        bo.reqParam.appName = reqId.substring(reqId.lastIndexOf("_") + 1, reqId.length());

        bo.userId = context.getUserId();
        bo.userName = context.getUserName();

        dsedLoanService.doLoan(bo);

        return resp;
    }

    public void map(DsedApplyLoanParam p, DsedApplyLoanBo bo) {
        DsedApplyLoanBo.ReqParam rp = bo.reqParam;
        rp.amount = p.amount;
        rp.prdType = p.prdType;
        rp.periods = p.periods;
        rp.payPwd = p.payPwd;
        rp.remark = p.remark;
        rp.loanRemark = p.loanRemark;
        rp.repayRemark = p.repayRemark;
        rp.city = p.city;
        rp.province = p.province;
        rp.county = p.county;
        rp.address = p.address;
        rp.latitude = p.latitude;
        rp.longitude = p.longitude;
        rp.blackBox = p.blackBox;
        rp.bqsBlackBox = p.bqsBlackBox;
    }

}
