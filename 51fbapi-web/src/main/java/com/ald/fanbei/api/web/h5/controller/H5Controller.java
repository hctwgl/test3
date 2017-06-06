package com.ald.fanbei.api.web.h5.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：
 * 
 * @author xiaotianjian 2017年2月14日下午2:55:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/")
public class H5Controller extends BaseController {

	
	
	 
	 
	/**
	 * 新h5页面处理，针对前端开发新的h5页面时请求的处理
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */

	@RequestMapping(value = { "app/*", "app/goods/*", "app/user/*", "app/sys/*", "activity/*", "activity/01/*", "activity/02/*", "activity/03/*", "activity/04/*", "activity/05/*", "activity/06/*", "activity/07/*", "activity/08/*", "activity/09/*", "activity/10/*", "activity/11/*", "activity/12/*", "fanbei-web/*"}, method = RequestMethod.GET)
	public String newVmPage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String returnUrl = request.getRequestURI();
		if(returnUrl.indexOf("fanbei-web/game") >=0){
			doMaidianLog(request);
		}
		
		return returnUrl;
	}

	@RequestMapping(value = { "app/sys/invitationRewardRule" }, method = RequestMethod.GET)
	public String downloadApp(HttpServletRequest request, ModelMap model) throws IOException {

		model.put("rule", "您邀请的所有好友在51返呗成功购物并获得返利后，您将获得返利的20%现金奖励；当您邀请的好友A邀请她的好友B成功在51返呗购物并获得返利后，");
		return "/app/sys/invitationRewardRule";
	}


	
	

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
