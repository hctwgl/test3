package com.ald.fanbei.api.web.h5.api.jsd;

import com.ald.fanbei.api.biz.bo.jsd.UntieBankCardBo;
import com.ald.fanbei.api.biz.bo.ups.UpsSignReleaseRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.HashMap;


/**
 * @author cfp 2018年11月23日下午1:06:18
 * @类描述：银行卡解绑
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("untieBankCardApi")
@Validator("untieBankCardReq")
public class UntieBankCardApi implements JsdH5Handle {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdUserBankcardService jsdUserBankcardService;
    @Resource
    UpsUtil upsUtil;
    @Resource
    JsdUserService jsdUserService;


    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        UntieBankCardBo.UntieBankCardReq cashReq = (UntieBankCardBo.UntieBankCardReq)context.getParamEntity();
        Long userId = context.getUserId();
        JsdUserDo jsdUserDo = jsdUserService.getById(userId);
        HashMap<String,Object> map = jsdUserBankcardService.getBankByBankNoAndUserId(userId,cashReq.bankNo);
        UpsSignReleaseRespBo upsResult = upsUtil.signRelease(userId+"", map.get("bankCode").toString(),
                jsdUserDo.getRealName(), map.get("mobile").toString(), jsdUserDo.getIdNumber(),
                map.get("cardNumber").toString(), "02");
        if(!upsResult.isSuccess()){
            throw new BizException("sign release error", BizExceptionCode.SIGN_RELEASE_ERROR);
        }
        JsdUserBankcardDo afUserBankcardDo = new JsdUserBankcardDo();
        afUserBankcardDo.setRid(Long.parseLong(map.get("rid").toString()));
        afUserBankcardDo.setUserId(userId);
        afUserBankcardDo.setStatus(BankcardStatus.UNBIND.getCode());
        jsdUserBankcardService.updateUserBankcard(afUserBankcardDo);
        return resp;

    }


}
