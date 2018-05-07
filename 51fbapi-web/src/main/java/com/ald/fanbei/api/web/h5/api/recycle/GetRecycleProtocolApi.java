package com.ald.fanbei.api.web.h5.api.recycle;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetRecycleProtocolParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        // 获取客户端请求参数
        GetRecycleProtocolParam param = (GetRecycleProtocolParam) context.getParamEntity();
        String userName = context.getUserName();
        Map<String, Object> data = new HashMap<>();
        List<AfResourceDo> afResourceDoList = afResourceService.getConfigByTypes("RECYCLE_PROTOCOL");
        for (AfResourceDo afResourceDo : afResourceDoList) {
            if ("RECYCLE_LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//回收借钱协议
                afResourceDo.setValue("/fanbei-web/h5/goodsRecoverProtocol?userName=" + userName +
                        "&amount=" + param.amount + "&goodsName=" + param.goodsName + "&borrowId=" + param.borrowId + "&goodsModel=" + param.goodsModel +
                        "&overdueRate=" + param.overdueRate + "&type=" + param.type);
            } else if ("DIGITAL_CERTIFICATE_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//数字证书
                afResourceDo.setValue("/fanbei-web/app/numProtocol?userName=" + userName);
            } else if ("LETTER_OF_RISK".equals(afResourceDo.getSecType())) {//风险提示协议
                afResourceDo.setValue("/app/sys/riskWarning");
            }
        }
        data.put("whiteLoanList", afResourceDoList);
        resp.setResponseData(data);
        return resp;
    }


}
