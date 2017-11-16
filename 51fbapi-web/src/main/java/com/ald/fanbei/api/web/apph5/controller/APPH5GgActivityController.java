package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5GgActivity;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBoluomeUserCouponVo;
import com.ald.fanbei.api.web.vo.BoluomeActivityInviteCeremonyVo;
import com.ald.fanbei.api.web.vo.BoluomeActivityInviteFriendVo;
import com.ald.fanbei.api.web.vo.userReturnBoluomeCouponVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: APPH5GgActivityController
 * @Description: 吃玩住行活动
 * @author chenqiwei
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @date 2017年11月14日 下午2:07:40
 *
 */
@RestController
@RequestMapping(value = "/h5GgActivity", produces = "application/json;charset=UTF-8")
public class APPH5GgActivityController extends BaseController {

    @Resource
    AfUserService afUserService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    TongdunUtil tongdunUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfBoluomeUserCouponService afBoluomeUserCouponService;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfShopService afShopService;
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    AfUserDao afUserDao;

    String opennative = "/fanbei-web/opennative?name=";

    @RequestMapping(value = "/returnCoupon", method = RequestMethod.POST)
    public String returnCoupon(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
	// String userName =
	// ObjectUtils.toString(request.getParameter("userName"),
	// "").toString();
	FanbeiWebContext context = new FanbeiWebContext();
	String resultStr = "";
	try {
	    context = doWebCheck(request, false);
	    Long userId = convertUserNameToUserId(context.getUserName());
	    List<userReturnBoluomeCouponVo> returnCouponList = new ArrayList<userReturnBoluomeCouponVo>();
	    AfBoluomeUserCouponVo vo = new AfBoluomeUserCouponVo();
	    // 未登录时初始化一些数据
	    AfShopDo shopDo = new AfShopDo();
	    shopDo.setType(H5GgActivity.WAIMAI.getCode());
	    AfShopDo shop = afShopService.getShopInfoBySecTypeOpen(shopDo);
	    if (shop != null) {
		vo.setShopId(shop.getRid());
	    }
	    BigDecimal couponAmount = new BigDecimal(String.valueOf(0));
	    BigDecimal inviteAmount = new BigDecimal(String.valueOf(0));
	    vo.setInviteAmount(inviteAmount);
	    vo.setCouponAmount(couponAmount);
	    vo.setReturnCouponList(returnCouponList);
	    if (userId == null) {
		return H5CommonResponse.getNewInstance(true, "获取返券列表成功", null, vo).toString();
	    }

	    // 登录时返回数据
	    AfBoluomeUserCouponDo queryUserCoupon = new AfBoluomeUserCouponDo();
	    queryUserCoupon.setUserId(userId);
	    queryUserCoupon.setChannel(H5GgActivity.RECOMMEND.getCode());

	    List<AfBoluomeUserCouponDo> userCouponList = afBoluomeUserCouponService.getUserCouponListByUerIdAndChannel(queryUserCoupon);
	    for (AfBoluomeUserCouponDo userCoupon : userCouponList) {
		long couponId = userCoupon.getCouponId();

		userReturnBoluomeCouponVo returnCouponVo = new userReturnBoluomeCouponVo();
		// returnCouponVo.setRegisterTime(DateUtil.formatDateForPatternWithHyhen(userCoupon.getGmtCreate()));
		// 被邀请人没有订单：未消费; 有订单且订单没有一个是完成的(或者有订单且用户优惠券记录的优惠券id等于0)：未完成。
		// 有完成的订单(或者有订单且用户优惠券记录的优惠券id大于0)：
		long refUserId = userCoupon.getRefId();
		// 手机号
		AfUserDo uDo = afUserService.getUserById(refUserId);
		if (uDo != null) {
		    returnCouponVo.setInviteeMobile(changePhone(uDo.getUserName()));
		    returnCouponVo.setRegisterTime(DateUtil.formatDateForPatternWithHyhen(uDo.getGmtCreate()));
		}

		// 订单状态
		AfOrderDo order = new AfOrderDo();
		order.setUserId(refUserId);
		int queryCount = afOrderService.getOrderCountByStatusAndUserId(order);
		if (queryCount <= 0) {
		    returnCouponVo.setStatus(H5GgActivity.NOCONSUME.getDescription());
		}
		if (queryCount > 0) {
		    if (couponId <= 0) {
			returnCouponVo.setStatus(H5GgActivity.NOFINISH.getDescription());
		    } else {
			returnCouponVo.setStatus(H5GgActivity.ALREADYCONSUME.getDescription());
		    }
		}
		if (couponId <= 0) {
		    returnCouponVo.setReward("0");
		} else {
		    AfResourceDo rDo = afResourceService.getResourceByResourceId(couponId);
		    if (rDo != null) {
			returnCouponVo.setReward(rDo.getName());
		    }
		    // 通过af_resoource 获取url，再调用菠萝觅接口,获取对应金额
		    try {
			AfResourceDo afResourceDo = afResourceService.getResourceByResourceId(couponId);
			if (afResourceDo != null) {
			    // List<BrandActivityCouponResponseBo>
			    // activityCouponList =
			    // boluomeUtil.getActivityCouponList(afResourceDo.getValue());
			    // BrandActivityCouponResponseBo bo =
			    // activityCouponList.get(0);
			    BigDecimal money = new BigDecimal(String.valueOf(afResourceDo.getPic1()));
			    couponAmount = couponAmount.add(money);
			}
		    } catch (Exception e) {
			logger.error("get coluome activity inviteAmount error", e.getStackTrace());
		    }
		}
		returnCouponList.add(returnCouponVo);
	    }
	    // 好友借钱邀请者得到的奖励总和 inviteAmount af_recommend_money表
	    inviteAmount = new BigDecimal(afRecommendUserService.getSumPrizeMoney(userId));

	    vo.setReturnCouponList(returnCouponList);
	    vo.setInviteAmount(inviteAmount);
	    vo.setCouponAmount(couponAmount);
	    resultStr = H5CommonResponse.getNewInstance(true, "获取返券列表成功", null, vo).toString();
	} catch (Exception e) {
	    logger.error("/h5GgActivity/returnCoupon" + context + "error = {}", e.getStackTrace());
	    resultStr = H5CommonResponse.getNewInstance(false, "获取返券列表失败").toString();
	}
	return resultStr;

    }

