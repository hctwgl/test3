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
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityCouponService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityItemsService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityResultService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserLoginService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserRebateService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
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
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.BoluomeUserRebateBankDo;
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
	@Resource
	BoluomeUtil boluomeUtil;
	private static String couponUrl = null;
	@Resource
	AfBoluomeActivityUserLoginService afBoluomeActivityUserLoginService;
	@Resource
	AfBoluomeActivityUserRebateService afBoluomeActivityUserRebateService;
	@Resource
	BizCacheUtil bizCacheUtil;

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
		H5CommonResponse resultStr;
		FanbeiH5Context context = new FanbeiH5Context();
		String referer = request.getHeader("referer");  
		context = doH5Check(request, false);
		// TODO:获取活动的id
		Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 1);
		try {

			// TODO:banner轮播图后台增加一个类型，和配置。GG_TOP_BANNER.根据类型和活动id去取。
			List<AfResourceDo> bannerList = afResourceService
					.getResourceHomeListByTypeOrderBy(AfResourceType.GGTopBanner.getCode());

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
			bDo.setScopeApplication("LIGHT");
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
											// 字符串转为json对象
											BoluomeCouponResponseBo BoluomeCouponResponseBo = JSONObject
													.parseObject(rString, BoluomeCouponResponseBo.class);
											Long userId = context.getUserId();
											List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil
													.getActivityCouponList(uri);
											BrandActivityCouponResponseBo bo = activityCouponList.get(0);
											if (userId != null) {
												// 判断用户是否拥有该优惠券 或者已经被领取完毕
												boolean flag = false;
												flag = boluomeUtil.isHasCoupon(resourceId+"", context.getUserName());
												if (flag) 
												/*if (boluomeUtil.isUserHasCoupon(uri, userId, 1)
														|| bo.getDistributed() >= bo.getTotal()) */
												
												{
													BoluomeCouponResponseBo.setIsHas(YesNoStatus.YES.getCode());
												} else {
													BoluomeCouponResponseBo.setIsHas(YesNoStatus.NO.getCode());
												}
											}
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
			nDo.setScopeApplication("LIGHT");
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
			String despcription = null;
			String supportedNum = null;
			if (activityDo != null) {
				despcription = activityDo.getDescription();
				String activityRule = activityDo.getActivityRule();
				List<JSONObject> listRule = JSONObject.parseArray(activityRule, JSONObject.class);
				JSONObject jsonObject = listRule.get(0);
				supportedNum = jsonObject.getString("num");
			}

			Map<String, Object> data = new HashMap<String, Object>();
			// TODO:用户如果登录，则用户的该活动获得的卡片list
			AfBoluomeActivityUserItemsDo useritemsDo = new AfBoluomeActivityUserItemsDo();

			// TODO:获取登录着的userName或者id
			String userName = context.getUserName();
			// 为了兼容从我也要点亮中调用主页接口
			if (StringUtil.isBlank(userName)) {
				userName = request.getParameter("userName");
			}

			// 定义终极大奖的初始状态（只有有资格领取并且未领取的状态是）
			// update 未登录则NY
			String superPrizeStatus = "NY";
			// 用户没登陆的时候默认是只灰色状态（已经领取过了）
			// String userName = request.getParameter("userName");
			if (!StringUtil.isBlank(userName)) {

				Long userId = convertUserNameToUserId(userName);
				if (userId != null && userId > 0) {
					superPrizeStatus = "N";// 若是已经登录了则是初始化为N的状态
					useritemsDo.setUserId(userId);
					useritemsDo.setBoluomeActivityId(activityId);
					useritemsDo.setStatus("NORMAL");
					List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
							.getListByCommonCondition(useritemsDo);
					data.put("userItemsList", userItemsList);

					// 判断是否已经领取红包
					AfBoluomeActivityResultDo conditionDo = new AfBoluomeActivityResultDo();
					conditionDo.setBoluomeActivityId(activityId);
					conditionDo.setUserId(userId);
					List<AfBoluomeActivityResultDo> isHave = afBoluomeActivityResultService
							.getListByCommonCondition(conditionDo);
					if (isHave != null && isHave.size() > 0) {// 用户已经领取终极大奖,暗的
						superPrizeStatus = "YN";
					} else {

						// 是否已经有资格领取
						// all userItmes list to be deleted later
						List<AfBoluomeActivityUserItemsDo> userList = new ArrayList<>();
						// TODO:判断用户是否有活动配置的所有的卡片
						if (itemsList != null && itemsList.size() > 0) {
							// 默认是亮的，只要有一个是没有的则为暗的
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
									superPrizeStatus = "N";// 没有资格领取
									break;
								}
								userList.add(useritemsList.get(0));

							}
							// 最终用userList 和应该得到的卡片数量对比 是否一样最最后一层拦截
							if (userList != null && userList.size() == itemsList.size()) {
								superPrizeStatus = "YY";// 有资格领取，并且还未领取
							}
						}
					}

					// 修改itemsList内容，把num统计上去
					itemsList = addNumber(activityId, userId);
					// 吧用户名传给页面，进行下一步操作。

					data.put("userId", userId);
					if (!StringUtil.isBlank(userName)) {
						data.put("userName", userName);
					}
				} else {
					// 未登录则终极大奖状态不变，并且卡片个数是-1
					for (AfBoluomeActivityItemsDo itemsDoo : itemsList) {
						itemsDoo.setNum(-1);
					}
				}
			} else {
				// 未登录则终极大奖状态不变，并且卡片个数是-1
				for (AfBoluomeActivityItemsDo itemsDoo : itemsList) {
					itemsDoo.setNum(-1);
				}
			}
			// 获取文案的信息
			String popupWords = "";
			String despotCoupon = "";
			Map<String, String> map = new HashMap<String, String>();

			// 获取饿了么场景Id
			Long shopId = null;
			AfShopDo shopDo = new AfShopDo();
			shopDo.setType("WAIMAI");
			AfShopDo resultShop = afShopService.getShopInfoBySecTypeOpen(shopDo);
			if (resultShop != null) {
				shopId = resultShop.getRid();
			}

			int alreadyNum = 0;
			// 和用户登录有关的
			if (StringUtil.isNotBlank(userName)) {
				Long userId = convertUserNameToUserId(userName);
				// TODO:获取弹框文案；
				map = afBoluomeActivityService.activityOffical(userId);
				if (map != null) {
					popupWords = map.get("officalText");
					despotCoupon = map.get("despotCoupon");
				}
				// 获取已经邀请的人数
				alreadyNum = afBoluomeActivityUserLoginService.getBindingNum(activityId, userId);

			}
			if (StringUtil.isBlank(userName)) {
				alreadyNum = 0;
			}

			data.put("despotCoupon", despotCoupon);
			data.put("alreadyNum", alreadyNum);
			data.put("supportedNum", supportedNum);
			data.put("shopId", shopId);
			data.put("popupWords", popupWords);
			data.put("superPrizeStatus", superPrizeStatus);
			data.put("bannerList", bannerList);
			data.put("fakeFinal", fakeFinal);
			data.put("fakeJoin", fakeJoin);
			data.put("boluomeCouponList", boluomeCouponList);
			data.put("normalCouponList", normalCouponList);
			data.put("itemsList", itemsList);
			data.put("despcription", despcription);
			resultStr = H5CommonResponse.getNewInstance(true, "初始化成功", "", data);
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"),referer);
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc());
			logger.error("initHomePage", e);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),referer);
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage());
			logger.error("initHomePage", exception);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"),referer);
		}
		return resultStr.toString();
	}

	private List<AfBoluomeActivityItemsDo> addNumber(Long activityId, Long userId) {
		AfBoluomeActivityItemsDo t = new AfBoluomeActivityItemsDo();
		t.setBoluomeActivityId(activityId);
		t.setStatus("O");
		List<AfBoluomeActivityItemsDo> resultList = afBoluomeActivityItemsService.getListByCommonCondition(t);
		// 选出特殊的那个itemsDo
		AfBoluomeActivityItemsDo specificDo = null;
		int specificIndex = 0;
		if (resultList != null && resultList.size() > 3) {
			for (int i = 0; i < resultList.size(); i++) {
				AfBoluomeActivityItemsDo itemsDo = resultList.get(i);
				Long itemsId = itemsDo.getRid();
				AfBoluomeActivityUserItemsDo conditionUserItems = new AfBoluomeActivityUserItemsDo();
				conditionUserItems.setItemsId(itemsId);
				conditionUserItems.setUserId(userId);
				conditionUserItems.setStatus("NORMAL");
				// 查处此用户用户的此卡片的数量
				List<AfBoluomeActivityUserItemsDo> numList = afBoluomeActivityUserItemsService
						.getListByCommonCondition(conditionUserItems);
				if (numList != null && numList.size() > 0) {
					int num = numList.size();
					itemsDo.setNum(num);
					if (num > 1 && specificDo == null) {
						specificDo = itemsDo;
						specificIndex = i;
					}
				}
			}
			if (specificDo != null) {
				AfBoluomeActivityItemsDo tempDo = new AfBoluomeActivityItemsDo();
				tempDo = resultList.get(2);
				resultList.set(2, specificDo);
				resultList.set(specificIndex, tempDo);
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
		iDo.setStatus("O");
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

			// resource +表中获取参与人数（user_items）
			String fakeJoinStr = fakeResourceDo.getValue1();
			Integer fakeJoin = new Integer(fakeJoinStr);
			Integer addFakeJoin = afBoluomeActivityUserItemsService.geFakeJoin(activityId);
			if (addFakeJoin != null) {
				fakeJoin += addFakeJoin;
			}

			// resource
			String fakeRebateStr = fakeResourceDo.getValue3();
			Integer fakeRebate = new Integer(fakeRebateStr);

			resultMap.put("fakeRebate", fakeJoin);
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
	@Deprecated
	@RequestMapping(value = "/sendItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String sendItems(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		H5CommonResponse resultStr = null;
		FanbeiH5Context context = new FanbeiH5Context();

		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");

		try {
			context = doH5Check(request, false);
			Long userId = context.getUserId();// 68885L;//
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

				AfBoluomeActivityUserItemsDo condition = new AfBoluomeActivityUserItemsDo();
				condition.setBoluomeActivityId(activityId);
				condition.setUserId(userId);
				condition.setStatus("NORMAL");
				List<AfBoluomeActivityUserItemsDo> resultList = afBoluomeActivityUserItemsService
						.getListByCommonCondition(condition);

				List<AfBoluomeActivityItemsDo> itemsList = new ArrayList<>();
				/*
				 * List<Long> tempItemsList =
				 * afBoluomeActivityUserItemsService.getItemsByActivityIdUserId(
				 * activityId, userId);// 大于1张卡片的用户记录--》update，改成所有的。
				 */

				// 根据是否领取终极大奖不同个逻辑
				boolean isGetSuperPrize = false;
				isGetSuperPrize = afBoluomeActivityResultService.isGetSuperPrize(userId, activityId);
				if (isGetSuperPrize) {// 已经获得终极大奖了则所有的卡片都可以赠送了
					if (resultList != null && resultList.size() > 0) {
						itemsList = addNumber(activityId, userId);
					}
				} else {
					// 若没有领取周终极大奖，则逻辑不变

					if (resultList != null && resultList.size() > 1) {// 此时判断是否大于1则。。
						itemsList = addNumber(activityId, userId);

					}
				}
				Map<String, Object> data = new HashMap<>();
				data.put("itemsList", itemsList);
				data.put("userItemsList", resultList);
				resultStr = H5CommonResponse.getNewInstance(true, "赠送卡片初始化成功", "", data);
				doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getErrorCode().getDesc());
			logger.error("sendItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getMessage());
			logger.error("sendItems" + context, e);
		}
		return resultStr.toString();
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
		H5CommonResponse resultStr;
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
			AfBoluomeActivityUserItemsDo prevousDo = afBoluomeActivityUserItemsService.getById(userItemsId);
			if (prevousDo != null && "NORMAL".equals(prevousDo.getStatus())) {
				updateUserItemsStatus(userItemsId, "FROZEN");
			}

			resultStr = H5CommonResponse.getNewInstance(true, "赠送成功");
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送失败", "", e.getErrorCode().getDesc());
			logger.error("doSendItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送失败", "", e.getMessage());
			logger.error("doSendItems" + context, e);
		}

		return resultStr.toString();
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
	public void updateUserItemsStatus(Long userItemsId, String status) throws Exception {
		try {
			// 检测是否有这个userItemsId的卡片，若有，则更新状态
			AfBoluomeActivityUserItemsDo prevousDo = afBoluomeActivityUserItemsService.getById(userItemsId);
			if (prevousDo != null) {

				// 验证这个用户是否拥有多余1张的此卡片
				AfBoluomeActivityUserItemsDo t = new AfBoluomeActivityUserItemsDo();
				t.setUserId(prevousDo.getUserId());
				t.setBoluomeActivityId(prevousDo.getBoluomeActivityId());
				t.setItemsId(prevousDo.getItemsId());
				t.setStatus("NORMAL");
				List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
						.getListByCommonCondition(t);
				if (userItemsList != null && userItemsList.size() > 0) {
					AfBoluomeActivityUserItemsDo resourceDo = new AfBoluomeActivityUserItemsDo();
					resourceDo.setRid(userItemsId);
					resourceDo.setStatus(status);
					resourceDo.setGmtModified(new Date());
					afBoluomeActivityUserItemsService.updateById(resourceDo);
				}
			}

		} catch (Exception e) {
			logger.error("update userItems status erro");
			e.printStackTrace();
		}
	}

	/**
	 * @说明：扫描二维码的时候，进行的业务逻辑（卡片冻结），然后重定向
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/submitShareCode", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public void submitShareCode(HttpServletRequest request, HttpServletResponse response) {
		try {

			String shareAppUrl = request.getParameter("shareAppUrl");
			if (StringUtil.isNotBlank(shareAppUrl)) {
				// shareAppUrl = new String(Base64.decode(shareAppUrl));
//				shareAppUrl = shareAppUrl.replace("_", "&");
//				Long userItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));
//
//				AfBoluomeActivityUserItemsDo prevousDo = afBoluomeActivityUserItemsService.getById(userItemsId);
//				if (prevousDo != null && "NORMAL".equals(prevousDo.getStatus())) {
//					afBoluomeActivityUserItemsService.updateUserItemsStatus(userItemsId, "FROZEN");
//				}
				/*
				 * String userName = request.getParameter("userName"); String
				 * activityId = request.getParameter("activityId");
				 */
				String redirectShareAppUrl = shareAppUrl;
				doMaidianLog(request, H5CommonResponse.getNewInstance(true, redirectShareAppUrl),request.getParameter("shareAppUrl"));
				response.sendRedirect(redirectShareAppUrl);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.error(exception.getMessage());
		}

	}

	/**
	 * 
	 * @说明：卡片赠送(专享初始化页面,无需登录) @param: @param request
	 * 
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/ggSendItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String ggSendItems(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr = null;
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
					resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data);
					doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
				}
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getErrorCode().getDesc());
			logger.error("ggSendItems error", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getMessage());
			logger.error("ggSendItems error", e);
		}

		return resultStr.toString();
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
		H5CommonResponse resultStr = null;
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, false);
			String userName = context.getUserName();
			// TODO:String userName = request.getParameter("userName");
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
					// 查看卡片是否过期 resourceUserItemsId
					if (!resourceUserItemsDo.getStatus().equals("FROZEN")) {
						if (resourceUserItemsDo.getStatus().equals("SENT")) {
							return H5CommonResponse.getNewInstance(true, "卡片已被其他用户领取，不能领取").toString();
						}
						return H5CommonResponse.getNewInstance(true, "该卡片已经超时退给赠送者，不能领取").toString();
					}

					Long reourceUserId = resourceUserItemsDo.getUserId();
					// 您没有权限领取此卡片
					if (reourceUserId.equals(userId)) {
						return H5CommonResponse.getNewInstance(true, "您没有权限领取此卡片").toString();
					}

					// 查看是否已经领走
					AfBoluomeActivityUserItemsDo newUserItemsDoCondition = new AfBoluomeActivityUserItemsDo();
					newUserItemsDoCondition.setUserId(userId);
					newUserItemsDoCondition.setSourceId(resourceUserItemsId);
					newUserItemsDoCondition.setStatus("NORMAL");
					List<AfBoluomeActivityUserItemsDo> list = afBoluomeActivityUserItemsService
							.getListByCommonCondition(newUserItemsDoCondition);
					if (list == null || list.size() == 0) {
						/*
						 * //检查卡片是否过期（是否有FROZEN状态变成了NORMAL） if
						 * (resourceUserItemsDo.getStatus() == "NORMAL") {
						 * return H5CommonResponse.getNewInstance(true,
						 * "改卡片已经超时退给赠送者，不能领取").toString(); }
						 */
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

						AfBoluomeActivityUserItemsDo prevousDo = afBoluomeActivityUserItemsService
								.getById(resourceUserItemsId);
						if (prevousDo != null && "FROZEN".equals(prevousDo.getStatus())) {
							updateUserItemsStatus(resourceUserItemsId, "SENT");
						}
						resultStr = H5CommonResponse.getNewInstance(true, "领取卡片成功");
						doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
					} else {
						return H5CommonResponse.getNewInstance(true, "您已领走卡片，无需重复领取").toString();
					}

				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "领取卡片失败", "", e.getErrorCode().getDesc());
			logger.error("pickUpItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "领取卡片失败", "", e.getMessage());
			logger.error("pickUpItems" + context, e);
		}

		return resultStr.toString();
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
		H5CommonResponse resultStr;
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
			resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data);
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getErrorCode().getDesc());
			logger.error("lightItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getMessage());
			logger.error("lightItems" + context, e);
		}

		return resultStr.toString();
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
		H5CommonResponse resultStr;
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
			resultStr = H5CommonResponse.getNewInstance(true, "获取卡片成功", "", data);
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getErrorCode().getDesc());
			logger.error("askForItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getMessage());
			logger.error("askForItems" + context, e);
		}

		return resultStr.toString();
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
		H5CommonResponse resultStr = null;
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

			AfBoluomeActivityItemsDo itemsDo = afBoluomeActivityItemsService.getById(itemsId);
			if (itemsDo != null) {
				resultStr = H5CommonResponse.getNewInstance(false, "您还没有可赠送的卡片", "", "");
			}
			// 验证登录用户该卡片是否大于1张
			AfBoluomeActivityUserItemsDo userItemsDo = new AfBoluomeActivityUserItemsDo();
			userItemsDo.setUserId(userId);
			userItemsDo.setItemsId(itemsId);
			userItemsDo.setStatus("NORMAL");
			List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
					.getListByCommonCondition(userItemsDo);
			if (userItemsList != null && userItemsList.size() > 0) {
				AfBoluomeActivityUserItemsDo userdoo = userItemsList.get(0);
				boolean isGetSuperPrize = afBoluomeActivityResultService.isGetSuperPrize(userId,
						userdoo.getBoluomeActivityId());

				// 若获取了终极大奖，则只有一个都可以赠送，若没有获取终极大奖，则必须是两个

				if (userItemsList == null || (!isGetSuperPrize && userItemsList.size() < 2)
						|| (isGetSuperPrize && userItemsList.size() < 1)) {
					return H5CommonResponse.getNewInstance(false, "抱歉，您还没有可赠送的卡片").toString();
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
				resultStr = H5CommonResponse.getNewInstance(true, "赠送成功");
				doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "您还没有可赠送的卡片", "", e.getErrorCode().getDesc());
			logger.error("sendToFriend" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "您还没有可赠送的卡片", "", e.getMessage());
			logger.error("sendToFriend" + context, e);
		}

		return resultStr.toString();
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
		H5CommonResponse resultStr = null;
		try {

			Long itemsId = NumberUtil.objToLong(request.getParameter("itemsId"));
			String userName = request.getParameter("friendName");
			logger.info("method = /H5GGShare/ggAskForItems" + "itemsId=" + itemsId + "friendName" + userName);
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
					resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data);
					doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
				}
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getErrorCode().getDesc());
			logger.error("ggAskForItems error", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getMessage());
			logger.error("ggAskForItems error", e);
		}

		return resultStr.toString();
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
		H5CommonResponse resultStr = null;
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
				int rebateNumber = 0;// 初始人数是25个

				Map<String, Integer> fakeDo = getFakePerson(activityId);
				if (fakeDo != null) {
					rebateNumber = fakeDo.get("fakeRebate");
				}

				data.put("rebateNumber", rebateNumber);
				data.put("rankList", rankList);
				resultStr = H5CommonResponse.getNewInstance(true, "获取排行榜成功", "", data);
				doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "获取排行榜片失败", "", e.getErrorCode().getDesc());
			logger.error("listRank", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "获取排行榜失败", "", e.getMessage());
			logger.error("listRank", e);
		}

		return resultStr.toString();
	}

	@RequestMapping(value = "/pickUpSuperPrize", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String pickUpSuperPrize(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr = null;
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
				activityItemsDo.setStatus("O");
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
							return H5CommonResponse.getNewInstance(false, "您还没有集齐卡片不能领取终极大奖").toString();
						}
						deleteList.add(useritemsList.get(0));
					}
				}
				// to delete
				if (deleteList != null && deleteList.size() > 0) {
					for (AfBoluomeActivityUserItemsDo delete : deleteList) {
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
						// bug:吧现金大奖转为用户余额。
						AfUserAccountDo accountDo = new AfUserAccountDo();
						BigDecimal rebateAmount = null;
						// 查到n券对应的金额
						AfCouponDo userCouponDo = afCouponService.getCouponById(resultCoupon.getCouponId());// afUserCouponService.getUserCouponById(resultCoupon.getCouponId());
						if (userCouponDo != null) {
							rebateAmount = userCouponDo.getAmount();
							accountDo.setRebateAmount(rebateAmount);
							accountDo.setUserId(userId);
							afUserAccountService.updateUserAccount(accountDo);
							// 从用户卡片去掉活动卡片的一个。
							resultStr = H5CommonResponse.getNewInstance(true, "红包领取成功，请前往  我的-返利金额  查看");
							doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
						}

					}
				}
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "红包领取失败", "", e.getErrorCode().getDesc());
			logger.error("pickUpSuperPrize" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "红包领取失败", "", e.getMessage());
			logger.error("pickUpSuperPrize" + context, e);
		}

		return resultStr.toString();
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
			    //存入缓存
			bizCacheUtil.saveObject("boluome:coupon:"+resourceInfo.getRid()+afUserDo.getUserName(),"Y",2*Constants.SECOND_OF_ONE_MONTH);
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
			return H5CommonResponse.getNewInstance(true, "恭喜您领券成功").toString();

		} catch (Exception e) {
			logger.error("pick brand coupon failed , e = {}", e.getMessage());
			return H5CommonResponse
					.getNewInstance(false, FanbeiExceptionCode.PICK_BRAND_COUPON_FAILED.getDesc(), "", null).toString();
		}

	}
}
