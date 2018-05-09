package com.ald.fanbei.api.web.h5.api.recycle;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetRecycleProtocolParam;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：展示商品代买提示语
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRecycleProtocolApi")
@Validator("getRecycleProtocolParam")
public class GetRecycleProtocolApi implements H5Handle {

    @Resource
    AfResourceService afResourceService;

    @Resource
    RiskUtil riskUtil;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        // 获取客户端请求参数
        GetRecycleProtocolParam param = (GetRecycleProtocolParam) context.getParamEntity();
        String userName = context.getUserName();
        Map<String, Object> data = new HashMap<>();
        String reqId = context.getId();
        String appName = reqId.substring(reqId.lastIndexOf("_") + 1, reqId.length());
        String ipAddress = context.getClientIp();
        BigDecimal oriRate = getOriRate(param.type,context.getUserId(),appName,ipAddress);
        List<AfResourceDo> afResourceDoList = afResourceService.getConfigByTypes("RECYCLE_PROTOCOL");
        for (AfResourceDo afResourceDo : afResourceDoList) {
            if ("RECYCLE_LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//回收借钱协议
					afResourceDo.setValue("/fanbei-web/h5/goodsRecoverProtocol?userName=" + userName +
					        "&amount=" + param.amount  + "&borrowId=" + param.borrowId  +
					        "&overdueRate=" + param.overdueRate + "&type=" + param.type+"&riskDailyRate="+oriRate);
            } else if ("DIGITAL_CERTIFICATE_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//数字证书
                afResourceDo.setValue("/fanbei-web/app/numProtocol?userName=" + userName);
            } else if ("LETTER_OF_RISK".equals(afResourceDo.getSecType())) {//风险提示协议
                afResourceDo.setValue("/app/sys/riskWarning");
            }
        }
        logger.info("getRecycleProtocolApi userId = "+context.getUserId()+",recycleList ="+JSONObject.toJSONString(afResourceDoList));
        data.put("recycleList", afResourceDoList);
        resp.setResponseData(data);
        return resp;
    }

    private BigDecimal getOriRate(String borrowType, Long userId, String appName, String ipAddress) {

        BigDecimal oriRate = BigDecimal.ZERO;
        // 查询新利率配置
        JSONObject params = new JSONObject();
        params.put("ipAddress", ipAddress);
        params.put("appName",appName);
        oriRate = riskUtil.getRiskOriRate(userId,params,borrowType);
        return oriRate;
    }
}
