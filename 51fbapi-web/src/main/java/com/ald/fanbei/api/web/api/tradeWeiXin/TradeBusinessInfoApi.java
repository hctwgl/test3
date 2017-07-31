package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.service.AfTradeBusinessInfoService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessDo;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author shencheng 2017/7/31 下午4:01
 * @类描述: TradeBusinessInfoApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tradeBusinessInfoApi")
public class TradeBusinessInfoApi implements ApiHandle {

    @Resource
    private AfTradeBusinessInfoService afTradeBusinessInfoService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getByBusinessId(businessId);
        resp.addResponseData("businessInfo", afTradeBusinessInfoDo);
        return resp;
    }
}
