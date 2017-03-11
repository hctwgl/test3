/**
 * 
 */
package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfCouponDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月8日下午8:36:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5FanBeiWebController extends BaseController {
    String  opennative = "/fanbei-web/opennative?name=";

	@Resource
	AfUserDao afUserDao;

	@Resource
	AfCouponDao afCouponDao;

	@Resource
	AfUserCouponDao afUserCouponDao;
	@Resource
	AfResourceDao afResourceDao;

	@RequestMapping(value = { "receiveCoupons" }, method = RequestMethod.GET)
	public void receiveCoupons(HttpServletRequest request, ModelMap model) throws IOException {
		AfResourceDo resourceDo = afResourceDao.getSingleResourceBytype(AfResourceType.PickedCoupon.getCode());
		String appInfotext = ObjectUtils.toString(request.getParameter("_appInfo"), "").toString();
		JSONObject appInfo = JSON.parseObject(appInfotext);
		String userName = ObjectUtils.toString(appInfo.get("userName"), "").toString();
//		String userName ="13500000405";

		String ids = resourceDo.getValue();
		List<AfCouponDo> afCouponList = afCouponDao.selectCouponByCouponIds(ids);
		List<Object> list = new ArrayList<Object>();
		for (AfCouponDo afCouponDo : afCouponList) {
			list.add(couponObjectWithAfUserCouponDto(afCouponDo));
		}
		model.put("couponList", list);
		model.put("userName", userName);
		logger.info(JSON.toJSONString(model));
	}

	public Map<String, Object> couponObjectWithAfUserCouponDto(AfCouponDo afCouponDo) {

		Map<String, Object> returnData = new HashMap<String, Object>();
		returnData.put("rid", afCouponDo.getRid());
		returnData.put("useRule", afCouponDo.getUseRule());
		returnData.put("limitAmount", afCouponDo.getLimitAmount());
		returnData.put("name", afCouponDo.getName());
		returnData.put("gmtStart", afCouponDo.getGmtStart());
		returnData.put("gmtEnd", afCouponDo.getGmtEnd());
		returnData.put("amount", afCouponDo.getAmount());
		returnData.put("limitCount", afCouponDo.getLimitCount());
		returnData.put("type", afCouponDo.getType());

		return returnData;

	}

	@ResponseBody
	@RequestMapping(value = "/pickCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickCoupon(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			String couponId = ObjectUtils.toString(request.getParameter("couponId"), "").toString();

			String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);

			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST)+opennative+H5OpenNativeType.AppLogin.getCode();

				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(), notifyUrl,null )
						.toString();
			}

			AfCouponDo couponDo = afCouponDao.getCouponById(NumberUtil.objToLongDefault(couponId, 1l));

			if (couponDo == null) {

				return H5CommonResponse
						.getNewInstance(false, FanbeiExceptionCode.USER_COUPON_NOT_EXIST_ERROR.getDesc(), "", null)
						.toString();
			}

			Integer limitCount = couponDo.getLimitCount();

			Integer myCount = afUserCouponDao.getUserCouponByUserIdAndCouponId(afUserDo.getRid(),
					NumberUtil.objToLongDefault(couponId, 1l));
			if (limitCount <= myCount) {
				return H5CommonResponse.getNewInstance(false,
						FanbeiExceptionCode.USER_COUPON_MORE_THAN_LIMIT_COUNT_ERROR.getDesc(), "", null).toString();
			}

			AfUserCouponDo userCoupon = new AfUserCouponDo();
			userCoupon.setCouponId(NumberUtil.objToLongDefault(couponId, 1l));
			userCoupon.setGmtStart(new Date());
			if (couponDo.getValidDays() == -1) {
				userCoupon.setGmtEnd(DateUtil.getFinalDate());
			} else {
				userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
			}
			userCoupon.setGmtEnd(DateUtil.addDays(new Date(), couponDo.getValidDays()));
			userCoupon.setSourceType(CouponSenceRuleType.PICK.getCode());
			userCoupon.setStatus(CouponStatus.NOUSE.getCode());
			userCoupon.setUserId(afUserDo.getRid());
			afUserCouponDao.addUserCoupon(userCoupon);
			AfCouponDo couponDoT = new AfCouponDo();
			couponDoT.setRid(couponDo.getRid());
			couponDoT.setQuotaAlready(1);
			afCouponDao.updateCouponquotaAlreadyById(couponDoT);
			return H5CommonResponse.getNewInstance(true, "成功", "", null).toString();

		} catch (Exception e) {
			return H5CommonResponse.getNewInstance(false, e.getMessage(), "", null).toString();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ald.fanbei.api.web.common.BaseController#checkCommonParam(java.lang.
	 * String, javax.servlet.http.HttpServletRequest, boolean)
	 */
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ald.fanbei.api.web.common.BaseController#parseRequestData(java.lang.
	 * String, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ald.fanbei.api.web.common.BaseController#doProcess(com.ald.fanbei.api
	 * .web.common.RequestDataVo, com.ald.fanbei.api.common.FanbeiContext,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
