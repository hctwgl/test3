/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

/**
 * @类描述：
 * @author suweili 2017年2月28日上午11:50:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/app/user/")
public class AppH5UserContorler extends BaseController {
	
	@Resource
	AfUserAccountDao afUserAccountDao;
	@Resource
	AfUserDao afUserDao;
	
	@Resource
	AfCouponDao afCouponDao;
	
	@Resource
	AfResourceDao afResourceDao;

	@RequestMapping(value = { "receiveCoupons" }, method = RequestMethod.GET)
	public void receiveCoupons(HttpServletRequest request, ModelMap model) throws IOException {
		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		AfResourceDo resourceDo = afResourceDao.getSingleResourceBytype(AfResourceType.PickedCoupon.getCode());

		String ids = resourceDo.getValue();
		List<AfCouponDo> afCouponList = afCouponDao.selectCouponByCouponIds(ids);
		
		

		model.put("couponList", afCouponList);
		logger.info(JSON.toJSONString(model));
	}
	
	@RequestMapping(value = { "invitationGift" }, method = RequestMethod.GET)
	public void invitationGift(HttpServletRequest request, ModelMap model) throws IOException {

		String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
		AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
		model.put("avatar", afUserDo.getAvatar());
		model.put("userName", afUserDo.getUserName());
		model.put("recommendCode", afUserDo.getRecommendCode());
		model.put("mobile", afUserDo.getMobile());
		logger.info(JSON.toJSONString(model));
	}
	
	
	@RequestMapping(value = { "register" }, method = RequestMethod.GET)
	public void register(HttpServletRequest request, ModelMap model) throws IOException {
//		Long modelId = NumberUtil.objToLongDefault(request.getParameter("modelId"), 1);
		AfResourceDo resourceDo = afResourceDao.getSingleResourceBytype(AfResourceType.RegisterProtocol.getCode());
		model.put("registerRule", resourceDo.getValue());
		logger.info(JSON.toJSONString(model));
	}
	
//	

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
