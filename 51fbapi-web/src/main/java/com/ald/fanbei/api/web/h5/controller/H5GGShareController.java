package com.ald.fanbei.api.web.h5.controller;

import java.util.ArrayList;
import java.util.Calendar;
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
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityCouponDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityResultDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

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
			AfResourceDo fakeResourceDo = afResourceService.getFakePersonByActivityId(activityId.toString());
			// TODO:取得对应条件的实体.
			if (fakeResourceDo == null) {
				throw new Exception();
			}
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
							String url = getCouponUrl() + "?" + "app_id" + app_id + "&user_id=" + user_id
									+ "&campaign_id=" + campaign_id;
							String reqResult = HttpUtil.doGet(url, 10);
							JSONObject result = JSONObject.parseObject(reqResult);

							if ("1000".equals(result.getString("code"))) {
								BoluomeCouponResponseParentBo responseBo = JSONObject
										.parseObject(result.getString("data"), BoluomeCouponResponseParentBo.class);
								logger.info("getCoupon result, responseBo={}", responseBo);
								boluomeCouponList = responseBo.getListCoupon();
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
			AfBoluomeActivityItemsDo iDo = new AfBoluomeActivityItemsDo();
			iDo.setBoluomeActivityId(activityId);
			iDo.setType("CARD");
			List<AfBoluomeActivityItemsDo> itemsList = afBoluomeActivityItemsService.getListByCommonCondition(iDo);

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

	private static String getCouponUrl() {
		if (couponUrl == null) {
			couponUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_COUPON_URL);
			return couponUrl;
		}
		return couponUrl;
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
	@RequestMapping(value = "sendItems", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	public String sendItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);
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
					List<AfBoluomeActivityUserItemsDo> userItemsList = afBoluomeActivityUserItemsService.getListByCommonCondition(t);
					if (userItemsList != null && userItemsList.size() > 0) {
						resultList.addAll(userItemsList);
					}
				}
			}
			Map<String,Object> data = new HashMap<>();
			data.put( "resultList", resultList);	
			resultStr = H5CommonResponse.getNewInstance(true,"赠送卡片成功", "", data).toString();
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
	 * @说明：赠送卡片
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "doSendItems", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String doSendItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);

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
	 * @说明：领走卡片
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "pickUpItems", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String pickUpItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);

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
	@RequestMapping(value = "lightItems", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	public String lightItems(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		FanbeiH5Context context = new FanbeiH5Context();
		try {
			context = doH5Check(request, true);

		} catch (FanbeiException e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getErrorCode().getDesc()).toString();
			logger.error("赠送卡片失败" + context, e);
		} catch (Exception e) {
			resultStr = H5CommonResponse.getNewInstance(false, "赠送卡片失败", "", e.getMessage()).toString();
			logger.error("赠送卡片失败" + context, e);
		}

		return resultStr;
	}

}
