package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.service.DsedBankService;
import com.ald.fanbei.api.biz.service.DsedUserBankcardService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedBankDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("getSmsCode")
public class GetSmsCode implements DsedH5Handle {

    @Resource
    private DsedUserService dsedUserService;

    @Resource
    private DsedUserBankcardService dsedUserBankcardService;

    @Resource
    private DsedBankService dsedBankService;

    @Resource
    UpsUtil upsUtil;
    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "请求成功");
        String busiFlag = ObjectUtils.toString(context.getData("busiFlag"), null);
        String openId = ObjectUtils.toString(context.getData("userId"), null);
        DsedUserDo user=dsedUserService.getByOpenId(openId);
        //判断是否已经被绑定
         DsedUserBankcardDo userBankcardDo=dsedUserBankcardService.getById(Long.valueOf(busiFlag));
        if(dsedUserBankcardService.getUserBankByCardNo(userBankcardDo.getCardNumber())>0){
            return new DsedH5HandleResponse(1545, FanbeiExceptionCode.DSED_BANK_BINDED.getDesc());
        }

        //默认赋值为借记卡
        String cardType = "00";

        //调用ups
        UpsAuthSignRespBo upsResult = upsUtil.authSign(user.getRid().toString(), user.getRealName(), userBankcardDo.getMobile(), user.getIdNumber(), userBankcardDo.getCardNumber(), "02",
                userBankcardDo.getBankCode(),cardType,userBankcardDo.getValidDate(),userBankcardDo.getSafeCode());

        if(!upsResult.isSuccess()){
            return new DsedH5HandleResponse(1542, FanbeiExceptionCode.AUTH_BINDCARD_ERROR.getDesc());
        }else if(!"10".equals(upsResult.getNeedCode())){
            return new DsedH5HandleResponse(1567, FanbeiExceptionCode.AUTH_BINDCARD_SMS_ERROR.getErrorMsg());
        }
        return resp;
    }
}
