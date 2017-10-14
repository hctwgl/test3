package com.ald.fanbei.api.web.apph5.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseParentBo;
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
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
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
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
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * <p>
 * Title:APPH5GGShareController
 * <p>
 * <p>
 * Description:
 * <p>
 * 
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年8月1日下午3:25:58
 *
 */
@Controller
@RequestMapping("/H5GG")
public class APPH5GGShareController extends BaseController {

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
	BoluomeUtil boluomeUtil;
	@Resource
	AfBoluomeActivityUserLoginService afBoluomeActivityUserLoginService;
	@Resource
	AfShopService afShopService;
	private static String couponUrl = null;

	@Resource
	AfBoluomeActivityUserRebateService afBoluomeActivityUserRebateService;

	String opennative = "/fanbei-web/opennative?name=";

	/**
	 * 
	 * @说明：逛逛首页券列表
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@ResponseBody
	@RequestMapping(value = "/showCoupon", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String showCoupon(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr = null;
		FanbeiWebContext context = new FanbeiWebContext();

		try {
			context = doWebCheck(request, false);

			List<AfResourceDo> listResource = afResourceService.getConfigsByTypesAndSecType("BOLUOMI", "GGENTRY");
			if (null != listResource && listResource.size() > 0) {
				List<BoluomeCouponResponseBo> boluomeCouponList = new ArrayList<>();

				for (AfResourceDo couponResourceDo : listResource) {

					logger.info("showCoupon getCouponUrl resourceId = {},couponResourceDo = {}",
							couponResourceDo.getRid(), couponResourceDo);
					if (couponResourceDo != null) {
						Long resourceId = couponResourceDo.getRid();
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
							logger.info("initHomePage getCouponUrl reqResult = {}", reqResult);
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
											String userName = context.getUserName();
											// 为了兼容从我也要点亮中调用主页接口
											if (StringUtil.isBlank(userName)) {
												userName = request.getParameter("userName");
											}
											List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil
													.getActivityCouponList(uri);
											BrandActivityCouponResponseBo bo = activityCouponList.get(0);
											if (userName != null) {
												Long userId = convertUserNameToUserId(userName);
												if (userId != null) {
													// 判断用户是否拥有该优惠券
													if (boluomeUtil.isUserHasCoupon(uri, userId, 1)
															|| bo.getDistributed() >= bo.getTotal()) {
														BoluomeCouponResponseBo.setIsHas(YesNoStatus.YES.getCode());
													} else {
														BoluomeCouponResponseBo.setIsHas(YesNoStatus.NO.getCode());
													}
												}
											}
											Map<String, Object> data = new HashMap<String, Object>();
											boluomeCouponList.add(BoluomeCouponResponseBo);
											data.put("boluomeCouponList", boluomeCouponList);
											resultStr = H5CommonResponse.getNewInstance(true, "初始化成功", "", data);
											doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
											
										}
									}

								}
							}
						}
					}
				}
			}

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr = H5CommonResponse.getNewInstance(false, "没有登录", "", data);
				return resultStr.toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc());
			logger.error("resultStr = {}", resultStr);
			logger.error("初始化数据失败  e = {} , resultStr = {}", e, resultStr);
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage());
			logger.error("初始化数据失败  e = {} , resultStr = {}", exception, resultStr);
		}
	
		return resultStr.toString();

	}

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
		H5CommonResponse resultStr ;
		FanbeiWebContext context = new FanbeiWebContext();

		// TODO:获取活动的id
		Long activityId = NumberUtil.objToLongDefault(request.getParameter("activityId"), 1);
		try {

			context = doWebCheck(request, false);
			// TODO:banner轮播图后台增加一个类型，和配置。GG_TOP_BANNER.根据类型和活动id去取。
			// List<Object> bannerList = new ArrayList<>();
			List<AfResourceDo> bannerList = afResourceService
					.getResourceHomeListByTypeOrderBy(AfResourceType.GGTopBanner.getCode());

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
			bDo.setScopeApplication("LIGHT");
			List<AfBoluomeActivityCouponDo> bList = afBoluomeActivityCouponService.getListByCommonCondition(bDo);

			// List<BoluomeCouponResponseBo> boluomeCouponList = new
			// ArrayList<>();
			List<BoluomeCouponResponseBo> boluomeCouponList = new ArrayList<>();
			if (bList != null && bList.size() > 0) {
				for (AfBoluomeActivityCouponDo bCouponDo : bList) {
					Long resourceId = bCouponDo.getCouponId();
					AfResourceDo couponResourceDo = afResourceService.getResourceByResourceId(resourceId);
					logger.info("initHomePage getCouponUrl resourceId = {},couponResourceDo = {}", resourceId,
							couponResourceDo);
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
							logger.info("initHomePage getCouponUrl reqResult = {}", reqResult);
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
											String userName = context.getUserName();
											// 为了兼容从我也要点亮中调用主页接口
											if (StringUtil.isBlank(userName)) {
												userName = request.getParameter("userName");
											}
											List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil
													.getActivityCouponList(uri);
											BrandActivityCouponResponseBo bo = activityCouponList.get(0);
											if (userName != null) {
												Long userId = convertUserNameToUserId(userName);
												if (userId != null) {
													// 判断用户是否拥有该优惠券
												    if (boluomeUtil.isUserHasCoupon(uri, userId, 1) || bo.getDistributed() >= bo.getTotal()) {
														BoluomeCouponResponseBo.setIsHas(YesNoStatus.YES.getCode());
													} else {
														BoluomeCouponResponseBo.setIsHas(YesNoStatus.NO.getCode());
													}
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
				superPrizeStatus = "N";// 若是已经登录了则是初始化为N的状态
				Long userId = convertUserNameToUserId(userName);
				if (userId != null && userId > 0) {
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
									superPrizeStatus = "N";
									break;
								}
								userList.add(useritemsList.get(0));

							}
							// 最终用userList 和应该得到的卡片数量对比 是否一样最最后一层拦截
							if (userList != null && userList.size() == itemsList.size()) {
								superPrizeStatus = "YY";
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
			}else {
				// 未登录则终极大奖状态不变，并且卡片个数是-1
				for (AfBoluomeActivityItemsDo itemsDoo : itemsList) {
					itemsDoo.setNum(-1);
				}
			}

			// 获取文案的信息
			String popupWords = null;

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
				if (userId != null) {
					// TODO:获取弹框文案；
					popupWords = afBoluomeActivityService.activityOffical(userId);
					// 获取已经邀请的人数
					alreadyNum = afBoluomeActivityUserLoginService.getBindingNum(activityId, userId);
				}
				if (userId == null) {
					alreadyNum = 0;
				}
			}

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
			logger.info("resultStr = {}", resultStr);
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr =  H5CommonResponse.getNewInstance(false, "没有登录", "", data);
				doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
				return resultStr.toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc());
			logger.error("resultStr = {}", resultStr);
			logger.error("活动点亮初始化数据失败  e = {} , resultStr = {}", e, resultStr);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage());
			logger.error("活动点亮初始化数据失败  e = {} , resultStr = {}", exception, resultStr);
			doMaidianLog(request, H5CommonResponse.getNewInstance(false, "fail"));
		}
		return resultStr.toString();
	}

	private List<AfBoluomeActivityItemsDo> addNumber(Long activityId, Long userId) {
		AfBoluomeActivityItemsDo t = new AfBoluomeActivityItemsDo();
		t.setBoluomeActivityId(activityId);
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
			/*
			 * AfBoluomeActivityResultDo t = new AfBoluomeActivityResultDo();
			 * t.setBoluomeActivityId(activityId);
			 * List<AfBoluomeActivityResultDo> listResult =
			 * afBoluomeActivityResultService.getListByCommonCondition(t); if
			 * (listResult != null && listResult.size() > 0) { fakeFinal +=
			 * listResult.size(); }
			 */
			// TOOD:resource +表中获取参与人数（user_items）
			String fakeJoinStr = fakeResourceDo.getValue1();
			Integer fakeJoin = new Integer(fakeJoinStr);
			Integer addFakeJoin = afBoluomeActivityUserItemsService.geFakeJoin(activityId);
			if (addFakeJoin != null) {
				fakeJoin += addFakeJoin;
			}
			/*
			 * AfBoluomeActivityUserItemsDo itemsDo = new
			 * AfBoluomeActivityUserItemsDo();
			 * itemsDo.setBoluomeActivityId(activityId);
			 * itemsDo.setStatus("NORMAL"); List<AfBoluomeActivityUserItemsDo>
			 * listItems =
			 * afBoluomeActivityUserItemsService.getListByCommonCondition(
			 * itemsDo); if (listItems != null && listItems.size() > 0) {
			 * fakeJoin += listItems.size(); }
			 */
			resultMap.put("fakeFinal", fakeFinal);
			resultMap.put("fakeJoin", fakeJoin);
		}
		return resultMap;
	}

	/**
	 * 
	 * @说明：赠送卡片(页面初始化) @param: @param request
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
		H5CommonResponse resultStr = null;
		FanbeiWebContext context = new FanbeiWebContext();

		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
		response.setContentType("application/json;charset=utf-8");

		try {
			context = doWebCheck(request, false);
			String userName = context.getUserName();// request.getParameter("userName");
			Long userId = convertUserNameToUserId(userName);
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
						userId);// 大于1张卡片的用户记录--》update，改成所有的。
				// 根据是否领取终极大奖不同个逻辑
				boolean isGetSuperPrize = false;
				isGetSuperPrize = afBoluomeActivityResultService.isGetSuperPrize(userId, activityId);
				if (isGetSuperPrize) {// 已经获得终极大奖了则所有的卡片都可以赠送了
					if (tempItemsList != null && tempItemsList.size() > 0) {
						itemsList = addNumber(activityId, userId);

						for (Long itemsId : tempItemsList) {
							AfBoluomeActivityUserItemsDo t = new AfBoluomeActivityUserItemsDo();
							t.setUserId(userId);
							t.setStatus("NORMAL");
							t.setBoluomeActivityId(activityId);
							t.setItemsId(itemsId);
							List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
									.getListByCommonCondition(t);
							if (userItemsList != null && userItemsList.size() > 0) {
								resultList.addAll(userItemsList);
							}
						}
					}
				} else {
					// 若没有领取周终极大奖，则逻辑不变

					if (tempItemsList != null && tempItemsList.size() > 1) {// 此时判断是否大于1则。。
						itemsList = addNumber(activityId, userId);

						for (Long itemsId : tempItemsList) {
							AfBoluomeActivityUserItemsDo t = new AfBoluomeActivityUserItemsDo();
							t.setUserId(userId);
							t.setStatus("NORMAL");
							t.setBoluomeActivityId(activityId);
							t.setItemsId(itemsId);
							List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
									.getListByCommonCondition(t);
							if (userItemsList != null && userItemsList.size() > 0) {
								resultList.addAll(userItemsList);
							}
						}
					}
				}

				Map<String, Object> data = new HashMap<>();
				data.put("itemsList", itemsList);
				data.put("userItemsList", resultList);
				resultStr = H5CommonResponse.getNewInstance(true, "赠送卡片初始化成功", "", data);
				doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
			}

		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "抱歉你暂时没有可以赠送的卡片", "", e.getErrorCode().getDesc());
					
			logger.error("sendItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "抱歉你暂时没有可以赠送的卡片", "", e.getMessage());
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
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			String userName = context.getUserName();
			// String userName = request.getParameter("userName");
			Long userId = convertUserNameToUserId(userName);
			if (userId == null) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				 resultStr = H5CommonResponse.getNewInstance(true, "没有登录", "", data);
				 return resultStr.toString();
			}
			Long userItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));
			// 改变用户卡片的中见状态
			updateUserItemsStatus(userItemsId, "FROZEN");
			resultStr = H5CommonResponse.getNewInstance(true, "赠送成功");
			doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				
				 resultStr =  H5CommonResponse.getNewInstance(true, "没有登录", "", data); 
				 return resultStr.toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getErrorCode().getDesc());
			logger.error("doSendItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getMessage());
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
		H5CommonResponse resultStr = null;
		try {
			Long userItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));
			AfBoluomeActivityUserItemsDo userItemsDo = afBoluomeActivityUserItemsService.getById(userItemsId);
			if (userItemsDo != null) {
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
					// 卡片的展示，之前是活动卡片实体，现在是banner实体。
					AfBoluomeActivityItemsDo itemsDo = afBoluomeActivityItemsService.getById(userItemsDo.getItemsId());
					AfResourceDo resourceDo = new AfResourceDo();
					if (itemsDo != null) {
						resourceDo = afResourceService.getGGSpecificBanner(itemsDo.getRefId().toString());
					}
					Map<String, Object> data = new HashMap<>();
					data.put("resourceDo", resourceDo);
					data.put("friend", friend);
					data.put("itemsDo", itemsDo);
					data.put("userItemsDo", userItemsDo);
					data.put("fakeFinal", fakeFinal);
					data.put("fakeJoin", fakeJoin);
					resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data);
					doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
				}
			}

			// }
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr =  H5CommonResponse.getNewInstance(true, "没有登录", "", data);
				return resultStr.toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getErrorCode().getDesc());
			logger.error("ggSendItems error", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getMessage());
			logger.error("ggSendItems error", e);
		}

		return resultStr.toString();
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
	@RequestMapping(value = "/pickUpItems", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String pickUpItems(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr = null;
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			String userName = context.getUserName();
			// String userName = request.getParameter("userName");
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
					Long destUserId = resourceUserItemsDo.getUserId();
					// 你没有权限领取此卡片
					if (destUserId.equals(userId)) {
						return H5CommonResponse.getNewInstance(true, "你没有权限领取此卡片").toString();
					}
					// 查看是否已经领走
					AfBoluomeActivityUserItemsDo newUserItemsDoCondition = new AfBoluomeActivityUserItemsDo();
					newUserItemsDoCondition.setUserId(userId);
					newUserItemsDoCondition.setSourceId(resourceUserItemsId);
					newUserItemsDoCondition.setStatus("NORMAL");
					List<AfBoluomeActivityUserItemsDo> list = afBoluomeActivityUserItemsService
							.getListByCommonCondition(newUserItemsDoCondition);
					int length = list.size();
					if (list == null || length == 0) {
						if (resourceUserItemsDo.getStatus() == "NORMAL") {
							return H5CommonResponse.getNewInstance(true, "改卡片已经超时退给赠送者，不能领取").toString();
						}
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
						resultStr = H5CommonResponse.getNewInstance(true, "领取卡片成功");
						doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
					} else {
					        resultStr = H5CommonResponse.getNewInstance(true, "你没有权限领取此卡片");
						return resultStr.toString();
					}

				}
			}
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				resultStr =  H5CommonResponse.getNewInstance(true, "没有登录", "", data);
				return resultStr.toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getErrorCode().getDesc());
			logger.error("pickUpItems" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getMessage());
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
	@RequestMapping(value = "/lightItems", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String lightItems(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr ;
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
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
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
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
	 * 说明：索要卡片初始化1
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
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
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
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
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
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			Long itemsId = NumberUtil.objToLong(request.getParameter("itemsId"));
			String friendName = request.getParameter("friendName");
			Long friendId = convertUserNameToUserId(friendName);
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
						return H5CommonResponse.getNewInstance(false, "抱歉，你暂时没有足够此卡片").toString();
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
			}
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getErrorCode().getDesc());
			logger.error("sendToFriend" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getMessage());
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
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
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
		H5CommonResponse resultStr = null ;
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
				/*
				 * Map<String, Integer> fakeMap = getFakePerson(activityId);
				 * Integer fakeFinal = fakeMap.get("fakeFinal");
				 */
				int rebateNumber = rankList.size();
				/*
				 * if (fakeFinal != null) { rebateNumber += fakeFinal; }
				 */
				data.put("rebateNumber", rebateNumber);
				data.put("rankList", rankList);
				resultStr = H5CommonResponse.getNewInstance(true, "获取排行榜成功", "", data);
				doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"));
			}
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getErrorCode().getDesc());
			logger.error("listRank", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getMessage());
			logger.error("listRank", e);
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

	@RequestMapping(value = "/pickUpSuperPrize", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String pickUpSuperPrize(HttpServletRequest request, HttpServletResponse response) {
		H5CommonResponse resultStr = null ;
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			context = doWebCheck(request, false);
			// TODO:获取登录着的userName或者id
			String userName = context.getUserName();
			// String userName = request.getParameter("userName").toString();
			Long userId = convertUserNameToUserId(userName);
			if (userId == null || StringUtil.isBlank(userName)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
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
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				return H5CommonResponse.getNewInstance(true, "没有登录", "", data).toString();
			}
			resultStr = H5CommonResponse.getNewInstance(false, "红包领取失败", "", e.getErrorCode().getDesc());
			logger.error("pickUpSuperPrize" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "红包领取失败", "", e.getMessage());
			logger.error("pickUpSuperPrize" + context, e);
		}
		return resultStr.toString();
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
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

}
