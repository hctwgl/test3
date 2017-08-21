package com.ald.fanbei.api.web.api.order;

import com.ald.fanbei.api.biz.service.AfOrderLogisticsService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.kdniao.KdniaoReqDataDataTraces;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfOrderLogisticsDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
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
            AfOrderLogisticsDo afOrderLogisticsDo = afOrderLogisticsService.getByOrderId(orderId);
            if (afOrderLogisticsDo != null) {
                resp.addResponseData("stateDesc", convertState(afOrderLogisticsDo.getState()));
                resp.addResponseData("shipperName",afOrderLogisticsDo.getShipperName());
                List<KdniaoReqDataDataTraces> traces= JSONObject.parseArray( afOrderLogisticsDo.getTraces(), KdniaoReqDataDataTraces.class);

                if(traces.size()>0){
                    resp.addResponseData("newestInfo",traces.get(traces.size()-1));
                }else{
                    //如果没有就虚拟一条空的轨迹
                    KdniaoReqDataDataTraces  empty=new KdniaoReqDataDataTraces();
                    empty.setAcceptTime(new Date());
                    empty.setAcceptStation("暂无物流轨迹");
                    resp.addResponseData("newestInfo",empty);
                }
                if(isOutTraces>0){
                    resp.addResponseData("tracesInfo",traces);
                }
            }
        } catch (Exception e) {
              logger.error("getOrderLogisticsApi :",e.toString());
              resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
        }

        return resp;
    }

    /**
     * 枚举转换
     * @param state 状态值
     * @return 描述
     */
    String convertState(int state) {
        String stateDesc = "";
        switch (state) {
            case 0:
                stateDesc = "无轨迹";
                break;
            case 1:
                stateDesc = "已揽收";
                break;
            case 2:
                stateDesc = "在途中";
                break;
            case 201:
                stateDesc = "到达派件城市";
                break;
            case 3:
                stateDesc = "签收";
                break;
            case 4:
                stateDesc = "问题件";
                break;
            default:
                stateDesc = "未识别的物流状态";
                break;
        }
        return stateDesc;
    }
}
