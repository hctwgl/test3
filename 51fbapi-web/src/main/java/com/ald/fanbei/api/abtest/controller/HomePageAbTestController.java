package com.ald.fanbei.api.abtest.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.AbTestUrl;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.controller.AbTestController;

/**
 *
 * @类描述：HomePageAbTestController
 * @author 陈金虎 2017年1月17日 下午6:15:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
public class HomePageAbTestController extends AbTestController {

	@RequestMapping(value = "/goods/getHomeInfoFront", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getHomeInfoFront(@RequestBody String body, HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");
		return this.processRequest(body, request, false);
	}

	@Override
	protected void abTest(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// 首页AB测试
		if (AbTestUrl.HOME_PAGE.equalsIgnoreCase(requestDataVo.getMethod())) {
			requestDataVo.setMethod(AbTestUrl.HOME_PAGE_V2);
		}

	}

}