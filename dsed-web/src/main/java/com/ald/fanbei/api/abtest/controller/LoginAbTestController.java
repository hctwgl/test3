package com.ald.fanbei.api.abtest.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfAbtestDeviceNewService;
import com.ald.fanbei.api.biz.service.AfTestManageService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfAbtestDeviceNewDo;
import com.ald.fanbei.api.dal.domain.AfTestManageDo;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.controller.AbTestController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 *
 * @类描述：HomePageAbTestController
 * @author 江荣波 2017年11月24日 下午6:15:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class LoginAbTestController extends AbTestController {

	@Resource
	AfTestManageService afTestManageService;
	@Resource
	AfAbtestDeviceNewService afAbtestDeviceNewService;

	@RequestMapping(value = "/user/loginFront", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String loginFront(@RequestBody String body, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		return this.processRequest(body, request, false);
	}

	@Override
	protected void abTest(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		AfTestManageDo testManage = afTestManageService.getTestInfoByTag("LOGIN_TYPE");
		Map<String,Object> params = Maps.newHashMap();
		params.put("loginType","old");
		String deviceId = (String) requestDataVo.getParams().get("deviceId");
		try {
			if (testManage != null && !StringUtils.isEmpty(deviceId)) {
				// 获取设备号尾号
				String deviceIdTail = StringUtil.getDeviceTailNum(deviceId);
				String rule = testManage.getRule();
				JSONArray testRule = JSON.parseArray(rule);
				for (int i = 0; i < testRule.size(); i++) {
					JSONObject strategy = testRule.getJSONObject(i);
					String strategyCode = strategy.getString("strategyCode");
					String testDeviceId = strategy.getString("deviceId");
					if(testDeviceId.contains(deviceIdTail)){
						params.put("loginType",strategyCode);
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("getHomeInfoFront error => {}", e.getMessage());
			
		}
		String userName = context.getUserName();
		Long userId = context.getUserId();
		if (userName != null && userId != null) {
		    try {
			//String deviceId = ObjectUtils.toString(requestDataVo.getParams().get("deviceId"));
			if (StringUtils.isNotEmpty(deviceId)) {
			  //String deviceIdTail = StringUtil.getDeviceTailNum(deviceId);
				AfAbtestDeviceNewDo abTestDeviceDo = new AfAbtestDeviceNewDo();
				abTestDeviceDo.setUserId(userId);
				abTestDeviceDo.setDeviceNum(deviceId);
				// 通过唯一组合索引控制数据不重复
				afAbtestDeviceNewService.addUserDeviceInfo(abTestDeviceDo);
			}
		}  catch (Exception e) {
			// ignore error.
		}
		}
		
		requestDataVo.setParams(params);
	}

}