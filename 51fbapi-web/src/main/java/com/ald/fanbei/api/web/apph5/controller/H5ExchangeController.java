package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @类描述：返现金额 兑换成 满减券
 * @date 2018年3月2日上午11:33:38
 * @author weiqingeng
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@RestController
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
public class H5ExchangeController extends BaseController{
	@Autowired
	private AfUserAccountService afUserAccountService;
	@Autowired
	private AfRecycleService afRecycleService;
	@Autowired
	private AfUserService afUserService;
	
	/**
	 * 翻倍兑换
	 */
	@RequestMapping(value = "/exchange", method = RequestMethod.POST)
	public String couponHomePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		String result = "";
		Integer amount = NumberUtil.objToInteger(request.getParameter("amount"));
		Long uid = NumberUtil.objToLong(request.getParameter("uid"));
		FanbeiWebContext context = new FanbeiWebContext();
		context = doWebCheck(request, false);
		try {
			if (uid == null) {
				throw new FanbeiException("user id is empty", FanbeiExceptionCode.PARAM_ERROR);
			}
			if (null == amount) {
				throw new FanbeiException("参数错误", true);
			}
			if (amount < 50) {
				throw new FanbeiException("兑换金额不能小于50", true);
			}
			AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(uid);
			if (null == afUserAccountDo || (null != afUserAccountDo && afUserAccountDo.getRebateAmount().intValue() < 50)) {
				throw new FanbeiException("账户余额不足50元,无法兑换", true);
			}
			if (amount.compareTo(afUserAccountDo.getRebateAmount().intValue()) > 0) {
				throw new FanbeiException("账户余额小于兑换金额", true);
			}
			AfRecycleRatioDo afRecycleRatioDo = afRecycleService.addExchange(uid, amount, afUserAccountDo.getRebateAmount());
			data.put("data", afRecycleRatioDo);
			result = H5CommonResponse.getNewInstance(true, "兑换成功", null, data).toString();
		} catch (Exception e) {
			logger.error("exchangeApi,error=",e);
			throw new FanbeiException(FanbeiExceptionCode.FAILED);
		}
		return result;
	}
	
	
	
	
	
	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 *         Long @throws
	 */
	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;
	}
	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);
			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}
	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}


}
