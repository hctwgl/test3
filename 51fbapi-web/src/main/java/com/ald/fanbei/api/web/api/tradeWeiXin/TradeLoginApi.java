package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.biz.service.AfTradeBusinessService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author shencheng 2017/7/28 下午3:32
 * @类描述: tradeLoginApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tradeLoginApi")
public class TradeLoginApi implements ApiHandle {
    @Resource
    private AfTradeBusinessService tradeBusinessService;
    @Resource
    private BizCacheUtil bizCacheUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        try {
            String username = ObjectUtils.toString(requestDataVo.getParams().get("username"), null);
            String password = ObjectUtils.toString(requestDataVo.getParams().get("password"), null);
            AfTradeBusinessDo businessDo = tradeBusinessService.getByName(username);
            if (businessDo == null) {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
            }
            byte[] curPwdBytes = DigestUtil.digestString(password.getBytes("UTF-8"), Constants.DEFAULT_SALT.getBytes(), Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
            String curPwd = DigestUtil.encodeHex(curPwdBytes);
            if (curPwd.equals(businessDo.getPassword())) {
                ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
                resp.addResponseData("businessId", businessDo.getId());
                resp.addResponseData("token", createLoginToke());
                //可提现金额

                //不可提现金额

                //今日交易金额

                //今日付款单数

                return resp;
            } else {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PASSWORD_ERROR);
            }
        } catch (Exception e) {
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
        }
    }

    private String createLoginToke() {
        String token = UUID.randomUUID().toString().replace("-", "");
        //将token放入redis
        bizCacheUtil.saveObject(token, token, Constants.SECOND_OF_HALF_HOUR);
        return token;
    }
}
