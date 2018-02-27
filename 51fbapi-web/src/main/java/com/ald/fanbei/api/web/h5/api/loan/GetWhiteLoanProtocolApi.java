package com.ald.fanbei.api.web.h5.api.loan;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.biz.util.ProtocolUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.GetWhiteLoanProtocolParam;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：展示商品代买提示语
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getWhiteLoanProtocolApi")
@Validator("getWhiteLoanProtocolParam")
public class GetWhiteLoanProtocolApi extends GetBorrowCashBase implements ApiHandle {


    @Resource
    ProtocolUtil protocolUtil;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        // 获取客户端请求参数
        GetWhiteLoanProtocolParam param = (GetWhiteLoanProtocolParam) requestDataVo.getParamObj();
        String userName = context.getUserName();
        Map<String, Object> data = new HashMap<>();
        List<AfResourceDo> afResourceDoList = afResourceService.getConfigByTypes("WHITE_AGREEMENT");
        for (AfResourceDo afResourceDo : afResourceDoList) {
            if ("WHITE_PLATFORM_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//白领贷平台服务协议
                afResourceDo.setValue("/fanbei-web/h5/whiteLoanPlatformServiceProtocol?userName=" + userName +
                        "&totalServiceFee=" + param.totalServiceFee + "&loanId=" + param.loanId + "&overdueRate=" + param.overdueRate + "&serviceRate=" + param.serviceRate +
                        "&interestRate=" + param.interestRate);
            } else if ("WHITE_LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//白领贷借钱协议
                afResourceDo.setValue("/fanbei-web/app/whiteLoanProtocol?userName=" + userName +
                        "&amount=" + param.amount + "&nper=" + param.nper + "&loanId=" + param.loanId + "&loanRemark=" + param.loanRemark +
                        "&repayRemark=" + param.repayRemark);
            }
        }
        data.put("whiteLoanList", afResourceDoList);
        resp.setResponseData(data);
        return resp;
    }


}