    @RequestMapping(value = "/inviteFriend", method = RequestMethod.POST)
    public String inviteFriend(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
	// String userName =
	// ObjectUtils.toString(request.getParameter("userName"),
	// "").toString();
	FanbeiWebContext context = new FanbeiWebContext();
	String resultStr = "";
	try {
	    context = doWebCheck(request, false);
	    Long userId = convertUserNameToUserId(context.getUserName());
	    BoluomeActivityInviteFriendVo inviteFriendVo = new BoluomeActivityInviteFriendVo();
	    AfResourceDo resourceInfo = new AfResourceDo();

	    resourceInfo = afResourceService.getConfigByTypesAndSecType(H5GgActivity.GGACTIVITY.getCode(), H5GgActivity.DINEANDDASH.getCode());
	    // 未登录时初始化一些数据
	    if (resourceInfo != null) {
		inviteFriendVo.setImage(resourceInfo.getValue());
		// inviteFriendVo.setText(text);
		inviteFriendVo.setActivityRule(resourceInfo.getValue1());
		inviteFriendVo.setExample(resourceInfo.getValue2());

	    }
	    if (userId == null) {
		return H5CommonResponse.getNewInstance(true, "获取邀请好友吃霸王餐页面信息成功", null, inviteFriendVo).toString();
	    }

	    // 登录时返回数据
	    AfUserDo uDo = afUserService.getUserById(userId);
	    if (uDo != null) {
		inviteFriendVo.setInviteCode(uDo.getRecommendCode());
	    }

	    resultStr = H5CommonResponse.getNewInstance(true, "获取邀请好友吃霸王餐页面信息成功", null, inviteFriendVo).toString();
	} catch (Exception e) {
	    logger.error("/h5GgActivity/returnCoupon" + context + "error = {}", e.getStackTrace());
	    resultStr = H5CommonResponse.getNewInstance(false, "获取邀请好友吃霸王餐页面信息失败").toString();
	}
	return resultStr;

    }

