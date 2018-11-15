package com.ald.fanbei.api.web.controller.third;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.AssetSideRespBo;
import com.ald.fanbei.api.biz.util.EdsPayProtocolUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author chengkang 2017年11月27日 下午16:45:39
 * @类现描述：和资产方平台对接入口
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/eProtocol")
public class EProtocolController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    EdsPayProtocolUtil edsPayProtocolUtil;

    /**
     * 资产方协议回传接口
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/giveBackPdfInfo"}, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public AssetSideRespBo giveBackCreditInfo(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObj = JSON.parseObject(requestData);
        String sendTime = StringUtil.null2Str(jsonObj.get("sendTime"));
        String data = StringUtil.null2Str(jsonObj.get("data"));
        String sign = StringUtil.null2Str(jsonObj.get("sign"));
        String appId = StringUtil.null2Str(jsonObj.get("appId"));
        logger.info("EProtocolController giveBackPdfInfo,appId=" + appId + ",sign=" + sign + ",data=" + data + ",sendTime=" + sendTime);
        AssetSideRespBo notifyRespBo = edsPayProtocolUtil.giveBackPdfInfo(sendTime, data, sign, appId);
        logger.info("EProtocolController giveBackPdfInfo,appId=" + appId + ",sendTime=" + sendTime + ",returnMsg=" + notifyRespBo.toString());
        return notifyRespBo;
    }

    /**
     * 资产方协议签章回传接口
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/giveBackSealInfo"}, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public AssetSideRespBo giveBackSealInfo(@RequestBody String requestData, HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObj = JSON.parseObject(requestData);
        String sendTime = StringUtil.null2Str(jsonObj.get("sendTime"));
        String data = StringUtil.null2Str(jsonObj.get("data"));
        String sign = StringUtil.null2Str(jsonObj.get("sign"));
        String appId = StringUtil.null2Str(jsonObj.get("appId"));
        logger.info("EProtocolController giveBackSealInfo,appId=" + appId + ",sign=" + sign + ",data=" + data + ",sendTime=" + sendTime);
        AssetSideRespBo notifyRespBo = edsPayProtocolUtil.giveBackSealInfo(sendTime, data, sign, appId);
        return notifyRespBo;
    }

}
