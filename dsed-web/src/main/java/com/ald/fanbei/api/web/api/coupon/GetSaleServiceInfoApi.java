package com.ald.fanbei.api.web.api.coupon;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Component("getSaleServiceInfoApi")
public class GetSaleServiceInfoApi implements ApiHandle {
    @Resource
    AfResourceService afResourceService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String serviceInfo = "";
        try{
            AfResourceDo afResourceDo = afResourceService.getSingleResourceBytype(Constants.COUPON_SALESERVICE);
            if(afResourceDo!=null){
                String value = afResourceDo.getValue();
                if(StringUtil.isNotBlank(value)){
                    serviceInfo = value;
                }
            }
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("serviceInfo", serviceInfo);
            resp.setResponseData(data);
            return resp;
        }catch (Exception e){
            logger.error("getSaleServiceInfo error for " + e);
            throw new FanbeiException("getSaleServiceInfo error", FanbeiExceptionCode.FAILED);
        }
    }
}
