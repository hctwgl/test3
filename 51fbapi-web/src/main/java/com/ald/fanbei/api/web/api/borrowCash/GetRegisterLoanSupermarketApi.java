package com.ald.fanbei.api.web.api.borrowCash;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component("getRegisterLoanSupermarketApi")
public class GetRegisterLoanSupermarketApi implements ApiHandle {
    @Resource
    AfLoanSupermarketService afLoanSupermarketService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String lsmNo = ObjectUtils.toString(requestDataVo.getParams().get("lsmNo"));
        AfLoanSupermarketDo afLoanSupermarket  = afLoanSupermarketService.getLoanSupermarketByLsmNo(lsmNo);
        if(afLoanSupermarket!=null){
            resp.setResponseData(afLoanSupermarket);
        }else{
            logger.error("贷款超市请求发起异常-贷款超市不存在，lsmNo："+lsmNo+"-userId:"+userId);
            resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
        }
        return resp;
    }
}
