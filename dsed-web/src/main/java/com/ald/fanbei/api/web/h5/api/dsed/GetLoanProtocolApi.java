package com.ald.fanbei.api.web.h5.api.dsed;


import com.ald.fanbei.api.biz.service.DsedResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedResourceDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetLoanProtocolParam;
import com.ald.fanbei.api.web.vo.DsedPrococolVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：展示商品代买提示语
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedGetLoanProtocolApi")
@Validator("getLoanProtocolParam")
public class GetLoanProtocolApi implements DsedH5Handle {

    @Resource
    DsedResourceService dsedResourceService;

    private static String notifyHost = null;

    private static String getNotifyHost(){
        if(notifyHost==null){
            notifyHost = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST);
            return notifyHost;
        }
        return notifyHost;
    }
    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        // 获取客户端请求参数
        GetLoanProtocolParam param = (GetLoanProtocolParam) context.getParamEntity();
        Long userId = context.getUserId();
        Map<String, Object> data = new HashMap<>();
        List<DsedResourceDo> dsedResourceDoList = dsedResourceService.getConfigByTypes("DSED_AGREEMENT");
        List<DsedPrococolVo> dsedPrococolVoList = new ArrayList<>();
        for (DsedResourceDo afResourceDo : dsedResourceDoList) {
            DsedPrococolVo dsedPrococolVo = new DsedPrococolVo();
            if ("DSED_PLATFORM_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//平台服务协议
                dsedPrococolVo.setProtocolName("平台服务协议");
                dsedPrococolVo.setProtocolUrl(getNotifyHost()+"/dsed-web/h5/whiteLoanPlatformServiceProtocol?userId=" + userId +
                        "&nper=" + param.nper + "&loanId=" + param.loanId + "&amount=" + param.amount + "&totalServiceFee=" + param.totalServiceFee);
            } else if ("DSED_LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//借钱协议
                dsedPrococolVo.setProtocolName("借钱协议");
                dsedPrococolVo.setProtocolUrl(getNotifyHost()+"/dsed-web/h5/loanProtocol?userId=" + userId +
                        "&amount=" + param.amount + "&nper=" + param.nper + "&loanId=" + param.loanId + "&loanRemark=" + param.loanRemark +
                        "&repayRemark=" + param.repayRemark);
            } else if ("DIGITAL_CERTIFICATE_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//数字证书
                dsedPrococolVo.setProtocolName("数字证书");
                dsedPrococolVo.setProtocolUrl(getNotifyHost()+"/dsed-web/app/numProtocol?userId=" + userId);
            } else if ("LETTER_OF_RISK".equals(afResourceDo.getSecType())) {//风险提示协议
                dsedPrococolVo.setProtocolName("风险提示协议");
                dsedPrococolVo.setProtocolUrl(getNotifyHost()+"/app/sys/riskWarning");
            }
            dsedPrococolVoList.add(dsedPrococolVo);
        }
        data.put("protocolList", dsedPrococolVoList);
        resp.setData(data);
        return resp;
    }


}
