package com.ald.fanbei.api.web.api.legalborrowV2;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.biz.util.ProtocolUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyLegalBorrowCashParam;
import com.ald.fanbei.api.web.validator.bean.GetCashLoanProtocolParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：展示商品代买提示语
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCashLoanProtocolV2Api")
@Validator("getCashLoanProtocolParam")
public class GetCashLoanProtocolV2Api extends GetBorrowCashBase implements ApiHandle {

    private static final String RESOURCE_TYPE = "AGENTBUY_HINTS";

    @Resource
    AfResourceService afResourceService;
    @Resource
    ProtocolUtil protocolUtil;
    @Resource
    NumberWordFormat numberWordFormat;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        // 获取客户端请求参数
        GetCashLoanProtocolParam param = (GetCashLoanProtocolParam) requestDataVo.getParamObj();
        Map<String, Object> data = new HashMap<>();
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("userName",context.getUserName());
        String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
        String ipAddress = CommonUtil.getIpAddr(request);
        String type = String.valueOf(numberWordFormat.borrowTime(param.getBorrowType()));
        map.put("type",type);
        map.put("borrowId","");
        map.put("poundage",param.getPoundage());
        map.put("borrowAmount",param.getBorrowAmount());
        map.put("appName",appName);
        map.put("ipAddress",ipAddress);
        List<AfResourceDo> cashLoanList = protocolUtil.getProtocolList("cashLoan",map);
        data.put("cashLoanList",cashLoanList);
        resp.setResponseData(data);
        return resp;
    }



}
