package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.biz.service.AfOrderLogisticsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.kdniao.KdniaoReqDataDataTraces;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderLogisticsDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.response.OpenimUserserviceGetResponse;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("getOrderLogisticsApi")
public class GetOrderLogisticsApi implements ApiHandle {
    @Autowired
    AfOrderLogisticsService afOrderLogisticsService;

    /**
     * 查询物流信息
     * @param requestDataVo
     * @param context
     * @param request
     * @return
     */
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        try {
            Map<String, Object> params = requestDataVo.getParams();
            long orderId = NumberUtil.strToLong(params.get("orderId").toString());
            long isOutTraces = NumberUtil.strToLong(params.get("traces")==null?String.valueOf(0) :params.get("traces").toString());
            AfOrderLogisticsBo afOrderLogisticsBo= afOrderLogisticsService.getOrderLogisticsBo(orderId,isOutTraces);
            if(afOrderLogisticsBo==null){
                resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.LOGISTICS_NOT_EXIST);
            }else{
                resp.addResponseData("logistics",afOrderLogisticsBo);
            }
        } catch (Exception e) {
              logger.error("getOrderLogisticsApi :",e.toString());
              resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
        }
         System.out.println(JSON.toJSONString(resp));
        return resp;
    }


}
