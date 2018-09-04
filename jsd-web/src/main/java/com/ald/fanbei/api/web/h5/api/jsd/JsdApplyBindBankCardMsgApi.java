package com.ald.fanbei.api.web.h5.api.jsd;


import com.ald.fanbei.api.biz.bo.UpsAuthSignRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jilong
 * @类现描述：申请绑卡银行短信
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("jsdApplyBindBankCardMsgApi")
@Validator("bankCardBindParam")
public class JsdApplyBindBankCardMsgApi implements DsedH5Handle {

    @Resource
    private JsdUserService jsdUserService;

    @Resource
    private JsdUserBankcardService jsdUserBankcardService;

    @Resource
    private JsdBankService jsdBankService;

    @Resource
    UpsUtil upsUtil;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "请求成功");
        String bankNo = ObjectUtils.toString(context.getData("bankNo"), null);
        String bankName = ObjectUtils.toString(context.getData("bankName"), null);
        String bankMobile = ObjectUtils.toString(context.getData("bankMobile"), null);
        String bindNo = ObjectUtils.toString(context.getData("bindNo"), "");
        String timestamp = ObjectUtils.toString(context.getData("timestamp"), "");

        Long userid=context.getUserId();
        logger.info("jsdApplyBindBankCardMsgApi context=" + JSON.toJSONString(context));

        //判断是否已经被绑定
        if (jsdUserBankcardService.getUserBankByCardNo(bankNo) > 0) {
            return new DsedH5HandleResponse(1545, FanbeiExceptionCode.DSED_BANK_BINDED.getDesc());
        }
        //是否是设主卡
        String isMain = YesNoStatus.NO.getCode();
        if (jsdUserBankcardService.getUserBankCardInfoByUserId(userid).size() == 0) {
            isMain = YesNoStatus.YES.getCode();
        }
        //创建用户银行卡新加状态
        JsdUserBankcardDo bankcardDo=jsdUserBankcardService.getByBindNo(bindNo);
        JsdBankDo bank = jsdBankService.getBankByName(bankName);
        JsdUserBankcardDo userBankcard = buildUserCard(bank.getBankCode(), bankName, bankNo, bankMobile, userid, isMain, bindNo);
        if(bankcardDo==null){
            jsdUserBankcardService.addUserBankcard(userBankcard);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("timestamp", timestamp);
        map.put("repaySMS", "Y");
        resp.setData(map);
        return resp;
    }

    private JsdUserBankcardDo buildUserCard(String bankCode, String bankName, String cardNumber, String mobile, Long userId, String isMain, String bindNo) {
        JsdUserBankcardDo bank = new JsdUserBankcardDo();
        bank.setBankCode(bankCode);
        bank.setBankName(bankName);
        bank.setBankCardNumber(cardNumber);
        bank.setIsMain(isMain);
        bank.setMobile(mobile);
        bank.setStatus(BankcardStatus.NEW.getCode());
        bank.setUserId(userId);
        bank.setValidDate("");
        bank.setSafeCode("");
        bank.setXgxyBindNo(bindNo);
        return bank;
    }
}
