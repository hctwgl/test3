package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.service.AfTradeWithdrawRecordService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * @author shencheng 2017/8/3 下午3:20
 * @类描述: GetWithdrawListDateApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getWithdrawListDateApi")
public class GetWithdrawListDateApi implements ApiHandle {

    @Resource
    private AfTradeWithdrawRecordService afTradeWithdrawRecordService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        Date startDate = requestDataVo.getParams().get("startDate") != null ? DateUtil.parseDate(requestDataVo.getParams().get("startDate").toString() + " 00:00:00", "yyyy-MM-dd HH:mm:ss") : null;
        Date endDate = requestDataVo.getParams().get("endDate") != null ? DateUtil.parseDate(requestDataVo.getParams().get("endDate").toString() + " 23:59:59", "yyyy-MM-dd HH:mm:ss") : null;
        List<String> date = afTradeWithdrawRecordService.withdrawGridDate(businessId, startDate, endDate);
        resp.addResponseData("date", date);
        return resp;
    }
}
