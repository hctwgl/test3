package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.service.AfTradeOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author shencheng 2017/7/31 下午4:58
 * @类描述: GetPayListApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getPayListApi")
public class GetPayListApi implements ApiHandle {

    @Resource
    private AfTradeOrderService afTradeOrderService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        Date startDate = requestDataVo.getParams().get("startDate") != null ? DateUtil.parseDate((String) requestDataVo.getParams().get("startDate")) : null;
        Date endDate = requestDataVo.getParams().get("endDate") != null ? DateUtil.parseDate((String) requestDataVo.getParams().get("endDate")) : null;
        Integer page = NumberUtil.objToPageIntDefault(requestDataVo.getParams().get("page"), 0);
        String orderStatus = ObjectUtils.toString(requestDataVo.getParams().get("orderStatus"));
        List<AfTradeOrderDto> list = afTradeOrderService.orderGrid(businessId, (page-1)* 20, 20, startDate, endDate, orderStatus, null);
        Long total = afTradeOrderService.orderGridTotal(businessId, startDate, endDate, orderStatus, null);
        resp.addResponseData("payList", list);
        resp.addResponseData("count", total);
        return resp;
    }
}
