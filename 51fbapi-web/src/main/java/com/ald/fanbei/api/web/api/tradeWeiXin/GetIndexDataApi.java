package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.service.AfTradeOrderService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.dto.AfTradeOrderStatisticsDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author shencheng 2017/8/4 下午2:14
 * @类描述: GetIndexDataApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getIndexDataApi")
public class GetIndexDataApi implements ApiHandle{

    @Resource
    private AfTradeOrderService afTradeOrderService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        //可提现金额
        BigDecimal canWithDraw = afTradeOrderService.getCanWithDrawMoney(businessId);
        resp.addResponseData("canWithDraw", canWithDraw == null ? BigDecimal.ZERO : canWithDraw);
        //不可提现金额
        BigDecimal cannotWithDraw = afTradeOrderService.getCannotWithDrawMoney(businessId);
        resp.addResponseData("cannotWithDraw", cannotWithDraw == null ? BigDecimal.ZERO : cannotWithDraw);
        //今日交易金额,今日付款单数
        Date startDate = DateUtil.getStartOfDate(new Date());
        Date endDate = DateUtil.getEndOfDate(new Date());
        AfTradeOrderStatisticsDto dto = afTradeOrderService.payOrderInfo(businessId, startDate, endDate);
        resp.addResponseData("money", dto.getMoney());
        resp.addResponseData("count", dto.getCount());
        return resp;
    }
}
