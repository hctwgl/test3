package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.service.AfTradeOrderService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author shencheng 2017/8/1 下午1:52
 * @类描述: TradeWithdrawApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tradeWithdrawApi")
public class TradeWithdrawApi implements ApiHandle {

    @Resource
    private AfTradeOrderService afTradeOrderService;
    @Resource
    BizCacheUtil bizCacheUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        String lockKey = Constants.CACHEKEY_BORROW_DELIVER_MONEY_LOCK + businessId;

        boolean isGetLock = bizCacheUtil.getLockTryTimes(lockKey, "1", Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")));
        if (isGetLock) {
            if (afTradeOrderService.withdraw(businessId)) {
                resp.addResponseData("result", "yes");
            } else {
                resp.addResponseData("result", "no");
            }
        } else {
            resp.addResponseData("result", "wait");
        }
        if (isGetLock) {
            bizCacheUtil.delCache(lockKey);
        }

        return resp;
    }
}
