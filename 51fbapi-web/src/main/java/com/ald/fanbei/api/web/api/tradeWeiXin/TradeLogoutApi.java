package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author shencheng 2017/7/31 上午11:27
 * @类描述: TradeLogoutApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tradeLogoutApi")
public class TradeLogoutApi implements ApiHandle {
    @Resource
    private BizCacheUtil bizCacheUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String loginKey = Constants.TRADE_LOGIN_BUSINESSID + request.getHeader("businessId");
        String sessionKey = Constants.TRADE_SESSIONID + request.getHeader("sessionId");
        bizCacheUtil.delCache(loginKey);
        bizCacheUtil.delCache(sessionKey);

        return resp;
    }
}
