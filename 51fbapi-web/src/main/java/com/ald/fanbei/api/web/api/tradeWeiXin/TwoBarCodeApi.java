package com.ald.fanbei.api.web.api.tradeWeiXin;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author shencheng 2017/7/31 下午4:14
 * @类描述: TwoBarCodeApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("twoBarCodeApi")
public class TwoBarCodeApi implements ApiHandle {
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        String requestDataVoId = StringUtil.isNotBlank(requestDataVo.getId()) ? requestDataVo.getId() : "trade weixin";
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVoId, FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 0l);
        JSONObject json = new JSONObject();
        json.put("type", "TRADE");
        json.put("url", "/fanbei-web/app/initTradeInfo?bid=" + createBid(businessId) + "&extReq=0000");
        resp.addResponseData("twoBarCode", new String(Base64.encodeBase64(json.toJSONString().getBytes())));

        return resp;
    }

    private String createBid(Long userId) {
        String encryptStr = "";
        try {
            encryptStr = new String(Base64.encodeBase64(AesUtil.encrypt(userId.toString(), Constants.TRADE_AES_DECRYPT_PASSWORD)), "UTF-8");
            encryptStr = URLEncoder.encode(encryptStr, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("加密商户ID出错",e);
        }
        return encryptStr;

    }
}
