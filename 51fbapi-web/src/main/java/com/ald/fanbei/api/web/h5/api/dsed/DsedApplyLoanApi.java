package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.bo.dsed.DsedApplyLoanBo;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.DsedApplyLoanParam;
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
public class DsedApplyLoanApi implements DsedH5Handle {

    @Resource
    private DsedLoanService dsedLoanService;
    @Resource
    private DsedUserService userService;

    @Override
    public DsedH5HandleResponse process(Context context) {

        DsedApplyLoanBo bo = new DsedApplyLoanBo();
        map((DsedApplyLoanParam) context.getParamEntity(), bo);

        bo.reqParam.ip = context.getClientIp();
        bo.userId = context.getUserId();
        bo.userName = context.getUserName();
        bo.realName = String.valueOf(context.getDataMap().get("realName"));
        bo.idNumber = String.valueOf(context.getDataMap().get("idNumber"));
        dsedLoanService.doLoan(bo);

        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "", null);
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
