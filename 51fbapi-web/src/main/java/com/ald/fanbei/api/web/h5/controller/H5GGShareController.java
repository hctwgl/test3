package com.ald.fanbei.api.web.h5.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseBo;
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityCouponService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityItemsService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityResultService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.AfResourceType;
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
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.fabric.xmlrpc.base.Data;

import sun.tools.tree.NewArrayExpression;

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
	private static String couponUrl = null;

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
			List<Object> bannerList = new ArrayList<>();
			List<AfResourceDo> bannerResclist = afResourceService
					.getResourceHomeListByTypeOrderBy(AfResourceType.GGTopBanner.getCode());
			if (bannerResclist != null && bannerResclist.size() > 0) {
				GetBorrowCashBase base = new GetBorrowCashBase();
				bannerList = base.getBannerObjectWithResourceDolist(bannerResclist);
			}

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
									JSONObject dataObj = JSON.parseObject(thirdResponseBo.getData());
									String coupons = dataObj.getString("activity_coupons");
									if (!StringUtil.isEmpty(coupons)) {
										BoluomeCouponResponseBo bo = JSONObject.parseObject(coupons,
												BoluomeCouponResponseBo.class);
										if (bo != null) {
											boluomeCouponList.add(bo);
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
			if (context.isLogin()) {
				// TODO:获取登录着的userName或者id
				Long userId = context.getUserId();
				useritemsDo.setUserId(userId);
				useritemsDo.setBoluomeActivityId(activityId);
				List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
						.getListByCommonCondition(useritemsDo);
				data.put("userItemsList", userItemsList);
			}
			data.put("bannerList", bannerList);
			data.put("fakeFinal", fakeFinal);
			data.put("fakeJoin", fakeJoin);
			data.put("boluomeCouponList", boluomeCouponList);
			data.put("normalCouponList", normalCouponList);
			data.put("itemsList", itemsList);
			data.put("despcription", despcription);
			resultStr = H5CommonResponse.getNewInstance(true, "初始化成功", "", data).toString();
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("活动点亮初始化数据失败", e);
		} catch (Exception exception) {
			resultStr = H5CommonResponse.getNewInstance(false, "初始化失败", "", exception.getMessage()).toString();
			logger.error("活动点亮初始化数据失败", exception);
		}

		return resultStr;
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
	 * @param: @param activityId
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

			AfBoluomeActivityResultDo t = new AfBoluomeActivityResultDo();
			t.setBoluomeActivityId(activityId);
			List<AfBoluomeActivityResultDo> listResult = afBoluomeActivityResultService.getListByCommonCondition(t);
			if (listResult != null && listResult.size() > 0) {
				fakeFinal += listResult.size();
			}

			// TOOD:resource +表中获取参与人数（user_items）
			String fakeJoinStr = fakeResourceDo.getValue1();
			Integer fakeJoin = new Integer(fakeJoinStr);
			AfBoluomeActivityUserItemsDo itemsDo = new AfBoluomeActivityUserItemsDo();
			itemsDo.setBoluomeActivityId(activityId);
			List<AfBoluomeActivityUserItemsDo> listItems = afBoluomeActivityUserItemsService
					.getListByCommonCondition(itemsDo);
			if (listItems != null && listItems.size() > 0) {
				fakeJoin += listItems.size();
			}
			resultMap.put("fakeFinal", fakeFinal);
			resultMap.put("fakeJoin", fakeJoin);
		}
		return resultMap;
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

	/**
	 * 
	 * @说明：赠送卡片(页面初始化) @param: @param request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 * 
	 */
	@RequestMapping(value = "/sendItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String sendItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			if (context.isLogin()) {
				Long userId = context.getUserId();
				Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
				// 选出itemsId
				List<AfBoluomeActivityUserItemsDo> resultList = new ArrayList<>();
				List<Long> itemsList = afBoluomeActivityUserItemsService.getItemsByActivityIdUserId(activityId, userId);
				if (itemsList != null && itemsList.size() > 0) {
					for (Long itemsId : itemsList) {
						AfBoluomeActivityUserItemsDo t = new AfBoluomeActivityUserItemsDo();
						t.setSourceUserId(userId);
						t.setBoluomeActivityId(activityId);
						t.setItemsId(itemsId);
						List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService
								.getListByCommonCondition(t);
						if (userItemsList != null && userItemsList.size() > 0) {
							resultList.addAll(userItemsList);
						}
					}
				}
				Map<String, Object> data = new HashMap<>();
				data.put("resultList", resultList);
				resultStr = H5CommonResponse.getNewInstance(true, "赠送卡片初始化成功", "", data).toString();
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("赠送卡片初始化失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getMessage()).toString();
			logger.error("赠送卡片初始化失败" + context, e);
		}

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
	public String doSendItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			if (context.isLogin()) {
				Long userItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));
				// 改变用户卡片的中见状态
				updateUserItemsStatus(userItemsId, "FROZEN");
			}

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("赠送卡片初始化失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片初始化失败", "", e.getMessage()).toString();
			logger.error("赠送卡片初始化失败" + context, e);
		}

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
		AfBoluomeActivityUserItemsDo resourceDo = afBoluomeActivityUserItemsService.getById(userItemsId);
		if (resourceDo != null) {
			resourceDo.setStatus(status);
			resourceDo.setGmtModified(new Date());
			afBoluomeActivityUserItemsService.updateById(resourceDo);
		}
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
	public String ggSendItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
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
					AfBoluomeActivityItemsDo itemsDo = afBoluomeActivityItemsService.getById(userItemsDo.getItemsId());
					
					Map<String, Object> data = new HashMap<>();
					data.put("friend", friend);
					data.put("itemsDo", itemsDo);
					data.put("userItemsDo", userItemsDo);
					data.put("fakeFinal", fakeFinal);
					data.put("fakeJoin", fakeJoin);
					resultStr = H5CommonResponse.getNewInstance(true, "成功", "", data).toString();
				}
			}

			// }
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("ggSendItems error", e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "失败", "", e.getMessage()).toString();
			logger.error("ggSendItems error", e);
		}

		return resultStr;
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
	public String pickUpItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			if (context.isLogin()) {
				Long userId = context.getUserId();// 登录的用户id
				Long resourceUserItemsId = NumberUtil.objToLong(request.getParameter("userItemsId"));// 卡片主人的主键id
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
					List<AfBoluomeActivityUserItemsDo> list = afBoluomeActivityUserItemsService
							.getListByCommonCondition(newUserItemsDoCondition);
					if (list != null && !list.isEmpty()) {
						return H5CommonResponse.getNewInstance(true, "你没有权限领取此卡片").toString();
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

				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("赠送卡片失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getMessage()).toString();
			logger.error("赠送卡片失败" + context, e);
		}

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
	@RequestMapping(value = "/lightItems", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String lightItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			resultStr = initHomepage(request, response);
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("赠送卡片失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getMessage()).toString();
			logger.error("赠送卡片失败" + context, e);
		}

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
	public String askForItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			Long activityId = NumberUtil.objToLong(request.getParameter("activityId"));
			List<AfBoluomeActivityItemsDo> itemsList = getActivityItems(activityId);
			Map<String, Object> data = new HashMap<>();
			data.put("itemsList", itemsList);
			resultStr = H5CommonResponse.getNewInstance(true, "获取卡片成功", "", data).toString();
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("索要初卡片失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getMessage()).toString();
			logger.error("索要初卡片失败" + context, e);
		}

		return resultStr;
	}

	/**
	 * 
	 * @说明：赠送给好友
	 * @param: @param request
	 * @param: @param response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/sendToFriend", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String sendToFriend(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
			Long itemsId = NumberUtil.objToLong(request.getParameter("itemsId"));
			Long friendId = NumberUtil.objToLong(request.getParameter("friendId"));
			Long userId = context.getUserId();
			if (userId != null) {
				AfBoluomeActivityItemsDo itemsDo = afBoluomeActivityItemsService.getById(itemsId);
				if (itemsDo != null) {
					//验证登录用户该卡片是否大于1张
					AfBoluomeActivityUserItemsDo userItemsDo = new AfBoluomeActivityUserItemsDo();
					userItemsDo.setUserId(userId);
					userItemsDo.setItemsId(itemsId);
					List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService.getListByCommonCondition(userItemsDo);
					if (userItemsList == null || userItemsList.size()<2) {
						return H5CommonResponse.getNewInstance(false, "抱歉，你暂时没有足够此卡片").toString();
					}
					//若大于一张则，
					//登录用户卡片选一张，然后赠状态设为已经赠送
					AfBoluomeActivityUserItemsDo resourceUserItemsDo = userItemsList.get(0);
					updateUserItemsStatus(resourceUserItemsDo.getRid(),"SENT");
				
					//朋友的userItems表中增加一条卡片记录
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
					
					resultStr = H5CommonResponse.getNewInstance(true, "获取卡片成功").toString();
				}
			}
		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("索要初卡片失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "索要初卡片失败", "", e.getMessage()).toString();
			logger.error("索要初卡片失败" + context, e);
		}

		return resultStr;
	}
	
	/**
	 * 
	 * @说明：索要初始化页面（原用户的id由页面带过来）
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/ggAskForItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String doAskForItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		try {
			Long itemsId = NumberUtil.objToLong(request.getParameter("itemsId"));
			Long userId = NumberUtil.objToLong(request.getParameter("userId"));// 索要人的用户id
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
					if (friend.isEmpty()) {
						friend = userDo.getUserName();
					}
					Map<String, Object> data = new HashMap<>();
					data.put("friend", friend);
					data.put("itemsDo", itemsDo);
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

		return resultStr;
	}
	
	






}