    @RequestMapping(value = "/inviteCeremony", method = RequestMethod.POST)
    public String inviteCeremony(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
	// String userName =
	// ObjectUtils.toString(request.getParameter("userName"),
	// "").toString();
	FanbeiWebContext context = new FanbeiWebContext();
	String resultStr = "";
	try {
	    context = doWebCheck(request, false);
	    Long userId = convertUserNameToUserId(context.getUserName());
	    BoluomeActivityInviteCeremonyVo inviteCeremonyVo = new BoluomeActivityInviteCeremonyVo();
	    AfResourceDo resourceInfo = new AfResourceDo();

	    resourceInfo = afResourceService.getConfigByTypesAndSecType(H5GgActivity.GGACTIVITY.getCode(), H5GgActivity.INVITECERMONY.getCode());
	    // 未登录时初始化一些数据
	    if (resourceInfo != null) {
		AfResourceDo resource = afResourceService.getResourceByResourceId(Long.parseLong(resourceInfo.getValue1()));
		if (resource != null) {
		    inviteCeremonyVo.setCouponAmount(resource.getPic1());
		}
		inviteCeremonyVo.setImage(resourceInfo.getValue());
		inviteCeremonyVo.setSpePreference(resourceInfo.getValue2());
		inviteCeremonyVo.setActivityRule(resourceInfo.getValue3());
		inviteCeremonyVo.setExample(resourceInfo.getValue4());
	    }
	    if (userId == null) {
		return H5CommonResponse.getNewInstance(true, "获取邀请有礼页面信息成功", null, inviteCeremonyVo).toString();
	    }

	    // 登录时返回数据
	    AfUserDo uDo = afUserService.getUserById(userId);
	    if (uDo != null) {
		inviteCeremonyVo.setInviteCode(uDo.getRecommendCode());
	    }

	    resultStr = H5CommonResponse.getNewInstance(true, "获取邀请有礼页面信息成功", null, inviteCeremonyVo).toString();
	} catch (Exception e) {
	    logger.error("/h5GgActivity/returnCoupon" + context + "error = {}", e.getStackTrace());
	    resultStr = H5CommonResponse.getNewInstance(false, "获取邀请有礼页面信息失败").toString();
	}
	return resultStr;

    }

    /**
     * @author qiao
     * @说明：逛逛活动点亮过程中的领券
     * @param: @param request
     * @param: @param model
     * @param: @return
     * @param: @throws IOException
     * @return: String
     */
    @ResponseBody
    @RequestMapping(value = "/pickBoluomeCoupon", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String pickBoluomeCouponV1(HttpServletRequest request, ModelMap model) throws IOException {
	try {
	    Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
	    FanbeiWebContext context = new FanbeiWebContext();
	    context = doWebCheck(request, false);
	    String userName = context.getUserName();
	    logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}", sceneId, userName);
	    if (sceneId == null) {
		return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc()).toString();
	    }

	    if (StringUtils.isEmpty(userName)) {
		String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
		return H5CommonResponse.getNewInstance(false, "没有登录", notifyUrl, null).toString();
	    }
	    AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
	    if (afUserDo == null) {
		String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
		return H5CommonResponse.getNewInstance(false, "没有登录", notifyUrl, null).toString();
	    }

	    AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
	    if (resourceInfo == null) {
		logger.error("couponSceneId is invalid");
		return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PARAM_ERROR.getDesc()).toString();
	    }
	    PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
	    bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);

	    Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
	    Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

	    if (DateUtil.beforeDay(new Date(), gmtStart)) {
		return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc()).toString();
	    }
	    if (DateUtil.afterDay(new Date(), gmtEnd)) {
		return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc()).toString();
	    }

	    String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
	    logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
	    JSONObject resultJson = JSONObject.parseObject(resultString);
	    String code = resultJson.getString("code");

	    if ("10222".equals(code) || "10206".equals(code) || "11206".equals(code)) {
		return H5CommonResponse.getNewInstance(true, "您已领过优惠券，快去使用吧~").toString();
	    } else if ("10305".equals(code)) {
		return H5CommonResponse.getNewInstance(true, "您下手慢了哦，优惠券已领完，下次再来吧").toString();
	    } else if (!"0".equals(code)) {
		return H5CommonResponse.getNewInstance(true, resultJson.getString("msg")).toString();
	    }
	    // 存入数据库
	    AfBoluomeUserCouponDo userCoupon = new AfBoluomeUserCouponDo();
	    userCoupon.setChannel(H5GgActivity.PICK.getCode());
	    userCoupon.setCouponId(sceneId);
	    userCoupon.setUserId(afUserDo.getRid());
	    userCoupon.setStatus(1);
	    int result = afBoluomeUserCouponService.saveRecord(userCoupon);
	    if (result == 0) {
		logger.info("pickBoluomeCoupon boluome and save userCoupon fail userCoupon = {},", JSONObject.toJSONString(userCoupon));
	    }

	    // bizCacheUtil.saveObject("boluome:coupon:"+resourceInfo.getRid()+afUserDo.getUserName(),"Y",2*Constants.SECOND_OF_ONE_MONTH);
	    return H5CommonResponse.getNewInstance(true, "恭喜您领券成功").toString();

	} catch (Exception e) {
	    logger.error("pick brand coupon failed , e = {}", e.getMessage());
	    return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
	}

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
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	// TODO Auto-generated method stub
	return null;
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

    private String changePhone(String userName) {
	String newUserName = "";
	if (!StringUtil.isBlank(userName)) {
	    newUserName = userName.substring(0, 3);
	    newUserName = newUserName + "****";
	    newUserName = newUserName + userName.substring(7, 11);
	}
	return newUserName;
    }

}
