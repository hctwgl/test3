package com.ald.fanbei.api.web.h5.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSFgoodsVo;
import com.ald.fanbei.api.dal.domain.AfUserCouponTigerMachineDo;
import com.ald.fanbei.api.web.common.H5CommonResponse;

/**  
 * @Title: H5SFSubsctribeController.java
 * @Package com.ald.fanbei.api.web.h5.controller
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2018年1月5日 下午3:19:19
 * @version V1.0  
 */
@RestController
@RequestMapping(value = "/H5SF",produces = "application/json;charset=UTF-8")
public class H5SFSubsctribeController extends H5Controller{
	
	@Resource
	AfGoodsDoubleEggsService afGoodsDoubleEggsService;
	@Resource
	AfResourceService afResourceService;
	
	@RequestMapping(value = "/initHomePage", method = RequestMethod.POST)
	public String initHomePage(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		java.util.Map<String, Object> data = new HashMap<>();

		try {
				// init the userId for the interface : getFivePictures
				Long userId = 0L;

				// get goods to subscribe
				List<AfSFgoodsVo> goodsList = afGoodsDoubleEggsService.getFivePictures(userId);
				data.put("goodsList", goodsList);
				logger.info("/appH5SF/initHomePage userId={} , goodsList={} ", new Object[] { userId, goodsList });
				// get configuration from afresource
				AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY",
						"INIT_HOME_PAGE");
				if (resourceDo != null) {
					data.put("describtion", resourceDo.getValue2());
					data.put("strategy_img", resourceDo.getValue());
					data.put("strategy_redirect_img", resourceDo.getValue1());
				}
				data.put("serviceTime", new Date());
				logger.info("/appH5SF/initHomePage userId={} , goodsList={} ,resourceDo = {}",
						new Object[] { userId, goodsList, resourceDo });
				result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}
	
	/**
	 * 
	* @Title: initHomePageMain
	* @author qiao
	* @date 2018年1月15日 上午10:46:25
	* @Description: 年货节主会场的主页面
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/initHomePageMain", method = RequestMethod.POST)
	public String initHomePageMain(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		java.util.Map<String, Object> data = new HashMap<>();

		try {
			
			int tigerTimes = 1;
			data.put("tigerTimes", tigerTimes);
			logger.info("/H5SF/initHomePageMain  tigerTimes={} ", new Object[] { tigerTimes });
			
			// get configuration from afresource
			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("SPRING_FESTIVAL_ACTIVITY",
					"MAIN_DESCRIBTION");
			if (resourceDo != null) {
				data.put("describtion", resourceDo.getValue2());
			}
			logger.info("/H5SF/initHomePageMain  tigerTimes={} ,resourceDo = {}",
					new Object[] {  tigerTimes, resourceDo });
			result = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		

		} catch (Exception exception) {
			result = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, result);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"), result);
		}
		return result;
	}
}
