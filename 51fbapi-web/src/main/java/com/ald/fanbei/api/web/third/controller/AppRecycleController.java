package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.AppRecycleUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.web.util.AppRecycleControllerUtil;
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

/**
 * @Description: 有得卖三方 回收业务
 * @author weiqingeng
 * @date 2018年2月26日 下午4:14:31
 */
@RestController
@RequestMapping(value = "/fanbei/ydm")
public class AppRecycleController extends BaseController{

	@Autowired
	private AfRecycleService afRecycleService;
	@Autowired
	private BizCacheUtil bizCacheUtil;
	@Autowired
	private AfUserService afUserService;
	@Autowired
	private AfUserAccountService afUserAccountService;

	/**
	 * 创建订单 有得卖 三方 推送过来的订单数据(发券)
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addOrder", method = RequestMethod.POST)
	public String addOrder(HttpServletRequest request) {
		String result = "";
		String key = "";
		try {
			AfRecycleQuery afRecycleQuery = AppRecycleControllerUtil.buildParam(request);
			if(AppRecycleUtil.PARTNER_ID.equals(afRecycleQuery.getPartnerId())){
				logger.info("/fanbei/ydm/addOrder,params ={}", afRecycleQuery.toString());
				String refOrderId = afRecycleQuery.getRefOrderId();
				Long uid = afRecycleQuery.getUid();
				key = Constants.CACHKEY_GET_COUPON_LOCK + ":" + refOrderId + ":" + uid;
				boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
				if (isNotLock) {
					AfRecycleDo afRecycleDo = afRecycleService.getRecycleOrder(afRecycleQuery);
					if(null == afRecycleDo){//订单不存在，新增一条订单
						afRecycleService.addRecycleOrder(afRecycleQuery);//新增一条订单
					}else{
						result = H5CommonResponse.getNewInstance(true, "订单已存在", null, "").toString();
					}
				}
			}else{
				logger.error("/fanbei/ydm/addOrder, partnerId error,partnerId={}", afRecycleQuery.getPartnerId());
				return H5CommonResponse.getNewInstance(false, "合作商id错误", null, "").toString();
			}
		}catch (Exception e) {
			logger.error("/fanbei/ydm/addOrder, error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "订单生成失败", null, "").toString();
		} finally {
			bizCacheUtil.delCache(key);
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
