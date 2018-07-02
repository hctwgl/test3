package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.service.AfTradeOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
 * @author shencheng 2017/8/3 上午10:58
 * @类描述: GetPayListDateApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getPayListDateApi")
public class GetPayListDateApi implements ApiHandle {

    @Resource
    private AfTradeOrderService afTradeOrderService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        Date startDate = requestDataVo.getParams().get("startDate") != null ? DateUtil.parseDate(requestDataVo.getParams().get("startDate").toString() + " 00:00:00", "yyyy-MM-dd HH:mm:ss") : null;
        Date endDate = requestDataVo.getParams().get("endDate") != null ? DateUtil.parseDate(requestDataVo.getParams().get("endDate").toString() + " 23:59:59", "yyyy-MM-dd HH:mm:ss") : null;
        String orderStatus = ObjectUtils.toString(requestDataVo.getParams().get("orderStatus"), null);
        List<String> date = afTradeOrderService.orderGridDate(businessId, startDate, endDate, orderStatus);
        resp.addResponseData("date", date);
        return resp;
    }
}
