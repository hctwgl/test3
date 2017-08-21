package com.ald.fanbei.api.web.h5.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseParentBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityCouponService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityItemsService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityResultService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserRebateService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityCouponDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityResultDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.BoluomeUserRebateBankDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <p>
 * Title:H5GGShareController
 * <p>
 * <p>
 * Description:
 * <p>
 * 
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年8月8日下午4:10:52
 *
 */
@Controller
@RequestMapping("/H5GGShare")
public class H5GGShareController extends H5Controller {

	@Resource
	AfBoluomeActivityItemsService afBoluomeActivityItemsService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfBoluomeActivityService afBoluomeActivityService;
	@Resource
	AfBoluomeActivityUserItemsService afBoluomeActivityUserItemsService;
	@Resource
	AfBoluomeActivityCouponService afBoluomeActivityCouponService;
	@Resource
	AfBoluomeActivityResultService afBoluomeActivityResultService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserCouponService afUserCouponService;
	private static String couponUrl = null;

	@Resource
	AfBoluomeActivityUserRebateService afBoluomeActivityUserRebateService;

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @说明：活动点亮初始化
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/initHomePage", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String initHomepage(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = " ";
		FanbeiH5Context context = new FanbeiH5Context();
		// TODO:获取活动的id
		Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 1);
		try {

			// TODO:banner轮播图后台增加一个类型，和配置。GG_TOP_BANNER.根据类型和活动id去取。
			// List<Object> bannerList = new ArrayList<>();
			List<AfResourceDo> bannerList = afResourceService
					.getResourceHomeListByTypeOrderBy(AfResourceType.GGTopBanner.getCode());
			/*
			 * if (bannerResclist != null && bannerResclist.size() > 0) {
			 * GetBorrowCashBase base = new GetBorrowCashBase(); bannerList =
			 * base.getBannerObjectWithResourceDolist(bannerResclist); }
			 */

			// TODO:resource+终极大奖的人数.初始化数据,根据类型和活动id去取。GG_FAKE_PERSON
			Map<String, Integer> fakeMap = getFakePerson(activityId);
			Integer fakeFinal = 0;
			Integer fakeJoin = 0;
			if (fakeMap != null) {
				fakeFinal = fakeMap.get("fakeFinal");
				fakeJoin = fakeMap.get("fakeJoin");
			}
			// TOOD:活动表和resource表中获取优惠券,信息
			AfBoluomeActivityCouponDo bDo = new AfBoluomeActivityCouponDo();
			bDo.setBoluomeActivityId(activityId);
			bDo.setStatus("O");
			bDo.setType("B");
			List<AfBoluomeActivityCouponDo> bList = afBoluomeActivityCouponService.getListByCommonCondition(bDo);

			// List<BoluomeCouponResponseBo> boluomeCouponList = new
			// ArrayList<>();
			List<BoluomeCouponResponseBo> boluomeCouponList = new ArrayList<>();
			if (bList != null && bList.size() > 0) {
				for (AfBoluomeActivityCouponDo bCouponDo : bList) {
					Long resourceId = bCouponDo.getCouponId();
					AfResourceDo couponResourceDo = afResourceService.getResourceByResourceId(resourceId);
					if (couponResourceDo != null) {
						String uri = couponResourceDo.getValue();
						String[] pieces = uri.split("/");
						if (pieces.length > 9) {
							String app_id = pieces[6];
							String campaign_id = pieces[8];
							String user_id = "0";
							// 获取boluome的券的内容
							String url = getCouponUrl() + "?" + "app_id=" + app_id + "&user_id=" + user_id
									+ "&campaign_id=" + campaign_id;
							String reqResult = HttpUtil.doGet(url, 10);
							if (!StringUtil.isBlank(reqResult)) {
								ThirdResponseBo thirdResponseBo = JSONObject.parseObject(reqResult,
										ThirdResponseBo.class);
								if (thirdResponseBo != null && "0".equals(thirdResponseBo.getCode())) {
									List<BoluomeCouponResponseParentBo> listParent = JSONArray
											.parseArray(thirdResponseBo.getData(), BoluomeCouponResponseParentBo.class);
									if (listParent != null && listParent.size() > 0) {
										BoluomeCouponResponseParentBo parentBo = listParent.get(0);
										if (parentBo != null) {
											String activityCoupons = parentBo.getActivity_coupons();
											String result = activityCoupons.substring(1, activityCoupons.length() - 1);
											String replacement = "," + "\"sceneId\":" + resourceId + "}";
											String rString = result.replaceAll("}", replacement);
											//字符串转为json对象
											BoluomeCouponResponseBo BoluomeCouponResponseBo = JSONObject.parseObject(rString,
													BoluomeCouponResponseBo.class);
											boluomeCouponList.add(BoluomeCouponResponseBo);

										}
									}

								}
							}
						}
					}
				}
			}
			// TODO:活动和coupon表获取终极大奖的信息
			AfBoluomeActivityCouponDo nDo = new AfBoluomeActivityCouponDo();
			nDo.setBoluomeActivityId(activityId);
			nDo.setStatus("O");
			nDo.setType("N");
			List<AfBoluomeActivityCouponDo> listnCoupon = afBoluomeActivityCouponService.getListByCommonCondition(nDo);
			List<AfCouponDo> normalCouponList = new ArrayList<>();
			if (listnCoupon != null && listnCoupon.size() > 0) {
				for (AfBoluomeActivityCouponDo ncoupon : listnCoupon) {
					AfCouponDo afCouponDo = afCouponService.getCouponById(ncoupon.getCouponId());
					if (afCouponDo != null) {
						normalCouponList.add(afCouponDo);
					}
				}
			}
			// TODO:活动的卡片
			List<AfBoluomeActivityItemsDo> itemsList = getActivityItems(activityId);

			// TODO:活动表活动规则
			AfBoluomeActivityDo activityDo = afBoluomeActivityService.getById(activityId);
			String despcription = activityDo.getDescription();

			Map<String, Object> data = new HashMap<String, Object>();
			// TODO:用户如果登录，则用户的该活动获得的卡片list
			AfBoluomeActivityUserItemsDo useritemsDo = new AfBoluomeActivityUserItemsDo();
			context = doH5Check(request, false);

			// TODO:获取登录着的userName或者id
			String userName = context.getUserName();
			// 为了兼容从我也要点亮中调用主页接口
			if (StringUtil.isBlank(userName)) {
				userName = request.getParameter("userName");
			}
			// String userName = request.getParameter("userName");
			if (!StringUtil.isBlank(userName)) {
				Long userId = convertUserNameToUserId(userName);
				if (userId != null && userId > 0) {
					useritemsDo.setUserId(userId);
					useritemsDo.setBoluomeActivityId(activityId);
					useritemsDo.setStatus("NORMAL");
					List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
							.getListByCommonCondition(useritemsDo);
					data.put("userItemsList", userItemsList);

					// 修改itemsList内容，把num统计上去
					itemsList = addNumber(activityId, userId);
					// 吧用户名传给页面，进行下一步操作。

					data.put("userId", userId);
					if (!StringUtil.isBlank(userName)) {
						data.put("userName", userName);
					}
				}
			}

			data.put("bannerList", bannerList);
			data.put("fakeFinal", fakeFinal);
			data.put("fakeJoin", fakeJoin);
			data.put("boluomeCouponList", boluomeCouponList);
			data.put("normalCouponList", normalCouponList);
			data.put("itemsList", itemsList);
			data.put("despcription", despcription);
			resultStr = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		    doMaidianLog(request, H5CommonResponse.getNewInstance(true, "success"));
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("initHomePage", e);
			 doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("initHomePage", exception);
			 doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		}

		return resultStr;
	}

	private List<AfBoluomeActivityItemsDo> addNumber(Long activityId, Long userId) {
		AfBoluomeActivityItemsDo t = new AfBoluomeActivityItemsDo();
		t.setBoluomeActivityId(activityId);
		List<AfBoluomeActivityItemsDo> resultList = afBoluomeActivityItemsService.getListByCommonCondition(t);
		if (resultList != null && resultList.size() > 0) {
			for (AfBoluomeActivityItemsDo itemsDo : resultList) {
				Long itemsId = itemsDo.getRid();
				AfBoluomeActivityUserItemsDo conditionUserItems = new AfBoluomeActivityUserItemsDo();
				conditionUserItems.setItemsId(itemsId);
				conditionUserItems.setUserId(userId);
				conditionUserItems.setStatus("NORMAL");
				// 查处此用户用户的此卡片的数量
				List<AfBoluomeActivityUserItemsDo> numList = afBoluomeActivityUserItemsService
						.getListByCommonCondition(conditionUserItems);
				if (numList != null && numList.size() > 0) {
					itemsDo.setNum(numList.size());
				}
			}
		}
		return resultList;
	}

	/**
	 * 
	 * @说明：获得活动的卡片
	 * @param: @param
	 *             activityId
	 * @param: @return
	 * @return: List<AfBoluomeActivityItemsDo>
	 */
	private List<AfBoluomeActivityItemsDo> getActivityItems(Long activityId) {
		AfBoluomeActivityItemsDo iDo = new AfBoluomeActivityItemsDo();
		iDo.setBoluomeActivityId(activityId);
		iDo.setType("CARD");
		List<AfBoluomeActivityItemsDo> itemsList = afBoluomeActivityItemsService.getListByCommonCondition(iDo);
		return itemsList;
	}

	/**
	 * 
	 * @说明：获得用户优惠券列表
	 * @param: @return
	 * @return: String
	 */
	private static String getCouponUrl() {
		if (couponUrl == null) {
			couponUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_COUPON_URL);
			return couponUrl;
		}
		return couponUrl;
	}

	/**
	 * 
	 * @说明：获得假的人数的数据
	 * @param: @param
	 *             activityId
	 * @param: @return
	 * @return: Map<String,Integer>
	 */
	private Map<String, Integer> getFakePerson(Long activityId) {
		Map<String, Integer> resultMap = new HashMap<>();
		AfResourceDo fakeResourceDo = afResourceService.getFakePersonByActivityId(activityId.toString());
		// TODO:取得对应条件的实体.
		if (fakeResourceDo != null) {

			String fakeFinalStr = fakeResourceDo.getValue2();
			Integer fakeFinal = new Integer(fakeFinalStr);

			Integer addFakeFinal = afBoluomeActivityUserItemsService.getFakeFinal(activityId);
			if (addFakeFinal != null) {
				fakeFinal += addFakeFinal;
			}
			/*AfBoluomeActivityResultDo t = new AfBoluomeActivityResultDo();
			t.setBoluomeActivityId(activityId);
			List<AfBoluomeActivityResultDo> listResult = afBoluomeActivityResultService.getListByCommonCondition(t);
			if (listResult != null && listResult.size() > 0) {
				fakeFinal += listResult.size();
			}
*/
			// TOOD:resource +表中获取参与人数（user_items）
			String fakeJoinStr = fakeResourceDo.getValue1();
			Integer fakeJoin = new Integer(fakeJoinStr);
			Integer addFakeJoin = afBoluomeActivityUserItemsService.geFakeJoin(activityId);
			if (addFakeJoin != null) {
				fakeJoin += addFakeJoin;
			}
			/*AfBoluomeActivityUserItemsDo itemsDo = new AfBoluomeActivityUserItemsDo();
			itemsDo.setBoluomeActivityId(activityId);
			itemsDo.setStatus("NORMAL");
			List<AfBoluomeActivityUserItemsDo> listItems = afBoluomeActivityUserItemsService.getListByCommonCondition(itemsDo);
			if (listItems != null && listItems.size() > 0) {
				fakeJoin += listItems.size();
			}*/
			resultMap.put("fakeFinal", fakeFinal);
			resultMap.put("fakeJoin", fakeJoin);
		}
		return resultMap;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	/**
	 * 
	 * @说明：赠送卡片
	 * @param: @param
	 *             request
	 * @param: response
	 * @param: @return
	 * @return: String
	 * @throws UnsupportedEncodingException
	 * 
	 */
	@RequestMapping(value = "/sendItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String sendItems(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();

		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");

		try {
			context = doH5Check(request, false);
			Long userId = context.getUserId();
			if (userId == null) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			if (userId != null && activityId != null) {
				// 选出itemsId
				List<AfBoluomeActivityUserItemsDo> resultList = new ArrayList<>();
				List<AfBoluomeActivityItemsDo> itemsList = new ArrayList<>();
				List<Long> tempItemsList = afBoluomeActivityUserItemsService.getItemsByActivityIdUserId(activityId,
						userId);// 大于1张卡片的用户记录
				if (tempItemsList != null && tempItemsList.size() > 0) {
					itemsList = addNumber(activityId, userId);

					for (Long itemsId : tempItemsList) {
						AfBoluomeActivityUserItemsDo t = new AfBoluomeActivityUserItemsDo();
						t.setSourceUserId(userId);
						t.setBoluomeActivityId(activityId);
						t.setItemsId(itemsId);
						t.setStatus("NORMAL");
						List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
								.getListByCommonCondition(t);
						if (userItemsList != null && userItemsList.size() > 0) {
							resultList.addAll(userItemsList);
						}
					}
				}
				Map<String, Object> data = new HashMap<>();
				data.put("itemsList", itemsList);
				data.put("userItemsList", resultList);
				resultStr = H5CommonResponse.getNewInstance(true, "赠送卡片初始化成功", "", data).toString();
			}
          
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("sendItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getMessage()).toString();
			logger.error("sendItems" + context, e);
		}
		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	/**
	 * 
	 * @说明：点击确定赠送，来改变卡片的中间状态
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/doSendItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String doSendItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			String userName = context.getUserName();
			// String userName = request.getParameter("userName");
			Long userId = convertUserNameToUserId(userName);
			if (userId == null) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			Long userItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));
			// 改变用户卡片的中见状态
			updateUserItemsStatus(userItemsId, "FROZEN");
			// 埋点
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "success"));
			resultStr = H5CommonResponse.getNewInstance(true, "赠送成功").toString();
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("doSendItems" + context, e);
			 doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送失败", "", e.getMessage()).toString();
			logger.error("doSendItems" + context, e);
			 doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	/**
	 * 
	 * @说明：改变用户卡片状态
	 * @param: @param
	 *             userItemsId
	 * @param: @param
	 *             status
	 * @return: void
	 */
	private void updateUserItemsStatus(Long userItemsId, String status) {
		AfBoluomeActivityUserItemsDo resourceDo = new AfBoluomeActivityUserItemsDo();// afBoluomeActivityUserItemsService.getById(userItemsId);
		resourceDo.setRid(userItemsId);
		resourceDo.setStatus(status);
		resourceDo.setGmtModified(new Date());
		afBoluomeActivityUserItemsService.updateById(resourceDo);

	}

	/**
	 * 
	 * @说明：卡片赠送(专享初始化页面,无需登录) @param: @param request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/ggSendItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String ggSendItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		try {
			Long userItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));
			AfBoluomeActivityUserItemsDo userItemsDo = afBoluomeActivityUserItemsService.getById(userItemsId);
			if (userItemsDo != null) {
				AfBoluomeActivityDo activityDo = afBoluomeActivityService.getById(userItemsDo.getBoluomeActivityId());
				String desctiption = "";
				if (activityDo != null) {
					desctiption = activityDo.getDescription();
				}
				Map<String, Integer> fakeMap = getFakePerson(userItemsDo.getBoluomeActivityId());
				Integer fakeFinal = 0;
				Integer fakeJoin = 0;
				if (fakeMap != null) {
					fakeFinal = fakeMap.get("fakeFinal");
					fakeJoin = fakeMap.get("fakeJoin");
				}
				AfUserDo userDo = afUserService.getUserById(userItemsDo.getUserId());
				if (userDo != null) {
					String friend = userDo.getNick();
					if (friend.isEmpty()) {
						friend = userDo.getUserName();
					}
					AfBoluomeActivityItemsDo itemsDo = afBoluomeActivityItemsService.getById(userItemsDo.getItemsId());
					AfResourceDo resourceDo = new AfResourceDo();
					if (itemsDo != null) {
						resourceDo = afResourceService.getGGSpecificBanner(itemsDo.getRefId().toString());
					}
					Map<String, Object> data = new HashMap<>();
					data.put("resourceDo", resourceDo);
					data.put("description", desctiption);
					data.put("friend", friend);
					data.put("itemsDo", itemsDo);
					data.put("userItemsDo", userItemsDo);
					data.put("fakeFinal", fakeFinal);
					data.put("fakeJoin", fakeJoin);
					resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
				}
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("ggSendItems error", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getMessage()).toString();
			logger.error("ggSendItems error", e);
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

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

	/**
	 * 
	 * @说明：领走卡片
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */

	@RequestMapping(value = "/pickUpItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String pickUpItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			String userName = context.getUserName();
			//TODO:String userName = request.getParameter("userName");
			Long userId = convertUserNameToUserId(userName);
			Long resourceUserItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));// 卡片主人的主键id
			if (userId == null) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			if (userId != null) {

				AfBoluomeActivityUserItemsDo resourceUserItemsDo = afBoluomeActivityUserItemsService
						.getById(resourceUserItemsId);// old卡片的内容
				if (resourceUserItemsDo != null) {
					//查看卡片是否过期 resourceUserItemsId
					if (!resourceUserItemsDo.getStatus().equals("FROZEN") ) {
						if (resourceUserItemsDo.getStatus().equals("SENT")) {
							return H5CommonResponse.getNewInstance(true, "卡片已被其他用户领取，不能领取").toString();
						}
						return H5CommonResponse.getNewInstance(true, "该卡片已经超时退给赠送者，不能领取").toString();
					}
					
					
					Long reourceUserId = resourceUserItemsDo.getUserId();
					// 你没有权限领取此卡片
					if (reourceUserId.equals(userId)) {
						return H5CommonResponse.getNewInstance(true, "你没有权限领取此卡片").toString();
					}
					
					
					// 查看是否已经领走
					AfBoluomeActivityUserItemsDo newUserItemsDoCondition = new AfBoluomeActivityUserItemsDo();
					newUserItemsDoCondition.setUserId(userId);
					newUserItemsDoCondition.setSourceId(resourceUserItemsId);
					newUserItemsDoCondition.setStatus("NORMAL");
					List<AfBoluomeActivityUserItemsDo> list = afBoluomeActivityUserItemsService
							.getListByCommonCondition(newUserItemsDoCondition);
					if (list == null || list.size() == 0) {
						/*//检查卡片是否过期（是否有FROZEN状态变成了NORMAL）
						if (resourceUserItemsDo.getStatus() == "NORMAL") {
							return H5CommonResponse.getNewInstance(true, "改卡片已经超时退给赠送者，不能领取").toString();
						}*/
						// 领取卡片成功，修改原来的用户卡片状态，并且增加一条新的用户卡片记录
						AfBoluomeActivityUserItemsDo insertDo = new AfBoluomeActivityUserItemsDo();
						insertDo.setBoluomeActivityId(resourceUserItemsDo.getBoluomeActivityId());
						AfUserDo insertUser = afUserService.getUserById(userId);
						if (insertUser == null) {
							return H5CommonResponse.getNewInstance(false, "用户账号异常").toString();
						}
						insertDo.setUserName(insertUser.getUserName());
						insertDo.setUserId(userId);
						insertDo.setStatus("NORMAL");
						insertDo.setSourceId(resourceUserItemsId);
						insertDo.setSourceUserId(resourceUserItemsDo.getUserId());
						insertDo.setItemsId(resourceUserItemsDo.getItemsId());
						insertDo.setGmtSended(new Date());
						afBoluomeActivityUserItemsService.saveRecord(insertDo);

						updateUserItemsStatus(resourceUserItemsId, "SENT");
						resultStr = H5CommonResponse.getNewInstance(true, "领取卡片成功").toString();
					} else {
						return H5CommonResponse.getNewInstance(true, "你已领走卡片，无需重复领取").toString();
					}

				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "领取卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("pickUpItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "领取卡片失败", "", e.getMessage()).toString();
			logger.error("pickUpItems" + context, e);
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	/**
	 * 
	 * @说明：我也要点亮
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/lightItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String lightItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			String userName = context.getUserName();
			// String userName = request.getParameter("userName");
			Long userId = convertUserNameToUserId(userName);
			Map<String, Object> data = new HashMap<>();
			if (userId == null) {

				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			data.put("userName", userName);
			resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("lightItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getMessage()).toString();
			logger.error("lightItems" + context, e);
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	/**
	 * 
	 * 说明：索要卡片
	 * 
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/askForItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String askForItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			String userName = context.getUserName();
			// String userName = request.getParameter("userName");
			Long userId = convertUserNameToUserId(userName);
			if (userId == null) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			List<AfBoluomeActivityItemsDo> itemsList = getActivityItems(activityId);
			Map<String, Object> data = new HashMap<>();
			data.put("itemsList", itemsList);
			resultStr = H5CommonResponse.getNewInstance(true, "获取卡片成功", "", data).toString();
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "success"));
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("askForItems" + context, e);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getMessage()).toString();
			logger.error("askForItems" + context, e);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	/**
	 * 
	 * @说明：赠送给好友
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/sendToFriend", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String sendToFriend(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			String userName = context.getUserName();
			// String userName = request.getParameter("userName");
			Long userId = convertUserNameToUserId(userName);
			if (userId == null) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			Long itemsId = NumberUtil.objToLong(request.getParameter("itemsId"));
			String friendName = request.getParameter("friendName");
			Long friendId = convertUserNameToUserId(friendName);
			if (userId != null) {
				AfBoluomeActivityItemsDo itemsDo = afBoluomeActivityItemsService.getById(itemsId);
				if (itemsDo != null) {
					// 验证登录用户该卡片是否大于1张
					AfBoluomeActivityUserItemsDo userItemsDo = new AfBoluomeActivityUserItemsDo();
					userItemsDo.setUserId(userId);
					userItemsDo.setItemsId(itemsId);
					userItemsDo.setStatus("NORMAL");
					List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
							.getListByCommonCondition(userItemsDo);
					if (userItemsList == null || userItemsList.size() < 2) {
						return H5CommonResponse.getNewInstance(true, "抱歉，你暂时没有足够此卡片").toString();
					}
					// 若大于一张则，
					// 登录用户卡片选一张，然后赠状态设为已经赠送
					AfBoluomeActivityUserItemsDo resourceUserItemsDo = userItemsList.get(0);
					updateUserItemsStatus(resourceUserItemsDo.getRid(), "SENT");

					// 朋友的userItems表中增加一条卡片记录
					AfBoluomeActivityUserItemsDo insertDo = new AfBoluomeActivityUserItemsDo();
					insertDo.setBoluomeActivityId(resourceUserItemsDo.getBoluomeActivityId());
					AfUserDo insertUser = afUserService.getUserById(friendId);
					if (insertUser == null) {
						return H5CommonResponse.getNewInstance(false, "用户账号异常").toString();
					}
					insertDo.setUserName(insertUser.getUserName());
					insertDo.setUserId(friendId);
					insertDo.setStatus("NORMAL");
					insertDo.setSourceId(resourceUserItemsDo.getRid());
					insertDo.setSourceUserId(resourceUserItemsDo.getUserId());
					insertDo.setItemsId(resourceUserItemsDo.getItemsId());
					insertDo.setGmtSended(new Date());
					afBoluomeActivityUserItemsService.saveRecord(insertDo);

					resultStr = H5CommonResponse.getNewInstance(true, "赠送成功").toString();
					// 埋点
					doMaidianLog(request, H5CommonResponse.getNewInstance(true, "success"));
				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("sendToFriend" + context, e);
			 doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送失败", "", e.getMessage()).toString();
			logger.error("sendToFriend" + context, e);
			 doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	/**
	 * 
	 * @说明：索要初始化页面（原用户的id由页面带过来） @param: @param request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/ggAskForItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String doAskForItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		try {
			
			Long itemsId = NumberUtil.objToLong(request.getParameter("itemsId"));
			String userName = request.getParameter("friendName");
			logger.info("method = /H5GGShare/ggAskForItems"+"itemsId=" +itemsId+"friendName"+userName);
			Long userId = convertUserNameToUserId(userName);// 绱㈣浜虹殑鐢ㄦ埛id
			AfBoluomeActivityItemsDo itemsDo = afBoluomeActivityItemsService.getById(itemsId);
			if (itemsDo != null) {
				AfBoluomeActivityDo activityDo = afBoluomeActivityService.getById(itemsDo.getBoluomeActivityId());
				String desctiption = "";
				if (activityDo != null) {
					desctiption = activityDo.getDescription();
				}
				Map<String, Integer> fakeMap = getFakePerson(itemsDo.getBoluomeActivityId());
				Integer fakeFinal = 0;
				Integer fakeJoin = 0;
				if (fakeMap != null) {
					fakeFinal = fakeMap.get("fakeFinal");
					fakeJoin = fakeMap.get("fakeJoin");
				}
				AfUserDo userDo = afUserService.getUserById(userId);
				if (userDo != null) {
					String friend = userDo.getNick();
					if (StringUtil.isBlank(friend)) {
						friend = userDo.getUserName();
					}
					AfResourceDo resourceDo = new AfResourceDo();
					if (itemsDo != null) {
						resourceDo = afResourceService.getGGSpecificBanner(itemsDo.getRefId().toString());
					}
					Map<String, Object> data = new HashMap<>();

					data.put("resourceDo", resourceDo);
					data.put("description", desctiption);
					data.put("friend", friend);
					data.put("friendId", userId);
					data.put("itemsDo", itemsDo);
					data.put("fakeFinal", fakeFinal);
					data.put("fakeJoin", fakeJoin);
					logger.info("data=" + JSONObject.toJSONString(data));
					resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
				}
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("ggAskForItems error", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getMessage()).toString();
			logger.error("ggAskForItems error", e);
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
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

	/**
	 * 
	 * @说明：获取排行榜
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/listRank", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String listBank(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		try {
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			List<BoluomeUserRebateBankDo> rankList = afBoluomeActivityUserRebateService.getBankList(activityId);
			if (rankList != null) {
				for (BoluomeUserRebateBankDo rebateBankDo : rankList) {
					String userName = rebateBankDo.getUserName();
					if (!StringUtil.isBlank(userName)) {
						userName = changePhone(userName);
						rebateBankDo.setUserName(userName);
					}
				}

				Map<String, Object> data = new HashMap<>();
				Map<String, Integer> fakeMap = getFakePerson(activityId);
				Integer fakeFinal = fakeMap.get("fakeFinal");
				int rebateNumber = rankList.size();
				if (fakeFinal != null) {
					rebateNumber += fakeFinal;
				}
				data.put("rebateNumber", rebateNumber);
				data.put("rankList", rankList);
				resultStr = H5CommonResponse.getNewInstance(true, "获取排行榜成功", "", data).toString();
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("listRank", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getMessage()).toString();
			logger.error("listRank", e);
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	@RequestMapping(value = "/pickUpSuperPrize", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String pickUpSuperPrize(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			String userName = context.getUserName();
			Long userId = convertUserNameToUserId(userName);
			if (userId == null) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			// Long userId = context.getUserId();
			if (activityId != null && userId != null) {
				// 判断是否已经领取红包
				AfBoluomeActivityResultDo conditionDo = new AfBoluomeActivityResultDo();
				conditionDo.setBoluomeActivityId(activityId);
				conditionDo.setUserId(userId);
				List<AfBoluomeActivityResultDo> isHave = afBoluomeActivityResultService
						.getListByCommonCondition(conditionDo);
				if (isHave != null && isHave.size() > 0) {
					return H5CommonResponse.getNewInstance(false, "您已经成功领取51元现金红包，不能重复领取").toString();
				}
				AfBoluomeActivityCouponDo conditionCoupon = new AfBoluomeActivityCouponDo();
				conditionCoupon.setBoluomeActivityId(activityId);
				conditionCoupon.setStatus("O");
				conditionCoupon.setType("N");
				AfBoluomeActivityCouponDo resultCoupon = afBoluomeActivityCouponService
						.getByCommonCondition(conditionCoupon);
				// 活动卡片
				AfBoluomeActivityItemsDo activityItemsDo = new AfBoluomeActivityItemsDo();
				activityItemsDo.setBoluomeActivityId(activityId);
				List<AfBoluomeActivityItemsDo> itemsList = afBoluomeActivityItemsService
						.getListByCommonCondition(activityItemsDo);

				// all userItmes list to be deleted later
				List<AfBoluomeActivityUserItemsDo> deleteList = new ArrayList<>();
				// TODO:判断用户是否有活动配置的所有的卡片
				if (itemsList != null && itemsList.size() > 0) {
					for (AfBoluomeActivityItemsDo uDo : itemsList) {
						Long itemsId = uDo.getRid();
						// 查到用户活动卡片
						AfBoluomeActivityUserItemsDo useritemsdoo = new AfBoluomeActivityUserItemsDo();
						useritemsdoo.setBoluomeActivityId(activityId);
						useritemsdoo.setUserId(userId);
						useritemsdoo.setStatus("NORMAL");
						useritemsdoo.setItemsId(itemsId);
						List<AfBoluomeActivityUserItemsDo> useritemsList = afBoluomeActivityUserItemsService
								.getListByCommonCondition(useritemsdoo);
						if (useritemsList == null || useritemsList.size() <= 0) {
							return H5CommonResponse.getNewInstance(false, "红包领取失败：缺少没有" + uDo.getName() + "卡片")
									.toString();
						}
						deleteList.add(useritemsList.get(0));
					}
				}
				// to delete
				if (deleteList != null && deleteList.size() > 0) {
					for(AfBoluomeActivityUserItemsDo delete : deleteList){
						afBoluomeActivityUserItemsService.deleteByRid(delete.getRid());
					}
					
					if (resultCoupon != null) {
						// 把终极大奖给插入用户result表中
						AfBoluomeActivityResultDo conditionResultDo = new AfBoluomeActivityResultDo();
						conditionResultDo.setBoluomeActivityId(activityId);
						conditionResultDo.setUserId(userId);
						conditionResultDo.setUserName(context.getUserName());
						conditionResultDo.setResult(resultCoupon.getCouponId());

						afBoluomeActivityResultService.saveRecord(conditionResultDo);
						//bug:吧现金大奖转为用户余额。
						AfUserAccountDo accountDo = new AfUserAccountDo();
						BigDecimal rebateAmount = null;
						//查到n券对应的金额
						AfCouponDo userCouponDo = afCouponService.getCouponById(resultCoupon.getCouponId());//afUserCouponService.getUserCouponById(resultCoupon.getCouponId());
						if (userCouponDo != null) {
							rebateAmount = userCouponDo.getAmount();
							accountDo.setRebateAmount(rebateAmount);
							accountDo.setUserId(userId);
							afUserAccountService.updateUserAccount(accountDo);
							// 从用户卡片去掉活动卡片的一个。
							resultStr = H5CommonResponse.getNewInstance(true, "红包领取成功，请前往  我的-返利金额  查看").toString();

						}
						
					}
				}
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "红包领取失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("pickUpSuperPrize" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "红包领取失败", "", e.getMessage()).toString();
			logger.error("pickUpSuperPrize" + context, e);
		}

		// doMaidianLog(request, resultStr);
		return resultStr;
	}

	@Resource
	AfShopService afShopService;
	@Resource
	AfUserDao afUserDao;

	/**
	 * @author qiao
	 * @说明：逛逛活动点亮过程中的领券
	 * @param: @param
	 *             request
	 * @param: @param
	 *             model
	 * @param: @return
	 * @param: @throws
	 *             IOException
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/pickBoluomeCouponWeb", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickBoluomeCouponWeb(HttpServletRequest request, ModelMap model) throws IOException {
		try {
			Long sceneId = NumberUtil.objToLongDefault(request.getParameter("sceneId"), null);
			FanbeiH5Context context = new FanbeiH5Context();
			context = doH5Check(request, false);
			String userName = context.getUserName();
			// String userName =
			// ObjectUtils.toString(request.getParameter("userName"),
			// "").toString();
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userName = {}", sceneId, userName);
			if (sceneId == null) {
				return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc())
						.toString();
			}

			if (StringUtils.isEmpty(userName)) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				return H5CommonResponse.getNewInstance(false, "没有登录", notifyUrl, null).toString();
			}
			AfUserDo afUserDo = afUserDao.getUserByUserName(userName);
			if (afUserDo == null) {
				String notifyUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
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
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START.getDesc())
						.toString();
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END.getDesc())
						.toString();
			}

			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
					resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			String code = resultJson.getString("code");

			if ("10222".equals(code) || "10206".equals(code)) {
				return H5CommonResponse.getNewInstance(true, "您已领过优惠券，快去使用吧~").toString();
			} else if ("10305".equals(code)) {
				return H5CommonResponse.getNewInstance(true, "您下手慢了哦，优惠券已领完，下次再来吧").toString();
			} else if (!"0".equals(code)) {
				return H5CommonResponse.getNewInstance(true, resultJson.getString("msg")).toString();
			}
			return H5CommonResponse.getNewInstance(true, "恭喜你领券成功").toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}
}
