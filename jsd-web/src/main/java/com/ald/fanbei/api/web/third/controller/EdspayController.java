package com.ald.fanbei.api.web.third.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.aassetside.edspay.AssetSideRespBo;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类现描述：和资产方平台对接入口
 * @author wujun
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/edspay")
public class EdspayController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;
	
	/**
	 * 钱包回传实时推送债权的打款情况接口
	 * @param requestData
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/giveBackPayResult" }, method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public AssetSideRespBo giveBackPayResult(@RequestBody String requestData,HttpServletRequest request, HttpServletResponse response) {
		logger.info("giveBackPayResult param = " + requestData);
		JSONObject jsonObj = JSON.parseObject(requestData);
		String sendTime = StringUtil.null2Str(jsonObj.get("sendTime"));
		String data = StringUtil.null2Str(jsonObj.get("data"));
		String sign = StringUtil.null2Str(jsonObj.get("sign"));
		String appId = StringUtil.null2Str(jsonObj.get("appId"));
		AssetSideRespBo notifyRespBo = assetSideEdspayUtil.giveBackPayResult(sendTime, data, sign,appId);
		logger.info("giveBackPayResult resp="+notifyRespBo.toString());
		return notifyRespBo;
	}
	
	/**
	 * 爱上街主动查询钱包打款结果后的调用api处理接口
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/queryEdspayApiHandle"}, method = RequestMethod.POST)
    @ResponseBody
    public String queryEdspayApiHandle(HttpServletRequest request, HttpServletResponse response) {
		String orderNo = request.getParameter("orderNo");
        int result = assetSideEdspayUtil.queryEdspayApiHandle(orderNo);
        if (result == 1) {
			return "FAIl";
		}
	  return "SUCCESS";
    }
	/**
	 * admin重推达上限调用api处理接口
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = {"/repushMaxApiHandle"}, method = RequestMethod.POST)
    @ResponseBody
    public String repushMaxApiHandle(HttpServletRequest request, HttpServletResponse response) {
		String orderNo = request.getParameter("orderNo");
        int result = assetSideEdspayUtil.repushMaxApiHandle(orderNo);
        if (result == 1) {
			return "FAIl";
		}
        return "SUCCESS";
    }
	
}
