package com.ald.fanbei.api.web.third.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/oauth2")
public class YiBaoController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 商户从微信报备的appId TODO:需商户自行赋值
     */
    private static final String OAUTH2_MERCHANT_APP_ID = "wx583e90560d329683";
    /**
     * 商户从微信报备的appSecret TODO:需商户自行赋值
     */
    private static final String OAUTH2_MERCHANT_APP_SECRET = "ea9b7a6055a1bc49049854cdf0893950";
    /**
     * 商户在易宝的商编 TODO:需商户自行赋值
     */
    private static final String MERCHANT_NO = "10015398796";

    /**
     * 注：此接口(例如http://host/auth2/callback?code=&state=)，即报备在微信的redirect_uri。<p>
     * 微信授权接口在收到请求后，会回调到此接口。<p>
     * 此接口需实现如下逻辑：接受微信回调，根据回调的code获取openId，拼接易宝收银台地址，并重定向。
     *
     * @param request
     * @return
     */
    @RequestMapping("/callback")
    public Object oauth2CallBack(HttpServletRequest request) {
        logger.info("com in");
        String url = Oauth2Client.oauthCallBack(request, OAUTH2_MERCHANT_APP_ID, OAUTH2_MERCHANT_APP_SECRET, MERCHANT_NO);
        return new RedirectView(url);
    }
}
