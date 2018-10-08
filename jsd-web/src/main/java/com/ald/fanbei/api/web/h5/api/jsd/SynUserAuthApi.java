package com.ald.fanbei.api.web.h5.api.jsd;

import com.ald.fanbei.api.biz.service.JsdUserAuthService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Component("synUserAuthApi")
public class SynUserAuthApi implements JsdH5Handle {

    @Resource
    JsdUserAuthService jsdUserAuthService;


    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        Long userId = context.getUserId();
        try {
            JsdUserAuthDo userAuthDo=new JsdUserAuthDo();
            userAuthDo.setGmtCreate(new Date());
            userAuthDo.setRiskAmount( new BigDecimal(String.valueOf(context.getDataMap().get("riskAmount"))));
            userAuthDo.setGmtRisk(DateUtil.stringToDate(String.valueOf(context.getDataMap().get("riskTime")) ));
            userAuthDo.setRiskStatus((String) context.getDataMap().get("riskStatus"));
            userAuthDo.setRiskNo((String) context.getDataMap().get("riskNo"));
            userAuthDo.setUserId(userId);
            userAuthDo.setRiskRate(new BigDecimal(String.valueOf(context.getDataMap().get("riskRate"))) );
            userAuthDo.setUrl(String.valueOf(context.getDataMap().get("url")));
            jsdUserAuthService.saveRecord(userAuthDo);
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JsdH5HandleResponse(504, "失败");
    }
}
